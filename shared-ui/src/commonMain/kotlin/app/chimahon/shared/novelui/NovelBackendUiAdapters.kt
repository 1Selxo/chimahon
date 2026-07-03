package app.chimahon.shared.novelui

fun ChimahonNovelLibraryLoadResult.toNovelLibraryUiState(
    query: String = "",
    activeCategoryId: String? = data.defaultCategoryId,
    selectedBookIds: Set<String> = emptySet(),
    displayMode: ChimahonNovelLibraryDisplayMode = ChimahonNovelLibraryDisplayMode.ComfortableGrid,
    sort: ChimahonNovelLibrarySort = ChimahonNovelLibrarySort.DateAdded,
    sortDescending: Boolean = true,
    loading: Boolean = false,
    title: String = "Novels",
    formatEpochMillis: (Long) -> String? = { null },
): ChimahonNovelLibraryUiState {
    return data.toNovelLibraryUiState(
        query = query,
        activeCategoryId = activeCategoryId,
        selectedBookIds = selectedBookIds,
        displayMode = displayMode,
        sort = sort,
        sortDescending = sortDescending,
        loading = loading,
        title = title,
        errorMessage = errors.toNovelBackendErrorMessage(),
        formatEpochMillis = formatEpochMillis,
    )
}

fun ChimahonNovelLibraryData.toNovelLibraryUiState(
    query: String = "",
    activeCategoryId: String? = defaultCategoryId,
    selectedBookIds: Set<String> = emptySet(),
    displayMode: ChimahonNovelLibraryDisplayMode = ChimahonNovelLibraryDisplayMode.ComfortableGrid,
    sort: ChimahonNovelLibrarySort = ChimahonNovelLibrarySort.DateAdded,
    sortDescending: Boolean = true,
    loading: Boolean = false,
    importing: Boolean = false,
    title: String = "Novels",
    errorMessage: String? = null,
    formatEpochMillis: (Long) -> String? = { null },
): ChimahonNovelLibraryUiState {
    val booksByCategory = books
        .flatMap { book ->
            if (book.categoryIds.isEmpty()) {
                listOf(null to book)
            } else {
                book.categoryIds.map { it to book }
            }
        }
        .groupBy({ it.first }, { it.second })

    return ChimahonNovelLibraryUiState(
        title = title,
        searchQuery = query,
        categories = categories.map { category ->
            category.toNovelCategoryUiModel(
                itemCount = booksByCategory[category.id]?.size ?: category.itemCount,
            )
        },
        books = books.map { it.toNovelBookUiModel(formatEpochMillis) },
        activeCategoryId = activeCategoryId,
        selectedBookIds = selectedBookIds,
        displayMode = displayMode,
        sort = sort,
        sortDescending = sortDescending,
        loading = loading,
        importing = importing,
        syncState = sync.toNovelSyncUiState(formatEpochMillis),
        defaultCategoryLabel = defaultCategoryLabel,
        storageLabel = storageLabel,
        errorMessage = errorMessage,
    )
}

fun ChimahonNovelDetailLoadResult.toNovelDetailUiState(
    selectedChapterIds: Set<String> = emptySet(),
    sortState: ChimahonNovelChapterSortState = ChimahonNovelChapterSortState(),
    filterState: ChimahonNovelChapterFilterState = ChimahonNovelChapterFilterState(),
    loading: Boolean = false,
    refreshing: Boolean = false,
    formatEpochMillis: (Long) -> String? = { null },
): ChimahonNovelDetailUiState {
    val detailData = detail
    val message = errors.toNovelBackendErrorMessage()
    if (detailData == null) {
        return ChimahonNovelDetailUiState(
            selectedChapterIds = selectedChapterIds,
            sortState = sortState,
            filterState = filterState,
            loading = loading,
            refreshing = refreshing,
            errorMessage = message,
        )
    }

    return detailData.toNovelDetailUiState(
        selectedChapterIds = selectedChapterIds,
        sortState = sortState,
        filterState = filterState,
        loading = loading,
        refreshing = refreshing,
        errorMessage = message,
        formatEpochMillis = formatEpochMillis,
    )
}

fun ChimahonNovelDetailData.toNovelDetailUiState(
    selectedChapterIds: Set<String> = emptySet(),
    sortState: ChimahonNovelChapterSortState = ChimahonNovelChapterSortState(),
    filterState: ChimahonNovelChapterFilterState = ChimahonNovelChapterFilterState(),
    loading: Boolean = false,
    refreshing: Boolean = false,
    errorMessage: String? = null,
    formatEpochMillis: (Long) -> String? = { null },
): ChimahonNovelDetailUiState {
    val activeChapterId = novel.progress.chapterId
    return ChimahonNovelDetailUiState(
        novel = novel.toNovelHeaderUiModel(
            categoryLabels = categories
                .filter { category -> category.id?.let { it in novel.categoryIds } == true }
                .map { it.title },
            sourceLabel = sourceInfo?.displayName ?: novel.source.title,
            formatEpochMillis = formatEpochMillis,
        ),
        volumes = volumes.map { it.toNovelVolumeUiModel() },
        chapters = chapters.map { chapter ->
            chapter.toNovelChapterUiModel(current = chapter.id == activeChapterId)
        },
        sortState = sortState,
        filterState = filterState,
        selectedChapterIds = selectedChapterIds,
        loading = loading,
        refreshing = refreshing,
        errorMessage = errorMessage,
    )
}

