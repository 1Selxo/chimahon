package app.chimahon.shared.animequeueui

import app.chimahon.shared.anime.ChimahonAnimeDownloadAction
import app.chimahon.shared.anime.ChimahonAnimeDownloadError
import app.chimahon.shared.anime.ChimahonAnimeDownloadMovePlacement
import app.chimahon.shared.anime.ChimahonAnimeDownloadPriority
import app.chimahon.shared.anime.ChimahonAnimeDownloadProgress
import app.chimahon.shared.anime.ChimahonAnimeDownloadQueueItem
import app.chimahon.shared.anime.ChimahonAnimeDownloadQueueSnapshot
import app.chimahon.shared.anime.ChimahonAnimeDownloadStartRequest
import app.chimahon.shared.anime.ChimahonAnimeDownloadStatus
import app.chimahon.shared.anime.ChimahonAnimeEpisodeDownloadDecision
import app.chimahon.shared.anime.ChimahonAnimeEpisodeDownloadSelection
import app.chimahon.shared.anime.ChimahonAnimeEpisodeEntry
import app.chimahon.shared.anime.ChimahonAnimeEntry
import app.chimahon.shared.anime.ChimahonAnimeSourceInfo
import app.chimahon.shared.anime.decideEpisodesForDownload
import app.chimahon.shared.anime.toStartDownloadRequest
import app.chimahon.shared.anime.toQueueItem
import kotlin.math.roundToInt

fun ChimahonAnimeDownloadQueueSnapshot.toAnimeDownloadQueueUiState(
    selectedEpisodeIds: Set<String> = emptySet(),
    title: String = "Anime downloads",
    subtitle: String? = null,
    loading: Boolean = false,
    includeOverflowActions: Boolean = false,
    callbacks: ChimahonAnimeQueueRowCallbacks = ChimahonAnimeQueueRowCallbacks(),
): ChimahonAnimeQueueUiState {
    val queueRows = items
        .sortedWith(
            compareBy<ChimahonAnimeDownloadQueueItem> { it.queuePosition }
                .thenBy { it.addedAtMillis }
                .thenBy { it.animeTitle.lowercase() }
                .thenBy { it.episodeTitle.lowercase() },
        )
        .mapIndexed { index, item ->
            item.toAnimeQueueRowUiModel(
                index = index,
                totalCount = items.size,
                selectedEpisodeIds = selectedEpisodeIds,
            )
        }
        .let { rows ->
            if (includeOverflowActions) {
                rows.map { row ->
                    row.withDefaultAnimeQueueOverflowActions(
                        callbacks = callbacks,
                        includeOpen = true,
                        includePlayback = true,
                        includePriority = true,
                        includeReorder = true,
                        includeRemove = true,
                    )
                }
            } else {
                rows
            }
        }
    val selectedIds = selectedEpisodeIds.intersect(queueRows.flatMapTo(mutableSetOf()) { row ->
        listOf(row.id, row.episodeId)
    })
    return ChimahonAnimeQueueUiState(
        title = title,
        subtitle = subtitle ?: queueSubtitle(),
        rows = queueRows,
        selectedEpisodeIds = selectedIds,
        playlist = queueRows.toAnimeAutoNextPlaylistUiState(
            autoPlayEnabled = !paused,
            title = "Queued downloads",
            subtitle = queueRows.firstOrNull()?.animeTitle,
            includeCompleted = false,
        ),
        paused = paused,
        activeCount = queueRows.count { it.status.isActive },
        queuedCount = queueRows.count { it.status == ChimahonAnimeQueueStatus.Queued },
        completedCount = queueRows.count { it.status == ChimahonAnimeQueueStatus.Complete },
        failedCount = queueRows.count { it.status == ChimahonAnimeQueueStatus.Failed },
        localCount = queueRows.count {
            ChimahonAnimeEpisodeAvailability.Local in it.availability ||
                ChimahonAnimeEpisodeAvailability.Downloaded in it.availability
        },
        streamCount = queueRows.count { ChimahonAnimeEpisodeAvailability.Stream in it.availability },
        loading = loading,
        errorMessage = errors.firstOrNull()?.message,
    )
}

