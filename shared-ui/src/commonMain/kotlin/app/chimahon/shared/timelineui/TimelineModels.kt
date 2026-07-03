package app.chimahon.shared.timelineui

import app.chimahon.shared.ChimahonHistoryEntry
import app.chimahon.shared.ChimahonMangaEntry
import app.chimahon.shared.ChimahonRecentUpdateEntry

enum class TimelineSurfaceKind(val title: String) {
    Updates("Updates"),
    History("History"),
}

enum class TimelineUpdateGrouping(val title: String) {
    Date("Date"),
    DateAndSource("Date and source"),
    Source("Source"),
}

enum class TimelineRowDensity {
    Compact,
    Comfortable,
}

enum class TimelineBadgeKind {
    Info,
    Active,
    Success,
    Warning,
    Error,
    Muted,
}

enum class TimelineQuickActionIcon {
    Bookmark,
    BookmarkBorder,
    CheckCircle,
    Delete,
    DoneAll,
    Download,
    Favorite,
    FavoriteBorder,
    History,
    More,
    Open,
    Play,
    Read,
    Unread,
    Updates,
}

enum class TimelineSwipeActionSide {
    Start,
    End,
}

enum class TimelineSwipeVisualState {
    Idle,
    Revealed,
    Active,
    Confirmed,
    Disabled,
}

enum class TimelineUpdatesFilter(val title: String) {
    All("All"),
    Unread("Unread"),
    Started("Started"),
    Bookmarked("Bookmarked"),
}

enum class TimelineUpdatesSort(val title: String) {
    Newest("Newest"),
    Oldest("Oldest"),
    Manga("Manga"),
}

enum class TimelineHistoryFilter(val title: String) {
    All("All"),
    UnfinishedManga("Unfinished manga"),
    UnfinishedChapter("Unfinished chapter"),
    NonLibrary("Non-library"),
    Completed("Completed"),
}

enum class TimelineHistorySort(val title: String) {
    Recent("Recent"),
    Oldest("Oldest"),
    Title("Title"),
}

data class TimelineBadge(
    val text: String,
    val kind: TimelineBadgeKind = TimelineBadgeKind.Info,
    val icon: TimelineQuickActionIcon? = null,
)

data class TimelineQuickAction(
    val id: String,
    val label: String,
    val icon: TimelineQuickActionIcon,
    val enabled: Boolean = true,
    val active: Boolean = false,
    val destructive: Boolean = false,
    val onClick: () -> Unit,
)

data class TimelineSwipeActionVisual(
    val side: TimelineSwipeActionSide,
    val label: String,
    val icon: TimelineQuickActionIcon,
    val state: TimelineSwipeVisualState = TimelineSwipeVisualState.Idle,
    val destructive: Boolean = false,
)

data class TimelineDateGroup(
    val id: String,
    val title: String,
    val detail: String? = null,
    val rows: List<TimelineRow>,
) {
    val stableKey: String
        get() = id

    fun stableLazyKey(index: Int): String {
        return "timeline:group:${stableKey.ifBlank { index.toString() }}"
    }
}

sealed interface TimelineRow {
    val id: String
    val mangaId: Long
    val mangaTitle: String
    val thumbnailUrl: String?
    val selected: Boolean
    val badges: List<TimelineBadge>
    val startSwipe: TimelineSwipeActionVisual?
    val endSwipe: TimelineSwipeActionVisual?

    val stableKey: String
        get() = id

    fun stableLazyKey(index: Int): String {
        return "timeline:row:${stableKey.ifBlank { index.toString() }}"
    }
}

data class TimelineUpdateRow(
    override val id: String,
    override val mangaId: Long,
    val chapterId: Long,
    override val mangaTitle: String,
    val chapterTitle: String,
    val scanlator: String? = null,
    val sourceLabel: String? = null,
    val dateFetch: Long,
    val read: Boolean,
    val bookmarked: Boolean,
    val lastPageRead: Long,
    val leader: Boolean = true,
    val downloaded: Boolean = false,
    override val thumbnailUrl: String? = null,
    override val selected: Boolean = false,
    override val badges: List<TimelineBadge> = emptyList(),
    val quickActions: List<TimelineQuickAction> = emptyList(),
    override val startSwipe: TimelineSwipeActionVisual? = null,
    override val endSwipe: TimelineSwipeActionVisual? = null,
    val sourceId: Long? = null,
    val dateLabel: String? = null,
) : TimelineRow

