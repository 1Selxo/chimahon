package app.chimahon.shared.novelreaderui

import kotlin.math.roundToInt

data class NovelReaderUiState(
    val title: String = "Untitled novel",
    val subtitle: String? = null,
    val sourceLabel: String? = null,
    val chapters: List<NovelReaderChapterUiModel> = emptyList(),
    val sections: List<NovelReaderSectionUiModel> = emptyList(),
    val pageParagraphs: List<NovelReaderParagraphUiModel> = emptyList(),
    val activeChapterId: String? = null,
    val activeSectionId: String? = null,
    val pageIndex: Int = 0,
    val pageCount: Int = 0,
    val progress: Float = 0f,
    val readingMode: NovelReadingMode = NovelReadingMode.Paged,
    val readingDirection: NovelReadingDirection = NovelReadingDirection.Default,
    val layout: NovelReaderLayoutState = NovelReaderLayoutState(),
    val hud: NovelReaderHudState = NovelReaderHudState(),
    val chrome: NovelReaderChromeState = NovelReaderChromeState(),
    val desktopControls: NovelReaderDesktopControlsState = NovelReaderDesktopControlsState(),
    val progressPersistence: NovelReaderProgressPersistenceUiState = NovelReaderProgressPersistenceUiState(),
    val selection: NovelReaderSelectionState? = null,
    val inputHints: List<NovelReaderInputHint> = NovelReaderDefaults.desktopInputHints,
    val drawerVisible: Boolean = false,
    val typographyVisible: Boolean = false,
    val loadingMessage: String? = null,
    val errorMessage: String? = null,
) {
    val activeChapter: NovelReaderChapterUiModel?
        get() = activeChapterId?.let { id -> chapters.firstOrNull { it.id == id } }
            ?: chapters.firstOrNull { it.current }
            ?: chapters.firstOrNull()

    val activeSection: NovelReaderSectionUiModel?
        get() = activeSectionId?.let { id -> sections.firstOrNull { it.id == id } }
            ?: activeChapter?.let { chapter -> sections.firstOrNull { it.chapterId == chapter.id } }
            ?: sections.firstOrNull()

    val visibleParagraphs: List<NovelReaderParagraphUiModel>
        get() = when {
            readingMode == NovelReadingMode.Paged && pageParagraphs.isNotEmpty() -> pageParagraphs
            activeSection != null -> activeSection!!.paragraphs
            activeChapter != null -> sections
                .filter { it.chapterId == activeChapter!!.id }
                .flatMap { it.paragraphs }
            else -> sections.flatMap { it.paragraphs }
        }

    val progressLabel: String
        get() {
            val percent = "${(progress.coerceIn(0f, 1f) * 100f).roundToInt()}%"
            return if (pageCount > 0) "${pageIndex + 1}/$pageCount - $percent" else percent
        }

    val contentState: NovelReaderContentState
        get() = when {
            loadingMessage != null -> NovelReaderContentState.Loading
            errorMessage != null -> NovelReaderContentState.Error
            visibleParagraphs.isEmpty() -> NovelReaderContentState.Empty
            else -> NovelReaderContentState.Content
        }
}

data class NovelReaderChapterUiModel(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val href: String? = null,
    val sourceOrder: Int = Int.MAX_VALUE,
    val spineIndex: Int? = null,
    val characterCount: Int? = null,
    val progress: Float = 0f,
    val current: Boolean = false,
    val read: Boolean = false,
    val bookmarked: Boolean = false,
    val imageOnly: Boolean = false,
    val sectionIds: List<String> = emptyList(),
) {
    val progressLabel: String?
        get() = when {
            characterCount != null -> "$characterCount chars"
            progress > 0f -> "${(progress.coerceIn(0f, 1f) * 100f).roundToInt()}%"
            else -> null
        }

    fun stableLazyKey(index: Int): String {
        return stableNovelChapterLazyKey(index, id, href, sourceOrder, spineIndex)
    }
}

