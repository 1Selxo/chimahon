package app.chimahon.shared

import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.online.HttpSource

internal actual suspend fun fetchSourcePageImage(
    source: CatalogueSource,
    page: Page,
): ByteArray {
    val httpSource = source as? HttpSource
        ?: error("${source.name} does not expose the desktop HTTP image API.")
    if (page.imageUrl.isNullOrBlank()) {
        page.imageUrl = httpSource.getImageUrl(page)
    }
    return httpSource.getImage(page).use { response ->
        response.body.bytes()
    }
}
