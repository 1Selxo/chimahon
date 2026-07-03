package app.chimahon.shared.novelui

data class ChimahonNovelLibraryUiState(
    val title: String = "Novels",
    val searchQuery: String = "",
    val categories: List<ChimahonNovelCategoryUiModel> = emptyList(),
    val books: List<ChimahonNovelBookUiModel> = emptyList(),
    val activeCategoryId: String? = null,
    val selectedBookIds: Set<String> = emptySet(),
    val displayMode: ChimahonNovelLibraryDisplayMode = ChimahonNovelLibraryDisplayMode.ComfortableGrid,
    val sort: ChimahonNovelLibrarySort = ChimahonNovelLibrarySort.DateAdded,
    val sortDescending: Boolean = true,
    val showCategoryTabs: Boolean = true,
    val showCategoryItemCount: Boolean = true,
    val showLanguageBadges: Boolean = true,
    val showGhostBadges: Boolean = true,
    val showProgressBadges: Boolean = true,
    val loading: Boolean = false,
    val importing: Boolean = false,
    val syncState: ChimahonNovelSyncUiState = ChimahonNovelSyncUiState(),
    val defaultCategoryLabel: String? = null,
    val storageLabel: String? = null,
    val errorMessage: String? = null,
) {
    val selectionMode: Boolean
        get() = selectedBookIds.isNotEmpty()

    val visibleCategories: List<ChimahonNovelCategoryUiModel>
        get() = categories.filterNot { it.hidden && it.itemCount == 0 }

    val activeCategory: ChimahonNovelCategoryUiModel?
        get() = visibleCategories.firstOrNull { it.id == activeCategoryId } ?: visibleCategories.firstOrNull()

    val visibleBooks: List<ChimahonNovelBookUiModel>
        get() = books
            .filter { book -> activeCategory?.contains(book) ?: true }
            .filter { book -> book.matches(searchQuery) }
            .sortedForNovelLibrary(sort, sortDescending)

    val contentState: ChimahonNovelContentState
        get() = when {
            loading -> ChimahonNovelContentState.Loading
            errorMessage != null -> ChimahonNovelContentState.Error
            books.isEmpty() -> ChimahonNovelContentState.Empty
            visibleBooks.isEmpty() -> ChimahonNovelContentState.FilteredEmpty
            else -> ChimahonNovelContentState.Content
        }

    val toolbarTitle: String
        get() {
            val categoryTitle = activeCategory?.title?.takeIf { showCategoryTabs.not() }
            val base = categoryTitle ?: title
            val count = if (showCategoryItemCount) visibleBooks.size else null
            return if (count != null) "$base ($count)" else base
        }
}

data class ChimahonNovelCategoryUiModel(
    val id: String?,
    val title: String,
    val itemCount: Int = 0,
    val isDefault: Boolean = false,
    val hidden: Boolean = false,
) {
    val displayTitle: String
        get() = if (itemCount > 0) "$title ($itemCount)" else title

    fun contains(book: ChimahonNovelBookUiModel): Boolean {
        return when {
            id == null -> true
            isDefault -> book.categoryIds.isEmpty() || id in book.categoryIds
            else -> id in book.categoryIds
        }
    }
}

data class ChimahonNovelSyncUiState(
    val providerName: String = "TTSU sync",
    val enabled: Boolean = false,
    val connected: Boolean = false,
    val syncing: Boolean = false,
    val progressPercent: Int? = null,
    val lastSyncLabel: String? = null,
    val summaryLabel: String? = null,
    val errorMessage: String? = null,
) {
    val statusLabel: String
        get() = when {
            syncing && progressPercent != null -> "Syncing ${progressPercent.coerceIn(0, 100)}%"
            syncing -> "Syncing"
            errorMessage != null -> "Sync failed"
            !enabled -> "Disabled"
            !connected -> "Not connected"
            lastSyncLabel != null -> lastSyncLabel
            else -> "Ready"
        }
}