data class NovelReaderSectionUiModel(
    val id: String,
    val chapterId: String,
    val title: String? = null,
    val sourceOrder: Int = Int.MAX_VALUE,
    val startOffset: Int? = null,
    val characterCount: Int? = null,
    val paragraphs: List<NovelReaderParagraphUiModel> = emptyList(),
) {
    val displayTitle: String
        get() = title?.takeIf(String::isNotBlank) ?: "Section ${sourceOrder + 1}"

    fun stableLazyKey(index: Int): String {
        return stableNovelSectionLazyKey(index, chapterId, id, sourceOrder)
    }
}

data class NovelReaderParagraphUiModel(
    val id: String,
    val text: String,
    val type: NovelReaderParagraphType = NovelReaderParagraphType.Body,
    val chapterId: String? = null,
    val sectionId: String? = null,
    val sourceOrder: Int = Int.MAX_VALUE,
    val startOffset: Int? = null,
    val selected: Boolean = false,
    val sasayakiCueId: String? = null,
) {
    val displayText: String
        get() = text.ifBlank { " " }

    fun stableLazyKey(index: Int): String {
        return stableNovelParagraphLazyKey(index, chapterId, sectionId, id, sourceOrder, startOffset)
    }
}

enum class NovelReaderParagraphType {
    Heading,
    Subheading,
    Body,
    Quote,
    Poem,
    Note,
    Divider,
}

enum class NovelReadingMode(val title: String) {
    Paged("Paged"),
    Continuous("Continuous"),
}

enum class NovelReadingDirection(val title: String) {
    Default("Default"),
    LeftToRight("Left to right"),
    RightToLeft("Right to left"),
    VerticalRightToLeft("Vertical right to left"),
}

enum class NovelReaderTheme(val title: String) {
    System("System"),
    Light("Light"),
    Dark("Dark"),
    Sepia("Sepia"),
    PureBlack("Pure black"),
    Custom("Custom"),
}

data class NovelReaderLayoutState(
    val fontSize: Float = 18f,
    val lineHeight: Float = 1.6f,
    val characterSpacing: Float = 0f,
    val paragraphSpacing: Float = 0.5f,
    val horizontalPaddingPercent: Float = 10f,
    val verticalPaddingPercent: Float = 10f,
    val selectedFont: String = "System Serif",
    val fontOptions: List<String> = listOf("System Serif", "System Sans", "Noto Serif", "Noto Sans", "Monospace"),
    val theme: NovelReaderTheme = NovelReaderTheme.System,
    val verticalWriting: Boolean = false,
    val justifyText: Boolean = false,
    val avoidPageBreak: Boolean = true,
    val hideFurigana: Boolean = false,
    val keepScreenOn: Boolean = false,
    val tapZonePercent: Float = 20f,
    val chapterSwipeDistance: Float = 96f,
    val backgroundColor: Int = 0xFFFFFFFF.toInt(),
    val textColor: Int = 0xFF111111.toInt(),
) {
    val modeBadges: List<String>
        get() = buildList {
            if (verticalWriting) add("Vertical")
            if (justifyText) add("Justified")
            if (hideFurigana) add("No furigana")
        }
}

data class NovelReaderHudState(
    val visible: Boolean = true,
    val focusMode: Boolean = false,
    val lookupActive: Boolean = false,
    val trackingActive: Boolean = false,
    val sasayakiAvailable: Boolean = false,
    val canGoPreviousPage: Boolean = true,
    val canGoNextPage: Boolean = true,
    val canGoPreviousChapter: Boolean = false,
    val canGoNextChapter: Boolean = false,
)

data class NovelReaderChromeState(
    val fullScreen: Boolean = false,
    val requestedFullScreen: Boolean = false,
    val controlsPinned: Boolean = false,
    val autoHideControls: Boolean = true,
    val showEdgeHints: Boolean = true,
    val topHint: String = "Enter shows controls",
    val bottomHint: String = "PageUp/PageDown turn pages - Home/End jump",
) {
    val immersive: Boolean
        get() = fullScreen || requestedFullScreen || autoHideControls

    val edgeHintsVisible: Boolean
        get() = showEdgeHints && immersive && !controlsPinned
}