data class TimelineHistoryRow(
    override val id: String,
    override val mangaId: Long,
    val chapterId: Long,
    override val mangaTitle: String,
    val chapterTitle: String,
    val sourceLabel: String? = null,
    val chapterNumberLabel: String? = null,
    val readAt: Long?,
    val readDuration: Long,
    val read: Boolean,
    val lastPageRead: Long,
    val totalCount: Double,
    val readCount: Double,
    val inLibrary: Boolean,
    override val thumbnailUrl: String? = null,
    override val selected: Boolean = false,
    override val badges: List<TimelineBadge> = emptyList(),
    val quickActions: List<TimelineQuickAction> = emptyList(),
    override val startSwipe: TimelineSwipeActionVisual? = null,
    override val endSwipe: TimelineSwipeActionVisual? = null,
    val sourceId: Long? = null,
    val readAtLabel: String? = null,
    val resumeLabel: String? = null,
    val progressFraction: Float? = null,
) : TimelineRow

data class TimelineFilterChip(
    val id: String,
    val label: String,
    val selected: Boolean = false,
    val enabled: Boolean = true,
    val badge: String? = null,
)

data class TimelineFilterBarState(
    val resultLabel: String? = null,
    val searchQuery: String = "",
    val searchPlaceholder: String = "Search",
    val filterChips: List<TimelineFilterChip> = emptyList(),
    val sortChips: List<TimelineFilterChip> = emptyList(),
    val activeBadges: List<TimelineBadge> = emptyList(),
)

data class TimelineUiState(
    val kind: TimelineSurfaceKind,
    val groups: List<TimelineDateGroup> = emptyList(),
    val filterBar: TimelineFilterBarState = TimelineFilterBarState(),
    val selectedIds: Set<String> = emptySet(),
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val emptyTitle: String = "Nothing here yet",
    val emptyDetail: String = "",
    val density: TimelineRowDensity = TimelineRowDensity.Comfortable,
) {
    val selectedCount: Int
        get() = selectedIds.size

    val totalRows: Int
        get() = groups.sumOf { it.rows.size }

    val selectionActive: Boolean
        get() = selectedIds.isNotEmpty()
}

fun ChimahonRecentUpdateEntry.toTimelineUpdateRow(
    manga: ChimahonMangaEntry? = null,
    selected: Boolean = false,
    leader: Boolean = true,
    downloaded: Boolean = false,
    sourceLabel: String? = null,
    quickActions: List<TimelineQuickAction> = emptyList(),
    startSwipe: TimelineSwipeActionVisual? = TimelineSwipeActionVisual(
        side = TimelineSwipeActionSide.Start,
        label = if (read) "Mark unread" else "Mark read",
        icon = if (read) TimelineQuickActionIcon.Unread else TimelineQuickActionIcon.DoneAll,
    ),
    endSwipe: TimelineSwipeActionVisual? = TimelineSwipeActionVisual(
        side = TimelineSwipeActionSide.End,
        label = "Download",
        icon = TimelineQuickActionIcon.Download,
    ),
): TimelineUpdateRow {
    return TimelineUpdateRow(
        id = "update:$chapterId",
        mangaId = mangaId,
        chapterId = chapterId,
        mangaTitle = mangaTitle,
        chapterTitle = chapterName,
        scanlator = scanlator,
        sourceId = this.sourceId,
        sourceLabel = sourceLabel,
        dateLabel = dateFetch.timelineDateBucket("Fetched"),
        dateFetch = dateFetch,
        read = read,
        bookmarked = bookmarked,
        lastPageRead = lastPageRead,
        leader = leader,
        downloaded = downloaded,
        thumbnailUrl = manga?.thumbnailUrl,
        selected = selected,
        badges = updateBadges(downloaded),
        quickActions = quickActions,
        startSwipe = startSwipe,
        endSwipe = endSwipe,
    )
}

