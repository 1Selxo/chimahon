package app.chimahon.shared.animeui

import app.chimahon.shared.anime.ChimahonAnimeSourceFilterKind
import app.chimahon.shared.anime.ChimahonAnimeSourceTriState

data class ChimahonAnimeSourceBrowseBadgeUiModel(
    val text: String,
    val tone: ChimahonAnimeSourceBrowseBadgeTone = ChimahonAnimeSourceBrowseBadgeTone.Neutral,
)

enum class ChimahonAnimeSourceBrowseBadgeTone {
    Neutral,
    Primary,
    Success,
    Warning,
    Error,
}

data class ChimahonAnimeSourceBrowseSourceUiModel(
    val id: String,
    val sourceId: Long,
    val name: String,
    val language: String = "",
    val iconUrl: String? = null,
    val baseUrl: String? = null,
    val extensionName: String? = null,
    val extensionVersion: String? = null,
    val installed: Boolean = true,
    val enabled: Boolean = true,
    val pinned: Boolean = false,
    val local: Boolean = false,
    val nsfw: Boolean = false,
    val supportsPopular: Boolean = true,
    val supportsLatest: Boolean = false,
    val supportsSearch: Boolean = true,
    val supportsFilters: Boolean = true,
    val health: ChimahonAnimeSourceBrowseHealth = ChimahonAnimeSourceBrowseHealth.Ready,
    val healthMessage: String? = null,
    val lastUsedAtMillis: Long = 0L,
    val lastUsedLabel: String? = null,
    val resultCount: Int? = null,
    val badges: List<ChimahonAnimeSourceBrowseBadgeUiModel> = emptyList(),
) {
    val lazyKey: String
        get() = "anime-source:$sourceId:$id"

    val available: Boolean
        get() = installed && enabled && health != ChimahonAnimeSourceBrowseHealth.Unavailable

    val displayLanguage: String?
        get() = language.trim().takeIf(String::isNotBlank)?.uppercase()

    val initials: String
        get() = name.toAnimeSourceBrowseInitials("A")

    val effectiveBadges: List<ChimahonAnimeSourceBrowseBadgeUiModel>
        get() = buildList {
            displayLanguage?.let { add(ChimahonAnimeSourceBrowseBadgeUiModel(it, ChimahonAnimeSourceBrowseBadgeTone.Primary)) }
            if (supportsLatest) add(ChimahonAnimeSourceBrowseBadgeUiModel("Latest"))
            if (supportsSearch) add(ChimahonAnimeSourceBrowseBadgeUiModel("Search"))
            if (pinned) add(ChimahonAnimeSourceBrowseBadgeUiModel("Pinned", ChimahonAnimeSourceBrowseBadgeTone.Success))
            if (local) add(ChimahonAnimeSourceBrowseBadgeUiModel("Local"))
            if (nsfw) add(ChimahonAnimeSourceBrowseBadgeUiModel("NSFW", ChimahonAnimeSourceBrowseBadgeTone.Warning))
            if (!installed) add(ChimahonAnimeSourceBrowseBadgeUiModel("Missing", ChimahonAnimeSourceBrowseBadgeTone.Warning))
            if (!enabled) add(ChimahonAnimeSourceBrowseBadgeUiModel("Disabled"))
            if (health != ChimahonAnimeSourceBrowseHealth.Ready) {
                add(ChimahonAnimeSourceBrowseBadgeUiModel(health.title, health.badgeTone))
            }
            addAll(badges)
        }
}

data class ChimahonAnimeSourceListUiState(
    val title: String = "Anime sources",
    val subtitle: String? = null,
    val sources: List<ChimahonAnimeSourceBrowseSourceUiModel> = emptyList(),
    val filters: ChimahonAnimeSourceListFilterState = ChimahonAnimeSourceListFilterState(),
    val displayMode: ChimahonAnimeSourceBrowseDisplayMode = ChimahonAnimeSourceBrowseDisplayMode.ComfortableList,
    val selectedSourceId: String? = null,
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val errorMessage: String? = null,
) {
    val visibleSources: List<ChimahonAnimeSourceBrowseSourceUiModel>
        get() = sources.filteredForAnimeSourceBrowse(filters)

    val languageOptions: List<String>
        get() = sources
            .mapNotNull { it.displayLanguage }
            .distinct()
            .sorted()

    val selectedSource: ChimahonAnimeSourceBrowseSourceUiModel?
        get() = sources.firstOrNull { it.id == selectedSourceId || it.sourceId.toString() == selectedSourceId }
}

