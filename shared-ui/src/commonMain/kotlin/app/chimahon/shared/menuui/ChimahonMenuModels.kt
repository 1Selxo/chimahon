package app.chimahon.shared.menuui

enum class ChimahonMenuActionArea {
    Screen,
    Manga,
    Anime,
    Reader,
    Novel,
    Player,
    Source,
    Repository,
    Settings,
    Desktop,
}

enum class ChimahonMenuActionId {
    Search,
    Filter,
    Sort,
    Refresh,
    Settings,
    AnimeSettings,
    NovelSettings,
    PlayerSettings,
    DesktopSettings,
    Help,
    Share,
    OpenInBrowser,
    CopyLink,
    WebView,
    SelectAll,
    ClearSelection,
    Favorite,
    Unfavorite,
    Categories,
    MarkRead,
    MarkUnread,
    MarkPreviousRead,
    MarkSeen,
    MarkUnseen,
    MarkPreviousSeen,
    Download,
    DeleteDownloads,
    RemoveFromLibrary,
    RemoveAnimeFromLibrary,
    RemoveNovelFromLibrary,
    EditInfo,
    Track,
    Migrate,
    Chapters,
    Statistics,
    ReaderSettings,
    ReadingMode,
    Orientation,
    Crop,
    OcrLookup,
    DictionaryLookup,
    AutoScroll,
    NovelTypography,
    NovelLookup,
    NovelImport,
    Retry,
    RetryAll,
    SaveImage,
    SetCover,
    SharePage,
    ResetReaderView,
    PinSource,
    UnpinSource,
    EnableSource,
    DisableSource,
    SourceSettings,
    ExtensionRepositories,
    RepositorySettings,
    TrustExtension,
    UntrustExtension,
    InstallExtension,
    UninstallExtension,
    UpdateExtension,
    SelectRepository,
    AddRepository,
    EditRepository,
    RemoveRepository,
    CopyRepositoryUrl,
    RefreshRepository,
    Backup,
    Restore,
    Import,
    Export,
    ResetSettings,
    DataAndStorage,
    Theme,
    About,
    SubtitleSettings,
    AudioDelay,
    VideoFilters,
    VideoOcr,
    PlaybackSpeed,
    Quality,
    Subtitles,
    AudioTracks,
    Episodes,
    Hosters,
    OpenExternalPlayer,
    TogglePictureInPicture,
    ToggleTheaterMode,
    ToggleCast,
    DesktopShortcuts,
    CommandPalette,
    Custom,
}

enum class ChimahonMenuActionRole {
    Normal,
    Toggle,
    Radio,
}

enum class ChimahonMenuActionTone {
    Normal,
    Primary,
    Warning,
    Destructive,
}

enum class ChimahonMenuIcon {
    Add,
    Backup,
    Bookmark,
    BookmarkBorder,
    Check,
    Close,
    Copy,
    Crop,
    Delete,
    DoneAll,
    Download,
    Edit,
    Extension,
    Favorite,
    FavoriteBorder,
    Filter,
    Help,
    Info,
    Label,
    Link,
    List,
    More,
    Open,
    Palette,
    Public,
    Refresh,
    Reorder,
    Search,
    Security,
    Settings,
    Share,
    Star,
    Storage,
    Swap,
    Visibility,
    VisibilityOff,
    Volume,
}

data class ChimahonMenuAction(
    val id: ChimahonMenuActionId,
    val label: String,
    val area: ChimahonMenuActionArea,
    val icon: ChimahonMenuIcon? = id.defaultMenuIcon(),
    val supportingText: String? = null,
    val enabled: Boolean = true,
    val checked: Boolean = false,
    val selected: Boolean = false,
    val busy: Boolean = false,
    val badge: String? = null,
    val shortcut: String? = null,
    val role: ChimahonMenuActionRole = ChimahonMenuActionRole.Normal,
    val tone: ChimahonMenuActionTone = id.defaultMenuTone(),
    val closeMenuOnClick: Boolean = true,
    val payloadKey: String? = null,
) {
    val clickable: Boolean
        get() = enabled && !busy

    val active: Boolean
        get() = checked || selected
}

data class ChimahonMenuSection(
    val title: String? = null,
    val actions: List<ChimahonMenuAction>,
    val initiallyExpanded: Boolean = true,
) {
    val visibleActions: List<ChimahonMenuAction>
        get() = actions.filterNot { it.label.isBlank() }
}

