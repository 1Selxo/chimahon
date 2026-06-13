package app.chimahon.shared

import app.cash.sqldelight.db.SqlDriver
import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.SourceRegistry
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ScriptExtensionSourceManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import io.ktor.http.isSuccess
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import tachiyomi.core.extensions.LoadedScriptExtension
import tachiyomi.core.extensions.ScriptExtensionInvoker
import tachiyomi.core.extensions.ScriptExtensionLoader
import tachiyomi.core.extensions.ScriptExtensionStore
import tachiyomi.core.platform.javascript.JavaScriptRuntimeFactory
import tachiyomi.core.platform.settings.FilePlatformSettingsStore
import tachiyomi.core.platform.storage.PlatformStorageDirectories
import tachiyomi.data.Chapters
import tachiyomi.data.Database
import tachiyomi.data.DatabaseHandler
import tachiyomi.data.Extension_repos
import tachiyomi.data.GetCategories
import tachiyomi.data.GetCategoriesByMangaId
import tachiyomi.data.Mangas
import tachiyomi.data.StringListColumnAdapter
import tachiyomi.data.libraryUpdateError.LibraryUpdateErrorRepositoryImpl
import tachiyomi.data.libraryUpdateErrorMessage.LibraryUpdateErrorMessageRepositoryImpl
import tachiyomi.domain.libraryUpdateError.interactor.GetLibraryUpdateErrors
import tachiyomi.domain.libraryUpdateErrorMessage.interactor.GetLibraryUpdateErrorMessages
import tachiyomi.view.History
import tachiyomi.view.UpdatesView
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class ChimahonSharedAppServices private constructor(
    private val platformServices: ChimahonPlatformServices = ChimahonPlatformServices(),
) {
    constructor() : this(ChimahonPlatformServices())

    private val libraryUpdateErrorRepository =
        LibraryUpdateErrorRepositoryImpl(platformServices.databaseHandler)
    private val getLibraryUpdateErrors =
        GetLibraryUpdateErrors(libraryUpdateErrorRepository)
    private val libraryUpdateErrorMessageRepository =
        LibraryUpdateErrorMessageRepositoryImpl(platformServices.databaseHandler)
    private val getLibraryUpdateErrorMessages =
        GetLibraryUpdateErrorMessages(libraryUpdateErrorMessageRepository)
    private val settingsRepository = ChimahonSettingsRepository(
        FilePlatformSettingsStore(platformServices.storageDirectories.filesDir / SETTINGS_FILE_NAME),
    )
    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val httpClient = HttpClient(CIO) {
        expectSuccess = false
        followRedirects = true
    }
    private val scriptExtensionLoader = ScriptExtensionLoader(platformServices.javaScriptRuntimeFactory)
    private val scriptExtensionManager = ScriptExtensionSourceManager(
        store = ScriptExtensionStore(
            storageDirectories = platformServices.storageDirectories,
            loader = scriptExtensionLoader,
        ),
        invoker = ScriptExtensionInvoker(platformServices.javaScriptRuntimeFactory),
        registry = platformServices.sourceRegistry,
    )
    private val readerProgressMutex = Mutex()
    private val readerProgressMarks = mutableMapOf<Long, TimeMark>()
    private val thumbnailCacheMutex = Mutex()
    private val thumbnailCache = mutableMapOf<String, ByteArray>()

    suspend fun loadSnapshot(): ChimahonSnapshot {
        val extensionState = loadExtensions()
        val allManga = platformServices.databaseHandler.awaitList {
            mangasQueries.getAllManga()
        }
        val errorMessages = getLibraryUpdateErrorMessages.await().associateBy { it.id }
        val updateErrors = getLibraryUpdateErrors.await()
        val extensionRepos = platformServices.databaseHandler.awaitList {
            extension_reposQueries.findAll()
        }
        val recentUpdates = platformServices.databaseHandler.awaitList {
            updatesViewQueries.getRecentUpdates(
                after = 0L,
                limit = 100L,
            )
        }
        val history = platformServices.databaseHandler.awaitList {
            historyViewQueries.history(
                bookmarkUnmask = CHAPTER_SHOW_NOT_BOOKMARKED,
                bookmarkMask = CHAPTER_SHOW_BOOKMARKED,
                unfinishedManga = null,
                unfinishedChapter = null,
                nonLibraryEntries = null,
                query = "",
            )
        }
        val libraryManga = allManga.filter(Mangas::favorite)
        val categories = platformServices.databaseHandler.awaitList {
            categoriesQueries.getCategories()
        }
        val libraryCategoryMemberships = libraryManga.associate { manga ->
            val categoryIds = platformServices.databaseHandler.awaitList {
                categoriesQueries.getCategoriesByMangaId(manga._id)
            }.map(GetCategoriesByMangaId::id)
            manga._id to categoryIds.ifEmpty { listOf(DEFAULT_LIBRARY_CATEGORY_ID) }
        }
        val detailMangaIds = (
            libraryManga.map(Mangas::_id) +
                recentUpdates.map(UpdatesView::mangaId) +
                history.map(History::mangaId)
            )
            .distinct()
        val chapters = if (detailMangaIds.isEmpty()) {
            emptyList()
        } else {
            platformServices.databaseHandler.awaitList {
                ehQueries.getChaptersByMangaIds(detailMangaIds)
            }
        }
        val mangaById = allManga.associateBy(Mangas::_id)
        val sharedMangaById = allManga
            .map(Mangas::toSharedMangaEntry)
            .associateBy(ChimahonMangaEntry::id)
        val sources = platformServices.sourceRegistry
            .getCatalogueSources()
            .sortedWith(compareBy<CatalogueSource> { it.lang }.thenBy { it.name.lowercase() })

        return ChimahonSnapshot(
            summary = ChimahonSummary(
                extensionCount = extensionState.extensionCount,
                sourceCount = sources.size,
                mangaCount = allManga.size,
            ),
            mangaDetails = sharedMangaById,
            library = libraryManga
                .asSequence()
                .map(Mangas::toSharedMangaEntry)
                .sortedBy { it.title.lowercase() }
                .toList(),
            libraryCategories = categories
                .map(GetCategories::toSharedLibraryCategory)
                .ifEmpty {
                    listOf(
                        ChimahonLibraryCategory(
                            id = DEFAULT_LIBRARY_CATEGORY_ID,
                            name = "",
                            order = -1L,
                            hidden = false,
                        ),
                    )
                },
            libraryCategoryMemberships = libraryCategoryMemberships,
            chaptersByMangaId = chapters
                .map(Chapters::toSharedChapterEntry)
                .groupBy(ChimahonChapterEntry::mangaId)
                .mapValues { (_, entries) ->
                    entries.sortedWith(
                        compareBy<ChimahonChapterEntry> { it.sourceOrder }
                            .thenBy { it.chapterNumber },
                    )
                },
            updates = recentUpdates.map(UpdatesView::toSharedUpdateEntry),
            history = history.map(History::toSharedHistoryEntry),
            sources = sources.map(CatalogueSource::toSharedSourceEntry),
            extensionRepos = extensionRepos
                .map(Extension_repos::toSharedExtensionRepoEntry)
                .sortedBy { it.name.lowercase() },
            installedExtensions = extensionState.installedExtensions,
            updateIssues = updateErrors
                .sortedByDescending { it.lastUpdate }
                .map { error ->
                    ChimahonUpdateIssue(
                        id = error.id,
                        mangaId = error.mangaId,
                        mangaTitle = mangaById[error.mangaId]?.title ?: "Manga #${error.mangaId}",
                        message = errorMessages[error.messageId]?.message ?: "Unknown update error",
                        lastUpdate = formatTimestamp(error.lastUpdate),
                    )
                },
            runtime = ChimahonRuntimeInfo(
                platformName = platformServices.platformName,
                filesDir = platformServices.storageDirectories.filesDir.toString(),
                cacheDir = platformServices.storageDirectories.cacheDir.toString(),
                downloadsDir = platformServices.storageDirectories.defaultDownloadsDir(APP_NAME).toString(),
                databaseState = "Connected",
                extensionState = extensionState.label,
                backgroundState = platformServices.backgroundState,
            ),
        )
    }

    suspend fun addExtensionRepo(input: String): ChimahonExtensionRepoEntry {
        val baseUrl = normalizeExtensionRepoBaseUrl(input)
        val repo = if (baseUrl.endsWith(SCRIPT_EXTENSION_SUFFIX, ignoreCase = true)) {
            val script = fetchText(baseUrl)
            val extension = scriptExtensionLoader.load(script)
            ChimahonExtensionRepoEntry(
                baseUrl = baseUrl,
                name = extension.manifest.name,
                shortName = extension.manifest.name.take(16),
                website = baseUrl,
                signingKeyFingerprint = "script-${extension.manifest.id}",
            )
        } else {
            val metadata = fetchRepoMetadata(baseUrl)
            loadExtensionRepoCatalog(metadata)
            metadata
        }

        platformServices.databaseHandler.await {
            extension_reposQueries.upsert(
                base_url = repo.baseUrl,
                name = repo.name,
                short_name = repo.shortName,
                website = repo.website,
                fingerprint = repo.signingKeyFingerprint,
            )
        }
        return repo
    }

    suspend fun loadExtensionRepoCatalog(
        repo: ChimahonExtensionRepoEntry,
    ): ChimahonExtensionRepoCatalog {
        val extensions = if (repo.baseUrl.endsWith(SCRIPT_EXTENSION_SUFFIX, ignoreCase = true)) {
            val script = fetchText(repo.baseUrl)
            val extension = scriptExtensionLoader.load(script)
            listOf(extension.toRepoExtensionEntry(repo.baseUrl, repo.baseUrl))
        } else {
            fetchFirstCompatibleRepoCatalog(
                repoBaseUrl = repo.baseUrl,
                urls = repoCatalogUrls(repo.baseUrl),
            )
        }
        require(extensions.isNotEmpty()) {
            "Repository has no compatible JavaScript or Android APK extensions."
        }
        return ChimahonExtensionRepoCatalog(
            repo = repo,
            extensions = extensions.sortedBy { it.name.lowercase() },
        )
    }

    suspend fun installExtension(
        extension: ChimahonRepoExtensionEntry,
    ): ChimahonInstalledExtensionEntry {
        return when (extension.packageType) {
            ChimahonExtensionPackageType.JavaScript -> {
                val script = fetchText(extension.artifactUrl)
                val installed = scriptExtensionManager.install(script)
                require(installed.manifest.id == extension.id) {
                    "Downloaded extension id ${installed.manifest.id} does not match ${extension.id}."
                }
                installed.toSharedInstalledExtensionEntry()
            }
            ChimahonExtensionPackageType.AndroidApk -> {
                platformServices.apkExtensionManager.install(
                    extension = extension,
                    apkBytes = fetchBytes(extension.artifactUrl),
                )
            }
        }
    }

    private suspend fun fetchRepoMetadata(baseUrl: String): ChimahonExtensionRepoEntry {
        val metadata = runCatching {
            val payload = fetchText("$baseUrl/repo.json")
            val root = json.parseToJsonElement(payload).jsonObject
            val meta = (root["meta"] as? JsonObject) ?: root
            val name = meta.stringValue("name")
                ?: baseUrl.repoHost()
            ChimahonExtensionRepoEntry(
                baseUrl = baseUrl,
                name = name,
                shortName = meta.stringValue("shortName", "short_name") ?: name.take(16),
                website = meta.stringValue("website") ?: baseUrl,
                signingKeyFingerprint = meta.stringValue(
                    "signingKeyFingerprint",
                    "signing_key_fingerprint",
                ) ?: "shared-${baseUrl.stableRepoHash()}",
            )
        }.getOrNull()

        return metadata ?: ChimahonExtensionRepoEntry(
            baseUrl = baseUrl,
            name = baseUrl.repoHost(),
            shortName = baseUrl.repoHost().substringBefore(".").take(16),
            website = baseUrl,
            signingKeyFingerprint = "shared-${baseUrl.stableRepoHash()}",
        )
    }

    private suspend fun fetchFirstAvailable(urls: List<String>): String {
        var lastError: Throwable? = null
        for (url in urls) {
            runCatching { fetchText(url) }
                .onSuccess { return it }
                .onFailure { lastError = it }
        }
        throw lastError ?: IllegalStateException("No repository index URL was available.")
    }

    private suspend fun fetchFirstCompatibleRepoCatalog(
        repoBaseUrl: String,
        urls: List<String>,
    ): List<ChimahonRepoExtensionEntry> {
        var lastError: Throwable? = null
        for (url in urls) {
            runCatching {
                parseRepoExtensions(
                    json = json,
                    repoBaseUrl = repoBaseUrl,
                    payload = fetchText(url),
                )
            }
                .onSuccess { extensions ->
                    if (extensions.isNotEmpty()) return extensions
                }
                .onFailure { lastError = it }
        }
        throw lastError ?: IllegalStateException(
            "Repository indexes did not contain compatible JavaScript or Android APK extensions.",
        )
    }

    private suspend fun fetchText(url: String): String {
        val response = httpClient.get(url)
        check(response.status.isSuccess()) {
            "HTTP ${response.status.value} while loading $url"
        }
        return response.bodyAsText()
    }

    private suspend fun fetchBytes(url: String): ByteArray {
        val response = httpClient.get(url)
        check(response.status.isSuccess()) {
            "HTTP ${response.status.value} while loading $url"
        }
        return response.readRawBytes()
    }

    suspend fun loadSourcePreview(
        sourceId: Long,
        mode: ChimahonSourceBrowseMode,
        query: String,
        pageNumber: Int,
    ): ChimahonSourcePreview {
        require(pageNumber > 0) { "Page number must be positive." }
        val source = platformServices.sourceRegistry
            .getCatalogueSources()
            .firstOrNull { it.id == sourceId }
            ?: error("Source $sourceId is not loaded.")
        val page = when (mode) {
            ChimahonSourceBrowseMode.Popular -> source.getPopularManga(pageNumber)
            ChimahonSourceBrowseMode.Latest -> {
                if (source.supportsLatest) {
                    source.getLatestUpdates(pageNumber)
                } else {
                    source.getPopularManga(pageNumber)
                }
            }
            ChimahonSourceBrowseMode.Search -> {
                require(query.isNotBlank()) { "Search query is empty." }
                source.getSearchManga(pageNumber, query, source.getFilterList())
            }
        }

        return ChimahonSourcePreview(
            sourceId = source.id,
            sourceName = source.name,
            mode = mode,
            entries = page.mangas.map { it.toRemoteMangaEntry(source.id) },
            hasNextPage = page.hasNextPage,
        )
    }

    suspend fun loadRemoteMangaDetail(
        remoteManga: ChimahonRemoteMangaEntry,
    ): ChimahonRemoteMangaDetail {
        val source = platformServices.sourceRegistry
            .getCatalogueSources()
            .firstOrNull { it.id == remoteManga.sourceId }
            ?: error("Source ${remoteManga.sourceId} is not loaded.")
        val seed = SManga(
            url = remoteManga.url,
            title = remoteManga.title,
            author = remoteManga.author,
            status = 0,
            thumbnail_url = remoteManga.thumbnailUrl,
            initialized = remoteManga.initialized,
        )
        val details = source.getMangaDetails(seed).withSeedFallback(seed)
        val chapters = source.getChapterList(details)

        return ChimahonRemoteMangaDetail(
            sourceId = source.id,
            sourceName = source.name,
            title = details.title,
            artist = details.artist,
            author = details.author,
            description = details.description,
            genres = details.getGenres().orEmpty(),
            status = mangaStatus(details.status.toLong()),
            statusCode = details.status.toLong(),
            url = details.url,
            thumbnailUrl = details.thumbnail_url,
            initialized = details.initialized,
            chapters = chapters
                .map(SChapter::toRemoteChapterEntry)
                .sortedWith(
                    compareByDescending<ChimahonRemoteChapterEntry> { it.chapterNumber }
                        .thenBy { it.name },
            ),
        )
    }

    fun openExternalUrl(url: String): Boolean {
        val externalUrl = url.normalizedExternalWebUrlOrNull() ?: return false
        return platformServices.openExternalUrl(externalUrl)
    }

    fun openRemoteMangaUrl(
        detail: ChimahonRemoteMangaDetail,
    ): Boolean {
        val source = platformServices.sourceRegistry
            .getCatalogueSources()
            .firstOrNull { it.id == detail.sourceId }
        val manga = SManga(
            url = detail.url,
            title = detail.title,
            artist = detail.artist,
            author = detail.author,
            description = detail.description,
            genre = detail.genres.joinToString(", ").takeIf { it.isNotBlank() },
            status = detail.statusCode.toInt(),
            thumbnail_url = detail.thumbnailUrl,
            initialized = detail.initialized,
        )
        val externalUrl = source
            ?.let { platformServices.resolveExternalMangaUrl(it, manga) }
            ?.normalizedExternalWebUrlOrNull()
            ?: detail.url.normalizedExternalWebUrlOrNull()
            ?: return false

        return platformServices.openExternalUrl(externalUrl)
    }

    suspend fun addRemoteMangaToLibrary(
        detail: ChimahonRemoteMangaDetail,
    ): Long {
        return importRemoteMangaToLibrary(
            databaseHandler = platformServices.databaseHandler,
            detail = detail,
            now = platformServices.currentTimeMillis(),
        )
    }

    suspend fun refreshManga(mangaId: Long) {
        val manga = platformServices.databaseHandler.awaitOneOrNull {
            mangasQueries.getMangaById(mangaId)
        } ?: error("Manga $mangaId is not present in the shared database.")
        val detail = loadRemoteMangaDetail(
            ChimahonRemoteMangaEntry(
                sourceId = manga.source,
                title = manga.title,
                author = manga.author,
                status = mangaStatus(manga.status),
                url = manga.url,
                thumbnailUrl = manga.thumbnail_url,
                initialized = manga.initialized,
            ),
        )
        importRemoteMangaToLibrary(
            databaseHandler = platformServices.databaseHandler,
            detail = detail,
            now = platformServices.currentTimeMillis(),
        )
    }

    suspend fun createCategory(name: String): Long {
        val normalizedName = name.trim()
        require(normalizedName.isNotEmpty()) { "Category name is empty." }
        val categories = platformServices.databaseHandler.awaitList {
            categoriesQueries.getCategories()
        }
        require(categories.none { it.name.equals(normalizedName, ignoreCase = true) }) {
            "A category named $normalizedName already exists."
        }
        return platformServices.databaseHandler.awaitOneExecutable(inTransaction = true) {
            categoriesQueries.insert(
                name = normalizedName,
                order = (categories.maxOfOrNull { it.order } ?: 0L) + 1L,
                flags = 0L,
                hidden = 0L,
            )
            categoriesQueries.selectLastInsertedRowId()
        }
    }

    suspend fun deleteCategory(categoryId: Long) {
        require(categoryId > DEFAULT_LIBRARY_CATEGORY_ID) { "The default category cannot be deleted." }
        platformServices.databaseHandler.await {
            categoriesQueries.delete(categoryId)
        }
    }

    suspend fun setMangaCategories(
        mangaId: Long,
        categoryIds: Set<Long>,
    ) {
        val selectedIds = categoryIds
            .filter { it >= DEFAULT_LIBRARY_CATEGORY_ID }
            .ifEmpty { listOf(DEFAULT_LIBRARY_CATEGORY_ID) }
        platformServices.databaseHandler.await(inTransaction = true) {
            mangas_categoriesQueries.deleteMangaCategoryByMangaId(mangaId)
            selectedIds.forEach { categoryId ->
                mangas_categoriesQueries.insert(mangaId, categoryId)
            }
        }
    }

    suspend fun setMangaFavorite(
        mangaId: Long,
        favorite: Boolean,
    ) {
        updateMangaFavorite(
            databaseHandler = platformServices.databaseHandler,
            mangaId = mangaId,
            favorite = favorite,
            now = platformServices.currentTimeMillis(),
        )
    }

    suspend fun setMangaNotes(
        mangaId: Long,
        notes: String,
    ) {
        updateMangaNotes(
            databaseHandler = platformServices.databaseHandler,
            mangaId = mangaId,
            notes = notes.trim(),
        )
    }

    suspend fun resetHistoryEntry(historyId: Long) {
        platformServices.databaseHandler.await(inTransaction = true) {
            historyQueries.resetHistoryByIds(listOf(historyId))
            historyQueries.removeResettedHistory()
        }
    }

    suspend fun resetHistoryForManga(mangaId: Long) {
        platformServices.databaseHandler.await(inTransaction = true) {
            historyQueries.resetHistoryByMangaIds(listOf(mangaId))
            historyQueries.removeResettedHistory()
        }
    }

    suspend fun clearHistory() {
        platformServices.databaseHandler.await {
            historyQueries.removeAllHistory()
        }
    }

    suspend fun dismissUpdateIssue(issueId: Long) {
        platformServices.databaseHandler.await {
            libraryUpdateErrorQueries.deleteErrors(listOf(issueId))
        }
    }

    suspend fun clearUpdateIssues() {
        platformServices.databaseHandler.await {
            libraryUpdateErrorQueries.deleteAllErrors()
        }
    }

    suspend fun setMangaChaptersRead(
        mangaId: Long,
        read: Boolean,
    ) {
        updateMangaChaptersRead(
            databaseHandler = platformServices.databaseHandler,
            mangaId = mangaId,
            read = read,
        )
    }

    suspend fun setChapterRead(
        chapterId: Long,
        read: Boolean,
    ) {
        updateChapterRead(
            databaseHandler = platformServices.databaseHandler,
            chapterId = chapterId,
            read = read,
        )
    }

    suspend fun setChapterBookmark(
        chapterId: Long,
        bookmarked: Boolean,
    ) {
        updateChapterBookmark(
            databaseHandler = platformServices.databaseHandler,
            chapterId = chapterId,
            bookmarked = bookmarked,
        )
    }

    suspend fun loadReaderChapter(
        request: ChimahonReaderRequest,
    ): ChimahonReaderChapter {
        val source = platformServices.sourceRegistry
            .getCatalogueSources()
            .firstOrNull { it.id == request.sourceId }
            ?: error("Source ${request.sourceId} is not loaded.")
        val chapter = SChapter(
            url = request.chapterUrl,
            name = request.chapterName,
            date_upload = request.dateUpload,
            chapter_number = request.chapterNumber.toFloat(),
            scanlator = request.scanlator,
        )
        val pages = source.getPageList(chapter)
        require(pages.isNotEmpty()) {
            "${source.name} returned no pages for ${request.chapterName}."
        }
        return ChimahonReaderChapter(
            request = request,
            pages = pages.mapIndexed { index, page ->
                ChimahonReaderPage(
                    sourceId = source.id,
                    index = index,
                    url = page.url,
                    imageUrl = page.imageUrl,
                )
            },
        )
    }

    suspend fun loadReaderPageImage(
        readerPage: ChimahonReaderPage,
    ): ByteArray {
        val source = platformServices.sourceRegistry
            .getCatalogueSources()
            .firstOrNull { it.id == readerPage.sourceId }
            ?: error("Source ${readerPage.sourceId} is not loaded.")
        return fetchSourcePageImage(
            source = source,
            page = Page(
                index = readerPage.index,
                url = readerPage.url,
                imageUrl = readerPage.imageUrl,
            ),
        )
    }

    suspend fun loadThumbnailImage(url: String): ByteArray {
        require(url.isNotBlank()) { "Thumbnail URL is empty." }
        thumbnailCacheMutex.withLock {
            thumbnailCache[url]?.let { return it }
        }
        val bytes = fetchBytes(url)
        thumbnailCacheMutex.withLock {
            if (thumbnailCache.size >= MAX_THUMBNAIL_CACHE_ENTRIES) {
                thumbnailCache.keys.firstOrNull()?.let(thumbnailCache::remove)
            }
            thumbnailCache[url] = bytes
        }
        return bytes
    }

    suspend fun loadSettings(): ChimahonSettings {
        return settingsRepository.loadSettings()
    }

    suspend fun saveReaderSettings(settings: ChimahonReaderSettings): ChimahonReaderSettings {
        return settingsRepository.saveReaderSettings(settings)
    }

    suspend fun setDownloadedOnly(enabled: Boolean): ChimahonAppModeSettings {
        return settingsRepository.setDownloadedOnly(enabled)
    }

    suspend fun setIncognitoMode(enabled: Boolean): ChimahonAppModeSettings {
        return settingsRepository.setIncognitoMode(enabled)
    }

    suspend fun saveReaderProgress(
        request: ChimahonReaderRequest,
        pageIndex: Int,
        completed: Boolean,
    ) {
        val chapterId = request.chapterId ?: return
        if (settingsRepository.isIncognitoModeEnabled()) return
        readerProgressMutex.withLock {
            val elapsedMillis = readerProgressMarks[chapterId]
                ?.elapsedNow()
                ?.inWholeMilliseconds
                ?.coerceAtLeast(0L)
                ?: 0L
            readerProgressMarks[chapterId] = TimeSource.Monotonic.markNow()

            val storedChapter = platformServices.databaseHandler.awaitOneOrNull {
                chaptersQueries.getChapterById(chapterId)
            } ?: return@withLock
            val isRead = storedChapter.read || completed
            platformServices.databaseHandler.await(inTransaction = true) {
                chaptersQueries.update(
                    mangaId = null,
                    url = null,
                    name = null,
                    scanlator = null,
                    read = true.takeIf { completed },
                    bookmark = null,
                    lastPageRead = if (isRead) 0L else pageIndex.coerceAtLeast(0).toLong(),
                    chapterNumber = null,
                    sourceOrder = null,
                    dateFetch = null,
                    dateUpload = null,
                    version = null,
                    isSyncing = 0L,
                    ocrReady = null,
                    chapterId = chapterId,
                )
                historyQueries.upsert(
                    chapterId = chapterId,
                    readAt = platformServices.currentTimeMillis(),
                    time_read = elapsedMillis,
                )
            }
        }
    }

    fun close() {
        httpClient.close()
        platformServices.apkExtensionManager.close()
        platformServices.close()
    }

    private suspend fun loadExtensions(): ExtensionStartupState {
        return runCatching {
            val scriptExtensions = scriptExtensionManager.reload()
            val apkExtensions = platformServices.apkExtensionManager.reload()
            val sourceCount = platformServices.sourceRegistry.sources.value.size
            ExtensionStartupState(
                extensionCount = scriptExtensions.size + apkExtensions.size,
                label = "$sourceCount catalogue sources",
                installedExtensions = (
                    scriptExtensions.map(LoadedScriptExtension::toSharedInstalledExtensionEntry) +
                        apkExtensions
                    )
                    .sortedBy { it.name.lowercase() },
            )
        }.getOrElse { error ->
            ExtensionStartupState(
                extensionCount = 0,
                label = "Extension reload failed: ${error.message ?: "unknown error"}",
                installedExtensions = emptyList(),
            )
        }
    }

    private data class ExtensionStartupState(
        val extensionCount: Int,
        val label: String,
        val installedExtensions: List<ChimahonInstalledExtensionEntry>,
    )
}

