package app.chimahon.shared.animeui

import app.chimahon.shared.anime.ChimahonAnimeBackendError
import app.chimahon.shared.anime.ChimahonAnimeFetchType
import app.chimahon.shared.anime.ChimahonAnimeSourceAnime
import app.chimahon.shared.anime.ChimahonAnimeSourceBrowseMode
import app.chimahon.shared.anime.ChimahonAnimeSourceBrowseRequest
import app.chimahon.shared.anime.ChimahonAnimeSourceBrowseResult
import app.chimahon.shared.anime.ChimahonAnimeSourceBrowseSource
import app.chimahon.shared.anime.ChimahonAnimeSourceDetailLoadRequest
import app.chimahon.shared.anime.ChimahonAnimeSourceDetailLoadResult
import app.chimahon.shared.anime.ChimahonAnimeSourceEpisode
import app.chimahon.shared.anime.ChimahonAnimeSourceFilter
import app.chimahon.shared.anime.ChimahonAnimeSourceFilterGroup
import app.chimahon.shared.anime.ChimahonAnimeSourceFilterOption
import app.chimahon.shared.anime.ChimahonAnimeSourceFilterSelection
import app.chimahon.shared.anime.ChimahonAnimeSourceFilterValue
import app.chimahon.shared.anime.ChimahonAnimeSourceFiltersLoadResult
import app.chimahon.shared.anime.ChimahonAnimeSourceInfo
import app.chimahon.shared.anime.ChimahonAnimeSourceListLoadRequest
import app.chimahon.shared.anime.ChimahonAnimeSourceListLoadResult
import app.chimahon.shared.anime.ChimahonAnimeSourceSearchRequest
import app.chimahon.shared.anime.ChimahonAnimeSourceSearchResult

fun ChimahonAnimeSourceListLoadResult.toAnimeSourceListUiState(
    filters: ChimahonAnimeSourceListFilterState = ChimahonAnimeSourceListFilterState(),
    displayMode: ChimahonAnimeSourceBrowseDisplayMode = ChimahonAnimeSourceBrowseDisplayMode.ComfortableList,
    selectedSourceId: String? = null,
    loading: Boolean = false,
    refreshing: Boolean = false,
    title: String = "Anime sources",
    formatTimestamp: (Long) -> String? = { null },
): ChimahonAnimeSourceListUiState {
    val sourceModels = sources.map { source ->
        source.toAnimeSourceBrowseSourceUiModel(
            pinned = source.pinned,
            formatTimestamp = formatTimestamp,
        )
    }
    return ChimahonAnimeSourceListUiState(
        title = title,
        subtitle = animeSourceListSubtitle(sourceModels, filters),
        sources = sourceModels,
        filters = filters,
        displayMode = displayMode,
        selectedSourceId = selectedSourceId,
        loading = loading,
        refreshing = refreshing,
        errorMessage = errors.toAnimeSourceBrowseErrorMessage(),
    )
}

fun ChimahonAnimeSourceBrowseResult.toAnimeSourceBrowseUiState(
    filters: ChimahonAnimeSourceFilterUiState? = null,
    displayMode: ChimahonAnimeSourceBrowseDisplayMode = ChimahonAnimeSourceBrowseDisplayMode.Grid,
    selectedResultId: String? = null,
    loading: Boolean = false,
    refreshing: Boolean = false,
    loadingMore: Boolean = false,
    formatTimestamp: (Long) -> String? = { null },
): ChimahonAnimeSourceBrowseUiState {
    val sourceModel = source?.toAnimeSourceBrowseSourceUiModel(formatTimestamp = formatTimestamp)
    val message = errors.toAnimeSourceBrowseErrorMessage()
    val filterState = filters ?: ChimahonAnimeSourceFilterUiState(
        sourceId = sourceId,
        sourceName = source?.displayName,
        query = request.query,
    )
    return ChimahonAnimeSourceBrowseUiState(
        source = sourceModel,
        mode = request.mode.toAnimeSourceBrowseUiMode(),
        query = request.query,
        results = entries.map { entry ->
            entry.toAnimeSourceBrowseItemUiModel(
                source = sourceModel,
                formatTimestamp = formatTimestamp,
            )
        },
        filters = filterState,
        page = page,
        hasNextPage = hasNextPage,
        displayMode = displayMode,
        selectedResultId = selectedResultId,
        loading = loading,
        refreshing = refreshing,
        loadingMore = loadingMore,
        errorMessage = message,
    )
}

