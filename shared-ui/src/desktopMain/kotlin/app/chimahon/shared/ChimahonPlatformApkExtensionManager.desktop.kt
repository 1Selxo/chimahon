package app.chimahon.shared

import com.googlecode.d2j.dex.Dex2jar
import com.googlecode.d2j.reader.MultiDexFileReader
import com.googlecode.dex2jar.tools.BaksmaliBaseDexExceptionHandler
import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceFactory
import eu.kanade.tachiyomi.source.SourceRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dongliu.apk.parser.ApkFile
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.SimpleRemapper
import tachiyomi.core.platform.storage.PlatformStorageDirectories
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.Enumeration
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.xml.parsers.DocumentBuilderFactory

internal actual class ChimahonPlatformApkExtensionManager actual constructor(
    storageDirectories: PlatformStorageDirectories,
    private val sourceRegistry: SourceRegistry,
) {
    private val extensionDirectory = Path.of(storageDirectories.filesDir.toString(), APK_EXTENSION_DIRECTORY)
    private val loadedPackages = linkedMapOf<String, LoadedPackage>()
    private var initialized = false

    actual suspend fun reload(): List<ChimahonInstalledExtensionEntry> = withContext(Dispatchers.IO) {
        if (initialized) return@withContext installedEntries()

        Files.createDirectories(extensionDirectory)
        Files.list(extensionDirectory).use { paths ->
            paths
                .filter { it.fileName.toString().endsWith(APK_SUFFIX, ignoreCase = true) }
                .sorted()
                .forEach { apkPath ->
                    runCatching {
                        val manifest = parseManifest(apkPath)
                        val jarPath = apkPath.resolveSibling("${apkPath.fileName.toString().removeSuffix(APK_SUFFIX)}$JAR_SUFFIX")
                        if (Files.notExists(jarPath)) {
                            convertApk(apkPath, jarPath)
                        }
                        activatePackage(
                            packageId = manifest.packageName,
                            name = manifest.label.substringAfter("Tachiyomi: ").substringAfter("Mihon: "),
                            version = manifest.versionName,
                            manifest = manifest,
                            jarPath = jarPath,
                        )
                    }
                }
        }
        initialized = true
        installedEntries()
    }

    actual suspend fun install(
        extension: ChimahonRepoExtensionEntry,
        apkBytes: ByteArray,
    ): ChimahonInstalledExtensionEntry = withContext(Dispatchers.IO) {
        Files.createDirectories(extensionDirectory)
        val fileStem = extension.id.toSafeFileName()
        val temporaryApk = Files.createTempFile(extensionDirectory, "$fileStem-", ".download")
        val temporaryJar = Files.createTempFile(extensionDirectory, "$fileStem-", ".jar")
        try {
            Files.write(temporaryApk, apkBytes)
            val manifest = parseManifest(temporaryApk)
            require(manifest.packageName == extension.id) {
                "Downloaded package ${manifest.packageName} does not match ${extension.id}."
            }
            require(manifest.isTachiyomiExtension) {
                "${extension.name} is not a Tachiyomi/Mihon extension package."
            }

            convertApk(temporaryApk, temporaryJar)
            validateLoad(manifest, temporaryJar).close()

            val finalApk = extensionDirectory.resolve("$fileStem$APK_SUFFIX")
            val finalJar = extensionDirectory.resolve("$fileStem$JAR_SUFFIX")
            deactivatePackage(extension.id)
            Files.move(temporaryApk, finalApk, StandardCopyOption.REPLACE_EXISTING)
            Files.move(temporaryJar, finalJar, StandardCopyOption.REPLACE_EXISTING)

            activatePackage(
                packageId = extension.id,
                name = extension.name,
                version = extension.version,
                manifest = manifest,
                jarPath = finalJar,
            ).entry
        } finally {
            Files.deleteIfExists(temporaryApk)
            Files.deleteIfExists(temporaryJar)
        }
    }

    actual fun close() {
        loadedPackages.values.forEach { loaded ->
            loaded.sourceIds.forEach(sourceRegistry::unregister)
            loaded.classLoader.close()
        }
        loadedPackages.clear()
        initialized = false
    }

    private fun activatePackage(
        packageId: String,
        name: String,
        version: String,
        manifest: ApkManifest,
        jarPath: Path,
    ): LoadedPackage {
        val candidate = loadPackage(
            packageId = packageId,
            name = name,
            version = version,
            manifest = manifest,
            jarPath = jarPath,
        )
        val replacedSourceIds = loadedPackages[packageId]?.sourceIds.orEmpty()
        val collision = candidate.sources.firstOrNull { source ->
            sourceRegistry.get(source.id) != null && source.id !in replacedSourceIds
        }
        if (collision != null) {
            candidate.classLoader.close()
            error("Source id ${collision.id} is already registered by another extension.")
        }

        deactivatePackage(packageId)
        candidate.sources.forEach(sourceRegistry::register)
        loadedPackages[packageId] = candidate
        return candidate
    }

    private fun validateLoad(
        manifest: ApkManifest,
        jarPath: Path,
    ): Closeable {
        val loaded = loadPackage(
            packageId = manifest.packageName,
            name = manifest.label,
            version = manifest.versionName,
            manifest = manifest,
            jarPath = jarPath,
        )
        require(loaded.sources.isNotEmpty()) {
            "The extension did not provide any compatible sources."
        }
        return loaded.classLoader
    }

    private fun loadPackage(
        packageId: String,
        name: String,
        version: String,
        manifest: ApkManifest,
        jarPath: Path,
    ): LoadedPackage {
        val classLoader = SystemFirstUrlClassLoader(arrayOf(jarPath.toUri().toURL()))
        try {
            val sources = manifest.sourceClasses.flatMap { className ->
                val instance = Class.forName(className, true, classLoader)
                    .getDeclaredConstructor()
                    .newInstance()
                when (instance) {
                    is Source -> listOf(instance)
                    is SourceFactory -> instance.createSources()
                    else -> error("$className is not a Source or SourceFactory.")
                }
            }
            return LoadedPackage(
                entry = ChimahonInstalledExtensionEntry(
                    id = packageId,
                    name = name,
                    version = version,
                    sourceCount = sources.count { it is CatalogueSource },
                    packageType = ChimahonExtensionPackageType.AndroidApk,
                ),
                sources = sources,
                classLoader = classLoader,
            )
        } catch (error: Throwable) {
            classLoader.close()
            val cause = error.rootCause()
            throw IllegalStateException(
                "This extension needs an Android API that the desktop compatibility engine does not support yet: " +
                    (cause.message ?: cause::class.simpleName.orEmpty()),
                error,
            )
        }
    }

    private fun deactivatePackage(packageId: String) {
        loadedPackages.remove(packageId)?.let { loaded ->
            loaded.sourceIds.forEach(sourceRegistry::unregister)
            loaded.classLoader.close()
        }
    }

    private fun installedEntries(): List<ChimahonInstalledExtensionEntry> {
        return loadedPackages.values
            .map(LoadedPackage::entry)
            .sortedBy { it.name.lowercase() }
    }

    private fun parseManifest(apkPath: Path): ApkManifest {
        val manifestXml = ApkFile(apkPath.toFile()).use(ApkFile::getManifestXml)
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            runCatching { setFeature("http://apache.org/xml/features/disallow-doctype-decl", true) }
        }
        val document = factory.newDocumentBuilder()
            .parse(ByteArrayInputStream(manifestXml.toByteArray()))
        val manifest = document.documentElement
        val packageName = manifest.getAttribute("package")
        val versionName = manifest.getAttributeNS(ANDROID_NAMESPACE, "versionName").ifBlank { "unknown" }
        val application = document.getElementsByTagName("application").item(0)
            ?: error("Extension manifest has no application element.")
        val label = application.attributes
            ?.getNamedItemNS(ANDROID_NAMESPACE, "label")
            ?.nodeValue
            ?.takeUnless { it.startsWith("@") }
            ?: packageName.substringAfterLast(".")
        val metadata = buildMap {
            val elements = document.getElementsByTagName("meta-data")
            for (index in 0 until elements.length) {
                val attributes = elements.item(index).attributes ?: continue
                val key = attributes.getNamedItemNS(ANDROID_NAMESPACE, "name")?.nodeValue ?: continue
                val value = attributes.getNamedItemNS(ANDROID_NAMESPACE, "value")?.nodeValue ?: continue
                put(key, value)
            }
        }
        val sourceClasses = metadata[METADATA_SOURCE_CLASS]
            ?.split(";")
            ?.map(String::trim)
            ?.filter(String::isNotBlank)
            ?.map { className ->
                if (className.startsWith(".")) packageName + className else className
            }
            .orEmpty()
        require(sourceClasses.isNotEmpty()) {
            "Extension manifest does not declare $METADATA_SOURCE_CLASS."
        }
        val isExtension = buildList {
            val features = document.getElementsByTagName("uses-feature")
            for (index in 0 until features.length) {
                features.item(index).attributes
                    ?.getNamedItemNS(ANDROID_NAMESPACE, "name")
                    ?.nodeValue
                    ?.let(::add)
            }
        }.contains(EXTENSION_FEATURE)

        return ApkManifest(
            packageName = packageName,
            versionName = versionName,
            label = label,
            sourceClasses = sourceClasses,
            isTachiyomiExtension = isExtension,
        )
    }

    private fun convertApk(
        apkPath: Path,
        jarPath: Path,
    ) {
        val exceptionHandler = BaksmaliBaseDexExceptionHandler()
        val reader = MultiDexFileReader.open(Files.readAllBytes(apkPath))
        Dex2jar.from(reader)
            .withExceptionHandler(exceptionHandler)
            .reUseReg(false)
            .topoLogicalSort()
            .skipDebug(true)
            .optimizeSynchronized(false)
            .printIR(false)
            .noCode(false)
            .skipExceptions(false)
            .dontSanitizeNames(true)
            .to(jarPath)
        if (exceptionHandler.hasException()) {
            exceptionHandler.dump(
                jarPath.resolveSibling("${jarPath.fileName}.errors.txt"),
                emptyArray(),
            )
        }
        adaptJarForDesktop(apkPath, jarPath)
    }

    private fun adaptJarForDesktop(
        apkPath: Path,
        jarPath: Path,
    ) {
        val output = ByteArrayOutputStream()
        val writtenEntries = linkedSetOf<String>()
        ZipOutputStream(output).use { zipOutput ->
            Files.newInputStream(jarPath).use { input ->
                ZipInputStream(input).use { zipInput ->
                    var entry = zipInput.nextEntry
                    while (entry != null) {
                        if (!entry.isDirectory && !entry.name.startsWith("META-INF/")) {
                            val bytes = zipInput.readBytes()
                            writeZipEntry(
                                output = zipOutput,
                                name = entry.name,
                                bytes = if (entry.name.endsWith(".class")) remapClass(bytes) else bytes,
                                writtenEntries = writtenEntries,
                            )
                        }
                        entry = zipInput.nextEntry
                    }
                }
            }
            Files.newInputStream(apkPath).use { input ->
                ZipInputStream(input).use { zipInput ->
                    var entry = zipInput.nextEntry
                    while (entry != null) {
                        if (!entry.isDirectory && entry.name.startsWith("assets/")) {
                            writeZipEntry(
                                output = zipOutput,
                                name = entry.name,
                                bytes = zipInput.readBytes(),
                                writtenEntries = writtenEntries,
                            )
                        }
                        entry = zipInput.nextEntry
                    }
                }
            }
        }
        Files.write(jarPath, output.toByteArray())
    }

    private fun remapClass(bytes: ByteArray): ByteArray {
        val reader = ClassReader(bytes)
        val writer = ClassWriter(reader, 0)
        reader.accept(
            ClassRemapper(writer, SimpleRemapper(DESKTOP_CLASS_REMAP)),
            0,
        )
        return writer.toByteArray()
    }

    private fun writeZipEntry(
        output: ZipOutputStream,
        name: String,
        bytes: ByteArray,
        writtenEntries: MutableSet<String>,
    ) {
        if (!writtenEntries.add(name)) return
        output.putNextEntry(ZipEntry(name))
        output.write(bytes)
        output.closeEntry()
    }

    private data class ApkManifest(
        val packageName: String,
        val versionName: String,
        val label: String,
        val sourceClasses: List<String>,
        val isTachiyomiExtension: Boolean,
    )

    private data class LoadedPackage(
        val entry: ChimahonInstalledExtensionEntry,
        val sources: List<Source>,
        val classLoader: SystemFirstUrlClassLoader,
    ) {
        val sourceIds = sources.map(Source::id).toSet()
    }

    private class SystemFirstUrlClassLoader(
        urls: Array<URL>,
    ) : URLClassLoader(urls, null) {
        private val systemClassLoader = getSystemClassLoader()

        override fun loadClass(
            name: String,
            resolve: Boolean,
        ): Class<*> {
            var loaded = findLoadedClass(name)
            if (loaded == null) {
                loaded = runCatching { systemClassLoader.loadClass(name) }.getOrNull()
            }
            if (loaded == null) {
                loaded = runCatching { findClass(name) }.getOrElse {
                    super.loadClass(name, resolve)
                }
            }
            if (resolve) resolveClass(loaded)
            return loaded
        }

        override fun getResource(name: String): URL? {
            return systemClassLoader.getResource(name)
                ?: findResource(name)
                ?: super.getResource(name)
        }

        override fun getResources(name: String): Enumeration<URL> {
            val resources = buildList {
                listOf(
                    systemClassLoader.getResources(name),
                    findResources(name),
                    parent?.getResources(name),
                ).forEach { enumeration ->
                    while (enumeration?.hasMoreElements() == true) {
                        add(enumeration.nextElement())
                    }
                }
            }
            return object : Enumeration<URL> {
                private val iterator = resources.iterator()

                override fun hasMoreElements(): Boolean = iterator.hasNext()

                override fun nextElement(): URL = iterator.next()
            }
        }
    }

    private companion object {
        const val APK_EXTENSION_DIRECTORY = "apk-extensions"
        const val APK_SUFFIX = ".apk"
        const val JAR_SUFFIX = ".jar"
        const val ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"
        const val EXTENSION_FEATURE = "tachiyomi.extension"
        const val METADATA_SOURCE_CLASS = "tachiyomi.extension.class"

        val DESKTOP_CLASS_REMAP = mapOf(
            "eu/kanade/tachiyomi/network/NetworkHelper" to
                "eu/kanade/tachiyomi/source/online/SourceNetworkContext",
            "android/net/Uri" to
                "eu/kanade/tachiyomi/source/model/PlatformUri",
        )
    }
}

private fun String.toSafeFileName(): String {
    return map { character ->
        if (character.isLetterOrDigit() || character == '.' || character == '-' || character == '_') {
            character
        } else {
            '_'
        }
    }.joinToString("").ifBlank { "extension" }
}

private fun Throwable.rootCause(): Throwable {
    var result = this
    while (result.cause != null && result.cause !== result) {
        result = result.cause!!
    }
    return result
}
