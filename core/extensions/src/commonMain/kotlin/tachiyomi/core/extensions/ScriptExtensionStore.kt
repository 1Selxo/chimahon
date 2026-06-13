package tachiyomi.core.extensions

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import tachiyomi.core.platform.storage.PlatformStorageDirectories

class ScriptExtensionStore(
    storageDirectories: PlatformStorageDirectories,
    private val loader: ScriptExtensionLoader,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {
    private val extensionsDirectory = storageDirectories.filesDir / EXTENSIONS_DIRECTORY

    suspend fun install(script: String): LoadedScriptExtension {
        val extension = loader.load(script)
        fileSystem.createDirectories(extensionsDirectory)

        val target = pathFor(extension.manifest.id)
        val temporary = extensionsDirectory / "${target.name}.tmp"
        fileSystem.write(temporary) {
            writeUtf8(script)
        }
        fileSystem.atomicMove(temporary, target)
        return extension
    }

    suspend fun loadInstalled(): List<LoadedScriptExtension> {
        val paths = fileSystem.listOrNull(extensionsDirectory)
            .orEmpty()
            .filter { it.name.endsWith(SCRIPT_EXTENSION_SUFFIX) }
            .sortedBy(Path::name)

        return paths.map { path ->
            loader.load(
                fileSystem.read(path) {
                    readUtf8()
                },
            )
        }
    }

    fun uninstall(extensionId: String): Boolean {
        val path = pathFor(extensionId)
        if (!fileSystem.exists(path)) return false
        fileSystem.delete(path)
        return true
    }

    private fun pathFor(extensionId: String): Path {
        require(extensionId.matches(EXTENSION_ID_PATTERN)) {
            "Extension id must contain only letters, numbers, dots, dashes, or underscores"
        }
        return extensionsDirectory / "$extensionId$SCRIPT_EXTENSION_SUFFIX".toPath()
    }

    private companion object {
        const val EXTENSIONS_DIRECTORY = "script-extensions"
        const val SCRIPT_EXTENSION_SUFFIX = ".js"
        val EXTENSION_ID_PATTERN = Regex("[A-Za-z0-9._-]+")
    }
}
