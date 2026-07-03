package app.chimahon.shared.novelopsui

import app.chimahon.shared.novelui.ChimahonNovelCategoryUiModel

enum class NovelImportPhase(val title: String) {
    Idle("Idle"),
    PickingFile("Picking file"),
    ReadingMetadata("Reading metadata"),
    CopyingContent("Copying content"),
    ParsingToc("Parsing table of contents"),
    Saving("Saving"),
    Complete("Complete"),
    Failed("Failed"),
}

enum class NovelImportConflictPolicy(val title: String) {
    Ask("Ask each time"),
    KeepExisting("Keep existing"),
    Replace("Replace"),
    MergeProgress("Merge progress"),
}

enum class NovelSourceKind(val title: String) {
    LocalEpub("Local EPUB"),
    Folder("Folder"),
    TtuSync("TTSU sync"),
    Backup("Backup"),
    RemoteUrl("Remote URL"),
}

enum class NovelSyncMode(val title: String) {
    Off("Off"),
    Auto("Auto"),
    Import("Import"),
    Export("Export"),
    TwoWay("Two-way"),
}

enum class NovelSyncDirection(val title: String) {
    Auto("Auto"),
    Import("Import from Drive"),
    Export("Export to Drive"),
}

enum class NovelDriveAuthStatus(val title: String) {
    MissingConfiguration("Missing OAuth configuration"),
    NotConnected("Not connected"),
    Connecting("Connecting"),
    Connected("Connected"),
    Failed("Failed"),
}

enum class NovelOperationAction {
    PickEpub,
    PickFolder,
    ImportCandidate,
    RetryImport,
    CancelImport,
    RemoveImport,
    ResolveConflict,
    OpenNovel,
    OpenSource,
    RefreshSource,
    ConnectDrive,
    DisconnectDrive,
    CopyDeviceCode,
    RunSync,
    StopSync,
    UpdateSyncMode,
    CreateCategory,
    RenameCategory,
    DeleteCategory,
    MoveCategoryUp,
    MoveCategoryDown,
    SetDefaultCategory,
}

data class NovelImportCandidateUiModel(
    val id: String,
    val title: String,
    val author: String? = null,
    val language: String? = null,
    val fileName: String? = null,
    val fileSizeLabel: String? = null,
    val chapterCount: Int = 0,
    val characterCount: Int? = null,
    val coverUrl: String? = null,
    val source: NovelSourceKind = NovelSourceKind.LocalEpub,
    val duplicateBookId: String? = null,
    val selected: Boolean = false,
) {
    val subtitle: String
        get() = listOfNotNull(
            author?.takeIf(String::isNotBlank),
            language?.uppercase()?.takeIf(String::isNotBlank),
            fileSizeLabel?.takeIf(String::isNotBlank),
        ).joinToString(" - ").ifBlank { source.title }

    val countLabel: String
        get() = when {
            characterCount != null && chapterCount > 0 -> "$chapterCount chapters, $characterCount chars"
            chapterCount > 0 -> "$chapterCount chapters"
            characterCount != null -> "$characterCount chars"
            else -> "Metadata pending"
        }

    fun stableLazyKey(index: Int): String = "novel-import:$index:$id:${fileName.orEmpty()}"
}

data class NovelImportQueueItemUiModel(
    val id: String,
    val candidate: NovelImportCandidateUiModel,
    val phase: NovelImportPhase = NovelImportPhase.Idle,
    val progressPercent: Int? = null,
    val message: String? = null,
    val errorMessage: String? = null,
) {
    val busy: Boolean
        get() = phase != NovelImportPhase.Idle &&
            phase != NovelImportPhase.Complete &&
            phase != NovelImportPhase.Failed

    val statusLabel: String
        get() = when {
            errorMessage != null -> errorMessage
            progressPercent != null -> "${phase.title} ${progressPercent.coerceIn(0, 100)}%"
            message != null -> message
            else -> phase.title
        }
}

data class NovelImportQueueState(
    val candidates: List<NovelImportCandidateUiModel> = emptyList(),
    val queue: List<NovelImportQueueItemUiModel> = emptyList(),
    val conflictPolicy: NovelImportConflictPolicy = NovelImportConflictPolicy.Ask,
    val importing: Boolean = false,
    val message: String? = null,
) {
    val hasWork: Boolean
        get() = candidates.isNotEmpty() || queue.isNotEmpty()
}

