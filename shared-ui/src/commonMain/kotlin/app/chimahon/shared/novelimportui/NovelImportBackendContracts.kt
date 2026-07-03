package app.chimahon.shared.novelimportui

import app.chimahon.shared.novelui.ChimahonNovelBackendError
import app.chimahon.shared.novelui.ChimahonNovelBookRecord

data class ChimahonNovelImportQueueLoadRequest(
    val includeMetadata: Boolean = true,
    val includeChapterMap: Boolean = true,
    val includeCompleted: Boolean = true,
    val selectedSourceId: String? = null,
)

data class ChimahonNovelImportQueueLoadResult(
    val queue: ChimahonNovelImportQueueSnapshot = ChimahonNovelImportQueueSnapshot(),
    val loadedAtMillis: Long = 0L,
    val errors: List<ChimahonNovelBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

data class ChimahonNovelImportQueueSnapshot(
    val title: String = "Local EPUB import",
    val subtitle: String? = "Stage light novels, review metadata, then import into the novel library.",
    val sources: List<ChimahonNovelImportSourceRecord> = emptyList(),
    val selectedSourceId: String? = sources.firstOrNull { it.enabled }?.id,
    val files: List<ChimahonNovelImportFileRecord> = emptyList(),
    val volumes: List<ChimahonNovelImportVolumeRecord> = emptyList(),
    val chapters: List<ChimahonNovelImportChapterRecord> = emptyList(),
    val options: ChimahonNovelImportBackendOptions = ChimahonNovelImportBackendOptions(),
    val progress: ChimahonNovelImportBackendProgress = ChimahonNovelImportBackendProgress(),
    val summary: ChimahonNovelImportBackendSummary = ChimahonNovelImportBackendSummary(),
    val stage: ChimahonNovelImportBackendStage = ChimahonNovelImportBackendStage.Idle,
    val warningMessage: String? = null,
    val errorMessage: String? = null,
)

data class ChimahonNovelImportSourceRecord(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val kind: ChimahonNovelImportSourceKind = ChimahonNovelImportSourceKind.EpubFiles,
    val enabled: Boolean = true,
    val recommended: Boolean = false,
    val badgeLabel: String? = null,
    val queuedCount: Int = 0,
)

data class ChimahonNovelImportFileRef(
    val id: String,
    val displayName: String,
    val uri: String? = null,
    val path: String? = null,
    val mimeType: String? = "application/epub+zip",
    val sizeBytes: Long? = null,
    val modifiedEpochMillis: Long = 0L,
)

data class ChimahonNovelImportFileRecord(
    val ref: ChimahonNovelImportFileRef,
    val sourceId: String? = null,
    val status: ChimahonNovelImportBackendFileStatus = ChimahonNovelImportBackendFileStatus.Staged,
    val progress: ChimahonNovelImportBackendProgress = ChimahonNovelImportBackendProgress(),
    val metadata: ChimahonNovelImportMetadataRecord? = null,
    val errorMessage: String? = null,
    val warningMessage: String? = null,
    val selected: Boolean = false,
    val duplicate: Boolean = false,
    val canRemove: Boolean = true,
)

data class ChimahonNovelImportMetadataRecord(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val authors: List<String> = emptyList(),
    val language: String? = null,
    val publisher: String? = null,
    val description: String? = null,
    val coverRef: String? = null,
    val sourceFileName: String? = null,
    val categoryLabels: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val volumeCount: Int = 0,
    val chapterCount: Int = 0,
    val characterCount: Int? = null,
    val wordCount: Int? = null,
    val createdEpochMillis: Long = 0L,
    val modifiedEpochMillis: Long = 0L,
    val existingLibraryMatch: ChimahonNovelImportLibraryMatchRecord? = null,
) {
    val displayTitle: String
        get() = title?.takeIf(String::isNotBlank) ?: sourceFileName ?: "Untitled novel"
}

data class ChimahonNovelImportLibraryMatchRecord(
    val bookId: String,
    val title: String,
    val categoryLabels: List<String> = emptyList(),
    val lastReadEpochMillis: Long = 0L,
    val progressPercent: Int? = null,
    val matchKind: ChimahonNovelImportLibraryMatchKind = ChimahonNovelImportLibraryMatchKind.PossibleDuplicate,
    val existingRecord: ChimahonNovelBookRecord? = null,
)

data class ChimahonNovelImportVolumeRecord(
    val id: String,
    val title: String,
    val sourceOrder: Int,
    val chapterIds: List<String> = emptyList(),
    val expanded: Boolean = true,
    val selectedCount: Int = 0,
    val warningMessage: String? = null,
)

data class ChimahonNovelImportChapterRecord(
    val id: String,
    val title: String,
    val href: String? = null,
    val sourceOrder: Int = Int.MAX_VALUE,
    val spineIndex: Int? = null,
    val volumeId: String? = null,
    val characterStart: Int? = null,
    val characterCount: Int? = null,
    val wordCount: Int? = null,
    val status: ChimahonNovelImportBackendChapterStatus = ChimahonNovelImportBackendChapterStatus.Ready,
    val selected: Boolean = true,
    val imageOnly: Boolean = false,
    val warnings: List<String> = emptyList(),
    val duplicateOfId: String? = null,
)

data class ChimahonNovelImportBackendOptions(
    val destinationCategoryId: String? = null,
    val destinationCategoryLabel: String? = null,
    val dictionaryProfileName: String? = null,
    val overwriteExisting: Boolean = false,
    val keepOriginalFileName: Boolean = true,
    val importSelectedChaptersOnly: Boolean = false,
    val showSkippedChapters: Boolean = true,
)

data class ChimahonNovelImportBackendProgress(
    val fraction: Double = 0.0,
    val processedCount: Int = 0,
    val totalCount: Int = 0,
    val processedBytes: Long? = null,
    val totalBytes: Long? = null,
    val speedBytesPerSecond: Long? = null,
    val etaSeconds: Long? = null,
    val message: String? = null,
) {
    val normalizedFraction: Double
        get() = fraction.coerceIn(0.0, 1.0)
}

data class ChimahonNovelImportBackendSummary(
    val total: Int = 0,
    val imported: Int = 0,
    val failed: Int = 0,
    val skipped: Int = 0,
    val duplicates: Int = 0,
)

enum class ChimahonNovelImportBackendStage {
    Idle,
    ChoosingSource,
    Scanning,
    ReadingMetadata,
    Ready,
    Importing,
    Paused,
    Complete,
    Failed,
    Canceled,
}

enum class ChimahonNovelImportBackendFileStatus {
    Staged,
    Queued,
    ReadingMetadata,
    Ready,
    Importing,
    Imported,
    Skipped,
    Warning,
    Failed,
    Canceled,
}

enum class ChimahonNovelImportBackendChapterStatus {
    Ready,
    Selected,
    Skipped,
    Duplicate,
    Warning,
    Error,
}

data class ChimahonNovelImportCommandRequest(
    val fileIds: List<String> = emptyList(),
    val chapterIds: List<String> = emptyList(),
    val volumeId: String? = null,
    val selected: Boolean? = null,
    val options: ChimahonNovelImportBackendOptions? = null,
)

data class ChimahonNovelImportStageFilesRequest(
    val files: List<ChimahonNovelImportFileRef>,
    val sourceId: String? = null,
    val options: ChimahonNovelImportBackendOptions = ChimahonNovelImportBackendOptions(),
)

data class ChimahonNovelImportStageFolderRequest(
    val folderPath: String,
    val sourceId: String? = null,
    val recursive: Boolean = true,
    val options: ChimahonNovelImportBackendOptions = ChimahonNovelImportBackendOptions(),
)

data class ChimahonNovelImportCommandResult(
    val queue: ChimahonNovelImportQueueSnapshot = ChimahonNovelImportQueueSnapshot(),
    val affectedFileIds: List<String> = emptyList(),
    val affectedChapterIds: List<String> = emptyList(),
    val errors: List<ChimahonNovelBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

interface ChimahonNovelImportRepositoryBridge {
    suspend fun loadQueue(
        request: ChimahonNovelImportQueueLoadRequest = ChimahonNovelImportQueueLoadRequest(),
    ): ChimahonNovelImportQueueLoadResult

    suspend fun stageFiles(request: ChimahonNovelImportStageFilesRequest): ChimahonNovelImportCommandResult

    suspend fun stageFolder(request: ChimahonNovelImportStageFolderRequest): ChimahonNovelImportCommandResult

    suspend fun scanQueue(request: ChimahonNovelImportCommandRequest): ChimahonNovelImportCommandResult

    suspend fun updateOptions(options: ChimahonNovelImportBackendOptions): ChimahonNovelImportCommandResult

    suspend fun startImport(request: ChimahonNovelImportCommandRequest): ChimahonNovelImportCommandResult

    suspend fun pauseImport(): ChimahonNovelImportCommandResult

    suspend fun resumeImport(): ChimahonNovelImportCommandResult

    suspend fun cancelImport(): ChimahonNovelImportCommandResult

    suspend fun retryFailed(): ChimahonNovelImportCommandResult

    suspend fun clearQueue(includeCompleted: Boolean = true): ChimahonNovelImportCommandResult

    suspend fun removeFile(fileId: String): ChimahonNovelImportCommandResult

    suspend fun setFileSelected(fileId: String, selected: Boolean): ChimahonNovelImportCommandResult

    suspend fun setChapterSelected(chapterId: String, selected: Boolean): ChimahonNovelImportCommandResult

    suspend fun setVolumeExpanded(volumeId: String, expanded: Boolean): ChimahonNovelImportCommandResult
}

data class ChimahonNovelImportRepositoryCallbacks(
    val loadQueue: suspend (ChimahonNovelImportQueueLoadRequest) -> ChimahonNovelImportQueueLoadResult,
    val stageFiles: suspend (ChimahonNovelImportStageFilesRequest) -> ChimahonNovelImportCommandResult,
    val stageFolder: suspend (ChimahonNovelImportStageFolderRequest) -> ChimahonNovelImportCommandResult,
    val scanQueue: suspend (ChimahonNovelImportCommandRequest) -> ChimahonNovelImportCommandResult,
    val updateOptions: suspend (ChimahonNovelImportBackendOptions) -> ChimahonNovelImportCommandResult,
    val startImport: suspend (ChimahonNovelImportCommandRequest) -> ChimahonNovelImportCommandResult,
    val pauseImport: suspend () -> ChimahonNovelImportCommandResult,
    val resumeImport: suspend () -> ChimahonNovelImportCommandResult,
    val cancelImport: suspend () -> ChimahonNovelImportCommandResult,
    val retryFailed: suspend () -> ChimahonNovelImportCommandResult,
    val clearQueue: suspend (Boolean) -> ChimahonNovelImportCommandResult,
    val removeFile: suspend (String) -> ChimahonNovelImportCommandResult,
    val setFileSelected: suspend (String, Boolean) -> ChimahonNovelImportCommandResult,
    val setChapterSelected: suspend (String, Boolean) -> ChimahonNovelImportCommandResult,
    val setVolumeExpanded: suspend (String, Boolean) -> ChimahonNovelImportCommandResult,
)

class ChimahonCallbackNovelImportRepositoryBridge(
    private val callbacks: ChimahonNovelImportRepositoryCallbacks,
) : ChimahonNovelImportRepositoryBridge {
    override suspend fun loadQueue(
        request: ChimahonNovelImportQueueLoadRequest,
    ): ChimahonNovelImportQueueLoadResult {
        return callbacks.loadQueue(request)
    }

    override suspend fun stageFiles(
        request: ChimahonNovelImportStageFilesRequest,
    ): ChimahonNovelImportCommandResult {
        return callbacks.stageFiles(request)
    }

    override suspend fun stageFolder(
        request: ChimahonNovelImportStageFolderRequest,
    ): ChimahonNovelImportCommandResult {
        return callbacks.stageFolder(request)
    }

    override suspend fun scanQueue(request: ChimahonNovelImportCommandRequest): ChimahonNovelImportCommandResult {
        return callbacks.scanQueue(request)
    }

    override suspend fun updateOptions(
        options: ChimahonNovelImportBackendOptions,
    ): ChimahonNovelImportCommandResult {
        return callbacks.updateOptions(options)
    }

    override suspend fun startImport(
        request: ChimahonNovelImportCommandRequest,
    ): ChimahonNovelImportCommandResult {
        return callbacks.startImport(request)
    }

    override suspend fun pauseImport(): ChimahonNovelImportCommandResult {
        return callbacks.pauseImport()
    }

    override suspend fun resumeImport(): ChimahonNovelImportCommandResult {
        return callbacks.resumeImport()
    }

    override suspend fun cancelImport(): ChimahonNovelImportCommandResult {
        return callbacks.cancelImport()
    }

    override suspend fun retryFailed(): ChimahonNovelImportCommandResult {
        return callbacks.retryFailed()
    }

    override suspend fun clearQueue(includeCompleted: Boolean): ChimahonNovelImportCommandResult {
        return callbacks.clearQueue(includeCompleted)
    }

    override suspend fun removeFile(fileId: String): ChimahonNovelImportCommandResult {
        return callbacks.removeFile(fileId)
    }

    override suspend fun setFileSelected(fileId: String, selected: Boolean): ChimahonNovelImportCommandResult {
        return callbacks.setFileSelected(fileId, selected)
    }

    override suspend fun setChapterSelected(chapterId: String, selected: Boolean): ChimahonNovelImportCommandResult {
        return callbacks.setChapterSelected(chapterId, selected)
    }

    override suspend fun setVolumeExpanded(volumeId: String, expanded: Boolean): ChimahonNovelImportCommandResult {
        return callbacks.setVolumeExpanded(volumeId, expanded)
    }
}

interface ChimahonNovelImportService : ChimahonNovelImportRepositoryBridge

class ChimahonRepositoryNovelImportService(
    private val bridge: ChimahonNovelImportRepositoryBridge,
) : ChimahonNovelImportService,
    ChimahonNovelImportRepositoryBridge by bridge

fun ChimahonNovelImportRepositoryBridge.asNovelImportService(): ChimahonNovelImportService {
    return ChimahonRepositoryNovelImportService(this)
}
