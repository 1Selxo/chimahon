package app.chimahon.shared.reader.appbars

enum class ReaderNavigationBarType {
    VerticalRight,
    VerticalLeft,
    Bottom,
}

enum class ReaderNavigatorOrientation {
    Horizontal,
    Vertical,
}

enum class ReaderReadingModeKind(
    val paged: Boolean,
    val rightToLeft: Boolean = false,
) {
    Webtoon(paged = false),
    ContinuousVertical(paged = false),
    LeftToRight(paged = true),
    RightToLeft(paged = true, rightToLeft = true),
}

enum class ReaderBottomButton(
    val key: String,
    val aliases: Set<String> = emptySet(),
    val contentDescription: String,
) {
    ViewChapters("vc", setOf("chapters"), "Chapters"),
    WebView("wb", setOf("source"), "Open in WebView"),
    Browser("br", setOf("browser"), "Open in browser"),
    Share("sh", setOf("share"), "Share"),
    ReadingMode("rm", setOf("mode"), "Reading mode"),
    Rotation("rot", setOf("rotation"), "Rotation"),
    CropBordersPager("cbp", setOf("crop"), "Crop borders"),
    CropBordersContinuousVertical("cbc", emptySet(), "Crop borders"),
    CropBordersWebtoon("cbw", emptySet(), "Crop borders"),
    PageLayout("pl", setOf("layout"), "Page layout"),
    ShiftPage("shift", setOf("double-page-shift"), "Shift double pages"),
    OcrLookup("ocr", setOf("lookup", "glens"), "OCR lookup"),
    MangaStats("ms", setOf("stats"), "Manga stats"),
    Settings("settings", emptySet(), "Reader settings"),
}

fun ReaderBottomButton.isEnabledIn(enabledButtons: Collection<String>): Boolean {
    return enabledButtons.any { saved -> saved == key || saved in aliases }
}

fun Collection<String>.hasReaderBottomButton(button: ReaderBottomButton): Boolean {
    return button.isEnabledIn(this)
}

fun ReaderReadingModeKind.cropButton(): ReaderBottomButton {
    return when (this) {
        ReaderReadingModeKind.Webtoon -> ReaderBottomButton.CropBordersWebtoon
        ReaderReadingModeKind.ContinuousVertical -> ReaderBottomButton.CropBordersContinuousVertical
        ReaderReadingModeKind.LeftToRight,
        ReaderReadingModeKind.RightToLeft,
        -> ReaderBottomButton.CropBordersPager
    }
}

data class ReaderOcrToggleState(
    val visible: Boolean = true,
    val enabled: Boolean = false,
    val loading: Boolean = false,
    val enableContentDescription: String = "Enable OCR",
    val disableContentDescription: String = "Disable OCR",
    val loadingContentDescription: String = "OCR loading",
) {
    val canToggle: Boolean
        get() = enabled || !loading

    val contentDescription: String
        get() = when {
            loading && !enabled -> loadingContentDescription
            enabled -> disableContentDescription
            else -> enableContentDescription
        }

    companion object {
        val Hidden = ReaderOcrToggleState(visible = false)
    }
}

data class ReaderTopBarState(
    val mangaTitle: String? = null,
    val chapterTitle: String? = null,
    val bookmarked: Boolean = false,
    val showBookmark: Boolean = true,
    val ocr: ReaderOcrToggleState = ReaderOcrToggleState.Hidden,
    val navigateUpContentDescription: String = "Navigate up",
    val bookmarkContentDescription: String = "Bookmark",
    val removeBookmarkContentDescription: String = "Remove bookmark",
    val openInWebViewContentDescription: String = "Open in WebView",
    val openInBrowserContentDescription: String = "Open in browser",
    val shareContentDescription: String = "Share",
    val overflowContentDescription: String = "More options",
)

data class ReaderTopBarActions(
    val navigateUp: () -> Unit = {},
    val onClickTopAppBar: () -> Unit = {},
    val onToggleBookmarked: () -> Unit = {},
    val onToggleOcr: (() -> Unit)? = null,
    val onOpenInWebView: (() -> Unit)? = null,
    val onOpenInBrowser: (() -> Unit)? = null,
    val onShare: (() -> Unit)? = null,
)

data class ReaderBottomBarState(
    val enabledButtons: Set<String> = ReaderBottomButton.entries.mapTo(mutableSetOf()) { it.key },
    val currentReadingMode: ReaderReadingModeKind = ReaderReadingModeKind.LeftToRight,
    val readingModeContentDescription: String = "Reading mode",
    val orientationContentDescription: String = "Rotation",
    val cropEnabled: Boolean = false,
    val dualPageSplitEnabled: Boolean = false,
    val doublePages: Boolean = false,
    val pageLayoutActive: Boolean = false,
    val shiftPageActive: Boolean = false,
    val mangaStatsActive: Boolean = false,
    val settingsActive: Boolean = false,
    val ocr: ReaderOcrToggleState = ReaderOcrToggleState.Hidden,
    val showSettingsButton: Boolean = true,
)

data class ReaderBottomBarActions(
    val onClickChapterList: (() -> Unit)? = null,
    val onClickWebView: (() -> Unit)? = null,
    val onClickBrowser: (() -> Unit)? = null,
    val onClickShare: (() -> Unit)? = null,
    val onClickReadingMode: (() -> Unit)? = null,
    val onClickOrientation: (() -> Unit)? = null,
    val onClickCropBorder: (() -> Unit)? = null,
    val onClickPageLayout: (() -> Unit)? = null,
    val onClickShiftPage: (() -> Unit)? = null,
    val onToggleOcrLookup: (() -> Unit)? = null,
    val onClickMangaStats: (() -> Unit)? = null,
    val onClickSettings: () -> Unit = {},
)

data class ReaderPageSliderState(
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val currentPageText: String = currentPage.toString(),
    val isRtl: Boolean = false,
    val contentDescription: String = "Page $currentPage of $totalPages",
) {
    val coercedTotalPages: Int
        get() = totalPages.coerceAtLeast(0)

    val coercedCurrentPage: Int
        get() = if (coercedTotalPages <= 0) 0 else currentPage.coerceIn(1, coercedTotalPages)

    val currentPageIndex: Int
        get() = (coercedCurrentPage - 1).coerceAtLeast(0)

    val progress: Float
        get() = when {
            coercedTotalPages <= 1 -> 0f
            else -> currentPageIndex.toFloat() / (coercedTotalPages - 1).toFloat()
        }
}

data class ReaderChapterNavigatorState(
    val isRtl: Boolean = false,
    val enabledNext: Boolean = false,
    val enabledPrevious: Boolean = false,
    val pageSlider: ReaderPageSliderState = ReaderPageSliderState(),
    val previousChapterContentDescription: String = "Previous chapter",
    val nextChapterContentDescription: String = "Next chapter",
)

data class ReaderChapterNavigatorActions(
    val onNextChapter: () -> Unit = {},
    val onPreviousChapter: () -> Unit = {},
    val onPageIndexChange: (Int) -> Unit = {},
)

data class ReaderAppBarsState(
    val visible: Boolean,
    val navBarType: ReaderNavigationBarType = ReaderNavigationBarType.Bottom,
    val topBar: ReaderTopBarState = ReaderTopBarState(),
    val bottomBar: ReaderBottomBarState = ReaderBottomBarState(),
    val navigator: ReaderChapterNavigatorState = ReaderChapterNavigatorState(),
)