internal expect class ChimahonPlatformServices() {
    val platformName: String
    val backgroundState: String
    val storageDirectories: PlatformStorageDirectories
    val sourceRegistry: SourceRegistry
    val database: Database
    val databaseHandler: DatabaseHandler
    val javaScriptRuntimeFactory: JavaScriptRuntimeFactory
    val apkExtensionManager: ChimahonPlatformApkExtensionManager

    fun currentTimeMillis(): Long
    fun resolveExternalMangaUrl(source: CatalogueSource, manga: SManga): String?
    fun openExternalUrl(url: String): Boolean
    fun close()
}

internal fun createDatabase(driver: SqlDriver): Database {
    return Database(
        driver = driver,
        mangasAdapter = Mangas.Adapter(
            genreAdapter = StringListColumnAdapter,
        ),
    )
}

private fun Mangas.toSharedMangaEntry(): ChimahonMangaEntry {
    return ChimahonMangaEntry(
        id = _id,
        sourceId = source,
        url = url,
        title = title,
        artist = artist,
        author = author,
        description = description,
        genres = genre.orEmpty(),
        status = mangaStatus(status),
        thumbnailUrl = thumbnail_url,
        favorite = favorite,
        initialized = initialized,
        dateAdded = date_added,
        lastUpdate = last_update,
        notes = notes,
    )
}

