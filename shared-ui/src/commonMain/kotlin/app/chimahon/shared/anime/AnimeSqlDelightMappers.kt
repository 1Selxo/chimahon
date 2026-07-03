package app.chimahon.shared.anime

import eu.kanade.tachiyomi.animesource.model.FetchType
import eu.kanade.tachiyomi.source.model.UpdateStrategy
import dataanime.Animes
import dataanime.Episodes

fun Animes.toChimahonAnimeEntry(): ChimahonAnimeEntry {
    return ChimahonAnimeEntry(
        id = _id,
        sourceId = source,
        url = url,
        title = title,
        artist = artist,
        author = author,
        thumbnailUrl = thumbnail_url,
        backgroundUrl = background_url,
        description = description,
        genres = genre.orEmpty(),
        status = ChimahonAnimeStatus.fromUpstreamValue(status),
        favorite = favorite,
        initialized = initialized,
        dateAdded = date_added,
        lastUpdate = last_update ?: 0L,
        nextUpdate = next_update ?: 0L,
        fetchInterval = calculate_interval.toInt(),
        viewerFlags = viewer,
        episodeFlags = episode_flags,
        seasonFlags = season_flags,
        seasonNumber = season_number,
        seasonSourceOrder = season_source_order,
        coverLastModified = cover_last_modified,
        backgroundLastModified = background_last_modified,
        updateStrategy = update_strategy.toChimahonAnimeUpdateStrategy(),
        fetchType = fetch_type.toChimahonAnimeFetchType(),
        parentId = parent_id,
        lastModifiedAt = last_modified_at,
        favoriteModifiedAt = favorite_modified_at,
        version = version,
    )
}

fun Episodes.toChimahonAnimeEpisodeEntry(): ChimahonAnimeEpisodeEntry {
    return ChimahonAnimeEpisodeEntry(
        id = _id,
        animeId = anime_id,
        url = url,
        name = name,
        episodeNumber = episode_number,
        scanlator = scanlator,
        seen = seen,
        bookmark = bookmark,
        fillermark = fillermark,
        summary = summary,
        previewUrl = preview_url,
        lastSecondSeen = last_second_seen,
        totalSeconds = total_seconds,
        dateFetch = date_fetch,
        dateUpload = date_upload,
        sourceOrder = source_order,
        lastModifiedAt = last_modified_at,
        version = version,
    )
}

fun List<Episodes>.toChimahonAnimeEpisodesByAnimeId(): Map<Long, List<ChimahonAnimeEpisodeEntry>> {
    return groupBy(Episodes::anime_id)
        .mapValues { (_, rows) ->
            rows
                .map(Episodes::toChimahonAnimeEpisodeEntry)
                .sortedWith(
                    compareBy<ChimahonAnimeEpisodeEntry> { it.sourceOrder }
                        .thenBy { it.id },
                )
        }
}

fun List<Animes>.toChimahonAnimeLibraryRecords(
    episodesByAnimeId: Map<Long, List<ChimahonAnimeEpisodeEntry>> = emptyMap(),
    sourceInfos: Map<Long, ChimahonAnimeSourceInfo> = emptyMap(),
    categoryIdsByAnimeId: Map<Long, List<Long>> = emptyMap(),
    downloadedEpisodeIds: Set<Long> = emptySet(),
    lastSeenByAnimeId: Map<Long, Long> = emptyMap(),
): List<ChimahonAnimeLibraryRecord> {
    return map { row ->
        val anime = row.toChimahonAnimeEntry()
        val episodes = episodesByAnimeId[anime.id].orEmpty()
        ChimahonAnimeLibraryRecord(
            anime = anime,
            categoryIds = categoryIdsByAnimeId[anime.id].orEmpty(),
            totalEpisodeCount = episodes.size,
            seenEpisodeCount = episodes.count { it.seen },
            bookmarkedEpisodeCount = episodes.count { it.bookmark },
            fillermarkedEpisodeCount = episodes.count { it.fillermark },
            downloadedEpisodeCount = episodes.count { it.id in downloadedEpisodeIds },
            latestUpload = episodes.maxOfOrNull { it.dateUpload } ?: 0L,
            episodeFetchedAt = episodes.maxOfOrNull { it.dateFetch } ?: 0L,
            lastSeen = lastSeenByAnimeId[anime.id] ?: 0L,
            sourceInfo = sourceInfos[anime.sourceId],
            isLocal = sourceInfos[anime.sourceId]?.isLocal == true,
        )
    }
}

private fun UpdateStrategy.toChimahonAnimeUpdateStrategy(): ChimahonAnimeUpdateStrategy {
    return when (this) {
        UpdateStrategy.ONLY_FETCH_ONCE -> ChimahonAnimeUpdateStrategy.OnlyFetchOnce
        UpdateStrategy.ALWAYS_UPDATE -> ChimahonAnimeUpdateStrategy.Always
    }
}

private fun FetchType.toChimahonAnimeFetchType(): ChimahonAnimeFetchType {
    return when (this) {
        FetchType.Seasons -> ChimahonAnimeFetchType.Seasons
        FetchType.Episodes -> ChimahonAnimeFetchType.Episodes
    }
}
