package mihon.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import mihon.domain.extensionrepo.exception.SaveExtensionRepoException
import mihon.domain.extensionrepo.interactor.DeleteExtensionRepo
import mihon.domain.extensionrepo.interactor.GetExtensionRepo
import mihon.domain.extensionrepo.interactor.GetExtensionRepoCount
import mihon.domain.extensionrepo.interactor.ReplaceExtensionRepo
import mihon.domain.extensionrepo.model.ExtensionRepo
import okio.Path.Companion.toOkioPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tachiyomi.core.database.DesktopDatabaseDriverFactory
import tachiyomi.data.Database
import tachiyomi.data.DesktopDatabaseHandler
import tachiyomi.data.Mangas
import tachiyomi.data.StringListColumnAdapter
import java.nio.file.Path

class ExtensionRepoRepositoryTest {
    @TempDir
    lateinit var temporaryDirectory: Path

    @Test
    fun managesExtensionRepositoriesThroughCommonInteractors() = runBlocking {
        val driver = DesktopDatabaseDriverFactory(temporaryDirectory.toOkioPath())
            .create(Database.Schema, "chimahon.db")

        driver.use {
            val database = Database(
                driver = driver,
                mangasAdapter = Mangas.Adapter(
                    genreAdapter = StringListColumnAdapter,
                ),
            )
            val repository = ExtensionRepoRepositoryImpl(
                DesktopDatabaseHandler(database, driver),
            )
            val deleteRepo = DeleteExtensionRepo(repository)
            val getRepo = GetExtensionRepo(repository)
            val getRepoCount = GetExtensionRepoCount(repository)
            val replaceRepo = ReplaceExtensionRepo(repository)
            val original = ExtensionRepo(
                baseUrl = "https://extensions.example",
                name = "Example",
                shortName = "EX",
                website = "https://example.org",
                signingKeyFingerprint = "fingerprint",
            )

            repository.insertRepo(
                original.baseUrl,
                original.name,
                original.shortName,
                original.website,
                original.signingKeyFingerprint,
            )

            assertEquals(listOf(original), getRepo.getAll())
            assertEquals(1, getRepoCount.subscribe().first())

            val replacement = original.copy(
                baseUrl = "https://new.example",
                name = "New Example",
            )
            replaceRepo.await(replacement)
            assertEquals(listOf(replacement), getRepo.subscribeAll().first())

            assertThrows(SaveExtensionRepoException::class.java) {
                runBlocking {
                    repository.insertRepo(
                        baseUrl = "https://duplicate.example",
                        name = "Duplicate",
                        shortName = null,
                        website = "https://duplicate.example",
                        signingKeyFingerprint = replacement.signingKeyFingerprint,
                    )
                }
            }

            deleteRepo.await(replacement.baseUrl)
            assertEquals(emptyList<ExtensionRepo>(), getRepo.getAll())
        }
    }
}
