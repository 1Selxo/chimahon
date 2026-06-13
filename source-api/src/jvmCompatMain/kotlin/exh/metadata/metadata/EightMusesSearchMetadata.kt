package exh.metadata.metadata

import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.model.copy
import kotlinx.serialization.Serializable
import tachiyomi.i18n.MR
import tachiyomi.i18n.sy.SYMR

@Serializable
class EightMusesSearchMetadata : RaisedSearchMetadata() {
    var path: List<String> = emptyList()

    var title by titleDelegate(TITLE_TYPE_MAIN)

    var thumbnailUrl: String? = null

    override fun createMangaInfo(manga: SManga): SManga {
        val key = path.joinToString("/", prefix = "/")

        val title = title

        val cover = thumbnailUrl

        val artist = tags.ofNamespace(ARTIST_NAMESPACE).joinToString { it.name }

        val genres = tagsToGenreString()

        val description = null

        return manga.copy(
            url = key,
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
                getItem(title) { string(MR.strings.title) },
                getItem(path.ifEmpty { null }, { it.joinToString("/", prefix = "/") }) {
                    string(SYMR.strings.path)
                },
                getItem(thumbnailUrl) { string(SYMR.strings.thumbnail_url) },
            )
        }
    }

    companion object {
        private const val TITLE_TYPE_MAIN = 0

        const val TAG_TYPE_DEFAULT = 0

        const val TAGS_NAMESPACE = "tags"
        const val ARTIST_NAMESPACE = "artist"
    }
}