data class ChimahonNovelBookUiModel(
    val id: String,
    val title: String,
    val author: String? = null,
    val language: String? = null,
    val coverUrl: String? = null,
    val folderLabel: String? = null,
    val hash: String? = null,
    val categoryIds: List<String> = emptyList(),
    val dateAddedEpochMillis: Long = 0L,
    val lastAccessEpochMillis: Long = 0L,
    val chapterStarts: List<Int> = emptyList(),
    val chapterCount: Int = chapterStarts.chapterCountFromStarts(),
    val characterCount: Int? = null,
    val readCharacterCount: Int = 0,
    val readTimeSeconds: Long = 0L,
    val progressPercent: Int? = null,
    val lastReadLabel: String? = null,
    val dateAddedLabel: String? = null,
    val ttuFolderName: String? = null,
    val dictionaryProfileName: String? = null,
    val ghost: Boolean = false,
) {
    val progressFraction: Float
        get() = when {
            progressPercent != null -> progressPercent.toFloat().coerceIn(0f, 100f) / 100f
            characterCount != null && characterCount > 0 -> readCharacterCount.toFloat()
                .div(characterCount.toFloat())
                .coerceIn(0f, 1f)
            else -> 0f
        }

    val progressLabel: String?
        get() = when {
            progressPercent != null -> "${progressPercent.coerceIn(0, 100)}%"
            characterCount != null && characterCount > 0 -> "${(progressFraction * 100f).toInt()}%"
            chapterCount > 0 -> "$chapterCount chapters"
            else -> null
        }

    val subtitle: String?
        get() = listOfNotNull(
            author?.takeIf(String::isNotBlank),
            language?.uppercase()?.takeIf(String::isNotBlank),
            lastReadLabel?.takeIf(String::isNotBlank),
        ).joinToString(" - ").takeIf(String::isNotBlank)

    val primaryBadge: String?
        get() = when {
            ghost -> "MISSING"
            !language.isNullOrBlank() -> language.uppercase()
            else -> progressLabel
        }

    fun stableLazyKey(index: Int): String {
        return "novel-book:$index:$id:${hash.orEmpty()}:${dateAddedEpochMillis}"
    }
}

