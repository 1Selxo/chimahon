package app.chimahon.shared

import tachiyomi.data.Chapters
import tachiyomi.data.DatabaseHandler
import tachiyomi.data.StringListColumnAdapter

internal suspend fun importRemoteMangaToLibrary(
    databaseHandler: DatabaseHandler,
    detail: ChimahonRemoteMangaDetail,
    now: Long,
): Long {
    val manga = databaseHandler.awaitOneExecutable {
        mangasQueries.insertNetworkManga(
            source = detail.sourceId,
            url = detail.url,
            artist = detail.artist,
            author = detail.author,
            description = detail.description,
            genre = detail.genres,
            title = detail.title,
            status = detail.statusCode,
            thumbnailUrl = detail.thumbnailUrl,
            favorite = true,
            lastUpdate = now,
            nextUpdate = null,
            initialized = true,
            viewerFlags = 0L,
            chapterFlags = 0L,
            coverLastModified = 0L,
            dateAdded = now,
            updateStrategy = 0L,
            calculateInterval = 0L,
            version = 0L,
            updateTitle = true,
            updateCover = true,
            updateDetails = true,
            updateInfo = true,
        )
    }
    val existingChapters = databaseHandler.awaitList {
        chaptersQueries.getChaptersByMangaId(
            mangaId = manga._id,
            applyFilter = 0L,
            bookmarkUnmask = 0L,
            bookmarkMask = 0L,
        )
    }.associateBy(Chapters::url)
    val sourceChapters = detail.chapters.distinctBy(ChimahonRemoteChapterEntry::url)

    databaseHandler.await(inTransaction = true) {
        mangasQueries.update(
            source = null,
            url = null,
            artist = detail.artist,
            author = detail.author,
            description = detail.description,
            genre = StringListColumnAdapter.encode(detail.genres),
            title = detail.title,
            status = detail.statusCode,
            thumbnailUrl = detail.thumbnailUrl,
            favorite = true,
            lastUpdate = now,
            nextUpdate = null,
            initialized = true,
            viewer = null,
            chapterFlags = null,
            coverLastModified = null,
            dateAdded = if (manga.favorite) null else now,
            updateStrategy = null,
            calculateInterval = null,
            version = null,
            isSyncing = 0L,
            notes = null,
            mangaId = manga._id,
        )

        if (sourceChapters.isEmpty()) return@await

        val sourceUrls = sourceChapters.mapTo(mutableSetOf(), ChimahonRemoteChapterEntry::url)
        val replacementCandidates = existingChapters.values
            .filterNot { it.url in sourceUrls }
            .toMutableList()
        val retainedChapterIds = mutableSetOf<Long>()

        sourceChapters.forEachIndexed { index, chapter ->
            val existing = existingChapters[chapter.url] ?: replacementCandidates
                .firstOrNull {
                    chapter.chapterNumber >= 0.0 &&
                        it.chapter_number == chapter.chapterNumber &&
                        it.scanlator == chapter.scanlator
                }
                ?.also(replacementCandidates::remove)
            if (existing == null) {
                chaptersQueries.insert(
                    mangaId = manga._id,
                    url = chapter.url,
                    name = chapter.name,
                    scanlator = chapter.scanlator,
                    read = false,
                    bookmark = false,
                    lastPageRead = 0L,
                    chapterNumber = chapter.chapterNumber,
                    sourceOrder = index.toLong(),
                    dateFetch = now + sourceChapters.size - index,
                    dateUpload = chapter.dateUpload.takeIf { it > 0L } ?: now,
                    version = 0L,
                )
            } else {
                retainedChapterIds += existing._id
                chaptersQueries.update(
                    mangaId = null,
                    url = chapter.url.takeIf { it != existing.url },
                    name = chapter.name,
                    scanlator = chapter.scanlator,
                    read = null,
                    bookmark = null,
                    lastPageRead = null,
                    chapterNumber = chapter.chapterNumber,
                    sourceOrder = index.toLong(),
                    dateFetch = null,
                    dateUpload = chapter.dateUpload.takeIf { it > 0L },
                    version = null,
                    isSyncing = 0L,
                    ocrReady = null,
                    chapterId = existing._id,
                )
            }
        }

        val removedChapterIds = existingChapters.values
            .filterNot { it._id in retainedChapterIds }
            .map(Chapters::_id)
        if (removedChapterIds.isNotEmpty()) {
            chaptersQueries.removeChaptersWithIds(removedChapterIds)
        }
    }
    return manga._id
}
