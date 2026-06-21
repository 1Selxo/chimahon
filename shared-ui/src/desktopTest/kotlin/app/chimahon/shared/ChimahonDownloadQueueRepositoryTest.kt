package app.chimahon.shared

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ChimahonDownloadQueueRepositoryTest {
    @Test
    fun claimNextPreservesActiveDownloadsAfterInitialRecovery() = runBlocking {
        val repository = ChimahonDownloadQueueRepository(InMemoryPlatformSettingsStore())
        repository.enqueue(
            listOf(
                downloadEntry(chapterId = 1L),
                downloadEntry(chapterId = 2L),
            ),
        )

        assertEquals(1L, repository.claimNext()?.chapterId)
        assertEquals(
            listOf(ChimahonDownloadState.Downloading, ChimahonDownloadState.Queued),
            repository.load().entries.map(ChimahonDownloadQueueEntry::status),
        )

        assertEquals(2L, repository.claimNext()?.chapterId)
        assertEquals(
            listOf(ChimahonDownloadState.Downloading, ChimahonDownloadState.Downloading),
            repository.load().entries.map(ChimahonDownloadQueueEntry::status),
        )
    }

    @Test
    fun newRepositoryInstanceRecoversPersistedActiveDownloads() = runBlocking {
        val settingsStore = InMemoryPlatformSettingsStore()
        val repository = ChimahonDownloadQueueRepository(settingsStore)
        repository.enqueue(listOf(downloadEntry(chapterId = 9L)))
        repository.claimNext()
        assertEquals(
            ChimahonDownloadState.Downloading,
            repository.load().entries.single().status,
        )

        val restartedRepository = ChimahonDownloadQueueRepository(settingsStore)

        assertEquals(
            ChimahonDownloadState.Queued,
            restartedRepository.load().entries.single().status,
        )
    }

    private fun downloadEntry(chapterId: Long): ChimahonDownloadQueueEntry {
        return ChimahonDownloadQueueEntry(
            id = chapterId.toString(),
            mangaId = 100L + chapterId,
            chapterId = chapterId,
            sourceId = 1L,
            mangaTitle = "Manga $chapterId",
            chapterName = "Chapter $chapterId",
            chapterUrl = "/chapter-$chapterId",
            addedAt = chapterId,
        )
    }
}
