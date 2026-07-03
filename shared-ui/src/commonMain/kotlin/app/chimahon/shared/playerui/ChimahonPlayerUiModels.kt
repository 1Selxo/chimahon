package app.chimahon.shared.playerui

import kotlin.math.floor
import kotlin.math.roundToInt

data class ChimahonPlayerUiState(
    val title: String = "No episode selected",
    val subtitle: String? = null,
    val sourceName: String? = null,
    val episodeTitle: String? = null,
    val positionSeconds: Long = 0L,
    val durationSeconds: Long = 0L,
    val bufferedSeconds: Long = 0L,
    val paused: Boolean = true,
    val loading: Boolean = false,
    val controlsVisible: Boolean = true,
    val controlsLocked: Boolean = false,
    val fullscreen: Boolean = false,
    val videoFit: ChimahonPlayerVideoFit = ChimahonPlayerVideoFit.Fit,
    val pictureInPictureAvailable: Boolean = false,
    val pictureInPicture: Boolean = false,
    val theaterMode: Boolean = false,
    val castAvailable: Boolean = false,
    val casting: Boolean = false,
    val videoOcrEnabled: Boolean = false,
    val subtitleLookupEnabled: Boolean = false,
    val selectedSheet: ChimahonPlayerSheet? = null,
    val selectedPanel: ChimahonPlayerPanel? = null,
    val selectedDrawer: ChimahonPlayerDrawer? = null,
    val episodes: List<ChimahonPlayerEpisodeUiModel> = emptyList(),
    val currentEpisodeId: String? = null,
    val chapters: List<ChimahonPlayerChapterUiModel> = emptyList(),
    val tracks: ChimahonPlayerTracksUiState = ChimahonPlayerTracksUiState(),
    val settings: ChimahonPlayerSettingsUiState = ChimahonPlayerSettingsUiState(),
    val stream: ChimahonPlayerStreamUiState = ChimahonPlayerStreamUiState(),
    val filters: List<ChimahonPlayerFilterUiModel> = ChimahonPlayerFilterDefaults.defaultFilters(),
    val edgeControls: ChimahonPlayerEdgeControlsUiState = ChimahonPlayerEdgeControlsUiState(),
    val lookup: ChimahonPlayerLookupUiState = ChimahonPlayerLookupUiState(),
    val actionHints: List<ChimahonPlayerActionHint> = defaultPlayerActionHints(),
    val keyHints: List<ChimahonPlayerKeyHint> = defaultDesktopPlayerKeyHints(),
    val message: ChimahonPlayerTransientMessage? = null,
    val errorMessage: String? = null,
) {
    val hasDuration: Boolean
        get() = durationSeconds > 0L

    val progressFraction: Float
        get() = if (hasDuration) {
            (positionSeconds.toFloat() / durationSeconds.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

    val bufferedFraction: Float
        get() = if (hasDuration) {
            (bufferedSeconds.toFloat() / durationSeconds.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

    val currentEpisode: ChimahonPlayerEpisodeUiModel?
        get() = episodes.firstOrNull { it.id == currentEpisodeId }

    val currentEpisodeIndex: Int?
        get() = episodes.indexOfFirst { it.id == currentEpisodeId }
            .takeIf { it >= 0 }

    val currentEpisodeLabel: String?
        get() = currentEpisodeIndex?.let { index ->
            "Episode ${index + 1} of ${episodes.size}"
        }

    val activeChapter: ChimahonPlayerChapterUiModel?
        get() = chapters.lastOrNull { it.startSeconds <= positionSeconds }

    val activeSkipSegment: ChimahonPlayerChapterUiModel?
        get() = chapters.firstOrNull { it.isSkipCandidate && it.contains(positionSeconds) }
}

data class ChimahonPlayerEpisodeUiModel(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val episodeNumber: Double? = null,
    val durationSeconds: Long = 0L,
    val progressSeconds: Long = 0L,
    val seen: Boolean = false,
    val bookmarked: Boolean = false,
    val filler: Boolean = false,
    val downloaded: Boolean = false,
    val sourceOrder: Long = 0L,
) {
    val progressFraction: Float
        get() = if (durationSeconds > 0L) {
            (progressSeconds.toFloat() / durationSeconds.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

    val displayNumber: String?
        get() = episodeNumber?.let { number ->
            if (floor(number) == number) {
                number.toInt().toString()
            } else {
                number.toString()
            }
        }
}

data class ChimahonPlayerChapterUiModel(
    val id: String,
    val title: String,
    val startSeconds: Long,
    val endSeconds: Long? = null,
    val intro: Boolean = false,
    val ending: Boolean = false,
    val kind: ChimahonPlayerChapterKind = when {
        intro -> ChimahonPlayerChapterKind.Opening
        ending -> ChimahonPlayerChapterKind.Ending
        else -> ChimahonPlayerChapterKind.Other
    },
    val source: ChimahonPlayerTimestampSource = ChimahonPlayerTimestampSource.Source,
    val autoSkip: Boolean = false,
) {
    val isSkipCandidate: Boolean
        get() = intro || ending || kind.skippable

    val skipActionTitle: String
        get() = when {
            intro || kind == ChimahonPlayerChapterKind.Opening -> "Skip intro"
            ending || kind == ChimahonPlayerChapterKind.Ending -> "Skip outro"
            kind == ChimahonPlayerChapterKind.Recap -> "Skip recap"
            else -> "Skip"
        }

    fun contains(positionSeconds: Long): Boolean {
        return positionSeconds >= startSeconds && (endSeconds == null || positionSeconds < endSeconds)
    }
}

data class ChimahonPlayerTracksUiState(
    val hosters: List<ChimahonPlayerHosterUiModel> = emptyList(),
    val selectedHosterId: String? = null,
    val subtitleTracks: List<ChimahonPlayerTrackUiModel> = emptyList(),
    val selectedSubtitleTrackIds: Set<String> = emptySet(),
    val audioTracks: List<ChimahonPlayerTrackUiModel> = emptyList(),
    val selectedAudioTrackId: String? = null,
    val qualities: List<ChimahonPlayerQualityUiModel> = emptyList(),
    val selectedQualityId: String? = null,
) {
    val selectedHoster: ChimahonPlayerHosterUiModel?
        get() = hosters.firstOrNull { it.id == selectedHosterId }

    val selectedQuality: ChimahonPlayerQualityUiModel?
        get() = qualities.firstOrNull { it.id == selectedQualityId }

    val selectedAudioTrack: ChimahonPlayerTrackUiModel?
        get() = audioTracks.firstOrNull { it.id == selectedAudioTrackId }

    val selectedSubtitleTracks: List<ChimahonPlayerTrackUiModel>
        get() = subtitleTracks.filter { it.id in selectedSubtitleTrackIds }
}

data class ChimahonPlayerHosterUiModel(
    val id: String,
    val title: String,
    val url: String? = null,
    val status: ChimahonPlayerHosterStatus = ChimahonPlayerHosterStatus.Idle,
    val videoCount: Int = 0,
    val preferred: Boolean = false,
    val errorMessage: String? = null,
    val internalKey: String = id,
) {
    val selectable: Boolean
        get() = status == ChimahonPlayerHosterStatus.Ready && videoCount > 0

    val statusLabel: String?
        get() = when (status) {
            ChimahonPlayerHosterStatus.Idle -> null
            ChimahonPlayerHosterStatus.Loading -> "Loading"
            ChimahonPlayerHosterStatus.Ready -> if (videoCount > 0) "$videoCount stream(s)" else "Ready"
            ChimahonPlayerHosterStatus.Empty -> "No streams"
            ChimahonPlayerHosterStatus.Error -> errorMessage ?: "Error"
        }
}

data class ChimahonPlayerTrackUiModel(
    val id: String,
    val title: String,
    val language: String? = null,
    val external: Boolean = false,
    val delaySeconds: Double = 0.0,
    val url: String? = null,
    val selectedByDefault: Boolean = false,
    val internalKey: String = id,
)

data class ChimahonPlayerQualityUiModel(
    val id: String,
    val title: String,
    val detail: String? = null,
    val hosterId: String? = null,
    val hoster: String? = null,
    val loading: Boolean = false,
    val status: ChimahonPlayerVideoStatus = if (loading) ChimahonPlayerVideoStatus.Loading else ChimahonPlayerVideoStatus.Ready,
    val resolution: Int? = null,
    val bitrate: Int? = null,
    val preferred: Boolean = false,
    val initialized: Boolean = true,
    val streamUrl: String? = null,
    val sourceUrl: String? = null,
    val internalKey: String = id,
    val errorMessage: String? = null,
    val ffmpegStreamArgs: List<Pair<String, String>> = emptyList(),
    val ffmpegVideoArgs: List<Pair<String, String>> = emptyList(),
) {
    val statusLabel: String?
        get() = when (status) {
            ChimahonPlayerVideoStatus.Queued -> "Queued"
            ChimahonPlayerVideoStatus.Loading -> "Loading"
            ChimahonPlayerVideoStatus.Ready -> null
            ChimahonPlayerVideoStatus.Error -> errorMessage ?: "Error"
        }
}

data class ChimahonPlayerStreamUiState(
    val playbackSource: ChimahonPlayerPlaybackSource = ChimahonPlayerPlaybackSource.Stream,
    val status: ChimahonPlayerStreamStatus = ChimahonPlayerStreamStatus.Idle,
    val downloadState: ChimahonPlayerDownloadState = ChimahonPlayerDownloadState.NotDownloaded,
    val downloadProgress: Int = 0,
    val sourceUrl: String? = null,
    val streamUrl: String? = null,
    val message: String? = null,
    val networkLabel: String? = null,
    val retryable: Boolean = false,
    val externalPlayerAvailable: Boolean = false,
) {
    val isBusy: Boolean
        get() = status == ChimahonPlayerStreamStatus.LoadingHosters ||
            status == ChimahonPlayerStreamStatus.LoadingVideo ||
            status == ChimahonPlayerStreamStatus.Buffering ||
            downloadState == ChimahonPlayerDownloadState.Downloading ||
            downloadState == ChimahonPlayerDownloadState.Queued

    val statusLabel: String?
        get() = message ?: when (status) {
            ChimahonPlayerStreamStatus.Idle -> null
            ChimahonPlayerStreamStatus.LoadingHosters -> "Loading hosters"
            ChimahonPlayerStreamStatus.LoadingVideo -> "Loading stream"
            ChimahonPlayerStreamStatus.Ready -> "Ready"
            ChimahonPlayerStreamStatus.Buffering -> "Buffering"
            ChimahonPlayerStreamStatus.Playing -> null
            ChimahonPlayerStreamStatus.Ended -> "Finished"
            ChimahonPlayerStreamStatus.Error -> "Playback error"
        }
}

enum class ChimahonPlayerChapterKind(val skippable: Boolean) {
    Opening(true),
    Ending(true),
    Recap(true),
    MixedOpening(true),
    Other(false),
}

enum class ChimahonPlayerTimestampSource {
    Source,
    AniSkip,
    Local,
    Manual,
}

enum class ChimahonPlayerHosterStatus {
    Idle,
    Loading,
    Ready,
    Empty,
    Error,
}

enum class ChimahonPlayerVideoStatus {
    Queued,
    Loading,
    Ready,
    Error,
}

enum class ChimahonPlayerStreamStatus {
    Idle,
    LoadingHosters,
    LoadingVideo,
    Ready,
    Buffering,
    Playing,
    Ended,
    Error,
}

enum class ChimahonPlayerPlaybackSource {
    Stream,
    Download,
    ExternalPlayer,
}

enum class ChimahonPlayerDownloadState(val title: String) {
    NotDownloaded("Download"),
    Queued("Queued"),
    Downloading("Downloading"),
    Downloaded("Downloaded"),
    Error("Retry download"),
}

data class ChimahonPlayerSettingsUiState(
    val playbackSpeed: Float = 1f,
    val subtitleDelaySeconds: Double = 0.0,
    val audioDelaySeconds: Double = 0.0,
    val subtitleFontScale: Float = 1f,
    val subtitleBackground: Boolean = true,
    val subtitleBorder: ChimahonPlayerSubtitleBorder = ChimahonPlayerSubtitleBorder.OutlineAndShadow,
    val subtitleRegexFiltersEnabled: Boolean = false,
    val skipIntroEnabled: Boolean = true,
    val autoPlayNextEpisode: Boolean = true,
    val screenshotIncludesSubtitles: Boolean = true,
    val preferredDecoder: ChimahonPlayerDecoder = ChimahonPlayerDecoder.Auto,
)

enum class ChimahonPlayerVideoFit(val title: String) {
    Fit("Fit"),
    Crop("Crop"),
    Stretch("Stretch"),
}

enum class ChimahonPlayerSubtitleBorder {
    None,
    Shadow,
    Outline,
    OutlineAndShadow,
}

enum class ChimahonPlayerDecoder {
    Auto,
    Hardware,
    Software,
}

enum class ChimahonPlayerSheet {
    Hosters,
    Episodes,
    SubtitleTracks,
    AudioTracks,
    QualityTracks,
    Chapters,
    PlaybackSpeed,
    Screenshot,
    More,
}

enum class ChimahonPlayerDrawer {
    Episodes,
}

enum class ChimahonPlayerPanel {
    SubtitleCueList,
    SubtitleSettings,
    SubtitleDelay,
    SubtitleRegex,
    AudioDelay,
    VideoFilters,
    VideoOcr,
}

data class ChimahonPlayerFilterUiModel(
    val id: String,
    val title: String,
    val value: Int,
    val min: Int = -100,
    val max: Int = 100,
    val neutral: Int = 0,
)

object ChimahonPlayerFilterDefaults {
    fun defaultFilters(): List<ChimahonPlayerFilterUiModel> = listOf(
        ChimahonPlayerFilterUiModel(id = "brightness", title = "Brightness", value = 0),
        ChimahonPlayerFilterUiModel(id = "contrast", title = "Contrast", value = 0),
        ChimahonPlayerFilterUiModel(id = "saturation", title = "Saturation", value = 0),
        ChimahonPlayerFilterUiModel(id = "gamma", title = "Gamma", value = 0),
        ChimahonPlayerFilterUiModel(id = "hue", title = "Hue", value = 0),
    )
}

data class ChimahonPlayerEdgeControlsUiState(
    val volume: ChimahonPlayerSideSliderUiState = ChimahonPlayerSideSliderUiState.volume(),
    val brightness: ChimahonPlayerSideSliderUiState = ChimahonPlayerSideSliderUiState.brightness(),
    val swapVolumeAndBrightness: Boolean = false,
) {
    fun sliderFor(side: ChimahonPlayerControlSide): ChimahonPlayerSideSliderUiState? {
        val volumeSide = if (swapVolumeAndBrightness) {
            ChimahonPlayerControlSide.Right
        } else {
            ChimahonPlayerControlSide.Left
        }
        val brightnessSide = if (swapVolumeAndBrightness) {
            ChimahonPlayerControlSide.Left
        } else {
            ChimahonPlayerControlSide.Right
        }
        return when (side) {
            volumeSide -> volume.takeIf { it.visible }
            brightnessSide -> brightness.takeIf { it.visible }
            else -> null
        }
    }

    val hasVisibleSlider: Boolean
        get() = volume.visible || brightness.visible
}

data class ChimahonPlayerSideSliderUiState(
    val kind: ChimahonPlayerSideSliderKind,
    val value: Float,
    val min: Float = 0f,
    val max: Float = 1f,
    val visible: Boolean = false,
    val valueLabel: String? = null,
    val label: String = kind.title,
    val boostedValue: Float? = null,
    val boostedMin: Float = 0f,
    val boostedMax: Float = 1f,
) {
    init {
        require(max > min) { "max must be greater than min" }
    }

    val fraction: Float
        get() = ((value - min) / (max - min)).coerceIn(0f, 1f)

    val boostedFraction: Float?
        get() = boostedValue
            ?.takeIf { boostedMax > boostedMin }
            ?.let { ((it - boostedMin) / (boostedMax - boostedMin)).coerceIn(0f, 1f) }

    val displayValue: String
        get() = valueLabel ?: when (kind) {
            ChimahonPlayerSideSliderKind.Volume -> "${(fraction * 100f).roundToInt()}%"
            ChimahonPlayerSideSliderKind.Brightness -> "${(value * 100f).roundToInt()}%"
        }

    companion object {
        fun volume(
            value: Float = 0f,
            max: Float = 1f,
            visible: Boolean = false,
            valueLabel: String? = null,
            boostedValue: Float? = null,
        ): ChimahonPlayerSideSliderUiState {
            val safeMax = max.coerceAtLeast(1f)
            return ChimahonPlayerSideSliderUiState(
                kind = ChimahonPlayerSideSliderKind.Volume,
                value = value,
                min = 0f,
                max = safeMax,
                visible = visible,
                valueLabel = valueLabel,
                boostedValue = boostedValue,
                boostedMin = 0f,
                boostedMax = safeMax,
            )
        }

        fun brightness(
            value: Float = 0f,
            visible: Boolean = false,
            valueLabel: String? = null,
        ): ChimahonPlayerSideSliderUiState {
            return ChimahonPlayerSideSliderUiState(
                kind = ChimahonPlayerSideSliderKind.Brightness,
                value = value,
                min = -0.75f,
                max = 1f,
                visible = visible,
                valueLabel = valueLabel,
            )
        }
    }
}

enum class ChimahonPlayerSideSliderKind(val title: String) {
    Volume("Volume"),
    Brightness("Brightness"),
}

enum class ChimahonPlayerControlSide {
    Left,
    Right,
}

data class ChimahonPlayerLookupUiState(
    val subtitleText: String? = null,
    val subtitleCues: List<ChimahonPlayerSubtitleCueUiModel> = emptyList(),
    val subtitleLookupAvailable: Boolean = true,
    val subtitleLookupActive: Boolean = false,
    val videoOcrAvailable: Boolean = true,
    val videoOcrActive: Boolean = false,
    val videoOcrBusy: Boolean = false,
    val selectedText: String? = null,
    val hint: String? = null,
) {
    val hasSubtitleText: Boolean
        get() = !subtitleText.isNullOrBlank() || subtitleCues.isNotEmpty()
}

data class ChimahonPlayerSubtitleCueUiModel(
    val id: String,
    val text: String,
    val startSeconds: Long? = null,
    val endSeconds: Long? = null,
    val selected: Boolean = false,
) {
    val timeLabel: String?
        get() = startSeconds?.let { start ->
            endSeconds?.let { end ->
                "${chimahonFormatPlayerTime(start)} - ${chimahonFormatPlayerTime(end)}"
            } ?: chimahonFormatPlayerTime(start)
        }
}

data class ChimahonPlayerActionHint(
    val action: String,
    val input: String,
    val kind: ChimahonPlayerInputKind = ChimahonPlayerInputKind.Keyboard,
    val description: String? = null,
)

enum class ChimahonPlayerInputKind(val title: String) {
    Keyboard("Keyboard"),
    Controller("Controller"),
    Remote("Remote"),
}

data class ChimahonPlayerKeyHint(
    val action: String,
    val shortcut: String,
)

data class ChimahonPlayerTransientMessage(
    val title: String,
    val detail: String? = null,
    val tone: ChimahonPlayerTone = ChimahonPlayerTone.Neutral,
)

enum class ChimahonPlayerTone {
    Neutral,
    Positive,
    Warning,
    Error,
}

data class ChimahonPlayerUiActions(
    val onBack: () -> Unit = {},
    val onToggleControls: () -> Unit = {},
    val onToggleLock: () -> Unit = {},
    val onTogglePlayback: () -> Unit = {},
    val onSeekTo: (Long) -> Unit = {},
    val onSeekBy: (Long) -> Unit = {},
    val onPreviousEpisode: () -> Unit = {},
    val onNextEpisode: () -> Unit = {},
    val onEpisodeClick: (ChimahonPlayerEpisodeUiModel) -> Unit = {},
    val onChapterClick: (ChimahonPlayerChapterUiModel) -> Unit = {},
    val onSkipSegment: (ChimahonPlayerChapterUiModel) -> Unit = {},
    val onOpenSheet: (ChimahonPlayerSheet) -> Unit = {},
    val onOpenPanel: (ChimahonPlayerPanel) -> Unit = {},
    val onDismissOverlay: () -> Unit = {},
    val onToggleFullscreen: () -> Unit = {},
    val onTogglePictureInPicture: () -> Unit = {},
    val onToggleTheaterMode: () -> Unit = {},
    val onToggleCast: () -> Unit = {},
    val onCycleVideoFit: () -> Unit = {},
    val onVideoFitChange: (ChimahonPlayerVideoFit) -> Unit = {},
    val onToggleSubtitleLookup: () -> Unit = {},
    val onCaptureVideoOcr: () -> Unit = {},
    val onOpenDrawer: (ChimahonPlayerDrawer) -> Unit = {},
    val onDismissDrawer: () -> Unit = {},
    val onVolumeChange: (Float) -> Unit = {},
    val onBrightnessChange: (Float) -> Unit = {},
    val onToggleAutoPlayNextEpisode: (Boolean) -> Unit = {},
    val onSubtitleCueClick: (ChimahonPlayerSubtitleCueUiModel) -> Unit = {},
    val onSelectHoster: (ChimahonPlayerHosterUiModel) -> Unit = {},
    val onSelectSubtitleTrack: (ChimahonPlayerTrackUiModel) -> Unit = {},
    val onSelectAudioTrack: (ChimahonPlayerTrackUiModel) -> Unit = {},
    val onSelectQuality: (ChimahonPlayerQualityUiModel) -> Unit = {},
    val onPlaybackSpeedChange: (Float) -> Unit = {},
    val onSubtitleDelayChange: (Double) -> Unit = {},
    val onAudioDelayChange: (Double) -> Unit = {},
    val onFilterChange: (ChimahonPlayerFilterUiModel, Int) -> Unit = { _, _ -> },
    val onResetFilters: () -> Unit = {},
    val onDownloadEpisode: () -> Unit = {},
    val onToggleDownloadPlayback: () -> Unit = {},
    val onOpenSource: () -> Unit = {},
    val onCopySourceUrl: () -> Unit = {},
    val onShareSourceUrl: () -> Unit = {},
    val onOpenExternalPlayer: () -> Unit = {},
    val onRetry: () -> Unit = {},
)

fun defaultDesktopPlayerKeyHints(): List<ChimahonPlayerKeyHint> = listOf(
    ChimahonPlayerKeyHint("Play / pause", "Space"),
    ChimahonPlayerKeyHint("Seek back / forward", "Left / Right"),
    ChimahonPlayerKeyHint("Previous / next episode", "Ctrl+Left / Ctrl+Right"),
    ChimahonPlayerKeyHint("Subtitle settings", "Ctrl+Shift+S"),
    ChimahonPlayerKeyHint("Audio delay", "Ctrl+Shift+Y"),
    ChimahonPlayerKeyHint("Video filters", "Ctrl+Shift+V"),
    ChimahonPlayerKeyHint("Toggle fullscreen", "F"),
)

fun defaultPlayerActionHints(): List<ChimahonPlayerActionHint> {
    return listOf(
        ChimahonPlayerActionHint("Play / pause", "Space", ChimahonPlayerInputKind.Keyboard),
        ChimahonPlayerActionHint("Seek back / forward", "Left / Right", ChimahonPlayerInputKind.Keyboard),
        ChimahonPlayerActionHint("Previous / next episode", "Ctrl+Left / Ctrl+Right", ChimahonPlayerInputKind.Keyboard),
        ChimahonPlayerActionHint("Episode drawer", "E", ChimahonPlayerInputKind.Keyboard),
        ChimahonPlayerActionHint("Subtitle lookup", "L", ChimahonPlayerInputKind.Keyboard),
        ChimahonPlayerActionHint("Video OCR", "O", ChimahonPlayerInputKind.Keyboard),
        ChimahonPlayerActionHint("Cycle fit", "A", ChimahonPlayerInputKind.Keyboard),
        ChimahonPlayerActionHint("Toggle fullscreen", "F", ChimahonPlayerInputKind.Keyboard),
        ChimahonPlayerActionHint("Play / pause", "A / X", ChimahonPlayerInputKind.Controller),
        ChimahonPlayerActionHint("Seek", "D-pad left / right", ChimahonPlayerInputKind.Controller),
        ChimahonPlayerActionHint("Episodes", "Menu", ChimahonPlayerInputKind.Controller),
        ChimahonPlayerActionHint("Back", "B / Circle", ChimahonPlayerInputKind.Controller),
    )
}

fun List<ChimahonPlayerKeyHint>.toPlayerActionHints(
    kind: ChimahonPlayerInputKind = ChimahonPlayerInputKind.Keyboard,
): List<ChimahonPlayerActionHint> {
    return map { hint ->
        ChimahonPlayerActionHint(
            action = hint.action,
            input = hint.shortcut,
            kind = kind,
        )
    }
}

fun chimahonFormatPlayerTime(totalSeconds: Long): String {
    val safeSeconds = totalSeconds.coerceAtLeast(0L)
    val hours = safeSeconds / 3600L
    val minutes = (safeSeconds % 3600L) / 60L
    val seconds = safeSeconds % 60L
    fun twoDigits(value: Long): String = if (value < 10L) "0$value" else value.toString()
    return if (hours > 0L) {
        "$hours:${twoDigits(minutes)}:${twoDigits(seconds)}"
    } else {
        "$minutes:${twoDigits(seconds)}"
    }
}