fun ChimahonAnimeSourceSearchResult.toAnimeSourceSearchUiState(
    loadingSourceIds: Set<String> = emptySet(),
    loading: Boolean = false,
    refreshing: Boolean = false,
    formatTimestamp: (Long) -> String? = { null },
): ChimahonAnimeSourceSearchUiState {
    return ChimahonAnimeSourceSearchUiState(
        query = request.query,
        submittedQuery = request.normalizedQuery,
        groups = sourceResults.map { result ->
            result.toAnimeSourceBrowseGroupUiModel(
                query = request.normalizedQuery,
                formatTimestamp = formatTimestamp,
            )
        },
        loadingSourceIds = loadingSourceIds,
        loading = loading,
        refreshing = refreshing,
        errorMessage = errors.toAnimeSourceBrowseErrorMessage(),
    )
}

fun ChimahonAnimeSourceBrowseResult.toAnimeSourceBrowseGroupUiModel(
    query: String? = null,
    loading: Boolean = false,
    formatTimestamp: (Long) -> String? = { null },
): ChimahonAnimeSourceBrowseGroupUiModel {
    val sourceModel = source?.toAnimeSourceBrowseSourceUiModel(formatTimestamp = formatTimestamp)
    val effectiveQuery = query ?: request.query
    return ChimahonAnimeSourceBrowseGroupUiModel(
        id = "anime-source-group:${sourceId}:${request.mode.name}:$effectiveQuery",
        source = sourceModel,
        mode = request.mode.toAnimeSourceBrowseUiMode(),
        query = effectiveQuery,
        results = entries.map { entry ->
            entry.toAnimeSourceBrowseItemUiModel(
                source = sourceModel,
                formatTimestamp = formatTimestamp,
            )
        },
        hasNextPage = hasNextPage,
        loading = loading,
        errorMessage = errors.toAnimeSourceBrowseErrorMessage(),
    )
}

fun ChimahonAnimeSourceDetailLoadResult.toAnimeSourceDetailUiState(
    loading: Boolean = false,
    refreshing: Boolean = false,
    formatTimestamp: (Long) -> String? = { null },
): ChimahonAnimeSourceDetailUiState {
    val sourceDetail = detail
    val message = errors.toAnimeSourceBrowseErrorMessage()
    if (sourceDetail == null) {
        return ChimahonAnimeSourceDetailUiState(
            loading = loading,
            refreshing = refreshing,
            errorMessage = message,
        )
    }

    val sourceModel = sourceDetail.source?.toAnimeSourceBrowseSourceUiModel(formatTimestamp = formatTimestamp)
    return ChimahonAnimeSourceDetailUiState(
        source = sourceModel,
        anime = sourceDetail.anime.toAnimeSourceBrowseItemUiModel(
            source = sourceModel,
            inLibrary = sourceDetail.inLibrary,
            libraryAnimeId = sourceDetail.libraryAnimeId,
            formatTimestamp = formatTimestamp,
        ),
        episodes = sourceDetail.episodes
            .sortedWith(
                compareBy<ChimahonAnimeSourceEpisode> { it.sourceOrder }
                    .thenBy { it.episodeNumber },
            )
            .map { episode -> episode.toAnimeSourceEpisodeUiModel(formatTimestamp) },
        relatedAnime = sourceDetail.relatedAnime.map { entry ->
            entry.toAnimeSourceBrowseItemUiModel(
                source = sourceModel,
                formatTimestamp = formatTimestamp,
            )
        },
        loading = loading,
        refreshing = refreshing,
        errorMessage = message,
    )
}

fun ChimahonAnimeSourceFiltersLoadResult.toAnimeSourceFilterUiState(
    sourceName: String? = null,
    query: String? = null,
    expandedGroupIds: Set<String>? = null,
    loading: Boolean = false,
    refreshing: Boolean = false,
): ChimahonAnimeSourceFilterUiState {
    val groupModels = groups.map { group ->
        group.toAnimeSourceFilterGroupUiModel(
            expanded = expandedGroupIds?.contains(group.id) ?: group.initiallyExpanded,
        )
    }
    return ChimahonAnimeSourceFilterUiState(
        sourceId = request.sourceId,
        sourceName = sourceName,
        query = query ?: request.query,
        groups = groupModels,
        expandedGroupIds = expandedGroupIds ?: groupModels.filter { it.expanded }.map { it.id }.toSet(),
        loading = loading,
        refreshing = refreshing,
        errorMessage = errors.toAnimeSourceBrowseErrorMessage(),
    )
}