internal fun SManga.withSeedFallback(seed: SManga): SManga {
    if (runCatching { url }.getOrNull().isNullOrBlank()) {
        url = seed.url
    }
    if (title.isBlank()) {
        title = seed.title
    }
    if (thumbnail_url.isNullOrBlank()) {
        thumbnail_url = seed.thumbnail_url
    }
    if (author.isNullOrBlank()) {
        author = seed.author
    }
    initialized = true
    return this
}

private fun CatalogueSource.toSharedSourceEntry(): ChimahonSourceEntry {
    return ChimahonSourceEntry(
        id = id,
        name = name,
        language = lang,
        supportsLatest = supportsLatest,
    )
}

private fun UpdatesView.toSharedUpdateEntry(): ChimahonRecentUpdateEntry {
    return ChimahonRecentUpdateEntry(
        mangaId = mangaId,
        chapterId = chapterId,
        mangaTitle = mangaTitle,
        chapterName = chapterName,
        scanlator = scanlator,
        sourceId = source,
        read = read,
        bookmarked = bookmark,
        lastPageRead = last_page_read,
        dateFetch = datefetch,
    )
}

private fun History.toSharedHistoryEntry(): ChimahonHistoryEntry {
    return ChimahonHistoryEntry(
        id = id,
        mangaId = mangaId,
        chapterId = chapterId,
        title = title,
        sourceId = source,
        chapterNumber = chapterNumber,
        read = read,
        lastPageRead = lastPageRead,
        totalCount = totalCountCalculated,
        readCount = readCountCalculated,
        readAt = readAt,
        readDuration = readDuration,
    )
}

