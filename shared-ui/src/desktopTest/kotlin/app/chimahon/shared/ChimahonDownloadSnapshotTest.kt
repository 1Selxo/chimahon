package app.chimahon.shared

import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChimahonDownloadSnapshotTest {
    @Test
    fun combinesQueueAndDownloadedIndexByChapterId() {
        val queue = ChimahonDownloadQueueData(
            entries = listOf(
                downloadEntry(
                    chapterId = 1L,
                    status = ChimahonDownloadState.Queued,
                    progress = 12,
                ),
                downloadEntry(
                    chapterId = 2L,
                    status = ChimahonDownloadState.Error,
                    progress = 58,
                    downloadedBytes = 32L,
                    errorMessage = "Network failed",
                ),
            ),
            paused = false,
        )
        val downloadedIndex = ChimahonDownloadedIndex(
            chapterPaths = mapOf(
                2L to "/downloads/manga/chapter-2.cbz".toPath(),
                3L to "/downloads/manga/chapter-3.cbz".toPath(),
            ),
            chapterSizes = mapOf(
                2L to 64L,
                3L to 128L,
            ),
            mangaIds = setOf(10L),
        )

        val snapshot = buildChimahonDownloadSnapshot(
            queue = queue,
            downloadedIndex = downloadedIndex,
        )

        val queued = snapshot.statusForChapter(1L)!!
        assertEquals(ChimahonDownloadState.Queued, queued.status)
        assertEquals(12, queued.progress)
        assertFalse(queued.downloadedOnDisk)

        val failedDownloaded = snapshot.statusForChapter(2L)!!
        assertEquals(ChimahonDownloadState.Error, failedDownloaded.status)
        assertEquals(58, failedDownloaded.progress)
        assertEquals(32L, failedDownloaded.downloadedBytes)
        assertTrue(failedDownloaded.downloadedOnDisk)
        assertEquals("Network failed", failedDownloaded.errorMessage)

        val downloadedOnly = snapshot.statusForChapter(3L)!!
        assertEquals(ChimahonDownloadState.Downloaded, downloadedOnly.status)
        assertEquals(100, downloadedOnly.progress)
        assertEquals(128L, downloadedOnly.downloadedBytes)
        assertTrue(downloadedOnly.downloadedOnDisk)
        assertEquals(queue, snapshot.queue)
    }

    private fun downloadEntry(
        chapterId: Long,
        status: ChimahonDownloadState,
        progress: Int = 0,
        downloadedBytes: Long = 0L,
        errorMessage: String? = null,
    ): ChimahonDownloadQueueEntry {
        return ChimahonDownloadQueueEntry(
            id = chapterId.toString(),
            mangaId = 10L,
            chapterId = chapterId,
            sourceId = 20L,
            mangaTitle = "Manga",
            chapterName = "Chapter $chapterId",
            chapterUrl = "/chapter-$chapterId",
            status = status,
            progress = progress,
            downloadedBytes = downloadedBytes,
            addedAt = chapterId,
            errorMessage = errorMessage,
        )
    }
}
