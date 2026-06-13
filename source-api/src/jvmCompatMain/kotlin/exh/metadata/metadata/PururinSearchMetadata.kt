package exh.metadata.metadata

import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.model.copy
import kotlinx.serialization.Serializable
import tachiyomi.i18n.MR
import tachiyomi.i18n.sy.SYMR

@Serializable
class PururinSearchMetadata : RaisedSearchMetadata() {
    var prId: Int? = null

    var prShortLink: String? = null

    var title by titleDelegate(TITLE_TYPE_TITLE)
    var altTitle by titleDelegate(TITLE_TYPE_ALT_TITLE)

    var thumbnailUrl: String? = null

    var uploaderDisp: String? = null

    var pages: Int? = null

    var fileSize: String? = null

    var ratingCount: Int? = null
    var averageRating: Double? = null

    override fun createMangaInfo(manga: SManga): SManga {
        val key = prId?.let { prId ->
            prShortLink?.let { prShortLink ->
                "/gallery/$prId/$prShortLink"
            }
        }

        val title = title ?: altTitle

        val cover = thumbnailUrl

        val artist = tags.ofNamespace(TAG_NAMESPACE_ARTIST).joinToString { it.name }

        val genres = tagsToGenreString()

        val description = null

        return manga.copy(
            url = key ?: manga.url,
            title = title ?: manga.title,
            thumbnail_url = cover ?: manga.thumbnail_url,
            artist = artist,
            genre = genres,
            description = description,
        )
    }

    override fun getExtraInfoPairs(strings: MetadataStringProvider): List<Pair<String, String>> {
        return with(strings) {
            listOfNotNull(
                getItem(prId) { string(SYMR.strings.id) },
                getItem(title) { string(MR.strings.title) },
                getItem(altTitle) { string(SYMR.strings.alt_title) },
                getItem(thumbnailUrl) { string(SYMR.strings.thumbnail_url) },
                getItem(uploaderDisp) { string(SYMR.strings.uploader_capital) },
                getItem(uploader) { string(SYMR.strings.uploader) },
                getItem(pages) { string(SYMR.strings.page_count) },
                getItem(fileSize) { string(SYMR.strings.gallery_size) },
                getItem(ratingCount) { string(SYMR.strings.total_ratings) },
                getItem(averageRating) { string(SYMR.strings.average_rating) },
            )
        }
    }

    companion object {
        private const val TITLE_TYPE_TITLE = 0
        private const val TITLE_TYPE_ALT_TITLE = 1

        const val TAG_TYPE_DEFAULT = 0

        private const val TAG_NAMESPACE_ARTIST = "artist"
        const val TAG_NAMESPACE_CATEGORY = "category"

        const val BASE_URL = "https://pururin.me"
    }
}