data class NovelSourceUiModel(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val kind: NovelSourceKind,
    val enabled: Boolean = true,
    val selected: Boolean = false,
    val syncing: Boolean = false,
    val itemCount: Int = 0,
    val lastSyncLabel: String? = null,
    val errorMessage: String? = null,
) {
    val statusLabel: String
        get() = when {
            !enabled -> "Disabled"
            syncing -> "Syncing"
            errorMessage != null -> "Error"
            lastSyncLabel != null -> lastSyncLabel
            itemCount > 0 -> "$itemCount item(s)"
            else -> "Ready"
        }
}

data class NovelDiscoverState(
    val sources: List<NovelSourceUiModel> = emptyList(),
    val candidates: List<NovelImportCandidateUiModel> = emptyList(),
    val query: String = "",
    val selectedSourceId: String? = null,
    val loading: Boolean = false,
    val errorMessage: String? = null,
) {
    val selectedSource: NovelSourceUiModel?
        get() = sources.firstOrNull { it.id == selectedSourceId } ?: sources.firstOrNull { it.selected }

    val visibleCandidates: List<NovelImportCandidateUiModel>
        get() {
            val normalized = query.trim()
            return candidates.filter {
                normalized.isBlank() ||
                    it.title.contains(normalized, ignoreCase = true) ||
                    it.author.orEmpty().contains(normalized, ignoreCase = true) ||
                    it.fileName.orEmpty().contains(normalized, ignoreCase = true)
            }
        }
}

data class NovelDeviceCodePromptUiModel(
    val verificationUrl: String,
    val userCode: String,
    val expiresInSeconds: Long,
    val pollIntervalSeconds: Long,
) {
    val instruction: String
        get() = "Open $verificationUrl and enter $userCode."
}

data class NovelTtuSyncState(
    val enabled: Boolean = false,
    val mode: NovelSyncMode = NovelSyncMode.Off,
    val direction: NovelSyncDirection = NovelSyncDirection.Auto,
    val authStatus: NovelDriveAuthStatus = NovelDriveAuthStatus.NotConnected,
    val prompt: NovelDeviceCodePromptUiModel? = null,
    val syncing: Boolean = false,
    val progressPercent: Int? = null,
    val lastSyncLabel: String? = null,
    val summary: String? = null,
    val errorMessage: String? = null,
) {
    val connected: Boolean
        get() = authStatus == NovelDriveAuthStatus.Connected

    val statusLabel: String
        get() = when {
            syncing && progressPercent != null -> "Syncing ${progressPercent.coerceIn(0, 100)}%"
            syncing -> "Syncing"
            errorMessage != null -> "Sync failed"
            !enabled -> "Disabled"
            else -> authStatus.title
        }
}

data class NovelCategoryManagerState(
    val categories: List<ChimahonNovelCategoryUiModel> = emptyList(),
    val defaultCategoryId: String? = null,
    val editingCategoryId: String? = null,
    val categoryNameDraft: String = "",
    val saving: Boolean = false,
    val message: String? = null,
) {
    val defaultCategory: ChimahonNovelCategoryUiModel?
        get() = defaultCategoryId?.let { id -> categories.firstOrNull { it.id == id } }
}

data class NovelOperationsState(
    val imports: NovelImportQueueState = NovelImportQueueState(),
    val discover: NovelDiscoverState = NovelDiscoverState(),
    val sync: NovelTtuSyncState = NovelTtuSyncState(),
    val categories: NovelCategoryManagerState = NovelCategoryManagerState(),
)

data class NovelOperationsActions(
    val onOperation: (NovelOperationAction) -> Unit = {},
    val onCandidateAction: (NovelOperationAction, NovelImportCandidateUiModel) -> Unit = { _, _ -> },
    val onQueueAction: (NovelOperationAction, NovelImportQueueItemUiModel) -> Unit = { _, _ -> },
    val onSourceAction: (NovelOperationAction, NovelSourceUiModel) -> Unit = { _, _ -> },
    val onCategoryAction: (NovelOperationAction, ChimahonNovelCategoryUiModel) -> Unit = { _, _ -> },
    val onQueryChange: (String) -> Unit = {},
    val onSyncModeChange: (NovelSyncMode) -> Unit = {},
    val onSyncDirectionChange: (NovelSyncDirection) -> Unit = {},
    val onCategoryNameDraftChange: (String) -> Unit = {},
)
