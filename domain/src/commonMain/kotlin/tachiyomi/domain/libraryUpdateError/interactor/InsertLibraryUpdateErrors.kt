package tachiyomi.domain.libraryUpdateError.interactor

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import tachiyomi.domain.libraryUpdateError.interactor.DeleteLibraryUpdateErrors.Result
import tachiyomi.domain.libraryUpdateError.model.LibraryUpdateError
import tachiyomi.domain.libraryUpdateError.repository.LibraryUpdateErrorRepository

class InsertLibraryUpdateErrors(
    private val libraryUpdateErrorRepository: LibraryUpdateErrorRepository,
) {
    suspend fun upsert(libraryUpdateError: LibraryUpdateError) = runCommand {
        libraryUpdateErrorRepository.upsert(libraryUpdateError)
    }

    suspend fun insert(libraryUpdateError: LibraryUpdateError) = runCommand {
        libraryUpdateErrorRepository.insert(libraryUpdateError)
    }

    suspend fun insertAll(libraryUpdateErrors: List<LibraryUpdateError>) = runCommand {
        libraryUpdateErrorRepository.insertAll(libraryUpdateErrors)
    }

    private suspend fun runCommand(block: suspend () -> Unit): Result {
        return withContext(NonCancellable) {
            try {
                block()
                Result.Success
            } catch (error: Exception) {
                Result.InternalError(error)
            }
        }
    }
}
