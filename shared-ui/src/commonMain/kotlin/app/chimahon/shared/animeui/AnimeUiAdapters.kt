package app.chimahon.shared.animeui

import app.chimahon.shared.ChimahonAnimeLibrarySettings as SavedAnimeLibrarySettings
import app.chimahon.shared.ChimahonFilterMode
import app.chimahon.shared.ChimahonLibraryDisplayMode
import app.chimahon.shared.ChimahonLibraryGroup
import app.chimahon.shared.ChimahonLibrarySort
import app.chimahon.shared.anime.CHIMAHON_ANIME_UNCATEGORIZED_ID
import app.chimahon.shared.anime.ChimahonAnimeCategory
import app.chimahon.shared.anime.ChimahonAnimeEntry
import app.chimahon.shared.anime.ChimahonAnimeEpisodeDisplayMode as DomainAnimeEpisodeDisplayMode
import app.chimahon.shared.anime.ChimahonAnimeEpisodeEntry
import app.chimahon.shared.anime.ChimahonAnimeEpisodeSortMode
import app.chimahon.shared.anime.ChimahonAnimeFetchType
import app.chimahon.shared.anime.ChimahonAnimeLibraryData
import app.chimahon.shared.anime.ChimahonAnimeLibraryEntry
import app.chimahon.shared.anime.ChimahonAnimeLibraryFilter
import app.chimahon.shared.anime.ChimahonAnimeStatus
import app.chimahon.shared.anime.ChimahonAnimeTriState
import app.chimahon.shared.anime.ChimahonAnimeUpdateStrategy
import app.chimahon.shared.anime.applyAnimeLibraryFilter
import kotlin.math.roundToInt

private const val AllAnimeCategoryId = "all"

/**
 * Adapts shared anime library data into the richer anime UI state used by the KMP surfaces.
 */
fun ChimahonAnimeLibraryData.toAnimeLibraryUiState(
    settings: SavedAnimeLibrarySettings = SavedAnimeLibrarySettings(),
    query: String = "",
    selectedCategoryId: String? = null,
    selectedAnimeIds: Set<String> = emptySet(),
    trackedAnimeIds: Set<Long> = emptySet(),
    loading: Boolean = false,
    refreshing: Boolean = false,
    errorMessage: String? = null,
    title: String = "Anime",
): ChimahonAnimeLibraryUiState {
    val filter = settings.toAnimeLibraryFilter(query)
    val filteredEntries = entries
        .applyAnimeLibraryFilter(filter)
        .filterByTracking(settings.trackedFilter, trackedAnimeIds)
    val buckets = toAnimeLibraryCategoryBuckets(settings, filteredEntries, trackedAnimeIds)
    val effectiveCategoryId = buckets.effectiveSelectedCategoryId(selectedCategoryId, settings)
    val visibleEntries = buckets
        .firstOrNull { it.id == effectiveCategoryId }
        ?.entries
        ?: filteredEntries

    return ChimahonAnimeLibraryUiState(
        title = title,
        subtitle = animeLibrarySubtitle(
            visibleCount = visibleEntries.size,
            filteredCount = filteredEntries.size,
            totalCount = totalAnimeCount,
            downloadedOnly = downloadedOnly,
        ),
        categories = if (settings.showCategoryTabs) {
            buckets.map { bucket ->
                ChimahonAnimeCategoryUiModel(
                    id = bucket.id,
                    name = bucket.name,
                    count = bucket.entries.size.takeIf { settings.showCategoryItemCount } ?: 0,
                    selected = bucket.id == effectiveCategoryId,
                )
            }
        } else {
            emptyList()
        },
        entries = visibleEntries
            .sortedForAnimeLibraryUi(settings)
            .map { entry ->
                entry.toAnimeLibraryEntryUiModel(
                    settings = settings,
                    tracked = entry.id in trackedAnimeIds,
                )
            },
        settings = settings.toAnimeUiLibrarySettings(),
        selectedAnimeIds = selectedAnimeIds,
        loading = loading,
        refreshing = refreshing,
        errorMessage = errorMessage,
    )
}

fun SavedAnimeLibrarySettings.toAnimeUiLibrarySettings(): ChimahonAnimeLibrarySettings {
    return ChimahonAnimeLibrarySettings(
        displayMode = displayMode.toAnimeUiDisplayMode(),
        coverRatio = displayMode.toAnimeUiCoverRatio(),
        continueButtonMode = if (showContinueWatchingButtons) {
            ChimahonAnimeContinueButtonMode.Started
        } else {
            ChimahonAnimeContinueButtonMode.Never
        },
        showUnreadBadges = showUnseenBadges,
        showDownloadBadges = showDownloadedBadges,
        showLocalBadges = showLocalBadges,
        showTrackingBadges = true,
        showSourceBadges = groupBy != ChimahonLibraryGroup.Source,
        showLanguageBadges = showLanguageBadges,
    )
}

