package app.chimahon.shared

import okio.FileSystem
import okio.Path
import tachiyomi.data.Chapters
import tachiyomi.data.Mangas

internal data class ChimahonDownloadedIndex(
    val chapterPaths: Map<Long, Path>,
    val chapterSizes: Map<Long, Long>,
    val mangaIds: Set<Long>,
) {
    val chapterIds: Set<Long>
        get() = chapterPaths.keys

    companion object {
        val Empty = ChimahonDownloadedIndex(
            chapterPaths = emptyMap(),
            chapterSizes = emptyMap(),
            mangaIds = emptySet(),
        )
    }
}

internal data class ChimahonFileTreeStats(
    val exists: Boolean,
    val sizeBytes: Long,
    val fileCount: Int,
    val directoryCount: Int,
)

internal class ChimahonDownloadStorage(
    private val downloadsDirectory: Path,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {
    fun scanDownloadedChapters(
        manga: List<Mangas>,
        chapters: List<Chapters>,
        sourceDirectoryNames: Map<Long, Set<String>>,
    ): ChimahonDownloadedIndex {
        val sourceDirectories = childDirectories(downloadsDirectory)
        if (sourceDirectories.isEmpty()) return ChimahonDownloadedIndex.Empty

        val allMangaDirectories = sourceDirectories.flatMap(::childDirectories)
        val chaptersByMangaId = chapters.groupBy(Chapters::manga_id)
        val chapterPaths = mutableMapOf<Long, Path>()
        val chapterSizes = mutableMapOf<Long, Long>()

        manga.forEach { mangaEntry ->
            val sourceNames = sourceDirectoryNames[mangaEntry.source]
                .orEmpty()
                .map(::validFileName)
                .map(String::lowercase)
                .toSet()
            val preferredSourceDirectories = sourceDirectories.filter { directory ->
                directory.name.lowercase() in sourceNames
            }
            val mangaDirectoryName = validFileName(mangaEntry.title)
            val mangaDirectories = (
                preferredSourceDirectories.flatMap(::childDirectories) +
                    allMangaDirectories
                )
                .distinct()
                .filter { it.name.equals(mangaDirectoryName, ignoreCase = true) }
            if (mangaDirectories.isEmpty()) return@forEach

            val diskChapterPaths = mangaDirectories
                .flatMap(::children)
                .filterNot { it.name == ".nomedia" || it.name.endsWith(".tmp") }
                .distinct()
            chaptersByMangaId[mangaEntry._id].orEmpty().forEach { chapter ->
                val matchingPath = diskChapterPaths.firstOrNull { path ->
                    path.matchesChapter(chapter)
                } ?: return@forEach
                chapterPaths[chapter._id] = matchingPath
                chapterSizes[chapter._id] = treeStats(matchingPath).sizeBytes
            }
        }

        return ChimahonDownloadedIndex(
            chapterPaths = chapterPaths,
            chapterSizes = chapterSizes,
            mangaIds = chapters
                .asSequence()
                .filter { it._id in chapterPaths }
                .map(Chapters::manga_id)
                .toSet(),
        )
    }

    fun treeStats(path: Path = downloadsDirectory): ChimahonFileTreeStats {
        val metadata = runCatching { fileSystem.metadata(path) }.getOrNull()
            ?: return ChimahonFileTreeStats(false, 0L, 0, 0)
        if (!metadata.isDirectory) {
            return ChimahonFileTreeStats(
                exists = true,
                sizeBytes = metadata.size ?: 0L,
                fileCount = 1,
                directoryCount = 0,
            )
        }

        var sizeBytes = 0L
        var fileCount = 0
        var directoryCount = 1
        children(path).forEach { child ->
            val childStats = treeStats(child)
            sizeBytes += childStats.sizeBytes
            fileCount += childStats.fileCount
            directoryCount += childStats.directoryCount
        }
        return ChimahonFileTreeStats(
            exists = true,
            sizeBytes = sizeBytes,
            fileCount = fileCount,
            directoryCount = directoryCount,
        )
    }

    private fun childDirectories(path: Path): List<Path> {
        return children(path).filter { child ->
            runCatching { fileSystem.metadata(child).isDirectory }.getOrDefault(false)
        }
    }

    private fun children(path: Path): List<Path> {
        return runCatching { fileSystem.list(path) }.getOrDefault(emptyList())
    }

    private fun Path.matchesChapter(chapter: Chapters): Boolean {
        val diskName = name.removeSuffix(CBZ_SUFFIX)
        val rawChapterName = chapter.name.ifBlank { "Chapter" }
        val prefixedName = if (chapter.scanlator.isNullOrBlank()) {
            rawChapterName
        } else {
            "${chapter.scanlator}_$rawChapterName"
        }
        val expected = validFileName(prefixedName, MAX_CHAPTER_NAME_BYTES)
        if (diskName.equals(expected, ignoreCase = true)) return true
        if (!diskName.startsWith("${expected}_", ignoreCase = true)) return false
        val suffix = diskName.substring(expected.length + 1)
        return suffix.length == CHAPTER_HASH_LENGTH && suffix.all(Char::isLetterOrDigit)
    }
}

internal fun collectTreeStats(
    path: Path,
    fileSystem: FileSystem = FileSystem.SYSTEM,
): ChimahonFileTreeStats {
    return ChimahonDownloadStorage(path, fileSystem).treeStats(path)
}

private fun validFileName(
    input: String,
    maxBytes: Int = MAX_FILE_NAME_BYTES,
): String {
    val trimmed = input.trim('.', ' ')
    if (trimmed.isEmpty()) return "(invalid)"
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
            val bytes = character.toString().encodeToByteArray().size
            if (byteCount + bytes > maxBytes) return@buildString
            append(character)
            byteCount += bytes
        }
    }
}

private const val CBZ_SUFFIX = ".cbz"
private const val CHAPTER_HASH_LENGTH = 6
private const val MAX_FILE_NAME_BYTES = 240
private const val MAX_CHAPTER_NAME_BYTES = MAX_FILE_NAME_BYTES - 11
private val INVALID_FILE_NAME_CHARACTERS = setOf('"', '*', '/', ':', '<', '>', '?', '\\', '|')