private fun Chapters.toSharedChapterEntry(): ChimahonChapterEntry {
    return ChimahonChapterEntry(
        id = _id,
        mangaId = manga_id,
        url = url,
        name = name,
        scanlator = scanlator,
        read = read,
        bookmarked = bookmark,
        lastPageRead = last_page_read,
        chapterNumber = chapter_number,
        sourceOrder = source_order,
        dateFetch = date_fetch,
        dateUpload = date_upload,
    )
}

private fun GetCategories.toSharedLibraryCategory(): ChimahonLibraryCategory {
    return ChimahonLibraryCategory(
        id = id,
        name = name,
        order = order,
        hidden = hidden != 0L,
    )
}

private fun Extension_repos.toSharedExtensionRepoEntry(): ChimahonExtensionRepoEntry {
    return ChimahonExtensionRepoEntry(
        baseUrl = base_url,
        name = name,
        shortName = short_name,
        website = website,
        signingKeyFingerprint = signing_key_fingerprint,
    )
}

private fun LoadedScriptExtension.toRepoExtensionEntry(
    repoBaseUrl: String,
    scriptUrl: String,
): ChimahonRepoExtensionEntry {
    return ChimahonRepoExtensionEntry(
        repoBaseUrl = repoBaseUrl,
        id = manifest.id,
        name = manifest.name,
        version = manifest.version,
        artifactUrl = scriptUrl,
    )
}

