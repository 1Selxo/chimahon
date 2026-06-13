package app.chimahon.shared

import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.online.ScriptHttpSource

internal actual suspend fun fetchSourcePageImage(
    source: CatalogueSource,
    page: Page,
): ByteArray {
    val scriptSource = source as? ScriptHttpSource
        ?: error("${source.name} does not expose the iOS script image API.")
    return scriptSource.getImageBytes(page)
}