fun SavedAnimeLibrarySettings.toAnimeLibraryFilter(query: String = ""): ChimahonAnimeLibraryFilter {
    return ChimahonAnimeLibraryFilter(
        query = query,
        unseen = unseenFilter.toAnimeTriState(),
        started = startedFilter.toAnimeTriState(),
        bookmarked = bookmarkedFilter.toAnimeTriState(),
        completed = completedFilter.toAnimeTriState(),
        downloaded = downloadedFilter.toAnimeTriState(),
        fillermarked = fillerFilter.toAnimeTriState(),
    )
}

fun ChimahonAnimeLibraryEntry.toAnimeLibraryEntryUiModel(
    settings: SavedAnimeLibrarySettings,
    tracked: Boolean = false,
): ChimahonAnimeLibraryEntryUiModel {
    return ChimahonAnimeLibraryEntryUiModel(
        id = id.toString(),
        title = anime.title.ifBlank { "Untitled anime" },
        thumbnailUrl = anime.thumbnailUrl,
        sourceName = sourceName?.takeIf { it.isNotBlank() },
        language = sourceLanguage.takeIf { it.isNotBlank() },
        status = anime.status.displayLabel(),
        unseenCount = unseenEpisodeCount,
        episodeCount = totalEpisodeCount,
        seenCount = seenEpisodeCount,
        downloadedCount = downloadedEpisodeCount,
        tracked = tracked,
        local = isLocal,
        favorite = anime.favorite,
        latestEpisodeLabel = latestUpload.takeIf { it > 0L }?.let { "Latest episode" },
        lastWatchedLabel = lastSeen.takeIf { it > 0L }?.let { "Watched" },
        nextAiringLabel = anime.nextEpisodeToAir.takeIf { it > 0 }?.let { "Ep $it airing" },
        progressLabel = watchProgressLabel(),
        warningLabel = anime.warningLabel(settings),
    )
}

/**
 * Adapts one library entry into the detail UI state, including sort/filter/display flags
 * persisted on the anime entry.
 */
fun ChimahonAnimeLibraryEntry.toAnimeDetailUiState(
    libraryData: ChimahonAnimeLibraryData? = null,
    selectedEpisodeIds: Set<String> = emptySet(),
    selectedSeasonId: String? = null,
    episodeQuery: String = "",
    downloadedEpisodeIds: Set<Long> = emptySet(),
    queuedEpisodeIds: Set<Long> = emptySet(),
    downloadingEpisodeProgress: Map<Long, Int> = emptyMap(),
    erroredEpisodeIds: Set<Long> = emptySet(),
    trackedAnimeIds: Set<Long> = emptySet(),
    refreshing: Boolean = false,
    errorMessage: String? = null,
): ChimahonAnimeDetailUiState {
    val sortState = anime.toAnimeEpisodeSortState()
    val filterState = anime.toAnimeEpisodeFilterState()
    val displayMode = anime.episodeDisplayMode.toAnimeUiDisplayMode()
    val fallbackDownloadedIds = if (downloadedEpisodeIds.isEmpty()) {
        fallbackDownloadedEpisodeIds()
    } else {
        emptySet()
    }
    val effectiveDownloadedIds = downloadedEpisodeIds + fallbackDownloadedIds
    val sortedEpisodes = episodes.sortedForAnimeDetail(sortState)
    val episodeModels = sortedEpisodes.map { episode ->
        episode.toAnimeDetailEpisodeUiModel(
            downloadState = episode.toAnimeDownloadState(
                isLocal = isLocal,
                downloadedEpisodeIds = effectiveDownloadedIds,
                queuedEpisodeIds = queuedEpisodeIds,
                downloadingEpisodeProgress = downloadingEpisodeProgress,
                erroredEpisodeIds = erroredEpisodeIds,
            ),
            downloadProgress = episode.toAnimeDownloadProgress(
                isLocal = isLocal,
                downloadedEpisodeIds = effectiveDownloadedIds,
                downloadingEpisodeProgress = downloadingEpisodeProgress,
            ),
        )
    }

    return ChimahonAnimeDetailUiState(
        anime = toAnimeHeaderUiModel(
            libraryData = libraryData,
            tracked = id in trackedAnimeIds,
        ),
        episodes = episodeModels,
        visibleEpisodes = episodeModels.filteredForAnimeUi(filterState, episodeQuery),
        seasons = toAnimeSeasonUiModels(
            libraryData = libraryData,
            selectedSeasonId = selectedSeasonId,
        ),
        relatedAnime = libraryData
            ?.relatedAnimeEntriesFor(this)
            .orEmpty()
            .map { entry ->
                ChimahonRelatedAnimeUiModel(
                    id = entry.id.toString(),
                    title = entry.anime.title.ifBlank { "Untitled anime" },
                    subtitle = entry.relatedAnimeSubtitle(),
                )
            },
        selectedEpisodeIds = selectedEpisodeIds,
        contentState = when {
            errorMessage != null -> ChimahonAnimeContentState.Error
            else -> ChimahonAnimeContentState.Content
        },
        refreshing = refreshing,
        errorMessage = errorMessage,
        sortState = sortState,
        filterState = filterState,
        displayMode = displayMode,
        episodeQuery = episodeQuery,
        missingEpisodeCount = episodes.missingEpisodeCount(),
    )
}

