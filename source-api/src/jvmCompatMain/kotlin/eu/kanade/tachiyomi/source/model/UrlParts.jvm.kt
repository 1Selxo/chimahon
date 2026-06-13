package eu.kanade.tachiyomi.source.model

import java.net.URI
import java.net.URISyntaxException

internal actual fun String.pathQueryAndFragment(): String {
    return try {
        val uri = URI(this)
        buildString {
            append(uri.path)
            uri.query?.let {
                append('?')
                append(it)
            }
            uri.fragment?.let {
                append('#')
                append(it)
            }
        }
    } catch (_: URISyntaxException) {
        this
    }
}