private fun LoadedScriptExtension.toSharedInstalledExtensionEntry(): ChimahonInstalledExtensionEntry {
    return ChimahonInstalledExtensionEntry(
        id = manifest.id,
        name = manifest.name,
        version = manifest.version,
        sourceCount = manifest.sources.size,
        packageType = ChimahonExtensionPackageType.JavaScript,
    )
}

private fun SManga.toRemoteMangaEntry(sourceId: Long): ChimahonRemoteMangaEntry {
    return ChimahonRemoteMangaEntry(
        sourceId = sourceId,
        title = title,
        author = author ?: artist,
        status = mangaStatus(status.toLong()),
        url = url,
        thumbnailUrl = thumbnail_url,
        initialized = initialized,
    )
}

private fun SChapter.toRemoteChapterEntry(): ChimahonRemoteChapterEntry {
    return ChimahonRemoteChapterEntry(
        name = name,
        url = url,
        chapterNumber = chapter_number.toDouble(),
        scanlator = scanlator,
        dateUpload = date_upload,
    )
}

private fun normalizeExtensionRepoBaseUrl(input: String): String {
    val cleaned = input.trim().trimEnd('/')
    require(cleaned.startsWith("https://")) { "Extension repo URL must start with https://." }
    val baseUrl = cleaned
        .removeSuffix("/chimahon.json")
        .removeSuffix("/index.json")
        .removeSuffix("/index.min.json")
        .trimEnd('/')
    require(baseUrl.length > "https://".length) { "Extension repo URL is empty." }
    return baseUrl
}

