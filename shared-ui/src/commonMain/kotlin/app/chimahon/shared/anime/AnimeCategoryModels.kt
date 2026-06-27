package app.chimahon.shared.anime

data class ChimahonAnimeCategory(
    val id: Long,
    val name: String,
    val order: Long,
    val flags: Long = 0L,
    val hidden: Boolean = false,
) {
    val isSystemCategory: Boolean
        get() = id == CHIMAHON_ANIME_UNCATEGORIZED_ID
}

data class ChimahonAnimeCategorySummary(
    val category: ChimahonAnimeCategory,
    val animeCount: Int,
    val unseenEpisodeCount: Int,
    val downloadedEpisodeCount: Int,
    val bookmarkedEpisodeCount: Int,
    val fillermarkedEpisodeCount: Int,
)

fun defaultAnimeCategory(name: String = "Default"): ChimahonAnimeCategory {
    return ChimahonAnimeCategory(
        id = CHIMAHON_ANIME_UNCATEGORIZED_ID,
        name = name,
        order = 0L,
    )
}
