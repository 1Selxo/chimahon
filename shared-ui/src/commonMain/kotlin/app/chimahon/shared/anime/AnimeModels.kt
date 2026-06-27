package app.chimahon.shared.anime

data class ChimahonAnimeEntry(
    val id: Long,
    val sourceId: Long,
    val url: String,
    val title: String,
    val artist: String? = null,
    val author: String? = null,
    val thumbnailUrl: String? = null,
    val backgroundUrl: String? = null,
    val description: String? = null,
    val genres: List<String> = emptyList(),
    val status: ChimahonAnimeStatus = ChimahonAnimeStatus.Unknown,
    val favorite: Boolean = false,
    val initialized: Boolean = false,
    val dateAdded: Long = 0L,
    val lastUpdate: Long = 0L,
    val nextUpdate: Long = 0L,
    val fetchInterval: Int = 0,
    val viewerFlags: Long = 0L,
    val episodeFlags: Long = 0L,
    val seasonFlags: Long = 0L,
    val seasonNumber: Double = 1.0,
    val seasonSourceOrder: Long = 0L,
    val coverLastModified: Long = 0L,
    val backgroundLastModified: Long = 0L,
    val updateStrategy: ChimahonAnimeUpdateStrategy = ChimahonAnimeUpdateStrategy.Always,
    val fetchType: ChimahonAnimeFetchType = ChimahonAnimeFetchType.Episodes,
    val parentId: Long? = null,
    val lastModifiedAt: Long = 0L,
    val favoriteModifiedAt: Long? = null,
    val version: Long = 0L,
) {
    val expectedNextUpdate: Long?
        get() = nextUpdate.takeIf { it > 0L }

    val skipIntroLength: Int
        get() = (viewerFlags and ANIME_INTRO_MASK).toInt()

    val skipIntroDisabled: Boolean
        get() = (viewerFlags and ANIME_INTRO_DISABLE_MASK) == ANIME_INTRO_DISABLE_MASK

    val nextEpisodeToAir: Int
        get() = ((viewerFlags and ANIME_AIRING_EPISODE_MASK) / HEX_SHIFT_TWO_BYTES).toInt()

    val nextEpisodeAiringAt: Long
        get() = (viewerFlags and ANIME_AIRING_TIME_MASK) / HEX_SHIFT_SIX_BYTES

    val episodeSortDescending: Boolean
        get() = (episodeFlags and EPISODE_SORT_DIR_MASK) == EPISODE_SORT_DESC

    val episodeSortMode: ChimahonAnimeEpisodeSortMode
        get() = ChimahonAnimeEpisodeSortMode.fromFlag(episodeFlags)

    val episodeDisplayMode: ChimahonAnimeEpisodeDisplayMode
        get() = ChimahonAnimeEpisodeDisplayMode.fromFlag(episodeFlags)

    val unseenFilter: ChimahonAnimeTriState
        get() = ChimahonAnimeTriState.fromFlags(
            value = episodeFlags,
            mask = EPISODE_UNSEEN_MASK,
            enabledFlag = EPISODE_SHOW_UNSEEN,
            disabledFlag = EPISODE_SHOW_SEEN,
        )

    val downloadedFilter: ChimahonAnimeTriState
        get() = ChimahonAnimeTriState.fromFlags(
            value = episodeFlags,
            mask = EPISODE_DOWNLOADED_MASK,
            enabledFlag = EPISODE_SHOW_DOWNLOADED,
            disabledFlag = EPISODE_SHOW_NOT_DOWNLOADED,
        )

    val bookmarkedFilter: ChimahonAnimeTriState
        get() = ChimahonAnimeTriState.fromFlags(
            value = episodeFlags,
            mask = EPISODE_BOOKMARKED_MASK,
            enabledFlag = EPISODE_SHOW_BOOKMARKED,
            disabledFlag = EPISODE_SHOW_NOT_BOOKMARKED,
        )

    val fillermarkedFilter: ChimahonAnimeTriState
        get() = ChimahonAnimeTriState.fromFlags(
            value = episodeFlags,
            mask = EPISODE_FILLERMARKED_MASK,
            enabledFlag = EPISODE_SHOW_FILLERMARKED,
            disabledFlag = EPISODE_SHOW_NOT_FILLERMARKED,
        )
}