fun ChimahonHistoryEntry.toTimelineHistoryRow(
    manga: ChimahonMangaEntry? = null,
    selected: Boolean = false,
    inLibrary: Boolean = manga?.favorite == true,
    sourceLabel: String? = null,
    quickActions: List<TimelineQuickAction> = emptyList(),
    startSwipe: TimelineSwipeActionVisual? = TimelineSwipeActionVisual(
        side = TimelineSwipeActionSide.Start,
        label = "Resume",
        icon = TimelineQuickActionIcon.Play,
    ),
    endSwipe: TimelineSwipeActionVisual? = TimelineSwipeActionVisual(
        side = TimelineSwipeActionSide.End,
        label = "Remove",
        icon = TimelineQuickActionIcon.Delete,
        destructive = true,
    ),
): TimelineHistoryRow {
    return TimelineHistoryRow(
        id = "history:$id",
        mangaId = mangaId,
        chapterId = chapterId,
        mangaTitle = title,
        chapterTitle = historyChapterTitle(),
        sourceLabel = sourceLabel,
        chapterNumberLabel = chapterNumber.timelineChapterNumberLabel(),
        sourceId = this.sourceId,
        readAt = readAt,
        readAtLabel = readAt?.timelineDateBucket("Read"),
        readDuration = readDuration,
        read = read,
        lastPageRead = lastPageRead,
        totalCount = totalCount,
        readCount = readCount,
        inLibrary = inLibrary,
        resumeLabel = historyResumeLabel(),
        progressFraction = historyProgressFraction(),
        thumbnailUrl = manga?.thumbnailUrl,
        selected = selected,
        badges = historyBadges(inLibrary),
        quickActions = quickActions,
        startSwipe = startSwipe,
        endSwipe = endSwipe,
    )
}

fun List<ChimahonRecentUpdateEntry>.toTimelineUpdateGroups(
    mangaById: Map<Long, ChimahonMangaEntry> = emptyMap(),
    selectedIds: Set<String> = emptySet(),
    downloadedChapterIds: Set<Long> = emptySet(),
    sourceLabels: Map<Long, String> = emptyMap(),
    sort: TimelineUpdatesSort = TimelineUpdatesSort.Newest,
    grouping: TimelineUpdateGrouping = TimelineUpdateGrouping.Date,
    nowEpochMillis: Long? = null,
): List<TimelineDateGroup> {
    val sorted = when (sort) {
        TimelineUpdatesSort.Newest -> sortedWith(
            compareByDescending<ChimahonRecentUpdateEntry> { it.dateFetch }
                .thenBy { it.mangaTitle.lowercase() }
                .thenBy { it.chapterName.lowercase() },
        )
        TimelineUpdatesSort.Oldest -> sortedWith(
            compareBy<ChimahonRecentUpdateEntry> { it.dateFetch }
                .thenBy { it.mangaTitle.lowercase() }
                .thenBy { it.chapterName.lowercase() },
        )
        TimelineUpdatesSort.Manga -> sortedWith(
            compareBy<ChimahonRecentUpdateEntry> { it.mangaTitle.lowercase() }
                .thenByDescending { it.dateFetch }
                .thenBy { it.chapterName.lowercase() },
        )
    }
    return sorted
        .groupBy { it.timelineUpdateGroupKey(grouping, sourceLabels, nowEpochMillis) }
        .map { (groupKey, entries) ->
            val rows = entries
                .groupBy { it.mangaId }
                .flatMap { (_, mangaUpdates) ->
                    mangaUpdates.mapIndexed { index, update ->
                        update.toTimelineUpdateRow(
                            manga = mangaById[update.mangaId],
                            selected = "update:${update.chapterId}" in selectedIds,
                            leader = index == 0,
                            downloaded = update.chapterId in downloadedChapterIds,
                            sourceLabel = sourceLabels[update.sourceId],
                        )
                    }
                }
            TimelineDateGroup(
                id = groupKey.id,
                title = groupKey.title,
                detail = listOfNotNull(groupKey.detail, entries.timelineUpdateCountLabel()).joinToString(" - "),
                rows = rows,
            )
        }
}

