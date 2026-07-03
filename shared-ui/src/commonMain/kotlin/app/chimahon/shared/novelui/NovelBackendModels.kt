package app.chimahon.shared.novelui

data class ChimahonNovelBackendError(
    val code: ChimahonNovelBackendErrorCode,
    val message: String? = null,
    val novelId: String? = null,
    val chapterId: String? = null,
    val categoryId: String? = null,
    val sourceId: String? = null,
    val fileRef: String? = null,
)

enum class ChimahonNovelBackendErrorCode {
    NotFound,
    ImportFailed,
    SyncUnavailable,
    Network,
    Storage,
    Unsupported,
    PermissionDenied,
    Conflict,
    Unknown,
}

enum class ChimahonNovelSourceKind(val title: String) {
    LocalEpub("Local EPUB"),
    Folder("Folder"),
    TtuSync("TTSU sync"),
    Backup("Backup"),
    RemoteUrl("Remote URL"),
    Unknown("Unknown"),
}

data class ChimahonNovelSourceInfo(
    val id: String,
    val name: String,
    val kind: ChimahonNovelSourceKind = ChimahonNovelSourceKind.LocalEpub,
    val language: String? = null,
    val rootPath: String? = null,
    val remoteUrl: String? = null,
    val enabled: Boolean = true,
    val supportsImport: Boolean = true,
    val supportsSync: Boolean = false,
    val lastScanEpochMillis: Long = 0L,
    val itemCount: Int = 0,
) {
    val displayName: String
        get() = name.ifBlank { kind.title }
}

data class ChimahonNovelCategoryRecord(
    val id: String?,
    val title: String,
    val order: Long = 0L,
    val flags: Long = 0L,
    val itemCount: Int = 0,
    val isDefault: Boolean = false,
    val hidden: Boolean = false,
)

data class ChimahonNovelProgressSummary(
    val chapterId: String? = null,
    val chapterIndex: Int = 0,
    val sectionId: String? = null,
    val pageIndex: Int = 0,
    val pageCount: Int = 0,
    val progress: Double = 0.0,
    val characterOffset: Int = 0,
    val readCharacterCount: Int = 0,
    val characterCount: Int? = null,
    val readTimeSeconds: Long = 0L,
    val lastReadEpochMillis: Long = 0L,
    val completed: Boolean = false,
) {
    val normalizedProgress: Double
        get() = progress.coerceIn(0.0, 1.0)

    fun progressPercent(totalCharacterCount: Int? = characterCount): Int? {
        if (completed) return 100
        if (normalizedProgress > 0.0) return (normalizedProgress * 100.0).toInt().coerceIn(0, 100)

        val total = totalCharacterCount ?: characterCount
        return if (total != null && total > 0 && readCharacterCount > 0) {
            ((readCharacterCount.toDouble() / total.toDouble()) * 100.0).toInt().coerceIn(0, 100)
        } else {
            null
        }
    }

    fun progressFraction(totalCharacterCount: Int? = characterCount): Double {
        if (completed) return 1.0
        if (normalizedProgress > 0.0) return normalizedProgress

        val total = totalCharacterCount ?: characterCount
        return if (total != null && total > 0 && readCharacterCount > 0) {
            (readCharacterCount.toDouble() / total.toDouble()).coerceIn(0.0, 1.0)
        } else {
            0.0
        }
    }
}

data class ChimahonNovelBookRecord(
    val id: String,
    val title: String,
    val author: String? = null,
    val language: String? = null,
    val coverUrl: String? = null,
    val folderPath: String? = null,
    val folderLabel: String? = null,
    val fileName: String? = null,
    val hash: String? = null,
    val source: ChimahonNovelSourceKind = ChimahonNovelSourceKind.LocalEpub,
    val sourceInfo: ChimahonNovelSourceInfo? = null,
    val categoryIds: List<String> = emptyList(),
    val dateAddedEpochMillis: Long = 0L,
    val lastAccessEpochMillis: Long = 0L,
    val lastModifiedEpochMillis: Long = 0L,
    val chapterStarts: List<Int> = emptyList(),
    val chapterCount: Int = chapterStarts.chapterCountFromStarts(),
    val volumeCount: Int = 0,
    val characterCount: Int? = null,
    val progress: ChimahonNovelProgressSummary = ChimahonNovelProgressSummary(),
    val dictionaryProfileName: String? = null,
    val ttuFolderName: String? = null,
    val ghost: Boolean = false,
    val initialized: Boolean = true,
    val metadata: Map<String, String> = emptyMap(),
) {
    val displayTitle: String
        get() = title.ifBlank { fileName ?: "Untitled novel" }

    val hasStarted: Boolean
        get() = progress.completed ||
            progress.normalizedProgress > 0.0 ||
            progress.readCharacterCount > 0 ||
            progress.readTimeSeconds > 0L
}

data class ChimahonNovelVolumeRecord(
    val id: String,
    val title: String,
    val sourceOrder: Int,
    val chapterIds: List<String> = emptyList(),
)