fun ChimahonAnimeDownloadQueueItem.toAnimeQueueRowUiModel(
    index: Int = queuePosition,
    totalCount: Int = queuePosition + 1,
    selectedEpisodeIds: Set<String> = emptySet(),
): ChimahonAnimeQueueRowUiModel {
    val rowId = animeDownloadQueueRowId()
    val selected = rowId in selectedEpisodeIds || episodeId.toString() in selectedEpisodeIds
    val status = status.toAnimeQueueStatus()
    return ChimahonAnimeQueueRowUiModel(
        id = rowId,
        animeTitle = animeTitle.ifBlank { "Untitled anime" },
        episodeTitle = episodeTitle.ifBlank { episodeFallbackTitle() },
        episodeId = episodeId.toString(),
        animeId = animeId.toString(),
        episodeNumberLabel = episodeNumberLabel(),
        subtitle = summary?.takeIf { it.isNotBlank() },
        sourceName = sourceName?.takeIf { it.isNotBlank() },
        thumbnailUrl = thumbnailUrl,
        intent = ChimahonAnimeQueueIntent.Download,
        availability = toAnimeEpisodeAvailability(),
        status = status,
        priority = priority.toAnimeQueuePriority(),
        progress = progress.toAnimeQueueProgress(status = status),
        durationLabel = durationSeconds.takeIf { it > 0L }?.toAnimeDownloadClock(),
        sizeLabel = progress.sizeLabel(),
        speedLabel = progress.speedLabel(),
        etaLabel = progress.etaLabel(),
        selected = selected,
        seen = seen,
        bookmarked = bookmarked,
        filler = filler,
        reorder = chimahonAnimeQueueReorderState(index = index, totalCount = totalCount),
        error = error?.toAnimeQueueError(),
    )
}

fun ChimahonAnimeDownloadStatus.toAnimeQueueStatus(): ChimahonAnimeQueueStatus {
    return when (this) {
        ChimahonAnimeDownloadStatus.NotDownloaded -> ChimahonAnimeQueueStatus.Ready
        ChimahonAnimeDownloadStatus.Queued -> ChimahonAnimeQueueStatus.Queued
        ChimahonAnimeDownloadStatus.Preparing -> ChimahonAnimeQueueStatus.Preparing
        ChimahonAnimeDownloadStatus.Downloading -> ChimahonAnimeQueueStatus.Downloading
        ChimahonAnimeDownloadStatus.Paused -> ChimahonAnimeQueueStatus.Paused
        ChimahonAnimeDownloadStatus.Downloaded -> ChimahonAnimeQueueStatus.Complete
        ChimahonAnimeDownloadStatus.Failed -> ChimahonAnimeQueueStatus.Failed
        ChimahonAnimeDownloadStatus.Canceled -> ChimahonAnimeQueueStatus.Canceled
        ChimahonAnimeDownloadStatus.Deleting -> ChimahonAnimeQueueStatus.Preparing
    }
}

fun ChimahonAnimeDownloadPriority.toAnimeQueuePriority(): ChimahonAnimeQueuePriority {
    return when (this) {
        ChimahonAnimeDownloadPriority.Low -> ChimahonAnimeQueuePriority.Low
        ChimahonAnimeDownloadPriority.Normal -> ChimahonAnimeQueuePriority.Normal
        ChimahonAnimeDownloadPriority.High -> ChimahonAnimeQueuePriority.High
        ChimahonAnimeDownloadPriority.Next -> ChimahonAnimeQueuePriority.Next
    }
}

fun ChimahonAnimeQueuePriority.toAnimeDownloadPriority(): ChimahonAnimeDownloadPriority {
    return when (this) {
        ChimahonAnimeQueuePriority.Low -> ChimahonAnimeDownloadPriority.Low
        ChimahonAnimeQueuePriority.Normal -> ChimahonAnimeDownloadPriority.Normal
        ChimahonAnimeQueuePriority.High -> ChimahonAnimeDownloadPriority.High
        ChimahonAnimeQueuePriority.Next -> ChimahonAnimeDownloadPriority.Next
    }
}

