package app.chimahon.shared.downloadhistoryui

data class ChimahonDownloadHistoryRowCallbacks(
    val onOpen: (ChimahonDownloadHistoryRow) -> Unit = {},
    val onOpenSource: (ChimahonDownloadHistoryRow) -> Unit = {},
    val onMarkRead: (ChimahonDownloadHistoryRow) -> Unit = {},
    val onMarkUnread: (ChimahonDownloadHistoryRow) -> Unit = {},
    val onDownload: (ChimahonDownloadHistoryRow) -> Unit = {},
    val onPause: (ChimahonDownloadQueueRowUiModel) -> Unit = {},
    val onResume: (ChimahonDownloadQueueRowUiModel) -> Unit = {},
    val onRetry: (ChimahonDownloadHistoryRow) -> Unit = {},
    val onRemove: (ChimahonDownloadHistoryRow) -> Unit = {},
    val onDelete: (ChimahonDownloadHistoryRow) -> Unit = {},
    val onMoveTop: (ChimahonDownloadQueueRowUiModel) -> Unit = {},
    val onMoveUp: (ChimahonDownloadQueueRowUiModel) -> Unit = {},
    val onMoveDown: (ChimahonDownloadQueueRowUiModel) -> Unit = {},
    val onMoveBottom: (ChimahonDownloadQueueRowUiModel) -> Unit = {},
    val onCopyTitle: (ChimahonDownloadHistoryRow) -> Unit = {},
)

data class ChimahonDownloadHistoryBatchCallbacks(
    val onSelectAll: () -> Unit = {},
    val onClearSelection: () -> Unit = {},
    val onMarkRead: () -> Unit = {},
    val onMarkUnread: () -> Unit = {},
    val onDownload: () -> Unit = {},
    val onPause: () -> Unit = {},
    val onResume: () -> Unit = {},
    val onRetry: () -> Unit = {},
    val onRemove: () -> Unit = {},
    val onDelete: () -> Unit = {},
)

fun ChimahonUpdateRowUiModel.withDefaultUpdateActions(
    callbacks: ChimahonDownloadHistoryRowCallbacks,
): ChimahonUpdateRowUiModel {
    return copy(actions = defaultUpdateActions(callbacks))
}

fun ChimahonHistoryRowUiModel.withDefaultHistoryActions(
    callbacks: ChimahonDownloadHistoryRowCallbacks,
): ChimahonHistoryRowUiModel {
    return copy(actions = defaultHistoryActions(callbacks))
}

fun ChimahonDownloadQueueRowUiModel.withDefaultDownloadActions(
    callbacks: ChimahonDownloadHistoryRowCallbacks,
): ChimahonDownloadQueueRowUiModel {
    return copy(actions = defaultDownloadActions(callbacks))
}

fun ChimahonUpdateRowUiModel.defaultUpdateActions(
    callbacks: ChimahonDownloadHistoryRowCallbacks,
): List<ChimahonDownloadHistoryAction> {
    return buildList {
        add(openAction { callbacks.onOpen(this@defaultUpdateActions) })
        add(downloadAction { callbacks.onDownload(this@defaultUpdateActions) })
        add(
            ChimahonDownloadHistoryAction(
                id = if (unreadCount > 0) {
                    ChimahonDownloadHistoryActionId.MarkRead
                } else {
                    ChimahonDownloadHistoryActionId.MarkUnread
                },
                label = if (unreadCount > 0) "Mark ${mediaKind.itemTitle.lowercase()} read" else "Mark unread",
                icon = if (unreadCount > 0) {
                    ChimahonDownloadHistoryActionIcon.MarkRead
                } else {
                    ChimahonDownloadHistoryActionIcon.MarkUnread
                },
                onClick = {
                    if (unreadCount > 0) {
                        callbacks.onMarkRead(this@defaultUpdateActions)
                    } else {
                        callbacks.onMarkUnread(this@defaultUpdateActions)
                    }
                },
            ),
        )
        add(sourceAction { callbacks.onOpenSource(this@defaultUpdateActions) })
    }
}

