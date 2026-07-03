package app.chimahon.shared.repositoryui

import app.chimahon.shared.ChimahonExtensionPackageType
import app.chimahon.shared.ChimahonExtensionRepoEntry
import app.chimahon.shared.ChimahonInstalledExtensionEntry
import app.chimahon.shared.ChimahonRepoExtensionEntry

enum class ChimahonRepositorySyncStatus(val title: String) {
    Idle("Not synced"),
    Queued("Queued"),
    Syncing("Syncing"),
    Synced("Synced"),
    Failed("Failed"),
    Disabled("Disabled"),
}

data class ChimahonRepositorySyncState(
    val status: ChimahonRepositorySyncStatus = ChimahonRepositorySyncStatus.Idle,
    val message: String? = null,
    val lastSyncedLabel: String? = null,
    val progressLabel: String? = null,
) {
    val isLoading: Boolean
        get() = status == ChimahonRepositorySyncStatus.Syncing || status == ChimahonRepositorySyncStatus.Queued

    val isError: Boolean
        get() = status == ChimahonRepositorySyncStatus.Failed
}

data class ChimahonRepositoryExtensionCounts(
    val available: Int = 0,
    val installed: Int = 0,
    val updates: Int = 0,
    val sources: Int = 0,
    val nsfw: Int = 0,
    val javascript: Int = 0,
    val androidApk: Int = 0,
) {
    val hasAnyExtensions: Boolean
        get() = available > 0 || installed > 0
}

enum class ChimahonRepositoryDisableState(val title: String) {
    Enabled("Enabled"),
    Disabled("Disabled"),
}

enum class ChimahonRepositoryTrustState(val title: String) {
    Trusted("Trusted"),
    Untrusted("Untrusted"),
    Unsigned("Unsigned"),
}

enum class ChimahonRepositoryRemovalState(val title: String) {
    Idle("Idle"),
    Confirming("Confirming"),
    Removing("Removing"),
    Removed("Removed"),
    Failed("Failed"),
}

data class ChimahonRepositoryPolicyState(
    val disableState: ChimahonRepositoryDisableState = ChimahonRepositoryDisableState.Enabled,
    val trustState: ChimahonRepositoryTrustState = ChimahonRepositoryTrustState.Unsigned,
    val removalState: ChimahonRepositoryRemovalState = ChimahonRepositoryRemovalState.Idle,
    val disabledReason: String? = null,
    val trustMessage: String? = null,
    val removalMessage: String? = null,
) {
    val enabled: Boolean
        get() = disableState == ChimahonRepositoryDisableState.Enabled

    val trusted: Boolean
        get() = trustState == ChimahonRepositoryTrustState.Trusted

    val removing: Boolean
        get() = removalState == ChimahonRepositoryRemovalState.Removing

    val removed: Boolean
        get() = removalState == ChimahonRepositoryRemovalState.Removed
}

data class ChimahonRepositoryUiModel(
    val baseUrl: String,
    val name: String,
    val shortName: String? = null,
    val website: String = "",
    val signingKeyFingerprint: String = "",
    val enabled: Boolean = true,
    val selected: Boolean = false,
    val trusted: Boolean = signingKeyFingerprint.isNotBlank(),
    val sync: ChimahonRepositorySyncState = ChimahonRepositorySyncState(),
    val counts: ChimahonRepositoryExtensionCounts = ChimahonRepositoryExtensionCounts(),
    val removalState: ChimahonRepositoryRemovalState = ChimahonRepositoryRemovalState.Idle,
    val disabledReason: String? = null,
    val trustMessage: String? = null,
    val removalMessage: String? = null,
) {
    val displayName: String
        get() = shortName?.takeIf { it.isNotBlank() } ?: name

    val host: String
        get() = website.ifBlank { baseUrl }.toChimahonRepositoryHost()

    val initial: String
        get() = displayName.toChimahonRepositoryInitial()

    val indexUrl: String
        get() = baseUrl.toChimahonRepositoryIndexUrl()

    val policyState: ChimahonRepositoryPolicyState
        get() = ChimahonRepositoryPolicyState(
            disableState = if (enabled) {
                ChimahonRepositoryDisableState.Enabled
            } else {
                ChimahonRepositoryDisableState.Disabled
            },
            trustState = toChimahonRepositoryTrustState(),
            removalState = removalState,
            disabledReason = disabledReason,
            trustMessage = trustMessage,
            removalMessage = removalMessage,
        )
}

data class ChimahonRepositoryListState(
    val repositories: List<ChimahonRepositoryUiModel> = emptyList(),
    val selectedBaseUrl: String? = null,
    val message: String? = null,
    val autoSyncEnabled: Boolean = true,
    val syncing: Boolean = false,
) {
    val selectedRepository: ChimahonRepositoryUiModel?
        get() = repositories.firstOrNull { it.baseUrl == selectedBaseUrl || it.selected }

    val enabledCount: Int
        get() = repositories.count { it.enabled }

    val disabledCount: Int
        get() = repositories.count { !it.enabled }
}

