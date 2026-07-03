package app.chimahon.shared.novelimportui

data class ChimahonNovelImportUiState(
    val title: String = "Local EPUB import",
    val subtitle: String? = "Stage light novels, review metadata, then import into the novel library.",
    val sources: List<ChimahonNovelImportSourceUiModel> = ChimahonNovelImportDefaults.sources,
    val selectedSourceId: String? = sources.firstOrNull { it.enabled }?.id,
    val files: List<ChimahonNovelImportFileUiModel> = emptyList(),
    val metadataPreview: ChimahonNovelImportMetadataUiModel? = files.firstNotNullOfOrNull { it.metadata },
    val volumes: List<ChimahonNovelImportVolumeUiModel> = emptyList(),
    val chapters: List<ChimahonNovelImportChapterUiModel> = emptyList(),
    val options: ChimahonNovelImportOptionsUiState = ChimahonNovelImportOptionsUiState(),
    val progress: ChimahonNovelImportProgressUiModel = ChimahonNovelImportProgressUiModel(),
    val summary: ChimahonNovelImportSummaryUiModel = ChimahonNovelImportSummaryUiModel(),
    val stage: ChimahonNovelImportStage = ChimahonNovelImportStage.Idle,
    val searchQuery: String = "",
    val selectedFileIds: Set<String> = emptySet(),
    val errorMessage: String? = null,
    val warningMessage: String? = null,
) {
    val selectedSource: ChimahonNovelImportSourceUiModel?
        get() = sources.firstOrNull { it.id == selectedSourceId } ?: sources.firstOrNull { it.enabled }

    val selectedFiles: List<ChimahonNovelImportFileUiModel>
        get() = files.filter { it.id in selectedFileIds }

    val activeFile: ChimahonNovelImportFileUiModel?
        get() = selectedFiles.firstOrNull() ?: files.firstOrNull { it.status.isActive } ?: files.firstOrNull()

    val visibleFiles: List<ChimahonNovelImportFileUiModel>
        get() = files.filter { it.matches(searchQuery) }

    val visibleChapters: List<ChimahonNovelImportChapterUiModel>
        get() = chapters
            .filter { chapter ->
                options.showSkippedChapters || chapter.status != ChimahonNovelImportChapterStatus.Skipped
            }
            .sortedWith(
                compareBy<ChimahonNovelImportChapterUiModel> { it.sourceOrder }
                    .thenBy { it.spineIndex ?: Int.MAX_VALUE },
            )

    val selectedChapterCount: Int
        get() = chapters.count { it.selected }

    val hasBusyWork: Boolean
        get() = stage.isBusy || files.any { it.status.isActive }

    val canStartImport: Boolean
        get() = !hasBusyWork && stage != ChimahonNovelImportStage.Paused && files.any { it.status.canImport }

    val canRetry: Boolean
        get() = !hasBusyWork && files.any { it.status == ChimahonNovelImportFileStatus.Failed }

    val statusLabel: String
        get() = when {
            errorMessage != null -> "Import needs attention"
            warningMessage != null -> "Import warning"
            stage == ChimahonNovelImportStage.Complete -> summary.displayLabel
            stage.isBusy -> progress.displayLabel ?: stage.label
            files.isEmpty() -> "No files staged"
            else -> summary.copy(total = files.size).displayLabel
        }
}

object ChimahonNovelImportDefaults {
    const val EpubFilesSourceId: String = "epub-files"
    const val FolderSourceId: String = "folder"
    const val ExistingLocalSourceId: String = "existing-local"

