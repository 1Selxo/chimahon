@file:OptIn(ExperimentalSerializationApi::class)

package app.chimahon.shared

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.ArrayDeque
import javax.imageio.ImageIO
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

internal object DesktopGlensOcrClient {
    private const val LENS_ENDPOINT = "https://lensfrontend-pa.googleapis.com/v1/crupload"
    private const val DEFAULT_API_KEY = "AIzaSyDr2UxVnv_U85AbhhY8XSHSIavUW0DC-sY"
    private const val DEFAULT_USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    private const val DEFAULT_REGION = "US"
    private const val DEFAULT_TIME_ZONE = "America/New_York"
    private const val IMAGE_MAX_DIMENSION = 1500
    private const val DEFAULT_LANGUAGE_CODE = "ja"
    private const val MIN_NORMALIZED_BOX_EDGE = 0.0001
    private const val KATAKANA_MIDDLE_DOTS = "\u30FB\uFF65\u30FB\uFF65\u30FB\uFF65"

    private val complexPunctuationRegex = Regex("[!?\\.\\u2026\\u22EE\\uff0e\\uff1f\\uff01\\uff65]{2,}")
    private val japaneseTextRegex = Regex("[\\u3041-\\u3096\\u30A1-\\u30FA\\u4E01-\\u9FFF]")
    private val kanjiTextRegex = Regex("[\\u4E00-\\u9FFF]")
    private val whitespaceRegex = Regex("\\s+")