fun ChimahonAnimeEntry.toAnimeEpisodeSortState(): ChimahonAnimeEpisodeSortState {
    return ChimahonAnimeEpisodeSortState(
        sort = episodeSortMode.toAnimeUiSort(),
        descending = episodeSortDescending,
    )
}

fun ChimahonAnimeEntry.toAnimeEpisodeFilterState(): ChimahonAnimeEpisodeFilterState {
    return ChimahonAnimeEpisodeFilterState(
        unseen = unseenFilter.toAnimeUiFilterMode(),
        downloaded = downloadedFilter.toAnimeUiFilterMode(),
        bookmarked = bookmarkedFilter.toAnimeUiFilterMode(),
        filler = fillermarkedFilter.toAnimeUiFilterMode(),
    )
}

fun DomainAnimeEpisodeDisplayMode.toAnimeUiDisplayMode(): ChimahonAnimeEpisodeDisplayMode {
    return when (this) {
        DomainAnimeEpisodeDisplayMode.Name -> ChimahonAnimeEpisodeDisplayMode.SourceTitle
        DomainAnimeEpisodeDisplayMode.Number -> ChimahonAnimeEpisodeDisplayMode.EpisodeNumber
    }
}

fun ChimahonAnimeEpisodeDisplayMode.toAnimeDomainEpisodeDisplayMode(): DomainAnimeEpisodeDisplayMode {
    return when (this) {
        ChimahonAnimeEpisodeDisplayMode.SourceTitle,
        ChimahonAnimeEpisodeDisplayMode.Title,
        -> DomainAnimeEpisodeDisplayMode.Name
        ChimahonAnimeEpisodeDisplayMode.EpisodeNumber -> DomainAnimeEpisodeDisplayMode.Number
    }
}

fun ChimahonAnimeEpisodeSort.toAnimeDomainEpisodeSortMode(): ChimahonAnimeEpisodeSortMode {
    return when (this) {
        ChimahonAnimeEpisodeSort.Source -> ChimahonAnimeEpisodeSortMode.Source
        ChimahonAnimeEpisodeSort.EpisodeNumber -> ChimahonAnimeEpisodeSortMode.EpisodeNumber
        ChimahonAnimeEpisodeSort.UploadDate -> ChimahonAnimeEpisodeSortMode.UploadDate
        ChimahonAnimeEpisodeSort.Alphabetical -> ChimahonAnimeEpisodeSortMode.Alphabetical
    }
}

fun ChimahonAnimeTriState.toAnimeUiFilterMode(): ChimahonAnimeFilterMode {
    return when (this) {
        ChimahonAnimeTriState.Disabled -> ChimahonAnimeFilterMode.Disabled
        ChimahonAnimeTriState.Include -> ChimahonAnimeFilterMode.Include
        ChimahonAnimeTriState.Exclude -> ChimahonAnimeFilterMode.Exclude
    }
}

fun ChimahonFilterMode.toAnimeTriState(): ChimahonAnimeTriState {
    return when (this) {
        ChimahonFilterMode.Any -> ChimahonAnimeTriState.Disabled
        ChimahonFilterMode.Include -> ChimahonAnimeTriState.Include
        ChimahonFilterMode.Exclude -> ChimahonAnimeTriState.Exclude
    }
}

