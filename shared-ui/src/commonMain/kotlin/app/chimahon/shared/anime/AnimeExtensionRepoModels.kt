package app.chimahon.shared.anime

import app.chimahon.shared.ChimahonExtensionPackageType

data class ChimahonAnimeExtensionRepoSummary(
    val baseUrl: String,
    val name: String,
    val shortName: String? = null,
    val website: String = "",
    val signingKeyFingerprint: String = "",
    val enabled: Boolean = true,
    val trusted: Boolean = true,
    val state: ChimahonAnimeExtensionRepoState = ChimahonAnimeExtensionRepoState.Idle,
    val unavailableReason: String? = null,
    val installedCount: Int = 0,
    val availableCount: Int = 0,
    val updateCount: Int = 0,
    val sourceCount: Int = 0,
    val languages: List<String> = emptyList(),
) {
    val displayName: String
        get() = shortName?.takeIf { it.isNotBlank() } ?: name

    val normalizedBaseUrl: String
        get() = baseUrl.toChimahonAnimeExtensionRepoBaseUrl()

    val indexUrl: String
        get() = normalizedBaseUrl.toChimahonAnimeExtensionRepoIndexUrl()

    val hasExtensions: Boolean
        get() = installedCount > 0 || availableCount > 0 || updateCount > 0 || sourceCount > 0

    val isUnavailable: Boolean
        get() = unavailableReason != null || state == ChimahonAnimeExtensionRepoState.Unavailable

    val status: ChimahonAnimeExtensionRepoStatus
        get() = toChimahonAnimeExtensionRepoStatus()
}

data class ChimahonAnimeExtensionRepoData(
    val repos: List<ChimahonAnimeExtensionRepoSummary>,
    val isRefreshing: Boolean = false,
    val selectedBaseUrl: String? = null,
    val autoSyncEnabled: Boolean = true,
    val message: String? = null,
    val dialog: ChimahonAnimeExtensionRepoDialog? = null,
) {
    val isEmpty: Boolean
        get() = repos.isEmpty()

    val count: Int
        get() = repos.size

    val enabledCount: Int
        get() = repos.count { it.enabled }

    val disabledCount: Int
        get() = repos.count { !it.enabled }

    val unavailableCount: Int
        get() = repos.count { it.isUnavailable }

    val installedCount: Int
        get() = repos.sumOf { it.installedCount }

    val availableCount: Int
        get() = repos.sumOf { it.availableCount }

    val updateCount: Int
        get() = repos.sumOf { it.updateCount }

    val sourceCount: Int
        get() = repos.sumOf { it.sourceCount }

    val trustedCount: Int
        get() = repos.count { it.trusted }

    val untrustedCount: Int
        get() = repos.count { !it.trusted }
}

enum class ChimahonAnimeExtensionRepoState {
    Idle,
    Refreshing,
    Loaded,
    Disabled,
    Unavailable,
}

enum class ChimahonAnimeExtensionRepoStatusSeverity {
    Ready,
    Busy,
    Disabled,
    Warning,
    Error,
}

data class ChimahonAnimeExtensionRepoStatus(
    val label: String,
    val detail: String? = null,
    val severity: ChimahonAnimeExtensionRepoStatusSeverity = ChimahonAnimeExtensionRepoStatusSeverity.Ready,
)

data class ChimahonAnimeExtensionPlatformCapabilities(
    val apkExtensionsSupported: Boolean = true,
    val javaScriptExtensionsSupported: Boolean = true,
    val platformName: String = "",
) {
    val supportsAnyExtensionPackage: Boolean
        get() = apkExtensionsSupported || javaScriptExtensionsSupported
}

enum class ChimahonAnimeExtensionPlatformSupportState {
    Supported,
    ApkUnsupported,
    JavaScriptUnsupported,
    UnsupportedPackage,
}

data class ChimahonAnimeExtensionRepoSaveRequest(
    val baseUrl: String,
    val name: String = "",
    val shortName: String? = null,
    val website: String = "",
    val signingKeyFingerprint: String = "",
    val originalBaseUrl: String? = null,
) {
    val normalizedBaseUrl: String
        get() = baseUrl.toChimahonAnimeExtensionRepoBaseUrl()

    val normalizedOriginalBaseUrl: String?
        get() = originalBaseUrl?.toChimahonAnimeExtensionRepoBaseUrl()

    val indexUrl: String
        get() = normalizedBaseUrl.toChimahonAnimeExtensionRepoIndexUrl()

    fun toSummary(): ChimahonAnimeExtensionRepoSummary {
        val display = name.trim().ifBlank { normalizedBaseUrl.toChimahonAnimeExtensionRepoHost() }
        return ChimahonAnimeExtensionRepoSummary(
            baseUrl = normalizedBaseUrl,
            name = display,
            shortName = shortName?.trim()?.takeIf(String::isNotBlank),
            website = website.trim(),
            signingKeyFingerprint = signingKeyFingerprint.trim(),
        )
    }
}