    private val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(20))
        .build()

    suspend fun recognize(
        bytes: ByteArray,
        languageCode: String,
    ): List<ChimahonReaderOcrBlock> = withContext(Dispatchers.IO) {
        retryGlens {
            val requestLanguageCode = languageCode.normalizedRequestedLanguageCode()
            val processed = prepareImageForGlens(bytes)
            val request = DesktopLensOverlayServerRequest(
                objectsRequest = DesktopLensOverlayObjectsRequest(
                    requestContext = DesktopLensOverlayRequestContext(
                        requestId = DesktopLensOverlayRequestId(
                            uuid = Random.nextLong() and Long.MAX_VALUE,
                            sequenceId = 1,
                            imageSequenceId = 1,
                        ),
                        clientContext = DesktopLensOverlayClientContext(
                            platform = 3,
                            surface = 4,
                            localeContext = DesktopLensLocaleContext(
                                language = requestLanguageCode,
                                region = DEFAULT_REGION,
                                timeZone = DEFAULT_TIME_ZONE,
                            ),
                        ),
                    ),
                    imageData = DesktopLensImageData(
                        payload = DesktopLensImagePayload(processed.bytes),
                        imageMetadata = DesktopLensImageMetadata(processed.width, processed.height),
                    ),
                ),
            )
            val payloadBytes = ProtoBuf.encodeToByteArray(request)
            val httpRequest = HttpRequest.newBuilder(URI.create(LENS_ENDPOINT))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "application/x-protobuf")
                .header("X-Goog-Api-Key", DEFAULT_API_KEY)
                .header("User-Agent", DEFAULT_USER_AGENT)
                .POST(HttpRequest.BodyPublishers.ofByteArray(payloadBytes))
                .build()
            val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray())
            if (response.statusCode() !in 200..299) {
                throw IOException("GLens API error ${response.statusCode()}")
            }
            ProtoBuf.decodeFromByteArray<DesktopLensOverlayServerResponse>(response.body())
                .toReaderOcrBlocks(requestLanguageCode)
        }
    }

    private suspend fun <T> retryGlens(block: suspend () -> T): T {
        var lastError: Throwable? = null
        repeat(3) { attempt ->
            try {
                return block()
            } catch (error: Throwable) {
                lastError = error
                if (attempt < 2) {
                    delay((attempt + 1) * 1_000L)
                }
            }
        }
        throw IOException("GLens OCR failed after retries", lastError)
    }

    private fun prepareImageForGlens(bytes: ByteArray): DesktopProcessedImage {
        ImageIO.setUseCache(false)
        val buffered = runCatching { ImageIO.read(ByteArrayInputStream(bytes)) }.getOrNull()
        if (buffered == null) {
            val skiaImage = runCatching { org.jetbrains.skia.Image.makeFromEncoded(bytes) }.getOrNull()
            if (skiaImage != null) {
                return DesktopProcessedImage(bytes, skiaImage.width, skiaImage.height)
            }
            throw IOException("Could not decode image for GLens OCR")
        }
        val resized = buffered.resizeForGlens()
        return ByteArrayOutputStream().use { out ->
            ImageIO.write(resized, "png", out)
            DesktopProcessedImage(out.toByteArray(), resized.width, resized.height)
        }
    }

    private fun BufferedImage.resizeForGlens(): BufferedImage {
        val scale = min(
            IMAGE_MAX_DIMENSION.toDouble() / width.toDouble(),
            IMAGE_MAX_DIMENSION.toDouble() / height.toDouble(),
        ).coerceAtMost(1.0)
        val targetWidth = (width * scale).toInt().coerceAtLeast(1)
        val targetHeight = (height * scale).toInt().coerceAtLeast(1)
        val output = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        val graphics = output.createGraphics()
        try {
            graphics.color = Color.WHITE
            graphics.fillRect(0, 0, targetWidth, targetHeight)
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            graphics.drawImage(this, 0, 0, targetWidth, targetHeight, null)
        } finally {
            graphics.dispose()
        }
        return output
    }

    private fun DesktopLensOverlayServerResponse.toReaderOcrBlocks(
        requestedLanguageCode: String,
    ): List<ChimahonReaderOcrBlock> {
        val text = objectsResponse?.text ?: return emptyList()
        val language = desktopOcrLanguage(
            requestedCode = requestedLanguageCode,
            detectedCode = text.contentLanguage,
        )
        val recognizedLines = text.textLayout
            ?.paragraphs
            .orEmpty()
            .flatMap { paragraph -> paragraph.lines.mapNotNull { it.toRecognizedLine(language) } }
            .distinctBy { line -> line.text + "|" + line.rect.cacheKey }

        if (recognizedLines.isEmpty()) return emptyList()

        return mergeDesktopOcrLines(recognizedLines, language)
            .mapNotNull { it.toReaderOcrBlock(language.outputCode) }
    }

    private fun DesktopTextLayoutLine.toRecognizedLine(language: DesktopOcrLanguage): DesktopRecognizedLine? {
        val rawText = words.joinToString(separator = "") { word ->
            word.plainText + (word.textSeparator ?: "")
        }.trim()
        val spacedText = if (language.prefersNoSpace) {
            rawText.replace(whitespaceRegex, "")
        } else {
            rawText
        }
        val cleanText = cleanDesktopOcrText(spacedText).trim()
        if (cleanText.isBlank()) return null

        val rect = geometry?.boundingBox?.toSafeNormalizedRect()
            ?: words.mapNotNull { it.geometry?.boundingBox?.toSafeNormalizedRect() }
                .unionNormalizedRects()
            ?: return null

        val isVertical = language.prefersVertical && if (abs(rect.rotationRadians) > 0.1) {
            abs(abs(rect.rotationRadians) - PI / 2.0) < 0.5
        } else {
            rect.width <= rect.height
        }
        val writingDirection = when {
            isVertical -> DesktopWritingDirection.TTB
            language.isRtl -> DesktopWritingDirection.RTL
            else -> DesktopWritingDirection.LTR
        }
        val dimension = if (isVertical) rect.height else rect.width
        val characterSize = dimension / cleanText.length.coerceAtLeast(1)

        return DesktopRecognizedLine(
            text = cleanText,
            rect = rect,
            writingDirection = writingDirection,
            characterSize = characterSize,
            hasJpText = japaneseTextRegex.containsMatchIn(cleanText),
            hasKanji = kanjiTextRegex.containsMatchIn(cleanText),
        )
    }

    private fun mergeDesktopOcrLines(
        lines: List<DesktopRecognizedLine>,
        language: DesktopOcrLanguage,
    ): List<DesktopMergedBlock> {
        val lineDicts = createLineDictionaries(lines, language)
        if (lineDicts.isEmpty()) return emptyList()

        val paragraphs = createParagraphsFromLines(lineDicts)
        val merged = mergeCloseParagraphs(paragraphs)
        val rows = groupParagraphsIntoRows(merged)
        return flattenRowsToParagraphs(reorderParagraphsInRows(rows))
    }

    private fun createLineDictionaries(
        lines: List<DesktopRecognizedLine>,
        language: DesktopOcrLanguage,
    ): List<DesktopLineDict> {
        return lines.mapNotNull { line ->
            if (line.text.isBlank()) return@mapNotNull null
            if (language.isJapanese && !line.hasJpText) return@mapNotNull null
            if (language.isChinese && !line.hasKanji) return@mapNotNull null

            val isVertical = line.writingDirection == DesktopWritingDirection.TTB
            val isRtl = line.writingDirection == DesktopWritingDirection.RTL ||
                (!isVertical && language.isRtl)

            DesktopLineDict(
                text = line.text,
                rect = line.rect,
                isVertical = isVertical,
                isRtl = isRtl,
                characterSize = line.characterSize,
                hasJpText = line.hasJpText,
                hasKanji = line.hasKanji,
            )
        }
    }

    private fun createParagraphsFromLines(lines: List<DesktopLineDict>): List<DesktopParagraphWithMeta> {
        val grouped = mutableSetOf<Int>()
        val paragraphs = mutableListOf<DesktopParagraphWithMeta>()

        fun groupLines(isVertical: Boolean, isRtl: Boolean) {
            val indices = lines.indices.filter { index ->
                lines[index].isVertical == isVertical &&
                    lines[index].isRtl == isRtl &&
                    index !in grouped
            }
            if (indices.size < 2) return

            val components = findConnectedComponents(
                items = indices.map { lines[it] },
                shouldConnect = { a, b -> shouldGroupInSameParagraph(a, b, isVertical) },
                getStartCoord = if (isVertical) {
                    { line -> line.rect.top }
                } else if (isRtl) {
                    { line -> line.rect.right }
                } else {
                    { line -> line.rect.left }
                },
                getEndCoord = if (isVertical) {
                    { line -> line.rect.bottom }
                } else if (isRtl) {
                    { line -> line.rect.left }
                } else {
                    { line -> line.rect.right }
                },
            )

            for (component in components) {
                if (component.size <= 1) continue
                val originalIndices = component.map { indices[it] }
                val paragraphLines = originalIndices.map { lines[it] }
                paragraphs += createParagraphFromLines(paragraphLines, isVertical, isRtl)
                grouped += originalIndices
            }
        }

        groupLines(isVertical = true, isRtl = false)
        groupLines(isVertical = false, isRtl = true)
        groupLines(isVertical = false, isRtl = false)

        lines.indices
            .filter { it !in grouped }
            .forEach { index ->
                val line = lines[index]
                paragraphs += createParagraphFromLines(listOf(line), line.isVertical, line.isRtl)
            }

        return paragraphs
    }

    private fun createParagraphFromLines(
        lines: List<DesktopLineDict>,
        isVertical: Boolean,
        isRtl: Boolean,
        filterFurigana: Boolean = true,
    ): DesktopParagraphWithMeta {
        val preordered = orderLineDicts(lines, isVertical, isRtl)
        val mergedFragments = mergeOverlappingLines(preordered, isVertical)
        val filtered = if (filterFurigana) {
            furiganaFilter(mergedFragments, isVertical).ifEmpty { mergedFragments.take(1) }
        } else {
            mergedFragments
        }
        val ordered = orderLineDicts(filtered, isVertical, isRtl)
        val rect = ordered.map { it.rect }.unionNormalizedRects()
            ?: preordered.first().rect
        val direction = when {
            isVertical -> DesktopWritingDirection.TTB
            isRtl -> DesktopWritingDirection.RTL
            else -> DesktopWritingDirection.LTR
        }
        val block = DesktopMergedBlock(
            lines = ordered.map { cleanDesktopOcrText(it.text) },
            rect = rect,
            writingDirection = direction,
            lineRects = ordered.map { it.rect },
        )
        val largestCharacterSize = if (isVertical) {
            ordered.maxByOrNull { it.rect.width }?.characterSize ?: 0.0
        } else {
            ordered.maxByOrNull { it.rect.height }?.characterSize ?: 0.0
        }

        return DesktopParagraphWithMeta(
            block = block,
            writingDirection = direction,
            characterSize = largestCharacterSize,
            sourceLines = ordered,
        )
    }

    private fun orderLineDicts(
        lines: List<DesktopLineDict>,
        isVertical: Boolean,
        isRtl: Boolean,
    ): List<DesktopLineDict> {
        return when {
            isVertical -> lines.sortedWith(
                compareByDescending<DesktopLineDict> { it.rect.centerX }
                    .thenBy { it.rect.top },
            )
            isRtl -> lines.sortedWith(
                compareBy<DesktopLineDict> { it.rect.centerY }
                    .thenByDescending { it.rect.right },
            )
            else -> lines.sortedWith(
                compareBy<DesktopLineDict> { it.rect.centerY }
                    .thenBy { it.rect.left },
            )
        }
    }

    private fun mergeOverlappingLines(lines: List<DesktopLineDict>, isVertical: Boolean): List<DesktopLineDict> {
        if (lines.size < 2) return lines

        val sorted = if (isVertical) {
            lines.sortedBy { it.rect.top }
        } else {
            lines.sortedBy { it.rect.left }
        }
        val merged = mutableListOf<DesktopLineDict>()
        val used = mutableSetOf<Int>()

        for (i in sorted.indices) {
            if (i in used) continue
            val mergeGroup = mutableListOf(sorted[i])
            used += i
            var lastLine = sorted[i]

            for (j in i + 1 until sorted.size) {
                if (j in used) continue
                val nextLine = sorted[j]
                if (isVertical) {
                    if (nextLine.rect.top - lastLine.rect.bottom > 0.5 * lastLine.characterSize) break
                } else {
                    if (nextLine.rect.left - lastLine.rect.right > 0.5 * lastLine.characterSize) break
                }
                if (shouldMergeLineFragments(lastLine, nextLine, isVertical)) {
                    mergeGroup += nextLine
                    used += j
                    lastLine = nextLine
                }
            }

            merged += if (mergeGroup.size > 1) {
                mergeMultipleLines(mergeGroup, isVertical)
            } else {
                sorted[i]
            }
        }

        return merged
    }

    private fun shouldMergeLineFragments(
        a: DesktopLineDict,
        b: DesktopLineDict,
        isVertical: Boolean,
    ): Boolean {
        return if (isVertical) {
            horizontalOverlap(a.rect, b.rect) > 0.8 && verticalOverlap(a.rect, b.rect) < 0.4
        } else {
            verticalOverlap(a.rect, b.rect) > 0.8 && horizontalOverlap(a.rect, b.rect) < 0.4
        }
    }

    private fun mergeMultipleLines(lines: List<DesktopLineDict>, isVertical: Boolean): DesktopLineDict {
        val sorted = if (isVertical) {
            lines.sortedBy { it.rect.centerY }
        } else {
            lines.sortedBy { it.rect.centerX }
        }
        val mergedText = cleanDesktopOcrText(sorted.joinToString(separator = "") { it.text })
        val rect = sorted.map { it.rect }.unionNormalizedRects() ?: sorted.first().rect
        val totalDimension = sorted.sumOf { if (isVertical) it.rect.height else it.rect.width }

        return DesktopLineDict(
            text = mergedText,
            rect = rect,
            isVertical = isVertical,
            isRtl = sorted.first().isRtl,
            characterSize = totalDimension / mergedText.length.coerceAtLeast(1),
            hasJpText = sorted.any { it.hasJpText },
            hasKanji = sorted.any { it.hasKanji },
        )
    }

    private fun shouldGroupInSameParagraph(
        line1: DesktopLineDict,
        line2: DesktopLineDict,
        isVertical: Boolean,
    ): Boolean {
        val characterSize = maxOf(line1.characterSize, line2.characterSize)

        if (isVertical) {
            val hDist = horizontalDistance(line1.rect, line2.rect)
            val lineWidth = maxOf(line1.rect.width, line2.rect.width)
            if (hDist >= lineWidth) return false
            if (abs(line1.rect.top - line2.rect.top) < characterSize) return true
        } else {
            val vDist = verticalDistance(line1.rect, line2.rect)
            val lineHeight = maxOf(line1.rect.height, line2.rect.height)
            if (vDist >= lineHeight * 2.0) return false

            val coord1 = if (line1.isRtl) line2.rect.right else line1.rect.left
            val coord2 = if (line1.isRtl) line1.rect.right else line2.rect.left
            if (coord1 - coord2 < 2.0 * characterSize) return true

            if (horizontalOverlap(line1.rect, line2.rect) > 0.9) return true
        }

        val filtered = furiganaFilter(listOf(line1, line2), isVertical)
        if (filtered.size == 1) {
            line1.isFurigana = true
            return true
        }

        return false
    }

    private fun furiganaFilter(lines: List<DesktopLineDict>, isVertical: Boolean): List<DesktopLineDict> {
        val filtered = mutableListOf<DesktopLineDict>()
        for (i in lines.indices) {
            if (lines[i].isFurigana) continue
            if (i >= lines.lastIndex) {
                filtered += lines[i]
                continue
            }

            val current = lines[i]
            val next = lines[i + 1]
            if (!(current.hasJpText && next.hasJpText)) {
                filtered += current
                continue
            }
            if (current.hasKanji || !next.hasKanji || next.isFurigana) {
                filtered += current
                continue
            }

            val passedPosition = if (isVertical) {
                val minHDist = abs(next.rect.width - current.rect.width) / 2.0
                val maxHDist = next.rect.width + current.rect.width / 2.0
                val hDist = current.rect.centerX - next.rect.centerX
                hDist > minHDist && hDist < maxHDist && verticalOverlap(current.rect, next.rect) > 0.4
            } else {
                val minVDist = abs(next.rect.height - current.rect.height) / 2.0
                val maxVDist = next.rect.height + current.rect.height / 2.0
                val vDist = next.rect.centerY - current.rect.centerY
                vDist > minVDist && vDist < maxVDist && horizontalOverlap(current.rect, next.rect) > 0.4
            }
            if (!passedPosition) {
                filtered += current
                continue
            }

            val passedSize = if (isVertical) {
                current.characterSize < next.characterSize * 0.85
            } else {
                current.rect.height < next.rect.height * 0.85
            }
            if (!passedSize) {
                filtered += current
            }
        }
        return filtered
    }

    private fun mergeCloseParagraphs(paragraphs: List<DesktopParagraphWithMeta>): List<DesktopMergedBlock> {
        if (paragraphs.size < 2) return paragraphs.map { it.block }

        val merged = mutableListOf<DesktopMergedBlock>()

        fun mergeOrientation(isVertical: Boolean) {
            val indices = paragraphs.indices.filter {
                (paragraphs[it].writingDirection == DesktopWritingDirection.TTB) == isVertical
            }
            if (indices.size <= 1) {
                indices.forEach { merged += paragraphs[it].block }
                return
            }

            val components = findConnectedComponents(
                items = indices.map { paragraphs[it] },
                shouldConnect = { a, b ->
                    val charSize = maxOf(a.characterSize, b.characterSize)
                    if (isVertical) {
                        verticalDistance(a.block.rect, b.block.rect) <= 2.0 * charSize &&
                            horizontalOverlap(a.block.rect, b.block.rect) > 0.9
                    } else {
                        a.writingDirection == b.writingDirection &&
                            horizontalDistance(a.block.rect, b.block.rect) <= 3.0 * charSize &&
                            verticalOverlap(a.block.rect, b.block.rect) > 0.9
                    }
                },
                getStartCoord = if (isVertical) {
                    { paragraph -> paragraph.block.rect.left }
                } else {
                    { paragraph -> paragraph.block.rect.top }
                },
                getEndCoord = if (isVertical) {
                    { paragraph -> paragraph.block.rect.right }
                } else {
                    { paragraph -> paragraph.block.rect.bottom }
                },
            )

            for (component in components) {
                val originalIndices = component.map { indices[it] }
                if (component.size == 1) {
                    merged += paragraphs[originalIndices[0]].block
                } else {
                    val componentParagraphs = originalIndices.map { paragraphs[it] }
                    val allLines = componentParagraphs.flatMap { it.sourceLines }
                    merged += createParagraphFromLines(
                        lines = allLines,
                        isVertical = isVertical,
                        isRtl = componentParagraphs.first().writingDirection == DesktopWritingDirection.RTL,
                        filterFurigana = false,
                    ).block
                }
            }
        }

        mergeOrientation(isVertical = true)
        mergeOrientation(isVertical = false)
        return merged
    }

    private fun groupParagraphsIntoRows(blocks: List<DesktopMergedBlock>): List<DesktopRow> {
        if (blocks.isEmpty()) return emptyList()
        if (blocks.size == 1) {
            val direction = blocks.first().writingDirection
            return listOf(DesktopRow(blocks, direction != DesktopWritingDirection.LTR))
        }

        val components = findConnectedComponents(
            items = blocks,
            shouldConnect = { a, b ->
                val overlapTop = maxOf(a.rect.top, b.rect.top)
                val overlapBottom = minOf(a.rect.bottom, b.rect.bottom)
                if (overlapBottom <= overlapTop) {
                    false
                } else {
                    val overlapHeight = overlapBottom - overlapTop
                    val smallerHeight = minOf(a.rect.height, b.rect.height)
                    overlapHeight / smallerHeight.coerceAtLeast(0.001) > 0.2
                }
            },
            getStartCoord = { it.rect.top },
            getEndCoord = { it.rect.bottom },
        )

        return components.map { component ->
            val rowBlocks = component.map { blocks[it] }
            val verticalOrRtlCount = rowBlocks.count { it.writingDirection != DesktopWritingDirection.LTR }
            DesktopRow(
                blocks = rowBlocks,
                isVerticalOrRtl = verticalOrRtlCount * 2 >= rowBlocks.size,
            )
        }
    }

    private fun reorderParagraphsInRows(rows: List<DesktopRow>): List<DesktopRow> {
        return rows.map { row ->
            if (row.blocks.size < 2) return@map row
            val sorted = row.blocks.sortedBy { it.rect.left }
            val reordered = if (row.isVerticalOrRtl) sorted.reversed() else sorted
            row.copy(blocks = reorderMixedOrientationBlocks(reordered, row.isVerticalOrRtl))
        }
    }

    private fun reorderMixedOrientationBlocks(
        blocks: List<DesktopMergedBlock>,
        isVerticalOrRtl: Boolean,
    ): List<DesktopMergedBlock> {
        if (blocks.size < 2) return blocks

        val result = mutableListOf<DesktopMergedBlock>()
        val currentBlock = mutableListOf(blocks[0])
        var currentOrientation = blocks[0].writingDirection == DesktopWritingDirection.TTB

        for (block in blocks.drop(1)) {
            val blockOrientation = block.writingDirection == DesktopWritingDirection.TTB
            if (blockOrientation == currentOrientation) {
                currentBlock += block
            } else {
                if (currentOrientation != isVerticalOrRtl) {
                    currentBlock.reverse()
                }
                result += currentBlock
                currentBlock.clear()
                currentBlock += block
                currentOrientation = blockOrientation
            }
        }

        if (currentOrientation != isVerticalOrRtl) {
            currentBlock.reverse()
        }
        result += currentBlock
        return result
    }

    private fun flattenRowsToParagraphs(rows: List<DesktopRow>): List<DesktopMergedBlock> {
        return rows.sortedBy { row -> row.blocks.minOf { it.rect.top } }
            .flatMap { it.blocks }
    }

    private fun <T> findConnectedComponents(
        items: List<T>,
        shouldConnect: (T, T) -> Boolean,
        getStartCoord: (T) -> Double,
        getEndCoord: (T) -> Double,
    ): List<List<Int>> {
        if (items.isEmpty()) return emptyList()
        if (items.size == 1) return listOf(listOf(0))

        val graph = Array(items.size) { mutableListOf<Int>() }
        val isReverseSweep = getStartCoord(items[0]) > getEndCoord(items[0])
        val sortedItems = items.indices.map { it to items[it] }
            .sortedBy { (_, item) -> getStartCoord(item) }
            .let { if (isReverseSweep) it.reversed() else it }
        val activeItems = mutableListOf<DesktopActiveItem<T>>()

        for ((originalIndex, item) in sortedItems) {
            val currentStart = getStartCoord(item)
            val lineEnd = getEndCoord(item)
            activeItems.retainAll { active ->
                if (isReverseSweep) active.end < currentStart else active.end > currentStart
            }
            for (active in activeItems) {
                if (shouldConnect(item, active.item)) {
                    graph[originalIndex] += active.index
                    graph[active.index] += originalIndex
                }
            }
            activeItems += DesktopActiveItem(originalIndex, item, lineEnd)
        }

        val visited = BooleanArray(items.size)
        val components = mutableListOf<List<Int>>()
        for (i in items.indices) {
            if (visited[i]) continue
            val component = mutableListOf<Int>()
            val queue = ArrayDeque<Int>()
            queue.add(i)
            visited[i] = true
            while (!queue.isEmpty()) {
                val node = queue.removeFirst()
                component += node
                for (neighbor in graph[node]) {
                    if (!visited[neighbor]) {
                        visited[neighbor] = true
                        queue.add(neighbor)
                    }
                }
            }
            components += component
        }

        return components
    }

    private fun horizontalOverlap(a: DesktopNormalizedRect, b: DesktopNormalizedRect): Double {
        val overlapLeft = maxOf(a.left, b.left)
        val overlapRight = minOf(a.right, b.right)
        if (overlapRight <= overlapLeft) return 0.0
        val smallerWidth = minOf(a.width, b.width)
        return if (smallerWidth > 0.0) (overlapRight - overlapLeft) / smallerWidth else 0.0
    }

    private fun verticalOverlap(a: DesktopNormalizedRect, b: DesktopNormalizedRect): Double {
        val overlapTop = maxOf(a.top, b.top)
        val overlapBottom = minOf(a.bottom, b.bottom)
        if (overlapBottom <= overlapTop) return 0.0
        val smallerHeight = minOf(a.height, b.height)
        return if (smallerHeight > 0.0) (overlapBottom - overlapTop) / smallerHeight else 0.0
    }

    private fun horizontalDistance(a: DesktopNormalizedRect, b: DesktopNormalizedRect): Double {
        return when {
            a.right < b.left -> b.left - a.right
            b.right < a.left -> a.left - b.right
            else -> 0.0
        }
    }

    private fun verticalDistance(a: DesktopNormalizedRect, b: DesktopNormalizedRect): Double {
        return when {
            a.bottom < b.top -> b.top - a.bottom
            b.bottom < a.top -> a.top - b.bottom
            else -> 0.0
        }
    }

    private fun DesktopMergedBlock.toReaderOcrBlock(languageCode: String): ChimahonReaderOcrBlock? {
        val linePairs = lines.zip(lineRects).mapNotNull { (line, rect) ->
            val cleanLine = line.trim()
            val safeRect = rect.clampedToUnit() ?: return@mapNotNull null
            if (cleanLine.isBlank()) null else cleanLine to safeRect
        }
        if (linePairs.isEmpty()) return null

        val blockRect = linePairs.map { it.second }.unionNormalizedRects()
            ?: rect.clampedToUnit()
            ?: return null
        val lineGeometries = linePairs.map { (_, rect) -> rect.toReaderLineGeometry() }

        return ChimahonReaderOcrBlock(
            xmin = blockRect.left.toFloat(),
            ymin = blockRect.top.toFloat(),
            xmax = blockRect.right.toFloat(),
            ymax = blockRect.bottom.toFloat(),
            lines = linePairs.map { it.first },
            vertical = writingDirection == DesktopWritingDirection.TTB,
            lineGeometries = lineGeometries,
            language = languageCode,
        )
    }

    private fun DesktopCenterRotatedBox.toSafeNormalizedRect(): DesktopNormalizedRect? {
        val cx = centerX.toDouble()
        val cy = centerY.toDouble()
        val boxWidth = abs(width.toDouble())
        val boxHeight = abs(height.toDouble())
        val rotation = rotationZ.toDouble()
        if (!cx.isFinite() || !cy.isFinite() || !boxWidth.isFinite() || !boxHeight.isFinite() || !rotation.isFinite()) {
            return null
        }
        if (boxWidth <= 0.0 || boxHeight <= 0.0) return null

        val left = (cx - boxWidth / 2.0).coerceIn(0.0, 1.0)
        val top = (cy - boxHeight / 2.0).coerceIn(0.0, 1.0)
        val right = (cx + boxWidth / 2.0).coerceIn(0.0, 1.0)
        val bottom = (cy + boxHeight / 2.0).coerceIn(0.0, 1.0)

        return DesktopNormalizedRect(
            left = minOf(left, right),
            top = minOf(top, bottom),
            right = maxOf(left, right),
            bottom = maxOf(top, bottom),
            rotationRadians = rotation,
        ).takeIf { it.isUsable }
    }

    private fun List<DesktopNormalizedRect>.unionNormalizedRects(): DesktopNormalizedRect? {
        if (isEmpty()) return null
        return DesktopNormalizedRect(
            left = minOf { it.left },
            top = minOf { it.top },
            right = maxOf { it.right },
            bottom = maxOf { it.bottom },
            rotationRadians = first().rotationRadians,
        ).takeIf { it.isUsable }
    }

    private fun DesktopNormalizedRect.clampedToUnit(): DesktopNormalizedRect? {
        return DesktopNormalizedRect(
            left = left.coerceIn(0.0, 1.0),
            top = top.coerceIn(0.0, 1.0),
            right = right.coerceIn(0.0, 1.0),
            bottom = bottom.coerceIn(0.0, 1.0),
            rotationRadians = rotationRadians,
        ).let { rect ->
            rect.copy(
                left = minOf(rect.left, rect.right),
                top = minOf(rect.top, rect.bottom),
                right = maxOf(rect.left, rect.right),
                bottom = maxOf(rect.top, rect.bottom),
            )
        }.takeIf { it.isUsable }
    }

    private fun DesktopNormalizedRect.toReaderLineGeometry(): ChimahonReaderOcrLineGeometry {
        return ChimahonReaderOcrLineGeometry(
            xmin = left.toFloat(),
            ymin = top.toFloat(),
            xmax = right.toFloat(),
            ymax = bottom.toFloat(),
            rotation = Math.toDegrees(rotationRadians).toFloat(),
        )
    }

    private fun cleanDesktopOcrText(text: String?): String {
        if (text.isNullOrBlank()) return text.orEmpty()
        return text.replace(complexPunctuationRegex) { match ->
            val value = match.value
            when {
                value.any { it == '.' || it == '\u2026' || it == '\u22EE' || it == '\uff0e' || it == '\uff65' } -> "\u2026"
                value.any { it == '?' || it == '\uff1f' } -> "?"
                else -> "!"
            }
        }.replace(KATAKANA_MIDDLE_DOTS, "\u2026")
    }

    private fun String.normalizedRequestedLanguageCode(): String {
        return normalizedLanguageCodeOrNull() ?: DEFAULT_LANGUAGE_CODE
    }

    private fun String.normalizedDetectedLanguageCodeOrNull(): String? {
        return normalizedLanguageCodeOrNull()
            ?.takeUnless { it == "und" || it == "mul" || it == "unknown" }
    }

    private fun String.normalizedLanguageCodeOrNull(): String? {
        val normalized = trim().replace('_', '-').lowercase()
        if (normalized.isBlank()) return null
        val primary = normalized.substringBefore('-')
        return when (primary) {
            "ja" -> normalized
            "jp", "jpn", "japanese" -> "ja"
            "zh" -> normalized
            "cn", "zho", "chi", "chinese" -> "zh"
            "yue", "cantonese" -> "yue"
            "ko" -> normalized
            "kr", "kor", "korean" -> "ko"
            "en" -> normalized
            "gb", "uk", "us", "eng", "english" -> "en"
            "ar", "arabic" -> "ar"
            "he", "hebrew" -> "he"
            else -> normalized
        }
    }

    private fun desktopOcrLanguage(requestedCode: String, detectedCode: String): DesktopOcrLanguage {
        val requested = requestedCode.normalizedRequestedLanguageCode()
        val output = detectedCode.normalizedDetectedLanguageCodeOrNull() ?: requested
        val primary = requested.substringBefore('-')
        val prefersVertical = primary == "ja" || primary == "zh" || primary == "yue"
        return DesktopOcrLanguage(
            outputCode = output,
            prefersVertical = prefersVertical,
            prefersNoSpace = prefersVertical,
            isJapanese = primary == "ja",
            isChinese = primary == "zh" || primary == "yue",
            isRtl = primary == "ar" || primary == "he",
        )
    }

    private data class DesktopProcessedImage(
        val bytes: ByteArray,
        val width: Int,
        val height: Int,
    )

    private enum class DesktopWritingDirection { LTR, TTB, RTL }

    private data class DesktopOcrLanguage(
        val outputCode: String,
        val prefersVertical: Boolean,
        val prefersNoSpace: Boolean,
        val isJapanese: Boolean,
        val isChinese: Boolean,
        val isRtl: Boolean,
    )

    private data class DesktopRecognizedLine(
        val text: String,
        val rect: DesktopNormalizedRect,
        val writingDirection: DesktopWritingDirection,
        val characterSize: Double,
        val hasJpText: Boolean,
        val hasKanji: Boolean,
    )

    private data class DesktopLineDict(
        val text: String,
        val rect: DesktopNormalizedRect,
        val isVertical: Boolean,
        val isRtl: Boolean,
        val characterSize: Double,
        val hasJpText: Boolean,
        val hasKanji: Boolean,
        var isFurigana: Boolean = false,
    )

    private data class DesktopMergedBlock(
        val lines: List<String>,
        val rect: DesktopNormalizedRect,
        val writingDirection: DesktopWritingDirection,
        val lineRects: List<DesktopNormalizedRect>,
    )

    private data class DesktopParagraphWithMeta(
        val block: DesktopMergedBlock,
        val writingDirection: DesktopWritingDirection,
        val characterSize: Double,
        val sourceLines: List<DesktopLineDict>,
    )

    private data class DesktopRow(
        val blocks: List<DesktopMergedBlock>,
        val isVerticalOrRtl: Boolean,
    )

    private data class DesktopActiveItem<T>(
        val index: Int,
        val item: T,
        val end: Double,
    )

    private data class DesktopNormalizedRect(
        val left: Double,
        val top: Double,
        val right: Double,
        val bottom: Double,
        val rotationRadians: Double,
    ) {
        val width: Double
            get() = right - left
        val height: Double
            get() = bottom - top
        val centerX: Double
            get() = (left + right) / 2.0
        val centerY: Double
            get() = (top + bottom) / 2.0
        val isUsable: Boolean
            get() = left.isFinite() &&
                top.isFinite() &&
                right.isFinite() &&
                bottom.isFinite() &&
                rotationRadians.isFinite() &&
                width > MIN_NORMALIZED_BOX_EDGE &&
                height > MIN_NORMALIZED_BOX_EDGE
        val cacheKey: String
            get() = "$left,$top,$right,$bottom,$rotationRadians"
    }
}

