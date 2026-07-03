@file:Suppress("PropertyName")

package eu.kanade.tachiyomi.animesource.model

import eu.kanade.tachiyomi.source.model.PlatformSerializable

interface SEpisode : PlatformSerializable {

    var url: String

    var name: String

    var date_upload: Long

    var episode_number: Float

    var fillermark: Boolean

    var scanlator: String?

    var summary: String?

    var preview_url: String?

    fun copyFrom(other: SEpisode) {
        name = other.name
        url = other.url
        date_upload = other.date_upload
        episode_number = other.episode_number
        fillermark = other.fillermark
        scanlator = other.scanlator
        summary = other.summary
        preview_url = other.preview_url
    }

    companion object {
        fun create(): SEpisode {
            return SEpisodeImpl()
        }
    }
}
