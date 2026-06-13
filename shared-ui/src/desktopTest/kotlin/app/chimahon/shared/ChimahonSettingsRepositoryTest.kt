package app.chimahon.shared

import kotlinx.coroutines.runBlocking
import tachiyomi.core.platform.settings.PlatformSettingsStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChimahonSettingsRepositoryTest {

    @Test
    fun defaultsMatchCurrentSharedReaderUi() = runBlocking {
        val repository = ChimahonSettingsRepository(MapSettingsStore())

        val settings = repository.loadSettings()

        assertEquals(ChimahonReaderMode.Webtoon, settings.reader.mode)
        assertEquals(ChimahonReaderScale.FitWidth, settings.reader.scale)
        assertEquals(ChimahonReaderCanvas.Black, settings.reader.canvas)
        assertFalse(settings.appMode.downloadedOnly)
        assertFalse(settings.appMode.incognitoMode)
    }

    @Test
    fun persistsReaderAndModeSettings() = runBlocking {
        val store = MapSettingsStore()
        val repository = ChimahonSettingsRepository(store)

        repository.saveReaderSettings(
            ChimahonReaderSettings(
                mode = ChimahonReaderMode.RightToLeft,
                scale = ChimahonReaderScale.FitScreen,
                canvas = ChimahonReaderCanvas.White,
            ),
        )
        repository.setDownloadedOnly(true)
        repository.setIncognitoMode(true)

        val settings = ChimahonSettingsRepository(store).loadSettings()

        assertEquals(ChimahonReaderMode.RightToLeft, settings.reader.mode)
        assertEquals(ChimahonReaderScale.FitScreen, settings.reader.scale)
        assertEquals(ChimahonReaderCanvas.White, settings.reader.canvas)
        assertTrue(settings.appMode.downloadedOnly)
        assertTrue(settings.appMode.incognitoMode)
        assertTrue(repository.isIncognitoModeEnabled())
    }

    @Test
    fun unknownEnumValuesFallBackToDefaults() = runBlocking {
        val store = MapSettingsStore(
            mutableMapOf(
                "__APP_STATE_chimahon_reader_mode" to "Paged",
                "__APP_STATE_chimahon_reader_scale" to "ActualSize",
                "__APP_STATE_chimahon_reader_canvas" to "Sepia",
            ),
        )
        val repository = ChimahonSettingsRepository(store)

        val readerSettings = repository.loadReaderSettings()

        assertEquals(ChimahonReaderMode.Webtoon, readerSettings.mode)
        assertEquals(ChimahonReaderScale.FitWidth, readerSettings.scale)
        assertEquals(ChimahonReaderCanvas.Black, readerSettings.canvas)
    }

    private class MapSettingsStore(
        private val values: MutableMap<String, String> = mutableMapOf(),
    ) : PlatformSettingsStore {
        override suspend fun readString(key: String): String? {
            return values[key]
        }

        override suspend fun writeString(key: String, value: String) {
            values[key] = value
        }

        override suspend fun remove(key: String) {
            values.remove(key)
        }

        override suspend fun clear() {
            values.clear()
        }

        override suspend fun snapshot(): Map<String, String> {
            return values.toMap()
        }
    }
}