fun ChimahonAnimeDownloadProgress.toAnimeQueueProgress(
    status: ChimahonAnimeQueueStatus,
): ChimahonAnimeQueueProgress {
    val knownPercent = normalizedPercent
    val displayPercent = when {
        knownPercent != null -> "$knownPercent%"
        status == ChimahonAnimeQueueStatus.Complete -> "100%"
        else -> null
    }
    return ChimahonAnimeQueueProgress(
        fraction = if (status == ChimahonAnimeQueueStatus.Complete) 1f else fraction,
        percentLabel = displayPercent,
        statusLabel = message?.takeIf { it.isNotBlank() } ?: status.label,
        downloadedBytes = downloadedBytes,
        totalBytes = totalBytes,
        indeterminate = indeterminate,
    )
}

fun ChimahonAnimeDownloadError.toAnimeQueueError(): ChimahonAnimeQueueError {
    return ChimahonAnimeQueueError(
        title = "Download failed",
        message = message ?: code.name,
        retryLabel = "Retry",
        canRetry = canRetry,
    )
}

fun ChimahonAnimeEpisodeDownloadDecision.toAnimeDownloadQueueRows(
    anime: ChimahonAnimeEntry,
    sourceInfo: ChimahonAnimeSourceInfo? = null,
    selectedEpisodeIds: Set<String> = emptySet(),
): List<ChimahonAnimeQueueRowUiModel> {
    val request = toStartDownloadRequest(
        anime = anime,
        sourceInfo = sourceInfo,
    )
    return request.episodes.mapIndexed { index, episode ->
        episode
            .toQueueItem(
                status = ChimahonAnimeDownloadStatus.NotDownloaded,
                priority = ChimahonAnimeDownloadPriority.Normal,
                queuePosition = index,
            )
            .toAnimeQueueRowUiModel(
                index = index,
                totalCount = request.episodes.size,
                selectedEpisodeIds = selectedEpisodeIds,
            )
    }
}

fun ChimahonAnimeEntry.toAnimeDownloadDecision(
    episodes: List<ChimahonAnimeEpisodeEntry>,
    selection: ChimahonAnimeEpisodeDownloadSelection,
    downloads: ChimahonAnimeDownloadQueueSnapshot = ChimahonAnimeDownloadQueueSnapshot(),
    sourceInfo: ChimahonAnimeSourceInfo? = null,
    includeSeen: Boolean = false,
): ChimahonAnimeEpisodeDownloadDecision {
    return app.chimahon.shared.anime.ChimahonAnimeEpisodeDownloadDecisionRequest(
        anime = this,
        episodes = episodes,
        selection = selection,
        downloads = downloads,
        sourceInfo = sourceInfo,
        includeSeen = includeSeen,
    ).decideEpisodesForDownload()
}

