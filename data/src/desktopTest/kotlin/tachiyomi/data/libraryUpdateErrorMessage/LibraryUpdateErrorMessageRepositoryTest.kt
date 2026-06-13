package tachiyomi.data.libraryUpdateErrorMessage

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toOkioPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tachiyomi.core.database.DesktopDatabaseDriverFactory
import tachiyomi.data.Database
import tachiyomi.data.DesktopDatabaseHandler
import tachiyomi.data.Mangas
import tachiyomi.data.StringListColumnAdapter
import tachiyomi.data.libraryUpdateError.LibraryUpdateErrorRepositoryImpl
import tachiyomi.domain.libraryUpdateError.interactor.DeleteLibraryUpdateErrors
import tachiyomi.domain.libraryUpdateError.interactor.GetLibraryUpdateErrors
import tachiyomi.domain.libraryUpdateError.interactor.InsertLibraryUpdateErrors
import tachiyomi.domain.libraryUpdateError.model.LibraryUpdateError
import tachiyomi.domain.libraryUpdateErrorMessage.interactor.DeleteLibraryUpdateErrorMessages
import tachiyomi.domain.libraryUpdateErrorMessage.interactor.GetLibraryUpdateErrorMessages
import tachiyomi.domain.libraryUpdateErrorMessage.interactor.InsertLibraryUpdateErrorMessages
import tachiyomi.domain.libraryUpdateErrorMessage.model.LibraryUpdateErrorMessage
import java.nio.file.Path

class LibraryUpdateErrorMessageRepositoryTest {
    @TempDir
    lateinit var temporaryDirectory: Path

    @Test
    fun readsAndWritesThroughCommonInteractors() = runBlocking {
        val driver = DesktopDatabaseDriverFactory(temporaryDirectory.toOkioPath())
            .create(Database.Schema, "chimahon.db")

        driver.use {
            val database = Database(
                driver = driver,
                mangasAdapter = Mangas.Adapter(
                    genreAdapter = StringListColumnAdapter,
                ),
            )
            val repository = LibraryUpdateErrorMessageRepositoryImpl(
                DesktopDatabaseHandler(database, driver),
            )
            val deleteMessages = DeleteLibraryUpdateErrorMessages(repository)
            val getMessages = GetLibraryUpdateErrorMessages(repository)
            val insertMessages = InsertLibraryUpdateErrorMessages(repository)

            assertNull(insertMessages.get("Network error"))

            val insertedId = insertMessages.insert(
                LibraryUpdateErrorMessage(id = -1, message = "Network error"),
            )

            assertEquals(insertedId, insertMessages.get("Network error"))
            assertEquals(
                listOf(LibraryUpdateErrorMessage(insertedId, "Network error")),
                getMessages.await(),
            )
            assertEquals(getMessages.await(), getMessages.subscribe().first())

            assertEquals(
                DeleteLibraryUpdateErrorMessages.Result.Success,
                deleteMessages.await(),
            )
            assertEquals(emptyList<LibraryUpdateErrorMessage>(), getMessages.await())
        }
    }

    @Test
    fun pairsErrorsWithSharedMessages() = runBlocking {
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
            val messageRepository = LibraryUpdateErrorMessageRepositoryImpl(handler)
            val errorRepository = LibraryUpdateErrorRepositoryImpl(handler)
            val deleteErrors = DeleteLibraryUpdateErrors(errorRepository)
            val getErrors = GetLibraryUpdateErrors(errorRepository)
            val insertErrors = InsertLibraryUpdateErrors(errorRepository)

            val firstMessageId = messageRepository.insert(
                LibraryUpdateErrorMessage(id = -1, message = "Timed out"),
            )
            val secondMessageId = messageRepository.insert(
                LibraryUpdateErrorMessage(id = -1, message = "Source unavailable"),
            )

            assertEquals(
                DeleteLibraryUpdateErrors.Result.Success,
                insertErrors.insert(
                    LibraryUpdateError(
                        id = -1,
                        mangaId = 42,
                        messageId = firstMessageId,
                    ),
                ),
            )
            assertEquals(firstMessageId, getErrors.await().single().messageId)

            assertEquals(
                DeleteLibraryUpdateErrors.Result.Success,
                insertErrors.upsert(
                    LibraryUpdateError(
                        id = -1,
                        mangaId = 42,
                        messageId = secondMessageId,
                    ),
                ),
            )
            assertEquals(secondMessageId, getErrors.subscribe().first().single().messageId)

            assertEquals(
                DeleteLibraryUpdateErrors.Result.Success,
                deleteErrors.deleteMangaError(listOf(42)),
            )
            assertEquals(emptyList<LibraryUpdateError>(), getErrors.await())
        }
    }
}
