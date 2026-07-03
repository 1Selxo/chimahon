package app.chimahon.shared.anime

data class ChimahonAnimeSourceBrowseSource(
    val id: Long,
    val name: String,
    val language: String = "",
    val iconUrl: String? = null,
    val baseUrl: String? = null,
    val extensionName: String? = null,
    val extensionVersion: String? = null,
    val installed: Boolean = true,
    val enabled: Boolean = true,
    val pinned: Boolean = false,
    val isLocal: Boolean = false,
    val isNsfw: Boolean = false,
    val supportsPopular: Boolean = true,
    val supportsLatest: Boolean = false,
    val supportsSearch: Boolean = true,
    val supportsFilters: Boolean = true,
    val lastUsedAtMillis: Long = 0L,
    val resultCount: Int? = null,
    val unavailableReason: String? = null,
) {
    val displayName: String
        get() = name.ifBlank {
            if (isLocal) {
                "Local anime"
            } else {
                "Source $id"
            }
        }

    val available: Boolean
        get() = installed && enabled && unavailableReason.isNullOrBlank()

    fun toSourceInfo(): ChimahonAnimeSourceInfo {
        return ChimahonAnimeSourceInfo(
            id = id,
            name = displayName,
            language = language,
            iconUrl = iconUrl,
            baseUrl = baseUrl,
            isLocal = isLocal,
            supportsSearch = supportsSearch,
            supportsLatest = supportsLatest,
        )
    }
}

data class ChimahonAnimeSourceListLoadRequest(
    val query: String = "",
    val enabledLanguages: List<String> = emptyList(),
    val pinnedSourceIds: Set<Long> = emptySet(),
    val pinnedOnly: Boolean = false,
    val installedOnly: Boolean = false,
    val enabledOnly: Boolean = false,
    val latestOnly: Boolean = false,
    val searchOnly: Boolean = false,
    val includeDisabled: Boolean = true,
    val includeUnavailable: Boolean = true,
    val includeNsfw: Boolean = true,
    val includeLocal: Boolean = true,
    val preferredMode: ChimahonAnimeSourceBrowseMode = ChimahonAnimeSourceBrowseMode.Popular,
)

