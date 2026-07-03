package app.chimahon.shared.detailui

import app.chimahon.shared.ChimahonChapterEntry
import app.chimahon.shared.ChimahonMangaEntry
import app.chimahon.shared.ChimahonRemoteChapterEntry
import app.chimahon.shared.ChimahonRemoteMangaDetail

fun ChimahonMangaEntry.toChimahonDetailHeaderModel(
    sourceName: String,
): ChimahonDetailHeaderModel {
    return ChimahonDetailHeaderModel(
        id = id.toString(),
        kind = ChimahonDetailKind.Library,
        title = title,
        sourceName = sourceName,
        thumbnailUrl = thumbnailUrl,
        author = author,
        artist = artist,
        description = description,
        genres = genres,
        status = status,
        url = url,
        favorite = favorite,
        initialized = initialized,
        inLibrary = favorite,
        notes = notes,
    )
}

fun ChimahonRemoteMangaDetail.toChimahonDetailHeaderModel(
    inLibrary: Boolean = false,
): ChimahonDetailHeaderModel {
    return ChimahonDetailHeaderModel(
        id = "$sourceId:$url",
        kind = ChimahonDetailKind.Remote,
        title = title,
        sourceName = sourceName,
        thumbnailUrl = thumbnailUrl,
        author = author,
        artist = artist,
        description = description,
        genres = genres,
        status = status,
        url = url,
        favorite = inLibrary,
        initialized = initialized,
        inLibrary = inLibrary,
    )
}

fun List<ChimahonChapterEntry>.toChimahonDetailChapterSummary(
    downloadedChapterIds: Set<Long> = emptySet(),
    selectedChapterIds: Set<Long> = emptySet(),
): ChimahonDetailChapterSummary {
    return ChimahonDetailChapterSummary(
        totalCount = size,
        readCount = count { it.read },
        unreadCount = count { !it.read },
        downloadedCount = count { it.id in downloadedChapterIds },
        bookmarkedCount = count { it.bookmarked },
        selectedCount = count { it.id in selectedChapterIds },
        latestUploadEpochMillis = map { it.dateUpload }.filter { it > 0L }.maxOrNull(),
    )
}

fun List<ChimahonRemoteChapterEntry>.toChimahonRemoteDetailChapterSummary(): ChimahonDetailChapterSummary {
    return ChimahonDetailChapterSummary(
        totalCount = size,
        latestUploadEpochMillis = map { it.dateUpload }.filter { it > 0L }.maxOrNull(),
    )
}

fun ChimahonDetailHeaderModel.defaultDetailActions(
    summary: ChimahonDetailChapterSummary = ChimahonDetailChapterSummary(),
): List<ChimahonDetailActionModel> {
    val startAction = if (summary.readCount > 0) {
        ChimahonDetailActionModel(
            action = ChimahonDetailAction.Resume,
            label = "Resume",
            enabled = summary.hasChapters,
        )
    } else {
        ChimahonDetailActionModel(
            action = ChimahonDetailAction.Start,
            label = "Start",
            enabled = summary.hasChapters,
        )
    }

    val libraryAction = if (kind == ChimahonDetailKind.Remote && !inLibrary) {
        ChimahonDetailActionModel(
            action = ChimahonDetailAction.AddToLibrary,
            label = "Add",
            selected = false,
        )
    } else {
        ChimahonDetailActionModel(
            action = ChimahonDetailAction.Favorite,
            label = if (favorite || inLibrary) "Library" else "Add",
            selected = favorite || inLibrary,
        )
    }

    return listOf(
        libraryAction,
        startAction,
        ChimahonDetailActionModel(
            action = ChimahonDetailAction.Tracking,
            label = "Tracking",
        ),
        ChimahonDetailActionModel(
            action = ChimahonDetailAction.Web,
            label = "Web",
            enabled = !url.isNullOrBlank(),
        ),
        ChimahonDetailActionModel(
            action = ChimahonDetailAction.Refresh,
            label = "Refresh",
        ),
    )
}

