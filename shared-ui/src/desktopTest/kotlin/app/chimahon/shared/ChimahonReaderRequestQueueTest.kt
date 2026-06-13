package app.chimahon.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ChimahonReaderRequestQueueTest {
    @Test
    fun remoteChaptersBuildReaderRequestsWithAdjacentQueueEntries() {
        val detail = remoteDetail()
        val chapters = listOf(
            remoteChapter("chapter-1", "Chapter 1", 1.0),
            remoteChapter("chapter-2", "Chapter 2", 2.0),
            remoteChapter("chapter-3", "Chapter 3", 3.0),
        )

        val request = chapters[1].toPrivateReaderRequest(detail, chapters)

        assertEquals(detail.sourceId, request.sourceId)
        assertEquals(detail.title, request.mangaTitle)
        assertEquals("Chapter 2", request.chapterName)
        assertEquals("chapter-2", request.chapterUrl)
        assertNull(request.mangaId)
        assertNull(request.chapterId)
        assertEquals(1, request.chapterIndex)
        assertEquals(
            listOf("chapter-1", "chapter-2", "chapter-3"),
            request.chapterQueue.map { it.chapterUrl },
        )

        val previous = request.chapterQueue.first().toPrivateReaderRequest(request)
        assertEquals("Chapter 1", previous.chapterName)
        assertEquals("chapter-1", previous.chapterUrl)
        assertEquals(0, previous.chapterIndex)
        assertEquals(request.chapterQueue, previous.chapterQueue)
    }

    @Test
    fun libraryChaptersBuildReaderRequestsWithIdsAndClampedInitialPages() {
        val manga = mangaEntry()
        val chapters = listOf(
            chapterEntry(id = 101L, url = "chapter-1", lastPageRead = -4L),
            chapterEntry(
                id = 102L,
                url = "chapter-2",
                name = "Chapter 2",
                chapterNumber = 2.0,
                lastPageRead = Int.MAX_VALUE.toLong() + 100L,
            ),
        )

        val request = chapters.first().toPrivateReaderRequest(manga, chapters)

        assertEquals(manga.id, request.mangaId)
        assertEquals(chapters.first().id, request.chapterId)
        assertEquals(0, request.initialPage)
        assertEquals(0, request.chapterIndex)
        assertEquals(listOf(101L, 102L), request.chapterQueue.map { it.chapterId })

        val next = request.chapterQueue.last().toPrivateReaderRequest(request)
        assertEquals("Chapter 2", next.chapterName)
        assertEquals(102L, next.chapterId)
        assertEquals(Int.MAX_VALUE, next.initialPage)
        assertEquals(1, next.chapterIndex)
        assertEquals(manga.sourceId, next.sourceId)
        assertEquals(manga.title, next.mangaTitle)
    }

    @Test
    fun adjacentNavigationPreservesDescendingQueueAndPerChapterProgress() {
        val manga = mangaEntry()
        val chapters = listOf(
            chapterEntry(id = 103L, url = "chapter-3", chapterNumber = 3.0, lastPageRead = 12L),
            chapterEntry(id = 102L, url = "chapter-2", chapterNumber = 2.0, lastPageRead = 6L),
            chapterEntry(id = 101L, url = "chapter-1", chapterNumber = 1.0, lastPageRead = 2L),
        )

        val current = chapters[1].toPrivateReaderRequest(manga, chapters)
        val newer = current.chapterQueue[0].toPrivateReaderRequest(current)
        val older = current.chapterQueue[2].toPrivateReaderRequest(current)

        assertEquals(listOf(3.0, 2.0, 1.0), current.chapterQueue.map { it.chapterNumber })
        assertEquals(1, current.chapterIndex)
        assertEquals(0, newer.chapterIndex)
        assertEquals(103L, newer.chapterId)
        assertEquals(12, newer.initialPage)
        assertEquals(2, older.chapterIndex)
        assertEquals(101L, older.chapterId)
        assertEquals(2, older.initialPage)
        assertEquals(current.chapterQueue, newer.chapterQueue)
        assertEquals(current.chapterQueue, older.chapterQueue)
        assertEquals(current.mangaId, newer.mangaId)
        assertEquals(current.sourceId, older.sourceId)
    }

    private fun ChimahonRemoteChapterEntry.toPrivateReaderRequest(
        detail: ChimahonRemoteMangaDetail,
        chapters: List<ChimahonRemoteChapterEntry>,
    ): ChimahonReaderRequest {
        @Suppress("UNCHECKED_CAST")
        return invokePrivateChimahonAppFunction(
            name = "toReaderRequest",
            parameterTypes = listOf(
                ChimahonRemoteChapterEntry::class.java,
                ChimahonRemoteMangaDetail::class.java,
                List::class.java,
            ),
            this,
            detail,
            chapters,
        ) as ChimahonReaderRequest
    }

    private fun ChimahonChapterEntry.toPrivateReaderRequest(
        manga: ChimahonMangaEntry,
        chapters: List<ChimahonChapterEntry>,
    ): ChimahonReaderRequest {
        @Suppress("UNCHECKED_CAST")
        return invokePrivateChimahonAppFunction(
            name = "toReaderRequest",
            parameterTypes = listOf(
                ChimahonChapterEntry::class.java,
                ChimahonMangaEntry::class.java,
                List::class.java,
            ),
            this,
            manga,
            chapters,
        ) as ChimahonReaderRequest
    }

    private fun ChimahonReaderChapterRef.toPrivateReaderRequest(
        current: ChimahonReaderRequest,
    ): ChimahonReaderRequest {
        @Suppress("UNCHECKED_CAST")
        return invokePrivateChimahonAppFunction(
            name = "toReaderRequest",
            parameterTypes = listOf(
                ChimahonReaderChapterRef::class.java,
                ChimahonReaderRequest::class.java,
            ),
            this,
            current,
        ) as ChimahonReaderRequest
    }

    private fun remoteDetail() = ChimahonRemoteMangaDetail(
        sourceId = 44L,
        sourceName = "Remote source",
        title = "Remote manga",
        artist = null,
        author = "Remote author",
        description = null,
        genres = emptyList(),
        status = "Ongoing",
        statusCode = 1L,
        url = "remote-manga",
        thumbnailUrl = null,
        initialized = true,
        chapters = emptyList(),
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
        dateUpload = 100L + number.toLong(),
    )

    private fun mangaEntry() = ChimahonMangaEntry(
        id = 9L,
        sourceId = 55L,
        url = "library-manga",
        title = "Library manga",
        artist = null,
        author = null,
        description = null,
        genres = emptyList(),
        status = "Ongoing",
        thumbnailUrl = null,
        favorite = true,
        initialized = true,
        dateAdded = 1L,
        lastUpdate = null,
        notes = "",
    )

    private fun chapterEntry(
        id: Long,
        url: String,
        name: String = "Chapter 1",
        chapterNumber: Double = 1.0,
        lastPageRead: Long = 0L,
    ) = ChimahonChapterEntry(
        id = id,
        mangaId = 9L,
        url = url,
        name = name,
        scanlator = null,
        read = false,
        bookmarked = false,
        lastPageRead = lastPageRead,
        chapterNumber = chapterNumber,
        sourceOrder = id,
        dateFetch = 0L,
        dateUpload = 0L,
    )
}