data class ChimahonNovelChapterRecord(
    val id: String,
    val novelId: String,
    val title: String,
    val href: String? = null,
    val sourceOrder: Int = Int.MAX_VALUE,
    val spineIndex: Int? = null,
    val volumeId: String? = null,
    val characterStart: Int? = null,
    val characterCount: Int? = null,
    val progress: ChimahonNovelProgressSummary = ChimahonNovelProgressSummary(),
    val read: Boolean = false,
    val bookmarked: Boolean = false,
    val imageOnly: Boolean = false,
    val bookmarkLabel: String? = null,
) {
    val displayTitle: String
        get() {
            val fallbackIndex = (spineIndex ?: sourceOrder).takeIf { it != Int.MAX_VALUE } ?: 0
            return title.ifBlank { "Chapter ${fallbackIndex.coerceAtLeast(0) + 1}" }
        }

    val progressFraction: Float
        get() = progress.progressFraction(characterCount).toFloat().coerceIn(0f, 1f)

    val effectivelyRead: Boolean
        get() = read || progress.completed || progressFraction >= 1f
}

data class ChimahonNovelLibraryData(
    val books: List<ChimahonNovelBookRecord> = emptyList(),
    val categories: List<ChimahonNovelCategoryRecord> = emptyList(),
    val sources: List<ChimahonNovelSourceInfo> = emptyList(),
    val sync: ChimahonNovelSyncOverview = ChimahonNovelSyncOverview(),
    val defaultCategoryId: String? = null,
    val defaultCategoryLabel: String? = null,
    val storageLabel: String? = null,
) {
    val totalChapterCount: Int
        get() = books.sumOf { it.chapterCount }

    val startedBookCount: Int
        get() = books.count { it.hasStarted }
}

data class ChimahonNovelSyncOverview(
    val providerName: String = "TTSU sync",
    val enabled: Boolean = false,
    val connected: Boolean = false,
    val syncing: Boolean = false,
    val progressPercent: Int? = null,
    val lastSyncEpochMillis: Long = 0L,
    val lastSyncLabel: String? = null,
    val summaryLabel: String? = null,
    val errorMessage: String? = null,
)

data class ChimahonNovelDetailData(
    val novel: ChimahonNovelBookRecord,
    val volumes: List<ChimahonNovelVolumeRecord> = emptyList(),
    val chapters: List<ChimahonNovelChapterRecord> = emptyList(),
    val categories: List<ChimahonNovelCategoryRecord> = emptyList(),
    val sourceInfo: ChimahonNovelSourceInfo? = novel.sourceInfo,
    val loadedAtMillis: Long = 0L,
)

data class ChimahonNovelLibraryLoadRequest(
    val includeCategories: Boolean = true,
    val includeChapters: Boolean = false,
    val includeProgress: Boolean = true,
    val includeSources: Boolean = true,
    val includeGhosts: Boolean = true,
    val includeSync: Boolean = true,
    val categoryId: String? = null,
    val query: String = "",
    val forceRefresh: Boolean = false,
)

data class ChimahonNovelLibraryLoadResult(
    val data: ChimahonNovelLibraryData = ChimahonNovelLibraryData(),
    val loadedAtMillis: Long = 0L,
    val errors: List<ChimahonNovelBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

data class ChimahonNovelDetailLoadRequest(
    val novelId: String,
    val includeVolumes: Boolean = true,
    val includeChapters: Boolean = true,
    val includeProgress: Boolean = true,
    val includeLibraryContext: Boolean = true,
    val refreshMetadata: Boolean = false,
)

data class ChimahonNovelDetailLoadResult(
    val detail: ChimahonNovelDetailData? = null,
    val loadedAtMillis: Long = 0L,
    val errors: List<ChimahonNovelBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()

    val found: Boolean
        get() = detail != null
}

data class ChimahonNovelCategoryMutationRequest(
    val novelIds: List<String>,
    val categoryIds: List<String>,
    val mode: ChimahonNovelCategoryMutationMode = ChimahonNovelCategoryMutationMode.Replace,
)

enum class ChimahonNovelCategoryMutationMode {
    Add,
    Remove,
    Replace,
}

data class ChimahonNovelMetadataMutationRequest(
    val novelId: String,
    val title: String? = null,
    val author: String? = null,
    val language: String? = null,
    val coverUrl: String? = null,
    val description: String? = null,
    val dictionaryProfileName: String? = null,
    val ttuFolderName: String? = null,
)

data class ChimahonNovelRemovalRequest(
    val novelIds: List<String>,
    val deleteLocalFiles: Boolean = true,
)

data class ChimahonNovelChapterFlagMutationRequest(
    val novelId: String,
    val chapterIds: List<String>,
    val read: Boolean? = null,
    val bookmarked: Boolean? = null,
)

data class ChimahonNovelProgressResetRequest(
    val novelIds: List<String>,
    val resetBookmark: Boolean = true,
    val resetStatistics: Boolean = true,
)

data class ChimahonNovelMutationResult(
    val novelCount: Int = 0,
    val chapterCount: Int = 0,
    val categoryCount: Int = 0,
    val errors: List<ChimahonNovelBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

private fun List<Int>.chapterCountFromStarts(): Int {
    return (size - 1).coerceAtLeast(0)
}
