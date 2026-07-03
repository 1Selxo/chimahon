package app.chimahon.shared.animequeueui

data class ChimahonAnimeQueueRowCallbacks(
    val onOpenRow: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onPlayEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onStreamEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onDownloadEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onRetryEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onPauseEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onResumeEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onCancelEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onRemoveEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onPriorityChange: (ChimahonAnimeQueueRowUiModel, ChimahonAnimeQueuePriority) -> Unit = { _, _ -> },
    val onMoveToTop: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onMoveUp: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onMoveDown: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onMoveToBottom: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
) {
    fun toRowActions(
        onSelectionChange: (ChimahonAnimeQueueRowUiModel, Boolean) -> Unit = { _, _ -> },
    ): ChimahonAnimeQueueRowActions {
        return ChimahonAnimeQueueRowActions(
            onOpenRow = onOpenRow,
            onSelectionChange = onSelectionChange,
            onPlayEpisode = onPlayEpisode,
            onStreamEpisode = onStreamEpisode,
            onDownloadEpisode = onDownloadEpisode,
            onRetryEpisode = onRetryEpisode,
            onPauseEpisode = onPauseEpisode,
            onResumeEpisode = onResumeEpisode,
            onCancelEpisode = onCancelEpisode,
            onRemoveEpisode = onRemoveEpisode,
            onPriorityChange = onPriorityChange,
            onMoveToTop = onMoveToTop,
            onMoveUp = onMoveUp,
            onMoveDown = onMoveDown,
            onMoveToBottom = onMoveToBottom,
        )
    }
}

data class ChimahonAnimeEpisodeBatchCallbacks(
    val onPlaySelected: () -> Unit = {},
    val onStreamSelected: () -> Unit = {},
    val onDownloadSelected: () -> Unit = {},
    val onPauseSelected: () -> Unit = {},
    val onResumeSelected: () -> Unit = {},
    val onRetrySelected: () -> Unit = {},
    val onCancelSelected: () -> Unit = {},
    val onRemoveSelected: () -> Unit = {},
    val onMarkSeenSelected: () -> Unit = {},
    val onMarkUnseenSelected: () -> Unit = {},
    val onMoveSelectedToTop: () -> Unit = {},
    val onMoveSelectedToBottom: () -> Unit = {},
    val onPrioritySelected: (ChimahonAnimeQueuePriority) -> Unit = {},
)

fun ChimahonAnimeQueueRowUiModel.withDefaultAnimeQueueOverflowActions(
    callbacks: ChimahonAnimeQueueRowCallbacks,
    includeOpen: Boolean = true,
    includePlayback: Boolean = true,
    includePriority: Boolean = true,
    includeReorder: Boolean = true,
    includeRemove: Boolean = true,
): ChimahonAnimeQueueRowUiModel {
    return copy(
        overflowActions = defaultAnimeQueueOverflowActions(
            callbacks = callbacks,
            includeOpen = includeOpen,
            includePlayback = includePlayback,
            includePriority = includePriority,
            includeReorder = includeReorder,
            includeRemove = includeRemove,
        ),
    )
}