private fun ChimahonAnimeLibraryData.toAnimeLibraryCategoryBuckets(
    settings: SavedAnimeLibrarySettings,
    filteredEntries: List<ChimahonAnimeLibraryEntry>,
    trackedAnimeIds: Set<Long>,
): List<AnimeLibraryCategoryBucket> {
    return when (settings.groupBy) {
        ChimahonLibraryGroup.Default -> defaultCategoryBuckets(filteredEntries)
        ChimahonLibraryGroup.Source -> sourceCategoryBuckets(filteredEntries)
        ChimahonLibraryGroup.Status -> statusCategoryBuckets(filteredEntries)
        ChimahonLibraryGroup.TrackingStatus -> trackingCategoryBuckets(filteredEntries, trackedAnimeIds)
    }
}

private fun ChimahonAnimeLibraryData.defaultCategoryBuckets(
    filteredEntries: List<ChimahonAnimeLibraryEntry>,
): List<AnimeLibraryCategoryBucket> {
    val categoryBuckets = displayCategories
        .sortedWith(
            compareBy<ChimahonAnimeCategory>(
                { it.order },
                { it.animeCategoryLabel().lowercase() },
            ),
        )
        .map { category ->
            AnimeLibraryCategoryBucket(
                id = animeCategoryUiId(category.id),
                name = category.animeCategoryLabel(),
                entries = filteredEntries.filter { entry ->
                    category.id in entry.categoryIds.ifEmpty { listOf(CHIMAHON_ANIME_UNCATEGORIZED_ID) }
                },
            )
        }
    return listOf(AnimeLibraryCategoryBucket(AllAnimeCategoryId, "All", filteredEntries)) + categoryBuckets
}

private fun sourceCategoryBuckets(
    filteredEntries: List<ChimahonAnimeLibraryEntry>,
): List<AnimeLibraryCategoryBucket> {
    val sourceBuckets = filteredEntries
        .groupBy { entry ->
            when {
                entry.isLocal -> "Local anime"
                !entry.sourceName.isNullOrBlank() -> entry.sourceName.orEmpty()
                else -> "Unknown source"
            }
        }
        .entries
        .sortedBy { it.key }
        .map { (sourceName, entries) ->
            AnimeLibraryCategoryBucket(
                id = "source:$sourceName",
                name = sourceName,
                entries = entries,
            )
        }
    return listOf(AnimeLibraryCategoryBucket(AllAnimeCategoryId, "All sources", filteredEntries)) + sourceBuckets
}

private fun statusCategoryBuckets(
    filteredEntries: List<ChimahonAnimeLibraryEntry>,
): List<AnimeLibraryCategoryBucket> {
    val statusBuckets = ChimahonAnimeStatus.entries.mapNotNull { status ->
        val entries = filteredEntries.filter { it.anime.status == status }
        if (entries.isEmpty()) {
            null
        } else {
            AnimeLibraryCategoryBucket(
                id = "status:${status.name}",
                name = status.displayLabel(),
                entries = entries,
            )
        }
    }
    return listOf(AnimeLibraryCategoryBucket(AllAnimeCategoryId, "All statuses", filteredEntries)) + statusBuckets
}

private fun trackingCategoryBuckets(
    filteredEntries: List<ChimahonAnimeLibraryEntry>,
    trackedAnimeIds: Set<Long>,
): List<AnimeLibraryCategoryBucket> {
    val trackedEntries = filteredEntries.filter { it.id in trackedAnimeIds }
    val untrackedEntries = filteredEntries.filterNot { it.id in trackedAnimeIds }
    return listOf(
        AnimeLibraryCategoryBucket(AllAnimeCategoryId, "All tracking", filteredEntries),
        AnimeLibraryCategoryBucket("tracking:tracked", "Tracked", trackedEntries),
        AnimeLibraryCategoryBucket("tracking:untracked", "Untracked", untrackedEntries),
    )
}

private fun List<AnimeLibraryCategoryBucket>.effectiveSelectedCategoryId(
    selectedCategoryId: String?,
    settings: SavedAnimeLibrarySettings,
): String {
    val defaultCategoryId = settings.defaultCategoryId
        .takeIf { settings.groupBy == ChimahonLibraryGroup.Default && it >= 0 }
        ?.let { animeCategoryUiId(it.toLong()) }
    val requestedId = selectedCategoryId ?: defaultCategoryId ?: AllAnimeCategoryId
    return firstOrNull { it.id == requestedId }?.id ?: firstOrNull()?.id ?: AllAnimeCategoryId
}

private fun animeCategoryUiId(categoryId: Long): String {
    return "category:$categoryId"
}