    val sources: List<ChimahonNovelImportSourceUiModel> = listOf(
        ChimahonNovelImportSourceUiModel(
            id = EpubFilesSourceId,
            title = "EPUB files",
            subtitle = "Pick one or more local .epub files.",
            kind = ChimahonNovelImportSourceKind.EpubFiles,
            icon = ChimahonNovelImportUiIcon.File,
            recommended = true,
        ),
        ChimahonNovelImportSourceUiModel(
            id = FolderSourceId,
            title = "Folder",
            subtitle = "Scan a local folder for EPUB archives.",
            kind = ChimahonNovelImportSourceKind.Folder,
            icon = ChimahonNovelImportUiIcon.Storage,
        ),
        ChimahonNovelImportSourceUiModel(
            id = ExistingLocalSourceId,
            title = "Local library",
            subtitle = "Review already staged local novel imports.",
            kind = ChimahonNovelImportSourceKind.ExistingLocal,
            icon = ChimahonNovelImportUiIcon.Queue,
        ),
    )
}

data class ChimahonNovelImportSourceUiModel(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val kind: ChimahonNovelImportSourceKind = ChimahonNovelImportSourceKind.EpubFiles,
    val icon: ChimahonNovelImportUiIcon = ChimahonNovelImportUiIcon.File,
    val enabled: Boolean = true,
    val recommended: Boolean = false,
    val badgeLabel: String? = null,
    val queuedCount: Int = 0,
) {
    val supportingLabel: String?
        get() = listOfNotNull(
            subtitle?.takeIf(String::isNotBlank),
            if (queuedCount > 0) "$queuedCount staged" else null,
        ).joinToString(" - ").takeIf(String::isNotBlank)
}

data class ChimahonNovelImportFileUiModel(
    val id: String,
    val displayName: String,
    val sourceId: String? = null,
    val pathLabel: String? = null,
    val sizeLabel: String? = null,
    val mimeTypeLabel: String? = "application/epub+zip",
    val modifiedLabel: String? = null,
    val status: ChimahonNovelImportFileStatus = ChimahonNovelImportFileStatus.Staged,
    val progress: ChimahonNovelImportProgressUiModel = ChimahonNovelImportProgressUiModel(),
    val metadata: ChimahonNovelImportMetadataUiModel? = null,
    val errorMessage: String? = null,
    val warningMessage: String? = null,
    val selected: Boolean = false,
    val duplicate: Boolean = false,
    val canRemove: Boolean = true,
    val canRetry: Boolean = status == ChimahonNovelImportFileStatus.Failed,
) {
    val title: String
        get() = metadata?.displayTitle ?: displayName

    val detailLabel: String?
        get() = listOfNotNull(
            sizeLabel,
            mimeTypeLabel,
            modifiedLabel,
            pathLabel,
        ).joinToString(" - ").takeIf(String::isNotBlank)

    val statusDetailLabel: String
        get() = errorMessage
            ?: warningMessage
            ?: progress.displayLabel
            ?: status.label

    val statusKind: ChimahonNovelImportStatusKind
        get() = when {
            errorMessage != null ||
                status == ChimahonNovelImportFileStatus.Failed -> ChimahonNovelImportStatusKind.Error
            warningMessage != null || duplicate || status == ChimahonNovelImportFileStatus.Warning -> {
                ChimahonNovelImportStatusKind.Warning
            }
            status == ChimahonNovelImportFileStatus.Imported -> ChimahonNovelImportStatusKind.Success
            status.isActive -> ChimahonNovelImportStatusKind.Active
            status == ChimahonNovelImportFileStatus.Skipped -> ChimahonNovelImportStatusKind.Muted
            else -> ChimahonNovelImportStatusKind.Info
        }

    fun stableLazyKey(index: Int): String {
        return "novel-import-file:$index:$id:$displayName:${sourceId.orEmpty()}"
    }
}

