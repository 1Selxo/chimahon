package app.chimahon.shared.playerui

import app.chimahon.shared.animeui.ChimahonAnimeDownloadUiState
import app.chimahon.shared.animeui.ChimahonAnimeEpisodeDisplayMode
import app.chimahon.shared.animeui.ChimahonAnimeEpisodeUiModel
import app.chimahon.shared.animeui.ChimahonAnimePlayerActionId
import app.chimahon.shared.animeui.ChimahonAnimePlayerCallbacks
import app.chimahon.shared.animeui.ChimahonAnimePlayerHosterStatus
import app.chimahon.shared.animeui.ChimahonAnimePlayerHosterUiModel
import app.chimahon.shared.animeui.ChimahonAnimePlayerPlaybackSource
import app.chimahon.shared.animeui.ChimahonAnimePlayerSkipKind
import app.chimahon.shared.animeui.ChimahonAnimePlayerSkipSegmentUiModel
import app.chimahon.shared.animeui.ChimahonAnimePlayerSkipSource
import app.chimahon.shared.animeui.ChimahonAnimePlayerStreamStatus
import app.chimahon.shared.animeui.ChimahonAnimePlayerStreamUiState
import app.chimahon.shared.animeui.ChimahonAnimePlayerTrackUiModel
import app.chimahon.shared.animeui.ChimahonAnimePlayerUiState
import app.chimahon.shared.animeui.ChimahonAnimePlayerVideoQualityUiModel
import app.chimahon.shared.animeui.ChimahonAnimePlayerVideoStatus

fun ChimahonAnimePlayerUiState.toChimahonPlayerUiState(
    episodes: List<ChimahonPlayerEpisodeUiModel> = emptyList(),
    currentEpisodeId: String? = episodes.firstOrNull { it.title == episodeTitle }?.id,
    selectedSheet: ChimahonPlayerSheet? = null,
    selectedPanel: ChimahonPlayerPanel? = null,
    selectedDrawer: ChimahonPlayerDrawer? = null,
    videoFit: ChimahonPlayerVideoFit = ChimahonPlayerVideoFit.Fit,
    edgeControls: ChimahonPlayerEdgeControlsUiState = ChimahonPlayerEdgeControlsUiState(),
    lookup: ChimahonPlayerLookupUiState = ChimahonPlayerLookupUiState(
        subtitleText = subtitleText,
        subtitleLookupAvailable = !subtitleText.isNullOrBlank(),
    ),
    actionHints: List<ChimahonPlayerActionHint> = defaultPlayerActionHints(),
): ChimahonPlayerUiState {
    return ChimahonPlayerUiState(
        title = animeTitle.ifBlank { "Anime" },
        subtitle = subtitleText?.takeIf { it.isNotBlank() }?.take(80),
        sourceName = sourceName,
        episodeTitle = episodeTitle.takeIf { it.isNotBlank() },
        positionSeconds = positionSeconds,
        durationSeconds = durationSeconds,
        bufferedSeconds = bufferedSeconds,
        paused = paused,
        loading = loading || loadingEpisode,
        controlsVisible = controlsVisible,
        controlsLocked = controlsLocked,
        fullscreen = fullscreen,
        videoFit = videoFit,
        pictureInPictureAvailable = pictureInPictureAvailable,
        pictureInPicture = pictureInPicture,
        theaterMode = theaterMode,
        selectedSheet = selectedSheet,
        selectedPanel = selectedPanel,
        selectedDrawer = selectedDrawer,
        episodes = episodes,
        currentEpisodeId = currentEpisodeId,
        chapters = skipSegments.map { it.toChimahonPlayerChapterUiModel() },
        tracks = ChimahonPlayerTracksUiState(
            hosters = hosters.map { it.toChimahonPlayerHosterUiModel() },
            selectedHosterId = selectedHosterId,
            subtitleTracks = subtitleTracks.map { it.toChimahonPlayerTrackUiModel() },
            selectedSubtitleTrackIds = selectedSubtitleTrackIds,
            audioTracks = audioTracks.map { it.toChimahonPlayerTrackUiModel() },
            selectedAudioTrackId = selectedAudioTrackId,
            qualities = qualities.map { it.toChimahonPlayerQualityUiModel() },
            selectedQualityId = selectedQualityId,
        ),
        settings = ChimahonPlayerSettingsUiState(playbackSpeed = playbackSpeed),
        stream = stream.toChimahonPlayerStreamUiState(
            fallbackSourceUrl = sourceUrl,
            fallbackStreamUrl = streamUrl,
        ),
        edgeControls = edgeControls,
        lookup = lookup,
        actionHints = actionHints,
        errorMessage = errorMessage,
    )
}

