package app.chimahon.shared.mangaui

import app.chimahon.shared.ChimahonChapterDownloadStatus
import app.chimahon.shared.ChimahonChapterEntry
import app.chimahon.shared.ChimahonDownloadState
import app.chimahon.shared.ChimahonHistoryEntry
import app.chimahon.shared.ChimahonLibraryCategory
import app.chimahon.shared.ChimahonLibraryCoverRatio
import app.chimahon.shared.ChimahonLibraryDisplayMode
import app.chimahon.shared.ChimahonLibrarySettings
import app.chimahon.shared.ChimahonMangaEntry
import app.chimahon.shared.ChimahonRecentUpdateEntry

fun ChimahonMangaEntry.toMangaLibraryParityItem(
    sourceName: String? = null,
    language: String? = null,
    chapters: List<ChimahonChapterEntry> = emptyList(),
    downloadStatusesByChapterId: Map<Long, ChimahonChapterDownloadStatus> = emptyMap(),
    categoryIds: List<String> = emptyList(),
    categoryLabels: List<String> = emptyList(),
    lastUpdateLabel: String? = null,
    lastHistoryLabel: String? = null,
    tracked: Boolean = false,
    localSourceId: Long? = null,
    hasNewUpdates: Boolean = false,
    extraBadges: List<ChimahonMangaLibraryBadgeModel> = emptyList(),
    coverRatio: ChimahonMangaLibraryCoverRatio = ChimahonMangaLibraryCoverRatio.Automatic,
): ChimahonMangaLibraryItemModel {
    val downloadedCount = chapters.count { chapter ->
        downloadStatusesByChapterId[chapter.id]?.status == ChimahonDownloadState.Downloaded
    }
    val unreadCount = chapters.count { !it.read }
    return ChimahonMangaLibraryItemModel(
        id = id.toString(),
        title = title,
        subtitle = author ?: artist ?: sourceName,
        thumbnailUrl = thumbnailUrl,
        sourceName = sourceName,
        language = language,
        status = status,
        author = author,
        artist = artist,
        unreadCount = unreadCount,
        downloadedCount = downloadedCount,
        bookmarkedCount = chapters.count { it.bookmarked },
        totalChapterCount = chapters.size,
        latestChapterLabel = chapters.latestChapterLabel(),
        lastUpdateLabel = lastUpdateLabel,
        lastHistoryLabel = lastHistoryLabel,
        categoryIds = categoryIds,
        categoryLabels = categoryLabels,
        extraBadges = extraBadges,
        favorite = favorite,
        initialized = initialized,
        localSource = localSourceId != null && sourceId == localSourceId,
        tracked = tracked,
        hasNewUpdates = hasNewUpdates,
        coverRatio = coverRatio,
    )
}

fun List<ChimahonMangaEntry>.toMangaLibraryParityItems(
    sourceNamesById: Map<Long, String> = emptyMap(),
    languagesBySourceId: Map<Long, String> = emptyMap(),
    chaptersByMangaId: Map<Long, List<ChimahonChapterEntry>> = emptyMap(),
    downloadStatusesByChapterId: Map<Long, ChimahonChapterDownloadStatus> = emptyMap(),
    categoryIdsByMangaId: Map<Long, List<String>> = emptyMap(),
    categoryLabelsByMangaId: Map<Long, List<String>> = emptyMap(),
    lastUpdateLabelsByMangaId: Map<Long, String> = emptyMap(),
    lastHistoryLabelsByMangaId: Map<Long, String> = emptyMap(),
    trackedMangaIds: Set<Long> = emptySet(),
    updatedMangaIds: Set<Long> = emptySet(),
    localSourceId: Long? = null,
    coverRatio: ChimahonMangaLibraryCoverRatio = ChimahonMangaLibraryCoverRatio.Automatic,
): List<ChimahonMangaLibraryItemModel> {
    return map { manga ->
        manga.toMangaLibraryParityItem(
            sourceName = sourceNamesById[manga.sourceId],
            language = languagesBySourceId[manga.sourceId],
            chapters = chaptersByMangaId[manga.id].orEmpty(),
            downloadStatusesByChapterId = downloadStatusesByChapterId,
            categoryIds = categoryIdsByMangaId[manga.id].orEmpty(),
            categoryLabels = categoryLabelsByMangaId[manga.id].orEmpty(),
            lastUpdateLabel = lastUpdateLabelsByMangaId[manga.id],
            lastHistoryLabel = lastHistoryLabelsByMangaId[manga.id],
            tracked = manga.id in trackedMangaIds,
            localSourceId = localSourceId,
            hasNewUpdates = manga.id in updatedMangaIds,
            coverRatio = coverRatio,
        )
    }
}

