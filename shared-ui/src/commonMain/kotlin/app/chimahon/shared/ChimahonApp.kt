package app.chimahon.shared

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
}

private enum class ReaderCanvas(val title: String, val color: Color) {
    Black("Black", Color.Black),
    Gray("Gray", Color(0xFF242424)),
    White("White", Color(0xFFF4F4F4)),
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
        onOpenRemoteMangaUrl = services::openRemoteMangaUrl,
        onSaveReaderProgress = services::saveReaderProgress,
        onAddRemoteMangaToLibrary = services::addRemoteMangaToLibrary,
        onRefreshManga = services::refreshManga,
        onCreateCategory = services::createCategory,
        onDeleteCategory = services::deleteCategory,
        onSetMangaCategories = services::setMangaCategories,
        onSetMangaFavorite = services::setMangaFavorite,
        onSetMangaNotes = services::setMangaNotes,
        onResetHistoryEntry = services::resetHistoryEntry,
        onResetHistoryForManga = services::resetHistoryForManga,
        onClearHistory = services::clearHistory,
        onDismissUpdateIssue = services::dismissUpdateIssue,
        onClearUpdateIssues = services::clearUpdateIssues,
        onSetMangaChaptersRead = services::setMangaChaptersRead,
        onSetChapterRead = services::setChapterRead,
        onSetChapterBookmark = services::setChapterBookmark,
        onAddExtensionRepo = services::addExtensionRepo,
        onLoadExtensionRepoCatalog = services::loadExtensionRepoCatalog,
        onInstallExtension = services::installExtension,
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
    onSetMangaNotes: suspend (Long, String) -> Unit = { _, _ -> },
    onResetHistoryEntry: suspend (Long) -> Unit = {},
    onResetHistoryForManga: suspend (Long) -> Unit = {},
    onClearHistory: suspend () -> Unit = {},
    onDismissUpdateIssue: suspend (Long) -> Unit = {},
    onClearUpdateIssues: suspend () -> Unit = {},
    onSetMangaChaptersRead: suspend (Long, Boolean) -> Unit = { _, _ -> },
    onSetChapterRead: suspend (Long, Boolean) -> Unit = { _, _ -> },
    onSetChapterBookmark: suspend (Long, Boolean) -> Unit = { _, _ -> },
    onAddExtensionRepo: suspend (String) -> ChimahonExtensionRepoEntry = { input ->
        ChimahonExtensionRepoEntry(
            baseUrl = input,
            name = input,
            shortName = null,
            website = input,
            signingKeyFingerprint = "preview",
        )
    },
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
    var downloadedOnlyMode by remember { mutableStateOf(false) }
    var incognitoMode by remember { mutableStateOf(false) }
    val appScope = rememberCoroutineScope()

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
                        ReaderTopBar(
                            request = topBarReader,
                            onBack = {
                                selectedReader = null
                                onRefresh()
                            },
                        )
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
                                        onSaveReaderProgress = { request, page, completed ->
                                            if (!incognitoMode) {
                                                onSaveReaderProgress(request, page, completed)
                                            }
                                        },
                                        onSetChapterBookmark = onSetChapterBookmark,
                                        onOpenReader = { selectedReader = it },
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
                                        onCreateCategory = onCreateCategory,
                                        onDeleteCategory = onDeleteCategory,
                                        onSetMangaCategories = onSetMangaCategories,
                                        onResetHistoryEntry = onResetHistoryEntry,
                                        onResetHistoryForManga = onResetHistoryForManga,
                                        onClearHistory = onClearHistory,
                                        onDismissUpdateIssue = onDismissUpdateIssue,
                                        onClearUpdateIssues = onClearUpdateIssues,
                                        onSetChapterRead = onSetChapterRead,
                                        onSetChapterBookmark = onSetChapterBookmark,
                                        onAddExtensionRepo = onAddExtensionRepo,
                                        onLoadExtensionRepoCatalog = onLoadExtensionRepoCatalog,
                                        onInstallExtension = onInstallExtension,
                                        onRepoSaved = onRefresh,
                                        downloadedOnlyMode = downloadedOnlyMode,
                                        incognitoMode = incognitoMode,
                                        onDownloadedOnlyModeChange = { downloadedOnlyMode = it },
                                        onIncognitoModeChange = { incognitoMode = it },
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
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(ReaderPalette.chrome)
            .border(1.dp, ReaderPalette.divider)
            .padding(start = 8.dp, end = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ReaderAction(
            icon = UiIcon.Back,
            contentDescription = "Back",
            onClick = onBack,
        )
        Column(modifier = Modifier.weight(1f)) {
            Label(
                text = request.mangaTitle,
                color = Color.White,
                size = 16,
                weight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Label(
                text = request.chapterName,
                color = ReaderPalette.secondaryText,
                size = 11,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun HomeNavigationRail(
    selected: HomeTab,
    state: ChimahonUiState,
    onSelect: (HomeTab) -> Unit,
) {
    Column(
        modifier = Modifier
            .width(92.dp)
            .fillMaxHeight()
            .background(ChimahonPalette.surface)
            .border(1.dp, ChimahonPalette.divider)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppMark()
        Spacer(Modifier.height(18.dp))
        HomeTab.entries.forEach { tab ->
            RailItem(
                tab = tab,
                selected = tab == selected,
                badge = state.badgeFor(tab),
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
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(76.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
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
            size = 11,
            maxLines = 1,
            modifier = Modifier.padding(top = 5.dp),
        )
    }
}

@Composable
private fun HomeNavigationBar(
    selected: HomeTab,
    state: ChimahonUiState,
    onSelect: (HomeTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
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
                    .padding(vertical = 5.dp),
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
                    size = 10,
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
        modifier = Modifier
            .width(44.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) ChimahonPalette.primaryContainer else Color.Transparent),
        contentAlignment = Alignment.Center,
    ) {
        IconGlyph(
            icon = icon,
            contentDescription = contentDescription,
            tint = if (selected) ChimahonPalette.primary else ChimahonPalette.secondaryText,
            modifier = Modifier.size(20.dp),
        )
        if (badge != null && badge > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(15.dp)
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
    onCreateCategory: suspend (String) -> Long,
    onDeleteCategory: suspend (Long) -> Unit,
    onSetMangaCategories: suspend (Long, Set<Long>) -> Unit,
    onResetHistoryEntry: suspend (Long) -> Unit,
    onResetHistoryForManga: suspend (Long) -> Unit,
    onClearHistory: suspend () -> Unit,
    onDismissUpdateIssue: suspend (Long) -> Unit,
    onClearUpdateIssues: suspend () -> Unit,
    onSetChapterRead: suspend (Long, Boolean) -> Unit,
    onSetChapterBookmark: suspend (Long, Boolean) -> Unit,
    onAddExtensionRepo: suspend (String) -> ChimahonExtensionRepoEntry,
    onLoadExtensionRepoCatalog: suspend (ChimahonExtensionRepoEntry) -> ChimahonExtensionRepoCatalog,
    onInstallExtension: suspend (ChimahonRepoExtensionEntry) -> ChimahonInstalledExtensionEntry,
    onRepoSaved: () -> Unit,
    downloadedOnlyMode: Boolean,
    incognitoMode: Boolean,
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
            onAddExtensionRepo = onAddExtensionRepo,
            onLoadExtensionRepoCatalog = onLoadExtensionRepoCatalog,
            onInstallExtension = onInstallExtension,
            onRepoSaved = onRepoSaved,
        )
        HomeTab.More -> MoreHome(
            snapshot = snapshot,
            onSelectTab = onSelectTab,
            onCreateCategory = onCreateCategory,
            onDeleteCategory = onDeleteCategory,
            onCategoriesChanged = onRepoSaved,
            downloadedOnlyMode = downloadedOnlyMode,
            incognitoMode = incognitoMode,
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
    var selectedFilter by remember { mutableStateOf(LibraryFilter.All) }
    var selectedSort by remember { mutableStateOf(LibrarySort.Alphabetical) }
    var displayMode by remember { mutableStateOf(LibraryDisplayMode.ComfortableGrid) }
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
            when (selectedFilter) {
                LibraryFilter.All -> true
                LibraryFilter.Unread -> chapters.any { !it.read }
                LibraryFilter.Started -> chapters.any { it.lastPageRead > 0L && !it.read }
                LibraryFilter.Bookmarked -> chapters.any { it.bookmarked }
            }
        }
        .let { entries ->
            when (selectedSort) {
                LibrarySort.Alphabetical -> entries.sortedBy { it.title.lowercase() }
                LibrarySort.DateAdded -> entries.sortedByDescending { it.dateAdded }
                LibrarySort.LastUpdated -> entries.sortedByDescending { it.lastUpdate ?: 0L }
                LibrarySort.UnreadCount -> entries.sortedByDescending { entry ->
                    snapshot.chaptersByMangaId[entry.id].orEmpty().count { !it.read }
                }
            }
        }
    val categoryCounts = categories.associate { category ->
        category.id to snapshot.libraryForCategory(category.id).size
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LibraryCategoryTabs(
            categories = categories,
            selectedCategoryId = selectedCategory.id,
            categoryCounts = categoryCounts,
            onSelectCategory = { selectedCategoryId = it },
        )
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
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                )
            }
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
                        unreadCount = chapters.count { !it.read },
                        onClick = { onOpenManga(entry.id) },
                        onContinue = continueChapter?.let { chapter ->
                            { onOpenReader(chapter.toReaderRequest(entry, chapters)) }
                        },
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(
                    if (displayMode == LibraryDisplayMode.CompactGrid) 92.dp else 124.dp,
                ),
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
                        unreadCount = chapters.count { !it.read },
                        onClick = { onOpenManga(entry.id) },
                        onContinue = continueChapter?.let { chapter ->
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
            showLibraryBadge = false,
            bottomEndAction = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.68f),
        )
        Label(
            text = entry.title,
            color = ChimahonPalette.onSurface,
            size = 13,
            weight = FontWeight.SemiBold,
            maxLines = 2,
            modifier = Modifier.padding(top = 7.dp),
        )
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
    onAddExtensionRepo: suspend (String) -> ChimahonExtensionRepoEntry,
    onLoadExtensionRepoCatalog: suspend (ChimahonExtensionRepoEntry) -> ChimahonExtensionRepoCatalog,
    onInstallExtension: suspend (ChimahonRepoExtensionEntry) -> ChimahonInstalledExtensionEntry,
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
                repoInputRequestKey = extensionRepoRequestKey,
                onAddExtensionRepo = onAddExtensionRepo,
                onLoadExtensionRepoCatalog = onLoadExtensionRepoCatalog,
                onInstallExtension = onInstallExtension,
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
    onOpenSource: (Long, ChimahonSourceBrowseMode) -> Unit,
    onInstallExtension: () -> Unit,
) {
    val filteredSources = sources.filter {
        query.isBlank() ||
            it.name.contains(query, ignoreCase = true) ||
            it.language.contains(query, ignoreCase = true)
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
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        filteredSources.groupBy { it.language.ifBlank { "multi" }.uppercase() }.forEach { (language, group) ->
            item {
                Label(
                    text = language,
                    color = ChimahonPalette.secondaryText,
                    size = 12,
                    weight = FontWeight.SemiBold,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ChimahonPalette.background)
                        .padding(horizontal = 16.dp, vertical = 9.dp),
                )
            }
            items(group, key = { it.id }) { source ->
                SourceListItem(
                    source = source,
                    onClick = { onOpenSource(source.id, ChimahonSourceBrowseMode.Popular) },
                    onClickLatest = { onOpenSource(source.id, ChimahonSourceBrowseMode.Latest) },
                )
            }
        }
    }
}

@Composable
private fun SourceListItem(
    source: ChimahonSourceEntry,
    onClick: () -> Unit,
    onClickLatest: () -> Unit,
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
                .size(42.dp)
                .clip(CircleShape)
                .background(coverColor(source.id, source.name).copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Label(
                source.name.firstOrNull()?.uppercase() ?: "S",
                coverColor(source.id, source.name),
                15,
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
private fun SourceDetailHome(
    snapshot: ChimahonSnapshot,
    sourceId: Long,
    initialMode: ChimahonSourceBrowseMode,
    searchKey: Int,
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
    var loadingMore by remember(sourceId, selectedMode, submittedQuery) { mutableStateOf(false) }
    var previewState by remember(sourceId, selectedMode, submittedQuery) {
        mutableStateOf<SourcePreviewUiState>(SourcePreviewUiState.Loading)
    }

    LaunchedEffect(sourceId, searchKey) {
        if (searchKey != handledSearchKey) {
            handledSearchKey = searchKey
            selectedMode = ChimahonSourceBrowseMode.Search
        }
    }

    LaunchedEffect(sourceId, selectedMode, submittedQuery, pageNumber) {
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
                    )
                }
                is SourcePreviewUiState.Ready -> {
                    if (preview.preview.entries.isEmpty()) {
                        EmptyMobileState(
                            marker = "S",
                            title = "No entries returned",
                            detail = if (preview.preview.mode == ChimahonSourceBrowseMode.Search) {
                                "No results matched \"$submittedQuery\"."
                            } else {
                                "${source.name} returned an empty ${preview.preview.mode.title.lowercase()} page."
                            },
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(124.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                SourcePreviewHeader(preview.preview)
                            }
                            items(preview.preview.entries, key = { "${it.sourceId}:${it.url}" }) { entry ->
                                RemoteMangaGridCard(
                                    entry = entry,
                                    inLibrary = snapshot.mangaDetails.values.any {
                                        it.sourceId == entry.sourceId && it.url == entry.url
                                    },
                                    onClick = { onOpenRemoteManga(entry) },
                                )
                            }
                            if (preview.preview.hasNextPage) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        TextButtonLike(
                                            text = if (loadingMore) "Loading" else "Load more",
                                            onClick = {
                                                if (!loadingMore) pageNumber++
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
    onSaveReaderProgress: suspend (ChimahonReaderRequest, Int, Boolean) -> Unit,
    onSetChapterBookmark: suspend (Long, Boolean) -> Unit,
    onOpenReader: (ChimahonReaderRequest) -> Unit,
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
            ReaderMessage(
                marker = "...",
                title = "Loading chapter",
                detail = "Fetching the page list from the source.",
            )
        }
        is ReaderUiState.Failed -> {
            ReaderMessage(
                marker = "!",
                title = "Chapter failed",
                detail = readerState.message,
                action = "Retry",
                onAction = { refreshKey++ },
            )
        }
        is ReaderUiState.Ready -> {
            ReaderContent(
                chapter = readerState.chapter,
                initialBookmarked = initialBookmarked,
                onLoadReaderPageImage = onLoadReaderPageImage,
                onSaveReaderProgress = onSaveReaderProgress,
                onSetChapterBookmark = onSetChapterBookmark,
                onOpenReader = onOpenReader,
            )
        }
    }
}

@Composable
private fun ReaderContent(
    chapter: ChimahonReaderChapter,
    initialBookmarked: Boolean,
    onLoadReaderPageImage: suspend (ChimahonReaderPage) -> ByteArray,
    onSaveReaderProgress: suspend (ChimahonReaderRequest, Int, Boolean) -> Unit,
    onSetChapterBookmark: suspend (Long, Boolean) -> Unit,
    onOpenReader: (ChimahonReaderRequest) -> Unit,
) {
    var mode by remember(chapter.request) { mutableStateOf(ReaderMode.Webtoon) }
    var scale by remember(chapter.request.mangaId) { mutableStateOf(ReaderScale.FitWidth) }
    var canvas by remember(chapter.request.mangaId) { mutableStateOf(ReaderCanvas.Black) }
    var settingsVisible by remember(chapter.request) { mutableStateOf(false) }
    var chaptersVisible by remember(chapter.request) { mutableStateOf(false) }
    var bookmarked by remember(chapter.request.chapterId) { mutableStateOf(initialBookmarked) }
    var bookmarkBusy by remember(chapter.request.chapterId) { mutableStateOf(false) }
    var retainedPage by remember(chapter.request) {
        mutableIntStateOf(chapter.request.initialPage.coerceIn(chapter.pages.indices))
    }
    val pageStates = remember(chapter.request) {
        mutableStateMapOf<Int, ReaderPageUiState>()
    }
    val previousChapter = chapter.request.chapterQueue.getOrNull(chapter.request.chapterIndex + 1)
    val nextChapter = chapter.request.chapterQueue.getOrNull(chapter.request.chapterIndex - 1)
    val scope = rememberCoroutineScope()
    val openAdjacentChapter: (ChimahonReaderChapterRef) -> Unit = { adjacent ->
        onOpenReader(adjacent.toReaderRequest(chapter.request))
    }

    key(mode) {
        if (mode.paged) {
            val initialPagerPage = if (mode.rightToLeft) {
                chapter.pages.lastIndex - retainedPage
            } else {
                retainedPage
            }.coerceIn(chapter.pages.indices)
            val pagerState = rememberPagerState(
                initialPage = initialPagerPage,
                pageCount = { chapter.pages.size },
            )
            val actualPage = if (mode.rightToLeft) {
                chapter.pages.lastIndex - pagerState.currentPage
            } else {
                pagerState.currentPage
            }
            LaunchedEffect(chapter.request, pagerState, mode) {
                snapshotFlow { pagerState.currentPage }
                    .distinctUntilChanged()
                    .collect { pagerPage ->
                        val pageIndex = if (mode.rightToLeft) {
                            chapter.pages.lastIndex - pagerPage
                        } else {
                            pagerPage
                        }
                        retainedPage = pageIndex
                        onSaveReaderProgress(
                            chapter.request,
                            pageIndex,
                            pageIndex == chapter.pages.lastIndex,
                        )
                    }
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
                settingsVisible = settingsVisible,
                chaptersVisible = chaptersVisible,
                previousChapter = previousChapter,
                nextChapter = nextChapter,
                onModeChange = {
                    retainedPage = actualPage
                    mode = it
                },
                onScaleChange = { scale = it },
                onCanvasChange = { canvas = it },
                onToggleSettings = {
                    settingsVisible = !settingsVisible
                    if (settingsVisible) chaptersVisible = false
                },
                onToggleChapters = {
                    chaptersVisible = !chaptersVisible
                    if (chaptersVisible) settingsVisible = false
                },
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
                    if (target in chapter.pages.indices) {
                        scope.launch { pagerState.animateScrollToPage(target) }
                    }
                },
                onNextPage = {
                    val target = if (mode.rightToLeft) {
                        pagerState.currentPage - 1
                    } else {
                        pagerState.currentPage + 1
                    }
                    if (target in chapter.pages.indices) {
                        scope.launch { pagerState.animateScrollToPage(target) }
                    }
                },
                onPageSelected = { pageIndex ->
                    val target = if (mode.rightToLeft) {
                        chapter.pages.lastIndex - pageIndex
                    } else {
                        pageIndex
                    }
                    scope.launch { pagerState.animateScrollToPage(target) }
                },
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(canvas.color),
                    key = { pagerPage ->
                        val pageIndex = if (mode.rightToLeft) {
                            chapter.pages.lastIndex - pagerPage
                        } else {
                            pagerPage
                        }
                        val page = chapter.pages[pageIndex]
                        "${page.index}:${page.url}"
                    },
                ) { pagerPage ->
                    val pageIndex = if (mode.rightToLeft) {
                        chapter.pages.lastIndex - pagerPage
                    } else {
                        pagerPage
                    }
                    ReaderPageImage(
                        page = chapter.pages[pageIndex],
                        paged = true,
                        scale = scale,
                        canvas = canvas,
                        cachedState = pageStates[pageIndex],
                        onStateChange = { pageStates[pageIndex] = it },
                        onLoadReaderPageImage = onLoadReaderPageImage,
                    )
                }
            }
        } else {
            val listState = rememberLazyListState(initialFirstVisibleItemIndex = retainedPage)
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
            ReaderScaffold(
                request = chapter.request,
                mode = mode,
                scale = scale,
                canvas = canvas,
                currentPage = listState.firstVisibleItemIndex + 1,
                pageCount = chapter.pages.size,
                bookmarked = bookmarked,
                bookmarkBusy = bookmarkBusy,
                settingsVisible = settingsVisible,
                chaptersVisible = chaptersVisible,
                previousChapter = previousChapter,
                nextChapter = nextChapter,
                onModeChange = {
                    retainedPage = listState.firstVisibleItemIndex
                    mode = it
                },
                onScaleChange = { scale = it },
                onCanvasChange = { canvas = it },
                onToggleSettings = {
                    settingsVisible = !settingsVisible
                    if (settingsVisible) chaptersVisible = false
                },
                onToggleChapters = {
                    chaptersVisible = !chaptersVisible
                    if (chaptersVisible) settingsVisible = false
                },
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
                    val target = (listState.firstVisibleItemIndex - 1).coerceAtLeast(0)
                    scope.launch { listState.animateScrollToItem(target) }
                },
                onNextPage = {
                    val target = (listState.firstVisibleItemIndex + 1).coerceAtMost(chapter.pages.lastIndex)
                    scope.launch { listState.animateScrollToItem(target) }
                },
                onPageSelected = { pageIndex ->
                    scope.launch { listState.animateScrollToItem(pageIndex) }
                },
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(canvas.color),
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
                            page = page,
                            paged = false,
                            scale = scale,
                            canvas = canvas,
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
    settingsVisible: Boolean,
    chaptersVisible: Boolean,
    previousChapter: ChimahonReaderChapterRef?,
    nextChapter: ChimahonReaderChapterRef?,
    onModeChange: (ReaderMode) -> Unit,
    onScaleChange: (ReaderScale) -> Unit,
    onCanvasChange: (ReaderCanvas) -> Unit,
    onToggleSettings: () -> Unit,
    onToggleChapters: () -> Unit,
    onToggleBookmark: (() -> Unit)?,
    onOpenChapter: (ChimahonReaderChapterRef) -> Unit,
    onPreviousChapter: (() -> Unit)?,
    onNextChapter: (() -> Unit)?,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onPageSelected: (Int) -> Unit,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(canvas.color)
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (event.key) {
                    Key.DirectionLeft,
                    Key.PageUp,
                    -> {
                        onPreviousPage()
                        true
                    }
                    Key.DirectionRight,
                    Key.Spacebar,
                    Key.PageDown,
                    -> {
                        onNextPage()
                        true
                    }
                    Key.DirectionUp -> {
                        onPreviousChapter?.invoke() ?: onPreviousPage()
                        true
                    }
                    Key.DirectionDown -> {
                        onNextChapter?.invoke() ?: onNextPage()
                        true
                    }
                    else -> false
                }
            },
    ) {
        if (chaptersVisible) {
            ReaderChapterQueuePanel(
                request = request,
                previousChapter = previousChapter,
                nextChapter = nextChapter,
                onOpenChapter = onOpenChapter,
            )
        }
        if (settingsVisible) {
            ReaderSettingsPanel(
                mode = mode,
                scale = scale,
                canvas = canvas,
                onModeChange = onModeChange,
                onScaleChange = onScaleChange,
                onCanvasChange = onCanvasChange,
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
        ReaderPageStrip(
            currentPage = currentPage,
            pageCount = pageCount,
            onPageSelected = onPageSelected,
        )
        ReaderControlBar(
            currentPage = currentPage,
            pageCount = pageCount,
            bookmarked = bookmarked,
            bookmarkBusy = bookmarkBusy,
            settingsVisible = settingsVisible,
            chaptersVisible = chaptersVisible,
            onToggleSettings = onToggleSettings,
            onToggleChapters = onToggleChapters,
            onToggleBookmark = onToggleBookmark,
            onPreviousChapter = onPreviousChapter,
            onNextChapter = onNextChapter,
            onPreviousPage = onPreviousPage,
            onNextPage = onNextPage,
        )
    }
}

@Composable
private fun ReaderPageStrip(
    currentPage: Int,
    pageCount: Int,
    onPageSelected: (Int) -> Unit,
) {
    if (pageCount <= 1) return
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(ReaderPalette.chrome)
            .border(1.dp, ReaderPalette.divider),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(pageCount) { pageIndex ->
            val selected = pageIndex + 1 == currentPage
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (selected) ReaderPalette.selectedControl else ReaderPalette.control)
                    .clickable { onPageSelected(pageIndex) },
                contentAlignment = Alignment.Center,
            ) {
                Label(
                    (pageIndex + 1).toString(),
                    if (selected) Color.White else ReaderPalette.secondaryText,
                    9,
                    weight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun ReaderSettingsPanel(
    mode: ReaderMode,
    scale: ReaderScale,
    canvas: ReaderCanvas,
    onModeChange: (ReaderMode) -> Unit,
    onScaleChange: (ReaderScale) -> Unit,
    onCanvasChange: (ReaderCanvas) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ReaderPalette.chrome)
            .border(1.dp, ReaderPalette.divider)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ReaderOptionRow(
            label = "Reading mode",
            options = ReaderMode.entries.map { it.title },
            selected = mode.title,
            onSelect = { title ->
                ReaderMode.entries.firstOrNull { it.title == title }?.let(onModeChange)
            },
        )
        ReaderOptionRow(
            label = "Scale",
            options = ReaderScale.entries.map { it.title },
            selected = scale.title,
            onSelect = { title ->
                ReaderScale.entries.firstOrNull { it.title == title }?.let(onScaleChange)
            },
        )
        ReaderOptionRow(
            label = "Background",
            options = ReaderCanvas.entries.map { it.title },
            selected = canvas.title,
            onSelect = { title ->
                ReaderCanvas.entries.firstOrNull { it.title == title }?.let(onCanvasChange)
            },
        )
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
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ReaderPalette.chrome)
            .border(1.dp, ReaderPalette.divider)
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
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            items(request.chapterQueue, key = { "${it.chapterId}:${it.chapterUrl}" }) { chapter ->
                val selected = chapter.chapterUrl == request.chapterUrl
                Column(
                    modifier = Modifier
                        .width(176.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (selected) ReaderPalette.selectedControl else ReaderPalette.control)
                        .clickable { onOpenChapter(chapter) }
                        .padding(horizontal = 11.dp, vertical = 9.dp),
                ) {
                    Label(
                        chapter.chapterName,
                        if (selected) Color.White else ReaderPalette.secondaryText,
                        11,
                        weight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                    )
                    Label(
                        buildString {
                            append("Ch. ").append(chapter.chapterNumber.toDisplayChapter())
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
private fun ReaderControlBar(
    currentPage: Int,
    pageCount: Int,
    bookmarked: Boolean,
    bookmarkBusy: Boolean,
    settingsVisible: Boolean,
    chaptersVisible: Boolean,
    onToggleSettings: () -> Unit,
    onToggleChapters: () -> Unit,
    onToggleBookmark: (() -> Unit)?,
    onPreviousChapter: (() -> Unit)?,
    onNextChapter: (() -> Unit)?,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(ReaderPalette.chrome)
            .border(1.dp, ReaderPalette.divider)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ReaderAction(
            icon = UiIcon.Chapters,
            contentDescription = "Chapter list",
            active = chaptersVisible,
            onClick = onToggleChapters,
        )
        onToggleBookmark?.let {
            ReaderAction(
                icon = if (bookmarked) UiIcon.Bookmark else UiIcon.BookmarkBorder,
                contentDescription = if (bookmarked) "Unbookmark chapter" else "Bookmark chapter",
                active = bookmarked || bookmarkBusy,
                onClick = it,
            )
        }
        if (onPreviousChapter != null) {
            ReaderAction(
                icon = UiIcon.SkipPrevious,
                contentDescription = "Previous chapter",
                onClick = onPreviousChapter,
            )
        }
        Spacer(Modifier.weight(1f))
        ReaderAction(
            icon = UiIcon.Back,
            contentDescription = "Previous page",
            onClick = onPreviousPage,
        )
        Label(
            text = "$currentPage / $pageCount",
            color = ReaderPalette.secondaryText,
            size = 12,
            weight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 10.dp),
        )
        ReaderAction(
            icon = UiIcon.Forward,
            contentDescription = "Next page",
            onClick = onNextPage,
        )
        Spacer(Modifier.weight(1f))
        if (onNextChapter != null) {
            ReaderAction(
                icon = UiIcon.SkipNext,
                contentDescription = "Next chapter",
                onClick = onNextChapter,
            )
        }
        ReaderAction(
            icon = UiIcon.Settings,
            contentDescription = "Reader settings",
            active = settingsVisible,
            onClick = onToggleSettings,
        )
    }
}

@Composable
private fun ReaderPageImage(
    page: ChimahonReaderPage,
    paged: Boolean,
    scale: ReaderScale,
    canvas: ReaderCanvas,
    cachedState: ReaderPageUiState?,
    onStateChange: (ReaderPageUiState) -> Unit,
    onLoadReaderPageImage: suspend (ChimahonReaderPage) -> ByteArray,
) {
    var retryKey by remember(page) { mutableIntStateOf(0) }

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
                .background(canvas.color)
                .padding(12.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .widthIn(max = if (scale == ReaderScale.FitScreen) 900.dp else 1180.dp)
                .heightIn(min = 260.dp)
                .background(canvas.color)
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
                Image(
                    bitmap = pageState.image,
                    contentDescription = "Page ${page.index + 1}",
                    contentScale = if (paged || scale == ReaderScale.FitScreen) {
                        ContentScale.Fit
                    } else {
                        ContentScale.FillWidth
                    },
                    modifier = if (paged && scale == ReaderScale.FitScreen) {
                        Modifier.fillMaxSize()
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(
                                pageState.image.width.toFloat() /
                                    pageState.image.height.coerceAtLeast(1).toFloat(),
                            )
                    },
                )
            }
        }
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
    repoInputRequestKey: Int,
    onAddExtensionRepo: suspend (String) -> ChimahonExtensionRepoEntry,
    onLoadExtensionRepoCatalog: suspend (ChimahonExtensionRepoEntry) -> ChimahonExtensionRepoCatalog,
    onInstallExtension: suspend (ChimahonRepoExtensionEntry) -> ChimahonInstalledExtensionEntry,
    onRepoSaved: () -> Unit,
) {
    var repoUrl by remember { mutableStateOf("") }
    var repoMessage by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    var selectedRepo by remember { mutableStateOf<ChimahonExtensionRepoEntry?>(null) }
    var catalogState by remember { mutableStateOf<ExtensionRepoCatalogUiState>(ExtensionRepoCatalogUiState.Idle) }
    var installingExtensionId by remember { mutableStateOf<String?>(null) }
    var installMessage by remember { mutableStateOf<String?>(null) }
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
                    InstalledExtensionListItem(extension)
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
                            .groupBy { it.language.ifBlank { "multi" }.uppercase() }
                            .forEach { (language, extensions) ->
                                item {
                                    ListGroupHeader(language)
                                }
                                items(extensions, key = { it.id }) { extension ->
                                    RepoExtensionListItem(
                                        extension = extension,
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
) {
    ExtensionListRow(
        marker = "R",
        title = repo.name,
        subtitle = repo.baseUrl,
        action = if (selected) "Selected" else "Browse",
        active = selected,
        onClick = onClick,
    )
}

@Composable
private fun InstalledExtensionListItem(extension: ChimahonInstalledExtensionEntry) {
    ExtensionListRow(
        marker = extension.packageType.marker,
        title = extension.name,
        subtitle = "${extension.sourceCount} source(s)  \u00b7  ${extension.version}",
        action = "Settings",
        active = true,
    )
}

@Composable
private fun RepoExtensionListItem(
    extension: ChimahonRepoExtensionEntry,
    installed: Boolean,
    installing: Boolean,
    onInstall: () -> Unit,
) {
    ExtensionListRow(
        marker = extension.packageType.marker,
        title = extension.name,
        subtitle = buildString {
            append(extension.language.ifBlank { "multi" }.uppercase())
            append("  \u00b7  ")
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
                            onClick = {
                                onOpenReader(chapter.toReaderRequest(manga, chapters))
                            },
                            onSetRead = { read -> onSetChapterRead(chapter.id, read) },
                            onSetBookmark = { bookmarked -> onSetChapterBookmark(chapter.id, bookmarked) },
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
                    onClick = {
                        onOpenReader(chapter.toReaderRequest(manga, chapters))
                    },
                    onSetRead = { read -> onSetChapterRead(chapter.id, read) },
                    onSetBookmark = { bookmarked -> onSetChapterBookmark(chapter.id, bookmarked) },
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
    onClick: () -> Unit,
    onSetRead: suspend (Boolean) -> Unit,
    onSetBookmark: suspend (Boolean) -> Unit,
) {
    var busyAction by remember(chapter.id) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
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
        }
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
    downloadedOnlyMode: Boolean,
    incognitoMode: Boolean,
    onDownloadedOnlyModeChange: (Boolean) -> Unit,
    onIncognitoModeChange: (Boolean) -> Unit,
    onOpenBrowseSection: (BrowseSection) -> Unit,
) {
    var selectedPage by remember { mutableStateOf(MorePage.Main) }
    if (selectedPage != MorePage.Main) {
        MoreDetailPage(
            page = selectedPage,
            snapshot = snapshot,
            onCreateCategory = onCreateCategory,
            onDeleteCategory = onDeleteCategory,
            onCategoriesChanged = onCategoriesChanged,
            onBack = { selectedPage = MorePage.Main },
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
                onClick = { selectedPage = MorePage.Downloads },
            )
        }
        item {
            PreferenceRow(
                "Categories",
                "${snapshot.libraryCategories.count { !it.hidden }} visible categories",
                UiIcon.Tag,
                onClick = { selectedPage = MorePage.Categories },
            )
        }
        item {
            PreferenceRow(
                "Statistics",
                "${snapshot.summary.mangaCount} manga in shared database",
                UiIcon.Statistics,
                onClick = { selectedPage = MorePage.Statistics },
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
                onClick = { selectedPage = MorePage.Storage },
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
                onClick = { selectedPage = MorePage.Settings },
            )
        }
        item {
            PreferenceRow(
                "About",
                "Chimahon ${snapshot.runtime.platformName}",
                UiIcon.Info,
                onClick = { selectedPage = MorePage.About },
            )
        }
        item {
            PreferenceRow(
                "Help",
                "Reader and extension help",
                UiIcon.Help,
                onClick = { selectedPage = MorePage.Help },
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
                        .clickable { selectedPage = MorePage.About }
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
    onBack: () -> Unit,
) {
    var categoryName by remember { mutableStateOf("") }
    var categoryMessage by remember { mutableStateOf<String?>(null) }
    var categoryBusy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
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
                    item {
                        EmptyMobileState(
                            marker = "D",
                            icon = UiIcon.Download,
                            title = "Download queue is empty",
                            detail = "Queued and active desktop downloads will appear here.",
                        )
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
                            "Unread chapters",
                            snapshot.library.sumOf { manga ->
                                snapshot.chaptersByMangaId[manga.id].orEmpty().count { !it.read }
                            }.toString(),
                            UiIcon.Updates,
                        )
                    }
                    item {
                        MoreStatisticRow(
                            "History entries",
                            snapshot.history.size.toString(),
                            UiIcon.History,
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
                    item { ListGroupHeader("Runtime") }
                    item { RuntimeLineRow("Platform", snapshot.runtime.platformName) }
                    item { RuntimeLineRow("Database", snapshot.runtime.databaseState) }
                    item { RuntimeLineRow("Extensions", snapshot.runtime.extensionState) }
                    item { RuntimeLineRow("Background work", snapshot.runtime.backgroundState) }
                    item { RuntimeLineRow("Files", snapshot.runtime.filesDir) }
                    item { RuntimeLineRow("Cache", snapshot.runtime.cacheDir) }
                    item { RuntimeLineRow("Downloads", snapshot.runtime.downloadsDir) }
                }
                MorePage.Settings -> {
                    item { ListGroupHeader("Reader") }
                    item {
                        PreferenceSwitchRow(
                            "Keep reader controls visible",
                            "Show navigation and viewer controls while reading",
                            UiIcon.Chapters,
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Right-to-left by default",
                            "Use manga page direction for new titles",
                            UiIcon.Swap,
                        )
                    }
                    item { ListGroupHeader("Library") }
                    item {
                        PreferenceSwitchRow(
                            "Show unread badges",
                            "Display unread chapter counts on covers",
                            UiIcon.Updates,
                        )
                    }
                    item {
                        PreferenceSwitchRow(
                            "Show category tabs",
                            "Keep library categories above the grid",
                            UiIcon.Tag,
                        )
                    }
                }
                MorePage.About -> {
                    item {
                        MoreLogoHeader(snapshot)
                    }
                    item { RuntimeLineRow("Database", snapshot.runtime.databaseState) }
                    item { RuntimeLineRow("Extension engine", snapshot.runtime.extensionState) }
                    item { RuntimeLineRow("Background tasks", snapshot.runtime.backgroundState) }
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
                }
                MorePage.Main -> Unit
            }
        }
    }
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
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (active) ReaderPalette.selectedControl else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        IconGlyph(
            icon = icon,
            contentDescription = contentDescription,
            tint = Color.White,
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
            fontSize = size.sp,
            fontWeight = weight,
            lineHeight = lineHeight.sp,
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

private const val ALL_LIBRARY_CATEGORY_ID = -1L
private const val DEFAULT_LIBRARY_CATEGORY_ID = 0L

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
}

@Composable
private fun IconGlyph(
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
            UiIcon.Incognito -> {
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

private object ChimahonPalette {
    val background = Color(0xFFFFFBFE)
    val surface = Color(0xFFFFFFFF)
    val surfaceVariant = Color(0xFFEDE7F1)
    val divider = Color(0xFFE4DCE8)
    val onSurface = Color(0xFF1D1B20)
    val secondaryText = Color(0xFF625B66)
    val primary = Color(0xFF5C5A86)
    val primaryContainer = Color(0xFFE8DEF8)
    val onPrimaryContainer = Color(0xFF2B2540)
    val error = Color(0xFFBA1A1A)
    val errorContainer = Color(0xFFFFDAD6)
}

private object ReaderPalette {
    val background = Color(0xFF0D0D0F)
    val chrome = Color(0xFF17171B)
    val control = Color(0xFF29292F)
    val selectedControl = Color(0xFF5C5A86)
    val divider = Color(0xFF34343B)
    val secondaryText = Color(0xFFB9B6C0)
}