fun ChimahonAnimePlayerUiState.toChimahonPlayerUiStateFromAnimeEpisodes(
    animeEpisodes: List<ChimahonAnimeEpisodeUiModel>,
    displayMode: ChimahonAnimeEpisodeDisplayMode = ChimahonAnimeEpisodeDisplayMode.SourceTitle,
    selectedEpisodeId: String? = animeEpisodes.firstOrNull { it.sourceTitle == episodeTitle || it.title == episodeTitle }?.id,
    selectedSheet: ChimahonPlayerSheet? = null,
    selectedPanel: ChimahonPlayerPanel? = null,
    selectedDrawer: ChimahonPlayerDrawer? = null,
): ChimahonPlayerUiState {
    return toChimahonPlayerUiState(
        episodes = animeEpisodes.map { it.toChimahonPlayerEpisodeUiModel(displayMode = displayMode) },
        currentEpisodeId = selectedEpisodeId,
        selectedSheet = selectedSheet,
        selectedPanel = selectedPanel,
        selectedDrawer = selectedDrawer,
    )
}

fun ChimahonAnimeEpisodeUiModel.toChimahonPlayerEpisodeUiModel(
    displayMode: ChimahonAnimeEpisodeDisplayMode = ChimahonAnimeEpisodeDisplayMode.SourceTitle,
    durationSeconds: Long = 0L,
    progressSeconds: Long = 0L,
): ChimahonPlayerEpisodeUiModel {
    return ChimahonPlayerEpisodeUiModel(
        id = id,
        title = displayTitle(displayMode),
        subtitle = metadataParts().joinToString(" - ").takeIf { it.isNotBlank() },
        episodeNumber = episodeNumber,
        durationSeconds = durationSeconds,
        progressSeconds = progressSeconds,
        seen = seen,
        bookmarked = bookmarked,
        filler = filler,
        downloaded = downloadState == ChimahonAnimeDownloadUiState.Downloaded,
        sourceOrder = sourceOrder,
    )
}

fun ChimahonAnimePlayerCallbacks.toChimahonPlayerUiActions(): ChimahonPlayerUiActions {
    return ChimahonPlayerUiActions(
        onBack = { onAction(ChimahonAnimePlayerActionId.Back) },
        onTogglePlayback = { onAction(ChimahonAnimePlayerActionId.PlayPause) },
        onSeekTo = onSeekToSeconds,
        onSeekBy = { amount ->
            onAction(
                if (amount < 0L) {
                    ChimahonAnimePlayerActionId.SeekBackward
                } else {
                    ChimahonAnimePlayerActionId.SeekForward
                },
            )
        },
        onPreviousEpisode = { onAction(ChimahonAnimePlayerActionId.PreviousEpisode) },
        onNextEpisode = { onAction(ChimahonAnimePlayerActionId.NextEpisode) },
        onToggleFullscreen = onToggleFullscreen,
        onTogglePictureInPicture = onTogglePictureInPicture,
        onToggleTheaterMode = onToggleTheaterMode,
        onSelectHoster = { onSelectHoster(it.toAnimePlayerHosterUiModel()) },
        onSelectSubtitleTrack = { onSelectSubtitleTrack(it.toAnimePlayerTrackUiModel()) },
        onSelectAudioTrack = { onSelectAudioTrack(it.toAnimePlayerTrackUiModel()) },
        onSelectQuality = { onSelectQuality(it.toAnimePlayerVideoQualityUiModel()) },
        onSkipSegment = { onSkipSegment(it.toAnimePlayerSkipSegmentUiModel()) },
        onPlaybackSpeedChange = onPlaybackSpeedChange,
        onDownloadEpisode = onDownload,
        onToggleDownloadPlayback = onToggleDownloadPlayback,
        onOpenSource = onOpenSource,
        onCopySourceUrl = onCopySource,
        onShareSourceUrl = onShareSource,
        onRetry = onRetry,
    )
}

