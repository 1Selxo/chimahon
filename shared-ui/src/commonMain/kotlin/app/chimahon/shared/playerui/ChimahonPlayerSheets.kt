package app.chimahon.shared.playerui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonPlayerOverlaySheet(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
) {
    val sheet = state.selectedSheet
    val panel = state.selectedPanel
    when {
        sheet != null -> ChimahonPlayerSheetContent(
            sheet = sheet,
            state = state,
            actions = actions,
            modifier = modifier,
        )
        panel != null -> ChimahonPlayerPanelContent(
            panel = panel,
            state = state,
            actions = actions,
            modifier = modifier,
        )
    }
}

@Composable
fun ChimahonPlayerSheetContent(
    sheet: ChimahonPlayerSheet,
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
) {
    ChimahonPlayerSheetSurface(modifier = modifier) {
        when (sheet) {
            ChimahonPlayerSheet.Hosters -> ChimahonHostersSheet(state, actions)
            ChimahonPlayerSheet.Episodes -> ChimahonEpisodesSheet(state, actions)
            ChimahonPlayerSheet.SubtitleTracks -> ChimahonSubtitleTracksSheet(state, actions)
            ChimahonPlayerSheet.AudioTracks -> ChimahonAudioTracksSheet(state, actions)
            ChimahonPlayerSheet.QualityTracks -> ChimahonQualityTracksSheet(state, actions)
            ChimahonPlayerSheet.Chapters -> ChimahonChaptersSheet(state, actions)
            ChimahonPlayerSheet.PlaybackSpeed -> ChimahonPlaybackSpeedSheet(state, actions)
            ChimahonPlayerSheet.Screenshot -> ChimahonScreenshotSheet(state, actions)
            ChimahonPlayerSheet.More -> ChimahonPlayerMoreSheet(state, actions)
        }
    }
}

@Composable
fun ChimahonPlayerPanelContent(
    panel: ChimahonPlayerPanel,
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
) {
    ChimahonPlayerSheetSurface(modifier = modifier) {
        when (panel) {
            ChimahonPlayerPanel.SubtitleCueList -> ChimahonSubtitleCueListPanel(state, actions)
            ChimahonPlayerPanel.SubtitleSettings -> ChimahonSubtitleSettingsPanel(state, actions)
            ChimahonPlayerPanel.SubtitleDelay -> ChimahonDelayPanel(
                title = "Subtitle delay",
                seconds = state.settings.subtitleDelaySeconds,
                onSecondsChange = actions.onSubtitleDelayChange,
                onClose = actions.onDismissOverlay,
            )
            ChimahonPlayerPanel.SubtitleRegex -> ChimahonSubtitleRegexPanel(state, actions)
            ChimahonPlayerPanel.AudioDelay -> ChimahonDelayPanel(
                title = "Audio delay",
                seconds = state.settings.audioDelaySeconds,
                onSecondsChange = actions.onAudioDelayChange,
                onClose = actions.onDismissOverlay,
            )
            ChimahonPlayerPanel.VideoFilters -> ChimahonVideoFiltersPanel(state, actions)
            ChimahonPlayerPanel.VideoOcr -> ChimahonVideoOcrPanel(state, actions)
        }
    }
}

@Composable
fun ChimahonPlayerSheetSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 12.dp,
    ) {
        content()
    }
}

@Composable
fun ChimahonEpisodesSheet(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
) {
    LazyColumn {
        item {
            ChimahonPlayerSheetHeader(
                title = "Episodes",
                subtitle = state.currentEpisodeLabel ?: "${state.episodes.size} episode(s)",
                onClose = actions.onDismissOverlay,
            )
        }
        if (state.episodes.isEmpty()) {
            item {
                ChimahonPlayerEmptyNote("Episode list will appear once the player receives a playlist.")
            }
        } else {
            items(state.episodes, key = { it.id }) { episode ->
                ChimahonPlayerEpisodeDrawerRow(
                    episode = episode,
                    selected = episode.id == state.currentEpisodeId,
                    onClick = {
                        actions.onEpisodeClick(episode)
                        actions.onDismissOverlay()
                    },
                )
            }
        }
    }
}