data class ChimahonAnimeSourceListFilterState(
    val query: String = "",
    val language: String? = null,
    val pinnedOnly: Boolean = false,
    val installedOnly: Boolean = false,
    val enabledOnly: Boolean = false,
    val latestOnly: Boolean = false,
    val searchOnly: Boolean = false,
    val hideUnavailable: Boolean = false,
    val includeNsfw: Boolean = true,
    val sort: ChimahonAnimeSourceListSort = ChimahonAnimeSourceListSort.Name,
) {
    val activeCount: Int
        get() = listOf(
            query.isNotBlank(),
            !language.isNullOrBlank(),
            pinnedOnly,
            installedOnly,
            enabledOnly,
            latestOnly,
            searchOnly,
            hideUnavailable,
            !includeNsfw,
            sort != ChimahonAnimeSourceListSort.Name,
        ).count { it }

    val hasActiveFilters: Boolean
        get() = activeCount > 0
}

data class ChimahonAnimeSourceBrowseUiState(
    val source: ChimahonAnimeSourceBrowseSourceUiModel? = null,
    val mode: ChimahonAnimeSourceBrowseUiMode = ChimahonAnimeSourceBrowseUiMode.Popular,
    val query: String = "",
    val results: List<ChimahonAnimeSourceBrowseItemUiModel> = emptyList(),
    val filters: ChimahonAnimeSourceFilterUiState = ChimahonAnimeSourceFilterUiState(),
    val page: Int = 1,
    val hasNextPage: Boolean = false,
    val displayMode: ChimahonAnimeSourceBrowseDisplayMode = ChimahonAnimeSourceBrowseDisplayMode.Grid,
    val selectedResultId: String? = null,
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val loadingMore: Boolean = false,
    val errorMessage: String? = null,
    val contentState: ChimahonAnimeSourceBrowseContentState = when {
        loading -> ChimahonAnimeSourceBrowseContentState.Loading
        errorMessage != null -> ChimahonAnimeSourceBrowseContentState.Error
        results.isEmpty() -> ChimahonAnimeSourceBrowseContentState.Empty
        else -> ChimahonAnimeSourceBrowseContentState.Content
    },
) {
    val canSearch: Boolean
        get() = source?.supportsSearch != false

    val canOpenLatest: Boolean
        get() = source?.supportsLatest == true

    val canLoadMore: Boolean
        get() = hasNextPage && !loading && !loadingMore

    val title: String
        get() = source?.name ?: "Anime source"

    val subtitle: String?
        get() = listOfNotNull(
            mode.title,
            query.takeIf { mode == ChimahonAnimeSourceBrowseUiMode.Search && it.isNotBlank() },
            results.size.takeIf { it > 0 }?.let { "$it result(s)" },
        ).joinToString(" - ").takeIf(String::isNotBlank)
}

