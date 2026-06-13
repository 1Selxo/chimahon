package app.chimahon.shared

import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.model.Page

internal expect suspend fun fetchSourcePageImage(
    source: CatalogueSource,
    page: Page,
): ByteArray
