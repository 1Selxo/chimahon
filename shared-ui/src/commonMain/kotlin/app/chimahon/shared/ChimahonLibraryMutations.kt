package app.chimahon.shared

import tachiyomi.data.DatabaseHandler

private const val DEFAULT_LIBRARY_CATEGORY_ID = 0L

internal suspend fun updateMangasCategories(
    databaseHandler: DatabaseHandler,
    mangaIds: Collection<Long>,
    categoryIds: Collection<Long>,
) {
    val selectedIds = categoryIds
        .filter { it >= DEFAULT_LIBRARY_CATEGORY_ID }
        .distinct()
        .ifEmpty { listOf(DEFAULT_LIBRARY_CATEGORY_ID) }
    databaseHandler.await(inTransaction = true) {
        mangaIds.distinct().forEach { mangaId ->
            mangas_categoriesQueries.deleteMangaCategoryByMangaId(mangaId)
            selectedIds.forEach { categoryId ->
                mangas_categoriesQueries.insert(mangaId, categoryId)
            }
        }
    }
}

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

internal suspend fun updateMangasFavorite(
    databaseHandler: DatabaseHandler,
    mangaIds: Collection<Long>,
    favorite: Boolean,
    now: Long,
) {
    mangaIds.distinct().forEach { mangaId ->
        updateMangaFavorite(
            databaseHandler = databaseHandler,
            mangaId = mangaId,
            favorite = favorite,
            now = now,
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

internal suspend fun updateMangaChapterFlags(
    databaseHandler: DatabaseHandler,
    mangaId: Long,
    chapterFlags: Long,
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
            chapterFlags = chapterFlags,
            coverLastModified = null,
            dateAdded = null,
            updateStrategy = null,
            calculateInterval = null,
            version = null,
            isSyncing = 0L,
            notes = null,
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

internal suspend fun updateMangasChaptersRead(
    databaseHandler: DatabaseHandler,
    mangaIds: Collection<Long>,
    read: Boolean,
) {
    mangaIds.distinct().forEach { mangaId ->
        updateMangaChaptersRead(
            databaseHandler = databaseHandler,
            mangaId = mangaId,
            read = read,
        )
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

internal suspend fun updateChaptersBookmark(
    databaseHandler: DatabaseHandler,
    chapterIds: Collection<Long>,
    bookmarked: Boolean,
) {
    databaseHandler.await(inTransaction = true) {
        chapterIds.distinct().forEach { chapterId ->
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
}
