package app.chimahon.shared.novelimportui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonNovelImportScreen(
    state: ChimahonNovelImportUiState,
    actions: ChimahonNovelImportActions,
    modifier: Modifier = Modifier,
    showNavigateUp: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(bottom = 24.dp),
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
    coverContent: @Composable BoxScope.(ChimahonNovelImportMetadataUiModel) -> Unit = {
        NovelImportCoverPlaceholder(
            title = it.displayTitle,
            modifier = Modifier.fillMaxSize(),
            colors = colors,
        )
    },
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        ChimahonNovelImportCompactToolbar(
            state = state.defaultToolbarState(actions),
            onNavigateUp = if (showNavigateUp) actions.onNavigateUp else null,
            colors = colors,
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = contentPadding,
        ) {
            item(key = "source-chooser") {
                ChimahonNovelImportSourceChooser(
                    sources = state.sources,
                    selectedSourceId = state.selectedSourceId,
                    onSourceSelected = actions.onSourceSelected,
                    colors = colors,
                )
            }
            item(key = "status-strip") {
                ChimahonNovelImportStatusStrip(
                    state = state,
                    actions = actions,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = colors,
                )
            }
            item(key = "options") {
                ChimahonNovelImportOptionsStrip(
                    options = state.options,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = colors,
                )
            }
            if (state.files.isEmpty()) {
                item(key = "empty") {
                    ChimahonNovelImportEmptyQueueState(
                        onPickFiles = actions.onPickFiles,
                        onPickFolder = actions.onPickFolder,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        colors = colors,
                    )
                }
            } else {
                item(key = "queue-header") {
                    NovelImportSectionHeader(
                        title = "File queue",
                        subtitle = "${state.visibleFiles.size} shown of ${state.files.size}",
                        colors = colors,
                    )
                }
                itemsIndexed(
                    items = state.visibleFiles,
                    key = { index, file -> file.stableLazyKey(index) },
                ) { _, file ->
                    ChimahonNovelImportFileRow(
                        file = file,
                        selected = file.id in state.selectedFileIds || file.selected,
                        onClick = { actions.onFileClick(file) },
                        onSelectedChange = { selected -> actions.onFileSelectedChange(file, selected) },
                        onRetry = { actions.onFileRetry(file) },
                        onRemove = { actions.onFileRemove(file) },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        colors = colors,
                    )
                }
            }
            state.metadataPreview?.let { metadata ->
                item(key = "metadata-preview") {
                    ChimahonNovelImportMetadataPreview(
                        metadata = metadata,
                        onEditClick = { actions.onEditMetadata(metadata) },
                        onCoverClick = { actions.onCoverClick(metadata) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        colors = colors,
                        coverContent = coverContent,
                    )
                }
            }
            if (state.volumes.isNotEmpty() || state.visibleChapters.isNotEmpty()) {
                item(key = "chapter-header") {
                    NovelImportSectionHeader(
                        title = "Volumes and chapters",
                        subtitle = "${state.selectedChapterCount} selected of ${state.chapters.size}",
                        colors = colors,
                    )
                }
                novelImportChapterItems(
                    volumes = state.volumes,
                    chapters = state.visibleChapters,
                    actions = actions,
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun ChimahonNovelImportStatusStrip(
    state: ChimahonNovelImportUiState,
    actions: ChimahonNovelImportActions,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    when {
        state.errorMessage != null -> ChimahonNovelImportErrorState(
            message = state.errorMessage,
            onRetry = actions.onRetryFailed,
            onDismiss = actions.onDismissError,
            modifier = modifier,
            colors = colors,
        )
        state.warningMessage != null -> NovelImportStatusCard(
            title = "Import warning",
            subtitle = state.warningMessage,
            icon = ChimahonNovelImportUiIcon.Warning,
            kind = ChimahonNovelImportStatusKind.Warning,
            actionLabel = "Dismiss",
            onAction = actions.onDismissError,
            modifier = modifier,
            colors = colors,
        )
        state.hasBusyWork -> ChimahonNovelImportProgressState(
            stage = state.stage,
            progress = state.progress,
            onCancel = actions.onCancelImport,
            modifier = modifier,
            colors = colors,
        )
        state.stage == ChimahonNovelImportStage.Complete -> ChimahonNovelImportCompleteState(
            summary = state.summary,
            onImportMore = actions.onPickFiles,
            onClearCompleted = actions.onClearCompleted,
            modifier = modifier,
            colors = colors,
        )
        else -> NovelImportStatusCard(
            title = state.statusLabel,
            subtitle = state.selectedSource?.supportingLabel ?: state.options.summaryLabel,
            icon = ChimahonNovelImportUiIcon.File,
            kind = ChimahonNovelImportStatusKind.Info,
            modifier = modifier,
            colors = colors,
        )
    }
}

@Composable
fun ChimahonNovelImportFileQueue(
    files: List<ChimahonNovelImportFileUiModel>,
    onFileClick: (ChimahonNovelImportFileUiModel) -> Unit,
    onFileSelectedChange: (ChimahonNovelImportFileUiModel, Boolean) -> Unit,
    onFileRetry: (ChimahonNovelImportFileUiModel) -> Unit,
    onFileRemove: (ChimahonNovelImportFileUiModel) -> Unit,
    modifier: Modifier = Modifier,
    selectedFileIds: Set<String> = emptySet(),
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        itemsIndexed(
            items = files,
            key = { index, file -> file.stableLazyKey(index) },
        ) { _, file ->
            ChimahonNovelImportFileRow(
                file = file,
                selected = file.id in selectedFileIds || file.selected,
                onClick = { onFileClick(file) },
                onSelectedChange = { selected -> onFileSelectedChange(file, selected) },
                onRetry = { onFileRetry(file) },
                onRemove = { onFileRemove(file) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonNovelImportFileRow(
    file: ChimahonNovelImportFileUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    onSelectedChange: (Boolean) -> Unit,
    onRetry: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    showSelection: Boolean = true,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    NovelImportCardSurface(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        colors = colors,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (showSelection) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = onSelectedChange,
                    enabled = !file.status.isActive,
                )
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(file.statusKind.backgroundColor(colors)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = file.status.icon().imageVector,
                    contentDescription = null,
                    tint = file.statusKind.foregroundColor(colors),
                    modifier = Modifier.size(21.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    NovelImportLabel(
                        text = file.title,
                        color = colors.content,
                        size = 14,
                        weight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                    )
                    NovelImportStatusChip(
                        text = file.status.label,
                        kind = file.statusKind,
                        colors = colors,
                    )
                }
                file.detailLabel?.let { label ->
                    NovelImportLabel(
                        text = label,
                        color = colors.secondaryContent,
                        size = 11,
                    )
                }
                NovelImportLabel(
                    text = file.statusDetailLabel,
                    color = if (file.statusKind == ChimahonNovelImportStatusKind.Error) {
                        colors.error
                    } else {
                        colors.secondaryContent
                    },
                    size = 11,
                    lineHeight = 16,
                    maxLines = 2,
                )
                if (file.status.isActive || file.progress.normalizedFraction > 0f) {
                    NovelImportProgressBar(
                        fraction = file.progress.normalizedFraction,
                        colors = colors,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                if (file.duplicate) {
                    NovelImportStatusChip(
                        text = "Duplicate",
                        kind = ChimahonNovelImportStatusKind.Warning,
                        colors = colors,
                    )
                }
            }
            if (file.canRetry) {
                NovelImportIconButton(
                    icon = ChimahonNovelImportUiIcon.Retry,
                    contentDescription = "Retry",
                    onClick = onRetry,
                    colors = colors,
                )
            }
            if (file.canRemove) {
                NovelImportIconButton(
                    icon = ChimahonNovelImportUiIcon.Remove,
                    contentDescription = "Remove",
                    onClick = onRemove,
                    enabled = !file.status.isActive,
                    destructive = true,
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun ChimahonNovelImportMetadataPreview(
    metadata: ChimahonNovelImportMetadataUiModel,
    onEditClick: () -> Unit,
    onCoverClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
    coverContent: @Composable BoxScope.(ChimahonNovelImportMetadataUiModel) -> Unit = {
        NovelImportCoverPlaceholder(
            title = it.displayTitle,
            modifier = Modifier.fillMaxSize(),
            colors = colors,
        )
    },
) {
    NovelImportCardSurface(
        modifier = modifier,
        colors = colors,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(88.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(colors.surfaceVariant)
                        .clickable(onClick = onCoverClick),
                ) {
                    coverContent(metadata)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        NovelImportLabel(
                            text = metadata.displayTitle,
                            color = colors.content,
                            size = 18,
                            weight = FontWeight.SemiBold,
                            maxLines = 2,
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(onClick = onEditClick) {
                            Text("Edit")
                        }
                    }
                    metadata.creatorLine?.let { creator ->
                        NovelImportLabel(
                            text = creator,
                            color = colors.content.copy(alpha = 0.78f),
                            size = 13,
                        )
                    }
                    metadata.subtitle?.let { subtitle ->
                        NovelImportLabel(
                            text = subtitle,
                            color = colors.secondaryContent,
                            size = 12,
                            maxLines = 2,
                            lineHeight = 17,
                        )
                    }
                    NovelImportLabel(
                        text = metadata.countLabel,
                        color = colors.secondaryContent,
                        size = 12,
                        maxLines = 2,
                    )
                    metadata.metadataLine?.let { line ->
                        NovelImportLabel(
                            text = line,
                            color = colors.secondaryContent,
                            size = 11,
                        )
                    }
                }
            }
            if (metadata.categoryLabels.isNotEmpty() || metadata.tags.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    (metadata.categoryLabels + metadata.tags).forEach { label ->
                        NovelImportStatusChip(
                            text = label,
                            kind = ChimahonNovelImportStatusKind.Info,
                            colors = colors,
                        )
                    }
                }
            }
            metadata.existingLibraryMatch?.let { match ->
                NovelImportStatusCard(
                    title = match.title,
                    subtitle = match.detailLabel ?: match.matchKind.label,
                    icon = ChimahonNovelImportUiIcon.Warning,
                    kind = ChimahonNovelImportStatusKind.Warning,
                    modifier = Modifier.padding(top = 12.dp),
                    colors = colors,
                )
            }
            metadata.description?.takeIf(String::isNotBlank)?.let { description ->
                Divider(
                    color = colors.divider,
                    modifier = Modifier.padding(top = 12.dp, bottom = 10.dp),
                )
                NovelImportLabel(
                    text = description,
                    color = colors.secondaryContent,
                    size = 12,
                    lineHeight = 18,
                    maxLines = 5,
                )
            }
        }
    }
}

@Composable
fun ChimahonNovelImportChapterVolumeList(
    volumes: List<ChimahonNovelImportVolumeUiModel>,
    chapters: List<ChimahonNovelImportChapterUiModel>,
    actions: ChimahonNovelImportActions,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        novelImportChapterItems(
            volumes = volumes,
            chapters = chapters,
            actions = actions,
            colors = colors,
        )
    }
}

@Composable
fun ChimahonNovelImportVolumeHeader(
    volume: ChimahonNovelImportVolumeUiModel,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onExpandedChange(!volume.expanded) },
        color = colors.background,
        contentColor = colors.content,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = if (volume.expanded) {
                    ChimahonNovelImportUiIcon.Collapse.imageVector
                } else {
                    ChimahonNovelImportUiIcon.Expand.imageVector
                },
                contentDescription = if (volume.expanded) "Collapse volume" else "Expand volume",
                tint = colors.secondaryContent,
                modifier = Modifier.size(18.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                NovelImportLabel(
                    text = volume.title,
                    color = colors.content,
                    size = 14,
                    weight = FontWeight.SemiBold,
                )
                NovelImportLabel(
                    text = volume.warningMessage ?: volume.chapterCountLabel,
                    color = if (volume.warningMessage != null) colors.warning else colors.secondaryContent,
                    size = 11,
                )
            }
            if (volume.selectedCount > 0) {
                NovelImportStatusChip(
                    text = "${volume.selectedCount} selected",
                    kind = ChimahonNovelImportStatusKind.Active,
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun ChimahonNovelImportChapterRow(
    chapter: ChimahonNovelImportChapterUiModel,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    val statusKind = chapter.status.statusKind(chapter.warnings.isNotEmpty(), chapter.duplicateOfId != null)
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (chapter.selected) colors.primary.copy(alpha = 0.05f) else colors.surface,
        contentColor = colors.content,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Checkbox(
                checked = chapter.selected,
                onCheckedChange = onSelectedChange,
                enabled = chapter.status != ChimahonNovelImportChapterStatus.Error,
            )
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(statusKind.backgroundColor(colors)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = chapter.status.icon().imageVector,
                    contentDescription = null,
                    tint = statusKind.foregroundColor(colors),
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                NovelImportLabel(
                    text = chapter.displayTitle,
                    color = if (chapter.selected) colors.content else colors.secondaryContent,
                    size = 14,
                    weight = if (chapter.selected) FontWeight.Medium else FontWeight.Normal,
                    maxLines = 2,
                )
                chapter.detailLabel?.let { detail ->
                    NovelImportLabel(
                        text = detail,
                        color = colors.secondaryContent,
                        size = 11,
                        lineHeight = 16,
                        maxLines = 2,
                    )
                }
            }
            when {
                chapter.status != ChimahonNovelImportChapterStatus.Ready -> NovelImportStatusChip(
                    text = chapter.status.label,
                    kind = statusKind,
                    colors = colors,
                )
                !chapter.selected -> NovelImportStatusChip(
                    text = "Skipped",
                    kind = ChimahonNovelImportStatusKind.Muted,
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun ChimahonNovelImportOptionsStrip(
    options: ChimahonNovelImportOptionsUiState,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    NovelImportCardSurface(
        modifier = modifier,
        colors = colors,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NovelImportLabel(
                text = "Options",
                color = colors.content,
                size = 13,
                weight = FontWeight.SemiBold,
            )
            NovelImportStatusChip(
                text = options.destinationCategoryLabel ?: "Default category",
                kind = ChimahonNovelImportStatusKind.Info,
                colors = colors,
            )
            options.dictionaryProfileName?.takeIf(String::isNotBlank)?.let { profile ->
                NovelImportStatusChip(
                    text = profile,
                    kind = ChimahonNovelImportStatusKind.Info,
                    colors = colors,
                )
            }
            if (options.overwriteExisting) {
                NovelImportStatusChip(
                    text = "Overwrite",
                    kind = ChimahonNovelImportStatusKind.Warning,
                    colors = colors,
                )
            }
            if (options.importSelectedChaptersOnly) {
                NovelImportStatusChip(
                    text = "Selected chapters",
                    kind = ChimahonNovelImportStatusKind.Active,
                    colors = colors,
                )
            }
            if (options.keepOriginalFileName) {
                NovelImportStatusChip(
                    text = "Keep file name",
                    kind = ChimahonNovelImportStatusKind.Info,
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun ChimahonNovelImportProgressState(
    stage: ChimahonNovelImportStage,
    progress: ChimahonNovelImportProgressUiModel,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    NovelImportStatusCard(
        title = stage.label,
        subtitle = progress.displayLabel ?: "Working through the staged EPUB queue.",
        icon = ChimahonNovelImportUiIcon.Import,
        kind = ChimahonNovelImportStatusKind.Active,
        progress = progress,
        actionLabel = "Cancel",
        onAction = onCancel,
        modifier = modifier,
        colors = colors,
    )
}

@Composable
fun ChimahonNovelImportErrorState(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    NovelImportStatusCard(
        title = "Import failed",
        subtitle = message,
        icon = ChimahonNovelImportUiIcon.Error,
        kind = ChimahonNovelImportStatusKind.Error,
        actionLabel = "Retry",
        onAction = onRetry,
        modifier = modifier,
        colors = colors,
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        TextButton(onClick = onDismiss) {
            Text("Dismiss")
        }
    }
}

@Composable
fun ChimahonNovelImportEmptyQueueState(
    onPickFiles: () -> Unit,
    onPickFolder: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NovelImportCenteredState(
                title = "No EPUBs staged",
                body = "Choose EPUB files or scan a folder to preview metadata before importing.",
                icon = ChimahonNovelImportUiIcon.File,
                colors = colors,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onPickFiles,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary,
                    ),
                ) {
                    Text("Pick files")
                }
                TextButton(onClick = onPickFolder) {
                    Text("Scan folder")
                }
            }
        }
    }
}

@Composable
fun ChimahonNovelImportCompleteState(
    summary: ChimahonNovelImportSummaryUiModel,
    onImportMore: () -> Unit,
    onClearCompleted: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    NovelImportStatusCard(
        title = if (summary.hasFailures) "Import finished with errors" else "Import complete",
        subtitle = summary.displayLabel,
        icon = if (summary.hasFailures) ChimahonNovelImportUiIcon.Warning else ChimahonNovelImportUiIcon.Check,
        kind = if (summary.hasFailures) {
            ChimahonNovelImportStatusKind.Warning
        } else {
            ChimahonNovelImportStatusKind.Success
        },
        actionLabel = "Import more",
        onAction = onImportMore,
        modifier = modifier,
        colors = colors,
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        TextButton(onClick = onClearCompleted) {
            Text("Clear completed")
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.novelImportChapterItems(
    volumes: List<ChimahonNovelImportVolumeUiModel>,
    chapters: List<ChimahonNovelImportChapterUiModel>,
    actions: ChimahonNovelImportActions,
    colors: ChimahonNovelImportUiColors,
) {
    val knownVolumeIds = volumes.map { it.id }.toSet()
    val chaptersByVolume = chapters.groupBy { it.volumeId }
    volumes.forEachIndexed { volumeIndex, volume ->
        item(key = volume.stableLazyKey(volumeIndex)) {
            ChimahonNovelImportVolumeHeader(
                volume = volume,
                onExpandedChange = { expanded -> actions.onVolumeExpandedChange(volume, expanded) },
                colors = colors,
            )
        }
        if (volume.expanded) {
            val volumeChapters = chaptersByVolume[volume.id].orEmpty()
            itemsIndexed(
                items = volumeChapters,
                key = { index, chapter -> chapter.stableLazyKey(index) },
            ) { _, chapter ->
                ChimahonNovelImportChapterRow(
                    chapter = chapter,
                    onSelectedChange = { selected -> actions.onChapterSelectedChange(chapter, selected) },
                    colors = colors,
                )
            }
        }
    }
    val ungroupedChapters = chapters.filter { it.volumeId == null || it.volumeId !in knownVolumeIds }
    if (volumes.isNotEmpty() && ungroupedChapters.isNotEmpty()) {
        item(key = "ungrouped-import-chapters") {
            NovelImportSectionHeader(
                title = "Ungrouped chapters",
                subtitle = "${ungroupedChapters.size} chapter(s)",
                colors = colors,
            )
        }
    }
    if (volumes.isEmpty()) {
        itemsIndexed(
            items = chapters,
            key = { index, chapter -> chapter.stableLazyKey(index) },
        ) { _, chapter ->
            ChimahonNovelImportChapterRow(
                chapter = chapter,
                onSelectedChange = { selected -> actions.onChapterSelectedChange(chapter, selected) },
                colors = colors,
            )
        }
    } else {
        itemsIndexed(
            items = ungroupedChapters,
            key = { index, chapter -> chapter.stableLazyKey(index) },
        ) { _, chapter ->
            ChimahonNovelImportChapterRow(
                chapter = chapter,
                onSelectedChange = { selected -> actions.onChapterSelectedChange(chapter, selected) },
                colors = colors,
            )
        }
    }
}

private fun ChimahonNovelImportChapterStatus.statusKind(
    hasWarnings: Boolean,
    duplicate: Boolean,
): ChimahonNovelImportStatusKind {
    return when {
        this == ChimahonNovelImportChapterStatus.Error -> ChimahonNovelImportStatusKind.Error
        this == ChimahonNovelImportChapterStatus.Warning || hasWarnings -> ChimahonNovelImportStatusKind.Warning
        this == ChimahonNovelImportChapterStatus.Duplicate || duplicate -> ChimahonNovelImportStatusKind.Warning
        this == ChimahonNovelImportChapterStatus.Skipped -> ChimahonNovelImportStatusKind.Muted
        this == ChimahonNovelImportChapterStatus.Selected -> ChimahonNovelImportStatusKind.Active
        else -> ChimahonNovelImportStatusKind.Info
    }
}

private fun ChimahonNovelImportStatusKind.foregroundColor(colors: ChimahonNovelImportUiColors) = when (this) {
    ChimahonNovelImportStatusKind.Info -> colors.secondaryContent
    ChimahonNovelImportStatusKind.Active -> colors.primary
    ChimahonNovelImportStatusKind.Success -> colors.success
    ChimahonNovelImportStatusKind.Warning -> colors.warning
    ChimahonNovelImportStatusKind.Error -> colors.error
    ChimahonNovelImportStatusKind.Muted -> colors.secondaryContent.copy(alpha = 0.72f)
}

private fun ChimahonNovelImportStatusKind.backgroundColor(colors: ChimahonNovelImportUiColors) = when (this) {
    ChimahonNovelImportStatusKind.Info -> colors.surfaceVariant
    ChimahonNovelImportStatusKind.Active -> colors.primaryContainer
    ChimahonNovelImportStatusKind.Success -> colors.success.copy(alpha = 0.14f)
    ChimahonNovelImportStatusKind.Warning -> colors.warning.copy(alpha = 0.14f)
    ChimahonNovelImportStatusKind.Error -> colors.error.copy(alpha = 0.12f)
    ChimahonNovelImportStatusKind.Muted -> colors.surfaceVariant
}