data class ChimahonActionSheetState(
    val title: String,
    val subtitle: String? = null,
    val sections: List<ChimahonMenuSection>,
    val primaryActionId: ChimahonMenuActionId? = null,
    val dismissLabel: String = "Cancel",
) {
    val actions: List<ChimahonMenuAction>
        get() = sections.flatMap { it.visibleActions }
}

data class ChimahonOverflowMenuState(
    val sections: List<ChimahonMenuSection>,
    val compact: Boolean = true,
) {
    val actions: List<ChimahonMenuAction>
        get() = sections.flatMap { it.visibleActions }
}

fun ChimahonMenuActionId.defaultMenuIcon(): ChimahonMenuIcon? {
    return when (this) {
        ChimahonMenuActionId.Search -> ChimahonMenuIcon.Search
        ChimahonMenuActionId.Filter -> ChimahonMenuIcon.Filter
        ChimahonMenuActionId.Sort -> ChimahonMenuIcon.Reorder
        ChimahonMenuActionId.Refresh -> ChimahonMenuIcon.Refresh
        ChimahonMenuActionId.Settings,
        ChimahonMenuActionId.AnimeSettings,
        ChimahonMenuActionId.NovelSettings,
        ChimahonMenuActionId.PlayerSettings,
        ChimahonMenuActionId.DesktopSettings,
        ChimahonMenuActionId.ReaderSettings,
        ChimahonMenuActionId.SourceSettings,
        ChimahonMenuActionId.RepositorySettings -> ChimahonMenuIcon.Settings
        ChimahonMenuActionId.Help -> ChimahonMenuIcon.Help
        ChimahonMenuActionId.Share,
        ChimahonMenuActionId.SharePage -> ChimahonMenuIcon.Share
        ChimahonMenuActionId.OpenInBrowser,
        ChimahonMenuActionId.WebView,
        ChimahonMenuActionId.OpenExternalPlayer,
        ChimahonMenuActionId.ToggleCast,
        ChimahonMenuActionId.Hosters -> ChimahonMenuIcon.Public
        ChimahonMenuActionId.CopyLink,
        ChimahonMenuActionId.CopyRepositoryUrl -> ChimahonMenuIcon.Copy
        ChimahonMenuActionId.SelectAll,
        ChimahonMenuActionId.MarkPreviousRead,
        ChimahonMenuActionId.MarkPreviousSeen -> ChimahonMenuIcon.DoneAll
        ChimahonMenuActionId.ClearSelection -> ChimahonMenuIcon.Close
        ChimahonMenuActionId.Favorite -> ChimahonMenuIcon.Favorite
        ChimahonMenuActionId.Unfavorite -> ChimahonMenuIcon.FavoriteBorder
        ChimahonMenuActionId.Categories -> ChimahonMenuIcon.Label
        ChimahonMenuActionId.MarkRead,
        ChimahonMenuActionId.MarkSeen -> ChimahonMenuIcon.Check
        ChimahonMenuActionId.MarkUnread,
        ChimahonMenuActionId.MarkUnseen -> ChimahonMenuIcon.VisibilityOff
        ChimahonMenuActionId.Download,
        ChimahonMenuActionId.SaveImage,
        ChimahonMenuActionId.Backup,
        ChimahonMenuActionId.Export -> ChimahonMenuIcon.Download
        ChimahonMenuActionId.DeleteDownloads,
        ChimahonMenuActionId.RemoveFromLibrary,
        ChimahonMenuActionId.RemoveAnimeFromLibrary,
        ChimahonMenuActionId.RemoveNovelFromLibrary,
        ChimahonMenuActionId.RemoveRepository,
        ChimahonMenuActionId.UninstallExtension,
        ChimahonMenuActionId.ResetSettings -> ChimahonMenuIcon.Delete
        ChimahonMenuActionId.EditInfo,
        ChimahonMenuActionId.EditRepository -> ChimahonMenuIcon.Edit
        ChimahonMenuActionId.Track -> ChimahonMenuIcon.Star
        ChimahonMenuActionId.Migrate -> ChimahonMenuIcon.Swap
        ChimahonMenuActionId.Chapters,
        ChimahonMenuActionId.Episodes,
        ChimahonMenuActionId.AudioTracks -> ChimahonMenuIcon.List
        ChimahonMenuActionId.Statistics -> ChimahonMenuIcon.Info
        ChimahonMenuActionId.ReadingMode,
        ChimahonMenuActionId.PlaybackSpeed -> ChimahonMenuIcon.Open
        ChimahonMenuActionId.Orientation -> ChimahonMenuIcon.Swap
        ChimahonMenuActionId.Crop,
        ChimahonMenuActionId.VideoFilters -> ChimahonMenuIcon.Crop
        ChimahonMenuActionId.OcrLookup,
        ChimahonMenuActionId.DictionaryLookup,
        ChimahonMenuActionId.NovelLookup,
        ChimahonMenuActionId.VideoOcr,
        ChimahonMenuActionId.CommandPalette -> ChimahonMenuIcon.Search
        ChimahonMenuActionId.AutoScroll,
        ChimahonMenuActionId.AudioDelay -> ChimahonMenuIcon.Volume
        ChimahonMenuActionId.Retry,
        ChimahonMenuActionId.RetryAll,
        ChimahonMenuActionId.RefreshRepository,
        ChimahonMenuActionId.UpdateExtension -> ChimahonMenuIcon.Refresh
        ChimahonMenuActionId.SetCover -> ChimahonMenuIcon.Bookmark
        ChimahonMenuActionId.ResetReaderView -> ChimahonMenuIcon.Open
        ChimahonMenuActionId.PinSource -> ChimahonMenuIcon.Star
        ChimahonMenuActionId.UnpinSource -> ChimahonMenuIcon.Star
        ChimahonMenuActionId.EnableSource,
        ChimahonMenuActionId.TrustExtension -> ChimahonMenuIcon.Visibility
        ChimahonMenuActionId.DisableSource,
        ChimahonMenuActionId.UntrustExtension -> ChimahonMenuIcon.VisibilityOff
        ChimahonMenuActionId.InstallExtension,
        ChimahonMenuActionId.ExtensionRepositories -> ChimahonMenuIcon.Extension
        ChimahonMenuActionId.SelectRepository -> ChimahonMenuIcon.Check
        ChimahonMenuActionId.AddRepository -> ChimahonMenuIcon.Add
        ChimahonMenuActionId.Restore,
        ChimahonMenuActionId.Import,
        ChimahonMenuActionId.NovelImport -> ChimahonMenuIcon.Backup
        ChimahonMenuActionId.DataAndStorage -> ChimahonMenuIcon.Storage
        ChimahonMenuActionId.Theme,
        ChimahonMenuActionId.NovelTypography,
        ChimahonMenuActionId.SubtitleSettings,
        ChimahonMenuActionId.Subtitles -> ChimahonMenuIcon.Palette
        ChimahonMenuActionId.About -> ChimahonMenuIcon.Info
        ChimahonMenuActionId.Quality,
        ChimahonMenuActionId.ToggleTheaterMode,
        ChimahonMenuActionId.TogglePictureInPicture -> ChimahonMenuIcon.Visibility
        ChimahonMenuActionId.DesktopShortcuts -> ChimahonMenuIcon.Reorder
        ChimahonMenuActionId.Custom -> null
    }
}