data class ChimahonNovelImportMetadataUiModel(
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
    val wordCountLabel: String? = null,
    val createdLabel: String? = null,
    val modifiedLabel: String? = null,
    val existingLibraryMatch: ChimahonNovelImportLibraryMatchUiModel? = null,
) {
    val displayTitle: String
        get() = title?.takeIf(String::isNotBlank) ?: sourceFileName ?: "Untitled novel"

    val creatorLine: String?
        get() = authors.joinToString(", ").takeIf(String::isNotBlank)

    val countLabel: String
        get() = buildList {
            if (volumeCount > 0) add(if (volumeCount == 1) "1 volume" else "$volumeCount volumes")
            if (chapterCount > 0) add(if (chapterCount == 1) "1 chapter" else "$chapterCount chapters")
            characterCount?.let { add("$it chars") }
            wordCountLabel?.takeIf(String::isNotBlank)?.let { add(it) }
        }.joinToString(" - ").ifBlank { "No chapter map yet" }

    val metadataLine: String?
        get() = listOfNotNull(
            language?.uppercase()?.takeIf(String::isNotBlank),
            publisher?.takeIf(String::isNotBlank),
            createdLabel?.takeIf(String::isNotBlank),
            modifiedLabel?.takeIf(String::isNotBlank),
        ).joinToString(" - ").takeIf(String::isNotBlank)
}

data class ChimahonNovelImportLibraryMatchUiModel(
    val bookId: String,
    val title: String,
    val categoryLabels: List<String> = emptyList(),
    val lastReadLabel: String? = null,
    val progressLabel: String? = null,
    val matchKind: ChimahonNovelImportLibraryMatchKind = ChimahonNovelImportLibraryMatchKind.PossibleDuplicate,
) {
    val detailLabel: String?
        get() = buildList {
            add(matchKind.label)
            progressLabel?.takeIf(String::isNotBlank)?.let { add(it) }
            lastReadLabel?.takeIf(String::isNotBlank)?.let { add(it) }
            if (categoryLabels.isNotEmpty()) add(categoryLabels.joinToString(", "))
        }.joinToString(" - ").takeIf(String::isNotBlank)
}

data class ChimahonNovelImportVolumeUiModel(
    val id: String,
    val title: String,
    val sourceOrder: Int,
    val chapterIds: List<String> = emptyList(),
    val expanded: Boolean = true,
    val selectedCount: Int = 0,
    val warningMessage: String? = null,
) {
    val chapterCountLabel: String
        get() = when (chapterIds.size) {
            0 -> "No chapters"
            1 -> "1 chapter"
            else -> "${chapterIds.size} chapters"
        }

    fun stableLazyKey(index: Int): String {
        return "novel-import-volume:$index:$id:$sourceOrder"
    }
}

data class ChimahonNovelImportChapterUiModel(
    val id: String,
    val title: String,
    val href: String? = null,
    val sourceOrder: Int = Int.MAX_VALUE,
    val spineIndex: Int? = null,
    val volumeId: String? = null,
    val characterStart: Int? = null,
    val characterCount: Int? = null,
    val wordCountLabel: String? = null,
    val status: ChimahonNovelImportChapterStatus = ChimahonNovelImportChapterStatus.Ready,
    val selected: Boolean = true,
    val imageOnly: Boolean = false,
    val warnings: List<String> = emptyList(),
    val duplicateOfId: String? = null,
) {
    val displayTitle: String
        get() {
            val fallbackIndex = (spineIndex ?: sourceOrder).takeIf { it != Int.MAX_VALUE } ?: 0
            return title.ifBlank { "Chapter ${fallbackIndex.coerceAtLeast(0) + 1}" }
        }

    val detailLabel: String?
        get() = buildList {
            spineIndex?.let { add("Spine ${it + 1}") }
            characterRangeLabel?.let { add(it) }
            wordCountLabel?.takeIf(String::isNotBlank)?.let { add(it) }
            if (imageOnly) add("Image-only")
            if (duplicateOfId != null) add("Duplicate")
            if (warnings.isNotEmpty()) add(warnings.first())
        }.joinToString(" - ").takeIf(String::isNotBlank)

    val characterRangeLabel: String?
        get() = when {
            characterStart != null && characterCount != null -> {
                "${characterStart + 1}-${characterStart + characterCount} chars"
            }
            characterCount != null -> "$characterCount chars"
            else -> null
        }

    fun stableLazyKey(index: Int): String {
        return "novel-import-chapter:$index:$id:${href.orEmpty()}:$sourceOrder:${spineIndex ?: -1}"
    }
}

