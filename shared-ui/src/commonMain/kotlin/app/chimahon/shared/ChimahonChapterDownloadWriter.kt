package app.chimahon.shared

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import okio.FileSystem
import okio.Path
import kotlin.random.Random

data class ChimahonChapterDownloadMetadata(
    val sourceName: String,
    val mangaTitle: String,
    val chapterName: String,
    val scanlator: String? = null,
    val collisionSuffix: String? = null,
) {
    init {
        require(sourceName.isNotBlank()) { "Source name must not be blank." }
        require(mangaTitle.isNotBlank()) { "Manga title must not be blank." }
        require(chapterName.isNotBlank()) { "Chapter name must not be blank." }
        require(
            collisionSuffix == null ||
                collisionSuffix.length == CHAPTER_COLLISION_SUFFIX_LENGTH &&
                collisionSuffix.all(Char::isLetterOrDigit),
        ) {
            "Collision suffix must contain exactly $CHAPTER_COLLISION_SUFFIX_LENGTH letters or digits."
        }
    }
}

data class ChimahonChapterPagePayload(
    val bytes: ByteArray,
    val fileExtension: String? = null,
) {
    init {
        require(bytes.isNotEmpty()) { "Chapter page payload must not be empty." }
    }

    override fun equals(other: Any?): Boolean {
        return other is ChimahonChapterPagePayload &&
            bytes.contentEquals(other.bytes) &&
            fileExtension == other.fileExtension
    }

    override fun hashCode(): Int {
        return 31 * bytes.contentHashCode() + (fileExtension?.hashCode() ?: 0)
    }
}

enum class ChimahonChapterDownloadStage {
    Preparing,
    WritingPages,
    Committing,
    Complete,
}

data class ChimahonChapterDownloadProgress(
    val stage: ChimahonChapterDownloadStage,
    val pagesWritten: Int,
    val totalPages: Int,
    val bytesWritten: Long,
    val totalBytes: Long,
) {
    val fraction: Float
        get() = when {
            totalBytes > 0L -> (bytesWritten.toDouble() / totalBytes.toDouble()).toFloat()
            totalPages > 0 -> pagesWritten.toFloat() / totalPages.toFloat()
            stage == ChimahonChapterDownloadStage.Complete -> 1f
            else -> 0f
        }.coerceIn(0f, 1f)
}

sealed interface ChimahonChapterDownloadResult {
    val chapterPath: Path
    val pagesWritten: Int
    val bytesWritten: Long

    data class Success(
        override val chapterPath: Path,
        override val pagesWritten: Int,
        override val bytesWritten: Long,
        val replacedExistingDownload: Boolean,
    ) : ChimahonChapterDownloadResult

    data class Failure(
        override val chapterPath: Path,
        override val pagesWritten: Int,
        override val bytesWritten: Long,
        val message: String,
        val cause: Throwable,
    ) : ChimahonChapterDownloadResult
}

sealed interface ChimahonDownloadRemovalResult {
    val path: Path

    data class Removed(
        override val path: Path,
        val filesRemoved: Int,
        val bytesRemoved: Long,
    ) : ChimahonDownloadRemovalResult

    data class NotFound(
        override val path: Path,
    ) : ChimahonDownloadRemovalResult

    data class Failure(
        override val path: Path,
        val message: String,
        val cause: Throwable,
    ) : ChimahonDownloadRemovalResult
}

