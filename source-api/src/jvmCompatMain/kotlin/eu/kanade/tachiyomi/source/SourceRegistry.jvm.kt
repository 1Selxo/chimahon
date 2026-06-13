package eu.kanade.tachiyomi.source

import eu.kanade.tachiyomi.source.online.HttpSource

fun SourceRegistry.getOnlineSources(): List<HttpSource> =
    sources.value.values.filterIsInstance<HttpSource>()