data class ChimahonNovelImportOptionsUiState(
    val destinationCategoryId: String? = null,
    val destinationCategoryLabel: String? = null,
    val dictionaryProfileName: String? = null,
    val overwriteExisting: Boolean = false,
    val keepOriginalFileName: Boolean = true,
    val importSelectedChaptersOnly: Boolean = false,
    val showSkippedChapters: Boolean = true,
) {
    val summaryLabel: String
        get() = buildList {
            destinationCategoryLabel?.takeIf(String::isNotBlank)?.let { add("Category: $it") }
            dictionaryProfileName?.takeIf(String::isNotBlank)?.let { add("Dictionary: $it") }
            if (overwriteExisting) add("Overwrite matches")
            if (importSelectedChaptersOnly) add("Selected chapters only")
        }.joinToString(" - ").ifBlank { "Default import options" }
}

data class ChimahonNovelImportProgressUiModel(
    val fraction: Float = 0f,
    val processedCount: Int = 0,
    val totalCount: Int = 0,
    val percentLabel: String? = null,
    val bytesLabel: String? = null,
    val speedLabel: String? = null,
    val etaLabel: String? = null,
    val message: String? = null,
) {
    val normalizedFraction: Float
        get() = fraction.coerceIn(0f, 1f)

    val displayPercentLabel: String
        get() = percentLabel ?: "${(normalizedFraction * 100f).toInt()}%"

    val countLabel: String?
        get() = if (totalCount > 0) "$processedCount/$totalCount" else null

    val detailLabel: String?
        get() = listOfNotNull(bytesLabel, speedLabel, etaLabel).joinToString(" - ").takeIf(String::isNotBlank)

    val displayLabel: String?
        get() = listOfNotNull(
            message?.takeIf(String::isNotBlank),
            countLabel,
            displayPercentLabel.takeIf { normalizedFraction > 0f },
            detailLabel,
        ).joinToString(" - ").takeIf(String::isNotBlank)
}

data class ChimahonNovelImportSummaryUiModel(
    val total: Int = 0,
    val imported: Int = 0,
    val failed: Int = 0,
    val skipped: Int = 0,
    val duplicates: Int = 0,
) {
    val hasFailures: Boolean
        get() = failed > 0

    val displayLabel: String
        get() = buildList {
            if (total > 0) add("$total staged")
            if (imported > 0) add("$imported imported")
            if (failed > 0) add("$failed failed")
            if (skipped > 0) add("$skipped skipped")
            if (duplicates > 0) add("$duplicates duplicate")
        }.joinToString(" - ").ifBlank { "Ready" }
}

data class ChimahonNovelImportActions(
    val onNavigateUp: () -> Unit = {},
    val onSourceSelected: (ChimahonNovelImportSourceUiModel) -> Unit = {},
    val onPickFiles: () -> Unit = {},
    val onPickFolder: () -> Unit = {},
    val onScanQueue: () -> Unit = {},
    val onStartImport: () -> Unit = {},
    val onPauseImport: () -> Unit = {},
    val onResumeImport: () -> Unit = {},
    val onCancelImport: () -> Unit = {},
    val onRetryFailed: () -> Unit = {},
    val onClearQueue: () -> Unit = {},
    val onClearCompleted: () -> Unit = {},
    val onDismissError: () -> Unit = {},
    val onFileClick: (ChimahonNovelImportFileUiModel) -> Unit = {},
    val onFileSelectedChange: (ChimahonNovelImportFileUiModel, Boolean) -> Unit = { _, _ -> },
    val onFileRemove: (ChimahonNovelImportFileUiModel) -> Unit = {},
    val onFileRetry: (ChimahonNovelImportFileUiModel) -> Unit = {},
    val onEditMetadata: (ChimahonNovelImportMetadataUiModel) -> Unit = {},
    val onCoverClick: (ChimahonNovelImportMetadataUiModel) -> Unit = {},
    val onVolumeExpandedChange: (ChimahonNovelImportVolumeUiModel, Boolean) -> Unit = { _, _ -> },
    val onChapterSelectedChange: (ChimahonNovelImportChapterUiModel, Boolean) -> Unit = { _, _ -> },
    val onOptionsClick: () -> Unit = {},
)

