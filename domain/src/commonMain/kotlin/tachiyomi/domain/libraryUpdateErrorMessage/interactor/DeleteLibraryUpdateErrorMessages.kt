package tachiyomi.domain.libraryUpdateErrorMessage.interactor

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import tachiyomi.domain.libraryUpdateErrorMessage.repository.LibraryUpdateErrorMessageRepository

class DeleteLibraryUpdateErrorMessages(
    private val libraryUpdateErrorMessageRepository: LibraryUpdateErrorMessageRepository,
) {

    suspend fun await(): Result {
        return withContext(NonCancellable) {
            try {
                libraryUpdateErrorMessageRepository.deleteAll()
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