fun ChimahonAnimeQueueRowUiModel.defaultAnimeQueueOverflowActions(
    callbacks: ChimahonAnimeQueueRowCallbacks,
    includeOpen: Boolean = true,
    includePlayback: Boolean = true,
    includePriority: Boolean = true,
    includeReorder: Boolean = true,
    includeRemove: Boolean = true,
): List<ChimahonAnimeQueueOverflowAction> {
    val row = this
    return buildList {
        if (includeOpen) {
            add(
                animeQueueAction(
                    id = "open",
                    label = "Open",
                    icon = ChimahonAnimeQueueActionIcon.Open,
                    onClick = { callbacks.onOpenRow(row) },
                ),
            )
        }
        if (includePlayback) {
            when {
                row.canRetry -> add(
                    animeQueueAction(
                        id = "retry",
                        label = row.error?.retryLabel ?: "Retry",
                        icon = ChimahonAnimeQueueActionIcon.Retry,
                        onClick = { callbacks.onRetryEpisode(row) },
                    ),
                )
                row.canResume -> add(
                    animeQueueAction(
                        id = "resume",
                        label = "Resume",
                        icon = ChimahonAnimeQueueActionIcon.Resume,
                        onClick = { callbacks.onResumeEpisode(row) },
                    ),
                )
                row.canPause -> add(
                    animeQueueAction(
                        id = "pause",
                        label = "Pause",
                        icon = ChimahonAnimeQueueActionIcon.Pause,
                        onClick = { callbacks.onPauseEpisode(row) },
                    ),
                )
            }
            if (row.canDownload) {
                add(
                    animeQueueAction(
                        id = "download",
                        label = "Download",
                        icon = ChimahonAnimeQueueActionIcon.Download,
                        onClick = { callbacks.onDownloadEpisode(row) },
                    ),
                )
            }
            if (row.canStream) {
                add(
                    animeQueueAction(
                        id = "stream",
                        label = "Stream",
                        icon = ChimahonAnimeQueueActionIcon.Stream,
                        onClick = { callbacks.onStreamEpisode(row) },
                    ),
                )
            }
            if (row.canPlay) {
                add(
                    animeQueueAction(
                        id = "play",
                        label = "Play",
                        icon = ChimahonAnimeQueueActionIcon.Play,
                        onClick = { callbacks.onPlayEpisode(row) },
                    ),
                )
            }
            if (row.canCancel) {
                add(
                    animeQueueAction(
                        id = "cancel",
                        label = "Cancel",
                        icon = ChimahonAnimeQueueActionIcon.Cancel,
                        destructive = true,
                        onClick = { callbacks.onCancelEpisode(row) },
                    ),
                )
            }
        }
        if (includePriority) {
            ChimahonAnimeQueuePriority.entries
                .filterNot { it == row.priority }
                .forEach { priority ->
                    add(
                        animeQueueAction(
                            id = "priority-${priority.name.lowercase()}",
                            label = "Priority: ${priority.label}",
                            icon = ChimahonAnimeQueueActionIcon.Priority,
                            onClick = { callbacks.onPriorityChange(row, priority) },
                        ),
                    )
                }
        }
        if (includeReorder && row.reorder.canMove) {
            addMoveAction(
                enabled = row.reorder.canMoveToTop,
                id = "move-top",
                label = "Move to top",
                icon = ChimahonAnimeQueueActionIcon.MoveTop,
                onClick = { callbacks.onMoveToTop(row) },
            )
            addMoveAction(
                enabled = row.reorder.canMoveUp,
                id = "move-up",
                label = "Move up",
                icon = ChimahonAnimeQueueActionIcon.MoveUp,
                onClick = { callbacks.onMoveUp(row) },
            )
            addMoveAction(
                enabled = row.reorder.canMoveDown,
                id = "move-down",
                label = "Move down",
                icon = ChimahonAnimeQueueActionIcon.MoveDown,
                onClick = { callbacks.onMoveDown(row) },
            )
            addMoveAction(
                enabled = row.reorder.canMoveToBottom,
                id = "move-bottom",
                label = "Move to bottom",
                icon = ChimahonAnimeQueueActionIcon.MoveBottom,
                onClick = { callbacks.onMoveToBottom(row) },
            )
        }
        if (includeRemove) {
            add(
                animeQueueAction(
                    id = "remove",
                    label = "Remove",
                    icon = ChimahonAnimeQueueActionIcon.Remove,
                    destructive = true,
                    onClick = { callbacks.onRemoveEpisode(row) },
                ),
            )
        }
    }
}

fun ChimahonAnimeQueueUiState.defaultAnimeEpisodeBatchActionBarState(
    callbacks: ChimahonAnimeEpisodeBatchCallbacks,
): ChimahonAnimeEpisodeBatchActionBarState {
    val rows = selectedRows
    val hasSelection = selectedCount > 0
    return ChimahonAnimeEpisodeBatchActionBarState(
        selectedCount = selectedCount,
        totalCount = this.rows.size,
        title = "$selectedCount episode(s) selected",
        allSelected = allRowsSelected,
        primaryActions = defaultAnimeEpisodeBatchActions(rows, callbacks),
        overflowActions = defaultAnimeEpisodeBatchOverflowActions(
            rows = rows,
            callbacks = callbacks,
            enabled = hasSelection,
        ),
    )
}

fun defaultAnimeEpisodeBatchActions(
    selectedRows: List<ChimahonAnimeQueueRowUiModel>,
    callbacks: ChimahonAnimeEpisodeBatchCallbacks,
): List<ChimahonAnimeEpisodeBatchAction> {
    val enabled = selectedRows.isNotEmpty()
    return listOf(
        ChimahonAnimeEpisodeBatchAction(
            id = "play",
            label = "Play",
            icon = ChimahonAnimeQueueActionIcon.Play,
            enabled = enabled && selectedRows.any { it.canPlay },
            onClick = callbacks.onPlaySelected,
        ),
        ChimahonAnimeEpisodeBatchAction(
            id = "download",
            label = "Download",
            icon = ChimahonAnimeQueueActionIcon.Download,
            enabled = enabled && selectedRows.any { it.canDownload },
            onClick = callbacks.onDownloadSelected,
        ),
        ChimahonAnimeEpisodeBatchAction(
            id = "pause",
            label = "Pause",
            icon = ChimahonAnimeQueueActionIcon.Pause,
            enabled = enabled && selectedRows.any { it.canPause },
            onClick = callbacks.onPauseSelected,
        ),
        ChimahonAnimeEpisodeBatchAction(
            id = "resume",
            label = "Resume",
            icon = ChimahonAnimeQueueActionIcon.Resume,
            enabled = enabled && selectedRows.any { it.canResume },
            onClick = callbacks.onResumeSelected,
        ),
        ChimahonAnimeEpisodeBatchAction(
            id = "retry",
            label = "Retry",
            icon = ChimahonAnimeQueueActionIcon.Retry,
            enabled = enabled && selectedRows.any { it.canRetry },
            onClick = callbacks.onRetrySelected,
        ),
    )
}

