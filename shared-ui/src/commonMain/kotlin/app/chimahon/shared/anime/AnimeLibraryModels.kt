package app.chimahon.shared.anime

data class ChimahonAnimeLibraryEntry(
    val anime: ChimahonAnimeEntry,
    val episodes: List<ChimahonAnimeEpisodeEntry> = emptyList(),
    val categoryIds: List<Long> = emptyList(),
    val totalEpisodeCount: Int = episodes.size,
    val seenEpisodeCount: Int = episodes.count { it.seen },
    val bookmarkedEpisodeCount: Int = episodes.count { it.bookmark },
    val fillermarkedEpisodeCount: Int = episodes.count { it.fillermark },
    val downloadedEpisodeCount: Int = 0,
    val latestUpload: Long = episodes.maxOfOrNull { it.dateUpload } ?: 0L,
    val episodeFetchedAt: Long = episodes.maxOfOrNull { it.dateFetch } ?: 0L,
    val lastSeen: Long = 0L,
    val sourceName: String? = null,
    val sourceLanguage: String = "",
    val isLocal: Boolean = false,
) {
    val id: Long
        get() = anime.id

    val unseenEpisodeCount: Int
        get() = (totalEpisodeCount - seenEpisodeCount).coerceAtLeast(0)

    val hasDownloads: Boolean
        get() = downloadedEpisodeCount > 0 || isLocal

    val hasBookmarks: Boolean
        get() = bookmarkedEpisodeCount > 0

    val hasFillermarks: Boolean
        get() = fillermarkedEpisodeCount > 0

    val hasStarted: Boolean
        get() = seenEpisodeCount > 0 || episodes.any { it.isStarted }

    fun matches(query: String): Boolean {
        if (query.isBlank()) return true

        return anime.title.contains(query, ignoreCase = true) ||
            anime.author.containsQuery(query) ||
            anime.artist.containsQuery(query) ||
            anime.description.containsQuery(query) ||
            query.split(",").map(String::trim).filter(String::isNotEmpty).all { constraint ->
                matchesConstraint(constraint)
            }
    }

    private fun matchesConstraint(constraint: String): Boolean {
        val negated = constraint.startsWith("-")
        val value = if (negated) constraint.substringAfter("-").trimStart() else constraint
        val matched = sourceName.containsQuery(value) ||
            anime.genres.any { genre -> genre.equals(value, ignoreCase = true) }
        return if (negated) !matched else matched
    }
}

data class ChimahonAnimeLibraryData(
    val entries: List<ChimahonAnimeLibraryEntry>,
    val categories: List<ChimahonAnimeCategory>,
    val downloadedOnly: Boolean = false,
    val totalAnimeCount: Int = entries.size,
    val downloadedAnimeCount: Int = entries.count(ChimahonAnimeLibraryEntry::hasDownloads),
) {
    val totalEpisodeCount: Int
        get() = entries.sumOf { it.totalEpisodeCount }

    val unseenEpisodeCount: Int
        get() = entries.sumOf { it.unseenEpisodeCount }

    val displayCategories: List<ChimahonAnimeCategory>
        get() = if (categories.isEmpty()) listOf(defaultAnimeCategory()) else categories

    fun entriesForCategory(categoryId: Long): List<ChimahonAnimeLibraryEntry> {
        return entries.filter { entry ->
            val ids = entry.categoryIds.ifEmpty { listOf(CHIMAHON_ANIME_UNCATEGORIZED_ID) }
            categoryId in ids
        }
    }

    fun categorySummaries(): List<ChimahonAnimeCategorySummary> {
        return displayCategories.map { category ->
            val categoryEntries = entriesForCategory(category.id)
            ChimahonAnimeCategorySummary(
                category = category,
                animeCount = categoryEntries.size,
                unseenEpisodeCount = categoryEntries.sumOf { it.unseenEpisodeCount },
                downloadedEpisodeCount = categoryEntries.sumOf { it.downloadedEpisodeCount },
                bookmarkedEpisodeCount = categoryEntries.sumOf { it.bookmarkedEpisodeCount },
                fillermarkedEpisodeCount = categoryEntries.sumOf { it.fillermarkedEpisodeCount },
            )
        }
    }
}

data class ChimahonAnimeLibraryFilter(
    val query: String = "",
    val unseen: ChimahonAnimeTriState = ChimahonAnimeTriState.Disabled,
    val started: ChimahonAnimeTriState = ChimahonAnimeTriState.Disabled,
    val bookmarked: ChimahonAnimeTriState = ChimahonAnimeTriState.Disabled,
    val completed: ChimahonAnimeTriState = ChimahonAnimeTriState.Disabled,
    val downloaded: ChimahonAnimeTriState = ChimahonAnimeTriState.Disabled,
    val fillermarked: ChimahonAnimeTriState = ChimahonAnimeTriState.Disabled,
) {
    val hasActiveFilters: Boolean
        get() = query.isNotBlank() ||
            unseen != ChimahonAnimeTriState.Disabled ||
            started != ChimahonAnimeTriState.Disabled ||
            bookmarked != ChimahonAnimeTriState.Disabled ||
            completed != ChimahonAnimeTriState.Disabled ||
            downloaded != ChimahonAnimeTriState.Disabled ||
            fillermarked != ChimahonAnimeTriState.Disabled
}

fun List<ChimahonAnimeLibraryEntry>.applyAnimeLibraryFilter(
    filter: ChimahonAnimeLibraryFilter,
): List<ChimahonAnimeLibraryEntry> {
    return asSequence()
        .filter { filter.query.isBlank() || it.matches(filter.query) }
        .filterTriState(filter.unseen) { it.unseenEpisodeCount > 0 }
        .filterTriState(filter.started) { it.hasStarted }
        .filterTriState(filter.bookmarked) { it.hasBookmarks }
        .filterTriState(filter.completed) { it.anime.status == ChimahonAnimeStatus.Completed }
        .filterTriState(filter.downloaded) { it.hasDownloads }
        .filterTriState(filter.fillermarked) { it.hasFillermarks }
        .toList()
}

private fun Sequence<ChimahonAnimeLibraryEntry>.filterTriState(
    state: ChimahonAnimeTriState,
    predicate: (ChimahonAnimeLibraryEntry) -> Boolean,
): Sequence<ChimahonAnimeLibraryEntry> {
    return when (state) {
        ChimahonAnimeTriState.Disabled -> this
        ChimahonAnimeTriState.Include -> filter(predicate)
        ChimahonAnimeTriState.Exclude -> filterNot(predicate)
    }
}

private fun String?.containsQuery(query: String): Boolean {
    return this?.contains(query, ignoreCase = true) == true
}