data class ChimahonNovelDetailUiState(
    val novel: ChimahonNovelHeaderUiModel? = null,
    val volumes: List<ChimahonNovelVolumeUiModel> = emptyList(),
    val chapters: List<ChimahonNovelChapterUiModel> = emptyList(),
    val sortState: ChimahonNovelChapterSortState = ChimahonNovelChapterSortState(),
    val filterState: ChimahonNovelChapterFilterState = ChimahonNovelChapterFilterState(),
    val selectedChapterIds: Set<String> = emptySet(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val errorMessage: String? = null,
) {
    val visibleChapters: List<ChimahonNovelChapterUiModel>
        get() = chapters
            .filter { it.matches(filterState) }
            .sortedForNovelDetail(sortState)

    val contentState: ChimahonNovelContentState
        get() = when {
            loading && novel == null -> ChimahonNovelContentState.Loading
            errorMessage != null && novel == null -> ChimahonNovelContentState.Error
            novel == null -> ChimahonNovelContentState.Empty
            visibleChapters.isEmpty() -> ChimahonNovelContentState.FilteredEmpty
            else -> ChimahonNovelContentState.Content
        }
}

data class ChimahonNovelHeaderUiModel(
    val id: String,
    val title: String,
    val author: String? = null,
    val language: String? = null,
    val coverUrl: String? = null,
    val description: String? = null,
    val categoryLabels: List<String> = emptyList(),
    val sourceLabel: String = "Local EPUB",
    val dictionaryProfileName: String? = null,
    val ttuFolderName: String? = null,
    val chapterCount: Int = 0,
    val characterCount: Int? = null,
    val progressPercent: Int? = null,
    val lastReadLabel: String? = null,
    val dateAddedLabel: String? = null,
    val ghost: Boolean = false,
) {
    val creatorLine: String?
        get() = author?.takeIf(String::isNotBlank)

    val progressLabel: String
        get() = when {
            progressPercent != null -> "${progressPercent.coerceIn(0, 100)}% read"
            chapterCount > 0 -> "$chapterCount chapters"
            else -> "Not started"
        }
}

data class ChimahonNovelVolumeUiModel(
    val id: String,
    val title: String,
    val sourceOrder: Int,
    val chapterIds: List<String>,
) {
    fun stableLazyKey(index: Int): String {
        return "novel-volume:$index:$id:$sourceOrder"
    }
}

data class ChimahonNovelChapterUiModel(
    val id: String,
    val title: String,
    val href: String? = null,
    val sourceOrder: Int = Int.MAX_VALUE,
    val spineIndex: Int? = null,
    val volumeId: String? = null,
    val characterStart: Int? = null,
    val characterCount: Int? = null,
    val progress: Float = 0f,
    val read: Boolean = false,
    val current: Boolean = false,
    val imageOnly: Boolean = false,
    val bookmarkLabel: String? = null,
) {
    val sourceTitle: String
        get() = title.ifBlank { "Chapter ${(spineIndex ?: sourceOrder) + 1}" }

    val characterRangeLabel: String?
        get() = when {
            characterStart != null && characterCount != null ->
                "${characterStart + 1}-${characterStart + characterCount} chars"
            characterCount != null -> "$characterCount chars"
            else -> null
        }

    fun stableLazyKey(index: Int): String {
        return "novel-chapter:$index:$id:${href.orEmpty()}:$sourceOrder:${spineIndex ?: -1}"
    }
}

data class ChimahonNovelChapterSortState(
    val sort: ChimahonNovelChapterSort = ChimahonNovelChapterSort.SourceOrder,
    val descending: Boolean = false,
) {
    val directionLabel: String
        get() = when (sort) {
            ChimahonNovelChapterSort.SourceOrder -> if (descending) "Reverse source order" else "Source order"
            ChimahonNovelChapterSort.SpineIndex -> if (descending) "Last spine item first" else "First spine item first"
            ChimahonNovelChapterSort.Title -> if (descending) "Z-A" else "A-Z"
            ChimahonNovelChapterSort.CharacterCount -> if (descending) "Longest first" else "Shortest first"
            ChimahonNovelChapterSort.Progress -> if (descending) "Most read first" else "Least read first"
        }
}

enum class ChimahonNovelChapterSort(val title: String) {
    SourceOrder("Source order"),
    SpineIndex("Spine index"),
    Title("Title"),
    CharacterCount("Character count"),
    Progress("Progress"),
}

data class ChimahonNovelChapterFilterState(
    val read: ChimahonNovelReadFilter = ChimahonNovelReadFilter.Any,
    val imageOnly: ChimahonNovelImageOnlyFilter = ChimahonNovelImageOnlyFilter.Any,
    val volumeId: String? = null,
) {
    val activeCount: Int
        get() = listOf(
            read != ChimahonNovelReadFilter.Any,
            imageOnly != ChimahonNovelImageOnlyFilter.Any,
            !volumeId.isNullOrBlank(),
        ).count { it }
}

enum class ChimahonNovelReadFilter(val title: String) {
    Any("All"),
    Unread("Unread"),
    Read("Read"),
}

enum class ChimahonNovelImageOnlyFilter(val title: String) {
    Any("Any chapter"),
    Text("Text"),
    ImageOnly("Image-only"),
}

enum class ChimahonNovelLibraryDisplayMode(val title: String) {
    List("List"),
    CompactGrid("Compact grid"),
    ComfortableGrid("Comfortable grid"),
    CoverOnlyGrid("Cover-only grid"),
}

enum class ChimahonNovelLibrarySort(val title: String) {
    Alphabetical("Alphabetical"),
    DateAdded("Date added"),
    LastRead("Last read"),
    Author("Author"),
    Progress("Progress"),
}

enum class ChimahonNovelContentState {
    Loading,
    Empty,
    FilteredEmpty,
    Error,
    Content,
}

data class ChimahonNovelLibraryActions(
    val onSearchQueryChange: (String) -> Unit = {},
    val onCategorySelected: (String?) -> Unit = {},
    val onBookClick: (ChimahonNovelBookUiModel) -> Unit = {},
    val onBookLongClick: (ChimahonNovelBookUiModel) -> Unit = {},
    val onImportEpub: () -> Unit = {},
    val onRefresh: () -> Unit = {},
    val onSortClick: () -> Unit = {},
    val onDisplaySettingsClick: () -> Unit = {},
    val onEditCategories: () -> Unit = {},
    val onSelectAll: () -> Unit = {},
    val onInvertSelection: () -> Unit = {},
    val onClearSelection: () -> Unit = {},
    val onMoveCategory: () -> Unit = {},
    val onResetProgress: () -> Unit = {},
    val onEditBook: () -> Unit = {},
    val onDeleteSelected: () -> Unit = {},
    val onSyncImport: (() -> Unit)? = null,
    val onSyncExport: (() -> Unit)? = null,
    val onSyncAuto: (() -> Unit)? = null,
)

data class ChimahonNovelDetailActions(
    val onNavigateUp: () -> Unit = {},
    val onCoverClick: (ChimahonNovelHeaderUiModel) -> Unit = {},
    val onReadClick: (ChimahonNovelHeaderUiModel) -> Unit = {},
    val onEditInfoClick: (ChimahonNovelHeaderUiModel) -> Unit = {},
    val onMoveCategoryClick: (ChimahonNovelHeaderUiModel) -> Unit = {},
    val onDictionaryProfileClick: (ChimahonNovelHeaderUiModel) -> Unit = {},
    val onSyncClick: (ChimahonNovelHeaderUiModel) -> Unit = {},
    val onDeleteClick: (ChimahonNovelHeaderUiModel) -> Unit = {},
    val onChapterClick: (ChimahonNovelChapterUiModel) -> Unit = {},
    val onChapterLongClick: (ChimahonNovelChapterUiModel) -> Unit = {},
    val onSortChange: (ChimahonNovelChapterSortState) -> Unit = {},
    val onFilterClick: () -> Unit = {},
    val onRefresh: () -> Unit = {},
    val onRetry: () -> Unit = {},
)

data class ChimahonNovelReaderBarState(
    val title: String,
    val chapterTitle: String? = null,
    val progressLabel: String = "0%",
    val chapterIndex: Int = 0,
    val chapterCount: Int = 0,
    val focusMode: Boolean = false,
    val lookupActive: Boolean = false,
    val trackingActive: Boolean = false,
    val sasayakiAvailable: Boolean = false,
    val canGoPrevious: Boolean = false,
    val canGoNext: Boolean = false,
)

data class ChimahonNovelReaderBarActions(
    val onNavigateUp: () -> Unit = {},
    val onToggleHud: () -> Unit = {},
    val onPreviousChapter: () -> Unit = {},
    val onNextChapter: () -> Unit = {},
    val onOpenChapters: () -> Unit = {},
    val onOpenAppearance: () -> Unit = {},
    val onOpenStatistics: () -> Unit = {},
    val onOpenSasayaki: () -> Unit = {},
    val onToggleFocusMode: () -> Unit = {},
    val onDismissLookup: () -> Unit = {},
)

data class ChimahonNovelTypographyState(
    val fontSize: Float = 18f,
    val lineHeight: Float = 1.6f,
    val characterSpacing: Float = 0f,
    val paragraphSpacing: Float = 0f,
    val horizontalPaddingPercent: Float = 10f,
    val verticalPaddingPercent: Float = 10f,
    val selectedFont: String = "System Serif",
    val fontOptions: List<String> = listOf("System Serif", "System Sans", "Noto Serif", "Noto Sans"),
    val theme: ChimahonNovelReaderTheme = ChimahonNovelReaderTheme.System,
    val verticalWriting: Boolean = true,
    val justifyText: Boolean = false,
    val avoidPageBreak: Boolean = true,
    val hideFurigana: Boolean = false,
    val continuousMode: Boolean = false,
    val tapZonePercent: Float = 20f,
    val chapterSwipeDistance: Float = 96f,
    val keepScreenOn: Boolean = false,
    val volumeKeys: Boolean = false,
    val volumeKeysInverted: Boolean = false,
) {
    val modeLabel: String
        get() = if (continuousMode) "Continuous" else "Paginated"
}

enum class ChimahonNovelReaderTheme(val title: String) {
    System("System"),
    Light("Light"),
    Dark("Dark"),
    Sepia("Sepia"),
    PureBlack("Pure black"),
    Custom("Custom"),
}

data class ChimahonNovelSelectionUiState(
    val selectedText: String,
    val sentence: String = selectedText,
    val language: String? = null,
    val dictionaryProfileName: String? = null,
    val lookupInProgress: Boolean = false,
    val ankiEnabled: Boolean = true,
) {
    val wordLabel: String
        get() = selectedText.take(32).ifBlank { "Selection" }
}

enum class ChimahonNovelSelectionAction {
    Lookup,
    Copy,
    SearchWeb,
    AddToAnki,
    Translate,
    Dismiss,
}

fun List<ChimahonNovelBookUiModel>.sortedForNovelLibrary(
    sort: ChimahonNovelLibrarySort,
    descending: Boolean,
): List<ChimahonNovelBookUiModel> {
    val comparator = Comparator<IndexedValue<ChimahonNovelBookUiModel>> { left, right ->
        val base = when (sort) {
            ChimahonNovelLibrarySort.Alphabetical ->
                left.value.title.lowercase().compareTo(right.value.title.lowercase())
            ChimahonNovelLibrarySort.DateAdded ->
                left.value.dateAddedEpochMillis.compareTo(right.value.dateAddedEpochMillis)
            ChimahonNovelLibrarySort.LastRead ->
                left.value.lastAccessEpochMillis.compareTo(right.value.lastAccessEpochMillis)
            ChimahonNovelLibrarySort.Author ->
                left.value.author.orEmpty().lowercase().compareTo(right.value.author.orEmpty().lowercase())
            ChimahonNovelLibrarySort.Progress ->
                left.value.progressFraction.compareTo(right.value.progressFraction)
        }
        val directed = if (descending) -base else base
        if (directed != 0) directed else left.index.compareTo(right.index)
    }
    return withIndex().sortedWith(comparator).map { it.value }
}

fun List<ChimahonNovelChapterUiModel>.sortedForNovelDetail(
    sortState: ChimahonNovelChapterSortState,
): List<ChimahonNovelChapterUiModel> {
    val comparator = Comparator<IndexedValue<ChimahonNovelChapterUiModel>> { left, right ->
        val base = when (sortState.sort) {
            ChimahonNovelChapterSort.SourceOrder ->
                left.value.sourceOrder.compareTo(right.value.sourceOrder)
            ChimahonNovelChapterSort.SpineIndex ->
                (left.value.spineIndex ?: Int.MAX_VALUE).compareTo(right.value.spineIndex ?: Int.MAX_VALUE)
            ChimahonNovelChapterSort.Title ->
                left.value.sourceTitle.lowercase().compareTo(right.value.sourceTitle.lowercase())
            ChimahonNovelChapterSort.CharacterCount ->
                (left.value.characterCount ?: 0).compareTo(right.value.characterCount ?: 0)
            ChimahonNovelChapterSort.Progress ->
                left.value.progress.compareTo(right.value.progress)
        }
        val directed = if (sortState.descending) -base else base
        if (directed != 0) directed else left.index.compareTo(right.index)
    }
    return withIndex().sortedWith(comparator).map { it.value }
}

fun ChimahonNovelBookUiModel.matches(query: String): Boolean {
    if (query.isBlank()) return true
    val needle = query.trim().lowercase()
    val haystack = listOfNotNull(
        title,
        author,
        language,
        folderLabel,
        ttuFolderName,
        dictionaryProfileName,
    ).joinToString(" ").lowercase()
    return needle in haystack
}

fun ChimahonNovelChapterUiModel.matches(filter: ChimahonNovelChapterFilterState): Boolean {
    val readMatches = when (filter.read) {
        ChimahonNovelReadFilter.Any -> true
        ChimahonNovelReadFilter.Unread -> !read
        ChimahonNovelReadFilter.Read -> read
    }
    val imageMatches = when (filter.imageOnly) {
        ChimahonNovelImageOnlyFilter.Any -> true
        ChimahonNovelImageOnlyFilter.Text -> !imageOnly
        ChimahonNovelImageOnlyFilter.ImageOnly -> imageOnly
    }
    val volumeMatches = filter.volumeId.isNullOrBlank() || filter.volumeId == volumeId
    return readMatches && imageMatches && volumeMatches
}

private fun List<Int>.chapterCountFromStarts(): Int {
    return (size - 1).coerceAtLeast(0)
}
