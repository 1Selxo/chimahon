package app.chimahon.shared

data class ChimahonExtensionDetails(
    val id: String,
    val name: String,
    val packageType: ChimahonExtensionPackageType,
    val installedVersion: String?,
    val availableVersion: String?,
    val installedSourceCount: Int,
    val availableSourceCount: Int,
    val language: String,
    val isNsfw: Boolean,
    val repoBaseUrl: String?,
    val artifactUrl: String?,
    val updateAvailable: Boolean,
    val isSupportedOnPlatform: Boolean,
) {
    val isInstalled: Boolean
        get() = installedVersion != null

    val isAvailable: Boolean
        get() = availableVersion != null

    val installState: ChimahonExtensionInstallState
        get() = when {
            !isSupportedOnPlatform && !isInstalled -> ChimahonExtensionInstallState.Unsupported
            updateAvailable -> ChimahonExtensionInstallState.UpdateAvailable
            isInstalled && isAvailable -> ChimahonExtensionInstallState.Installed
            isInstalled -> ChimahonExtensionInstallState.MissingFromRepositories
            else -> ChimahonExtensionInstallState.NotInstalled
        }

    val canInstall: Boolean
        get() = !isInstalled && isAvailable && isSupportedOnPlatform

    val canUpdate: Boolean
        get() = updateAvailable && isSupportedOnPlatform

    val displayVersion: String
        get() = installedVersion ?: availableVersion.orEmpty()

    val displaySourceCount: Int
        get() = maxOf(installedSourceCount, availableSourceCount)
}

enum class ChimahonExtensionInstallState {
    NotInstalled,
    Installed,
    UpdateAvailable,
    MissingFromRepositories,
    Unsupported,
}

data class ChimahonExtensionManagementData(
    val installedExtensions: List<ChimahonInstalledExtensionEntry>,
    val extensions: List<ChimahonExtensionDetails>,
    val availableUpdates: List<ChimahonExtensionDetails>,
    val apkExtensionsSupported: Boolean,
    val registeredApkSourceCount: Int,
    val errors: List<String>,
)

data class ChimahonExtensionUninstallResult(
    val extensionId: String,
    val packageType: ChimahonExtensionPackageType,
    val supported: Boolean,
    val removed: Boolean,
    val installedExtensions: List<ChimahonInstalledExtensionEntry>,
    val errors: List<String>,
)

internal fun buildExtensionManagementData(
    installedExtensions: List<ChimahonInstalledExtensionEntry>,
    availableExtensions: List<ChimahonRepoExtensionEntry>,
    apkStatus: ChimahonApkExtensionManagerStatus,
    errors: List<String>,
): ChimahonExtensionManagementData {
    val installedByKey = installedExtensions.associateBy(ChimahonInstalledExtensionEntry::extensionKey)
    val availableByKey = availableExtensions
        .groupBy(ChimahonRepoExtensionEntry::extensionKey)
        .mapValues { (_, entries) ->
            entries.maxWithOrNull { first, second ->
                val versionComparison = compareExtensionVersions(first.version, second.version)
                if (versionComparison != 0) {
                    versionComparison
                } else {
                    compareValuesBy(
                        first,
                        second,
                        { it.sourceCount },
                        { it.language },
                    )
                }
            } ?: entries.first()
        }
    val keys = (installedByKey.keys + availableByKey.keys)
        .sortedWith(compareBy<ExtensionKey> { it.packageType.ordinal }.thenBy { it.id })
    val details = keys.map { key ->
        val installed = installedByKey[key]
        val available = availableByKey[key]
        ChimahonExtensionDetails(
            id = key.id,
            name = installed?.name ?: available?.name ?: key.id,
            packageType = key.packageType,
            installedVersion = installed?.version,
            availableVersion = available?.version,
            installedSourceCount = installed?.sourceCount ?: 0,
            availableSourceCount = available?.sourceCount ?: 0,
            language = available?.language.orEmpty(),
            isNsfw = available?.isNsfw == true,
            repoBaseUrl = available?.repoBaseUrl,
            artifactUrl = available?.artifactUrl,
            updateAvailable = installed != null &&
                available != null &&
                compareExtensionVersions(available.version, installed.version) > 0,
            isSupportedOnPlatform = key.packageType != ChimahonExtensionPackageType.AndroidApk ||
                apkStatus.isSupported,
        )
    }
    return ChimahonExtensionManagementData(
        installedExtensions = installedExtensions.sortedBy { it.name.lowercase() },
        extensions = details.sortedWith(
            compareByDescending<ChimahonExtensionDetails> { it.isInstalled }
                .thenBy { it.name.lowercase() },
        ),
        availableUpdates = details
            .filter(ChimahonExtensionDetails::canUpdate)
            .sortedBy { it.name.lowercase() },
        apkExtensionsSupported = apkStatus.isSupported,
        registeredApkSourceCount = apkStatus.registeredSourceCount,
        errors = (errors + apkStatus.errors).distinct(),
    )
}

private data class ExtensionKey(
    val id: String,
    val packageType: ChimahonExtensionPackageType,
)

private fun ChimahonInstalledExtensionEntry.extensionKey(): ExtensionKey {
    return ExtensionKey(id = id, packageType = packageType)
}

private fun ChimahonRepoExtensionEntry.extensionKey(): ExtensionKey {
    return ExtensionKey(id = id, packageType = packageType)
}

private fun compareExtensionVersions(first: String, second: String): Int {
    if (first == second) return 0
    val firstTokens = VERSION_TOKEN.findAll(first).map(MatchResult::value).toList()
    val secondTokens = VERSION_TOKEN.findAll(second).map(MatchResult::value).toList()
    val tokenCount = maxOf(firstTokens.size, secondTokens.size)
    repeat(tokenCount) { index ->
        val firstToken = firstTokens.getOrNull(index) ?: "0"
        val secondToken = secondTokens.getOrNull(index) ?: "0"
        val firstNumber = firstToken.toLongOrNull()
        val secondNumber = secondToken.toLongOrNull()
        val comparison = when {
            firstNumber != null && secondNumber != null -> firstNumber.compareTo(secondNumber)
            firstNumber != null -> 1
            secondNumber != null -> -1
            else -> firstToken.compareTo(secondToken, ignoreCase = true)
        }
        if (comparison != 0) return comparison
    }
    return first.compareTo(second, ignoreCase = true)
}

private val VERSION_TOKEN = Regex("""\d+|[A-Za-z]+""")