fun List<ChimahonHistoryEntry>.toTimelineHistoryGroups(
    mangaById: Map<Long, ChimahonMangaEntry> = emptyMap(),
    selectedIds: Set<String> = emptySet(),
    libraryMangaIds: Set<Long> = mangaById.values.filter { it.favorite }.mapTo(mutableSetOf()) { it.id },
    sourceLabels: Map<Long, String> = emptyMap(),
    sort: TimelineHistorySort = TimelineHistorySort.Recent,
    nowEpochMillis: Long? = null,
): List<TimelineDateGroup> {
    val sorted = when (sort) {
        TimelineHistorySort.Recent -> sortedByDescending { it.readAt ?: 0L }
        TimelineHistorySort.Oldest -> sortedBy { it.readAt ?: Long.MAX_VALUE }
        TimelineHistorySort.Title -> sortedWith(
            compareBy<ChimahonHistoryEntry> { it.title.lowercase() }
                .thenByDescending { it.readAt ?: 0L },
        )
    }
    return sorted
        .groupBy { it.readAt?.timelineDateBucket("Read", nowEpochMillis) ?: "Unknown date" }
        .map { (title, entries) ->
            TimelineDateGroup(
                id = "history:$title",
                title = title,
                detail = "${entries.map { it.mangaId }.distinct().size} manga - ${entries.size} item(s)",
                rows = entries.map { entry ->
                    entry.toTimelineHistoryRow(
                        manga = mangaById[entry.mangaId],
                        selected = "history:${entry.id}" in selectedIds,
                        inLibrary = entry.mangaId in libraryMangaIds,
                        sourceLabel = sourceLabels[entry.sourceId],
                    )
                },
            )
        }
}

fun ChimahonRecentUpdateEntry.matches(filter: TimelineUpdatesFilter): Boolean {
    return when (filter) {
        TimelineUpdatesFilter.All -> true
        TimelineUpdatesFilter.Unread -> !read
        TimelineUpdatesFilter.Started -> lastPageRead > 0L && !read
        TimelineUpdatesFilter.Bookmarked -> bookmarked
    }
}

fun ChimahonHistoryEntry.matches(
    filter: TimelineHistoryFilter,
    libraryMangaIds: Set<Long> = emptySet(),
): Boolean {
    return when (filter) {
        TimelineHistoryFilter.All -> true
        TimelineHistoryFilter.UnfinishedManga -> totalCount > 0.0 && readCount < totalCount
        TimelineHistoryFilter.UnfinishedChapter -> !read
        TimelineHistoryFilter.NonLibrary -> mangaId !in libraryMangaIds
        TimelineHistoryFilter.Completed -> read
    }
}

fun ChimahonHistoryEntry.matchesTimelineQuery(query: String): Boolean {
    return query.isBlank() || title.contains(query, ignoreCase = true) ||
        historyChapterTitle().contains(query, ignoreCase = true)
}

private data class TimelineUpdateGroupKey(
    val id: String,
    val title: String,
    val detail: String? = null,
)

private fun ChimahonRecentUpdateEntry.timelineUpdateGroupKey(
    grouping: TimelineUpdateGrouping,
    sourceLabels: Map<Long, String>,
    nowEpochMillis: Long?,
): TimelineUpdateGroupKey {
    val epochDay = dateFetch.timelineEpochDay()
    val dateTitle = dateFetch.timelineDateBucket("Fetched", nowEpochMillis)
    val sourceTitle = sourceLabels[sourceId]?.takeIf { it.isNotBlank() } ?: "Source $sourceId"
    return when (grouping) {
        TimelineUpdateGrouping.Date -> TimelineUpdateGroupKey(
            id = "updates:date:$epochDay",
            title = dateTitle,
        )
        TimelineUpdateGrouping.DateAndSource -> TimelineUpdateGroupKey(
            id = "updates:date:$epochDay:source:$sourceId",
            title = dateTitle,
            detail = sourceTitle,
        )
        TimelineUpdateGrouping.Source -> TimelineUpdateGroupKey(
            id = "updates:source:$sourceId",
            title = sourceTitle,
        )
    }
}

private fun List<ChimahonRecentUpdateEntry>.timelineUpdateCountLabel(): String {
    val mangaCount = map { it.mangaId }.distinct().size
    val itemCount = size
    val mangaLabel = if (mangaCount == 1) "1 manga" else "$mangaCount manga"
    val itemLabel = if (itemCount == 1) "1 chapter" else "$itemCount chapters"
    return "$mangaLabel - $itemLabel"
}