data class ChimahonAnimeSourceListLoadResult(
    val sources: List<ChimahonAnimeSourceBrowseSource> = emptyList(),
    val loadedAtMillis: Long = 0L,
    val errors: List<ChimahonAnimeBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

data class ChimahonAnimeSourceBrowseRequest(
    val sourceId: Long,
    val mode: ChimahonAnimeSourceBrowseMode = ChimahonAnimeSourceBrowseMode.Popular,
    val page: Int = 1,
    val query: String = "",
    val filters: List<ChimahonAnimeSourceFilterSelection> = emptyList(),
    val refresh: Boolean = false,
    val includeLibraryState: Boolean = true,
) {
    val normalizedPage: Int
        get() = page.coerceAtLeast(1)

    val requiresQuery: Boolean
        get() = mode == ChimahonAnimeSourceBrowseMode.Search
}

data class ChimahonAnimeSourceSearchRequest(
    val query: String,
    val sourceIds: List<Long> = emptyList(),
    val page: Int = 1,
    val filtersBySourceId: Map<Long, List<ChimahonAnimeSourceFilterSelection>> = emptyMap(),
    val pinnedOnly: Boolean = false,
    val includePinnedSources: Boolean = true,
    val includeLibraryState: Boolean = true,
) {
    val normalizedQuery: String
        get() = query.trim()
}

data class ChimahonAnimeSourceBrowseResult(
    val request: ChimahonAnimeSourceBrowseRequest,
    val source: ChimahonAnimeSourceBrowseSource? = null,
    val entries: List<ChimahonAnimeSourceAnime> = emptyList(),
    val hasNextPage: Boolean = false,
    val loadedAtMillis: Long = 0L,
    val errors: List<ChimahonAnimeBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()

    val page: Int
        get() = request.normalizedPage

    val sourceId: Long
        get() = request.sourceId
}

data class ChimahonAnimeSourceSearchResult(
    val request: ChimahonAnimeSourceSearchRequest,
    val sourceResults: List<ChimahonAnimeSourceBrowseResult> = emptyList(),
    val loadedAtMillis: Long = 0L,
    val errors: List<ChimahonAnimeBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty() && sourceResults.all { it.successful }

    val entries: List<ChimahonAnimeSourceAnime>
        get() = sourceResults.flatMap { it.entries }
}

data class ChimahonAnimeSourceAnime(
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
    val fetchType: ChimahonAnimeFetchType = ChimahonAnimeFetchType.Episodes,
    val updateStrategy: ChimahonAnimeUpdateStrategy = ChimahonAnimeUpdateStrategy.Always,
    val initialized: Boolean = false,
    val inLibrary: Boolean = false,
    val libraryAnimeId: Long? = null,
    val sourceName: String? = null,
    val sourceLanguage: String = "",
    val rank: Int? = null,
    val latestEpisodeLabel: String? = null,
    val lastUpdatedAtMillis: Long = 0L,
) {
    val stableKey: String
        get() = listOf("anime-source", sourceId.toString(), url.ifBlank { title }).joinToString(":")

    fun toAnimeEntry(
        id: Long = libraryAnimeId ?: 0L,
        favorite: Boolean = inLibrary,
    ): ChimahonAnimeEntry {
        return ChimahonAnimeEntry(
            id = id,
            sourceId = sourceId,
            url = url,
            title = title,
            artist = artist,
            author = author,
            thumbnailUrl = thumbnailUrl,
            backgroundUrl = backgroundUrl,
            description = description,
            genres = genres,
            status = status,
            favorite = favorite,
            initialized = initialized,
            lastUpdate = lastUpdatedAtMillis,
            updateStrategy = updateStrategy,
            fetchType = fetchType,
        )
    }
}

data class ChimahonAnimeSourceEpisode(
    val url: String,
    val name: String,
    val episodeNumber: Double = -1.0,
    val scanlator: String? = null,
    val summary: String? = null,
    val previewUrl: String? = null,
    val totalSeconds: Long = 0L,
    val dateUpload: Long = -1L,
    val sourceOrder: Long = 0L,
) {
    val isRecognizedNumber: Boolean
        get() = episodeNumber >= 0.0
}

data class ChimahonAnimeSourceDetailLoadRequest(
    val sourceId: Long,
    val animeUrl: String,
    val seed: ChimahonAnimeSourceAnime? = null,
    val refresh: Boolean = false,
    val includeEpisodes: Boolean = true,
    val includeRelatedAnime: Boolean = true,
    val includeLibraryState: Boolean = true,
)

data class ChimahonAnimeSourceDetail(
    val source: ChimahonAnimeSourceBrowseSource? = null,
    val anime: ChimahonAnimeSourceAnime,
    val episodes: List<ChimahonAnimeSourceEpisode> = emptyList(),
    val relatedAnime: List<ChimahonAnimeSourceAnime> = emptyList(),
    val inLibrary: Boolean = anime.inLibrary,
    val libraryAnimeId: Long? = anime.libraryAnimeId,
)

data class ChimahonAnimeSourceDetailLoadResult(
    val request: ChimahonAnimeSourceDetailLoadRequest,
    val detail: ChimahonAnimeSourceDetail? = null,
    val loadedAtMillis: Long = 0L,
    val errors: List<ChimahonAnimeBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()

    val found: Boolean
        get() = detail != null
}

data class ChimahonAnimeSourceFiltersLoadRequest(
    val sourceId: Long,
    val query: String = "",
    val includeDefaults: Boolean = true,
)

data class ChimahonAnimeSourceFiltersLoadResult(
    val request: ChimahonAnimeSourceFiltersLoadRequest,
    val groups: List<ChimahonAnimeSourceFilterGroup> = emptyList(),
    val loadedAtMillis: Long = 0L,
    val errors: List<ChimahonAnimeBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()

    val selections: List<ChimahonAnimeSourceFilterSelection>
        get() = groups.flatMap { group -> group.filters.mapNotNull { it.selectionOrNull() } }
}

data class ChimahonAnimeSourceFilterGroup(
    val id: String,
    val title: String,
    val filters: List<ChimahonAnimeSourceFilter> = emptyList(),
    val initiallyExpanded: Boolean = true,
)

data class ChimahonAnimeSourceFilter(
    val id: String,
    val title: String,
    val kind: ChimahonAnimeSourceFilterKind,
    val options: List<ChimahonAnimeSourceFilterOption> = emptyList(),
    val value: ChimahonAnimeSourceFilterValue = ChimahonAnimeSourceFilterValue(),
    val enabled: Boolean = true,
) {
    val active: Boolean
        get() = value.isActive

    fun selectionOrNull(): ChimahonAnimeSourceFilterSelection? {
        return if (active) {
            ChimahonAnimeSourceFilterSelection(
                filterId = id,
                kind = kind,
                value = value,
            )
        } else {
            null
        }
    }
}

data class ChimahonAnimeSourceFilterOption(
    val id: String,
    val title: String,
    val selected: Boolean = false,
)

data class ChimahonAnimeSourceFilterValue(
    val text: String? = null,
    val checked: Boolean? = null,
    val selectedOptionId: String? = null,
    val selectedOptionIds: Set<String> = emptySet(),
    val triState: ChimahonAnimeSourceTriState = ChimahonAnimeSourceTriState.Ignore,
    val sortOptionId: String? = null,
    val sortAscending: Boolean = true,
) {
    val isActive: Boolean
        get() = !text.isNullOrBlank() ||
            checked == true ||
            !selectedOptionId.isNullOrBlank() ||
            selectedOptionIds.isNotEmpty() ||
            triState != ChimahonAnimeSourceTriState.Ignore ||
            !sortOptionId.isNullOrBlank()
}

data class ChimahonAnimeSourceFilterSelection(
    val filterId: String,
    val kind: ChimahonAnimeSourceFilterKind,
    val value: ChimahonAnimeSourceFilterValue,
)

enum class ChimahonAnimeSourceBrowseMode {
    Popular,
    Latest,
    Search,
}

enum class ChimahonAnimeSourceFilterKind {
    Header,
    Separator,
    Text,
    CheckBox,
    TriState,
    Select,
    MultiSelect,
    Sort,
}

enum class ChimahonAnimeSourceTriState {
    Ignore,
    Include,
    Exclude,
}

fun ChimahonAnimeSourceInfo.toAnimeSourceBrowseSource(
    extensionName: String? = null,
    installed: Boolean = true,
    enabled: Boolean = true,
    pinned: Boolean = false,
    supportsPopular: Boolean = true,
): ChimahonAnimeSourceBrowseSource {
    return ChimahonAnimeSourceBrowseSource(
        id = id,
        name = displayName,
        language = language,
        iconUrl = iconUrl,
        baseUrl = baseUrl,
        extensionName = extensionName,
        installed = installed,
        enabled = enabled,
        pinned = pinned,
        isLocal = isLocal,
        supportsPopular = supportsPopular,
        supportsLatest = supportsLatest,
        supportsSearch = supportsSearch,
    )
}

fun Iterable<ChimahonAnimeSourceInfo>.toAnimeSourceListLoadResult(
    loadedAtMillis: Long = 0L,
    pinnedSourceIds: Set<Long> = emptySet(),
): ChimahonAnimeSourceListLoadResult {
    return ChimahonAnimeSourceListLoadResult(
        sources = map { sourceInfo ->
            sourceInfo.toAnimeSourceBrowseSource(
                pinned = sourceInfo.id in pinnedSourceIds,
            )
        },
        loadedAtMillis = loadedAtMillis,
    )
}

fun Iterable<ChimahonAnimeSourceBrowseSource>.filteredForAnimeSourceListRequest(
    request: ChimahonAnimeSourceListLoadRequest,
): List<ChimahonAnimeSourceBrowseSource> {
    val normalizedQuery = request.query.trim()
    val languages = request.enabledLanguages
        .map { it.trim().lowercase() }
        .filter(String::isNotBlank)
        .toSet()
    return asSequence()
        .filter { source -> languages.isEmpty() || source.language.trim().lowercase() in languages }
        .filter { source -> request.includeDisabled || source.enabled }
        .filter { source -> request.includeUnavailable || source.available }
        .filter { source -> request.includeNsfw || !source.isNsfw }
        .filter { source -> request.includeLocal || !source.isLocal }
        .filter { source -> !request.pinnedOnly || source.pinned || source.id in request.pinnedSourceIds }
        .filter { source -> !request.installedOnly || source.installed }
        .filter { source -> !request.enabledOnly || source.enabled }
        .filter { source -> !request.latestOnly || source.supportsLatest }
        .filter { source -> !request.searchOnly || source.supportsSearch }
        .filter { source ->
            normalizedQuery.isBlank() ||
                source.displayName.contains(normalizedQuery, ignoreCase = true) ||
                source.extensionName.orEmpty().contains(normalizedQuery, ignoreCase = true) ||
                source.language.contains(normalizedQuery, ignoreCase = true)
        }
        .map { source ->
            if (source.id in request.pinnedSourceIds && !source.pinned) {
                source.copy(pinned = true)
            } else {
                source
            }
        }
        .toList()
}