fun ChimahonHistoryRowUiModel.defaultHistoryActions(
    callbacks: ChimahonDownloadHistoryRowCallbacks,
): List<ChimahonDownloadHistoryAction> {
    return buildList {
        add(openAction(label = continueLabel ?: "Resume") { callbacks.onOpen(this@defaultHistoryActions) })
        if (!completed) {
            add(
                ChimahonDownloadHistoryAction(
                    id = ChimahonDownloadHistoryActionId.MarkRead,
                    label = "Mark complete",
                    icon = ChimahonDownloadHistoryActionIcon.MarkRead,
                    role = ChimahonDownloadHistoryActionRole.Primary,
                    onClick = { callbacks.onMarkRead(this@defaultHistoryActions) },
                ),
            )
        }
        add(
            ChimahonDownloadHistoryAction(
                id = ChimahonDownloadHistoryActionId.Remove,
                label = "Remove from history",
                icon = ChimahonDownloadHistoryActionIcon.Remove,
                role = ChimahonDownloadHistoryActionRole.Destructive,
                onClick = { callbacks.onRemove(this@defaultHistoryActions) },
            ),
        )
        add(sourceAction { callbacks.onOpenSource(this@defaultHistoryActions) })
    }
}

fun ChimahonDownloadQueueRowUiModel.defaultDownloadActions(
    callbacks: ChimahonDownloadHistoryRowCallbacks,
): List<ChimahonDownloadHistoryAction> {
    return buildList {
        add(openAction { callbacks.onOpen(this@defaultDownloadActions) })
        when (status) {
            ChimahonDownloadHistoryStatus.Downloading,
            ChimahonDownloadHistoryStatus.Queued -> add(
                ChimahonDownloadHistoryAction(
                    id = ChimahonDownloadHistoryActionId.Pause,
                    label = "Pause",
                    icon = ChimahonDownloadHistoryActionIcon.Pause,
                    onClick = { callbacks.onPause(this@defaultDownloadActions) },
                ),
            )
            ChimahonDownloadHistoryStatus.Paused -> add(
                ChimahonDownloadHistoryAction(
                    id = ChimahonDownloadHistoryActionId.Resume,
                    label = "Resume",
                    icon = ChimahonDownloadHistoryActionIcon.Resume,
                    role = ChimahonDownloadHistoryActionRole.Primary,
                    onClick = { callbacks.onResume(this@defaultDownloadActions) },
                ),
            )
            ChimahonDownloadHistoryStatus.Failed -> add(
                ChimahonDownloadHistoryAction(
                    id = ChimahonDownloadHistoryActionId.Retry,
                    label = "Retry",
                    icon = ChimahonDownloadHistoryActionIcon.Retry,
                    role = ChimahonDownloadHistoryActionRole.Primary,
                    onClick = { callbacks.onRetry(this@defaultDownloadActions) },
                ),
            )
            else -> Unit
        }
        if (canMove) {
            add(moveAction(ChimahonDownloadHistoryActionId.MoveUp, "Move up", ChimahonDownloadHistoryActionIcon.MoveUp) {
                callbacks.onMoveUp(this@defaultDownloadActions)
            })
            add(moveAction(ChimahonDownloadHistoryActionId.MoveDown, "Move down", ChimahonDownloadHistoryActionIcon.MoveDown) {
                callbacks.onMoveDown(this@defaultDownloadActions)
            })
        }
        add(
            ChimahonDownloadHistoryAction(
                id = ChimahonDownloadHistoryActionId.Remove,
                label = "Remove",
                icon = ChimahonDownloadHistoryActionIcon.Remove,
                role = ChimahonDownloadHistoryActionRole.Destructive,
                onClick = { callbacks.onRemove(this@defaultDownloadActions) },
            ),
        )
    }
}

fun defaultDownloadQueueMoveActions(
    row: ChimahonDownloadQueueRowUiModel,
    callbacks: ChimahonDownloadHistoryRowCallbacks,
): List<ChimahonDownloadHistoryAction> {
    if (!row.canMove) return emptyList()
    return listOf(
        moveAction(ChimahonDownloadHistoryActionId.MoveTop, "Move to top", ChimahonDownloadHistoryActionIcon.MoveTop) {
            callbacks.onMoveTop(row)
        },
        moveAction(ChimahonDownloadHistoryActionId.MoveUp, "Move up", ChimahonDownloadHistoryActionIcon.MoveUp) {
            callbacks.onMoveUp(row)
        },
        moveAction(ChimahonDownloadHistoryActionId.MoveDown, "Move down", ChimahonDownloadHistoryActionIcon.MoveDown) {
            callbacks.onMoveDown(row)
        },
        moveAction(ChimahonDownloadHistoryActionId.MoveBottom, "Move to bottom", ChimahonDownloadHistoryActionIcon.MoveBottom) {
            callbacks.onMoveBottom(row)
        },
    )
}