fun ChimahonNovelBookRecord.toNovelBookUiModel(
    formatEpochMillis: (Long) -> String? = { null },
): ChimahonNovelBookUiModel {
    return ChimahonNovelBookUiModel(
        id = id,
        title = displayTitle,
        author = author,
        language = language,
        coverUrl = coverUrl,
        folderLabel = folderLabel ?: fileName,
        hash = hash,
        categoryIds = categoryIds,
        dateAddedEpochMillis = dateAddedEpochMillis,
        lastAccessEpochMillis = lastAccessEpochMillis,
        chapterStarts = chapterStarts,
        chapterCount = chapterCount,
        characterCount = characterCount,
        readCharacterCount = progress.readCharacterCount,
        readTimeSeconds = progress.readTimeSeconds,
        progressPercent = progress.progressPercent(characterCount),
        lastReadLabel = progress.lastReadEpochMillis.takeIf { it > 0L }?.let(formatEpochMillis),
        dateAddedLabel = dateAddedEpochMillis.takeIf { it > 0L }?.let(formatEpochMillis),
        ttuFolderName = ttuFolderName,
        dictionaryProfileName = dictionaryProfileName,
        ghost = ghost,
    )
}

fun ChimahonNovelBookRecord.toNovelHeaderUiModel(
    categoryLabels: List<String> = emptyList(),
    sourceLabel: String? = null,
    formatEpochMillis: (Long) -> String? = { null },
): ChimahonNovelHeaderUiModel {
    return ChimahonNovelHeaderUiModel(
        id = id,
        title = displayTitle,
        author = author,
        language = language,
        coverUrl = coverUrl,
        description = metadata["description"],
        categoryLabels = categoryLabels,
        sourceLabel = sourceLabel ?: sourceInfo?.displayName ?: source.title,
        dictionaryProfileName = dictionaryProfileName,
        ttuFolderName = ttuFolderName,
        chapterCount = chapterCount,
        characterCount = characterCount,
        progressPercent = progress.progressPercent(characterCount),
        lastReadLabel = progress.lastReadEpochMillis.takeIf { it > 0L }?.let(formatEpochMillis),
        dateAddedLabel = dateAddedEpochMillis.takeIf { it > 0L }?.let(formatEpochMillis),
        ghost = ghost,
    )
}

fun ChimahonNovelCategoryRecord.toNovelCategoryUiModel(
    itemCount: Int = this.itemCount,
): ChimahonNovelCategoryUiModel {
    return ChimahonNovelCategoryUiModel(
        id = id,
        title = title,
        itemCount = itemCount,
        isDefault = isDefault,
        hidden = hidden,
    )
}

fun ChimahonNovelVolumeRecord.toNovelVolumeUiModel(): ChimahonNovelVolumeUiModel {
    return ChimahonNovelVolumeUiModel(
        id = id,
        title = title,
        sourceOrder = sourceOrder,
        chapterIds = chapterIds,
    )
}

fun ChimahonNovelChapterRecord.toNovelChapterUiModel(
    current: Boolean = false,
): ChimahonNovelChapterUiModel {
    return ChimahonNovelChapterUiModel(
        id = id,
        title = displayTitle,
        href = href,
        sourceOrder = sourceOrder,
        spineIndex = spineIndex,
        volumeId = volumeId,
        characterStart = characterStart,
        characterCount = characterCount,
        progress = progressFraction,
        read = effectivelyRead,
        current = current,
        imageOnly = imageOnly,
        bookmarkLabel = bookmarkLabel,
    )
}

fun ChimahonNovelSyncOverview.toNovelSyncUiState(
    formatEpochMillis: (Long) -> String? = { null },
): ChimahonNovelSyncUiState {
    return ChimahonNovelSyncUiState(
        providerName = providerName,
        enabled = enabled,
        connected = connected,
        syncing = syncing,
        progressPercent = progressPercent,
        lastSyncLabel = lastSyncLabel ?: lastSyncEpochMillis.takeIf { it > 0L }?.let(formatEpochMillis),
        summaryLabel = summaryLabel,
        errorMessage = errorMessage,
    )
}

private fun List<ChimahonNovelBackendError>.toNovelBackendErrorMessage(): String? {
    return firstOrNull()?.let { error ->
        error.message ?: error.code.name
    }
}
