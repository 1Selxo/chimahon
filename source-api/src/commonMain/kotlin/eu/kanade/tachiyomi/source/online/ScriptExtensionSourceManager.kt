package eu.kanade.tachiyomi.source.online

import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceRegistry
import tachiyomi.core.extensions.LoadedScriptExtension
import tachiyomi.core.extensions.ScriptExtensionInvoker
import tachiyomi.core.extensions.ScriptExtensionStore

class ScriptExtensionSourceManager(
    private val store: ScriptExtensionStore,
    private val invoker: ScriptExtensionInvoker,
    private val registry: SourceRegistry,
) {
    private var managedSourceIdsByExtension = emptyMap<String, Set<Long>>()

    suspend fun reload(): List<LoadedScriptExtension> {
        val extensions = store.loadInstalled()
        replaceManagedSources(extensions)
        return extensions
    }

    suspend fun install(script: String): LoadedScriptExtension {
        val extension = store.install(script)
        replaceManagedSources(
            store.loadInstalled(),
        )
        return extension
    }

    fun uninstall(extensionId: String): Boolean {
        if (!store.uninstall(extensionId)) return false

        managedSourceIdsByExtension[extensionId].orEmpty().forEach(registry::unregister)
        managedSourceIdsByExtension = managedSourceIdsByExtension - extensionId
        return true
    }

    private fun replaceManagedSources(extensions: List<LoadedScriptExtension>) {
        val sourcesByExtension = extensions.associate { extension ->
            extension.manifest.id to createScriptSources(extension, invoker)
        }
        validateSourceIds(sourcesByExtension)

        managedSourceIdsByExtension.values.flatten().forEach(registry::unregister)
        sourcesByExtension.values.flatten().forEach(registry::register)
        managedSourceIdsByExtension = sourcesByExtension.mapValues { (_, sources) ->
            sources.map(Source::id).toSet()
        }
    }

    private fun validateSourceIds(sourcesByExtension: Map<String, List<Source>>) {
        val previousManagedIds = managedSourceIdsByExtension.values.flatten().toSet()
        val newSources = sourcesByExtension.values.flatten()
        val duplicateIds = newSources.groupBy(Source::id)
            .filterValues { sources -> sources.size > 1 }
            .keys
        require(duplicateIds.isEmpty()) {
            "Script extensions declare duplicate source ids: ${duplicateIds.sorted().joinToString()}"
        }

        val conflictingIds = newSources.map(Source::id)
            .filter { sourceId ->
                sourceId !in previousManagedIds && registry.get(sourceId) != null
            }
            .toSet()
        require(conflictingIds.isEmpty()) {
            "Script extensions conflict with registered source ids: ${conflictingIds.sorted().joinToString()}"
        }
    }
}

internal expect fun createScriptSources(
    extension: LoadedScriptExtension,
    invoker: ScriptExtensionInvoker,
): List<Source>
