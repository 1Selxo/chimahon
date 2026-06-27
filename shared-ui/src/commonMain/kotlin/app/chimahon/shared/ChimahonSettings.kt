package app.chimahon.shared

import tachiyomi.core.platform.settings.PlatformSettingsStore
import tachiyomi.core.platform.settings.readBoolean
import tachiyomi.core.platform.settings.readInt
import tachiyomi.core.platform.settings.writeBoolean
import tachiyomi.core.platform.settings.writeInt

data class ChimahonSettings(
    val appearance: ChimahonAppearanceSettings = ChimahonAppearanceSettings(),
    val reader: ChimahonReaderSettings = ChimahonReaderSettings(),
    val library: ChimahonLibrarySettings = ChimahonLibrarySettings(),
    val animeLibrary: ChimahonAnimeLibrarySettings = ChimahonAnimeLibrarySettings(),
    val downloads: ChimahonDownloadPreferences = ChimahonDownloadPreferences(),
    val browse: ChimahonBrowseSettings = ChimahonBrowseSettings(),
    val navigation: ChimahonNavigationSettings = ChimahonNavigationSettings(),
    val player: ChimahonPlayerSettings = ChimahonPlayerSettings(),
    val tracking: ChimahonTrackingSettings = ChimahonTrackingSettings(),
    val connections: ChimahonConnectionSettings = ChimahonConnectionSettings(),
    val dictionary: ChimahonDictionarySettings = ChimahonDictionarySettings(),
    val security: ChimahonSecuritySettings = ChimahonSecuritySettings(),
    val appMode: ChimahonAppModeSettings = ChimahonAppModeSettings(),
)

data class ChimahonAppearanceSettings(
    val themeMode: ChimahonThemeMode = ChimahonThemeMode.System,
    val appTheme: ChimahonAppTheme = ChimahonAppTheme.Default,
    val colorTheme: ChimahonColorTheme = ChimahonColorTheme.Default,
    val appIcon: ChimahonAppIcon = ChimahonAppIcon.Default,
    val customThemeStyle: ChimahonMaterialPaletteStyle = ChimahonMaterialPaletteStyle.Fidelity,
    val amoled: Boolean = false,
    val fontScalePercent: Int = 100,
    val tabletUiMode: ChimahonTabletUiMode = ChimahonTabletUiMode.Automatic,
    val dateFormat: ChimahonDateFormat = ChimahonDateFormat.Default,
    val compactNavigation: Boolean = false,
    val relativeDates: Boolean = true,
    val showDescriptionImages: Boolean = true,
    val mangaInfoCoverBasedTheme: Boolean = true,
    val mangaInfoCoverBasedStyle: ChimahonMaterialPaletteStyle = ChimahonMaterialPaletteStyle.Vibrant,
    val usePanoramaCoverMangaInfo: Boolean = false,
    val topAlignCover: Boolean = false,
    val startScreen: ChimahonStartScreen = ChimahonStartScreen.Library,
    val bottomBarLabels: Boolean = true,
    val showNavigationBadges: Boolean = true,
    val showTopBarSubtitle: Boolean = false,
    val usePanoramaCoverFlow: Boolean = false,
    val expandSearchFilters: Boolean = false,
    val recommendsInOverflow: Boolean = false,
    val mergeInOverflow: Boolean = true,
    val previewsRowCount: Int = 4,
)

enum class ChimahonThemeMode(val title: String) {
    System("Follow system"),
    Light("Light"),
    Dark("Dark"),
}

enum class ChimahonAppTheme(val title: String) {
    Default("Default"),
    Monet("Monet"),
    Custom("Custom"),
    Catppuccin("Catppuccin"),
    Cloudflare("Cloudflare"),
    CottonCandy("Cotton Candy"),
    Doom("Doom"),
    GreenApple("Green Apple"),
    Lavender("Lavender"),
    Matrix("Matrix"),
    MidnightDusk("Midnight Dusk"),
    Mocha("Mocha"),
    Monochrome("Monochrome"),
    Nord("Nord"),
    Sapphire("Sapphire"),
    StrawberryDaiquiri("Strawberry Daiquiri"),
    Tako("Tako"),
    TealTurquoise("Teal Turquoise"),
    TidalWave("Tidal Wave"),
    YinYang("Yin & Yang"),
    Yotsuba("Yotsuba"),
}

enum class ChimahonColorTheme(val title: String) {
    Default("Default"),
    Dynamic("Dynamic"),
    Blue("Blue"),
    Green("Green"),
    Orange("Orange"),
    Pink("Pink"),
    Purple("Purple"),
    Red("Red"),
    Teal("Teal"),
}

enum class ChimahonAppIcon(val title: String) {
    Default("Default"),
    Classic("Classic"),
    Monochrome("Monochrome"),
    Legacy("Legacy"),
    Tachiyomi("Tachiyomi"),
    Mihon("Mihon"),
}

enum class ChimahonMaterialPaletteStyle(val title: String) {
    TonalSpot("Tonal spot"),
    Neutral("Neutral"),
    Vibrant("Vibrant"),
    Expressive("Expressive"),
    Rainbow("Rainbow"),
    FruitSalad("Fruit salad"),
    Monochrome("Monochrome"),
    Fidelity("Fidelity"),
    Content("Content"),
}

enum class ChimahonTabletUiMode(val title: String) {
    Automatic("Automatic"),
    Always("Always"),
    Landscape("Landscape"),
    Never("Never"),
}

enum class ChimahonDateFormat(val title: String) {
    Default("Default"),
    MonthDayYear("MM/dd/yy"),
    DayMonthYear("dd/MM/yy"),
    Iso("yyyy-MM-dd"),
    DayShortMonthYear("dd MMM yyyy"),
    ShortMonthDayYear("MMM dd, yyyy"),
}

enum class ChimahonStartScreen(val title: String) {
    Library("Library"),
    Anime("Anime"),
    Updates("Updates"),
    History("History"),
    Browse("Browse"),
    Dictionary("Dictionary"),
    Novels("Novels"),
    More("More"),
}

data class ChimahonNavigationSettings(
    val tabLayout: List<ChimahonNavigationTabEntry> = ChimahonNavigationTabDefaults,
    val startScreen: ChimahonStartScreen = ChimahonStartScreen.Library,
    val showUpdatesTab: Boolean = false,
    val showHistoryTab: Boolean = true,
)

data class ChimahonNavigationTabEntry(
    val tab: ChimahonNavigationTab,
    val section: ChimahonNavigationSection,
)

enum class ChimahonNavigationTab(val key: String, val title: String) {
    Library("Library", "Library"),
    Novels("Novels", "Novels"),
    Anime("Anime", "Anime"),
    Updates("Updates", "Updates"),
    History("History", "History"),
    Browse("Browse", "Browse"),
    Dictionary("Dictionary", "Dictionary"),
}

enum class ChimahonNavigationSection(val wireName: String, val title: String) {
    Navbar("navbar", "Navigation bar"),
    More("more", "More"),
    Disabled("disabled", "Disabled"),
}

val ChimahonNavigationTabDefaults: List<ChimahonNavigationTabEntry> = listOf(
    ChimahonNavigationTabEntry(ChimahonNavigationTab.Library, ChimahonNavigationSection.Navbar),
    ChimahonNavigationTabEntry(ChimahonNavigationTab.Novels, ChimahonNavigationSection.Navbar),
    ChimahonNavigationTabEntry(ChimahonNavigationTab.Anime, ChimahonNavigationSection.Navbar),
    ChimahonNavigationTabEntry(ChimahonNavigationTab.Updates, ChimahonNavigationSection.More),
    ChimahonNavigationTabEntry(ChimahonNavigationTab.History, ChimahonNavigationSection.Navbar),
    ChimahonNavigationTabEntry(ChimahonNavigationTab.Browse, ChimahonNavigationSection.Navbar),
    ChimahonNavigationTabEntry(ChimahonNavigationTab.Dictionary, ChimahonNavigationSection.Navbar),
)

data class ChimahonReaderSettings(
    val mode: ChimahonReaderMode = ChimahonReaderMode.Webtoon,
    val scale: ChimahonReaderScale = ChimahonReaderScale.FitWidth,
    val canvas: ChimahonReaderCanvas = ChimahonReaderCanvas.Black,
    val pureBlackBackground: Boolean = true,
    val orientation: ChimahonReaderOrientation = ChimahonReaderOrientation.Free,
    val dualPageMode: ChimahonDualPageMode = ChimahonDualPageMode.Off,
    val splitWidePages: Boolean = false,
    val rotateWidePagesToFit: Boolean = false,
    val invertWidePageRotation: Boolean = false,
    val customBrightnessEnabled: Boolean = false,
    val customBrightnessValue: Int = 0,
    val colorFilterEnabled: Boolean = false,
    val colorFilterValue: Int = 0,
    val colorFilterMode: ChimahonReaderColorFilterMode = ChimahonReaderColorFilterMode.Default,
    val grayscale: Boolean = false,
    val invertColors: Boolean = false,
    val brightness: Int = 0,
    val doubleTapAnimationSpeedMillis: Int = 500,
    val navigationMode: ChimahonReaderNavigationMode = ChimahonReaderNavigationMode.Automatic,
    val showPageStrip: Boolean = true,
    val forceHorizontalSeekbar: Boolean = false,
    val landscapeVerticalSeekbar: Boolean = true,
    val leftVerticalSeekbar: Boolean = false,
    val keepControlsVisible: Boolean = true,
    val tapZonesEnabled: Boolean = true,
    val smallerTapZones: Boolean = false,
    val invertTapZones: ChimahonTapZoneInvert = ChimahonTapZoneInvert.None,
    val swipeNavigationEnabled: Boolean = true,
    val doubleTapToZoom: Boolean = true,
    val volumeKeysEnabled: Boolean = false,
    val volumeKeysInverted: Boolean = false,
    val longTapEnabled: Boolean = true,
    val readWithLongTap: Boolean = true,
    val keepScreenOn: Boolean = false,
    val fullscreen: Boolean = true,
    val drawUnderCutout: Boolean = false,
    val ocrOutlineVisible: Boolean = false,
    val cropBorders: Boolean = false,
    val pageTransitions: Boolean = true,
    val eInkSwipeSensitivity: Boolean = false,
    val flashOnPageChange: Boolean = false,
    val flashDurationMillis: Int = 500,
    val flashPageInterval: Int = 1,
    val flashColor: ChimahonReaderFlashColor = ChimahonReaderFlashColor.White,
    val showPageNumber: Boolean = true,
    val verticalWriting: Boolean = true,
    val continuousMode: Boolean = false,
    val fontSize: Double = 18.0,
    val lineHeight: Double = 1.6,
    val horizontalPadding: Double = 10.0,
    val verticalPadding: Double = 10.0,
    val avoidPageBreak: Boolean = true,
    val justifyText: Boolean = false,
    val characterSpacing: Double = 0.0,
    val paragraphSpacing: Double = 0.0,
    val hideFurigana: Boolean = false,
    val showTitle: Boolean = true,
    val showCharacters: Boolean = true,
    val showPercentage: Boolean = true,
    val showProgressTop: Boolean = true,
    val showReadingSpeed: Boolean = true,
    val showReadingTime: Boolean = true,
    val tapZonePercent: Int = 20,
    val chapterSwipeDistance: Int = 96,
    val readerStartupDelay: Boolean = false,
    val showReadingMode: Boolean = true,
    val showNavigationOverlayOnStart: Boolean = false,
    val skipReadChapters: Boolean = false,
    val skipFilteredChapters: Boolean = false,
    val skipDuplicateChapters: Boolean = false,
    val alwaysShowChapterTransition: Boolean = false,
    val navigateToPan: Boolean = false,
    val folderPerManga: Boolean = false,
    val preloadSize: Int = 6,
    val readerThreads: Int = 2,
    val readerCacheSizeMb: Int = 250,
    val aggressivePageLoading: Boolean = false,
    val preserveReadingPosition: Boolean = true,
    val useAutoWebtoon: Boolean = false,
    val invertDoublePages: Boolean = false,
    val rotateWidePagesToFitWebtoon: Boolean = false,
    val invertWidePageRotationWebtoon: Boolean = false,
    val centerMarginDp: Int = 0,
    val pagedZoomStart: ChimahonReaderZoomStart = ChimahonReaderZoomStart.Automatic,
    val landscapeZoom: Boolean = true,
    val landscapeZoomType: ChimahonReaderLandscapeZoomType = ChimahonReaderLandscapeZoomType.Fit,
    val pagedDisableZoomIn: Boolean = false,
    val webtoonScaleType: ChimahonWebtoonScaleType = ChimahonWebtoonScaleType.Fit,
    val smartLongStripGapScale: Boolean = false,
    val webtoonSidePaddingPercent: Int = 0,
    val webtoonReaderHideThreshold: ChimahonReaderHideThreshold = ChimahonReaderHideThreshold.Low,
    val webtoonPinchToZoom: Boolean = true,
    val webtoonDisableZoomOut: Boolean = false,
    val continuousVerticalTappingByPage: Boolean = false,
    val ocrAutoOnDownload: Boolean = false,
    val bottomButtons: List<String> = listOf(
        "vc",
        "wb",
        "cbp",
        "cbc",
        "pl",
        "ms",
    ),
)

enum class ChimahonReaderMode {
    Webtoon,
    Vertical,
    LeftToRight,
    RightToLeft,
}

enum class ChimahonReaderScale {
    FitScreen,
    FitWidth,
    FitHeight,
}

enum class ChimahonReaderCanvas {
    Black,
    Gray,
    White,
}

enum class ChimahonReaderFlashColor {
    Black,
    White,
    WhiteBlack,
}

enum class ChimahonReaderColorFilterMode(val title: String) {
    Default("Default"),
    Multiply("Multiply"),
    Screen("Screen"),
    Overlay("Overlay"),
    Lighten("Lighten"),
    Darken("Darken"),
}

enum class ChimahonReaderZoomStart(val title: String) {
    Automatic("Automatic"),
    Left("Left"),
    Right("Right"),
    Center("Center"),
}

enum class ChimahonReaderLandscapeZoomType(val title: String) {
    Fit("Fit screen"),
    Double("Double zoom"),
}

enum class ChimahonWebtoonScaleType(val title: String) {
    Fit("Fit screen"),
    FourThree("4:3"),
    ThreeTwo("3:2"),
    SixteenNine("16:9"),
    TwentyNine("20:9"),
}

enum class ChimahonReaderHideThreshold(val title: String) {
    Highest("Highest"),
    High("High"),
    Low("Low"),
    Lowest("Lowest"),
}

enum class ChimahonReaderOrientation {
    Free,
    Portrait,
    Landscape,
    ReversePortrait,
    ReverseLandscape,
}

enum class ChimahonDualPageMode {
    Off,
    Automatic,
    Always,
}

enum class ChimahonReaderNavigationMode {
    Automatic,
    LeftToRight,
    RightToLeft,
    Vertical,
}

enum class ChimahonTapZoneInvert {
    None,
    Horizontal,
    Vertical,
    Both,
}

data class ChimahonLibrarySettings(
    val displayMode: ChimahonLibraryDisplayMode = ChimahonLibraryDisplayMode.ComfortableGrid,
    val gridColumnsPortrait: Int = 0,
    val gridColumnsLandscape: Int = 0,
    val coverAspectRatio: ChimahonLibraryCoverRatio = ChimahonLibraryCoverRatio.Automatic,
    val defaultCategory: String = "Default",
    val categorizedDisplaySettings: Boolean = false,
    val showCategoryTabs: Boolean = true,
    val showUnreadBadges: Boolean = true,
    val showDownloadedBadges: Boolean = true,
    val showLanguageBadges: Boolean = false,
    val showContinueButtons: Boolean = true,
    val sort: ChimahonLibrarySort = ChimahonLibrarySort.Alphabetical,
    val sortAscending: Boolean = true,
    val downloadedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val unreadFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val startedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val bookmarkedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val completedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val trackedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val autoUpdateIntervalHours: Int = 0,
    val updateOnlyOnWifi: Boolean = true,
    val updateRestrictions: List<String> = listOf("Wi-Fi only"),
    val updateIncludedCategories: List<String> = emptyList(),
    val updateExcludedCategories: List<String> = emptyList(),
    val updateGroupMode: ChimahonLibraryUpdateGroupMode = ChimahonLibraryUpdateGroupMode.Global,
    val autoUpdateMetadata: Boolean = false,
    val smartUpdateRestrictions: List<String> = listOf(
        "Has unread chapters",
        "Started",
        "Not completed",
        "In release period",
    ),
    val showUpdateCount: Boolean = true,
    val updateNotificationsEnabled: Boolean = true,
    val showUpdatingProgressBanner: Boolean = true,
    val swipeToStartAction: ChimahonChapterSwipeAction = ChimahonChapterSwipeAction.ToggleBookmark,
    val swipeToEndAction: ChimahonChapterSwipeAction = ChimahonChapterSwipeAction.ToggleRead,
    val duplicateReadChapterHandling: List<String> = emptyList(),
    val hideMissingChapters: Boolean = false,
    val showEmptyCategoriesSearch: Boolean = false,
    val fetchMetadataOnAdd: Boolean = false,
    val fetchChaptersOnAdd: Boolean = false,
    val updateMangaTitles: Boolean = false,
    val disallowNonAsciiFilenames: Boolean = false,
)

enum class ChimahonLibraryDisplayMode {
    ComfortableGrid,
    ComfortableGridPanorama,
    CompactGrid,
    CoverOnlyGrid,
    List,
}

enum class ChimahonLibraryCoverRatio {
    Automatic,
    Square,
    ThreeToFour,
    TwoToThree,
    Original,
}

enum class ChimahonLibrarySort {
    Alphabetical,
    LastRead,
    LastUpdate,
    UnreadCount,
    TotalChapters,
    LatestChapter,
    ChapterFetchDate,
    DateAdded,
    Random,
}

enum class ChimahonFilterMode {
    Any,
    Include,
    Exclude,
}

enum class ChimahonLibraryUpdateGroupMode(val title: String) {
    Global("Global"),
    AllButUngrouped("All but ungrouped"),
    All("All categories"),
}

enum class ChimahonChapterSwipeAction(val title: String) {
    Disabled("Disabled"),
    ToggleBookmark("Bookmark"),
    ToggleRead("Mark read"),
    Download("Download"),
}

data class ChimahonAnimeLibrarySettings(
    val displayMode: ChimahonLibraryDisplayMode = ChimahonLibraryDisplayMode.CompactGrid,
    val gridColumnsPortrait: Int = 0,
    val gridColumnsLandscape: Int = 0,
    val defaultCategoryId: Int = -1,
    val categorizedDisplaySettings: Boolean = false,
    val showCategoryTabs: Boolean = true,
    val showCategoryItemCount: Boolean = false,
    val showUnseenBadges: Boolean = true,
    val showDownloadedBadges: Boolean = false,
    val showLocalBadges: Boolean = true,
    val showLanguageBadges: Boolean = false,
    val showContinueWatchingButtons: Boolean = false,
    val sort: ChimahonLibrarySort = ChimahonLibrarySort.Alphabetical,
    val sortAscending: Boolean = true,
    val downloadedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val unseenFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val startedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val bookmarkedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val completedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val fillerFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val trackedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val groupBy: ChimahonLibraryGroup = ChimahonLibraryGroup.Default,
    val updateRestrictions: List<String> = listOf("Outside release period"),
    val swipeToStartAction: ChimahonEpisodeSwipeAction = ChimahonEpisodeSwipeAction.ToggleSeen,
    val swipeToEndAction: ChimahonEpisodeSwipeAction = ChimahonEpisodeSwipeAction.ToggleSeen,
)

enum class ChimahonLibraryGroup(val title: String) {
    Default("Default"),
    Source("Source"),
    Status("Status"),
    TrackingStatus("Tracking status"),
}

enum class ChimahonEpisodeSwipeAction(val title: String) {
    Disabled("Disabled"),
    ToggleBookmark("Bookmark"),
    ToggleSeen("Mark seen"),
    ToggleFiller("Mark filler"),
    Download("Download"),
}

data class ChimahonDownloadPreferences(
    val wifiOnly: Boolean = true,
    val saveAsCbz: Boolean = true,
    val splitTallImages: Boolean = true,
    val autoDownloadWhileReadingCount: Int = 0,
    val removeAfterReadSlots: Int = -1,
    val removeAfterMarkedRead: Boolean = false,
    val removeBookmarkedChapters: Boolean = false,
    val removeExcludedCategories: List<String> = emptyList(),
    val downloadNewChapters: Boolean = false,
    val downloadNewUnreadOnly: Boolean = false,
    val downloadNewIncludedCategories: List<String> = emptyList(),
    val downloadNewExcludedCategories: List<String> = emptyList(),
    val parallelSourceDownloads: Int = 5,
    val parallelPageDownloads: Int = 5,
    val includeChapterUrlHash: Boolean = true,
    val downloadCacheRenewIntervalHours: Int = 1,
)

data class ChimahonBrowseSettings(
    val showNsfwSources: Boolean = true,
    val hideLibraryEntries: Boolean = false,
    val autoLoadMore: Boolean = true,
    val enabledLanguages: List<String> = emptyList(),
    val pinnedSourceIds: List<Long> = emptyList(),
    val disabledExtensionRepoUrls: List<String> = emptyList(),
    val sourceDisplayMode: ChimahonBrowseSourceDisplayMode = ChimahonBrowseSourceDisplayMode.List,
    val groupSourcesByLanguage: Boolean = true,
    val showSourceLanguage: Boolean = true,
    val extensionUpdateNotificationsEnabled: Boolean = true,
    val relatedMangaRecommendations: Boolean = true,
    val expandRelatedMangaSections: Boolean = true,
    val relatedMangaInOverflow: Boolean = false,
    val showHomeInRelatedManga: Boolean = true,
    val sourceCategoriesFilter: Boolean = false,
    val useNewSourceNavigation: Boolean = true,
    val allowLocalSourceHiddenFolders: Boolean = false,
    val hideFeedTab: Boolean = false,
    val feedTabInFront: Boolean = false,
    val hideLibraryFeedEntries: Boolean = false,
)

enum class ChimahonBrowseSourceDisplayMode {
    List,
    CompactList,
    Grid,
}

data class ChimahonPlayerSettings(
    val preserveWatchingPosition: Boolean = false,
    val progressPreference: Double = 0.85,
    val defaultOrientation: ChimahonPlayerOrientation = ChimahonPlayerOrientation.SensorLandscape,
    val allowGesturesInPanels: Boolean = false,
    val showLoadingCircle: Boolean = true,
    val showCurrentEpisode: Boolean = true,
    val rememberBrightness: Boolean = false,
    val rememberedBrightness: Double = -1.0,
    val rememberVolume: Boolean = false,
    val rememberedVolume: Double = -1.0,
    val showFailedHosters: Boolean = false,
    val showEmptyHosters: Boolean = false,
    val fullscreen: Boolean = true,
    val hideControls: Boolean = false,
    val displayVolumePercent: Boolean = true,
    val showSystemStatusBar: Boolean = false,
    val reduceMotion: Boolean = false,
    val controlsHideDelayMillis: Int = 4000,
    val panelOpacityPercent: Int = 60,
    val skipIntroEnabled: Boolean = true,
    val autoSkipIntro: Boolean = false,
    val netflixStyleSkipIntro: Boolean = false,
    val skipIntroWaitSeconds: Int = 5,
    val aniSkipEnabled: Boolean = false,
    val disableAniSkipOnChapters: Boolean = true,
    val pipEnabled: Boolean = true,
    val pipEpisodeToasts: Boolean = true,
    val pipOnExit: Boolean = false,
    val pipReplaceWithPrevious: Boolean = false,
    val castEnabled: Boolean = false,
    val alwaysUseExternalPlayer: Boolean = false,
    val externalPlayerPackage: String = "",
    val playerSpeed: Double = 1.0,
    val speedPresets: List<String> = listOf(
        "0.25",
        "0.5",
        "0.75",
        "1.0",
        "1.25",
        "1.5",
        "1.75",
        "2.0",
        "2.5",
        "3.0",
        "3.5",
        "4.0",
    ),
    val invertDuration: Boolean = false,
    val aspect: ChimahonPlayerAspect = ChimahonPlayerAspect.Fit,
    val autoplayEnabled: Boolean = false,
    val gestures: ChimahonPlayerGestureSettings = ChimahonPlayerGestureSettings(),
    val decoder: ChimahonPlayerDecoderSettings = ChimahonPlayerDecoderSettings(),
    val subtitles: ChimahonPlayerSubtitleSettings = ChimahonPlayerSubtitleSettings(),
    val audio: ChimahonPlayerAudioSettings = ChimahonPlayerAudioSettings(),
    val selections: ChimahonPlayerSelectionSettings = ChimahonPlayerSelectionSettings(),
    val advanced: ChimahonPlayerAdvancedSettings = ChimahonPlayerAdvancedSettings(),
)

data class ChimahonPlayerGestureSettings(
    val volumeBrightnessGestures: Boolean = true,
    val swapVolumeAndBrightness: Boolean = false,
    val horizontalSeekGesture: Boolean = true,
    val showSeekBar: Boolean = false,
    val defaultIntroLengthSeconds: Int = 85,
    val skipLengthSeconds: Int = 10,
    val smoothSeek: Boolean = false,
    val leftDoubleTap: ChimahonPlayerGestureAction = ChimahonPlayerGestureAction.Seek,
    val centerDoubleTap: ChimahonPlayerGestureAction = ChimahonPlayerGestureAction.PlayPause,
    val rightDoubleTap: ChimahonPlayerGestureAction = ChimahonPlayerGestureAction.Seek,
    val mediaPrevious: ChimahonPlayerGestureAction = ChimahonPlayerGestureAction.Switch,
    val mediaPlayPause: ChimahonPlayerGestureAction = ChimahonPlayerGestureAction.PlayPause,
    val mediaNext: ChimahonPlayerGestureAction = ChimahonPlayerGestureAction.Switch,
)

