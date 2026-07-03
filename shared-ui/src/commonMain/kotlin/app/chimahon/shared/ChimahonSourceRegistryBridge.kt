package app.chimahon.shared

import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceRegistry
import tachiyomi.core.extensions.LoadedScriptExtension
import tachiyomi.core.extensions.ScriptExtensionPackageType
import tachiyomi.core.extensions.ScriptSourceManifest

enum class ChimahonExtensionSourceRegistrationStatus(val title: String) {
    Pending("Pending"),
    Empty("No sources"),
    Registered("Registered"),
    PartiallyRegistered("Partially registered"),
    Unsupported("Unsupported"),
    Conflict("Source ID conflict"),
    Failed("Failed"),
    Removed("Removed"),
}

data class ChimahonExtensionSourceDescriptor(
    val id: Long,
    val name: String,
    val language: String,
    val supportsLatest: Boolean = false,
    val baseUrl: String = "",
    val extensionId: String? = null,
    val packageType: ChimahonExtensionPackageType? = null,
    val enabled: Boolean = true,
) {
    val sourceEntry: ChimahonSourceEntry
        get() = ChimahonSourceEntry(
            id = id,
            name = name,
            language = language,
            supportsLatest = supportsLatest,
        )
}

data class ChimahonInstalledExtensionSourceRegistration(
    val key: ChimahonExtensionInstallKey,
    val extensionName: String,
    val version: String,
    val declaredSourceCount: Int,
    val registeredSources: List<ChimahonExtensionSourceDescriptor> = emptyList(),
    val messages: List<String> = emptyList(),
    val status: ChimahonExtensionSourceRegistrationStatus = chimahonExtensionSourceRegistrationStatus(
        declaredSourceCount = declaredSourceCount,
        registeredSourceCount = registeredSources.size,
        messages = messages,
    ),
) {
    val registeredSourceCount: Int
        get() = registeredSources.size

    val missingSourceCount: Int
        get() = (declaredSourceCount - registeredSourceCount).coerceAtLeast(0)

    val isComplete: Boolean
        get() = status == ChimahonExtensionSourceRegistrationStatus.Registered ||
            status == ChimahonExtensionSourceRegistrationStatus.Empty

    val sourceEntries: List<ChimahonSourceEntry>
        get() = registeredSources.map(ChimahonExtensionSourceDescriptor::sourceEntry)
}

data class ChimahonSourceRegistrySnapshot(
    val registrations: List<ChimahonInstalledExtensionSourceRegistration> = emptyList(),
    val orphanSources: List<ChimahonExtensionSourceDescriptor> = emptyList(),
    val registrySourceCount: Int = registrations.sumOf { it.registeredSourceCount } + orphanSources.size,
    val messages: List<String> = emptyList(),
) {
    val registeredExtensionCount: Int
        get() = registrations.count(ChimahonInstalledExtensionSourceRegistration::isComplete)

    val pendingExtensionCount: Int
        get() = registrations.count {
            it.status == ChimahonExtensionSourceRegistrationStatus.Pending ||
                it.status == ChimahonExtensionSourceRegistrationStatus.PartiallyRegistered
        }

    val failedExtensionCount: Int
        get() = registrations.count {
            it.status == ChimahonExtensionSourceRegistrationStatus.Failed ||
                it.status == ChimahonExtensionSourceRegistrationStatus.Conflict
        }

    val allSourceEntries: List<ChimahonSourceEntry>
        get() = (registrations.flatMap { it.sourceEntries } + orphanSources.map { it.sourceEntry })
            .distinctBy(ChimahonSourceEntry::id)
            .sortedBy { it.name.lowercase() }
}

enum class ChimahonSourceRegistryMutationKind {
    Register,
    Unregister,
}

data class ChimahonSourceRegistryMutationResult(
    val kind: ChimahonSourceRegistryMutationKind,
    val changedSourceIds: List<Long> = emptyList(),
    val skippedSourceIds: List<Long> = emptyList(),
    val messages: List<String> = emptyList(),
) {
    val changed: Boolean
        get() = changedSourceIds.isNotEmpty()

    val hasIssues: Boolean
        get() = skippedSourceIds.isNotEmpty() || messages.isNotEmpty()
}