data class ChimahonRepositoryUiActions(
    val onSelect: (ChimahonRepositoryUiModel) -> Unit = {},
    val onRefresh: (ChimahonRepositoryUiModel) -> Unit = {},
    val onRefreshAll: () -> Unit = {},
    val onAdd: () -> Unit = {},
    val onEdit: (ChimahonRepositoryUiModel) -> Unit = {},
    val onRemove: (ChimahonRepositoryUiModel) -> Unit = {},
    val onSetTrusted: (ChimahonRepositoryUiModel, Boolean) -> Unit = { _, _ -> },
    val onCopyIndexUrl: (ChimahonRepositoryUiModel) -> Unit = {},
    val onOpenWebsite: (ChimahonRepositoryUiModel) -> Unit = {},
    val onToggleEnabled: (ChimahonRepositoryUiModel, Boolean) -> Unit = { _, _ -> },
    val onAction: (ChimahonRepositoryAction, ChimahonRepositoryUiModel) -> Unit = { _, _ -> },
)

enum class ChimahonRepositoryAction {
    Select,
    Refresh,
    Edit,
    CopyIndexUrl,
    OpenWebsite,
    Enable,
    Disable,
    Trust,
    Remove,
}

data class ChimahonRepositoryActionSpec(
    val action: ChimahonRepositoryAction,
    val title: String,
    val subtitle: String? = null,
    val enabled: Boolean = true,
    val destructive: Boolean = false,
)

enum class ChimahonRepositoryFormMode(val title: String, val actionTitle: String) {
    Add("Add extension repository", "Add"),
    Edit("Edit extension repository", "Save"),
}

data class ChimahonRepositoryFormState(
    val mode: ChimahonRepositoryFormMode = ChimahonRepositoryFormMode.Add,
    val originalBaseUrl: String? = null,
    val url: String = "",
    val name: String = "",
    val shortName: String = "",
    val website: String = "",
    val signingKeyFingerprint: String = "",
    val saving: Boolean = false,
    val message: String? = null,
) {
    val normalizedUrl: String
        get() = url.trim()

    val isEditing: Boolean
        get() = mode == ChimahonRepositoryFormMode.Edit
}

enum class ChimahonRepositoryValidationSeverity {
    Info,
    Warning,
    Error,
}

data class ChimahonRepositoryValidationMessage(
    val text: String,
    val severity: ChimahonRepositoryValidationSeverity = ChimahonRepositoryValidationSeverity.Info,
)

data class ChimahonRepositoryValidationResult(
    val messages: List<ChimahonRepositoryValidationMessage> = emptyList(),
) {
    val isValid: Boolean
        get() = messages.none { it.severity == ChimahonRepositoryValidationSeverity.Error }
}

fun ChimahonExtensionRepoEntry.toChimahonRepositoryUiModel(
    enabled: Boolean = true,
    selected: Boolean = false,
    sync: ChimahonRepositorySyncState = ChimahonRepositorySyncState(),
    counts: ChimahonRepositoryExtensionCounts = ChimahonRepositoryExtensionCounts(),
): ChimahonRepositoryUiModel {
    return ChimahonRepositoryUiModel(
        baseUrl = baseUrl,
        name = name,
        shortName = shortName,
        website = website,
        signingKeyFingerprint = signingKeyFingerprint,
        enabled = enabled,
        selected = selected,
        sync = sync,
        counts = counts,
    )
}

fun ChimahonRepositoryUiModel.toExtensionRepoEntry(): ChimahonExtensionRepoEntry {
    return ChimahonExtensionRepoEntry(
        baseUrl = baseUrl,
        name = name,
        shortName = shortName,
        website = website,
        signingKeyFingerprint = signingKeyFingerprint,
    )
}