fun ChimahonAnimePlayerHosterUiModel.toChimahonPlayerHosterUiModel(): ChimahonPlayerHosterUiModel {
    return ChimahonPlayerHosterUiModel(
        id = id,
        title = title,
        url = url,
        status = status.toChimahonPlayerHosterStatus(),
        videoCount = videoCount,
        preferred = preferred,
        errorMessage = errorMessage,
        internalKey = internalKey,
    )
}

fun ChimahonPlayerHosterUiModel.toAnimePlayerHosterUiModel(): ChimahonAnimePlayerHosterUiModel {
    return ChimahonAnimePlayerHosterUiModel(
        id = id,
        title = title,
        url = url,
        status = status.toAnimePlayerHosterStatus(),
        videoCount = videoCount,
        preferred = preferred,
        errorMessage = errorMessage,
        internalKey = internalKey,
    )
}

fun ChimahonAnimePlayerTrackUiModel.toChimahonPlayerTrackUiModel(): ChimahonPlayerTrackUiModel {
    return ChimahonPlayerTrackUiModel(
        id = id,
        title = title,
        language = language,
        external = external,
        delaySeconds = delaySeconds,
        url = url,
        selectedByDefault = selectedByDefault,
        internalKey = internalKey,
    )
}

fun ChimahonPlayerTrackUiModel.toAnimePlayerTrackUiModel(): ChimahonAnimePlayerTrackUiModel {
    return ChimahonAnimePlayerTrackUiModel(
        id = id,
        title = title,
        language = language,
        url = url,
        external = external,
        delaySeconds = delaySeconds,
        selectedByDefault = selectedByDefault,
        internalKey = internalKey,
    )
}

fun ChimahonAnimePlayerVideoQualityUiModel.toChimahonPlayerQualityUiModel(): ChimahonPlayerQualityUiModel {
    return ChimahonPlayerQualityUiModel(
        id = id,
        title = title,
        detail = detail,
        hosterId = hosterId,
        hoster = hosterTitle,
        resolution = resolution,
        bitrate = bitrate,
        preferred = preferred,
        initialized = initialized,
        status = status.toChimahonPlayerVideoStatus(),
        sourceUrl = sourceUrl,
        streamUrl = streamUrl,
        internalKey = internalKey,
        errorMessage = errorMessage,
    )
}

fun ChimahonPlayerQualityUiModel.toAnimePlayerVideoQualityUiModel(): ChimahonAnimePlayerVideoQualityUiModel {
    return ChimahonAnimePlayerVideoQualityUiModel(
        id = id,
        title = title,
        detail = detail,
        hosterId = hosterId,
        hosterTitle = hoster,
        resolution = resolution,
        bitrate = bitrate,
        preferred = preferred,
        initialized = initialized,
        status = status.toAnimePlayerVideoStatus(),
        sourceUrl = sourceUrl,
        streamUrl = streamUrl,
        errorMessage = errorMessage,
        internalKey = internalKey,
    )
}

fun ChimahonAnimePlayerSkipSegmentUiModel.toChimahonPlayerChapterUiModel(): ChimahonPlayerChapterUiModel {
    return ChimahonPlayerChapterUiModel(
        id = id,
        title = title,
        startSeconds = startSeconds,
        endSeconds = endSeconds,
        kind = kind.toChimahonPlayerChapterKind(),
        source = source.toChimahonPlayerTimestampSource(),
        autoSkip = autoSkip,
    )
}

fun ChimahonPlayerChapterUiModel.toAnimePlayerSkipSegmentUiModel(): ChimahonAnimePlayerSkipSegmentUiModel {
    return ChimahonAnimePlayerSkipSegmentUiModel(
        id = id,
        title = title,
        startSeconds = startSeconds,
        endSeconds = endSeconds,
        kind = kind.toAnimePlayerSkipKind(),
        source = source.toAnimePlayerSkipSource(),
        autoSkip = autoSkip,
    )
}