class ChimahonChapterDownloadWriter(
    private val downloadsRoot: Path,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {
    private val mutex = Mutex()

    fun chapterPath(metadata: ChimahonChapterDownloadMetadata): Path {
        return mangaPath(metadata.sourceName, metadata.mangaTitle) /
            chapterDirectoryName(metadata)
    }

    fun mangaPath(sourceName: String, mangaTitle: String): Path {
        return sourcePath(sourceName) / safeDownloadName(mangaTitle)
    }

    fun sourcePath(sourceName: String): Path {
        return downloadsRoot / safeDownloadName(sourceName)
    }

    suspend fun write(
        metadata: ChimahonChapterDownloadMetadata,
        pages: List<ChimahonChapterPagePayload>,
        onProgress: (ChimahonChapterDownloadProgress) -> Unit = {},
    ): ChimahonChapterDownloadResult {
        val target = chapterPath(metadata)
        if (pages.isEmpty()) {
            val cause = IllegalArgumentException("A chapter download must contain at least one page.")
            return ChimahonChapterDownloadResult.Failure(
                chapterPath = target,
                pagesWritten = 0,
                bytesWritten = 0L,
                message = cause.message.orEmpty(),
                cause = cause,
            )
        }

        mutex.lock()
        try {
            return writeLocked(target, pages, onProgress)
        } finally {
            mutex.unlock()
        }
    }

    suspend fun removeChapter(
        metadata: ChimahonChapterDownloadMetadata,
    ): ChimahonDownloadRemovalResult {
        val target = chapterPath(metadata)
        mutex.lock()
        try {
            val result = removeLocked(target)
            if (result is ChimahonDownloadRemovalResult.Removed) {
                pruneEmptyParents(target.parent, stopAt = downloadsRoot)
            }
            return result
        } finally {
            mutex.unlock()
        }
    }

    suspend fun removeManga(
        sourceName: String,
        mangaTitle: String,
    ): ChimahonDownloadRemovalResult {
        val target = mangaPath(sourceName, mangaTitle)
        mutex.lock()
        try {
            val result = removeLocked(target)
            if (result is ChimahonDownloadRemovalResult.Removed) {
                pruneEmptyParents(target.parent, stopAt = downloadsRoot)
            }
            return result
        } finally {
            mutex.unlock()
        }
    }

    suspend fun removeSource(sourceName: String): ChimahonDownloadRemovalResult {
        val target = sourcePath(sourceName)
        mutex.lock()
        try {
            return removeLocked(target)
        } finally {
            mutex.unlock()
        }
    }

    private suspend fun writeLocked(
        target: Path,
        pages: List<ChimahonChapterPagePayload>,
        onProgress: (ChimahonChapterDownloadProgress) -> Unit,
    ): ChimahonChapterDownloadResult {
        val parent = checkNotNull(target.parent) { "Chapter download path has no parent." }
        val staging = uniqueSibling(target, TEMPORARY_SUFFIX)
        val backup = uniqueSibling(target, BACKUP_SUFFIX)
        val totalBytes = pages.sumOf { it.bytes.size.toLong() }
        var pagesWritten = 0
        var bytesWritten = 0L
        var backupCreated = false
        val replacedExisting = fileSystem.exists(target)

        try {
            currentCoroutineContext().ensureActive()
            onProgress(
                progress(
                    stage = ChimahonChapterDownloadStage.Preparing,
                    pagesWritten = 0,
                    totalPages = pages.size,
                    bytesWritten = 0L,
                    totalBytes = totalBytes,
                ),
            )
            fileSystem.createDirectories(parent)
            fileSystem.createDirectories(staging)

            val pageNumberWidth = maxOf(MINIMUM_PAGE_NUMBER_WIDTH, pages.size.toString().length)
            pages.forEachIndexed { index, page ->
                currentCoroutineContext().ensureActive()
                val extension = page.imageExtension()
                val pageName = (index + 1).toString().padStart(pageNumberWidth, '0')
                fileSystem.write(staging / "$pageName.$extension") {
                    write(page.bytes)
                }
                pagesWritten += 1
                bytesWritten += page.bytes.size
                onProgress(
                    progress(
                        stage = ChimahonChapterDownloadStage.WritingPages,
                        pagesWritten = pagesWritten,
                        totalPages = pages.size,
                        bytesWritten = bytesWritten,
                        totalBytes = totalBytes,
                    ),
                )
            }

            currentCoroutineContext().ensureActive()
            onProgress(
                progress(
                    stage = ChimahonChapterDownloadStage.Committing,
                    pagesWritten = pagesWritten,
                    totalPages = pages.size,
                    bytesWritten = bytesWritten,
                    totalBytes = totalBytes,
                ),
            )

            if (replacedExisting) {
                fileSystem.atomicMove(target, backup)
                backupCreated = true
            }
            try {
                fileSystem.atomicMove(staging, target)
            } catch (commitFailure: Throwable) {
                if (backupCreated && !fileSystem.exists(target)) {
                    runCatching { fileSystem.atomicMove(backup, target) }
                }
                throw commitFailure
            }
            if (backupCreated) {
                deleteIfPresent(backup)
            }

            val result = ChimahonChapterDownloadResult.Success(
                chapterPath = target,
                pagesWritten = pagesWritten,
                bytesWritten = bytesWritten,
                replacedExistingDownload = replacedExisting,
            )
            runCatching {
                onProgress(
                    progress(
                        stage = ChimahonChapterDownloadStage.Complete,
                        pagesWritten = pagesWritten,
                        totalPages = pages.size,
                        bytesWritten = bytesWritten,
                        totalBytes = totalBytes,
                    ),
                )
            }
            return result
        } catch (cancellation: CancellationException) {
            deleteIfPresent(staging)
            if (backupCreated && !fileSystem.exists(target)) {
                runCatching { fileSystem.atomicMove(backup, target) }
            }
            throw cancellation
        } catch (failure: Throwable) {
            deleteIfPresent(staging)
            if (backupCreated && !fileSystem.exists(target)) {
                runCatching { fileSystem.atomicMove(backup, target) }
            }
            return ChimahonChapterDownloadResult.Failure(
                chapterPath = target,
                pagesWritten = pagesWritten,
                bytesWritten = bytesWritten,
                message = failure.message ?: "Chapter download could not be written.",
                cause = failure,
            )
        } finally {
            if (fileSystem.exists(staging)) {
                deleteIfPresent(staging)
            }
            if (backupCreated && fileSystem.exists(target) && fileSystem.exists(backup)) {
                deleteIfPresent(backup)
            }
        }
    }

    private suspend fun removeLocked(path: Path): ChimahonDownloadRemovalResult {
        currentCoroutineContext().ensureActive()
        if (!fileSystem.exists(path)) return ChimahonDownloadRemovalResult.NotFound(path)

        return try {
            val stats = treeStats(path)
            fileSystem.deleteRecursively(path)
            ChimahonDownloadRemovalResult.Removed(
                path = path,
                filesRemoved = stats.fileCount,
                bytesRemoved = stats.bytes,
            )
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (failure: Throwable) {
            ChimahonDownloadRemovalResult.Failure(
                path = path,
                message = failure.message ?: "Download could not be removed.",
                cause = failure,
            )
        }
    }

    private fun chapterDirectoryName(metadata: ChimahonChapterDownloadMetadata): String {
        val rawName = if (metadata.scanlator.isNullOrBlank()) {
            metadata.chapterName
        } else {
            "${metadata.scanlator}_${metadata.chapterName}"
        }
        val baseName = safeDownloadName(rawName, MAX_CHAPTER_NAME_BYTES)
        return metadata.collisionSuffix?.let { "${baseName}_$it" } ?: baseName
    }

    private fun uniqueSibling(target: Path, suffix: String): Path {
        val parent = checkNotNull(target.parent) { "Download path has no parent." }
        repeat(UNIQUE_PATH_ATTEMPTS) {
            val candidate = parent / "${target.name}$suffix-${Random.nextInt(Int.MAX_VALUE)}"
            if (!fileSystem.exists(candidate)) return candidate
        }
        error("Unable to allocate a temporary download path beside $target.")
    }

    private fun pruneEmptyParents(start: Path?, stopAt: Path) {
        var current = start
        while (current != null && current != stopAt && current != downloadsRoot) {
            if (!fileSystem.exists(current) || fileSystem.list(current).isNotEmpty()) return
            fileSystem.delete(current)
            current = current.parent
        }
    }

    private fun deleteIfPresent(path: Path) {
        if (fileSystem.exists(path)) {
            runCatching { fileSystem.deleteRecursively(path) }
        }
    }

    private fun treeStats(path: Path): DownloadTreeStats {
        val metadata = fileSystem.metadata(path)
        if (!metadata.isDirectory) {
            return DownloadTreeStats(
                fileCount = 1,
                bytes = metadata.size ?: 0L,
            )
        }

        return fileSystem.list(path)
            .map(::treeStats)
            .fold(DownloadTreeStats()) { total, child ->
                DownloadTreeStats(
                    fileCount = total.fileCount + child.fileCount,
                    bytes = total.bytes + child.bytes,
                )
            }
    }
}

private data class DownloadTreeStats(
    val fileCount: Int = 0,
    val bytes: Long = 0L,
)

private fun progress(
    stage: ChimahonChapterDownloadStage,
    pagesWritten: Int,
    totalPages: Int,
    bytesWritten: Long,
    totalBytes: Long,
): ChimahonChapterDownloadProgress {
    return ChimahonChapterDownloadProgress(
        stage = stage,
        pagesWritten = pagesWritten,
        totalPages = totalPages,
        bytesWritten = bytesWritten,
        totalBytes = totalBytes,
    )
}

private fun ChimahonChapterPagePayload.imageExtension(): String {
    val requested = fileExtension
        ?.trim()
        ?.trimStart('.')
        ?.lowercase()
        ?.let { if (it == "jpeg") "jpg" else it }
        ?.takeIf { it in SUPPORTED_IMAGE_EXTENSIONS }
    return requested ?: sniffImageExtension(bytes)
}

private fun sniffImageExtension(bytes: ByteArray): String {
    return when {
        bytes.startsWith(0x89, 0x50, 0x4e, 0x47) -> "png"
        bytes.startsWith(0xff, 0xd8, 0xff) -> "jpg"
        bytes.startsWithAscii("GIF87a") || bytes.startsWithAscii("GIF89a") -> "gif"
        bytes.size >= 12 &&
            bytes.sliceEquals(0, "RIFF") &&
            bytes.sliceEquals(8, "WEBP") -> "webp"
        bytes.startsWith(0x42, 0x4d) -> "bmp"
        bytes.size >= 12 &&
            bytes.sliceEquals(4, "ftyp") &&
            (bytes.sliceEquals(8, "avif") || bytes.sliceEquals(8, "avis")) -> "avif"
        else -> DEFAULT_IMAGE_EXTENSION
    }
}

private fun ByteArray.startsWith(vararg prefix: Int): Boolean {
    if (size < prefix.size) return false
    return prefix.indices.all { index -> this[index].toInt() and 0xff == prefix[index] }
}

private fun ByteArray.startsWithAscii(prefix: String): Boolean {
    return sliceEquals(0, prefix)
}

private fun ByteArray.sliceEquals(offset: Int, value: String): Boolean {
    val encoded = value.encodeToByteArray()
    if (size < offset + encoded.size) return false
    return encoded.indices.all { index -> this[offset + index] == encoded[index] }
}

private fun safeDownloadName(
    input: String,
    maxBytes: Int = MAX_FILE_NAME_BYTES,
): String {
    val trimmed = input.trim('.', ' ')
    if (trimmed.isEmpty()) return INVALID_FILE_NAME
    val sanitized = buildString(trimmed.length) {
        trimmed.forEach { character ->
            append(
                when {
                    character.code in 0x00..0x1f -> '_'
                    character == '\u007f' -> '_'
                    character in INVALID_FILE_NAME_CHARACTERS -> '_'
                    else -> character
                },
            )
        }
    }
    if (sanitized.encodeToByteArray().size <= maxBytes) return sanitized

    var byteCount = 0
    return buildString {
        sanitized.forEach { character ->
            val characterBytes = character.toString().encodeToByteArray().size
            if (byteCount + characterBytes > maxBytes) return@buildString
            append(character)
            byteCount += characterBytes
        }
    }.ifBlank { INVALID_FILE_NAME }
}

private const val CHAPTER_COLLISION_SUFFIX_LENGTH = 6
private const val MAX_FILE_NAME_BYTES = 240
private const val MAX_CHAPTER_NAME_BYTES = MAX_FILE_NAME_BYTES - 11
private const val MINIMUM_PAGE_NUMBER_WIDTH = 3
private const val UNIQUE_PATH_ATTEMPTS = 32
private const val TEMPORARY_SUFFIX = ".tmp"
private const val BACKUP_SUFFIX = ".previous"
private const val DEFAULT_IMAGE_EXTENSION = "jpg"
private const val INVALID_FILE_NAME = "(invalid)"
private val INVALID_FILE_NAME_CHARACTERS = setOf('"', '*', '/', ':', '<', '>', '?', '\\', '|')
private val SUPPORTED_IMAGE_EXTENSIONS = setOf("avif", "bmp", "gif", "jpg", "png", "webp")
