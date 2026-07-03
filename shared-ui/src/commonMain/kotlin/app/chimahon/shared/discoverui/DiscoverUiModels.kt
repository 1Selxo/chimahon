package app.chimahon.shared.discoverui

import app.chimahon.shared.ChimahonRemoteMangaEntry
import app.chimahon.shared.ChimahonSourceBrowseMode
import app.chimahon.shared.ChimahonSourceEntry
import app.chimahon.shared.animeui.ChimahonAnimeLibraryEntryUiModel
import app.chimahon.shared.novelui.ChimahonNovelBookUiModel

const val ChimahonDiscoverPageSize: Int = 40
const val ChimahonDiscoverPrefetchItemCount: Int = 96

enum class ChimahonDiscoverContentType(
    val title: String,
    val pluralTitle: String,
    val shortLabel: String,
) {
    Manga("Manga", "Manga", "M"),
    Anime("Anime", "Anime", "A"),
    LightNovel("Light novel", "Light novels", "LN"),
}

enum class ChimahonDiscoverDisplayMode(val title: String) {
    Grid("Grid"),
    ComfortableList("List"),
    CompactList("Compact"),
}

enum class ChimahonDiscoverBrowseMode(val title: String) {
    Popular("Popular"),
    Latest("Latest"),
    Search("Search"),
    Library("Library"),
}

enum class ChimahonDiscoverSort(val title: String) {
    Relevance("Relevance"),
    Title("Title"),
    Source("Source"),
    Status("Status"),
}

enum class ChimahonDiscoverSourceHealth(val title: String) {
    Ready("Ready"),
    Loading("Loading"),
    Warning("Warning"),
    Error("Error"),
    Disabled("Disabled"),
}

data class ChimahonDiscoverBadge(
    val text: String,
    val tone: ChimahonDiscoverBadgeTone = ChimahonDiscoverBadgeTone.Neutral,
)

enum class ChimahonDiscoverBadgeTone {
    Neutral,
    Primary,
    Success,
    Warning,
    Error,
}

data class ChimahonDiscoverSourceUiModel(
    val id: String,
    val sourceId: Long? = null,
    val name: String,
    val contentType: ChimahonDiscoverContentType,
    val language: String = "",
    val supportsLatest: Boolean = false,
    val pinned: Boolean = false,
    val installed: Boolean = true,
    val extensionName: String? = null,
    val health: ChimahonDiscoverSourceHealth = ChimahonDiscoverSourceHealth.Ready,
    val healthMessage: String? = null,
    val resultCount: Int? = null,
    val badges: List<ChimahonDiscoverBadge> = emptyList(),
) {
    val lazyKey: String
        get() = "discover-source:${contentType.name}:$id:${sourceId ?: ""}"

    val displayLanguage: String?
        get() = language.trim().takeIf(String::isNotBlank)?.uppercase()

    val primaryMarker: String
        get() = name.discoverInitials(contentType.shortLabel)

    val effectiveBadges: List<ChimahonDiscoverBadge>
        get() = buildList {
            displayLanguage?.let { add(ChimahonDiscoverBadge(it, ChimahonDiscoverBadgeTone.Primary)) }
            if (supportsLatest) add(ChimahonDiscoverBadge("Latest", ChimahonDiscoverBadgeTone.Neutral))
            if (pinned) add(ChimahonDiscoverBadge("Pinned", ChimahonDiscoverBadgeTone.Success))
            if (!installed) add(ChimahonDiscoverBadge("Missing", ChimahonDiscoverBadgeTone.Warning))
            addAll(badges)
    }
}

data class ChimahonDiscoverExtensionHealthUiModel(
    val id: String,
    val name: String,
    val repositoryName: String? = null,
    val versionName: String? = null,
    val contentTypes: List<ChimahonDiscoverContentType> = emptyList(),
    val installedSourceCount: Int = 0,
    val availableSourceCount: Int = 0,
    val trusted: Boolean = true,
    val health: ChimahonDiscoverSourceHealth = ChimahonDiscoverSourceHealth.Ready,
    val message: String? = null,
) {
    val title: String
        get() = name.ifBlank { "Extension" }

    val subtitle: String
        get() = listOfNotNull(
            repositoryName?.takeIf(String::isNotBlank),
            versionName?.takeIf(String::isNotBlank),
            if (availableSourceCount > 0) "$installedSourceCount/$availableSourceCount sources" else "$installedSourceCount sources",
            message?.takeIf(String::isNotBlank),
        ).joinToString(" - ").ifBlank { health.title }

    val badges: List<ChimahonDiscoverBadge>
        get() = buildList {
            contentTypes.distinct().forEach { type ->
                add(ChimahonDiscoverBadge(type.shortLabel, ChimahonDiscoverBadgeTone.Primary))
            }
            if (trusted) {
                add(ChimahonDiscoverBadge("Trusted", ChimahonDiscoverBadgeTone.Success))
            } else {
                add(ChimahonDiscoverBadge("Untrusted", ChimahonDiscoverBadgeTone.Warning))
            }
            if (health != ChimahonDiscoverSourceHealth.Ready) {
                add(ChimahonDiscoverBadge(health.title, health.badgeTone))
            }
        }
}