fun ChimahonRepositoryUiModel.defaultActionSpecs(): List<ChimahonRepositoryActionSpec> {
    val refreshEnabled = enabled && !sync.isLoading
    return listOf(
        ChimahonRepositoryActionSpec(
            action = ChimahonRepositoryAction.Refresh,
            title = if (sync.isLoading) "Syncing" else "Refresh",
            subtitle = sync.lastSyncedLabel?.let { "Last synced $it" },
            enabled = refreshEnabled,
        ),
        ChimahonRepositoryActionSpec(
            action = ChimahonRepositoryAction.CopyIndexUrl,
            title = "Copy index URL",
            subtitle = indexUrl,
        ),
        ChimahonRepositoryActionSpec(
            action = ChimahonRepositoryAction.OpenWebsite,
            title = "Open website",
            subtitle = website.ifBlank { host },
            enabled = website.isNotBlank() || baseUrl.startsWith("http", ignoreCase = true),
        ),
        ChimahonRepositoryActionSpec(
            action = ChimahonRepositoryAction.Edit,
            title = "Edit",
            subtitle = "Change repository URL or trust details",
        ),
        ChimahonRepositoryActionSpec(
            action = if (enabled) ChimahonRepositoryAction.Disable else ChimahonRepositoryAction.Enable,
            title = if (enabled) "Disable" else "Enable",
            subtitle = if (enabled) {
                "Hide extensions from this repository"
            } else {
                disabledReason ?: "Use this repository again"
            },
        ),
        if (!trusted) {
            ChimahonRepositoryActionSpec(
                action = ChimahonRepositoryAction.Trust,
                title = "Trust",
                subtitle = trustMessage ?: "Allow this repository for extension installs",
            )
        } else {
            null
        },
        ChimahonRepositoryActionSpec(
            action = ChimahonRepositoryAction.Remove,
            title = if (removalState == ChimahonRepositoryRemovalState.Removing) "Removing" else "Remove",
            subtitle = removalMessage ?: "Installed extensions stay installed",
            enabled = removalState != ChimahonRepositoryRemovalState.Removing,
            destructive = true,
        ),
    ).filterNotNull()
}

fun ChimahonRepositoryUiActions.perform(
    action: ChimahonRepositoryAction,
    repository: ChimahonRepositoryUiModel,
) {
    when (action) {
        ChimahonRepositoryAction.Select -> onSelect(repository)
        ChimahonRepositoryAction.Refresh -> onRefresh(repository)
        ChimahonRepositoryAction.Edit -> onEdit(repository)
        ChimahonRepositoryAction.CopyIndexUrl -> onCopyIndexUrl(repository)
        ChimahonRepositoryAction.OpenWebsite -> onOpenWebsite(repository)
        ChimahonRepositoryAction.Enable -> onToggleEnabled(repository, false)
        ChimahonRepositoryAction.Disable -> onToggleEnabled(repository, true)
        ChimahonRepositoryAction.Trust -> onSetTrusted(repository, true)
        ChimahonRepositoryAction.Remove -> onRemove(repository)
    }
    onAction(action, repository)
}

fun ChimahonRepositoryUiModel.toChimahonRepositoryTrustState(): ChimahonRepositoryTrustState {
    return when {
        trusted -> ChimahonRepositoryTrustState.Trusted
        signingKeyFingerprint.isBlank() -> ChimahonRepositoryTrustState.Unsigned
        else -> ChimahonRepositoryTrustState.Untrusted
    }
}

fun validateChimahonRepositoryForm(
    state: ChimahonRepositoryFormState,
    knownBaseUrls: Collection<String> = emptyList(),
): ChimahonRepositoryValidationResult {
    val messages = validateChimahonRepositoryUrl(
        url = state.url,
        knownBaseUrls = knownBaseUrls,
        originalBaseUrl = state.originalBaseUrl,
    ).toMutableList()

    if (state.name.length > 80) {
        messages += ChimahonRepositoryValidationMessage(
            text = "Repository name is unusually long.",
            severity = ChimahonRepositoryValidationSeverity.Warning,
        )
    }
    if (state.signingKeyFingerprint.isNotBlank() && state.signingKeyFingerprint.length < 16) {
        messages += ChimahonRepositoryValidationMessage(
            text = "Signing key fingerprint looks too short.",
            severity = ChimahonRepositoryValidationSeverity.Warning,
        )
    }
    state.message?.takeIf { it.isNotBlank() }?.let {
        messages += ChimahonRepositoryValidationMessage(
            text = it,
            severity = if (it.startsWith("Could not", ignoreCase = true)) {
                ChimahonRepositoryValidationSeverity.Error
            } else {
                ChimahonRepositoryValidationSeverity.Info
            },
        )
    }

    return ChimahonRepositoryValidationResult(messages)
}