class ChimahonSourceRegistryBridge(
    private val sourceRegistry: SourceRegistry,
) {
    fun sourceEntries(): List<ChimahonSourceEntry> {
        return sourceRegistry.getCatalogueSources()
            .map { source -> source.toChimahonExtensionSourceDescriptor().sourceEntry }
            .sortedBy { it.name.lowercase() }
    }

    fun sourceDescriptors(): List<ChimahonExtensionSourceDescriptor> {
        return sourceRegistry.getCatalogueSources()
            .map { source -> source.toChimahonExtensionSourceDescriptor() }
            .sortedBy { it.name.lowercase() }
    }

    fun snapshot(
        installedExtensions: Iterable<ChimahonInstalledExtensionEntry>,
        sourcesByExtension: Map<ChimahonExtensionInstallKey, List<ChimahonExtensionSourceDescriptor>> = emptyMap(),
        messagesByExtension: Map<ChimahonExtensionInstallKey, List<String>> = emptyMap(),
    ): ChimahonSourceRegistrySnapshot {
        val registrySources = sourceDescriptors()
        val claimedSourceIds = sourcesByExtension.values.flatten().map(ChimahonExtensionSourceDescriptor::id).toSet()
        val registrations = installedExtensions.map { extension ->
            val key = extension.toChimahonExtensionInstallKey()
            val registeredSources = sourcesByExtension[key].orEmpty()
            val messages = messagesByExtension[key].orEmpty()
            ChimahonInstalledExtensionSourceRegistration(
                key = key,
                extensionName = extension.name,
                version = extension.version,
                declaredSourceCount = extension.sourceCount,
                registeredSources = registeredSources,
                status = chimahonExtensionSourceRegistrationStatus(
                    declaredSourceCount = extension.sourceCount,
                    registeredSourceCount = registeredSources.size,
                    messages = messages,
                ),
                messages = messages,
            )
        }
        return ChimahonSourceRegistrySnapshot(
            registrations = registrations.sortedBy { it.extensionName.lowercase() },
            orphanSources = registrySources.filterNot { it.id in claimedSourceIds },
            registrySourceCount = registrySources.size,
            messages = messagesByExtension.values.flatten().distinct(),
        )
    }

    fun registerSources(sources: Iterable<Source>): ChimahonSourceRegistryMutationResult {
        val changed = mutableListOf<Long>()
        val skipped = mutableListOf<Long>()
        val messages = mutableListOf<String>()
        sources.forEach { source ->
            val existing = sourceRegistry.get(source.id)
            if (existing != null && existing !== source) {
                skipped += source.id
                messages += "Source ID ${source.id} is already registered by ${existing.name}."
            } else {
                sourceRegistry.register(source)
                changed += source.id
            }
        }
        return ChimahonSourceRegistryMutationResult(
            kind = ChimahonSourceRegistryMutationKind.Register,
            changedSourceIds = changed.distinct(),
            skippedSourceIds = skipped.distinct(),
            messages = messages,
        )
    }

    fun unregisterSources(sourceIds: Iterable<Long>): ChimahonSourceRegistryMutationResult {
        val changed = mutableListOf<Long>()
        val skipped = mutableListOf<Long>()
        sourceIds.distinct().forEach { sourceId ->
            if (sourceRegistry.get(sourceId) == null) {
                skipped += sourceId
            } else {
                sourceRegistry.unregister(sourceId)
                changed += sourceId
            }
        }
        return ChimahonSourceRegistryMutationResult(
            kind = ChimahonSourceRegistryMutationKind.Unregister,
            changedSourceIds = changed,
            skippedSourceIds = skipped,
        )
    }
}

fun Source.toChimahonExtensionSourceDescriptor(
    extensionId: String? = null,
    packageType: ChimahonExtensionPackageType? = null,
    enabled: Boolean = true,
): ChimahonExtensionSourceDescriptor {
    val catalogueSource = this as? CatalogueSource
    return ChimahonExtensionSourceDescriptor(
        id = id,
        name = name,
        language = lang,
        supportsLatest = catalogueSource?.supportsLatest == true,
        extensionId = extensionId,
        packageType = packageType,
        enabled = enabled,
    )
}