fun defaultUpdatesBatchActions(
    callbacks: ChimahonDownloadHistoryBatchCallbacks,
): List<ChimahonDownloadHistoryBatchAction> {
    return listOf(
        ChimahonDownloadHistoryBatchAction(
            id = "mark-read",
            label = "Read",
            icon = ChimahonDownloadHistoryActionIcon.MarkRead,
            role = ChimahonDownloadHistoryActionRole.Primary,
            onClick = callbacks.onMarkRead,
        ),
        ChimahonDownloadHistoryBatchAction(
            id = "download",
            label = "Download",
            icon = ChimahonDownloadHistoryActionIcon.Download,
            onClick = callbacks.onDownload,
        ),
        ChimahonDownloadHistoryBatchAction(
            id = "delete",
            label = "Delete",
            icon = ChimahonDownloadHistoryActionIcon.Delete,
            role = ChimahonDownloadHistoryActionRole.Destructive,
            onClick = callbacks.onDelete,
        ),
    )
}

fun defaultHistoryBatchActions(
    callbacks: ChimahonDownloadHistoryBatchCallbacks,
): List<ChimahonDownloadHistoryBatchAction> {
    return listOf(
        ChimahonDownloadHistoryBatchAction(
            id = "mark-read",
            label = "Complete",
            icon = ChimahonDownloadHistoryActionIcon.MarkRead,
            role = ChimahonDownloadHistoryActionRole.Primary,
            onClick = callbacks.onMarkRead,
        ),
        ChimahonDownloadHistoryBatchAction(
            id = "remove",
            label = "Remove",
            icon = ChimahonDownloadHistoryActionIcon.Remove,
            role = ChimahonDownloadHistoryActionRole.Destructive,
            onClick = callbacks.onRemove,
        ),
    )
}

fun defaultDownloadsBatchActions(
    callbacks: ChimahonDownloadHistoryBatchCallbacks,
): List<ChimahonDownloadHistoryBatchAction> {
    return listOf(
        ChimahonDownloadHistoryBatchAction(
            id = "resume",
            label = "Resume",
            icon = ChimahonDownloadHistoryActionIcon.Resume,
            role = ChimahonDownloadHistoryActionRole.Primary,
            onClick = callbacks.onResume,
        ),
        ChimahonDownloadHistoryBatchAction(
            id = "pause",
            label = "Pause",
            icon = ChimahonDownloadHistoryActionIcon.Pause,
            onClick = callbacks.onPause,
        ),
        ChimahonDownloadHistoryBatchAction(
            id = "retry",
            label = "Retry",
            icon = ChimahonDownloadHistoryActionIcon.Retry,
            onClick = callbacks.onRetry,
        ),
        ChimahonDownloadHistoryBatchAction(
            id = "remove",
            label = "Remove",
            icon = ChimahonDownloadHistoryActionIcon.Remove,
            role = ChimahonDownloadHistoryActionRole.Destructive,
            onClick = callbacks.onRemove,
        ),
    )
}

private fun openAction(
    label: String = "Open",
    onClick: () -> Unit,
): ChimahonDownloadHistoryAction {
    return ChimahonDownloadHistoryAction(
        id = ChimahonDownloadHistoryActionId.Open,
        label = label,
        icon = ChimahonDownloadHistoryActionIcon.Open,
        role = ChimahonDownloadHistoryActionRole.Primary,
        onClick = onClick,
    )
}

private fun sourceAction(onClick: () -> Unit): ChimahonDownloadHistoryAction {
    return ChimahonDownloadHistoryAction(
        id = ChimahonDownloadHistoryActionId.OpenSource,
        label = "Source",
        icon = ChimahonDownloadHistoryActionIcon.Source,
        onClick = onClick,
    )
}

private fun downloadAction(onClick: () -> Unit): ChimahonDownloadHistoryAction {
    return ChimahonDownloadHistoryAction(
        id = ChimahonDownloadHistoryActionId.Download,
        label = "Download",
        icon = ChimahonDownloadHistoryActionIcon.Download,
        onClick = onClick,
    )
}

private fun moveAction(
    id: ChimahonDownloadHistoryActionId,
    label: String,
    icon: ChimahonDownloadHistoryActionIcon,
    onClick: () -> Unit,
): ChimahonDownloadHistoryAction {
    return ChimahonDownloadHistoryAction(
        id = id,
        label = label,
        icon = icon,
        onClick = onClick,
    )
}