data class ChimahonNovelImportToolbarUiState(
    val title: String,
    val subtitle: String? = null,
    val statusLabel: String? = null,
    val busy: Boolean = false,
    val selectedCount: Int = 0,
    val totalCount: Int = 0,
    val primaryActions: List<ChimahonNovelImportToolbarAction> = emptyList(),
    val overflowActions: List<ChimahonNovelImportToolbarAction> = emptyList(),
)

data class ChimahonNovelImportToolbarAction(
    val id: String,
    val label: String,
    val icon: ChimahonNovelImportUiIcon,
    val enabled: Boolean = true,
    val active: Boolean = false,
    val destructive: Boolean = false,
    val onClick: () -> Unit,
)

enum class ChimahonNovelImportSourceKind(val label: String) {
    EpubFiles("EPUB files"),
    Folder("Folder"),
    ExistingLocal("Local library"),
    SyncProvider("Sync provider"),
    Custom("Custom"),
}

enum class ChimahonNovelImportStage(val label: String) {
    Idle("Ready"),
    ChoosingSource("Choose source"),
    Scanning("Scanning files"),
    ReadingMetadata("Reading metadata"),
    Ready("Ready to import"),
    Importing("Importing"),
    Paused("Paused"),
    Complete("Import complete"),
    Failed("Import failed"),
    Canceled("Canceled"),
}

val ChimahonNovelImportStage.isBusy: Boolean
    get() = this == ChimahonNovelImportStage.Scanning ||
        this == ChimahonNovelImportStage.ReadingMetadata ||
        this == ChimahonNovelImportStage.Importing

enum class ChimahonNovelImportFileStatus(val label: String) {
    Staged("Staged"),
    Queued("Queued"),
    ReadingMetadata("Reading metadata"),
    Ready("Ready"),
    Importing("Importing"),
    Imported("Imported"),
    Skipped("Skipped"),
    Warning("Warning"),
    Failed("Failed"),
    Canceled("Canceled"),
}

val ChimahonNovelImportFileStatus.isActive: Boolean
    get() = this == ChimahonNovelImportFileStatus.ReadingMetadata ||
        this == ChimahonNovelImportFileStatus.Importing

val ChimahonNovelImportFileStatus.canImport: Boolean
    get() = this == ChimahonNovelImportFileStatus.Staged ||
        this == ChimahonNovelImportFileStatus.Queued ||
        this == ChimahonNovelImportFileStatus.Ready ||
        this == ChimahonNovelImportFileStatus.Warning

enum class ChimahonNovelImportChapterStatus(val label: String) {
    Ready("Ready"),
    Selected("Selected"),
    Skipped("Skipped"),
    Duplicate("Duplicate"),
    Warning("Warning"),
    Error("Error"),
}

enum class ChimahonNovelImportLibraryMatchKind(val label: String) {
    ExactId("Existing novel"),
    SameTitle("Same title"),
    SameFile("Same source file"),
    PossibleDuplicate("Possible duplicate"),
}

enum class ChimahonNovelImportStatusKind {
    Info,
    Active,
    Success,
    Warning,
    Error,
    Muted,
}