fun Long.timelineEpochMillis(): Long {
    if (this <= 0L) return 0L
    return if (this < 100_000_000_000L) this * 1_000L else this
}

fun Long.timelineEpochDay(): Long {
    val epochMillis = timelineEpochMillis()
    if (epochMillis <= 0L) return 0L
    return epochMillis / 86_400_000L
}

fun Long.timelineDateBucket(prefix: String, nowEpochMillis: Long? = null): String {
    if (this <= 0L) return "Unknown date"
    val epochDay = timelineEpochDay()
    val nowDay = nowEpochMillis?.timelineEpochDay()
    return when (nowDay?.minus(epochDay)) {
        0L -> "$prefix today"
        1L -> "$prefix yesterday"
        -1L -> "$prefix tomorrow"
        else -> "$prefix day $epochDay"
    }
}

fun Long.timelineReadingDurationLabel(): String {
    if (this <= 0L) return "Less than a minute"
    val minutes = ((this + 59L) / 60L).coerceAtLeast(1L)
    if (minutes < 60L) return "$minutes min"
    val hours = minutes / 60L
    val remainingMinutes = minutes % 60L
    return if (remainingMinutes == 0L) "$hours hr" else "$hours hr $remainingMinutes min"
}

fun Double.timelineChapterNumberLabel(): String? {
    if (this <= 0.0) return null
    val value = if (rem(1.0) == 0.0) toLong().toString() else toString()
    return "Ch. $value"
}

private fun ChimahonRecentUpdateEntry.updateBadges(downloaded: Boolean): List<TimelineBadge> {
    return buildList {
        if (!read) add(TimelineBadge("Unread", TimelineBadgeKind.Active))
        if (lastPageRead > 0L && !read) add(TimelineBadge("Page ${lastPageRead + 1}", TimelineBadgeKind.Warning))
        if (bookmarked) add(TimelineBadge("Bookmarked", TimelineBadgeKind.Success, TimelineQuickActionIcon.Bookmark))
        if (downloaded) add(TimelineBadge("Downloaded", TimelineBadgeKind.Success, TimelineQuickActionIcon.Download))
        scanlator?.takeIf { it.isNotBlank() }?.let { add(TimelineBadge(it, TimelineBadgeKind.Muted)) }
    }
}

private fun ChimahonHistoryEntry.historyBadges(inLibrary: Boolean): List<TimelineBadge> {
    return buildList {
        add(
            TimelineBadge(
                text = if (inLibrary) "In library" else "Not in library",
                kind = if (inLibrary) TimelineBadgeKind.Success else TimelineBadgeKind.Muted,
                icon = if (inLibrary) TimelineQuickActionIcon.Favorite else TimelineQuickActionIcon.FavoriteBorder,
            ),
        )
        if (!read) add(TimelineBadge("Unfinished chapter", TimelineBadgeKind.Warning))
        if (lastPageRead > 0L && !read) add(TimelineBadge("Page ${lastPageRead + 1}", TimelineBadgeKind.Warning))
        if (totalCount > 0.0) {
            add(TimelineBadge("${readCount.toLong()}/${totalCount.toLong()} read", TimelineBadgeKind.Info))
        }
        if (readDuration > 0L) add(TimelineBadge(readDuration.timelineReadingDurationLabel(), TimelineBadgeKind.Muted))
    }
}

private fun ChimahonHistoryEntry.historyChapterTitle(): String {
    return chapterNumber.timelineChapterNumberLabel() ?: "Chapter $chapterId"
}

private fun ChimahonHistoryEntry.historyResumeLabel(): String {
    return when {
        read -> "Read again"
        lastPageRead > 0L -> "Resume page ${lastPageRead + 1L}"
        readCount > 0.0 && totalCount > 0.0 -> "Resume ${readCount.toLong()}/${totalCount.toLong()}"
        else -> "Start"
    }
}

private fun ChimahonHistoryEntry.historyProgressFraction(): Float? {
    if (totalCount <= 0.0) return null
    return (readCount / totalCount).toFloat().coerceIn(0f, 1f)
}