fun ChimahonAnimeQueueRowUiModel.toAnimeDownloadAction(
    command: ChimahonAnimeQueueDownloadCommand,
): ChimahonAnimeDownloadAction? {
    val parsedEpisodeId = episodeId.toLongOrNull() ?: return null
    return when (command) {
        ChimahonAnimeQueueDownloadCommand.Start -> {
            val parsedAnimeId = animeId?.toLongOrNull() ?: return null
            ChimahonAnimeDownloadAction.Start(
                ChimahonAnimeDownloadStartRequest(
                    episodes = listOf(
                        toAnimeDownloadEpisodeRequest(
                            animeId = parsedAnimeId,
                            episodeId = parsedEpisodeId,
                        ),
                    ),
                    startNow = false,
                    priority = priority.toAnimeDownloadPriority(),
                ),
            )
        }
        ChimahonAnimeQueueDownloadCommand.StartNow -> {
            val parsedAnimeId = animeId?.toLongOrNull() ?: return null
            ChimahonAnimeDownloadAction.Start(
                ChimahonAnimeDownloadStartRequest(
                    episodes = listOf(
                        toAnimeDownloadEpisodeRequest(
                            animeId = parsedAnimeId,
                            episodeId = parsedEpisodeId,
                        ),
                    ),
                    startNow = true,
                    priority = ChimahonAnimeDownloadPriority.Next,
                ),
            )
        }
        ChimahonAnimeQueueDownloadCommand.Pause -> ChimahonAnimeDownloadAction.Pause(setOf(parsedEpisodeId))
        ChimahonAnimeQueueDownloadCommand.Resume -> ChimahonAnimeDownloadAction.Resume(setOf(parsedEpisodeId))
        ChimahonAnimeQueueDownloadCommand.Retry -> ChimahonAnimeDownloadAction.Retry(setOf(parsedEpisodeId))
        ChimahonAnimeQueueDownloadCommand.Cancel -> ChimahonAnimeDownloadAction.Cancel(setOf(parsedEpisodeId))
        ChimahonAnimeQueueDownloadCommand.Delete -> ChimahonAnimeDownloadAction.Delete(setOf(parsedEpisodeId))
        ChimahonAnimeQueueDownloadCommand.MoveTop -> ChimahonAnimeDownloadAction.Move(
            episodeId = parsedEpisodeId,
            placement = ChimahonAnimeDownloadMovePlacement.Top,
        )
        ChimahonAnimeQueueDownloadCommand.MoveUp -> ChimahonAnimeDownloadAction.Move(
            episodeId = parsedEpisodeId,
            placement = ChimahonAnimeDownloadMovePlacement.Up,
        )
        ChimahonAnimeQueueDownloadCommand.MoveDown -> ChimahonAnimeDownloadAction.Move(
            episodeId = parsedEpisodeId,
            placement = ChimahonAnimeDownloadMovePlacement.Down,
        )
        ChimahonAnimeQueueDownloadCommand.MoveBottom -> ChimahonAnimeDownloadAction.Move(
            episodeId = parsedEpisodeId,
            placement = ChimahonAnimeDownloadMovePlacement.Bottom,
        )
    }
}

fun List<ChimahonAnimeQueueRowUiModel>.toAnimeDownloadReorderAction(): ChimahonAnimeDownloadAction.Reorder {
    return ChimahonAnimeDownloadAction.Reorder(
        episodeIds = mapNotNull { row -> row.episodeId.toLongOrNull() },
    )
}

enum class ChimahonAnimeQueueDownloadCommand {
    Start,
    StartNow,
    Pause,
    Resume,
    Retry,
    Cancel,
    Delete,
    MoveTop,
    MoveUp,
    MoveDown,
    MoveBottom,
}

private fun ChimahonAnimeDownloadQueueSnapshot.queueSubtitle(): String? {
    return listOfNotNull(
        items.size.takeIf { it > 0 }?.let { "$it episode(s)" },
        activeCount.takeIf { it > 0 }?.let { "$it active" },
        queuedCount.takeIf { it > 0 }?.let { "$it queued" },
        completedCount.takeIf { it > 0 }?.let { "$it complete" },
        failedCount.takeIf { it > 0 }?.let { "$it failed" },
        paused.takeIf { it }?.let { "Paused" },
    ).joinToString(" - ").ifBlank { null }
}

private fun ChimahonAnimeDownloadQueueItem.animeDownloadQueueRowId(): String {
    return id.ifBlank { "anime-$animeId-episode-$episodeId" }
}

private fun ChimahonAnimeDownloadQueueItem.episodeFallbackTitle(): String {
    return episodeNumberLabel() ?: "Episode"
}

private fun ChimahonAnimeDownloadQueueItem.episodeNumberLabel(): String? {
    return if (isRecognizedEpisodeNumber) {
        "Episode ${episodeNumber.toAnimeEpisodeNumberLabel()}"
    } else {
        null
    }
}

private fun ChimahonAnimeDownloadQueueItem.toAnimeEpisodeAvailability(): Set<ChimahonAnimeEpisodeAvailability> {
    if (!streamable && !local && !downloadedOnDisk && status != ChimahonAnimeDownloadStatus.Downloaded) {
        return setOf(ChimahonAnimeEpisodeAvailability.Unavailable)
    }
    return buildSet {
        if (local) add(ChimahonAnimeEpisodeAvailability.Local)
        if (streamable) add(ChimahonAnimeEpisodeAvailability.Stream)
        if (downloadedOnDisk || status == ChimahonAnimeDownloadStatus.Downloaded) {
            add(ChimahonAnimeEpisodeAvailability.Downloaded)
        }
        if (isEmpty()) add(ChimahonAnimeEpisodeAvailability.Unavailable)
    }
}