internal fun repoCatalogUrls(baseUrl: String): List<String> {
    return listOf(
        "$baseUrl/chimahon.json",
        "$baseUrl/index.min.json",
        "$baseUrl/index.json",
    )
}

private fun JsonObject.stringValue(vararg keys: String): String? {
    return keys.firstNotNullOfOrNull { key ->
        runCatching { this[key]?.jsonPrimitive?.content }
            .getOrNull()
            ?.takeIf { it.isNotBlank() }
    }
}

private fun JsonObject.intValue(key: String): Int? {
    return runCatching { this[key]?.jsonPrimitive?.content?.toInt() }.getOrNull()
}

internal fun parseRepoExtensions(
    json: Json,
    repoBaseUrl: String,
    payload: String,
): List<ChimahonRepoExtensionEntry> {
    val root = json.parseToJsonElement(payload)
    val entries = when (root) {
        is JsonArray -> root
        is JsonObject -> {
            (root["extensions"] as? JsonArray)
                ?: (root["packages"] as? JsonArray)
                ?: JsonArray(emptyList())
        }
        else -> JsonArray(emptyList())
    }
    val objects = entries.mapNotNull { it as? JsonObject }
    val scriptEntries = objects.mapNotNull { entry ->
        val scriptPath = entry.stringValue(
            "scriptUrl",
            "script_url",
            "downloadUrl",
            "download_url",
            "url",
            "script",
        )?.takeIf { it.startsWith("https://") || it.startsWith("http://") || it.endsWith(".js") }
            ?: return@mapNotNull null
        val scriptUrl = resolveRepoUrl(repoBaseUrl, scriptPath)
        val fallbackId = scriptUrl.substringAfterLast("/").substringBeforeLast(".")
        ChimahonRepoExtensionEntry(
            repoBaseUrl = repoBaseUrl,
            id = entry.stringValue("id", "pkg", "package") ?: fallbackId,
            name = entry.stringValue("name") ?: fallbackId,
            version = entry.stringValue("version", "versionName", "version_name") ?: "remote",
            artifactUrl = scriptUrl,
        )
    }
    val apkEntries = objects.mapNotNull { entry ->
        val apkName = entry.stringValue("apk") ?: return@mapNotNull null
        val packageName = entry.stringValue("pkg", "package", "id") ?: return@mapNotNull null
        val name = entry.stringValue("name")
            ?.substringAfter("Tachiyomi: ")
            ?.substringAfter("Mihon: ")
            ?: packageName.substringAfterLast(".")
        ChimahonRepoExtensionEntry(
            repoBaseUrl = repoBaseUrl,
            id = packageName,
            name = name,
            version = entry.stringValue("version", "versionName", "version_name") ?: "remote",
            artifactUrl = resolveRepoUrl(repoBaseUrl, "apk/$apkName"),
            packageType = ChimahonExtensionPackageType.AndroidApk,
            language = entry.stringValue("lang", "language").orEmpty(),
            sourceCount = (entry["sources"] as? JsonArray)?.size ?: 0,
            isNsfw = entry.intValue("nsfw") == 1,
        )
    }
    return scriptEntries + apkEntries
}

