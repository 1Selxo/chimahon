package app.chimahon.shared

import tachiyomi.data.DatabaseHandler

internal suspend fun updateMangaFavorite(
    databaseHandler: DatabaseHandler,
    mangaId: Long,
    favorite: Boolean,
    now: Long,
) {
    databaseHandler.await {
        mangasQueries.update(
            source = null,
            url = null,
            artist = null,
            author = null,
            description = null,
            genre = null,
            title = null,
            status = null,
            thumbnailUrl = null,
            favorite = favorite,
            lastUpdate = null,
            nextUpdate = null,
            initialized = null,
            viewer = null,
            chapterFlags = null,
            coverLastModified = null,
            dateAdded = if (favorite) now else 0L,
            updateStrategy = null,
            calculateInterval = null,
            version = null,
            isSyncing = 0L,
            notes = null,
            mangaId = mangaId,
        )
    }
}

internal suspend fun updateMangaNotes(
    databaseHandler: DatabaseHandler,
    mangaId: Long,
    notes: String,
) {
    databaseHandler.await {
        mangasQueries.update(
            source = null,
            url = null,
            artist = null,
            author = null,
            description = null,
            genre = null,
            title = null,
            status = null,
            thumbnailUrl = null,
            favorite = null,
            lastUpdate = null,
            nextUpdate = null,
            initialized = null,
            viewer = null,
            chapterFlags = null,
            coverLastModified = null,
            dateAdded = null,
            updateStrategy = null,
            calculateInterval = null,
            version = null,
            isSyncing = 0L,
            notes = notes,
            mangaId = mangaId,
        )
    }
}

internal suspend fun updateChapterRead(
    databaseHandler: DatabaseHandler,
    chapterId: Long,
    read: Boolean,
) {
    databaseHandler.await {
        chaptersQueries.update(
            mangaId = null,
            url = null,
            name = null,
            scanlator = null,
            read = read,
            bookmark = null,
            lastPageRead = 0L.takeIf { !read },
            chapterNumber = null,
            sourceOrder = null,
            dateFetch = null,
            dateUpload = null,
            version = null,
            isSyncing = 0L,
            ocrReady = null,
            chapterId = chapterId,
        )
    }
}

internal suspend fun updateMangaChaptersRead(
    databaseHandler: DatabaseHandler,
    mangaId: Long,
    read: Boolean,
) {
    val chapters = databaseHandler.awaitList {
        chaptersQueries.getChaptersByMangaId(
            mangaId = mangaId,
            applyFilter = 0L,
            bookmarkUnmask = 0L,
            bookmarkMask = 0L,
        )
    }
    databaseHandler.await(inTransaction = true) {
        chapters.forEach { chapter ->
            chaptersQueries.update(
                mangaId = null,
                url = null,
                name = null,
                scanlator = null,
                read = read,
                bookmark = null,
                lastPageRead = 0L.takeIf { !read },
                chapterNumber = null,
                sourceOrder = null,
                dateFetch = null,
                dateUpload = null,
                version = null,
                isSyncing = 0L,
                ocrReady = null,
                chapterId = chapter._id,
            )
        }
    }
}

internal suspend fun updateChapterBookmark(
    databaseHandler: DatabaseHandler,
    chapterId: Long,
    bookmarked: Boolean,
) {
    databaseHandler.await {
        chaptersQueries.update(
            mangaId = null,
            url = null,
            name = null,
            scanlator = null,
            read = null,
            bookmark = bookmarked,
            lastPageRead = null,
            chapterNumber = null,
            sourceOrder = null,
            dateFetch = null,
            dateUpload = null,
            version = null,
            isSyncing = 0L,
            ocrReady = null,
            chapterId = chapterId,
        )
    }
}
