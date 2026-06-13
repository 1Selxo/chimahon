package app.chimahon.shared

import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.SourceRegistry
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ChimahonSourcePaginationTest {
    @Test
    fun loadSourcePreviewPassesRequestedPageNumbersToSourceCallbacks() = runBlocking {
        val source = RecordingCatalogueSource(supportsLatest = true)
        val services = chimahonServiceForTest(
            sourceRegistry = SourceRegistry(listOf(source)),
        )

        val popular = services.loadSourcePreview(
            sourceId = source.id,
            mode = ChimahonSourceBrowseMode.Popular,
            query = "",
            pageNumber = 2,
        )
        val latest = services.loadSourcePreview(
            sourceId = source.id,
            mode = ChimahonSourceBrowseMode.Latest,
            query = "",
            pageNumber = 3,
        )
        val search = services.loadSourcePreview(
            sourceId = source.id,
            mode = ChimahonSourceBrowseMode.Search,
            query = "space cats",
            pageNumber = 4,
        )

        assertEquals(
            listOf(
                SourceCall("popular", 2),
                SourceCall("latest", 3),
                SourceCall("search", 4, "space cats"),
            ),
            source.calls,
        )
        assertEquals(listOf("popular-page-2"), popular.entries.map { it.title })
        assertEquals(listOf("latest-page-3"), latest.entries.map { it.title })
        assertEquals(listOf("search-space cats-page-4"), search.entries.map { it.title })
        assertTrue(popular.hasNextPage)
    }

    @Test
    fun latestFallsBackToPopularWithSamePageWhenSourceDoesNotSupportLatest() = runBlocking {
        val source = RecordingCatalogueSource(supportsLatest = false)
        val services = chimahonServiceForTest(
            sourceRegistry = SourceRegistry(listOf(source)),
        )

        val preview = services.loadSourcePreview(
            sourceId = source.id,
            mode = ChimahonSourceBrowseMode.Latest,
            query = "",
            pageNumber = 5,
        )

        assertEquals(listOf(SourceCall("popular", 5)), source.calls)
        assertEquals(listOf("popular-page-5"), preview.entries.map { it.title })
        assertEquals(ChimahonSourceBrowseMode.Latest, preview.mode)
    }

    @Test
    fun loadSourcePreviewRejectsInvalidPageAndBlankSearchBeforeCallingSource() = runBlocking {
        val source = RecordingCatalogueSource()
        val services = chimahonServiceForTest(
            sourceRegistry = SourceRegistry(listOf(source)),
        )

        assertFailsWith<IllegalArgumentException> {
            services.loadSourcePreview(
                sourceId = source.id,
                mode = ChimahonSourceBrowseMode.Popular,
                query = "",
                pageNumber = 0,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            services.loadSourcePreview(
                sourceId = source.id,
                mode = ChimahonSourceBrowseMode.Search,
                query = " ",
                pageNumber = 1,
            )
        }
        assertTrue(source.calls.isEmpty())
    }

    private data class SourceCall(
        val kind: String,
        val page: Int,
        val query: String? = null,
    )

    private class RecordingCatalogueSource(
        override val supportsLatest: Boolean = true,
    ) : CatalogueSource {
        override val id: Long = 77L
        override val name: String = "Recording Source"
        override val lang: String = "en"
        val calls = mutableListOf<SourceCall>()

        override suspend fun getPopularManga(page: Int): MangasPage {
            calls += SourceCall("popular", page)
            return mangasPage("popular-page-$page", hasNextPage = page < 9)
        }

        override suspend fun getSearchManga(
            page: Int,
            query: String,
            filters: FilterList,
        ): MangasPage {
            calls += SourceCall("search", page, query)
            return mangasPage("search-$query-page-$page", hasNextPage = false)
        }

        override suspend fun getLatestUpdates(page: Int): MangasPage {
            calls += SourceCall("latest", page)
            return mangasPage("latest-page-$page", hasNextPage = false)
        }

        override fun getFilterList(): FilterList = FilterList()

        override suspend fun getMangaDetails(manga: SManga): SManga = manga

        override suspend fun getChapterList(manga: SManga): List<SChapter> = emptyList()

        override suspend fun getPageList(chapter: SChapter): List<Page> = emptyList()

        private fun mangasPage(title: String, hasNextPage: Boolean): MangasPage {
            return MangasPage(
                mangas = listOf(
                    SManga(
                        url = "/$title",
                        title = title,
                        author = "Author",
                        status = SManga.ONGOING,
                        thumbnail_url = "https://example.test/$title.jpg",
                        initialized = true,
                    ),
                ),
                hasNextPage = hasNextPage,
            )
        }
    }
}
