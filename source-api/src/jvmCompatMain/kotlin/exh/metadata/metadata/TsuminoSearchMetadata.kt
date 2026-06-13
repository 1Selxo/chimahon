package exh.metadata.metadata

import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.model.copy
import exh.metadata.MetadataUtil
import kotlinx.serialization.Serializable
import tachiyomi.i18n.MR
import tachiyomi.i18n.sy.SYMR
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale

@Serializable
class TsuminoSearchMetadata : RaisedSearchMetadata() {
    var tmId: Int? = null

    var title by titleDelegate(TITLE_TYPE_MAIN)

    var artist: String? = null

    var uploadDate: Long? = null

    var length: Int? = null

    var ratingString: String? = null

    var averageRating: Float? = null

    var userRatings: Long? = null

    var favorites: Long? = null

    var category: String? = null

    var collection: String? = null

    var group: String? = null

    var parody: List<String> = emptyList()

    var character: List<String> = emptyList()

    override fun createMangaInfo(manga: SManga): SManga {
        val title = title
        val cover = tmId?.let { BASE_URL.replace("www", "content") + thumbUrlFromId(it.toString()) }

        val artist = artist

        val status = SManga.UNKNOWN

        // Copy tags -> genres
        val genres = tagsToGenreString()

        val description = null

        return manga.copy(
            title = title ?: manga.title,
            thumbnail_url = cover ?: manga.thumbnail_url,
            artist = artist ?: manga.artist,
            status = status,
            genre = genres,
            description = description,
        )
    }

    override fun getExtraInfoPairs(strings: MetadataStringProvider): List<Pair<String, String>> {
        return with(strings) {
            listOfNotNull(
                getItem(tmId) { string(SYMR.strings.id) },
                getItem(title) { string(MR.strings.title) },
                getItem(uploader) { string(SYMR.strings.uploader) },
                getItem(
                    uploadDate,
                    {
                        MetadataUtil.EX_DATE_FORMAT
                            .format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()))
                    },
                ) {
                    string(SYMR.strings.date_posted)
                },
                getItem(length) { string(SYMR.strings.page_count) },
                getItem(ratingString) { string(SYMR.strings.rating_string) },
                getItem(averageRating) { string(SYMR.strings.average_rating) },
                getItem(userRatings) { string(SYMR.strings.total_ratings) },
                getItem(favorites) { string(SYMR.strings.total_favorites) },
                getItem(category) { string(SYMR.strings.genre) },
                getItem(collection) { string(SYMR.strings.collection) },
                getItem(group) { string(SYMR.strings.group) },
                getItem(parody.ifEmpty { null }, { it.joinToString() }) { string(SYMR.strings.parodies) },
                getItem(character.ifEmpty { null }, { it.joinToString() }) { string(SYMR.strings.characters) },
            )
        }
    }

    companion object {
        private const val TITLE_TYPE_MAIN = 0

        const val TAG_TYPE_DEFAULT = 0

        val BASE_URL = "https://www.tsumino.com"

        val TSUMINO_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        fun tmIdFromUrl(url: String) =
            url.substringBefore("?")
                .substringBefore("#")
                .trimEnd('/')
                .substringAfterLast('/')

        fun thumbUrlFromId(id: String) = "/thumbs/$id/1"
    }
}