private fun List<ChimahonAnimeLibraryEntry>.filterByTracking(
    trackedFilter: ChimahonFilterMode,
    trackedAnimeIds: Set<Long>,
): List<ChimahonAnimeLibraryEntry> {
    return when (trackedFilter) {
        ChimahonFilterMode.Any -> this
        ChimahonFilterMode.Include -> filter { it.id in trackedAnimeIds }
        ChimahonFilterMode.Exclude -> filterNot { it.id in trackedAnimeIds }
    }
}

private fun List<ChimahonAnimeLibraryEntry>.sortedForAnimeLibraryUi(
    settings: SavedAnimeLibrarySettings,
): List<ChimahonAnimeLibraryEntry> {
    val sorted = when (settings.sort) {
        ChimahonLibrarySort.Alphabetical -> sortedBy { it.anime.title.lowercase() }
        ChimahonLibrarySort.LastRead -> sortedByDescending { it.lastSeen }
        ChimahonLibrarySort.LastUpdate -> sortedByDescending { it.anime.lastUpdate }
        ChimahonLibrarySort.UnreadCount -> sortedByDescending { it.unseenEpisodeCount }
        ChimahonLibrarySort.TotalChapters -> sortedByDescending { it.totalEpisodeCount }
        ChimahonLibrarySort.LatestChapter -> sortedByDescending { it.latestUpload }
        ChimahonLibrarySort.ChapterFetchDate -> sortedByDescending { it.episodeFetchedAt }
        ChimahonLibrarySort.DateAdded -> sortedByDescending { it.anime.dateAdded }
        ChimahonLibrarySort.Random -> sortedBy {
            ((it.id * 1_103_515_245L) xor it.anime.title.hashCode().toLong()) and Long.MAX_VALUE
        }
    }
    return if (settings.sortAscending) sorted else sorted.asReversed()
}

private fun ChimahonLibraryDisplayMode.toAnimeUiDisplayMode(): ChimahonAnimeLibraryDisplayMode {
    return when (this) {
        ChimahonLibraryDisplayMode.ComfortableGrid,
        ChimahonLibraryDisplayMode.ComfortableGridPanorama,
        -> ChimahonAnimeLibraryDisplayMode.ComfortableGrid
        ChimahonLibraryDisplayMode.CompactGrid -> ChimahonAnimeLibraryDisplayMode.CompactGrid
        ChimahonLibraryDisplayMode.CoverOnlyGrid -> ChimahonAnimeLibraryDisplayMode.CoverOnlyGrid
        ChimahonLibraryDisplayMode.List -> ChimahonAnimeLibraryDisplayMode.List
    }
}

private fun ChimahonLibraryDisplayMode.toAnimeUiCoverRatio(): ChimahonAnimeCoverRatio {
    return when (this) {
        ChimahonLibraryDisplayMode.ComfortableGridPanorama -> ChimahonAnimeCoverRatio.Widescreen
        else -> ChimahonAnimeCoverRatio.Poster
    }
}

private fun ChimahonAnimeLibraryEntry.toAnimeHeaderUiModel(
    libraryData: ChimahonAnimeLibraryData?,
    tracked: Boolean,
): ChimahonAnimeHeaderUiModel {
    return ChimahonAnimeHeaderUiModel(
        id = id.toString(),
        title = anime.title.ifBlank { "Untitled anime" },
        sourceLine = listOfNotNull(
            sourceName?.takeIf { it.isNotBlank() },
            sourceLanguage.takeIf { it.isNotBlank() },
            anime.status.displayLabel(),
        ).joinToString(" | ").ifBlank { anime.status.displayLabel() },
        creatorLine = listOfNotNull(anime.author, anime.artist)
            .map(String::trim)
            .filter(String::isNotEmpty)
            .distinct()
            .joinToString(" / ")
            .takeIf { it.isNotBlank() },
        status = anime.status.displayLabel(),
        description = anime.description?.takeIf { it.isNotBlank() },
        tags = anime.genres,
        categoryLabels = libraryData?.categoryLabelsFor(this).orEmpty(),
        favorite = anime.favorite,
        trackedCount = if (tracked) 1 else 0,
        webUrl = anime.url.takeIf { it.isNotBlank() },
        nextUpdateLabel = anime.expectedNextUpdate?.let { "Scheduled" },
        nextAiringLabel = anime.nextEpisodeToAir.takeIf { it > 0 }?.let { "Episode $it" },
        totalDurationLabel = episodes.totalDurationLabel(),
        totalEpisodeCount = totalEpisodeCount,
        unseenCount = unseenEpisodeCount,
        downloadedCount = downloadedEpisodeCount,
        skipIntroEnabled = !anime.skipIntroDisabled && anime.skipIntroLength > 0,
    )
}