@Composable
fun ChimahonHostersSheet(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
) {
    LazyColumn {
        item {
            ChimahonPlayerSheetHeader(
                title = "Hosters",
                subtitle = "Sources that can provide video streams",
                onClose = actions.onDismissOverlay,
            )
        }
        items(state.tracks.hosters, key = { it.id }) { hoster ->
            ChimahonPlayerTrackRow(
                title = hoster.title,
                subtitle = listOfNotNull(hoster.url, hoster.statusLabel).joinToString(" - "),
                selected = hoster.id == state.tracks.selectedHosterId,
                icon = ChimahonPlayerIcon.Hoster,
                trailing = if (hoster.preferred) "Preferred" else null,
                onClick = { actions.onSelectHoster(hoster) },
            )
        }
    }
}

@Composable
fun ChimahonSubtitleTracksSheet(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
) {
    LazyColumn {
        item {
            ChimahonPlayerSheetHeader(
                title = "Subtitles",
                subtitle = "Tracks, Jimaku lookup, delay, and style",
                onClose = actions.onDismissOverlay,
            )
        }
        items(state.tracks.subtitleTracks, key = { it.id }) { track ->
            ChimahonPlayerTrackRow(
                title = track.title,
                subtitle = listOfNotNull(track.language, if (track.external) "External" else null).joinToString(" - "),
                selected = track.id in state.tracks.selectedSubtitleTrackIds,
                icon = ChimahonPlayerIcon.Subtitles,
                trailing = formatSignedSeconds(track.delaySeconds),
                onClick = { actions.onSelectSubtitleTrack(track) },
            )
        }
        item {
            ChimahonPlayerDividerSpacer()
            ChimahonPlayerTrackRow(
                title = "Subtitle settings",
                subtitle = "Typography, colors, background, and borders",
                selected = false,
                icon = ChimahonPlayerIcon.Settings,
                onClick = { actions.onOpenPanel(ChimahonPlayerPanel.SubtitleSettings) },
            )
            ChimahonPlayerTrackRow(
                title = "Subtitle delay",
                subtitle = formatSignedSeconds(state.settings.subtitleDelaySeconds),
                selected = state.settings.subtitleDelaySeconds != 0.0,
                icon = ChimahonPlayerIcon.Speed,
                onClick = { actions.onOpenPanel(ChimahonPlayerPanel.SubtitleDelay) },
            )
            ChimahonPlayerTrackRow(
                title = "Subtitle lookup",
                subtitle = "Tap subtitles to search dictionary text",
                selected = state.subtitleLookupEnabled,
                icon = ChimahonPlayerIcon.Lookup,
                onClick = actions.onToggleSubtitleLookup,
            )
            ChimahonPlayerTrackRow(
                title = "Regex filters",
                subtitle = "Clean signs, lyrics, and unwanted lines",
                selected = state.settings.subtitleRegexFiltersEnabled,
                icon = ChimahonPlayerIcon.Filters,
                onClick = { actions.onOpenPanel(ChimahonPlayerPanel.SubtitleRegex) },
            )
        }
    }
}

@Composable
fun ChimahonAudioTracksSheet(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
) {
    LazyColumn {
        item {
            ChimahonPlayerSheetHeader(
                title = "Audio",
                subtitle = "Tracks and sync delay",
                onClose = actions.onDismissOverlay,
            )
        }
        items(state.tracks.audioTracks, key = { it.id }) { track ->
            ChimahonPlayerTrackRow(
                title = track.title,
                subtitle = track.language,
                selected = track.id == state.tracks.selectedAudioTrackId,
                icon = ChimahonPlayerIcon.Audio,
                trailing = formatSignedSeconds(track.delaySeconds),
                onClick = { actions.onSelectAudioTrack(track) },
            )
        }
        item {
            ChimahonPlayerDividerSpacer()
            ChimahonPlayerTrackRow(
                title = "Audio delay",
                subtitle = formatSignedSeconds(state.settings.audioDelaySeconds),
                selected = state.settings.audioDelaySeconds != 0.0,
                icon = ChimahonPlayerIcon.Speed,
                onClick = { actions.onOpenPanel(ChimahonPlayerPanel.AudioDelay) },
            )
        }
    }
}