data class NovelReaderDesktopControlsState(
    val enabled: Boolean = true,
    val keyboardEnabled: Boolean = true,
    val mouseWheelEnabled: Boolean = true,
    val arrowKeysEnabled: Boolean = true,
    val pageUpDownEnabled: Boolean = true,
    val homeEndEnabled: Boolean = true,
    val vimKeysEnabled: Boolean = true,
    val escapeClosesChrome: Boolean = true,
    val wheelBehavior: NovelReaderWheelBehavior = NovelReaderWheelBehavior.Auto,
    val invertWheelDirection: Boolean = false,
    val wheelStepThreshold: Float = 0.6f,
    val consumeHandledWheel: Boolean = true,
)

enum class NovelReaderWheelBehavior(val title: String) {
    Auto("Auto"),
    ScrollContent("Scroll content"),
    TurnPage("Turn pages"),
    ChangeChapter("Change chapters"),
    AdjustFontSize("Adjust font size"),
    Disabled("Disabled"),
}

data class NovelReaderProgressPersistencePolicy(
    val enabled: Boolean = true,
    val saveOnReaderOpened: Boolean = true,
    val saveOnPageChanged: Boolean = true,
    val saveOnChapterChanged: Boolean = true,
    val saveOnReaderClosed: Boolean = true,
    val savePeriodically: Boolean = true,
    val periodicIntervalSeconds: Long = 30L,
    val minimumProgressDelta: Float = 0.0025f,
    val minimumReadSecondsDelta: Long = 5L,
) {
    fun enabledFor(trigger: ChimahonNovelReaderProgressTrigger): Boolean {
        if (!enabled) return false
        return when (trigger) {
            ChimahonNovelReaderProgressTrigger.ReaderOpened -> saveOnReaderOpened
            ChimahonNovelReaderProgressTrigger.PageChanged -> saveOnPageChanged
            ChimahonNovelReaderProgressTrigger.ChapterChanged -> saveOnChapterChanged
            ChimahonNovelReaderProgressTrigger.ReaderClosed -> saveOnReaderClosed
            ChimahonNovelReaderProgressTrigger.Periodic -> savePeriodically
            ChimahonNovelReaderProgressTrigger.Sync -> true
        }
    }
}

data class NovelReaderProgressPersistenceUiState(
    val policy: NovelReaderProgressPersistencePolicy = NovelReaderProgressPersistencePolicy(),
    val pendingSave: Boolean = false,
    val lastSavedProgress: Float? = null,
    val lastSavedChapterId: String? = null,
    val lastSavedPageIndex: Int? = null,
    val lastSavedReadTimeSeconds: Long = 0L,
    val lastSavedEpochMillis: Long = 0L,
    val lastErrorMessage: String? = null,
) {
    val savedProgressLabel: String?
        get() = lastSavedProgress?.let { "${(it.coerceIn(0f, 1f) * 100f).roundToInt()}%" }
}

data class NovelReaderSelectionState(
    val selectedText: String,
    val sentence: String = selectedText,
    val language: String? = null,
    val dictionaryProfileName: String? = null,
    val lookupInProgress: Boolean = false,
    val ankiEnabled: Boolean = true,
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 0f,
    val height: Float = 0f,
    val handlesVisible: Boolean = true,
    val startHandle: NovelReaderSelectionHandleUiModel? = null,
    val endHandle: NovelReaderSelectionHandleUiModel? = null,
) {
    val title: String
        get() = selectedText.take(36).ifBlank { "Selection" }

    val anchored: Boolean
        get() = x != 0f || y != 0f || width > 0f || height > 0f || startHandle != null || endHandle != null

    val resolvedStartHandle: NovelReaderSelectionHandleUiModel
        get() = startHandle ?: NovelReaderSelectionHandleUiModel(
            role = NovelReaderSelectionHandleRole.Start,
            x = x,
            y = y + height,
        )

    val resolvedEndHandle: NovelReaderSelectionHandleUiModel
        get() = endHandle ?: NovelReaderSelectionHandleUiModel(
            role = NovelReaderSelectionHandleRole.End,
            x = x + width.coerceAtLeast(72f),
            y = y + height,
        )
}

data class NovelReaderSelectionHandleUiModel(
    val role: NovelReaderSelectionHandleRole,
    val x: Float,
    val y: Float,
)

enum class NovelReaderSelectionHandleRole {
    Start,
    End,
}

enum class NovelReaderSelectionAction {
    Lookup,
    Copy,
    SearchWeb,
    AddToAnki,
    Translate,
    Clear,
}

