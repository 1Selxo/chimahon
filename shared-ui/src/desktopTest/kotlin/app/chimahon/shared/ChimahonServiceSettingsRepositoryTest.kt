package app.chimahon.shared

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChimahonServiceSettingsRepositoryTest {
    @Test
    fun defaultsMatchServiceBehavior() = runBlocking {
        val repository = ChimahonServiceSettingsRepository(InMemoryPlatformSettingsStore())

        val settings = repository.load()

        assertFalse(settings.ui.keepReaderControlsVisible)
        assertFalse(settings.ui.rightToLeftByDefault)
        assertTrue(settings.ui.showUnreadBadges)
        assertTrue(settings.ui.showCategoryTabs)
        assertFalse(settings.ui.showHiddenCategories)
        assertTrue(settings.downloads.downloadOnlyOverWifi)
        assertFalse(settings.downloads.autoDownloadNewChapters)
        assertTrue(settings.downloads.autoDownloadUnreadOnly)
        assertEquals(2, settings.downloads.parallelDownloads)
    }

    @Test
    fun persistsUiSettingsAndClampsParallelDownloads() = runBlocking {
        val store = InMemoryPlatformSettingsStore()
        val repository = ChimahonServiceSettingsRepository(store)

        repository.saveUiSettings(
            ChimahonUiSettings(
                keepReaderControlsVisible = true,
                rightToLeftByDefault = true,
                showUnreadBadges = false,
                showCategoryTabs = false,
                showHiddenCategories = true,
            ),
        )
        val normalizedDownloads = repository.saveDownloadSettings(
            ChimahonDownloadSettings(
                downloadOnlyOverWifi = false,
                autoDownloadNewChapters = true,
                autoDownloadUnreadOnly = false,
                parallelDownloads = 99,
            ),
        )

        assertEquals(8, normalizedDownloads.parallelDownloads)
        val reloaded = ChimahonServiceSettingsRepository(store).load()
        assertTrue(reloaded.ui.keepReaderControlsVisible)
        assertTrue(reloaded.ui.rightToLeftByDefault)
        assertFalse(reloaded.ui.showUnreadBadges)
        assertFalse(reloaded.ui.showCategoryTabs)
        assertTrue(reloaded.ui.showHiddenCategories)
        assertFalse(reloaded.downloads.downloadOnlyOverWifi)
        assertTrue(reloaded.downloads.autoDownloadNewChapters)
        assertFalse(reloaded.downloads.autoDownloadUnreadOnly)
        assertEquals(8, reloaded.downloads.parallelDownloads)
    }

    @Test
    fun malformedAndOutOfRangeStoredValuesFallBackOrClamp() = runBlocking {
        val store = InMemoryPlatformSettingsStore(
            mutableMapOf(
                "__APP_STATE_show_unread_badges" to "sometimes",
                "__APP_STATE_show_category_tabs" to "1",
                "__APP_STATE_parallel_downloads" to "-20",
            ),
        )

        val settings = ChimahonServiceSettingsRepository(store).load()

        assertTrue(settings.ui.showUnreadBadges)
        assertTrue(settings.ui.showCategoryTabs)
        assertEquals(1, settings.downloads.parallelDownloads)
    }
}