private fun ChimahonAnimeEpisodeEntry.toAnimeDetailEpisodeUiModel(
    downloadState: ChimahonAnimeDownloadUiState,
    downloadProgress: Int,
): ChimahonAnimeEpisodeUiModel {
    val sourceTitle = name.ifBlank {
        episodeNumber.takeIf { isRecognizedNumber }
            ?.let { "Episode ${it.trimmedAnimeNumber()}" }
            ?: "Episode"
    }
    return ChimahonAnimeEpisodeUiModel(
        id = id.toString(),
        title = sourceTitle,
        sourceTitle = sourceTitle,
        episodeNumber = episodeNumber.takeIf { isRecognizedNumber },
        episodeMarker = episodeNumber.takeIf { isRecognizedNumber }?.trimmedAnimeNumber() ?: "E",
        subtitle = summary?.takeIf { it.isNotBlank() },
        scanlator = scanlator?.takeIf { it.isNotBlank() },
        uploadDateLabel = dateUpload.takeIf { it > 0L }?.let { "Uploaded" },
        durationLabel = totalSeconds.toAnimeDurationLabel(),
        progressLabel = progressLabel(),
        seen = seen,
        bookmarked = bookmark,
        filler = fillermark,
        downloadState = downloadState,
        downloadProgress = downloadProgress,
        sourceOrder = sourceOrder,
        url = url,
    )
}

private fun ChimahonAnimeEpisodeEntry.toAnimeDownloadState(
    isLocal: Boolean,
    downloadedEpisodeIds: Set<Long>,
    queuedEpisodeIds: Set<Long>,
    downloadingEpisodeProgress: Map<Long, Int>,
    erroredEpisodeIds: Set<Long>,
): ChimahonAnimeDownloadUiState {
    return when {
        id in erroredEpisodeIds -> ChimahonAnimeDownloadUiState.Error
        id in downloadingEpisodeProgress -> ChimahonAnimeDownloadUiState.Downloading
        id in queuedEpisodeIds -> ChimahonAnimeDownloadUiState.Queued
        isLocal || id in downloadedEpisodeIds -> ChimahonAnimeDownloadUiState.Downloaded
        else -> ChimahonAnimeDownloadUiState.NotDownloaded
    }
}

private fun ChimahonAnimeEpisodeEntry.toAnimeDownloadProgress(
    isLocal: Boolean,
    downloadedEpisodeIds: Set<Long>,
    downloadingEpisodeProgress: Map<Long, Int>,
): Int {
    return when {
        id in downloadingEpisodeProgress -> downloadingEpisodeProgress[id]?.coerceIn(0, 100) ?: 0
        isLocal || id in downloadedEpisodeIds -> 100
        else -> 0
    }
}

private fun ChimahonAnimeLibraryEntry.fallbackDownloadedEpisodeIds(): Set<Long> {
    if (downloadedEpisodeCount <= 0) return emptySet()
    return episodes
        .sortedWith(compareBy<ChimahonAnimeEpisodeEntry> { it.sourceOrder }.thenBy { it.id })
        .take(downloadedEpisodeCount)
        .map { it.id }
        .toSet()
}

private fun List<ChimahonAnimeEpisodeEntry>.sortedForAnimeDetail(
    state: ChimahonAnimeEpisodeSortState,
): List<ChimahonAnimeEpisodeEntry> {
    val sorted = when (state.sort) {
        ChimahonAnimeEpisodeSort.Source -> sortedWith(
            compareBy<ChimahonAnimeEpisodeEntry> { it.sourceOrder }
                .thenBy { it.url }
                .thenBy { it.id },
        )
        ChimahonAnimeEpisodeSort.EpisodeNumber -> sortedWith(
            compareBy<ChimahonAnimeEpisodeEntry> {
                if (it.isRecognizedNumber) it.episodeNumber else Double.MAX_VALUE
            }.thenBy { it.sourceOrder }.thenBy { it.id },
        )
        ChimahonAnimeEpisodeSort.UploadDate -> sortedWith(
            compareBy<ChimahonAnimeEpisodeEntry> { it.dateUpload }
                .thenBy { it.sourceOrder }
                .thenBy { it.id },
        )
        ChimahonAnimeEpisodeSort.Alphabetical -> sortedWith(
            compareBy<ChimahonAnimeEpisodeEntry> { it.name.lowercase() }
                .thenBy { it.sourceOrder }
                .thenBy { it.id },
        )
    }
    return when {
        state.sort == ChimahonAnimeEpisodeSort.Source && state.descending -> sorted
        state.sort == ChimahonAnimeEpisodeSort.Source -> sorted.asReversed()
        state.descending -> sorted.asReversed()
        else -> sorted
    }
}

