package app.chimahon.shared.animeui

import app.chimahon.shared.ChimahonAnimeLibrarySettings as SavedAnimeLibrarySettings
import app.chimahon.shared.anime.ChimahonAnimeBackendError
import app.chimahon.shared.anime.ChimahonAnimeDetailLoadResult
import app.chimahon.shared.anime.ChimahonAnimeDownloadSnapshot
import app.chimahon.shared.anime.ChimahonAnimeEpisodeDownloadState
import app.chimahon.shared.anime.ChimahonAnimeHistoryData
import app.chimahon.shared.anime.ChimahonAnimeHistoryEntry
import app.chimahon.shared.anime.ChimahonAnimeHistoryLoadResult
import app.chimahon.shared.anime.ChimahonAnimeHoster
import app.chimahon.shared.anime.ChimahonAnimeHosterStatus
import app.chimahon.shared.anime.ChimahonAnimeLibraryLoadResult
import app.chimahon.shared.anime.ChimahonAnimeMediaTrack
import app.chimahon.shared.anime.ChimahonAnimePlaybackSource
import app.chimahon.shared.anime.ChimahonAnimePlayerLoadResult
import app.chimahon.shared.anime.ChimahonAnimePlayerSession
import app.chimahon.shared.anime.ChimahonAnimeSkipKind
import app.chimahon.shared.anime.ChimahonAnimeSkipSegment
import app.chimahon.shared.anime.ChimahonAnimeSkipSource
import app.chimahon.shared.anime.ChimahonAnimeVideoStatus
import app.chimahon.shared.anime.ChimahonAnimeVideoStream
import kotlin.math.roundToInt

fun ChimahonAnimeLibraryLoadResult.toAnimeLibraryUiState(
    settings: SavedAnimeLibrarySettings = SavedAnimeLibrarySettings(),
    query: String = "",
    selectedCategoryId: String? = null,
    selectedAnimeIds: Set<String> = emptySet(),
    trackedAnimeIds: Set<Long> = emptySet(),
    loading: Boolean = false,
    refreshing: Boolean = false,
    title: String = "Anime",
): ChimahonAnimeLibraryUiState {
    return data.toAnimeLibraryUiState(
        settings = settings,
        query = query,
        selectedCategoryId = selectedCategoryId,
        selectedAnimeIds = selectedAnimeIds,
        trackedAnimeIds = trackedAnimeIds,
        loading = loading,
        refreshing = refreshing,
        errorMessage = errors.toAnimeBackendErrorMessage(),
        title = title,
    )
}

fun ChimahonAnimeDetailLoadResult.toAnimeDetailUiState(
    selectedEpisodeIds: Set<String> = emptySet(),
    selectedSeasonId: String? = null,
    episodeQuery: String = "",
    refreshing: Boolean = false,
): ChimahonAnimeDetailUiState {
    val detailData = detail
    val message = errors.toAnimeBackendErrorMessage()
    if (detailData == null) {
        return ChimahonAnimeDetailUiState(
            contentState = if (message == null) {
                ChimahonAnimeContentState.Empty
            } else {
                ChimahonAnimeContentState.Error
            },
            errorMessage = message,
            refreshing = refreshing,
        )
    }

    return detailData.entry.toAnimeDetailUiState(
        libraryData = detailData.libraryData,
        selectedEpisodeIds = selectedEpisodeIds,
        selectedSeasonId = selectedSeasonId,
        episodeQuery = episodeQuery,
        downloadedEpisodeIds = detailData.downloads.downloadedEpisodeIds,
        queuedEpisodeIds = detailData.downloads.queuedEpisodeIds,
        downloadingEpisodeProgress = detailData.downloads.downloadingEpisodeProgress,
        erroredEpisodeIds = detailData.downloads.erroredEpisodeIds,
        trackedAnimeIds = detailData.trackedAnimeIds,
        refreshing = refreshing,
        errorMessage = message,
    )
}

