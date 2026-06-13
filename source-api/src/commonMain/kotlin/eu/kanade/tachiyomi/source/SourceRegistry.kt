package eu.kanade.tachiyomi.source

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class SourceRegistry(
    initialSources: Iterable<Source> = emptyList(),
) {
    private val sourcesById = MutableStateFlow(initialSources.associateBy(Source::id))

    val sources = sourcesById.asStateFlow()

    val catalogueSources: Flow<List<CatalogueSource>> = sourcesById.map { sources ->
        sources.values.filterIsInstance<CatalogueSource>()
    }

    fun replaceAll(sources: Iterable<Source>) {
        sourcesById.value = sources.associateBy(Source::id)
    }

    fun register(source: Source) {
        sourcesById.update { sources ->
            sources + (source.id to source)
        }
    }

    fun unregister(sourceId: Long) {
        sourcesById.update { sources ->
            sources - sourceId
        }
    }

    fun get(sourceId: Long): Source? = sourcesById.value[sourceId]

    fun getCatalogueSources(): List<CatalogueSource> = sourcesById.value.values.filterIsInstance<CatalogueSource>()
}
