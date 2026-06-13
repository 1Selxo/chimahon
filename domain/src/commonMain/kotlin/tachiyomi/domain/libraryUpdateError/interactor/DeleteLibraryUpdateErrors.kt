package tachiyomi.domain.libraryUpdateError.interactor

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import tachiyomi.domain.libraryUpdateError.repository.LibraryUpdateErrorRepository

class DeleteLibraryUpdateErrors(
    private val libraryUpdateErrorRepository: LibraryUpdateErrorRepository,
) {

    suspend fun deleteAll() = runCommand {
        libraryUpdateErrorRepository.deleteAll()
    }

    suspend fun delete(errorIds: List<Long>) = runCommand {
        libraryUpdateErrorRepository.delete(errorIds)
    }

    suspend fun deleteMangaError(mangaIds: List<Long>) = runCommand {
        libraryUpdateErrorRepository.deleteMangaError(mangaIds)
    }

    suspend fun cleanUnrelevantMangaErrors() = runCommand {
        libraryUpdateErrorRepository.cleanUnrelevantMangaErrors()
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

    sealed class Result {
        data object Success : Result()
        data class InternalError(val error: Throwable) : Result()
    }
}
