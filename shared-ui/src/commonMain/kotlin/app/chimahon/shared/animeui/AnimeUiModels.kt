package app.chimahon.shared.animeui

import app.chimahon.shared.anime.ChimahonAnimeEntry
import app.chimahon.shared.anime.ChimahonAnimeEpisodeEntry
import app.chimahon.shared.anime.ChimahonAnimeEpisodeSortMode
import app.chimahon.shared.anime.ChimahonAnimeLibraryEntry
import app.chimahon.shared.anime.ChimahonAnimeStatus
import kotlin.math.roundToInt

data class ChimahonAnimeLibraryUiState(
    val title: String = "Anime",
    val subtitle: String? = null,
    val categories: List<ChimahonAnimeCategoryUiModel> = emptyList(),
    val entries: List<ChimahonAnimeLibraryEntryUiModel> = emptyList(),
    val settings: ChimahonAnimeLibrarySettings = ChimahonAnimeLibrarySettings(),
    val selectedAnimeIds: Set<String> = emptySet(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val errorMessage: String? = null,
) {
    val visibleCount: Int
        get() = entries.size

    val isSelectionMode: Boolean
        get() = selectedAnimeIds.isNotEmpty()
}

data class ChimahonAnimeCategoryUiModel(
    val id: String,
    val name: String,
    val count: Int = 0,
    val selected: Boolean = false,
) {
    val displayTitle: String
        get() = if (count > 0) "$name ($count)" else name
}

data class ChimahonAnimeLibrarySettings(
    val displayMode: ChimahonAnimeLibraryDisplayMode = ChimahonAnimeLibraryDisplayMode.ComfortableGrid,
    val coverRatio: ChimahonAnimeCoverRatio = ChimahonAnimeCoverRatio.Poster,
    val continueButtonMode: ChimahonAnimeContinueButtonMode = ChimahonAnimeContinueButtonMode.Started,
    val showUnreadBadges: Boolean = true,
    val showDownloadBadges: Boolean = true,
    val showLocalBadges: Boolean = true,
    val showTrackingBadges: Boolean = true,
    val showSourceBadges: Boolean = true,
    val showLanguageBadges: Boolean = true,
)

data class ChimahonAnimeLibraryEntryUiModel(
    val id: String,
    val title: String,
    val thumbnailUrl: String? = null,
    val sourceName: String? = null,
    val language: String? = null,
    val status: String? = null,
    val unseenCount: Int = 0,
    val episodeCount: Int = 0,
    val seenCount: Int = 0,
    val downloadedCount: Int = 0,
    val tracked: Boolean = false,
    val local: Boolean = false,
    val favorite: Boolean = true,
    val latestEpisodeLabel: String? = null,
    val lastWatchedLabel: String? = null,
    val nextAiringLabel: String? = null,
    val progressLabel: String? = null,
    val warningLabel: String? = null,
) {
    fun badges(settings: ChimahonAnimeLibrarySettings): List<ChimahonAnimeBadgeUiModel> {
        val badges = mutableListOf<ChimahonAnimeBadgeUiModel>()
        if (settings.showUnreadBadges && unseenCount > 0) {
            badges += ChimahonAnimeBadgeUiModel(unseenCount.toString(), ChimahonAnimeBadgeKind.Unseen)
        }
        if (settings.showDownloadBadges && downloadedCount > 0) {
            badges += ChimahonAnimeBadgeUiModel(downloadedCount.toString(), ChimahonAnimeBadgeKind.Downloaded)
        }
        if (settings.showLocalBadges && local) {
            badges += ChimahonAnimeBadgeUiModel("Local", ChimahonAnimeBadgeKind.Local)
        }
        if (settings.showTrackingBadges && tracked) {
            badges += ChimahonAnimeBadgeUiModel("Track", ChimahonAnimeBadgeKind.Tracking)
        }
        if (settings.showSourceBadges) {
            sourceName?.takeIf(String::isNotBlank)?.let {
                badges += ChimahonAnimeBadgeUiModel(it, ChimahonAnimeBadgeKind.Source)
            }
        }
        if (settings.showLanguageBadges) {
            language?.takeIf(String::isNotBlank)?.let {
                badges += ChimahonAnimeBadgeUiModel(it.uppercase(), ChimahonAnimeBadgeKind.Language)
            }
        }
        warningLabel?.takeIf(String::isNotBlank)?.let {
            badges += ChimahonAnimeBadgeUiModel(it, ChimahonAnimeBadgeKind.Warning)
        }
        return badges
    }

    fun shouldShowContinueButton(settings: ChimahonAnimeLibrarySettings): Boolean {
        return when (settings.continueButtonMode) {
            ChimahonAnimeContinueButtonMode.Never -> false
            ChimahonAnimeContinueButtonMode.Always -> true
            ChimahonAnimeContinueButtonMode.Unseen -> unseenCount > 0
            ChimahonAnimeContinueButtonMode.Started -> seenCount > 0 || !lastWatchedLabel.isNullOrBlank()
        }
    }
}

data class ChimahonAnimeDetailUiState(
    val anime: ChimahonAnimeHeaderUiModel? = null,
    val episodes: List<ChimahonAnimeEpisodeUiModel> = emptyList(),
    val visibleEpisodes: List<ChimahonAnimeEpisodeUiModel> = episodes,
    val seasons: List<ChimahonAnimeSeasonUiModel> = emptyList(),
    val relatedAnime: List<ChimahonRelatedAnimeUiModel> = emptyList(),
    val selectedEpisodeIds: Set<String> = emptySet(),
    val contentState: ChimahonAnimeContentState = when {
        anime == null -> ChimahonAnimeContentState.Empty
        else -> ChimahonAnimeContentState.Content
    },
    val refreshing: Boolean = false,
    val errorMessage: String? = null,
    val sortState: ChimahonAnimeEpisodeSortState = ChimahonAnimeEpisodeSortState(),
    val filterState: ChimahonAnimeEpisodeFilterState = ChimahonAnimeEpisodeFilterState(),
    val displayMode: ChimahonAnimeEpisodeDisplayMode = ChimahonAnimeEpisodeDisplayMode.SourceTitle,
    val episodeQuery: String = "",
    val missingEpisodeCount: Int = 0,
)

data class ChimahonAnimeHeaderUiModel(
    val id: String,
    val title: String,
    val sourceLine: String,
    val creatorLine: String? = null,
    val status: String? = null,
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val categoryLabels: List<String> = emptyList(),
    val favorite: Boolean = false,
    val trackedCount: Int = 0,
    val webUrl: String? = null,
    val nextUpdateLabel: String? = null,
    val nextAiringLabel: String? = null,
    val totalDurationLabel: String? = null,
    val totalEpisodeCount: Int = 0,
    val unseenCount: Int = 0,
    val downloadedCount: Int = 0,
    val skipIntroEnabled: Boolean = false,
)

data class ChimahonAnimeActions(
    val onNavigateUp: () -> Unit = {},
    val onRetry: () -> Unit = {},
    val onRefresh: () -> Unit = {},
    val onFilterClick: () -> Unit = {},
    val onSortChange: (ChimahonAnimeEpisodeSortState) -> Unit = {},
    val onFavoriteClick: (ChimahonAnimeHeaderUiModel) -> Unit = {},
    val onTrackingClick: (ChimahonAnimeHeaderUiModel) -> Unit = {},
    val onWebViewClick: (ChimahonAnimeHeaderUiModel) -> Unit = {},
    val onEditIntervalClick: (ChimahonAnimeHeaderUiModel) -> Unit = {},
    val onSkipIntroClick: (ChimahonAnimeHeaderUiModel) -> Unit = {},
    val onContinueWatchingClick: (ChimahonAnimeHeaderUiModel) -> Unit = {},
    val onTagClick: (String) -> Unit = {},
    val onSeasonClick: (ChimahonAnimeSeasonUiModel) -> Unit = {},
    val onRelatedAnimeClick: (ChimahonRelatedAnimeUiModel) -> Unit = {},
    val onEpisodeClick: (ChimahonAnimeEpisodeUiModel) -> Unit = {},
    val onEpisodeLongClick: (ChimahonAnimeEpisodeUiModel) -> Unit = {},
    val onEpisodeDownloadClick: (ChimahonAnimeEpisodeUiModel) -> Unit = {},
)

data class ChimahonAnimeSeasonUiModel(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val countLabel: String = "",
    val unseenCount: Int = 0,
    val selected: Boolean = false,
)

data class ChimahonRelatedAnimeUiModel(
    val id: String,
    val title: String,
    val subtitle: String? = null,
)

data class ChimahonAnimeEpisodeUiModel(
    val id: String,
    val title: String,
    val sourceTitle: String = title,
    val episodeNumber: Double? = null,
    val episodeMarker: String = episodeNumber?.trimmedAnimeNumber() ?: "?",
    val subtitle: String? = null,
    val scanlator: String? = null,
    val uploadDateLabel: String? = null,
    val durationLabel: String? = null,
    val progressLabel: String? = null,
    val seen: Boolean = false,
    val bookmarked: Boolean = false,
    val filler: Boolean = false,
    val downloadState: ChimahonAnimeDownloadUiState = ChimahonAnimeDownloadUiState.NotDownloaded,
    val downloadProgress: Int = 0,
    val sourceOrder: Long = 0L,
    val url: String = id,
) {
    fun stableLazyKey(index: Int): String {
        return listOf("anime-episode", id, sourceOrder.toString(), url, index.toString()).joinToString(":")
    }

    fun displayTitle(displayMode: ChimahonAnimeEpisodeDisplayMode): String {
        return when (displayMode) {
            ChimahonAnimeEpisodeDisplayMode.SourceTitle -> sourceTitle
            ChimahonAnimeEpisodeDisplayMode.EpisodeNumber -> episodeNumber?.let { "Episode ${it.trimmedAnimeNumber()}" } ?: sourceTitle
            ChimahonAnimeEpisodeDisplayMode.Title -> title
        }
    }

    fun metadataParts(): List<String> {
        return listOfNotNull(
            subtitle?.takeIf(String::isNotBlank),
            scanlator?.takeIf(String::isNotBlank),
            uploadDateLabel?.takeIf(String::isNotBlank),
            durationLabel?.takeIf(String::isNotBlank),
            progressLabel?.takeIf(String::isNotBlank),
        )
    }
}

data class ChimahonAnimeEpisodeSortState(
    val sort: ChimahonAnimeEpisodeSort = ChimahonAnimeEpisodeSort.Source,
    val descending: Boolean = true,
) {
    val directionTitle: String
        get() = when (sort) {
            ChimahonAnimeEpisodeSort.Source -> if (descending) "Source order" else "Reverse source order"
            ChimahonAnimeEpisodeSort.EpisodeNumber -> if (descending) "Highest first" else "Lowest first"
            ChimahonAnimeEpisodeSort.UploadDate -> if (descending) "Newest first" else "Oldest first"
            ChimahonAnimeEpisodeSort.Alphabetical -> if (descending) "Z-A" else "A-Z"
        }

    fun toggledDirection(): ChimahonAnimeEpisodeSortState {
        return copy(descending = !descending)
    }

    fun withSort(nextSort: ChimahonAnimeEpisodeSort): ChimahonAnimeEpisodeSortState {
        return copy(sort = nextSort)
    }
}

data class ChimahonAnimeEpisodeFilterState(
    val unseen: ChimahonAnimeFilterMode = ChimahonAnimeFilterMode.Disabled,
    val downloaded: ChimahonAnimeFilterMode = ChimahonAnimeFilterMode.Disabled,
    val bookmarked: ChimahonAnimeFilterMode = ChimahonAnimeFilterMode.Disabled,
    val filler: ChimahonAnimeFilterMode = ChimahonAnimeFilterMode.Disabled,
) {
    val activeCount: Int
        get() = listOf(unseen, downloaded, bookmarked, filler).count { it != ChimahonAnimeFilterMode.Disabled }

    val hasActiveFilters: Boolean
        get() = activeCount > 0
}

data class ChimahonAnimeUpdatesUiState(
    val title: String = "Anime updates",
    val subtitle: String? = null,
    val rows: List<ChimahonAnimeUpdateRowUiModel> = emptyList(),
    val selectedRowIds: Set<String> = emptySet(),
    val refreshing: Boolean = false,
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val lastUpdatedLabel: String? = null,
) {
    val selectedCount: Int
        get() = selectedRowIds.size
}

data class ChimahonAnimeHistoryUiState(
    val title: String = "Anime history",
    val subtitle: String? = null,
    val rows: List<ChimahonAnimeHistoryRowUiModel> = emptyList(),
    val selectedRowIds: Set<String> = emptySet(),
    val loading: Boolean = false,
    val errorMessage: String? = null,
) {
    val selectedCount: Int
        get() = selectedRowIds.size
}

data class ChimahonAnimeUpdateRowUiModel(
    val id: String,
    val animeTitle: String,
    val subtitle: String? = null,
    val sourceName: String? = null,
    val updateHeader: String? = null,
    val episodes: List<ChimahonAnimeEpisodeUiModel> = emptyList(),
    val selected: Boolean = false,
) {
    val episodeCountLabel: String
        get() = when (episodes.size) {
            0 -> "No episodes"
            1 -> "1 episode"
            else -> "${episodes.size} episodes"
        }
}

data class ChimahonAnimeHistoryRowUiModel(
    val id: String,
    val animeTitle: String,
    val episodeTitle: String,
    val watchedAtLabel: String? = null,
    val progressLabel: String? = null,
    val sourceName: String? = null,
    val selected: Boolean = false,
)

data class ChimahonAnimePlayerUiState(
    val animeTitle: String = "",
    val episodeTitle: String = "",
    val sourceName: String? = null,
    val sourceUrl: String? = null,
    val streamUrl: String? = null,
    val selectedHoster: String? = null,
    val selectedQuality: String? = null,
    val selectedAudioTrack: String? = null,
    val playbackSpeed: Float = 1f,
    val paused: Boolean = true,
    val loading: Boolean = false,
    val loadingEpisode: Boolean = false,
    val controlsVisible: Boolean = true,
    val controlsLocked: Boolean = false,
    val fullscreen: Boolean = false,
    val pictureInPictureAvailable: Boolean = false,
    val pictureInPicture: Boolean = false,
    val theaterMode: Boolean = false,
    val hasPreviousEpisode: Boolean = false,
    val hasNextEpisode: Boolean = false,
    val positionSeconds: Long = 0L,
    val durationSeconds: Long = 0L,
    val bufferedSeconds: Long = 0L,
    val progressFraction: Float = 0f,
    val bufferedFraction: Float = 0f,
    val positionLabel: String = "0:00",
    val durationLabel: String = "0:00",
    val subtitleText: String? = null,
    val hosters: List<ChimahonAnimePlayerHosterUiModel> = emptyList(),
    val qualities: List<ChimahonAnimePlayerVideoQualityUiModel> = emptyList(),
    val subtitleTracks: List<ChimahonAnimePlayerTrackUiModel> = emptyList(),
    val selectedSubtitleTrackIds: Set<String> = emptySet(),
    val audioTracks: List<ChimahonAnimePlayerTrackUiModel> = emptyList(),
    val selectedAudioTrackId: String? = null,
    val selectedHosterId: String? = null,
    val selectedQualityId: String? = null,
    val skipSegments: List<ChimahonAnimePlayerSkipSegmentUiModel> = emptyList(),
    val stream: ChimahonAnimePlayerStreamUiState = ChimahonAnimePlayerStreamUiState(),
    val errorMessage: String? = null,
    val actionSurface: ChimahonAnimePlayerActionSurface = ChimahonAnimePlayerActionSurface.default(),
) {
    val selectedHosterModel: ChimahonAnimePlayerHosterUiModel?
        get() = hosters.firstOrNull { it.id == selectedHosterId }

    val selectedQualityModel: ChimahonAnimePlayerVideoQualityUiModel?
        get() = qualities.firstOrNull { it.id == selectedQualityId }

    val selectedAudioTrackModel: ChimahonAnimePlayerTrackUiModel?
        get() = audioTracks.firstOrNull { it.id == selectedAudioTrackId }

    val selectedSubtitleTracks: List<ChimahonAnimePlayerTrackUiModel>
        get() = subtitleTracks.filter { it.id in selectedSubtitleTrackIds }

    val activeSkipSegment: ChimahonAnimePlayerSkipSegmentUiModel?
        get() = skipSegments.firstOrNull { it.contains(positionSeconds) }
}

data class ChimahonAnimePlayerCallbacks(
    val onAction: (ChimahonAnimePlayerActionId) -> Unit = {},
    val onSeekFractionChange: (Float) -> Unit = {},
    val onSeekToSeconds: (Long) -> Unit = {},
    val onSelectHoster: (ChimahonAnimePlayerHosterUiModel) -> Unit = {},
    val onSelectQuality: (ChimahonAnimePlayerVideoQualityUiModel) -> Unit = {},
    val onSelectSubtitleTrack: (ChimahonAnimePlayerTrackUiModel) -> Unit = {},
    val onSelectAudioTrack: (ChimahonAnimePlayerTrackUiModel) -> Unit = {},
    val onSkipSegment: (ChimahonAnimePlayerSkipSegmentUiModel) -> Unit = {},
    val onPlaybackSpeedChange: (Float) -> Unit = {},
    val onTogglePictureInPicture: () -> Unit = {},
    val onToggleFullscreen: () -> Unit = {},
    val onToggleTheaterMode: () -> Unit = {},
    val onRetry: () -> Unit = {},
    val onDownload: () -> Unit = {},
    val onToggleDownloadPlayback: () -> Unit = {},
    val onOpenSource: () -> Unit = {},
    val onCopySource: () -> Unit = {},
    val onShareSource: () -> Unit = {},
)

data class ChimahonAnimePlayerHosterUiModel(
    val id: String,
    val title: String,
    val url: String? = null,
    val status: ChimahonAnimePlayerHosterStatus = ChimahonAnimePlayerHosterStatus.Idle,
    val videoCount: Int = 0,
    val preferred: Boolean = false,
    val errorMessage: String? = null,
    val internalKey: String = id,
) {
    val statusLabel: String?
        get() = when (status) {
            ChimahonAnimePlayerHosterStatus.Idle -> null
            ChimahonAnimePlayerHosterStatus.Loading -> "Loading"
            ChimahonAnimePlayerHosterStatus.Ready -> if (videoCount > 0) "$videoCount stream(s)" else "Ready"
            ChimahonAnimePlayerHosterStatus.Empty -> "No streams"
            ChimahonAnimePlayerHosterStatus.Error -> errorMessage ?: "Error"
        }
}

data class ChimahonAnimePlayerVideoQualityUiModel(
    val id: String,
    val title: String,
    val detail: String? = null,
    val hosterId: String? = null,
    val hosterTitle: String? = null,
    val resolution: Int? = null,
    val bitrate: Int? = null,
    val preferred: Boolean = false,
    val initialized: Boolean = true,
    val status: ChimahonAnimePlayerVideoStatus = ChimahonAnimePlayerVideoStatus.Ready,
    val sourceUrl: String? = null,
    val streamUrl: String? = null,
    val errorMessage: String? = null,
    val internalKey: String = id,
) {
    val statusLabel: String?
        get() = when (status) {
            ChimahonAnimePlayerVideoStatus.Queued -> "Queued"
            ChimahonAnimePlayerVideoStatus.Loading -> "Loading"
            ChimahonAnimePlayerVideoStatus.Ready -> null
            ChimahonAnimePlayerVideoStatus.Error -> errorMessage ?: "Error"
        }
}

data class ChimahonAnimePlayerTrackUiModel(
    val id: String,
    val title: String,
    val language: String? = null,
    val url: String? = null,
    val external: Boolean = false,
    val delaySeconds: Double = 0.0,
    val selectedByDefault: Boolean = false,
    val internalKey: String = id,
)

data class ChimahonAnimePlayerSkipSegmentUiModel(
    val id: String,
    val title: String,
    val startSeconds: Long,
    val endSeconds: Long? = null,
    val kind: ChimahonAnimePlayerSkipKind = ChimahonAnimePlayerSkipKind.Other,
    val source: ChimahonAnimePlayerSkipSource = ChimahonAnimePlayerSkipSource.Source,
    val autoSkip: Boolean = false,
) {
    val actionLabel: String
        get() = when (kind) {
            ChimahonAnimePlayerSkipKind.Opening -> "Skip intro"
            ChimahonAnimePlayerSkipKind.Ending -> "Skip outro"
            ChimahonAnimePlayerSkipKind.Recap -> "Skip recap"
            ChimahonAnimePlayerSkipKind.MixedOpening -> "Skip intro"
            ChimahonAnimePlayerSkipKind.Other -> "Skip"
        }

    fun contains(positionSeconds: Long): Boolean {
        return positionSeconds >= startSeconds && (endSeconds == null || positionSeconds < endSeconds)
    }
}

data class ChimahonAnimePlayerStreamUiState(
    val playbackSource: ChimahonAnimePlayerPlaybackSource = ChimahonAnimePlayerPlaybackSource.Stream,
    val status: ChimahonAnimePlayerStreamStatus = ChimahonAnimePlayerStreamStatus.Idle,
    val downloadState: ChimahonAnimeDownloadUiState = ChimahonAnimeDownloadUiState.NotDownloaded,
    val downloadProgress: Int = 0,
    val sourceUrl: String? = null,
    val streamUrl: String? = null,
    val message: String? = null,
    val networkLabel: String? = null,
    val retryable: Boolean = false,
    val externalPlayerAvailable: Boolean = false,
) {
    val statusLabel: String?
        get() = message ?: when (status) {
            ChimahonAnimePlayerStreamStatus.Idle -> null
            ChimahonAnimePlayerStreamStatus.LoadingHosters -> "Loading hosters"
            ChimahonAnimePlayerStreamStatus.LoadingVideo -> "Loading stream"
            ChimahonAnimePlayerStreamStatus.Ready -> "Ready"
            ChimahonAnimePlayerStreamStatus.Buffering -> "Buffering"
            ChimahonAnimePlayerStreamStatus.Playing -> null
            ChimahonAnimePlayerStreamStatus.Ended -> "Finished"
            ChimahonAnimePlayerStreamStatus.Error -> "Playback error"
        }
}

data class ChimahonAnimePlayerActionSurface(
    val primaryActions: List<ChimahonAnimePlayerAction> = emptyList(),
    val secondaryActions: List<ChimahonAnimePlayerAction> = emptyList(),
) {
    companion object {
        fun default(): ChimahonAnimePlayerActionSurface {
            return ChimahonAnimePlayerActionSurface(
                primaryActions = listOf(
                    ChimahonAnimePlayerAction(ChimahonAnimePlayerActionId.Playlist, "Playlist"),
                    ChimahonAnimePlayerAction(ChimahonAnimePlayerActionId.Subtitles, "Subtitles"),
                    ChimahonAnimePlayerAction(ChimahonAnimePlayerActionId.Audio, "Audio"),
                    ChimahonAnimePlayerAction(ChimahonAnimePlayerActionId.Quality, "Quality"),
                    ChimahonAnimePlayerAction(ChimahonAnimePlayerActionId.Speed, "Speed"),
                ),
                secondaryActions = listOf(
                    ChimahonAnimePlayerAction(ChimahonAnimePlayerActionId.VideoFilters, "Filters"),
                    ChimahonAnimePlayerAction(ChimahonAnimePlayerActionId.SleepTimer, "Timer"),
                    ChimahonAnimePlayerAction(ChimahonAnimePlayerActionId.OcrLookup, "OCR"),
                    ChimahonAnimePlayerAction(ChimahonAnimePlayerActionId.Screenshot, "Shot"),
                    ChimahonAnimePlayerAction(ChimahonAnimePlayerActionId.Cast, "Cast"),
                    ChimahonAnimePlayerAction(ChimahonAnimePlayerActionId.Settings, "Settings"),
                ),
            )
        }
    }
}

data class ChimahonAnimePlayerAction(
    val id: ChimahonAnimePlayerActionId,
    val label: String,
    val enabled: Boolean = true,
    val selected: Boolean = false,
    val valueLabel: String? = null,
)

data class ChimahonAnimeBadgeUiModel(
    val label: String,
    val kind: ChimahonAnimeBadgeKind = ChimahonAnimeBadgeKind.Info,
)

enum class ChimahonAnimeContentState {
    Loading,
    Error,
    Empty,
    Content,
}

enum class ChimahonAnimeLibraryDisplayMode {
    CompactGrid,
    ComfortableGrid,
    CoverOnlyGrid,
    List,
}

enum class ChimahonAnimeCoverRatio(val ratio: Float) {
    Poster(2f / 3f),
    Square(1f),
    Widescreen(16f / 9f),
}

enum class ChimahonAnimeContinueButtonMode {
    Never,
    Unseen,
    Started,
    Always,
}

enum class ChimahonAnimeBadgeKind {
    Info,
    Unseen,
    Downloaded,
    Local,
    Tracking,
    Source,
    Language,
    Status,
    Warning,
}

enum class ChimahonAnimeDownloadUiState(val title: String) {
    NotDownloaded("Download"),
    Queued("Queued"),
    Downloading("Downloading"),
    Downloaded("Downloaded"),
    Error("Retry"),
}

enum class ChimahonAnimePlayerHosterStatus {
    Idle,
    Loading,
    Ready,
    Empty,
    Error,
}

enum class ChimahonAnimePlayerVideoStatus {
    Queued,
    Loading,
    Ready,
    Error,
}

enum class ChimahonAnimePlayerSkipKind {
    Opening,
    Ending,
    Recap,
    MixedOpening,
    Other,
}

enum class ChimahonAnimePlayerSkipSource {
    Source,
    AniSkip,
    Local,
    Manual,
}

enum class ChimahonAnimePlayerStreamStatus {
    Idle,
    LoadingHosters,
    LoadingVideo,
    Ready,
    Buffering,
    Playing,
    Ended,
    Error,
}

enum class ChimahonAnimePlayerPlaybackSource {
    Stream,
    Download,
    ExternalPlayer,
}

enum class ChimahonAnimePlayerActionId {
    Back,
    PlayPause,
    SeekBackward,
    SeekForward,
    PreviousEpisode,
    NextEpisode,
    Playlist,
    Subtitles,
    Audio,
    Quality,
    Speed,
    VideoFilters,
    SleepTimer,
    OcrLookup,
    Screenshot,
    Cast,
    LockControls,
    Settings,
    Fullscreen,
}

enum class ChimahonAnimeEpisodeDisplayMode {
    SourceTitle,
    EpisodeNumber,
    Title,
}

enum class ChimahonAnimeEpisodeSort(val title: String) {
    Source("Source order"),
    EpisodeNumber("Episode number"),
    UploadDate("Upload date"),
    Alphabetical("Alphabetical"),
}

enum class ChimahonAnimeFilterMode {
    Disabled,
    Include,
    Exclude,
}

fun ChimahonAnimeLibraryEntry.toAnimeLibraryEntryUiModel(): ChimahonAnimeLibraryEntryUiModel {
    return ChimahonAnimeLibraryEntryUiModel(
        id = id.toString(),
        title = anime.title,
        thumbnailUrl = anime.thumbnailUrl,
        sourceName = sourceName,
        language = sourceLanguage,
        status = anime.status.displayLabel(),
        unseenCount = unseenEpisodeCount,
        episodeCount = totalEpisodeCount,
        seenCount = seenEpisodeCount,
        downloadedCount = downloadedEpisodeCount,
        local = isLocal,
        latestEpisodeLabel = latestUpload.takeIf { it > 0L }?.let { "Latest episode" },
        lastWatchedLabel = lastSeen.takeIf { it > 0L }?.let { "Watched" },
        nextAiringLabel = anime.nextEpisodeToAir.takeIf { it > 0 }?.let { "Ep $it airing" },
        progressLabel = "$seenEpisodeCount / $totalEpisodeCount",
    )
}

fun ChimahonAnimeEntry.toAnimeHeaderUiModel(
    sourceName: String? = null,
    episodes: List<ChimahonAnimeEpisodeEntry> = emptyList(),
    downloadedCount: Int = 0,
    trackedCount: Int = 0,
    categoryLabels: List<String> = emptyList(),
): ChimahonAnimeHeaderUiModel {
    return ChimahonAnimeHeaderUiModel(
        id = id.toString(),
        title = title,
        sourceLine = listOfNotNull(sourceName, status.displayLabel()).joinToString(" | ").ifBlank { status.displayLabel() },
        creatorLine = listOfNotNull(author, artist).distinct().joinToString(" / ").takeIf { it.isNotBlank() },
        status = status.displayLabel(),
        description = description,
        tags = genres,
        categoryLabels = categoryLabels,
        favorite = favorite,
        trackedCount = trackedCount,
        webUrl = url,
        nextUpdateLabel = expectedNextUpdate?.let { "Scheduled" },
        nextAiringLabel = nextEpisodeToAir.takeIf { it > 0 }?.let { "Episode $it" },
        totalEpisodeCount = episodes.size,
        unseenCount = episodes.count { !it.seen },
        downloadedCount = downloadedCount,
        skipIntroEnabled = !skipIntroDisabled && skipIntroLength > 0,
    )
}

fun ChimahonAnimeEpisodeEntry.toAnimeEpisodeUiModel(
    downloadState: ChimahonAnimeDownloadUiState = ChimahonAnimeDownloadUiState.NotDownloaded,
    downloadProgress: Int = 0,
): ChimahonAnimeEpisodeUiModel {
    return ChimahonAnimeEpisodeUiModel(
        id = id.toString(),
        title = name,
        sourceTitle = name,
        episodeNumber = episodeNumber.takeIf { it >= 0.0 },
        subtitle = summary,
        scanlator = scanlator,
        uploadDateLabel = dateUpload.takeIf { it > 0L }?.let { "Uploaded" },
        durationLabel = totalSeconds.takeIf { it > 0L }?.let { seconds -> "${seconds / 60}m" },
        progressLabel = progressFraction.takeIf { it > 0f }?.let { "${(it * 100f).roundToInt()}%" },
        seen = seen,
        bookmarked = bookmark,
        filler = fillermark,
        downloadState = downloadState,
        downloadProgress = downloadProgress,
        sourceOrder = sourceOrder,
        url = url,
    )
}

fun List<ChimahonAnimeEpisodeUiModel>.sortedForAnimeUi(
    state: ChimahonAnimeEpisodeSortState,
): List<ChimahonAnimeEpisodeUiModel> {
    val comparator = when (state.sort) {
        ChimahonAnimeEpisodeSort.Source -> compareBy<ChimahonAnimeEpisodeUiModel> { it.sourceOrder }
            .thenBy { it.url }
            .thenBy { it.id }
        ChimahonAnimeEpisodeSort.EpisodeNumber -> compareBy<ChimahonAnimeEpisodeUiModel> {
            it.episodeNumber ?: Double.MAX_VALUE
        }.thenBy { it.sourceOrder }.thenBy { it.id }
        ChimahonAnimeEpisodeSort.UploadDate -> compareBy<ChimahonAnimeEpisodeUiModel> { it.uploadDateLabel ?: "" }
            .thenBy { it.sourceOrder }
            .thenBy { it.id }
        ChimahonAnimeEpisodeSort.Alphabetical -> compareBy<ChimahonAnimeEpisodeUiModel> { it.title.lowercase() }
            .thenBy { it.sourceOrder }
            .thenBy { it.id }
    }
    val sorted = sortedWith(comparator)
    return when {
        state.sort == ChimahonAnimeEpisodeSort.Source && state.descending -> sorted
        state.sort == ChimahonAnimeEpisodeSort.Source -> sorted.asReversed()
        state.descending -> sorted.asReversed()
        else -> sorted
    }
}

fun ChimahonAnimeEpisodeSortMode.toAnimeUiSort(): ChimahonAnimeEpisodeSort {
    return when (this) {
        ChimahonAnimeEpisodeSortMode.Source -> ChimahonAnimeEpisodeSort.Source
        ChimahonAnimeEpisodeSortMode.EpisodeNumber -> ChimahonAnimeEpisodeSort.EpisodeNumber
        ChimahonAnimeEpisodeSortMode.UploadDate -> ChimahonAnimeEpisodeSort.UploadDate
        ChimahonAnimeEpisodeSortMode.Alphabetical -> ChimahonAnimeEpisodeSort.Alphabetical
    }
}

fun ChimahonAnimeStatus.displayLabel(): String {
    return when (this) {
        ChimahonAnimeStatus.Unknown -> "Unknown"
        ChimahonAnimeStatus.Ongoing -> "Ongoing"
        ChimahonAnimeStatus.Completed -> "Completed"
        ChimahonAnimeStatus.Licensed -> "Licensed"
        ChimahonAnimeStatus.PublishingFinished -> "Finished"
        ChimahonAnimeStatus.Cancelled -> "Cancelled"
        ChimahonAnimeStatus.OnHiatus -> "On hiatus"
    }
}

private fun Double.trimmedAnimeNumber(): String {
    val intValue = toInt()
    return if (this == intValue.toDouble()) intValue.toString() else toString().trimEnd('0').trimEnd('.')
}