fun ChimahonAnimePlayerStreamUiState.toChimahonPlayerStreamUiState(
    fallbackSourceUrl: String? = null,
    fallbackStreamUrl: String? = null,
): ChimahonPlayerStreamUiState {
    return ChimahonPlayerStreamUiState(
        playbackSource = playbackSource.toChimahonPlayerPlaybackSource(),
        status = status.toChimahonPlayerStreamStatus(),
        downloadState = downloadState.toChimahonPlayerDownloadState(),
        downloadProgress = downloadProgress,
        sourceUrl = sourceUrl ?: fallbackSourceUrl,
        streamUrl = streamUrl ?: fallbackStreamUrl,
        message = message,
        networkLabel = networkLabel,
        retryable = retryable,
        externalPlayerAvailable = externalPlayerAvailable,
    )
}

private fun ChimahonAnimePlayerHosterStatus.toChimahonPlayerHosterStatus(): ChimahonPlayerHosterStatus {
    return when (this) {
        ChimahonAnimePlayerHosterStatus.Idle -> ChimahonPlayerHosterStatus.Idle
        ChimahonAnimePlayerHosterStatus.Loading -> ChimahonPlayerHosterStatus.Loading
        ChimahonAnimePlayerHosterStatus.Ready -> ChimahonPlayerHosterStatus.Ready
        ChimahonAnimePlayerHosterStatus.Empty -> ChimahonPlayerHosterStatus.Empty
        ChimahonAnimePlayerHosterStatus.Error -> ChimahonPlayerHosterStatus.Error
    }
}

private fun ChimahonPlayerHosterStatus.toAnimePlayerHosterStatus(): ChimahonAnimePlayerHosterStatus {
    return when (this) {
        ChimahonPlayerHosterStatus.Idle -> ChimahonAnimePlayerHosterStatus.Idle
        ChimahonPlayerHosterStatus.Loading -> ChimahonAnimePlayerHosterStatus.Loading
        ChimahonPlayerHosterStatus.Ready -> ChimahonAnimePlayerHosterStatus.Ready
        ChimahonPlayerHosterStatus.Empty -> ChimahonAnimePlayerHosterStatus.Empty
        ChimahonPlayerHosterStatus.Error -> ChimahonAnimePlayerHosterStatus.Error
    }
}

private fun ChimahonAnimePlayerVideoStatus.toChimahonPlayerVideoStatus(): ChimahonPlayerVideoStatus {
    return when (this) {
        ChimahonAnimePlayerVideoStatus.Queued -> ChimahonPlayerVideoStatus.Queued
        ChimahonAnimePlayerVideoStatus.Loading -> ChimahonPlayerVideoStatus.Loading
        ChimahonAnimePlayerVideoStatus.Ready -> ChimahonPlayerVideoStatus.Ready
        ChimahonAnimePlayerVideoStatus.Error -> ChimahonPlayerVideoStatus.Error
    }
}

private fun ChimahonPlayerVideoStatus.toAnimePlayerVideoStatus(): ChimahonAnimePlayerVideoStatus {
    return when (this) {
        ChimahonPlayerVideoStatus.Queued -> ChimahonAnimePlayerVideoStatus.Queued
        ChimahonPlayerVideoStatus.Loading -> ChimahonAnimePlayerVideoStatus.Loading
        ChimahonPlayerVideoStatus.Ready -> ChimahonAnimePlayerVideoStatus.Ready
        ChimahonPlayerVideoStatus.Error -> ChimahonAnimePlayerVideoStatus.Error
    }
}

private fun ChimahonAnimePlayerSkipKind.toChimahonPlayerChapterKind(): ChimahonPlayerChapterKind {
    return when (this) {
        ChimahonAnimePlayerSkipKind.Opening -> ChimahonPlayerChapterKind.Opening
        ChimahonAnimePlayerSkipKind.Ending -> ChimahonPlayerChapterKind.Ending
        ChimahonAnimePlayerSkipKind.Recap -> ChimahonPlayerChapterKind.Recap
        ChimahonAnimePlayerSkipKind.MixedOpening -> ChimahonPlayerChapterKind.MixedOpening
        ChimahonAnimePlayerSkipKind.Other -> ChimahonPlayerChapterKind.Other
    }
}