fun defaultAnimeEpisodeBatchOverflowActions(
    rows: List<ChimahonAnimeQueueRowUiModel>,
    callbacks: ChimahonAnimeEpisodeBatchCallbacks,
    enabled: Boolean = rows.isNotEmpty(),
): List<ChimahonAnimeQueueOverflowAction> {
    return buildList {
        add(
            animeQueueAction(
                id = "stream",
                label = "Stream",
                icon = ChimahonAnimeQueueActionIcon.Stream,
                enabled = enabled && rows.any { it.canStream },
                onClick = callbacks.onStreamSelected,
            ),
        )
        add(
            animeQueueAction(
                id = "mark-seen",
                label = "Mark seen",
                icon = ChimahonAnimeQueueActionIcon.MarkSeen,
                enabled = enabled,
                onClick = callbacks.onMarkSeenSelected,
            ),
        )
        add(
            animeQueueAction(
                id = "mark-unseen",
                label = "Mark unseen",
                icon = ChimahonAnimeQueueActionIcon.MarkUnseen,
                enabled = enabled,
                onClick = callbacks.onMarkUnseenSelected,
            ),
        )
        ChimahonAnimeQueuePriority.entries.forEach { priority ->
            add(
                animeQueueAction(
                    id = "priority-${priority.name.lowercase()}",
                    label = "Priority: ${priority.label}",
                    icon = ChimahonAnimeQueueActionIcon.Priority,
                    enabled = enabled,
                    onClick = { callbacks.onPrioritySelected(priority) },
                ),
            )
        }
        add(
            animeQueueAction(
                id = "move-top",
                label = "Move to top",
                icon = ChimahonAnimeQueueActionIcon.MoveTop,
                enabled = enabled && rows.any { it.reorder.canMoveToTop },
                onClick = callbacks.onMoveSelectedToTop,
            ),
        )
        add(
            animeQueueAction(
                id = "move-bottom",
                label = "Move to bottom",
                icon = ChimahonAnimeQueueActionIcon.MoveBottom,
                enabled = enabled && rows.any { it.reorder.canMoveToBottom },
                onClick = callbacks.onMoveSelectedToBottom,
            ),
        )
        add(
            animeQueueAction(
                id = "cancel",
                label = "Cancel",
                icon = ChimahonAnimeQueueActionIcon.Cancel,
                enabled = enabled && rows.any { it.canCancel },
                destructive = true,
                onClick = callbacks.onCancelSelected,
            ),
        )
        add(
            animeQueueAction(
                id = "remove",
                label = "Remove",
                icon = ChimahonAnimeQueueActionIcon.Remove,
                enabled = enabled,
                destructive = true,
                onClick = callbacks.onRemoveSelected,
            ),
        )
    }
}

fun ChimahonAnimeQueueUiState.withSelectedEpisodes(
    episodeIds: Set<String>,
): ChimahonAnimeQueueUiState {
    val validIds = episodeIds.intersect(rows.mapTo(mutableSetOf()) { it.id })
    return copy(
        selectedEpisodeIds = validIds,
        rows = rows.map { row -> row.copy(selected = row.id in validIds) },
        playlist = playlist.copy(
            episodes = playlist.episodes.map { episode -> episode.copy(selected = episode.id in validIds) },
        ),
    )
}

fun ChimahonAnimeQueueUiState.withAllEpisodesSelected(): ChimahonAnimeQueueUiState {
    return withSelectedEpisodes(rows.mapTo(mutableSetOf()) { it.id })
}

fun ChimahonAnimeQueueUiState.withSelectionCleared(): ChimahonAnimeQueueUiState {
    return withSelectedEpisodes(emptySet())
}

fun List<ChimahonAnimeQueueRowUiModel>.withAnimeQueueReorderStates(): List<ChimahonAnimeQueueRowUiModel> {
    val totalCount = size
    return mapIndexed { index, row ->
        row.copy(reorder = chimahonAnimeQueueReorderState(index = index, totalCount = totalCount))
    }
}