fun ChimahonAnimeSourceBrowseSource.toAnimeSourceBrowseSourceUiModel(
    pinned: Boolean? = null,
    formatTimestamp: (Long) -> String? = { null },
): ChimahonAnimeSourceBrowseSourceUiModel {
    return ChimahonAnimeSourceBrowseSourceUiModel(
        id = id.toString(),
        sourceId = id,
        name = displayName,
        language = language,
        iconUrl = iconUrl,
        baseUrl = baseUrl,
        extensionName = extensionName,
        extensionVersion = extensionVersion,
        installed = installed,
        enabled = enabled,
        pinned = pinned ?: this.pinned,
        local = isLocal,
        nsfw = isNsfw,
        supportsPopular = supportsPopular,
        supportsLatest = supportsLatest,
        supportsSearch = supportsSearch,
        supportsFilters = supportsFilters,
        health = toAnimeSourceBrowseHealth(),
        healthMessage = unavailableReason,
        lastUsedAtMillis = lastUsedAtMillis,
        lastUsedLabel = lastUsedAtMillis.takeIf { it > 0L }?.let(formatTimestamp),
        resultCount = resultCount,
    )
}

fun ChimahonAnimeSourceInfo.toAnimeSourceBrowseSourceUiModel(
    pinned: Boolean = false,
    installed: Boolean = true,
    enabled: Boolean = true,
): ChimahonAnimeSourceBrowseSourceUiModel {
    return ChimahonAnimeSourceBrowseSourceUiModel(
        id = id.toString(),
        sourceId = id,
        name = displayName,
        language = language,
        iconUrl = iconUrl,
        baseUrl = baseUrl,
        installed = installed,
        enabled = enabled,
        pinned = pinned,
        local = isLocal,
        supportsLatest = supportsLatest,
        supportsSearch = supportsSearch,
    )
}

fun ChimahonAnimeSourceAnime.toAnimeSourceBrowseItemUiModel(
    source: ChimahonAnimeSourceBrowseSourceUiModel? = null,
    inLibrary: Boolean = this.inLibrary,
    libraryAnimeId: Long? = this.libraryAnimeId,
    formatTimestamp: (Long) -> String? = { null },
): ChimahonAnimeSourceBrowseItemUiModel {
    val effectiveSourceName = source?.name ?: sourceName
    val effectiveLanguage = source?.language ?: sourceLanguage
    return ChimahonAnimeSourceBrowseItemUiModel(
        id = libraryAnimeId?.toString() ?: stableKey,
        sourceId = sourceId,
        sourceName = effectiveSourceName,
        sourceLanguage = effectiveLanguage,
        title = title.ifBlank { "Untitled anime" },
        subtitle = listOfNotNull(
            author?.takeIf(String::isNotBlank),
            artist?.takeIf(String::isNotBlank),
        ).distinct().joinToString(" - ").takeIf(String::isNotBlank),
        author = author,
        artist = artist,
        url = url,
        thumbnailUrl = thumbnailUrl,
        backgroundUrl = backgroundUrl,
        status = status.displayLabel(),
        description = description,
        tags = genres,
        initialized = initialized,
        inLibrary = inLibrary,
        libraryAnimeId = libraryAnimeId?.toString(),
        rankLabel = rank?.let { "#$it" },
        latestEpisodeLabel = latestEpisodeLabel,
        updatedLabel = lastUpdatedAtMillis.takeIf { it > 0L }?.let(formatTimestamp),
        fetchTypeLabel = fetchType.toAnimeSourceFetchTypeLabel(),
    )
}

fun ChimahonAnimeSourceEpisode.toAnimeSourceEpisodeUiModel(
    formatTimestamp: (Long) -> String? = { null },
): ChimahonAnimeSourceEpisodeUiModel {
    return ChimahonAnimeSourceEpisodeUiModel(
        id = url.ifBlank { "$sourceOrder:$name" },
        title = name.ifBlank { episodeNumber.takeIf { it >= 0.0 }?.let { "Episode ${it.trimmedAnimeSourceBrowseNumber()}" } ?: "Episode" },
        episodeNumber = episodeNumber.takeIf { it >= 0.0 },
        subtitle = summary,
        scanlator = scanlator,
        uploadDateLabel = dateUpload.takeIf { it > 0L }?.let(formatTimestamp),
        durationLabel = totalSeconds.takeIf { it > 0L }?.toAnimeSourceDurationLabel(),
        previewUrl = previewUrl,
        sourceOrder = sourceOrder,
        url = url,
    )
}