@Serializable
private data class DesktopLensOverlayServerRequest(
    @ProtoNumber(1) val objectsRequest: DesktopLensOverlayObjectsRequest? = null,
)

@Serializable
private data class DesktopLensOverlayObjectsRequest(
    @ProtoNumber(1) val requestContext: DesktopLensOverlayRequestContext? = null,
    @ProtoNumber(3) val imageData: DesktopLensImageData? = null,
)

@Serializable
private data class DesktopLensOverlayRequestContext(
    @ProtoNumber(3) val requestId: DesktopLensOverlayRequestId? = null,
    @ProtoNumber(4) val clientContext: DesktopLensOverlayClientContext? = null,
)

@Serializable
private data class DesktopLensOverlayRequestId(
    @ProtoNumber(1) val uuid: Long = 0L,
    @ProtoNumber(2) val sequenceId: Int = 1,
    @ProtoNumber(3) val imageSequenceId: Int = 1,
)

@Serializable
private data class DesktopLensOverlayClientContext(
    @ProtoNumber(1) val platform: Int = 0,
    @ProtoNumber(2) val surface: Int = 0,
    @ProtoNumber(4) val localeContext: DesktopLensLocaleContext? = null,
)

@Serializable
private data class DesktopLensLocaleContext(
    @ProtoNumber(1) val language: String = "",
    @ProtoNumber(2) val region: String = "",
    @ProtoNumber(3) val timeZone: String = "",
)