data class ChimahonAnimeSourceSearchUiState(
    val query: String = "",
    val submittedQuery: String = query,
    val groups: List<ChimahonAnimeSourceBrowseGroupUiModel> = emptyList(),
    val loadingSourceIds: Set<String> = emptySet(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val errorMessage: String? = null,
) {
    val results: List<ChimahonAnimeSourceBrowseItemUiModel>
        get() = groups.flatMap { it.results }

    val resultCount: Int
        get() = groups.sumOf { it.results.size }

    val hasPartialErrors: Boolean
        get() = groups.any { it.errorMessage != null }
}

data class ChimahonAnimeSourceBrowseGroupUiModel(
    val id: String,
    val source: ChimahonAnimeSourceBrowseSourceUiModel? = null,
    val mode: ChimahonAnimeSourceBrowseUiMode = ChimahonAnimeSourceBrowseUiMode.Search,
    val query: String = "",
    val results: List<ChimahonAnimeSourceBrowseItemUiModel> = emptyList(),
    val hasNextPage: Boolean = false,
    val loading: Boolean = false,
    val errorMessage: String? = null,
) {
    val title: String
        get() = source?.name ?: "Anime source"

    val subtitle: String?
        get() = listOfNotNull(
            source?.displayLanguage,
            results.size.takeIf { it > 0 }?.let { "$it result(s)" },
            errorMessage?.takeIf(String::isNotBlank),
        ).joinToString(" - ").takeIf(String::isNotBlank)
}

data class ChimahonAnimeSourceBrowseItemUiModel(
    val id: String,
    val sourceId: Long,
    val sourceName: String? = null,
    val sourceLanguage: String = "",
    val title: String,
    val subtitle: String? = null,
    val author: String? = null,
    val artist: String? = null,
    val url: String,
    val thumbnailUrl: String? = null,
    val backgroundUrl: String? = null,
    val status: String? = null,
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val initialized: Boolean = false,
    val inLibrary: Boolean = false,
    val libraryAnimeId: String? = null,
    val rankLabel: String? = null,
    val latestEpisodeLabel: String? = null,
    val updatedLabel: String? = null,
    val fetchTypeLabel: String? = null,
    val badges: List<ChimahonAnimeSourceBrowseBadgeUiModel> = emptyList(),
) {
    val stableKey: String
        get() = "anime-source-result:$sourceId:${id.ifBlank { url.ifBlank { title } }}"

    val marker: String
        get() = title.toAnimeSourceBrowseInitials("A")

    val primaryMetaLine: String?
        get() = listOfNotNull(
            subtitle?.takeIf(String::isNotBlank),
            author?.takeIf(String::isNotBlank),
            artist?.takeIf(String::isNotBlank),
        ).distinct().joinToString(" - ").takeIf(String::isNotBlank)

    val secondaryMetaLine: String?
        get() = listOfNotNull(
            sourceName?.takeIf(String::isNotBlank),
            status?.takeIf(String::isNotBlank),
            latestEpisodeLabel?.takeIf(String::isNotBlank),
            updatedLabel?.takeIf(String::isNotBlank),
        ).joinToString(" - ").takeIf(String::isNotBlank)

    val effectiveBadges: List<ChimahonAnimeSourceBrowseBadgeUiModel>
        get() = buildList {
            if (inLibrary) add(ChimahonAnimeSourceBrowseBadgeUiModel("Library", ChimahonAnimeSourceBrowseBadgeTone.Success))
            sourceLanguage.takeIf(String::isNotBlank)?.uppercase()?.let {
                add(ChimahonAnimeSourceBrowseBadgeUiModel(it, ChimahonAnimeSourceBrowseBadgeTone.Primary))
            }
            status?.takeIf(String::isNotBlank)?.let { add(ChimahonAnimeSourceBrowseBadgeUiModel(it)) }
            fetchTypeLabel?.takeIf(String::isNotBlank)?.let { add(ChimahonAnimeSourceBrowseBadgeUiModel(it)) }
            addAll(badges)
        }
}

data class ChimahonAnimeSourceDetailUiState(
    val source: ChimahonAnimeSourceBrowseSourceUiModel? = null,
    val anime: ChimahonAnimeSourceBrowseItemUiModel? = null,
    val episodes: List<ChimahonAnimeSourceEpisodeUiModel> = emptyList(),
    val relatedAnime: List<ChimahonAnimeSourceBrowseItemUiModel> = emptyList(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val errorMessage: String? = null,
    val contentState: ChimahonAnimeSourceBrowseContentState = when {
        loading -> ChimahonAnimeSourceBrowseContentState.Loading
        errorMessage != null -> ChimahonAnimeSourceBrowseContentState.Error
        anime == null -> ChimahonAnimeSourceBrowseContentState.Empty
        else -> ChimahonAnimeSourceBrowseContentState.Content
    },
) {
    val title: String
        get() = anime?.title ?: "Anime"

    val canAddToLibrary: Boolean
        get() = anime != null && !anime.inLibrary
}

data class ChimahonAnimeSourceEpisodeUiModel(
    val id: String,
    val title: String,
    val episodeNumber: Double? = null,
    val episodeMarker: String = episodeNumber?.trimmedAnimeSourceBrowseNumber() ?: "?",
    val subtitle: String? = null,
    val scanlator: String? = null,
    val uploadDateLabel: String? = null,
    val durationLabel: String? = null,
    val previewUrl: String? = null,
    val sourceOrder: Long = 0L,
    val url: String = id,
) {
    val stableKey: String
        get() = "anime-source-episode:$id:$sourceOrder:$url"
}

data class ChimahonAnimeSourceFilterUiState(
    val sourceId: Long? = null,
    val sourceName: String? = null,
    val query: String = "",
    val groups: List<ChimahonAnimeSourceFilterGroupUiModel> = emptyList(),
    val expandedGroupIds: Set<String> = groups.filter { it.expanded }.map { it.id }.toSet(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val errorMessage: String? = null,
) {
    val activeCount: Int
        get() = groups.sumOf { group -> group.filters.count { it.active } }

    val hasActiveFilters: Boolean
        get() = activeCount > 0 || query.isNotBlank()
}

data class ChimahonAnimeSourceFilterGroupUiModel(
    val id: String,
    val title: String,
    val filters: List<ChimahonAnimeSourceFilterControlUiModel> = emptyList(),
    val expanded: Boolean = true,
)

data class ChimahonAnimeSourceFilterControlUiModel(
    val id: String,
    val title: String,
    val kind: ChimahonAnimeSourceFilterKind,
    val options: List<ChimahonAnimeSourceFilterOptionUiModel> = emptyList(),
    val textValue: String? = null,
    val checked: Boolean? = null,
    val selectedOptionId: String? = null,
    val selectedOptionIds: Set<String> = emptySet(),
    val triState: ChimahonAnimeSourceTriState = ChimahonAnimeSourceTriState.Ignore,
    val sortOptionId: String? = null,
    val sortAscending: Boolean = true,
    val enabled: Boolean = true,
) {
    val active: Boolean
        get() = !textValue.isNullOrBlank() ||
            checked == true ||
            !selectedOptionId.isNullOrBlank() ||
            selectedOptionIds.isNotEmpty() ||
            triState != ChimahonAnimeSourceTriState.Ignore ||
            !sortOptionId.isNullOrBlank()

    val valueLabel: String?
        get() = when (kind) {
            ChimahonAnimeSourceFilterKind.Text -> textValue?.takeIf(String::isNotBlank)
            ChimahonAnimeSourceFilterKind.CheckBox -> if (checked == true) "On" else null
            ChimahonAnimeSourceFilterKind.TriState -> triState.takeIf { it != ChimahonAnimeSourceTriState.Ignore }?.name
            ChimahonAnimeSourceFilterKind.Select -> options.firstOrNull { it.id == selectedOptionId }?.title
            ChimahonAnimeSourceFilterKind.MultiSelect -> selectedOptionIds
                .mapNotNull { selectedId -> options.firstOrNull { it.id == selectedId }?.title }
                .joinToString(", ")
                .takeIf(String::isNotBlank)
            ChimahonAnimeSourceFilterKind.Sort -> options.firstOrNull { it.id == sortOptionId }?.title?.let { title ->
                if (sortAscending) "$title ascending" else "$title descending"
            }
            ChimahonAnimeSourceFilterKind.Header,
            ChimahonAnimeSourceFilterKind.Separator,
            -> null
        }
}

data class ChimahonAnimeSourceFilterOptionUiModel(
    val id: String,
    val title: String,
    val selected: Boolean = false,
)

data class ChimahonAnimeSourceListActions(
    val onOpenSource: (ChimahonAnimeSourceBrowseSourceUiModel, ChimahonAnimeSourceBrowseUiMode) -> Unit = { _, _ -> },
    val onFiltersChange: (ChimahonAnimeSourceListFilterState) -> Unit = {},
    val onDisplayModeChange: (ChimahonAnimeSourceBrowseDisplayMode) -> Unit = {},
    val onTogglePinned: (ChimahonAnimeSourceBrowseSourceUiModel) -> Unit = {},
    val onOpenSettings: (ChimahonAnimeSourceBrowseSourceUiModel) -> Unit = {},
    val onRefresh: () -> Unit = {},
)

data class ChimahonAnimeSourceBrowseActions(
    val onNavigateUp: () -> Unit = {},
    val onModeChange: (ChimahonAnimeSourceBrowseUiMode) -> Unit = {},
    val onQueryChange: (String) -> Unit = {},
    val onSubmitSearch: (String) -> Unit = {},
    val onFiltersChange: (ChimahonAnimeSourceFilterUiState) -> Unit = {},
    val onDisplayModeChange: (ChimahonAnimeSourceBrowseDisplayMode) -> Unit = {},
    val onOpenResult: (ChimahonAnimeSourceBrowseItemUiModel) -> Unit = {},
    val onLongPressResult: (ChimahonAnimeSourceBrowseItemUiModel) -> Unit = {},
    val onAddToLibrary: (ChimahonAnimeSourceBrowseItemUiModel) -> Unit = {},
    val onLoadMore: () -> Unit = {},
    val onRefresh: () -> Unit = {},
)

data class ChimahonAnimeSourceDetailActions(
    val onNavigateUp: () -> Unit = {},
    val onRefresh: () -> Unit = {},
    val onAddToLibrary: (ChimahonAnimeSourceBrowseItemUiModel) -> Unit = {},
    val onOpenEpisode: (ChimahonAnimeSourceEpisodeUiModel) -> Unit = {},
    val onOpenRelatedAnime: (ChimahonAnimeSourceBrowseItemUiModel) -> Unit = {},
    val onOpenInWebView: (ChimahonAnimeSourceBrowseItemUiModel) -> Unit = {},
)

enum class ChimahonAnimeSourceBrowseUiMode(val title: String) {
    Popular("Popular"),
    Latest("Latest"),
    Search("Search"),
}

enum class ChimahonAnimeSourceBrowseDisplayMode(val title: String) {
    Grid("Grid"),
    ComfortableList("List"),
    CompactList("Compact"),
}

enum class ChimahonAnimeSourceListSort(val title: String) {
    Name("Name"),
    Language("Language"),
    LastUsed("Last used"),
    Relevance("Relevance"),
}

enum class ChimahonAnimeSourceBrowseHealth(val title: String) {
    Ready("Ready"),
    Loading("Loading"),
    Warning("Warning"),
    Error("Error"),
    Unavailable("Unavailable"),
}

enum class ChimahonAnimeSourceBrowseContentState {
    Loading,
    Error,
    Empty,
    Content,
}

val ChimahonAnimeSourceBrowseHealth.badgeTone: ChimahonAnimeSourceBrowseBadgeTone
    get() = when (this) {
        ChimahonAnimeSourceBrowseHealth.Ready -> ChimahonAnimeSourceBrowseBadgeTone.Success
        ChimahonAnimeSourceBrowseHealth.Loading -> ChimahonAnimeSourceBrowseBadgeTone.Primary
        ChimahonAnimeSourceBrowseHealth.Warning -> ChimahonAnimeSourceBrowseBadgeTone.Warning
        ChimahonAnimeSourceBrowseHealth.Error -> ChimahonAnimeSourceBrowseBadgeTone.Error
        ChimahonAnimeSourceBrowseHealth.Unavailable -> ChimahonAnimeSourceBrowseBadgeTone.Neutral
    }

fun List<ChimahonAnimeSourceBrowseSourceUiModel>.filteredForAnimeSourceBrowse(
    filters: ChimahonAnimeSourceListFilterState,
): List<ChimahonAnimeSourceBrowseSourceUiModel> {
    val normalizedQuery = filters.query.trim()
    val normalizedLanguage = filters.language?.trim()?.lowercase()
    return asSequence()
        .filter { source -> normalizedLanguage.isNullOrBlank() || source.language.trim().lowercase() == normalizedLanguage }
        .filter { source -> !filters.pinnedOnly || source.pinned }
        .filter { source -> !filters.installedOnly || source.installed }
        .filter { source -> !filters.enabledOnly || source.enabled }
        .filter { source -> !filters.latestOnly || source.supportsLatest }
        .filter { source -> !filters.searchOnly || source.supportsSearch }
        .filter { source -> !filters.hideUnavailable || source.available }
        .filter { source -> filters.includeNsfw || !source.nsfw }
        .filter { source ->
            normalizedQuery.isBlank() ||
                source.name.contains(normalizedQuery, ignoreCase = true) ||
                source.extensionName.orEmpty().contains(normalizedQuery, ignoreCase = true) ||
                source.language.contains(normalizedQuery, ignoreCase = true)
        }
        .let { entries ->
            when (filters.sort) {
                ChimahonAnimeSourceListSort.Name -> entries.sortedWith(
                    compareByDescending<ChimahonAnimeSourceBrowseSourceUiModel> { it.pinned }
                        .thenBy { it.name.lowercase() },
                )
                ChimahonAnimeSourceListSort.Language -> entries.sortedWith(
                    compareBy<ChimahonAnimeSourceBrowseSourceUiModel> { it.language.lowercase() }
                        .thenByDescending { it.pinned }
                        .thenBy { it.name.lowercase() },
                )
                ChimahonAnimeSourceListSort.LastUsed -> entries.sortedWith(
                    compareByDescending<ChimahonAnimeSourceBrowseSourceUiModel> { it.lastUsedAtMillis }
                        .thenByDescending { it.pinned }
                        .thenBy { it.name.lowercase() },
                )
                ChimahonAnimeSourceListSort.Relevance -> entries.sortedWith(
                    compareByDescending<ChimahonAnimeSourceBrowseSourceUiModel> { it.pinned }
                        .thenByDescending { it.supportsLatest }
                        .thenByDescending { it.supportsSearch }
                        .thenBy { it.name.lowercase() },
                )
            }
        }
        .toList()
}

private fun String.toAnimeSourceBrowseInitials(fallback: String): String {
    val cleanWords = trim()
        .split(" ", "-", "_", ".", "/", "\\")
        .map(String::trim)
        .filter(String::isNotBlank)
    return cleanWords
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { fallback }
        .take(3)
}

private fun Double.trimmedAnimeSourceBrowseNumber(): String {
    val intValue = toInt()
    return if (this == intValue.toDouble()) {
        intValue.toString()
    } else {
        toString().trimEnd('0').trimEnd('.')
    }
}