fun chimahonAnimeQueueReorderState(
    index: Int,
    totalCount: Int,
): ChimahonAnimeQueueReorderState {
    val safeIndex = index.coerceIn(0, (totalCount - 1).coerceAtLeast(0))
    val canMoveUp = totalCount > 1 && safeIndex > 0
    val canMoveDown = totalCount > 1 && safeIndex < totalCount - 1
    return ChimahonAnimeQueueReorderState(
        positionLabel = if (totalCount > 0) "#${safeIndex + 1}" else null,
        canMoveToTop = canMoveUp,
        canMoveUp = canMoveUp,
        canMoveDown = canMoveDown,
        canMoveToBottom = canMoveDown,
    )
}

fun chimahonAnimeEpisodeAvailability(
    local: Boolean = false,
    stream: Boolean = true,
    downloaded: Boolean = false,
    unavailable: Boolean = false,
): Set<ChimahonAnimeEpisodeAvailability> {
    if (unavailable) return setOf(ChimahonAnimeEpisodeAvailability.Unavailable)
    return buildSet {
        if (local) add(ChimahonAnimeEpisodeAvailability.Local)
        if (stream) add(ChimahonAnimeEpisodeAvailability.Stream)
        if (downloaded) add(ChimahonAnimeEpisodeAvailability.Downloaded)
        if (isEmpty()) add(ChimahonAnimeEpisodeAvailability.Unavailable)
    }
}

fun List<ChimahonAnimeQueueRowUiModel>.toAnimeAutoNextPlaylistUiState(
    autoPlayEnabled: Boolean,
    title: String = "Up next",
    subtitle: String? = null,
    currentEpisodeId: String? = null,
    countdownSeconds: Int? = null,
    includeCompleted: Boolean = false,
): ChimahonAnimeAutoNextPlaylistUiState {
    val playlistRows = filter { includeCompleted || it.status != ChimahonAnimeQueueStatus.Complete }
    val resolvedCurrentId = currentEpisodeId
        ?.takeIf { id -> playlistRows.any { it.id == id } }
        ?: playlistRows.firstOrNull()?.id
    val episodes = playlistRows.map { row ->
        row.toAnimePlaylistEpisodeUiModel(
            current = row.id == resolvedCurrentId,
            autoPlayEnabled = autoPlayEnabled,
        )
    }
    return ChimahonAnimeAutoNextPlaylistUiState(
        title = title,
        subtitle = subtitle ?: playlistRows.firstOrNull()?.animeTitle,
        episodes = episodes,
        currentEpisodeId = resolvedCurrentId,
        autoPlayEnabled = autoPlayEnabled,
        countdownSeconds = countdownSeconds ?: if (autoPlayEnabled && episodes.size > 1) 8 else null,
    )
}

fun ChimahonAnimeQueueRowUiModel.toAnimePlaylistEpisodeUiModel(
    current: Boolean = false,
    autoPlayEnabled: Boolean = true,
): ChimahonAnimePlaylistEpisodeUiModel {
    return ChimahonAnimePlaylistEpisodeUiModel(
        id = id,
        animeTitle = animeTitle,
        episodeTitle = episodeTitle,
        episodeNumberLabel = episodeNumberLabel,
        sourceName = sourceName,
        thumbnailUrl = thumbnailUrl,
        availability = availability,
        status = when {
            current && autoPlayEnabled -> ChimahonAnimeQueueStatus.Playing
            current -> ChimahonAnimeQueueStatus.Ready
            else -> status
        },
        priority = priority,
        progress = progress,
        durationLabel = durationLabel,
        selected = selected,
        current = current,
        seen = seen,
        reorder = reorder,
        error = error,
    )
}

private fun MutableList<ChimahonAnimeQueueOverflowAction>.addMoveAction(
    enabled: Boolean,
    id: String,
    label: String,
    icon: ChimahonAnimeQueueActionIcon,
    onClick: () -> Unit,
) {
    add(
        animeQueueAction(
            id = id,
            label = label,
            icon = icon,
            enabled = enabled,
            onClick = onClick,
        ),
    )
}

private fun animeQueueAction(
    id: String,
    label: String,
    icon: ChimahonAnimeQueueActionIcon,
    enabled: Boolean = true,
    destructive: Boolean = false,
    onClick: () -> Unit,
): ChimahonAnimeQueueOverflowAction {
    return ChimahonAnimeQueueOverflowAction(
        id = id,
        label = label,
        icon = icon,
        enabled = enabled,
        destructive = destructive,
        onClick = onClick,
    )
}