data class ChimahonDiscoverSourceFilterState(
    val query: String = "",
    val contentType: ChimahonDiscoverContentType? = null,
    val language: String? = null,
    val pinnedOnly: Boolean = false,
    val latestOnly: Boolean = false,
    val installedOnly: Boolean = false,
    val sort: ChimahonDiscoverSort = ChimahonDiscoverSort.Title,
) {
    val hasActiveFilters: Boolean
        get() = query.isNotBlank() ||
            contentType != null ||
            !language.isNullOrBlank() ||
            pinnedOnly ||
            latestOnly ||
            installedOnly ||
            sort != ChimahonDiscoverSort.Title
}

val ChimahonDiscoverSourceHealth.badgeTone: ChimahonDiscoverBadgeTone
    get() = when (this) {
        ChimahonDiscoverSourceHealth.Ready -> ChimahonDiscoverBadgeTone.Success
        ChimahonDiscoverSourceHealth.Loading -> ChimahonDiscoverBadgeTone.Primary
        ChimahonDiscoverSourceHealth.Warning -> ChimahonDiscoverBadgeTone.Warning
        ChimahonDiscoverSourceHealth.Error -> ChimahonDiscoverBadgeTone.Error
        ChimahonDiscoverSourceHealth.Disabled -> ChimahonDiscoverBadgeTone.Neutral
    }

data class ChimahonDiscoverSourceListState(
    val sources: List<ChimahonDiscoverSourceUiModel> = emptyList(),
    val filters: ChimahonDiscoverSourceFilterState = ChimahonDiscoverSourceFilterState(),
    val displayMode: ChimahonDiscoverDisplayMode = ChimahonDiscoverDisplayMode.ComfortableList,
    val selectedSourceId: String? = null,
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val errorMessage: String? = null,
) {
    val visibleSources: List<ChimahonDiscoverSourceUiModel>
        get() = sources.filteredForDiscover(filters)

    val languageOptions: List<String>
        get() = sources
            .mapNotNull { it.displayLanguage }
            .distinct()
            .sorted()

    val contentTypeOptions: List<ChimahonDiscoverContentType>
        get() = sources
            .map { it.contentType }
            .distinct()
            .ifEmpty { ChimahonDiscoverContentType.entries }
}

data class ChimahonDiscoverMediaResult(
    val id: String,
    val contentType: ChimahonDiscoverContentType,
    val sourceId: Long? = null,
    val sourceName: String? = null,
    val title: String,
    val subtitle: String? = null,
    val author: String? = null,
    val artist: String? = null,
    val url: String = "",
    val thumbnailUrl: String? = null,
    val status: String? = null,
    val description: String? = null,
    val genres: List<String> = emptyList(),
    val initialized: Boolean = true,
    val inLibrary: Boolean = false,
    val progressLabel: String? = null,
    val updatedLabel: String? = null,
    val badges: List<ChimahonDiscoverBadge> = emptyList(),
) {
    val stableKey: String
        get() = "discover-media:${contentType.name}:${sourceId ?: "local"}:${id.ifBlank { url.ifBlank { title } }}"

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
            progressLabel?.takeIf(String::isNotBlank),
            updatedLabel?.takeIf(String::isNotBlank),
        ).joinToString(" - ").takeIf(String::isNotBlank)

    val marker: String
        get() = title.discoverInitials(contentType.shortLabel)

    fun effectiveBadges(): List<ChimahonDiscoverBadge> = buildList {
        if (inLibrary) add(ChimahonDiscoverBadge("Library", ChimahonDiscoverBadgeTone.Success))
        status?.takeIf(String::isNotBlank)?.let { add(ChimahonDiscoverBadge(it, ChimahonDiscoverBadgeTone.Neutral)) }
        progressLabel?.takeIf(String::isNotBlank)?.let { add(ChimahonDiscoverBadge(it, ChimahonDiscoverBadgeTone.Primary)) }
        addAll(badges)
    }
}