fun ChimahonAnimeSourceFilterGroup.toAnimeSourceFilterGroupUiModel(
    expanded: Boolean? = null,
): ChimahonAnimeSourceFilterGroupUiModel {
    return ChimahonAnimeSourceFilterGroupUiModel(
        id = id,
        title = title.ifBlank { "Filters" },
        filters = filters.map { it.toAnimeSourceFilterControlUiModel() },
        expanded = expanded ?: initiallyExpanded,
    )
}

fun ChimahonAnimeSourceFilter.toAnimeSourceFilterControlUiModel(): ChimahonAnimeSourceFilterControlUiModel {
    val selectedOptionId = value.selectedOptionId
        ?: options.firstOrNull { it.selected }?.id
    val selectedOptionIds = value.selectedOptionIds.ifEmpty {
        options.filter { it.selected }.map { it.id }.toSet()
    }
    return ChimahonAnimeSourceFilterControlUiModel(
        id = id,
        title = title,
        kind = kind,
        options = options.map { option ->
            option.toAnimeSourceFilterOptionUiModel(
                selected = option.selected ||
                    option.id == selectedOptionId ||
                    option.id in selectedOptionIds,
            )
        },
        textValue = value.text,
        checked = value.checked,
        selectedOptionId = selectedOptionId,
        selectedOptionIds = selectedOptionIds,
        triState = value.triState,
        sortOptionId = value.sortOptionId,
        sortAscending = value.sortAscending,
        enabled = enabled,
    )
}

fun ChimahonAnimeSourceFilterOption.toAnimeSourceFilterOptionUiModel(
    selected: Boolean? = null,
): ChimahonAnimeSourceFilterOptionUiModel {
    return ChimahonAnimeSourceFilterOptionUiModel(
        id = id,
        title = title,
        selected = selected ?: this.selected,
    )
}

fun ChimahonAnimeSourceFilterUiState.toAnimeSourceFilterSelections(): List<ChimahonAnimeSourceFilterSelection> {
    return groups.flatMap { group ->
        group.filters.mapNotNull { filter -> filter.toAnimeSourceFilterSelectionOrNull() }
    }
}

fun ChimahonAnimeSourceFilterControlUiModel.toAnimeSourceFilterSelectionOrNull(): ChimahonAnimeSourceFilterSelection? {
    val value = toAnimeSourceFilterValue()
    return if (value.isActive) {
        ChimahonAnimeSourceFilterSelection(
            filterId = id,
            kind = kind,
            value = value,
        )
    } else {
        null
    }
}

fun ChimahonAnimeSourceFilterControlUiModel.toAnimeSourceFilterValue(): ChimahonAnimeSourceFilterValue {
    return ChimahonAnimeSourceFilterValue(
        text = textValue,
        checked = checked,
        selectedOptionId = selectedOptionId,
        selectedOptionIds = selectedOptionIds,
        triState = triState,
        sortOptionId = sortOptionId,
        sortAscending = sortAscending,
    )
}

fun ChimahonAnimeSourceListFilterState.toAnimeSourceListLoadRequest(
    pinnedSourceIds: Set<Long> = emptySet(),
    preferredMode: ChimahonAnimeSourceBrowseUiMode = ChimahonAnimeSourceBrowseUiMode.Popular,
): ChimahonAnimeSourceListLoadRequest {
    return ChimahonAnimeSourceListLoadRequest(
        query = query,
        enabledLanguages = language?.takeIf(String::isNotBlank)?.let { listOf(it) }.orEmpty(),
        pinnedSourceIds = pinnedSourceIds,
        pinnedOnly = pinnedOnly,
        installedOnly = installedOnly,
        enabledOnly = enabledOnly,
        latestOnly = latestOnly,
        searchOnly = searchOnly,
        includeDisabled = !enabledOnly,
        includeUnavailable = !hideUnavailable,
        includeNsfw = includeNsfw,
        preferredMode = preferredMode.toAnimeSourceBrowseMode(),
    )
}

fun ChimahonAnimeSourceBrowseUiState.toAnimeSourceBrowseRequest(
    page: Int? = null,
    refresh: Boolean = false,
): ChimahonAnimeSourceBrowseRequest? {
    val selectedSource = source ?: return null
    return ChimahonAnimeSourceBrowseRequest(
        sourceId = selectedSource.sourceId,
        mode = mode.toAnimeSourceBrowseMode(),
        page = page ?: this.page,
        query = query,
        filters = filters.toAnimeSourceFilterSelections(),
        refresh = refresh,
    )
}

