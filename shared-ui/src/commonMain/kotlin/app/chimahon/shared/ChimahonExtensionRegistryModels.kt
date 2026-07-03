package app.chimahon.shared

data class ChimahonExtensionInstallKey(
    val packageType: ChimahonExtensionPackageType,
    val extensionId: String,
) {
    val stableKey: String
        get() = "${packageType.name}:$extensionId"
}

data class ChimahonExtensionRegistryPlatformSupport(
    val apkExtensionsSupported: Boolean = true,
    val javaScriptExtensionsSupported: Boolean = true,
    val platformName: String = "",
    val apkUnsupportedMessage: String? = null,
    val javaScriptUnsupportedMessage: String? = null,
) {
    val supportsAnyExtensionPackage: Boolean
        get() = apkExtensionsSupported || javaScriptExtensionsSupported
}

enum class ChimahonExtensionRegistryPlatformSupportState(
    val title: String,
    val badgeLabel: String,
    val supported: Boolean,
) {
    Supported("Supported", "SUPPORTED", true),
    ApkUnsupported("APK unsupported", "UNSUPPORTED", false),
    JavaScriptUnsupported("JavaScript unsupported", "UNSUPPORTED", false),
    UnsupportedPackage("Unsupported package", "UNSUPPORTED", false),
}

data class ChimahonExtensionRegistryPlatformSupportMessage(
    val packageType: ChimahonExtensionPackageType,
    val state: ChimahonExtensionRegistryPlatformSupportState,
    val title: String = state.title,
    val detail: String? = null,
    val badgeLabel: String = state.badgeLabel,
) {
    val supported: Boolean
        get() = state.supported
}

fun ChimahonInstalledExtensionEntry.toChimahonExtensionInstallKey(): ChimahonExtensionInstallKey {
    return ChimahonExtensionInstallKey(
        packageType = packageType,
        extensionId = id,
    )
}

fun ChimahonRepoExtensionEntry.toChimahonExtensionInstallKey(): ChimahonExtensionInstallKey {
    return ChimahonExtensionInstallKey(
        packageType = packageType,
        extensionId = id,
    )
}

fun ChimahonExtensionPackageType.toChimahonExtensionRegistryPlatformSupportMessage(
    support: ChimahonExtensionRegistryPlatformSupport = ChimahonExtensionRegistryPlatformSupport(),
): ChimahonExtensionRegistryPlatformSupportMessage {
    val platformLabel = support.platformName
        .takeIf(String::isNotBlank)
        ?.let { " on $it" }
        .orEmpty()
    return when (this) {
        ChimahonExtensionPackageType.JavaScript -> {
            if (support.javaScriptExtensionsSupported) {
                ChimahonExtensionRegistryPlatformSupportMessage(
                    packageType = this,
                    state = ChimahonExtensionRegistryPlatformSupportState.Supported,
                    detail = "JavaScript extensions can register sources$platformLabel.",
                )
            } else {
                ChimahonExtensionRegistryPlatformSupportMessage(
                    packageType = this,
                    state = ChimahonExtensionRegistryPlatformSupportState.JavaScriptUnsupported,
                    detail = support.javaScriptUnsupportedMessage
                        ?: "JavaScript extensions cannot register sources$platformLabel.",
                )
            }
        }
        ChimahonExtensionPackageType.AndroidApk -> {
            if (support.apkExtensionsSupported) {
                ChimahonExtensionRegistryPlatformSupportMessage(
                    packageType = this,
                    state = ChimahonExtensionRegistryPlatformSupportState.Supported,
                    detail = "Android APK extensions can register sources$platformLabel.",
                )
            } else {
                ChimahonExtensionRegistryPlatformSupportMessage(
                    packageType = this,
                    state = ChimahonExtensionRegistryPlatformSupportState.ApkUnsupported,
                    detail = support.apkUnsupportedMessage
                        ?: "Android APK extensions cannot register sources$platformLabel.",
                )
            }
        }
    }
}

fun ChimahonRepoExtensionEntry.toChimahonExtensionRegistryPlatformSupportMessage(
    support: ChimahonExtensionRegistryPlatformSupport = ChimahonExtensionRegistryPlatformSupport(),
): ChimahonExtensionRegistryPlatformSupportMessage {
    return packageType.toChimahonExtensionRegistryPlatformSupportMessage(support)
}

fun ChimahonInstalledExtensionEntry.toChimahonExtensionRegistryPlatformSupportMessage(
    support: ChimahonExtensionRegistryPlatformSupport = ChimahonExtensionRegistryPlatformSupport(),
): ChimahonExtensionRegistryPlatformSupportMessage {
    return packageType.toChimahonExtensionRegistryPlatformSupportMessage(support)
}