private fun List<ChimahonAnimeEpisodeUiModel>.filteredForAnimeUi(
    filterState: ChimahonAnimeEpisodeFilterState,
    query: String,
): List<ChimahonAnimeEpisodeUiModel> {
    return asSequence()
        .filter { query.isBlank() || it.matchesAnimeEpisodeQuery(query) }
        .filterByMode(filterState.unseen) { !it.seen }
        .filterByMode(filterState.downloaded) { it.downloadState == ChimahonAnimeDownloadUiState.Downloaded }
        .filterByMode(filterState.bookmarked) { it.bookmarked }
        .filterByMode(filterState.filler) { it.filler }
        .toList()
}

private fun Sequence<ChimahonAnimeEpisodeUiModel>.filterByMode(
    mode: ChimahonAnimeFilterMode,
    predicate: (ChimahonAnimeEpisodeUiModel) -> Boolean,
): Sequence<ChimahonAnimeEpisodeUiModel> {
    return when (mode) {
        ChimahonAnimeFilterMode.Disabled -> this
        ChimahonAnimeFilterMode.Include -> filter(predicate)
        ChimahonAnimeFilterMode.Exclude -> filterNot(predicate)
    }
}

private fun ChimahonAnimeEpisodeUiModel.matchesAnimeEpisodeQuery(query: String): Boolean {
    return title.contains(query, ignoreCase = true) ||
        sourceTitle.contains(query, ignoreCase = true) ||
        episodeMarker.contains(query, ignoreCase = true) ||
        subtitle?.contains(query, ignoreCase = true) == true ||
        scanlator?.contains(query, ignoreCase = true) == true
}

private fun ChimahonAnimeLibraryEntry.toAnimeSeasonUiModels(
    libraryData: ChimahonAnimeLibraryData?,
    selectedSeasonId: String?,
): List<ChimahonAnimeSeasonUiModel> {
    val seasonEntries = (listOf(this) + libraryData.relatedAnimeEntriesFor(this))
        .distinctBy { it.id }
        .sortedWith(compareBy<ChimahonAnimeLibraryEntry> { it.anime.seasonSourceOrder }
            .thenBy { it.anime.seasonNumber }
            .thenBy { it.anime.title.lowercase() })
    val shouldShowSeasons = seasonEntries.size > 1 ||
        anime.fetchType == ChimahonAnimeFetchType.Seasons ||
        anime.parentId != null ||
        anime.seasonNumber != 1.0
    if (!shouldShowSeasons) return emptyList()

    return seasonEntries.map { entry ->
        val id = animeSeasonUiId(entry.id)
        ChimahonAnimeSeasonUiModel(
            id = id,
            title = entry.anime.seasonTitle(),
            subtitle = entry.anime.title.takeIf { it.isNotBlank() && it != entry.anime.seasonTitle() },
            countLabel = entry.totalEpisodeCount.toEpisodeCountLabel(),
            unseenCount = entry.unseenEpisodeCount,
            selected = selectedSeasonId?.let { it == id } ?: (entry.id == this.id),
        )
    }
}

private fun ChimahonAnimeLibraryData?.relatedAnimeEntriesFor(
    entry: ChimahonAnimeLibraryEntry,
): List<ChimahonAnimeLibraryEntry> {
    if (this == null) return emptyList()
    val rootId = entry.anime.parentId ?: entry.id
    return entries
        .asSequence()
        .filter { candidate -> candidate.id != entry.id }
        .filter { candidate ->
            val candidateRootId = candidate.anime.parentId ?: candidate.id
            candidateRootId == rootId ||
                candidate.anime.parentId == entry.id ||
                entry.anime.parentId == candidate.id
        }
        .distinctBy { it.id }
        .sortedWith(compareBy<ChimahonAnimeLibraryEntry> { it.anime.seasonSourceOrder }
            .thenBy { it.anime.seasonNumber }
            .thenBy { it.anime.title.lowercase() })
        .toList()
}

private fun ChimahonAnimeLibraryData.categoryLabelsFor(
    entry: ChimahonAnimeLibraryEntry,
): List<String> {
    val labelsById = displayCategories.associate { it.id to it.animeCategoryLabel() }
    return entry.categoryIds
        .ifEmpty { listOf(CHIMAHON_ANIME_UNCATEGORIZED_ID) }
        .map { categoryId -> labelsById[categoryId] ?: "Category $categoryId" }
        .distinct()
}