private fun resolveRepoUrl(baseUrl: String, value: String): String {
    if (value.startsWith("https://") || value.startsWith("http://")) return value
    if (value.startsWith("/")) {
        return "https://${baseUrl.removePrefix("https://").substringBefore("/")}$value"
    }
    return "${baseUrl.trimEnd('/')}/${value.trimStart('/')}"
}

private fun String.normalizedExternalWebUrlOrNull(): String? {
    val normalized = trim()
    if (normalized.any(Char::isWhitespace)) return null
    return normalized.takeIf {
        it.startsWith("https://", ignoreCase = true) ||
            it.startsWith("http://", ignoreCase = true)
    }
}

private fun String.repoHost(): String {
    return removePrefix("https://")
        .removePrefix("http://")
        .substringBefore("/")
        .ifBlank { "Extension repo" }
}

private fun String.stableRepoHash(): String {
    var hash = 1125899906842597L
    forEach { character ->
        hash = (hash * 31) + character.code
    }
    return hash.toString().replace("-", "n")
}

private fun mangaStatus(status: Long): String = when (status) {
    1L -> "Ongoing"
    2L -> "Completed"
    3L -> "Licensed"
    4L -> "Publishing finished"
    5L -> "Cancelled"
    6L -> "On hiatus"
    else -> "Unknown"
}

private fun formatTimestamp(epochSeconds: Long): String {
    if (epochSeconds <= 0L) return "Unknown time"
    return "Unix $epochSeconds"
}

internal const val APP_NAME = "chimahon"
internal const val DATABASE_DIRECTORY = "database"
internal const val DATABASE_NAME = "chimahon.db"
internal const val SETTINGS_FILE_NAME = "chimahon.settings"

private const val CHAPTER_SHOW_BOOKMARKED = 0x00000020L
private const val CHAPTER_SHOW_NOT_BOOKMARKED = 0x00000040L
private const val DEFAULT_LIBRARY_CATEGORY_ID = 0L
private const val MAX_THUMBNAIL_CACHE_ENTRIES = 192
private const val SCRIPT_EXTENSION_SUFFIX = ".js"