fun ChimahonNovelImportUiState.defaultToolbarState(
    actions: ChimahonNovelImportActions,
): ChimahonNovelImportToolbarUiState {
    val sourceKind = selectedSource?.kind
    val addAction = when (sourceKind) {
        ChimahonNovelImportSourceKind.Folder -> ChimahonNovelImportToolbarAction(
            id = "pick-folder",
            label = "Folder",
            icon = ChimahonNovelImportUiIcon.Storage,
            enabled = !hasBusyWork,
            onClick = actions.onPickFolder,
        )
        else -> ChimahonNovelImportToolbarAction(
            id = "pick-files",
            label = "Files",
            icon = ChimahonNovelImportUiIcon.File,
            enabled = !hasBusyWork,
            onClick = actions.onPickFiles,
        )
    }
    val primary = buildList {
        add(addAction)
        add(
            ChimahonNovelImportToolbarAction(
                id = "start-import",
                label = if (stage == ChimahonNovelImportStage.Complete) "Import more" else "Import",
                icon = ChimahonNovelImportUiIcon.Import,
                enabled = canStartImport,
                active = stage == ChimahonNovelImportStage.Importing,
                onClick = actions.onStartImport,
            ),
        )
        if (hasBusyWork) {
            add(
                ChimahonNovelImportToolbarAction(
                    id = "pause",
                    label = "Pause",
                    icon = ChimahonNovelImportUiIcon.Pause,
                    onClick = actions.onPauseImport,
                ),
            )
        } else if (stage == ChimahonNovelImportStage.Paused) {
            add(
                ChimahonNovelImportToolbarAction(
                    id = "resume",
                    label = "Resume",
                    icon = ChimahonNovelImportUiIcon.Resume,
                    onClick = actions.onResumeImport,
                ),
            )
        }
        if (canRetry) {
            add(
                ChimahonNovelImportToolbarAction(
                    id = "retry",
                    label = "Retry",
                    icon = ChimahonNovelImportUiIcon.Retry,
                    onClick = actions.onRetryFailed,
                ),
            )
        }
    }
    val overflow = buildList {
        add(
            ChimahonNovelImportToolbarAction(
                id = "scan",
                label = "Rescan metadata",
                icon = ChimahonNovelImportUiIcon.Refresh,
                enabled = files.isNotEmpty() && !hasBusyWork,
                onClick = actions.onScanQueue,
            ),
        )
        add(
            ChimahonNovelImportToolbarAction(
                id = "options",
                label = "Import options",
                icon = ChimahonNovelImportUiIcon.Filter,
                enabled = !hasBusyWork,
                onClick = actions.onOptionsClick,
            ),
        )
        add(
            ChimahonNovelImportToolbarAction(
                id = "clear-complete",
                label = "Clear completed",
                icon = ChimahonNovelImportUiIcon.Check,
                enabled = files.any { it.status == ChimahonNovelImportFileStatus.Imported } && !hasBusyWork,
                onClick = actions.onClearCompleted,
            ),
        )
        add(
            ChimahonNovelImportToolbarAction(
                id = "clear",
                label = "Clear queue",
                icon = ChimahonNovelImportUiIcon.Remove,
                enabled = files.isNotEmpty() && !hasBusyWork,
                destructive = true,
                onClick = actions.onClearQueue,
            ),
        )
        if (hasBusyWork || stage == ChimahonNovelImportStage.Paused) {
            add(
                ChimahonNovelImportToolbarAction(
                    id = "cancel",
                    label = "Cancel import",
                    icon = ChimahonNovelImportUiIcon.Error,
                    destructive = true,
                    onClick = actions.onCancelImport,
                ),
            )
        }
    }
    return ChimahonNovelImportToolbarUiState(
        title = title,
        subtitle = subtitle,
        statusLabel = statusLabel,
        busy = hasBusyWork,
        selectedCount = selectedFileIds.size,
        totalCount = files.size,
        primaryActions = primary,
        overflowActions = overflow,
    )
}

fun ChimahonNovelImportFileUiModel.matches(query: String): Boolean {
    if (query.isBlank()) return true
    val needle = query.trim().lowercase()
    val haystack = listOfNotNull(
        displayName,
        pathLabel,
        sizeLabel,
        mimeTypeLabel,
        metadata?.displayTitle,
        metadata?.creatorLine,
        metadata?.language,
        errorMessage,
        warningMessage,
    ).joinToString(" ").lowercase()
    return needle in haystack
}