fun ChimahonDetailHeaderModel.detailChips(
    summary: ChimahonDetailChapterSummary = ChimahonDetailChapterSummary(),
): List<ChimahonDetailChipModel> {
    return buildList {
        add(ChimahonDetailChipModel(displayStatus, ChimahonDetailChipKind.Status))
        add(ChimahonDetailChipModel(sourceName, ChimahonDetailChipKind.Source))
        creatorLine?.let { add(ChimahonDetailChipModel(it, ChimahonDetailChipKind.Creator)) }
        if (inLibrary) add(ChimahonDetailChipModel("In library", ChimahonDetailChipKind.Library))
        if (!initialized) add(ChimahonDetailChipModel("Needs refresh", ChimahonDetailChipKind.Neutral))
        if (!sourceUrl.isNullOrBlank()) add(ChimahonDetailChipModel("Source URL", ChimahonDetailChipKind.Url))
        if (summary.totalCount > 0) {
            add(ChimahonDetailChipModel("${summary.totalCount} chapters", ChimahonDetailChipKind.Count))
        }
        if (summary.unreadCount > 0) {
            add(ChimahonDetailChipModel("${summary.unreadCount} unread", ChimahonDetailChipKind.Count))
        }
        if (summary.downloadedCount > 0) {
            add(ChimahonDetailChipModel("${summary.downloadedCount} downloaded", ChimahonDetailChipKind.Count))
        }
        if (summary.bookmarkedCount > 0) {
            add(ChimahonDetailChipModel("${summary.bookmarkedCount} bookmarked", ChimahonDetailChipKind.Count))
        }
        if (summary.selectedCount > 0) {
            add(ChimahonDetailChipModel("${summary.selectedCount} selected", ChimahonDetailChipKind.Count))
        }
    }
}

fun ChimahonDetailHeaderModel.genreChips(): List<ChimahonDetailChipModel> {
    return genres.map { genre ->
        ChimahonDetailChipModel(genre, ChimahonDetailChipKind.Genre)
    }
}

fun ChimahonDetailHeaderModel.defaultRelatedActions(
    showRecommendations: Boolean = true,
    showMerge: Boolean = kind == ChimahonDetailKind.Library,
): List<ChimahonDetailRelatedActionModel> {
    val detailSourceUrl = this.sourceUrl
    return buildList {
        add(
            ChimahonDetailRelatedActionModel(
                action = ChimahonDetailRelatedAction.BrowseSource,
                label = "Browse source",
                supportingLabel = sourceName,
            ),
        )
        add(
            ChimahonDetailRelatedActionModel(
                action = ChimahonDetailRelatedAction.OpenInWebView,
                label = "Open in WebView",
                enabled = !detailSourceUrl.isNullOrBlank(),
                url = detailSourceUrl,
            ),
        )
        add(
            ChimahonDetailRelatedActionModel(
                action = ChimahonDetailRelatedAction.OpenExternal,
                label = "Open externally",
                enabled = !detailSourceUrl.isNullOrBlank(),
                url = detailSourceUrl,
            ),
        )
        add(
            ChimahonDetailRelatedActionModel(
                action = ChimahonDetailRelatedAction.CopyUrl,
                label = "Copy source URL",
                enabled = !detailSourceUrl.isNullOrBlank(),
                url = detailSourceUrl,
            ),
        )
        if (showRecommendations) {
            add(
                ChimahonDetailRelatedActionModel(
                    action = ChimahonDetailRelatedAction.Recommendations,
                    label = "Recommendations",
                    enabled = title.isNotBlank(),
                ),
            )
        }
        if (showMerge) {
            add(
                ChimahonDetailRelatedActionModel(
                    action = ChimahonDetailRelatedAction.Merge,
                    label = "Merge with another source",
                    enabled = kind == ChimahonDetailKind.Library,
                ),
            )
        }
    }
}

fun ChimahonDetailChapterSummary.toChimahonDetailChapterControlsState(
    query: String = "",
    sortState: ChimahonDetailChapterSortState = ChimahonDetailChapterSortState(),
    filterState: ChimahonDetailChapterFilterState = ChimahonDetailChapterFilterState(),
    displayMode: ChimahonDetailChapterDisplayMode = ChimahonDetailChapterDisplayMode.SourceTitle,
    filtersVisible: Boolean = false,
): ChimahonDetailChapterControlsState {
    return ChimahonDetailChapterControlsState(
        summary = this,
        query = query,
        sortLabel = sortState.sort.title,
        filterLabel = filterState.label,
        displayLabel = displayMode.title,
        descending = sortState.descending,
        filtersVisible = filtersVisible,
        sortState = sortState,
        filterState = filterState,
        displayMode = displayMode,
    )
}