private fun ChimahonAnimeQueueRowUiModel.toAnimeDownloadEpisodeRequest(
    animeId: Long,
    episodeId: Long,
): app.chimahon.shared.anime.ChimahonAnimeDownloadEpisodeRequest {
    return app.chimahon.shared.anime.ChimahonAnimeDownloadEpisodeRequest(
        animeId = animeId,
        episodeId = episodeId,
        animeTitle = animeTitle,
        episodeTitle = episodeTitle,
        thumbnailUrl = thumbnailUrl,
        sourceName = sourceName,
        seen = seen,
        bookmarked = bookmarked,
        filler = filler,
        streamable = ChimahonAnimeEpisodeAvailability.Stream in availability,
    )
}

private fun ChimahonAnimeDownloadProgress.sizeLabel(): String? {
    val downloaded = downloadedBytes
    val total = totalBytes
    return when {
        downloaded != null && total != null && total > 0L -> {
            "${downloaded.toAnimeDownloadBytes()} / ${total.toAnimeDownloadBytes()}"
        }
        downloaded != null && downloaded > 0L -> downloaded.toAnimeDownloadBytes()
        total != null && total > 0L -> total.toAnimeDownloadBytes()
        else -> null
    }
}

private fun ChimahonAnimeDownloadProgress.speedLabel(): String? {
    val bytesPerSecond = speedBytesPerSecond?.takeIf { it > 0L } ?: return null
    return "${bytesPerSecond.toAnimeDownloadBytes()}/s"
}

private fun ChimahonAnimeDownloadProgress.etaLabel(): String? {
    val seconds = estimatedRemainingSeconds?.takeIf { it > 0L } ?: return null
    return "${seconds.toAnimeDownloadDurationLabel()} left"
}

private fun Long.toAnimeDownloadBytes(): String {
    val value = coerceAtLeast(0L)
    val units = listOf("B", "KB", "MB", "GB")
    var scaled = value.toDouble()
    var unitIndex = 0
    while (scaled >= 1024.0 && unitIndex < units.lastIndex) {
        scaled /= 1024.0
        unitIndex += 1
    }
    return if (unitIndex == 0) {
        "$value ${units[unitIndex]}"
    } else {
        "${scaled.toAnimeDownloadSingleDecimal()} ${units[unitIndex]}"
    }
}

private fun Long.toAnimeDownloadDurationLabel(): String {
    val totalSeconds = coerceAtLeast(0L)
    val hours = totalSeconds / 3_600L
    val minutes = (totalSeconds % 3_600L) / 60L
    val seconds = totalSeconds % 60L
    return when {
        hours > 0L && minutes > 0L -> "${hours}h ${minutes}m"
        hours > 0L -> "${hours}h"
        minutes > 0L && seconds > 0L -> "${minutes}m ${seconds}s"
        minutes > 0L -> "${minutes}m"
        else -> "${seconds}s"
    }
}

private fun Long.toAnimeDownloadClock(): String {
    val totalSeconds = coerceAtLeast(0L)
    val hours = totalSeconds / 3_600L
    val minutes = (totalSeconds % 3_600L) / 60L
    val seconds = totalSeconds % 60L
    return if (hours > 0L) {
        "$hours:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    } else {
        "$minutes:${seconds.toString().padStart(2, '0')}"
    }
}

private fun Double.toAnimeEpisodeNumberLabel(): String {
    val intValue = toInt()
    return if (this == intValue.toDouble()) {
        intValue.toString()
    } else {
        toString().trimEnd('0').trimEnd('.')
    }
}

private fun Double.toAnimeDownloadSingleDecimal(): String {
    val rounded = (this * 10.0).roundToInt()
    val whole = rounded / 10
    val fraction = rounded % 10
    return if (fraction == 0) whole.toString() else "$whole.$fraction"
}
