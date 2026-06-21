package app.chimahon.shared

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.decodeToImageBitmap
import kotlin.math.min

sealed interface ChimahonUiState {
    data object Loading : ChimahonUiState
    data class Ready(val snapshot: ChimahonSnapshot) : ChimahonUiState
    data class Failed(val message: String) : ChimahonUiState
}

data class ChimahonSnapshot(
    val summary: ChimahonSummary,
    val mangaDetails: Map<Long, ChimahonMangaEntry>,
    val library: List<ChimahonMangaEntry>,
    val libraryCategories: List<ChimahonLibraryCategory>,
    val libraryCategoryMemberships: Map<Long, List<Long>>,
    val chaptersByMangaId: Map<Long, List<ChimahonChapterEntry>>,
    val updates: List<ChimahonRecentUpdateEntry>,
    val history: List<ChimahonHistoryEntry>,
    val sources: List<ChimahonSourceEntry>,
    val extensionRepos: List<ChimahonExtensionRepoEntry>,
    val installedExtensions: List<ChimahonInstalledExtensionEntry>,
    val updateIssues: List<ChimahonUpdateIssue>,
    val runtime: ChimahonRuntimeInfo,
)

data class ChimahonSummary(
    val extensionCount: Int,
    val sourceCount: Int,
    val mangaCount: Int,
)

data class ChimahonMangaEntry(
    val id: Long,
    val sourceId: Long,
    val url: String,
    val title: String,
    val artist: String?,
    val author: String?,
    val description: String?,
    val genres: List<String>,
    val status: String,
    val thumbnailUrl: String?,
    val favorite: Boolean,
    val initialized: Boolean,
    val dateAdded: Long,
    val lastUpdate: Long?,
    val notes: String,
)

data class ChimahonLibraryCategory(
    val id: Long,
    val name: String,
    val order: Long,
    val hidden: Boolean,
)

data class ChimahonSourceEntry(
    val id: Long,
    val name: String,
    val language: String,
    val supportsLatest: Boolean,
)

enum class ChimahonSourceBrowseMode(val title: String) {
    Popular("Popular"),
    Latest("Latest"),
    Search("Search"),
}

private enum class LibraryFilter(val title: String) {
    All("All"),
    Unread("Unread"),
    Started("Started"),
    Bookmarked("Bookmarked"),
}

private enum class LibrarySort(val title: String) {
    Alphabetical("Alphabetical"),
    DateAdded("Date added"),
    LastUpdated("Last updated"),
    UnreadCount("Unread count"),
}

private enum class LibraryDisplayMode(val title: String) {
    ComfortableGrid("Comfortable"),
    CompactGrid("Compact"),
    List("List"),
}

private fun ChimahonLibraryDisplayMode.toUiLibraryDisplayMode(): LibraryDisplayMode = when (this) {
    ChimahonLibraryDisplayMode.ComfortableGrid -> LibraryDisplayMode.ComfortableGrid
    ChimahonLibraryDisplayMode.CompactGrid -> LibraryDisplayMode.CompactGrid
    ChimahonLibraryDisplayMode.List -> LibraryDisplayMode.List
}

private fun LibraryDisplayMode.toSettingsLibraryDisplayMode(): ChimahonLibraryDisplayMode = when (this) {
    LibraryDisplayMode.ComfortableGrid -> ChimahonLibraryDisplayMode.ComfortableGrid
    LibraryDisplayMode.CompactGrid -> ChimahonLibraryDisplayMode.CompactGrid
    LibraryDisplayMode.List -> ChimahonLibraryDisplayMode.List
}

private enum class UpdatesFilter(val title: String) {
    All("All"),
    Unread("Unread"),
    Bookmarked("Bookmarked"),
}

private enum class HistoryFilter(val title: String) {
    All("All"),
    Unfinished("Unfinished"),
    Completed("Completed"),
}

private enum class ExtensionFilter(val title: String) {
    All("All"),
    Installed("Installed"),
    Available("Available"),
    Nsfw("NSFW"),
}

private enum class ChapterFilter(val title: String) {
    All("All"),
    Unread("Unread"),
    Started("Started"),
    Bookmarked("Bookmarked"),
}

private enum class MorePage(val title: String) {
    Main("More"),
    Downloads("Download queue"),
    Categories("Categories"),
    Statistics("Statistics"),
    Storage("Data and storage"),
    Settings("Settings"),
    AppearanceSettings("Appearance"),
    LibrarySettings("Library"),
    ReaderSettings("Reader"),
    DownloadSettings("Downloads"),
    TrackingSettings("Tracking"),
    ConnectionsSettings("Connections"),
    BrowseSettings("Browse"),
    DictionarySettings("Dictionary"),
    SecuritySettings("Security"),
    AdvancedSettings("Advanced"),
    About("About"),
    Help("Help"),
}

data class ChimahonSourcePreview(
    val sourceId: Long,
    val sourceName: String,
    val mode: ChimahonSourceBrowseMode,
    val entries: List<ChimahonRemoteMangaEntry>,
    val hasNextPage: Boolean,
)

data class ChimahonRemoteMangaEntry(
    val sourceId: Long,
    val title: String,
    val author: String?,
    val status: String,
    val url: String,
    val thumbnailUrl: String?,
    val initialized: Boolean,
)

data class ChimahonRemoteMangaDetail(
    val sourceId: Long,
    val sourceName: String,
    val title: String,
    val artist: String?,
    val author: String?,
    val description: String?,
    val genres: List<String>,
    val status: String,
    val statusCode: Long,
    val url: String,
    val thumbnailUrl: String?,
    val initialized: Boolean,
    val chapters: List<ChimahonRemoteChapterEntry>,
)

data class ChimahonRemoteChapterEntry(
    val name: String,
    val url: String,
    val chapterNumber: Double,
    val scanlator: String?,
    val dateUpload: Long,
)

data class ChimahonReaderRequest(
    val sourceId: Long,
    val mangaTitle: String,
    val chapterName: String,
    val chapterUrl: String,
    val chapterNumber: Double,
    val scanlator: String?,
    val dateUpload: Long,
    val mangaId: Long? = null,
    val chapterId: Long? = null,
    val initialPage: Int = 0,
    val chapterQueue: List<ChimahonReaderChapterRef> = emptyList(),
    val chapterIndex: Int = -1,
)

data class ChimahonReaderChapterRef(
    val chapterId: Long?,
    val chapterName: String,
    val chapterUrl: String,
    val chapterNumber: Double,
    val scanlator: String?,
    val dateUpload: Long,
    val initialPage: Int = 0,
    val read: Boolean = false,
    val bookmarked: Boolean = false,
    val lastPageRead: Long = 0L,
)

data class ChimahonReaderChapter(
    val request: ChimahonReaderRequest,
    val pages: List<ChimahonReaderPage>,
)

data class ChimahonReaderPage(
    val sourceId: Long,
    val index: Int,
    val url: String,
    val imageUrl: String?,
)

data class ChimahonExtensionRepoEntry(
    val baseUrl: String,
    val name: String,
    val shortName: String?,
    val website: String,
    val signingKeyFingerprint: String,
)

data class ChimahonExtensionRepoCatalog(
    val repo: ChimahonExtensionRepoEntry,
    val extensions: List<ChimahonRepoExtensionEntry>,
)

enum class ChimahonExtensionPackageType {
    JavaScript,
    AndroidApk,
}

data class ChimahonRepoExtensionEntry(
    val repoBaseUrl: String,
    val id: String,
    val name: String,
    val version: String,
    val artifactUrl: String,
    val packageType: ChimahonExtensionPackageType = ChimahonExtensionPackageType.JavaScript,
    val language: String = "",
    val sourceCount: Int = 0,
    val isNsfw: Boolean = false,
)

data class ChimahonInstalledExtensionEntry(
    val id: String,
    val name: String,
    val version: String,
    val sourceCount: Int,
    val packageType: ChimahonExtensionPackageType = ChimahonExtensionPackageType.JavaScript,
)

data class ChimahonRecentUpdateEntry(
    val mangaId: Long,
    val chapterId: Long,
    val mangaTitle: String,
    val chapterName: String,
    val scanlator: String?,
    val sourceId: Long,
    val read: Boolean,
    val bookmarked: Boolean,
    val lastPageRead: Long,
    val dateFetch: Long,
)

data class ChimahonHistoryEntry(
    val id: Long,
    val mangaId: Long,
    val chapterId: Long,
    val title: String,
    val sourceId: Long,
    val chapterNumber: Double,
    val read: Boolean,
    val lastPageRead: Long,
    val totalCount: Double,
    val readCount: Double,
    val readAt: Long?,
    val readDuration: Long,
)

data class ChimahonChapterEntry(
    val id: Long,
    val mangaId: Long,
    val url: String,
    val name: String,
    val scanlator: String?,
    val read: Boolean,
    val bookmarked: Boolean,
    val lastPageRead: Long,
    val chapterNumber: Double,
    val sourceOrder: Long,
    val dateFetch: Long,
    val dateUpload: Long,
)

data class ChimahonUpdateIssue(
    val id: Long,
    val mangaId: Long,
    val mangaTitle: String,
    val message: String,
    val lastUpdate: String,
)

data class ChimahonRuntimeInfo(
    val platformName: String,
    val filesDir: String,
    val cacheDir: String,
    val downloadsDir: String,
    val databaseState: String,
    val extensionState: String,
    val backgroundState: String,
)

private sealed interface SourcePreviewUiState {
    data object Idle : SourcePreviewUiState
    data object Loading : SourcePreviewUiState
    data class Ready(val preview: ChimahonSourcePreview) : SourcePreviewUiState
    data class Failed(val message: String) : SourcePreviewUiState
}

private sealed interface RemoteMangaDetailUiState {
    data object Loading : RemoteMangaDetailUiState
    data class Ready(val detail: ChimahonRemoteMangaDetail) : RemoteMangaDetailUiState
    data class Failed(val message: String) : RemoteMangaDetailUiState
}

private sealed interface ReaderUiState {
    data object Loading : ReaderUiState
    data class Ready(val chapter: ChimahonReaderChapter) : ReaderUiState
    data class Failed(val message: String) : ReaderUiState
}

private sealed interface ReaderPageUiState {
    data object Loading : ReaderPageUiState
    data class Ready(val image: ImageBitmap) : ReaderPageUiState
    data class Failed(val message: String) : ReaderPageUiState
}

private sealed interface ThumbnailUiState {
    data object Loading : ThumbnailUiState
    data class Ready(val image: ImageBitmap) : ThumbnailUiState
    data object Failed : ThumbnailUiState
}

private val LocalThumbnailLoader = staticCompositionLocalOf<suspend (String) -> ByteArray> {
    { error("Thumbnail loading is unavailable.") }
}

private enum class ReaderMode(
    val title: String,
    val paged: Boolean,
    val rightToLeft: Boolean = false,
) {
    Webtoon("Webtoon", paged = false),
    Vertical("Vertical", paged = false),
    LeftToRight("Left to right", paged = true),
    RightToLeft("Right to left", paged = true, rightToLeft = true),
}

private enum class ReaderScale(val title: String) {
    FitScreen("Fit screen"),
    FitWidth("Fit width"),
    FitHeight("Fit height"),
}

private enum class ReaderCanvas(val title: String, val color: Color) {
    Black("Black", Color.Black),
    Gray("Gray", Color(0xFF242424)),
    White("White", Color(0xFFF4F4F4)),
}

private fun ChimahonReaderMode.toUiReaderMode(): ReaderMode = when (this) {
    ChimahonReaderMode.Webtoon -> ReaderMode.Webtoon
    ChimahonReaderMode.Vertical -> ReaderMode.Vertical
    ChimahonReaderMode.LeftToRight -> ReaderMode.LeftToRight
    ChimahonReaderMode.RightToLeft -> ReaderMode.RightToLeft
}

private fun ReaderMode.toSettingsReaderMode(): ChimahonReaderMode = when (this) {
    ReaderMode.Webtoon -> ChimahonReaderMode.Webtoon
    ReaderMode.Vertical -> ChimahonReaderMode.Vertical
    ReaderMode.LeftToRight -> ChimahonReaderMode.LeftToRight
    ReaderMode.RightToLeft -> ChimahonReaderMode.RightToLeft
}

private fun ChimahonReaderScale.toUiReaderScale(): ReaderScale = when (this) {
    ChimahonReaderScale.FitScreen -> ReaderScale.FitScreen
    ChimahonReaderScale.FitWidth -> ReaderScale.FitWidth
    ChimahonReaderScale.FitHeight -> ReaderScale.FitHeight
}

private fun ReaderScale.toSettingsReaderScale(): ChimahonReaderScale = when (this) {
    ReaderScale.FitScreen -> ChimahonReaderScale.FitScreen
    ReaderScale.FitWidth -> ChimahonReaderScale.FitWidth
    ReaderScale.FitHeight -> ChimahonReaderScale.FitHeight
}

private fun ChimahonReaderCanvas.toUiReaderCanvas(): ReaderCanvas = when (this) {
    ChimahonReaderCanvas.Black -> ReaderCanvas.Black
    ChimahonReaderCanvas.Gray -> ReaderCanvas.Gray
    ChimahonReaderCanvas.White -> ReaderCanvas.White
}

private fun ReaderCanvas.toSettingsReaderCanvas(): ChimahonReaderCanvas = when (this) {
    ReaderCanvas.Black -> ChimahonReaderCanvas.Black
    ReaderCanvas.Gray -> ChimahonReaderCanvas.Gray
    ReaderCanvas.White -> ChimahonReaderCanvas.White
}

private sealed interface ExtensionRepoCatalogUiState {
    data object Idle : ExtensionRepoCatalogUiState
    data object Loading : ExtensionRepoCatalogUiState
    data class Ready(val catalog: ChimahonExtensionRepoCatalog) : ExtensionRepoCatalogUiState
    data class Failed(val message: String) : ExtensionRepoCatalogUiState
}

@Composable
fun ChimahonServiceApp(
    services: ChimahonSharedAppServices,
) {
    var refreshKey by remember { mutableIntStateOf(0) }
    var state by remember { mutableStateOf<ChimahonUiState>(ChimahonUiState.Loading) }

    LaunchedEffect(refreshKey) {
        state = ChimahonUiState.Loading
        state = runCatching { services.loadSnapshot() }
            .fold(
                onSuccess = ChimahonUiState::Ready,
                onFailure = { ChimahonUiState.Failed(it.message ?: "Unable to initialize Chimahon services.") },
            )
    }

    ChimahonApp(
        state = state,
        onRefresh = { refreshKey++ },
        onLoadSourcePreview = services::loadSourcePreview,
        onLoadRemoteMangaDetail = services::loadRemoteMangaDetail,
        onLoadReaderChapter = services::loadReaderChapter,
        onLoadReaderPageImage = services::loadReaderPageImage,
        onLoadThumbnailImage = services::loadThumbnailImage,
        onLoadSettings = services::loadSettings,
        onSaveAppearanceSettings = services::saveAppearanceSettings,
        onSaveReaderSettings = services::saveReaderSettings,
        onSaveLibrarySettings = services::saveLibrarySettings,
        onSaveDownloadPreferences = services::saveDownloadPreferences,
        onSaveBrowseSettings = services::saveBrowseSettings,
        onSaveTrackingSettings = services::saveTrackingSettings,
        onSaveConnectionSettings = services::saveConnectionSettings,
        onSaveDictionarySettings = services::saveDictionarySettings,
        onSaveSecuritySettings = services::saveSecuritySettings,
        onSetDownloadedOnly = services::setDownloadedOnly,
        onSetIncognitoMode = services::setIncognitoMode,
        onOpenExternalUrl = services::openExternalUrl,
        onOpenRemoteMangaUrl = services::openRemoteMangaUrl,
        onSaveReaderProgress = services::saveReaderProgress,
        onAddRemoteMangaToLibrary = services::addRemoteMangaToLibrary,
        onRefreshManga = services::refreshManga,
        onCreateCategory = services::createCategory,
        onDeleteCategory = services::deleteCategory,
        onSetMangaCategories = services::setMangaCategories,
        onSetMangaFavorite = services::setMangaFavorite,
        onSetMangasFavorite = services::setMangasFavorite,
        onSetMangaNotes = services::setMangaNotes,
        onResetHistoryEntry = services::resetHistoryEntry,
        onResetHistoryForManga = services::resetHistoryForManga,
        onClearHistory = services::clearHistory,
        onDismissUpdateIssue = services::dismissUpdateIssue,
        onClearUpdateIssues = services::clearUpdateIssues,
        onLoadDataMaintenance = services::loadDataMaintenanceSnapshot,
        onClearThumbnailCache = services::clearThumbnailCache,
        onClearCacheData = services::clearCacheData,
        onRunDatabaseMaintenance = services::runDatabaseMaintenance,
        onSetMangaChaptersRead = services::setMangaChaptersRead,
        onSetMangasChaptersRead = services::setMangasChaptersRead,
        onSetChapterRead = services::setChapterRead,
        onSetChapterBookmark = services::setChapterBookmark,
        onEnqueueDownload = services::enqueueDownload,
        onEnqueueDownloads = services::enqueueDownloads,
        onLoadDownloadSnapshot = services::loadDownloadSnapshot,
        onLoadDownloadQueue = services::loadDownloadQueue,
        onPauseDownloadQueue = services::pauseDownloadQueue,
        onResumeDownloadQueue = services::resumeDownloadQueue,
        onProcessNextDownload = services::processNextDownload,
        onPauseDownload = services::pauseDownload,
        onResumeDownload = services::resumeDownload,
        onRetryDownload = services::retryDownload,
        onRemoveDownload = services::removeDownload,
        onClearCompletedDownloads = services::clearCompletedDownloads,
        onMoveDownloadToTop = services::moveDownloadToTop,
        onMoveDownloadToBottom = services::moveDownloadToBottom,
        onAddExtensionRepo = services::addExtensionRepo,
        onDeleteExtensionRepo = services::deleteExtensionRepo,
        onLoadExtensionRepoCatalog = services::loadExtensionRepoCatalog,
        onInstallExtension = services::installExtension,
        onUninstallExtension = services::uninstallExtension,
    )
}

@Composable
fun ChimahonApp(
    state: ChimahonUiState = ChimahonUiState.Ready(nativeCheckpointSnapshot()),
    onRefresh: () -> Unit = {},
    onLoadSourcePreview: suspend (Long, ChimahonSourceBrowseMode, String, Int) -> ChimahonSourcePreview = {
            sourceId,
            mode,
            _,
            _,
        ->
        ChimahonSourcePreview(
            sourceId = sourceId,
            sourceName = "Source $sourceId",
            mode = mode,
            entries = emptyList(),
            hasNextPage = false,
        )
    },
    onLoadRemoteMangaDetail: suspend (ChimahonRemoteMangaEntry) -> ChimahonRemoteMangaDetail = { remoteManga ->
        ChimahonRemoteMangaDetail(
            sourceId = remoteManga.sourceId,
            sourceName = "Source ${remoteManga.sourceId}",
            title = remoteManga.title,
            artist = null,
            author = remoteManga.author,
            description = null,
            genres = emptyList(),
            status = remoteManga.status,
            statusCode = 0L,
            url = remoteManga.url,
            thumbnailUrl = remoteManga.thumbnailUrl,
            initialized = remoteManga.initialized,
            chapters = emptyList(),
        )
    },
    onLoadReaderChapter: suspend (ChimahonReaderRequest) -> ChimahonReaderChapter = { request ->
        ChimahonReaderChapter(request = request, pages = emptyList())
    },
    onLoadReaderPageImage: suspend (ChimahonReaderPage) -> ByteArray = {
        error("Reader page image loading is unavailable in preview.")
    },
    onLoadThumbnailImage: suspend (String) -> ByteArray = {
        error("Thumbnail loading is unavailable in preview.")
    },
    onLoadSettings: suspend () -> ChimahonSettings = { ChimahonSettings() },
    onSaveAppearanceSettings: suspend (ChimahonAppearanceSettings) -> ChimahonAppearanceSettings = { it },
    onSaveReaderSettings: suspend (ChimahonReaderSettings) -> ChimahonReaderSettings = { it },
    onSaveLibrarySettings: suspend (ChimahonLibrarySettings) -> ChimahonLibrarySettings = { it },
    onSaveDownloadPreferences: suspend (ChimahonDownloadPreferences) -> ChimahonDownloadPreferences = { it },
    onSaveBrowseSettings: suspend (ChimahonBrowseSettings) -> ChimahonBrowseSettings = { it },
    onSaveTrackingSettings: suspend (ChimahonTrackingSettings) -> ChimahonTrackingSettings = { it },
    onSaveConnectionSettings: suspend (ChimahonConnectionSettings) -> ChimahonConnectionSettings = { it },
    onSaveDictionarySettings: suspend (ChimahonDictionarySettings) -> ChimahonDictionarySettings = { it },
    onSaveSecuritySettings: suspend (ChimahonSecuritySettings) -> ChimahonSecuritySettings = { it },
    onSetDownloadedOnly: suspend (Boolean) -> ChimahonAppModeSettings = {
        ChimahonAppModeSettings(downloadedOnly = it)
    },
    onSetIncognitoMode: suspend (Boolean) -> ChimahonAppModeSettings = {
        ChimahonAppModeSettings(incognitoMode = it)
    },
    onOpenExternalUrl: (String) -> Boolean = { false },
    onOpenRemoteMangaUrl: (ChimahonRemoteMangaDetail) -> Boolean = { false },
    onSaveReaderProgress: suspend (ChimahonReaderRequest, Int, Boolean) -> Unit = { _, _, _ -> },
    onAddRemoteMangaToLibrary: suspend (ChimahonRemoteMangaDetail) -> Long = {
        error("Adding remote manga is unavailable in preview.")
    },
    onRefreshManga: suspend (Long) -> Unit = {},
    onCreateCategory: suspend (String) -> Long = { 0L },
    onDeleteCategory: suspend (Long) -> Unit = {},
    onSetMangaCategories: suspend (Long, Set<Long>) -> Unit = { _, _ -> },
    onSetMangaFavorite: suspend (Long, Boolean) -> Unit = { _, _ -> },
    onSetMangasFavorite: suspend (Set<Long>, Boolean) -> ChimahonLibraryBulkActionResult = { mangaIds, _ ->
        ChimahonLibraryBulkActionResult(mangaCount = mangaIds.size)
    },
    onSetMangaNotes: suspend (Long, String) -> Unit = { _, _ -> },
    onResetHistoryEntry: suspend (Long) -> Unit = {},
    onResetHistoryForManga: suspend (Long) -> Unit = {},
    onClearHistory: suspend () -> Unit = {},
    onDismissUpdateIssue: suspend (Long) -> Unit = {},
    onClearUpdateIssues: suspend () -> Unit = {},
    onLoadDataMaintenance: suspend () -> ChimahonDataMaintenanceSnapshot = { previewDataMaintenanceSnapshot() },
    onClearThumbnailCache: suspend () -> ChimahonCacheClearResult = { previewCacheClearResult(ChimahonCacheClearTarget.Thumbnails) },
    onClearCacheData: suspend () -> ChimahonCacheClearResult = { previewCacheClearResult(ChimahonCacheClearTarget.ApplicationCache) },
    onRunDatabaseMaintenance: suspend () -> ChimahonDatabaseMaintenanceResult = { previewDatabaseMaintenanceResult() },
    onSetMangaChaptersRead: suspend (Long, Boolean) -> Unit = { _, _ -> },
    onSetMangasChaptersRead: suspend (Set<Long>, Boolean) -> ChimahonLibraryBulkActionResult = { mangaIds, _ ->
        ChimahonLibraryBulkActionResult(mangaCount = mangaIds.size)
    },
    onSetChapterRead: suspend (Long, Boolean) -> Unit = { _, _ -> },
    onSetChapterBookmark: suspend (Long, Boolean) -> Unit = { _, _ -> },
    onEnqueueDownload: suspend (Long) -> ChimahonDownloadQueueData = {
        ChimahonDownloadQueueData(entries = emptyList(), paused = false)
    },
    onEnqueueDownloads: suspend (Collection<Long>) -> ChimahonDownloadQueueData = {
        ChimahonDownloadQueueData(entries = emptyList(), paused = false)
    },
    onLoadDownloadSnapshot: suspend () -> ChimahonDownloadSnapshot = {
        ChimahonDownloadSnapshot(
            chaptersById = emptyMap(),
            queue = ChimahonDownloadQueueData(entries = emptyList(), paused = false),
        )
    },
    onLoadDownloadQueue: suspend () -> ChimahonDownloadQueueData = {
        ChimahonDownloadQueueData(entries = emptyList(), paused = false)
    },
    onPauseDownloadQueue: suspend () -> ChimahonDownloadQueueData = { onLoadDownloadQueue() },
    onResumeDownloadQueue: suspend () -> ChimahonDownloadQueueData = { onLoadDownloadQueue() },
    onProcessNextDownload: suspend () -> ChimahonDownloadQueueData = { onLoadDownloadQueue() },
    onPauseDownload: suspend (Long) -> ChimahonDownloadQueueData = { onLoadDownloadQueue() },
    onResumeDownload: suspend (Long) -> ChimahonDownloadQueueData = { onLoadDownloadQueue() },
    onRetryDownload: suspend (Long) -> ChimahonDownloadQueueData = { onLoadDownloadQueue() },
    onRemoveDownload: suspend (Long) -> ChimahonDownloadQueueData = { onLoadDownloadQueue() },
    onClearCompletedDownloads: suspend () -> ChimahonDownloadQueueData = { onLoadDownloadQueue() },
    onMoveDownloadToTop: suspend (Long) -> ChimahonDownloadQueueData = { onLoadDownloadQueue() },
    onMoveDownloadToBottom: suspend (Long) -> ChimahonDownloadQueueData = { onLoadDownloadQueue() },
    onAddExtensionRepo: suspend (String) -> ChimahonExtensionRepoEntry = { input ->
        ChimahonExtensionRepoEntry(
            baseUrl = input,
            name = input,
            shortName = null,
            website = input,
            signingKeyFingerprint = "preview",
        )
    },
    onDeleteExtensionRepo: suspend (String) -> Unit = {},
    onLoadExtensionRepoCatalog: suspend (ChimahonExtensionRepoEntry) -> ChimahonExtensionRepoCatalog = { repo ->
        ChimahonExtensionRepoCatalog(repo = repo, extensions = emptyList())
    },
    onInstallExtension: suspend (ChimahonRepoExtensionEntry) -> ChimahonInstalledExtensionEntry = { extension ->
        ChimahonInstalledExtensionEntry(
            id = extension.id,
            name = extension.name,
            version = extension.version,
            sourceCount = 0,
            packageType = extension.packageType,
        )
    },
    onUninstallExtension: suspend (
        String,
        ChimahonExtensionPackageType,
    ) -> ChimahonExtensionUninstallResult = { extensionId, packageType ->
        ChimahonExtensionUninstallResult(
            extensionId = extensionId,
            packageType = packageType,
            supported = true,
            removed = true,
            installedExtensions = emptyList(),
            errors = emptyList(),
        )
    },
) {
    var selectedTab by remember { mutableStateOf(HomeTab.Library) }
    var selectedBrowseSection by remember { mutableStateOf(BrowseSection.Sources) }
    var selectedMangaId by remember { mutableStateOf<Long?>(null) }
    var selectedSourceId by remember { mutableStateOf<Long?>(null) }
    var selectedSourceInitialMode by remember { mutableStateOf(ChimahonSourceBrowseMode.Popular) }
    var selectedRemoteManga by remember { mutableStateOf<ChimahonRemoteMangaEntry?>(null) }
    var selectedReader by remember { mutableStateOf<ChimahonReaderRequest?>(null) }
    var sourceSearchKey by remember { mutableIntStateOf(0) }
    var homeSearchActive by remember { mutableStateOf(false) }
    var homeSearchQuery by remember { mutableStateOf("") }
    var homeFiltersVisible by remember { mutableStateOf(false) }
    var feedManageMode by remember { mutableStateOf(false) }
    var migrateHelpVisible by remember { mutableStateOf(false) }
    var extensionRepoRequestKey by remember { mutableIntStateOf(0) }
    var persistedSettings by remember { mutableStateOf(ChimahonSettings()) }
    var downloadedOnlyMode by remember { mutableStateOf(false) }
    var incognitoMode by remember { mutableStateOf(false) }
    val appScope = rememberCoroutineScope()
    val systemDarkTheme = isSystemInDarkTheme()
    val darkTheme = when (persistedSettings.appearance.themeMode) {
        ChimahonThemeMode.System -> systemDarkTheme
        ChimahonThemeMode.Light -> false
        ChimahonThemeMode.Dark -> true
    }
    ChimahonPalette.apply(
        theme = persistedSettings.appearance.appTheme,
        colorTheme = persistedSettings.appearance.colorTheme,
        dark = darkTheme,
        amoled = persistedSettings.appearance.amoled,
        fontScalePercent = persistedSettings.appearance.fontScalePercent,
    )

    LaunchedEffect(Unit) {
        runCatching { onLoadSettings() }
            .onSuccess { settings ->
                persistedSettings = settings
                downloadedOnlyMode = settings.appMode.downloadedOnly
                incognitoMode = settings.appMode.incognitoMode
            }
    }

    CompositionLocalProvider(LocalThumbnailLoader provides onLoadThumbnailImage) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(ChimahonPalette.background)
                .safeDrawingPadding(),
        ) {
            val wide = maxWidth >= 840.dp

            Row(modifier = Modifier.fillMaxSize()) {
                if (
                    wide &&
                    selectedMangaId == null &&
                    selectedSourceId == null &&
                    selectedRemoteManga == null &&
                    selectedReader == null
                ) {
                    HomeNavigationRail(
                        selected = selectedTab,
                        state = state,
                        compact = persistedSettings.appearance.compactNavigation,
                        onSelect = {
                            selectedTab = it
                            homeSearchActive = false
                            homeSearchQuery = ""
                            homeFiltersVisible = false
                            feedManageMode = false
                            migrateHelpVisible = false
                        },
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                ) {
                    val topBarMangaId = selectedMangaId
                    val topBarSourceId = selectedSourceId
                    val topBarRemoteManga = selectedRemoteManga
                    val topBarReader = selectedReader
                    if (state is ChimahonUiState.Ready && topBarReader != null) {
                        Unit
                    } else if (state is ChimahonUiState.Ready && topBarRemoteManga != null) {
                        RemoteMangaDetailTopBar(
                            title = topBarRemoteManga.title,
                            onBack = { selectedRemoteManga = null },
                        )
                    } else if (state is ChimahonUiState.Ready && topBarMangaId != null) {
                        MangaDetailTopBar(
                            title = state.snapshot.detailTitle(topBarMangaId),
                            onBack = { selectedMangaId = null },
                            onRefresh = {
                                appScope.launch {
                                    runCatching { onRefreshManga(topBarMangaId) }
                                    onRefresh()
                                }
                            },
                        )
                    } else if (state is ChimahonUiState.Ready && topBarSourceId != null) {
                        SourceDetailTopBar(
                            title = state.snapshot.sourceName(topBarSourceId),
                            onBack = {
                                selectedRemoteManga = null
                                selectedSourceId = null
                            },
                            onSearch = { sourceSearchKey++ },
                            onRefresh = onRefresh,
                        )
                    } else {
                        if (selectedTab != HomeTab.More) {
                            val toolbarActions = buildList {
                                val searchEnabled = selectedTab == HomeTab.Library ||
                                    selectedTab == HomeTab.History ||
                                    (
                                        selectedTab == HomeTab.Browse &&
                                            selectedBrowseSection in listOf(
                                                BrowseSection.Sources,
                                                BrowseSection.Extensions,
                                            )
                                        )
                                if (searchEnabled) {
                                    add(
                                        HomeToolbarAction(
                                            icon = UiIcon.Search,
                                            contentDescription = "Search",
                                            onClick = { homeSearchActive = true },
                                        ),
                                    )
                                }
                                when (selectedTab) {
                                    HomeTab.Library,
                                    HomeTab.Updates,
                                    HomeTab.History,
                                    -> add(
                                        HomeToolbarAction(
                                            icon = UiIcon.Filter,
                                            contentDescription = "Filter",
                                            active = homeFiltersVisible,
                                            onClick = { homeFiltersVisible = !homeFiltersVisible },
                                        ),
                                    )
                                    HomeTab.Browse -> when (selectedBrowseSection) {
                                        BrowseSection.Feed -> {
                                            add(
                                                HomeToolbarAction(
                                                    icon = UiIcon.Reorder,
                                                    contentDescription = "Manage feeds",
                                                    active = feedManageMode,
                                                    onClick = { feedManageMode = !feedManageMode },
                                                ),
                                            )
                                        }
                                        BrowseSection.Extensions -> {
                                            add(
                                                HomeToolbarAction(
                                                    icon = UiIcon.Filter,
                                                    contentDescription = "Filter extensions",
                                                    active = homeFiltersVisible,
                                                    onClick = { homeFiltersVisible = !homeFiltersVisible },
                                                ),
                                            )
                                            add(
                                                HomeToolbarAction(
                                                    icon = UiIcon.Extensions,
                                                    contentDescription = "Extension repositories",
                                                    onClick = { extensionRepoRequestKey++ },
                                                ),
                                            )
                                        }
                                        BrowseSection.Migrate -> add(
                                            HomeToolbarAction(
                                                icon = UiIcon.Help,
                                                contentDescription = "Migration help",
                                                active = migrateHelpVisible,
                                                onClick = { migrateHelpVisible = !migrateHelpVisible },
                                            ),
                                        )
                                        BrowseSection.Sources -> Unit
                                    }
                                    HomeTab.More -> Unit
                                }
                                if (
                                    selectedTab == HomeTab.Library ||
                                    selectedTab == HomeTab.Updates ||
                                    selectedTab == HomeTab.Browse
                                ) {
                                    add(
                                        HomeToolbarAction(
                                            icon = UiIcon.Refresh,
                                            contentDescription = "Refresh",
                                            onClick = onRefresh,
                                        ),
                                    )
                                }
                            }
                            HomeTopBar(
                                selected = selectedTab,
                                state = state,
                                searchActive = homeSearchActive,
                                searchQuery = homeSearchQuery,
                                searchPlaceholder = when {
                                    selectedTab == HomeTab.Library -> "Search library"
                                    selectedTab == HomeTab.History -> "Search history"
                                    selectedBrowseSection == BrowseSection.Extensions -> "Search extensions"
                                    else -> "Search sources"
                                },
                                onSearchActiveChange = { active ->
                                    homeSearchActive = active
                                    if (!active) homeSearchQuery = ""
                                },
                                onSearchQueryChange = { homeSearchQuery = it },
                                actions = toolbarActions,
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        when (state) {
                            ChimahonUiState.Loading -> LoadingHome()
                            is ChimahonUiState.Failed -> FailureHome(state.message, onRefresh)
                            is ChimahonUiState.Ready -> {
                                val detailMangaId = selectedMangaId
                                val detailSourceId = selectedSourceId
                                val detailRemoteManga = selectedRemoteManga
                                val readerRequest = selectedReader
                                if (readerRequest != null) {
                                    ReaderHome(
                                        request = readerRequest,
                                        initialBookmarked = readerRequest.chapterId?.let { chapterId ->
                                            readerRequest.mangaId?.let { mangaId ->
                                                state.snapshot.chaptersByMangaId[mangaId]
                                                .orEmpty()
                                                .firstOrNull { it.id == chapterId }
                                                ?.bookmarked
                                            }
                                        } == true,
                                        onLoadReaderChapter = onLoadReaderChapter,
                                        onLoadReaderPageImage = onLoadReaderPageImage,
                                        settings = persistedSettings.reader,
                                        onSettingsChange = { settings ->
                                            persistedSettings = persistedSettings.copy(reader = settings)
                                            appScope.launch {
                                                runCatching { onSaveReaderSettings(settings) }
                                            }
                                        },
                                        onSaveReaderProgress = { request, page, completed ->
                                            if (!incognitoMode) {
                                                onSaveReaderProgress(request, page, completed)
                                            }
                                        },
                                        onSetChapterRead = onSetChapterRead,
                                        onSetChapterBookmark = onSetChapterBookmark,
                                        onEnqueueDownload = onEnqueueDownload,
                                        onOpenExternalUrl = onOpenExternalUrl,
                                        onOpenReader = { selectedReader = it },
                                        onCloseReader = {
                                            selectedReader = null
                                            onRefresh()
                                        },
                                    )
                                } else if (detailRemoteManga != null) {
                                    val existingMangaId = state.snapshot.mangaDetails.values
                                        .firstOrNull {
                                            it.sourceId == detailRemoteManga.sourceId &&
                                                it.url == detailRemoteManga.url
                                        }
                                        ?.id
                                    RemoteMangaDetailHome(
                                        remoteManga = detailRemoteManga,
                                        existingMangaId = existingMangaId,
                                        onLoadRemoteMangaDetail = onLoadRemoteMangaDetail,
                                        onOpenReader = { selectedReader = it },
                                        onOpenLibraryManga = { mangaId ->
                                            selectedRemoteManga = null
                                            selectedSourceId = null
                                            selectedMangaId = mangaId
                                        },
                                        onOpenRemoteMangaUrl = onOpenRemoteMangaUrl,
                                        onAddToLibrary = { detail ->
                                            val mangaId = onAddRemoteMangaToLibrary(detail)
                                            selectedRemoteManga = null
                                            selectedSourceId = null
                                            selectedMangaId = mangaId
                                            onRefresh()
                                        },
                                    )
                                } else if (detailMangaId != null) {
                                    MangaDetailHome(
                                        snapshot = state.snapshot,
                                        mangaId = detailMangaId,
                                        onOpenReader = { selectedReader = it },
                                        onSetMangaFavorite = { mangaId, favorite ->
                                            onSetMangaFavorite(mangaId, favorite)
                                            onRefresh()
                                        },
                                        onSetMangaCategories = { mangaId, categories ->
                                            onSetMangaCategories(mangaId, categories)
                                            onRefresh()
                                        },
                                        onSetMangaNotes = { mangaId, notes ->
                                            onSetMangaNotes(mangaId, notes)
                                            onRefresh()
                                        },
                                        onSetMangaChaptersRead = { mangaId, read ->
                                            onSetMangaChaptersRead(mangaId, read)
                                            onRefresh()
                                        },
                                        onSetChapterRead = { chapterId, read ->
                                            onSetChapterRead(chapterId, read)
                                            onRefresh()
                                        },
                                        onSetChapterBookmark = { chapterId, bookmarked ->
                                            onSetChapterBookmark(chapterId, bookmarked)
                                            onRefresh()
                                        },
                                        onEnqueueDownload = onEnqueueDownload,
                                        onEnqueueDownloads = onEnqueueDownloads,
                                        onLoadDownloadSnapshot = onLoadDownloadSnapshot,
                                        onSelectBrowse = {
                                            selectedMangaId = null
                                            selectedTab = HomeTab.Browse
                                        },
                                    )
                                } else if (detailSourceId != null) {
                                    SourceDetailHome(
                                        snapshot = state.snapshot,
                                        sourceId = detailSourceId,
                                        initialMode = selectedSourceInitialMode,
                                        searchKey = sourceSearchKey,
                                        browseSettings = persistedSettings.browse,
                                        onRefresh = onRefresh,
                                        onLoadSourcePreview = onLoadSourcePreview,
                                        onOpenRemoteManga = { selectedRemoteManga = it },
                                    )
                                } else {
                                    HomeContent(
                                        selected = selectedTab,
                                        snapshot = state.snapshot,
                                        selectedBrowseSection = selectedBrowseSection,
                                        onBrowseSectionChange = {
                                            selectedBrowseSection = it
                                            homeSearchActive = false
                                            homeSearchQuery = ""
                                            homeFiltersVisible = false
                                            feedManageMode = false
                                            migrateHelpVisible = false
                                        },
                                        onSelectTab = {
                                            selectedTab = it
                                            homeSearchActive = false
                                            homeSearchQuery = ""
                                            homeFiltersVisible = false
                                            feedManageMode = false
                                            migrateHelpVisible = false
                                        },
                                        homeSearchQuery = homeSearchQuery,
                                        homeFiltersVisible = homeFiltersVisible,
                                        feedManageMode = feedManageMode,
                                        migrateHelpVisible = migrateHelpVisible,
                                        extensionRepoRequestKey = extensionRepoRequestKey,
                                        onLoadSourcePreview = onLoadSourcePreview,
                                        onLoadRemoteMangaDetail = onLoadRemoteMangaDetail,
                                        onOpenManga = { selectedMangaId = it },
                                        onOpenRemoteManga = { selectedRemoteManga = it },
                                        onOpenReader = { selectedReader = it },
                                        onOpenSource = { sourceId, mode ->
                                            selectedSourceId = sourceId
                                            selectedSourceInitialMode = mode
                                        },
                                        onAddRemoteMangaToLibrary = onAddRemoteMangaToLibrary,
                                        onSetMangaFavorite = onSetMangaFavorite,
                                        onSetMangasFavorite = onSetMangasFavorite,
                                        onCreateCategory = onCreateCategory,
                                        onDeleteCategory = onDeleteCategory,
                                        onSetMangaCategories = onSetMangaCategories,
                                        onResetHistoryEntry = onResetHistoryEntry,
                                        onResetHistoryForManga = onResetHistoryForManga,
                                        onClearHistory = onClearHistory,
                                        onDismissUpdateIssue = onDismissUpdateIssue,
                                        onClearUpdateIssues = onClearUpdateIssues,
                                        onLoadDataMaintenance = onLoadDataMaintenance,
                                        onClearThumbnailCache = onClearThumbnailCache,
                                        onClearCacheData = onClearCacheData,
                                        onRunDatabaseMaintenance = onRunDatabaseMaintenance,
                                        onSetChapterRead = onSetChapterRead,
                                        onSetChapterBookmark = onSetChapterBookmark,
                                        onSetMangasChaptersRead = onSetMangasChaptersRead,
                                        onLoadDownloadQueue = onLoadDownloadQueue,
                                        onPauseDownloadQueue = onPauseDownloadQueue,
                                        onResumeDownloadQueue = onResumeDownloadQueue,
                                        onProcessNextDownload = onProcessNextDownload,
                                        onPauseDownload = onPauseDownload,
                                        onResumeDownload = onResumeDownload,
                                        onRetryDownload = onRetryDownload,
                                        onRemoveDownload = onRemoveDownload,
                                        onClearCompletedDownloads = onClearCompletedDownloads,
                                        onMoveDownloadToTop = onMoveDownloadToTop,
                                        onMoveDownloadToBottom = onMoveDownloadToBottom,
                                        onAddExtensionRepo = onAddExtensionRepo,
                                        onDeleteExtensionRepo = onDeleteExtensionRepo,
                                        onLoadExtensionRepoCatalog = onLoadExtensionRepoCatalog,
                                        onInstallExtension = onInstallExtension,
                                        onUninstallExtension = onUninstallExtension,
                                        onRepoSaved = onRefresh,
                                        downloadedOnlyMode = downloadedOnlyMode,
                                        incognitoMode = incognitoMode,
                                        settings = persistedSettings,
                                        onAppearanceSettingsChange = { settings ->
                                            persistedSettings = persistedSettings.copy(appearance = settings)
                                            appScope.launch {
                                                runCatching { onSaveAppearanceSettings(settings) }
                                            }
                                        },
                                        onLibrarySettingsChange = { settings ->
                                            persistedSettings = persistedSettings.copy(library = settings)
                                            appScope.launch {
                                                runCatching { onSaveLibrarySettings(settings) }
                                            }
                                        },
                                        onReaderSettingsChange = { settings ->
                                            persistedSettings = persistedSettings.copy(reader = settings)
                                            appScope.launch {
                                                runCatching { onSaveReaderSettings(settings) }
                                            }
                                        },
                                        onDownloadPreferencesChange = { settings ->
                                            persistedSettings = persistedSettings.copy(downloads = settings)
                                            appScope.launch {
                                                runCatching { onSaveDownloadPreferences(settings) }
                                            }
                                        },
                                        onBrowseSettingsChange = { settings ->
                                            persistedSettings = persistedSettings.copy(browse = settings)
                                            appScope.launch {
                                                runCatching { onSaveBrowseSettings(settings) }
                                            }
                                        },
                                        onTrackingSettingsChange = { settings ->
                                            persistedSettings = persistedSettings.copy(tracking = settings)
                                            appScope.launch {
                                                runCatching { onSaveTrackingSettings(settings) }
                                            }
                                        },
                                        onConnectionSettingsChange = { settings ->
                                            persistedSettings = persistedSettings.copy(connections = settings)
                                            appScope.launch {
                                                runCatching { onSaveConnectionSettings(settings) }
                                            }
                                        },
                                        onDictionarySettingsChange = { settings ->
                                            persistedSettings = persistedSettings.copy(dictionary = settings)
                                            appScope.launch {
                                                runCatching { onSaveDictionarySettings(settings) }
                                            }
                                        },
                                        onSecuritySettingsChange = { settings ->
                                            persistedSettings = persistedSettings.copy(security = settings)
                                            appScope.launch {
                                                runCatching { onSaveSecuritySettings(settings) }
                                            }
                                        },
                                        onOpenExternalUrl = onOpenExternalUrl,
                                        onDownloadedOnlyModeChange = { enabled ->
                                            downloadedOnlyMode = enabled
                                            appScope.launch {
                                                runCatching { onSetDownloadedOnly(enabled) }
                                            }
                                        },
                                        onIncognitoModeChange = { enabled ->
                                            incognitoMode = enabled
                                            appScope.launch {
                                                runCatching { onSetIncognitoMode(enabled) }
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }

                    if (
                        !wide &&
                        selectedMangaId == null &&
                        selectedSourceId == null &&
                        selectedRemoteManga == null &&
                        selectedReader == null
                    ) {
                        HomeNavigationBar(
                            selected = selectedTab,
                            state = state,
                            compact = persistedSettings.appearance.compactNavigation,
                            onSelect = {
                                selectedTab = it
                                homeSearchActive = false
                                homeSearchQuery = ""
                                homeFiltersVisible = false
                                feedManageMode = false
                                migrateHelpVisible = false
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    selected: HomeTab,
    state: ChimahonUiState,
    searchActive: Boolean,
    searchQuery: String,
    searchPlaceholder: String,
    onSearchActiveChange: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    actions: List<HomeToolbarAction>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(ChimahonPalette.surface)
            .border(1.dp, ChimahonPalette.divider)
            .padding(start = 20.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (searchActive) {
            TopAction(
                icon = UiIcon.Back,
                contentDescription = "Close search",
                onClick = { onSearchActiveChange(false) },
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (searchQuery.isBlank()) {
                    Label(
                        text = searchPlaceholder,
                        color = ChimahonPalette.secondaryText,
                        size = 15,
                        maxLines = 1,
                    )
                }
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = ChimahonPalette.onSurface,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Label(
                    text = selected.title,
                    color = ChimahonPalette.onSurface,
                    size = 20,
                    weight = FontWeight.SemiBold,
                    maxLines = 1,
                )
                Label(
                    text = selected.subtitle(state),
                    color = ChimahonPalette.secondaryText,
                    size = 12,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            actions.forEach { action ->
                TopAction(
                    icon = action.icon,
                    contentDescription = action.contentDescription,
                    tint = if (action.active) ChimahonPalette.primary else ChimahonPalette.secondaryText,
                    onClick = action.onClick,
                )
            }
        }
    }
}

@Composable
private fun MangaDetailTopBar(
    title: String,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(ChimahonPalette.surface)
            .border(1.dp, ChimahonPalette.divider)
            .padding(start = 8.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TopAction(
            icon = UiIcon.Back,
            contentDescription = "Back",
            onClick = onBack,
        )
        Column(modifier = Modifier.weight(1f)) {
            Label(
                text = title,
                color = ChimahonPalette.onSurface,
                size = 18,
                weight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Label(
                text = "Manga details",
                color = ChimahonPalette.secondaryText,
                size = 12,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        TopAction(
            icon = UiIcon.Search,
            contentDescription = "Search",
        )
        TopAction(
            icon = UiIcon.Refresh,
            contentDescription = "Refresh",
            onClick = onRefresh,
        )
    }
}

@Composable
private fun SourceDetailTopBar(
    title: String,
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(ChimahonPalette.surface)
            .border(1.dp, ChimahonPalette.divider)
            .padding(start = 8.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TopAction(
            icon = UiIcon.Back,
            contentDescription = "Back",
            onClick = onBack,
        )
        Column(modifier = Modifier.weight(1f)) {
            Label(
                text = title,
                color = ChimahonPalette.onSurface,
                size = 18,
                weight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Label(
                text = "Source browser",
                color = ChimahonPalette.secondaryText,
                size = 12,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        TopAction(
            icon = UiIcon.Search,
            contentDescription = "Search",
            onClick = onSearch,
        )
        TopAction(
            icon = UiIcon.Refresh,
            contentDescription = "Refresh",
            onClick = onRefresh,
        )
    }
}

@Composable
private fun RemoteMangaDetailTopBar(
    title: String,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(ChimahonPalette.surface)
            .border(1.dp, ChimahonPalette.divider)
            .padding(start = 8.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TopAction(
            icon = UiIcon.Back,
            contentDescription = "Back",
            onClick = onBack,
        )
        Column(modifier = Modifier.weight(1f)) {
            Label(
                text = title,
                color = ChimahonPalette.onSurface,
                size = 18,
                weight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Label(
                text = "Source manga",
                color = ChimahonPalette.secondaryText,
                size = 12,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun ReaderTopBar(
    request: ChimahonReaderRequest,
    currentPage: Int,
    pageCount: Int,
    canvas: ReaderCanvas = ReaderCanvas.Black,
    showTitle: Boolean = true,
    bookmarked: Boolean,
    bookmarkBusy: Boolean,
    onBack: () -> Unit,
    onToggleHud: () -> Unit = {},
    onToggleBookmark: (() -> Unit)?,
    downloadBusy: Boolean = false,
    onDownloadChapter: (() -> Unit)? = null,
    readBusy: Boolean = false,
    onMarkChapterRead: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(readerHudBackground(canvas))
            .clickable(onClick = onToggleHud)
            .padding(start = 8.dp, end = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ReaderAction(
            icon = UiIcon.Back,
            contentDescription = "Back",
            tint = readerHudContent(canvas),
            onClick = onBack,
        )
        Column(modifier = Modifier.weight(1f)) {
            if (showTitle) {
                Label(
                    text = request.mangaTitle,
                    color = readerHudContent(canvas),
                    size = 16,
                    weight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
            Label(
                text = request.chapterName,
                color = readerHudContent(canvas).copy(alpha = 0.68f),
                size = if (showTitle) 11 else 16,
                weight = if (showTitle) FontWeight.Normal else FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        if (pageCount > 0) {
            Label(
                text = "$currentPage / $pageCount",
                color = readerHudContent(canvas).copy(alpha = 0.68f),
                size = 11,
                weight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
        onDownloadChapter?.let {
            ReaderAction(
                icon = UiIcon.Download,
                contentDescription = "Download chapter",
                active = downloadBusy,
                tint = readerHudContent(canvas),
                onClick = it,
            )
        }
        onMarkChapterRead?.let {
            ReaderAction(
                icon = UiIcon.DoneAll,
                contentDescription = "Mark chapter read",
                active = readBusy,
                tint = readerHudContent(canvas),
                onClick = it,
            )
        }
        onToggleBookmark?.let {
            ReaderAction(
                icon = if (bookmarked) UiIcon.Bookmark else UiIcon.BookmarkBorder,
                contentDescription = if (bookmarked) "Unbookmark chapter" else "Bookmark chapter",
                active = bookmarked || bookmarkBusy,
                tint = readerHudContent(canvas),
                onClick = it,
            )
        }
    }
}

@Composable
private fun HomeNavigationRail(
    selected: HomeTab,
    state: ChimahonUiState,
    compact: Boolean,
    onSelect: (HomeTab) -> Unit,
) {
    Column(
        modifier = Modifier
            .width(if (compact) 74.dp else 92.dp)
            .fillMaxHeight()
            .background(ChimahonPalette.surface)
            .border(1.dp, ChimahonPalette.divider)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppMark()
        Spacer(Modifier.height(if (compact) 10.dp else 18.dp))
        HomeTab.entries.forEach { tab ->
            RailItem(
                tab = tab,
                selected = tab == selected,
                badge = state.badgeFor(tab),
                compact = compact,
                onClick = { onSelect(tab) },
            )
        }
    }
}

@Composable
private fun RailItem(
    tab: HomeTab,
    selected: Boolean,
    badge: Int?,
    compact: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(if (compact) 64.dp else 76.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = if (compact) 6.dp else 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NavIcon(
            icon = tab.icon,
            contentDescription = tab.title,
            selected = selected,
            badge = badge,
        )
        Label(
            text = tab.title,
            color = if (selected) ChimahonPalette.primary else ChimahonPalette.secondaryText,
            size = if (compact) 9 else 11,
            weight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            modifier = Modifier.padding(top = if (compact) 4.dp else 5.dp),
        )
    }
}

@Composable
private fun HomeNavigationBar(
    selected: HomeTab,
    state: ChimahonUiState,
    compact: Boolean,
    onSelect: (HomeTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (compact) 58.dp else 72.dp)
            .background(ChimahonPalette.surface)
            .border(1.dp, ChimahonPalette.divider)
            .padding(horizontal = 6.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        HomeTab.entries.forEach { tab ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onSelect(tab) }
                    .padding(vertical = if (compact) 2.dp else 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                NavIcon(
                    icon = tab.icon,
                    contentDescription = tab.title,
                    selected = tab == selected,
                    badge = state.badgeFor(tab),
                )
                Label(
                    text = tab.title,
                    color = if (tab == selected) ChimahonPalette.primary else ChimahonPalette.secondaryText,
                    size = if (compact) 9 else 10,
                    weight = if (tab == selected) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
        }
    }
}

@Composable
private fun NavIcon(
    icon: UiIcon,
    contentDescription: String,
    selected: Boolean,
    badge: Int?,
) {
    Box(
        modifier = Modifier.size(width = 54.dp, height = 34.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(46.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(if (selected) ChimahonPalette.primaryContainer else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            IconGlyph(
                icon = icon,
                contentDescription = contentDescription,
                tint = if (selected) ChimahonPalette.primary else ChimahonPalette.secondaryText,
                modifier = Modifier.size(if (selected) 22.dp else 21.dp),
            )
        }
        if (badge != null && badge > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(17.dp)
                    .clip(CircleShape)
                    .background(ChimahonPalette.error),
                contentAlignment = Alignment.Center,
            ) {
                Label(
                    text = badge.coerceAtMost(99).toString(),
                    color = Color.White,
                    size = 8,
                    weight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    selected: HomeTab,
    snapshot: ChimahonSnapshot,
    selectedBrowseSection: BrowseSection,
    onBrowseSectionChange: (BrowseSection) -> Unit,
    onSelectTab: (HomeTab) -> Unit,
    homeSearchQuery: String,
    homeFiltersVisible: Boolean,
    feedManageMode: Boolean,
    migrateHelpVisible: Boolean,
    extensionRepoRequestKey: Int,
    onLoadSourcePreview: suspend (Long, ChimahonSourceBrowseMode, String, Int) -> ChimahonSourcePreview,
    onLoadRemoteMangaDetail: suspend (ChimahonRemoteMangaEntry) -> ChimahonRemoteMangaDetail,
    onOpenManga: (Long) -> Unit,
    onOpenRemoteManga: (ChimahonRemoteMangaEntry) -> Unit,
    onOpenReader: (ChimahonReaderRequest) -> Unit,
    onOpenSource: (Long, ChimahonSourceBrowseMode) -> Unit,
    onAddRemoteMangaToLibrary: suspend (ChimahonRemoteMangaDetail) -> Long,
    onSetMangaFavorite: suspend (Long, Boolean) -> Unit,
    onSetMangasFavorite: suspend (Set<Long>, Boolean) -> ChimahonLibraryBulkActionResult,
    onCreateCategory: suspend (String) -> Long,
    onDeleteCategory: suspend (Long) -> Unit,
    onSetMangaCategories: suspend (Long, Set<Long>) -> Unit,
    onResetHistoryEntry: suspend (Long) -> Unit,
    onResetHistoryForManga: suspend (Long) -> Unit,
    onClearHistory: suspend () -> Unit,
    onDismissUpdateIssue: suspend (Long) -> Unit,
    onClearUpdateIssues: suspend () -> Unit,
    onLoadDataMaintenance: suspend () -> ChimahonDataMaintenanceSnapshot,
    onClearThumbnailCache: suspend () -> ChimahonCacheClearResult,
    onClearCacheData: suspend () -> ChimahonCacheClearResult,
    onRunDatabaseMaintenance: suspend () -> ChimahonDatabaseMaintenanceResult,
    onSetChapterRead: suspend (Long, Boolean) -> Unit,
    onSetChapterBookmark: suspend (Long, Boolean) -> Unit,
    onSetMangasChaptersRead: suspend (Set<Long>, Boolean) -> ChimahonLibraryBulkActionResult,
    onLoadDownloadQueue: suspend () -> ChimahonDownloadQueueData,
    onPauseDownloadQueue: suspend () -> ChimahonDownloadQueueData,
    onResumeDownloadQueue: suspend () -> ChimahonDownloadQueueData,
    onProcessNextDownload: suspend () -> ChimahonDownloadQueueData,
    onPauseDownload: suspend (Long) -> ChimahonDownloadQueueData,
    onResumeDownload: suspend (Long) -> ChimahonDownloadQueueData,
    onRetryDownload: suspend (Long) -> ChimahonDownloadQueueData,
    onRemoveDownload: suspend (Long) -> ChimahonDownloadQueueData,
    onClearCompletedDownloads: suspend () -> ChimahonDownloadQueueData,
    onMoveDownloadToTop: suspend (Long) -> ChimahonDownloadQueueData,
    onMoveDownloadToBottom: suspend (Long) -> ChimahonDownloadQueueData,
    onAddExtensionRepo: suspend (String) -> ChimahonExtensionRepoEntry,
    onDeleteExtensionRepo: suspend (String) -> Unit,
    onLoadExtensionRepoCatalog: suspend (ChimahonExtensionRepoEntry) -> ChimahonExtensionRepoCatalog,
    onInstallExtension: suspend (ChimahonRepoExtensionEntry) -> ChimahonInstalledExtensionEntry,
    onUninstallExtension: suspend (String, ChimahonExtensionPackageType) -> ChimahonExtensionUninstallResult,
    onRepoSaved: () -> Unit,
    downloadedOnlyMode: Boolean,
    incognitoMode: Boolean,
    settings: ChimahonSettings,
    onAppearanceSettingsChange: (ChimahonAppearanceSettings) -> Unit,
    onLibrarySettingsChange: (ChimahonLibrarySettings) -> Unit,
    onReaderSettingsChange: (ChimahonReaderSettings) -> Unit,
    onDownloadPreferencesChange: (ChimahonDownloadPreferences) -> Unit,
    onBrowseSettingsChange: (ChimahonBrowseSettings) -> Unit,
    onTrackingSettingsChange: (ChimahonTrackingSettings) -> Unit,
    onConnectionSettingsChange: (ChimahonConnectionSettings) -> Unit,
    onDictionarySettingsChange: (ChimahonDictionarySettings) -> Unit,
    onSecuritySettingsChange: (ChimahonSecuritySettings) -> Unit,
    onOpenExternalUrl: (String) -> Boolean,
    onDownloadedOnlyModeChange: (Boolean) -> Unit,
    onIncognitoModeChange: (Boolean) -> Unit,
) {
    when (selected) {
        HomeTab.Library -> LibraryHome(
            snapshot = snapshot,
            query = homeSearchQuery,
            filtersVisible = homeFiltersVisible,
            onBrowseClick = { onSelectTab(HomeTab.Browse) },
            onOpenManga = onOpenManga,
            onOpenReader = onOpenReader,
            onSetMangasFavorite = onSetMangasFavorite,
            onSetMangasChaptersRead = onSetMangasChaptersRead,
            onRefresh = onRepoSaved,
            settings = settings.library,
            onSettingsChange = onLibrarySettingsChange,
        )
        HomeTab.Updates -> UpdatesHome(
            snapshot = snapshot,
            filtersVisible = homeFiltersVisible,
            onOpenManga = onOpenManga,
            onOpenReader = onOpenReader,
            onSetChapterRead = onSetChapterRead,
            onSetChapterBookmark = onSetChapterBookmark,
            onDismissUpdateIssue = onDismissUpdateIssue,
            onClearUpdateIssues = onClearUpdateIssues,
            onRefresh = onRepoSaved,
        )
        HomeTab.History -> HistoryHome(
            snapshot = snapshot,
            query = homeSearchQuery,
            filtersVisible = homeFiltersVisible,
            onOpenManga = onOpenManga,
            onOpenReader = onOpenReader,
            onResetHistoryEntry = onResetHistoryEntry,
            onResetHistoryForManga = onResetHistoryForManga,
            onClearHistory = onClearHistory,
            onRefresh = onRepoSaved,
        )
        HomeTab.Browse -> BrowseHome(
            snapshot = snapshot,
            selectedSection = selectedBrowseSection,
            query = homeSearchQuery,
            filtersVisible = homeFiltersVisible,
            feedManageMode = feedManageMode,
            migrateHelpVisible = migrateHelpVisible,
            extensionRepoRequestKey = extensionRepoRequestKey,
            onSectionChange = onBrowseSectionChange,
            onOpenManga = onOpenManga,
            onLoadSourcePreview = onLoadSourcePreview,
            onLoadRemoteMangaDetail = onLoadRemoteMangaDetail,
            onOpenRemoteManga = onOpenRemoteManga,
            onOpenSource = onOpenSource,
            onAddRemoteMangaToLibrary = onAddRemoteMangaToLibrary,
            onSetMangaFavorite = onSetMangaFavorite,
            browseSettings = settings.browse,
            onAddExtensionRepo = onAddExtensionRepo,
            onDeleteExtensionRepo = onDeleteExtensionRepo,
            onLoadExtensionRepoCatalog = onLoadExtensionRepoCatalog,
            onInstallExtension = onInstallExtension,
            onUninstallExtension = onUninstallExtension,
            onRepoSaved = onRepoSaved,
        )
        HomeTab.More -> MoreHome(
            snapshot = snapshot,
            onSelectTab = onSelectTab,
            onCreateCategory = onCreateCategory,
            onDeleteCategory = onDeleteCategory,
            onCategoriesChanged = onRepoSaved,
            onLoadDataMaintenance = onLoadDataMaintenance,
            onClearThumbnailCache = onClearThumbnailCache,
            onClearCacheData = onClearCacheData,
            onRunDatabaseMaintenance = onRunDatabaseMaintenance,
            onLoadDownloadQueue = onLoadDownloadQueue,
            onPauseDownloadQueue = onPauseDownloadQueue,
            onResumeDownloadQueue = onResumeDownloadQueue,
            onProcessNextDownload = onProcessNextDownload,
            onPauseDownload = onPauseDownload,
            onResumeDownload = onResumeDownload,
            onRetryDownload = onRetryDownload,
            onRemoveDownload = onRemoveDownload,
            onClearCompletedDownloads = onClearCompletedDownloads,
            onMoveDownloadToTop = onMoveDownloadToTop,
            onMoveDownloadToBottom = onMoveDownloadToBottom,
            downloadedOnlyMode = downloadedOnlyMode,
            incognitoMode = incognitoMode,
            settings = settings,
            onAppearanceSettingsChange = onAppearanceSettingsChange,
            onLibrarySettingsChange = onLibrarySettingsChange,
            onReaderSettingsChange = onReaderSettingsChange,
            onDownloadPreferencesChange = onDownloadPreferencesChange,
            onBrowseSettingsChange = onBrowseSettingsChange,
            onTrackingSettingsChange = onTrackingSettingsChange,
            onConnectionSettingsChange = onConnectionSettingsChange,
            onDictionarySettingsChange = onDictionarySettingsChange,
            onSecuritySettingsChange = onSecuritySettingsChange,
            onOpenExternalUrl = onOpenExternalUrl,
            onDownloadedOnlyModeChange = onDownloadedOnlyModeChange,
            onIncognitoModeChange = onIncognitoModeChange,
            onOpenBrowseSection = { section ->
                onBrowseSectionChange(section)
                onSelectTab(HomeTab.Browse)
            },
        )
    }
}

@Composable
private fun LibraryHome(
    snapshot: ChimahonSnapshot,
    query: String,
    filtersVisible: Boolean,
    onBrowseClick: () -> Unit,
    onOpenManga: (Long) -> Unit,
    onOpenReader: (ChimahonReaderRequest) -> Unit,
    onSetMangasFavorite: suspend (Set<Long>, Boolean) -> ChimahonLibraryBulkActionResult,
    onSetMangasChaptersRead: suspend (Set<Long>, Boolean) -> ChimahonLibraryBulkActionResult,
    onRefresh: () -> Unit,
    settings: ChimahonLibrarySettings,
    onSettingsChange: (ChimahonLibrarySettings) -> Unit,
) {
    val library = snapshot.library
    if (library.isEmpty()) {
        EmptyMobileState(
            marker = "L",
            icon = UiIcon.Library,
            title = "Your library is empty",
            detail = "Add manga from Browse and they will appear here in the familiar library grid.",
            action = "Browse sources",
            onAction = onBrowseClick,
        )
        return
    }

    var selectedCategoryId by remember { mutableStateOf(ALL_LIBRARY_CATEGORY_ID) }
    val categories = snapshot.libraryCategoryTabs()
    val categoryKey = categories.joinToString(separator = ",") { it.id.toString() }
    LaunchedEffect(categoryKey) {
        if (categories.none { it.id == selectedCategoryId }) {
            selectedCategoryId = ALL_LIBRARY_CATEGORY_ID
        }
    }
    var selectedFilter by remember(settings.downloadedFilter, settings.unreadFilter, settings.startedFilter, settings.bookmarkedFilter) {
        mutableStateOf(settings.toInitialLibraryFilter())
    }
    var selectedSort by remember(settings.sort) {
        mutableStateOf(settings.sort.toUiLibrarySort())
    }
    var displayMode by remember(settings.displayMode) {
        mutableStateOf(settings.displayMode.toUiLibraryDisplayMode())
    }
    val selectedMangaIds = remember { mutableStateMapOf<Long, Boolean>() }
    var bulkMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val selectedCategory = categories.firstOrNull { it.id == selectedCategoryId } ?: categories.first()
    val categoryLibrary = snapshot.libraryForCategory(selectedCategory.id)
    val selectedLibrary = categoryLibrary
        .filter { entry ->
            query.isBlank() ||
                entry.title.contains(query, ignoreCase = true) ||
                entry.author.orEmpty().contains(query, ignoreCase = true) ||
                entry.artist.orEmpty().contains(query, ignoreCase = true)
        }
        .filter { entry ->
            val chapters = snapshot.chaptersByMangaId[entry.id].orEmpty()
            settings.unreadFilter.matches(chapters.any { !it.read }) &&
                settings.startedFilter.matches(chapters.any { it.lastPageRead > 0L && !it.read }) &&
                settings.bookmarkedFilter.matches(chapters.any { it.bookmarked }) &&
                settings.completedFilter.matches(chapters.isNotEmpty() && chapters.all { it.read })
        }
        .filter { entry ->
            val chapters = snapshot.chaptersByMangaId[entry.id].orEmpty()
            when (selectedFilter) {
                LibraryFilter.All -> true
                LibraryFilter.Unread -> chapters.any { !it.read }
                LibraryFilter.Started -> chapters.any { it.lastPageRead > 0L && !it.read }
                LibraryFilter.Bookmarked -> chapters.any { it.bookmarked }
            }
        }
        .let { entries ->
            val sorted = when (selectedSort) {
                LibrarySort.Alphabetical -> entries.sortedBy { it.title.lowercase() }
                LibrarySort.DateAdded -> entries.sortedByDescending { it.dateAdded }
                LibrarySort.LastUpdated -> entries.sortedByDescending { it.lastUpdate ?: 0L }
                LibrarySort.UnreadCount -> entries.sortedByDescending { entry ->
                    snapshot.chaptersByMangaId[entry.id].orEmpty().count { !it.read }
                }
            }
            if (settings.sortAscending) sorted else sorted.asReversed()
        }
    val categoryCounts = categories.associate { category ->
        category.id to snapshot.libraryForCategory(category.id).size
    }
    LaunchedEffect(selectedLibrary.map { it.id }.joinToString()) {
        selectedMangaIds.keys
            .filterNot { mangaId -> selectedLibrary.any { it.id == mangaId } }
            .forEach(selectedMangaIds::remove)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (settings.showCategoryTabs) {
            LibraryCategoryTabs(
                categories = categories,
                selectedCategoryId = selectedCategory.id,
                categoryCounts = categoryCounts,
                onSelectCategory = { selectedCategoryId = it },
            )
        }
        if (filtersVisible) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ChimahonPalette.surface)
                    .padding(vertical = 8.dp),
            ) {
                Label(
                    "Filter",
                    ChimahonPalette.secondaryText,
                    11,
                    weight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
                FilterChips(
                    chips = LibraryFilter.entries.map { it.title },
                    selected = selectedFilter.title,
                    onSelect = { selected ->
                        selectedFilter = LibraryFilter.entries.firstOrNull { it.title == selected } ?: LibraryFilter.All
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                )
                Label(
                    "Sort",
                    ChimahonPalette.secondaryText,
                    11,
                    weight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 4.dp),
                )
                FilterChips(
                    chips = LibrarySort.entries.map { it.title },
                    selected = selectedSort.title,
                    onSelect = { selected ->
                        selectedSort = LibrarySort.entries.firstOrNull { it.title == selected }
                            ?: LibrarySort.Alphabetical
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                )
                Label(
                    "Display",
                    ChimahonPalette.secondaryText,
                    11,
                    weight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 4.dp),
                )
                FilterChips(
                    chips = LibraryDisplayMode.entries.map { it.title },
                    selected = displayMode.title,
                    onSelect = { selected ->
                        displayMode = LibraryDisplayMode.entries.firstOrNull { it.title == selected }
                            ?: LibraryDisplayMode.ComfortableGrid
                        onSettingsChange(
                            settings.copy(displayMode = displayMode.toSettingsLibraryDisplayMode()),
                        )
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                )
            }
        }
        if (selectedLibrary.isNotEmpty()) {
            MultiSelectionBar(
                selectedCount = selectedMangaIds.count { it.value },
                totalCount = selectedLibrary.size,
                onSelectAll = {
                    selectedLibrary.forEach { selectedMangaIds[it.id] = true }
                },
                onInvert = {
                    selectedLibrary.forEach { manga ->
                        selectedMangaIds[manga.id] = selectedMangaIds[manga.id] != true
                    }
                },
                onClear = {
                    selectedMangaIds.clear()
                    bulkMessage = null
                },
                actions = listOf(
                    SelectionAction("Favorite", UiIcon.Favorite) {
                        val selected = selectedMangaIds.filterValues { it }.keys
                        if (selected.isNotEmpty()) {
                            scope.launch {
                                runCatching { onSetMangasFavorite(selected, true) }
                                    .onSuccess { bulkMessage = it.libraryBulkSummary("favorited") }
                                    .onFailure { bulkMessage = it.message ?: "Could not update favorites" }
                                selectedMangaIds.clear()
                                onRefresh()
                            }
                        }
                    },
                    SelectionAction("Unfavorite", UiIcon.FavoriteBorder) {
                        val selected = selectedMangaIds.filterValues { it }.keys
                        if (selected.isNotEmpty()) {
                            scope.launch {
                                runCatching { onSetMangasFavorite(selected, false) }
                                    .onSuccess { bulkMessage = it.libraryBulkSummary("unfavorited") }
                                    .onFailure { bulkMessage = it.message ?: "Could not update favorites" }
                                selectedMangaIds.clear()
                                onRefresh()
                            }
                        }
                    },
                    SelectionAction("Read", UiIcon.DoneAll) {
                        val selected = selectedMangaIds.filterValues { it }.keys
                        if (selected.isNotEmpty()) {
                            scope.launch {
                                runCatching { onSetMangasChaptersRead(selected, true) }
                                    .onSuccess { bulkMessage = it.libraryBulkSummary("marked read") }
                                    .onFailure { bulkMessage = it.message ?: "Could not mark chapters read" }
                                selectedMangaIds.clear()
                                onRefresh()
                            }
                        }
                    },
                    SelectionAction("Unread", UiIcon.Circle) {
                        val selected = selectedMangaIds.filterValues { it }.keys
                        if (selected.isNotEmpty()) {
                            scope.launch {
                                runCatching { onSetMangasChaptersRead(selected, false) }
                                    .onSuccess { bulkMessage = it.libraryBulkSummary("marked unread") }
                                    .onFailure { bulkMessage = it.message ?: "Could not mark chapters unread" }
                                selectedMangaIds.clear()
                                onRefresh()
                            }
                        }
                    },
                ),
            )
        }
        bulkMessage?.let { message ->
            ExtensionStatusRow(
                title = if (message.startsWith("Could not")) "Library action failed" else "Library action complete",
                subtitle = message,
                error = message.startsWith("Could not"),
            )
        }
        if (selectedLibrary.isEmpty()) {
            EmptyListPanel(
                marker = "L",
                title = "No matching manga",
                detail = "Try another category, filter, or search term.",
            )
        } else if (displayMode == LibraryDisplayMode.List) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 4.dp),
            ) {
                items(selectedLibrary, key = { it.id }) { entry ->
                    val chapters = snapshot.chaptersByMangaId[entry.id].orEmpty()
                    val continueChapter = chapters.nextReadableChapter()
                    LibraryMangaListItem(
                        entry = entry,
                        chapterCount = chapters.size,
                        unreadCount = chapters.count { !it.read }.takeIf { settings.showUnreadBadges } ?: 0,
                        selected = selectedMangaIds[entry.id] == true,
                        onToggleSelected = {
                            selectedMangaIds[entry.id] = selectedMangaIds[entry.id] != true
                            bulkMessage = null
                        },
                        onClick = {
                            if (selectedMangaIds.any { it.value }) {
                                selectedMangaIds[entry.id] = selectedMangaIds[entry.id] != true
                            } else {
                                onOpenManga(entry.id)
                            }
                        },
                        onContinue = continueChapter?.takeIf { settings.showContinueButtons }?.let { chapter ->
                            { onOpenReader(chapter.toReaderRequest(entry, chapters)) }
                        },
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = if (settings.gridColumnsLandscape > 0) {
                    GridCells.Fixed(settings.gridColumnsLandscape.coerceIn(2, 8))
                } else {
                    GridCells.Adaptive(
                        if (displayMode == LibraryDisplayMode.CompactGrid) 92.dp else 124.dp,
                    )
                },
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(selectedLibrary, key = { it.id }) { entry ->
                    val chapters = snapshot.chaptersByMangaId[entry.id].orEmpty()
                    val continueChapter = chapters.nextReadableChapter()
                    LibraryMangaCard(
                        entry = entry,
                        chapterCount = chapters.size,
                        unreadCount = chapters.count { !it.read }.takeIf { settings.showUnreadBadges } ?: 0,
                        selected = selectedMangaIds[entry.id] == true,
                        onToggleSelected = {
                            selectedMangaIds[entry.id] = selectedMangaIds[entry.id] != true
                            bulkMessage = null
                        },
                        onClick = {
                            if (selectedMangaIds.any { it.value }) {
                                selectedMangaIds[entry.id] = selectedMangaIds[entry.id] != true
                            } else {
                                onOpenManga(entry.id)
                            }
                        },
                        onContinue = continueChapter?.takeIf { settings.showContinueButtons }?.let { chapter ->
                            { onOpenReader(chapter.toReaderRequest(entry, chapters)) }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryMangaListItem(
    entry: ChimahonMangaEntry,
    chapterCount: Int,
    unreadCount: Int,
    selected: Boolean,
    onToggleSelected: () -> Unit,
    onClick: () -> Unit,
    onContinue: (() -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .background(ChimahonPalette.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MangaCoverTile(
            manga = entry,
            showStatus = false,
            showLibraryBadge = false,
            modifier = Modifier
                .width(52.dp)
                .fillMaxHeight(),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
        ) {
            Label(entry.title, ChimahonPalette.onSurface, 14, weight = FontWeight.SemiBold, maxLines = 2)
            Label(
                entry.author ?: entry.artist ?: entry.status,
                ChimahonPalette.secondaryText,
                11,
                maxLines = 1,
                modifier = Modifier.padding(top = 4.dp),
            )
            Label(
                "$chapterCount chapters",
                ChimahonPalette.secondaryText,
                10,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ChimahonPalette.primaryContainer)
                    .padding(horizontal = 9.dp, vertical = 5.dp),
            ) {
                Label(
                    unreadCount.coerceAtMost(999).toString(),
                    ChimahonPalette.primary,
                    11,
                    weight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
        }
        ChapterQuickAction(
            icon = if (selected) UiIcon.CheckCircle else UiIcon.Circle,
            contentDescription = if (selected) "Deselect manga" else "Select manga",
            active = selected,
            onClick = onToggleSelected,
        )
        onContinue?.let {
            ChapterQuickAction(
                icon = UiIcon.Play,
                contentDescription = "Continue reading",
                active = false,
                onClick = it,
            )
        }
    }
}

@Composable
private fun LibrarySummaryRow(
    snapshot: ChimahonSnapshot,
    selectedCategory: ChimahonLibraryCategory,
    categoryLibrary: List<ChimahonMangaEntry>,
    selectedLibrary: List<ChimahonMangaEntry>,
) {
    val unread = selectedLibrary.sumOf { entry ->
        snapshot.chaptersByMangaId[entry.id].orEmpty().count { !it.read }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Label(selectedCategory.displayName(), ChimahonPalette.onSurface, 15, weight = FontWeight.SemiBold, maxLines = 1)
            Label(
                "${selectedLibrary.size}/${categoryLibrary.size} manga - $unread unread chapter(s)",
                ChimahonPalette.secondaryText,
                12,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun LibrarySearchField(
    query: String,
    resultCount: Int,
    onQueryChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ChimahonPalette.surface)
            .border(1.dp, ChimahonPalette.divider, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (query.isBlank()) {
                Label("Search library", ChimahonPalette.secondaryText, 13, maxLines = 1)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = ChimahonPalette.onSurface,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Label(resultCount.toString(), ChimahonPalette.secondaryText, 12, maxLines = 1, modifier = Modifier.padding(start = 12.dp))
    }
}

@Composable
private fun LibraryCategoryTabs(
    categories: List<ChimahonLibraryCategory>,
    selectedCategoryId: Long,
    categoryCounts: Map<Long, Int>,
    onSelectCategory: (Long) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ChimahonPalette.divider),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(categories, key = { it.id }) { category ->
            val isSelected = category.id == selectedCategoryId
            Column(
                modifier = Modifier
                    .clickable { onSelectCategory(category.id) }
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .height(46.dp)
                        .padding(horizontal = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    Label(
                        text = category.displayName(),
                        color = if (isSelected) ChimahonPalette.primary else ChimahonPalette.onSurface,
                        size = 13,
                        weight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(ChimahonPalette.surfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Label(
                            text = (categoryCounts[category.id] ?: 0).toString(),
                            color = ChimahonPalette.secondaryText,
                            size = 10,
                            maxLines = 1,
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .height(3.dp)
                        .fillMaxWidth()
                        .background(if (isSelected) ChimahonPalette.primary else Color.Transparent),
                )
            }
        }
    }
}

@Composable
private fun LibraryMangaCard(
    entry: ChimahonMangaEntry,
    chapterCount: Int,
    unreadCount: Int,
    selected: Boolean,
    onToggleSelected: () -> Unit,
    onClick: () -> Unit,
    onContinue: (() -> Unit)?,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
    ) {
        MangaCoverTile(
            manga = entry,
            topStartLabel = unreadCount.takeIf { it > 0 }?.let { "$it unread" },
            showStatus = false,
            showLibraryBadge = selected,
            bottomEndAction = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.68f),
        )
        Row(
            modifier = Modifier.padding(top = 7.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Label(
                text = entry.title,
                color = ChimahonPalette.onSurface,
                size = 13,
                weight = FontWeight.SemiBold,
                maxLines = 2,
                modifier = Modifier.weight(1f),
            )
            ChapterQuickAction(
                icon = if (selected) UiIcon.CheckCircle else UiIcon.Circle,
                contentDescription = if (selected) "Deselect manga" else "Select manga",
                active = selected,
                onClick = onToggleSelected,
            )
        }
        Label(
            text = if (chapterCount > 0) "$chapterCount chapters" else entry.author ?: "Unknown author",
            color = ChimahonPalette.secondaryText,
            size = 11,
            maxLines = 1,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun UpdatesHome(
    snapshot: ChimahonSnapshot,
    filtersVisible: Boolean,
    onOpenManga: (Long) -> Unit,
    onOpenReader: (ChimahonReaderRequest) -> Unit,
    onSetChapterRead: suspend (Long, Boolean) -> Unit,
    onSetChapterBookmark: suspend (Long, Boolean) -> Unit,
    onDismissUpdateIssue: suspend (Long) -> Unit,
    onClearUpdateIssues: suspend () -> Unit,
    onRefresh: () -> Unit,
) {
    var selectedFilter by remember { mutableStateOf(UpdatesFilter.All) }
    val selectedChapterIds = remember { mutableStateMapOf<Long, Boolean>() }
    val scope = rememberCoroutineScope()
    val updates = snapshot.updates.filter { update ->
        when (selectedFilter) {
            UpdatesFilter.All -> true
            UpdatesFilter.Unread -> !update.read
            UpdatesFilter.Bookmarked -> update.bookmarked
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        if (filtersVisible) {
            item {
                FilterChips(
                    chips = UpdatesFilter.entries.map { it.title },
                    selected = selectedFilter.title,
                    onSelect = { title ->
                        selectedFilter = UpdatesFilter.entries.firstOrNull { it.title == title } ?: UpdatesFilter.All
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
        }
        if (updates.isNotEmpty()) {
            item {
                MultiSelectionBar(
                    selectedCount = selectedChapterIds.count { it.value },
                    totalCount = updates.size,
                    onSelectAll = {
                        updates.forEach { selectedChapterIds[it.chapterId] = true }
                    },
                    onInvert = {
                        updates.forEach { update ->
                            selectedChapterIds[update.chapterId] = selectedChapterIds[update.chapterId] != true
                        }
                    },
                    onClear = { selectedChapterIds.clear() },
                    actions = listOf(
                        SelectionAction("Read", UiIcon.DoneAll) {
                            val selected = updates.filter { selectedChapterIds[it.chapterId] == true }
                            scope.launch {
                                selected.forEach { update ->
                                    runCatching { onSetChapterRead(update.chapterId, true) }
                                }
                                selectedChapterIds.clear()
                                onRefresh()
                            }
                        },
                        SelectionAction("Bookmark", UiIcon.Bookmark) {
                            val selected = updates.filter { selectedChapterIds[it.chapterId] == true }
                            scope.launch {
                                selected.forEach { update ->
                                    runCatching { onSetChapterBookmark(update.chapterId, true) }
                                }
                                selectedChapterIds.clear()
                                onRefresh()
                            }
                        },
                    ),
                )
            }
        }
        if (updates.isEmpty()) {
            item {
                EmptyMobileState(
                    marker = "U",
                    icon = UiIcon.Updates,
                    title = if (snapshot.updates.isEmpty()) "No recent updates" else "No matching updates",
                    detail = if (snapshot.updates.isEmpty()) {
                        "Chapter updates from favorited manga will appear here from the shared database."
                    } else {
                        "Change the update filter to show more chapters."
                    },
                )
            }
        } else {
            updates.groupBy { it.dateFetch.toDateBucket("Fetched") }.forEach { (date, dateUpdates) ->
                item {
                    ListGroupHeader(date)
                }
                dateUpdates.groupBy { it.mangaId }.forEach { (mangaId, mangaUpdates) ->
                    mangaUpdates.forEachIndexed { index, update ->
                        item(key = "${update.mangaId}:${update.chapterId}") {
                            val manga = snapshot.mangaDetails[mangaId]
                            val chapter = snapshot.chaptersByMangaId[mangaId]
                                .orEmpty()
                                .firstOrNull { it.id == update.chapterId }
                            RecentUpdateListItem(
                                update = update,
                                manga = manga,
                                isLeader = index == 0,
                                selected = selectedChapterIds[update.chapterId] == true,
                                onToggleSelected = {
                                    selectedChapterIds[update.chapterId] = selectedChapterIds[update.chapterId] != true
                                },
                                onOpenManga = { onOpenManga(update.mangaId) },
                                onOpenChapter = if (manga != null && chapter != null) {
                                    {
                                        onOpenReader(
                                            chapter.toReaderRequest(
                                                manga,
                                                snapshot.chaptersByMangaId[mangaId].orEmpty(),
                                            ),
                                        )
                                    }
                                } else {
                                    null
                                },
                                onSetRead = { read -> onSetChapterRead(update.chapterId, read) },
                                onSetBookmark = { bookmarked ->
                                    onSetChapterBookmark(update.chapterId, bookmarked)
                                },
                            )
                        }
                    }
                }
            }
        }
        if (snapshot.updateIssues.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ChimahonPalette.background)
                        .padding(start = 16.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Label(
                        "Library update issues",
                        ChimahonPalette.secondaryText,
                        12,
                        weight = FontWeight.SemiBold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                    )
                    TextButtonLike("Clear", onClick = {
                        scope.launch {
                            runCatching { onClearUpdateIssues() }
                            onRefresh()
                        }
                    })
                }
            }
            items(snapshot.updateIssues) { issue ->
                UpdateIssueListItem(
                    issue = issue,
                    onClick = { onOpenManga(issue.mangaId) },
                    onDismiss = {
                        scope.launch {
                            runCatching { onDismissUpdateIssue(issue.id) }
                            onRefresh()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun RecentUpdateListItem(
    update: ChimahonRecentUpdateEntry,
    manga: ChimahonMangaEntry?,
    isLeader: Boolean,
    selected: Boolean,
    onToggleSelected: () -> Unit,
    onOpenManga: () -> Unit,
    onOpenChapter: (() -> Unit)?,
    onSetRead: suspend (Boolean) -> Unit,
    onSetBookmark: suspend (Boolean) -> Unit,
) {
    var busyAction by remember(update.chapterId) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .clickable(onClick = onOpenChapter ?: onOpenManga)
            .padding(
                start = 16.dp,
                top = if (isLeader) 12.dp else 4.dp,
                end = 8.dp,
                bottom = if (isLeader) 8.dp else 6.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isLeader) {
            Box(
                modifier = Modifier
                .width(48.dp)
                .height(72.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onOpenManga),
            ) {
                ThumbnailArtwork(
                    url = manga?.thumbnailUrl,
                    title = update.mangaTitle,
                    fallbackColor = coverColor(update.mangaId, update.mangaTitle),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        } else {
            Spacer(Modifier.width(48.dp))
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            if (isLeader) {
                Label(
                    text = update.mangaTitle,
                    color = if (update.read) ChimahonPalette.secondaryText else ChimahonPalette.onSurface,
                    size = 13,
                    maxLines = 1,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!update.read) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(ChimahonPalette.primary),
                    )
                    Spacer(Modifier.width(6.dp))
                }
                if (update.bookmarked) {
                    IconGlyph(
                        icon = UiIcon.Bookmark,
                        contentDescription = "Bookmarked",
                        tint = ChimahonPalette.primary,
                        modifier = Modifier.size(15.dp),
                    )
                    Spacer(Modifier.width(5.dp))
                }
                Label(
                    text = buildString {
                        append(update.chapterName)
                        if (update.lastPageRead > 0L) append("  \u00b7  Page ${update.lastPageRead + 1}")
                    },
                    color = if (update.read) ChimahonPalette.secondaryText else ChimahonPalette.onSurface,
                    size = 11,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        ChapterQuickAction(
            icon = if (selected) UiIcon.CheckCircle else UiIcon.Circle,
            contentDescription = if (selected) "Deselect chapter" else "Select chapter",
            active = selected,
            onClick = onToggleSelected,
        )
        ChapterQuickAction(
            icon = if (update.read) UiIcon.Circle else UiIcon.CheckCircle,
            contentDescription = if (update.read) "Mark unread" else "Mark read",
            active = busyAction == "read",
            onClick = {
                if (busyAction == null) {
                    scope.launch {
                        busyAction = "read"
                        runCatching { onSetRead(!update.read) }
                        busyAction = null
                    }
                }
            },
        )
        ChapterQuickAction(
            icon = if (update.bookmarked) UiIcon.Bookmark else UiIcon.BookmarkBorder,
            contentDescription = if (update.bookmarked) "Unbookmark" else "Bookmark",
            active = update.bookmarked || busyAction == "bookmark",
            onClick = {
                if (busyAction == null) {
                    scope.launch {
                        busyAction = "bookmark"
                        runCatching { onSetBookmark(!update.bookmarked) }
                        busyAction = null
                    }
                }
            },
        )
    }
}

@Composable
private fun UpdateIssueListItem(
    issue: ChimahonUpdateIssue,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ChimahonPalette.errorContainer),
            contentAlignment = Alignment.Center,
        ) {
            Label("!", ChimahonPalette.error, 16, weight = FontWeight.Bold, maxLines = 1)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
        ) {
            Label(issue.mangaTitle, ChimahonPalette.onSurface, 13, weight = FontWeight.SemiBold, maxLines = 1)
            Label(
                issue.message,
                ChimahonPalette.secondaryText,
                11,
                maxLines = 2,
                modifier = Modifier.padding(top = 3.dp),
            )
            Label(
                issue.lastUpdate,
                ChimahonPalette.secondaryText,
                10,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        ChapterQuickAction(
            icon = UiIcon.Delete,
            contentDescription = "Dismiss issue",
            active = false,
            onClick = onDismiss,
        )
    }
}

@Composable
private fun HistoryHome(
    snapshot: ChimahonSnapshot,
    query: String,
    filtersVisible: Boolean,
    onOpenManga: (Long) -> Unit,
    onOpenReader: (ChimahonReaderRequest) -> Unit,
    onResetHistoryEntry: suspend (Long) -> Unit,
    onResetHistoryForManga: suspend (Long) -> Unit,
    onClearHistory: suspend () -> Unit,
    onRefresh: () -> Unit,
) {
    var selectedFilter by remember { mutableStateOf(HistoryFilter.All) }
    val selectedHistoryIds = remember { mutableStateMapOf<Long, Boolean>() }
    val scope = rememberCoroutineScope()
    val history = snapshot.history
        .filter { entry ->
            query.isBlank() || entry.title.contains(query, ignoreCase = true)
        }
        .filter { entry ->
            when (selectedFilter) {
                HistoryFilter.All -> true
                HistoryFilter.Unfinished -> !entry.read || entry.lastPageRead > 0L
                HistoryFilter.Completed -> entry.read
            }
        }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        if (filtersVisible) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ChimahonPalette.surface)
                        .padding(vertical = 8.dp),
                ) {
                    FilterChips(
                        chips = HistoryFilter.entries.map { it.title },
                        selected = selectedFilter.title,
                        onSelect = { title ->
                            selectedFilter = HistoryFilter.entries.firstOrNull { it.title == title } ?: HistoryFilter.All
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButtonLike("Clear history", onClick = {
                            scope.launch {
                                runCatching { onClearHistory() }
                                onRefresh()
                            }
                        })
                    }
                }
            }
        }
        if (history.isNotEmpty()) {
            item {
                MultiSelectionBar(
                    selectedCount = selectedHistoryIds.count { it.value },
                    totalCount = history.size,
                    onSelectAll = {
                        history.forEach { selectedHistoryIds[it.id] = true }
                    },
                    onInvert = {
                        history.forEach { entry ->
                            selectedHistoryIds[entry.id] = selectedHistoryIds[entry.id] != true
                        }
                    },
                    onClear = { selectedHistoryIds.clear() },
                    actions = listOf(
                        SelectionAction("Remove", UiIcon.Delete) {
                            val selected = history.filter { selectedHistoryIds[it.id] == true }
                            scope.launch {
                                selected.forEach { entry ->
                                    runCatching { onResetHistoryEntry(entry.id) }
                                }
                                selectedHistoryIds.clear()
                                onRefresh()
                            }
                        },
                    ),
                )
            }
        }
        if (history.isEmpty()) {
            item {
                EmptyMobileState(
                    marker = "H",
                    icon = UiIcon.History,
                    title = if (snapshot.history.isEmpty()) "Nothing read recently" else "No results found",
                    detail = if (snapshot.history.isEmpty()) {
                        "Recently read manga will appear here from the shared history view."
                    } else {
                        "Try another search or history filter."
                    },
                )
            }
        } else {
            history.groupBy { it.readAt?.toDateBucket("Read") ?: "Unknown date" }.forEach { (date, entries) ->
                item {
                    ListGroupHeader(date)
                }
                items(entries, key = { "${it.mangaId}:${it.chapterId}" }) { entry ->
                    val manga = snapshot.mangaDetails[entry.mangaId]
                    val chapters = snapshot.chaptersByMangaId[entry.mangaId].orEmpty()
                    val chapter = chapters.firstOrNull { it.id == entry.chapterId }
                    HistoryListItem(
                        entry = entry,
                        manga = manga,
                        selected = selectedHistoryIds[entry.id] == true,
                        onToggleSelected = {
                            selectedHistoryIds[entry.id] = selectedHistoryIds[entry.id] != true
                        },
                        onOpenManga = { onOpenManga(entry.mangaId) },
                        onResume = if (manga != null && chapter != null) {
                            { onOpenReader(chapter.toReaderRequest(manga, chapters)) }
                        } else {
                            null
                        },
                        onResetEntry = {
                            scope.launch {
                                runCatching { onResetHistoryEntry(entry.id) }
                                onRefresh()
                            }
                        },
                        onResetManga = {
                            scope.launch {
                                runCatching { onResetHistoryForManga(entry.mangaId) }
                                onRefresh()
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun MultiSelectionBar(
    selectedCount: Int,
    totalCount: Int,
    onSelectAll: () -> Unit,
    onInvert: () -> Unit,
    onClear: () -> Unit,
    actions: List<SelectionAction>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selectedCount > 0) ChimahonPalette.primaryContainer else ChimahonPalette.surface,
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Label(
            if (selectedCount > 0) "$selectedCount selected" else "$totalCount items",
            if (selectedCount > 0) ChimahonPalette.onPrimaryContainer else ChimahonPalette.secondaryText,
            12,
            weight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        TextButtonLike(
            text = if (selectedCount == totalCount) "Clear" else "All",
            onClick = if (selectedCount == totalCount) onClear else onSelectAll,
        )
        TextButtonLike("Invert", onClick = onInvert)
        if (selectedCount > 0) {
            actions.forEach { action ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(onClick = action.onClick),
                    contentAlignment = Alignment.Center,
                ) {
                    IconGlyph(
                        icon = action.icon,
                        contentDescription = action.title,
                        tint = ChimahonPalette.primary,
                        modifier = Modifier.size(19.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ListGroupHeader(title: String) {
    Label(
        text = title,
        color = ChimahonPalette.secondaryText,
        size = 12,
        weight = FontWeight.SemiBold,
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.background)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun HistoryListItem(
    entry: ChimahonHistoryEntry,
    manga: ChimahonMangaEntry?,
    selected: Boolean,
    onToggleSelected: () -> Unit,
    onOpenManga: () -> Unit,
    onResume: (() -> Unit)?,
    onResetEntry: () -> Unit,
    onResetManga: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(ChimahonPalette.surface)
            .clickable(onClick = onResume ?: onOpenManga)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(53.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onOpenManga),
        ) {
            ThumbnailArtwork(
                url = manga?.thumbnailUrl,
                title = entry.title,
                fallbackColor = coverColor(entry.mangaId, entry.title),
                modifier = Modifier.fillMaxSize(),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 8.dp),
        ) {
            Label(
                text = entry.title,
                color = if (entry.read) ChimahonPalette.secondaryText else ChimahonPalette.onSurface,
                size = 13,
                weight = FontWeight.SemiBold,
                maxLines = 2,
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!entry.read) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(ChimahonPalette.primary),
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Label(
                text = buildString {
                    append("Ch. ${entry.chapterNumber.toDisplayChapter()}")
                    entry.readAt?.let { append("  \u00b7  ").append(it.toDateBucket("Read")) }
                    if (entry.lastPageRead > 0L) append("  \u00b7  Page ${entry.lastPageRead + 1}")
                    if (entry.totalCount > 0.0) {
                        append("  \u00b7  ")
                        append(entry.readCount.toLong()).append("/").append(entry.totalCount.toLong())
                    }
                },
                    color = ChimahonPalette.secondaryText,
                    size = 11,
                    maxLines = 1,
                )
            }
        }
        onResume?.let {
            ChapterQuickAction(
                icon = UiIcon.Play,
                contentDescription = "Resume reading",
                active = false,
                onClick = it,
            )
        }
        ChapterQuickAction(
            icon = if (selected) UiIcon.CheckCircle else UiIcon.Circle,
            contentDescription = if (selected) "Deselect history entry" else "Select history entry",
            active = selected,
            onClick = onToggleSelected,
        )
        ChapterQuickAction(
            icon = UiIcon.Delete,
            contentDescription = "Remove history entry",
            active = false,
            onClick = onResetEntry,
        )
        ChapterQuickAction(
            icon = UiIcon.DoneAll,
            contentDescription = "Clear manga history",
            active = false,
            onClick = onResetManga,
        )
    }
}

@Composable
private fun BrowseHome(
    snapshot: ChimahonSnapshot,
    selectedSection: BrowseSection,
    query: String,
    filtersVisible: Boolean,
    feedManageMode: Boolean,
    migrateHelpVisible: Boolean,
    extensionRepoRequestKey: Int,
    onSectionChange: (BrowseSection) -> Unit,
    onOpenManga: (Long) -> Unit,
    onLoadSourcePreview: suspend (Long, ChimahonSourceBrowseMode, String, Int) -> ChimahonSourcePreview,
    onLoadRemoteMangaDetail: suspend (ChimahonRemoteMangaEntry) -> ChimahonRemoteMangaDetail,
    onOpenRemoteManga: (ChimahonRemoteMangaEntry) -> Unit,
    onOpenSource: (Long, ChimahonSourceBrowseMode) -> Unit,
    onAddRemoteMangaToLibrary: suspend (ChimahonRemoteMangaDetail) -> Long,
    onSetMangaFavorite: suspend (Long, Boolean) -> Unit,
    browseSettings: ChimahonBrowseSettings,
    onAddExtensionRepo: suspend (String) -> ChimahonExtensionRepoEntry,
    onDeleteExtensionRepo: suspend (String) -> Unit,
    onLoadExtensionRepoCatalog: suspend (ChimahonExtensionRepoEntry) -> ChimahonExtensionRepoCatalog,
    onInstallExtension: suspend (ChimahonRepoExtensionEntry) -> ChimahonInstalledExtensionEntry,
    onUninstallExtension: suspend (String, ChimahonExtensionPackageType) -> ChimahonExtensionUninstallResult,
    onRepoSaved: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        BrowseTabs(
            selected = selectedSection,
            onSelect = onSectionChange,
        )
        when (selectedSection) {
            BrowseSection.Sources -> SourcesSection(
                sources = snapshot.sources,
                query = query,
                settings = browseSettings,
                onOpenSource = onOpenSource,
                onInstallExtension = { onSectionChange(BrowseSection.Extensions) },
            )
            BrowseSection.Feed -> FeedSection(
                sources = snapshot.sources,
                manageMode = feedManageMode,
                onLoadSourcePreview = onLoadSourcePreview,
                onOpenRemoteManga = onOpenRemoteManga,
                onOpenSource = onOpenSource,
            )
            BrowseSection.Extensions -> ExtensionsSection(
                snapshot = snapshot,
                query = query,
                filtersVisible = filtersVisible,
                browseSettings = browseSettings,
                repoInputRequestKey = extensionRepoRequestKey,
                onAddExtensionRepo = onAddExtensionRepo,
                onDeleteExtensionRepo = onDeleteExtensionRepo,
                onLoadExtensionRepoCatalog = onLoadExtensionRepoCatalog,
                onInstallExtension = onInstallExtension,
                onUninstallExtension = onUninstallExtension,
                onRepoSaved = onRepoSaved,
            )
            BrowseSection.Migrate -> MigrateSection(
                library = snapshot.library,
                sources = snapshot.sources,
                helpVisible = migrateHelpVisible,
                onOpenManga = onOpenManga,
                onLoadSourcePreview = onLoadSourcePreview,
                onLoadRemoteMangaDetail = onLoadRemoteMangaDetail,
                onAddRemoteMangaToLibrary = onAddRemoteMangaToLibrary,
                onSetMangaFavorite = onSetMangaFavorite,
                onMigrationComplete = onOpenManga,
            )
        }
    }
}

@Composable
private fun BrowseTabs(
    selected: BrowseSection,
    onSelect: (BrowseSection) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ChimahonPalette.divider),
    ) {
        BrowseSection.entries.forEach { section ->
            val isSelected = section == selected
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(section) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.height(48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Label(
                        text = section.title,
                        color = if (isSelected) ChimahonPalette.primary else ChimahonPalette.onSurface,
                        size = 13,
                        weight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                    )
                }
                Box(
                    modifier = Modifier
                        .height(3.dp)
                        .fillMaxWidth()
                        .background(if (isSelected) ChimahonPalette.primary else Color.Transparent),
                )
            }
        }
    }
}

@Composable
private fun SourcesSection(
    sources: List<ChimahonSourceEntry>,
    query: String,
    settings: ChimahonBrowseSettings,
    onOpenSource: (Long, ChimahonSourceBrowseMode) -> Unit,
    onInstallExtension: () -> Unit,
) {
    val filteredSources = sources.filter {
        (settings.enabledLanguages.isEmpty() || it.language.sourceLanguageCode() in settings.enabledLanguages) &&
            (
                query.isBlank() ||
                    it.name.contains(query, ignoreCase = true) ||
                    it.language.contains(query, ignoreCase = true)
                )
    }
    if (sources.isEmpty()) {
        EmptyMobileState(
            marker = "S",
            icon = UiIcon.Browse,
            title = "No sources installed",
            detail = "Script extension sources loaded by the shared engine will appear here.",
            action = "Install extension",
            onAction = onInstallExtension,
        )
        return
    }
    if (filteredSources.isEmpty()) {
        EmptyListPanel(
            marker = "S",
            title = "No matching sources",
            detail = "No installed source matches \"$query\".",
        )
        return
    }
    val sourceGroups = filteredSources.sourceGroups(settings.groupSourcesByLanguage)
    if (settings.sourceDisplayMode == ChimahonBrowseSourceDisplayMode.Grid) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            sourceGroups.forEach { (language, group) ->
                if (language != null) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ListGroupHeader(language)
                    }
                }
                items(group, key = { it.id }) { source ->
                    SourceGridCard(
                        source = source,
                        showLanguage = settings.showSourceLanguage,
                        onClick = { onOpenSource(source.id, ChimahonSourceBrowseMode.Popular) },
                        onClickLatest = { onOpenSource(source.id, ChimahonSourceBrowseMode.Latest) },
                    )
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 4.dp),
        ) {
            sourceGroups.forEach { (language, group) ->
                if (language != null) {
                    item { ListGroupHeader(language) }
                }
                items(group, key = { it.id }) { source ->
                    SourceListItem(
                        source = source,
                        compact = settings.sourceDisplayMode == ChimahonBrowseSourceDisplayMode.CompactList,
                        showLanguage = settings.showSourceLanguage,
                        onClick = { onOpenSource(source.id, ChimahonSourceBrowseMode.Popular) },
                        onClickLatest = { onOpenSource(source.id, ChimahonSourceBrowseMode.Latest) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SourceListItem(
    source: ChimahonSourceEntry,
    compact: Boolean,
    showLanguage: Boolean,
    onClick: () -> Unit,
    onClickLatest: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .clickable(onClick = onClick)
            .padding(
                start = 16.dp,
                end = 8.dp,
                top = if (compact) 7.dp else 10.dp,
                bottom = if (compact) 7.dp else 10.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 34.dp else 42.dp)
                .clip(CircleShape)
                .background(coverColor(source.id, source.name).copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Label(
                source.name.firstOrNull()?.uppercase() ?: "S",
                coverColor(source.id, source.name),
                if (compact) 12 else 15,
                weight = FontWeight.Bold,
                maxLines = 1,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
        ) {
            Label(source.name, ChimahonPalette.onSurface, 14, weight = FontWeight.SemiBold, maxLines = 1)
            if (showLanguage || !compact) {
                Label(
                    source.language.ifBlank { "multi" }.uppercase(),
                    ChimahonPalette.secondaryText,
                    11,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        if (source.supportsLatest) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .clickable(onClick = onClickLatest)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Label("Latest", ChimahonPalette.primary, 12, weight = FontWeight.SemiBold, maxLines = 1)
            }
        }
    }
}

@Composable
private fun SourceGridCard(
    source: ChimahonSourceEntry,
    showLanguage: Boolean,
    onClick: () -> Unit,
    onClickLatest: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ChimahonPalette.surface)
            .border(1.dp, ChimahonPalette.divider, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(coverColor(source.id, source.name).copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Label(
                    source.name.firstOrNull()?.uppercase() ?: "S",
                    coverColor(source.id, source.name),
                    14,
                    weight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
            if (source.supportsLatest) {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ChimahonPalette.primaryContainer)
                        .clickable(onClick = onClickLatest)
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                ) {
                    Label("Latest", ChimahonPalette.primary, 10, weight = FontWeight.SemiBold, maxLines = 1)
                }
            }
        }
        Label(
            source.name,
            ChimahonPalette.onSurface,
            13,
            weight = FontWeight.SemiBold,
            maxLines = 2,
            modifier = Modifier.padding(top = 10.dp),
        )
        if (showLanguage) {
            Label(
                source.language.ifBlank { "multi" }.uppercase(),
                ChimahonPalette.secondaryText,
                11,
                maxLines = 1,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun SourceDetailHome(
    snapshot: ChimahonSnapshot,
    sourceId: Long,
    initialMode: ChimahonSourceBrowseMode,
    searchKey: Int,
    browseSettings: ChimahonBrowseSettings,
    onRefresh: () -> Unit,
    onLoadSourcePreview: suspend (Long, ChimahonSourceBrowseMode, String, Int) -> ChimahonSourcePreview,
    onOpenRemoteManga: (ChimahonRemoteMangaEntry) -> Unit,
) {
    val source = snapshot.sources.firstOrNull { it.id == sourceId }
    if (source == null) {
        EmptyMobileState(
            marker = "?",
            title = "Source not found",
            detail = "This source is not available in the shared runtime registry.",
            action = "Refresh",
            onAction = onRefresh,
        )
        return
    }

    val modes = if (source.supportsLatest) {
        ChimahonSourceBrowseMode.entries
    } else {
        listOf(ChimahonSourceBrowseMode.Popular, ChimahonSourceBrowseMode.Search)
    }
    var selectedMode by remember(sourceId, initialMode) { mutableStateOf(initialMode) }
    var searchText by remember(sourceId) { mutableStateOf("") }
    var submittedQuery by remember(sourceId) { mutableStateOf("") }
    var handledSearchKey by remember(sourceId) { mutableIntStateOf(searchKey) }
    var pageNumber by remember(sourceId, selectedMode, submittedQuery) { mutableIntStateOf(1) }
    var pageRequestKey by remember(sourceId, selectedMode, submittedQuery) { mutableIntStateOf(0) }
    var loadingMore by remember(sourceId, selectedMode, submittedQuery) { mutableStateOf(false) }
    var loadMoreError by remember(sourceId, selectedMode, submittedQuery) { mutableStateOf<String?>(null) }
    var previewState by remember(sourceId, selectedMode, submittedQuery) {
        mutableStateOf<SourcePreviewUiState>(SourcePreviewUiState.Loading)
    }

    LaunchedEffect(sourceId, searchKey) {
        if (searchKey != handledSearchKey) {
            handledSearchKey = searchKey
            selectedMode = ChimahonSourceBrowseMode.Search
        }
    }

    LaunchedEffect(sourceId, selectedMode, submittedQuery, pageNumber, pageRequestKey) {
        if (selectedMode == ChimahonSourceBrowseMode.Search && submittedQuery.isBlank()) {
            previewState = SourcePreviewUiState.Idle
            return@LaunchedEffect
        }
        if (pageNumber == 1) {
            previewState = SourcePreviewUiState.Loading
        } else {
            loadingMore = true
        }
        runCatching { onLoadSourcePreview(sourceId, selectedMode, submittedQuery, pageNumber) }
            .onSuccess { loaded ->
                val previous = (previewState as? SourcePreviewUiState.Ready)?.preview
                loadMoreError = null
                previewState = SourcePreviewUiState.Ready(
                    if (pageNumber > 1 && previous != null) {
                        loaded.copy(
                            entries = (previous.entries + loaded.entries).distinctBy { it.url },
                        )
                    } else {
                        loaded
                    },
                )
            }
            .onFailure { error ->
                if (pageNumber == 1) {
                    previewState = SourcePreviewUiState.Failed(error.message ?: "Unable to load source.")
                } else {
                    loadMoreError = error.message ?: "Unable to load more entries."
                }
            }
        loadingMore = false
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SourceBrowseModeTabs(
            modes = modes,
            selected = selectedMode,
            onSelect = { selectedMode = it },
        )
        if (selectedMode == ChimahonSourceBrowseMode.Search) {
            SourceSearchPanel(
                query = searchText,
                onQueryChange = { searchText = it },
                onSearch = {
                    searchText.trim().takeIf(String::isNotEmpty)?.let { submittedQuery = it }
                },
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            when (val preview = previewState) {
                SourcePreviewUiState.Idle -> {
                    EmptyMobileState(
                        marker = "Q",
                        icon = UiIcon.Search,
                        title = "Search ${source.name}",
                        detail = "Enter a title or keyword to search this source.",
                    )
                }
                SourcePreviewUiState.Loading -> {
                    EmptyMobileState(
                        marker = "S",
                        title = "Loading ${selectedMode.title.lowercase()}",
                        detail = "Fetching manga through the shared source engine.",
                    )
                }
                is SourcePreviewUiState.Failed -> {
                    EmptyMobileState(
                        marker = "!",
                        title = "Source fetch failed",
                        detail = preview.message,
                        action = "Retry",
                        onAction = { pageRequestKey++ },
                    )
                }
                is SourcePreviewUiState.Ready -> {
                    val libraryRemoteKeys = snapshot.mangaDetails.values
                        .map { "${it.sourceId}:${it.url}" }
                        .toSet()
                    val visiblePreview = preview.preview.copy(
                        entries = preview.preview.entries.filter { entry ->
                            !browseSettings.hideLibraryEntries ||
                                "${entry.sourceId}:${entry.url}" !in libraryRemoteKeys
                        },
                    )
                    if (visiblePreview.entries.isEmpty()) {
                        EmptyMobileState(
                            marker = "S",
                            title = if (preview.preview.entries.isEmpty()) {
                                "No entries returned"
                            } else {
                                "No new entries"
                            },
                            detail = if (preview.preview.mode == ChimahonSourceBrowseMode.Search) {
                                "No results matched \"$submittedQuery\"."
                            } else if (preview.preview.entries.isNotEmpty()) {
                                "All returned titles are already in your library. Disable Hide library entries to show them."
                            } else {
                                "${source.name} returned an empty ${preview.preview.mode.title.lowercase()} page."
                            },
                        )
                    } else {
                        val gridState = rememberLazyGridState()
                        LaunchedEffect(
                            gridState,
                            visiblePreview.entries.size,
                            visiblePreview.hasNextPage,
                            browseSettings.autoLoadMore,
                            loadingMore,
                            loadMoreError,
                        ) {
                            if (
                                !browseSettings.autoLoadMore ||
                                !visiblePreview.hasNextPage ||
                                loadingMore ||
                                loadMoreError != null
                            ) {
                                return@LaunchedEffect
                            }
                            snapshotFlow {
                                val layoutInfo = gridState.layoutInfo
                                val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                                lastVisibleIndex >= (layoutInfo.totalItemsCount - 6).coerceAtLeast(0)
                            }
                                .distinctUntilChanged()
                                .collect { nearEnd ->
                                    if (nearEnd && !loadingMore && loadMoreError == null) {
                                        loadingMore = true
                                        pageNumber++
                                    }
                                }
                        }
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(124.dp),
                            state = gridState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                SourcePreviewHeader(visiblePreview)
                            }
                            items(visiblePreview.entries, key = { "${it.sourceId}:${it.url}" }) { entry ->
                                RemoteMangaGridCard(
                                    entry = entry,
                                    inLibrary = "${entry.sourceId}:${entry.url}" in libraryRemoteKeys,
                                    onClick = { onOpenRemoteManga(entry) },
                                )
                            }
                            if (
                                visiblePreview.hasNextPage &&
                                (!browseSettings.autoLoadMore || loadingMore || loadMoreError != null)
                            ) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (loadingMore) {
                                            Label(
                                                text = "Loading more...",
                                                color = ChimahonPalette.secondaryText,
                                                size = 12,
                                                weight = FontWeight.SemiBold,
                                            )
                                        } else {
                                            TextButtonLike(
                                                text = if (loadMoreError == null) "Load more" else "Retry",
                                                onClick = {
                                                    if (loadMoreError == null) {
                                                        pageNumber++
                                                    } else {
                                                        loadMoreError = null
                                                        loadingMore = true
                                                        pageRequestKey++
                                                    }
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                            loadMoreError?.let { message ->
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Label(
                                        text = message,
                                        color = ChimahonPalette.error,
                                        size = 11,
                                        maxLines = 3,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SourceBrowseModeTabs(
    modes: List<ChimahonSourceBrowseMode>,
    selected: ChimahonSourceBrowseMode,
    onSelect: (ChimahonSourceBrowseMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ChimahonPalette.divider),
    ) {
        modes.forEach { mode ->
            val isSelected = mode == selected
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(mode) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.height(46.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Label(
                        mode.title,
                        if (isSelected) ChimahonPalette.primary else ChimahonPalette.onSurface,
                        13,
                        weight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                    )
                }
                Box(
                    modifier = Modifier
                        .height(3.dp)
                        .fillMaxWidth()
                        .background(if (isSelected) ChimahonPalette.primary else Color.Transparent),
                )
            }
        }
    }
}

@Composable
private fun SourceSearchPanel(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(ChimahonPalette.surfaceVariant)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (query.isBlank()) {
                Label("Search manga", ChimahonPalette.secondaryText, 12, maxLines = 1)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = ChimahonPalette.onSurface,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        TextButtonLike(
            text = "Search",
            onClick = onSearch,
        )
    }
}

@Composable
private fun SourceDetailHeader(
    source: ChimahonSourceEntry,
    localCount: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ChimahonPalette.surface)
            .border(1.dp, ChimahonPalette.divider, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(ChimahonPalette.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Label(
                source.name.firstOrNull()?.uppercase() ?: "S",
                ChimahonPalette.primary,
                20,
                weight = FontWeight.Bold,
                maxLines = 1,
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Label(source.name, ChimahonPalette.onSurface, 18, weight = FontWeight.SemiBold, maxLines = 1)
            Label(
                "${source.language.ifBlank { "multi" }.uppercase()} - $localCount in library",
                ChimahonPalette.secondaryText,
                12,
                maxLines = 1,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        Label(
            if (source.supportsLatest) "Latest" else "Popular",
            ChimahonPalette.primary,
            12,
            weight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun SourcePreviewHeader(preview: ChimahonSourcePreview) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Label(preview.mode.title, ChimahonPalette.onSurface, 16, weight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Label(
            if (preview.hasNextPage) "${preview.entries.size} entries, more" else "${preview.entries.size} entries",
            ChimahonPalette.secondaryText,
            12,
            maxLines = 1,
        )
    }
}

@Composable
private fun RemoteMangaGridCard(
    entry: ChimahonRemoteMangaEntry,
    inLibrary: Boolean,
    onClick: () -> Unit,
) {
    val fallbackColor = coverColor(entry.sourceId xor entry.url.hashCode().toLong(), entry.title)
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.68f)
                .clip(RoundedCornerShape(6.dp))
                .background(fallbackColor),
        ) {
            ThumbnailArtwork(
                url = entry.thumbnailUrl,
                title = entry.title,
                fallbackColor = fallbackColor,
                modifier = Modifier.fillMaxSize(),
            )
            if (inLibrary) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(7.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ChimahonPalette.primary)
                        .padding(horizontal = 7.dp, vertical = 4.dp),
                ) {
                    Label("In library", Color.White, 9, weight = FontWeight.SemiBold, maxLines = 1)
                }
            }
            if (entry.status != "Unknown") {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.44f))
                        .padding(horizontal = 7.dp, vertical = 5.dp),
                ) {
                    Label(entry.status, Color.White, 9, weight = FontWeight.SemiBold, maxLines = 1)
                }
            }
        }
        Label(
            text = entry.title,
            color = ChimahonPalette.onSurface,
            size = 13,
            weight = FontWeight.SemiBold,
            maxLines = 2,
            modifier = Modifier.padding(top = 6.dp),
        )
        entry.author?.takeIf { it.isNotBlank() }?.let { author ->
            Label(
                text = author,
                color = ChimahonPalette.secondaryText,
                size = 10,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun RemoteMangaDetailHome(
    remoteManga: ChimahonRemoteMangaEntry,
    existingMangaId: Long?,
    onLoadRemoteMangaDetail: suspend (ChimahonRemoteMangaEntry) -> ChimahonRemoteMangaDetail,
    onOpenReader: (ChimahonReaderRequest) -> Unit,
    onOpenLibraryManga: (Long) -> Unit,
    onOpenRemoteMangaUrl: (ChimahonRemoteMangaDetail) -> Boolean,
    onAddToLibrary: suspend (ChimahonRemoteMangaDetail) -> Unit,
) {
    var refreshKey by remember(remoteManga.sourceId, remoteManga.url) { mutableIntStateOf(0) }
    var detailState by remember(remoteManga.sourceId, remoteManga.url) {
        mutableStateOf<RemoteMangaDetailUiState>(RemoteMangaDetailUiState.Loading)
    }

    LaunchedEffect(remoteManga.sourceId, remoteManga.url, refreshKey) {
        detailState = RemoteMangaDetailUiState.Loading
        detailState = runCatching { onLoadRemoteMangaDetail(remoteManga) }
            .fold(
                onSuccess = RemoteMangaDetailUiState::Ready,
                onFailure = { RemoteMangaDetailUiState.Failed(it.message ?: "Unable to load manga details.") },
            )
    }

    when (val detail = detailState) {
        RemoteMangaDetailUiState.Loading -> {
            EmptyMobileState(
                marker = "M",
                title = "Loading manga",
                detail = "Fetching details and chapters through the shared source engine.",
                action = "Retry",
                onAction = { refreshKey++ },
            )
        }
        is RemoteMangaDetailUiState.Failed -> {
            EmptyMobileState(
                marker = "!",
                title = "Manga fetch failed",
                detail = detail.message,
                action = "Retry",
                onAction = { refreshKey++ },
            )
        }
        is RemoteMangaDetailUiState.Ready -> {
            RemoteMangaDetailContent(
                detail = detail.detail,
                existingMangaId = existingMangaId,
                onOpenReader = onOpenReader,
                onOpenLibraryManga = onOpenLibraryManga,
                onOpenRemoteMangaUrl = onOpenRemoteMangaUrl,
                onAddToLibrary = onAddToLibrary,
            )
        }
    }
}

@Composable
private fun RemoteMangaDetailContent(
    detail: ChimahonRemoteMangaDetail,
    existingMangaId: Long?,
    onOpenReader: (ChimahonReaderRequest) -> Unit,
    onOpenLibraryManga: (Long) -> Unit,
    onOpenRemoteMangaUrl: (ChimahonRemoteMangaDetail) -> Boolean,
    onAddToLibrary: suspend (ChimahonRemoteMangaDetail) -> Unit,
) {
    var chapterDescending by remember(detail.sourceId, detail.url) { mutableStateOf(true) }
    var chapterFiltersVisible by remember(detail.sourceId, detail.url) { mutableStateOf(false) }
    var chapterQuery by remember(detail.sourceId, detail.url) { mutableStateOf("") }
    val searchedChapters = detail.chapters.filter { chapter ->
        chapterQuery.isBlank() ||
            chapter.name.contains(chapterQuery, ignoreCase = true) ||
            chapter.scanlator.orEmpty().contains(chapterQuery, ignoreCase = true)
    }
    val visibleChapters = if (chapterDescending) {
        searchedChapters.sortedWith(
            compareByDescending<ChimahonRemoteChapterEntry> { it.chapterNumber }.thenBy { it.name },
        )
    } else {
        searchedChapters.sortedWith(
            compareBy<ChimahonRemoteChapterEntry> { it.chapterNumber }.thenBy { it.name },
        )
    }
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth >= 900.dp) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(0.42f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    RemoteMangaDetailHeader(
                        detail = detail,
                        centered = true,
                    )
                    RemoteMangaActionRow(
                        detail = detail,
                        existingMangaId = existingMangaId,
                        onOpenReader = onOpenReader,
                        onOpenLibraryManga = onOpenLibraryManga,
                        onOpenRemoteMangaUrl = onOpenRemoteMangaUrl,
                        onAddToLibrary = onAddToLibrary,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                    RemoteMangaDescriptionPanel(
                        detail = detail,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(ChimahonPalette.divider),
                )
                RemoteChapterPane(
                    detail = detail.copy(chapters = visibleChapters),
                    filtersVisible = chapterFiltersVisible,
                    descending = chapterDescending,
                    query = chapterQuery,
                    onToggleFilters = { chapterFiltersVisible = !chapterFiltersVisible },
                    onQueryChange = { chapterQuery = it },
                    onSortDirectionChange = { chapterDescending = it },
                    onOpenReader = onOpenReader,
                    modifier = Modifier
                        .weight(0.58f)
                        .fillMaxHeight(),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                item {
                    RemoteMangaDetailHeader(
                        detail = detail,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
                item {
                    RemoteMangaActionRow(
                        detail = detail,
                        existingMangaId = existingMangaId,
                        onOpenReader = onOpenReader,
                        onOpenLibraryManga = onOpenLibraryManga,
                        onOpenRemoteMangaUrl = onOpenRemoteMangaUrl,
                        onAddToLibrary = onAddToLibrary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
                item {
                    RemoteMangaDescriptionPanel(
                        detail = detail,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }
                item {
                    RemoteChapterHeader(
                        chapters = visibleChapters,
                        filtersVisible = chapterFiltersVisible,
                        onToggleFilters = { chapterFiltersVisible = !chapterFiltersVisible },
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
                if (chapterFiltersVisible) {
                    item {
                        RemoteChapterFilterPanel(
                            descending = chapterDescending,
                            query = chapterQuery,
                            onQueryChange = { chapterQuery = it },
                            onSortDirectionChange = { chapterDescending = it },
                        )
                    }
                }
                if (detail.chapters.isEmpty()) {
                    item {
                        EmptyListPanel(
                            marker = "C",
                            title = "No chapters returned",
                            detail = "${detail.sourceName} did not return chapters for this manga.",
                        )
                    }
                } else {
                    items(visibleChapters, key = { it.url }) { chapter ->
                        RemoteChapterListItem(
                            chapter = chapter,
                            onClick = {
                                onOpenReader(chapter.toReaderRequest(detail, detail.chapters))
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RemoteChapterPane(
    detail: ChimahonRemoteMangaDetail,
    filtersVisible: Boolean,
    descending: Boolean,
    query: String,
    onToggleFilters: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSortDirectionChange: (Boolean) -> Unit,
    onOpenReader: (ChimahonReaderRequest) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 10.dp, bottom = 18.dp),
    ) {
        item {
            RemoteChapterHeader(
                chapters = detail.chapters,
                filtersVisible = filtersVisible,
                onToggleFilters = onToggleFilters,
            )
        }
        if (filtersVisible) {
            item {
                RemoteChapterFilterPanel(
                    descending = descending,
                    query = query,
                    onQueryChange = onQueryChange,
                    onSortDirectionChange = onSortDirectionChange,
                )
            }
        }
        if (detail.chapters.isEmpty()) {
            item {
                EmptyListPanel(
                    marker = "C",
                    title = "No chapters returned",
                    detail = "${detail.sourceName} did not return chapters for this manga.",
                )
            }
        } else {
            items(detail.chapters, key = { it.url }) { chapter ->
                RemoteChapterListItem(
                    chapter = chapter,
                    onClick = {
                        onOpenReader(chapter.toReaderRequest(detail, detail.chapters))
                    },
                )
            }
        }
    }
}

@Composable
private fun RemoteMangaDetailHeader(
    detail: ChimahonRemoteMangaDetail,
    centered: Boolean = false,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
    ) {
        val useCenteredLayout = centered || maxWidth < 420.dp
        if (!useCenteredLayout) {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                RemoteMangaCoverTile(
                    detail = detail,
                    modifier = Modifier
                        .width(154.dp)
                        .height(226.dp),
                )
                RemoteMangaInfoColumn(
                    detail = detail,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RemoteMangaCoverTile(
                    detail = detail,
                    modifier = Modifier
                        .width(if (centered) 176.dp else 132.dp)
                        .height(if (centered) 258.dp else 194.dp),
                )
                RemoteMangaInfoColumn(
                    detail = detail,
                    centered = true,
                    modifier = Modifier.padding(top = 14.dp),
                )
            }
        }
    }
}

@Composable
private fun RemoteMangaCoverTile(
    detail: ChimahonRemoteMangaDetail,
    modifier: Modifier = Modifier,
) {
    val fallbackColor = coverColor(detail.sourceId xor detail.url.hashCode().toLong(), detail.title)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(fallbackColor),
    ) {
        ThumbnailArtwork(
            url = detail.thumbnailUrl,
            title = detail.title,
            fallbackColor = fallbackColor,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black.copy(alpha = 0.44f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Label("Source", Color.White, 10, weight = FontWeight.SemiBold, maxLines = 1)
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.42f))
                .padding(8.dp),
        ) {
            Label(
                text = detail.status,
                color = Color.White,
                size = 10,
                weight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun RemoteMangaInfoColumn(
    detail: ChimahonRemoteMangaDetail,
    centered: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        Label(detail.title, ChimahonPalette.onSurface, 22, weight = FontWeight.SemiBold, lineHeight = 27)
        Label(
            text = detail.author ?: detail.artist ?: "Unknown author",
            color = ChimahonPalette.secondaryText,
            size = 13,
            maxLines = 1,
            modifier = Modifier.padding(top = 5.dp),
        )
        Label(
            text = "${detail.status}  \u00b7  ${detail.sourceName}",
            color = ChimahonPalette.secondaryText,
            size = 12,
            maxLines = 1,
            modifier = Modifier.padding(top = 5.dp),
        )
        Label(
            text = "${detail.chapters.size} chapters",
            color = ChimahonPalette.secondaryText,
            size = 11,
            maxLines = 1,
            modifier = Modifier.padding(top = 3.dp),
        )
    }
}

@Composable
private fun RemoteMangaActionRow(
    detail: ChimahonRemoteMangaDetail,
    existingMangaId: Long?,
    onOpenReader: (ChimahonReaderRequest) -> Unit,
    onOpenLibraryManga: (Long) -> Unit,
    onOpenRemoteMangaUrl: (ChimahonRemoteMangaDetail) -> Boolean,
    onAddToLibrary: suspend (ChimahonRemoteMangaDetail) -> Unit,
    modifier: Modifier = Modifier,
) {
    var adding by remember(detail.sourceId, detail.url) { mutableStateOf(false) }
    var addError by remember(detail.sourceId, detail.url) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            MangaActionButton(
                icon = UiIcon.Play,
                title = "Start",
                modifier = Modifier.weight(1f),
                onClick = {
                    detail.chapters.lastOrNull()?.let { chapter ->
                        onOpenReader(chapter.toReaderRequest(detail, detail.chapters))
                    }
                },
            )
            MangaActionButton(
                icon = if (existingMangaId != null) UiIcon.Favorite else UiIcon.FavoriteBorder,
                title = when {
                    existingMangaId != null -> "In library"
                    adding -> "Adding"
                    else -> "Add"
                },
                active = existingMangaId != null || adding,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (existingMangaId != null) {
                        onOpenLibraryManga(existingMangaId)
                    } else if (!adding) {
                        scope.launch {
                            adding = true
                            addError = null
                            runCatching { onAddToLibrary(detail) }
                                .onFailure { addError = it.message ?: "Could not add manga to the library." }
                            adding = false
                        }
                    }
                },
            )
            MangaActionButton(
                icon = UiIcon.Web,
                title = "URL",
                modifier = Modifier.weight(1f),
                onClick = {
                    onOpenRemoteMangaUrl(detail)
                },
            )
        }
        addError?.let { message ->
            Label(
                text = message,
                color = ChimahonPalette.error,
                size = 11,
                lineHeight = 16,
                maxLines = 3,
                modifier = Modifier.padding(top = 8.dp, start = 4.dp, end = 4.dp),
            )
        }
    }
}

@Composable
private fun RemoteMangaDescriptionPanel(
    detail: ChimahonRemoteMangaDetail,
    modifier: Modifier = Modifier,
) {
    var expanded by remember(detail.sourceId, detail.url) { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
    ) {
        SectionHeader("About")
        Label(
            text = detail.description?.takeIf { it.isNotBlank() } ?: "No description available.",
            color = ChimahonPalette.onSurface,
            size = 13,
            lineHeight = 19,
            maxLines = if (expanded) Int.MAX_VALUE else 7,
            modifier = Modifier.padding(top = 4.dp),
        )
        if (!detail.description.isNullOrBlank()) {
            Label(
                if (expanded) "Show less" else "Show more",
                ChimahonPalette.primary,
                11,
                weight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(vertical = 7.dp),
            )
        }
        if (detail.genres.isNotEmpty()) {
            FilterChips(
                chips = detail.genres,
                selected = "",
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun RemoteChapterHeader(
    chapters: List<ChimahonRemoteChapterEntry>,
    filtersVisible: Boolean,
    onToggleFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Label(
            "${chapters.size} chapters",
            ChimahonPalette.onSurface,
            16,
            weight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (filtersVisible) ChimahonPalette.primaryContainer else Color.Transparent)
                .clickable(onClick = onToggleFilters),
            contentAlignment = Alignment.Center,
        ) {
            IconGlyph(
                icon = UiIcon.Filter,
                contentDescription = "Chapter filters",
                tint = if (filtersVisible) ChimahonPalette.primary else ChimahonPalette.secondaryText,
                modifier = Modifier.size(19.dp),
            )
        }
    }
}

@Composable
private fun RemoteChapterFilterPanel(
    descending: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onSortDirectionChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(bottom = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(horizontal = 12.dp, vertical = 2.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ChimahonPalette.background)
                .border(1.dp, ChimahonPalette.divider, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (query.isBlank()) {
                Label("Search chapters", ChimahonPalette.secondaryText, 12, maxLines = 1)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = ChimahonPalette.onSurface,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        FilterChips(
            chips = listOf("Newest first", "Oldest first"),
            selected = if (descending) "Newest first" else "Oldest first",
            onSelect = { onSortDirectionChange(it == "Newest first") },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun RemoteChapterListItem(
    chapter: ChimahonRemoteChapterEntry,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Label(
                text = chapter.name,
                color = ChimahonPalette.onSurface,
                size = 13,
                maxLines = 1,
            )
            Label(
                text = buildString {
                    if (chapter.dateUpload > 0L) append(chapter.dateUpload.toDateBucket("Uploaded"))
                    chapter.scanlator?.takeIf { it.isNotBlank() }?.let {
                        if (isNotEmpty()) append("  \u00b7  ")
                        append(it)
                    }
                }.ifBlank { "Source chapter" },
                color = ChimahonPalette.secondaryText,
                size = 11,
                maxLines = 1,
            )
        }
        IconGlyph(
            icon = UiIcon.Play,
            contentDescription = "Read chapter",
            tint = ChimahonPalette.secondaryText,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun ReaderHome(
    request: ChimahonReaderRequest,
    initialBookmarked: Boolean,
    onLoadReaderChapter: suspend (ChimahonReaderRequest) -> ChimahonReaderChapter,
    onLoadReaderPageImage: suspend (ChimahonReaderPage) -> ByteArray,
    settings: ChimahonReaderSettings,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
    onSaveReaderProgress: suspend (ChimahonReaderRequest, Int, Boolean) -> Unit,
    onSetChapterRead: suspend (Long, Boolean) -> Unit,
    onSetChapterBookmark: suspend (Long, Boolean) -> Unit,
    onEnqueueDownload: suspend (Long) -> ChimahonDownloadQueueData,
    onOpenExternalUrl: (String) -> Boolean,
    onOpenReader: (ChimahonReaderRequest) -> Unit,
    onCloseReader: () -> Unit,
) {
    var refreshKey by remember(request) { mutableIntStateOf(0) }
    var state by remember(request) {
        mutableStateOf<ReaderUiState>(ReaderUiState.Loading)
    }

    LaunchedEffect(request, refreshKey) {
        state = ReaderUiState.Loading
        state = runCatching { onLoadReaderChapter(request) }
            .fold(
                onSuccess = ReaderUiState::Ready,
                onFailure = { ReaderUiState.Failed(it.message ?: "Unable to load chapter pages.") },
            )
    }

    when (val readerState = state) {
        ReaderUiState.Loading -> {
            ReaderStatusScreen(
                request = request,
                marker = "...",
                title = "Loading chapter",
                detail = "Fetching the page list from the source.",
                onBack = onCloseReader,
            )
        }
        is ReaderUiState.Failed -> {
            ReaderStatusScreen(
                request = request,
                marker = "!",
                title = "Chapter failed",
                detail = readerState.message,
                action = "Retry",
                onAction = { refreshKey++ },
                onBack = onCloseReader,
            )
        }
        is ReaderUiState.Ready -> {
            if (readerState.chapter.pages.isEmpty()) {
                ReaderStatusScreen(
                    request = request,
                    marker = "0",
                    title = "No pages found",
                    detail = "The source returned an empty page list for this chapter.",
                    onBack = onCloseReader,
                )
            } else {
                ReaderContent(
                    chapter = readerState.chapter,
                    initialBookmarked = initialBookmarked,
                    onLoadReaderPageImage = onLoadReaderPageImage,
                    settings = settings,
                    onSettingsChange = onSettingsChange,
                    onSaveReaderProgress = onSaveReaderProgress,
                    onSetChapterRead = onSetChapterRead,
                    onSetChapterBookmark = onSetChapterBookmark,
                    onEnqueueDownload = onEnqueueDownload,
                    onOpenExternalUrl = onOpenExternalUrl,
                    onOpenReader = onOpenReader,
                    onCloseReader = onCloseReader,
                )
            }
        }
    }
}

@Composable
private fun ReaderStatusScreen(
    request: ChimahonReaderRequest,
    marker: String,
    title: String,
    detail: String,
    onBack: () -> Unit,
    action: String? = null,
    onAction: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ReaderPalette.background),
    ) {
        ReaderTopBar(
            request = request,
            currentPage = 0,
            pageCount = 0,
            bookmarked = false,
            bookmarkBusy = false,
            onBack = onBack,
            onToggleBookmark = null,
        )
        Box(modifier = Modifier.weight(1f)) {
            ReaderMessage(
                marker = marker,
                title = title,
                detail = detail,
                action = action,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun ReaderContent(
    chapter: ChimahonReaderChapter,
    initialBookmarked: Boolean,
    onLoadReaderPageImage: suspend (ChimahonReaderPage) -> ByteArray,
    settings: ChimahonReaderSettings,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
    onSaveReaderProgress: suspend (ChimahonReaderRequest, Int, Boolean) -> Unit,
    onSetChapterRead: suspend (Long, Boolean) -> Unit,
    onSetChapterBookmark: suspend (Long, Boolean) -> Unit,
    onEnqueueDownload: suspend (Long) -> ChimahonDownloadQueueData,
    onOpenExternalUrl: (String) -> Boolean,
    onOpenReader: (ChimahonReaderRequest) -> Unit,
    onCloseReader: () -> Unit,
) {
    var readerPreferences by remember(chapter.request) { mutableStateOf(settings) }
    var mode by remember(chapter.request) { mutableStateOf(settings.mode.toUiReaderMode()) }
    var scale by remember(chapter.request.mangaId) { mutableStateOf(settings.scale.toUiReaderScale()) }
    var canvas by remember(chapter.request.mangaId) { mutableStateOf(settings.canvas.toUiReaderCanvas()) }
    var controlsVisible by remember(chapter.request) { mutableStateOf(settings.keepControlsVisible) }
    var settingsVisible by remember(chapter.request) { mutableStateOf(false) }
    var chaptersVisible by remember(chapter.request) { mutableStateOf(false) }
    var statsVisible by remember(chapter.request) { mutableStateOf(false) }
    var showPageStrip by remember(chapter.request.mangaId) { mutableStateOf(settings.showPageStrip) }
    var showControlsOnStart by remember(chapter.request.mangaId) {
        mutableStateOf(settings.keepControlsVisible)
    }
    var bookmarked by remember(chapter.request.chapterId) { mutableStateOf(initialBookmarked) }
    var bookmarkBusy by remember(chapter.request.chapterId) { mutableStateOf(false) }
    var readBusy by remember(chapter.request.chapterId) { mutableStateOf(false) }
    var downloadBusy by remember(chapter.request.chapterId) { mutableStateOf(false) }
    var retainedPage by remember(chapter.request) {
        mutableIntStateOf(chapter.request.initialPage.coerceIn(chapter.pages.indices))
    }
    val pageStates = remember(chapter.request) {
        mutableStateMapOf<Int, ReaderPageUiState>()
    }
    val previousChapter = chapter.request.chapterQueue.getOrNull(chapter.request.chapterIndex + 1)
    val nextChapter = chapter.request.chapterQueue.getOrNull(chapter.request.chapterIndex - 1)
    val scope = rememberCoroutineScope()
    val preloadReaderPage: suspend (Int) -> Unit = { pageIndex ->
        if (pageIndex in chapter.pages.indices && pageStates[pageIndex] == null) {
            pageStates[pageIndex] = ReaderPageUiState.Loading
            pageStates[pageIndex] = runCatching {
                onLoadReaderPageImage(chapter.pages[pageIndex]).decodeToImageBitmap()
            }.fold(
                onSuccess = ReaderPageUiState::Ready,
                onFailure = { ReaderPageUiState.Failed(it.message ?: "Unable to decode page image.") },
            )
        }
    }
    val saveReaderPreferences: (ChimahonReaderSettings) -> Unit = { updated ->
        readerPreferences = updated
        onSettingsChange(updated)
    }
    val openAdjacentChapter: (ChimahonReaderChapterRef) -> Unit = { adjacent ->
        onOpenReader(adjacent.toReaderRequest(chapter.request))
    }
    val bookmarkChapterRef: (ChimahonReaderChapterRef) -> Unit = { ref ->
        ref.chapterId?.let { chapterId ->
            scope.launch {
                runCatching { onSetChapterBookmark(chapterId, !ref.bookmarked) }
            }
        }
    }
    val downloadChapterRef: (ChimahonReaderChapterRef) -> Unit = { ref ->
        ref.chapterId?.let { chapterId ->
            scope.launch {
                runCatching { onEnqueueDownload(chapterId) }
            }
        }
    }
    val markChapterRefRead: (ChimahonReaderChapterRef) -> Unit = { ref ->
        ref.chapterId?.let { chapterId ->
            scope.launch {
                runCatching { onSetChapterRead(chapterId, !ref.read) }
            }
        }
    }
    val markChapterRead = chapter.request.chapterId?.let { chapterId ->
        {
            if (!readBusy) {
                scope.launch {
                    readBusy = true
                    runCatching {
                        onSetChapterRead(chapterId, true)
                        onSaveReaderProgress(chapter.request, chapter.pages.lastIndex, true)
                    }
                    readBusy = false
                }
            }
        }
    }
    val downloadChapter = chapter.request.chapterId?.let { chapterId ->
        {
            if (!downloadBusy) {
                scope.launch {
                    downloadBusy = true
                    runCatching { onEnqueueDownload(chapterId) }
                    downloadBusy = false
                }
            }
        }
    }
    val openChapterUrl: (() -> Unit)? = chapter.request.chapterUrl
        .takeIf { it.isNotBlank() }
        ?.let { url -> { onOpenExternalUrl(url) } }
    val toggleControls = {
        controlsVisible = !controlsVisible
        if (!controlsVisible) {
            settingsVisible = false
            chaptersVisible = false
            statsVisible = false
        }
    }

    key(mode, readerPreferences.dualPageMode) {
        if (mode.paged) {
            val dualPage = readerPreferences.dualPageMode != ChimahonDualPageMode.Off
            val spreadGroups = remember(chapter.pages, dualPage) {
                if (dualPage) {
                    chapter.pages.indices.chunked(2)
                } else {
                    chapter.pages.indices.map { listOf(it) }
                }
            }
            val retainedGroupIndex = spreadGroups
                .indexOfFirst { retainedPage in it }
                .takeIf { it >= 0 }
                ?: 0
            val initialPagerPage = if (mode.rightToLeft) {
                spreadGroups.lastIndex - retainedGroupIndex
            } else {
                retainedGroupIndex
            }.coerceIn(spreadGroups.indices)
            val pagerState = rememberPagerState(
                initialPage = initialPagerPage,
                pageCount = { spreadGroups.size },
            )
            val actualGroupIndex = if (mode.rightToLeft) {
                spreadGroups.lastIndex - pagerState.currentPage
            } else {
                pagerState.currentPage
            }.coerceIn(spreadGroups.indices)
            val actualPage = spreadGroups.getOrNull(actualGroupIndex)?.firstOrNull() ?: 0
            LaunchedEffect(chapter.request, pagerState, mode, spreadGroups) {
                snapshotFlow { pagerState.currentPage }
                    .distinctUntilChanged()
                    .collect { pagerPage ->
                        val groupIndex = if (mode.rightToLeft) {
                            spreadGroups.lastIndex - pagerPage
                        } else {
                            pagerPage
                        }.coerceIn(spreadGroups.indices)
                        val pageIndex = spreadGroups.getOrNull(groupIndex)?.firstOrNull() ?: 0
                        retainedPage = pageIndex
                        onSaveReaderProgress(
                            chapter.request,
                            pageIndex,
                            pageIndex == chapter.pages.lastIndex,
                        )
                    }
            }
            LaunchedEffect(chapter.request, actualGroupIndex, spreadGroups) {
                listOf(actualGroupIndex - 1, actualGroupIndex + 1)
                    .filter { it in spreadGroups.indices }
                    .flatMap { spreadGroups[it] }
                    .distinct()
                    .forEach { preloadReaderPage(it) }
            }
            ReaderScaffold(
                request = chapter.request,
                mode = mode,
                scale = scale,
                canvas = canvas,
                currentPage = actualPage + 1,
                pageCount = chapter.pages.size,
                bookmarked = bookmarked,
                bookmarkBusy = bookmarkBusy,
                readBusy = readBusy,
                downloadBusy = downloadBusy,
                controlsVisible = controlsVisible,
                settingsVisible = settingsVisible,
                chaptersVisible = chaptersVisible,
                statsVisible = statsVisible,
                showPageStrip = showPageStrip,
                showControlsOnStart = showControlsOnStart,
                readerSettings = readerPreferences.copy(
                    mode = mode.toSettingsReaderMode(),
                    scale = scale.toSettingsReaderScale(),
                    canvas = canvas.toSettingsReaderCanvas(),
                    showPageStrip = showPageStrip,
                    keepControlsVisible = showControlsOnStart,
                ),
                onReaderSettingsChange = saveReaderPreferences,
                previousChapter = previousChapter,
                nextChapter = nextChapter,
                onModeChange = {
                    retainedPage = actualPage
                    mode = it
                    saveReaderPreferences(
                        readerPreferences.copy(
                            mode = it.toSettingsReaderMode(),
                            scale = scale.toSettingsReaderScale(),
                            canvas = canvas.toSettingsReaderCanvas(),
                            showPageStrip = showPageStrip,
                            keepControlsVisible = showControlsOnStart,
                        ),
                    )
                },
                onScaleChange = {
                    scale = it
                    saveReaderPreferences(
                        readerPreferences.copy(
                            mode = mode.toSettingsReaderMode(),
                            scale = it.toSettingsReaderScale(),
                            canvas = canvas.toSettingsReaderCanvas(),
                            showPageStrip = showPageStrip,
                            keepControlsVisible = showControlsOnStart,
                        ),
                    )
                },
                onCanvasChange = {
                    canvas = it
                    saveReaderPreferences(
                        readerPreferences.copy(
                            mode = mode.toSettingsReaderMode(),
                            scale = scale.toSettingsReaderScale(),
                            canvas = it.toSettingsReaderCanvas(),
                            showPageStrip = showPageStrip,
                            keepControlsVisible = showControlsOnStart,
                        ),
                    )
                },
                onShowPageStripChange = {
                    showPageStrip = it
                    saveReaderPreferences(
                        readerPreferences.copy(
                            mode = mode.toSettingsReaderMode(),
                            scale = scale.toSettingsReaderScale(),
                            canvas = canvas.toSettingsReaderCanvas(),
                            showPageStrip = it,
                            keepControlsVisible = showControlsOnStart,
                        ),
                    )
                },
                onShowControlsOnStartChange = {
                    showControlsOnStart = it
                    saveReaderPreferences(
                        readerPreferences.copy(
                            mode = mode.toSettingsReaderMode(),
                            scale = scale.toSettingsReaderScale(),
                            canvas = canvas.toSettingsReaderCanvas(),
                            showPageStrip = showPageStrip,
                            keepControlsVisible = it,
                        ),
                    )
                },
                onToggleControls = toggleControls,
                onDismissPanels = {
                    settingsVisible = false
                    chaptersVisible = false
                    statsVisible = false
                },
                onToggleSettings = {
                    controlsVisible = true
                    settingsVisible = !settingsVisible
                    if (settingsVisible) chaptersVisible = false
                    if (settingsVisible) statsVisible = false
                },
                onToggleChapters = {
                    controlsVisible = true
                    chaptersVisible = !chaptersVisible
                    if (chaptersVisible) settingsVisible = false
                    if (chaptersVisible) statsVisible = false
                },
                onToggleStats = {
                    controlsVisible = true
                    statsVisible = !statsVisible
                    if (statsVisible) {
                        settingsVisible = false
                        chaptersVisible = false
                    }
                },
                onToggleCrop = {
                    val updated = readerPreferences.copy(cropBorders = !readerPreferences.cropBorders)
                    readerPreferences = updated
                    saveReaderPreferences(updated)
                },
                onMarkChapterRead = markChapterRead,
                onDownloadChapter = downloadChapter,
                onOpenChapterUrl = openChapterUrl,
                onBookmarkChapterRef = bookmarkChapterRef,
                onDownloadChapterRef = downloadChapterRef,
                onMarkChapterRefRead = markChapterRefRead,
                onToggleBookmark = chapter.request.chapterId?.let { chapterId ->
                    {
                        if (!bookmarkBusy) {
                            scope.launch {
                                bookmarkBusy = true
                                val newValue = !bookmarked
                                runCatching { onSetChapterBookmark(chapterId, newValue) }
                                    .onSuccess { bookmarked = newValue }
                                bookmarkBusy = false
                            }
                        }
                    }
                },
                onOpenChapter = openAdjacentChapter,
                onPreviousChapter = previousChapter?.let { { openAdjacentChapter(it) } },
                onNextChapter = nextChapter?.let { { openAdjacentChapter(it) } },
                onPreviousPage = {
                    val target = if (mode.rightToLeft) {
                        pagerState.currentPage + 1
                    } else {
                        pagerState.currentPage - 1
                    }
                    if (target in spreadGroups.indices) {
                        scope.launch {
                            if (readerPreferences.pageTransitions) {
                                pagerState.animateScrollToPage(target)
                            } else {
                                pagerState.scrollToPage(target)
                            }
                        }
                    } else {
                        previousChapter?.let(openAdjacentChapter)
                    }
                },
                onNextPage = {
                    val target = if (mode.rightToLeft) {
                        pagerState.currentPage - 1
                    } else {
                        pagerState.currentPage + 1
                    }
                    if (target in spreadGroups.indices) {
                        scope.launch {
                            if (readerPreferences.pageTransitions) {
                                pagerState.animateScrollToPage(target)
                            } else {
                                pagerState.scrollToPage(target)
                            }
                        }
                    } else {
                        nextChapter?.let(openAdjacentChapter)
                    }
                },
                onPageSelected = { pageIndex ->
                    val groupIndex = spreadGroups
                        .indexOfFirst { pageIndex in it }
                        .takeIf { it >= 0 }
                        ?: 0
                    val target = if (mode.rightToLeft) {
                        spreadGroups.lastIndex - groupIndex
                    } else {
                        groupIndex
                    }
                    scope.launch { pagerState.scrollToPage(target) }
                },
                onBack = onCloseReader,
            ) {
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = readerPreferences.swipeNavigationEnabled,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(canvas.readerBackground(readerPreferences.pureBlackBackground)),
                    key = { pagerPage ->
                        val groupIndex = if (mode.rightToLeft) {
                            spreadGroups.lastIndex - pagerPage
                        } else {
                            pagerPage
                        }.coerceIn(spreadGroups.indices)
                        spreadGroups[groupIndex].joinToString("|") { pageIndex ->
                            val page = chapter.pages[pageIndex]
                            "${page.index}:${page.url}"
                        }
                    },
                ) { pagerPage ->
                    val groupIndex = if (mode.rightToLeft) {
                        spreadGroups.lastIndex - pagerPage
                    } else {
                        pagerPage
                    }.coerceIn(spreadGroups.indices)
                    val spread = spreadGroups[groupIndex]
                    if (spread.size == 1) {
                        val pageIndex = spread.first()
                        ReaderPageImage(
                            request = chapter.request,
                            page = chapter.pages[pageIndex],
                            paged = true,
                            scale = scale,
                            canvas = canvas,
                            readerSettings = readerPreferences,
                            cachedState = pageStates[pageIndex],
                            onStateChange = { pageStates[pageIndex] = it },
                            onLoadReaderPageImage = onLoadReaderPageImage,
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val visibleSpread = if (mode.rightToLeft) spread.reversed() else spread
                            visibleSpread.forEach { pageIndex ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    ReaderPageImage(
                                        request = chapter.request,
                                        page = chapter.pages[pageIndex],
                                        paged = true,
                                        scale = scale,
                                        canvas = canvas,
                                        readerSettings = readerPreferences,
                                        cachedState = pageStates[pageIndex],
                                        onStateChange = { pageStates[pageIndex] = it },
                                        onLoadReaderPageImage = onLoadReaderPageImage,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            val listState = rememberLazyListState(initialFirstVisibleItemIndex = retainedPage)
            val visibleListPage = listState.firstVisibleItemIndex
            LaunchedEffect(chapter.request, listState, mode) {
                snapshotFlow { listState.firstVisibleItemIndex }
                    .distinctUntilChanged()
                    .collect { pageIndex ->
                        retainedPage = pageIndex
                        onSaveReaderProgress(
                            chapter.request,
                            pageIndex,
                            pageIndex == chapter.pages.lastIndex,
                        )
                    }
            }
            LaunchedEffect(chapter.request, visibleListPage) {
                (visibleListPage + 1..visibleListPage + 3).forEach { preloadReaderPage(it) }
            }
            ReaderScaffold(
                request = chapter.request,
                mode = mode,
                scale = scale,
                canvas = canvas,
                currentPage = visibleListPage + 1,
                pageCount = chapter.pages.size,
                bookmarked = bookmarked,
                bookmarkBusy = bookmarkBusy,
                readBusy = readBusy,
                downloadBusy = downloadBusy,
                controlsVisible = controlsVisible,
                settingsVisible = settingsVisible,
                chaptersVisible = chaptersVisible,
                statsVisible = statsVisible,
                showPageStrip = showPageStrip,
                showControlsOnStart = showControlsOnStart,
                readerSettings = readerPreferences.copy(
                    mode = mode.toSettingsReaderMode(),
                    scale = scale.toSettingsReaderScale(),
                    canvas = canvas.toSettingsReaderCanvas(),
                    showPageStrip = showPageStrip,
                    keepControlsVisible = showControlsOnStart,
                ),
                onReaderSettingsChange = saveReaderPreferences,
                previousChapter = previousChapter,
                nextChapter = nextChapter,
                onModeChange = {
                    retainedPage = listState.firstVisibleItemIndex
                    mode = it
                    saveReaderPreferences(
                        readerPreferences.copy(
                            mode = it.toSettingsReaderMode(),
                            scale = scale.toSettingsReaderScale(),
                            canvas = canvas.toSettingsReaderCanvas(),
                            showPageStrip = showPageStrip,
                            keepControlsVisible = showControlsOnStart,
                        ),
                    )
                },
                onScaleChange = {
                    scale = it
                    saveReaderPreferences(
                        readerPreferences.copy(
                            mode = mode.toSettingsReaderMode(),
                            scale = it.toSettingsReaderScale(),
                            canvas = canvas.toSettingsReaderCanvas(),
                            showPageStrip = showPageStrip,
                            keepControlsVisible = showControlsOnStart,
                        ),
                    )
                },
                onCanvasChange = {
                    canvas = it
                    saveReaderPreferences(
                        readerPreferences.copy(
                            mode = mode.toSettingsReaderMode(),
                            scale = scale.toSettingsReaderScale(),
                            canvas = it.toSettingsReaderCanvas(),
                            showPageStrip = showPageStrip,
                            keepControlsVisible = showControlsOnStart,
                        ),
                    )
                },
                onShowPageStripChange = {
                    showPageStrip = it
                    saveReaderPreferences(
                        readerPreferences.copy(
                            mode = mode.toSettingsReaderMode(),
                            scale = scale.toSettingsReaderScale(),
                            canvas = canvas.toSettingsReaderCanvas(),
                            showPageStrip = it,
                            keepControlsVisible = showControlsOnStart,
                        ),
                    )
                },
                onShowControlsOnStartChange = {
                    showControlsOnStart = it
                    saveReaderPreferences(
                        readerPreferences.copy(
                            mode = mode.toSettingsReaderMode(),
                            scale = scale.toSettingsReaderScale(),
                            canvas = canvas.toSettingsReaderCanvas(),
                            showPageStrip = showPageStrip,
                            keepControlsVisible = it,
                        ),
                    )
                },
                onToggleControls = toggleControls,
                onDismissPanels = {
                    settingsVisible = false
                    chaptersVisible = false
                    statsVisible = false
                },
                onToggleSettings = {
                    controlsVisible = true
                    settingsVisible = !settingsVisible
                    if (settingsVisible) chaptersVisible = false
                    if (settingsVisible) statsVisible = false
                },
                onToggleChapters = {
                    controlsVisible = true
                    chaptersVisible = !chaptersVisible
                    if (chaptersVisible) settingsVisible = false
                    if (chaptersVisible) statsVisible = false
                },
                onToggleStats = {
                    controlsVisible = true
                    statsVisible = !statsVisible
                    if (statsVisible) {
                        settingsVisible = false
                        chaptersVisible = false
                    }
                },
                onToggleCrop = {
                    val updated = readerPreferences.copy(cropBorders = !readerPreferences.cropBorders)
                    readerPreferences = updated
                    saveReaderPreferences(updated)
                },
                onMarkChapterRead = markChapterRead,
                onDownloadChapter = downloadChapter,
                onOpenChapterUrl = openChapterUrl,
                onBookmarkChapterRef = bookmarkChapterRef,
                onDownloadChapterRef = downloadChapterRef,
                onMarkChapterRefRead = markChapterRefRead,
                onToggleBookmark = chapter.request.chapterId?.let { chapterId ->
                    {
                        if (!bookmarkBusy) {
                            scope.launch {
                                bookmarkBusy = true
                                val newValue = !bookmarked
                                runCatching { onSetChapterBookmark(chapterId, newValue) }
                                    .onSuccess { bookmarked = newValue }
                                bookmarkBusy = false
                            }
                        }
                    }
                },
                onOpenChapter = openAdjacentChapter,
                onPreviousChapter = previousChapter?.let { { openAdjacentChapter(it) } },
                onNextChapter = nextChapter?.let { { openAdjacentChapter(it) } },
                onPreviousPage = {
                    if (listState.firstVisibleItemIndex > 0) {
                        scope.launch {
                            val target = listState.firstVisibleItemIndex - 1
                            if (readerPreferences.pageTransitions) {
                                listState.animateScrollToItem(target)
                            } else {
                                listState.scrollToItem(target)
                            }
                        }
                    } else {
                        previousChapter?.let(openAdjacentChapter)
                    }
                },
                onNextPage = {
                    if (listState.firstVisibleItemIndex < chapter.pages.lastIndex) {
                        scope.launch {
                            val target = listState.firstVisibleItemIndex + 1
                            if (readerPreferences.pageTransitions) {
                                listState.animateScrollToItem(target)
                            } else {
                                listState.scrollToItem(target)
                            }
                        }
                    } else {
                        nextChapter?.let(openAdjacentChapter)
                    }
                },
                onPageSelected = { pageIndex ->
                    scope.launch { listState.scrollToItem(pageIndex) }
                },
                onBack = onCloseReader,
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(canvas.readerBackground(readerPreferences.pureBlackBackground)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = if (mode == ReaderMode.Vertical) {
                        Arrangement.spacedBy(12.dp)
                    } else {
                        Arrangement.Top
                    },
                    contentPadding = if (mode == ReaderMode.Vertical) {
                        PaddingValues(vertical = 12.dp)
                    } else {
                        PaddingValues()
                    },
                ) {
                    items(chapter.pages, key = { "${it.index}:${it.url}" }) { page ->
                        ReaderPageImage(
                            request = chapter.request,
                            page = page,
                            paged = false,
                            scale = scale,
                            canvas = canvas,
                            readerSettings = readerPreferences,
                            cachedState = pageStates[page.index],
                            onStateChange = { pageStates[page.index] = it },
                            onLoadReaderPageImage = onLoadReaderPageImage,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReaderScaffold(
    request: ChimahonReaderRequest,
    mode: ReaderMode,
    scale: ReaderScale,
    canvas: ReaderCanvas,
    currentPage: Int,
    pageCount: Int,
    bookmarked: Boolean,
    bookmarkBusy: Boolean,
    readBusy: Boolean,
    downloadBusy: Boolean,
    controlsVisible: Boolean,
    settingsVisible: Boolean,
    chaptersVisible: Boolean,
    statsVisible: Boolean,
    showPageStrip: Boolean,
    showControlsOnStart: Boolean,
    readerSettings: ChimahonReaderSettings,
    onReaderSettingsChange: (ChimahonReaderSettings) -> Unit,
    previousChapter: ChimahonReaderChapterRef?,
    nextChapter: ChimahonReaderChapterRef?,
    onModeChange: (ReaderMode) -> Unit,
    onScaleChange: (ReaderScale) -> Unit,
    onCanvasChange: (ReaderCanvas) -> Unit,
    onShowPageStripChange: (Boolean) -> Unit,
    onShowControlsOnStartChange: (Boolean) -> Unit,
    onToggleControls: () -> Unit,
    onDismissPanels: () -> Unit,
    onToggleSettings: () -> Unit,
    onToggleChapters: () -> Unit,
    onToggleStats: () -> Unit,
    onToggleCrop: () -> Unit,
    onMarkChapterRead: (() -> Unit)?,
    onDownloadChapter: (() -> Unit)?,
    onOpenChapterUrl: (() -> Unit)?,
    onBookmarkChapterRef: (ChimahonReaderChapterRef) -> Unit,
    onDownloadChapterRef: (ChimahonReaderChapterRef) -> Unit,
    onMarkChapterRefRead: (ChimahonReaderChapterRef) -> Unit,
    onToggleBookmark: (() -> Unit)?,
    onOpenChapter: (ChimahonReaderChapterRef) -> Unit,
    onPreviousChapter: (() -> Unit)?,
    onNextChapter: (() -> Unit)?,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onPageSelected: (Int) -> Unit,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val effectiveNavigationMode = when (readerSettings.navigationMode) {
        ChimahonReaderNavigationMode.Automatic -> when {
            mode.rightToLeft -> ChimahonReaderNavigationMode.RightToLeft
            mode == ReaderMode.Webtoon || mode == ReaderMode.Vertical -> ChimahonReaderNavigationMode.Vertical
            else -> ChimahonReaderNavigationMode.LeftToRight
        }
        else -> readerSettings.navigationMode
    }

    LaunchedEffect(request) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(canvas.readerBackground(readerSettings.pureBlackBackground))
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (event.key) {
                    Key.VolumeUp -> {
                        if (!readerSettings.volumeKeysEnabled) {
                            false
                        } else {
                            if (readerSettings.volumeKeysInverted) onNextPage() else onPreviousPage()
                            true
                        }
                    }
                    Key.VolumeDown -> {
                        if (!readerSettings.volumeKeysEnabled) {
                            false
                        } else {
                            if (readerSettings.volumeKeysInverted) onPreviousPage() else onNextPage()
                            true
                        }
                    }
                    Key.DirectionLeft,
                    Key.PageUp,
                    Key.DirectionUp,
                    -> {
                        onPreviousPage()
                        true
                    }
                    Key.DirectionRight,
                    Key.Spacebar,
                    Key.PageDown,
                    Key.DirectionDown,
                    -> {
                        onNextPage()
                        true
                    }
                    Key.Enter -> {
                        onToggleControls()
                        true
                    }
                    Key.MoveHome -> {
                        onPageSelected(0)
                        true
                    }
                    Key.MoveEnd -> {
                        onPageSelected(pageCount - 1)
                        true
                    }
                    Key.Escape -> {
                        when {
                            settingsVisible || chaptersVisible || statsVisible -> onDismissPanels()
                            controlsVisible -> onToggleControls()
                            else -> onBack()
                        }
                        true
                    }
                    else -> false
                }
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(currentPage, pageCount, controlsVisible, readerSettings) {
                    detectTapGestures(
                        onLongPress = {
                            if (readerSettings.longTapEnabled) onToggleControls()
                        },
                        onDoubleTap = {
                            if (readerSettings.doubleTapToZoom) {
                                onScaleChange(
                                    if (scale == ReaderScale.FitWidth) {
                                        ReaderScale.FitScreen
                                    } else {
                                        ReaderScale.FitWidth
                                    },
                                )
                            }
                        },
                        onTap = { offset ->
                            if (!readerSettings.tapZonesEnabled || size.width <= 0 || size.height <= 0) {
                                onToggleControls()
                                return@detectTapGestures
                            }
                            when (
                                readerTapActionAt(
                                    offset = offset,
                                    width = size.width,
                                    height = size.height,
                                    smallerZones = readerSettings.smallerTapZones,
                                    tapZonePercent = readerSettings.tapZonePercent,
                                    inversion = readerSettings.invertTapZones,
                                    navigationMode = effectiveNavigationMode,
                                )
                            ) {
                                ReaderTapAction.Previous -> onPreviousPage()
                                ReaderTapAction.Menu -> onToggleControls()
                                ReaderTapAction.Next -> onNextPage()
                            }
                        },
                    )
                },
        ) {
            content()
        }

        if (!controlsVisible && pageCount > 0 && readerSettings.showPageNumber) {
            ReaderPageIndicator(
                currentPage = currentPage,
                pageCount = pageCount,
                modifier = Modifier
                    .align(if (readerSettings.showProgressTop) Alignment.TopEnd else Alignment.BottomEnd)
                    .padding(16.dp),
            )
        }

        if (controlsVisible) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ReaderTopBar(
                    request = request,
                    currentPage = currentPage,
                    pageCount = pageCount,
                    canvas = canvas,
                    showTitle = readerSettings.showTitle,
                    bookmarked = bookmarked,
                    bookmarkBusy = bookmarkBusy,
                    onBack = onBack,
                    onToggleHud = onToggleControls,
                    onToggleBookmark = onToggleBookmark,
                    downloadBusy = downloadBusy,
                    onDownloadChapter = onDownloadChapter,
                    readBusy = readBusy,
                    onMarkChapterRead = onMarkChapterRead,
                )
                if (readerSettings.keepScreenOn) {
                    Label(
                        text = "Screen stays awake while reading",
                        color = Color.White,
                        size = 10,
                        weight = FontWeight.SemiBold,
                        maxLines = 1,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ReaderPalette.selectedControl.copy(alpha = 0.9f))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (statsVisible) {
                    ReaderStatisticsPanel(
                        request = request,
                        mode = mode,
                        scale = scale,
                        currentPage = currentPage,
                        pageCount = pageCount,
                        previousChapter = previousChapter,
                        nextChapter = nextChapter,
                        modifier = Modifier
                            .fillMaxWidth(0.96f)
                            .widthIn(max = 680.dp)
                            .padding(bottom = 8.dp),
                    )
                }
                if (chaptersVisible) {
                    ReaderChapterQueuePanel(
                        request = request,
                        previousChapter = previousChapter,
                        nextChapter = nextChapter,
                        onOpenChapter = onOpenChapter,
                        onBookmarkChapter = onBookmarkChapterRef,
                        onDownloadChapter = onDownloadChapterRef,
                        onMarkChapterRead = onMarkChapterRefRead,
                        modifier = Modifier
                            .fillMaxWidth(0.96f)
                            .widthIn(max = 760.dp)
                            .padding(bottom = 8.dp),
                    )
                }
                if (settingsVisible) {
                    ReaderSettingsPanel(
                        mode = mode,
                        scale = scale,
                        canvas = canvas,
                        settings = readerSettings,
                        onModeChange = onModeChange,
                        onScaleChange = onScaleChange,
                        onCanvasChange = onCanvasChange,
                        onSettingsChange = { updated ->
                            if (updated.showPageStrip != showPageStrip) {
                                onShowPageStripChange(updated.showPageStrip)
                            } else if (updated.keepControlsVisible != showControlsOnStart) {
                                onShowControlsOnStartChange(updated.keepControlsVisible)
                            } else {
                                onReaderSettingsChange(updated)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.96f)
                            .widthIn(max = 680.dp)
                            .padding(bottom = 8.dp),
                    )
                }
                if (showPageStrip) {
                    ReaderPageStrip(
                        currentPage = currentPage,
                        pageCount = pageCount,
                        canvas = canvas,
                        onPageSelected = onPageSelected,
                        onPreviousChapter = onPreviousChapter,
                        onNextChapter = onNextChapter,
                        isRtl = mode.rightToLeft,
                    )
                }
                ReaderControlBar(
                    mode = mode,
                    canvas = canvas,
                    currentPage = currentPage,
                    pageCount = pageCount,
                    settingsVisible = settingsVisible,
                    chaptersVisible = chaptersVisible,
                    statsVisible = statsVisible,
                    cropActive = readerSettings.cropBorders,
                    showPercentage = readerSettings.showPercentage,
                    onOpenChapterUrl = onOpenChapterUrl,
                    onCycleMode = {
                        val nextIndex = (ReaderMode.entries.indexOf(mode) + 1) % ReaderMode.entries.size
                        onModeChange(ReaderMode.entries[nextIndex])
                    },
                    onToggleSettings = onToggleSettings,
                    onToggleChapters = onToggleChapters,
                    onToggleStats = onToggleStats,
                    onToggleCrop = onToggleCrop,
                )
            }
        }
    }
}

private enum class ReaderTapAction {
    Previous,
    Menu,
    Next,
}

private fun readerTapActionAt(
    offset: Offset,
    width: Int,
    height: Int,
    smallerZones: Boolean,
    tapZonePercent: Int,
    inversion: ChimahonTapZoneInvert,
    navigationMode: ChimahonReaderNavigationMode,
): ReaderTapAction {
    var x = (offset.x / width.toFloat()).coerceIn(0f, 1f)
    var y = (offset.y / height.toFloat()).coerceIn(0f, 1f)
    if (inversion == ChimahonTapZoneInvert.Horizontal || inversion == ChimahonTapZoneInvert.Both) {
        x = 1f - x
    }
    if (inversion == ChimahonTapZoneInvert.Vertical || inversion == ChimahonTapZoneInvert.Both) {
        y = 1f - y
    }
    if (navigationMode == ChimahonReaderNavigationMode.RightToLeft) {
        x = 1f - x
    }
    val configuredEdge = tapZonePercent.coerceIn(0, 45) / 100f
    val edge = if (smallerZones) min(configuredEdge, 0.22f) else configuredEdge
    if (navigationMode == ChimahonReaderNavigationMode.Vertical) {
        return when {
            y < edge -> ReaderTapAction.Previous
            y > 1f - edge -> ReaderTapAction.Next
            else -> ReaderTapAction.Menu
        }
    }
    return when {
        x < edge || (x < 1f - edge && y < edge) -> ReaderTapAction.Previous
        x > 1f - edge || (x > edge && y > 1f - edge) -> ReaderTapAction.Next
        else -> ReaderTapAction.Menu
    }
}

@Composable
private fun ReaderPageIndicator(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    Label(
        text = "$currentPage / $pageCount",
        color = Color.White,
        size = 11,
        weight = FontWeight.Bold,
        maxLines = 1,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ReaderPalette.chrome.copy(alpha = 0.78f))
            .padding(horizontal = 9.dp, vertical = 5.dp),
    )
}

@Composable
private fun ReaderPageStrip(
    currentPage: Int,
    pageCount: Int,
    canvas: ReaderCanvas,
    onPageSelected: (Int) -> Unit,
    onPreviousChapter: (() -> Unit)?,
    onNextChapter: (() -> Unit)?,
    isRtl: Boolean = false,
) {
    var scrubPage by remember(pageCount) {
        mutableIntStateOf((currentPage - 1).coerceIn(0, (pageCount - 1).coerceAtLeast(0)))
    }
    LaunchedEffect(currentPage, pageCount) {
        scrubPage = (currentPage - 1).coerceIn(0, (pageCount - 1).coerceAtLeast(0))
    }
    val selectPage: (Int) -> Unit = { pageIndex ->
        if (pageIndex != scrubPage) {
            scrubPage = pageIndex
            onPageSelected(pageIndex)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ReaderNavigatorChapterAction(
            icon = UiIcon.SkipPrevious,
            contentDescription = "Previous chapter",
            canvas = canvas,
            onClick = onPreviousChapter,
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(readerHudBackground(canvas))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Label(
                text = currentPage.toString(),
                color = readerHudContent(canvas),
                size = 11,
                weight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier.width(32.dp),
            )
            val progress = if (pageCount <= 1) {
                0f
            } else {
                (currentPage - 1).toFloat() / (pageCount - 1).toFloat()
            }
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .height(28.dp)
                    .semantics {
                        contentDescription = "Page $currentPage of $pageCount"
                    }
                    .pointerInput(pageCount, isRtl) {
                        detectTapGestures { offset ->
                            selectPage(
                                readerPageIndexAt(
                                    x = offset.x,
                                    width = size.width,
                                    pageCount = pageCount,
                                    isRtl = isRtl,
                                ),
                            )
                        }
                    }
                    .pointerInput(pageCount, isRtl) {
                        detectHorizontalDragGestures { change, _ ->
                            selectPage(
                                readerPageIndexAt(
                                    x = change.position.x,
                                    width = size.width,
                                    pageCount = pageCount,
                                    isRtl = isRtl,
                                ),
                            )
                        }
                    },
            ) {
                val y = size.height / 2f
                val visualProgress = if (isRtl) 1f - progress else progress
                val thumbX = size.width * visualProgress
                drawLine(
                    color = readerHudContent(canvas).copy(alpha = 0.20f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 5f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = ReaderPalette.selectedControl,
                    start = if (isRtl) Offset(size.width, y) else Offset(0f, y),
                    end = Offset(thumbX, y),
                    strokeWidth = 5f,
                    cap = StrokeCap.Round,
                )
                drawCircle(
                    color = readerHudContent(canvas),
                    radius = 7f,
                    center = Offset(thumbX, y),
                )
            }
            Label(
                text = pageCount.toString(),
                color = readerHudContent(canvas).copy(alpha = 0.62f),
                size = 11,
                weight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier.width(32.dp),
            )
        }
        ReaderNavigatorChapterAction(
            icon = UiIcon.SkipNext,
            contentDescription = "Next chapter",
            canvas = canvas,
            onClick = onNextChapter,
        )
    }
}

private fun readerPageIndexAt(
    x: Float,
    width: Int,
    pageCount: Int,
    isRtl: Boolean,
): Int {
    if (width <= 0 || pageCount <= 1) return 0
    val visualFraction = (x / width.toFloat()).coerceIn(0f, 1f)
    val logicalFraction = if (isRtl) 1f - visualFraction else visualFraction
    return (logicalFraction * (pageCount - 1).toFloat() + 0.5f)
        .toInt()
        .coerceIn(0, pageCount - 1)
}

@Composable
private fun ReaderNavigatorChapterAction(
    icon: UiIcon,
    contentDescription: String,
    canvas: ReaderCanvas,
    onClick: (() -> Unit)?,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(readerHudBackground(canvas))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(10.dp),
        contentAlignment = Alignment.Center,
    ) {
        IconGlyph(
            icon = icon,
            contentDescription = contentDescription,
            tint = if (onClick != null) readerHudContent(canvas) else readerHudContent(canvas).copy(alpha = 0.28f),
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun ReaderSettingsPanel(
    mode: ReaderMode,
    scale: ReaderScale,
    canvas: ReaderCanvas,
    settings: ChimahonReaderSettings,
    onModeChange: (ReaderMode) -> Unit,
    onScaleChange: (ReaderScale) -> Unit,
    onCanvasChange: (ReaderCanvas) -> Unit,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .heightIn(max = 560.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ReaderPalette.chrome.copy(alpha = 0.97f))
            .border(1.dp, ReaderPalette.divider, RoundedCornerShape(8.dp))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Label(
            "Appearance",
            Color.White,
            16,
            weight = FontWeight.SemiBold,
            maxLines = 1,
        )
        ReaderSheetSectionTitle("Theme")
        ReaderThemeSwatchRow(
            selected = canvas,
            onSelect = onCanvasChange,
        )
        ReaderToggleRow(
            label = "Pure black background",
            checked = settings.pureBlackBackground,
            onCheckedChange = { onSettingsChange(settings.copy(pureBlackBackground = it)) },
        )
        ReaderSheetSectionTitle("Mode")
        ReaderOptionRow(
            label = "Reading mode",
            options = ReaderMode.entries.map { it.title },
            selected = mode.title,
            onSelect = { title ->
                ReaderMode.entries.firstOrNull { it.title == title }?.let(onModeChange)
            },
        )
        ReaderOptionRow(
            label = "Orientation",
            options = ChimahonReaderOrientation.entries.map { it.readerTitle() },
            selected = settings.orientation.readerTitle(),
            onSelect = { title ->
                ChimahonReaderOrientation.entries
                    .firstOrNull { it.readerTitle() == title }
                    ?.let { onSettingsChange(settings.copy(orientation = it)) }
            },
        )
        ReaderOptionRow(
            label = "Dual page",
            options = ChimahonDualPageMode.entries.map { it.readerTitle() },
            selected = settings.dualPageMode.readerTitle(),
            onSelect = { title ->
                ChimahonDualPageMode.entries
                    .firstOrNull { it.readerTitle() == title }
                    ?.let { onSettingsChange(settings.copy(dualPageMode = it)) }
            },
        )
        ReaderToggleRow(
            label = "Page scrubber",
            checked = settings.showPageStrip,
            onCheckedChange = { onSettingsChange(settings.copy(showPageStrip = it)) },
        )
        ReaderSheetSectionTitle("Typography")
        ReaderValueStepperRow(
            label = "Font size",
            value = "${settings.fontSize.toCleanDecimal()}px",
            onDecrease = {
                onSettingsChange(settings.copy(fontSize = (settings.fontSize - 0.5).coerceIn(12.0, 72.0)))
            },
            onIncrease = {
                onSettingsChange(settings.copy(fontSize = (settings.fontSize + 0.5).coerceIn(12.0, 72.0)))
            },
        )
        ReaderValueStepperRow(
            label = "Line height",
            value = settings.lineHeight.toCleanDecimal(),
            onDecrease = {
                onSettingsChange(settings.copy(lineHeight = (settings.lineHeight - 0.05).coerceIn(1.0, 2.5)))
            },
            onIncrease = {
                onSettingsChange(settings.copy(lineHeight = (settings.lineHeight + 0.05).coerceIn(1.0, 2.5)))
            },
        )
        ReaderToggleRow(
            label = "Hide furigana",
            checked = settings.hideFurigana,
            onCheckedChange = { onSettingsChange(settings.copy(hideFurigana = it)) },
        )
        ReaderSheetSectionTitle("Margins")
        ReaderValueStepperRow(
            label = "Horizontal padding",
            value = "${settings.horizontalPadding.toCleanDecimal()}%",
            onDecrease = {
                onSettingsChange(
                    settings.copy(horizontalPadding = (settings.horizontalPadding - 0.5).coerceIn(0.0, 50.0)),
                )
            },
            onIncrease = {
                onSettingsChange(
                    settings.copy(horizontalPadding = (settings.horizontalPadding + 0.5).coerceIn(0.0, 50.0)),
                )
            },
        )
        ReaderValueStepperRow(
            label = "Vertical padding",
            value = "${settings.verticalPadding.toCleanDecimal()}%",
            onDecrease = {
                onSettingsChange(
                    settings.copy(verticalPadding = (settings.verticalPadding - 0.5).coerceIn(0.0, 50.0)),
                )
            },
            onIncrease = {
                onSettingsChange(
                    settings.copy(verticalPadding = (settings.verticalPadding + 0.5).coerceIn(0.0, 50.0)),
                )
            },
        )
        ReaderSheetSectionTitle("Image")
        ReaderOptionRow(
            label = "Scale",
            options = ReaderScale.entries.map { it.title },
            selected = scale.title,
            onSelect = { title ->
                ReaderScale.entries.firstOrNull { it.title == title }?.let(onScaleChange)
            },
        )
        ReaderToggleRow(
            label = "Crop borders",
            checked = settings.cropBorders,
            onCheckedChange = { onSettingsChange(settings.copy(cropBorders = it)) },
        )
        ReaderToggleRow(
            label = "Split wide pages",
            checked = settings.splitWidePages,
            onCheckedChange = { onSettingsChange(settings.copy(splitWidePages = it)) },
        )
        ReaderToggleRow(
            label = "Color filter",
            checked = settings.colorFilterEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(colorFilterEnabled = it)) },
        )
        ReaderToggleRow(
            label = "Grayscale",
            checked = settings.grayscale,
            onCheckedChange = { onSettingsChange(settings.copy(grayscale = it, colorFilterEnabled = true)) },
        )
        ReaderToggleRow(
            label = "Invert colors",
            checked = settings.invertColors,
            onCheckedChange = { onSettingsChange(settings.copy(invertColors = it, colorFilterEnabled = true)) },
        )
        ReaderSheetSectionTitle("Navigation")
        ReaderOptionRow(
            label = "Navigation",
            options = ChimahonReaderNavigationMode.entries.map { it.readerTitle() },
            selected = settings.navigationMode.readerTitle(),
            onSelect = { title ->
                ChimahonReaderNavigationMode.entries
                    .firstOrNull { it.readerTitle() == title }
                    ?.let { onSettingsChange(settings.copy(navigationMode = it)) }
            },
        )
        ReaderToggleRow(
            label = "Tap zones",
            checked = settings.tapZonesEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(tapZonesEnabled = it)) },
        )
        ReaderToggleRow(
            label = "Smaller tap zones",
            checked = settings.smallerTapZones,
            onCheckedChange = { onSettingsChange(settings.copy(smallerTapZones = it)) },
        )
        ReaderOptionRow(
            label = "Invert tap zones",
            options = ChimahonTapZoneInvert.entries.map { it.readerTitle() },
            selected = settings.invertTapZones.readerTitle(),
            onSelect = { title ->
                ChimahonTapZoneInvert.entries
                    .firstOrNull { it.readerTitle() == title }
                    ?.let { onSettingsChange(settings.copy(invertTapZones = it)) }
            },
        )
        ReaderToggleRow(
            label = "Page transitions",
            checked = settings.pageTransitions,
            onCheckedChange = { onSettingsChange(settings.copy(pageTransitions = it)) },
        )
        ReaderToggleRow(
            label = "Swipe navigation",
            checked = settings.swipeNavigationEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(swipeNavigationEnabled = it)) },
        )
        ReaderToggleRow(
            label = "Double tap zoom",
            checked = settings.doubleTapToZoom,
            onCheckedChange = { onSettingsChange(settings.copy(doubleTapToZoom = it)) },
        )
        ReaderValueStepperRow(
            label = "Tap zone size",
            value = "${settings.tapZonePercent}%",
            onDecrease = {
                onSettingsChange(settings.copy(tapZonePercent = (settings.tapZonePercent - 1).coerceIn(0, 40)))
            },
            onIncrease = {
                onSettingsChange(settings.copy(tapZonePercent = (settings.tapZonePercent + 1).coerceIn(0, 40)))
            },
        )
        ReaderValueStepperRow(
            label = "Chapter swipe",
            value = "${settings.chapterSwipeDistance}px",
            onDecrease = {
                onSettingsChange(
                    settings.copy(chapterSwipeDistance = (settings.chapterSwipeDistance - 8).coerceIn(32, 256)),
                )
            },
            onIncrease = {
                onSettingsChange(
                    settings.copy(chapterSwipeDistance = (settings.chapterSwipeDistance + 8).coerceIn(32, 256)),
                )
            },
        )
        ReaderSheetSectionTitle("Layout")
        ReaderToggleRow(
            label = "Vertical writing",
            checked = settings.verticalWriting,
            onCheckedChange = { onSettingsChange(settings.copy(verticalWriting = it)) },
        )
        ReaderToggleRow(
            label = "Continuous mode",
            checked = settings.continuousMode,
            onCheckedChange = { onSettingsChange(settings.copy(continuousMode = it)) },
        )
        ReaderToggleRow(
            label = "Avoid page break",
            checked = settings.avoidPageBreak,
            onCheckedChange = { onSettingsChange(settings.copy(avoidPageBreak = it)) },
        )
        ReaderToggleRow(
            label = "Justify text",
            checked = settings.justifyText,
            onCheckedChange = { onSettingsChange(settings.copy(justifyText = it)) },
        )
        ReaderValueStepperRow(
            label = "Character spacing",
            value = settings.characterSpacing.toCleanDecimal(),
            onDecrease = {
                onSettingsChange(
                    settings.copy(characterSpacing = (settings.characterSpacing - 0.05).coerceIn(0.0, 0.5)),
                )
            },
            onIncrease = {
                onSettingsChange(
                    settings.copy(characterSpacing = (settings.characterSpacing + 0.05).coerceIn(0.0, 0.5)),
                )
            },
        )
        ReaderValueStepperRow(
            label = "Paragraph spacing",
            value = "${settings.paragraphSpacing.toCleanDecimal()} em",
            onDecrease = {
                onSettingsChange(
                    settings.copy(paragraphSpacing = (settings.paragraphSpacing - 0.05).coerceIn(0.0, 2.0)),
                )
            },
            onIncrease = {
                onSettingsChange(
                    settings.copy(paragraphSpacing = (settings.paragraphSpacing + 0.05).coerceIn(0.0, 2.0)),
                )
            },
        )
        ReaderSheetSectionTitle("Behavior")
        ReaderToggleRow(
            label = "Show controls on start",
            checked = settings.keepControlsVisible,
            onCheckedChange = { onSettingsChange(settings.copy(keepControlsVisible = it)) },
        )
        ReaderToggleRow(
            label = "Show page number",
            checked = settings.showPageNumber,
            onCheckedChange = { onSettingsChange(settings.copy(showPageNumber = it)) },
        )
        ReaderToggleRow(
            label = "Show title",
            checked = settings.showTitle,
            onCheckedChange = { onSettingsChange(settings.copy(showTitle = it)) },
        )
        ReaderToggleRow(
            label = "Show characters",
            checked = settings.showCharacters,
            onCheckedChange = { onSettingsChange(settings.copy(showCharacters = it)) },
        )
        ReaderToggleRow(
            label = "Show percentage",
            checked = settings.showPercentage,
            onCheckedChange = { onSettingsChange(settings.copy(showPercentage = it)) },
        )
        ReaderToggleRow(
            label = "Progress on top",
            checked = settings.showProgressTop,
            onCheckedChange = { onSettingsChange(settings.copy(showProgressTop = it)) },
        )
        ReaderToggleRow(
            label = "Reading speed",
            checked = settings.showReadingSpeed,
            onCheckedChange = { onSettingsChange(settings.copy(showReadingSpeed = it)) },
        )
        ReaderToggleRow(
            label = "Reading time",
            checked = settings.showReadingTime,
            onCheckedChange = { onSettingsChange(settings.copy(showReadingTime = it)) },
        )
        ReaderToggleRow(
            label = "Volume keys",
            checked = settings.volumeKeysEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(volumeKeysEnabled = it)) },
        )
        ReaderToggleRow(
            label = "Invert volume keys",
            checked = settings.volumeKeysInverted,
            onCheckedChange = { onSettingsChange(settings.copy(volumeKeysInverted = it)) },
        )
        ReaderToggleRow(
            label = "Long tap controls",
            checked = settings.longTapEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(longTapEnabled = it)) },
        )
        ReaderToggleRow(
            label = "Keep screen on",
            checked = settings.keepScreenOn,
            onCheckedChange = { onSettingsChange(settings.copy(keepScreenOn = it)) },
        )
    }
}

private fun ChimahonTapZoneInvert.readerTitle(): String = when (this) {
    ChimahonTapZoneInvert.None -> "None"
    ChimahonTapZoneInvert.Horizontal -> "Horizontal"
    ChimahonTapZoneInvert.Vertical -> "Vertical"
    ChimahonTapZoneInvert.Both -> "Both"
}

private fun ChimahonReaderOrientation.readerTitle(): String = when (this) {
    ChimahonReaderOrientation.Free -> "Free"
    ChimahonReaderOrientation.Portrait -> "Portrait"
    ChimahonReaderOrientation.Landscape -> "Landscape"
    ChimahonReaderOrientation.ReversePortrait -> "Reverse portrait"
    ChimahonReaderOrientation.ReverseLandscape -> "Reverse landscape"
}

private fun ChimahonDualPageMode.readerTitle(): String = when (this) {
    ChimahonDualPageMode.Off -> "Off"
    ChimahonDualPageMode.Automatic -> "Automatic"
    ChimahonDualPageMode.Always -> "Always"
}

private fun ChimahonReaderNavigationMode.readerTitle(): String = when (this) {
    ChimahonReaderNavigationMode.Automatic -> "Automatic"
    ChimahonReaderNavigationMode.LeftToRight -> "Left to right"
    ChimahonReaderNavigationMode.RightToLeft -> "Right to left"
    ChimahonReaderNavigationMode.Vertical -> "Vertical"
}

private fun ChimahonLibraryCoverRatio.libraryTitle(): String = when (this) {
    ChimahonLibraryCoverRatio.Automatic -> "Automatic"
    ChimahonLibraryCoverRatio.Square -> "Square"
    ChimahonLibraryCoverRatio.ThreeToFour -> "3:4"
    ChimahonLibraryCoverRatio.TwoToThree -> "2:3"
    ChimahonLibraryCoverRatio.Original -> "Original"
}

private fun ChimahonLibrarySort.libraryTitle(): String = when (this) {
    ChimahonLibrarySort.Alphabetical -> "Alphabetical"
    ChimahonLibrarySort.LastRead -> "Last read"
    ChimahonLibrarySort.LastUpdate -> "Last update"
    ChimahonLibrarySort.UnreadCount -> "Unread count"
    ChimahonLibrarySort.TotalChapters -> "Total chapters"
    ChimahonLibrarySort.LatestChapter -> "Latest chapter"
    ChimahonLibrarySort.ChapterFetchDate -> "Chapter fetch date"
    ChimahonLibrarySort.DateAdded -> "Date added"
    ChimahonLibrarySort.Random -> "Random"
}

private fun ChimahonLibrarySort.toUiLibrarySort(): LibrarySort = when (this) {
    ChimahonLibrarySort.DateAdded -> LibrarySort.DateAdded
    ChimahonLibrarySort.LastRead,
    ChimahonLibrarySort.LastUpdate,
    ChimahonLibrarySort.LatestChapter,
    ChimahonLibrarySort.ChapterFetchDate,
    -> LibrarySort.LastUpdated
    ChimahonLibrarySort.UnreadCount -> LibrarySort.UnreadCount
    ChimahonLibrarySort.Alphabetical,
    ChimahonLibrarySort.TotalChapters,
    ChimahonLibrarySort.Random,
    -> LibrarySort.Alphabetical
}

private fun ChimahonLibrarySettings.toInitialLibraryFilter(): LibraryFilter {
    return when {
        unreadFilter == ChimahonFilterMode.Include -> LibraryFilter.Unread
        startedFilter == ChimahonFilterMode.Include -> LibraryFilter.Started
        bookmarkedFilter == ChimahonFilterMode.Include -> LibraryFilter.Bookmarked
        else -> LibraryFilter.All
    }
}

private fun ChimahonFilterMode.filterTitle(): String = when (this) {
    ChimahonFilterMode.Any -> "Any"
    ChimahonFilterMode.Include -> "Include"
    ChimahonFilterMode.Exclude -> "Exclude"
}

private fun Int.toColumnTitle(): String = if (this <= 0) "Automatic" else toString()

private fun String.toColumnCount(): Int = if (this == "Automatic") 0 else toIntOrNull() ?: 0

private fun Int.toOffCountTitle(): String = if (this <= 0) "Off" else toString()

private fun String.toOffCount(): Int = if (this == "Off") 0 else toIntOrNull()?.coerceAtLeast(0) ?: 0

private fun Int.toRemoveAfterReadTitle(): String = when (this) {
    1 -> "After next chapter"
    2 -> "After 2 chapters"
    5 -> "After 5 chapters"
    else -> "Never"
}

private fun String.toRemoveAfterReadSlots(): Int = when (this) {
    "After next chapter" -> 1
    "After 2 chapters" -> 2
    "After 5 chapters" -> 5
    else -> -1
}

private fun ChimahonBrowseSourceDisplayMode.browseTitle(): String = when (this) {
    ChimahonBrowseSourceDisplayMode.List -> "List"
    ChimahonBrowseSourceDisplayMode.CompactList -> "Compact list"
    ChimahonBrowseSourceDisplayMode.Grid -> "Grid"
}

private fun ChimahonConnectionOpeningPreference.connectionTitle(): String = when (this) {
    ChimahonConnectionOpeningPreference.InApp -> "In app"
    ChimahonConnectionOpeningPreference.ExternalBrowser -> "External browser"
    ChimahonConnectionOpeningPreference.AskEveryTime -> "Ask every time"
}

private fun ChimahonSecureScreenMode.securityTitle(): String = when (this) {
    ChimahonSecureScreenMode.Always -> "Always"
    ChimahonSecureScreenMode.Incognito -> "Incognito"
    ChimahonSecureScreenMode.Never -> "Never"
}

private fun Int.toLockAfterTitle(): String = when (this) {
    0 -> "Immediately"
    1 -> "1 minute"
    5 -> "5 minutes"
    15 -> "15 minutes"
    30 -> "30 minutes"
    else -> "$this minutes"
}

private fun String.toLockAfterMinutes(): Int = when (this) {
    "Immediately" -> 0
    "1 minute" -> 1
    "5 minutes" -> 5
    "15 minutes" -> 15
    "30 minutes" -> 30
    else -> removeSuffix(" minutes").toIntOrNull()?.coerceAtLeast(0) ?: 0
}

private fun Int.toTrackingIntervalTitle(): String = when (this) {
    0 -> "Off"
    6 -> "6 hours"
    12 -> "12 hours"
    24 -> "24 hours"
    48 -> "48 hours"
    else -> "$this hours"
}

private fun String.toTrackingIntervalHours(): Int = when (this) {
    "Off" -> 0
    "6 hours" -> 6
    "12 hours" -> 12
    "24 hours" -> 24
    "48 hours" -> 48
    else -> removeSuffix(" hours").toIntOrNull()?.coerceAtLeast(0) ?: 24
}

private fun commonReaderLanguages(): List<String> {
    return listOf("English", "Japanese", "Korean", "Chinese", "Spanish", "French", "German", "Arabic")
}

private fun String.sourceLanguageCode(): String = ifBlank { "multi" }.uppercase()

private fun ChimahonSnapshot.sourceLanguageOptions(): List<String> {
    val sourceLanguages = sources.map { it.language.sourceLanguageCode() }
    val installedLanguages = installedExtensions
        .mapNotNull { extension ->
            extension.id
                .substringAfterLast('.', missingDelimiterValue = "")
                .takeIf { it.length in 2..5 }
                ?.uppercase()
        }
    return (sourceLanguages + installedLanguages + listOf("EN", "JA", "KO", "ZH", "ES", "FR", "DE", "AR", "MULTI"))
        .distinct()
        .sorted()
}

private fun List<String>.toggled(value: String): List<String> {
    return if (value in this) {
        filterNot { it == value }
    } else {
        (this + value).distinct()
    }
}

private fun List<ChimahonSourceEntry>.sourceGroups(
    groupByLanguage: Boolean,
): List<Pair<String?, List<ChimahonSourceEntry>>> {
    val sorted = sortedWith(
        compareBy<ChimahonSourceEntry> { it.language.ifBlank { "multi" }.lowercase() }
            .thenBy { it.name.lowercase() },
    )
    if (!groupByLanguage) return listOf(null to sorted)
    return sorted
        .groupBy { it.language.ifBlank { "multi" }.uppercase() }
        .map { (language, entries) -> language to entries }
}

private fun List<ChimahonRepoExtensionEntry>.extensionGroups(
    groupByLanguage: Boolean,
): List<Pair<String?, List<ChimahonRepoExtensionEntry>>> {
    val sorted = sortedWith(
        compareBy<ChimahonRepoExtensionEntry> { it.language.ifBlank { "multi" }.lowercase() }
            .thenBy { it.name.lowercase() },
    )
    if (!groupByLanguage) return listOf(null to sorted)
    return sorted
        .groupBy { it.language.ifBlank { "multi" }.uppercase() }
        .map { (language, entries) -> language to entries }
}

private fun ChimahonLibraryBulkActionResult.libraryBulkSummary(action: String): String {
    return buildString {
        append(mangaCount).append(" manga ")
        append(action)
        if (chapterCount > 0) {
            append(" with ").append(chapterCount).append(" chapter(s) updated")
        }
        if (categoryCount > 0) {
            append(" across ").append(categoryCount).append(" categorization change(s)")
        }
    }
}

@Composable
private fun ReaderSheetSectionTitle(text: String) {
    Label(
        text = text,
        color = ReaderPalette.secondaryText,
        size = 11,
        weight = FontWeight.Bold,
        maxLines = 1,
        modifier = Modifier.padding(top = 6.dp),
    )
}

@Composable
private fun ReaderThemeSwatchRow(
    selected: ReaderCanvas,
    onSelect: (ReaderCanvas) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ReaderCanvas.entries.forEach { canvas ->
            ReaderThemeSwatchButton(
                canvas = canvas,
                selected = canvas == selected,
                onClick = { onSelect(canvas) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ReaderThemeSwatchButton(
    canvas: ReaderCanvas,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textColor = readerHudContent(canvas)
    Column(
        modifier = modifier
            .height(74.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(canvas.color)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) ReaderPalette.selectedControl else ReaderPalette.divider,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(onClick = onClick)
            .padding(9.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Label(
            "Aa",
            textColor,
            19,
            weight = FontWeight.Bold,
            maxLines = 1,
        )
        Label(
            canvas.title,
            textColor.copy(alpha = 0.76f),
            10,
            weight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun ReaderToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Label(
            text = label,
            color = ReaderPalette.secondaryText,
            size = 11,
            weight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .width(38.dp)
                .height(22.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(if (checked) ReaderPalette.selectedControl else ReaderPalette.control)
                .padding(3.dp),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color.White),
            )
        }
    }
}

@Composable
private fun ReaderValueStepperRow(
    label: String,
    value: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Label(
            text = label,
            color = ReaderPalette.secondaryText,
            size = 11,
            weight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        ReaderAction(
            icon = UiIcon.Back,
            contentDescription = "Decrease $label",
            onClick = onDecrease,
            modifier = Modifier.size(32.dp),
        )
        Label(
            text = value,
            color = Color.White,
            size = 11,
            weight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.width(62.dp),
        )
        ReaderAction(
            icon = UiIcon.Forward,
            contentDescription = "Increase $label",
            onClick = onIncrease,
            modifier = Modifier.size(32.dp),
        )
    }
}

private fun Double.toCleanDecimal(): String {
    val rounded = kotlin.math.round(this * 100.0) / 100.0
    return if (rounded.rem(1.0) == 0.0) {
        rounded.toInt().toString()
    } else {
        rounded.toString()
    }
}

@Composable
private fun ReaderOptionRow(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Label(
            label,
            ReaderPalette.secondaryText,
            11,
            weight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.width(104.dp),
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(options) { option ->
                val active = option == selected
                Label(
                    text = option,
                    color = if (active) Color.White else ReaderPalette.secondaryText,
                    size = 11,
                    weight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (active) ReaderPalette.selectedControl else ReaderPalette.control)
                        .clickable { onSelect(option) }
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                )
            }
        }
    }
}

@Composable
private fun ReaderChapterQueuePanel(
    request: ChimahonReaderRequest,
    previousChapter: ChimahonReaderChapterRef?,
    nextChapter: ChimahonReaderChapterRef?,
    onOpenChapter: (ChimahonReaderChapterRef) -> Unit,
    onBookmarkChapter: (ChimahonReaderChapterRef) -> Unit,
    onDownloadChapter: (ChimahonReaderChapterRef) -> Unit,
    onMarkChapterRead: (ChimahonReaderChapterRef) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember(request.chapterQueue) { mutableStateOf("") }
    val visibleChapters = request.chapterQueue.filter { chapter ->
        query.isBlank() ||
            chapter.chapterName.contains(query, ignoreCase = true) ||
            chapter.scanlator.orEmpty().contains(query, ignoreCase = true)
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ReaderPalette.chrome.copy(alpha = 0.97f))
            .border(1.dp, ReaderPalette.divider, RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Label(
                "Chapters",
                Color.White,
                13,
                weight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Label(
                "${request.chapterIndex + 1} / ${request.chapterQueue.size}",
                ReaderPalette.secondaryText,
                11,
                maxLines = 1,
            )
        }
        if (request.chapterQueue.size > 8) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ReaderPalette.control)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (query.isBlank()) {
                    Label("Search chapters", ReaderPalette.secondaryText, 11, maxLines = 1)
                }
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        if (request.chapterQueue.isEmpty()) {
            Label(
                "This source did not provide a chapter queue.",
                ReaderPalette.secondaryText,
                11,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 330.dp)
                    .padding(top = 8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(visibleChapters, key = { "${it.chapterId}:${it.chapterUrl}" }) { chapter ->
                    val selected = chapter.chapterUrl == request.chapterUrl &&
                        chapter.chapterId == request.chapterId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selected) ReaderPalette.selectedControl else ReaderPalette.control)
                            .clickable { onOpenChapter(chapter) }
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (chapter.bookmarked) {
                                    IconGlyph(
                                        icon = UiIcon.Bookmark,
                                        contentDescription = "Bookmarked",
                                        tint = if (selected) Color.White else ReaderPalette.secondaryText,
                                        modifier = Modifier.size(14.dp),
                                    )
                                }
                                if (!chapter.read && chapter.lastPageRead <= 0L) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(if (selected) Color.White else ReaderPalette.selectedControl),
                                    )
                                }
                                Label(
                                    chapter.chapterName,
                                    if (selected) Color.White else ReaderPalette.secondaryText,
                                    11,
                                    weight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            Label(
                                buildString {
                                    if (chapter.chapterNumber > 0.0) {
                                        append("Ch. ").append(chapter.chapterNumber.toDisplayChapter())
                                    } else {
                                        append("Chapter")
                                    }
                                    if (chapter.read) {
                                        append("  \u00b7  Read")
                                    } else if (chapter.lastPageRead > 0L) {
                                        append("  \u00b7  Page ").append(chapter.lastPageRead + 1)
                                    }
                                    chapter.scanlator?.takeIf { it.isNotBlank() }?.let {
                                        append("  \u00b7  ").append(it)
                                    }
                                },
                                ReaderPalette.secondaryText,
                                9,
                                maxLines = 1,
                                modifier = Modifier.padding(top = 3.dp),
                            )
                        }
                        if (selected) {
                            Label(
                                "Current",
                                Color.White,
                                9,
                                weight = FontWeight.SemiBold,
                                maxLines = 1,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.16f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                        if (chapter.chapterId != null) {
                            ReaderChapterRowAction(
                                icon = if (chapter.read) UiIcon.Circle else UiIcon.DoneAll,
                                contentDescription = if (chapter.read) "Mark unread" else "Mark read",
                                active = chapter.read,
                                onClick = { onMarkChapterRead(chapter) },
                            )
                            ReaderChapterRowAction(
                                icon = if (chapter.bookmarked) UiIcon.Bookmark else UiIcon.BookmarkBorder,
                                contentDescription = if (chapter.bookmarked) {
                                    "Unbookmark chapter"
                                } else {
                                    "Bookmark chapter"
                                },
                                active = chapter.bookmarked,
                                onClick = { onBookmarkChapter(chapter) },
                            )
                            ReaderChapterRowAction(
                                icon = UiIcon.Download,
                                contentDescription = "Download chapter",
                                onClick = { onDownloadChapter(chapter) },
                            )
                        }
                    }
                }
            }
            if (visibleChapters.isEmpty()) {
                Label(
                    "No matching chapters.",
                    ReaderPalette.secondaryText,
                    11,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            Label(
                buildString {
                    previousChapter?.let { append("Previous: ").append(it.chapterName) }
                    if (previousChapter != null && nextChapter != null) append("  \u00b7  ")
                    nextChapter?.let { append("Next: ").append(it.chapterName) }
                },
                ReaderPalette.secondaryText,
                9,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun ReaderChapterRowAction(
    icon: UiIcon,
    contentDescription: String,
    active: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (active) Color.White.copy(alpha = 0.14f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        IconGlyph(
            icon = icon,
            contentDescription = contentDescription,
            tint = if (active) Color.White else ReaderPalette.secondaryText,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun ReaderStatisticsPanel(
    request: ChimahonReaderRequest,
    mode: ReaderMode,
    scale: ReaderScale,
    currentPage: Int,
    pageCount: Int,
    previousChapter: ChimahonReaderChapterRef?,
    nextChapter: ChimahonReaderChapterRef?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ReaderPalette.chrome.copy(alpha = 0.97f))
            .border(1.dp, ReaderPalette.divider, RoundedCornerShape(8.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Label(
            "Statistics",
            Color.White,
            16,
            weight = FontWeight.SemiBold,
            maxLines = 1,
        )
        ReaderStatisticRow("Progress", readerProgressText(currentPage, pageCount))
        ReaderStatisticRow("Page", "$currentPage / $pageCount")
        ReaderStatisticRow("Mode", mode.title)
        ReaderStatisticRow("Scale", scale.title)
        if (request.chapterQueue.isNotEmpty()) {
            ReaderStatisticRow(
                "Chapter",
                "${(request.chapterIndex + 1).coerceAtLeast(1)} / ${request.chapterQueue.size}",
            )
        }
        previousChapter?.let { ReaderStatisticRow("Previous", it.chapterName) }
        nextChapter?.let { ReaderStatisticRow("Next", it.chapterName) }
    }
}

@Composable
private fun ReaderStatisticRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Label(
            text = label,
            color = ReaderPalette.secondaryText,
            size = 11,
            weight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.width(92.dp),
        )
        Label(
            text = value,
            color = Color.White,
            size = 12,
            weight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ReaderControlBar(
    mode: ReaderMode,
    canvas: ReaderCanvas,
    currentPage: Int,
    pageCount: Int,
    settingsVisible: Boolean,
    chaptersVisible: Boolean,
    statsVisible: Boolean,
    cropActive: Boolean,
    showPercentage: Boolean,
    onOpenChapterUrl: (() -> Unit)?,
    onCycleMode: () -> Unit,
    onToggleSettings: () -> Unit,
    onToggleChapters: () -> Unit,
    onToggleStats: () -> Unit,
    onToggleCrop: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(readerHudBackground(canvas))
            .padding(horizontal = 16.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Label(
            text = if (showPercentage) readerProgressText(currentPage, pageCount) else "$currentPage / $pageCount",
            color = readerHudContent(canvas).copy(alpha = 0.74f),
            size = 13,
            weight = FontWeight.SemiBold,
            maxLines = 1,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ReaderAction(
                icon = UiIcon.Chapters,
                contentDescription = "Chapters",
                active = chaptersVisible,
                tint = readerHudContent(canvas),
                onClick = onToggleChapters,
            )
            ReaderAction(
                icon = UiIcon.Settings,
                contentDescription = "Appearance",
                active = settingsVisible,
                tint = readerHudContent(canvas),
                onClick = onToggleSettings,
            )
            ReaderAction(
                icon = UiIcon.Statistics,
                contentDescription = "Statistics",
                active = statsVisible,
                tint = readerHudContent(canvas),
                onClick = onToggleStats,
            )
            ReaderAction(
                icon = UiIcon.Crop,
                contentDescription = "Crop borders",
                active = cropActive,
                tint = readerHudContent(canvas),
                onClick = onToggleCrop,
            )
            onOpenChapterUrl?.let {
                ReaderAction(
                    icon = UiIcon.Web,
                    contentDescription = "Open source page",
                    tint = readerHudContent(canvas),
                    onClick = it,
                )
            }
            ReaderAction(
                icon = UiIcon.Swap,
                contentDescription = "Reading mode: ${mode.title}",
                tint = readerHudContent(canvas),
                onClick = onCycleMode,
            )
        }
    }
}

@Composable
private fun ReaderPageImage(
    request: ChimahonReaderRequest,
    page: ChimahonReaderPage,
    paged: Boolean,
    scale: ReaderScale,
    canvas: ReaderCanvas,
    readerSettings: ChimahonReaderSettings,
    cachedState: ReaderPageUiState?,
    onStateChange: (ReaderPageUiState) -> Unit,
    onLoadReaderPageImage: suspend (ChimahonReaderPage) -> ByteArray,
) {
    var retryKey by remember(page) { mutableIntStateOf(0) }
    var pageActionsVisible by remember(page) { mutableStateOf(false) }
    var pageActionBusy by remember(page) { mutableStateOf<String?>(null) }
    var pageActionMessage by remember(page) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(page, retryKey) {
        if (retryKey == 0 && cachedState is ReaderPageUiState.Ready) return@LaunchedEffect
        onStateChange(ReaderPageUiState.Loading)
        onStateChange(
            runCatching {
                onLoadReaderPageImage(page).decodeToImageBitmap()
            }.fold(
                onSuccess = ReaderPageUiState::Ready,
                onFailure = { ReaderPageUiState.Failed(it.message ?: "Unable to decode page image.") },
            ),
        )
    }

    Box(
        modifier = if (paged) {
            Modifier
                .fillMaxSize()
                .background(canvas.readerBackground(readerSettings.pureBlackBackground))
                .padding(
                    horizontal = if (readerSettings.cropBorders) 0.dp else readerSettings.horizontalPadding.dp,
                    vertical = if (readerSettings.cropBorders) 0.dp else readerSettings.verticalPadding.dp,
                )
        } else {
            Modifier
                .fillMaxWidth()
                .widthIn(
                    max = when (scale) {
                        ReaderScale.FitScreen -> 900.dp
                        ReaderScale.FitWidth -> 1180.dp
                        ReaderScale.FitHeight -> 760.dp
                    },
                )
                .heightIn(min = 260.dp)
                .background(canvas.readerBackground(readerSettings.pureBlackBackground))
                .padding(
                    horizontal = if (readerSettings.cropBorders) 0.dp else readerSettings.horizontalPadding.dp,
                    vertical = if (readerSettings.cropBorders) 0.dp else readerSettings.verticalPadding.dp,
                )
        },
        contentAlignment = Alignment.Center,
    ) {
        when (val pageState = cachedState ?: ReaderPageUiState.Loading) {
            ReaderPageUiState.Loading -> {
                ReaderPageMessage(
                    marker = (page.index + 1).toString(),
                    title = "Loading page",
                    detail = "Requesting image from the source.",
                )
            }
            is ReaderPageUiState.Failed -> {
                ReaderPageMessage(
                    marker = "!",
                    title = "Page ${page.index + 1} failed",
                    detail = pageState.message,
                    action = "Retry",
                    onAction = { retryKey++ },
                )
            }
            is ReaderPageUiState.Ready -> {
                val aspectRatio = pageState.image.width.toFloat() /
                    pageState.image.height.coerceAtLeast(1).toFloat()
                val colorFilter = readerSettings.readerColorFilter()
                Image(
                    bitmap = pageState.image,
                    contentDescription = "Page ${page.index + 1}",
                    contentScale = if (readerSettings.cropBorders) ContentScale.Crop else ContentScale.Fit,
                    colorFilter = colorFilter,
                    modifier = when {
                        paged && readerSettings.cropBorders -> Modifier.fillMaxSize()
                        paged && scale == ReaderScale.FitScreen -> Modifier.fillMaxSize()
                        paged && scale == ReaderScale.FitHeight -> Modifier
                            .fillMaxHeight()
                            .aspectRatio(aspectRatio)
                        else -> Modifier
                            .fillMaxWidth()
                            .aspectRatio(aspectRatio)
                    }.pointerInput(page) {
                        detectTapGestures(
                            onLongPress = {
                                pageActionMessage = null
                                pageActionsVisible = true
                            },
                        )
                    },
                )
                readerSettings.readerBrightnessOverlay()?.let { overlay ->
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(overlay),
                    )
                }
            }
        }
    }
    if (pageActionsVisible) {
        ReaderPageActionsDialog(
            request = request,
            page = page,
            busyAction = pageActionBusy,
            message = pageActionMessage,
            onDismiss = {
                if (pageActionBusy == null) pageActionsVisible = false
            },
            onCopyUrl = {
                pageActionMessage = ChimahonReaderPageActions.copyPageUrlToClipboard(page).message
            },
            onCopyInfo = {
                pageActionMessage = ChimahonReaderPageActions.copyPageTextToClipboard(request, page).message
            },
            onShareInfo = {
                pageActionMessage = ChimahonReaderPageActions.sharePageText(request, page).message
            },
            onSaveImage = {
                if (pageActionBusy == null) {
                    scope.launch {
                        pageActionBusy = "save"
                        pageActionMessage = runCatching {
                            val bytes = onLoadReaderPageImage(page)
                            ChimahonReaderPageActions.savePageBytes(request, page, bytes).message
                        }.getOrElse { it.message ?: "Page image could not be saved." }
                        pageActionBusy = null
                    }
                }
            },
            onShareImage = {
                if (pageActionBusy == null) {
                    scope.launch {
                        pageActionBusy = "share"
                        pageActionMessage = runCatching {
                            val bytes = onLoadReaderPageImage(page)
                            ChimahonReaderPageActions.sharePageBytes(request, page, bytes).message
                        }.getOrElse { it.message ?: "Page image could not be shared." }
                        pageActionBusy = null
                    }
                }
            },
        )
    }
}

private fun ChimahonReaderSettings.readerColorFilter(): ColorFilter? {
    if (!colorFilterEnabled) return null
    return when {
        invertColors -> ColorFilter.colorMatrix(
            ColorMatrix(
                floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f,
                ),
            ),
        )
        grayscale -> ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
        else -> null
    }
}

private fun ChimahonReaderSettings.readerBrightnessOverlay(): Color? {
    if (!colorFilterEnabled || brightness == 0) return null
    val alpha = (kotlin.math.abs(brightness).coerceAtMost(100) / 100f) * 0.42f
    return if (brightness > 0) {
        Color.White.copy(alpha = alpha)
    } else {
        Color.Black.copy(alpha = alpha)
    }
}

@Composable
private fun ReaderPageActionsDialog(
    request: ChimahonReaderRequest,
    page: ChimahonReaderPage,
    busyAction: String?,
    message: String?,
    onDismiss: () -> Unit,
    onSaveImage: () -> Unit,
    onShareImage: () -> Unit,
    onCopyUrl: () -> Unit,
    onCopyInfo: () -> Unit,
    onShareInfo: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .widthIn(min = 280.dp, max = 420.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ReaderPalette.chrome)
                .border(1.dp, ReaderPalette.divider, RoundedCornerShape(8.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Label(
                "Page actions",
                Color.White,
                17,
                weight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Label(
                "${request.chapterName} - page ${page.index + 1}",
                ReaderPalette.secondaryText,
                11,
                maxLines = 2,
            )
            ReaderPageActionRow(
                title = if (busyAction == "save") "Saving image" else "Save image",
                icon = UiIcon.Download,
                active = busyAction == "save",
                enabled = busyAction == null,
                onClick = onSaveImage,
            )
            ReaderPageActionRow(
                title = if (busyAction == "share") "Sharing image" else "Share image",
                icon = UiIcon.Web,
                active = busyAction == "share",
                enabled = busyAction == null,
                onClick = onShareImage,
            )
            ReaderPageActionRow(
                title = "Copy page URL",
                icon = UiIcon.Link,
                enabled = busyAction == null,
                onClick = onCopyUrl,
            )
            ReaderPageActionRow(
                title = "Copy page info",
                icon = UiIcon.Info,
                enabled = busyAction == null,
                onClick = onCopyInfo,
            )
            ReaderPageActionRow(
                title = "Share page info",
                icon = UiIcon.Forward,
                enabled = busyAction == null,
                onClick = onShareInfo,
            )
            message?.let {
                Label(
                    it,
                    if (busyAction == null) ReaderPalette.secondaryText else Color.White,
                    11,
                    lineHeight = 16,
                    maxLines = 3,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                ReaderTextAction(
                    text = "Close",
                    onClick = onDismiss,
                )
            }
        }
    }
}

@Composable
private fun ReaderPageActionRow(
    title: String,
    icon: UiIcon,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (active) ReaderPalette.selectedControl else ReaderPalette.control)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        IconGlyph(
            icon = icon,
            contentDescription = title,
            tint = if (enabled || active) Color.White else ReaderPalette.secondaryText.copy(alpha = 0.45f),
            modifier = Modifier.size(19.dp),
        )
        Label(
            title,
            if (enabled || active) Color.White else ReaderPalette.secondaryText.copy(alpha = 0.45f),
            12,
            weight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun ReaderMessage(
    marker: String,
    title: String,
    detail: String,
    action: String? = null,
    onAction: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ReaderPalette.background),
        contentAlignment = Alignment.Center,
    ) {
        ReaderPageMessage(
            marker = marker,
            title = title,
            detail = detail,
            action = action,
            onAction = onAction,
        )
    }
}

@Composable
private fun ReaderPageMessage(
    marker: String,
    title: String,
    detail: String,
    action: String? = null,
    onAction: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .widthIn(max = 360.dp)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ReaderPalette.control),
            contentAlignment = Alignment.Center,
        ) {
            Label(marker, Color.White, 13, weight = FontWeight.Bold, maxLines = 1)
        }
        Label(
            text = title,
            color = Color.White,
            size = 14,
            weight = FontWeight.SemiBold,
            maxLines = 2,
            modifier = Modifier.padding(top = 12.dp),
        )
        Label(
            text = detail,
            color = ReaderPalette.secondaryText,
            size = 11,
            lineHeight = 16,
            maxLines = 4,
            modifier = Modifier.padding(top = 5.dp),
        )
        if (action != null) {
            ReaderTextAction(
                text = action,
                onClick = onAction,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun FeedSection(
    sources: List<ChimahonSourceEntry>,
    manageMode: Boolean,
    onLoadSourcePreview: suspend (Long, ChimahonSourceBrowseMode, String, Int) -> ChimahonSourcePreview,
    onOpenRemoteManga: (ChimahonRemoteMangaEntry) -> Unit,
    onOpenSource: (Long, ChimahonSourceBrowseMode) -> Unit,
) {
    if (sources.isEmpty()) {
        EmptyMobileState(
            marker = "F",
            icon = UiIcon.Browse,
            title = "Your feed is empty",
            detail = "Install an extension source to add catalogue feeds.",
        )
        return
    }
    val enabledSources = remember { mutableStateMapOf<Long, Boolean>() }
    if (manageMode) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            item {
                Label(
                    "Choose feeds",
                    ChimahonPalette.onSurface,
                    16,
                    weight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
            items(sources, key = { it.id }) { source ->
                val enabled = enabledSources[source.id] != false
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ChimahonPalette.surface)
                        .clickable { enabledSources[source.id] = !enabled }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconGlyph(
                        icon = if (enabled) UiIcon.CheckCircle else UiIcon.Circle,
                        contentDescription = if (enabled) "Disable feed" else "Enable feed",
                        tint = if (enabled) ChimahonPalette.primary else ChimahonPalette.secondaryText,
                        modifier = Modifier.size(22.dp),
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 14.dp),
                    ) {
                        Label(source.name, ChimahonPalette.onSurface, 14, weight = FontWeight.SemiBold, maxLines = 1)
                        Label(
                            source.language.ifBlank { "multi" }.uppercase(),
                            ChimahonPalette.secondaryText,
                            11,
                            maxLines = 1,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    IconGlyph(
                        icon = UiIcon.Reorder,
                        contentDescription = "Reorder ${source.name}",
                        tint = ChimahonPalette.secondaryText,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
        return
    }
    val visibleSources = sources.filter { enabledSources[it.id] != false }
    if (visibleSources.isEmpty()) {
        EmptyMobileState(
            marker = "F",
            icon = UiIcon.Browse,
            title = "Your feed is empty",
            detail = "Use Manage feeds to enable at least one source.",
        )
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(visibleSources, key = { it.id }) { source ->
            SourceFeedBlock(
                source = source,
                onLoadSourcePreview = onLoadSourcePreview,
                onOpenRemoteManga = onOpenRemoteManga,
                onOpenSource = {
                    onOpenSource(
                        source.id,
                        if (source.supportsLatest) {
                            ChimahonSourceBrowseMode.Latest
                        } else {
                            ChimahonSourceBrowseMode.Popular
                        },
                    )
                },
            )
        }
    }
}

@Composable
private fun SourceFeedBlock(
    source: ChimahonSourceEntry,
    onLoadSourcePreview: suspend (Long, ChimahonSourceBrowseMode, String, Int) -> ChimahonSourcePreview,
    onOpenRemoteManga: (ChimahonRemoteMangaEntry) -> Unit,
    onOpenSource: () -> Unit,
) {
    val mode = if (source.supportsLatest) {
        ChimahonSourceBrowseMode.Latest
    } else {
        ChimahonSourceBrowseMode.Popular
    }
    var state by remember(source.id) {
        mutableStateOf<SourcePreviewUiState>(SourcePreviewUiState.Loading)
    }

    LaunchedEffect(source.id, mode) {
        state = SourcePreviewUiState.Loading
        state = runCatching { onLoadSourcePreview(source.id, mode, "", 1) }
            .fold(
                onSuccess = SourcePreviewUiState::Ready,
                onFailure = { SourcePreviewUiState.Failed(it.message ?: "Unable to load feed.") },
            )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenSource)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Label(
                    source.name,
                    ChimahonPalette.onSurface,
                    14,
                    weight = FontWeight.SemiBold,
                    maxLines = 1,
                )
                Label(
                    "${mode.title}  \u00b7  ${source.language.ifBlank { "multi" }.uppercase()}",
                    ChimahonPalette.secondaryText,
                    11,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            IconGlyph(
                icon = UiIcon.Forward,
                contentDescription = "Open ${source.name}",
                tint = ChimahonPalette.secondaryText,
                modifier = Modifier.size(18.dp),
            )
        }
        when (val current = state) {
            SourcePreviewUiState.Idle,
            SourcePreviewUiState.Loading,
            -> {
                Label(
                    "Loading feed...",
                    ChimahonPalette.secondaryText,
                    12,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                )
            }
            is SourcePreviewUiState.Failed -> {
                Label(
                    current.message,
                    ChimahonPalette.error,
                    11,
                    maxLines = 2,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
            is SourcePreviewUiState.Ready -> {
                if (current.preview.entries.isEmpty()) {
                    Label(
                        "No results",
                        ChimahonPalette.secondaryText,
                        12,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                    )
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(current.preview.entries.take(12), key = { "${it.sourceId}:${it.url}" }) { entry ->
                            FeedMangaCard(
                                entry = entry,
                                onClick = { onOpenRemoteManga(entry) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedMangaCard(
    entry: ChimahonRemoteMangaEntry,
    onClick: () -> Unit,
) {
    val fallbackColor = coverColor(entry.sourceId xor entry.url.hashCode().toLong(), entry.title)
    Column(
        modifier = Modifier
            .width(116.dp)
            .clickable(onClick = onClick),
    ) {
        ThumbnailArtwork(
            url = entry.thumbnailUrl,
            title = entry.title,
            fallbackColor = fallbackColor,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.68f)
                .clip(RoundedCornerShape(5.dp)),
        )
        Label(
            entry.title,
            ChimahonPalette.onSurface,
            11,
            maxLines = 2,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun ExtensionsSection(
    snapshot: ChimahonSnapshot,
    query: String,
    filtersVisible: Boolean,
    browseSettings: ChimahonBrowseSettings,
    repoInputRequestKey: Int,
    onAddExtensionRepo: suspend (String) -> ChimahonExtensionRepoEntry,
    onDeleteExtensionRepo: suspend (String) -> Unit,
    onLoadExtensionRepoCatalog: suspend (ChimahonExtensionRepoEntry) -> ChimahonExtensionRepoCatalog,
    onInstallExtension: suspend (ChimahonRepoExtensionEntry) -> ChimahonInstalledExtensionEntry,
    onUninstallExtension: suspend (String, ChimahonExtensionPackageType) -> ChimahonExtensionUninstallResult,
    onRepoSaved: () -> Unit,
) {
    var repoUrl by remember { mutableStateOf("") }
    var repoMessage by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    var selectedRepo by remember { mutableStateOf<ChimahonExtensionRepoEntry?>(null) }
    var repoPendingDeletion by remember { mutableStateOf<ChimahonExtensionRepoEntry?>(null) }
    var deletingRepoUrl by remember { mutableStateOf<String?>(null) }
    var repoDeleteMessage by remember { mutableStateOf<String?>(null) }
    var catalogState by remember { mutableStateOf<ExtensionRepoCatalogUiState>(ExtensionRepoCatalogUiState.Idle) }
    var installingExtensionId by remember { mutableStateOf<String?>(null) }
    var uninstallingExtensionId by remember { mutableStateOf<String?>(null) }
    var installMessage by remember { mutableStateOf<String?>(null) }
    var uninstallMessage by remember { mutableStateOf<String?>(null) }
    var selectedFilter by remember { mutableStateOf(ExtensionFilter.All) }
    var showRepoInput by remember { mutableStateOf(snapshot.extensionRepos.isEmpty()) }
    val scope = rememberCoroutineScope()
    val installedExtensionIds = snapshot.installedExtensions.map { it.id }.toSet()
    val filteredInstalledExtensions = snapshot.installedExtensions.filter { extension ->
        query.isBlank() ||
            extension.name.contains(query, ignoreCase = true) ||
            extension.id.contains(query, ignoreCase = true)
    }

    LaunchedEffect(snapshot.extensionRepos) {
        if (selectedRepo == null) {
            selectedRepo = snapshot.extensionRepos.firstOrNull()
        }
    }

    LaunchedEffect(repoInputRequestKey) {
        if (repoInputRequestKey > 0) {
            showRepoInput = true
        }
    }

    LaunchedEffect(selectedRepo?.baseUrl) {
        val repo = selectedRepo ?: run {
            catalogState = ExtensionRepoCatalogUiState.Idle
            return@LaunchedEffect
        }
        catalogState = ExtensionRepoCatalogUiState.Loading
        catalogState = runCatching { onLoadExtensionRepoCatalog(repo) }
            .fold(
                onSuccess = ExtensionRepoCatalogUiState::Ready,
                onFailure = { ExtensionRepoCatalogUiState.Failed(it.message ?: "Could not load repository") },
            )
    }

    repoPendingDeletion?.let { repo ->
        ExtensionRepoDeleteDialog(
            repo = repo,
            deleting = deletingRepoUrl == repo.baseUrl,
            onDismiss = {
                if (deletingRepoUrl == null) {
                    repoPendingDeletion = null
                }
            },
            onDelete = {
                if (deletingRepoUrl == null) {
                    deletingRepoUrl = repo.baseUrl
                    scope.launch {
                        runCatching { onDeleteExtensionRepo(repo.baseUrl) }
                            .onSuccess {
                                selectedRepo = null
                                catalogState = ExtensionRepoCatalogUiState.Idle
                                installMessage = null
                                repoDeleteMessage = null
                                repoPendingDeletion = null
                                onRepoSaved()
                            }
                            .onFailure { error ->
                                repoDeleteMessage = error.message ?: "Could not delete repository"
                                repoPendingDeletion = null
                            }
                        deletingRepoUrl = null
                    }
                }
            },
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        item {
            ExtensionSectionToolbar(
                extensionCount = snapshot.summary.extensionCount,
                repoCount = snapshot.extensionRepos.size,
                onAddRepo = { showRepoInput = !showRepoInput },
            )
        }
        if (filtersVisible) {
            item {
                FilterChips(
                    chips = ExtensionFilter.entries.map { it.title },
                    selected = selectedFilter.title,
                    onSelect = { title ->
                        selectedFilter = ExtensionFilter.entries.firstOrNull { it.title == title } ?: ExtensionFilter.All
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
        }
        if (showRepoInput) {
            item {
                ExtensionRepoInputPanel(
                    repoUrl = repoUrl,
                    message = repoMessage,
                    saving = saving,
                    onRepoUrlChange = {
                        repoUrl = it
                        repoMessage = null
                    },
                    onAddRepo = {
                        if (repoUrl.isNotBlank() && !saving) {
                            saving = true
                            scope.launch {
                                runCatching { onAddExtensionRepo(repoUrl) }
                                    .onSuccess { repo ->
                                        repoUrl = ""
                                        repoMessage = "Saved ${repo.name}"
                                        showRepoInput = false
                                        onRepoSaved()
                                    }
                                    .onFailure { error ->
                                        repoMessage = error.message ?: "Could not save repository"
                                    }
                                saving = false
                            }
                        }
                    },
                )
            }
        }
        if (selectedFilter in listOf(ExtensionFilter.All, ExtensionFilter.Installed)) {
            item { ListGroupHeader("Installed") }
            if (filteredInstalledExtensions.isEmpty()) {
                item {
                    ExtensionStatusRow(
                        title = if (snapshot.installedExtensions.isEmpty()) {
                            "No extensions installed"
                        } else {
                            "No installed results"
                        },
                        subtitle = "Select a repository and install an extension below.",
                    )
                }
            } else {
                items(filteredInstalledExtensions, key = { it.id }) { extension ->
                    InstalledExtensionListItem(
                        extension = extension,
                        uninstalling = uninstallingExtensionId == extension.id,
                        onUninstall = {
                            if (uninstallingExtensionId == null) {
                                uninstallingExtensionId = extension.id
                                uninstallMessage = null
                                scope.launch {
                                    runCatching { onUninstallExtension(extension.id, extension.packageType) }
                                        .onSuccess { result ->
                                            uninstallMessage = if (result.removed) {
                                                "Uninstalled ${extension.name}"
                                            } else {
                                                "No installed copy of ${extension.name} was found"
                                            }
                                            onRepoSaved()
                                        }
                                        .onFailure { error ->
                                            uninstallMessage = error.message ?: "Could not uninstall extension"
                                        }
                                    uninstallingExtensionId = null
                                }
                            }
                        },
                    )
                }
            }
            uninstallMessage?.let { message ->
                item {
                    Label(
                        text = message,
                        color = if (message.startsWith("Could not")) {
                            ChimahonPalette.error
                        } else {
                            ChimahonPalette.primary
                        },
                        size = 12,
                        maxLines = 2,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }
        }
        item { ListGroupHeader("Extension repositories") }
        if (snapshot.extensionRepos.isEmpty()) {
            item {
                ExtensionStatusRow(
                    title = "More extensions...",
                    subtitle = "Add a repository to browse available extensions.",
                    action = "Add repo",
                    onAction = { showRepoInput = true },
                )
            }
        } else {
            items(snapshot.extensionRepos, key = { it.baseUrl }) { repo ->
                ExtensionRepoListItem(
                    repo = repo,
                    selected = repo.baseUrl == selectedRepo?.baseUrl,
                    onClick = {
                        selectedRepo = repo
                        installMessage = null
                    },
                    onDelete = {
                        repoDeleteMessage = null
                        repoPendingDeletion = repo
                    },
                )
            }
        }
        repoDeleteMessage?.let { message ->
            item {
                ExtensionStatusRow(
                    title = "Could not delete repository",
                    subtitle = message,
                    error = true,
                )
            }
        }
        when (val catalog = catalogState) {
            ExtensionRepoCatalogUiState.Idle -> Unit
            ExtensionRepoCatalogUiState.Loading -> {
                item {
                    ExtensionStatusRow(
                        title = "Loading repository",
                        subtitle = "Fetching the extension catalog.",
                    )
                }
            }
            is ExtensionRepoCatalogUiState.Failed -> {
                item {
                    ExtensionStatusRow(
                        title = "Repository unavailable",
                        subtitle = catalog.message,
                        error = true,
                    )
                }
            }
            is ExtensionRepoCatalogUiState.Ready -> {
                val filteredExtensions = catalog.catalog.extensions.filter { extension ->
                    (
                        query.isBlank() ||
                            extension.name.contains(query, ignoreCase = true) ||
                            extension.id.contains(query, ignoreCase = true) ||
                            extension.language.contains(query, ignoreCase = true)
                        ) &&
                        (
                            browseSettings.enabledLanguages.isEmpty() ||
                                extension.language.sourceLanguageCode() in browseSettings.enabledLanguages
                            ) &&
                        (browseSettings.showNsfwSources || !extension.isNsfw) &&
                        when (selectedFilter) {
                            ExtensionFilter.All,
                            ExtensionFilter.Available,
                            -> true
                            ExtensionFilter.Installed -> false
                            ExtensionFilter.Nsfw -> extension.isNsfw
                        }
                }
                if (selectedFilter != ExtensionFilter.Installed) {
                    item {
                        ListGroupHeader("Available")
                    }
                    if (catalog.catalog.extensions.isEmpty()) {
                        item {
                            ExtensionStatusRow(
                                title = "No compatible extensions",
                                subtitle = "This repository did not expose a supported extension package.",
                            )
                        }
                    } else {
                        if (filteredExtensions.isEmpty()) {
                            item {
                                ExtensionStatusRow(
                                    title = "No results found",
                                    subtitle = "Try another name, package, language, or filter.",
                                )
                            }
                        }
                        filteredExtensions
                            .extensionGroups(browseSettings.groupSourcesByLanguage)
                            .forEach { (language, extensions) ->
                                if (language != null) {
                                    item {
                                        ListGroupHeader(language)
                                    }
                                }
                                items(extensions, key = { it.id }) { extension ->
                                    RepoExtensionListItem(
                                        extension = extension,
                                        showLanguage = browseSettings.showSourceLanguage,
                                        installed = extension.id in installedExtensionIds,
                                        installing = installingExtensionId == extension.id,
                                        onInstall = {
                                            if (installingExtensionId == null) {
                                                installingExtensionId = extension.id
                                                installMessage = null
                                                scope.launch {
                                                    runCatching { onInstallExtension(extension) }
                                                        .onSuccess { installed ->
                                                            installMessage = "Installed ${installed.name}"
                                                            onRepoSaved()
                                                        }
                                                        .onFailure { error ->
                                                            installMessage = error.message ?: "Could not install extension"
                                                        }
                                                    installingExtensionId = null
                                                }
                                            }
                                        },
                                    )
                                }
                            }
                    }
                    installMessage?.let { message ->
                        item {
                            Label(
                                text = message,
                                color = ChimahonPalette.primary,
                                size = 12,
                                maxLines = 2,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExtensionSectionToolbar(
    extensionCount: Int,
    repoCount: Int,
    onAddRepo: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Label(
                "$extensionCount extensions",
                ChimahonPalette.onSurface,
                14,
                weight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Label(
                "$repoCount repositories",
                ChimahonPalette.secondaryText,
                11,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        TextButtonLike("Add repo", onClick = onAddRepo)
    }
}

@Composable
private fun ExtensionStatusRow(
    title: String,
    subtitle: String,
    action: String? = null,
    error: Boolean = false,
    onAction: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Label(
                title,
                if (error) ChimahonPalette.error else ChimahonPalette.onSurface,
                13,
                weight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Label(
                subtitle,
                ChimahonPalette.secondaryText,
                11,
                maxLines = 2,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        action?.let {
            TextButtonLike(it, onClick = onAction)
        }
    }
}

@Composable
private fun ExtensionCatalogSearchField(
    query: String,
    resultCount: Int,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ChimahonPalette.surface)
            .border(1.dp, ChimahonPalette.divider, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (query.isBlank()) {
                Label(
                    "Search extensions",
                    ChimahonPalette.secondaryText,
                    13,
                    maxLines = 1,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = ChimahonPalette.onSurface,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Label(
            resultCount.toString(),
            ChimahonPalette.secondaryText,
            12,
            maxLines = 1,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun ExtensionRepoInputPanel(
    repoUrl: String,
    message: String?,
    saving: Boolean,
    onRepoUrlChange: (String) -> Unit,
    onAddRepo: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Label("Add extension repo", ChimahonPalette.onSurface, 14, weight = FontWeight.SemiBold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ChimahonPalette.background)
                .border(1.dp, ChimahonPalette.divider, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (repoUrl.isBlank()) {
                Label(
                    "https://example.org/repo/index.min.json",
                    ChimahonPalette.secondaryText,
                    12,
                    maxLines = 1,
                )
            }
            BasicTextField(
                value = repoUrl,
                onValueChange = onRepoUrlChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = ChimahonPalette.onSurface,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Label(
                text = message ?: "Saved repos are stored in the shared SQLDelight database.",
                color = if (message == null) ChimahonPalette.secondaryText else ChimahonPalette.primary,
                size = 12,
                maxLines = 2,
                modifier = Modifier.weight(1f),
            )
            TextButtonLike(
                text = if (saving) "Saving" else "Add repo",
                onClick = onAddRepo,
            )
        }
    }
}

@Composable
private fun ExtensionRepoListItem(
    repo: ChimahonExtensionRepoEntry,
    selected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (selected) ChimahonPalette.primaryContainer else ChimahonPalette.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Label(
                "R",
                if (selected) ChimahonPalette.primary else ChimahonPalette.secondaryText,
                11,
                weight = FontWeight.Bold,
                maxLines = 1,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            Label(repo.name, ChimahonPalette.onSurface, 13, weight = FontWeight.SemiBold, maxLines = 1)
            Label(
                repo.baseUrl,
                ChimahonPalette.secondaryText,
                11,
                maxLines = 2,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 7.dp),
        ) {
            Label(
                if (selected) "Selected" else "Browse",
                if (selected) ChimahonPalette.primary else ChimahonPalette.secondaryText,
                11,
                weight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
        ChapterQuickAction(
            icon = UiIcon.Delete,
            contentDescription = "Delete ${repo.name} repository",
            active = false,
            onClick = onDelete,
        )
    }
}

@Composable
private fun ExtensionRepoDeleteDialog(
    repo: ChimahonExtensionRepoEntry,
    deleting: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ChimahonPalette.surface)
                .padding(20.dp),
        ) {
            Label(
                "Delete repository?",
                ChimahonPalette.onSurface,
                18,
                weight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Label(
                "Remove ${repo.name} from extension repositories?\n${repo.baseUrl}",
                ChimahonPalette.secondaryText,
                13,
                lineHeight = 19,
                maxLines = 4,
                modifier = Modifier.padding(top = 10.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ExtensionRepoDialogAction(
                    text = "Cancel",
                    color = ChimahonPalette.primary,
                    enabled = !deleting,
                    onClick = onDismiss,
                )
                ExtensionRepoDialogAction(
                    text = if (deleting) "Deleting" else "Delete",
                    color = ChimahonPalette.error,
                    enabled = !deleting,
                    onClick = onDelete,
                )
            }
        }
    }
}

@Composable
private fun ExtensionRepoDialogAction(
    text: String,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Label(
            text,
            if (enabled) color else ChimahonPalette.secondaryText,
            12,
            weight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun InstalledExtensionListItem(
    extension: ChimahonInstalledExtensionEntry,
    uninstalling: Boolean,
    onUninstall: () -> Unit,
) {
    ExtensionListRow(
        marker = extension.packageType.marker,
        title = extension.name,
        subtitle = "${extension.sourceCount} source(s)  \u00b7  ${extension.version}",
        action = if (uninstalling) "Removing" else "Uninstall",
        active = true,
        onClick = if (uninstalling) {
            {}
        } else {
            onUninstall
        },
    )
}

@Composable
private fun RepoExtensionListItem(
    extension: ChimahonRepoExtensionEntry,
    showLanguage: Boolean,
    installed: Boolean,
    installing: Boolean,
    onInstall: () -> Unit,
) {
    ExtensionListRow(
        marker = extension.packageType.marker,
        title = extension.name,
        subtitle = buildString {
            if (showLanguage) {
                append(extension.language.ifBlank { "multi" }.uppercase())
                append("  \u00b7  ")
            }
            append(extension.version)
            append("  \u00b7  ")
            append(extension.sourceCount)
            append(" source(s)")
            if (extension.isNsfw) append("  \u00b7  NSFW")
        },
        action = when {
            installing -> "Installing"
            installed -> "Installed"
            else -> "Install"
        },
        active = installed,
        onClick = if (installed || installing) {
            {}
        } else {
            onInstall
        },
    )
}

@Composable
private fun ExtensionListRow(
    marker: String,
    title: String,
    subtitle: String,
    action: String,
    active: Boolean = false,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (active) ChimahonPalette.primaryContainer else ChimahonPalette.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Label(
                marker,
                if (active) ChimahonPalette.primary else ChimahonPalette.secondaryText,
                11,
                weight = FontWeight.Bold,
                maxLines = 1,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            Label(title, ChimahonPalette.onSurface, 13, weight = FontWeight.SemiBold, maxLines = 1)
            Label(
                subtitle,
                ChimahonPalette.secondaryText,
                11,
                maxLines = 2,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 7.dp),
        ) {
            Label(
                action,
                if (active) ChimahonPalette.primary else ChimahonPalette.secondaryText,
                11,
                weight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
    }
}

private val ChimahonExtensionPackageType.marker: String
    get() = when (this) {
        ChimahonExtensionPackageType.JavaScript -> "JS"
        ChimahonExtensionPackageType.AndroidApk -> "APK"
    }

@Composable
private fun MigrateSection(
    library: List<ChimahonMangaEntry>,
    sources: List<ChimahonSourceEntry>,
    helpVisible: Boolean,
    onOpenManga: (Long) -> Unit,
    onLoadSourcePreview: suspend (Long, ChimahonSourceBrowseMode, String, Int) -> ChimahonSourcePreview,
    onLoadRemoteMangaDetail: suspend (ChimahonRemoteMangaEntry) -> ChimahonRemoteMangaDetail,
    onAddRemoteMangaToLibrary: suspend (ChimahonRemoteMangaDetail) -> Long,
    onSetMangaFavorite: suspend (Long, Boolean) -> Unit,
    onMigrationComplete: (Long) -> Unit,
) {
    var selectedSourceId by remember { mutableStateOf<Long?>(null) }
    var selectedMangaId by remember { mutableStateOf<Long?>(null) }
    var targetSourceId by remember { mutableStateOf<Long?>(null) }
    var searchText by remember { mutableStateOf("") }
    var submittedQuery by remember { mutableStateOf("") }
    var previewState by remember { mutableStateOf<SourcePreviewUiState>(SourcePreviewUiState.Idle) }
    var migratingUrl by remember { mutableStateOf<String?>(null) }
    var migrationMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val libraryBySource = library.groupBy { it.sourceId }
    val selectedSource = sources.firstOrNull { it.id == selectedSourceId }
    val sourceManga = selectedSourceId?.let { libraryBySource[it].orEmpty() }.orEmpty()
    val selectedManga = library.firstOrNull { it.id == selectedMangaId }
    val targetSource = sources.firstOrNull { it.id == targetSourceId }

    LaunchedEffect(targetSourceId, submittedQuery) {
        val sourceId = targetSourceId
        if (sourceId == null || submittedQuery.isBlank()) {
            previewState = SourcePreviewUiState.Idle
            return@LaunchedEffect
        }
        previewState = SourcePreviewUiState.Loading
        previewState = runCatching {
            onLoadSourcePreview(sourceId, ChimahonSourceBrowseMode.Search, submittedQuery, 1)
        }.fold(
            onSuccess = SourcePreviewUiState::Ready,
            onFailure = { SourcePreviewUiState.Failed(it.message ?: "Could not search the target source.") },
        )
    }

    if (library.isEmpty()) {
        EmptyMobileState(
            marker = "M",
            icon = UiIcon.Swap,
            title = "Your library is empty",
            detail = "Add manga to your library before starting a migration.",
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        if (helpVisible) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ChimahonPalette.primaryContainer)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconGlyph(
                        icon = UiIcon.Info,
                        contentDescription = "",
                        tint = ChimahonPalette.primary,
                        modifier = Modifier.size(22.dp),
                    )
                    Label(
                        "Choose the current source, select a library title, then pick its replacement source.",
                        ChimahonPalette.onPrimaryContainer,
                        12,
                        lineHeight = 17,
                        modifier = Modifier.padding(start = 12.dp),
                    )
                }
            }
        }
        when {
            selectedSource == null -> {
            item {
                Label(
                    "Select a source to migrate from",
                    ChimahonPalette.onSurface,
                    16,
                    weight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                )
            }
            sources
                .filter { libraryBySource[it.id].orEmpty().isNotEmpty() }
                .groupBy { it.language.ifBlank { "multi" }.uppercase() }
                .forEach { (language, sourceGroup) ->
                    item {
                        ListGroupHeader(language)
                    }
                    items(sourceGroup, key = { it.id }) { source ->
                        MigrateSourceListItem(
                            source = source,
                            mangaCount = libraryBySource[source.id].orEmpty().size,
                            onClick = {
                                selectedSourceId = source.id
                                selectedMangaId = null
                                targetSourceId = null
                            },
                        )
                    }
                }
            }
            selectedManga == null -> {
            item {
                MigrateBackHeader(
                    title = selectedSource.name,
                    subtitle = "${sourceManga.size} manga to migrate",
                    onBack = { selectedSourceId = null },
                )
            }
            items(sourceManga, key = { it.id }) { entry ->
                MigrateMangaListItem(
                    manga = entry,
                    onOpen = { onOpenManga(entry.id) },
                    onMigrate = {
                        selectedMangaId = entry.id
                        targetSourceId = null
                        migrationMessage = null
                    },
                )
            }
            }
            targetSource == null -> {
                item {
                    MigrateBackHeader(
                        title = selectedManga.title,
                        subtitle = "Choose a replacement source",
                        onBack = { selectedMangaId = null },
                    )
                }
                sources
                    .filterNot { it.id == selectedManga.sourceId }
                    .groupBy { it.language.ifBlank { "multi" }.uppercase() }
                    .forEach { (language, sourceGroup) ->
                        item { ListGroupHeader(language) }
                        items(sourceGroup, key = { it.id }) { source ->
                            MigrateSourceListItem(
                                source = source,
                                mangaCount = 0,
                                showCount = false,
                                onClick = {
                                    targetSourceId = source.id
                                    searchText = selectedManga.title
                                    submittedQuery = selectedManga.title
                                    migrationMessage = null
                                },
                            )
                        }
                    }
            }
            else -> {
                item {
                    MigrateBackHeader(
                        title = targetSource.name,
                        subtitle = "Find a replacement for ${selectedManga.title}",
                        onBack = { targetSourceId = null },
                    )
                }
                item {
                    SourceSearchPanel(
                        query = searchText,
                        onQueryChange = {
                            searchText = it
                            migrationMessage = null
                        },
                        onSearch = {
                            searchText.trim().takeIf { it.isNotEmpty() }?.let {
                                submittedQuery = it
                            }
                        },
                    )
                }
                migrationMessage?.let { message ->
                    item {
                        Label(
                            message,
                            if (message.startsWith("Migrated")) ChimahonPalette.primary else ChimahonPalette.error,
                            12,
                            lineHeight = 17,
                            maxLines = 3,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        )
                    }
                }
                when (val preview = previewState) {
                    SourcePreviewUiState.Idle -> item {
                        EmptyListPanel("Q", "Search the target source", "Enter a title to find a replacement manga.")
                    }
                    SourcePreviewUiState.Loading -> item {
                        EmptyListPanel("M", "Searching ${targetSource.name}", "Looking for matching manga.")
                    }
                    is SourcePreviewUiState.Failed -> item {
                        EmptyListPanel("!", "Search failed", preview.message)
                    }
                    is SourcePreviewUiState.Ready -> {
                        if (preview.preview.entries.isEmpty()) {
                            item {
                                EmptyListPanel("M", "No matches found", "Try a shorter or alternate title.")
                            }
                        } else {
                            items(preview.preview.entries, key = { "${it.sourceId}:${it.url}" }) { candidate ->
                                MigrateCandidateListItem(
                                    candidate = candidate,
                                    migrating = migratingUrl == candidate.url,
                                    onMigrate = {
                                        if (migratingUrl == null) {
                                            migratingUrl = candidate.url
                                            migrationMessage = null
                                            scope.launch {
                                                runCatching {
                                                    val detail = onLoadRemoteMangaDetail(candidate)
                                                    val newMangaId = onAddRemoteMangaToLibrary(detail)
                                                    onSetMangaFavorite(selectedManga.id, false)
                                                    newMangaId
                                                }.onSuccess { newMangaId ->
                                                    migrationMessage = "Migrated ${selectedManga.title}"
                                                    onMigrationComplete(newMangaId)
                                                }.onFailure { error ->
                                                    migrationMessage = error.message ?: "Migration failed."
                                                }
                                                migratingUrl = null
                                            }
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MigrateSourceListItem(
    source: ChimahonSourceEntry,
    mangaCount: Int,
    showCount: Boolean = true,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(coverColor(source.id, source.name).copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Label(
                source.name.firstOrNull()?.uppercase() ?: "S",
                coverColor(source.id, source.name),
                14,
                weight = FontWeight.Bold,
                maxLines = 1,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
        ) {
            Label(source.name, ChimahonPalette.onSurface, 14, weight = FontWeight.SemiBold, maxLines = 1)
            Label(
                source.language.ifBlank { "multi" }.uppercase(),
                ChimahonPalette.secondaryText,
                11,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        if (showCount) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ChimahonPalette.primaryContainer)
                    .padding(horizontal = 9.dp, vertical = 4.dp),
            ) {
                Label(
                    mangaCount.toString(),
                    ChimahonPalette.primary,
                    11,
                    weight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
        } else {
            IconGlyph(
                icon = UiIcon.Forward,
                contentDescription = "Select ${source.name}",
                tint = ChimahonPalette.secondaryText,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun MigrateMangaListItem(
    manga: ChimahonMangaEntry,
    onOpen: () -> Unit,
    onMigrate: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(ChimahonPalette.surface)
            .clickable(onClick = onMigrate)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ThumbnailArtwork(
            url = manga.thumbnailUrl,
            title = manga.title,
            fallbackColor = coverColor(manga.id, manga.title),
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onOpen),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
        ) {
            Label(manga.title, ChimahonPalette.onSurface, 13, weight = FontWeight.SemiBold, maxLines = 2)
            Label(
                manga.author ?: manga.artist ?: "Unknown author",
                ChimahonPalette.secondaryText,
                11,
                maxLines = 1,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        Label(
            "Migrate",
            ChimahonPalette.primary,
            11,
            weight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier
                .clickable(onClick = onMigrate)
                .padding(horizontal = 8.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun MigrateBackHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .clickable(onClick = onBack)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconGlyph(
            icon = UiIcon.Back,
            contentDescription = "Back",
            tint = ChimahonPalette.primary,
            modifier = Modifier.size(20.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            Label(title, ChimahonPalette.onSurface, 14, weight = FontWeight.SemiBold, maxLines = 1)
            Label(
                subtitle,
                ChimahonPalette.secondaryText,
                11,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun MigrateCandidateListItem(
    candidate: ChimahonRemoteMangaEntry,
    migrating: Boolean,
    onMigrate: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .background(ChimahonPalette.surface)
            .clickable(onClick = onMigrate)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ThumbnailArtwork(
            url = candidate.thumbnailUrl,
            title = candidate.title,
            fallbackColor = coverColor(candidate.sourceId xor candidate.url.hashCode().toLong(), candidate.title),
            modifier = Modifier
                .width(52.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp)),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
        ) {
            Label(candidate.title, ChimahonPalette.onSurface, 13, weight = FontWeight.SemiBold, maxLines = 2)
            Label(
                candidate.author ?: candidate.status,
                ChimahonPalette.secondaryText,
                11,
                maxLines = 1,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        Label(
            if (migrating) "Migrating" else "Migrate",
            ChimahonPalette.primary,
            11,
            weight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun MangaDetailHome(
    snapshot: ChimahonSnapshot,
    mangaId: Long,
    onOpenReader: (ChimahonReaderRequest) -> Unit,
    onSetMangaFavorite: suspend (Long, Boolean) -> Unit,
    onSetMangaCategories: suspend (Long, Set<Long>) -> Unit,
    onSetMangaNotes: suspend (Long, String) -> Unit,
    onSetMangaChaptersRead: suspend (Long, Boolean) -> Unit,
    onSetChapterRead: suspend (Long, Boolean) -> Unit,
    onSetChapterBookmark: suspend (Long, Boolean) -> Unit,
    onEnqueueDownload: suspend (Long) -> ChimahonDownloadQueueData,
    onEnqueueDownloads: suspend (Collection<Long>) -> ChimahonDownloadQueueData,
    onLoadDownloadSnapshot: suspend () -> ChimahonDownloadSnapshot,
    onSelectBrowse: () -> Unit,
) {
    val manga = snapshot.mangaDetails[mangaId]
    if (manga == null) {
        EmptyMobileState(
            marker = "?",
            title = "Manga not found",
            detail = "This entry is not present in the shared database snapshot.",
            action = "Browse sources",
            onAction = onSelectBrowse,
        )
        return
    }

    val chapters = snapshot.chaptersByMangaId[mangaId].orEmpty()
    var selectedChapterFilter by remember(mangaId) { mutableStateOf(ChapterFilter.All) }
    var chapterDescending by remember(mangaId) { mutableStateOf(true) }
    var chapterFiltersVisible by remember(mangaId) { mutableStateOf(false) }
    var chapterQuery by remember(mangaId) { mutableStateOf("") }
    val selectedChapterIds = remember(mangaId) { mutableStateMapOf<Long, Boolean>() }
    var downloadSnapshot by remember(mangaId) { mutableStateOf<ChimahonDownloadSnapshot?>(null) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(mangaId) {
        downloadSnapshot = runCatching { onLoadDownloadSnapshot() }.getOrNull()
    }
    val refreshDownloadSnapshot: suspend () -> Unit = {
        downloadSnapshot = runCatching { onLoadDownloadSnapshot() }.getOrNull() ?: downloadSnapshot
    }
    val visibleChapters = chapters
        .filter { chapter ->
            chapterQuery.isBlank() ||
                chapter.name.contains(chapterQuery, ignoreCase = true) ||
                chapter.scanlator.orEmpty().contains(chapterQuery, ignoreCase = true)
        }
        .filter { chapter ->
            when (selectedChapterFilter) {
                ChapterFilter.All -> true
                ChapterFilter.Unread -> !chapter.read
                ChapterFilter.Started -> chapter.lastPageRead > 0L && !chapter.read
                ChapterFilter.Bookmarked -> chapter.bookmarked
            }
        }
        .let { entries ->
            if (chapterDescending) {
                entries.sortedWith(compareByDescending<ChimahonChapterEntry> { it.chapterNumber }.thenBy { it.name })
            } else {
                entries.sortedWith(compareBy<ChimahonChapterEntry> { it.chapterNumber }.thenBy { it.name })
            }
        }
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth >= 900.dp) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(0.42f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    MangaDetailHeader(
                        manga = manga,
                        sourceName = snapshot.sourceName(manga.sourceId),
                        chapterCount = chapters.size,
                        centered = true,
                    )
                    MangaActionRow(
                        manga = manga,
                        chapters = chapters,
                        onOpenReader = onOpenReader,
                        onSetMangaFavorite = onSetMangaFavorite,
                        onSetMangaChaptersRead = onSetMangaChaptersRead,
                        onEnqueueDownloads = { chapterIds ->
                            val result = onEnqueueDownloads(chapterIds)
                            refreshDownloadSnapshot()
                            result
                        },
                        onSelectBrowse = onSelectBrowse,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                    MangaCategoryPanel(
                        categories = snapshot.libraryCategoryTabs().filterNot { it.id == ALL_LIBRARY_CATEGORY_ID },
                        selectedCategoryIds = snapshot.libraryCategoryMemberships[manga.id].orEmpty().toSet(),
                        onToggleCategory = { categoryId ->
                            onSetMangaCategories(
                                manga.id,
                                toggledCategoryIds(
                                    current = snapshot.libraryCategoryMemberships[manga.id].orEmpty().toSet(),
                                    categoryId = categoryId,
                                ),
                            )
                        },
                    )
                    MangaDescriptionPanel(
                        manga = manga,
                        onSetMangaNotes = onSetMangaNotes,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(ChimahonPalette.divider),
                )
                MangaChapterPane(
                    chapters = visibleChapters,
                    allChapterCount = chapters.size,
                    manga = manga,
                    selectedFilter = selectedChapterFilter,
                    descending = chapterDescending,
                    filtersVisible = chapterFiltersVisible,
                    query = chapterQuery,
                    onToggleFilters = { chapterFiltersVisible = !chapterFiltersVisible },
                    onQueryChange = { chapterQuery = it },
                    onFilterChange = { selectedChapterFilter = it },
                    onSortDirectionChange = { chapterDescending = it },
                    onOpenReader = onOpenReader,
                    onSetChapterRead = onSetChapterRead,
                    onSetChapterBookmark = onSetChapterBookmark,
                    onEnqueueDownload = { chapterId ->
                        val result = onEnqueueDownload(chapterId)
                        refreshDownloadSnapshot()
                        result
                    },
                    downloadStatuses = downloadSnapshot?.chaptersById.orEmpty(),
                    selectedChapterIds = selectedChapterIds.filterValues { it }.keys,
                    onToggleChapterSelected = { chapterId ->
                        selectedChapterIds[chapterId] = selectedChapterIds[chapterId] != true
                    },
                    onSelectAllChapters = {
                        visibleChapters.forEach { selectedChapterIds[it.id] = true }
                    },
                    onInvertChapterSelection = {
                        visibleChapters.forEach { chapter ->
                            selectedChapterIds[chapter.id] = selectedChapterIds[chapter.id] != true
                        }
                    },
                    onClearChapterSelection = { selectedChapterIds.clear() },
                    onMarkSelectedRead = {
                        val selected = visibleChapters.filter { selectedChapterIds[it.id] == true }
                        scope.launch {
                            selected.forEach { chapter ->
                                runCatching { onSetChapterRead(chapter.id, true) }
                            }
                            selectedChapterIds.clear()
                        }
                    },
                    onBookmarkSelected = {
                        val selected = visibleChapters.filter { selectedChapterIds[it.id] == true }
                        scope.launch {
                            selected.forEach { chapter ->
                                runCatching { onSetChapterBookmark(chapter.id, true) }
                            }
                            selectedChapterIds.clear()
                        }
                    },
                    onDownloadSelected = {
                        val selected = visibleChapters.filter { selectedChapterIds[it.id] == true }
                        scope.launch {
                            if (runCatching { onEnqueueDownloads(selected.map { it.id }) }.isSuccess) {
                                refreshDownloadSnapshot()
                            }
                            selectedChapterIds.clear()
                        }
                    },
                    modifier = Modifier
                        .weight(0.58f)
                        .fillMaxHeight(),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                item {
                    MangaDetailHeader(
                        manga = manga,
                        sourceName = snapshot.sourceName(manga.sourceId),
                        chapterCount = chapters.size,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
                item {
                    MangaActionRow(
                        manga = manga,
                        chapters = chapters,
                        onOpenReader = onOpenReader,
                        onSetMangaFavorite = onSetMangaFavorite,
                        onSetMangaChaptersRead = onSetMangaChaptersRead,
                        onEnqueueDownloads = { chapterIds ->
                            val result = onEnqueueDownloads(chapterIds)
                            refreshDownloadSnapshot()
                            result
                        },
                        onSelectBrowse = onSelectBrowse,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
                item {
                    MangaCategoryPanel(
                        categories = snapshot.libraryCategoryTabs().filterNot { it.id == ALL_LIBRARY_CATEGORY_ID },
                        selectedCategoryIds = snapshot.libraryCategoryMemberships[manga.id].orEmpty().toSet(),
                        onToggleCategory = { categoryId ->
                            onSetMangaCategories(
                                manga.id,
                                toggledCategoryIds(
                                    current = snapshot.libraryCategoryMemberships[manga.id].orEmpty().toSet(),
                                    categoryId = categoryId,
                                ),
                            )
                        },
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }
                item {
                    MangaDescriptionPanel(
                        manga = manga,
                        onSetMangaNotes = onSetMangaNotes,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }
                item {
                    ChapterHeader(
                        visibleChapterCount = visibleChapters.size,
                        allChapterCount = chapters.size,
                        filtersVisible = chapterFiltersVisible,
                        onToggleFilters = { chapterFiltersVisible = !chapterFiltersVisible },
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
                if (chapterFiltersVisible) {
                    item {
                        ChapterFilterPanel(
                            selectedFilter = selectedChapterFilter,
                            descending = chapterDescending,
                            query = chapterQuery,
                            onQueryChange = { chapterQuery = it },
                            onFilterChange = { selectedChapterFilter = it },
                            onSortDirectionChange = { chapterDescending = it },
                        )
                    }
                }
                if (visibleChapters.isNotEmpty()) {
                    item {
                        MultiSelectionBar(
                            selectedCount = selectedChapterIds.count { it.value },
                            totalCount = visibleChapters.size,
                            onSelectAll = {
                                visibleChapters.forEach { selectedChapterIds[it.id] = true }
                            },
                            onInvert = {
                                visibleChapters.forEach { chapter ->
                                    selectedChapterIds[chapter.id] = selectedChapterIds[chapter.id] != true
                                }
                            },
                            onClear = { selectedChapterIds.clear() },
                            actions = listOf(
                                SelectionAction("Mark read", UiIcon.DoneAll) {
                                    val selected = visibleChapters.filter { selectedChapterIds[it.id] == true }
                                    scope.launch {
                                        selected.forEach { chapter ->
                                            runCatching { onSetChapterRead(chapter.id, true) }
                                        }
                                        selectedChapterIds.clear()
                                    }
                                },
                                SelectionAction("Bookmark", UiIcon.Bookmark) {
                                    val selected = visibleChapters.filter { selectedChapterIds[it.id] == true }
                                    scope.launch {
                                        selected.forEach { chapter ->
                                            runCatching { onSetChapterBookmark(chapter.id, true) }
                                        }
                                        selectedChapterIds.clear()
                                    }
                                },
                                SelectionAction("Download", UiIcon.Download) {
                                    val selected = visibleChapters.filter { selectedChapterIds[it.id] == true }
                                    scope.launch {
                                        if (runCatching { onEnqueueDownloads(selected.map { it.id }) }.isSuccess) {
                                            refreshDownloadSnapshot()
                                        }
                                        selectedChapterIds.clear()
                                    }
                                },
                            ),
                        )
                    }
                }
                if (visibleChapters.isEmpty()) {
                    item {
                        EmptyListPanel(
                            marker = "C",
                            title = if (chapters.isEmpty()) "No chapters" else "No matching chapters",
                            detail = if (chapters.isEmpty()) {
                                "Chapters from the shared database will appear here after this manga is initialized."
                            } else {
                                "Change the chapter filter to show more chapters."
                            },
                        )
                    }
                } else {
                    items(visibleChapters, key = { it.id }) { chapter ->
                        MangaChapterListItem(
                            chapter = chapter,
                            selected = selectedChapterIds[chapter.id] == true,
                            onToggleSelected = {
                                selectedChapterIds[chapter.id] = selectedChapterIds[chapter.id] != true
                            },
                            onClick = {
                                onOpenReader(chapter.toReaderRequest(manga, chapters))
                            },
                            onSetRead = { read -> onSetChapterRead(chapter.id, read) },
                            onSetBookmark = { bookmarked -> onSetChapterBookmark(chapter.id, bookmarked) },
                            onEnqueueDownload = {
                                val result = onEnqueueDownload(chapter.id)
                                refreshDownloadSnapshot()
                                result
                            },
                            downloadStatus = downloadSnapshot?.statusForChapter(chapter.id),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MangaChapterPane(
    chapters: List<ChimahonChapterEntry>,
    allChapterCount: Int,
    manga: ChimahonMangaEntry,
    selectedFilter: ChapterFilter,
    descending: Boolean,
    filtersVisible: Boolean,
    query: String,
    onToggleFilters: () -> Unit,
    onQueryChange: (String) -> Unit,
    onFilterChange: (ChapterFilter) -> Unit,
    onSortDirectionChange: (Boolean) -> Unit,
    onOpenReader: (ChimahonReaderRequest) -> Unit,
    onSetChapterRead: suspend (Long, Boolean) -> Unit,
    onSetChapterBookmark: suspend (Long, Boolean) -> Unit,
    onEnqueueDownload: suspend (Long) -> ChimahonDownloadQueueData,
    downloadStatuses: Map<Long, ChimahonChapterDownloadStatus>,
    selectedChapterIds: Set<Long>,
    onToggleChapterSelected: (Long) -> Unit,
    onSelectAllChapters: () -> Unit,
    onInvertChapterSelection: () -> Unit,
    onClearChapterSelection: () -> Unit,
    onMarkSelectedRead: () -> Unit,
    onBookmarkSelected: () -> Unit,
    onDownloadSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 10.dp, bottom = 18.dp),
    ) {
        item {
            ChapterHeader(
                visibleChapterCount = chapters.size,
                allChapterCount = allChapterCount,
                filtersVisible = filtersVisible,
                onToggleFilters = onToggleFilters,
            )
        }
        if (filtersVisible) {
            item {
                ChapterFilterPanel(
                selectedFilter = selectedFilter,
                descending = descending,
                query = query,
                onQueryChange = onQueryChange,
                onFilterChange = onFilterChange,
                    onSortDirectionChange = onSortDirectionChange,
                )
            }
        }
        if (chapters.isNotEmpty()) {
            item {
                MultiSelectionBar(
                    selectedCount = selectedChapterIds.size,
                    totalCount = chapters.size,
                    onSelectAll = onSelectAllChapters,
                    onInvert = onInvertChapterSelection,
                    onClear = onClearChapterSelection,
                    actions = listOf(
                        SelectionAction("Mark read", UiIcon.DoneAll, onMarkSelectedRead),
                        SelectionAction("Bookmark", UiIcon.Bookmark, onBookmarkSelected),
                        SelectionAction("Download", UiIcon.Download, onDownloadSelected),
                    ),
                )
            }
        }
        if (chapters.isEmpty()) {
            item {
                EmptyListPanel(
                    marker = "C",
                    title = if (allChapterCount == 0) "No chapters" else "No matching chapters",
                    detail = if (allChapterCount == 0) {
                        "Chapters from the shared database will appear here after this manga is initialized."
                    } else {
                        "Change the chapter filter to show more chapters."
                    },
                )
            }
        } else {
            items(chapters, key = { it.id }) { chapter ->
                MangaChapterListItem(
                    chapter = chapter,
                    selected = chapter.id in selectedChapterIds,
                    onToggleSelected = { onToggleChapterSelected(chapter.id) },
                    onClick = {
                        onOpenReader(chapter.toReaderRequest(manga, chapters))
                    },
                    onSetRead = { read -> onSetChapterRead(chapter.id, read) },
                    onSetBookmark = { bookmarked -> onSetChapterBookmark(chapter.id, bookmarked) },
                    onEnqueueDownload = { onEnqueueDownload(chapter.id) },
                    downloadStatus = downloadStatuses[chapter.id],
                )
            }
        }
    }
}

@Composable
private fun MangaDetailHeader(
    manga: ChimahonMangaEntry,
    sourceName: String,
    chapterCount: Int,
    centered: Boolean = false,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
    ) {
        val useCenteredLayout = centered || maxWidth < 420.dp
        if (!useCenteredLayout) {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                MangaCoverTile(
                    manga = manga,
                    modifier = Modifier
                        .width(154.dp)
                        .height(226.dp),
                )
                MangaInfoColumn(
                    manga = manga,
                    sourceName = sourceName,
                    chapterCount = chapterCount,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MangaCoverTile(
                    manga = manga,
                    modifier = Modifier
                        .width(if (centered) 176.dp else 132.dp)
                        .height(if (centered) 258.dp else 194.dp),
                )
                MangaInfoColumn(
                    manga = manga,
                    sourceName = sourceName,
                    chapterCount = chapterCount,
                    centered = true,
                    modifier = Modifier.padding(top = 14.dp),
                )
            }
        }
    }
}

@Composable
private fun MangaCoverTile(
    manga: ChimahonMangaEntry,
    modifier: Modifier = Modifier,
    topStartLabel: String? = null,
    showStatus: Boolean = true,
    showLibraryBadge: Boolean = true,
    bottomEndAction: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(coverColor(manga.id, manga.title)),
    ) {
        ThumbnailArtwork(
            url = manga.thumbnailUrl,
            title = manga.title,
            fallbackColor = coverColor(manga.id, manga.title),
            modifier = Modifier.fillMaxSize(),
        )
        if (topStartLabel != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.44f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Label(topStartLabel, Color.White, 10, weight = FontWeight.SemiBold, maxLines = 1)
            }
        }
        if (manga.favorite && showLibraryBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.44f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Label("Library", Color.White, 10, weight = FontWeight.SemiBold, maxLines = 1)
            }
        }
        if (showStatus) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.42f))
                    .padding(8.dp),
            ) {
                Label(
                    text = manga.status,
                    color = Color.White,
                    size = 10,
                    weight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
        }
        bottomEndAction?.let { onContinue ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ChimahonPalette.primary)
                    .clickable(onClick = onContinue),
                contentAlignment = Alignment.Center,
            ) {
                IconGlyph(
                    icon = UiIcon.Play,
                    contentDescription = "Continue reading",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun ThumbnailArtwork(
    url: String?,
    title: String,
    fallbackColor: Color,
    modifier: Modifier = Modifier,
) {
    val loadThumbnail = LocalThumbnailLoader.current
    var state by remember(url) {
        mutableStateOf<ThumbnailUiState>(
            if (url.isNullOrBlank()) ThumbnailUiState.Failed else ThumbnailUiState.Loading,
        )
    }

    LaunchedEffect(url) {
        val thumbnailUrl = url?.takeIf { it.isNotBlank() } ?: return@LaunchedEffect
        state = ThumbnailUiState.Loading
        state = runCatching {
            loadThumbnail(thumbnailUrl).decodeToImageBitmap()
        }.fold(
            onSuccess = ThumbnailUiState::Ready,
            onFailure = { ThumbnailUiState.Failed },
        )
    }

    Box(
        modifier = modifier.background(fallbackColor),
        contentAlignment = Alignment.Center,
    ) {
        when (val current = state) {
            is ThumbnailUiState.Ready -> {
                Image(
                    bitmap = current.image,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            ThumbnailUiState.Loading,
            ThumbnailUiState.Failed,
            -> {
                Label(
                    text = title.firstOrNull()?.uppercase() ?: "?",
                    color = Color.White,
                    size = 38,
                    weight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun MangaInfoColumn(
    manga: ChimahonMangaEntry,
    sourceName: String,
    chapterCount: Int,
    centered: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        Label(
            manga.title,
            ChimahonPalette.onSurface,
            22,
            weight = FontWeight.SemiBold,
            lineHeight = 27,
        )
        Label(
            text = manga.author ?: manga.artist ?: "Unknown author",
            color = ChimahonPalette.secondaryText,
            size = 13,
            maxLines = 1,
            modifier = Modifier.padding(top = 5.dp),
        )
        Label(
            text = "${manga.status}  \u00b7  $sourceName",
            color = ChimahonPalette.secondaryText,
            size = 12,
            maxLines = 1,
            modifier = Modifier.padding(top = 5.dp),
        )
        Label(
            text = "$chapterCount chapters",
            color = ChimahonPalette.secondaryText,
            size = 11,
            maxLines = 1,
            modifier = Modifier.padding(top = 3.dp),
        )
    }
}

@Composable
private fun DetailStatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Label(label, ChimahonPalette.secondaryText, 11, weight = FontWeight.SemiBold, modifier = Modifier.width(92.dp))
        Label(value, ChimahonPalette.onSurface, 12, maxLines = 1, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MangaActionRow(
    manga: ChimahonMangaEntry,
    chapters: List<ChimahonChapterEntry>,
    onOpenReader: (ChimahonReaderRequest) -> Unit,
    onSetMangaFavorite: suspend (Long, Boolean) -> Unit,
    onSetMangaChaptersRead: suspend (Long, Boolean) -> Unit,
    onEnqueueDownloads: suspend (Collection<Long>) -> ChimahonDownloadQueueData,
    onSelectBrowse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val unread = chapters.count { !it.read }
    var busyAction by remember(manga.id) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        MangaActionButton(
            icon = UiIcon.Play,
            title = if (chapters.any { it.lastPageRead > 0L }) "Resume" else "Start",
            modifier = Modifier.weight(1f),
            onClick = {
                (
                    chapters.firstOrNull { it.lastPageRead > 0L }
                        ?: chapters.lastOrNull { !it.read }
                        ?: chapters.lastOrNull()
                    )?.let { chapter ->
                    onOpenReader(chapter.toReaderRequest(manga, chapters))
                }
            },
        )
        MangaActionButton(
            icon = UiIcon.DoneAll,
            title = if (unread > 0) "Mark read" else "Mark unread",
            active = busyAction == "read",
            modifier = Modifier.weight(1f),
            onClick = {
                if (chapters.isNotEmpty() && busyAction == null) {
                    scope.launch {
                        busyAction = "read"
                        runCatching {
                            onSetMangaChaptersRead(manga.id, unread > 0)
                        }
                        busyAction = null
                    }
                }
            },
        )
        MangaActionButton(
            icon = UiIcon.Download,
            title = if (busyAction == "download") "Queued" else "Download",
            active = busyAction == "download",
            modifier = Modifier.weight(1f),
            onClick = {
                if (chapters.isNotEmpty() && busyAction == null) {
                    scope.launch {
                        busyAction = "download"
                        runCatching { onEnqueueDownloads(chapters.map { it.id }) }
                        busyAction = null
                    }
                }
            },
        )
        MangaActionButton(
            icon = UiIcon.Swap,
            title = "Migrate",
            modifier = Modifier.weight(1f),
            onClick = onSelectBrowse,
        )
        MangaActionButton(
            icon = if (manga.favorite) UiIcon.Favorite else UiIcon.FavoriteBorder,
            title = if (manga.favorite) "In library" else "Favorite",
            active = manga.favorite || busyAction == "favorite",
            modifier = Modifier.weight(1f),
            onClick = {
                if (busyAction == null) {
                    scope.launch {
                        busyAction = "favorite"
                        runCatching {
                            onSetMangaFavorite(manga.id, !manga.favorite)
                        }
                        busyAction = null
                    }
                }
            },
        )
    }
}

@Composable
private fun MangaActionButton(
    icon: UiIcon,
    title: String,
    active: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .height(70.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        IconGlyph(
            icon = icon,
            contentDescription = title,
            tint = if (active) ChimahonPalette.primary else ChimahonPalette.secondaryText,
            modifier = Modifier.size(24.dp),
        )
        Label(
            title,
            if (active) ChimahonPalette.primary else ChimahonPalette.secondaryText,
            11,
            weight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun MangaDescriptionPanel(
    manga: ChimahonMangaEntry,
    onSetMangaNotes: suspend (Long, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var descriptionExpanded by remember(manga.id) { mutableStateOf(false) }
    var notesEditing by remember(manga.id) { mutableStateOf(false) }
    var notesText by remember(manga.id, manga.notes) { mutableStateOf(manga.notes) }
    var notesSaving by remember(manga.id) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
    ) {
        SectionHeader("About")
        Label(
            text = manga.description?.takeIf { it.isNotBlank() } ?: "No description available.",
            color = ChimahonPalette.onSurface,
            size = 13,
            lineHeight = 19,
            maxLines = if (descriptionExpanded) Int.MAX_VALUE else 7,
            modifier = Modifier.padding(top = 4.dp),
        )
        if (!manga.description.isNullOrBlank()) {
            Label(
                if (descriptionExpanded) "Show less" else "Show more",
                ChimahonPalette.primary,
                11,
                weight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier
                    .clickable { descriptionExpanded = !descriptionExpanded }
                    .padding(vertical = 7.dp),
            )
        }
        if (manga.genres.isNotEmpty()) {
            FilterChips(
                chips = manga.genres,
                selected = "",
                modifier = Modifier.padding(top = 12.dp),
            )
        }
        SectionHeader("Notes")
        if (notesEditing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 92.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ChimahonPalette.surfaceVariant)
                    .padding(12.dp),
            ) {
                if (notesText.isBlank()) {
                    Label("Add private notes", ChimahonPalette.secondaryText, 12)
                }
                BasicTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    textStyle = TextStyle(
                        color = ChimahonPalette.onSurface,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButtonLike("Cancel", onClick = {
                    notesText = manga.notes
                    notesEditing = false
                })
                TextButtonLike(if (notesSaving) "Saving" else "Save", onClick = {
                    if (!notesSaving) {
                        notesSaving = true
                        scope.launch {
                            runCatching { onSetMangaNotes(manga.id, notesText) }
                                .onSuccess { notesEditing = false }
                            notesSaving = false
                        }
                    }
                })
            }
        } else {
            Label(
                text = manga.notes.ifBlank { "No notes yet." },
                color = ChimahonPalette.secondaryText,
                size = 12,
                lineHeight = 18,
                maxLines = 6,
                modifier = Modifier.padding(top = 4.dp),
            )
            Label(
                "Edit notes",
                ChimahonPalette.primary,
                11,
                weight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier
                    .clickable { notesEditing = true }
                    .padding(vertical = 7.dp),
            )
        }
        DetailStatRow("Added", manga.dateAdded.toDateBucket("Day"))
        manga.lastUpdate?.let { DetailStatRow("Updated", it.toDateBucket("Day")) }
        DetailStatRow("Source id", manga.sourceId.toString())
    }
}

@Composable
private fun MangaCategoryPanel(
    categories: List<ChimahonLibraryCategory>,
    selectedCategoryIds: Set<Long>,
    onToggleCategory: suspend (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (categories.isEmpty()) return
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
    ) {
        SectionHeader("Categories")
        LazyRow(
            contentPadding = PaddingValues(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            items(categories, key = { it.id }) { category ->
                val selected = category.id in selectedCategoryIds
                Label(
                    text = category.displayName(),
                    color = if (selected) ChimahonPalette.onPrimaryContainer else ChimahonPalette.secondaryText,
                    size = 11,
                    weight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selected) ChimahonPalette.primaryContainer else ChimahonPalette.surfaceVariant)
                        .clickable {
                            scope.launch {
                                onToggleCategory(category.id)
                            }
                        }
                        .padding(horizontal = 11.dp, vertical = 7.dp),
                )
            }
        }
    }
}

private fun toggledCategoryIds(
    current: Set<Long>,
    categoryId: Long,
): Set<Long> {
    val updated = current.toMutableSet()
    if (!updated.add(categoryId)) {
        updated.remove(categoryId)
    }
    if (categoryId != DEFAULT_LIBRARY_CATEGORY_ID && categoryId in updated) {
        updated.remove(DEFAULT_LIBRARY_CATEGORY_ID)
    }
    if (categoryId == DEFAULT_LIBRARY_CATEGORY_ID && categoryId in updated) {
        updated.removeAll { it != DEFAULT_LIBRARY_CATEGORY_ID }
    }
    if (updated.isEmpty()) {
        updated += DEFAULT_LIBRARY_CATEGORY_ID
    }
    return updated
}

@Composable
private fun ChapterHeader(
    visibleChapterCount: Int,
    allChapterCount: Int,
    filtersVisible: Boolean,
    onToggleFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Label(
            if (visibleChapterCount == allChapterCount) {
                "$allChapterCount chapters"
            } else {
                "$visibleChapterCount of $allChapterCount chapters"
            },
            ChimahonPalette.onSurface,
            16,
            weight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (filtersVisible) ChimahonPalette.primaryContainer else Color.Transparent)
                .clickable(onClick = onToggleFilters),
            contentAlignment = Alignment.Center,
        ) {
            IconGlyph(
                icon = UiIcon.Filter,
                contentDescription = "Chapter filters",
                tint = if (filtersVisible) ChimahonPalette.primary else ChimahonPalette.secondaryText,
                modifier = Modifier.size(19.dp),
            )
        }
    }
}

@Composable
private fun ChapterFilterPanel(
    selectedFilter: ChapterFilter,
    descending: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterChange: (ChapterFilter) -> Unit,
    onSortDirectionChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(bottom = 10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(horizontal = 12.dp, vertical = 2.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ChimahonPalette.background)
                .border(1.dp, ChimahonPalette.divider, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (query.isBlank()) {
                Label("Search chapters", ChimahonPalette.secondaryText, 12, maxLines = 1)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = ChimahonPalette.onSurface,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        FilterChips(
            chips = ChapterFilter.entries.map { it.title },
            selected = selectedFilter.title,
            onSelect = { title ->
                ChapterFilter.entries.firstOrNull { it.title == title }?.let(onFilterChange)
            },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        )
        FilterChips(
            chips = listOf("Newest first", "Oldest first"),
            selected = if (descending) "Newest first" else "Oldest first",
            onSelect = { onSortDirectionChange(it == "Newest first") },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun MangaChapterListItem(
    chapter: ChimahonChapterEntry,
    selected: Boolean,
    onToggleSelected: () -> Unit,
    onClick: () -> Unit,
    onSetRead: suspend (Boolean) -> Unit,
    onSetBookmark: suspend (Boolean) -> Unit,
    onEnqueueDownload: suspend () -> ChimahonDownloadQueueData,
    downloadStatus: ChimahonChapterDownloadStatus? = null,
) {
    var busyAction by remember(chapter.id) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val downloadState = downloadStatus?.status
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!chapter.read) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(ChimahonPalette.primary),
                    )
                }
                if (chapter.bookmarked) {
                    IconGlyph(
                        icon = UiIcon.Bookmark,
                        contentDescription = "Bookmarked",
                        tint = ChimahonPalette.primary,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Label(
                    chapter.name,
                    if (chapter.read) ChimahonPalette.secondaryText else ChimahonPalette.onSurface,
                    13,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
            }
            Label(
                text = buildString {
                    if (chapter.dateUpload > 0L) {
                        append(chapter.dateUpload.toDateBucket("Uploaded"))
                    }
                    if (chapter.lastPageRead > 0L) {
                        if (isNotEmpty()) append("  \u00b7  ")
                        append("Page ${chapter.lastPageRead + 1}")
                    }
                    chapter.scanlator?.takeIf { it.isNotBlank() }?.let {
                        if (isNotEmpty()) append("  \u00b7  ")
                        append(it)
                    }
                },
                color = ChimahonPalette.secondaryText.copy(alpha = if (chapter.read) 0.58f else 1f),
                size = 11,
                maxLines = 1,
            )
            if (downloadStatus != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconGlyph(
                        icon = downloadState?.statusIcon() ?: UiIcon.Download,
                        contentDescription = downloadState?.queueTitle() ?: "Download",
                        tint = downloadState?.statusColor() ?: ChimahonPalette.primary,
                        modifier = Modifier.size(14.dp),
                    )
                    Label(
                        downloadStatus.chapterDownloadStatusLabel(),
                        downloadState?.statusColor() ?: ChimahonPalette.secondaryText,
                        11,
                        maxLines = 1,
                    )
                }
            }
        }
        ChapterQuickAction(
            icon = if (selected) UiIcon.CheckCircle else UiIcon.Circle,
            contentDescription = if (selected) "Deselect chapter" else "Select chapter",
            active = selected,
            onClick = onToggleSelected,
        )
        ChapterQuickAction(
            icon = if (chapter.read) UiIcon.Circle else UiIcon.CheckCircle,
            contentDescription = if (chapter.read) "Mark unread" else "Mark read",
            active = busyAction == "read",
            onClick = {
                if (busyAction == null) {
                    scope.launch {
                        busyAction = "read"
                        runCatching { onSetRead(!chapter.read) }
                        busyAction = null
                    }
                }
            },
        )
        ChapterQuickAction(
            icon = if (chapter.bookmarked) UiIcon.Bookmark else UiIcon.BookmarkBorder,
            contentDescription = if (chapter.bookmarked) "Unbookmark" else "Bookmark",
            active = chapter.bookmarked || busyAction == "bookmark",
            onClick = {
                if (busyAction == null) {
                    scope.launch {
                        busyAction = "bookmark"
                        runCatching { onSetBookmark(!chapter.bookmarked) }
                        busyAction = null
                    }
                }
            },
        )
        ChapterQuickAction(
            icon = downloadState?.statusIcon() ?: UiIcon.Download,
            contentDescription = downloadState?.queueTitle() ?: "Download chapter",
            active = busyAction == "download" ||
                downloadState == ChimahonDownloadState.Queued ||
                downloadState == ChimahonDownloadState.Downloading ||
                downloadState == ChimahonDownloadState.Downloaded,
            onClick = {
                if (busyAction == null && downloadState != ChimahonDownloadState.Downloaded) {
                    scope.launch {
                        busyAction = "download"
                        runCatching { onEnqueueDownload() }
                        busyAction = null
                    }
                }
            },
        )
    }
}

@Composable
private fun ChapterQuickAction(
    icon: UiIcon,
    contentDescription: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        IconGlyph(
            icon = icon,
            contentDescription = contentDescription,
            tint = if (active) ChimahonPalette.primary else ChimahonPalette.secondaryText,
            modifier = Modifier.size(21.dp),
        )
    }
}

@Composable
private fun MoreHome(
    snapshot: ChimahonSnapshot,
    onSelectTab: (HomeTab) -> Unit,
    onCreateCategory: suspend (String) -> Long,
    onDeleteCategory: suspend (Long) -> Unit,
    onCategoriesChanged: () -> Unit,
    onLoadDataMaintenance: suspend () -> ChimahonDataMaintenanceSnapshot,
    onClearThumbnailCache: suspend () -> ChimahonCacheClearResult,
    onClearCacheData: suspend () -> ChimahonCacheClearResult,
    onRunDatabaseMaintenance: suspend () -> ChimahonDatabaseMaintenanceResult,
    onLoadDownloadQueue: suspend () -> ChimahonDownloadQueueData,
    onPauseDownloadQueue: suspend () -> ChimahonDownloadQueueData,
    onResumeDownloadQueue: suspend () -> ChimahonDownloadQueueData,
    onProcessNextDownload: suspend () -> ChimahonDownloadQueueData,
    onPauseDownload: suspend (Long) -> ChimahonDownloadQueueData,
    onResumeDownload: suspend (Long) -> ChimahonDownloadQueueData,
    onRetryDownload: suspend (Long) -> ChimahonDownloadQueueData,
    onRemoveDownload: suspend (Long) -> ChimahonDownloadQueueData,
    onClearCompletedDownloads: suspend () -> ChimahonDownloadQueueData,
    onMoveDownloadToTop: suspend (Long) -> ChimahonDownloadQueueData,
    onMoveDownloadToBottom: suspend (Long) -> ChimahonDownloadQueueData,
    downloadedOnlyMode: Boolean,
    incognitoMode: Boolean,
    settings: ChimahonSettings,
    onAppearanceSettingsChange: (ChimahonAppearanceSettings) -> Unit,
    onLibrarySettingsChange: (ChimahonLibrarySettings) -> Unit,
    onReaderSettingsChange: (ChimahonReaderSettings) -> Unit,
    onDownloadPreferencesChange: (ChimahonDownloadPreferences) -> Unit,
    onBrowseSettingsChange: (ChimahonBrowseSettings) -> Unit,
    onTrackingSettingsChange: (ChimahonTrackingSettings) -> Unit,
    onConnectionSettingsChange: (ChimahonConnectionSettings) -> Unit,
    onDictionarySettingsChange: (ChimahonDictionarySettings) -> Unit,
    onSecuritySettingsChange: (ChimahonSecuritySettings) -> Unit,
    onOpenExternalUrl: (String) -> Boolean,
    onDownloadedOnlyModeChange: (Boolean) -> Unit,
    onIncognitoModeChange: (Boolean) -> Unit,
    onOpenBrowseSection: (BrowseSection) -> Unit,
) {
    var pageStack by remember { mutableStateOf(listOf(MorePage.Main)) }
    val selectedPage = pageStack.last()
    val openPage: (MorePage) -> Unit = { page ->
        if (page != selectedPage) pageStack = pageStack + page
    }
    if (selectedPage != MorePage.Main) {
        MoreDetailPage(
            page = selectedPage,
            snapshot = snapshot,
            onCreateCategory = onCreateCategory,
            onDeleteCategory = onDeleteCategory,
            onCategoriesChanged = onCategoriesChanged,
            onLoadDataMaintenance = onLoadDataMaintenance,
            onClearThumbnailCache = onClearThumbnailCache,
            onClearCacheData = onClearCacheData,
            onRunDatabaseMaintenance = onRunDatabaseMaintenance,
            onLoadDownloadQueue = onLoadDownloadQueue,
            onPauseDownloadQueue = onPauseDownloadQueue,
            onResumeDownloadQueue = onResumeDownloadQueue,
            onProcessNextDownload = onProcessNextDownload,
            onPauseDownload = onPauseDownload,
            onResumeDownload = onResumeDownload,
            onRetryDownload = onRetryDownload,
            onRemoveDownload = onRemoveDownload,
            onClearCompletedDownloads = onClearCompletedDownloads,
            onMoveDownloadToTop = onMoveDownloadToTop,
            onMoveDownloadToBottom = onMoveDownloadToBottom,
            downloadedOnlyMode = downloadedOnlyMode,
            incognitoMode = incognitoMode,
            settings = settings,
            onAppearanceSettingsChange = onAppearanceSettingsChange,
            onLibrarySettingsChange = onLibrarySettingsChange,
            onReaderSettingsChange = onReaderSettingsChange,
            onDownloadPreferencesChange = onDownloadPreferencesChange,
            onBrowseSettingsChange = onBrowseSettingsChange,
            onTrackingSettingsChange = onTrackingSettingsChange,
            onConnectionSettingsChange = onConnectionSettingsChange,
            onDictionarySettingsChange = onDictionarySettingsChange,
            onSecuritySettingsChange = onSecuritySettingsChange,
            onOpenExternalUrl = onOpenExternalUrl,
            onDownloadedOnlyModeChange = onDownloadedOnlyModeChange,
            onIncognitoModeChange = onIncognitoModeChange,
            onOpenPage = openPage,
            onBack = {
                pageStack = if (pageStack.size > 1) pageStack.dropLast(1) else listOf(MorePage.Main)
            },
        )
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        item {
            MoreLogoHeader(snapshot)
        }
        item {
            PreferenceSwitchRow(
                title = "Downloaded only",
                subtitle = "Show manga saved for offline reading",
                icon = UiIcon.Download,
                checked = downloadedOnlyMode,
                onCheckedChange = onDownloadedOnlyModeChange,
            )
        }
        item {
            PreferenceSwitchRow(
                title = "Incognito mode",
                subtitle = "Pause reading history",
                icon = UiIcon.Incognito,
                checked = incognitoMode,
                onCheckedChange = onIncognitoModeChange,
            )
        }
        item { PreferenceDivider() }
        item {
            PreferenceRow(
                "Download queue",
                "No pending downloads",
                UiIcon.Download,
                onClick = { openPage(MorePage.Downloads) },
            )
        }
        item {
            PreferenceRow(
                "Categories",
                "${snapshot.libraryCategories.count { !it.hidden }} visible categories",
                UiIcon.Tag,
                onClick = { openPage(MorePage.Categories) },
            )
        }
        item {
            PreferenceRow(
                "Statistics",
                "${snapshot.summary.mangaCount} manga in shared database",
                UiIcon.Statistics,
                onClick = { openPage(MorePage.Statistics) },
            )
        }
        if (snapshot.updateIssues.isNotEmpty()) {
            item {
                PreferenceRow(
                    title = "Library update errors",
                    subtitle = "${snapshot.updateIssues.size} unresolved issue(s)",
                    icon = UiIcon.Updates,
                    onClick = { onSelectTab(HomeTab.Updates) },
                )
            }
        }
        item {
            PreferenceRow(
                "Data and storage",
                snapshot.runtime.filesDir,
                UiIcon.Storage,
                onClick = { openPage(MorePage.Storage) },
            )
        }
        item {
            PreferenceRow(
                title = "Sources",
                subtitle = "${snapshot.summary.sourceCount} installed",
                icon = UiIcon.Browse,
                onClick = { onOpenBrowseSection(BrowseSection.Sources) },
            )
        }
        item {
            PreferenceRow(
                "Extension repositories",
                "${snapshot.extensionRepos.size} configured",
                UiIcon.Extensions,
                onClick = { onOpenBrowseSection(BrowseSection.Extensions) },
            )
        }
        item { PreferenceDivider() }
        item {
            PreferenceRow(
                "Settings",
                "Reader, downloads, tracking, appearance",
                UiIcon.Settings,
                onClick = { openPage(MorePage.Settings) },
            )
        }
        item {
            PreferenceRow(
                "About",
                "Chimahon ${snapshot.runtime.platformName}",
                UiIcon.Info,
                onClick = { openPage(MorePage.About) },
            )
        }
        item {
            PreferenceRow(
                "Help",
                "Reader and extension help",
                UiIcon.Help,
                onClick = { openPage(MorePage.Help) },
            )
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    modifier = Modifier
                        .border(2.dp, ChimahonPalette.primary, RoundedCornerShape(6.dp))
                        .clickable { onOpenExternalUrl(CHIMAHON_PROJECT_URL) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconGlyph(
                        icon = UiIcon.Star,
                        contentDescription = "Star Chimahon",
                        tint = ChimahonPalette.primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Label(
                        "Star me",
                        ChimahonPalette.primary,
                        12,
                        weight = FontWeight.SemiBold,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 6.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun MoreDetailPage(
    page: MorePage,
    snapshot: ChimahonSnapshot,
    onCreateCategory: suspend (String) -> Long,
    onDeleteCategory: suspend (Long) -> Unit,
    onCategoriesChanged: () -> Unit,
    onLoadDataMaintenance: suspend () -> ChimahonDataMaintenanceSnapshot,
    onClearThumbnailCache: suspend () -> ChimahonCacheClearResult,
    onClearCacheData: suspend () -> ChimahonCacheClearResult,
    onRunDatabaseMaintenance: suspend () -> ChimahonDatabaseMaintenanceResult,
    onLoadDownloadQueue: suspend () -> ChimahonDownloadQueueData,
    onPauseDownloadQueue: suspend () -> ChimahonDownloadQueueData,
    onResumeDownloadQueue: suspend () -> ChimahonDownloadQueueData,
    onProcessNextDownload: suspend () -> ChimahonDownloadQueueData,
    onPauseDownload: suspend (Long) -> ChimahonDownloadQueueData,
    onResumeDownload: suspend (Long) -> ChimahonDownloadQueueData,
    onRetryDownload: suspend (Long) -> ChimahonDownloadQueueData,
    onRemoveDownload: suspend (Long) -> ChimahonDownloadQueueData,
    onClearCompletedDownloads: suspend () -> ChimahonDownloadQueueData,
    onMoveDownloadToTop: suspend (Long) -> ChimahonDownloadQueueData,
    onMoveDownloadToBottom: suspend (Long) -> ChimahonDownloadQueueData,
    downloadedOnlyMode: Boolean,
    incognitoMode: Boolean,
    settings: ChimahonSettings,
    onAppearanceSettingsChange: (ChimahonAppearanceSettings) -> Unit,
    onLibrarySettingsChange: (ChimahonLibrarySettings) -> Unit,
    onReaderSettingsChange: (ChimahonReaderSettings) -> Unit,
    onDownloadPreferencesChange: (ChimahonDownloadPreferences) -> Unit,
    onBrowseSettingsChange: (ChimahonBrowseSettings) -> Unit,
    onTrackingSettingsChange: (ChimahonTrackingSettings) -> Unit,
    onConnectionSettingsChange: (ChimahonConnectionSettings) -> Unit,
    onDictionarySettingsChange: (ChimahonDictionarySettings) -> Unit,
    onSecuritySettingsChange: (ChimahonSecuritySettings) -> Unit,
    onOpenExternalUrl: (String) -> Boolean,
    onDownloadedOnlyModeChange: (Boolean) -> Unit,
    onIncognitoModeChange: (Boolean) -> Unit,
    onOpenPage: (MorePage) -> Unit,
    onBack: () -> Unit,
) {
    var categoryName by remember { mutableStateOf("") }
    var categoryMessage by remember { mutableStateOf<String?>(null) }
    var categoryBusy by remember { mutableStateOf(false) }
    var downloadQueue by remember { mutableStateOf<ChimahonDownloadQueueData?>(null) }
    var downloadQueueMessage by remember { mutableStateOf<String?>(null) }
    var downloadAutoRun by remember { mutableStateOf(true) }
    var downloadProcessorRunning by remember { mutableStateOf(false) }
    var maintenanceSnapshot by remember { mutableStateOf<ChimahonDataMaintenanceSnapshot?>(null) }
    var maintenanceMessage by remember { mutableStateOf<String?>(null) }
    var maintenanceBusy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(page) {
        if (page == MorePage.Downloads) {
            downloadQueue = runCatching { onLoadDownloadQueue() }.getOrNull()
        }
        if (page == MorePage.Storage) {
            maintenanceSnapshot = runCatching { onLoadDataMaintenance() }
                .onFailure { maintenanceMessage = it.message ?: "Could not load storage data" }
                .getOrNull()
        }
    }
    val downloadQueueSignature = downloadQueue?.entries
        ?.joinToString("|") { entry -> "${entry.id}:${entry.status}:${entry.progress}" }
    LaunchedEffect(
        page,
        downloadAutoRun,
        downloadQueue?.paused,
        downloadQueue?.pendingCount,
        downloadQueue?.activeCount,
        downloadQueueSignature,
    ) {
        val queue = downloadQueue
        if (
            page == MorePage.Downloads &&
            downloadAutoRun &&
            queue != null &&
            !queue.paused &&
            queue.pendingCount > 0 &&
            queue.activeCount == 0
        ) {
            downloadProcessorRunning = true
            downloadQueueMessage = "Downloading next chapter..."
            val result = runCatching { onProcessNextDownload() }
            downloadQueue = result.getOrElse { error ->
                downloadQueueMessage = error.message ?: "Could not process next download"
                runCatching { onLoadDownloadQueue() }.getOrNull() ?: queue
            }
            result.getOrNull()?.let { updated ->
                downloadQueueMessage = when {
                    updated.pendingCount > 0 -> "Downloaded one chapter. ${updated.pendingCount} waiting."
                    updated.failedCount > 0 -> "Queue finished with ${updated.failedCount} failed item(s)."
                    else -> "Download queue finished."
                }
            }
            downloadProcessorRunning = false
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .background(ChimahonPalette.surface)
                .border(1.dp, ChimahonPalette.divider)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TopAction(UiIcon.Back, "Back", onClick = onBack)
            Label(page.title, ChimahonPalette.onSurface, 17, weight = FontWeight.SemiBold, maxLines = 1)
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 20.dp),
        ) {
            when (page) {
                MorePage.Downloads -> {
                    val queue = downloadQueue
                    item {
                        MoreDownloadSummary(
                            downloadsDirectory = snapshot.runtime.downloadsDir,
                            downloadedOnlyMode = downloadedOnlyMode,
                            onOpenSettings = { onOpenPage(MorePage.DownloadSettings) },
                        )
                    }
                    item {
                        DownloadQueueToolbar(
                            queue = queue,
                            message = downloadQueueMessage,
                            processing = downloadProcessorRunning,
                            autoRun = downloadAutoRun,
                            onAutoRunChange = { downloadAutoRun = it },
                            onTogglePaused = {
                                scope.launch {
                                    downloadQueue = if (downloadQueue?.paused == true) {
                                        runCatching { onResumeDownloadQueue() }
                                            .onFailure { downloadQueueMessage = it.message ?: "Could not resume queue" }
                                            .getOrNull()
                                    } else {
                                        runCatching { onPauseDownloadQueue() }
                                            .onFailure { downloadQueueMessage = it.message ?: "Could not pause queue" }
                                            .getOrNull()
                                    } ?: downloadQueue
                                }
                            },
                            onProcessNext = {
                                if (!downloadProcessorRunning) {
                                    scope.launch {
                                        val current = downloadQueue
                                        downloadProcessorRunning = true
                                        downloadQueueMessage = "Downloading next chapter..."
                                        val result = runCatching { onProcessNextDownload() }
                                        downloadQueue = result.getOrElse { error ->
                                            downloadQueueMessage = error.message ?: "Could not process next download"
                                            runCatching { onLoadDownloadQueue() }.getOrNull() ?: current
                                        }
                                        result.getOrNull()?.let { updated ->
                                            downloadQueueMessage = when {
                                                updated.pendingCount > 0 -> "Downloaded one chapter. ${updated.pendingCount} waiting."
                                                updated.failedCount > 0 -> "Queue finished with ${updated.failedCount} failed item(s)."
                                                else -> "Download queue finished."
                                            }
                                        }
                                        downloadProcessorRunning = false
                                    }
                                }
                            },
                            onClearCompleted = {
                                scope.launch {
                                    downloadQueue = runCatching { onClearCompletedDownloads() }
                                        .onSuccess { downloadQueueMessage = "Cleared completed downloads" }
                                        .onFailure { downloadQueueMessage = it.message ?: "Could not clear completed downloads" }
                                        .getOrNull() ?: downloadQueue
                                }
                            },
                        )
                    }
                    if (queue == null) {
                        item {
                            ExtensionStatusRow(
                                title = "Loading download queue",
                                subtitle = "Reading saved queue state.",
                            )
                        }
                    } else if (queue.entries.isEmpty()) {
                        item {
                            EmptyMobileState(
                                marker = "D",
                                icon = UiIcon.Download,
                                title = "Download queue is empty",
                                detail = "New chapter downloads will appear here with pause, resume, reorder, and cancel controls.",
                            )
                        }
                    } else {
                        items(queue.entries, key = { it.id }) { entry ->
                            DownloadQueueEntryRow(
                                entry = entry,
                                onPrimaryAction = {
                                    scope.launch {
                                        downloadQueue = runCatching {
                                            when (entry.status) {
                                                ChimahonDownloadState.Paused -> onResumeDownload(entry.chapterId)
                                                ChimahonDownloadState.Error -> onRetryDownload(entry.chapterId)
                                                ChimahonDownloadState.Downloaded -> onRemoveDownload(entry.chapterId)
                                                ChimahonDownloadState.Queued,
                                                ChimahonDownloadState.Downloading,
                                                -> onPauseDownload(entry.chapterId)
                                            }
                                        }
                                            .onFailure { downloadQueueMessage = it.message ?: "Could not update download" }
                                            .getOrNull() ?: downloadQueue
                                    }
                                },
                                onMoveTop = {
                                    scope.launch {
                                        downloadQueue = runCatching { onMoveDownloadToTop(entry.chapterId) }
                                            .onFailure { downloadQueueMessage = it.message ?: "Could not move download" }
                                            .getOrNull() ?: downloadQueue
                                    }
                                },
                                onMoveBottom = {
                                    scope.launch {
                                        downloadQueue = runCatching { onMoveDownloadToBottom(entry.chapterId) }
                                            .onFailure { downloadQueueMessage = it.message ?: "Could not move download" }
                                            .getOrNull() ?: downloadQueue
                                    }
                                },
                                onRemove = {
                                    scope.launch {
                                        downloadQueue = runCatching { onRemoveDownload(entry.chapterId) }
                                            .onFailure { downloadQueueMessage = it.message ?: "Could not remove download" }
                                            .getOrNull() ?: downloadQueue
                                    }
                                },
                            )
                        }
                    }
                }
                MorePage.Categories -> {
                    item { ListGroupHeader("Library categories") }
                    item {
                        CategoryInputRow(
                            name = categoryName,
                            message = categoryMessage,
                            busy = categoryBusy,
                            onNameChange = {
                                categoryName = it
                                categoryMessage = null
                            },
                            onCreate = {
                                if (categoryName.isNotBlank() && !categoryBusy) {
                                    categoryBusy = true
                                    scope.launch {
                                        runCatching { onCreateCategory(categoryName) }
                                            .onSuccess {
                                                categoryMessage = "Category created."
                                                categoryName = ""
                                                onCategoriesChanged()
                                            }
                                            .onFailure { error ->
                                                categoryMessage = error.message ?: "Could not create category."
                                            }
                                        categoryBusy = false
                                    }
                                }
                            },
                        )
                    }
                    val categories = snapshot.libraryCategories
                        .filterNot { it.hidden }
                        .sortedBy { it.order }
                    if (categories.isEmpty()) {
                        item {
                            ExtensionStatusRow(
                                title = "Default",
                                subtitle = "${snapshot.library.size} manga",
                            )
                        }
                    } else {
                        items(categories, key = { it.id }) { category ->
                            ExtensionStatusRow(
                                title = category.displayName(),
                                subtitle = "${snapshot.libraryForCategory(category.id).size} manga",
                                action = "Delete".takeIf { category.id > DEFAULT_LIBRARY_CATEGORY_ID },
                                onAction = {
                                    if (!categoryBusy) {
                                        categoryBusy = true
                                        scope.launch {
                                            runCatching { onDeleteCategory(category.id) }
                                                .onSuccess {
                                                    categoryMessage = "Deleted ${category.displayName()}."
                                                    onCategoriesChanged()
                                                }
                                                .onFailure { error ->
                                                    categoryMessage = error.message ?: "Could not delete category."
                                                }
                                            categoryBusy = false
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
                MorePage.Statistics -> {
                    val allChapters = snapshot.library.flatMap { manga ->
                        snapshot.chaptersByMangaId[manga.id].orEmpty()
                    }
                    val readDuration = snapshot.history.sumOf { it.readDuration }
                    item {
                        MoreStatisticsHero(
                            readDuration = readDuration,
                            readChapters = allChapters.count { it.read },
                            totalChapters = allChapters.size,
                        )
                    }
                    item { ListGroupHeader("Library") }
                    item {
                        MoreStatisticRow(
                            "Manga",
                            snapshot.library.size.toString(),
                            UiIcon.Library,
                        )
                    }
                    item {
                        MoreStatisticRow(
                            "Read chapters",
                            allChapters.count { it.read }.toString(),
                            UiIcon.DoneAll,
                        )
                    }
                    item {
                        MoreStatisticRow(
                            "Unread chapters",
                            allChapters.count { !it.read }.toString(),
                            UiIcon.Updates,
                        )
                    }
                    item {
                        MoreStatisticRow(
                            "Bookmarked chapters",
                            allChapters.count { it.bookmarked }.toString(),
                            UiIcon.Bookmark,
                        )
                    }
                    item {
                        MoreStatisticRow(
                            "History entries",
                            snapshot.history.size.toString(),
                            UiIcon.History,
                        )
                    }
                    item {
                        MoreStatisticRow(
                            "Categories",
                            snapshot.libraryCategories.count { !it.hidden }.toString(),
                            UiIcon.Tag,
                        )
                    }
                    item { ListGroupHeader("Sources") }
                    item { MoreStatisticRow("Sources", snapshot.summary.sourceCount.toString(), UiIcon.Browse) }
                    item {
                        MoreStatisticRow(
                            "Extensions",
                            snapshot.summary.extensionCount.toString(),
                            UiIcon.Extensions,
                        )
                    }
                }
                MorePage.Storage -> {
                    val maintenance = maintenanceSnapshot
                    item { ListGroupHeader("Runtime") }
                    item { RuntimeLineRow("Platform", snapshot.runtime.platformName) }
                    item { RuntimeLineRow("Database", snapshot.runtime.databaseState) }
                    item { RuntimeLineRow("Extensions", snapshot.runtime.extensionState) }
                    item { RuntimeLineRow("Background work", snapshot.runtime.backgroundState) }
                    item { RuntimeLineRow("Files", snapshot.runtime.filesDir) }
                    item { RuntimeLineRow("Cache", snapshot.runtime.cacheDir) }
                    item { RuntimeLineRow("Downloads", snapshot.runtime.downloadsDir) }
                    item { ListGroupHeader("Storage usage") }
                    if (maintenance == null) {
                        item {
                            ExtensionStatusRow(
                                title = "Loading storage data",
                                subtitle = "Scanning shared files, cache, and downloads.",
                            )
                        }
                    } else {
                        item {
                            StorageUsageHero(storage = maintenance.storage)
                        }
                        item {
                            StorageSectionRow("Files", maintenance.storage.files)
                        }
                        item {
                            StorageSectionRow("Cache", maintenance.storage.cache)
                        }
                        item {
                            StorageSectionRow("Downloads", maintenance.storage.downloads)
                        }
                        item { ListGroupHeader("Maintenance") }
                        item {
                            ExtensionStatusRow(
                                title = "Thumbnail cache",
                                subtitle = "${maintenance.thumbnailCache.entryCount} cached image(s), ${maintenance.thumbnailCache.sizeBytes.toReadableBytes()} in memory",
                                action = if (maintenanceBusy) "Working" else "Clear",
                                onAction = {
                                    if (!maintenanceBusy) {
                                        maintenanceBusy = true
                                        scope.launch {
                                            runCatching { onClearThumbnailCache() }
                                                .onSuccess { maintenanceMessage = it.cacheClearSummary() }
                                                .onFailure { maintenanceMessage = it.message ?: "Could not clear thumbnail cache" }
                                            maintenanceSnapshot = runCatching { onLoadDataMaintenance() }
                                                .getOrNull() ?: maintenanceSnapshot
                                            maintenanceBusy = false
                                        }
                                    }
                                },
                            )
                        }
                        item {
                            ExtensionStatusRow(
                                title = "Application cache",
                                subtitle = "${maintenance.storage.cache.sizeBytes.toReadableBytes()} across ${maintenance.storage.cache.fileCount} file(s)",
                                action = if (maintenanceBusy) "Working" else "Clear",
                                onAction = {
                                    if (!maintenanceBusy) {
                                        maintenanceBusy = true
                                        scope.launch {
                                            runCatching { onClearCacheData() }
                                                .onSuccess { maintenanceMessage = it.cacheClearSummary() }
                                                .onFailure { maintenanceMessage = it.message ?: "Could not clear app cache" }
                                            maintenanceSnapshot = runCatching { onLoadDataMaintenance() }
                                                .getOrNull() ?: maintenanceSnapshot
                                            maintenanceBusy = false
                                        }
                                    }
                                },
                            )
                        }
                        item {
                            ExtensionStatusRow(
                                title = "Database maintenance",
                                subtitle = "${maintenance.database.historyEntryCount} history record(s), ${maintenance.database.updateIssues.totalIssueCount} update issue(s)",
                                action = if (maintenanceBusy) "Working" else "Run",
                                onAction = {
                                    if (!maintenanceBusy) {
                                        maintenanceBusy = true
                                        scope.launch {
                                            runCatching { onRunDatabaseMaintenance() }
                                                .onSuccess { maintenanceMessage = it.databaseMaintenanceSummary() }
                                                .onFailure { maintenanceMessage = it.message ?: "Could not run maintenance" }
                                            maintenanceSnapshot = runCatching { onLoadDataMaintenance() }
                                                .getOrNull() ?: maintenanceSnapshot
                                            maintenanceBusy = false
                                        }
                                    }
                                },
                            )
                        }
                        maintenanceMessage?.let { message ->
                            item {
                                ExtensionStatusRow(
                                    title = "Maintenance result",
                                    subtitle = message,
                                    error = message.startsWith("Could not"),
                                )
                            }
                        }
                    }
                    item { ListGroupHeader("Database content") }
                    item { RuntimeLineRow("Manga records", snapshot.mangaDetails.size.toString()) }
                    item {
                        RuntimeLineRow(
                            "Chapter records",
                            snapshot.chaptersByMangaId.values.sumOf { it.size }.toString(),
                        )
                    }
                    item { RuntimeLineRow("History records", snapshot.history.size.toString()) }
                }
                MorePage.Settings -> {
                    item {
                        PreferenceRow(
                            "Appearance",
                            "Theme, dark mode, navigation, and dates",
                            UiIcon.Settings,
                            onClick = { onOpenPage(MorePage.AppearanceSettings) },
                        )
                    }
                    item {
                        PreferenceRow(
                            "Library",
                            "Categories, badges, continue buttons, display mode",
                            UiIcon.Library,
                            onClick = { onOpenPage(MorePage.LibrarySettings) },
                        )
                    }
                    item {
                        PreferenceRow(
                            "Reader",
                            "Reading mode, scale, background, page controls",
                            UiIcon.Chapters,
                            onClick = { onOpenPage(MorePage.ReaderSettings) },
                        )
                    }
                    item {
                        PreferenceRow(
                            "Downloads",
                            "Offline library and download location",
                            UiIcon.Download,
                            onClick = { onOpenPage(MorePage.DownloadSettings) },
                        )
                    }
                    item {
                        PreferenceRow(
                            "Tracking",
                            "Reading progress and account integrations",
                            UiIcon.History,
                            onClick = { onOpenPage(MorePage.TrackingSettings) },
                        )
                    }
                    item {
                        PreferenceRow(
                            "Connections",
                            "External services and account links",
                            UiIcon.Web,
                            onClick = { onOpenPage(MorePage.ConnectionsSettings) },
                        )
                    }
                    item {
                        PreferenceRow(
                            "Browse",
                            "Sources, extensions, repositories, and feeds",
                            UiIcon.Browse,
                            onClick = { onOpenPage(MorePage.BrowseSettings) },
                        )
                    }
                    item {
                        PreferenceRow(
                            "Dictionary",
                            "Reader dictionaries and language tools",
                            UiIcon.Chapters,
                            onClick = { onOpenPage(MorePage.DictionarySettings) },
                        )
                    }
                    item {
                        PreferenceRow(
                            "Data and storage",
                            "Database and platform directories",
                            UiIcon.Storage,
                            onClick = { onOpenPage(MorePage.Storage) },
                        )
                    }
                    item {
                        PreferenceRow(
                            "Security",
                            "Privacy and protected access",
                            UiIcon.Incognito,
                            onClick = { onOpenPage(MorePage.SecuritySettings) },
                        )
                    }
                    item {
                        PreferenceRow(
                            "Advanced",
                            "Incognito mode and runtime diagnostics",
                            UiIcon.Settings,
                            onClick = { onOpenPage(MorePage.AdvancedSettings) },
                        )
                    }
                    item {
                        PreferenceRow(
                            "About",
                            "Chimahon ${snapshot.runtime.platformName}",
                            UiIcon.Info,
                            onClick = { onOpenPage(MorePage.About) },
                        )
                    }
                }
                MorePage.AppearanceSettings -> {
                    item { ListGroupHeader("Theme") }
                    item {
                        SettingsChoiceRow(
                            title = "Theme mode",
                            options = ChimahonThemeMode.entries.map { it.name },
                            selected = settings.appearance.themeMode.name,
                            onSelect = { selected ->
                                ChimahonThemeMode.entries.firstOrNull { it.name == selected }?.let {
                                    onAppearanceSettingsChange(settings.appearance.copy(themeMode = it))
                                }
                            },
                        )
                    }
                    item {
                        ThemePickerRow(
                            selected = settings.appearance.appTheme,
                            dark = settings.appearance.themeMode != ChimahonThemeMode.Light,
                            onSelect = {
                                onAppearanceSettingsChange(settings.appearance.copy(appTheme = it))
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Color theme",
                            options = ChimahonColorTheme.entries.map { it.title },
                            selected = settings.appearance.colorTheme.title,
                            onSelect = { selected ->
                                ChimahonColorTheme.entries.firstOrNull { it.title == selected }?.let {
                                    onAppearanceSettingsChange(settings.appearance.copy(colorTheme = it))
                                }
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "App icon",
                            options = ChimahonAppIcon.entries.map { it.title },
                            selected = settings.appearance.appIcon.title,
                            onSelect = { selected ->
                                ChimahonAppIcon.entries.firstOrNull { it.title == selected }?.let {
                                    onAppearanceSettingsChange(settings.appearance.copy(appIcon = it))
                                }
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Pure black dark mode",
                            "Use an AMOLED black background while dark mode is active",
                            UiIcon.Circle,
                            checked = settings.appearance.amoled,
                            onCheckedChange = {
                                onAppearanceSettingsChange(settings.appearance.copy(amoled = it))
                            },
                        )
                    }
                    item { ListGroupHeader("Display") }
                    item {
                        SettingsChoiceRow(
                            title = "Font scale",
                            options = listOf("85%", "90%", "95%", "100%", "105%", "110%", "115%", "120%"),
                            selected = "${settings.appearance.fontScalePercent}%",
                            onSelect = { selected ->
                                selected.removeSuffix("%").toIntOrNull()?.let {
                                    onAppearanceSettingsChange(
                                        settings.appearance.copy(fontScalePercent = it),
                                    )
                                }
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Compact navigation",
                            "Reduce navigation rail and toolbar spacing",
                            UiIcon.Reorder,
                            checked = settings.appearance.compactNavigation,
                            onCheckedChange = {
                                onAppearanceSettingsChange(
                                    settings.appearance.copy(compactNavigation = it),
                                )
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Relative dates",
                            "Use today and yesterday where supported",
                            UiIcon.History,
                            checked = settings.appearance.relativeDates,
                            onCheckedChange = {
                                onAppearanceSettingsChange(settings.appearance.copy(relativeDates = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Images in descriptions",
                            "Show source-provided images in manga descriptions",
                            UiIcon.Web,
                            checked = settings.appearance.showDescriptionImages,
                            onCheckedChange = {
                                onAppearanceSettingsChange(
                                    settings.appearance.copy(showDescriptionImages = it),
                                )
                            },
                        )
                    }
                }
                MorePage.LibrarySettings -> {
                    item { ListGroupHeader("Display") }
                    item {
                        SettingsChoiceRow(
                            title = "Default display mode",
                            options = LibraryDisplayMode.entries.map { it.title },
                            selected = settings.library.displayMode.toUiLibraryDisplayMode().title,
                            onSelect = { title ->
                                val displayMode = LibraryDisplayMode.entries
                                    .firstOrNull { it.title == title }
                                    ?: LibraryDisplayMode.ComfortableGrid
                                onLibrarySettingsChange(
                                    settings.library.copy(
                                        displayMode = displayMode.toSettingsLibraryDisplayMode(),
                                    ),
                                )
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Grid columns portrait",
                            options = listOf("Automatic", "2", "3", "4", "5", "6"),
                            selected = settings.library.gridColumnsPortrait.toColumnTitle(),
                            onSelect = { selected ->
                                onLibrarySettingsChange(
                                    settings.library.copy(gridColumnsPortrait = selected.toColumnCount()),
                                )
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Grid columns landscape",
                            options = listOf("Automatic", "3", "4", "5", "6", "7", "8"),
                            selected = settings.library.gridColumnsLandscape.toColumnTitle(),
                            onSelect = { selected ->
                                onLibrarySettingsChange(
                                    settings.library.copy(gridColumnsLandscape = selected.toColumnCount()),
                                )
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Cover ratio",
                            options = ChimahonLibraryCoverRatio.entries.map { it.libraryTitle() },
                            selected = settings.library.coverAspectRatio.libraryTitle(),
                            onSelect = { selected ->
                                ChimahonLibraryCoverRatio.entries
                                    .firstOrNull { it.libraryTitle() == selected }
                                    ?.let { onLibrarySettingsChange(settings.library.copy(coverAspectRatio = it)) }
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Show category tabs",
                            "Keep library categories above the grid",
                            UiIcon.Tag,
                            checked = settings.library.showCategoryTabs,
                            onCheckedChange = {
                                onLibrarySettingsChange(settings.library.copy(showCategoryTabs = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Show unread badges",
                            "Display unread chapter counts on covers",
                            UiIcon.Updates,
                            checked = settings.library.showUnreadBadges,
                            onCheckedChange = {
                                onLibrarySettingsChange(settings.library.copy(showUnreadBadges = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Show downloaded badges",
                            "Display offline availability on library covers",
                            UiIcon.Download,
                            checked = settings.library.showDownloadedBadges,
                            onCheckedChange = {
                                onLibrarySettingsChange(settings.library.copy(showDownloadedBadges = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Show language badges",
                            "Display source language on library entries",
                            UiIcon.Web,
                            checked = settings.library.showLanguageBadges,
                            onCheckedChange = {
                                onLibrarySettingsChange(settings.library.copy(showLanguageBadges = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Show continue buttons",
                            "Resume the next readable chapter from library items",
                            UiIcon.Play,
                            checked = settings.library.showContinueButtons,
                            onCheckedChange = {
                                onLibrarySettingsChange(settings.library.copy(showContinueButtons = it))
                            },
                        )
                    }
                    item { ListGroupHeader("Filter and sort") }
                    item {
                        SettingsChoiceRow(
                            title = "Default sort",
                            options = ChimahonLibrarySort.entries.map { it.libraryTitle() },
                            selected = settings.library.sort.libraryTitle(),
                            onSelect = { selected ->
                                ChimahonLibrarySort.entries
                                    .firstOrNull { it.libraryTitle() == selected }
                                    ?.let { onLibrarySettingsChange(settings.library.copy(sort = it)) }
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Ascending sort",
                            "Reverse the selected default sort when disabled",
                            UiIcon.Swap,
                            checked = settings.library.sortAscending,
                            onCheckedChange = {
                                onLibrarySettingsChange(settings.library.copy(sortAscending = it))
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Downloaded filter",
                            options = ChimahonFilterMode.entries.map { it.filterTitle() },
                            selected = settings.library.downloadedFilter.filterTitle(),
                            onSelect = { selected ->
                                ChimahonFilterMode.entries
                                    .firstOrNull { it.filterTitle() == selected }
                                    ?.let { onLibrarySettingsChange(settings.library.copy(downloadedFilter = it)) }
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Unread filter",
                            options = ChimahonFilterMode.entries.map { it.filterTitle() },
                            selected = settings.library.unreadFilter.filterTitle(),
                            onSelect = { selected ->
                                ChimahonFilterMode.entries
                                    .firstOrNull { it.filterTitle() == selected }
                                    ?.let { onLibrarySettingsChange(settings.library.copy(unreadFilter = it)) }
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Started filter",
                            options = ChimahonFilterMode.entries.map { it.filterTitle() },
                            selected = settings.library.startedFilter.filterTitle(),
                            onSelect = { selected ->
                                ChimahonFilterMode.entries
                                    .firstOrNull { it.filterTitle() == selected }
                                    ?.let { onLibrarySettingsChange(settings.library.copy(startedFilter = it)) }
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Bookmarked filter",
                            options = ChimahonFilterMode.entries.map { it.filterTitle() },
                            selected = settings.library.bookmarkedFilter.filterTitle(),
                            onSelect = { selected ->
                                ChimahonFilterMode.entries
                                    .firstOrNull { it.filterTitle() == selected }
                                    ?.let { onLibrarySettingsChange(settings.library.copy(bookmarkedFilter = it)) }
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Completed filter",
                            options = ChimahonFilterMode.entries.map { it.filterTitle() },
                            selected = settings.library.completedFilter.filterTitle(),
                            onSelect = { selected ->
                                ChimahonFilterMode.entries
                                    .firstOrNull { it.filterTitle() == selected }
                                    ?.let { onLibrarySettingsChange(settings.library.copy(completedFilter = it)) }
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Tracked filter",
                            options = ChimahonFilterMode.entries.map { it.filterTitle() },
                            selected = settings.library.trackedFilter.filterTitle(),
                            onSelect = { selected ->
                                ChimahonFilterMode.entries
                                    .firstOrNull { it.filterTitle() == selected }
                                    ?.let { onLibrarySettingsChange(settings.library.copy(trackedFilter = it)) }
                            },
                        )
                    }
                    item { ListGroupHeader("Global update") }
                    item {
                        SettingsChoiceRow(
                            title = "Update interval",
                            options = listOf("Off", "6 hours", "12 hours", "24 hours", "48 hours"),
                            selected = settings.library.autoUpdateIntervalHours.toTrackingIntervalTitle(),
                            onSelect = { selected ->
                                onLibrarySettingsChange(
                                    settings.library.copy(
                                        autoUpdateIntervalHours = selected.toTrackingIntervalHours(),
                                    ),
                                )
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Update only on Wi-Fi",
                            "Run automatic library updates only on unmetered networks",
                            UiIcon.Web,
                            checked = settings.library.updateOnlyOnWifi,
                            onCheckedChange = {
                                onLibrarySettingsChange(settings.library.copy(updateOnlyOnWifi = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Show update count",
                            "Show pending chapter counts in library and update surfaces",
                            UiIcon.Updates,
                            checked = settings.library.showUpdateCount,
                            onCheckedChange = {
                                onLibrarySettingsChange(settings.library.copy(showUpdateCount = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Update notifications",
                            "Notify when automatic library updates find new chapters",
                            UiIcon.Updates,
                            checked = settings.library.updateNotificationsEnabled,
                            onCheckedChange = {
                                onLibrarySettingsChange(settings.library.copy(updateNotificationsEnabled = it))
                            },
                        )
                    }
                }
                MorePage.ReaderSettings -> {
                    item { ListGroupHeader("Reading") }
                    item {
                        SettingsChoiceRow(
                            title = "Default reading mode",
                            options = ReaderMode.entries.map { it.title },
                            selected = settings.reader.mode.toUiReaderMode().title,
                            onSelect = { title ->
                                ReaderMode.entries.firstOrNull { it.title == title }?.let { mode ->
                                    onReaderSettingsChange(
                                        settings.reader.copy(mode = mode.toSettingsReaderMode()),
                                    )
                                }
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Scale type",
                            options = ReaderScale.entries.map { it.title },
                            selected = settings.reader.scale.toUiReaderScale().title,
                            onSelect = { title ->
                                ReaderScale.entries.firstOrNull { it.title == title }?.let { scale ->
                                    onReaderSettingsChange(
                                        settings.reader.copy(scale = scale.toSettingsReaderScale()),
                                    )
                                }
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Background",
                            options = ReaderCanvas.entries.map { it.title },
                            selected = settings.reader.canvas.toUiReaderCanvas().title,
                            onSelect = { title ->
                                ReaderCanvas.entries.firstOrNull { it.title == title }?.let { canvas ->
                                    onReaderSettingsChange(
                                        settings.reader.copy(canvas = canvas.toSettingsReaderCanvas()),
                                    )
                                }
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Orientation",
                            options = ChimahonReaderOrientation.entries.map { it.readerTitle() },
                            selected = settings.reader.orientation.readerTitle(),
                            onSelect = { title ->
                                ChimahonReaderOrientation.entries
                                    .firstOrNull { it.readerTitle() == title }
                                    ?.let { onReaderSettingsChange(settings.reader.copy(orientation = it)) }
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Dual page",
                            options = ChimahonDualPageMode.entries.map { it.readerTitle() },
                            selected = settings.reader.dualPageMode.readerTitle(),
                            onSelect = { title ->
                                ChimahonDualPageMode.entries
                                    .firstOrNull { it.readerTitle() == title }
                                    ?.let { onReaderSettingsChange(settings.reader.copy(dualPageMode = it)) }
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Navigation mode",
                            options = ChimahonReaderNavigationMode.entries.map { it.readerTitle() },
                            selected = settings.reader.navigationMode.readerTitle(),
                            onSelect = { title ->
                                ChimahonReaderNavigationMode.entries
                                    .firstOrNull { it.readerTitle() == title }
                                    ?.let { onReaderSettingsChange(settings.reader.copy(navigationMode = it)) }
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Split wide pages",
                            "Split landscape manga pages in paged readers",
                            UiIcon.Swap,
                            checked = settings.reader.splitWidePages,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(splitWidePages = it))
                            },
                        )
                    }
                    item { ListGroupHeader("Color filter") }
                    item {
                        PreferenceSwitchRow(
                            "Enable color filter",
                            "Apply reader-level color adjustments",
                            UiIcon.Filter,
                            checked = settings.reader.colorFilterEnabled,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(colorFilterEnabled = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Grayscale",
                            "Render pages without color",
                            UiIcon.VisibilityOff,
                            checked = settings.reader.grayscale,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(grayscale = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Invert colors",
                            "Invert manga page colors while reading",
                            UiIcon.Circle,
                            checked = settings.reader.invertColors,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(invertColors = it))
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Brightness",
                            options = listOf("-50", "-25", "0", "25", "50"),
                            selected = settings.reader.brightness.toString(),
                            onSelect = { selected ->
                                selected.toIntOrNull()?.let {
                                    onReaderSettingsChange(settings.reader.copy(brightness = it))
                                }
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Page scrubber",
                            "Show direct page navigation above reader controls",
                            UiIcon.Reorder,
                            checked = settings.reader.showPageStrip,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(showPageStrip = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Show controls on start",
                            "Open each chapter with reader bars visible",
                            UiIcon.Settings,
                            checked = settings.reader.keepControlsVisible,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(keepControlsVisible = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Show page number",
                            "Display the current page while reader controls are hidden",
                            UiIcon.Info,
                            checked = settings.reader.showPageNumber,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(showPageNumber = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Crop borders",
                            "Fill the reader canvas and trim empty image edges",
                            UiIcon.Filter,
                            checked = settings.reader.cropBorders,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(cropBorders = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Page transitions",
                            "Animate movement between pages",
                            UiIcon.Swap,
                            checked = settings.reader.pageTransitions,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(pageTransitions = it))
                            },
                        )
                    }
                    item { ListGroupHeader("Controls") }
                    item {
                        PreferenceSwitchRow(
                            "Swipe navigation",
                            "Allow swiping and wheel gestures between pages",
                            UiIcon.Swap,
                            checked = settings.reader.swipeNavigationEnabled,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(swipeNavigationEnabled = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Double tap to zoom",
                            "Enable image zoom gestures where supported",
                            UiIcon.Search,
                            checked = settings.reader.doubleTapToZoom,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(doubleTapToZoom = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Tap zones",
                            "Tap page edges to navigate and the center to show controls",
                            UiIcon.Play,
                            checked = settings.reader.tapZonesEnabled,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(tapZonesEnabled = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Smaller tap zones",
                            "Reserve more of the page center for opening reader controls",
                            UiIcon.Reorder,
                            checked = settings.reader.smallerTapZones,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(smallerTapZones = it))
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Invert tap zones",
                            options = ChimahonTapZoneInvert.entries.map { it.readerTitle() },
                            selected = settings.reader.invertTapZones.readerTitle(),
                            onSelect = { title ->
                                ChimahonTapZoneInvert.entries
                                    .firstOrNull { it.readerTitle() == title }
                                    ?.let {
                                        onReaderSettingsChange(settings.reader.copy(invertTapZones = it))
                                    }
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Volume keys",
                            "Use volume buttons to navigate pages on supported platforms",
                            UiIcon.SkipNext,
                            checked = settings.reader.volumeKeysEnabled,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(volumeKeysEnabled = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Invert volume keys",
                            "Swap previous and next page volume-button actions",
                            UiIcon.Swap,
                            checked = settings.reader.volumeKeysInverted,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(volumeKeysInverted = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Long tap controls",
                            "Press and hold the page to show or hide reader controls",
                            UiIcon.Settings,
                            checked = settings.reader.longTapEnabled,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(longTapEnabled = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Keep screen on",
                            "Request an uninterrupted reading session while the reader is open",
                            UiIcon.Incognito,
                            checked = settings.reader.keepScreenOn,
                            onCheckedChange = {
                                onReaderSettingsChange(settings.reader.copy(keepScreenOn = it))
                            },
                        )
                    }
                }
                MorePage.DownloadSettings -> {
                    item { ListGroupHeader("Offline reading") }
                    item {
                        PreferenceSwitchRow(
                            "Downloaded only",
                            "Show manga saved for offline reading",
                            UiIcon.Download,
                            checked = downloadedOnlyMode,
                            onCheckedChange = onDownloadedOnlyModeChange,
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Wi-Fi only",
                            "Start new downloads only on unmetered connections",
                            UiIcon.Web,
                            checked = settings.downloads.wifiOnly,
                            onCheckedChange = {
                                onDownloadPreferencesChange(settings.downloads.copy(wifiOnly = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Save as CBZ",
                            "Package downloaded chapters as comic archives when supported",
                            UiIcon.Download,
                            checked = settings.downloads.saveAsCbz,
                            onCheckedChange = {
                                onDownloadPreferencesChange(settings.downloads.copy(saveAsCbz = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Split tall images",
                            "Split very tall pages for smoother native reading",
                            UiIcon.Swap,
                            checked = settings.downloads.splitTallImages,
                            onCheckedChange = {
                                onDownloadPreferencesChange(settings.downloads.copy(splitTallImages = it))
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Ahead while reading",
                            options = listOf("Off", "1", "2", "3", "5", "10"),
                            selected = settings.downloads.autoDownloadWhileReadingCount.toOffCountTitle(),
                            onSelect = { selected ->
                                onDownloadPreferencesChange(
                                    settings.downloads.copy(
                                        autoDownloadWhileReadingCount = selected.toOffCount(),
                                    ),
                                )
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Source downloads",
                            options = listOf("1", "2", "3", "4", "5", "8"),
                            selected = settings.downloads.parallelSourceDownloads.toString(),
                            onSelect = { selected ->
                                selected.toIntOrNull()?.let {
                                    onDownloadPreferencesChange(
                                        settings.downloads.copy(parallelSourceDownloads = it),
                                    )
                                }
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Page downloads",
                            options = listOf("1", "2", "3", "4", "5", "8"),
                            selected = settings.downloads.parallelPageDownloads.toString(),
                            onSelect = { selected ->
                                selected.toIntOrNull()?.let {
                                    onDownloadPreferencesChange(
                                        settings.downloads.copy(parallelPageDownloads = it),
                                    )
                                }
                            },
                        )
                    }
                    item { ListGroupHeader("Automatic cleanup") }
                    item {
                        SettingsChoiceRow(
                            title = "Remove after read",
                            options = listOf("Never", "After next chapter", "After 2 chapters", "After 5 chapters"),
                            selected = settings.downloads.removeAfterReadSlots.toRemoveAfterReadTitle(),
                            onSelect = { selected ->
                                onDownloadPreferencesChange(
                                    settings.downloads.copy(removeAfterReadSlots = selected.toRemoveAfterReadSlots()),
                                )
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Remove marked read",
                            "Delete downloaded chapters when they are marked read",
                            UiIcon.Delete,
                            checked = settings.downloads.removeAfterMarkedRead,
                            onCheckedChange = {
                                onDownloadPreferencesChange(
                                    settings.downloads.copy(removeAfterMarkedRead = it),
                                )
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Keep bookmarks",
                            "Do not remove downloaded bookmarked chapters",
                            UiIcon.Bookmark,
                            checked = settings.downloads.removeBookmarkedChapters.not(),
                            onCheckedChange = {
                                onDownloadPreferencesChange(
                                    settings.downloads.copy(removeBookmarkedChapters = !it),
                                )
                            },
                        )
                    }
                    item { ListGroupHeader("New chapters") }
                    item {
                        PreferenceSwitchRow(
                            "Download new chapters",
                            "Automatically queue new library chapters",
                            UiIcon.Updates,
                            checked = settings.downloads.downloadNewChapters,
                            onCheckedChange = {
                                onDownloadPreferencesChange(settings.downloads.copy(downloadNewChapters = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Unread only",
                            "Skip new chapters already marked read",
                            UiIcon.DoneAll,
                            checked = settings.downloads.downloadNewUnreadOnly,
                            onCheckedChange = {
                                onDownloadPreferencesChange(settings.downloads.copy(downloadNewUnreadOnly = it))
                            },
                        )
                    }
                    item { RuntimeLineRow("Download directory", snapshot.runtime.downloadsDir) }
                    item {
                        ExtensionStatusRow(
                            title = "Download queue",
                            subtitle = "Queued chapters can be paused, resumed, reordered, or cancelled from More.",
                            action = "Open",
                            onAction = { onOpenPage(MorePage.Downloads) },
                        )
                    }
                }
                MorePage.TrackingSettings -> {
                    item { ListGroupHeader("Reading progress") }
                    item {
                        PreferenceSwitchRow(
                            "Auto-sync tracking",
                            "Queue tracking sync work when reader progress changes",
                            UiIcon.Refresh,
                            checked = settings.tracking.autoSyncEnabled,
                            onCheckedChange = {
                                onTrackingSettingsChange(settings.tracking.copy(autoSyncEnabled = it))
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Update interval",
                            options = listOf("Off", "6 hours", "12 hours", "24 hours", "48 hours"),
                            selected = settings.tracking.updateIntervalHours.toTrackingIntervalTitle(),
                            onSelect = { selected ->
                                onTrackingSettingsChange(
                                    settings.tracking.copy(updateIntervalHours = selected.toTrackingIntervalHours()),
                                )
                            },
                        )
                    }
                    item { ListGroupHeader("Services") }
                    item {
                        ExtensionStatusRow(
                            title = if (incognitoMode) "Tracking paused" else "Local tracking data",
                            subtitle = if (incognitoMode) {
                                "Incognito mode is enabled, so reader progress is not written."
                            } else {
                                "${snapshot.history.size} history item(s) and ${snapshot.chaptersByMangaId.values.sumOf { chapters -> chapters.count { it.read } }} read chapter(s) in the shared database."
                            },
                        )
                    }
                }
                MorePage.ConnectionsSettings -> {
                    item { ListGroupHeader("External services") }
                    item {
                        SettingsChoiceRow(
                            title = "Open links",
                            options = ChimahonConnectionOpeningPreference.entries.map { it.connectionTitle() },
                            selected = settings.connections.openingPreference.connectionTitle(),
                            onSelect = { selected ->
                                ChimahonConnectionOpeningPreference.entries
                                    .firstOrNull { it.connectionTitle() == selected }
                                    ?.let {
                                        onConnectionSettingsChange(
                                            settings.connections.copy(openingPreference = it),
                                        )
                                    }
                            },
                        )
                    }
                    item {
                        ExtensionStatusRow(
                            title = "Source connections",
                            subtitle = "${snapshot.summary.sourceCount} sources are available through the shared extension engine.",
                        )
                    }
                    item {
                        ExtensionStatusRow(
                            title = "External opening preference",
                            subtitle = "Current mode: ${settings.connections.openingPreference.connectionTitle()} on ${snapshot.runtime.platformName}.",
                        )
                    }
                }
                MorePage.BrowseSettings -> {
                    item { ListGroupHeader("Sources") }
                    item { RuntimeLineRow("Installed sources", snapshot.summary.sourceCount.toString()) }
                    item { RuntimeLineRow("Installed extensions", snapshot.summary.extensionCount.toString()) }
                    item { RuntimeLineRow("Repositories", snapshot.extensionRepos.size.toString()) }
                    item {
                        PreferenceSwitchRow(
                            "Show NSFW sources",
                            "Include sources marked adult in Browse",
                            UiIcon.VisibilityOff,
                            checked = settings.browse.showNsfwSources,
                            onCheckedChange = {
                                onBrowseSettingsChange(settings.browse.copy(showNsfwSources = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Hide library entries",
                            "Dim or hide titles already in your library",
                            UiIcon.Library,
                            checked = settings.browse.hideLibraryEntries,
                            onCheckedChange = {
                                onBrowseSettingsChange(settings.browse.copy(hideLibraryEntries = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Auto-load more",
                            "Continue loading extension/source lists as you scroll",
                            UiIcon.Refresh,
                            checked = settings.browse.autoLoadMore,
                            onCheckedChange = {
                                onBrowseSettingsChange(settings.browse.copy(autoLoadMore = it))
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Source display",
                            options = ChimahonBrowseSourceDisplayMode.entries.map { it.browseTitle() },
                            selected = settings.browse.sourceDisplayMode.browseTitle(),
                            onSelect = { selected ->
                                ChimahonBrowseSourceDisplayMode.entries
                                    .firstOrNull { it.browseTitle() == selected }
                                    ?.let { onBrowseSettingsChange(settings.browse.copy(sourceDisplayMode = it)) }
                            },
                        )
                    }
                    item {
                        SettingsMultiChoiceRow(
                            title = "Source languages",
                            options = snapshot.sourceLanguageOptions(),
                            selected = settings.browse.enabledLanguages,
                            emptyLabel = "All languages",
                            onToggle = { language ->
                                onBrowseSettingsChange(
                                    settings.browse.copy(
                                        enabledLanguages = settings.browse.enabledLanguages.toggled(language),
                                    ),
                                )
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Group by language",
                            "Group extension sources by language",
                            UiIcon.Web,
                            checked = settings.browse.groupSourcesByLanguage,
                            onCheckedChange = {
                                onBrowseSettingsChange(settings.browse.copy(groupSourcesByLanguage = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Show source language",
                            "Display language badges beside sources",
                            UiIcon.Tag,
                            checked = settings.browse.showSourceLanguage,
                            onCheckedChange = {
                                onBrowseSettingsChange(settings.browse.copy(showSourceLanguage = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Extension update notices",
                            "Notify when installed extensions have updates",
                            UiIcon.Updates,
                            checked = settings.browse.extensionUpdateNotificationsEnabled,
                            onCheckedChange = {
                                onBrowseSettingsChange(
                                    settings.browse.copy(extensionUpdateNotificationsEnabled = it),
                                )
                            },
                        )
                    }
                    item {
                        ExtensionStatusRow(
                            title = "Migration",
                            subtitle = "Choose a library source, title, and replacement source from Browse > Migrate.",
                        )
                    }
                }
                MorePage.DictionarySettings -> {
                    item { ListGroupHeader("Reader tools") }
                    item {
                        PreferenceSwitchRow(
                            title = "Dictionary lookup",
                            subtitle = "Enable shared reader lookup actions where a dictionary engine is present",
                            icon = UiIcon.Search,
                            checked = settings.dictionary.enabled,
                            onCheckedChange = {
                                onDictionarySettingsChange(settings.dictionary.copy(enabled = it))
                            },
                        )
                    }
                    item {
                        SettingsMultiChoiceRow(
                            title = "Lookup languages",
                            options = commonReaderLanguages(),
                            selected = settings.dictionary.enabledLanguages,
                            emptyLabel = "All languages",
                            onToggle = { language ->
                                onDictionarySettingsChange(
                                    settings.dictionary.copy(
                                        enabledLanguages = settings.dictionary.enabledLanguages.toggled(language),
                                    ),
                                )
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            title = "OCR",
                            subtitle = "Allow image text extraction on platforms with native OCR engines",
                            icon = UiIcon.Search,
                            checked = settings.dictionary.ocrEnabled,
                            onCheckedChange = {
                                onDictionarySettingsChange(settings.dictionary.copy(ocrEnabled = it))
                            },
                        )
                    }
                }
                MorePage.SecuritySettings -> {
                    item { ListGroupHeader("Privacy") }
                    item {
                        PreferenceSwitchRow(
                            "Incognito mode",
                            "Pause reading history and reader progress updates",
                            UiIcon.Incognito,
                            checked = incognitoMode,
                            onCheckedChange = onIncognitoModeChange,
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Incognito by default",
                            "Start new sessions with history paused",
                            UiIcon.Incognito,
                            checked = settings.security.incognitoModeByDefault,
                            onCheckedChange = {
                                onSecuritySettingsChange(settings.security.copy(incognitoModeByDefault = it))
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Secure screen",
                            options = ChimahonSecureScreenMode.entries.map { it.securityTitle() },
                            selected = settings.security.secureScreenMode.securityTitle(),
                            onSelect = { selected ->
                                ChimahonSecureScreenMode.entries
                                    .firstOrNull { it.securityTitle() == selected }
                                    ?.let { onSecuritySettingsChange(settings.security.copy(secureScreenMode = it)) }
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Hide notification content",
                            "Mask titles and chapters in notifications",
                            UiIcon.VisibilityOff,
                            checked = settings.security.hideNotificationContent,
                            onCheckedChange = {
                                onSecuritySettingsChange(settings.security.copy(hideNotificationContent = it))
                            },
                        )
                    }
                    item { ListGroupHeader("App lock") }
                    item {
                        PreferenceSwitchRow(
                            "Require authentication",
                            "Use the native platform lock when available",
                            UiIcon.Settings,
                            checked = settings.security.requireAuthentication,
                            onCheckedChange = {
                                onSecuritySettingsChange(settings.security.copy(requireAuthentication = it))
                            },
                        )
                    }
                    item {
                        SettingsChoiceRow(
                            title = "Lock after",
                            options = listOf("Immediately", "1 minute", "5 minutes", "15 minutes", "30 minutes"),
                            selected = settings.security.lockAfterMinutes.toLockAfterTitle(),
                            onSelect = { selected ->
                                onSecuritySettingsChange(
                                    settings.security.copy(lockAfterMinutes = selected.toLockAfterMinutes()),
                                )
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Lock on app exit",
                            "Require authentication after closing the app",
                            UiIcon.Back,
                            checked = settings.security.lockOnAppExit,
                            onCheckedChange = {
                                onSecuritySettingsChange(settings.security.copy(lockOnAppExit = it))
                            },
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Protect downloads",
                            "Treat downloaded files as private app data where supported",
                            UiIcon.Download,
                            checked = settings.security.protectDownloads,
                            onCheckedChange = {
                                onSecuritySettingsChange(settings.security.copy(protectDownloads = it))
                            },
                        )
                    }
                }
                MorePage.AdvancedSettings -> {
                    item { ListGroupHeader("Privacy") }
                    item {
                        PreferenceSwitchRow(
                            "Incognito mode",
                            "Pause reading history and reader progress updates",
                            UiIcon.Incognito,
                            checked = incognitoMode,
                            onCheckedChange = onIncognitoModeChange,
                        )
                    }
                    item { ListGroupHeader("Runtime diagnostics") }
                    item { RuntimeLineRow("Database", snapshot.runtime.databaseState) }
                    item { RuntimeLineRow("Extension engine", snapshot.runtime.extensionState) }
                    item { RuntimeLineRow("Background work", snapshot.runtime.backgroundState) }
                }
                MorePage.About -> {
                    item {
                        MoreLogoHeader(snapshot)
                    }
                    item { RuntimeLineRow("Database", snapshot.runtime.databaseState) }
                    item { RuntimeLineRow("Extension engine", snapshot.runtime.extensionState) }
                    item { RuntimeLineRow("Background tasks", snapshot.runtime.backgroundState) }
                    item {
                        ExtensionStatusRow(
                            title = "Source code",
                            subtitle = CHIMAHON_PROJECT_URL,
                            action = "Open",
                            onAction = { onOpenExternalUrl(CHIMAHON_PROJECT_URL) },
                        )
                    }
                }
                MorePage.Help -> {
                    item { ListGroupHeader("Getting started") }
                    item {
                        ExtensionStatusRow(
                            title = "Install sources",
                            subtitle = "Open Browse, choose Extensions, add a compatible repository, then install an extension.",
                        )
                    }
                    item {
                        ExtensionStatusRow(
                            title = "Add manga",
                            subtitle = "Open a source, choose a title, and use Add to place it in your library.",
                        )
                    }
                    item {
                        ExtensionStatusRow(
                            title = "Reader controls",
                            subtitle = "Use the bottom bar for pages and chapters. Settings contains viewer, fit, and background modes.",
                        )
                    }
                    item {
                        ExtensionStatusRow(
                            title = "Project help",
                            subtitle = CHIMAHON_PROJECT_URL,
                            action = "Open",
                            onAction = { onOpenExternalUrl(CHIMAHON_PROJECT_URL) },
                        )
                    }
                }
                MorePage.Main -> Unit
            }
        }
    }
}

@Composable
private fun MoreDownloadSummary(
    downloadsDirectory: String,
    downloadedOnlyMode: Boolean,
    onOpenSettings: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .clickable(onClick = onOpenSettings)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ChimahonPalette.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            IconGlyph(
                icon = UiIcon.Download,
                contentDescription = "",
                tint = ChimahonPalette.primary,
                modifier = Modifier.size(24.dp),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
        ) {
            Label(
                if (downloadedOnlyMode) "Downloaded-only mode enabled" else "Downloads ready",
                ChimahonPalette.onSurface,
                14,
                weight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Label(
                downloadsDirectory,
                ChimahonPalette.secondaryText,
                11,
                maxLines = 2,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        IconGlyph(
            icon = UiIcon.Settings,
            contentDescription = "Download settings",
            tint = ChimahonPalette.secondaryText,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun StorageUsageHero(
    storage: ChimahonStorageData,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(ChimahonPalette.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            IconGlyph(
                icon = UiIcon.Storage,
                contentDescription = "",
                tint = ChimahonPalette.primary,
                modifier = Modifier.size(28.dp),
            )
        }
        Label(
            storage.totalSizeBytes.toReadableBytes(),
            ChimahonPalette.onSurface,
            25,
            weight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.padding(top = 12.dp),
        )
        Label(
            "${storage.downloadedMangaCount} downloaded manga - ${storage.downloadedChapterCount} downloaded chapter(s)",
            ChimahonPalette.secondaryText,
            12,
            maxLines = 2,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun StorageSectionRow(
    title: String,
    section: ChimahonStorageSection,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ChimahonPalette.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            IconGlyph(
                icon = UiIcon.Storage,
                contentDescription = "",
                tint = ChimahonPalette.primary,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Label(title, ChimahonPalette.onSurface, 14, weight = FontWeight.SemiBold, maxLines = 1)
            Label(
                if (section.exists) section.path else "${section.path} (missing)",
                ChimahonPalette.secondaryText,
                11,
                maxLines = 2,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Label(section.sizeBytes.toReadableBytes(), ChimahonPalette.onSurface, 12, weight = FontWeight.SemiBold)
            Label(
                "${section.fileCount} file(s), ${section.directoryCount} dir(s)",
                ChimahonPalette.secondaryText,
                10,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun DownloadQueueToolbar(
    queue: ChimahonDownloadQueueData?,
    message: String?,
    processing: Boolean,
    autoRun: Boolean,
    onAutoRunChange: (Boolean) -> Unit,
    onTogglePaused: () -> Unit,
    onProcessNext: () -> Unit,
    onClearCompleted: () -> Unit,
) {
    val entries = queue?.entries.orEmpty()
    val completedCount = entries.count { it.status == ChimahonDownloadState.Downloaded }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ChimahonPalette.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                IconGlyph(
                    icon = if (queue?.paused == true) UiIcon.Refresh else UiIcon.Download,
                    contentDescription = "",
                    tint = ChimahonPalette.primary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Label(
                    if (queue?.paused == true) "Download queue paused" else "Download queue",
                    ChimahonPalette.onSurface,
                    14,
                    weight = FontWeight.SemiBold,
                    maxLines = 1,
                )
                Label(
                    if (processing) "Processing the next chapter" else queue?.downloadQueueSummary() ?: "Loading saved queue",
                    ChimahonPalette.secondaryText,
                    11,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
            TextButtonLike(
                text = if (queue?.paused == true) "Resume all" else "Pause all",
                onClick = onTogglePaused,
            )
        }
        if (queue != null && entries.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                QueueStatChip("Queued", queue.pendingCount, UiIcon.Chapters, Modifier.weight(1f))
                QueueStatChip("Active", queue.activeCount, UiIcon.Play, Modifier.weight(1f))
                QueueStatChip("Failed", queue.failedCount, UiIcon.Info, Modifier.weight(1f))
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Label(
                message ?: if (completedCount > 0) {
                    "$completedCount completed download(s) can be cleared"
                } else {
                    "Per-chapter controls match the Android download queue"
                },
                if (message?.startsWith("Could not") == true) ChimahonPalette.error else ChimahonPalette.secondaryText,
                11,
                maxLines = 2,
                modifier = Modifier.weight(1f),
            )
            if (queue != null && queue.pendingCount > 0 && queue.paused.not()) {
                TextButtonLike(if (processing) "Working" else "Start next", onClick = onProcessNext)
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (queue != null && queue.entries.isNotEmpty()) {
                QueueTextAction(
                    if (autoRun) "Auto on" else "Auto off",
                    onClick = { onAutoRunChange(!autoRun) },
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (completedCount > 0) {
                TextButtonLike("Clear done", onClick = onClearCompleted)
            }
        }
    }
}

@Composable
private fun QueueStatChip(
    label: String,
    value: Int,
    icon: UiIcon,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ChimahonPalette.background)
            .border(1.dp, ChimahonPalette.divider, RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        IconGlyph(icon, "", ChimahonPalette.primary, Modifier.size(16.dp))
        Label(label, ChimahonPalette.secondaryText, 10, maxLines = 1)
        Label(value.toString(), ChimahonPalette.onSurface, 11, weight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
private fun DownloadQueueEntryRow(
    entry: ChimahonDownloadQueueEntry,
    onPrimaryAction: () -> Unit,
    onMoveTop: () -> Unit,
    onMoveBottom: () -> Unit,
    onRemove: () -> Unit,
) {
    val progress = entry.downloadProgressFraction()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(entry.status.statusBackground()),
                contentAlignment = Alignment.Center,
            ) {
                IconGlyph(
                    icon = entry.status.statusIcon(),
                    contentDescription = "",
                    tint = entry.status.statusColor(),
                    modifier = Modifier.size(23.dp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Label(entry.mangaTitle, ChimahonPalette.onSurface, 14, weight = FontWeight.SemiBold, maxLines = 1)
                Label(entry.chapterName, ChimahonPalette.secondaryText, 12, maxLines = 1, modifier = Modifier.padding(top = 2.dp))
                Label(
                    entry.downloadSubtitle(),
                    if (entry.status == ChimahonDownloadState.Error) ChimahonPalette.error else ChimahonPalette.secondaryText,
                    11,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
            TextButtonLike(entry.primaryDownloadActionTitle(), onClick = onPrimaryAction)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ChimahonPalette.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(entry.status.statusColor()),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Label(
                entry.status.queueTitle(),
                entry.status.statusColor(),
                11,
                weight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
            QueueTextAction("Top", onMoveTop)
            QueueTextAction("Bottom", onMoveBottom)
            QueueTextAction("Remove", onRemove, destructive = true)
        }
    }
}

@Composable
private fun QueueTextAction(
    label: String,
    onClick: () -> Unit,
    destructive: Boolean = false,
) {
    Label(
        label,
        if (destructive) ChimahonPalette.error else ChimahonPalette.primary,
        11,
        weight = FontWeight.SemiBold,
        maxLines = 1,
        modifier = Modifier
            .clip(RoundedCornerShape(7.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 5.dp),
    )
}

@Composable
private fun MoreStatisticsHero(
    readDuration: Long,
    readChapters: Int,
    totalChapters: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(horizontal = 20.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Label(
            text = readDuration.toReadingDuration(),
            color = ChimahonPalette.onSurface,
            size = 34,
            weight = FontWeight.SemiBold,
            maxLines = 1,
        )
        Label(
            text = "total reading time",
            color = ChimahonPalette.secondaryText,
            size = 12,
            maxLines = 1,
            modifier = Modifier.padding(top = 3.dp),
        )
        val progress = if (totalChapters == 0) 0f else readChapters.toFloat() / totalChapters.toFloat()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ChimahonPalette.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(ChimahonPalette.primary),
            )
        }
        Label(
            text = "$readChapters of $totalChapters chapters read",
            color = ChimahonPalette.secondaryText,
            size = 11,
            maxLines = 1,
            modifier = Modifier.padding(top = 7.dp),
        )
    }
}

@Composable
private fun SettingsChoiceRow(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Label(
            title,
            ChimahonPalette.onSurface,
            14,
            weight = FontWeight.SemiBold,
            maxLines = 1,
        )
        FilterChips(
            chips = options,
            selected = selected,
            onSelect = onSelect,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun SettingsMultiChoiceRow(
    title: String,
    options: List<String>,
    selected: List<String>,
    emptyLabel: String,
    onToggle: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Label(
            title,
            ChimahonPalette.onSurface,
            14,
            weight = FontWeight.SemiBold,
            maxLines = 1,
        )
        Label(
            if (selected.isEmpty()) emptyLabel else selected.joinToString(),
            ChimahonPalette.secondaryText,
            11,
            maxLines = 2,
            modifier = Modifier.padding(top = 3.dp),
        )
        LazyRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            items(options, key = { it }) { option ->
                val active = option in selected
                Label(
                    text = option,
                    color = if (active) Color.White else ChimahonPalette.secondaryText,
                    size = 11,
                    weight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (active) ChimahonPalette.primary else ChimahonPalette.surfaceVariant)
                        .clickable { onToggle(option) }
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                )
            }
        }
    }
}

@Composable
private fun ThemePickerRow(
    selected: ChimahonAppTheme,
    dark: Boolean,
    onSelect: (ChimahonAppTheme) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(vertical = 12.dp),
    ) {
        Label(
            "App theme",
            ChimahonPalette.onSurface,
            14,
            weight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            items(ChimahonAppTheme.entries, key = { it.name }) { theme ->
                val colors = chimahonThemeColors(theme, dark = dark, amoled = false)
                val active = theme == selected
                Column(
                    modifier = Modifier
                        .width(132.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(colors.surface)
                        .border(
                            width = if (active) 2.dp else 1.dp,
                            color = if (active) colors.primary else colors.divider,
                            shape = RoundedCornerShape(7.dp),
                        )
                        .clickable { onSelect(theme) }
                        .padding(10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        listOf(colors.primary, colors.primaryContainer, colors.surfaceVariant).forEach {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(it),
                            )
                        }
                    }
                    Label(
                        theme.title,
                        colors.onSurface,
                        11,
                        weight = if (active) FontWeight.Bold else FontWeight.SemiBold,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
    }
}

private fun Long.toReadingDuration(): String {
    if (this <= 0L) return "0m"
    val hours = this / 3_600_000L
    val minutes = (this % 3_600_000L) / 60_000L
    return buildString {
        if (hours > 0L) append(hours).append("h ")
        append(minutes).append("m")
    }.trim()
}

@Composable
private fun MoreStatisticRow(
    label: String,
    value: String,
    icon: UiIcon,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconGlyph(icon, "", ChimahonPalette.primary, Modifier.size(22.dp))
        Label(label, ChimahonPalette.onSurface, 13, modifier = Modifier.weight(1f).padding(start = 18.dp))
        Label(value, ChimahonPalette.secondaryText, 13, weight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
private fun CategoryInputRow(
    name: String,
    message: String?,
    busy: Boolean,
    onNameChange: (String) -> Unit,
    onCreate: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ChimahonPalette.background)
                    .border(1.dp, ChimahonPalette.divider, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (name.isBlank()) {
                    Label("New category", ChimahonPalette.secondaryText, 12, maxLines = 1)
                }
                BasicTextField(
                    value = name,
                    onValueChange = onNameChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = ChimahonPalette.onSurface,
                        fontSize = 13.sp,
                        lineHeight = 17.sp,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            TextButtonLike(if (busy) "Saving" else "Add", onClick = onCreate)
        }
        message?.let {
            Label(
                it,
                if (it.contains("created") || it.startsWith("Deleted")) {
                    ChimahonPalette.primary
                } else {
                    ChimahonPalette.error
                },
                11,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun RuntimeLineRow(
    label: String,
    value: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Label(label, ChimahonPalette.secondaryText, 10, weight = FontWeight.SemiBold)
        Label(value, ChimahonPalette.onSurface, 12, maxLines = 3, modifier = Modifier.padding(top = 3.dp))
    }
}

@Composable
private fun MoreLogoHeader(snapshot: ChimahonSnapshot) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppMark(size = 72.dp)
        Label(
            "Chimahon",
            ChimahonPalette.onSurface,
            18,
            weight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.padding(top = 10.dp),
        )
        Label(
            snapshot.runtime.platformName,
            ChimahonPalette.secondaryText,
            11,
            maxLines = 1,
            modifier = Modifier.padding(top = 3.dp),
        )
    }
}

@Composable
private fun PreferenceSwitchRow(
    title: String,
    subtitle: String,
    icon: UiIcon,
    checked: Boolean? = null,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    var localEnabled by remember(title) { mutableStateOf(false) }
    val enabled = checked ?: localEnabled
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .clickable {
                val next = !enabled
                if (checked == null) {
                    localEnabled = next
                }
                onCheckedChange(next)
            }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconGlyph(
            icon = icon,
            contentDescription = "",
            tint = ChimahonPalette.primary,
            modifier = Modifier.size(24.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Label(
                title,
                ChimahonPalette.onSurface,
                14,
                maxLines = 2,
                modifier = Modifier.padding(start = 24.dp),
            )
            Label(
                subtitle,
                ChimahonPalette.secondaryText,
                12,
                maxLines = 1,
                modifier = Modifier.padding(start = 24.dp, top = 3.dp),
            )
        }
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (enabled) ChimahonPalette.primary else ChimahonPalette.surfaceVariant)
                .padding(3.dp),
            contentAlignment = if (enabled) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(ChimahonPalette.surface),
            )
        }
    }
}

@Composable
private fun PreferenceRow(
    title: String,
    subtitle: String?,
    icon: UiIcon,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChimahonPalette.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconGlyph(
            icon = icon,
            contentDescription = "",
            tint = ChimahonPalette.primary,
            modifier = Modifier.size(24.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp),
        ) {
            Label(title, ChimahonPalette.onSurface, 14, maxLines = 2)
            subtitle?.takeIf { it.isNotBlank() }?.let {
                Label(
                    it,
                    ChimahonPalette.secondaryText,
                    11,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
        }
        IconGlyph(
            icon = UiIcon.Forward,
            contentDescription = "",
            tint = ChimahonPalette.secondaryText,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun PreferenceDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(ChimahonPalette.divider),
    )
}

@Composable
private fun RuntimePanel(runtime: ChimahonRuntimeInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ChimahonPalette.surface)
            .border(1.dp, ChimahonPalette.divider, RoundedCornerShape(10.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Label("Runtime", ChimahonPalette.onSurface, 14, weight = FontWeight.SemiBold)
        RuntimeLine("Platform", runtime.platformName)
        RuntimeLine("Database", runtime.databaseState)
        RuntimeLine("Extensions", runtime.extensionState)
        RuntimeLine("Background work", runtime.backgroundState)
        RuntimeLine("Files", runtime.filesDir)
    }
}

@Composable
private fun RuntimeLine(label: String, value: String) {
    Column {
        Label(label, ChimahonPalette.secondaryText, 10, weight = FontWeight.SemiBold)
        Label(value, ChimahonPalette.onSurface, 12, maxLines = 2, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun MobileListItem(
    marker: String,
    title: String,
    subtitle: String,
    trailing: String,
    markerColor: Color,
    markerTextColor: Color,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ChimahonPalette.surface)
            .clickable(onClick = onClick)
            .border(1.dp, ChimahonPalette.divider, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(markerColor),
            contentAlignment = Alignment.Center,
        ) {
            Label(marker, markerTextColor, 13, weight = FontWeight.Bold, maxLines = 1)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Label(title, ChimahonPalette.onSurface, 14, weight = FontWeight.SemiBold, maxLines = 1)
            Label(subtitle, ChimahonPalette.secondaryText, 12, maxLines = 1, modifier = Modifier.padding(top = 3.dp))
        }
        Label(trailing, ChimahonPalette.secondaryText, 11, maxLines = 1, modifier = Modifier.padding(start = 12.dp))
    }
}

@Composable
private fun MobileBanner(title: String, detail: String, action: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ChimahonPalette.primaryContainer)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Label(title, ChimahonPalette.primary, 15, weight = FontWeight.SemiBold)
            Label(detail, ChimahonPalette.onPrimaryContainer, 12, maxLines = 2, modifier = Modifier.padding(top = 4.dp))
        }
        TextButtonLike(action, onClick = {})
    }
}

@Composable
private fun EmptyMobileState(
    marker: String,
    icon: UiIcon? = null,
    title: String,
    detail: String,
    action: String? = null,
    onAction: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(ChimahonPalette.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            if (icon != null) {
                IconGlyph(
                    icon = icon,
                    contentDescription = "",
                    tint = ChimahonPalette.primary,
                    modifier = Modifier.size(30.dp),
                )
            } else {
                Label(marker, ChimahonPalette.primary, 28, weight = FontWeight.Bold)
            }
        }
        Label(title, ChimahonPalette.onSurface, 18, weight = FontWeight.SemiBold, modifier = Modifier.padding(top = 18.dp))
        Label(
            text = detail,
            color = ChimahonPalette.secondaryText,
            size = 13,
            lineHeight = 19,
            modifier = Modifier
                .padding(top = 7.dp)
                .widthIn(max = 420.dp),
        )
        if (action != null) {
            TextButtonLike(action, onClick = onAction, modifier = Modifier.padding(top = 18.dp))
        }
    }
}

@Composable
private fun EmptyListPanel(marker: String, title: String, detail: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ChimahonPalette.surface)
            .border(1.dp, ChimahonPalette.divider, RoundedCornerShape(12.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ChimahonPalette.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Label(marker, ChimahonPalette.secondaryText, 18, weight = FontWeight.Bold)
        }
        Label(title, ChimahonPalette.onSurface, 15, weight = FontWeight.SemiBold, modifier = Modifier.padding(top = 14.dp))
        Label(detail, ChimahonPalette.secondaryText, 12, lineHeight = 18, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
private fun SectionHeader(text: String) {
    Label(
        text = text,
        color = ChimahonPalette.secondaryText,
        size = 11,
        weight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 2.dp),
    )
}

@Composable
private fun FilterChips(
    chips: List<String>,
    selected: String,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chips.forEach { chip ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (chip == selected) ChimahonPalette.primaryContainer else ChimahonPalette.surface)
                    .clickable { onSelect(chip) }
                    .border(
                        1.dp,
                        if (chip == selected) ChimahonPalette.primaryContainer else ChimahonPalette.divider,
                        RoundedCornerShape(18.dp),
                    )
                    .padding(horizontal = 13.dp, vertical = 8.dp),
            ) {
                Label(
                    text = chip,
                    color = if (chip == selected) ChimahonPalette.primary else ChimahonPalette.secondaryText,
                    size = 12,
                    weight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun <T> SegmentTabs(
    values: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ChimahonPalette.surfaceVariant)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        values.forEach { value ->
            val isSelected = value == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) ChimahonPalette.surface else Color.Transparent)
                    .clickable { onSelect(value) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Label(
                    text = label(value),
                    color = if (isSelected) ChimahonPalette.primary else ChimahonPalette.secondaryText,
                    size = 12,
                    weight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun TextButtonLike(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(ChimahonPalette.primary)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Label(text, Color.White, 12, weight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
private fun TopAction(
    icon: UiIcon,
    contentDescription: String,
    tint: Color = ChimahonPalette.primary,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        IconGlyph(
            icon = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun ReaderAction(
    icon: UiIcon,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    tint: Color = Color.White,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (active) tint.copy(alpha = 0.16f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        IconGlyph(
            icon = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(21.dp),
        )
    }
}

@Composable
private fun ReaderTextAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(ReaderPalette.control)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Label(text, Color.White, 12, weight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
private fun AppMark(size: androidx.compose.ui.unit.Dp = 44.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(ChimahonPalette.primary),
        contentAlignment = Alignment.Center,
    ) {
        Label("C", Color.White, if (size > 50.dp) 30 else 20, weight = FontWeight.Bold)
    }
}

@Composable
private fun LoadingHome() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(ChimahonPalette.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(ChimahonPalette.primary),
            )
        }
        Label(
            text = "Loading Chimahon",
            color = ChimahonPalette.secondaryText,
            size = 13,
            modifier = Modifier.padding(top = 14.dp),
        )
    }
}

@Composable
private fun FailureHome(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ChimahonPalette.errorContainer),
            contentAlignment = Alignment.Center,
        ) {
            Label("!", ChimahonPalette.error, 24, weight = FontWeight.Bold)
        }
        Label("Chimahon could not start", ChimahonPalette.onSurface, 18, weight = FontWeight.SemiBold, modifier = Modifier.padding(top = 16.dp))
        Label(message, ChimahonPalette.secondaryText, 13, lineHeight = 19, modifier = Modifier.padding(top = 8.dp))
        TextButtonLike("Try again", onClick = onRetry, modifier = Modifier.padding(top = 18.dp))
    }
}

@Composable
private fun Label(
    text: String,
    color: Color,
    size: Int,
    modifier: Modifier = Modifier,
    weight: FontWeight = FontWeight.Normal,
    lineHeight: Int = size + 4,
    maxLines: Int = Int.MAX_VALUE,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(
            color = color,
            fontSize = (size * ChimahonPalette.fontScale).sp,
            fontWeight = weight,
            lineHeight = (lineHeight * ChimahonPalette.fontScale).sp,
        ),
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}

private fun ChimahonUiState.badgeFor(tab: HomeTab): Int? {
    if (this !is ChimahonUiState.Ready) return null
    return when (tab) {
        HomeTab.Library -> snapshot.library.size
        HomeTab.Updates -> (snapshot.updates.size + snapshot.updateIssues.size).takeIf { it > 0 }
        HomeTab.History -> snapshot.history.size.takeIf { it > 0 }
        HomeTab.Browse -> snapshot.summary.sourceCount.takeIf { it > 0 }
        HomeTab.More -> null
    }
}

private fun HomeTab.subtitle(state: ChimahonUiState): String {
    val snapshot = (state as? ChimahonUiState.Ready)?.snapshot
    return when (this) {
        HomeTab.Library -> snapshot?.let { "${it.library.size} manga in your library" } ?: "Your manga library"
        HomeTab.Updates -> snapshot?.let { "${it.updates.size} recent chapter update(s)" } ?: "Recent chapters"
        HomeTab.History -> snapshot?.let { "${it.history.size} reading history item(s)" } ?: "Recently read manga"
        HomeTab.Browse -> snapshot?.let { "${it.summary.sourceCount} source(s), ${it.summary.extensionCount} extension(s)" } ?: "Sources and extensions"
        HomeTab.More -> snapshot?.runtime?.platformName?.let { "$it settings and tools" } ?: "Settings and tools"
    }
}

private fun ChimahonFilterMode.matches(value: Boolean): Boolean {
    return when (this) {
        ChimahonFilterMode.Any -> true
        ChimahonFilterMode.Include -> value
        ChimahonFilterMode.Exclude -> !value
    }
}

private fun ChimahonDownloadQueueData.downloadQueueSummary(): String {
    if (entries.isEmpty()) return "No queued chapter downloads"
    val total = entries.size
    val downloaded = entries.count { it.status == ChimahonDownloadState.Downloaded }
    return "$total chapter(s) - $activeCount active, $pendingCount queued, $downloaded completed"
}

private fun ChimahonDownloadQueueEntry.downloadProgressFraction(): Float {
    val total = totalBytes
    return when {
        status == ChimahonDownloadState.Downloaded -> 1f
        total != null && total > 0L -> (downloadedBytes.toFloat() / total.toFloat()).coerceIn(0f, 1f)
        progress > 0 -> (progress.toFloat() / 100f).coerceIn(0f, 1f)
        status == ChimahonDownloadState.Downloading -> 0.08f
        else -> 0f
    }
}

private fun ChimahonDownloadQueueEntry.downloadSubtitle(): String {
    if (status == ChimahonDownloadState.Error && !errorMessage.isNullOrBlank()) {
        return errorMessage
    }
    val byteLabel = totalBytes?.takeIf { it > 0L }?.let { total ->
        "${downloadedBytes.toReadableBytes()} / ${total.toReadableBytes()}"
    } ?: downloadedBytes.takeIf { it > 0L }?.toReadableBytes()
    return listOfNotNull(
        "Source $sourceId",
        byteLabel,
        progress.takeIf { it > 0 && it < 100 }?.let { "$it%" },
    ).joinToString(" - ")
}

private fun ChimahonDownloadQueueEntry.primaryDownloadActionTitle(): String {
    return when (status) {
        ChimahonDownloadState.Paused -> "Resume"
        ChimahonDownloadState.Error -> "Retry"
        ChimahonDownloadState.Downloaded -> "Remove"
        ChimahonDownloadState.Queued,
        ChimahonDownloadState.Downloading,
        -> "Pause"
    }
}

private fun ChimahonDownloadState.queueTitle(): String {
    return when (this) {
        ChimahonDownloadState.Queued -> "Queued"
        ChimahonDownloadState.Downloading -> "Downloading"
        ChimahonDownloadState.Paused -> "Paused"
        ChimahonDownloadState.Downloaded -> "Downloaded"
        ChimahonDownloadState.Error -> "Error"
    }
}

private fun ChimahonDownloadState.statusIcon(): UiIcon {
    return when (this) {
        ChimahonDownloadState.Queued -> UiIcon.Chapters
        ChimahonDownloadState.Downloading -> UiIcon.Download
        ChimahonDownloadState.Paused -> UiIcon.Refresh
        ChimahonDownloadState.Downloaded -> UiIcon.CheckCircle
        ChimahonDownloadState.Error -> UiIcon.Info
    }
}

private fun ChimahonDownloadState.statusColor(): Color {
    return when (this) {
        ChimahonDownloadState.Error -> ChimahonPalette.error
        ChimahonDownloadState.Downloaded -> ChimahonPalette.primary
        ChimahonDownloadState.Paused -> ChimahonPalette.secondaryText
        else -> ChimahonPalette.primary
    }
}

private fun ChimahonDownloadState.statusBackground(): Color {
    return when (this) {
        ChimahonDownloadState.Error -> ChimahonPalette.errorContainer
        ChimahonDownloadState.Paused -> ChimahonPalette.surfaceVariant
        else -> ChimahonPalette.primaryContainer
    }
}

private fun ChimahonChapterDownloadStatus.chapterDownloadStatusLabel(): String {
    if (status == ChimahonDownloadState.Error && !errorMessage.isNullOrBlank()) {
        return errorMessage
    }
    return when (status) {
        ChimahonDownloadState.Downloading -> progress
            .takeIf { it in 1..99 }
            ?.let { "Downloading $it%" }
            ?: "Downloading"
        ChimahonDownloadState.Downloaded -> if (downloadedOnDisk) "Downloaded" else "Download complete"
        else -> status.queueTitle()
    }
}

private fun Long.toReadableBytes(): String {
    if (this <= 0L) return "0 B"
    val units = listOf("B", "KB", "MB", "GB", "TB")
    var value = this.toDouble()
    var index = 0
    while (value >= 1024.0 && index < units.lastIndex) {
        value /= 1024.0
        index += 1
    }
    return if (index == 0) {
        "${value.toLong()} ${units[index]}"
    } else {
        "${(value * 10.0).toInt() / 10.0} ${units[index]}"
    }
}

private fun ChimahonCacheClearResult.cacheClearSummary(): String {
    val targetTitle = when (target) {
        ChimahonCacheClearTarget.Thumbnails -> "Thumbnail cache"
        ChimahonCacheClearTarget.ApplicationCache -> "Application cache"
    }
    val removedBytes = diskBytesRemoved + memoryBytesRemoved
    val removedEntries = diskFilesRemoved + memoryEntriesRemoved
    val base = "$targetTitle cleared: ${removedBytes.toReadableBytes()}, $removedEntries file/cache item(s)"
    return if (failures.isEmpty()) {
        base
    } else {
        "$base, ${failures.size} path(s) could not be removed"
    }
}

private fun ChimahonDatabaseMaintenanceResult.databaseMaintenanceSummary(): String {
    return "Removed $historyEntriesRemoved history record(s) and $updateIssuesRemoved update issue(s). " +
        "$remainingHistoryEntryCount history record(s) remain."
}

private fun previewDataMaintenanceSnapshot(): ChimahonDataMaintenanceSnapshot {
    val section = ChimahonStorageSection(
        path = "preview",
        exists = false,
        sizeBytes = 0L,
        fileCount = 0,
        directoryCount = 0,
    )
    return ChimahonDataMaintenanceSnapshot(
        storage = ChimahonStorageData(
            files = section,
            cache = section,
            downloads = section,
            totalSizeBytes = 0L,
            downloadedMangaCount = 0,
            downloadedChapterCount = 0,
        ),
        thumbnailCache = ChimahonThumbnailCacheData(entryCount = 0, sizeBytes = 0L),
        database = ChimahonDatabaseMaintenanceData(
            historyEntryCount = 0,
            updateIssues = ChimahonUpdateIssueSummary(
                totalIssueCount = 0,
                affectedMangaCount = 0,
                staleIssueCount = 0,
                groups = emptyList(),
            ),
        ),
    )
}

private fun previewCacheClearResult(target: ChimahonCacheClearTarget): ChimahonCacheClearResult {
    return ChimahonCacheClearResult(
        target = target,
        diskBytesRemoved = 0L,
        diskFilesRemoved = 0,
        diskDirectoriesRemoved = 0,
        memoryBytesRemoved = 0L,
        memoryEntriesRemoved = 0,
    )
}

private fun previewDatabaseMaintenanceResult(): ChimahonDatabaseMaintenanceResult {
    return ChimahonDatabaseMaintenanceResult(
        historyEntriesRemoved = 0,
        updateIssuesRemoved = 0,
        remainingHistoryEntryCount = 0,
        remainingUpdateIssueCount = 0,
    )
}

private fun ChimahonRecentUpdateEntry.updateStateLabel(): String {
    return when {
        read -> "Read"
        lastPageRead > 0L -> "Started"
        bookmarked -> "Bookmarked"
        else -> "New"
    }
}

private fun ChimahonHistoryEntry.historyStateLabel(): String {
    return when {
        read -> "Read"
        lastPageRead > 0L -> "Page ${lastPageRead + 1}"
        readAt != null && readAt > 0L -> "Recent"
        else -> "History"
    }
}

private fun ChimahonSnapshot.detailTitle(mangaId: Long?): String {
    return mangaId?.let { mangaDetails[it]?.title } ?: "Manga details"
}

private fun ChimahonSnapshot.sourceName(sourceId: Long): String {
    return sources.firstOrNull { it.id == sourceId }?.name ?: "Source $sourceId"
}

private fun ChimahonSnapshot.libraryCategoryTabs(): List<ChimahonLibraryCategory> {
    val defaultCategory = libraryCategories.firstOrNull { it.id == DEFAULT_LIBRARY_CATEGORY_ID }
        ?: ChimahonLibraryCategory(
            id = DEFAULT_LIBRARY_CATEGORY_ID,
            name = "",
            order = -1L,
            hidden = false,
        )
    val userCategories = libraryCategories
        .asSequence()
        .filter { !it.hidden }
        .filterNot { it.id == DEFAULT_LIBRARY_CATEGORY_ID }
        .sortedWith(compareBy<ChimahonLibraryCategory> { it.order }.thenBy { it.displayName().lowercase() })
        .toList()

    return listOf(
        ChimahonLibraryCategory(
            id = ALL_LIBRARY_CATEGORY_ID,
            name = "All",
            order = -2L,
            hidden = false,
        ),
        defaultCategory.copy(hidden = false),
    ) + userCategories
}

private fun ChimahonSnapshot.libraryForCategory(categoryId: Long): List<ChimahonMangaEntry> {
    if (categoryId == ALL_LIBRARY_CATEGORY_ID) return library
    return library.filter { manga ->
        libraryCategoryMemberships[manga.id].orEmpty().contains(categoryId)
    }
}

private fun ChimahonLibraryCategory.displayName(): String {
    return when {
        id == ALL_LIBRARY_CATEGORY_ID -> "All"
        name.isBlank() -> "Default"
        else -> name
    }
}

private fun List<ChimahonChapterEntry>.nextReadableChapter(): ChimahonChapterEntry? {
    return firstOrNull { it.lastPageRead > 0L && !it.read }
        ?: lastOrNull { !it.read }
        ?: lastOrNull()
}

private fun ChimahonChapterEntry.chapterMarker(): String {
    return when {
        bookmarked -> "B"
        read -> "R"
        chapterNumber > 0.0 -> chapterNumber.toDisplayChapter()
        else -> "C"
    }
}

private fun ChimahonChapterEntry.chapterStateLabel(): String {
    return when {
        read -> "Read"
        lastPageRead > 0L -> "Page ${lastPageRead + 1}"
        bookmarked -> "Bookmarked"
        else -> "Unread"
    }
}

private fun ChimahonRemoteChapterEntry.remoteChapterMarker(): String {
    return if (chapterNumber > 0.0) chapterNumber.toDisplayChapter() else "C"
}

private fun ChimahonRemoteChapterEntry.toReaderRequest(
    detail: ChimahonRemoteMangaDetail,
    chapters: List<ChimahonRemoteChapterEntry>,
): ChimahonReaderRequest {
    val queue = chapters.map(ChimahonRemoteChapterEntry::toReaderChapterRef)
    return ChimahonReaderRequest(
        sourceId = detail.sourceId,
        mangaTitle = detail.title,
        chapterName = name,
        chapterUrl = url,
        chapterNumber = chapterNumber,
        scanlator = scanlator,
        dateUpload = dateUpload,
        chapterQueue = queue,
        chapterIndex = chapters.indexOf(this),
    )
}

private fun ChimahonChapterEntry.toReaderRequest(
    manga: ChimahonMangaEntry,
    chapters: List<ChimahonChapterEntry>,
): ChimahonReaderRequest {
    val queue = chapters.map(ChimahonChapterEntry::toReaderChapterRef)
    return ChimahonReaderRequest(
        sourceId = manga.sourceId,
        mangaTitle = manga.title,
        chapterName = name,
        chapterUrl = url,
        chapterNumber = chapterNumber,
        scanlator = scanlator,
        dateUpload = dateUpload,
        mangaId = manga.id,
        chapterId = id,
        initialPage = lastPageRead.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
        chapterQueue = queue,
        chapterIndex = chapters.indexOf(this),
    )
}

private fun ChimahonRemoteChapterEntry.toReaderChapterRef(): ChimahonReaderChapterRef {
    return ChimahonReaderChapterRef(
        chapterId = null,
        chapterName = name,
        chapterUrl = url,
        chapterNumber = chapterNumber,
        scanlator = scanlator,
        dateUpload = dateUpload,
    )
}

private fun ChimahonChapterEntry.toReaderChapterRef(): ChimahonReaderChapterRef {
    return ChimahonReaderChapterRef(
        chapterId = id,
        chapterName = name,
        chapterUrl = url,
        chapterNumber = chapterNumber,
        scanlator = scanlator,
        dateUpload = dateUpload,
        initialPage = lastPageRead.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
        read = read,
        bookmarked = bookmarked,
        lastPageRead = lastPageRead,
    )
}

private fun ChimahonReaderChapterRef.toReaderRequest(
    current: ChimahonReaderRequest,
): ChimahonReaderRequest {
    return current.copy(
        chapterName = chapterName,
        chapterUrl = chapterUrl,
        chapterNumber = chapterNumber,
        scanlator = scanlator,
        dateUpload = dateUpload,
        chapterId = chapterId,
        initialPage = initialPage,
        chapterIndex = current.chapterQueue.indexOf(this),
    )
}

private fun Long.toDateBucket(prefix: String): String {
    if (this <= 0L) return "Unknown date"
    val day = this / 86_400L
    return "$prefix day $day"
}

private fun Double.toDisplayChapter(): String {
    return if (this.rem(1.0) == 0.0) {
        toLong().toString()
    } else {
        toString()
    }
}

private fun coverColor(seed: Long, title: String): Color {
    val colors = listOf(
        Color(0xFF6F5B8E),
        Color(0xFF3F6F73),
        Color(0xFF87654A),
        Color(0xFF526D43),
        Color(0xFF7B5260),
        Color(0xFF4F6387),
    )
    val index = ((seed xor title.hashCode().toLong()) and Long.MAX_VALUE).rem(colors.size).toInt()
    return colors[index]
}

private fun nativeCheckpointSnapshot(): ChimahonSnapshot = ChimahonSnapshot(
    summary = ChimahonSummary(
        extensionCount = 0,
        sourceCount = 0,
        mangaCount = 0,
    ),
    mangaDetails = emptyMap(),
    library = emptyList(),
    libraryCategories = emptyList(),
    libraryCategoryMemberships = emptyMap(),
    chaptersByMangaId = emptyMap(),
    updates = emptyList(),
    history = emptyList(),
    sources = emptyList(),
    extensionRepos = emptyList(),
    installedExtensions = emptyList(),
    updateIssues = emptyList(),
    runtime = ChimahonRuntimeInfo(
        platformName = "iOS",
        filesDir = "Native container",
        cacheDir = "Native cache",
        downloadsDir = "Native documents",
        databaseState = "Shared schema compiled",
        extensionState = "JavaScriptCore ready",
        backgroundState = "BGTaskScheduler bridge ready",
    ),
)

private enum class HomeTab(
    val title: String,
    val icon: UiIcon,
) {
    Library("Library", UiIcon.Library),
    Updates("Updates", UiIcon.Updates),
    History("History", UiIcon.History),
    Browse("Browse", UiIcon.Browse),
    More("More", UiIcon.More),
}

private enum class BrowseSection(val title: String) {
    Sources("Sources"),
    Feed("Feed"),
    Extensions("Extensions"),
    Migrate("Migrate"),
}

private data class HomeToolbarAction(
    val icon: UiIcon,
    val contentDescription: String,
    val active: Boolean = false,
    val onClick: () -> Unit,
)

private data class SelectionAction(
    val title: String,
    val icon: UiIcon,
    val onClick: () -> Unit,
)

private const val ALL_LIBRARY_CATEGORY_ID = -1L
private const val DEFAULT_LIBRARY_CATEGORY_ID = 0L
private const val CHIMAHON_PROJECT_URL = "https://github.com/sohilsayed/chimahon"

private enum class UiIcon {
    Library,
    Updates,
    History,
    Browse,
    More,
    Search,
    Filter,
    Refresh,
    Back,
    Forward,
    Play,
    DoneAll,
    Swap,
    Favorite,
    FavoriteBorder,
    Web,
    CheckCircle,
    Circle,
    Bookmark,
    BookmarkBorder,
    Download,
    Incognito,
    VisibilityOff,
    Tag,
    Statistics,
    Storage,
    Extensions,
    Settings,
    Info,
    Help,
    Star,
    Add,
    Reorder,
    Chapters,
    SkipPrevious,
    SkipNext,
    Delete,
    Crop,
    Link,
}

@Composable
private fun IconGlyph(
    icon: UiIcon,
    contentDescription: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = icon.imageVector,
        contentDescription = contentDescription.takeIf { it.isNotBlank() },
        tint = tint,
        modifier = modifier,
    )
}

private val UiIcon.imageVector: ImageVector
    get() = when (this) {
        UiIcon.Library -> Icons.Outlined.CollectionsBookmark
        UiIcon.Updates -> Icons.Outlined.NewReleases
        UiIcon.History -> Icons.Outlined.History
        UiIcon.Browse -> Icons.Outlined.Explore
        UiIcon.More -> Icons.Outlined.MoreHoriz
        UiIcon.Search -> Icons.Outlined.Search
        UiIcon.Filter -> Icons.Outlined.FilterList
        UiIcon.Refresh -> Icons.Outlined.Refresh
        UiIcon.Back -> Icons.Outlined.ArrowBack
        UiIcon.Forward -> Icons.Outlined.ArrowForward
        UiIcon.Play -> Icons.Outlined.PlayArrow
        UiIcon.DoneAll -> Icons.Outlined.DoneAll
        UiIcon.Swap -> Icons.Outlined.SwapHoriz
        UiIcon.Favorite -> Icons.Outlined.Favorite
        UiIcon.FavoriteBorder -> Icons.Outlined.FavoriteBorder
        UiIcon.Web -> Icons.Outlined.Public
        UiIcon.CheckCircle -> Icons.Outlined.CheckCircle
        UiIcon.Circle -> Icons.Outlined.RadioButtonUnchecked
        UiIcon.Bookmark -> Icons.Outlined.Bookmark
        UiIcon.BookmarkBorder -> Icons.Outlined.BookmarkBorder
        UiIcon.Download -> Icons.Outlined.Download
        UiIcon.Incognito -> Icons.Outlined.VisibilityOff
        UiIcon.VisibilityOff -> Icons.Outlined.VisibilityOff
        UiIcon.Tag -> Icons.Outlined.Label
        UiIcon.Statistics -> Icons.Outlined.QueryStats
        UiIcon.Storage -> Icons.Outlined.Storage
        UiIcon.Extensions -> Icons.Outlined.Extension
        UiIcon.Settings -> Icons.Outlined.Settings
        UiIcon.Info -> Icons.Outlined.Info
        UiIcon.Help -> Icons.Outlined.HelpOutline
        UiIcon.Star -> Icons.Outlined.Star
        UiIcon.Add -> Icons.Outlined.Add
        UiIcon.Reorder -> Icons.Outlined.Reorder
        UiIcon.Chapters -> Icons.Outlined.MenuBook
        UiIcon.SkipPrevious -> Icons.Outlined.SkipPrevious
        UiIcon.SkipNext -> Icons.Outlined.SkipNext
        UiIcon.Delete -> Icons.Outlined.DeleteOutline
        UiIcon.Crop -> Icons.Outlined.CropFree
        UiIcon.Link -> Icons.Outlined.Link
    }

@Suppress("unused")
@Composable
private fun LegacyIconGlyph(
    icon: UiIcon,
    contentDescription: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    val iconModifier = if (contentDescription.isBlank()) {
        modifier
    } else {
        modifier.semantics {
            this.contentDescription = contentDescription
        }
    }
    Canvas(modifier = iconModifier) {
        val w = size.width
        val h = size.height
        val side = min(w, h)
        val stroke = Stroke(
            width = side * 0.09f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )

        fun point(x: Float, y: Float) = Offset(w * x, h * y)
        fun line(x1: Float, y1: Float, x2: Float, y2: Float) {
            drawLine(tint, point(x1, y1), point(x2, y2), strokeWidth = stroke.width, cap = StrokeCap.Round)
        }

        fun heartPath(): Path {
            return Path().apply {
                moveTo(w * 0.5f, h * 0.82f)
                cubicTo(w * 0.18f, h * 0.58f, w * 0.14f, h * 0.32f, w * 0.31f, h * 0.24f)
                cubicTo(w * 0.42f, h * 0.19f, w * 0.49f, h * 0.27f, w * 0.5f, h * 0.34f)
                cubicTo(w * 0.51f, h * 0.27f, w * 0.58f, h * 0.19f, w * 0.69f, h * 0.24f)
                cubicTo(w * 0.86f, h * 0.32f, w * 0.82f, h * 0.58f, w * 0.5f, h * 0.82f)
                close()
            }
        }

        fun bookmarkPath(): Path {
            return Path().apply {
                moveTo(w * 0.30f, h * 0.18f)
                lineTo(w * 0.70f, h * 0.18f)
                lineTo(w * 0.70f, h * 0.82f)
                lineTo(w * 0.50f, h * 0.66f)
                lineTo(w * 0.30f, h * 0.82f)
                close()
            }
        }

        when (icon) {
            UiIcon.Library -> {
                drawRect(tint, topLeft = point(0.20f, 0.22f), size = Size(w * 0.46f, h * 0.58f), style = stroke)
                drawRect(tint, topLeft = point(0.34f, 0.18f), size = Size(w * 0.46f, h * 0.58f), style = stroke)
                line(0.42f, 0.34f, 0.70f, 0.34f)
                line(0.42f, 0.48f, 0.70f, 0.48f)
            }
            UiIcon.Updates,
            UiIcon.Refresh,
            -> {
                drawArc(
                    color = tint,
                    startAngle = 35f,
                    sweepAngle = 285f,
                    useCenter = false,
                    topLeft = point(0.18f, 0.18f),
                    size = Size(w * 0.64f, h * 0.64f),
                    style = stroke,
                )
                line(0.76f, 0.20f, 0.82f, 0.42f)
                line(0.76f, 0.20f, 0.56f, 0.24f)
            }
            UiIcon.History -> {
                drawCircle(tint, radius = side * 0.34f, center = point(0.50f, 0.50f), style = stroke)
                line(0.50f, 0.30f, 0.50f, 0.52f)
                line(0.50f, 0.52f, 0.66f, 0.62f)
            }
            UiIcon.Browse,
            UiIcon.Web,
            -> {
                drawCircle(tint, radius = side * 0.34f, center = point(0.50f, 0.50f), style = stroke)
                line(0.18f, 0.50f, 0.82f, 0.50f)
                line(0.50f, 0.18f, 0.50f, 0.82f)
                drawArc(
                    color = tint,
                    startAngle = 90f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = point(0.30f, 0.18f),
                    size = Size(w * 0.40f, h * 0.64f),
                    style = stroke,
                )
            }
            UiIcon.More -> {
                drawCircle(tint, radius = side * 0.07f, center = point(0.25f, 0.50f))
                drawCircle(tint, radius = side * 0.07f, center = point(0.50f, 0.50f))
                drawCircle(tint, radius = side * 0.07f, center = point(0.75f, 0.50f))
            }
            UiIcon.Search -> {
                drawCircle(tint, radius = side * 0.25f, center = point(0.43f, 0.43f), style = stroke)
                line(0.61f, 0.61f, 0.82f, 0.82f)
            }
            UiIcon.Filter -> {
                line(0.16f, 0.24f, 0.84f, 0.24f)
                line(0.28f, 0.50f, 0.72f, 0.50f)
                line(0.40f, 0.76f, 0.60f, 0.76f)
            }
            UiIcon.Back -> {
                line(0.64f, 0.18f, 0.34f, 0.50f)
                line(0.34f, 0.50f, 0.64f, 0.82f)
                line(0.36f, 0.50f, 0.82f, 0.50f)
            }
            UiIcon.Forward -> {
                line(0.36f, 0.18f, 0.66f, 0.50f)
                line(0.66f, 0.50f, 0.36f, 0.82f)
                line(0.18f, 0.50f, 0.64f, 0.50f)
            }
            UiIcon.Play -> {
                val path = Path().apply {
                    moveTo(w * 0.34f, h * 0.22f)
                    lineTo(w * 0.78f, h * 0.50f)
                    lineTo(w * 0.34f, h * 0.78f)
                    close()
                }
                drawPath(path, tint)
            }
            UiIcon.DoneAll -> {
                line(0.14f, 0.56f, 0.28f, 0.70f)
                line(0.28f, 0.70f, 0.56f, 0.34f)
                line(0.42f, 0.64f, 0.52f, 0.74f)
                line(0.52f, 0.74f, 0.86f, 0.30f)
            }
            UiIcon.Swap -> {
                line(0.18f, 0.34f, 0.78f, 0.34f)
                line(0.66f, 0.22f, 0.78f, 0.34f)
                line(0.66f, 0.46f, 0.78f, 0.34f)
                line(0.82f, 0.66f, 0.22f, 0.66f)
                line(0.34f, 0.54f, 0.22f, 0.66f)
                line(0.34f, 0.78f, 0.22f, 0.66f)
            }
            UiIcon.Favorite -> drawPath(heartPath(), tint)
            UiIcon.FavoriteBorder -> drawPath(heartPath(), tint, style = stroke)
            UiIcon.CheckCircle -> {
                drawCircle(tint, radius = side * 0.34f, center = point(0.50f, 0.50f), style = stroke)
                line(0.32f, 0.52f, 0.46f, 0.66f)
                line(0.46f, 0.66f, 0.70f, 0.36f)
            }
            UiIcon.Circle -> drawCircle(tint, radius = side * 0.34f, center = point(0.50f, 0.50f), style = stroke)
            UiIcon.Bookmark -> drawPath(bookmarkPath(), tint)
            UiIcon.BookmarkBorder -> drawPath(bookmarkPath(), tint, style = stroke)
            UiIcon.Download -> {
                line(0.50f, 0.18f, 0.50f, 0.62f)
                line(0.34f, 0.48f, 0.50f, 0.64f)
                line(0.66f, 0.48f, 0.50f, 0.64f)
                line(0.24f, 0.78f, 0.76f, 0.78f)
            }
            UiIcon.Crop -> {
                line(0.24f, 0.12f, 0.24f, 0.76f)
                line(0.24f, 0.76f, 0.88f, 0.76f)
                line(0.12f, 0.24f, 0.76f, 0.24f)
                line(0.76f, 0.24f, 0.76f, 0.88f)
            }
            UiIcon.Link -> {
                drawArc(tint, 130f, 250f, false, point(0.16f, 0.28f), Size(w * 0.38f, h * 0.38f), style = stroke)
                drawArc(tint, -50f, 250f, false, point(0.46f, 0.34f), Size(w * 0.38f, h * 0.38f), style = stroke)
                line(0.40f, 0.57f, 0.60f, 0.43f)
            }
            UiIcon.Incognito,
            UiIcon.VisibilityOff,
            -> {
                line(0.18f, 0.42f, 0.82f, 0.42f)
                line(0.32f, 0.42f, 0.38f, 0.28f)
                line(0.68f, 0.42f, 0.62f, 0.28f)
                drawCircle(tint, radius = side * 0.16f, center = point(0.34f, 0.62f), style = stroke)
                drawCircle(tint, radius = side * 0.16f, center = point(0.66f, 0.62f), style = stroke)
                line(0.50f, 0.62f, 0.50f, 0.62f)
            }
            UiIcon.Tag -> {
                val path = Path().apply {
                    moveTo(w * 0.18f, h * 0.28f)
                    lineTo(w * 0.58f, h * 0.28f)
                    lineTo(w * 0.82f, h * 0.50f)
                    lineTo(w * 0.58f, h * 0.72f)
                    lineTo(w * 0.18f, h * 0.72f)
                    close()
                }
                drawPath(path, tint, style = stroke)
                drawCircle(tint, radius = side * 0.05f, center = point(0.32f, 0.50f))
            }
            UiIcon.Statistics -> {
                drawRect(tint, topLeft = point(0.18f, 0.58f), size = Size(w * 0.14f, h * 0.24f))
                drawRect(tint, topLeft = point(0.43f, 0.38f), size = Size(w * 0.14f, h * 0.44f))
                drawRect(tint, topLeft = point(0.68f, 0.20f), size = Size(w * 0.14f, h * 0.62f))
            }
            UiIcon.Storage -> {
                drawOval(tint, topLeft = point(0.20f, 0.18f), size = Size(w * 0.60f, h * 0.22f), style = stroke)
                drawArc(tint, 0f, 180f, false, point(0.20f, 0.38f), Size(w * 0.60f, h * 0.22f), style = stroke)
                drawArc(tint, 0f, 180f, false, point(0.20f, 0.60f), Size(w * 0.60f, h * 0.22f), style = stroke)
                line(0.20f, 0.29f, 0.20f, 0.71f)
                line(0.80f, 0.29f, 0.80f, 0.71f)
            }
            UiIcon.Extensions -> {
                drawRect(tint, topLeft = point(0.20f, 0.20f), size = Size(w * 0.25f, h * 0.25f), style = stroke)
                drawRect(tint, topLeft = point(0.55f, 0.20f), size = Size(w * 0.25f, h * 0.25f), style = stroke)
                drawRect(tint, topLeft = point(0.20f, 0.55f), size = Size(w * 0.25f, h * 0.25f), style = stroke)
                drawRect(tint, topLeft = point(0.55f, 0.55f), size = Size(w * 0.25f, h * 0.25f), style = stroke)
            }
            UiIcon.Settings -> {
                drawCircle(tint, radius = side * 0.16f, center = point(0.50f, 0.50f), style = stroke)
                drawCircle(tint, radius = side * 0.34f, center = point(0.50f, 0.50f), style = stroke)
                line(0.50f, 0.08f, 0.50f, 0.20f)
                line(0.50f, 0.80f, 0.50f, 0.92f)
                line(0.08f, 0.50f, 0.20f, 0.50f)
                line(0.80f, 0.50f, 0.92f, 0.50f)
            }
            UiIcon.Info -> {
                drawCircle(tint, radius = side * 0.34f, center = point(0.50f, 0.50f), style = stroke)
                drawCircle(tint, radius = side * 0.045f, center = point(0.50f, 0.32f))
                line(0.50f, 0.46f, 0.50f, 0.70f)
            }
            UiIcon.Help -> {
                drawCircle(tint, radius = side * 0.34f, center = point(0.50f, 0.50f), style = stroke)
                drawArc(tint, 190f, 215f, false, point(0.34f, 0.25f), Size(w * 0.32f, h * 0.32f), style = stroke)
                line(0.50f, 0.54f, 0.50f, 0.62f)
                drawCircle(tint, radius = side * 0.04f, center = point(0.50f, 0.73f))
            }
            UiIcon.Star -> {
                val path = Path().apply {
                    moveTo(w * 0.50f, h * 0.12f)
                    lineTo(w * 0.61f, h * 0.38f)
                    lineTo(w * 0.88f, h * 0.40f)
                    lineTo(w * 0.67f, h * 0.58f)
                    lineTo(w * 0.74f, h * 0.86f)
                    lineTo(w * 0.50f, h * 0.70f)
                    lineTo(w * 0.26f, h * 0.86f)
                    lineTo(w * 0.33f, h * 0.58f)
                    lineTo(w * 0.12f, h * 0.40f)
                    lineTo(w * 0.39f, h * 0.38f)
                    close()
                }
                drawPath(path, tint, style = stroke)
            }
            UiIcon.Add -> {
                line(0.50f, 0.18f, 0.50f, 0.82f)
                line(0.18f, 0.50f, 0.82f, 0.50f)
            }
            UiIcon.Reorder -> {
                line(0.28f, 0.28f, 0.82f, 0.28f)
                line(0.28f, 0.50f, 0.82f, 0.50f)
                line(0.28f, 0.72f, 0.82f, 0.72f)
                drawCircle(tint, radius = side * 0.045f, center = point(0.14f, 0.28f))
                drawCircle(tint, radius = side * 0.045f, center = point(0.14f, 0.50f))
                drawCircle(tint, radius = side * 0.045f, center = point(0.14f, 0.72f))
            }
            UiIcon.Chapters -> {
                drawRect(tint, topLeft = point(0.18f, 0.18f), size = Size(w * 0.26f, h * 0.64f), style = stroke)
                drawRect(tint, topLeft = point(0.56f, 0.18f), size = Size(w * 0.26f, h * 0.64f), style = stroke)
                line(0.27f, 0.34f, 0.35f, 0.34f)
                line(0.65f, 0.34f, 0.73f, 0.34f)
            }
            UiIcon.SkipPrevious -> {
                line(0.24f, 0.22f, 0.24f, 0.78f)
                line(0.72f, 0.22f, 0.38f, 0.50f)
                line(0.38f, 0.50f, 0.72f, 0.78f)
            }
            UiIcon.SkipNext -> {
                line(0.76f, 0.22f, 0.76f, 0.78f)
                line(0.28f, 0.22f, 0.62f, 0.50f)
                line(0.62f, 0.50f, 0.28f, 0.78f)
            }
            UiIcon.Delete -> {
                line(0.26f, 0.30f, 0.74f, 0.30f)
                line(0.40f, 0.18f, 0.60f, 0.18f)
                line(0.34f, 0.30f, 0.38f, 0.82f)
                line(0.66f, 0.30f, 0.62f, 0.82f)
                line(0.38f, 0.82f, 0.62f, 0.82f)
                line(0.46f, 0.42f, 0.46f, 0.70f)
                line(0.54f, 0.42f, 0.54f, 0.70f)
            }
        }
    }
}

private data class ChimahonThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val divider: Color,
    val onSurface: Color,
    val secondaryText: Color,
    val primary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val error: Color,
    val errorContainer: Color,
)

private data class ChimahonThemeSeed(
    val lightPrimary: Color,
    val lightBackground: Color,
    val lightSurface: Color,
    val lightVariant: Color,
    val lightOnSurface: Color,
    val darkPrimary: Color,
    val darkBackground: Color,
    val darkSurface: Color,
    val darkVariant: Color,
    val darkOnSurface: Color,
)

private fun chimahonThemeColors(
    theme: ChimahonAppTheme,
    dark: Boolean,
    amoled: Boolean,
): ChimahonThemeColors {
    val seed = when (theme) {
        ChimahonAppTheme.Default -> ChimahonThemeSeed(
            Color(0xFF0058CA), Color(0xFFFEFBFF), Color(0xFFFEFBFF), Color(0xFFF3EDF7), Color(0xFF1B1B1F),
            Color(0xFFB0C6FF), Color(0xFF1B1B1F), Color(0xFF1B1B1F), Color(0xFF211F26), Color(0xFFE3E2E6),
        )
        ChimahonAppTheme.Catppuccin -> ChimahonThemeSeed(
            Color(0xFF8839EF), Color(0xFFE6E9EF), Color(0xFFE6E9EF), Color(0xFFEFF1F5), Color(0xFF4C4F69),
            Color(0xFFCBA6F7), Color(0xFF181825), Color(0xFF181825), Color(0xFF1E1E2E), Color(0xFFCDD6F4),
        )
        ChimahonAppTheme.Cloudflare -> ChimahonThemeSeed(
            Color(0xFFF38020), Color(0xFFEFF2F5), Color(0xFFEFF2F5), Color(0xFFDDD8E7), Color(0xFF1B1B22),
            Color(0xFFF38020), Color(0xFF1B1B22), Color(0xFF1B1B22), Color(0xFF3F3F46), Color(0xFFEFF2F5),
        )
        ChimahonAppTheme.CottonCandy -> ChimahonThemeSeed(
            Color(0xFF8F4A4C), Color(0xFFFFF8F7), Color(0xFFFFF8F7), Color(0xFFF4DDDD), Color(0xFF22191A),
            Color(0xFFFFB3B4), Color(0xFF1A1111), Color(0xFF1A1112), Color(0xFF524343), Color(0xFFF0DEDF),
        )
        ChimahonAppTheme.Doom -> ChimahonThemeSeed(
            Color(0xFFFF0000), Color(0xFF212121), Color(0xFF212121), Color(0xFF4D4D4D), Color.White,
            Color(0xFFFF0000), Color(0xFF1B1B1B), Color(0xFF1B1B1B), Color(0xFF303030), Color.White,
        )
        ChimahonAppTheme.GreenApple -> ChimahonThemeSeed(
            Color(0xFF005927), Color(0xFFF6FBF2), Color(0xFFF6FBF2), Color(0xFFDAE6D7), Color(0xFF181D18),
            Color(0xFF7ADB8F), Color(0xFF0F1510), Color(0xFF0F1510), Color(0xFF3F493F), Color(0xFFDFE4DB),
        )
        ChimahonAppTheme.Lavender -> ChimahonThemeSeed(
            Color(0xFF6D41C8), Color(0xFFEDE2FF), Color(0xFFEDE2FF), Color(0xFFE4D5F8), Color(0xFF1D1A22),
            Color(0xFFA177FF), Color(0xFF111129), Color(0xFF111129), Color(0xFF3D2F6B), Color(0xFFE7E0EC),
        )
        ChimahonAppTheme.Matrix -> ChimahonThemeSeed(
            Color(0xFF00C000), Color.Black, Color.Black, Color(0xFF111111), Color.White,
            Color(0xFF00FF00), Color(0xFF111111), Color(0xFF111111), Color(0xFF212121), Color.White,
        )
        ChimahonAppTheme.MidnightDusk -> ChimahonThemeSeed(
            Color(0xFFBB0054), Color(0xFFFFFBFF), Color(0xFFFFFBFF), Color(0xFFF9E6F1), Color(0xFF1C1B1F),
            Color(0xFFF02475), Color(0xFF16151D), Color(0xFF16151D), Color(0xFF281624), Color(0xFFE5E1E5),
        )
        ChimahonAppTheme.Mocha -> ChimahonThemeSeed(
            Color(0xFF89511F), Color(0xFFFFF8F5), Color(0xFFFFF8F5), Color(0xFFF4DED3), Color(0xFF221A14),
            Color(0xFFFFB77F), Color(0xFF19120C), Color(0xFF19120D), Color(0xFF52443C), Color(0xFFF0DFD6),
        )
        ChimahonAppTheme.Monochrome -> ChimahonThemeSeed(
            Color.Black, Color.White, Color.White, Color(0xFFE8E8E8), Color.Black,
            Color.White, Color.Black, Color.Black, Color(0xFF202020), Color.White,
        )
        ChimahonAppTheme.Nord -> ChimahonThemeSeed(
            Color(0xFF5E81AC), Color(0xFFECEFF4), Color(0xFFE5E9F0), Color(0xFFDAE0EA), Color(0xFF2E3440),
            Color(0xFF88C0D0), Color(0xFF2E3440), Color(0xFF2E3440), Color(0xFF414C5C), Color(0xFFECEFF4),
        )
        ChimahonAppTheme.Sapphire -> ChimahonThemeSeed(
            Color(0xFF1E88E5), Color.White, Color.White, Color(0xFFB3E5FC), Color(0xFF212121),
            Color(0xFF1E88E5), Color(0xFF212121), Color(0xFF212121), Color(0xFF424242), Color.White,
        )
        ChimahonAppTheme.StrawberryDaiquiri -> ChimahonThemeSeed(
            Color(0xFFA10833), Color(0xFFFAFAFA), Color(0xFFFAFAFA), Color(0xFFF6EAED), Color(0xFF261819),
            Color(0xFFFFB2B8), Color(0xFF201A1A), Color(0xFF201A1A), Color(0xFF322727), Color(0xFFF7DCDD),
        )
        ChimahonAppTheme.Tako -> ChimahonThemeSeed(
            Color(0xFF66577E), Color(0xFFF7F5FF), Color(0xFFF7F5FF), Color(0xFFE8E0EB), Color(0xFF1B1B22),
            Color(0xFFF3B375), Color(0xFF21212E), Color(0xFF21212E), Color(0xFF2A2A3C), Color(0xFFE3E0F2),
        )
        ChimahonAppTheme.TealTurquoise -> ChimahonThemeSeed(
            Color(0xFF008080), Color(0xFFFAFAFA), Color(0xFFFAFAFA), Color(0xFFEBF3F1), Color(0xFF050505),
            Color(0xFF40E0D0), Color(0xFF202125), Color(0xFF202125), Color(0xFF233133), Color(0xFFDFDEDA),
        )
        ChimahonAppTheme.TidalWave -> ChimahonThemeSeed(
            Color(0xFF006780), Color(0xFFFDFBFF), Color(0xFFFDFBFF), Color(0xFFE8EFF5), Color(0xFF001C3B),
            Color(0xFF5ED4FC), Color(0xFF001C3B), Color(0xFF001C3B), Color(0xFF082B4B), Color(0xFFD5E3FF),
        )
        ChimahonAppTheme.YinYang -> ChimahonThemeSeed(
            Color.Black, Color(0xFFFDFDFD), Color(0xFFFDFDFD), Color(0xFFE8E8E8), Color(0xFF222222),
            Color.White, Color(0xFF1E1E1E), Color(0xFF1E1E1E), Color(0xFF313131), Color(0xFFE6E6E6),
        )
        ChimahonAppTheme.Yotsuba -> ChimahonThemeSeed(
            Color(0xFFAE3200), Color(0xFFFCFCFC), Color(0xFFFCFCFC), Color(0xFFF6EBE7), Color(0xFF211A18),
            Color(0xFFFFB59D), Color(0xFF211A18), Color(0xFF211A18), Color(0xFF332723), Color(0xFFEDE0DD),
        )
    }
    val onSurface = if (dark) seed.darkOnSurface else seed.lightOnSurface
    val primary = if (dark) seed.darkPrimary else seed.lightPrimary
    val baseBackground = if (dark) seed.darkBackground else seed.lightBackground
    val baseSurface = if (dark) seed.darkSurface else seed.lightSurface
    val surfaceVariant = if (dark) seed.darkVariant else seed.lightVariant
    val background = if (dark && amoled) Color.Black else baseBackground
    val surface = if (dark && amoled) Color.Black else baseSurface
    return ChimahonThemeColors(
        background = background,
        surface = surface,
        surfaceVariant = surfaceVariant,
        divider = onSurface.copy(alpha = if (dark) 0.22f else 0.16f),
        onSurface = onSurface,
        secondaryText = onSurface.copy(alpha = 0.72f),
        primary = primary,
        primaryContainer = surfaceVariant,
        onPrimaryContainer = onSurface,
        error = if (dark) Color(0xFFFFB4AB) else Color(0xFFBA1A1A),
        errorContainer = if (dark) Color(0xFF93000A) else Color(0xFFFFDAD6),
    )
}

private object ChimahonPalette {
    var background = Color(0xFFFEFBFF)
        private set
    var surface = Color(0xFFFEFBFF)
        private set
    var surfaceVariant = Color(0xFFF3EDF7)
        private set
    var divider = Color(0xFFC5C6D0)
        private set
    var onSurface = Color(0xFF1B1B1F)
        private set
    var secondaryText = Color(0xFF44464F)
        private set
    var primary = Color(0xFF0058CA)
        private set
    var primaryContainer = Color(0xFFD9E2FF)
        private set
    var onPrimaryContainer = Color(0xFF001945)
        private set
    var error = Color(0xFFBA1A1A)
        private set
    var errorContainer = Color(0xFFFFDAD6)
        private set
    var fontScale = 1f
        private set

    fun apply(
        theme: ChimahonAppTheme,
        colorTheme: ChimahonColorTheme,
        dark: Boolean,
        amoled: Boolean,
        fontScalePercent: Int,
    ) {
        val colors = chimahonThemeColors(theme, dark, amoled)
        background = colors.background
        surface = colors.surface
        surfaceVariant = colors.surfaceVariant
        divider = colors.divider
        onSurface = colors.onSurface
        secondaryText = colors.secondaryText
        primary = colorTheme.primaryOverride(dark) ?: colors.primary
        primaryContainer = colors.primaryContainer
        onPrimaryContainer = colors.onPrimaryContainer
        error = colors.error
        errorContainer = colors.errorContainer
        fontScale = (fontScalePercent.coerceIn(80, 130) / 100f)
    }
}

private fun ChimahonColorTheme.primaryOverride(dark: Boolean): Color? = when (this) {
    ChimahonColorTheme.Default,
    ChimahonColorTheme.Dynamic,
    -> null
    ChimahonColorTheme.Blue -> if (dark) Color(0xFF9BCBFF) else Color(0xFF0061A4)
    ChimahonColorTheme.Green -> if (dark) Color(0xFF7DDB91) else Color(0xFF006D3B)
    ChimahonColorTheme.Orange -> if (dark) Color(0xFFFFB86C) else Color(0xFF9A4F00)
    ChimahonColorTheme.Pink -> if (dark) Color(0xFFFFB1C8) else Color(0xFFB71858)
    ChimahonColorTheme.Purple -> if (dark) Color(0xFFD0BCFF) else Color(0xFF6750A4)
    ChimahonColorTheme.Red -> if (dark) Color(0xFFFFB4AB) else Color(0xFFBA1A1A)
    ChimahonColorTheme.Teal -> if (dark) Color(0xFF80D8CF) else Color(0xFF006A60)
}

private object ReaderPalette {
    val background = Color(0xFF0D0D0F)
    val chrome = Color(0xFF17171B)
    val control = Color(0xFF29292F)
    val selectedControl = Color(0xFF5C5A86)
    val divider = Color(0xFF34343B)
    val secondaryText = Color(0xFFB9B6C0)
}

private fun readerHudBackground(canvas: ReaderCanvas): Color {
    return when (canvas) {
        ReaderCanvas.White -> Color(0xFFFFFFFF).copy(alpha = 0.90f)
        ReaderCanvas.Gray -> Color(0xFF27272B).copy(alpha = 0.92f)
        ReaderCanvas.Black -> ReaderPalette.chrome.copy(alpha = 0.94f)
    }
}

private fun ReaderCanvas.readerBackground(pureBlack: Boolean): Color {
    return when (this) {
        ReaderCanvas.Black -> if (pureBlack) Color.Black else ReaderPalette.background
        ReaderCanvas.Gray,
        ReaderCanvas.White,
        -> color
    }
}

private fun readerHudContent(canvas: ReaderCanvas): Color {
    return when (canvas) {
        ReaderCanvas.White -> Color(0xFF242329)
        ReaderCanvas.Gray,
        ReaderCanvas.Black,
        -> Color.White
    }
}

private fun readerProgressText(currentPage: Int, pageCount: Int): String {
    if (pageCount <= 0) return "0%"
    val percent = ((currentPage.coerceIn(0, pageCount).toFloat() / pageCount.toFloat()) * 100f).toInt()
    return "$percent%"
}