fun List<ChimahonLibraryCategory>.toMangaLibraryCategoryModels(
    selectedCategoryId: Long?,
    countsByCategoryId: Map<Long, Int>,
    unreadCountsByCategoryId: Map<Long, Int> = emptyMap(),
    updateCountsByCategoryId: Map<Long, Int> = emptyMap(),
    downloadedCountsByCategoryId: Map<Long, Int> = emptyMap(),
    allCount: Int,
    includeAllCategory: Boolean = true,
): List<ChimahonMangaLibraryCategoryModel> {
    val categoryModels = sortedWith(compareBy<ChimahonLibraryCategory> { it.order }.thenBy { it.name.lowercase() })
        .map { category ->
            ChimahonMangaLibraryCategoryModel(
                id = category.id.toString(),
                title = category.displayTitle(),
                count = countsByCategoryId[category.id] ?: 0,
                selected = selectedCategoryId == category.id,
                hidden = category.hidden,
                isDefault = category.id == 0L || category.name.equals("Default", ignoreCase = true),
                unreadCount = unreadCountsByCategoryId[category.id] ?: 0,
                updateCount = updateCountsByCategoryId[category.id] ?: 0,
                downloadedCount = downloadedCountsByCategoryId[category.id] ?: 0,
            )
        }
    if (!includeAllCategory) return categoryModels
    return listOf(
        ChimahonMangaLibraryCategoryModel(
            id = null,
            title = "All",
            count = allCount,
            selected = selectedCategoryId == null,
            unreadCount = unreadCountsByCategoryId.values.sum(),
            updateCount = updateCountsByCategoryId.values.sum(),
            downloadedCount = downloadedCountsByCategoryId.values.sum(),
        ),
    ) + categoryModels
}

fun ChimahonLibrarySettings.toMangaLibraryDisplayOptions(): ChimahonMangaLibraryDisplayOptions {
    return ChimahonMangaLibraryDisplayOptions(
        showUnreadBadges = showUnreadBadges,
        showDownloadedBadges = showDownloadedBadges,
        showUpdateBadges = showUpdateCount,
        showHistoryBadges = showLastReadAt,
        showLocalBadges = showLocalBadges,
        showLanguageBadges = showLanguageBadges,
        showSourceBadges = showSourceBadges,
        showTrackingBadges = showTrackingBadges,
        showCategoryBadges = showCategoryItemCount,
        showLatestChapter = showLatestChapter,
        showLastUpdate = showUpdateCount,
        showLastHistory = showLastReadAt,
        showContinueButton = showContinueButtons,
    )
}

fun ChimahonLibraryDisplayMode.toMangaLibraryParityDisplayMode(): ChimahonMangaLibraryDisplayMode {
    return when (this) {
        ChimahonLibraryDisplayMode.ComfortableGrid -> ChimahonMangaLibraryDisplayMode.ComfortableGrid
        ChimahonLibraryDisplayMode.ComfortableGridPanorama -> ChimahonMangaLibraryDisplayMode.PanoramaGrid
        ChimahonLibraryDisplayMode.CompactGrid -> ChimahonMangaLibraryDisplayMode.CompactGrid
        ChimahonLibraryDisplayMode.CoverOnlyGrid -> ChimahonMangaLibraryDisplayMode.CoverOnlyGrid
        ChimahonLibraryDisplayMode.List -> ChimahonMangaLibraryDisplayMode.DetailedList
    }
}

fun ChimahonLibraryCoverRatio.toMangaLibraryParityCoverRatio(): ChimahonMangaLibraryCoverRatio {
    return when (this) {
        ChimahonLibraryCoverRatio.Automatic -> ChimahonMangaLibraryCoverRatio.Automatic
        ChimahonLibraryCoverRatio.Square -> ChimahonMangaLibraryCoverRatio.Square
        ChimahonLibraryCoverRatio.ThreeToFour -> ChimahonMangaLibraryCoverRatio.ThreeToFour
        ChimahonLibraryCoverRatio.TwoToThree -> ChimahonMangaLibraryCoverRatio.TwoToThree
        ChimahonLibraryCoverRatio.Original -> ChimahonMangaLibraryCoverRatio.Automatic
    }
}

fun ChimahonRecentUpdateEntry.toMangaLibraryUpdateBadge(
    label: String? = null,
): ChimahonMangaLibraryBadgeModel {
    return ChimahonMangaLibraryBadgeModel(
        label = label ?: chapterName.ifBlank { "Updated" },
        kind = ChimahonMangaLibraryBadgeKind.Update,
        contentDescription = "$mangaTitle updated",
    )
}

fun ChimahonHistoryEntry.toMangaLibraryHistoryBadge(
    label: String? = null,
): ChimahonMangaLibraryBadgeModel {
    return ChimahonMangaLibraryBadgeModel(
        label = label ?: if (lastPageRead > 0L) "Page ${lastPageRead + 1}" else "History",
        kind = ChimahonMangaLibraryBadgeKind.History,
        contentDescription = "$title reading history",
    )
}

fun List<ChimahonMangaLibraryItemModel>.filteredForMangaLibrary(
    query: String,
    categoryId: String? = null,
): List<ChimahonMangaLibraryItemModel> {
    return filter { item ->
        val queryMatches = if (query.isBlank()) {
            true
        } else {
            val needle = query.trim().lowercase()
            val haystack = buildList {
                add(item.title)
                item.subtitle?.let(::add)
                item.sourceName?.let(::add)
                item.status?.let(::add)
                addAll(item.categoryLabels)
            }.joinToString(" ").lowercase()
            needle in haystack
        }
        val categoryMatches = categoryId == null ||
            categoryId in item.categoryIds ||
            categoryId in item.categoryLabels
        queryMatches && categoryMatches
    }
}

private fun List<ChimahonChapterEntry>.latestChapterLabel(): String? {
    val latest = map { it.chapterNumber }.filter { it > 0.0 }.maxOrNull() ?: return null
    return "Ch. ${latest.toLibraryDisplayChapter()}"
}

private fun ChimahonLibraryCategory.displayTitle(): String {
    return name.takeUnless { it.isBlank() || it.equals("default", ignoreCase = true) } ?: "Default"
}

private fun Double.toLibraryDisplayChapter(): String {
    if (this % 1.0 == 0.0) return toLong().toString()
    return toString().trimEnd('0').trimEnd('.')
}