data class ChimahonAnimeEpisodeEntry(
    val id: Long,
    val animeId: Long,
    val url: String,
    val name: String,
    val episodeNumber: Double = -1.0,
    val scanlator: String? = null,
    val seen: Boolean = false,
    val bookmark: Boolean = false,
    val fillermark: Boolean = false,
    val summary: String? = null,
    val previewUrl: String? = null,
    val lastSecondSeen: Long = 0L,
    val totalSeconds: Long = 0L,
    val dateFetch: Long = 0L,
    val dateUpload: Long = -1L,
    val sourceOrder: Long = 0L,
    val lastModifiedAt: Long = 0L,
    val version: Long = 1L,
) {
    val isRecognizedNumber: Boolean
        get() = episodeNumber >= 0.0

    val isStarted: Boolean
        get() = seen || lastSecondSeen > 0L

    val progressFraction: Float
        get() = if (totalSeconds > 0L) {
            (lastSecondSeen.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

    fun copySourceFieldsFrom(other: ChimahonAnimeEpisodeEntry): ChimahonAnimeEpisodeEntry {
        return copy(
            name = other.name,
            url = other.url,
            dateUpload = other.dateUpload,
            episodeNumber = other.episodeNumber,
            scanlator = other.scanlator?.ifBlank { null },
            summary = other.summary?.ifBlank { null },
            previewUrl = other.previewUrl?.ifBlank { null },
        )
    }
}

enum class ChimahonAnimeStatus(val upstreamValue: Long) {
    Unknown(0L),
    Ongoing(1L),
    Completed(2L),
    Licensed(3L),
    PublishingFinished(4L),
    Cancelled(5L),
    OnHiatus(6L),
    ;

    companion object {
        fun fromUpstreamValue(value: Long): ChimahonAnimeStatus {
            return entries.firstOrNull { it.upstreamValue == value } ?: Unknown
        }
    }
}

enum class ChimahonAnimeUpdateStrategy {
    Always,
    OnlyFetchOnce,
}

enum class ChimahonAnimeFetchType {
    Episodes,
    Seasons,
}

enum class ChimahonAnimeTriState {
    Disabled,
    Include,
    Exclude,
    ;

    companion object {
        fun fromFlags(
            value: Long,
            mask: Long,
            enabledFlag: Long,
            disabledFlag: Long,
        ): ChimahonAnimeTriState {
            return when (value and mask) {
                enabledFlag -> Include
                disabledFlag -> Exclude
                else -> Disabled
            }
        }
    }
}

enum class ChimahonAnimeEpisodeSortMode(val flag: Long) {
    Source(EPISODE_SORTING_SOURCE),
    EpisodeNumber(EPISODE_SORTING_NUMBER),
    UploadDate(EPISODE_SORTING_UPLOAD_DATE),
    Alphabetical(EPISODE_SORTING_ALPHABET),
    ;

    companion object {
        fun fromFlag(flags: Long): ChimahonAnimeEpisodeSortMode {
            return entries.firstOrNull { (flags and EPISODE_SORTING_MASK) == it.flag } ?: Source
        }
    }
}

enum class ChimahonAnimeEpisodeDisplayMode(val flag: Long) {
    Name(EPISODE_DISPLAY_NAME),
    Number(EPISODE_DISPLAY_NUMBER),
    ;

    companion object {
        fun fromFlag(flags: Long): ChimahonAnimeEpisodeDisplayMode {
            return entries.firstOrNull { (flags and EPISODE_DISPLAY_MASK) == it.flag } ?: Name
        }
    }
}

const val CHIMAHON_ANIME_UNCATEGORIZED_ID: Long = 0L

const val EPISODE_SORT_DESC: Long = 0x00000000L
const val EPISODE_SORT_ASC: Long = 0x00000001L
const val EPISODE_SORT_DIR_MASK: Long = 0x00000001L

const val EPISODE_SHOW_UNSEEN: Long = 0x00000002L
const val EPISODE_SHOW_SEEN: Long = 0x00000004L
const val EPISODE_UNSEEN_MASK: Long = 0x00000006L

const val EPISODE_SHOW_DOWNLOADED: Long = 0x00000008L
const val EPISODE_SHOW_NOT_DOWNLOADED: Long = 0x00000010L
const val EPISODE_DOWNLOADED_MASK: Long = 0x00000018L

const val EPISODE_SHOW_BOOKMARKED: Long = 0x00000020L
const val EPISODE_SHOW_NOT_BOOKMARKED: Long = 0x00000040L
const val EPISODE_BOOKMARKED_MASK: Long = 0x00000060L

const val EPISODE_SHOW_FILLERMARKED: Long = 0x00000080L
const val EPISODE_SHOW_NOT_FILLERMARKED: Long = 0x00000100L
const val EPISODE_FILLERMARKED_MASK: Long = 0x00000180L

const val EPISODE_SORTING_SOURCE: Long = 0x00000000L
const val EPISODE_SORTING_NUMBER: Long = 0x00000100L
const val EPISODE_SORTING_UPLOAD_DATE: Long = 0x00000200L
const val EPISODE_SORTING_ALPHABET: Long = 0x00000300L
const val EPISODE_SORTING_MASK: Long = 0x00000300L

const val EPISODE_DISPLAY_NAME: Long = 0x00000000L
const val EPISODE_DISPLAY_NUMBER: Long = 0x00100000L
const val EPISODE_DISPLAY_MASK: Long = 0x00100000L

private const val ANIME_INTRO_MASK: Long = 0x0000000000000FFL
private const val ANIME_AIRING_EPISODE_MASK: Long = 0x000000000FFFF00L
private const val ANIME_AIRING_TIME_MASK: Long = 0x0FFFFFFFF000000L
private const val ANIME_INTRO_DISABLE_MASK: Long = 0x100000000000000L
private const val HEX_SHIFT_TWO_BYTES: Long = 0x100L
private const val HEX_SHIFT_SIX_BYTES: Long = 0x1000000L