fun ChimahonAnimeSourceSearchUiState.toAnimeSourceSearchRequest(
    sourceIds: List<Long>? = null,
    page: Int = 1,
): ChimahonAnimeSourceSearchRequest {
    return ChimahonAnimeSourceSearchRequest(
        query = submittedQuery.ifBlank { query },
        sourceIds = sourceIds ?: groups.mapNotNull { it.source?.sourceId },
        page = page,
    )
}

fun ChimahonAnimeSourceBrowseItemUiModel.toAnimeSourceDetailLoadRequest(
    refresh: Boolean = false,
): ChimahonAnimeSourceDetailLoadRequest {
    return ChimahonAnimeSourceDetailLoadRequest(
        sourceId = sourceId,
        animeUrl = url,
        seed = toAnimeSourceAnime(),
        refresh = refresh,
    )
}

fun ChimahonAnimeSourceBrowseItemUiModel.toAnimeSourceAnime(): ChimahonAnimeSourceAnime {
    return ChimahonAnimeSourceAnime(
        sourceId = sourceId,
        url = url,
        title = title,
        artist = artist,
        author = author,
        thumbnailUrl = thumbnailUrl,
        backgroundUrl = backgroundUrl,
        description = description,
        genres = tags,
        initialized = initialized,
        inLibrary = inLibrary,
        libraryAnimeId = libraryAnimeId?.toLongOrNull(),
        sourceName = sourceName,
        sourceLanguage = sourceLanguage,
        latestEpisodeLabel = latestEpisodeLabel,
    )
}

fun ChimahonAnimeSourceBrowseMode.toAnimeSourceBrowseUiMode(): ChimahonAnimeSourceBrowseUiMode {
    return when (this) {
        ChimahonAnimeSourceBrowseMode.Popular -> ChimahonAnimeSourceBrowseUiMode.Popular
        ChimahonAnimeSourceBrowseMode.Latest -> ChimahonAnimeSourceBrowseUiMode.Latest
        ChimahonAnimeSourceBrowseMode.Search -> ChimahonAnimeSourceBrowseUiMode.Search
    }
}

fun ChimahonAnimeSourceBrowseUiMode.toAnimeSourceBrowseMode(): ChimahonAnimeSourceBrowseMode {
    return when (this) {
        ChimahonAnimeSourceBrowseUiMode.Popular -> ChimahonAnimeSourceBrowseMode.Popular
        ChimahonAnimeSourceBrowseUiMode.Latest -> ChimahonAnimeSourceBrowseMode.Latest
        ChimahonAnimeSourceBrowseUiMode.Search -> ChimahonAnimeSourceBrowseMode.Search
    }
}

private fun ChimahonAnimeSourceBrowseSource.toAnimeSourceBrowseHealth(): ChimahonAnimeSourceBrowseHealth {
    return when {
        !installed || !enabled -> ChimahonAnimeSourceBrowseHealth.Unavailable
        !unavailableReason.isNullOrBlank() -> ChimahonAnimeSourceBrowseHealth.Error
        else -> ChimahonAnimeSourceBrowseHealth.Ready
    }
}

private fun animeSourceListSubtitle(
    sources: List<ChimahonAnimeSourceBrowseSourceUiModel>,
    filters: ChimahonAnimeSourceListFilterState,
): String? {
    val visibleCount = sources.filteredForAnimeSourceBrowse(filters).size
    val totalCount = sources.size
    return when {
        totalCount == 0 -> null
        visibleCount == totalCount -> "$totalCount source(s)"
        else -> "$visibleCount of $totalCount source(s)"
    }
}

private fun List<ChimahonAnimeBackendError>.toAnimeSourceBrowseErrorMessage(): String? {
    return firstOrNull()?.let { error ->
        error.message ?: error.code.name
    }
}

private fun ChimahonAnimeFetchType.toAnimeSourceFetchTypeLabel(): String {
    return when (this) {
        ChimahonAnimeFetchType.Episodes -> "Episodes"
        ChimahonAnimeFetchType.Seasons -> "Seasons"
    }
}

private fun Long.toAnimeSourceDurationLabel(): String {
    val seconds = coerceAtLeast(0L)
    val hours = seconds / 3_600L
    val minutes = (seconds % 3_600L) / 60L
    return when {
        hours > 0L && minutes > 0L -> "${hours}h ${minutes}m"
        hours > 0L -> "${hours}h"
        minutes > 0L -> "${minutes}m"
        else -> "${seconds}s"
    }
}

private fun Double.trimmedAnimeSourceBrowseNumber(): String {
    val intValue = toInt()
    return if (this == intValue.toDouble()) {
        intValue.toString()
    } else {
        toString().trimEnd('0').trimEnd('.')
    }
}