fun Iterable<Source>.toChimahonExtensionSourceDescriptors(
    key: ChimahonExtensionInstallKey,
    enabled: Boolean = true,
): List<ChimahonExtensionSourceDescriptor> {
    return map { source ->
        source.toChimahonExtensionSourceDescriptor(
            extensionId = key.extensionId,
            packageType = key.packageType,
            enabled = enabled,
        )
    }
}

fun ScriptSourceManifest.toChimahonExtensionSourceDescriptor(
    extensionId: String,
    packageType: ChimahonExtensionPackageType = ChimahonExtensionPackageType.JavaScript,
    enabled: Boolean = true,
): ChimahonExtensionSourceDescriptor {
    return ChimahonExtensionSourceDescriptor(
        id = id,
        name = name,
        language = language,
        supportsLatest = supportsLatest,
        baseUrl = baseUrl,
        extensionId = extensionId,
        packageType = packageType,
        enabled = enabled,
    )
}

fun LoadedScriptExtension.toChimahonInstalledExtensionEntry(): ChimahonInstalledExtensionEntry {
    return ChimahonInstalledExtensionEntry(
        id = manifest.id,
        name = manifest.name,
        version = manifest.version,
        sourceCount = manifest.sourceCount,
        packageType = manifest.packageType.toChimahonExtensionPackageType(),
    )
}

fun ChimahonInstalledExtensionEntry.toChimahonInstalledExtensionSourceRegistration(
    registeredSources: List<ChimahonExtensionSourceDescriptor> = emptyList(),
    messages: List<String> = emptyList(),
    status: ChimahonExtensionSourceRegistrationStatus? = null,
): ChimahonInstalledExtensionSourceRegistration {
    return ChimahonInstalledExtensionSourceRegistration(
        key = toChimahonExtensionInstallKey(),
        extensionName = name,
        version = version,
        declaredSourceCount = sourceCount,
        registeredSources = registeredSources,
        messages = messages,
        status = status ?: chimahonExtensionSourceRegistrationStatus(
            declaredSourceCount = sourceCount,
            registeredSourceCount = registeredSources.size,
            messages = messages,
        ),
    )
}

fun LoadedScriptExtension.toChimahonInstalledExtensionSourceRegistration(
    status: ChimahonExtensionSourceRegistrationStatus? = null,
    messages: List<String> = emptyList(),
): ChimahonInstalledExtensionSourceRegistration {
    val packageType = manifest.packageType.toChimahonExtensionPackageType()
    return ChimahonInstalledExtensionSourceRegistration(
        key = ChimahonExtensionInstallKey(
            packageType = packageType,
            extensionId = manifest.id,
        ),
        extensionName = manifest.name,
        version = manifest.version,
        declaredSourceCount = manifest.sourceCount,
        registeredSources = manifest.sources.map { source ->
            source.toChimahonExtensionSourceDescriptor(
                extensionId = manifest.id,
                packageType = packageType,
            )
        },
        messages = messages,
        status = status ?: chimahonExtensionSourceRegistrationStatus(
            declaredSourceCount = manifest.sourceCount,
            registeredSourceCount = manifest.sources.size,
            messages = messages,
        ),
    )
}

fun ScriptExtensionPackageType.toChimahonExtensionPackageType(): ChimahonExtensionPackageType {
    return when (this) {
        ScriptExtensionPackageType.JavaScript -> ChimahonExtensionPackageType.JavaScript
        ScriptExtensionPackageType.AndroidApk -> ChimahonExtensionPackageType.AndroidApk
    }
}

private fun chimahonExtensionSourceRegistrationStatus(
    declaredSourceCount: Int,
    registeredSourceCount: Int,
    messages: List<String> = emptyList(),
): ChimahonExtensionSourceRegistrationStatus {
    return when {
        messages.any { it.contains("conflict", ignoreCase = true) } ->
            ChimahonExtensionSourceRegistrationStatus.Conflict
        messages.isNotEmpty() && registeredSourceCount == 0 ->
            ChimahonExtensionSourceRegistrationStatus.Failed
        declaredSourceCount <= 0 ->
            ChimahonExtensionSourceRegistrationStatus.Empty
        registeredSourceCount <= 0 ->
            ChimahonExtensionSourceRegistrationStatus.Pending
        registeredSourceCount < declaredSourceCount ->
            ChimahonExtensionSourceRegistrationStatus.PartiallyRegistered
        else ->
            ChimahonExtensionSourceRegistrationStatus.Registered
    }
}