data class ChimahonPlayerDecoderSettings(
    val tryHardwareDecoding: Boolean = true,
    val gpuNext: Boolean = false,
    val debanding: ChimahonPlayerDebanding = ChimahonPlayerDebanding.None,
    val useYuv420p: Boolean = true,
    val brightnessFilter: Int = 0,
    val saturationFilter: Int = 0,
    val contrastFilter: Int = 0,
    val gammaFilter: Int = 0,
    val hueFilter: Int = 0,
)

data class ChimahonPlayerSubtitleSettings(
    val preferredLanguages: String = "",
    val whitelist: String = "",
    val blacklist: String = "",
    val jimakuApiKey: String = "",
    val jimakuTitle: String = "",
    val regexRemoveSpeakerNames: Boolean = false,
    val regexMergeMultiline: Boolean = false,
    val regexRemoveBracketedText: Boolean = false,
    val regexRemoveUppercaseLines: Boolean = false,
    val regexRemoveMusicSymbols: Boolean = false,
    val regexRemoveCurlyBracedText: Boolean = false,
    val regexCustomEnabled: Boolean = false,
    val regexCustomPattern: String = "",
    val screenshotSubtitles: Boolean = false,
    val listMode: ChimahonSubtitleListMode = ChimahonSubtitleListMode.SideList,
    val font: String = "Sans Serif",
    val fontSize: Int = 55,
    val fontScale: Double = 1.0,
    val borderSize: Int = 3,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val textColorArgb: Int = -1,
    val borderColorArgb: Int = -16777216,
    val borderStyle: ChimahonSubtitleBorderStyle = ChimahonSubtitleBorderStyle.OutlineAndShadow,
    val shadowOffset: Int = 0,
    val backgroundColorArgb: Int = 0,
    val justification: ChimahonSubtitleJustification = ChimahonSubtitleJustification.Auto,
    val positionPercent: Int = 100,
    val overrideAss: Boolean = false,
    val delayMillis: Int = 0,
    val speed: Double = 1.0,
    val secondaryDelayMillis: Int = 0,
)

data class ChimahonPlayerAudioSettings(
    val preferredLanguages: String = "",
    val pitchCorrection: Boolean = true,
    val channels: ChimahonAudioChannels = ChimahonAudioChannels.AutoSafe,
    val volumeBoostCap: Int = 30,
    val delayMillis: Int = 0,
)

data class ChimahonPlayerSelectionSettings(
    val rememberQuality: Boolean = true,
    val rememberSubtitleTracks: Boolean = true,
    val restoreAddedSubtitleTracks: Boolean = true,
    val preferredHosterKey: String = "",
    val preferredVideoKey: String = "",
    val primarySubtitleKey: String = "",
    val secondarySubtitleKey: String = "",
    val addedSubtitleKeys: List<String> = emptyList(),
)

data class ChimahonPlayerAdvancedSettings(
    val mpvScriptsEnabled: Boolean = false,
    val mpvConfig: String = "",
    val mpvInput: String = "",
    val statisticsPage: Int = 0,
)

enum class ChimahonPlayerOrientation(val title: String) {
    Free("Free"),
    Portrait("Portrait"),
    Landscape("Landscape"),
    SensorPortrait("Sensor portrait"),
    SensorLandscape("Sensor landscape"),
    ReversePortrait("Reverse portrait"),
    ReverseLandscape("Reverse landscape"),
}

enum class ChimahonPlayerAspect(val title: String) {
    Fit("Fit"),
    Crop("Crop"),
    Stretch("Stretch"),
}

enum class ChimahonPlayerGestureAction(val title: String) {
    None("None"),
    Seek("Seek"),
    PlayPause("Play/pause"),
    Switch("Switch episode"),
}

enum class ChimahonPlayerDebanding(val title: String) {
    None("None"),
    Weak("Weak"),
    Medium("Medium"),
    Strong("Strong"),
}

enum class ChimahonSubtitleBorderStyle(val title: String) {
    OutlineAndShadow("Outline and shadow"),
    OpaqueBox("Opaque box"),
    BackgroundBox("Background box"),
}

enum class ChimahonSubtitleListMode(val title: String) {
    SideList("Side list"),
    Overlay("Overlay"),
}

enum class ChimahonSubtitleJustification(val title: String) {
    Left("Left"),
    Center("Center"),
    Right("Right"),
    Auto("Auto"),
}

enum class ChimahonAudioChannels(val title: String) {
    Auto("Auto"),
    AutoSafe("Auto safe"),
    Mono("Mono"),
    Stereo("Stereo"),
    ReverseStereo("Reverse stereo"),
}

data class ChimahonTrackingSettings(
    val trackOnAddToLibrary: Boolean = false,
    val autoSyncEnabled: Boolean = false,
    val updateIntervalHours: Int = 24,
    val syncRestrictions: List<String> = listOf("Wi-Fi only"),
    val syncIncludedCategories: List<String> = emptyList(),
    val syncExcludedCategories: List<String> = emptyList(),
    val syncLibraryEntriesOnly: Boolean = true,
    val autoUpdateOnMarkRead: ChimahonAutoTrackOnMarkRead = ChimahonAutoTrackOnMarkRead.Always,
    val autoSyncProgressFromTrackers: Boolean = false,
    val resolveUsingSourceMetadata: Boolean = false,
)

enum class ChimahonAutoTrackOnMarkRead(val title: String) {
    Always("Always"),
    Ask("Ask"),
    Never("Never"),
}

data class ChimahonConnectionSettings(
    val openingPreference: ChimahonConnectionOpeningPreference =
        ChimahonConnectionOpeningPreference.InApp,
    val discordRpcEnabled: Boolean = false,
    val discordStatus: ChimahonDiscordStatus = ChimahonDiscordStatus.Online,
    val discordRpcIncognito: Boolean = false,
    val discordRpcIncognitoCategories: List<String> = emptyList(),
    val discordShowMangaTitle: Boolean = true,
    val discordShowCoverArt: Boolean = true,
    val discordShowSourceName: Boolean = false,
    val discordUseChapterTitles: Boolean = false,
    val discordShowProgress: Boolean = true,
    val discordShowTimestamp: Boolean = true,
    val discordShowButtons: Boolean = true,
    val discordShowDownloadButton: Boolean = true,
    val discordShowDiscordButton: Boolean = true,
)

enum class ChimahonConnectionOpeningPreference {
    InApp,
    ExternalBrowser,
    AskEveryTime,
}

enum class ChimahonDiscordStatus(val title: String) {
    DoNotDisturb("Do not disturb"),
    Idle("Idle"),
    Online("Online"),
}

data class ChimahonDictionarySettings(
    val enabled: Boolean = false,
    val enabledLanguages: List<String> = emptyList(),
    val ocrEnabled: Boolean = false,
    val popupWidth: Int = 300,
    val popupHeight: Int = 360,
    val fontSize: Int = 16,
    val ocrBoxScaleXPercent: Int = 100,
    val ocrBoxScaleYPercent: Int = 100,
    val ocrBoxOpacityPercent: Int = 0,
    val ocrEngine: ChimahonDictionaryOcrEngine = ChimahonDictionaryOcrEngine.Cloud,
    val themeMode: ChimahonDictionaryThemeMode = ChimahonDictionaryThemeMode.System,
    val eInkMode: Boolean = false,
    val paginatedScrolling: Boolean = false,
    val showFrequencyHarmonic: Boolean = false,
    val showFrequencyAverage: Boolean = false,
    val groupPitches: Boolean = false,
    val groupTerms: Boolean = true,
    val showNavigationButtons: Boolean = true,
    val popupMode: ChimahonDictionaryPopupMode = ChimahonDictionaryPopupMode.Floating,
    val recursiveLookupMode: ChimahonDictionaryRecursiveLookupMode = ChimahonDictionaryRecursiveLookupMode.Tabs,
    val showPitchDiagram: Boolean = true,
    val showPitchNumber: Boolean = true,
    val showPitchText: Boolean = true,
    val autoKanaConversion: Boolean = true,
    val wordAudioEnabled: Boolean = true,
    val wordAudioAutoplay: Boolean = false,
    val wordAudioLocalEnabled: Boolean = false,
)

enum class ChimahonDictionaryOcrEngine(val title: String) {
    Cloud("Cloud"),
    Local("Local"),
}

enum class ChimahonDictionaryThemeMode(val title: String) {
    System("System"),
    Light("Light"),
    Dark("Dark"),
    PureBlack("Pure black"),
}

enum class ChimahonDictionaryPopupMode(val title: String) {
    Floating("Floating"),
    FullWidth("Full-width"),
    FullHeight("Full-height"),
}

enum class ChimahonDictionaryRecursiveLookupMode(val title: String) {
    Tabs("Tabs"),
    Stack("Back stack"),
}

data class ChimahonSecuritySettings(
    val secureScreenMode: ChimahonSecureScreenMode = ChimahonSecureScreenMode.Incognito,
    val hideNotificationContent: Boolean = false,
    val crashReportsEnabled: Boolean = false,
    val requireAuthentication: Boolean = false,
    val lockAfterMinutes: Int = 0,
    val lockOnAppExit: Boolean = false,
    val incognitoModeByDefault: Boolean = false,
    val protectDownloads: Boolean = false,
    val downloadEncryption: ChimahonDownloadEncryption = ChimahonDownloadEncryption.Aes256,
    val biometricLockDays: List<String> = listOf(
        "Sunday",
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
    ),
    val encryptDatabase: Boolean = false,
)

enum class ChimahonSecureScreenMode {
    Always,
    Incognito,
    Never,
}

enum class ChimahonDownloadEncryption(val title: String) {
    Aes256("AES-256"),
    Aes128("AES-128"),
    ZipStandard("Standard ZIP"),
}

data class ChimahonAppModeSettings(
    val downloadedOnly: Boolean = false,
    val incognitoMode: Boolean = false,
)