fun ChimahonAnimeHistoryLoadResult.toAnimeHistoryUiState(
    selectedRowIds: Set<String> = emptySet(),
    loading: Boolean = false,
    formatWatchedAt: (Long) -> String? = { null },
): ChimahonAnimeHistoryUiState {
    return data.toAnimeHistoryUiState(
        selectedRowIds = selectedRowIds,
        loading = loading,
        errorMessage = errors.toAnimeBackendErrorMessage(),
        formatWatchedAt = formatWatchedAt,
    )
}

fun ChimahonAnimeHistoryData.toAnimeHistoryUiState(
    selectedRowIds: Set<String> = emptySet(),
    loading: Boolean = false,
    errorMessage: String? = null,
    formatWatchedAt: (Long) -> String? = { null },
): ChimahonAnimeHistoryUiState {
    return ChimahonAnimeHistoryUiState(
        subtitle = when {
            query.isNotBlank() -> "${entries.size} result(s)"
            totalWatchDurationMillis > 0L -> totalWatchDurationMillis.toAnimeDurationMillisLabel()
            else -> null
        },
        rows = entries.map { entry ->
            entry.toAnimeHistoryRowUiModel(
                selected = entry.id.toString() in selectedRowIds,
                formatWatchedAt = formatWatchedAt,
            )
        },
        selectedRowIds = selectedRowIds,
        loading = loading,
        errorMessage = errorMessage,
    )
}

fun ChimahonAnimeHistoryEntry.toAnimeHistoryRowUiModel(
    selected: Boolean = false,
    formatWatchedAt: (Long) -> String? = { null },
): ChimahonAnimeHistoryRowUiModel {
    return ChimahonAnimeHistoryRowUiModel(
        id = id.toString(),
        animeTitle = animeTitle.ifBlank { "Untitled anime" },
        episodeTitle = episodeTitle
            ?.takeIf { it.isNotBlank() }
            ?: episodeNumber.takeIf { isRecognizedEpisodeNumber }?.let { "Episode ${it.trimmedAnimeBackendNumber()}" }
            ?: "Episode",
        watchedAtLabel = watchedAtMillis?.let(formatWatchedAt),
        progressLabel = toAnimeHistoryProgressLabel(),
        sourceName = sourceName?.takeIf { it.isNotBlank() },
        selected = selected,
    )
}

fun ChimahonAnimePlayerLoadResult.toAnimePlayerUiState(): ChimahonAnimePlayerUiState {
    val playerSession = session
    val message = (errors + listOfNotNull(playerSession?.error)).toAnimeBackendErrorMessage()
    if (playerSession == null) {
        return ChimahonAnimePlayerUiState(
            loading = false,
            errorMessage = message,
        )
    }

    return playerSession.toAnimePlayerUiState(errorMessage = message)
}