data class ChimahonDiscoverCatalogueState(
    val source: ChimahonDiscoverSourceUiModel? = null,
    val mode: ChimahonDiscoverBrowseMode = ChimahonDiscoverBrowseMode.Popular,
    val query: String = "",
    val results: List<ChimahonDiscoverMediaResult> = emptyList(),
    val visibleCount: Int = ChimahonDiscoverPageSize,
    val displayMode: ChimahonDiscoverDisplayMode = ChimahonDiscoverDisplayMode.Grid,
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val loadingMore: Boolean = false,
    val hasNextPage: Boolean = false,
    val errorMessage: String? = null,
    val health: ChimahonDiscoverSourceHealth = ChimahonDiscoverSourceHealth.Ready,
    val healthMessage: String? = null,
) {
    val visibleResults: List<ChimahonDiscoverMediaResult>
        get() = results.take(visibleCount.coerceAtLeast(0))

    val autoLoadState: ChimahonDiscoverAutoLoadState
        get() {
            val total = results.size
            return ChimahonDiscoverAutoLoadState(
                enabled = true,
                hasMore = hasNextPage || visibleCount < total,
                loading = loading || loadingMore,
                visibleCount = visibleResults.size,
                totalCount = if (hasNextPage) total + ChimahonDiscoverPageSize else total,
            )
        }
}

data class ChimahonDiscoverAutoLoadState(
    val enabled: Boolean,
    val hasMore: Boolean,
    val loading: Boolean = false,
    val visibleCount: Int = 0,
    val totalCount: Int = 0,
)

data class ChimahonDiscoverSourceActions(
    val onOpen: (ChimahonDiscoverSourceUiModel, ChimahonDiscoverBrowseMode) -> Unit = { _, _ -> },
    val onFiltersChange: (ChimahonDiscoverSourceFilterState) -> Unit = {},
    val onDisplayModeChange: (ChimahonDiscoverDisplayMode) -> Unit = {},
    val onTogglePinned: (ChimahonDiscoverSourceUiModel) -> Unit = {},
    val onOpenSettings: (ChimahonDiscoverSourceUiModel) -> Unit = {},
    val onRefresh: () -> Unit = {},
    val onResetFilters: () -> Unit = {},
)

data class ChimahonDiscoverCatalogueActions(
    val onBack: () -> Unit = {},
    val onSearchQueryChange: (String) -> Unit = {},
    val onModeChange: (ChimahonDiscoverBrowseMode) -> Unit = {},
    val onRefresh: () -> Unit = {},
    val onToggleSourcePinned: (ChimahonDiscoverSourceUiModel) -> Unit = {},
    val onOpenSourceSettings: (ChimahonDiscoverSourceUiModel) -> Unit = {},
    val onOpenResult: (ChimahonDiscoverMediaResult) -> Unit = {},
    val onLongPressResult: (ChimahonDiscoverMediaResult) -> Unit = {},
    val onLoadMore: () -> Unit = {},
)

fun List<ChimahonDiscoverSourceUiModel>.filteredForDiscover(
    filters: ChimahonDiscoverSourceFilterState,
): List<ChimahonDiscoverSourceUiModel> {
    val normalizedQuery = filters.query.trim()
    val normalizedLanguage = filters.language?.trim()?.lowercase()
    return asSequence()
        .filter { source -> filters.contentType == null || source.contentType == filters.contentType }
        .filter { source -> normalizedLanguage.isNullOrBlank() || source.language.trim().lowercase() == normalizedLanguage }
        .filter { source -> !filters.pinnedOnly || source.pinned }
        .filter { source -> !filters.latestOnly || source.supportsLatest }
        .filter { source -> !filters.installedOnly || source.installed }
        .filter { source ->
            normalizedQuery.isBlank() ||
                source.name.contains(normalizedQuery, ignoreCase = true) ||
                source.extensionName.orEmpty().contains(normalizedQuery, ignoreCase = true) ||
                source.language.contains(normalizedQuery, ignoreCase = true) ||
                source.contentType.title.contains(normalizedQuery, ignoreCase = true)
        }
        .let { entries ->
            when (filters.sort) {
                ChimahonDiscoverSort.Relevance -> entries.sortedWith(
                    compareByDescending<ChimahonDiscoverSourceUiModel> { it.pinned }
                        .thenByDescending { it.supportsLatest }
                        .thenBy { it.name.lowercase() },
                )
                ChimahonDiscoverSort.Title -> entries.sortedWith(
                    compareBy<ChimahonDiscoverSourceUiModel> { it.name.lowercase() }
                        .thenBy { it.contentType.name },
                )
                ChimahonDiscoverSort.Source -> entries.sortedWith(
                    compareBy<ChimahonDiscoverSourceUiModel> { it.extensionName.orEmpty().lowercase() }
                        .thenBy { it.name.lowercase() },
                )
                ChimahonDiscoverSort.Status -> entries.sortedWith(
                    compareBy<ChimahonDiscoverSourceUiModel> { it.health.ordinal }
                        .thenBy { it.name.lowercase() },
                )
            }
        }
        .toList()
}