internal class ChimahonSettingsRepository(
    private val settingsStore: PlatformSettingsStore,
) {

    suspend fun loadSettings(): ChimahonSettings {
        return ChimahonSettings(
            appearance = loadAppearanceSettings(),
            reader = loadReaderSettings(),
            library = loadLibrarySettings(),
            animeLibrary = loadAnimeLibrarySettings(),
            downloads = loadDownloadSettings(),
            browse = loadBrowseSettings(),
            navigation = loadNavigationSettings(),
            player = loadPlayerSettings(),
            tracking = loadTrackingSettings(),
            connections = loadConnectionSettings(),
            dictionary = loadDictionarySettings(),
            security = loadSecuritySettings(),
            appMode = loadAppModeSettings(),
        )
    }

    suspend fun loadAppearanceSettings(): ChimahonAppearanceSettings {
        return ChimahonAppearanceSettings(
            themeMode = readEnum(APPEARANCE_THEME_MODE_KEY, ChimahonThemeMode.System),
            appTheme = readEnum(APPEARANCE_APP_THEME_KEY, ChimahonAppTheme.Default),
            colorTheme = readEnum(APPEARANCE_COLOR_THEME_KEY, ChimahonColorTheme.Default),
            appIcon = readEnum(APPEARANCE_APP_ICON_KEY, ChimahonAppIcon.Default),
            customThemeStyle = readEnum(
                APPEARANCE_CUSTOM_THEME_STYLE_KEY,
                ChimahonMaterialPaletteStyle.Fidelity,
            ),
            amoled = settingsStore.readBoolean(APPEARANCE_AMOLED_KEY),
            fontScalePercent = settingsStore.readInt(
                APPEARANCE_FONT_SCALE_PERCENT_KEY,
                defaultValue = 100,
            ),
            tabletUiMode = readEnum(APPEARANCE_TABLET_UI_MODE_KEY, ChimahonTabletUiMode.Automatic),
            dateFormat = readEnum(APPEARANCE_DATE_FORMAT_KEY, ChimahonDateFormat.Default),
            compactNavigation = settingsStore.readBoolean(APPEARANCE_COMPACT_NAVIGATION_KEY),
            relativeDates = settingsStore.readBoolean(APPEARANCE_RELATIVE_DATES_KEY, defaultValue = true),
            showDescriptionImages = settingsStore.readBoolean(
                APPEARANCE_DESCRIPTION_IMAGES_KEY,
                defaultValue = true,
            ),
            mangaInfoCoverBasedTheme = settingsStore.readBoolean(
                APPEARANCE_MANGA_INFO_COVER_BASED_THEME_KEY,
                defaultValue = true,
            ),
            mangaInfoCoverBasedStyle = readEnum(
                APPEARANCE_MANGA_INFO_COVER_BASED_STYLE_KEY,
                ChimahonMaterialPaletteStyle.Vibrant,
            ),
            usePanoramaCoverMangaInfo = settingsStore.readBoolean(
                APPEARANCE_MANGA_INFO_PANORAMA_COVER_KEY,
            ),
            topAlignCover = settingsStore.readBoolean(APPEARANCE_MANGA_INFO_TOP_ALIGN_COVER_KEY),
            startScreen = readEnum(APPEARANCE_START_SCREEN_KEY, ChimahonStartScreen.Library),
            bottomBarLabels = settingsStore.readBoolean(
                APPEARANCE_BOTTOM_BAR_LABELS_KEY,
                defaultValue = true,
            ),
            showNavigationBadges = settingsStore.readBoolean(
                APPEARANCE_NAVIGATION_BADGES_KEY,
                defaultValue = true,
            ),
            showTopBarSubtitle = settingsStore.readBoolean(APPEARANCE_TOP_BAR_SUBTITLE_KEY),
            usePanoramaCoverFlow = settingsStore.readBoolean(APPEARANCE_PANORAMA_COVER_FLOW_KEY),
            expandSearchFilters = settingsStore.readBoolean(APPEARANCE_EXPAND_SEARCH_FILTERS_KEY),
            recommendsInOverflow = settingsStore.readBoolean(APPEARANCE_RECOMMENDS_IN_OVERFLOW_KEY),
            mergeInOverflow = settingsStore.readBoolean(
                APPEARANCE_MERGE_IN_OVERFLOW_KEY,
                defaultValue = true,
            ),
            previewsRowCount = settingsStore.readInt(
                APPEARANCE_PREVIEWS_ROW_COUNT_KEY,
                defaultValue = 4,
            ),
        )
    }

    suspend fun saveAppearanceSettings(
        settings: ChimahonAppearanceSettings,
    ): ChimahonAppearanceSettings {
        settingsStore.writeString(APPEARANCE_THEME_MODE_KEY, settings.themeMode.name)
        settingsStore.writeString(APPEARANCE_APP_THEME_KEY, settings.appTheme.name)
        settingsStore.writeString(APPEARANCE_COLOR_THEME_KEY, settings.colorTheme.name)
        settingsStore.writeString(APPEARANCE_APP_ICON_KEY, settings.appIcon.name)
        settingsStore.writeString(APPEARANCE_CUSTOM_THEME_STYLE_KEY, settings.customThemeStyle.name)
        settingsStore.writeBoolean(APPEARANCE_AMOLED_KEY, settings.amoled)
        settingsStore.writeInt(APPEARANCE_FONT_SCALE_PERCENT_KEY, settings.fontScalePercent)
        settingsStore.writeString(APPEARANCE_TABLET_UI_MODE_KEY, settings.tabletUiMode.name)
        settingsStore.writeString(APPEARANCE_DATE_FORMAT_KEY, settings.dateFormat.name)
        settingsStore.writeBoolean(APPEARANCE_COMPACT_NAVIGATION_KEY, settings.compactNavigation)
        settingsStore.writeBoolean(APPEARANCE_RELATIVE_DATES_KEY, settings.relativeDates)
        settingsStore.writeBoolean(APPEARANCE_DESCRIPTION_IMAGES_KEY, settings.showDescriptionImages)
        settingsStore.writeBoolean(
            APPEARANCE_MANGA_INFO_COVER_BASED_THEME_KEY,
            settings.mangaInfoCoverBasedTheme,
        )
        settingsStore.writeString(
            APPEARANCE_MANGA_INFO_COVER_BASED_STYLE_KEY,
            settings.mangaInfoCoverBasedStyle.name,
        )
        settingsStore.writeBoolean(
            APPEARANCE_MANGA_INFO_PANORAMA_COVER_KEY,
            settings.usePanoramaCoverMangaInfo,
        )
        settingsStore.writeBoolean(APPEARANCE_MANGA_INFO_TOP_ALIGN_COVER_KEY, settings.topAlignCover)
        settingsStore.writeString(APPEARANCE_START_SCREEN_KEY, settings.startScreen.name)
        settingsStore.writeBoolean(APPEARANCE_BOTTOM_BAR_LABELS_KEY, settings.bottomBarLabels)
        settingsStore.writeBoolean(APPEARANCE_NAVIGATION_BADGES_KEY, settings.showNavigationBadges)
        settingsStore.writeBoolean(APPEARANCE_TOP_BAR_SUBTITLE_KEY, settings.showTopBarSubtitle)
        settingsStore.writeBoolean(APPEARANCE_PANORAMA_COVER_FLOW_KEY, settings.usePanoramaCoverFlow)
        settingsStore.writeBoolean(APPEARANCE_EXPAND_SEARCH_FILTERS_KEY, settings.expandSearchFilters)
        settingsStore.writeBoolean(APPEARANCE_RECOMMENDS_IN_OVERFLOW_KEY, settings.recommendsInOverflow)
        settingsStore.writeBoolean(APPEARANCE_MERGE_IN_OVERFLOW_KEY, settings.mergeInOverflow)
        settingsStore.writeInt(APPEARANCE_PREVIEWS_ROW_COUNT_KEY, settings.previewsRowCount)
        return settings
    }

    suspend fun loadReaderSettings(): ChimahonReaderSettings {
        return ChimahonReaderSettings()
            .loadReaderDisplaySettings()
            .loadReaderControlSettings()
            .loadReaderReadingSettings()
            .loadReaderPerformanceSettings()
    }

    private suspend fun ChimahonReaderSettings.loadReaderDisplaySettings(): ChimahonReaderSettings {
        return copy(
            mode = readEnum(READER_MODE_KEY, ChimahonReaderMode.Webtoon),
            scale = readEnum(READER_SCALE_KEY, ChimahonReaderScale.FitWidth),
            canvas = readEnum(READER_CANVAS_KEY, ChimahonReaderCanvas.Black),
            pureBlackBackground = settingsStore.readBoolean(
                READER_PURE_BLACK_BACKGROUND_KEY,
                defaultValue = true,
            ),
            orientation = readEnum(READER_ORIENTATION_KEY, ChimahonReaderOrientation.Free),
            dualPageMode = readEnum(READER_DUAL_PAGE_MODE_KEY, ChimahonDualPageMode.Off),
            splitWidePages = settingsStore.readBoolean(READER_SPLIT_WIDE_PAGES_KEY),
            rotateWidePagesToFit = settingsStore.readBoolean(READER_ROTATE_WIDE_PAGES_TO_FIT_KEY),
            invertWidePageRotation = settingsStore.readBoolean(READER_INVERT_WIDE_PAGE_ROTATION_KEY),
            customBrightnessEnabled = settingsStore.readBoolean(READER_CUSTOM_BRIGHTNESS_ENABLED_KEY),
            customBrightnessValue = settingsStore.readInt(READER_CUSTOM_BRIGHTNESS_VALUE_KEY),
            colorFilterEnabled = settingsStore.readBoolean(READER_COLOR_FILTER_ENABLED_KEY),
            colorFilterValue = settingsStore.readInt(READER_COLOR_FILTER_VALUE_KEY),
            colorFilterMode = readEnum(
                READER_COLOR_FILTER_MODE_KEY,
                ChimahonReaderColorFilterMode.Default,
            ),
            grayscale = settingsStore.readBoolean(READER_GRAYSCALE_KEY),
            invertColors = settingsStore.readBoolean(READER_INVERT_COLORS_KEY),
            brightness = settingsStore.readInt(READER_BRIGHTNESS_KEY),
        )
    }

    private suspend fun ChimahonReaderSettings.loadReaderControlSettings(): ChimahonReaderSettings {
        return copy(
            doubleTapAnimationSpeedMillis = settingsStore.readInt(
                READER_DOUBLE_TAP_ANIMATION_SPEED_KEY,
                defaultValue = 500,
            ),
            navigationMode = readEnum(
                READER_NAVIGATION_MODE_KEY,
                ChimahonReaderNavigationMode.Automatic,
            ),
            showPageStrip = settingsStore.readBoolean(READER_PAGE_STRIP_KEY, defaultValue = true),
            forceHorizontalSeekbar = settingsStore.readBoolean(READER_FORCE_HORIZONTAL_SEEKBAR_KEY),
            landscapeVerticalSeekbar = settingsStore.readBoolean(
                READER_LANDSCAPE_VERTICAL_SEEKBAR_KEY,
                defaultValue = true,
            ),
            leftVerticalSeekbar = settingsStore.readBoolean(READER_LEFT_VERTICAL_SEEKBAR_KEY),
            keepControlsVisible = settingsStore.readBoolean(READER_CONTROLS_KEY, defaultValue = true),
            tapZonesEnabled = settingsStore.readBoolean(READER_TAP_ZONES_KEY, defaultValue = true),
            smallerTapZones = settingsStore.readBoolean(READER_SMALLER_TAP_ZONES_KEY),
            invertTapZones = readEnum(READER_INVERT_TAP_ZONES_KEY, ChimahonTapZoneInvert.None),
            swipeNavigationEnabled = settingsStore.readBoolean(
                READER_SWIPE_NAVIGATION_KEY,
                defaultValue = true,
            ),
            doubleTapToZoom = settingsStore.readBoolean(
                READER_DOUBLE_TAP_ZOOM_KEY,
                defaultValue = true,
            ),
            volumeKeysEnabled = settingsStore.readBoolean(READER_VOLUME_KEYS_KEY),
            volumeKeysInverted = settingsStore.readBoolean(READER_VOLUME_KEYS_INVERTED_KEY),
            longTapEnabled = settingsStore.readBoolean(READER_LONG_TAP_KEY, defaultValue = true),
            readWithLongTap = settingsStore.readBoolean(READER_READ_WITH_LONG_TAP_KEY, defaultValue = true),
            keepScreenOn = settingsStore.readBoolean(READER_KEEP_SCREEN_ON_KEY),
            fullscreen = settingsStore.readBoolean(READER_FULLSCREEN_KEY, defaultValue = true),
            drawUnderCutout = settingsStore.readBoolean(READER_DRAW_UNDER_CUTOUT_KEY),
        )
    }

    private suspend fun ChimahonReaderSettings.loadReaderReadingSettings(): ChimahonReaderSettings {
        return copy(
            ocrOutlineVisible = settingsStore.readBoolean(READER_OCR_OUTLINE_VISIBLE_KEY),
            cropBorders = settingsStore.readBoolean(READER_CROP_BORDERS_KEY),
            pageTransitions = settingsStore.readBoolean(READER_PAGE_TRANSITIONS_KEY, defaultValue = true),
            eInkSwipeSensitivity = settingsStore.readBoolean(READER_E_INK_SWIPE_SENSITIVITY_KEY),
            flashOnPageChange = settingsStore.readBoolean(READER_FLASH_ON_PAGE_CHANGE_KEY),
            flashDurationMillis = settingsStore.readInt(READER_FLASH_DURATION_MILLIS_KEY, defaultValue = 500),
            flashPageInterval = settingsStore.readInt(READER_FLASH_PAGE_INTERVAL_KEY, defaultValue = 1),
            flashColor = readEnum(READER_FLASH_COLOR_KEY, ChimahonReaderFlashColor.White),
            showPageNumber = settingsStore.readBoolean(READER_SHOW_PAGE_NUMBER_KEY, defaultValue = true),
            verticalWriting = settingsStore.readBoolean(READER_VERTICAL_WRITING_KEY, defaultValue = true),
            continuousMode = settingsStore.readBoolean(READER_CONTINUOUS_MODE_KEY),
            fontSize = readDouble(READER_FONT_SIZE_KEY, defaultValue = 18.0),
            lineHeight = readDouble(READER_LINE_HEIGHT_KEY, defaultValue = 1.6),
            horizontalPadding = readDouble(READER_HORIZONTAL_PADDING_KEY, defaultValue = 10.0),
            verticalPadding = readDouble(READER_VERTICAL_PADDING_KEY, defaultValue = 10.0),
            avoidPageBreak = settingsStore.readBoolean(READER_AVOID_PAGE_BREAK_KEY, defaultValue = true),
            justifyText = settingsStore.readBoolean(READER_JUSTIFY_TEXT_KEY),
            characterSpacing = readDouble(READER_CHARACTER_SPACING_KEY, defaultValue = 0.0),
            paragraphSpacing = readDouble(READER_PARAGRAPH_SPACING_KEY, defaultValue = 0.0),
            hideFurigana = settingsStore.readBoolean(READER_HIDE_FURIGANA_KEY),
            showTitle = settingsStore.readBoolean(READER_SHOW_TITLE_KEY, defaultValue = true),
            showCharacters = settingsStore.readBoolean(READER_SHOW_CHARACTERS_KEY, defaultValue = true),
            showPercentage = settingsStore.readBoolean(READER_SHOW_PERCENTAGE_KEY, defaultValue = true),
            showProgressTop = settingsStore.readBoolean(READER_SHOW_PROGRESS_TOP_KEY, defaultValue = true),
            showReadingSpeed = settingsStore.readBoolean(READER_SHOW_READING_SPEED_KEY, defaultValue = true),
            showReadingTime = settingsStore.readBoolean(READER_SHOW_READING_TIME_KEY, defaultValue = true),
        )
    }

    private suspend fun ChimahonReaderSettings.loadReaderPerformanceSettings(): ChimahonReaderSettings {
        return copy(
            tapZonePercent = settingsStore.readInt(READER_TAP_ZONE_PERCENT_KEY, defaultValue = 20),
            chapterSwipeDistance = settingsStore.readInt(READER_CHAPTER_SWIPE_DISTANCE_KEY, defaultValue = 96),
            readerStartupDelay = settingsStore.readBoolean(READER_STARTUP_DELAY_KEY),
            showReadingMode = settingsStore.readBoolean(READER_SHOW_READING_MODE_KEY, defaultValue = true),
            showNavigationOverlayOnStart = settingsStore.readBoolean(
                READER_SHOW_NAVIGATION_OVERLAY_KEY,
            ),
            skipReadChapters = settingsStore.readBoolean(READER_SKIP_READ_CHAPTERS_KEY),
            skipFilteredChapters = settingsStore.readBoolean(READER_SKIP_FILTERED_CHAPTERS_KEY),
            skipDuplicateChapters = settingsStore.readBoolean(READER_SKIP_DUPLICATE_CHAPTERS_KEY),
            alwaysShowChapterTransition = settingsStore.readBoolean(READER_ALWAYS_SHOW_TRANSITION_KEY),
            navigateToPan = settingsStore.readBoolean(READER_NAVIGATE_TO_PAN_KEY),
            folderPerManga = settingsStore.readBoolean(READER_FOLDER_PER_MANGA_KEY),
            preloadSize = settingsStore.readInt(READER_PRELOAD_SIZE_KEY, defaultValue = 6),
            readerThreads = settingsStore.readInt(READER_THREADS_KEY, defaultValue = 2),
            readerCacheSizeMb = settingsStore.readInt(READER_CACHE_SIZE_MB_KEY, defaultValue = 250),
            aggressivePageLoading = settingsStore.readBoolean(READER_AGGRESSIVE_LOADING_KEY),
            preserveReadingPosition = settingsStore.readBoolean(
                READER_PRESERVE_READING_POSITION_KEY,
                defaultValue = true,
            ),
            useAutoWebtoon = settingsStore.readBoolean(READER_USE_AUTO_WEBTOON_KEY),
            invertDoublePages = settingsStore.readBoolean(READER_INVERT_DOUBLE_PAGES_KEY),
            rotateWidePagesToFitWebtoon = settingsStore.readBoolean(READER_ROTATE_WIDE_PAGES_TO_FIT_WEBTOON_KEY),
            invertWidePageRotationWebtoon = settingsStore.readBoolean(READER_INVERT_WIDE_PAGE_ROTATION_WEBTOON_KEY),
            centerMarginDp = settingsStore.readInt(READER_CENTER_MARGIN_DP_KEY),
            pagedZoomStart = readEnum(READER_PAGED_ZOOM_START_KEY, ChimahonReaderZoomStart.Automatic),
            landscapeZoom = settingsStore.readBoolean(READER_LANDSCAPE_ZOOM_KEY, defaultValue = true),
            landscapeZoomType = readEnum(
                READER_LANDSCAPE_ZOOM_TYPE_KEY,
                ChimahonReaderLandscapeZoomType.Fit,
            ),
            pagedDisableZoomIn = settingsStore.readBoolean(READER_PAGED_DISABLE_ZOOM_IN_KEY),
            webtoonScaleType = readEnum(READER_WEBTOON_SCALE_TYPE_KEY, ChimahonWebtoonScaleType.Fit),
            smartLongStripGapScale = settingsStore.readBoolean(READER_SMART_LONG_STRIP_GAP_SCALE_KEY),
            webtoonSidePaddingPercent = settingsStore.readInt(READER_WEBTOON_SIDE_PADDING_KEY),
            webtoonReaderHideThreshold = readEnum(
                READER_WEBTOON_HIDE_THRESHOLD_KEY,
                ChimahonReaderHideThreshold.Low,
            ),
            webtoonPinchToZoom = settingsStore.readBoolean(
                READER_WEBTOON_PINCH_TO_ZOOM_KEY,
                defaultValue = true,
            ),
            webtoonDisableZoomOut = settingsStore.readBoolean(READER_WEBTOON_DISABLE_ZOOM_OUT_KEY),
            continuousVerticalTappingByPage = settingsStore.readBoolean(
                READER_CONTINUOUS_VERTICAL_TAPPING_BY_PAGE_KEY,
            ),
            ocrAutoOnDownload = settingsStore.readBoolean(READER_OCR_AUTO_ON_DOWNLOAD_KEY),
            bottomButtons = readStringList(
                READER_BOTTOM_BUTTONS_KEY,
                defaultValue = ChimahonReaderSettings().bottomButtons,
            ),
        )
    }

    suspend fun saveReaderSettings(settings: ChimahonReaderSettings): ChimahonReaderSettings {
        settingsStore.writeString(READER_MODE_KEY, settings.mode.name)
        settingsStore.writeString(READER_SCALE_KEY, settings.scale.name)
        settingsStore.writeString(READER_CANVAS_KEY, settings.canvas.name)
        settingsStore.writeBoolean(READER_PURE_BLACK_BACKGROUND_KEY, settings.pureBlackBackground)
        settingsStore.writeString(READER_ORIENTATION_KEY, settings.orientation.name)
        settingsStore.writeString(READER_DUAL_PAGE_MODE_KEY, settings.dualPageMode.name)
        settingsStore.writeBoolean(READER_SPLIT_WIDE_PAGES_KEY, settings.splitWidePages)
        settingsStore.writeBoolean(READER_ROTATE_WIDE_PAGES_TO_FIT_KEY, settings.rotateWidePagesToFit)
        settingsStore.writeBoolean(READER_INVERT_WIDE_PAGE_ROTATION_KEY, settings.invertWidePageRotation)
        settingsStore.writeBoolean(READER_CUSTOM_BRIGHTNESS_ENABLED_KEY, settings.customBrightnessEnabled)
        settingsStore.writeInt(READER_CUSTOM_BRIGHTNESS_VALUE_KEY, settings.customBrightnessValue)
        settingsStore.writeBoolean(READER_COLOR_FILTER_ENABLED_KEY, settings.colorFilterEnabled)
        settingsStore.writeInt(READER_COLOR_FILTER_VALUE_KEY, settings.colorFilterValue)
        settingsStore.writeString(READER_COLOR_FILTER_MODE_KEY, settings.colorFilterMode.name)
        settingsStore.writeBoolean(READER_GRAYSCALE_KEY, settings.grayscale)
        settingsStore.writeBoolean(READER_INVERT_COLORS_KEY, settings.invertColors)
        settingsStore.writeInt(READER_BRIGHTNESS_KEY, settings.brightness)
        settingsStore.writeInt(
            READER_DOUBLE_TAP_ANIMATION_SPEED_KEY,
            settings.doubleTapAnimationSpeedMillis,
        )
        settingsStore.writeString(READER_NAVIGATION_MODE_KEY, settings.navigationMode.name)
        settingsStore.writeBoolean(READER_PAGE_STRIP_KEY, settings.showPageStrip)
        settingsStore.writeBoolean(READER_FORCE_HORIZONTAL_SEEKBAR_KEY, settings.forceHorizontalSeekbar)
        settingsStore.writeBoolean(READER_LANDSCAPE_VERTICAL_SEEKBAR_KEY, settings.landscapeVerticalSeekbar)
        settingsStore.writeBoolean(READER_LEFT_VERTICAL_SEEKBAR_KEY, settings.leftVerticalSeekbar)
        settingsStore.writeBoolean(READER_CONTROLS_KEY, settings.keepControlsVisible)
        settingsStore.writeBoolean(READER_TAP_ZONES_KEY, settings.tapZonesEnabled)
        settingsStore.writeBoolean(READER_SMALLER_TAP_ZONES_KEY, settings.smallerTapZones)
        settingsStore.writeString(READER_INVERT_TAP_ZONES_KEY, settings.invertTapZones.name)
        settingsStore.writeBoolean(READER_SWIPE_NAVIGATION_KEY, settings.swipeNavigationEnabled)
        settingsStore.writeBoolean(READER_DOUBLE_TAP_ZOOM_KEY, settings.doubleTapToZoom)
        settingsStore.writeBoolean(READER_VOLUME_KEYS_KEY, settings.volumeKeysEnabled)
        settingsStore.writeBoolean(READER_VOLUME_KEYS_INVERTED_KEY, settings.volumeKeysInverted)
        settingsStore.writeBoolean(READER_LONG_TAP_KEY, settings.longTapEnabled)
        settingsStore.writeBoolean(READER_READ_WITH_LONG_TAP_KEY, settings.readWithLongTap)
        settingsStore.writeBoolean(READER_KEEP_SCREEN_ON_KEY, settings.keepScreenOn)
        settingsStore.writeBoolean(READER_FULLSCREEN_KEY, settings.fullscreen)
        settingsStore.writeBoolean(READER_DRAW_UNDER_CUTOUT_KEY, settings.drawUnderCutout)
        settingsStore.writeBoolean(READER_OCR_OUTLINE_VISIBLE_KEY, settings.ocrOutlineVisible)
        settingsStore.writeBoolean(READER_CROP_BORDERS_KEY, settings.cropBorders)
        settingsStore.writeBoolean(READER_PAGE_TRANSITIONS_KEY, settings.pageTransitions)
        settingsStore.writeBoolean(READER_E_INK_SWIPE_SENSITIVITY_KEY, settings.eInkSwipeSensitivity)
        settingsStore.writeBoolean(READER_FLASH_ON_PAGE_CHANGE_KEY, settings.flashOnPageChange)
        settingsStore.writeInt(READER_FLASH_DURATION_MILLIS_KEY, settings.flashDurationMillis)
        settingsStore.writeInt(READER_FLASH_PAGE_INTERVAL_KEY, settings.flashPageInterval)
        settingsStore.writeString(READER_FLASH_COLOR_KEY, settings.flashColor.name)
        settingsStore.writeBoolean(READER_SHOW_PAGE_NUMBER_KEY, settings.showPageNumber)
        settingsStore.writeBoolean(READER_VERTICAL_WRITING_KEY, settings.verticalWriting)
        settingsStore.writeBoolean(READER_CONTINUOUS_MODE_KEY, settings.continuousMode)
        writeDouble(READER_FONT_SIZE_KEY, settings.fontSize)
        writeDouble(READER_LINE_HEIGHT_KEY, settings.lineHeight)
        writeDouble(READER_HORIZONTAL_PADDING_KEY, settings.horizontalPadding)
        writeDouble(READER_VERTICAL_PADDING_KEY, settings.verticalPadding)
        settingsStore.writeBoolean(READER_AVOID_PAGE_BREAK_KEY, settings.avoidPageBreak)
        settingsStore.writeBoolean(READER_JUSTIFY_TEXT_KEY, settings.justifyText)
        writeDouble(READER_CHARACTER_SPACING_KEY, settings.characterSpacing)
        writeDouble(READER_PARAGRAPH_SPACING_KEY, settings.paragraphSpacing)
        settingsStore.writeBoolean(READER_HIDE_FURIGANA_KEY, settings.hideFurigana)
        settingsStore.writeBoolean(READER_SHOW_TITLE_KEY, settings.showTitle)
        settingsStore.writeBoolean(READER_SHOW_CHARACTERS_KEY, settings.showCharacters)
        settingsStore.writeBoolean(READER_SHOW_PERCENTAGE_KEY, settings.showPercentage)
        settingsStore.writeBoolean(READER_SHOW_PROGRESS_TOP_KEY, settings.showProgressTop)
        settingsStore.writeBoolean(READER_SHOW_READING_SPEED_KEY, settings.showReadingSpeed)
        settingsStore.writeBoolean(READER_SHOW_READING_TIME_KEY, settings.showReadingTime)
        settingsStore.writeInt(READER_TAP_ZONE_PERCENT_KEY, settings.tapZonePercent)
        settingsStore.writeInt(READER_CHAPTER_SWIPE_DISTANCE_KEY, settings.chapterSwipeDistance)
        settingsStore.writeBoolean(READER_STARTUP_DELAY_KEY, settings.readerStartupDelay)
        settingsStore.writeBoolean(READER_SHOW_READING_MODE_KEY, settings.showReadingMode)
        settingsStore.writeBoolean(
            READER_SHOW_NAVIGATION_OVERLAY_KEY,
            settings.showNavigationOverlayOnStart,
        )
        settingsStore.writeBoolean(READER_SKIP_READ_CHAPTERS_KEY, settings.skipReadChapters)
        settingsStore.writeBoolean(READER_SKIP_FILTERED_CHAPTERS_KEY, settings.skipFilteredChapters)
        settingsStore.writeBoolean(READER_SKIP_DUPLICATE_CHAPTERS_KEY, settings.skipDuplicateChapters)
        settingsStore.writeBoolean(READER_ALWAYS_SHOW_TRANSITION_KEY, settings.alwaysShowChapterTransition)
        settingsStore.writeBoolean(READER_NAVIGATE_TO_PAN_KEY, settings.navigateToPan)
        settingsStore.writeBoolean(READER_FOLDER_PER_MANGA_KEY, settings.folderPerManga)
        settingsStore.writeInt(READER_PRELOAD_SIZE_KEY, settings.preloadSize)
        settingsStore.writeInt(READER_THREADS_KEY, settings.readerThreads)
        settingsStore.writeInt(READER_CACHE_SIZE_MB_KEY, settings.readerCacheSizeMb)
        settingsStore.writeBoolean(READER_AGGRESSIVE_LOADING_KEY, settings.aggressivePageLoading)
        settingsStore.writeBoolean(READER_PRESERVE_READING_POSITION_KEY, settings.preserveReadingPosition)
        settingsStore.writeBoolean(READER_USE_AUTO_WEBTOON_KEY, settings.useAutoWebtoon)
        settingsStore.writeBoolean(READER_INVERT_DOUBLE_PAGES_KEY, settings.invertDoublePages)
        settingsStore.writeBoolean(READER_ROTATE_WIDE_PAGES_TO_FIT_WEBTOON_KEY, settings.rotateWidePagesToFitWebtoon)
        settingsStore.writeBoolean(READER_INVERT_WIDE_PAGE_ROTATION_WEBTOON_KEY, settings.invertWidePageRotationWebtoon)
        settingsStore.writeInt(READER_CENTER_MARGIN_DP_KEY, settings.centerMarginDp)
        settingsStore.writeString(READER_PAGED_ZOOM_START_KEY, settings.pagedZoomStart.name)
        settingsStore.writeBoolean(READER_LANDSCAPE_ZOOM_KEY, settings.landscapeZoom)
        settingsStore.writeString(READER_LANDSCAPE_ZOOM_TYPE_KEY, settings.landscapeZoomType.name)
        settingsStore.writeBoolean(READER_PAGED_DISABLE_ZOOM_IN_KEY, settings.pagedDisableZoomIn)
        settingsStore.writeString(READER_WEBTOON_SCALE_TYPE_KEY, settings.webtoonScaleType.name)
        settingsStore.writeBoolean(
            READER_SMART_LONG_STRIP_GAP_SCALE_KEY,
            settings.smartLongStripGapScale,
        )
        settingsStore.writeInt(READER_WEBTOON_SIDE_PADDING_KEY, settings.webtoonSidePaddingPercent)
        settingsStore.writeString(
            READER_WEBTOON_HIDE_THRESHOLD_KEY,
            settings.webtoonReaderHideThreshold.name,
        )
        settingsStore.writeBoolean(READER_WEBTOON_PINCH_TO_ZOOM_KEY, settings.webtoonPinchToZoom)
        settingsStore.writeBoolean(READER_WEBTOON_DISABLE_ZOOM_OUT_KEY, settings.webtoonDisableZoomOut)
        settingsStore.writeBoolean(
            READER_CONTINUOUS_VERTICAL_TAPPING_BY_PAGE_KEY,
            settings.continuousVerticalTappingByPage,
        )
        settingsStore.writeBoolean(READER_OCR_AUTO_ON_DOWNLOAD_KEY, settings.ocrAutoOnDownload)
        writeStringList(READER_BOTTOM_BUTTONS_KEY, settings.bottomButtons)
        return settings
    }

    suspend fun loadLibrarySettings(): ChimahonLibrarySettings {
        return ChimahonLibrarySettings(
            displayMode = readEnum(
                LIBRARY_DISPLAY_MODE_KEY,
                ChimahonLibraryDisplayMode.ComfortableGrid,
            ),
            gridColumnsPortrait = settingsStore.readInt(LIBRARY_GRID_COLUMNS_PORTRAIT_KEY),
            gridColumnsLandscape = settingsStore.readInt(LIBRARY_GRID_COLUMNS_LANDSCAPE_KEY),
            coverAspectRatio = readEnum(
                LIBRARY_COVER_ASPECT_RATIO_KEY,
                ChimahonLibraryCoverRatio.Automatic,
            ),
            defaultCategory = settingsStore.readString(LIBRARY_DEFAULT_CATEGORY_KEY) ?: "Default",
            categorizedDisplaySettings = settingsStore.readBoolean(
                LIBRARY_CATEGORIZED_DISPLAY_SETTINGS_KEY,
            ),
            showCategoryTabs = settingsStore.readBoolean(
                LIBRARY_CATEGORY_TABS_KEY,
                defaultValue = true,
            ),
            showUnreadBadges = settingsStore.readBoolean(
                LIBRARY_UNREAD_BADGES_KEY,
                defaultValue = true,
            ),
            showDownloadedBadges = settingsStore.readBoolean(
                LIBRARY_DOWNLOADED_BADGES_KEY,
                defaultValue = true,
            ),
            showLanguageBadges = settingsStore.readBoolean(LIBRARY_LANGUAGE_BADGES_KEY),
            showContinueButtons = settingsStore.readBoolean(
                LIBRARY_CONTINUE_BUTTONS_KEY,
                defaultValue = true,
            ),
            sort = readEnum(LIBRARY_SORT_KEY, ChimahonLibrarySort.Alphabetical),
            sortAscending = settingsStore.readBoolean(LIBRARY_SORT_ASCENDING_KEY, defaultValue = true),
            downloadedFilter = readEnum(LIBRARY_FILTER_DOWNLOADED_KEY, ChimahonFilterMode.Any),
            unreadFilter = readEnum(LIBRARY_FILTER_UNREAD_KEY, ChimahonFilterMode.Any),
            startedFilter = readEnum(LIBRARY_FILTER_STARTED_KEY, ChimahonFilterMode.Any),
            bookmarkedFilter = readEnum(LIBRARY_FILTER_BOOKMARKED_KEY, ChimahonFilterMode.Any),
            completedFilter = readEnum(LIBRARY_FILTER_COMPLETED_KEY, ChimahonFilterMode.Any),
            trackedFilter = readEnum(LIBRARY_FILTER_TRACKED_KEY, ChimahonFilterMode.Any),
            autoUpdateIntervalHours = settingsStore.readInt(LIBRARY_UPDATE_INTERVAL_KEY),
            updateOnlyOnWifi = settingsStore.readBoolean(LIBRARY_UPDATE_WIFI_ONLY_KEY, defaultValue = true),
            updateRestrictions = readStringList(
                LIBRARY_UPDATE_RESTRICTIONS_KEY,
                defaultValue = listOf("Wi-Fi only"),
            ),
            updateIncludedCategories = readStringList(LIBRARY_UPDATE_INCLUDED_CATEGORIES_KEY),
            updateExcludedCategories = readStringList(LIBRARY_UPDATE_EXCLUDED_CATEGORIES_KEY),
            updateGroupMode = readEnum(
                LIBRARY_UPDATE_GROUP_MODE_KEY,
                ChimahonLibraryUpdateGroupMode.Global,
            ),
            autoUpdateMetadata = settingsStore.readBoolean(LIBRARY_AUTO_UPDATE_METADATA_KEY),
            smartUpdateRestrictions = readStringList(
                LIBRARY_SMART_UPDATE_RESTRICTIONS_KEY,
                defaultValue = listOf(
                    "Has unread chapters",
                    "Started",
                    "Not completed",
                    "In release period",
                ),
            ),
            showUpdateCount = settingsStore.readBoolean(LIBRARY_SHOW_UPDATE_COUNT_KEY, defaultValue = true),
            updateNotificationsEnabled = settingsStore.readBoolean(
                LIBRARY_UPDATE_NOTIFICATIONS_KEY,
                defaultValue = true,
            ),
            showUpdatingProgressBanner = settingsStore.readBoolean(
                LIBRARY_SHOW_UPDATING_PROGRESS_BANNER_KEY,
                defaultValue = true,
            ),
            swipeToStartAction = readEnum(
                LIBRARY_SWIPE_TO_START_ACTION_KEY,
                ChimahonChapterSwipeAction.ToggleBookmark,
            ),
            swipeToEndAction = readEnum(
                LIBRARY_SWIPE_TO_END_ACTION_KEY,
                ChimahonChapterSwipeAction.ToggleRead,
            ),
            duplicateReadChapterHandling = readStringList(LIBRARY_DUPLICATE_READ_CHAPTER_HANDLING_KEY),
            hideMissingChapters = settingsStore.readBoolean(LIBRARY_HIDE_MISSING_CHAPTERS_KEY),
            showEmptyCategoriesSearch = settingsStore.readBoolean(LIBRARY_SHOW_EMPTY_CATEGORIES_SEARCH_KEY),
            fetchMetadataOnAdd = settingsStore.readBoolean(LIBRARY_FETCH_METADATA_ON_ADD_KEY),
            fetchChaptersOnAdd = settingsStore.readBoolean(LIBRARY_FETCH_CHAPTERS_ON_ADD_KEY),
            updateMangaTitles = settingsStore.readBoolean(LIBRARY_UPDATE_MANGA_TITLES_KEY),
            disallowNonAsciiFilenames = settingsStore.readBoolean(
                LIBRARY_DISALLOW_NON_ASCII_FILENAMES_KEY,
            ),
        )
    }

    suspend fun saveLibrarySettings(settings: ChimahonLibrarySettings): ChimahonLibrarySettings {
        settingsStore.writeString(LIBRARY_DISPLAY_MODE_KEY, settings.displayMode.name)
        settingsStore.writeInt(LIBRARY_GRID_COLUMNS_PORTRAIT_KEY, settings.gridColumnsPortrait)
        settingsStore.writeInt(LIBRARY_GRID_COLUMNS_LANDSCAPE_KEY, settings.gridColumnsLandscape)
        settingsStore.writeString(LIBRARY_COVER_ASPECT_RATIO_KEY, settings.coverAspectRatio.name)
        settingsStore.writeString(LIBRARY_DEFAULT_CATEGORY_KEY, settings.defaultCategory)
        settingsStore.writeBoolean(
            LIBRARY_CATEGORIZED_DISPLAY_SETTINGS_KEY,
            settings.categorizedDisplaySettings,
        )
        settingsStore.writeBoolean(LIBRARY_CATEGORY_TABS_KEY, settings.showCategoryTabs)
        settingsStore.writeBoolean(LIBRARY_UNREAD_BADGES_KEY, settings.showUnreadBadges)
        settingsStore.writeBoolean(LIBRARY_DOWNLOADED_BADGES_KEY, settings.showDownloadedBadges)
        settingsStore.writeBoolean(LIBRARY_LANGUAGE_BADGES_KEY, settings.showLanguageBadges)
        settingsStore.writeBoolean(LIBRARY_CONTINUE_BUTTONS_KEY, settings.showContinueButtons)
        settingsStore.writeString(LIBRARY_SORT_KEY, settings.sort.name)
        settingsStore.writeBoolean(LIBRARY_SORT_ASCENDING_KEY, settings.sortAscending)
        settingsStore.writeString(LIBRARY_FILTER_DOWNLOADED_KEY, settings.downloadedFilter.name)
        settingsStore.writeString(LIBRARY_FILTER_UNREAD_KEY, settings.unreadFilter.name)
        settingsStore.writeString(LIBRARY_FILTER_STARTED_KEY, settings.startedFilter.name)
        settingsStore.writeString(LIBRARY_FILTER_BOOKMARKED_KEY, settings.bookmarkedFilter.name)
        settingsStore.writeString(LIBRARY_FILTER_COMPLETED_KEY, settings.completedFilter.name)
        settingsStore.writeString(LIBRARY_FILTER_TRACKED_KEY, settings.trackedFilter.name)
        settingsStore.writeInt(LIBRARY_UPDATE_INTERVAL_KEY, settings.autoUpdateIntervalHours)
        settingsStore.writeBoolean(LIBRARY_UPDATE_WIFI_ONLY_KEY, settings.updateOnlyOnWifi)
        writeStringList(LIBRARY_UPDATE_RESTRICTIONS_KEY, settings.updateRestrictions)
        writeStringList(LIBRARY_UPDATE_INCLUDED_CATEGORIES_KEY, settings.updateIncludedCategories)
        writeStringList(LIBRARY_UPDATE_EXCLUDED_CATEGORIES_KEY, settings.updateExcludedCategories)
        settingsStore.writeString(LIBRARY_UPDATE_GROUP_MODE_KEY, settings.updateGroupMode.name)
        settingsStore.writeBoolean(LIBRARY_AUTO_UPDATE_METADATA_KEY, settings.autoUpdateMetadata)
        writeStringList(LIBRARY_SMART_UPDATE_RESTRICTIONS_KEY, settings.smartUpdateRestrictions)
        settingsStore.writeBoolean(LIBRARY_SHOW_UPDATE_COUNT_KEY, settings.showUpdateCount)
        settingsStore.writeBoolean(LIBRARY_UPDATE_NOTIFICATIONS_KEY, settings.updateNotificationsEnabled)
        settingsStore.writeBoolean(
            LIBRARY_SHOW_UPDATING_PROGRESS_BANNER_KEY,
            settings.showUpdatingProgressBanner,
        )
        settingsStore.writeString(LIBRARY_SWIPE_TO_START_ACTION_KEY, settings.swipeToStartAction.name)
        settingsStore.writeString(LIBRARY_SWIPE_TO_END_ACTION_KEY, settings.swipeToEndAction.name)
        writeStringList(
            LIBRARY_DUPLICATE_READ_CHAPTER_HANDLING_KEY,
            settings.duplicateReadChapterHandling,
        )
        settingsStore.writeBoolean(LIBRARY_HIDE_MISSING_CHAPTERS_KEY, settings.hideMissingChapters)
        settingsStore.writeBoolean(
            LIBRARY_SHOW_EMPTY_CATEGORIES_SEARCH_KEY,
            settings.showEmptyCategoriesSearch,
        )
        settingsStore.writeBoolean(LIBRARY_FETCH_METADATA_ON_ADD_KEY, settings.fetchMetadataOnAdd)
        settingsStore.writeBoolean(LIBRARY_FETCH_CHAPTERS_ON_ADD_KEY, settings.fetchChaptersOnAdd)
        settingsStore.writeBoolean(LIBRARY_UPDATE_MANGA_TITLES_KEY, settings.updateMangaTitles)
        settingsStore.writeBoolean(
            LIBRARY_DISALLOW_NON_ASCII_FILENAMES_KEY,
            settings.disallowNonAsciiFilenames,
        )
        return settings
    }

    suspend fun loadAnimeLibrarySettings(): ChimahonAnimeLibrarySettings {
        return ChimahonAnimeLibrarySettings(
            displayMode = readEnum(
                ANIME_LIBRARY_DISPLAY_MODE_KEY,
                ChimahonLibraryDisplayMode.CompactGrid,
            ),
            gridColumnsPortrait = settingsStore.readInt(ANIME_LIBRARY_GRID_COLUMNS_PORTRAIT_KEY),
            gridColumnsLandscape = settingsStore.readInt(ANIME_LIBRARY_GRID_COLUMNS_LANDSCAPE_KEY),
            defaultCategoryId = settingsStore.readInt(
                ANIME_LIBRARY_DEFAULT_CATEGORY_ID_KEY,
                defaultValue = -1,
            ),
            categorizedDisplaySettings = settingsStore.readBoolean(
                ANIME_LIBRARY_CATEGORIZED_DISPLAY_SETTINGS_KEY,
            ),
            showCategoryTabs = settingsStore.readBoolean(
                ANIME_LIBRARY_CATEGORY_TABS_KEY,
                defaultValue = true,
            ),
            showCategoryItemCount = settingsStore.readBoolean(ANIME_LIBRARY_CATEGORY_ITEM_COUNT_KEY),
            showUnseenBadges = settingsStore.readBoolean(
                ANIME_LIBRARY_UNSEEN_BADGES_KEY,
                defaultValue = true,
            ),
            showDownloadedBadges = settingsStore.readBoolean(ANIME_LIBRARY_DOWNLOADED_BADGES_KEY),
            showLocalBadges = settingsStore.readBoolean(
                ANIME_LIBRARY_LOCAL_BADGES_KEY,
                defaultValue = true,
            ),
            showLanguageBadges = settingsStore.readBoolean(ANIME_LIBRARY_LANGUAGE_BADGES_KEY),
            showContinueWatchingButtons = settingsStore.readBoolean(
                ANIME_LIBRARY_CONTINUE_WATCHING_BUTTONS_KEY,
            ),
            sort = readEnum(ANIME_LIBRARY_SORT_KEY, ChimahonLibrarySort.Alphabetical),
            sortAscending = settingsStore.readBoolean(
                ANIME_LIBRARY_SORT_ASCENDING_KEY,
                defaultValue = true,
            ),
            downloadedFilter = readEnum(
                ANIME_LIBRARY_FILTER_DOWNLOADED_KEY,
                ChimahonFilterMode.Any,
            ),
            unseenFilter = readEnum(ANIME_LIBRARY_FILTER_UNSEEN_KEY, ChimahonFilterMode.Any),
            startedFilter = readEnum(ANIME_LIBRARY_FILTER_STARTED_KEY, ChimahonFilterMode.Any),
            bookmarkedFilter = readEnum(
                ANIME_LIBRARY_FILTER_BOOKMARKED_KEY,
                ChimahonFilterMode.Any,
            ),
            completedFilter = readEnum(ANIME_LIBRARY_FILTER_COMPLETED_KEY, ChimahonFilterMode.Any),
            fillerFilter = readEnum(ANIME_LIBRARY_FILTER_FILLER_KEY, ChimahonFilterMode.Any),
            trackedFilter = readEnum(ANIME_LIBRARY_FILTER_TRACKED_KEY, ChimahonFilterMode.Any),
            groupBy = readEnum(ANIME_LIBRARY_GROUP_BY_KEY, ChimahonLibraryGroup.Default),
            updateRestrictions = readStringList(
                ANIME_LIBRARY_UPDATE_RESTRICTIONS_KEY,
                defaultValue = listOf("Outside release period"),
            ),
            swipeToStartAction = readEnum(
                ANIME_LIBRARY_SWIPE_TO_START_ACTION_KEY,
                ChimahonEpisodeSwipeAction.ToggleSeen,
            ),
            swipeToEndAction = readEnum(
                ANIME_LIBRARY_SWIPE_TO_END_ACTION_KEY,
                ChimahonEpisodeSwipeAction.ToggleSeen,
            ),
        )
    }

    suspend fun saveAnimeLibrarySettings(
        settings: ChimahonAnimeLibrarySettings,
    ): ChimahonAnimeLibrarySettings {
        settingsStore.writeString(ANIME_LIBRARY_DISPLAY_MODE_KEY, settings.displayMode.name)
        settingsStore.writeInt(ANIME_LIBRARY_GRID_COLUMNS_PORTRAIT_KEY, settings.gridColumnsPortrait)
        settingsStore.writeInt(ANIME_LIBRARY_GRID_COLUMNS_LANDSCAPE_KEY, settings.gridColumnsLandscape)
        settingsStore.writeInt(ANIME_LIBRARY_DEFAULT_CATEGORY_ID_KEY, settings.defaultCategoryId)
        settingsStore.writeBoolean(
            ANIME_LIBRARY_CATEGORIZED_DISPLAY_SETTINGS_KEY,
            settings.categorizedDisplaySettings,
        )
        settingsStore.writeBoolean(ANIME_LIBRARY_CATEGORY_TABS_KEY, settings.showCategoryTabs)
        settingsStore.writeBoolean(ANIME_LIBRARY_CATEGORY_ITEM_COUNT_KEY, settings.showCategoryItemCount)
        settingsStore.writeBoolean(ANIME_LIBRARY_UNSEEN_BADGES_KEY, settings.showUnseenBadges)
        settingsStore.writeBoolean(ANIME_LIBRARY_DOWNLOADED_BADGES_KEY, settings.showDownloadedBadges)
        settingsStore.writeBoolean(ANIME_LIBRARY_LOCAL_BADGES_KEY, settings.showLocalBadges)
        settingsStore.writeBoolean(ANIME_LIBRARY_LANGUAGE_BADGES_KEY, settings.showLanguageBadges)
        settingsStore.writeBoolean(
            ANIME_LIBRARY_CONTINUE_WATCHING_BUTTONS_KEY,
            settings.showContinueWatchingButtons,
        )
        settingsStore.writeString(ANIME_LIBRARY_SORT_KEY, settings.sort.name)
        settingsStore.writeBoolean(ANIME_LIBRARY_SORT_ASCENDING_KEY, settings.sortAscending)
        settingsStore.writeString(ANIME_LIBRARY_FILTER_DOWNLOADED_KEY, settings.downloadedFilter.name)
        settingsStore.writeString(ANIME_LIBRARY_FILTER_UNSEEN_KEY, settings.unseenFilter.name)
        settingsStore.writeString(ANIME_LIBRARY_FILTER_STARTED_KEY, settings.startedFilter.name)
        settingsStore.writeString(ANIME_LIBRARY_FILTER_BOOKMARKED_KEY, settings.bookmarkedFilter.name)
        settingsStore.writeString(ANIME_LIBRARY_FILTER_COMPLETED_KEY, settings.completedFilter.name)
        settingsStore.writeString(ANIME_LIBRARY_FILTER_FILLER_KEY, settings.fillerFilter.name)
        settingsStore.writeString(ANIME_LIBRARY_FILTER_TRACKED_KEY, settings.trackedFilter.name)
        settingsStore.writeString(ANIME_LIBRARY_GROUP_BY_KEY, settings.groupBy.name)
        writeStringList(ANIME_LIBRARY_UPDATE_RESTRICTIONS_KEY, settings.updateRestrictions)
        settingsStore.writeString(
            ANIME_LIBRARY_SWIPE_TO_START_ACTION_KEY,
            settings.swipeToStartAction.name,
        )
        settingsStore.writeString(
            ANIME_LIBRARY_SWIPE_TO_END_ACTION_KEY,
            settings.swipeToEndAction.name,
        )
        return settings
    }

    suspend fun loadDownloadSettings(): ChimahonDownloadPreferences {
        return ChimahonDownloadPreferences(
            wifiOnly = settingsStore.readBoolean(DOWNLOAD_WIFI_ONLY_KEY, defaultValue = true),
            saveAsCbz = settingsStore.readBoolean(DOWNLOAD_SAVE_AS_CBZ_KEY, defaultValue = true),
            splitTallImages = settingsStore.readBoolean(DOWNLOAD_SPLIT_TALL_IMAGES_KEY, defaultValue = true),
            autoDownloadWhileReadingCount = settingsStore.readInt(DOWNLOAD_WHILE_READING_KEY),
            removeAfterReadSlots = settingsStore.readInt(DOWNLOAD_REMOVE_AFTER_READ_KEY, defaultValue = -1),
            removeAfterMarkedRead = settingsStore.readBoolean(DOWNLOAD_REMOVE_AFTER_MARKED_READ_KEY),
            removeBookmarkedChapters = settingsStore.readBoolean(DOWNLOAD_REMOVE_BOOKMARKED_KEY),
            removeExcludedCategories = readStringList(DOWNLOAD_REMOVE_EXCLUDED_CATEGORIES_KEY),
            downloadNewChapters = settingsStore.readBoolean(DOWNLOAD_NEW_CHAPTERS_KEY),
            downloadNewUnreadOnly = settingsStore.readBoolean(DOWNLOAD_NEW_UNREAD_ONLY_KEY),
            downloadNewIncludedCategories = readStringList(DOWNLOAD_NEW_INCLUDED_CATEGORIES_KEY),
            downloadNewExcludedCategories = readStringList(DOWNLOAD_NEW_EXCLUDED_CATEGORIES_KEY),
            parallelSourceDownloads = settingsStore.readInt(
                DOWNLOAD_PARALLEL_SOURCES_KEY,
                defaultValue = 5,
            ),
            parallelPageDownloads = settingsStore.readInt(
                DOWNLOAD_PARALLEL_PAGES_KEY,
                defaultValue = 5,
            ),
            includeChapterUrlHash = settingsStore.readBoolean(
                DOWNLOAD_INCLUDE_CHAPTER_URL_HASH_KEY,
                defaultValue = true,
            ),
            downloadCacheRenewIntervalHours = settingsStore.readInt(
                DOWNLOAD_CACHE_RENEW_INTERVAL_KEY,
                defaultValue = 1,
            ),
        )
    }

    suspend fun saveDownloadSettings(
        settings: ChimahonDownloadPreferences,
    ): ChimahonDownloadPreferences {
        settingsStore.writeBoolean(DOWNLOAD_WIFI_ONLY_KEY, settings.wifiOnly)
        settingsStore.writeBoolean(DOWNLOAD_SAVE_AS_CBZ_KEY, settings.saveAsCbz)
        settingsStore.writeBoolean(DOWNLOAD_SPLIT_TALL_IMAGES_KEY, settings.splitTallImages)
        settingsStore.writeInt(DOWNLOAD_WHILE_READING_KEY, settings.autoDownloadWhileReadingCount)
        settingsStore.writeInt(DOWNLOAD_REMOVE_AFTER_READ_KEY, settings.removeAfterReadSlots)
        settingsStore.writeBoolean(DOWNLOAD_REMOVE_AFTER_MARKED_READ_KEY, settings.removeAfterMarkedRead)
        settingsStore.writeBoolean(DOWNLOAD_REMOVE_BOOKMARKED_KEY, settings.removeBookmarkedChapters)
        writeStringList(DOWNLOAD_REMOVE_EXCLUDED_CATEGORIES_KEY, settings.removeExcludedCategories)
        settingsStore.writeBoolean(DOWNLOAD_NEW_CHAPTERS_KEY, settings.downloadNewChapters)
        settingsStore.writeBoolean(DOWNLOAD_NEW_UNREAD_ONLY_KEY, settings.downloadNewUnreadOnly)
        writeStringList(DOWNLOAD_NEW_INCLUDED_CATEGORIES_KEY, settings.downloadNewIncludedCategories)
        writeStringList(DOWNLOAD_NEW_EXCLUDED_CATEGORIES_KEY, settings.downloadNewExcludedCategories)
        settingsStore.writeInt(DOWNLOAD_PARALLEL_SOURCES_KEY, settings.parallelSourceDownloads)
        settingsStore.writeInt(DOWNLOAD_PARALLEL_PAGES_KEY, settings.parallelPageDownloads)
        settingsStore.writeBoolean(DOWNLOAD_INCLUDE_CHAPTER_URL_HASH_KEY, settings.includeChapterUrlHash)
        settingsStore.writeInt(DOWNLOAD_CACHE_RENEW_INTERVAL_KEY, settings.downloadCacheRenewIntervalHours)
        return settings
    }

    suspend fun loadBrowseSettings(): ChimahonBrowseSettings {
        return ChimahonBrowseSettings(
            showNsfwSources = settingsStore.readBoolean(BROWSE_SHOW_NSFW_KEY, defaultValue = true),
            hideLibraryEntries = settingsStore.readBoolean(BROWSE_HIDE_LIBRARY_ENTRIES_KEY),
            autoLoadMore = settingsStore.readBoolean(BROWSE_AUTO_LOAD_MORE_KEY, defaultValue = true),
            enabledLanguages = readStringList(BROWSE_ENABLED_LANGUAGES_KEY),
            pinnedSourceIds = readStringList(BROWSE_PINNED_SOURCE_IDS_KEY).mapNotNull { it.toLongOrNull() },
            disabledExtensionRepoUrls = readStringList(BROWSE_DISABLED_EXTENSION_REPOS_KEY),
            sourceDisplayMode = readEnum(
                BROWSE_SOURCE_DISPLAY_MODE_KEY,
                ChimahonBrowseSourceDisplayMode.List,
            ),
            groupSourcesByLanguage = settingsStore.readBoolean(
                BROWSE_GROUP_SOURCES_BY_LANGUAGE_KEY,
                defaultValue = true,
            ),
            showSourceLanguage = settingsStore.readBoolean(
                BROWSE_SHOW_SOURCE_LANGUAGE_KEY,
                defaultValue = true,
            ),
            extensionUpdateNotificationsEnabled = settingsStore.readBoolean(
                BROWSE_EXTENSION_UPDATE_NOTIFICATIONS_KEY,
                defaultValue = true,
            ),
            relatedMangaRecommendations = settingsStore.readBoolean(
                BROWSE_RELATED_MANGA_RECOMMENDATIONS_KEY,
                defaultValue = true,
            ),
            expandRelatedMangaSections = settingsStore.readBoolean(
                BROWSE_EXPAND_RELATED_MANGA_KEY,
                defaultValue = true,
            ),
            relatedMangaInOverflow = settingsStore.readBoolean(BROWSE_RELATED_MANGA_IN_OVERFLOW_KEY),
            showHomeInRelatedManga = settingsStore.readBoolean(
                BROWSE_SHOW_HOME_IN_RELATED_MANGA_KEY,
                defaultValue = true,
            ),
            sourceCategoriesFilter = settingsStore.readBoolean(BROWSE_SOURCE_CATEGORIES_FILTER_KEY),
            useNewSourceNavigation = settingsStore.readBoolean(
                BROWSE_USE_NEW_SOURCE_NAVIGATION_KEY,
                defaultValue = true,
            ),
            allowLocalSourceHiddenFolders = settingsStore.readBoolean(
                BROWSE_ALLOW_LOCAL_SOURCE_HIDDEN_FOLDERS_KEY,
            ),
            hideFeedTab = settingsStore.readBoolean(BROWSE_HIDE_FEED_TAB_KEY),
            feedTabInFront = settingsStore.readBoolean(BROWSE_FEED_TAB_IN_FRONT_KEY),
            hideLibraryFeedEntries = settingsStore.readBoolean(BROWSE_HIDE_LIBRARY_FEED_ENTRIES_KEY),
        )
    }

    suspend fun saveBrowseSettings(settings: ChimahonBrowseSettings): ChimahonBrowseSettings {
        settingsStore.writeBoolean(BROWSE_SHOW_NSFW_KEY, settings.showNsfwSources)
        settingsStore.writeBoolean(BROWSE_HIDE_LIBRARY_ENTRIES_KEY, settings.hideLibraryEntries)
        settingsStore.writeBoolean(BROWSE_AUTO_LOAD_MORE_KEY, settings.autoLoadMore)
        writeStringList(BROWSE_ENABLED_LANGUAGES_KEY, settings.enabledLanguages)
        writeStringList(BROWSE_PINNED_SOURCE_IDS_KEY, settings.pinnedSourceIds.map(Long::toString))
        writeStringList(BROWSE_DISABLED_EXTENSION_REPOS_KEY, settings.disabledExtensionRepoUrls)
        settingsStore.writeString(BROWSE_SOURCE_DISPLAY_MODE_KEY, settings.sourceDisplayMode.name)
        settingsStore.writeBoolean(
            BROWSE_GROUP_SOURCES_BY_LANGUAGE_KEY,
            settings.groupSourcesByLanguage,
        )
        settingsStore.writeBoolean(BROWSE_SHOW_SOURCE_LANGUAGE_KEY, settings.showSourceLanguage)
        settingsStore.writeBoolean(
            BROWSE_EXTENSION_UPDATE_NOTIFICATIONS_KEY,
            settings.extensionUpdateNotificationsEnabled,
        )
        settingsStore.writeBoolean(
            BROWSE_RELATED_MANGA_RECOMMENDATIONS_KEY,
            settings.relatedMangaRecommendations,
        )
        settingsStore.writeBoolean(BROWSE_EXPAND_RELATED_MANGA_KEY, settings.expandRelatedMangaSections)
        settingsStore.writeBoolean(BROWSE_RELATED_MANGA_IN_OVERFLOW_KEY, settings.relatedMangaInOverflow)
        settingsStore.writeBoolean(
            BROWSE_SHOW_HOME_IN_RELATED_MANGA_KEY,
            settings.showHomeInRelatedManga,
        )
        settingsStore.writeBoolean(BROWSE_SOURCE_CATEGORIES_FILTER_KEY, settings.sourceCategoriesFilter)
        settingsStore.writeBoolean(BROWSE_USE_NEW_SOURCE_NAVIGATION_KEY, settings.useNewSourceNavigation)
        settingsStore.writeBoolean(
            BROWSE_ALLOW_LOCAL_SOURCE_HIDDEN_FOLDERS_KEY,
            settings.allowLocalSourceHiddenFolders,
        )
        settingsStore.writeBoolean(BROWSE_HIDE_FEED_TAB_KEY, settings.hideFeedTab)
        settingsStore.writeBoolean(BROWSE_FEED_TAB_IN_FRONT_KEY, settings.feedTabInFront)
        settingsStore.writeBoolean(BROWSE_HIDE_LIBRARY_FEED_ENTRIES_KEY, settings.hideLibraryFeedEntries)
        return settings
    }

    suspend fun loadNavigationSettings(): ChimahonNavigationSettings {
        return ChimahonNavigationSettings(
            tabLayout = parseNavigationTabLayout(
                settingsStore.readString(NAVIGATION_TAB_LAYOUT_KEY),
            ),
            startScreen = readEnum(NAVIGATION_START_SCREEN_KEY, ChimahonStartScreen.Library),
            showUpdatesTab = settingsStore.readBoolean(NAVIGATION_SHOW_UPDATES_TAB_KEY),
            showHistoryTab = settingsStore.readBoolean(
                NAVIGATION_SHOW_HISTORY_TAB_KEY,
                defaultValue = true,
            ),
        )
    }

    suspend fun saveNavigationSettings(
        settings: ChimahonNavigationSettings,
    ): ChimahonNavigationSettings {
        settingsStore.writeString(
            NAVIGATION_TAB_LAYOUT_KEY,
            serializeNavigationTabLayout(settings.tabLayout),
        )
        settingsStore.writeString(NAVIGATION_START_SCREEN_KEY, settings.startScreen.name)
        settingsStore.writeBoolean(NAVIGATION_SHOW_UPDATES_TAB_KEY, settings.showUpdatesTab)
        settingsStore.writeBoolean(NAVIGATION_SHOW_HISTORY_TAB_KEY, settings.showHistoryTab)
        return settings
    }

    suspend fun loadPlayerSettings(): ChimahonPlayerSettings {
        return ChimahonPlayerSettings(
            preserveWatchingPosition = settingsStore.readBoolean(PLAYER_PRESERVE_POSITION_KEY),
            progressPreference = readDouble(PLAYER_PROGRESS_PREFERENCE_KEY, defaultValue = 0.85),
            defaultOrientation = readEnum(
                PLAYER_DEFAULT_ORIENTATION_KEY,
                ChimahonPlayerOrientation.SensorLandscape,
            ),
            allowGesturesInPanels = settingsStore.readBoolean(PLAYER_ALLOW_GESTURES_IN_PANELS_KEY),
            showLoadingCircle = settingsStore.readBoolean(PLAYER_SHOW_LOADING_KEY, defaultValue = true),
            showCurrentEpisode = settingsStore.readBoolean(
                PLAYER_SHOW_CURRENT_EPISODE_KEY,
                defaultValue = true,
            ),
            rememberBrightness = settingsStore.readBoolean(PLAYER_REMEMBER_BRIGHTNESS_KEY),
            rememberedBrightness = readDouble(PLAYER_BRIGHTNESS_VALUE_KEY, defaultValue = -1.0),
            rememberVolume = settingsStore.readBoolean(PLAYER_REMEMBER_VOLUME_KEY),
            rememberedVolume = readDouble(PLAYER_VOLUME_VALUE_KEY, defaultValue = -1.0),
            showFailedHosters = settingsStore.readBoolean(PLAYER_SHOW_FAILED_HOSTERS_KEY),
            showEmptyHosters = settingsStore.readBoolean(PLAYER_SHOW_EMPTY_HOSTERS_KEY),
            fullscreen = settingsStore.readBoolean(PLAYER_FULLSCREEN_KEY, defaultValue = true),
            hideControls = settingsStore.readBoolean(PLAYER_HIDE_CONTROLS_KEY),
            displayVolumePercent = settingsStore.readBoolean(
                PLAYER_DISPLAY_VOLUME_PERCENT_KEY,
                defaultValue = true,
            ),
            showSystemStatusBar = settingsStore.readBoolean(PLAYER_SHOW_SYSTEM_STATUS_BAR_KEY),
            reduceMotion = settingsStore.readBoolean(PLAYER_REDUCE_MOTION_KEY),
            controlsHideDelayMillis = settingsStore.readInt(
                PLAYER_CONTROLS_HIDE_DELAY_KEY,
                defaultValue = 4000,
            ),
            panelOpacityPercent = settingsStore.readInt(PLAYER_PANEL_OPACITY_KEY, defaultValue = 60),
            skipIntroEnabled = settingsStore.readBoolean(PLAYER_SKIP_INTRO_ENABLED_KEY, defaultValue = true),
            autoSkipIntro = settingsStore.readBoolean(PLAYER_AUTO_SKIP_INTRO_KEY),
            netflixStyleSkipIntro = settingsStore.readBoolean(PLAYER_NETFLIX_STYLE_SKIP_INTRO_KEY),
            skipIntroWaitSeconds = settingsStore.readInt(PLAYER_SKIP_INTRO_WAIT_KEY, defaultValue = 5),
            aniSkipEnabled = settingsStore.readBoolean(PLAYER_ANI_SKIP_ENABLED_KEY),
            disableAniSkipOnChapters = settingsStore.readBoolean(
                PLAYER_DISABLE_ANI_SKIP_ON_CHAPTERS_KEY,
                defaultValue = true,
            ),
            pipEnabled = settingsStore.readBoolean(PLAYER_PIP_ENABLED_KEY, defaultValue = true),
            pipEpisodeToasts = settingsStore.readBoolean(
                PLAYER_PIP_EPISODE_TOASTS_KEY,
                defaultValue = true,
            ),
            pipOnExit = settingsStore.readBoolean(PLAYER_PIP_ON_EXIT_KEY),
            pipReplaceWithPrevious = settingsStore.readBoolean(PLAYER_PIP_REPLACE_WITH_PREVIOUS_KEY),
            castEnabled = settingsStore.readBoolean(PLAYER_CAST_ENABLED_KEY),
            alwaysUseExternalPlayer = settingsStore.readBoolean(PLAYER_ALWAYS_EXTERNAL_KEY),
            externalPlayerPackage = settingsStore.readString(PLAYER_EXTERNAL_PACKAGE_KEY) ?: "",
            playerSpeed = readDouble(PLAYER_SPEED_KEY, defaultValue = 1.0),
            speedPresets = readStringList(
                PLAYER_SPEED_PRESETS_KEY,
                defaultValue = ChimahonPlayerSettings().speedPresets,
            ),
            invertDuration = settingsStore.readBoolean(PLAYER_INVERT_DURATION_KEY),
            aspect = readEnum(PLAYER_ASPECT_KEY, ChimahonPlayerAspect.Fit),
            autoplayEnabled = settingsStore.readBoolean(PLAYER_AUTOPLAY_KEY),
            gestures = loadPlayerGestureSettings(),
            decoder = loadPlayerDecoderSettings(),
            subtitles = loadPlayerSubtitleSettings(),
            audio = loadPlayerAudioSettings(),
            selections = loadPlayerSelectionSettings(),
            advanced = loadPlayerAdvancedSettings(),
        )
    }

    suspend fun savePlayerSettings(settings: ChimahonPlayerSettings): ChimahonPlayerSettings {
        settingsStore.writeBoolean(PLAYER_PRESERVE_POSITION_KEY, settings.preserveWatchingPosition)
        writeDouble(PLAYER_PROGRESS_PREFERENCE_KEY, settings.progressPreference)
        settingsStore.writeString(PLAYER_DEFAULT_ORIENTATION_KEY, settings.defaultOrientation.name)
        settingsStore.writeBoolean(PLAYER_ALLOW_GESTURES_IN_PANELS_KEY, settings.allowGesturesInPanels)
        settingsStore.writeBoolean(PLAYER_SHOW_LOADING_KEY, settings.showLoadingCircle)
        settingsStore.writeBoolean(PLAYER_SHOW_CURRENT_EPISODE_KEY, settings.showCurrentEpisode)
        settingsStore.writeBoolean(PLAYER_REMEMBER_BRIGHTNESS_KEY, settings.rememberBrightness)
        writeDouble(PLAYER_BRIGHTNESS_VALUE_KEY, settings.rememberedBrightness)
        settingsStore.writeBoolean(PLAYER_REMEMBER_VOLUME_KEY, settings.rememberVolume)
        writeDouble(PLAYER_VOLUME_VALUE_KEY, settings.rememberedVolume)
        settingsStore.writeBoolean(PLAYER_SHOW_FAILED_HOSTERS_KEY, settings.showFailedHosters)
        settingsStore.writeBoolean(PLAYER_SHOW_EMPTY_HOSTERS_KEY, settings.showEmptyHosters)
        settingsStore.writeBoolean(PLAYER_FULLSCREEN_KEY, settings.fullscreen)
        settingsStore.writeBoolean(PLAYER_HIDE_CONTROLS_KEY, settings.hideControls)
        settingsStore.writeBoolean(PLAYER_DISPLAY_VOLUME_PERCENT_KEY, settings.displayVolumePercent)
        settingsStore.writeBoolean(PLAYER_SHOW_SYSTEM_STATUS_BAR_KEY, settings.showSystemStatusBar)
        settingsStore.writeBoolean(PLAYER_REDUCE_MOTION_KEY, settings.reduceMotion)
        settingsStore.writeInt(PLAYER_CONTROLS_HIDE_DELAY_KEY, settings.controlsHideDelayMillis)
        settingsStore.writeInt(PLAYER_PANEL_OPACITY_KEY, settings.panelOpacityPercent)
        settingsStore.writeBoolean(PLAYER_SKIP_INTRO_ENABLED_KEY, settings.skipIntroEnabled)
        settingsStore.writeBoolean(PLAYER_AUTO_SKIP_INTRO_KEY, settings.autoSkipIntro)
        settingsStore.writeBoolean(PLAYER_NETFLIX_STYLE_SKIP_INTRO_KEY, settings.netflixStyleSkipIntro)
        settingsStore.writeInt(PLAYER_SKIP_INTRO_WAIT_KEY, settings.skipIntroWaitSeconds)
        settingsStore.writeBoolean(PLAYER_ANI_SKIP_ENABLED_KEY, settings.aniSkipEnabled)
        settingsStore.writeBoolean(
            PLAYER_DISABLE_ANI_SKIP_ON_CHAPTERS_KEY,
            settings.disableAniSkipOnChapters,
        )
        settingsStore.writeBoolean(PLAYER_PIP_ENABLED_KEY, settings.pipEnabled)
        settingsStore.writeBoolean(PLAYER_PIP_EPISODE_TOASTS_KEY, settings.pipEpisodeToasts)
        settingsStore.writeBoolean(PLAYER_PIP_ON_EXIT_KEY, settings.pipOnExit)
        settingsStore.writeBoolean(PLAYER_PIP_REPLACE_WITH_PREVIOUS_KEY, settings.pipReplaceWithPrevious)
        settingsStore.writeBoolean(PLAYER_CAST_ENABLED_KEY, settings.castEnabled)
        settingsStore.writeBoolean(PLAYER_ALWAYS_EXTERNAL_KEY, settings.alwaysUseExternalPlayer)
        settingsStore.writeString(PLAYER_EXTERNAL_PACKAGE_KEY, settings.externalPlayerPackage)
        writeDouble(PLAYER_SPEED_KEY, settings.playerSpeed)
        writeStringList(PLAYER_SPEED_PRESETS_KEY, settings.speedPresets)
        settingsStore.writeBoolean(PLAYER_INVERT_DURATION_KEY, settings.invertDuration)
        settingsStore.writeString(PLAYER_ASPECT_KEY, settings.aspect.name)
        settingsStore.writeBoolean(PLAYER_AUTOPLAY_KEY, settings.autoplayEnabled)
        savePlayerGestureSettings(settings.gestures)
        savePlayerDecoderSettings(settings.decoder)
        savePlayerSubtitleSettings(settings.subtitles)
        savePlayerAudioSettings(settings.audio)
        savePlayerSelectionSettings(settings.selections)
        savePlayerAdvancedSettings(settings.advanced)
        return settings
    }

    private suspend fun loadPlayerGestureSettings(): ChimahonPlayerGestureSettings {
        return ChimahonPlayerGestureSettings(
            volumeBrightnessGestures = settingsStore.readBoolean(
                PLAYER_GESTURE_VOLUME_BRIGHTNESS_KEY,
                defaultValue = true,
            ),
            swapVolumeAndBrightness = settingsStore.readBoolean(PLAYER_GESTURE_SWAP_SLIDERS_KEY),
            horizontalSeekGesture = settingsStore.readBoolean(
                PLAYER_GESTURE_HORIZONTAL_SEEK_KEY,
                defaultValue = true,
            ),
            showSeekBar = settingsStore.readBoolean(PLAYER_GESTURE_SHOW_SEEKBAR_KEY),
            defaultIntroLengthSeconds = settingsStore.readInt(
                PLAYER_GESTURE_DEFAULT_INTRO_LENGTH_KEY,
                defaultValue = 85,
            ),
            skipLengthSeconds = settingsStore.readInt(
                PLAYER_GESTURE_SKIP_LENGTH_KEY,
                defaultValue = 10,
            ),
            smoothSeek = settingsStore.readBoolean(PLAYER_GESTURE_SMOOTH_SEEK_KEY),
            leftDoubleTap = readEnum(
                PLAYER_GESTURE_LEFT_DOUBLE_TAP_KEY,
                ChimahonPlayerGestureAction.Seek,
            ),
            centerDoubleTap = readEnum(
                PLAYER_GESTURE_CENTER_DOUBLE_TAP_KEY,
                ChimahonPlayerGestureAction.PlayPause,
            ),
            rightDoubleTap = readEnum(
                PLAYER_GESTURE_RIGHT_DOUBLE_TAP_KEY,
                ChimahonPlayerGestureAction.Seek,
            ),
            mediaPrevious = readEnum(
                PLAYER_GESTURE_MEDIA_PREVIOUS_KEY,
                ChimahonPlayerGestureAction.Switch,
            ),
            mediaPlayPause = readEnum(
                PLAYER_GESTURE_MEDIA_PLAY_PAUSE_KEY,
                ChimahonPlayerGestureAction.PlayPause,
            ),
            mediaNext = readEnum(PLAYER_GESTURE_MEDIA_NEXT_KEY, ChimahonPlayerGestureAction.Switch),
        )
    }

    private suspend fun savePlayerGestureSettings(settings: ChimahonPlayerGestureSettings) {
        settingsStore.writeBoolean(PLAYER_GESTURE_VOLUME_BRIGHTNESS_KEY, settings.volumeBrightnessGestures)
        settingsStore.writeBoolean(PLAYER_GESTURE_SWAP_SLIDERS_KEY, settings.swapVolumeAndBrightness)
        settingsStore.writeBoolean(PLAYER_GESTURE_HORIZONTAL_SEEK_KEY, settings.horizontalSeekGesture)
        settingsStore.writeBoolean(PLAYER_GESTURE_SHOW_SEEKBAR_KEY, settings.showSeekBar)
        settingsStore.writeInt(PLAYER_GESTURE_DEFAULT_INTRO_LENGTH_KEY, settings.defaultIntroLengthSeconds)
        settingsStore.writeInt(PLAYER_GESTURE_SKIP_LENGTH_KEY, settings.skipLengthSeconds)
        settingsStore.writeBoolean(PLAYER_GESTURE_SMOOTH_SEEK_KEY, settings.smoothSeek)
        settingsStore.writeString(PLAYER_GESTURE_LEFT_DOUBLE_TAP_KEY, settings.leftDoubleTap.name)
        settingsStore.writeString(PLAYER_GESTURE_CENTER_DOUBLE_TAP_KEY, settings.centerDoubleTap.name)
        settingsStore.writeString(PLAYER_GESTURE_RIGHT_DOUBLE_TAP_KEY, settings.rightDoubleTap.name)
        settingsStore.writeString(PLAYER_GESTURE_MEDIA_PREVIOUS_KEY, settings.mediaPrevious.name)
        settingsStore.writeString(PLAYER_GESTURE_MEDIA_PLAY_PAUSE_KEY, settings.mediaPlayPause.name)
        settingsStore.writeString(PLAYER_GESTURE_MEDIA_NEXT_KEY, settings.mediaNext.name)
    }

    private suspend fun loadPlayerDecoderSettings(): ChimahonPlayerDecoderSettings {
        return ChimahonPlayerDecoderSettings(
            tryHardwareDecoding = settingsStore.readBoolean(
                PLAYER_DECODER_TRY_HW_KEY,
                defaultValue = true,
            ),
            gpuNext = settingsStore.readBoolean(PLAYER_DECODER_GPU_NEXT_KEY),
            debanding = readEnum(PLAYER_DECODER_DEBANDING_KEY, ChimahonPlayerDebanding.None),
            useYuv420p = settingsStore.readBoolean(PLAYER_DECODER_USE_YUV420P_KEY, defaultValue = true),
            brightnessFilter = settingsStore.readInt(PLAYER_DECODER_BRIGHTNESS_FILTER_KEY),
            saturationFilter = settingsStore.readInt(PLAYER_DECODER_SATURATION_FILTER_KEY),
            contrastFilter = settingsStore.readInt(PLAYER_DECODER_CONTRAST_FILTER_KEY),
            gammaFilter = settingsStore.readInt(PLAYER_DECODER_GAMMA_FILTER_KEY),
            hueFilter = settingsStore.readInt(PLAYER_DECODER_HUE_FILTER_KEY),
        )
    }

    private suspend fun savePlayerDecoderSettings(settings: ChimahonPlayerDecoderSettings) {
        settingsStore.writeBoolean(PLAYER_DECODER_TRY_HW_KEY, settings.tryHardwareDecoding)
        settingsStore.writeBoolean(PLAYER_DECODER_GPU_NEXT_KEY, settings.gpuNext)
        settingsStore.writeString(PLAYER_DECODER_DEBANDING_KEY, settings.debanding.name)
        settingsStore.writeBoolean(PLAYER_DECODER_USE_YUV420P_KEY, settings.useYuv420p)
        settingsStore.writeInt(PLAYER_DECODER_BRIGHTNESS_FILTER_KEY, settings.brightnessFilter)
        settingsStore.writeInt(PLAYER_DECODER_SATURATION_FILTER_KEY, settings.saturationFilter)
        settingsStore.writeInt(PLAYER_DECODER_CONTRAST_FILTER_KEY, settings.contrastFilter)
        settingsStore.writeInt(PLAYER_DECODER_GAMMA_FILTER_KEY, settings.gammaFilter)
        settingsStore.writeInt(PLAYER_DECODER_HUE_FILTER_KEY, settings.hueFilter)
    }

    private suspend fun loadPlayerSubtitleSettings(): ChimahonPlayerSubtitleSettings {
        return ChimahonPlayerSubtitleSettings(
            preferredLanguages = settingsStore.readString(PLAYER_SUBTITLE_LANGUAGES_KEY) ?: "",
            whitelist = settingsStore.readString(PLAYER_SUBTITLE_WHITELIST_KEY) ?: "",
            blacklist = settingsStore.readString(PLAYER_SUBTITLE_BLACKLIST_KEY) ?: "",
            jimakuApiKey = settingsStore.readString(PLAYER_SUBTITLE_JIMAKU_API_KEY) ?: "",
            jimakuTitle = settingsStore.readString(PLAYER_SUBTITLE_JIMAKU_TITLE_KEY) ?: "",
            regexRemoveSpeakerNames = settingsStore.readBoolean(PLAYER_SUBTITLE_REGEX_REMOVE_SPEAKERS_KEY),
            regexMergeMultiline = settingsStore.readBoolean(PLAYER_SUBTITLE_REGEX_MERGE_MULTILINE_KEY),
            regexRemoveBracketedText = settingsStore.readBoolean(PLAYER_SUBTITLE_REGEX_REMOVE_BRACKETED_KEY),
            regexRemoveUppercaseLines = settingsStore.readBoolean(PLAYER_SUBTITLE_REGEX_REMOVE_UPPERCASE_KEY),
            regexRemoveMusicSymbols = settingsStore.readBoolean(PLAYER_SUBTITLE_REGEX_REMOVE_MUSIC_KEY),
            regexRemoveCurlyBracedText = settingsStore.readBoolean(PLAYER_SUBTITLE_REGEX_REMOVE_CURLY_KEY),
            regexCustomEnabled = settingsStore.readBoolean(PLAYER_SUBTITLE_REGEX_CUSTOM_ENABLED_KEY),
            regexCustomPattern = settingsStore.readString(PLAYER_SUBTITLE_REGEX_CUSTOM_PATTERN_KEY) ?: "",
            screenshotSubtitles = settingsStore.readBoolean(PLAYER_SUBTITLE_SCREENSHOT_KEY),
            listMode = readEnum(PLAYER_SUBTITLE_LIST_MODE_KEY, ChimahonSubtitleListMode.SideList),
            font = settingsStore.readString(PLAYER_SUBTITLE_FONT_KEY) ?: "Sans Serif",
            fontSize = settingsStore.readInt(PLAYER_SUBTITLE_FONT_SIZE_KEY, defaultValue = 55),
            fontScale = readDouble(PLAYER_SUBTITLE_FONT_SCALE_KEY, defaultValue = 1.0),
            borderSize = settingsStore.readInt(PLAYER_SUBTITLE_BORDER_SIZE_KEY, defaultValue = 3),
            bold = settingsStore.readBoolean(PLAYER_SUBTITLE_BOLD_KEY),
            italic = settingsStore.readBoolean(PLAYER_SUBTITLE_ITALIC_KEY),
            textColorArgb = settingsStore.readInt(PLAYER_SUBTITLE_TEXT_COLOR_KEY, defaultValue = -1),
            borderColorArgb = settingsStore.readInt(
                PLAYER_SUBTITLE_BORDER_COLOR_KEY,
                defaultValue = -16777216,
            ),
            borderStyle = readEnum(
                PLAYER_SUBTITLE_BORDER_STYLE_KEY,
                ChimahonSubtitleBorderStyle.OutlineAndShadow,
            ),
            shadowOffset = settingsStore.readInt(PLAYER_SUBTITLE_SHADOW_OFFSET_KEY),
            backgroundColorArgb = settingsStore.readInt(PLAYER_SUBTITLE_BACKGROUND_COLOR_KEY),
            justification = readEnum(
                PLAYER_SUBTITLE_JUSTIFICATION_KEY,
                ChimahonSubtitleJustification.Auto,
            ),
            positionPercent = settingsStore.readInt(PLAYER_SUBTITLE_POSITION_KEY, defaultValue = 100),
            overrideAss = settingsStore.readBoolean(PLAYER_SUBTITLE_OVERRIDE_ASS_KEY),
            delayMillis = settingsStore.readInt(PLAYER_SUBTITLE_DELAY_KEY),
            speed = readDouble(PLAYER_SUBTITLE_SPEED_KEY, defaultValue = 1.0),
            secondaryDelayMillis = settingsStore.readInt(PLAYER_SUBTITLE_SECONDARY_DELAY_KEY),
        )
    }

    private suspend fun savePlayerSubtitleSettings(settings: ChimahonPlayerSubtitleSettings) {
        settingsStore.writeString(PLAYER_SUBTITLE_LANGUAGES_KEY, settings.preferredLanguages)
        settingsStore.writeString(PLAYER_SUBTITLE_WHITELIST_KEY, settings.whitelist)
        settingsStore.writeString(PLAYER_SUBTITLE_BLACKLIST_KEY, settings.blacklist)
        settingsStore.writeString(PLAYER_SUBTITLE_JIMAKU_API_KEY, settings.jimakuApiKey)
        settingsStore.writeString(PLAYER_SUBTITLE_JIMAKU_TITLE_KEY, settings.jimakuTitle)
        settingsStore.writeBoolean(PLAYER_SUBTITLE_REGEX_REMOVE_SPEAKERS_KEY, settings.regexRemoveSpeakerNames)
        settingsStore.writeBoolean(PLAYER_SUBTITLE_REGEX_MERGE_MULTILINE_KEY, settings.regexMergeMultiline)
        settingsStore.writeBoolean(PLAYER_SUBTITLE_REGEX_REMOVE_BRACKETED_KEY, settings.regexRemoveBracketedText)
        settingsStore.writeBoolean(PLAYER_SUBTITLE_REGEX_REMOVE_UPPERCASE_KEY, settings.regexRemoveUppercaseLines)
        settingsStore.writeBoolean(PLAYER_SUBTITLE_REGEX_REMOVE_MUSIC_KEY, settings.regexRemoveMusicSymbols)
        settingsStore.writeBoolean(PLAYER_SUBTITLE_REGEX_REMOVE_CURLY_KEY, settings.regexRemoveCurlyBracedText)
        settingsStore.writeBoolean(PLAYER_SUBTITLE_REGEX_CUSTOM_ENABLED_KEY, settings.regexCustomEnabled)
        settingsStore.writeString(PLAYER_SUBTITLE_REGEX_CUSTOM_PATTERN_KEY, settings.regexCustomPattern)
        settingsStore.writeBoolean(PLAYER_SUBTITLE_SCREENSHOT_KEY, settings.screenshotSubtitles)
        settingsStore.writeString(PLAYER_SUBTITLE_LIST_MODE_KEY, settings.listMode.name)
        settingsStore.writeString(PLAYER_SUBTITLE_FONT_KEY, settings.font)
        settingsStore.writeInt(PLAYER_SUBTITLE_FONT_SIZE_KEY, settings.fontSize)
        writeDouble(PLAYER_SUBTITLE_FONT_SCALE_KEY, settings.fontScale)
        settingsStore.writeInt(PLAYER_SUBTITLE_BORDER_SIZE_KEY, settings.borderSize)
        settingsStore.writeBoolean(PLAYER_SUBTITLE_BOLD_KEY, settings.bold)
        settingsStore.writeBoolean(PLAYER_SUBTITLE_ITALIC_KEY, settings.italic)
        settingsStore.writeInt(PLAYER_SUBTITLE_TEXT_COLOR_KEY, settings.textColorArgb)
        settingsStore.writeInt(PLAYER_SUBTITLE_BORDER_COLOR_KEY, settings.borderColorArgb)
        settingsStore.writeString(PLAYER_SUBTITLE_BORDER_STYLE_KEY, settings.borderStyle.name)
        settingsStore.writeInt(PLAYER_SUBTITLE_SHADOW_OFFSET_KEY, settings.shadowOffset)
        settingsStore.writeInt(PLAYER_SUBTITLE_BACKGROUND_COLOR_KEY, settings.backgroundColorArgb)
        settingsStore.writeString(PLAYER_SUBTITLE_JUSTIFICATION_KEY, settings.justification.name)
        settingsStore.writeInt(PLAYER_SUBTITLE_POSITION_KEY, settings.positionPercent)
        settingsStore.writeBoolean(PLAYER_SUBTITLE_OVERRIDE_ASS_KEY, settings.overrideAss)
        settingsStore.writeInt(PLAYER_SUBTITLE_DELAY_KEY, settings.delayMillis)
        writeDouble(PLAYER_SUBTITLE_SPEED_KEY, settings.speed)
        settingsStore.writeInt(PLAYER_SUBTITLE_SECONDARY_DELAY_KEY, settings.secondaryDelayMillis)
    }

    private suspend fun loadPlayerAudioSettings(): ChimahonPlayerAudioSettings {
        return ChimahonPlayerAudioSettings(
            preferredLanguages = settingsStore.readString(PLAYER_AUDIO_LANGUAGES_KEY) ?: "",
            pitchCorrection = settingsStore.readBoolean(
                PLAYER_AUDIO_PITCH_CORRECTION_KEY,
                defaultValue = true,
            ),
            channels = readEnum(PLAYER_AUDIO_CHANNELS_KEY, ChimahonAudioChannels.AutoSafe),
            volumeBoostCap = settingsStore.readInt(PLAYER_AUDIO_VOLUME_BOOST_CAP_KEY, defaultValue = 30),
            delayMillis = settingsStore.readInt(PLAYER_AUDIO_DELAY_KEY),
        )
    }

    private suspend fun savePlayerAudioSettings(settings: ChimahonPlayerAudioSettings) {
        settingsStore.writeString(PLAYER_AUDIO_LANGUAGES_KEY, settings.preferredLanguages)
        settingsStore.writeBoolean(PLAYER_AUDIO_PITCH_CORRECTION_KEY, settings.pitchCorrection)
        settingsStore.writeString(PLAYER_AUDIO_CHANNELS_KEY, settings.channels.name)
        settingsStore.writeInt(PLAYER_AUDIO_VOLUME_BOOST_CAP_KEY, settings.volumeBoostCap)
        settingsStore.writeInt(PLAYER_AUDIO_DELAY_KEY, settings.delayMillis)
    }

    private suspend fun loadPlayerSelectionSettings(): ChimahonPlayerSelectionSettings {
        return ChimahonPlayerSelectionSettings(
            rememberQuality = settingsStore.readBoolean(
                PLAYER_SELECTION_REMEMBER_QUALITY_KEY,
                defaultValue = true,
            ),
            rememberSubtitleTracks = settingsStore.readBoolean(
                PLAYER_SELECTION_REMEMBER_SUBTITLES_KEY,
                defaultValue = true,
            ),
            restoreAddedSubtitleTracks = settingsStore.readBoolean(
                PLAYER_SELECTION_RESTORE_ADDED_SUBTITLES_KEY,
                defaultValue = true,
            ),
            preferredHosterKey = settingsStore.readString(PLAYER_SELECTION_HOSTER_KEY) ?: "",
            preferredVideoKey = settingsStore.readString(PLAYER_SELECTION_VIDEO_KEY) ?: "",
            primarySubtitleKey = settingsStore.readString(PLAYER_SELECTION_PRIMARY_SUBTITLE_KEY) ?: "",
            secondarySubtitleKey = settingsStore.readString(PLAYER_SELECTION_SECONDARY_SUBTITLE_KEY) ?: "",
            addedSubtitleKeys = readStringList(PLAYER_SELECTION_ADDED_SUBTITLE_KEYS),
        )
    }

    private suspend fun savePlayerSelectionSettings(settings: ChimahonPlayerSelectionSettings) {
        settingsStore.writeBoolean(PLAYER_SELECTION_REMEMBER_QUALITY_KEY, settings.rememberQuality)
        settingsStore.writeBoolean(PLAYER_SELECTION_REMEMBER_SUBTITLES_KEY, settings.rememberSubtitleTracks)
        settingsStore.writeBoolean(
            PLAYER_SELECTION_RESTORE_ADDED_SUBTITLES_KEY,
            settings.restoreAddedSubtitleTracks,
        )
        settingsStore.writeString(PLAYER_SELECTION_HOSTER_KEY, settings.preferredHosterKey)
        settingsStore.writeString(PLAYER_SELECTION_VIDEO_KEY, settings.preferredVideoKey)
        settingsStore.writeString(PLAYER_SELECTION_PRIMARY_SUBTITLE_KEY, settings.primarySubtitleKey)
        settingsStore.writeString(PLAYER_SELECTION_SECONDARY_SUBTITLE_KEY, settings.secondarySubtitleKey)
        writeStringList(PLAYER_SELECTION_ADDED_SUBTITLE_KEYS, settings.addedSubtitleKeys)
    }

    private suspend fun loadPlayerAdvancedSettings(): ChimahonPlayerAdvancedSettings {
        return ChimahonPlayerAdvancedSettings(
            mpvScriptsEnabled = settingsStore.readBoolean(PLAYER_ADVANCED_MPV_SCRIPTS_KEY),
            mpvConfig = settingsStore.readString(PLAYER_ADVANCED_MPV_CONFIG_KEY) ?: "",
            mpvInput = settingsStore.readString(PLAYER_ADVANCED_MPV_INPUT_KEY) ?: "",
            statisticsPage = settingsStore.readInt(PLAYER_ADVANCED_STATISTICS_PAGE_KEY),
        )
    }

    private suspend fun savePlayerAdvancedSettings(settings: ChimahonPlayerAdvancedSettings) {
        settingsStore.writeBoolean(PLAYER_ADVANCED_MPV_SCRIPTS_KEY, settings.mpvScriptsEnabled)
        settingsStore.writeString(PLAYER_ADVANCED_MPV_CONFIG_KEY, settings.mpvConfig)
        settingsStore.writeString(PLAYER_ADVANCED_MPV_INPUT_KEY, settings.mpvInput)
        settingsStore.writeInt(PLAYER_ADVANCED_STATISTICS_PAGE_KEY, settings.statisticsPage)
    }

    suspend fun loadTrackingSettings(): ChimahonTrackingSettings {
        return ChimahonTrackingSettings(
            trackOnAddToLibrary = settingsStore.readBoolean(TRACKING_TRACK_ON_ADD_TO_LIBRARY_KEY),
            autoSyncEnabled = settingsStore.readBoolean(TRACKING_AUTO_SYNC_ENABLED_KEY),
            updateIntervalHours = settingsStore.readInt(
                TRACKING_UPDATE_INTERVAL_HOURS_KEY,
                defaultValue = 24,
            ),
            syncRestrictions = readStringList(
                TRACKING_SYNC_RESTRICTIONS_KEY,
                defaultValue = listOf("Wi-Fi only"),
            ),
            syncIncludedCategories = readStringList(TRACKING_SYNC_INCLUDED_CATEGORIES_KEY),
            syncExcludedCategories = readStringList(TRACKING_SYNC_EXCLUDED_CATEGORIES_KEY),
            syncLibraryEntriesOnly = settingsStore.readBoolean(
                TRACKING_SYNC_LIBRARY_ENTRIES_ONLY_KEY,
                defaultValue = true,
            ),
            autoUpdateOnMarkRead = readEnum(
                TRACKING_AUTO_UPDATE_ON_MARK_READ_KEY,
                ChimahonAutoTrackOnMarkRead.Always,
            ),
            autoSyncProgressFromTrackers = settingsStore.readBoolean(
                TRACKING_AUTO_SYNC_PROGRESS_FROM_TRACKERS_KEY,
            ),
            resolveUsingSourceMetadata = settingsStore.readBoolean(
                TRACKING_RESOLVE_USING_SOURCE_METADATA_KEY,
            ),
        )
    }

    suspend fun saveTrackingSettings(settings: ChimahonTrackingSettings): ChimahonTrackingSettings {
        settingsStore.writeBoolean(TRACKING_TRACK_ON_ADD_TO_LIBRARY_KEY, settings.trackOnAddToLibrary)
        settingsStore.writeBoolean(TRACKING_AUTO_SYNC_ENABLED_KEY, settings.autoSyncEnabled)
        settingsStore.writeInt(TRACKING_UPDATE_INTERVAL_HOURS_KEY, settings.updateIntervalHours)
        writeStringList(TRACKING_SYNC_RESTRICTIONS_KEY, settings.syncRestrictions)
        writeStringList(TRACKING_SYNC_INCLUDED_CATEGORIES_KEY, settings.syncIncludedCategories)
        writeStringList(TRACKING_SYNC_EXCLUDED_CATEGORIES_KEY, settings.syncExcludedCategories)
        settingsStore.writeBoolean(
            TRACKING_SYNC_LIBRARY_ENTRIES_ONLY_KEY,
            settings.syncLibraryEntriesOnly,
        )
        settingsStore.writeString(TRACKING_AUTO_UPDATE_ON_MARK_READ_KEY, settings.autoUpdateOnMarkRead.name)
        settingsStore.writeBoolean(
            TRACKING_AUTO_SYNC_PROGRESS_FROM_TRACKERS_KEY,
            settings.autoSyncProgressFromTrackers,
        )
        settingsStore.writeBoolean(
            TRACKING_RESOLVE_USING_SOURCE_METADATA_KEY,
            settings.resolveUsingSourceMetadata,
        )
        return settings
    }

    suspend fun loadConnectionSettings(): ChimahonConnectionSettings {
        return ChimahonConnectionSettings(
            openingPreference = readEnum(
                CONNECTIONS_OPENING_PREFERENCE_KEY,
                ChimahonConnectionOpeningPreference.InApp,
            ),
            discordRpcEnabled = settingsStore.readBoolean(CONNECTIONS_DISCORD_RPC_ENABLED_KEY),
            discordStatus = readEnum(CONNECTIONS_DISCORD_STATUS_KEY, ChimahonDiscordStatus.Online),
            discordRpcIncognito = settingsStore.readBoolean(CONNECTIONS_DISCORD_RPC_INCOGNITO_KEY),
            discordRpcIncognitoCategories = readStringList(
                CONNECTIONS_DISCORD_RPC_INCOGNITO_CATEGORIES_KEY,
            ),
            discordShowMangaTitle = settingsStore.readBoolean(
                CONNECTIONS_DISCORD_SHOW_MANGA_TITLE_KEY,
                defaultValue = true,
            ),
            discordShowCoverArt = settingsStore.readBoolean(
                CONNECTIONS_DISCORD_SHOW_COVER_ART_KEY,
                defaultValue = true,
            ),
            discordShowSourceName = settingsStore.readBoolean(
                CONNECTIONS_DISCORD_SHOW_SOURCE_NAME_KEY,
            ),
            discordUseChapterTitles = settingsStore.readBoolean(
                CONNECTIONS_DISCORD_USE_CHAPTER_TITLES_KEY,
            ),
            discordShowProgress = settingsStore.readBoolean(
                CONNECTIONS_DISCORD_SHOW_PROGRESS_KEY,
                defaultValue = true,
            ),
            discordShowTimestamp = settingsStore.readBoolean(
                CONNECTIONS_DISCORD_SHOW_TIMESTAMP_KEY,
                defaultValue = true,
            ),
            discordShowButtons = settingsStore.readBoolean(
                CONNECTIONS_DISCORD_SHOW_BUTTONS_KEY,
                defaultValue = true,
            ),
            discordShowDownloadButton = settingsStore.readBoolean(
                CONNECTIONS_DISCORD_SHOW_DOWNLOAD_BUTTON_KEY,
                defaultValue = true,
            ),
            discordShowDiscordButton = settingsStore.readBoolean(
                CONNECTIONS_DISCORD_SHOW_DISCORD_BUTTON_KEY,
                defaultValue = true,
            ),
        )
    }

    suspend fun saveConnectionSettings(settings: ChimahonConnectionSettings): ChimahonConnectionSettings {
        settingsStore.writeString(CONNECTIONS_OPENING_PREFERENCE_KEY, settings.openingPreference.name)
        settingsStore.writeBoolean(CONNECTIONS_DISCORD_RPC_ENABLED_KEY, settings.discordRpcEnabled)
        settingsStore.writeString(CONNECTIONS_DISCORD_STATUS_KEY, settings.discordStatus.name)
        settingsStore.writeBoolean(CONNECTIONS_DISCORD_RPC_INCOGNITO_KEY, settings.discordRpcIncognito)
        writeStringList(
            CONNECTIONS_DISCORD_RPC_INCOGNITO_CATEGORIES_KEY,
            settings.discordRpcIncognitoCategories,
        )
        settingsStore.writeBoolean(
            CONNECTIONS_DISCORD_SHOW_MANGA_TITLE_KEY,
            settings.discordShowMangaTitle,
        )
        settingsStore.writeBoolean(
            CONNECTIONS_DISCORD_SHOW_COVER_ART_KEY,
            settings.discordShowCoverArt,
        )
        settingsStore.writeBoolean(
            CONNECTIONS_DISCORD_SHOW_SOURCE_NAME_KEY,
            settings.discordShowSourceName,
        )
        settingsStore.writeBoolean(
            CONNECTIONS_DISCORD_USE_CHAPTER_TITLES_KEY,
            settings.discordUseChapterTitles,
        )
        settingsStore.writeBoolean(CONNECTIONS_DISCORD_SHOW_PROGRESS_KEY, settings.discordShowProgress)
        settingsStore.writeBoolean(CONNECTIONS_DISCORD_SHOW_TIMESTAMP_KEY, settings.discordShowTimestamp)
        settingsStore.writeBoolean(CONNECTIONS_DISCORD_SHOW_BUTTONS_KEY, settings.discordShowButtons)
        settingsStore.writeBoolean(
            CONNECTIONS_DISCORD_SHOW_DOWNLOAD_BUTTON_KEY,
            settings.discordShowDownloadButton,
        )
        settingsStore.writeBoolean(
            CONNECTIONS_DISCORD_SHOW_DISCORD_BUTTON_KEY,
            settings.discordShowDiscordButton,
        )
        return settings
    }

    suspend fun loadDictionarySettings(): ChimahonDictionarySettings {
        return ChimahonDictionarySettings(
            enabled = settingsStore.readBoolean(DICTIONARY_ENABLED_KEY),
            enabledLanguages = settingsStore.readString(DICTIONARY_ENABLED_LANGUAGES_KEY)
                ?.split(',')
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                .orEmpty(),
            ocrEnabled = settingsStore.readBoolean(DICTIONARY_OCR_ENABLED_KEY),
            popupWidth = settingsStore.readInt(DICTIONARY_POPUP_WIDTH_KEY, defaultValue = 300),
            popupHeight = settingsStore.readInt(DICTIONARY_POPUP_HEIGHT_KEY, defaultValue = 360),
            fontSize = settingsStore.readInt(DICTIONARY_FONT_SIZE_KEY, defaultValue = 16),
            ocrBoxScaleXPercent = settingsStore.readInt(
                DICTIONARY_OCR_BOX_SCALE_X_PERCENT_KEY,
                defaultValue = 100,
            ),
            ocrBoxScaleYPercent = settingsStore.readInt(
                DICTIONARY_OCR_BOX_SCALE_Y_PERCENT_KEY,
                defaultValue = 100,
            ),
            ocrBoxOpacityPercent = settingsStore.readInt(DICTIONARY_OCR_BOX_OPACITY_PERCENT_KEY),
            ocrEngine = readEnum(DICTIONARY_OCR_ENGINE_KEY, ChimahonDictionaryOcrEngine.Cloud),
            themeMode = readEnum(DICTIONARY_THEME_MODE_KEY, ChimahonDictionaryThemeMode.System),
            eInkMode = settingsStore.readBoolean(DICTIONARY_E_INK_MODE_KEY),
            paginatedScrolling = settingsStore.readBoolean(DICTIONARY_PAGINATED_SCROLLING_KEY),
            showFrequencyHarmonic = settingsStore.readBoolean(DICTIONARY_SHOW_FREQUENCY_HARMONIC_KEY),
            showFrequencyAverage = settingsStore.readBoolean(DICTIONARY_SHOW_FREQUENCY_AVERAGE_KEY),
            groupPitches = settingsStore.readBoolean(DICTIONARY_GROUP_PITCHES_KEY),
            groupTerms = settingsStore.readBoolean(DICTIONARY_GROUP_TERMS_KEY, defaultValue = true),
            showNavigationButtons = settingsStore.readBoolean(
                DICTIONARY_SHOW_NAVIGATION_BUTTONS_KEY,
                defaultValue = true,
            ),
            popupMode = readEnum(DICTIONARY_POPUP_MODE_KEY, ChimahonDictionaryPopupMode.Floating),
            recursiveLookupMode = readEnum(
                DICTIONARY_RECURSIVE_LOOKUP_MODE_KEY,
                ChimahonDictionaryRecursiveLookupMode.Tabs,
            ),
            showPitchDiagram = settingsStore.readBoolean(
                DICTIONARY_SHOW_PITCH_DIAGRAM_KEY,
                defaultValue = true,
            ),
            showPitchNumber = settingsStore.readBoolean(
                DICTIONARY_SHOW_PITCH_NUMBER_KEY,
                defaultValue = true,
            ),
            showPitchText = settingsStore.readBoolean(
                DICTIONARY_SHOW_PITCH_TEXT_KEY,
                defaultValue = true,
            ),
            autoKanaConversion = settingsStore.readBoolean(
                DICTIONARY_AUTO_KANA_CONVERSION_KEY,
                defaultValue = true,
            ),
            wordAudioEnabled = settingsStore.readBoolean(
                DICTIONARY_WORD_AUDIO_ENABLED_KEY,
                defaultValue = true,
            ),
            wordAudioAutoplay = settingsStore.readBoolean(DICTIONARY_WORD_AUDIO_AUTOPLAY_KEY),
            wordAudioLocalEnabled = settingsStore.readBoolean(DICTIONARY_WORD_AUDIO_LOCAL_ENABLED_KEY),
        )
    }

    suspend fun saveDictionarySettings(settings: ChimahonDictionarySettings): ChimahonDictionarySettings {
        settingsStore.writeBoolean(DICTIONARY_ENABLED_KEY, settings.enabled)
        settingsStore.writeString(
            DICTIONARY_ENABLED_LANGUAGES_KEY,
            settings.enabledLanguages.joinToString(","),
        )
        settingsStore.writeBoolean(DICTIONARY_OCR_ENABLED_KEY, settings.ocrEnabled)
        settingsStore.writeInt(DICTIONARY_POPUP_WIDTH_KEY, settings.popupWidth)
        settingsStore.writeInt(DICTIONARY_POPUP_HEIGHT_KEY, settings.popupHeight)
        settingsStore.writeInt(DICTIONARY_FONT_SIZE_KEY, settings.fontSize)
        settingsStore.writeInt(DICTIONARY_OCR_BOX_SCALE_X_PERCENT_KEY, settings.ocrBoxScaleXPercent)
        settingsStore.writeInt(DICTIONARY_OCR_BOX_SCALE_Y_PERCENT_KEY, settings.ocrBoxScaleYPercent)
        settingsStore.writeInt(DICTIONARY_OCR_BOX_OPACITY_PERCENT_KEY, settings.ocrBoxOpacityPercent)
        settingsStore.writeString(DICTIONARY_OCR_ENGINE_KEY, settings.ocrEngine.name)
        settingsStore.writeString(DICTIONARY_THEME_MODE_KEY, settings.themeMode.name)
        settingsStore.writeBoolean(DICTIONARY_E_INK_MODE_KEY, settings.eInkMode)
        settingsStore.writeBoolean(DICTIONARY_PAGINATED_SCROLLING_KEY, settings.paginatedScrolling)
        settingsStore.writeBoolean(DICTIONARY_SHOW_FREQUENCY_HARMONIC_KEY, settings.showFrequencyHarmonic)
        settingsStore.writeBoolean(DICTIONARY_SHOW_FREQUENCY_AVERAGE_KEY, settings.showFrequencyAverage)
        settingsStore.writeBoolean(DICTIONARY_GROUP_PITCHES_KEY, settings.groupPitches)
        settingsStore.writeBoolean(DICTIONARY_GROUP_TERMS_KEY, settings.groupTerms)
        settingsStore.writeBoolean(DICTIONARY_SHOW_NAVIGATION_BUTTONS_KEY, settings.showNavigationButtons)
        settingsStore.writeString(DICTIONARY_POPUP_MODE_KEY, settings.popupMode.name)
        settingsStore.writeString(DICTIONARY_RECURSIVE_LOOKUP_MODE_KEY, settings.recursiveLookupMode.name)
        settingsStore.writeBoolean(DICTIONARY_SHOW_PITCH_DIAGRAM_KEY, settings.showPitchDiagram)
        settingsStore.writeBoolean(DICTIONARY_SHOW_PITCH_NUMBER_KEY, settings.showPitchNumber)
        settingsStore.writeBoolean(DICTIONARY_SHOW_PITCH_TEXT_KEY, settings.showPitchText)
        settingsStore.writeBoolean(DICTIONARY_AUTO_KANA_CONVERSION_KEY, settings.autoKanaConversion)
        settingsStore.writeBoolean(DICTIONARY_WORD_AUDIO_ENABLED_KEY, settings.wordAudioEnabled)
        settingsStore.writeBoolean(DICTIONARY_WORD_AUDIO_AUTOPLAY_KEY, settings.wordAudioAutoplay)
        settingsStore.writeBoolean(DICTIONARY_WORD_AUDIO_LOCAL_ENABLED_KEY, settings.wordAudioLocalEnabled)
        return settings
    }

    suspend fun loadSecuritySettings(): ChimahonSecuritySettings {
        return ChimahonSecuritySettings(
            secureScreenMode = readEnum(SECURITY_SECURE_SCREEN_KEY, ChimahonSecureScreenMode.Incognito),
            hideNotificationContent = settingsStore.readBoolean(SECURITY_HIDE_NOTIFICATION_CONTENT_KEY),
            crashReportsEnabled = settingsStore.readBoolean(SECURITY_CRASH_REPORTS_ENABLED_KEY),
            requireAuthentication = settingsStore.readBoolean(SECURITY_REQUIRE_AUTHENTICATION_KEY),
            lockAfterMinutes = settingsStore.readInt(SECURITY_LOCK_AFTER_MINUTES_KEY),
            lockOnAppExit = settingsStore.readBoolean(SECURITY_LOCK_ON_APP_EXIT_KEY),
            incognitoModeByDefault = settingsStore.readBoolean(
                SECURITY_INCOGNITO_BY_DEFAULT_KEY,
            ),
            protectDownloads = settingsStore.readBoolean(SECURITY_PROTECT_DOWNLOADS_KEY),
            downloadEncryption = readEnum(
                SECURITY_DOWNLOAD_ENCRYPTION_KEY,
                ChimahonDownloadEncryption.Aes256,
            ),
            biometricLockDays = readStringList(
                SECURITY_BIOMETRIC_LOCK_DAYS_KEY,
                defaultValue = listOf(
                    "Sunday",
                    "Monday",
                    "Tuesday",
                    "Wednesday",
                    "Thursday",
                    "Friday",
                    "Saturday",
                ),
            ),
            encryptDatabase = settingsStore.readBoolean(SECURITY_ENCRYPT_DATABASE_KEY),
        )
    }

    suspend fun saveSecuritySettings(settings: ChimahonSecuritySettings): ChimahonSecuritySettings {
        settingsStore.writeString(SECURITY_SECURE_SCREEN_KEY, settings.secureScreenMode.name)
        settingsStore.writeBoolean(
            SECURITY_HIDE_NOTIFICATION_CONTENT_KEY,
            settings.hideNotificationContent,
        )
        settingsStore.writeBoolean(SECURITY_CRASH_REPORTS_ENABLED_KEY, settings.crashReportsEnabled)
        settingsStore.writeBoolean(SECURITY_REQUIRE_AUTHENTICATION_KEY, settings.requireAuthentication)
        settingsStore.writeInt(SECURITY_LOCK_AFTER_MINUTES_KEY, settings.lockAfterMinutes)
        settingsStore.writeBoolean(SECURITY_LOCK_ON_APP_EXIT_KEY, settings.lockOnAppExit)
        settingsStore.writeBoolean(
            SECURITY_INCOGNITO_BY_DEFAULT_KEY,
            settings.incognitoModeByDefault,
        )
        settingsStore.writeBoolean(SECURITY_PROTECT_DOWNLOADS_KEY, settings.protectDownloads)
        settingsStore.writeString(SECURITY_DOWNLOAD_ENCRYPTION_KEY, settings.downloadEncryption.name)
        writeStringList(SECURITY_BIOMETRIC_LOCK_DAYS_KEY, settings.biometricLockDays)
        settingsStore.writeBoolean(SECURITY_ENCRYPT_DATABASE_KEY, settings.encryptDatabase)
        return settings
    }

    suspend fun loadAppModeSettings(): ChimahonAppModeSettings {
        return ChimahonAppModeSettings(
            downloadedOnly = settingsStore.readBoolean(DOWNLOADED_ONLY_KEY),
            incognitoMode = settingsStore.readBoolean(INCOGNITO_MODE_KEY),
        )
    }

    suspend fun setDownloadedOnly(enabled: Boolean): ChimahonAppModeSettings {
        settingsStore.writeBoolean(DOWNLOADED_ONLY_KEY, enabled)
        return loadAppModeSettings()
    }

    suspend fun setIncognitoMode(enabled: Boolean): ChimahonAppModeSettings {
        settingsStore.writeBoolean(INCOGNITO_MODE_KEY, enabled)
        return loadAppModeSettings()
    }

    suspend fun isIncognitoModeEnabled(): Boolean {
        return settingsStore.readBoolean(INCOGNITO_MODE_KEY)
    }

    private fun parseNavigationTabLayout(serialized: String?): List<ChimahonNavigationTabEntry> {
        if (serialized.isNullOrBlank()) return ChimahonNavigationTabDefaults

        val parsed = serialized
            .split(",")
            .mapNotNull { token ->
                val parts = token.trim().split(":", limit = 2)
                if (parts.size != 2) return@mapNotNull null

                val tab = ChimahonNavigationTab.entries.firstOrNull { it.key == parts[0] }
                    ?: return@mapNotNull null
                val section = ChimahonNavigationSection.entries.firstOrNull {
                    it.wireName == parts[1].lowercase()
                } ?: ChimahonNavigationSection.Navbar

                ChimahonNavigationTabEntry(tab, section)
            }
            .distinctBy { it.tab }

        if (parsed.isEmpty()) return ChimahonNavigationTabDefaults

        val missingDefaults = ChimahonNavigationTabDefaults.filter { defaultEntry ->
            parsed.none { it.tab == defaultEntry.tab }
        }
        return parsed + missingDefaults
    }

    private fun serializeNavigationTabLayout(
        entries: List<ChimahonNavigationTabEntry>,
    ): String {
        val normalized = entries.distinctBy { it.tab }
        val missingDefaults = ChimahonNavigationTabDefaults.filter { defaultEntry ->
            normalized.none { it.tab == defaultEntry.tab }
        }
        return (normalized + missingDefaults).joinToString(",") { entry ->
            "${entry.tab.key}:${entry.section.wireName}"
        }
    }

    private suspend fun readStringList(
        key: String,
        defaultValue: List<String> = emptyList(),
    ): List<String> {
        return settingsStore.readString(key)
            ?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: defaultValue
    }

    private suspend fun writeStringList(
        key: String,
        values: List<String>,
    ) {
        settingsStore.writeString(key, values.distinct().joinToString(","))
    }

    private suspend inline fun <reified T : Enum<T>> readEnum(
        key: String,
        defaultValue: T,
    ): T {
        val stored = settingsStore.readString(key) ?: return defaultValue
        return enumValues<T>().firstOrNull { it.name == stored } ?: defaultValue
    }

    private suspend fun readDouble(
        key: String,
        defaultValue: Double,
    ): Double {
        return settingsStore.readString(key)?.toDoubleOrNull() ?: defaultValue
    }

    private suspend fun writeDouble(
        key: String,
        value: Double,
    ) {
        settingsStore.writeString(key, value.toString())
    }

    private companion object {
        const val APPEARANCE_THEME_MODE_KEY = "__APP_STATE_chimahon_theme_mode"
        const val APPEARANCE_APP_THEME_KEY = "__APP_STATE_chimahon_app_theme"
        const val APPEARANCE_COLOR_THEME_KEY = "__APP_STATE_chimahon_color_theme"
        const val APPEARANCE_APP_ICON_KEY = "__APP_STATE_chimahon_app_icon"
        const val APPEARANCE_CUSTOM_THEME_STYLE_KEY =
            "__APP_STATE_chimahon_custom_theme_style"
        const val APPEARANCE_AMOLED_KEY = "__APP_STATE_chimahon_theme_amoled"
        const val APPEARANCE_FONT_SCALE_PERCENT_KEY = "__APP_STATE_chimahon_font_scale_percent"
        const val APPEARANCE_TABLET_UI_MODE_KEY = "__APP_STATE_chimahon_tablet_ui_mode"
        const val APPEARANCE_DATE_FORMAT_KEY = "__APP_STATE_chimahon_date_format"
        const val APPEARANCE_COMPACT_NAVIGATION_KEY = "__APP_STATE_chimahon_compact_navigation"
        const val APPEARANCE_RELATIVE_DATES_KEY = "__APP_STATE_chimahon_relative_dates"
        const val APPEARANCE_DESCRIPTION_IMAGES_KEY = "__APP_STATE_chimahon_description_images"
        const val APPEARANCE_MANGA_INFO_COVER_BASED_THEME_KEY =
            "__APP_STATE_chimahon_manga_info_cover_based_theme"
        const val APPEARANCE_MANGA_INFO_COVER_BASED_STYLE_KEY =
            "__APP_STATE_chimahon_manga_info_cover_based_style"
        const val APPEARANCE_MANGA_INFO_PANORAMA_COVER_KEY =
            "__APP_STATE_chimahon_manga_info_panorama_cover"
        const val APPEARANCE_MANGA_INFO_TOP_ALIGN_COVER_KEY =
            "__APP_STATE_chimahon_manga_info_top_align_cover"
        const val APPEARANCE_START_SCREEN_KEY = "__APP_STATE_chimahon_start_screen"
        const val APPEARANCE_BOTTOM_BAR_LABELS_KEY = "__APP_STATE_chimahon_bottom_bar_labels"
        const val APPEARANCE_NAVIGATION_BADGES_KEY =
            "__APP_STATE_chimahon_navigation_badges"
        const val APPEARANCE_TOP_BAR_SUBTITLE_KEY =
            "__APP_STATE_chimahon_top_bar_subtitle"
        const val APPEARANCE_PANORAMA_COVER_FLOW_KEY =
            "__APP_STATE_chimahon_panorama_cover_flow"
        const val APPEARANCE_EXPAND_SEARCH_FILTERS_KEY =
            "__APP_STATE_chimahon_expand_search_filters"
        const val APPEARANCE_RECOMMENDS_IN_OVERFLOW_KEY =
            "__APP_STATE_chimahon_recommends_in_overflow"
        const val APPEARANCE_MERGE_IN_OVERFLOW_KEY = "__APP_STATE_chimahon_merge_in_overflow"
        const val APPEARANCE_PREVIEWS_ROW_COUNT_KEY = "__APP_STATE_chimahon_previews_row_count"
        const val READER_MODE_KEY = "__APP_STATE_chimahon_reader_mode"
        const val READER_SCALE_KEY = "__APP_STATE_chimahon_reader_scale"
        const val READER_CANVAS_KEY = "__APP_STATE_chimahon_reader_canvas"
        const val READER_PURE_BLACK_BACKGROUND_KEY =
            "__APP_STATE_chimahon_reader_pure_black_background"
        const val READER_ORIENTATION_KEY = "__APP_STATE_chimahon_reader_orientation"
        const val READER_DUAL_PAGE_MODE_KEY = "__APP_STATE_chimahon_reader_dual_page_mode"
        const val READER_SPLIT_WIDE_PAGES_KEY = "__APP_STATE_chimahon_reader_split_wide_pages"
        const val READER_ROTATE_WIDE_PAGES_TO_FIT_KEY =
            "__APP_STATE_chimahon_reader_rotate_wide_pages_to_fit"
        const val READER_INVERT_WIDE_PAGE_ROTATION_KEY =
            "__APP_STATE_chimahon_reader_invert_wide_page_rotation"
        const val READER_CUSTOM_BRIGHTNESS_ENABLED_KEY =
            "__APP_STATE_chimahon_reader_custom_brightness_enabled"
        const val READER_CUSTOM_BRIGHTNESS_VALUE_KEY =
            "__APP_STATE_chimahon_reader_custom_brightness_value"
        const val READER_COLOR_FILTER_ENABLED_KEY =
            "__APP_STATE_chimahon_reader_color_filter_enabled"
        const val READER_COLOR_FILTER_VALUE_KEY =
            "__APP_STATE_chimahon_reader_color_filter_value"
        const val READER_COLOR_FILTER_MODE_KEY =
            "__APP_STATE_chimahon_reader_color_filter_mode"
        const val READER_GRAYSCALE_KEY = "__APP_STATE_chimahon_reader_grayscale"
        const val READER_INVERT_COLORS_KEY = "__APP_STATE_chimahon_reader_invert_colors"
        const val READER_BRIGHTNESS_KEY = "__APP_STATE_chimahon_reader_brightness"
        const val READER_DOUBLE_TAP_ANIMATION_SPEED_KEY =
            "__APP_STATE_chimahon_reader_double_tap_animation_speed"
        const val READER_NAVIGATION_MODE_KEY = "__APP_STATE_chimahon_reader_navigation_mode"
        const val READER_PAGE_STRIP_KEY = "__APP_STATE_chimahon_reader_page_strip"
        const val READER_FORCE_HORIZONTAL_SEEKBAR_KEY =
            "__APP_STATE_chimahon_reader_force_horizontal_seekbar"
        const val READER_LANDSCAPE_VERTICAL_SEEKBAR_KEY =
            "__APP_STATE_chimahon_reader_landscape_vertical_seekbar"
        const val READER_LEFT_VERTICAL_SEEKBAR_KEY =
            "__APP_STATE_chimahon_reader_left_vertical_seekbar"
        const val READER_CONTROLS_KEY = "__APP_STATE_chimahon_reader_controls"
        const val READER_TAP_ZONES_KEY = "__APP_STATE_chimahon_reader_tap_zones"
        const val READER_SMALLER_TAP_ZONES_KEY = "__APP_STATE_chimahon_reader_smaller_tap_zones"
        const val READER_INVERT_TAP_ZONES_KEY = "__APP_STATE_chimahon_reader_invert_tap_zones"
        const val READER_SWIPE_NAVIGATION_KEY = "__APP_STATE_chimahon_reader_swipe_navigation"
        const val READER_DOUBLE_TAP_ZOOM_KEY = "__APP_STATE_chimahon_reader_double_tap_zoom"
        const val READER_VOLUME_KEYS_KEY = "__APP_STATE_chimahon_reader_volume_keys"
        const val READER_VOLUME_KEYS_INVERTED_KEY = "__APP_STATE_chimahon_reader_volume_keys_inverted"
        const val READER_LONG_TAP_KEY = "__APP_STATE_chimahon_reader_long_tap"
        const val READER_READ_WITH_LONG_TAP_KEY = "__APP_STATE_chimahon_reader_read_with_long_tap"
        const val READER_KEEP_SCREEN_ON_KEY = "__APP_STATE_chimahon_reader_keep_screen_on"
        const val READER_FULLSCREEN_KEY = "__APP_STATE_chimahon_reader_fullscreen"
        const val READER_DRAW_UNDER_CUTOUT_KEY = "__APP_STATE_chimahon_reader_draw_under_cutout"
        const val READER_OCR_OUTLINE_VISIBLE_KEY =
            "__APP_STATE_chimahon_reader_ocr_outline_visible"
        const val READER_CROP_BORDERS_KEY = "__APP_STATE_chimahon_reader_crop_borders"
        const val READER_PAGE_TRANSITIONS_KEY = "__APP_STATE_chimahon_reader_page_transitions"
        const val READER_E_INK_SWIPE_SENSITIVITY_KEY =
            "__APP_STATE_chimahon_reader_e_ink_swipe_sensitivity"
        const val READER_FLASH_ON_PAGE_CHANGE_KEY =
            "__APP_STATE_chimahon_reader_flash_on_page_change"
        const val READER_FLASH_DURATION_MILLIS_KEY =
            "__APP_STATE_chimahon_reader_flash_duration_millis"
        const val READER_FLASH_PAGE_INTERVAL_KEY =
            "__APP_STATE_chimahon_reader_flash_page_interval"
        const val READER_FLASH_COLOR_KEY = "__APP_STATE_chimahon_reader_flash_color"
        const val READER_SHOW_PAGE_NUMBER_KEY = "__APP_STATE_chimahon_reader_show_page_number"
        const val READER_VERTICAL_WRITING_KEY = "__APP_STATE_chimahon_reader_vertical_writing"
        const val READER_CONTINUOUS_MODE_KEY = "__APP_STATE_chimahon_reader_continuous_mode"
        const val READER_FONT_SIZE_KEY = "__APP_STATE_chimahon_reader_font_size"
        const val READER_LINE_HEIGHT_KEY = "__APP_STATE_chimahon_reader_line_height"
        const val READER_HORIZONTAL_PADDING_KEY = "__APP_STATE_chimahon_reader_horizontal_padding"
        const val READER_VERTICAL_PADDING_KEY = "__APP_STATE_chimahon_reader_vertical_padding"
        const val READER_AVOID_PAGE_BREAK_KEY = "__APP_STATE_chimahon_reader_avoid_page_break"
        const val READER_JUSTIFY_TEXT_KEY = "__APP_STATE_chimahon_reader_justify_text"
        const val READER_CHARACTER_SPACING_KEY = "__APP_STATE_chimahon_reader_character_spacing"
        const val READER_PARAGRAPH_SPACING_KEY = "__APP_STATE_chimahon_reader_paragraph_spacing"
        const val READER_HIDE_FURIGANA_KEY = "__APP_STATE_chimahon_reader_hide_furigana"
        const val READER_SHOW_TITLE_KEY = "__APP_STATE_chimahon_reader_show_title"
        const val READER_SHOW_CHARACTERS_KEY = "__APP_STATE_chimahon_reader_show_characters"
        const val READER_SHOW_PERCENTAGE_KEY = "__APP_STATE_chimahon_reader_show_percentage"
        const val READER_SHOW_PROGRESS_TOP_KEY = "__APP_STATE_chimahon_reader_show_progress_top"
        const val READER_SHOW_READING_SPEED_KEY =
            "__APP_STATE_chimahon_reader_show_reading_speed"
        const val READER_SHOW_READING_TIME_KEY = "__APP_STATE_chimahon_reader_show_reading_time"
        const val READER_TAP_ZONE_PERCENT_KEY = "__APP_STATE_chimahon_reader_tap_zone_percent"
        const val READER_CHAPTER_SWIPE_DISTANCE_KEY =
            "__APP_STATE_chimahon_reader_chapter_swipe_distance"
        const val READER_STARTUP_DELAY_KEY = "__APP_STATE_chimahon_reader_startup_delay"
        const val READER_SHOW_READING_MODE_KEY =
            "__APP_STATE_chimahon_reader_show_reading_mode"
        const val READER_SHOW_NAVIGATION_OVERLAY_KEY =
            "__APP_STATE_chimahon_reader_show_navigation_overlay"
        const val READER_SKIP_READ_CHAPTERS_KEY = "__APP_STATE_chimahon_reader_skip_read_chapters"
        const val READER_SKIP_FILTERED_CHAPTERS_KEY =
            "__APP_STATE_chimahon_reader_skip_filtered_chapters"
        const val READER_SKIP_DUPLICATE_CHAPTERS_KEY =
            "__APP_STATE_chimahon_reader_skip_duplicate_chapters"
        const val READER_ALWAYS_SHOW_TRANSITION_KEY =
            "__APP_STATE_chimahon_reader_always_show_transition"
        const val READER_NAVIGATE_TO_PAN_KEY = "__APP_STATE_chimahon_reader_navigate_to_pan"
        const val READER_FOLDER_PER_MANGA_KEY = "__APP_STATE_chimahon_reader_folder_per_manga"
        const val READER_PRELOAD_SIZE_KEY = "__APP_STATE_chimahon_reader_preload_size"
        const val READER_THREADS_KEY = "__APP_STATE_chimahon_reader_threads"
        const val READER_CACHE_SIZE_MB_KEY = "__APP_STATE_chimahon_reader_cache_size_mb"
        const val READER_AGGRESSIVE_LOADING_KEY =
            "__APP_STATE_chimahon_reader_aggressive_loading"
        const val READER_PRESERVE_READING_POSITION_KEY =
            "__APP_STATE_chimahon_reader_preserve_reading_position"
        const val READER_USE_AUTO_WEBTOON_KEY = "__APP_STATE_chimahon_reader_use_auto_webtoon"
        const val READER_INVERT_DOUBLE_PAGES_KEY =
            "__APP_STATE_chimahon_reader_invert_double_pages"
        const val READER_ROTATE_WIDE_PAGES_TO_FIT_WEBTOON_KEY =
            "__APP_STATE_chimahon_reader_rotate_wide_pages_to_fit_webtoon"
        const val READER_INVERT_WIDE_PAGE_ROTATION_WEBTOON_KEY =
            "__APP_STATE_chimahon_reader_invert_wide_page_rotation_webtoon"
        const val READER_CENTER_MARGIN_DP_KEY = "__APP_STATE_chimahon_reader_center_margin_dp"
        const val READER_PAGED_ZOOM_START_KEY = "__APP_STATE_chimahon_reader_paged_zoom_start"
        const val READER_LANDSCAPE_ZOOM_KEY = "__APP_STATE_chimahon_reader_landscape_zoom"
        const val READER_LANDSCAPE_ZOOM_TYPE_KEY =
            "__APP_STATE_chimahon_reader_landscape_zoom_type"
        const val READER_PAGED_DISABLE_ZOOM_IN_KEY =
            "__APP_STATE_chimahon_reader_paged_disable_zoom_in"
        const val READER_WEBTOON_SCALE_TYPE_KEY =
            "__APP_STATE_chimahon_reader_webtoon_scale_type"
        const val READER_SMART_LONG_STRIP_GAP_SCALE_KEY =
            "__APP_STATE_chimahon_reader_smart_long_strip_gap_scale"
        const val READER_WEBTOON_SIDE_PADDING_KEY =
            "__APP_STATE_chimahon_reader_webtoon_side_padding"
        const val READER_WEBTOON_HIDE_THRESHOLD_KEY =
            "__APP_STATE_chimahon_reader_webtoon_hide_threshold"
        const val READER_WEBTOON_PINCH_TO_ZOOM_KEY =
            "__APP_STATE_chimahon_reader_webtoon_pinch_to_zoom"
        const val READER_WEBTOON_DISABLE_ZOOM_OUT_KEY =
            "__APP_STATE_chimahon_reader_webtoon_disable_zoom_out"
        const val READER_CONTINUOUS_VERTICAL_TAPPING_BY_PAGE_KEY =
            "__APP_STATE_chimahon_reader_continuous_vertical_tapping_by_page"
        const val READER_OCR_AUTO_ON_DOWNLOAD_KEY =
            "__APP_STATE_chimahon_reader_ocr_auto_on_download"
        const val READER_BOTTOM_BUTTONS_KEY = "__APP_STATE_chimahon_reader_bottom_buttons"
        const val LIBRARY_DISPLAY_MODE_KEY = "__APP_STATE_chimahon_library_display_mode"
        const val LIBRARY_GRID_COLUMNS_PORTRAIT_KEY =
            "__APP_STATE_chimahon_library_grid_columns_portrait"
        const val LIBRARY_GRID_COLUMNS_LANDSCAPE_KEY =
            "__APP_STATE_chimahon_library_grid_columns_landscape"
        const val LIBRARY_COVER_ASPECT_RATIO_KEY =
            "__APP_STATE_chimahon_library_cover_aspect_ratio"
        const val LIBRARY_DEFAULT_CATEGORY_KEY = "__APP_STATE_chimahon_library_default_category"
        const val LIBRARY_CATEGORIZED_DISPLAY_SETTINGS_KEY =
            "__APP_STATE_chimahon_library_categorized_display_settings"
        const val LIBRARY_CATEGORY_TABS_KEY = "__APP_STATE_chimahon_library_category_tabs"
        const val LIBRARY_UNREAD_BADGES_KEY = "__APP_STATE_chimahon_library_unread_badges"
        const val LIBRARY_DOWNLOADED_BADGES_KEY = "__APP_STATE_chimahon_library_downloaded_badges"
        const val LIBRARY_LANGUAGE_BADGES_KEY = "__APP_STATE_chimahon_library_language_badges"
        const val LIBRARY_CONTINUE_BUTTONS_KEY = "__APP_STATE_chimahon_library_continue_buttons"
        const val LIBRARY_SORT_KEY = "__APP_STATE_chimahon_library_sort"
        const val LIBRARY_SORT_ASCENDING_KEY = "__APP_STATE_chimahon_library_sort_ascending"
        const val LIBRARY_FILTER_DOWNLOADED_KEY = "__APP_STATE_chimahon_library_filter_downloaded"
        const val LIBRARY_FILTER_UNREAD_KEY = "__APP_STATE_chimahon_library_filter_unread"
        const val LIBRARY_FILTER_STARTED_KEY = "__APP_STATE_chimahon_library_filter_started"
        const val LIBRARY_FILTER_BOOKMARKED_KEY = "__APP_STATE_chimahon_library_filter_bookmarked"
        const val LIBRARY_FILTER_COMPLETED_KEY = "__APP_STATE_chimahon_library_filter_completed"
        const val LIBRARY_FILTER_TRACKED_KEY = "__APP_STATE_chimahon_library_filter_tracked"
        const val LIBRARY_UPDATE_INTERVAL_KEY = "__APP_STATE_chimahon_library_update_interval"
        const val LIBRARY_UPDATE_WIFI_ONLY_KEY = "__APP_STATE_chimahon_library_update_wifi_only"
        const val LIBRARY_UPDATE_RESTRICTIONS_KEY =
            "__APP_STATE_chimahon_library_update_restrictions"
        const val LIBRARY_UPDATE_INCLUDED_CATEGORIES_KEY =
            "__APP_STATE_chimahon_library_update_included_categories"
        const val LIBRARY_UPDATE_EXCLUDED_CATEGORIES_KEY =
            "__APP_STATE_chimahon_library_update_excluded_categories"
        const val LIBRARY_UPDATE_GROUP_MODE_KEY =
            "__APP_STATE_chimahon_library_update_group_mode"
        const val LIBRARY_AUTO_UPDATE_METADATA_KEY =
            "__APP_STATE_chimahon_library_auto_update_metadata"
        const val LIBRARY_SMART_UPDATE_RESTRICTIONS_KEY =
            "__APP_STATE_chimahon_library_smart_update_restrictions"
        const val LIBRARY_SHOW_UPDATE_COUNT_KEY = "__APP_STATE_chimahon_library_show_update_count"
        const val LIBRARY_UPDATE_NOTIFICATIONS_KEY = "__APP_STATE_chimahon_library_update_notifications"
        const val LIBRARY_SHOW_UPDATING_PROGRESS_BANNER_KEY =
            "__APP_STATE_chimahon_library_show_updating_progress_banner"
        const val LIBRARY_SWIPE_TO_START_ACTION_KEY =
            "__APP_STATE_chimahon_library_swipe_to_start_action"
        const val LIBRARY_SWIPE_TO_END_ACTION_KEY =
            "__APP_STATE_chimahon_library_swipe_to_end_action"
        const val LIBRARY_DUPLICATE_READ_CHAPTER_HANDLING_KEY =
            "__APP_STATE_chimahon_library_duplicate_read_chapter_handling"
        const val LIBRARY_HIDE_MISSING_CHAPTERS_KEY =
            "__APP_STATE_chimahon_library_hide_missing_chapters"
        const val LIBRARY_SHOW_EMPTY_CATEGORIES_SEARCH_KEY =
            "__APP_STATE_chimahon_library_show_empty_categories_search"
        const val LIBRARY_FETCH_METADATA_ON_ADD_KEY =
            "__APP_STATE_chimahon_library_fetch_metadata_on_add"
        const val LIBRARY_FETCH_CHAPTERS_ON_ADD_KEY =
            "__APP_STATE_chimahon_library_fetch_chapters_on_add"
        const val LIBRARY_UPDATE_MANGA_TITLES_KEY =
            "__APP_STATE_chimahon_library_update_manga_titles"
        const val LIBRARY_DISALLOW_NON_ASCII_FILENAMES_KEY =
            "__APP_STATE_chimahon_library_disallow_non_ascii_filenames"
        const val ANIME_LIBRARY_DISPLAY_MODE_KEY = "__APP_STATE_chimahon_anime_library_display_mode"
        const val ANIME_LIBRARY_GRID_COLUMNS_PORTRAIT_KEY =
            "__APP_STATE_chimahon_anime_library_grid_columns_portrait"
        const val ANIME_LIBRARY_GRID_COLUMNS_LANDSCAPE_KEY =
            "__APP_STATE_chimahon_anime_library_grid_columns_landscape"
        const val ANIME_LIBRARY_DEFAULT_CATEGORY_ID_KEY =
            "__APP_STATE_chimahon_anime_library_default_category_id"
        const val ANIME_LIBRARY_CATEGORIZED_DISPLAY_SETTINGS_KEY =
            "__APP_STATE_chimahon_anime_library_categorized_display_settings"
        const val ANIME_LIBRARY_CATEGORY_TABS_KEY =
            "__APP_STATE_chimahon_anime_library_category_tabs"
        const val ANIME_LIBRARY_CATEGORY_ITEM_COUNT_KEY =
            "__APP_STATE_chimahon_anime_library_category_item_count"
        const val ANIME_LIBRARY_UNSEEN_BADGES_KEY =
            "__APP_STATE_chimahon_anime_library_unseen_badges"
        const val ANIME_LIBRARY_DOWNLOADED_BADGES_KEY =
            "__APP_STATE_chimahon_anime_library_downloaded_badges"
        const val ANIME_LIBRARY_LOCAL_BADGES_KEY =
            "__APP_STATE_chimahon_anime_library_local_badges"
        const val ANIME_LIBRARY_LANGUAGE_BADGES_KEY =
            "__APP_STATE_chimahon_anime_library_language_badges"
        const val ANIME_LIBRARY_CONTINUE_WATCHING_BUTTONS_KEY =
            "__APP_STATE_chimahon_anime_library_continue_watching_buttons"
        const val ANIME_LIBRARY_SORT_KEY = "__APP_STATE_chimahon_anime_library_sort"
        const val ANIME_LIBRARY_SORT_ASCENDING_KEY =
            "__APP_STATE_chimahon_anime_library_sort_ascending"
        const val ANIME_LIBRARY_FILTER_DOWNLOADED_KEY =
            "__APP_STATE_chimahon_anime_library_filter_downloaded"
        const val ANIME_LIBRARY_FILTER_UNSEEN_KEY =
            "__APP_STATE_chimahon_anime_library_filter_unseen"
        const val ANIME_LIBRARY_FILTER_STARTED_KEY =
            "__APP_STATE_chimahon_anime_library_filter_started"
        const val ANIME_LIBRARY_FILTER_BOOKMARKED_KEY =
            "__APP_STATE_chimahon_anime_library_filter_bookmarked"
        const val ANIME_LIBRARY_FILTER_COMPLETED_KEY =
            "__APP_STATE_chimahon_anime_library_filter_completed"
        const val ANIME_LIBRARY_FILTER_FILLER_KEY =
            "__APP_STATE_chimahon_anime_library_filter_filler"
        const val ANIME_LIBRARY_FILTER_TRACKED_KEY =
            "__APP_STATE_chimahon_anime_library_filter_tracked"
        const val ANIME_LIBRARY_GROUP_BY_KEY = "__APP_STATE_chimahon_anime_library_group_by"
        const val ANIME_LIBRARY_UPDATE_RESTRICTIONS_KEY =
            "__APP_STATE_chimahon_anime_library_update_restrictions"
        const val ANIME_LIBRARY_SWIPE_TO_START_ACTION_KEY =
            "__APP_STATE_chimahon_anime_library_swipe_to_start_action"
        const val ANIME_LIBRARY_SWIPE_TO_END_ACTION_KEY =
            "__APP_STATE_chimahon_anime_library_swipe_to_end_action"
        const val DOWNLOAD_WIFI_ONLY_KEY = "__APP_STATE_chimahon_download_wifi_only"
        const val DOWNLOAD_SAVE_AS_CBZ_KEY = "__APP_STATE_chimahon_download_save_as_cbz"
        const val DOWNLOAD_SPLIT_TALL_IMAGES_KEY = "__APP_STATE_chimahon_download_split_tall_images"
        const val DOWNLOAD_WHILE_READING_KEY = "__APP_STATE_chimahon_download_while_reading"
        const val DOWNLOAD_REMOVE_AFTER_READ_KEY = "__APP_STATE_chimahon_download_remove_after_read"
        const val DOWNLOAD_REMOVE_AFTER_MARKED_READ_KEY = "__APP_STATE_chimahon_download_remove_after_marked_read"
        const val DOWNLOAD_REMOVE_BOOKMARKED_KEY = "__APP_STATE_chimahon_download_remove_bookmarked"
        const val DOWNLOAD_REMOVE_EXCLUDED_CATEGORIES_KEY =
            "__APP_STATE_chimahon_download_remove_excluded_categories"
        const val DOWNLOAD_NEW_CHAPTERS_KEY = "__APP_STATE_chimahon_download_new_chapters"
        const val DOWNLOAD_NEW_UNREAD_ONLY_KEY = "__APP_STATE_chimahon_download_new_unread_only"
        const val DOWNLOAD_NEW_INCLUDED_CATEGORIES_KEY =
            "__APP_STATE_chimahon_download_new_included_categories"
        const val DOWNLOAD_NEW_EXCLUDED_CATEGORIES_KEY =
            "__APP_STATE_chimahon_download_new_excluded_categories"
        const val DOWNLOAD_PARALLEL_SOURCES_KEY = "__APP_STATE_chimahon_download_parallel_sources"
        const val DOWNLOAD_PARALLEL_PAGES_KEY = "__APP_STATE_chimahon_download_parallel_pages"
        const val DOWNLOAD_INCLUDE_CHAPTER_URL_HASH_KEY =
            "__APP_STATE_chimahon_download_include_chapter_url_hash"
        const val DOWNLOAD_CACHE_RENEW_INTERVAL_KEY =
            "__APP_STATE_chimahon_download_cache_renew_interval"
        const val BROWSE_SHOW_NSFW_KEY = "__APP_STATE_chimahon_browse_show_nsfw"
        const val BROWSE_HIDE_LIBRARY_ENTRIES_KEY = "__APP_STATE_chimahon_browse_hide_library_entries"
        const val BROWSE_AUTO_LOAD_MORE_KEY = "__APP_STATE_chimahon_browse_auto_load_more"
        const val BROWSE_ENABLED_LANGUAGES_KEY = "__APP_STATE_chimahon_browse_enabled_languages"
        const val BROWSE_PINNED_SOURCE_IDS_KEY = "__APP_STATE_chimahon_browse_pinned_source_ids"
        const val BROWSE_DISABLED_EXTENSION_REPOS_KEY =
            "__APP_STATE_chimahon_browse_disabled_extension_repos"
        const val BROWSE_SOURCE_DISPLAY_MODE_KEY =
            "__APP_STATE_chimahon_browse_source_display_mode"
        const val BROWSE_GROUP_SOURCES_BY_LANGUAGE_KEY =
            "__APP_STATE_chimahon_browse_group_sources_by_language"
        const val BROWSE_SHOW_SOURCE_LANGUAGE_KEY =
            "__APP_STATE_chimahon_browse_show_source_language"
        const val BROWSE_EXTENSION_UPDATE_NOTIFICATIONS_KEY =
            "__APP_STATE_chimahon_browse_extension_update_notifications"
        const val BROWSE_RELATED_MANGA_RECOMMENDATIONS_KEY =
            "__APP_STATE_chimahon_browse_related_manga_recommendations"
        const val BROWSE_EXPAND_RELATED_MANGA_KEY =
            "__APP_STATE_chimahon_browse_expand_related_manga"
        const val BROWSE_RELATED_MANGA_IN_OVERFLOW_KEY =
            "__APP_STATE_chimahon_browse_related_manga_in_overflow"
        const val BROWSE_SHOW_HOME_IN_RELATED_MANGA_KEY =
            "__APP_STATE_chimahon_browse_show_home_in_related_manga"
        const val BROWSE_SOURCE_CATEGORIES_FILTER_KEY =
            "__APP_STATE_chimahon_browse_source_categories_filter"
        const val BROWSE_USE_NEW_SOURCE_NAVIGATION_KEY =
            "__APP_STATE_chimahon_browse_use_new_source_navigation"
        const val BROWSE_ALLOW_LOCAL_SOURCE_HIDDEN_FOLDERS_KEY =
            "__APP_STATE_chimahon_browse_allow_local_source_hidden_folders"
        const val BROWSE_HIDE_FEED_TAB_KEY = "__APP_STATE_chimahon_browse_hide_feed_tab"
        const val BROWSE_FEED_TAB_IN_FRONT_KEY =
            "__APP_STATE_chimahon_browse_feed_tab_in_front"
        const val BROWSE_HIDE_LIBRARY_FEED_ENTRIES_KEY =
            "__APP_STATE_chimahon_browse_hide_library_feed_entries"
        const val NAVIGATION_TAB_LAYOUT_KEY = "__APP_STATE_chimahon_navigation_tab_layout"
        const val NAVIGATION_START_SCREEN_KEY = "__APP_STATE_chimahon_navigation_start_screen"
        const val NAVIGATION_SHOW_UPDATES_TAB_KEY =
            "__APP_STATE_chimahon_navigation_show_updates_tab"
        const val NAVIGATION_SHOW_HISTORY_TAB_KEY =
            "__APP_STATE_chimahon_navigation_show_history_tab"
        const val PLAYER_PRESERVE_POSITION_KEY = "__APP_STATE_chimahon_player_preserve_position"
        const val PLAYER_PROGRESS_PREFERENCE_KEY = "__APP_STATE_chimahon_player_progress_preference"
        const val PLAYER_DEFAULT_ORIENTATION_KEY = "__APP_STATE_chimahon_player_default_orientation"
        const val PLAYER_ALLOW_GESTURES_IN_PANELS_KEY =
            "__APP_STATE_chimahon_player_allow_gestures_in_panels"
        const val PLAYER_SHOW_LOADING_KEY = "__APP_STATE_chimahon_player_show_loading"
        const val PLAYER_SHOW_CURRENT_EPISODE_KEY =
            "__APP_STATE_chimahon_player_show_current_episode"
        const val PLAYER_REMEMBER_BRIGHTNESS_KEY =
            "__APP_STATE_chimahon_player_remember_brightness"
        const val PLAYER_BRIGHTNESS_VALUE_KEY = "__APP_STATE_chimahon_player_brightness_value"
        const val PLAYER_REMEMBER_VOLUME_KEY = "__APP_STATE_chimahon_player_remember_volume"
        const val PLAYER_VOLUME_VALUE_KEY = "__APP_STATE_chimahon_player_volume_value"
        const val PLAYER_SHOW_FAILED_HOSTERS_KEY =
            "__APP_STATE_chimahon_player_show_failed_hosters"
        const val PLAYER_SHOW_EMPTY_HOSTERS_KEY =
            "__APP_STATE_chimahon_player_show_empty_hosters"
        const val PLAYER_FULLSCREEN_KEY = "__APP_STATE_chimahon_player_fullscreen"
        const val PLAYER_HIDE_CONTROLS_KEY = "__APP_STATE_chimahon_player_hide_controls"
        const val PLAYER_DISPLAY_VOLUME_PERCENT_KEY =
            "__APP_STATE_chimahon_player_display_volume_percent"
        const val PLAYER_SHOW_SYSTEM_STATUS_BAR_KEY =
            "__APP_STATE_chimahon_player_show_system_status_bar"
        const val PLAYER_REDUCE_MOTION_KEY = "__APP_STATE_chimahon_player_reduce_motion"
        const val PLAYER_CONTROLS_HIDE_DELAY_KEY =
            "__APP_STATE_chimahon_player_controls_hide_delay"
        const val PLAYER_PANEL_OPACITY_KEY = "__APP_STATE_chimahon_player_panel_opacity"
        const val PLAYER_SKIP_INTRO_ENABLED_KEY =
            "__APP_STATE_chimahon_player_skip_intro_enabled"
        const val PLAYER_AUTO_SKIP_INTRO_KEY = "__APP_STATE_chimahon_player_auto_skip_intro"
        const val PLAYER_NETFLIX_STYLE_SKIP_INTRO_KEY =
            "__APP_STATE_chimahon_player_netflix_style_skip_intro"
        const val PLAYER_SKIP_INTRO_WAIT_KEY = "__APP_STATE_chimahon_player_skip_intro_wait"
        const val PLAYER_ANI_SKIP_ENABLED_KEY = "__APP_STATE_chimahon_player_ani_skip_enabled"
        const val PLAYER_DISABLE_ANI_SKIP_ON_CHAPTERS_KEY =
            "__APP_STATE_chimahon_player_disable_ani_skip_on_chapters"
        const val PLAYER_PIP_ENABLED_KEY = "__APP_STATE_chimahon_player_pip_enabled"
        const val PLAYER_PIP_EPISODE_TOASTS_KEY =
            "__APP_STATE_chimahon_player_pip_episode_toasts"
        const val PLAYER_PIP_ON_EXIT_KEY = "__APP_STATE_chimahon_player_pip_on_exit"
        const val PLAYER_PIP_REPLACE_WITH_PREVIOUS_KEY =
            "__APP_STATE_chimahon_player_pip_replace_with_previous"
        const val PLAYER_CAST_ENABLED_KEY = "__APP_STATE_chimahon_player_cast_enabled"
        const val PLAYER_ALWAYS_EXTERNAL_KEY = "__APP_STATE_chimahon_player_always_external"
        const val PLAYER_EXTERNAL_PACKAGE_KEY = "__APP_STATE_chimahon_player_external_package"
        const val PLAYER_SPEED_KEY = "__APP_STATE_chimahon_player_speed"
        const val PLAYER_SPEED_PRESETS_KEY = "__APP_STATE_chimahon_player_speed_presets"
        const val PLAYER_INVERT_DURATION_KEY = "__APP_STATE_chimahon_player_invert_duration"
        const val PLAYER_ASPECT_KEY = "__APP_STATE_chimahon_player_aspect"
        const val PLAYER_AUTOPLAY_KEY = "__APP_STATE_chimahon_player_autoplay"
        const val PLAYER_GESTURE_VOLUME_BRIGHTNESS_KEY =
            "__APP_STATE_chimahon_player_gesture_volume_brightness"
        const val PLAYER_GESTURE_SWAP_SLIDERS_KEY =
            "__APP_STATE_chimahon_player_gesture_swap_sliders"
        const val PLAYER_GESTURE_HORIZONTAL_SEEK_KEY =
            "__APP_STATE_chimahon_player_gesture_horizontal_seek"
        const val PLAYER_GESTURE_SHOW_SEEKBAR_KEY =
            "__APP_STATE_chimahon_player_gesture_show_seekbar"
        const val PLAYER_GESTURE_DEFAULT_INTRO_LENGTH_KEY =
            "__APP_STATE_chimahon_player_gesture_default_intro_length"
        const val PLAYER_GESTURE_SKIP_LENGTH_KEY =
            "__APP_STATE_chimahon_player_gesture_skip_length"
        const val PLAYER_GESTURE_SMOOTH_SEEK_KEY =
            "__APP_STATE_chimahon_player_gesture_smooth_seek"
        const val PLAYER_GESTURE_LEFT_DOUBLE_TAP_KEY =
            "__APP_STATE_chimahon_player_gesture_left_double_tap"
        const val PLAYER_GESTURE_CENTER_DOUBLE_TAP_KEY =
            "__APP_STATE_chimahon_player_gesture_center_double_tap"
        const val PLAYER_GESTURE_RIGHT_DOUBLE_TAP_KEY =
            "__APP_STATE_chimahon_player_gesture_right_double_tap"
        const val PLAYER_GESTURE_MEDIA_PREVIOUS_KEY =
            "__APP_STATE_chimahon_player_gesture_media_previous"
        const val PLAYER_GESTURE_MEDIA_PLAY_PAUSE_KEY =
            "__APP_STATE_chimahon_player_gesture_media_play_pause"
        const val PLAYER_GESTURE_MEDIA_NEXT_KEY =
            "__APP_STATE_chimahon_player_gesture_media_next"
        const val PLAYER_DECODER_TRY_HW_KEY = "__APP_STATE_chimahon_player_decoder_try_hw"
        const val PLAYER_DECODER_GPU_NEXT_KEY = "__APP_STATE_chimahon_player_decoder_gpu_next"
        const val PLAYER_DECODER_DEBANDING_KEY =
            "__APP_STATE_chimahon_player_decoder_debanding"
        const val PLAYER_DECODER_USE_YUV420P_KEY =
            "__APP_STATE_chimahon_player_decoder_use_yuv420p"
        const val PLAYER_DECODER_BRIGHTNESS_FILTER_KEY =
            "__APP_STATE_chimahon_player_decoder_brightness_filter"
        const val PLAYER_DECODER_SATURATION_FILTER_KEY =
            "__APP_STATE_chimahon_player_decoder_saturation_filter"
        const val PLAYER_DECODER_CONTRAST_FILTER_KEY =
            "__APP_STATE_chimahon_player_decoder_contrast_filter"
        const val PLAYER_DECODER_GAMMA_FILTER_KEY =
            "__APP_STATE_chimahon_player_decoder_gamma_filter"
        const val PLAYER_DECODER_HUE_FILTER_KEY =
            "__APP_STATE_chimahon_player_decoder_hue_filter"
        const val PLAYER_SUBTITLE_LANGUAGES_KEY =
            "__APP_STATE_chimahon_player_subtitle_languages"
        const val PLAYER_SUBTITLE_WHITELIST_KEY =
            "__APP_STATE_chimahon_player_subtitle_whitelist"
        const val PLAYER_SUBTITLE_BLACKLIST_KEY =
            "__APP_STATE_chimahon_player_subtitle_blacklist"
        const val PLAYER_SUBTITLE_JIMAKU_API_KEY =
            "__APP_STATE_chimahon_player_subtitle_jimaku_api_key"
        const val PLAYER_SUBTITLE_JIMAKU_TITLE_KEY =
            "__APP_STATE_chimahon_player_subtitle_jimaku_title"
        const val PLAYER_SUBTITLE_REGEX_REMOVE_SPEAKERS_KEY =
            "__APP_STATE_chimahon_player_subtitle_regex_remove_speakers"
        const val PLAYER_SUBTITLE_REGEX_MERGE_MULTILINE_KEY =
            "__APP_STATE_chimahon_player_subtitle_regex_merge_multiline"
        const val PLAYER_SUBTITLE_REGEX_REMOVE_BRACKETED_KEY =
            "__APP_STATE_chimahon_player_subtitle_regex_remove_bracketed"
        const val PLAYER_SUBTITLE_REGEX_REMOVE_UPPERCASE_KEY =
            "__APP_STATE_chimahon_player_subtitle_regex_remove_uppercase"
        const val PLAYER_SUBTITLE_REGEX_REMOVE_MUSIC_KEY =
            "__APP_STATE_chimahon_player_subtitle_regex_remove_music"
        const val PLAYER_SUBTITLE_REGEX_REMOVE_CURLY_KEY =
            "__APP_STATE_chimahon_player_subtitle_regex_remove_curly"
        const val PLAYER_SUBTITLE_REGEX_CUSTOM_ENABLED_KEY =
            "__APP_STATE_chimahon_player_subtitle_regex_custom_enabled"
        const val PLAYER_SUBTITLE_REGEX_CUSTOM_PATTERN_KEY =
            "__APP_STATE_chimahon_player_subtitle_regex_custom_pattern"
        const val PLAYER_SUBTITLE_SCREENSHOT_KEY =
            "__APP_STATE_chimahon_player_subtitle_screenshot"
        const val PLAYER_SUBTITLE_LIST_MODE_KEY =
            "__APP_STATE_chimahon_player_subtitle_list_mode"
        const val PLAYER_SUBTITLE_FONT_KEY = "__APP_STATE_chimahon_player_subtitle_font"
        const val PLAYER_SUBTITLE_FONT_SIZE_KEY =
            "__APP_STATE_chimahon_player_subtitle_font_size"
        const val PLAYER_SUBTITLE_FONT_SCALE_KEY =
            "__APP_STATE_chimahon_player_subtitle_font_scale"
        const val PLAYER_SUBTITLE_BORDER_SIZE_KEY =
            "__APP_STATE_chimahon_player_subtitle_border_size"
        const val PLAYER_SUBTITLE_BOLD_KEY = "__APP_STATE_chimahon_player_subtitle_bold"
        const val PLAYER_SUBTITLE_ITALIC_KEY = "__APP_STATE_chimahon_player_subtitle_italic"
        const val PLAYER_SUBTITLE_TEXT_COLOR_KEY =
            "__APP_STATE_chimahon_player_subtitle_text_color"
        const val PLAYER_SUBTITLE_BORDER_COLOR_KEY =
            "__APP_STATE_chimahon_player_subtitle_border_color"
        const val PLAYER_SUBTITLE_BORDER_STYLE_KEY =
            "__APP_STATE_chimahon_player_subtitle_border_style"
        const val PLAYER_SUBTITLE_SHADOW_OFFSET_KEY =
            "__APP_STATE_chimahon_player_subtitle_shadow_offset"
        const val PLAYER_SUBTITLE_BACKGROUND_COLOR_KEY =
            "__APP_STATE_chimahon_player_subtitle_background_color"
        const val PLAYER_SUBTITLE_JUSTIFICATION_KEY =
            "__APP_STATE_chimahon_player_subtitle_justification"
        const val PLAYER_SUBTITLE_POSITION_KEY =
            "__APP_STATE_chimahon_player_subtitle_position"
        const val PLAYER_SUBTITLE_OVERRIDE_ASS_KEY =
            "__APP_STATE_chimahon_player_subtitle_override_ass"
        const val PLAYER_SUBTITLE_DELAY_KEY = "__APP_STATE_chimahon_player_subtitle_delay"
        const val PLAYER_SUBTITLE_SPEED_KEY = "__APP_STATE_chimahon_player_subtitle_speed"
        const val PLAYER_SUBTITLE_SECONDARY_DELAY_KEY =
            "__APP_STATE_chimahon_player_subtitle_secondary_delay"
        const val PLAYER_AUDIO_LANGUAGES_KEY = "__APP_STATE_chimahon_player_audio_languages"
        const val PLAYER_AUDIO_PITCH_CORRECTION_KEY =
            "__APP_STATE_chimahon_player_audio_pitch_correction"
        const val PLAYER_AUDIO_CHANNELS_KEY = "__APP_STATE_chimahon_player_audio_channels"
        const val PLAYER_AUDIO_VOLUME_BOOST_CAP_KEY =
            "__APP_STATE_chimahon_player_audio_volume_boost_cap"
        const val PLAYER_AUDIO_DELAY_KEY = "__APP_STATE_chimahon_player_audio_delay"
        const val PLAYER_SELECTION_REMEMBER_QUALITY_KEY =
            "__APP_STATE_chimahon_player_selection_remember_quality"
        const val PLAYER_SELECTION_REMEMBER_SUBTITLES_KEY =
            "__APP_STATE_chimahon_player_selection_remember_subtitles"
        const val PLAYER_SELECTION_RESTORE_ADDED_SUBTITLES_KEY =
            "__APP_STATE_chimahon_player_selection_restore_added_subtitles"
        const val PLAYER_SELECTION_HOSTER_KEY =
            "__APP_STATE_chimahon_player_selection_hoster"
        const val PLAYER_SELECTION_VIDEO_KEY =
            "__APP_STATE_chimahon_player_selection_video"
        const val PLAYER_SELECTION_PRIMARY_SUBTITLE_KEY =
            "__APP_STATE_chimahon_player_selection_primary_subtitle"
        const val PLAYER_SELECTION_SECONDARY_SUBTITLE_KEY =
            "__APP_STATE_chimahon_player_selection_secondary_subtitle"
        const val PLAYER_SELECTION_ADDED_SUBTITLE_KEYS =
            "__APP_STATE_chimahon_player_selection_added_subtitles"
        const val PLAYER_ADVANCED_MPV_SCRIPTS_KEY =
            "__APP_STATE_chimahon_player_advanced_mpv_scripts"
        const val PLAYER_ADVANCED_MPV_CONFIG_KEY =
            "__APP_STATE_chimahon_player_advanced_mpv_config"
        const val PLAYER_ADVANCED_MPV_INPUT_KEY =
            "__APP_STATE_chimahon_player_advanced_mpv_input"
        const val PLAYER_ADVANCED_STATISTICS_PAGE_KEY =
            "__APP_STATE_chimahon_player_advanced_statistics_page"
        const val TRACKING_AUTO_SYNC_ENABLED_KEY =
            "__APP_STATE_chimahon_tracking_auto_sync_enabled"
        const val TRACKING_UPDATE_INTERVAL_HOURS_KEY =
            "__APP_STATE_chimahon_tracking_update_interval_hours"
        const val TRACKING_SYNC_RESTRICTIONS_KEY =
            "__APP_STATE_chimahon_tracking_sync_restrictions"
        const val TRACKING_SYNC_INCLUDED_CATEGORIES_KEY =
            "__APP_STATE_chimahon_tracking_sync_included_categories"
        const val TRACKING_SYNC_EXCLUDED_CATEGORIES_KEY =
            "__APP_STATE_chimahon_tracking_sync_excluded_categories"
        const val TRACKING_SYNC_LIBRARY_ENTRIES_ONLY_KEY =
            "__APP_STATE_chimahon_tracking_sync_library_entries_only"
        const val TRACKING_TRACK_ON_ADD_TO_LIBRARY_KEY =
            "__APP_STATE_chimahon_tracking_track_on_add_to_library"
        const val TRACKING_AUTO_UPDATE_ON_MARK_READ_KEY =
            "__APP_STATE_chimahon_tracking_auto_update_on_mark_read"
        const val TRACKING_AUTO_SYNC_PROGRESS_FROM_TRACKERS_KEY =
            "__APP_STATE_chimahon_tracking_auto_sync_progress_from_trackers"
        const val TRACKING_RESOLVE_USING_SOURCE_METADATA_KEY =
            "__APP_STATE_chimahon_tracking_resolve_using_source_metadata"
        const val CONNECTIONS_OPENING_PREFERENCE_KEY =
            "__APP_STATE_chimahon_connections_opening_preference"
        const val CONNECTIONS_DISCORD_RPC_ENABLED_KEY =
            "__APP_STATE_chimahon_connections_discord_rpc_enabled"
        const val CONNECTIONS_DISCORD_STATUS_KEY =
            "__APP_STATE_chimahon_connections_discord_status"
        const val CONNECTIONS_DISCORD_RPC_INCOGNITO_KEY =
            "__APP_STATE_chimahon_connections_discord_rpc_incognito"
        const val CONNECTIONS_DISCORD_RPC_INCOGNITO_CATEGORIES_KEY =
            "__APP_STATE_chimahon_connections_discord_rpc_incognito_categories"
        const val CONNECTIONS_DISCORD_SHOW_MANGA_TITLE_KEY =
            "__APP_STATE_chimahon_connections_discord_show_manga_title"
        const val CONNECTIONS_DISCORD_SHOW_COVER_ART_KEY =
            "__APP_STATE_chimahon_connections_discord_show_cover_art"
        const val CONNECTIONS_DISCORD_SHOW_SOURCE_NAME_KEY =
            "__APP_STATE_chimahon_connections_discord_show_source_name"
        const val CONNECTIONS_DISCORD_USE_CHAPTER_TITLES_KEY =
            "__APP_STATE_chimahon_connections_discord_use_chapter_titles"
        const val CONNECTIONS_DISCORD_SHOW_PROGRESS_KEY =
            "__APP_STATE_chimahon_connections_discord_show_progress"
        const val CONNECTIONS_DISCORD_SHOW_TIMESTAMP_KEY =
            "__APP_STATE_chimahon_connections_discord_show_timestamp"
        const val CONNECTIONS_DISCORD_SHOW_BUTTONS_KEY =
            "__APP_STATE_chimahon_connections_discord_show_buttons"
        const val CONNECTIONS_DISCORD_SHOW_DOWNLOAD_BUTTON_KEY =
            "__APP_STATE_chimahon_connections_discord_show_download_button"
        const val CONNECTIONS_DISCORD_SHOW_DISCORD_BUTTON_KEY =
            "__APP_STATE_chimahon_connections_discord_show_discord_button"
        const val DICTIONARY_ENABLED_KEY = "__APP_STATE_chimahon_dictionary_enabled"
        const val DICTIONARY_ENABLED_LANGUAGES_KEY =
            "__APP_STATE_chimahon_dictionary_enabled_languages"
        const val DICTIONARY_OCR_ENABLED_KEY = "__APP_STATE_chimahon_dictionary_ocr_enabled"
        const val DICTIONARY_POPUP_WIDTH_KEY = "__APP_STATE_chimahon_dictionary_popup_width"
        const val DICTIONARY_POPUP_HEIGHT_KEY = "__APP_STATE_chimahon_dictionary_popup_height"
        const val DICTIONARY_FONT_SIZE_KEY = "__APP_STATE_chimahon_dictionary_font_size"
        const val DICTIONARY_OCR_BOX_SCALE_X_PERCENT_KEY =
            "__APP_STATE_chimahon_dictionary_ocr_box_scale_x_percent"
        const val DICTIONARY_OCR_BOX_SCALE_Y_PERCENT_KEY =
            "__APP_STATE_chimahon_dictionary_ocr_box_scale_y_percent"
        const val DICTIONARY_OCR_BOX_OPACITY_PERCENT_KEY =
            "__APP_STATE_chimahon_dictionary_ocr_box_opacity_percent"
        const val DICTIONARY_OCR_ENGINE_KEY = "__APP_STATE_chimahon_dictionary_ocr_engine"
        const val DICTIONARY_THEME_MODE_KEY = "__APP_STATE_chimahon_dictionary_theme_mode"
        const val DICTIONARY_E_INK_MODE_KEY = "__APP_STATE_chimahon_dictionary_e_ink_mode"
        const val DICTIONARY_PAGINATED_SCROLLING_KEY =
            "__APP_STATE_chimahon_dictionary_paginated_scrolling"
        const val DICTIONARY_SHOW_FREQUENCY_HARMONIC_KEY =
            "__APP_STATE_chimahon_dictionary_show_frequency_harmonic"
        const val DICTIONARY_SHOW_FREQUENCY_AVERAGE_KEY =
            "__APP_STATE_chimahon_dictionary_show_frequency_average"
        const val DICTIONARY_GROUP_PITCHES_KEY =
            "__APP_STATE_chimahon_dictionary_group_pitches"
        const val DICTIONARY_GROUP_TERMS_KEY = "__APP_STATE_chimahon_dictionary_group_terms"
        const val DICTIONARY_SHOW_NAVIGATION_BUTTONS_KEY =
            "__APP_STATE_chimahon_dictionary_show_navigation_buttons"
        const val DICTIONARY_POPUP_MODE_KEY = "__APP_STATE_chimahon_dictionary_popup_mode"
        const val DICTIONARY_RECURSIVE_LOOKUP_MODE_KEY =
            "__APP_STATE_chimahon_dictionary_recursive_lookup_mode"
        const val DICTIONARY_SHOW_PITCH_DIAGRAM_KEY =
            "__APP_STATE_chimahon_dictionary_show_pitch_diagram"
        const val DICTIONARY_SHOW_PITCH_NUMBER_KEY =
            "__APP_STATE_chimahon_dictionary_show_pitch_number"
        const val DICTIONARY_SHOW_PITCH_TEXT_KEY =
            "__APP_STATE_chimahon_dictionary_show_pitch_text"
        const val DICTIONARY_AUTO_KANA_CONVERSION_KEY =
            "__APP_STATE_chimahon_dictionary_auto_kana_conversion"
        const val DICTIONARY_WORD_AUDIO_ENABLED_KEY =
            "__APP_STATE_chimahon_dictionary_word_audio_enabled"
        const val DICTIONARY_WORD_AUDIO_AUTOPLAY_KEY =
            "__APP_STATE_chimahon_dictionary_word_audio_autoplay"
        const val DICTIONARY_WORD_AUDIO_LOCAL_ENABLED_KEY =
            "__APP_STATE_chimahon_dictionary_word_audio_local_enabled"
        const val SECURITY_SECURE_SCREEN_KEY = "__APP_STATE_chimahon_security_secure_screen"
        const val SECURITY_HIDE_NOTIFICATION_CONTENT_KEY =
            "__APP_STATE_chimahon_security_hide_notification_content"
        const val SECURITY_CRASH_REPORTS_ENABLED_KEY =
            "__APP_STATE_chimahon_security_crash_reports_enabled"
        const val SECURITY_REQUIRE_AUTHENTICATION_KEY =
            "__APP_STATE_chimahon_security_require_authentication"
        const val SECURITY_LOCK_AFTER_MINUTES_KEY = "__APP_STATE_chimahon_security_lock_after_minutes"
        const val SECURITY_LOCK_ON_APP_EXIT_KEY = "__APP_STATE_chimahon_security_lock_on_app_exit"
        const val SECURITY_INCOGNITO_BY_DEFAULT_KEY =
            "__APP_STATE_chimahon_security_incognito_by_default"
        const val SECURITY_PROTECT_DOWNLOADS_KEY = "__APP_STATE_chimahon_security_protect_downloads"
        const val SECURITY_DOWNLOAD_ENCRYPTION_KEY =
            "__APP_STATE_chimahon_security_download_encryption"
        const val SECURITY_BIOMETRIC_LOCK_DAYS_KEY =
            "__APP_STATE_chimahon_security_biometric_lock_days"
        const val SECURITY_ENCRYPT_DATABASE_KEY = "__APP_STATE_chimahon_security_encrypt_database"
        const val DOWNLOADED_ONLY_KEY = "__APP_STATE_pref_downloaded_only"
        const val INCOGNITO_MODE_KEY = "__APP_STATE_incognito_mode"
    }
}