sealed interface ChimahonAnimeExtensionRepoDialog {
    data object Create : ChimahonAnimeExtensionRepoDialog
    data class Delete(val baseUrl: String) : ChimahonAnimeExtensionRepoDialog
    data class Confirm(val url: String) : ChimahonAnimeExtensionRepoDialog
    data class Edit(val repo: ChimahonAnimeExtensionRepoSummary) : ChimahonAnimeExtensionRepoDialog
    data class Conflict(
        val oldRepo: ChimahonAnimeExtensionRepoSummary,
        val newRepo: ChimahonAnimeExtensionRepoSummary,
    ) : ChimahonAnimeExtensionRepoDialog
}

sealed interface ChimahonAnimeExtensionRepoEvent {
    data object InvalidUrl : ChimahonAnimeExtensionRepoEvent
    data object RepoAlreadyExists : ChimahonAnimeExtensionRepoEvent
    data class ValidationFailed(val messages: List<String>) : ChimahonAnimeExtensionRepoEvent
    data class RepoUnavailable(val baseUrl: String, val reason: String) : ChimahonAnimeExtensionRepoEvent
    data class DuplicateFingerprint(
        val oldRepo: ChimahonAnimeExtensionRepoSummary,
        val newRepo: ChimahonAnimeExtensionRepoSummary,
    ) : ChimahonAnimeExtensionRepoEvent
}

fun String.toChimahonAnimeExtensionRepoBaseUrl(): String {
    return trim()
        .trimEnd('/')
        .removeSuffix("/repo.json")
        .removeSuffix("/chimahon.json")
        .removeSuffix("/index.json")
        .removeSuffix("/index.min.json")
        .trimEnd('/')
}

fun String.toChimahonAnimeExtensionRepoIndexUrl(): String {
    val normalized = toChimahonAnimeExtensionRepoBaseUrl()
    if (normalized.isBlank()) return ""
    return if (normalized.endsWith(".json", ignoreCase = true) || normalized.endsWith(".js", ignoreCase = true)) {
        normalized
    } else {
        "$normalized/index.min.json"
    }
}

fun String.toChimahonAnimeExtensionRepoHost(): String {
    return ifBlank { "Anime extension repo" }
        .substringAfter("://", this)
        .substringBefore('/')
        .ifBlank { "Anime extension repo" }
}

fun ChimahonAnimeExtensionRepoSummary.toChimahonAnimeExtensionRepoStatus(): ChimahonAnimeExtensionRepoStatus {
    return when {
        !enabled -> ChimahonAnimeExtensionRepoStatus(
            label = "Disabled",
            detail = "Disabled repositories stay saved but are excluded from extension refreshes.",
            severity = ChimahonAnimeExtensionRepoStatusSeverity.Disabled,
        )
        state == ChimahonAnimeExtensionRepoState.Refreshing -> ChimahonAnimeExtensionRepoStatus(
            label = "Refreshing",
            severity = ChimahonAnimeExtensionRepoStatusSeverity.Busy,
        )
        unavailableReason != null || state == ChimahonAnimeExtensionRepoState.Unavailable -> ChimahonAnimeExtensionRepoStatus(
            label = "Unavailable",
            detail = unavailableReason,
            severity = ChimahonAnimeExtensionRepoStatusSeverity.Error,
        )
        !trusted -> ChimahonAnimeExtensionRepoStatus(
            label = "Untrusted",
            detail = "Repository signing metadata should be reviewed before installing extensions.",
            severity = ChimahonAnimeExtensionRepoStatusSeverity.Warning,
        )
        state == ChimahonAnimeExtensionRepoState.Loaded -> ChimahonAnimeExtensionRepoStatus(
            label = "Loaded",
            severity = ChimahonAnimeExtensionRepoStatusSeverity.Ready,
        )
        else -> ChimahonAnimeExtensionRepoStatus(
            label = "Ready",
            severity = ChimahonAnimeExtensionRepoStatusSeverity.Ready,
        )
    }
}

fun ChimahonExtensionPackageType.toChimahonAnimeExtensionPlatformSupportState(
    capabilities: ChimahonAnimeExtensionPlatformCapabilities,
): ChimahonAnimeExtensionPlatformSupportState {
    return when (this) {
        ChimahonExtensionPackageType.AndroidApk -> {
            if (capabilities.apkExtensionsSupported) {
                ChimahonAnimeExtensionPlatformSupportState.Supported
            } else {
                ChimahonAnimeExtensionPlatformSupportState.ApkUnsupported
            }
        }
        ChimahonExtensionPackageType.JavaScript -> {
            if (capabilities.javaScriptExtensionsSupported) {
                ChimahonAnimeExtensionPlatformSupportState.Supported
            } else {
                ChimahonAnimeExtensionPlatformSupportState.JavaScriptUnsupported
            }
        }
    }
}