fun ChimahonSourceEntry.toDiscoverSourceUiModel(
    contentType: ChimahonDiscoverContentType = ChimahonDiscoverContentType.Manga,
    pinned: Boolean = false,
    installed: Boolean = true,
    extensionName: String? = null,
    health: ChimahonDiscoverSourceHealth = ChimahonDiscoverSourceHealth.Ready,
): ChimahonDiscoverSourceUiModel {
    return ChimahonDiscoverSourceUiModel(
        id = "$contentType:$id",
        sourceId = id,
        name = name,
        contentType = contentType,
        language = language,
        supportsLatest = supportsLatest,
        pinned = pinned,
        installed = installed,
        extensionName = extensionName,
        health = health,
    )
}

fun ChimahonRemoteMangaEntry.toDiscoverMediaResult(
    sourceName: String,
    contentType: ChimahonDiscoverContentType = ChimahonDiscoverContentType.Manga,
): ChimahonDiscoverMediaResult {
    return ChimahonDiscoverMediaResult(
        id = url.ifBlank { "$sourceId:$title" },
        contentType = contentType,
        sourceId = sourceId,
        sourceName = sourceName,
        title = title,
        author = author,
        url = url,
        thumbnailUrl = thumbnailUrl,
        status = status.takeIf(String::isNotBlank),
        initialized = initialized,
    )
}

fun ChimahonAnimeLibraryEntryUiModel.toDiscoverMediaResult(): ChimahonDiscoverMediaResult {
    return ChimahonDiscoverMediaResult(
        id = id,
        contentType = ChimahonDiscoverContentType.Anime,
        sourceName = sourceName,
        title = title,
        subtitle = listOfNotNull(
            status?.takeIf(String::isNotBlank),
            sourceName?.takeIf(String::isNotBlank),
            progressLabel?.takeIf(String::isNotBlank),
        ).joinToString(" - ").takeIf(String::isNotBlank),
        url = id,
        thumbnailUrl = thumbnailUrl,
        status = status,
        inLibrary = favorite,
        progressLabel = progressLabel,
        updatedLabel = lastWatchedLabel ?: nextAiringLabel ?: latestEpisodeLabel,
        badges = listOfNotNull(
            language?.takeIf(String::isNotBlank)?.uppercase()?.let {
                ChimahonDiscoverBadge(it, ChimahonDiscoverBadgeTone.Primary)
            },
            if (downloadedCount > 0) ChimahonDiscoverBadge("$downloadedCount offline", ChimahonDiscoverBadgeTone.Success) else null,
            if (local) ChimahonDiscoverBadge("Local", ChimahonDiscoverBadgeTone.Neutral) else null,
        ),
    )
}

fun ChimahonNovelBookUiModel.toDiscoverMediaResult(): ChimahonDiscoverMediaResult {
    return ChimahonDiscoverMediaResult(
        id = id,
        contentType = ChimahonDiscoverContentType.LightNovel,
        title = title,
        author = author,
        url = id,
        thumbnailUrl = coverUrl,
        progressLabel = progressLabel,
        updatedLabel = lastReadLabel ?: dateAddedLabel,
        badges = listOfNotNull(
            language?.takeIf(String::isNotBlank)?.uppercase()?.let {
                ChimahonDiscoverBadge(it, ChimahonDiscoverBadgeTone.Primary)
            },
            if (ghost) ChimahonDiscoverBadge("Missing", ChimahonDiscoverBadgeTone.Warning) else null,
            folderLabel?.takeIf(String::isNotBlank)?.let { ChimahonDiscoverBadge(it, ChimahonDiscoverBadgeTone.Neutral) },
        ),
    )
}

fun ChimahonSourceBrowseMode.toDiscoverBrowseMode(): ChimahonDiscoverBrowseMode = when (this) {
    ChimahonSourceBrowseMode.Popular -> ChimahonDiscoverBrowseMode.Popular
    ChimahonSourceBrowseMode.Latest -> ChimahonDiscoverBrowseMode.Latest
    ChimahonSourceBrowseMode.Search -> ChimahonDiscoverBrowseMode.Search
}

fun ChimahonDiscoverBrowseMode.toSourceBrowseMode(): ChimahonSourceBrowseMode? = when (this) {
    ChimahonDiscoverBrowseMode.Popular -> ChimahonSourceBrowseMode.Popular
    ChimahonDiscoverBrowseMode.Latest -> ChimahonSourceBrowseMode.Latest
    ChimahonDiscoverBrowseMode.Search -> ChimahonSourceBrowseMode.Search
    ChimahonDiscoverBrowseMode.Library -> null
}

private fun String.discoverInitials(fallback: String): String {
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
