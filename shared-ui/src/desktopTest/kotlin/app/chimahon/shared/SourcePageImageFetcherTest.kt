package app.chimahon.shared

import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SourcePageImageFetcherTest {
    @Test
    fun cachesDefensiveCopiesAndHonorsRefreshAndBypassPolicies() = runBlocking {
        var loads = 0
        val fetcher = testFetcher { _, _, _ ->
            loads += 1
            byteArrayOf(loads.toByte(), 2, 3)
        }
        val page = Page(index = 0, imageUrl = "https://example.test/0.jpg")

        val first = fetcher.fetch(TEST_SOURCE, page, SourceImageFetchOptions())
        first[0] = 99
        val cached = fetcher.fetch(TEST_SOURCE, page, SourceImageFetchOptions())
        val refreshed = fetcher.fetch(
            TEST_SOURCE,
            page,
            SourceImageFetchOptions(cachePolicy = SourceImageCachePolicy.REFRESH),
        )
        val bypassed = fetcher.fetch(
            TEST_SOURCE,
            page,
            SourceImageFetchOptions(cachePolicy = SourceImageCachePolicy.BYPASS),
        )
        val cachedAfterBypass = fetcher.fetch(TEST_SOURCE, page, SourceImageFetchOptions())

        assertContentEquals(byteArrayOf(1, 2, 3), cached)
        assertContentEquals(byteArrayOf(2, 2, 3), refreshed)
        assertContentEquals(byteArrayOf(3, 2, 3), bypassed)
        assertContentEquals(byteArrayOf(2, 2, 3), cachedAfterBypass)
        assertEquals(3, loads)
    }

    @Test
    fun coalescesConcurrentRequestsForTheSamePage() = runBlocking {
        var loads = 0
        val started = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val fetcher = testFetcher { _, _, _ ->
            loads += 1
            started.complete(Unit)
            release.await()
            byteArrayOf(4, 5, 6)
        }
        val page = Page(index = 2, imageUrl = "https://example.test/2.jpg")

        coroutineScope {
            val first = async { fetcher.fetch(TEST_SOURCE, page, SourceImageFetchOptions()) }
            started.await()
            val second = async { fetcher.fetch(TEST_SOURCE, page, SourceImageFetchOptions()) }
            yield()
            release.complete(Unit)

            assertContentEquals(first.await(), second.await())
        }
        assertEquals(1, loads)
    }

    @Test
    fun cacheKeysIncludeHeadersAndReferer() = runBlocking {
        var loads = 0
        val fetcher = testFetcher { _, _, _ ->
            loads += 1
            byteArrayOf(loads.toByte())
        }
        val page = Page(index = 0, imageUrl = "https://example.test/image.jpg")

        fetcher.fetch(TEST_SOURCE, page, SourceImageFetchOptions(referer = "https://one.test"))
        fetcher.fetch(TEST_SOURCE, page, SourceImageFetchOptions(referer = "https://two.test"))
        fetcher.fetch(
            TEST_SOURCE,
            page,
            SourceImageFetchOptions(headers = mapOf("Referer" to "https://one.test")),
        )

        assertEquals(2, loads)
    }

    @Test
    fun keepsTheCacheKeyStableWhenLoadingResolvesTheImageUrl() = runBlocking {
        var loads = 0
        val fetcher = testFetcher { _, page, _ ->
            loads += 1
            page.imageUrl = "https://cdn.example.test/resolved.jpg"
            byteArrayOf(7, 8, 9)
        }
        val page = Page(index = 0, url = "page-token")

        fetcher.fetch(TEST_SOURCE, page, SourceImageFetchOptions())
        fetcher.fetch(TEST_SOURCE, page, SourceImageFetchOptions())

        assertEquals(1, loads)
    }

    @Test
    fun reportsEmptyResponsesAndInvalidHeadersClearly() = runBlocking {
        val emptyFetcher = testFetcher { _, _, _ -> byteArrayOf() }
        val page = Page(index = 4, imageUrl = "https://example.test/4.jpg")

        val emptyError = assertFailsWith<SourceImageFetchException> {
            emptyFetcher.fetch(TEST_SOURCE, page, SourceImageFetchOptions())
        }
        val headerError = assertFailsWith<SourceImageFetchException> {
            emptyFetcher.fetch(
                TEST_SOURCE,
                page,
                SourceImageFetchOptions(headers = mapOf("X-Test" to "bad\nvalue")),
            )
        }

        assertEquals(SourceImageFetchFailure.EMPTY_RESPONSE, emptyError.failure)
        assertEquals(SourceImageFetchFailure.INVALID_REQUEST, headerError.failure)
        assertEquals(TEST_SOURCE.id, emptyError.sourceId)
        assertEquals(page.index, emptyError.pageIndex)
    }

    @Test
    fun clearsOnlyTheRequestedSourcesCacheEntries() = runBlocking {
        var loads = 0
        val fetcher = testFetcher { _, _, _ ->
            loads += 1
            byteArrayOf(loads.toByte())
        }
        val otherSource = TestCatalogueSource(id = 43L, name = "Other source")
        val page = Page(index = 0, imageUrl = "https://example.test/image.jpg")

        fetcher.fetch(TEST_SOURCE, page, SourceImageFetchOptions())
        fetcher.fetch(otherSource, page, SourceImageFetchOptions())
        fetcher.clear(TEST_SOURCE.id)

        val stats = fetcher.stats()
        fetcher.fetch(TEST_SOURCE, page, SourceImageFetchOptions())
        fetcher.fetch(otherSource, page, SourceImageFetchOptions())

        assertEquals(1, stats.entryCount)
        assertEquals(3, loads)
    }

    private fun testFetcher(
        loader: suspend (
            source: CatalogueSource,
            page: Page,
            options: SourceImageFetchOptions,
        ) -> ByteArray,
    ): SourcePageImageFetcher {
        return SourcePageImageFetcher(
            maxCacheEntries = 4,
            maxCacheBytes = 1_024,
            loadUncached = loader,
        )
    }

    private companion object {
        val TEST_SOURCE = TestCatalogueSource(id = 42L, name = "Test source")
    }
}

private class TestCatalogueSource(
    override val id: Long,
    override val name: String,
) : CatalogueSource {
    override val lang: String = "en"
    override val supportsLatest: Boolean = false

    override suspend fun getPopularManga(page: Int): MangasPage = unsupported()

    override suspend fun getSearchManga(
        page: Int,
        query: String,
        filters: FilterList,
    ): MangasPage = unsupported()

    override suspend fun getLatestUpdates(page: Int): MangasPage = unsupported()

    override fun getFilterList(): FilterList = FilterList()

    override suspend fun getMangaDetails(manga: SManga): SManga = unsupported()

    override suspend fun getChapterList(manga: SManga): List<SChapter> = unsupported()

    override suspend fun getPageList(chapter: SChapter): List<Page> = unsupported()

    private fun <T> unsupported(): T = error("Not used by source image fetcher tests.")
}
