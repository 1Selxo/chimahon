package app.chimahon.shared

import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath
import tachiyomi.core.database.DesktopDatabaseDriverFactory
import tachiyomi.data.Database
import tachiyomi.data.DesktopDatabaseHandler
import java.nio.file.Files
import java.nio.file.Path
import java.util.Comparator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChimahonLibraryImporterTest {
    @Test
    fun importsAndMergesRemoteMangaWithoutLosingChapterState() = runBlocking {
        val root = Files.createTempDirectory("chimahon-library-import-test")
        val driver = DesktopDatabaseDriverFactory(root.toString().toPath())
            .create(Database.Schema, "test.db")
        val database = createDatabase(driver)
        val handler = DesktopDatabaseHandler(database, driver)
        try {
            val detail = remoteDetail(
                chapters = listOf(
                    remoteChapter("chapter-2", "Chapter 2", 2.0),
                    remoteChapter("chapter-1", "Chapter 1", 1.0),
                    remoteChapter("chapter-1", "Duplicate chapter", 1.0),
                ),
            )
            val mangaId = importRemoteMangaToLibrary(handler, detail, now = 1_000L)

            val manga = handler.awaitOne { mangasQueries.getMangaById(mangaId) }
            assertTrue(manga.favorite)
            assertEquals(detail.title, manga.title)
            val firstImport = handler.awaitList {
                chaptersQueries.getChaptersByMangaId(mangaId, 0L, 0L, 0L)
            }
            assertEquals(2, firstImport.size)
            assertEquals(listOf(0L, 1L), firstImport.sortedBy { it.source_order }.map { it.source_order })

            val chapterOne = firstImport.single { it.url == "chapter-1" }
            updateMangaFavorite(handler, mangaId, favorite = false, now = 1_500L)
            handler.awaitOne { mangasQueries.getMangaById(mangaId) }.let { updated ->
                assertFalse(updated.favorite)
                assertEquals(0L, updated.date_added)
            }
            updateMangaFavorite(handler, mangaId, favorite = true, now = 1_600L)
            handler.awaitOne { mangasQueries.getMangaById(mangaId) }.let { updated ->
                assertTrue(updated.favorite)
                assertEquals(1_600L, updated.date_added)
            }
            updateChapterRead(handler, chapterOne._id, read = true)
            assertTrue(handler.awaitOne { chaptersQueries.getChapterById(chapterOne._id) }.read)
            updateChapterRead(handler, chapterOne._id, read = false)
            handler.awaitOne { chaptersQueries.getChapterById(chapterOne._id) }.let { updated ->
                assertFalse(updated.read)
                assertEquals(0L, updated.last_page_read)
            }
            updateChapterBookmark(handler, chapterOne._id, bookmarked = true)
            assertTrue(handler.awaitOne { chaptersQueries.getChapterById(chapterOne._id) }.bookmark)
            updateMangaChaptersRead(handler, mangaId, read = true)
            assertTrue(
                handler.awaitList { chaptersQueries.getChaptersByMangaId(mangaId, 0L, 0L, 0L) }
                    .all { it.read },
            )
            updateMangaChaptersRead(handler, mangaId, read = false)
            assertTrue(
                handler.awaitList { chaptersQueries.getChaptersByMangaId(mangaId, 0L, 0L, 0L) }
                    .none { it.read || it.last_page_read > 0L },
            )
            handler.await {
                chaptersQueries.update(
                    mangaId = null,
                    url = null,
                    name = null,
                    scanlator = null,
                    read = true,
                    bookmark = true,
                    lastPageRead = 7L,
                    chapterNumber = null,
                    sourceOrder = null,
                    dateFetch = null,
                    dateUpload = null,
                    version = null,
                    isSyncing = 0L,
                    ocrReady = null,
                    chapterId = chapterOne._id,
                )
            }

            val secondMangaId = importRemoteMangaToLibrary(
                databaseHandler = handler,
                detail = remoteDetail(
                    title = "Test manga refreshed",
                    chapters = listOf(
                        remoteChapter("chapter-3", "Chapter 3", 3.0),
                        remoteChapter("chapter-1-renamed", "Chapter 1 revised", 1.0),
                    ),
                ),
                now = 2_000L,
            )

            assertEquals(mangaId, secondMangaId)
            assertEquals(1, handler.awaitList { mangasQueries.getAllManga() }.size)
            assertEquals(
                "Test manga refreshed",
                handler.awaitOne { mangasQueries.getMangaById(mangaId) }.title,
            )
            val merged = handler.awaitList {
                chaptersQueries.getChaptersByMangaId(mangaId, 0L, 0L, 0L)
            }
            assertEquals(2, merged.size)
            assertTrue(merged.none { it.url == "chapter-2" })
            val preserved = merged.single { it.url == "chapter-1-renamed" }
            assertTrue(preserved.read)
            assertTrue(preserved.bookmark)
            assertEquals(7L, preserved.last_page_read)
            assertEquals("Chapter 1 revised", preserved.name)
            assertEquals(1L, preserved.source_order)

            importRemoteMangaToLibrary(
                databaseHandler = handler,
                detail = remoteDetail(title = "Test manga still available", chapters = emptyList()),
                now = 3_000L,
            )
            assertEquals(
                2,
                handler.awaitList {
                    chaptersQueries.getChaptersByMangaId(mangaId, 0L, 0L, 0L)
                }.size,
            )
        } finally {
            driver.close()
            root.deleteRecursively()
        }
    }

    private fun remoteDetail(
        title: String = "Test manga",
        chapters: List<ChimahonRemoteChapterEntry>,
    ) = ChimahonRemoteMangaDetail(
        sourceId = 42L,
        sourceName = "Test source",
        title = title,
        artist = "Artist",
        author = "Author",
        description = "Description",
        genres = listOf("Action"),
        status = "Ongoing",
        statusCode = 1L,
        url = "test-manga",
        thumbnailUrl = "https://example.com/cover.jpg",
        initialized = true,
        chapters = chapters,
    )

    private fun remoteChapter(
        url: String,
        name: String,
        number: Double,
    ) = ChimahonRemoteChapterEntry(
        name = name,
        url = url,
        chapterNumber = number,
        scanlator = null,
        dateUpload = 100L,
    )

    private fun Path.deleteRecursively() {
        if (Files.notExists(this)) return
        Files.walk(this).use { paths ->
            paths.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists)
        }
    }
}