fun ChimahonMenuActionId.defaultMenuTone(): ChimahonMenuActionTone {
    return when (this) {
        ChimahonMenuActionId.DeleteDownloads,
        ChimahonMenuActionId.RemoveFromLibrary,
        ChimahonMenuActionId.RemoveAnimeFromLibrary,
        ChimahonMenuActionId.RemoveNovelFromLibrary,
        ChimahonMenuActionId.RemoveRepository,
        ChimahonMenuActionId.UninstallExtension,
        ChimahonMenuActionId.ResetSettings -> ChimahonMenuActionTone.Destructive
        ChimahonMenuActionId.DisableSource,
        ChimahonMenuActionId.UntrustExtension -> ChimahonMenuActionTone.Warning
        ChimahonMenuActionId.SelectRepository,
        ChimahonMenuActionId.AddRepository,
        ChimahonMenuActionId.InstallExtension,
        ChimahonMenuActionId.UpdateExtension,
        ChimahonMenuActionId.Backup,
        ChimahonMenuActionId.Restore,
        ChimahonMenuActionId.NovelImport,
        ChimahonMenuActionId.OpenExternalPlayer -> ChimahonMenuActionTone.Primary
        else -> ChimahonMenuActionTone.Normal
    }
}

fun ChimahonMenuAction.asChecked(
    checked: Boolean,
    role: ChimahonMenuActionRole = ChimahonMenuActionRole.Toggle,
): ChimahonMenuAction {
    return copy(checked = checked, role = role)
}

fun ChimahonMenuAction.disabled(reason: String? = null): ChimahonMenuAction {
    return copy(enabled = false, supportingText = reason ?: supportingText)
}

fun List<ChimahonMenuAction>.inSingleSection(title: String? = null): List<ChimahonMenuSection> {
    return listOf(ChimahonMenuSection(title = title, actions = this))
}