data class NovelReaderInputHint(
    val label: String,
    val shortcut: String,
    val kind: NovelReaderInputKind = NovelReaderInputKind.Keyboard,
)

enum class NovelReaderInputKind {
    Keyboard,
    Mouse,
    Touch,
}

enum class NovelReaderInputIntent {
    PreviousPage,
    NextPage,
    PreviousChapter,
    NextChapter,
    FirstPage,
    LastPage,
    ToggleHud,
    OpenChapters,
    CloseChapters,
    OpenTypography,
    CloseTypography,
    LookupSelection,
    ClearSelection,
    ToggleFullScreen,
}

enum class NovelReaderContentState {
    Loading,
    Empty,
    Error,
    Content,
}

data class NovelReaderActions(
    val onBack: () -> Unit = {},
    val onToggleHud: () -> Unit = {},
    val onToggleFocusMode: () -> Unit = {},
    val onOpenChapters: () -> Unit = {},
    val onCloseChapters: () -> Unit = {},
    val onOpenTypography: () -> Unit = {},
    val onCloseTypography: () -> Unit = {},
    val onLayoutChange: (NovelReaderLayoutState) -> Unit = {},
    val onOpenStatistics: () -> Unit = {},
    val onOpenSasayaki: () -> Unit = {},
    val onPreviousPage: () -> Unit = {},
    val onNextPage: () -> Unit = {},
    val onPreviousChapter: () -> Unit = {},
    val onNextChapter: () -> Unit = {},
    val onProgressChange: (Float) -> Unit = {},
    val onChapterSelected: (NovelReaderChapterUiModel) -> Unit = {},
    val onSectionSelected: (NovelReaderSectionUiModel) -> Unit = {},
    val onParagraphSelected: (NovelReaderParagraphUiModel) -> Unit = {},
    val onSelectionAction: (NovelReaderSelectionAction, NovelReaderSelectionState) -> Unit = { _, _ -> },
    val onSelectionHandleDrag: (NovelReaderSelectionHandleRole, Float, Float, NovelReaderSelectionState) -> Unit = { _, _, _, _ -> },
    val onToggleFullScreen: () -> Unit = {},
    val onInputIntent: (NovelReaderInputIntent) -> Unit = {},
    val onRetry: () -> Unit = {},
)

object NovelReaderDefaults {
    val desktopInputHints: List<NovelReaderInputHint> = listOf(
        NovelReaderInputHint("Page", "Left / Right / PageUp / PageDown"),
        NovelReaderInputHint("Jump", "Home / End"),
        NovelReaderInputHint("Chapter", "Shift+Left / Shift+Right"),
        NovelReaderInputHint("HUD", "Enter / Space"),
        NovelReaderInputHint("Lookup", "Alt+G"),
        NovelReaderInputHint("Full screen", "Alt+Enter"),
        NovelReaderInputHint("Mouse page", "Wheel", NovelReaderInputKind.Mouse),
    )

    val touchInputHints: List<NovelReaderInputHint> = listOf(
        NovelReaderInputHint("Page", "Left / right tap", NovelReaderInputKind.Touch),
        NovelReaderInputHint("HUD", "Top / bottom tap", NovelReaderInputKind.Touch),
        NovelReaderInputHint("Lookup", "Select text", NovelReaderInputKind.Touch),
    )
}

fun stableNovelChapterLazyKey(
    index: Int,
    id: String,
    href: String?,
    sourceOrder: Int,
    spineIndex: Int?,
): String = "novel-reader-chapter:$index:$id:${href.orEmpty()}:$sourceOrder:${spineIndex ?: -1}"

fun stableNovelSectionLazyKey(
    index: Int,
    chapterId: String,
    id: String,
    sourceOrder: Int,
): String = "novel-reader-section:$index:$chapterId:$id:$sourceOrder"

fun stableNovelParagraphLazyKey(
    index: Int,
    chapterId: String?,
    sectionId: String?,
    id: String,
    sourceOrder: Int,
    startOffset: Int?,
): String = "novel-reader-paragraph:$index:${chapterId.orEmpty()}:${sectionId.orEmpty()}:$id:$sourceOrder:${startOffset ?: -1}"
