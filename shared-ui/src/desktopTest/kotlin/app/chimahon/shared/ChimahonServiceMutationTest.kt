package app.chimahon.shared

import kotlinx.coroutines.runBlocking
import tachiyomi.data.DatabaseHandler
import tachiyomi.data.GetCategoriesByMangaId
import tachiyomi.data.History
import tachiyomi.data.LibraryUpdateError
import tachiyomi.data.Mangas
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChimahonServiceMutationTest {
    @Test
    fun categoryServiceMutationsCreateAssignDefaultAndDeleteCategories() = runBlocking {
        createSharedUiTestDatabase("chimahon-category-service-test").use { testDb ->
            val handler = testDb.handler
            val services = chimahonServiceForTest(databaseHandler = handler)
            val mangaId = handler.insertManga()

            val categoryId = services.createCategory("  Reading  ")

            val categories = handler.awaitList { categoriesQueries.getCategories() }
            assertEquals(listOf("", "Reading"), categories.map { it.name })
            assertEquals(0L, categories.single { it.id == categoryId }.order)

            assertFailsWith<IllegalArgumentException> {
                services.createCategory("reading")
            }
            assertFailsWith<IllegalArgumentException> {
                services.deleteCategory(0L)
            }

            services.setMangaCategories(mangaId, setOf(-1L, categoryId))
            assertEquals(
                listOf(categoryId),
                handler.categoryIdsForManga(mangaId),
            )

            services.setMangaCategories(mangaId, emptySet())
            assertEquals(listOf(0L), handler.categoryIdsForManga(mangaId))

            services.deleteCategory(categoryId)
            assertEquals(listOf(0L), handler.awaitList { categoriesQueries.getCategories() }.map { it.id })
            assertEquals(emptyList(), handler.categoryIdsForManga(mangaId))
        }
    }

    @Test
    fun historyServiceMutationsResetSingleMangaAndAllHistoryRows() = runBlocking {
        createSharedUiTestDatabase("chimahon-history-service-test").use { testDb ->
            val handler = testDb.handler
            val services = chimahonServiceForTest(databaseHandler = handler)
            val firstMangaId = handler.insertManga(url = "/history-one")
            val secondMangaId = handler.insertManga(url = "/history-two")
            val firstChapterId = handler.insertChapter(firstMangaId, url = "/chapter-1")
            val secondChapterId = handler.insertChapter(firstMangaId, url = "/chapter-2")
            val otherMangaChapterId = handler.insertChapter(secondMangaId, url = "/chapter-3")

            handler.insertHistory(firstChapterId, readAt = 1_000L)
            handler.insertHistory(secondChapterId, readAt = 2_000L)
            handler.insertHistory(otherMangaChapterId, readAt = 3_000L)

            val firstHistoryId = handler.historyRows()
                .single { it.chapter_id == firstChapterId }
                ._id
            services.resetHistoryEntry(firstHistoryId)
            assertEquals(
                listOf(secondChapterId, otherMangaChapterId),
                handler.historyRows().map { it.chapter_id }.sorted(),
            )

            services.resetHistoryForManga(firstMangaId)
            assertEquals(listOf(otherMangaChapterId), handler.historyRows().map { it.chapter_id })

            handler.insertHistory(firstChapterId, readAt = 4_000L)
            services.clearHistory()
            assertEquals(emptyList(), handler.historyRows())
        }
    }

    @Test
    fun updateIssueServiceMutationsDismissSingleIssueAndClearRemainingIssues() = runBlocking {
        createSharedUiTestDatabase("chimahon-update-issue-service-test").use { testDb ->
            val handler = testDb.handler
            val services = chimahonServiceForTest(databaseHandler = handler)
            val firstMangaId = handler.insertManga(url = "/issue-one")
            val secondMangaId = handler.insertManga(url = "/issue-two")

            handler.insertUpdateIssue(firstMangaId, messageId = 10L)
            handler.insertUpdateIssue(secondMangaId, messageId = 20L)
            val firstIssueId = handler.updateIssues()
                .single { it.manga_id == firstMangaId }
                ._id

            services.dismissUpdateIssue(firstIssueId)
            handler.updateIssues().single().let { issue ->
                assertEquals(secondMangaId, issue.manga_id)
                assertEquals(20L, issue.message_id)
            }

            services.clearUpdateIssues()
            assertEquals(emptyList(), handler.updateIssues())
        }
    }

    @Test
    fun readerProgressMutationStoresPageHistoryAndCompletion() = runBlocking {
        createSharedUiTestDatabase("chimahon-reader-progress-test").use { testDb ->
            val handler = testDb.handler
            val services = chimahonServiceForTest(databaseHandler = handler)
            val mangaId = handler.insertManga(url = "/reader-progress")
            val chapterId = handler.insertChapter(mangaId, url = "/reader-chapter")
            val request = ChimahonReaderRequest(
                sourceId = 1L,
                mangaTitle = "Reader manga",
                chapterName = "Chapter 1",
                chapterUrl = "/reader-chapter",
                chapterNumber = 1.0,
                scanlator = null,
                dateUpload = 0L,
                mangaId = mangaId,
                chapterId = chapterId,
            )

            services.saveReaderProgress(request, pageIndex = 4, completed = false)
            handler.awaitOne { chaptersQueries.getChapterById(chapterId) }.let { chapter ->
                assertFalse(chapter.read)
                assertEquals(4L, chapter.last_page_read)
            }
            assertEquals(listOf(chapterId), handler.historyRows().map { it.chapter_id })

            services.saveReaderProgress(request, pageIndex = 9, completed = true)
            handler.awaitOne { chaptersQueries.getChapterById(chapterId) }.let { chapter ->
                assertTrue(chapter.read)
                assertEquals(0L, chapter.last_page_read)
            }
            assertEquals(1, handler.historyRows().size)
        }
    }

    private suspend fun DatabaseHandler.insertManga(
        source: Long = 1L,
        url: String = "/manga",
        title: String = "Manga $url",
    ): Long {
        await {
            mangasQueries.insert(
                source = source,
                url = url,
                artist = null,
                author = null,
                description = null,
                genre = listOf("Action"),
                title = title,
                status = 1L,
                thumbnailUrl = null,
                favorite = true,
                lastUpdate = null,
                nextUpdate = null,
                initialized = true,
                viewerFlags = 0L,
                chapterFlags = 0L,
                coverLastModified = 0L,
                dateAdded = 1L,
                updateStrategy = 0L,
                calculateInterval = 0L,
                version = 0L,
                notes = "",
            )
        }
        return awaitOne { mangasQueries.getMangaByUrlAndSource(url, source) }._id
    }

    private suspend fun DatabaseHandler.insertChapter(
        mangaId: Long,
        url: String,
        name: String = "Chapter $url",
    ): Long {
        await {
            chaptersQueries.insert(
                mangaId = mangaId,
                url = url,
                name = name,
                scanlator = null,
                read = false,
                bookmark = false,
                lastPageRead = 0L,
                chapterNumber = 1.0,
                sourceOrder = 0L,
                dateFetch = 0L,
                dateUpload = 0L,
                version = 0L,
            )
        }
        return awaitOne { chaptersQueries.getChapterByUrlAndMangaId(url, mangaId) }._id
    }

    private suspend fun DatabaseHandler.categoryIdsForManga(mangaId: Long): List<Long> {
        return awaitList { categoriesQueries.getCategoriesByMangaId(mangaId) }
            .map(GetCategoriesByMangaId::id)
    }

    private suspend fun DatabaseHandler.insertHistory(chapterId: Long, readAt: Long) {
        await {
            historyQueries.upsert(
                chapterId = chapterId,
                readAt = readAt,
                time_read = 123L,
            )
        }
    }

    private suspend fun DatabaseHandler.historyRows(): List<History> {
        return awaitList { historyQueries.getAllHistory() }
    }

    private suspend fun DatabaseHandler.insertUpdateIssue(mangaId: Long, messageId: Long) {
        await {
            libraryUpdateErrorQueries.insert(
                mangaId = mangaId,
                messageId = messageId,
            )
        }
    }

    private suspend fun DatabaseHandler.updateIssues(): List<LibraryUpdateError> {
        return awaitList { libraryUpdateErrorQueries.getAllErrors() }
    }
}