fun ChimahonAnimePlayerSession.toAnimePlayerUiState(
    errorMessage: String? = error?.message,
): ChimahonAnimePlayerUiState {
    val selectedVideo = selectedVideo()
    val selectedHoster = selectedHoster()
    val effectiveSubtitleTracks = subtitleTracks.ifEmpty { selectedVideo?.subtitleTracks.orEmpty() }
    val effectiveAudioTracks = audioTracks.ifEmpty { selectedVideo?.audioTracks.orEmpty() }
    val effectiveSkipSegments = skipSegments.ifEmpty { selectedVideo?.skipSegments.orEmpty() }
    val effectiveDuration = durationSeconds.takeIf { it > 0L } ?: episode.totalSeconds
    val effectivePosition = positionSeconds.takeIf { it > 0L }
        ?: historyEntry?.lastSecondSeen
        ?: episode.lastSecondSeen
    val progressFraction = if (effectiveDuration > 0L) {
        (effectivePosition.toFloat() / effectiveDuration.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val bufferedFraction = if (effectiveDuration > 0L) {
        (bufferedSeconds.toFloat() / effectiveDuration.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    return ChimahonAnimePlayerUiState(
        animeTitle = anime.title.ifBlank { "Untitled anime" },
        episodeTitle = episode.name.ifBlank {
            episode.episodeNumber.takeIf { episode.isRecognizedNumber }
                ?.let { "Episode ${it.trimmedAnimeBackendNumber()}" }
                ?: "Episode"
        },
        sourceName = sourceInfo?.displayName,
        sourceUrl = episode.url.takeIf { it.isNotBlank() },
        streamUrl = selectedVideo?.streamUrl,
        selectedHoster = selectedHoster?.title,
        selectedQuality = selectedVideo?.title,
        selectedAudioTrack = effectiveAudioTracks.firstOrNull { it.key == selectedAudioTrackKey }?.title,
        playbackSpeed = playbackSpeed,
        paused = paused,
        loading = loading,
        loadingEpisode = selectedVideo?.status == ChimahonAnimeVideoStatus.Loading,
        hasPreviousEpisode = previousEpisode != null,
        hasNextEpisode = nextEpisode != null,
        positionSeconds = effectivePosition,
        durationSeconds = effectiveDuration,
        bufferedSeconds = bufferedSeconds,
        progressFraction = progressFraction,
        bufferedFraction = bufferedFraction,
        positionLabel = effectivePosition.toAnimePlaybackClock(),
        durationLabel = effectiveDuration.toAnimePlaybackClock(),
        hosters = hosters.map { it.toAnimePlayerHosterUiModel() },
        qualities = videos.map { it.toAnimePlayerVideoQualityUiModel() },
        subtitleTracks = effectiveSubtitleTracks.map { it.toAnimePlayerTrackUiModel() },
        selectedSubtitleTrackIds = selectedSubtitleTrackKeys,
        audioTracks = effectiveAudioTracks.map { it.toAnimePlayerTrackUiModel() },
        selectedAudioTrackId = selectedAudioTrackKey,
        selectedHosterId = selectedHoster?.key,
        selectedQualityId = selectedVideo?.key,
        skipSegments = effectiveSkipSegments.map { it.toAnimePlayerSkipSegmentUiModel() },
        stream = ChimahonAnimePlayerStreamUiState(
            playbackSource = playbackSource.toAnimePlayerPlaybackSource(),
            status = toAnimePlayerStreamStatus(selectedVideo = selectedVideo, errorMessage = errorMessage),
            sourceUrl = selectedVideo?.sourceUrl ?: episode.url.takeIf { it.isNotBlank() },
            streamUrl = selectedVideo?.streamUrl,
            message = errorMessage,
            retryable = errorMessage != null,
        ),
        errorMessage = errorMessage,
    )
}

fun ChimahonAnimeDownloadSnapshot.stateForUi(
    episodeId: Long,
    animeId: Long? = null,
): ChimahonAnimeDownloadUiState {
    return stateFor(episodeId, animeId).toAnimeDownloadUiState()
}

fun ChimahonAnimeEpisodeDownloadState.toAnimeDownloadUiState(): ChimahonAnimeDownloadUiState {
    return when (this) {
        ChimahonAnimeEpisodeDownloadState.NotDownloaded -> ChimahonAnimeDownloadUiState.NotDownloaded
        ChimahonAnimeEpisodeDownloadState.Queued -> ChimahonAnimeDownloadUiState.Queued
        ChimahonAnimeEpisodeDownloadState.Downloading -> ChimahonAnimeDownloadUiState.Downloading
        ChimahonAnimeEpisodeDownloadState.Downloaded -> ChimahonAnimeDownloadUiState.Downloaded
        ChimahonAnimeEpisodeDownloadState.Error -> ChimahonAnimeDownloadUiState.Error
    }
}

private fun ChimahonAnimePlayerSession.selectedHoster(): ChimahonAnimeHoster? {
    return hosters.firstOrNull { it.key == selectedHosterKey }
        ?: hosters.firstOrNull { it.preferred }
        ?: hosters.firstOrNull()
}

private fun ChimahonAnimePlayerSession.selectedVideo(): ChimahonAnimeVideoStream? {
    return videos.firstOrNull { it.key == selectedVideoKey }
        ?: videos.firstOrNull { it.preferred }
        ?: videos.firstOrNull()
}

private fun ChimahonAnimeHoster.toAnimePlayerHosterUiModel(): ChimahonAnimePlayerHosterUiModel {
    return ChimahonAnimePlayerHosterUiModel(
        id = key,
        title = title.ifBlank { key },
        url = url,
        status = status.toAnimePlayerHosterStatus(),
        videoCount = videoCount,
        preferred = preferred,
        errorMessage = errorMessage,
        internalKey = internalData,
    )
}

private fun ChimahonAnimeHosterStatus.toAnimePlayerHosterStatus(): ChimahonAnimePlayerHosterStatus {
    return when (this) {
        ChimahonAnimeHosterStatus.Idle -> ChimahonAnimePlayerHosterStatus.Idle
        ChimahonAnimeHosterStatus.Loading -> ChimahonAnimePlayerHosterStatus.Loading
        ChimahonAnimeHosterStatus.Ready -> ChimahonAnimePlayerHosterStatus.Ready
        ChimahonAnimeHosterStatus.Empty -> ChimahonAnimePlayerHosterStatus.Empty
        ChimahonAnimeHosterStatus.Error -> ChimahonAnimePlayerHosterStatus.Error
    }
}

private fun ChimahonAnimeVideoStream.toAnimePlayerVideoQualityUiModel(): ChimahonAnimePlayerVideoQualityUiModel {
    return ChimahonAnimePlayerVideoQualityUiModel(
        id = key,
        title = title.ifBlank { resolution?.let { "${it}p" } ?: key },
        detail = bitrate?.let { "$it kbps" },
        hosterId = hosterKey,
        hosterTitle = hosterTitle,
        resolution = resolution,
        bitrate = bitrate,
        preferred = preferred,
        initialized = initialized,
        status = status.toAnimePlayerVideoStatus(),
        sourceUrl = sourceUrl,
        streamUrl = streamUrl,
        errorMessage = errorMessage,
        internalKey = internalData,
    )
}

private fun ChimahonAnimeVideoStatus.toAnimePlayerVideoStatus(): ChimahonAnimePlayerVideoStatus {
    return when (this) {
        ChimahonAnimeVideoStatus.Queued -> ChimahonAnimePlayerVideoStatus.Queued
        ChimahonAnimeVideoStatus.Loading -> ChimahonAnimePlayerVideoStatus.Loading
        ChimahonAnimeVideoStatus.Ready -> ChimahonAnimePlayerVideoStatus.Ready
        ChimahonAnimeVideoStatus.Error -> ChimahonAnimePlayerVideoStatus.Error
    }
}

private fun ChimahonAnimeMediaTrack.toAnimePlayerTrackUiModel(): ChimahonAnimePlayerTrackUiModel {
    return ChimahonAnimePlayerTrackUiModel(
        id = key,
        title = title.ifBlank { language ?: key },
        language = language,
        url = url,
        external = external,
        delaySeconds = delaySeconds,
        selectedByDefault = selectedByDefault,
        internalKey = internalData,
    )
}

private fun ChimahonAnimeSkipSegment.toAnimePlayerSkipSegmentUiModel(): ChimahonAnimePlayerSkipSegmentUiModel {
    return ChimahonAnimePlayerSkipSegmentUiModel(
        id = key,
        title = title.ifBlank { kind.name },
        startSeconds = startSeconds,
        endSeconds = endSeconds,
        kind = kind.toAnimePlayerSkipKind(),
        source = source.toAnimePlayerSkipSource(),
        autoSkip = autoSkip,
    )
}

private fun ChimahonAnimeSkipKind.toAnimePlayerSkipKind(): ChimahonAnimePlayerSkipKind {
    return when (this) {
        ChimahonAnimeSkipKind.Opening -> ChimahonAnimePlayerSkipKind.Opening
        ChimahonAnimeSkipKind.Ending -> ChimahonAnimePlayerSkipKind.Ending
        ChimahonAnimeSkipKind.Recap -> ChimahonAnimePlayerSkipKind.Recap
        ChimahonAnimeSkipKind.MixedOpening -> ChimahonAnimePlayerSkipKind.MixedOpening
        ChimahonAnimeSkipKind.Other -> ChimahonAnimePlayerSkipKind.Other
    }
}

private fun ChimahonAnimeSkipSource.toAnimePlayerSkipSource(): ChimahonAnimePlayerSkipSource {
    return when (this) {
        ChimahonAnimeSkipSource.Source -> ChimahonAnimePlayerSkipSource.Source
        ChimahonAnimeSkipSource.AniSkip -> ChimahonAnimePlayerSkipSource.AniSkip
        ChimahonAnimeSkipSource.Local -> ChimahonAnimePlayerSkipSource.Local
        ChimahonAnimeSkipSource.Manual -> ChimahonAnimePlayerSkipSource.Manual
    }
}

private fun ChimahonAnimePlaybackSource.toAnimePlayerPlaybackSource(): ChimahonAnimePlayerPlaybackSource {
    return when (this) {
        ChimahonAnimePlaybackSource.Stream -> ChimahonAnimePlayerPlaybackSource.Stream
        ChimahonAnimePlaybackSource.Download -> ChimahonAnimePlayerPlaybackSource.Download
        ChimahonAnimePlaybackSource.ExternalPlayer -> ChimahonAnimePlayerPlaybackSource.ExternalPlayer
    }
}

private fun ChimahonAnimePlayerSession.toAnimePlayerStreamStatus(
    selectedVideo: ChimahonAnimeVideoStream?,
    errorMessage: String?,
): ChimahonAnimePlayerStreamStatus {
    return when {
        errorMessage != null -> ChimahonAnimePlayerStreamStatus.Error
        loading -> ChimahonAnimePlayerStreamStatus.LoadingVideo
        selectedVideo?.status == ChimahonAnimeVideoStatus.Loading -> ChimahonAnimePlayerStreamStatus.LoadingVideo
        hosters.any { it.status == ChimahonAnimeHosterStatus.Loading } -> ChimahonAnimePlayerStreamStatus.LoadingHosters
        selectedVideo?.status == ChimahonAnimeVideoStatus.Ready && selectedVideo.streamUrl.isNotBlank() ->
            ChimahonAnimePlayerStreamStatus.Ready
        selectedVideo?.status == ChimahonAnimeVideoStatus.Error -> ChimahonAnimePlayerStreamStatus.Error
        else -> ChimahonAnimePlayerStreamStatus.Idle
    }
}

private fun ChimahonAnimeHistoryEntry.toAnimeHistoryProgressLabel(): String? {
    return when {
        seen -> "Seen"
        lastSecondSeen > 0L && totalSeconds > 0L ->
            "${lastSecondSeen.toAnimePlaybackClock()} / ${totalSeconds.toAnimePlaybackClock()}"
        lastSecondSeen > 0L -> "Started"
        progressFraction > 0f -> "${(progressFraction * 100f).roundToInt()}%"
        else -> null
    }
}

private fun List<ChimahonAnimeBackendError>.toAnimeBackendErrorMessage(): String? {
    return firstOrNull()?.let { error ->
        error.message ?: error.code.name
    }
}

private fun Long.toAnimeDurationMillisLabel(): String {
    val totalSeconds = (this / 1_000L).coerceAtLeast(0L)
    val hours = totalSeconds / 3_600L
    val minutes = (totalSeconds % 3_600L) / 60L
    return when {
        hours > 0L && minutes > 0L -> "${hours}h ${minutes}m watched"
        hours > 0L -> "${hours}h watched"
        minutes > 0L -> "${minutes}m watched"
        else -> "${totalSeconds}s watched"
    }
}

private fun Long.toAnimePlaybackClock(): String {
    val seconds = coerceAtLeast(0L)
    val hours = seconds / 3_600L
    val minutes = (seconds % 3_600L) / 60L
    val remainingSeconds = seconds % 60L
    return if (hours > 0L) {
        "$hours:${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}"
    } else {
        "$minutes:${remainingSeconds.toString().padStart(2, '0')}"
    }
}

private fun Double.trimmedAnimeBackendNumber(): String {
    val intValue = toInt()
    return if (this == intValue.toDouble()) {
        intValue.toString()
    } else {
        toString().trimEnd('0').trimEnd('.')
    }
}