fun validateChimahonRepositoryUrl(
    url: String,
    knownBaseUrls: Collection<String> = emptyList(),
    originalBaseUrl: String? = null,
): List<ChimahonRepositoryValidationMessage> {
    val trimmed = url.trim()
    val messages = mutableListOf<ChimahonRepositoryValidationMessage>()
    if (trimmed.isBlank()) {
        return listOf(
            ChimahonRepositoryValidationMessage(
                text = "Repository URL is required.",
                severity = ChimahonRepositoryValidationSeverity.Error,
            ),
        )
    }
    if (trimmed.any { it.isWhitespace() }) {
        messages += ChimahonRepositoryValidationMessage(
            text = "Repository URL cannot contain spaces.",
            severity = ChimahonRepositoryValidationSeverity.Error,
        )
    }
    if (!trimmed.startsWith("https://", ignoreCase = true) &&
        !trimmed.startsWith("http://", ignoreCase = true)
    ) {
        messages += ChimahonRepositoryValidationMessage(
            text = "Use a full http or https URL.",
            severity = ChimahonRepositoryValidationSeverity.Error,
        )
    }
    if (trimmed.toChimahonRepositoryHost().isBlank()) {
        messages += ChimahonRepositoryValidationMessage(
            text = "Repository URL needs a host.",
            severity = ChimahonRepositoryValidationSeverity.Error,
        )
    }

    val identity = trimmed.toChimahonRepositoryIdentityKey()
    val originalIdentity = originalBaseUrl?.toChimahonRepositoryIdentityKey()
    val duplicate = knownBaseUrls
        .map { it.toChimahonRepositoryIdentityKey() }
        .any { it == identity && it != originalIdentity }
    if (duplicate) {
        messages += ChimahonRepositoryValidationMessage(
            text = "This repository is already added.",
            severity = ChimahonRepositoryValidationSeverity.Error,
        )
    }
    if (!trimmed.endsWith(".json", ignoreCase = true) && !trimmed.endsWith("/")) {
        messages += ChimahonRepositoryValidationMessage(
            text = "The app will read ${trimmed.toChimahonRepositoryIndexUrl()}.",
            severity = ChimahonRepositoryValidationSeverity.Info,
        )
    }
    return messages
}

fun Iterable<ChimahonRepoExtensionEntry>.toChimahonRepositoryExtensionCounts(
    installed: Iterable<ChimahonInstalledExtensionEntry> = emptyList(),
): ChimahonRepositoryExtensionCounts {
    val available = toList()
    val installedByKey = installed.associateBy { "${it.packageType.name}:${it.id}" }
    val updates = available.count { extension ->
        val installedVersion = installedByKey["${extension.packageType.name}:${extension.id}"]?.version
        installedVersion != null && compareChimahonRepositoryVersions(extension.version, installedVersion) > 0
    }
    return ChimahonRepositoryExtensionCounts(
        available = available.size,
        installed = available.count { "${it.packageType.name}:${it.id}" in installedByKey },
        updates = updates,
        sources = available.sumOf { it.sourceCount.coerceAtLeast(0) },
        nsfw = available.count { it.isNsfw },
        javascript = available.count { it.packageType == ChimahonExtensionPackageType.JavaScript },
        androidApk = available.count { it.packageType == ChimahonExtensionPackageType.AndroidApk },
    )
}

fun String.toChimahonRepositoryIndexUrl(): String {
    val trimmed = trim()
    return if (trimmed.endsWith(".json", ignoreCase = true) || trimmed.endsWith(".js", ignoreCase = true)) {
        trimmed
    } else {
        "${trimmed.trimEnd('/')}/index.min.json"
    }
}

fun String.toChimahonRepositoryIdentityKey(): String {
    return trim()
        .removeSuffix("/")
        .removeSuffix("/index.min.json")
        .removeSuffix("/index.json")
        .removeSuffix("/repo.min.json")
        .removeSuffix("/repo.json")
        .trimEnd('/')
        .lowercase()
}

fun String.toChimahonRepositoryHost(): String {
    return trim()
        .substringAfter("://", "")
        .substringBefore('/')
        .substringBefore('?')
        .substringBefore('#')
}

fun String.toChimahonRepositoryInitial(): String {
    val words = trim()
        .replace('-', ' ')
        .replace('_', ' ')
        .split(' ')
        .mapNotNull { word -> word.firstOrNull { it.isLetterOrDigit() }?.uppercaseChar()?.toString() }
    return when {
        words.size >= 2 -> (words[0] + words[1]).take(2)
        words.size == 1 -> words[0]
        else -> "R"
    }
}

private fun compareChimahonRepositoryVersions(first: String, second: String): Int {
    val firstParts = first.versionParts()
    val secondParts = second.versionParts()
    val count = maxOf(firstParts.size, secondParts.size)
    for (index in 0 until count) {
        val left = firstParts.getOrNull(index) ?: "0"
        val right = secondParts.getOrNull(index) ?: "0"
        val leftNumber = left.toLongOrNull()
        val rightNumber = right.toLongOrNull()
        val comparison = if (leftNumber != null && rightNumber != null) {
            leftNumber.compareTo(rightNumber)
        } else {
            left.compareTo(right, ignoreCase = true)
        }
        if (comparison != 0) return comparison
    }
    return 0
}

private fun String.versionParts(): List<String> {
    return split(Regex("[^A-Za-z0-9]+")).filter { it.isNotBlank() }
}