@Composable
fun ChimahonQualityTracksSheet(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
) {
    LazyColumn {
        item {
            ChimahonPlayerSheetHeader(
                title = "Quality",
                subtitle = "Hosters and video streams",
                onClose = actions.onDismissOverlay,
            )
        }
        items(state.tracks.qualities, key = { it.id }) { quality ->
            ChimahonPlayerTrackRow(
                title = quality.title,
                subtitle = listOfNotNull(
                    quality.hoster,
                    quality.detail,
                    quality.resolution?.let { "${it}p" },
                    quality.bitrate?.let { "${it / 1000} Mbps" },
                ).joinToString(" - "),
                selected = quality.id == state.tracks.selectedQualityId,
                icon = ChimahonPlayerIcon.Quality,
                trailing = quality.statusLabel ?: if (quality.preferred) "Preferred" else null,
                onClick = { actions.onSelectQuality(quality) },
            )
        }
    }
}

@Composable
fun ChimahonChaptersSheet(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
) {
    LazyColumn {
        item {
            ChimahonPlayerSheetHeader(
                title = "Chapters",
                subtitle = state.activeChapter?.title,
                onClose = actions.onDismissOverlay,
            )
        }
        items(state.chapters, key = { it.id }) { chapter ->
            ChimahonPlayerTrackRow(
                title = chapter.title,
                subtitle = chimahonFormatPlayerTime(chapter.startSeconds),
                selected = chapter.contains(state.positionSeconds),
                icon = ChimahonPlayerIcon.Chapters,
                trailing = when {
                    chapter.intro || chapter.kind == ChimahonPlayerChapterKind.Opening -> "Intro"
                    chapter.ending || chapter.kind == ChimahonPlayerChapterKind.Ending -> "ED"
                    chapter.kind == ChimahonPlayerChapterKind.Recap -> "Recap"
                    else -> null
                },
                onClick = {
                    if (chapter.isSkipCandidate) {
                        actions.onSkipSegment(chapter)
                    } else {
                        actions.onChapterClick(chapter)
                    }
                },
            )
        }
    }
}

@Composable
fun ChimahonPlaybackSpeedSheet(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
) {
    Column {
        ChimahonPlayerSheetHeader(
            title = "Playback speed",
            subtitle = "${state.settings.playbackSpeed}x",
            onClose = actions.onDismissOverlay,
        )
        ChimahonPlayerSliderRow(
            title = "Speed",
            value = state.settings.playbackSpeed,
            valueLabel = "${state.settings.playbackSpeed}x",
            min = 0.25f,
            max = 3f,
            icon = ChimahonPlayerIcon.Speed,
            onValueChange = actions.onPlaybackSpeedChange,
        )
        listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f).forEach { speed ->
            ChimahonPlayerTrackRow(
                title = "${speed}x",
                selected = state.settings.playbackSpeed == speed,
                icon = ChimahonPlayerIcon.Speed,
                onClick = { actions.onPlaybackSpeedChange(speed) },
            )
        }
    }
}