private fun ChimahonAnimeLibraryEntry.relatedAnimeSubtitle(): String? {
    return listOfNotNull(
        anime.seasonTitle().takeIf { it != anime.title },
        sourceName?.takeIf { it.isNotBlank() },
        anime.status.displayLabel(),
    ).joinToString(" | ").takeIf { it.isNotBlank() }
}

private fun animeSeasonUiId(animeId: Long): String {
    return "season:$animeId"
}

private fun ChimahonAnimeEntry.seasonTitle(): String {
    return seasonNumber
        .takeIf { it > 0.0 }
        ?.let { "Season ${it.trimmedAnimeNumber()}" }
        ?: title.ifBlank { "Season" }
}

private fun ChimahonAnimeLibraryEntry.watchProgressLabel(): String {
    return when {
        totalEpisodeCount <= 0 -> "No episodes"
        seenEpisodeCount >= totalEpisodeCount -> "Complete"
        seenEpisodeCount > 0 -> "$seenEpisodeCount / $totalEpisodeCount watched"
        unseenEpisodeCount > 0 -> "$unseenEpisodeCount unseen"
        else -> "Not started"
    }
}

private fun ChimahonAnimeEntry.warningLabel(settings: SavedAnimeLibrarySettings): String? {
    return when {
        !initialized -> "Not initialized"
        updateStrategy == ChimahonAnimeUpdateStrategy.OnlyFetchOnce -> "Fetch once"
        settings.updateRestrictions.isNotEmpty() && status == ChimahonAnimeStatus.Completed -> "Update limited"
        else -> null
    }
}

private fun List<ChimahonAnimeEpisodeEntry>.totalDurationLabel(): String? {
    val totalSeconds = sumOf { it.totalSeconds.coerceAtLeast(0L) }
    return totalSeconds.toAnimeDurationLabel()
}

private fun ChimahonAnimeEpisodeEntry.progressLabel(): String? {
    return when {
        seen -> "Seen"
        lastSecondSeen > 0L && totalSeconds > 0L ->
            "${lastSecondSeen.toPlaybackClock()} / ${totalSeconds.toPlaybackClock()}"
        lastSecondSeen > 0L -> "Started"
        progressFraction > 0f -> "${(progressFraction * 100f).roundToInt()}%"
        else -> null
    }
}

private fun Long.toAnimeDurationLabel(): String? {
    val seconds = coerceAtLeast(0L)
    if (seconds <= 0L) return null
    val hours = seconds / 3_600L
    val minutes = (seconds % 3_600L) / 60L
    return when {
        hours > 0L && minutes > 0L -> "${hours}h ${minutes}m"
        hours > 0L -> "${hours}h"
        else -> "${minutes.coerceAtLeast(1L)}m"
    }
}

private fun Long.toPlaybackClock(): String {
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

private fun List<ChimahonAnimeEpisodeEntry>.missingEpisodeCount(): Int {
    val numbers = mapNotNull { episode ->
        episode.episodeNumber
            .takeIf { episode.isRecognizedNumber && it == it.toInt().toDouble() }
            ?.toInt()
    }
        .filter { it > 0 }
        .distinct()
        .sorted()
    if (numbers.size < 2) return 0
    return numbers.zipWithNext().sumOf { (left, right) ->
        (right - left - 1).coerceAtLeast(0)
    }
}

private fun Int.toEpisodeCountLabel(): String {
    return when (this) {
        0 -> "No episodes"
        1 -> "1 episode"
        else -> "$this episodes"
    }
}

private fun animeLibrarySubtitle(
    visibleCount: Int,
    filteredCount: Int,
    totalCount: Int,
    downloadedOnly: Boolean,
): String {
    return listOfNotNull(
        "Downloaded only".takeIf { downloadedOnly },
        when {
            visibleCount == totalCount -> "$visibleCount anime"
            filteredCount == totalCount -> "$visibleCount of $totalCount anime"
            else -> "$visibleCount of $filteredCount filtered"
        },
    ).joinToString(" | ")
}

private fun ChimahonAnimeCategory.animeCategoryLabel(): String {
    return name.ifBlank { "Default" }
}

private fun Double.trimmedAnimeNumber(): String {
    val intValue = toInt()
    return if (this == intValue.toDouble()) {
        intValue.toString()
    } else {
        toString().trimEnd('0').trimEnd('.')
    }
}

private data class AnimeLibraryCategoryBucket(
    val id: String,
    val name: String,
    val entries: List<ChimahonAnimeLibraryEntry>,
)
