package app.chimahon.shared

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChimahonSettingsRepositoryTest {

    @Test
    fun defaultsMatchCurrentSharedReaderUi() = runBlocking {
        val repository = ChimahonSettingsRepository(InMemoryPlatformSettingsStore())

        val settings = repository.loadSettings()

        assertEquals(ChimahonReaderMode.Webtoon, settings.reader.mode)
        assertEquals(ChimahonReaderScale.FitWidth, settings.reader.scale)
        assertEquals(ChimahonReaderCanvas.Black, settings.reader.canvas)
        assertTrue(settings.reader.showPageStrip)
        assertTrue(settings.reader.keepControlsVisible)
        assertEquals(ChimahonLibraryDisplayMode.ComfortableGrid, settings.library.displayMode)
        assertTrue(settings.library.showCategoryTabs)
        assertTrue(settings.library.showUnreadBadges)
        assertTrue(settings.library.showContinueButtons)
        assertFalse(settings.appMode.downloadedOnly)
        assertFalse(settings.appMode.incognitoMode)
    }

    @Test
    fun persistsReaderLibraryAndModeSettings() = runBlocking {
        val store = InMemoryPlatformSettingsStore()
        val repository = ChimahonSettingsRepository(store)

        repository.saveReaderSettings(
            ChimahonReaderSettings(
                mode = ChimahonReaderMode.RightToLeft,
                scale = ChimahonReaderScale.FitScreen,
                canvas = ChimahonReaderCanvas.White,
                showPageStrip = false,
                keepControlsVisible = false,
            ),
        )
        repository.saveLibrarySettings(
            ChimahonLibrarySettings(
                displayMode = ChimahonLibraryDisplayMode.List,
                showCategoryTabs = false,
                showUnreadBadges = false,
                showContinueButtons = false,
            ),
        )
        repository.setDownloadedOnly(true)
        repository.setIncognitoMode(true)

        val settings = ChimahonSettingsRepository(store).loadSettings()

        assertEquals(ChimahonReaderMode.RightToLeft, settings.reader.mode)
        assertEquals(ChimahonReaderScale.FitScreen, settings.reader.scale)
        assertEquals(ChimahonReaderCanvas.White, settings.reader.canvas)
        assertFalse(settings.reader.showPageStrip)
        assertFalse(settings.reader.keepControlsVisible)
        assertEquals(ChimahonLibraryDisplayMode.List, settings.library.displayMode)
        assertFalse(settings.library.showCategoryTabs)
        assertFalse(settings.library.showUnreadBadges)
        assertFalse(settings.library.showContinueButtons)
        assertTrue(settings.appMode.downloadedOnly)
        assertTrue(settings.appMode.incognitoMode)
        assertTrue(repository.isIncognitoModeEnabled())
    }

    @Test
    fun unknownEnumValuesFallBackToDefaults() = runBlocking {
        val store = InMemoryPlatformSettingsStore(
            mutableMapOf(
                "__APP_STATE_chimahon_reader_mode" to "Paged",
                "__APP_STATE_chimahon_reader_scale" to "ActualSize",
                "__APP_STATE_chimahon_reader_canvas" to "Sepia",
                "__APP_STATE_chimahon_reader_page_strip" to "sometimes",
                "__APP_STATE_chimahon_reader_controls" to "",
                "__APP_STATE_chimahon_library_display_mode" to "Shelf",
                "__APP_STATE_chimahon_library_category_tabs" to "1",
                "__APP_STATE_chimahon_library_unread_badges" to "TRUE",
                "__APP_STATE_chimahon_library_continue_buttons" to "yes",
            ),
        )
        val repository = ChimahonSettingsRepository(store)

        val settings = repository.loadSettings()

        assertEquals(ChimahonReaderMode.Webtoon, settings.reader.mode)
        assertEquals(ChimahonReaderScale.FitWidth, settings.reader.scale)
        assertEquals(ChimahonReaderCanvas.Black, settings.reader.canvas)
        assertTrue(settings.reader.showPageStrip)
        assertTrue(settings.reader.keepControlsVisible)
        assertEquals(ChimahonLibraryDisplayMode.ComfortableGrid, settings.library.displayMode)
        assertTrue(settings.library.showCategoryTabs)
        assertTrue(settings.library.showUnreadBadges)
        assertTrue(settings.library.showContinueButtons)
    }
}