@Composable
fun ChimahonScreenshotSheet(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
) {
    Column {
        ChimahonPlayerSheetHeader(
            title = "Screenshot",
            subtitle = "Capture current video frame",
            onClose = actions.onDismissOverlay,
        )
        ChimahonPlayerSettingSwitchRow(
            title = "Include subtitles",
            subtitle = "Use the rendered subtitle layer in screenshots",
            checked = state.settings.screenshotIncludesSubtitles,
            onCheckedChange = {},
            icon = ChimahonPlayerIcon.Subtitles,
        )
        ChimahonPlayerTrackRow(
            title = "Video OCR",
            subtitle = "Capture the current frame and send it to OCR lookup",
            selected = state.videoOcrEnabled,
            icon = ChimahonPlayerIcon.Ocr,
            onClick = actions.onCaptureVideoOcr,
        )
    }
}

@Composable
fun ChimahonPlayerMoreSheet(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
) {
    LazyColumn {
        item {
            ChimahonPlayerSheetHeader(
                title = "Player",
                subtitle = "Controls, filters, decoder, and desktop shortcuts",
                onClose = actions.onDismissOverlay,
            )
        }
        item {
            ChimahonPlayerTrackRow(
                title = "Playback speed",
                subtitle = "${state.settings.playbackSpeed}x",
                selected = state.settings.playbackSpeed != 1f,
                icon = ChimahonPlayerIcon.Speed,
                onClick = { actions.onOpenSheet(ChimahonPlayerSheet.PlaybackSpeed) },
            )
            ChimahonPlayerTrackRow(
                title = "Video filters",
                subtitle = "Brightness, contrast, saturation, gamma, hue",
                selected = state.filters.any { it.value != it.neutral },
                icon = ChimahonPlayerIcon.Filters,
                onClick = { actions.onOpenPanel(ChimahonPlayerPanel.VideoFilters) },
            )
            ChimahonPlayerTrackRow(
                title = "Chapters",
                subtitle = "${state.chapters.size} marker(s)",
                selected = state.chapters.isNotEmpty(),
                icon = ChimahonPlayerIcon.Chapters,
                onClick = { actions.onOpenSheet(ChimahonPlayerSheet.Chapters) },
            )
            ChimahonPlayerTrackRow(
                title = "Picture in picture",
                subtitle = if (state.pictureInPictureAvailable) "Keep playback floating" else "Unavailable on this platform",
                selected = state.pictureInPicture,
                icon = ChimahonPlayerIcon.PictureInPicture,
                onClick = actions.onTogglePictureInPicture,
            )
            ChimahonPlayerTrackRow(
                title = "Theater mode",
                subtitle = "Use the wider player layout",
                selected = state.theaterMode,
                icon = ChimahonPlayerIcon.Theater,
                onClick = actions.onToggleTheaterMode,
            )
            ChimahonPlayerTrackRow(
                title = "Screen fit",
                subtitle = state.videoFit.title,
                selected = state.videoFit != ChimahonPlayerVideoFit.Fit,
                icon = ChimahonPlayerIcon.Fit,
                onClick = actions.onCycleVideoFit,
            )
            ChimahonPlayerTrackRow(
                title = state.stream.downloadState.title,
                subtitle = streamPlaybackSubtitle(state.stream),
                selected = state.stream.playbackSource == ChimahonPlayerPlaybackSource.Download,
                icon = if (state.stream.downloadState == ChimahonPlayerDownloadState.Downloaded) {
                    ChimahonPlayerIcon.DownloadOffline
                } else {
                    ChimahonPlayerIcon.Download
                },
                onClick = {
                    if (state.stream.downloadState == ChimahonPlayerDownloadState.Downloaded) {
                        actions.onToggleDownloadPlayback()
                    } else {
                        actions.onDownloadEpisode()
                    }
                },
            )
            if (state.stream.externalPlayerAvailable) {
                ChimahonPlayerTrackRow(
                    title = "External player",
                    subtitle = "Open this stream with a platform player",
                    selected = state.stream.playbackSource == ChimahonPlayerPlaybackSource.ExternalPlayer,
                    icon = ChimahonPlayerIcon.OpenSource,
                    onClick = actions.onOpenExternalPlayer,
                )
            }
            ChimahonPlayerTrackRow(
                title = "Screenshot",
                subtitle = "Frame capture and OCR",
                selected = false,
                icon = ChimahonPlayerIcon.Ocr,
                onClick = { actions.onOpenSheet(ChimahonPlayerSheet.Screenshot) },
            )
        }
        if (!state.stream.sourceUrl.isNullOrBlank()) {
            item {
                ChimahonPlayerDividerSpacer()
                ChimahonPlayerSectionTitle("Source")
                ChimahonPlayerTrackRow(
                    title = "Open source",
                    subtitle = state.stream.sourceUrl,
                    selected = false,
                    icon = ChimahonPlayerIcon.OpenSource,
                    onClick = actions.onOpenSource,
                )
                ChimahonPlayerTrackRow(
                    title = "Copy source URL",
                    subtitle = state.stream.sourceUrl,
                    selected = false,
                    icon = ChimahonPlayerIcon.Copy,
                    onClick = actions.onCopySourceUrl,
                )
                ChimahonPlayerTrackRow(
                    title = "Share source",
                    subtitle = state.title,
                    selected = false,
                    icon = ChimahonPlayerIcon.Share,
                    onClick = actions.onShareSourceUrl,
                )
            }
        }
        item {
            ChimahonPlayerDividerSpacer()
            ChimahonPlayerSectionTitle("Input hints")
        }
        val actionHints = state.actionHints.ifEmpty { state.keyHints.toPlayerActionHints() }
        items(actionHints, key = { "${it.kind}:${it.action}:${it.input}" }) { hint ->
            ChimahonPlayerActionHintRow(hint = hint)
        }
    }
}

