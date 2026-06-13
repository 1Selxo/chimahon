package tachiyomi.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toOkioPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tachiyomi.core.database.DesktopDatabaseDriverFactory
import java.nio.file.Path

class DesktopDatabaseTest {
    @TempDir
    lateinit var temporaryDirectory: Path

    @Test
    fun usesTheProductionSchemaThroughTheDesktopHandler() = runBlocking {
        val driver = DesktopDatabaseDriverFactory(temporaryDirectory.toOkioPath())
            .create(Database.Schema, "chimahon.db")

        driver.use {
            val database = Database(
                driver = driver,
                mangasAdapter = Mangas.Adapter(
                    genreAdapter = StringListColumnAdapter,
                ),
            )
            val handler = DesktopDatabaseHandler(database, driver)

            assertTrue(handler.awaitList { mangasQueries.getAllManga() }.isEmpty())

            handler.await(inTransaction = true) {
                mangasQueries.insert(
                    source = 1,
                    url = "/desktop",
                    artist = null,
                    author = null,
                    description = null,
                    genre = listOf("KMP"),
                    title = "Desktop manga",
                    status = 0,
                    thumbnailUrl = null,
                    favorite = true,
                    lastUpdate = null,
                    nextUpdate = null,
                    initialized = false,
                    viewerFlags = 0,
                    chapterFlags = 0,
                    coverLastModified = 0,
                    dateAdded = 0,
                    updateStrategy = 0,
                    calculateInterval = 0,
                    version = 0,
                    notes = "",
                )
            }

            val inserted = handler.awaitOne {
                mangasQueries.getMangaByUrlAndSource("/desktop", 1)
            }
            assertEquals("Desktop manga", inserted.title)
            assertEquals(
                listOf(inserted),
                handler.subscribeToList { mangasQueries.getAllManga() }.first(),
            )
        }
    }
}