@Serializable
private data class DesktopLensImageData(
    @ProtoNumber(1) val payload: DesktopLensImagePayload? = null,
    @ProtoNumber(3) val imageMetadata: DesktopLensImageMetadata? = null,
)

@Serializable
private data class DesktopLensImagePayload(
    @ProtoNumber(1) val imageBytes: ByteArray = byteArrayOf(),
) {
    override fun equals(other: Any?): Boolean {
        return other is DesktopLensImagePayload && imageBytes.contentEquals(other.imageBytes)
    }

    override fun hashCode(): Int {
        return imageBytes.contentHashCode()
    }
}

@Serializable
private data class DesktopLensImageMetadata(
    @ProtoNumber(1) val width: Int = 0,
    @ProtoNumber(2) val height: Int = 0,
)

@Serializable
private data class DesktopLensOverlayServerResponse(
    @ProtoNumber(2) val objectsResponse: DesktopLensOverlayObjectsResponse? = null,
)

@Serializable
private data class DesktopLensOverlayObjectsResponse(
    @ProtoNumber(3) val text: DesktopLensText? = null,
)

@Serializable
private data class DesktopLensText(
    @ProtoNumber(1) val textLayout: DesktopTextLayout? = null,
    @ProtoNumber(2) val contentLanguage: String = "",
)