@Composable
fun ChimahonSubtitleSettingsPanel(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
) {
    Column {
        ChimahonPlayerSheetHeader(
            title = "Subtitle settings",
            subtitle = "Typography, outline, and background",
            onClose = actions.onDismissOverlay,
        )
        ChimahonPlayerSliderRow(
            title = "Font scale",
            value = state.settings.subtitleFontScale,
            valueLabel = "${(state.settings.subtitleFontScale * 100f).toInt()}%",
            min = 0.5f,
            max = 2f,
            icon = ChimahonPlayerIcon.Subtitles,
            onValueChange = {},
        )
        ChimahonPlayerSettingSwitchRow(
            title = "Subtitle background",
            subtitle = "Show translucent line background",
            checked = state.settings.subtitleBackground,
            onCheckedChange = {},
            icon = ChimahonPlayerIcon.Visibility,
        )
        ChimahonPlayerSectionTitle("Border style")
        ChimahonPlayerSubtitleBorder.entries.forEach { border ->
            ChimahonPlayerTrackRow(
                title = border.displayName,
                selected = border == state.settings.subtitleBorder,
                icon = ChimahonPlayerIcon.Subtitles,
                onClick = {},
            )
        }
    }
}

@Composable
fun ChimahonDelayPanel(
    title: String,
    seconds: Double,
    onSecondsChange: (Double) -> Unit,
    onClose: () -> Unit,
) {
    Column {
        ChimahonPlayerSheetHeader(
            title = title,
            subtitle = formatSignedSeconds(seconds),
            onClose = onClose,
        )
        ChimahonPlayerSliderRow(
            title = "Delay",
            value = seconds.toFloat(),
            valueLabel = formatSignedSeconds(seconds),
            min = -10f,
            max = 10f,
            icon = ChimahonPlayerIcon.Speed,
            onValueChange = { onSecondsChange(it.toDouble()) },
        )
        listOf(-1.0, -0.5, -0.1, 0.0, 0.1, 0.5, 1.0).forEach { step ->
            ChimahonPlayerTrackRow(
                title = if (step == 0.0) "Reset" else formatSignedSeconds(step),
                selected = seconds == step,
                icon = ChimahonPlayerIcon.Speed,
                onClick = { onSecondsChange(step) },
            )
        }
    }
}

