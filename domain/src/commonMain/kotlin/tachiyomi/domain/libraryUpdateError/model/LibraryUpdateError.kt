package tachiyomi.domain.libraryUpdateError.model

data class LibraryUpdateError(
    val id: Long,
    val mangaId: Long,
    val messageId: Long,
    val lastUpdate: Long = 0L,
)