private fun ChimahonPlayerChapterKind.toAnimePlayerSkipKind(): ChimahonAnimePlayerSkipKind {
    return when (this) {
        ChimahonPlayerChapterKind.Opening -> ChimahonAnimePlayerSkipKind.Opening
        ChimahonPlayerChapterKind.Ending -> ChimahonAnimePlayerSkipKind.Ending
        ChimahonPlayerChapterKind.Recap -> ChimahonAnimePlayerSkipKind.Recap
        ChimahonPlayerChapterKind.MixedOpening -> ChimahonAnimePlayerSkipKind.MixedOpening
        ChimahonPlayerChapterKind.Other -> ChimahonAnimePlayerSkipKind.Other
    }
}

private fun ChimahonAnimePlayerSkipSource.toChimahonPlayerTimestampSource(): ChimahonPlayerTimestampSource {
    return when (this) {
        ChimahonAnimePlayerSkipSource.Source -> ChimahonPlayerTimestampSource.Source
        ChimahonAnimePlayerSkipSource.AniSkip -> ChimahonPlayerTimestampSource.AniSkip
        ChimahonAnimePlayerSkipSource.Local -> ChimahonPlayerTimestampSource.Local
        ChimahonAnimePlayerSkipSource.Manual -> ChimahonPlayerTimestampSource.Manual
    }
}

private fun ChimahonPlayerTimestampSource.toAnimePlayerSkipSource(): ChimahonAnimePlayerSkipSource {
    return when (this) {
        ChimahonPlayerTimestampSource.Source -> ChimahonAnimePlayerSkipSource.Source
        ChimahonPlayerTimestampSource.AniSkip -> ChimahonAnimePlayerSkipSource.AniSkip
        ChimahonPlayerTimestampSource.Local -> ChimahonAnimePlayerSkipSource.Local
        ChimahonPlayerTimestampSource.Manual -> ChimahonAnimePlayerSkipSource.Manual
    }
}

private fun ChimahonAnimePlayerPlaybackSource.toChimahonPlayerPlaybackSource(): ChimahonPlayerPlaybackSource {
    return when (this) {
        ChimahonAnimePlayerPlaybackSource.Stream -> ChimahonPlayerPlaybackSource.Stream
        ChimahonAnimePlayerPlaybackSource.Download -> ChimahonPlayerPlaybackSource.Download
        ChimahonAnimePlayerPlaybackSource.ExternalPlayer -> ChimahonPlayerPlaybackSource.ExternalPlayer
    }
}

private fun ChimahonAnimePlayerStreamStatus.toChimahonPlayerStreamStatus(): ChimahonPlayerStreamStatus {
    return when (this) {
        ChimahonAnimePlayerStreamStatus.Idle -> ChimahonPlayerStreamStatus.Idle
        ChimahonAnimePlayerStreamStatus.LoadingHosters -> ChimahonPlayerStreamStatus.LoadingHosters
        ChimahonAnimePlayerStreamStatus.LoadingVideo -> ChimahonPlayerStreamStatus.LoadingVideo
        ChimahonAnimePlayerStreamStatus.Ready -> ChimahonPlayerStreamStatus.Ready
        ChimahonAnimePlayerStreamStatus.Buffering -> ChimahonPlayerStreamStatus.Buffering
        ChimahonAnimePlayerStreamStatus.Playing -> ChimahonPlayerStreamStatus.Playing
        ChimahonAnimePlayerStreamStatus.Ended -> ChimahonPlayerStreamStatus.Ended
        ChimahonAnimePlayerStreamStatus.Error -> ChimahonPlayerStreamStatus.Error
    }
}

private fun ChimahonAnimeDownloadUiState.toChimahonPlayerDownloadState(): ChimahonPlayerDownloadState {
    return when (this) {
        ChimahonAnimeDownloadUiState.NotDownloaded -> ChimahonPlayerDownloadState.NotDownloaded
        ChimahonAnimeDownloadUiState.Queued -> ChimahonPlayerDownloadState.Queued
        ChimahonAnimeDownloadUiState.Downloading -> ChimahonPlayerDownloadState.Downloading
        ChimahonAnimeDownloadUiState.Downloaded -> ChimahonPlayerDownloadState.Downloaded
        ChimahonAnimeDownloadUiState.Error -> ChimahonPlayerDownloadState.Error
    }
}