@Composable
fun ChimahonSubtitleRegexPanel(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
) {
    Column {
        ChimahonPlayerSheetHeader(
            title = "Subtitle regex filters",
            subtitle = "Remove unwanted subtitle lines before lookup",
            onClose = actions.onDismissOverlay,
        )
        ChimahonPlayerSettingSwitchRow(
            title = "Enable filters",
            subtitle = "Apply configured subtitle cleanup patterns",
            checked = state.settings.subtitleRegexFiltersEnabled,
            onCheckedChange = {},
            icon = ChimahonPlayerIcon.Filters,
        )
        ChimahonPlayerEmptyNote("Regex rule editing will plug into the shared settings repository.")
    }
}

@Composable
fun ChimahonVideoFiltersPanel(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
) {
    LazyColumn {
        item {
            ChimahonPlayerSheetHeader(
                title = "Video filters",
                subtitle = "Matches Android player filter panel",
                onClose = actions.onDismissOverlay,
            )
        }
        items(state.filters, key = { it.id }) { filter ->
            ChimahonPlayerSliderRow(
                title = filter.title,
                value = filter.value.toFloat(),
                valueLabel = filter.value.toString(),
                min = filter.min.toFloat(),
                max = filter.max.toFloat(),
                icon = ChimahonPlayerIcon.Filters,
                onValueChange = { actions.onFilterChange(filter, it.toInt()) },
            )
        }
        item {
            ChimahonPlayerTrackRow(
                title = "Reset filters",
                subtitle = "Return all video filters to neutral",
                selected = false,
                icon = ChimahonPlayerIcon.Refresh,
                onClick = actions.onResetFilters,
            )
        }
    }
}

@Composable
fun ChimahonVideoOcrPanel(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
) {
    Column {
        ChimahonPlayerSheetHeader(
            title = "Video OCR",
            subtitle = "Capture subtitles/signs and run GLens lookup",
            onClose = actions.onDismissOverlay,
        )
        ChimahonPlayerTrackRow(
            title = "Capture current frame",
            subtitle = "Pause video and send the frame through OCR",
            selected = state.videoOcrEnabled,
            icon = ChimahonPlayerIcon.Ocr,
            onClick = actions.onCaptureVideoOcr,
        )
        ChimahonPlayerEmptyNote("OCR boxes and lookup cards can reuse the shared reader OCR UI.")
    }
}

@Composable
fun ChimahonPlayerEmptyNote(
    message: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = message,
        modifier = modifier
            .fillMaxWidth()
            .padding(18.dp)
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.06f), RoundedCornerShape(8.dp))
            .padding(16.dp),
        style = MaterialTheme.typography.body2,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f),
    )
}

val ChimahonPlayerSubtitleBorder.displayName: String
    get() = when (this) {
        ChimahonPlayerSubtitleBorder.None -> "None"
        ChimahonPlayerSubtitleBorder.Shadow -> "Shadow"
        ChimahonPlayerSubtitleBorder.Outline -> "Outline"
        ChimahonPlayerSubtitleBorder.OutlineAndShadow -> "Outline and shadow"
    }

private fun formatSignedSeconds(seconds: Double): String {
    val prefix = if (seconds > 0.0) "+" else ""
    return "$prefix${seconds}s"
}

private fun streamPlaybackSubtitle(stream: ChimahonPlayerStreamUiState): String {
    return listOfNotNull(
        stream.statusLabel,
        stream.playbackSource.title,
        stream.downloadProgress.takeIf { it > 0 }?.let { "$it%" },
    ).joinToString(" - ").ifBlank { stream.playbackSource.title }
}

val ChimahonPlayerPlaybackSource.title: String
    get() = when (this) {
        ChimahonPlayerPlaybackSource.Stream -> "Streaming"
        ChimahonPlayerPlaybackSource.Download -> "Downloaded playback"
        ChimahonPlayerPlaybackSource.ExternalPlayer -> "External player"
    }
