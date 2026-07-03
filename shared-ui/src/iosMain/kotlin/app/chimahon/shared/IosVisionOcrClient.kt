package app.chimahon.shared

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRect
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.Vision.VNImageRequestHandler
import platform.Vision.VNRecognizeTextRequest
import platform.Vision.VNRecognizedText
import platform.Vision.VNRecognizedTextObservation
import platform.Vision.VNRequestTextRecognitionLevelAccurate

@OptIn(ExperimentalForeignApi::class)
internal object IosVisionOcrClient {
    fun recognize(bytes: ByteArray, languageCode: String): List<ChimahonReaderOcrBlock> {
        if (bytes.isEmpty()) return emptyList()
        val requestedLanguages = languageCode.visionRecognitionLanguages()

        return runCatching {
            val request = VNRecognizeTextRequest()
            request.recognitionLevel = VNRequestTextRecognitionLevelAccurate
            request.usesLanguageCorrection = false
            request.preferBackgroundProcessing = true
            request.automaticallyDetectsLanguage = requestedLanguages.isEmpty()
            if (requestedLanguages.isNotEmpty()) {
                request.recognitionLanguages = requestedLanguages
            }

            val handler = VNImageRequestHandler(bytes.toNSData(), options = emptyMap<Any?, Any>())
            if (!handler.performRequests(listOf(request), null)) {
                return@runCatching emptyList()
            }

            request.results
                .orEmpty()
                .filterIsInstance<VNRecognizedTextObservation>()
                .mapNotNull { observation -> observation.toReaderOcrBlock(languageCode) }
                .sortedWith(
                    compareBy<ChimahonReaderOcrBlock> { it.ymin }
                        .thenBy { it.xmin },
                )
        }.getOrDefault(emptyList())
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData {
    return usePinned { pinned ->
        NSData.dataWithBytes(bytes = pinned.addressOf(0), length = size.toULong())
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun VNRecognizedTextObservation.toReaderOcrBlock(languageCode: String): ChimahonReaderOcrBlock? {
    val text = topCandidates(1u)
        .firstOrNull()
        ?.let { it as? VNRecognizedText }
        ?.string
        ?.trim()
        .orEmpty()
    if (text.isBlank()) return null

    val rect = boundingBox.toTopLeftRect()
    if (!rect.isUsable) return null

    val lines = text
        .lineSequence()
        .map(String::trim)
        .filter(String::isNotEmpty)
        .toList()
        .ifEmpty { listOf(text) }

    return ChimahonReaderOcrBlock(
        xmin = rect.xmin,
        ymin = rect.ymin,
        xmax = rect.xmax,
        ymax = rect.ymax,
        lines = lines,
        vertical = rect.height > rect.width * 1.25f && languageCode.isVerticalScriptHint(),
        lineGeometries = listOf(
            ChimahonReaderOcrLineGeometry(
                xmin = rect.xmin,
                ymin = rect.ymin,
                xmax = rect.xmax,
                ymax = rect.ymax,
            ),
        ),
        language = languageCode.trim(),
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun kotlinx.cinterop.CValue<CGRect>.toTopLeftRect(): IosVisionRect {
    return useContents {
        val left = origin.x.toFloat().coerceIn(0f, 1f)
        val bottom = origin.y.toFloat().coerceIn(0f, 1f)
        val right = (origin.x + size.width).toFloat().coerceIn(0f, 1f)
        val top = (origin.y + size.height).toFloat().coerceIn(0f, 1f)
        IosVisionRect(
            xmin = minOf(left, right),
            ymin = (1f - maxOf(bottom, top)).coerceIn(0f, 1f),
            xmax = maxOf(left, right),
            ymax = (1f - minOf(bottom, top)).coerceIn(0f, 1f),
        )
    }
}

private data class IosVisionRect(
    val xmin: Float,
    val ymin: Float,
    val xmax: Float,
    val ymax: Float,
) {
    val width: Float get() = xmax - xmin
    val height: Float get() = ymax - ymin
    val isUsable: Boolean get() = width > 0.001f && height > 0.001f
}

private fun String.visionRecognitionLanguages(): List<String> {
    val normalized = trim().replace('_', '-')
    if (normalized.isBlank()) return emptyList()
    val primary = normalized.substringBefore('-').lowercase()
    return when (primary) {
        "ja" -> listOf("ja-JP", "ja")
        "zh" -> listOf(normalized, "zh-Hans", "zh-Hant", "zh")
        "ko" -> listOf("ko-KR", "ko")
        "en" -> listOf("en-US", "en")
        else -> listOf(normalized)
    }.distinct()
}

private fun String.isVerticalScriptHint(): Boolean {
    return trim()
        .substringBefore('-')
        .lowercase() in setOf("ja", "zh", "ko")
}