@Serializable
private data class DesktopTextLayout(
    @ProtoNumber(1) val paragraphs: List<DesktopTextLayoutParagraph> = emptyList(),
)

@Serializable
private data class DesktopTextLayoutParagraph(
    @ProtoNumber(2) val lines: List<DesktopTextLayoutLine> = emptyList(),
    @ProtoNumber(3) val geometry: DesktopGeometry? = null,
)

@Serializable
private data class DesktopTextLayoutLine(
    @ProtoNumber(1) val words: List<DesktopTextLayoutWord> = emptyList(),
    @ProtoNumber(2) val geometry: DesktopGeometry? = null,
)

@Serializable
private data class DesktopTextLayoutWord(
    @ProtoNumber(2) val plainText: String = "",
    @ProtoNumber(3) val textSeparator: String? = null,
    @ProtoNumber(4) val geometry: DesktopGeometry? = null,
)

@Serializable
private data class DesktopGeometry(
    @ProtoNumber(1) val boundingBox: DesktopCenterRotatedBox? = null,
)

@Serializable
private data class DesktopCenterRotatedBox(
    @ProtoNumber(1) val centerX: Float = 0f,
    @ProtoNumber(2) val centerY: Float = 0f,
    @ProtoNumber(3) val width: Float = 0f,
    @ProtoNumber(4) val height: Float = 0f,
    @ProtoNumber(5) val rotationZ: Float = 0f,
)
