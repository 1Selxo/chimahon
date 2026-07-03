package app.chimahon.shared.animeextensionui

import app.chimahon.shared.ChimahonExtensionPackageType
import app.chimahon.shared.ChimahonExtensionRepoEntry
import app.chimahon.shared.ChimahonInstalledExtensionEntry
import app.chimahon.shared.ChimahonRepoExtensionEntry
import app.chimahon.shared.anime.ChimahonAnimeExtensionRepoDialog
import app.chimahon.shared.anime.ChimahonAnimeExtensionPlatformCapabilities
import app.chimahon.shared.anime.ChimahonAnimeExtensionPlatformSupportState
import app.chimahon.shared.anime.ChimahonAnimeExtensionRepoSaveRequest
import app.chimahon.shared.anime.ChimahonAnimeExtensionRepoState
import app.chimahon.shared.anime.ChimahonAnimeExtensionRepoSummary
import app.chimahon.shared.anime.toChimahonAnimeExtensionPlatformSupportState

const val AnimeExtensionPageSize: Int = 40
const val AnimeExtensionPrefetchItemCount: Int = 80

enum class AnimeExtensionKind(val title: String) {
    Apk("Android APK"),
    JavaScript("JavaScript"),
    Unknown("Extension"),
}

enum class AnimeExtensionInstallStep(val title: String) {
    Idle("Idle"),
    Pending("Pending"),
    Downloading("Downloading"),
    Installing("Installing"),
    Installed("Installed"),
    Error("Error"),
}

enum class AnimeExtensionTrustState(val title: String) {
    Trusted("Trusted"),
    Untrusted("Untrusted"),
    Obsolete("Obsolete"),
    Shared("Shared"),
}

enum class AnimeExtensionOperationKind(val title: String) {
    Install("Install"),
    Update("Update"),
    Uninstall("Uninstall"),
    Trust("Trust"),
    ToggleSource("Toggle source"),
    Repository("Repository"),
}

enum class AnimeExtensionOperationStatus(val title: String) {
    Idle("Idle"),
    Queued("Queued"),
    Downloading("Downloading"),
    Installing("Installing"),
    Updating("Updating"),
    Uninstalling("Uninstalling"),
    Trusting("Trusting"),
    TogglingSource("Updating source"),
    Complete("Complete"),
    Error("Error"),
    Blocked("Unavailable"),
}

data class AnimeExtensionOperationState(
    val status: AnimeExtensionOperationStatus = AnimeExtensionOperationStatus.Idle,
    val message: String? = null,
) {
    val busy: Boolean
        get() = status == AnimeExtensionOperationStatus.Queued ||
            status == AnimeExtensionOperationStatus.Downloading ||
            status == AnimeExtensionOperationStatus.Installing ||
            status == AnimeExtensionOperationStatus.Updating ||
            status == AnimeExtensionOperationStatus.Uninstalling ||
            status == AnimeExtensionOperationStatus.Trusting ||
            status == AnimeExtensionOperationStatus.TogglingSource

    val failed: Boolean
        get() = status == AnimeExtensionOperationStatus.Error
}

data class AnimeExtensionPlatformState(
    val apkExtensionsSupported: Boolean = true,
    val javaScriptExtensionsSupported: Boolean = true,
    val platformName: String = "",
) {
    companion object {
        val Android: AnimeExtensionPlatformState = AnimeExtensionPlatformState(
            apkExtensionsSupported = true,
            javaScriptExtensionsSupported = true,
            platformName = "Android",
        )

        val JavaScriptOnly: AnimeExtensionPlatformState = AnimeExtensionPlatformState(
            apkExtensionsSupported = false,
            javaScriptExtensionsSupported = true,
        )

        val Ios: AnimeExtensionPlatformState = JavaScriptOnly.copy(platformName = "iOS")
    }

    val backendCapabilities: ChimahonAnimeExtensionPlatformCapabilities
        get() = ChimahonAnimeExtensionPlatformCapabilities(
            apkExtensionsSupported = apkExtensionsSupported,
            javaScriptExtensionsSupported = javaScriptExtensionsSupported,
            platformName = platformName,
        )

    val supportsAnyExtensionPackage: Boolean
        get() = apkExtensionsSupported || javaScriptExtensionsSupported
}

enum class AnimeExtensionPlatformSupportState(
    val title: String,
    val badgeLabel: String,
    val supported: Boolean,
) {
    Supported("Supported", "SUPPORTED", true),
    ApkUnsupported("APK unsupported", "UNSUPPORTED", false),
    JavaScriptUnsupported("JavaScript unsupported", "UNSUPPORTED", false),
    UnsupportedPackage("Unsupported package", "UNSUPPORTED", false),
    UnknownPackage("Unknown package", "UNKNOWN", true),
}

enum class AnimeExtensionFilter(val title: String) {
    All("All"),
    Updates("Updates"),
    Installed("Installed"),
    Available("Available"),
    Untrusted("Untrusted"),
    Obsolete("Obsolete"),
    Nsfw("NSFW"),
    Torrent("Torrent"),
}

enum class AnimeExtensionSort(val title: String) {
    Name("Name"),
    Language("Language"),
    Repository("Repository"),
    SourceCount("Source count"),
    Version("Version"),
}

enum class AnimeExtensionHeader(val title: String) {
    Updates("Updates pending"),
    Installed("Installed"),
    Available("Available"),
    Untrusted("Untrusted"),
    Repository("Repository"),
}

enum class AnimeExtensionRepoSyncState(val title: String) {
    Idle("Ready"),
    Refreshing("Refreshing"),
    Loaded("Loaded"),
    Disabled("Disabled"),
    Error("Unavailable"),
}

enum class AnimeExtensionRepoDialogMode(val title: String, val actionTitle: String) {
    Add("Add anime extension repo", "Add"),
    Edit("Edit anime extension repo", "Save"),
}

enum class AnimeExtensionRepoAction {
    Select,
    OpenWebsite,
    CopyIndexUrl,
    Refresh,
    Edit,
    Enable,
    Disable,
    Trust,
    Remove,
}

enum class AnimeExtensionAction {
    Open,
    Details,
    Install,
    Update,
    UpdateAll,
    Cancel,
    Retry,
    Uninstall,
    Trust,
    OpenWebsite,
    OpenSettings,
    EnableSource,
    DisableSource,
    EnableAllSources,
    DisableAllSources,
    ClearCookies,
    AppInfo,
    CopyDebugInfo,
    ToggleIncognito,
}

data class AnimeExtensionActionState(
    val action: AnimeExtensionAction,
    val title: String,
    val enabled: Boolean = true,
    val loading: Boolean = false,
    val active: Boolean = false,
    val status: AnimeExtensionOperationStatus = AnimeExtensionOperationStatus.Idle,
    val blockedReason: String? = null,
)

enum class AnimeExtensionSourceToggleStatus(val title: String) {
    Enabled("Enabled"),
    Disabled("Disabled"),
    Enabling("Enabling"),
    Disabling("Disabling"),
    Error("Error"),
}

data class AnimeExtensionSourceActionState(
    val action: AnimeExtensionAction,
    val title: String,
    val enabled: Boolean = true,
    val loading: Boolean = false,
    val status: AnimeExtensionSourceToggleStatus,
    val message: String? = null,
)

enum class AnimeExtensionRepoStatusSeverity {
    Ready,
    Busy,
    Disabled,
    Warning,
    Error,
}

data class AnimeExtensionRepoStatusModel(
    val label: String,
    val detail: String? = null,
    val severity: AnimeExtensionRepoStatusSeverity = AnimeExtensionRepoStatusSeverity.Ready,
)

data class AnimeExtensionRepoRowActionState(
    val action: AnimeExtensionRepoAction,
    val label: String,
    val icon: AnimeExtensionIcon,
    val enabled: Boolean = true,
    val loading: Boolean = false,
    val active: Boolean = false,
)

data class AnimeExtensionSourceUiModel(
    val id: Long,
    val name: String,
    val language: String,
    val baseUrl: String = "",
    val enabled: Boolean = true,
    val configurable: Boolean = false,
    val labelAsName: Boolean = false,
    val toggleEnabled: Boolean = true,
    val toggleLoading: Boolean = false,
    val statusMessage: String? = null,
) {
    val displayName: String
        get() = if (labelAsName) name else language.animeExtensionLanguageDisplayName()

    val subtitle: String
        get() = listOf(name.takeIf { labelAsName.not() }, baseUrl.takeIf(String::isNotBlank))
            .filterNotNull()
            .joinToString(" / ")

    val toggleStatus: AnimeExtensionSourceToggleStatus
        get() = when {
            statusMessage != null -> AnimeExtensionSourceToggleStatus.Error
            toggleLoading && enabled -> AnimeExtensionSourceToggleStatus.Disabling
            toggleLoading -> AnimeExtensionSourceToggleStatus.Enabling
            enabled -> AnimeExtensionSourceToggleStatus.Enabled
            else -> AnimeExtensionSourceToggleStatus.Disabled
        }

    val canToggle: Boolean
        get() = toggleEnabled && !toggleLoading
}

data class AnimeExtensionRepoUiModel(
    val baseUrl: String,
    val name: String,
    val shortName: String? = null,
    val website: String = "",
    val signingKeyFingerprint: String = "",
    val enabled: Boolean = true,
    val selected: Boolean = false,
    val trusted: Boolean = true,
    val syncState: AnimeExtensionRepoSyncState = AnimeExtensionRepoSyncState.Idle,
    val errorMessage: String? = null,
    val installedCount: Int = 0,
    val availableCount: Int = 0,
    val updateCount: Int = 0,
    val sourceCount: Int = 0,
    val languages: List<String> = emptyList(),
    val openWebsiteEnabled: Boolean = true,
    val copyIndexEnabled: Boolean = true,
    val refreshEnabled: Boolean = true,
    val enableToggleEnabled: Boolean = true,
    val editEnabled: Boolean = true,
    val trustEnabled: Boolean = true,
    val removeEnabled: Boolean = true,
) {
    val normalizedBaseUrl: String
        get() = baseUrl.trim().trimEnd('/')

    val displayName: String
        get() = shortName?.takeIf { it.isNotBlank() } ?: name.ifBlank { normalizedBaseUrl.animeExtensionRepoHost() }

    val indexUrl: String
        get() = normalizedBaseUrl.toAnimeExtensionIndexUrl()

    val hasExtensions: Boolean
        get() = installedCount > 0 || availableCount > 0 || updateCount > 0 || sourceCount > 0

    val totalExtensionCount: Int
        get() = installedCount + availableCount

    val isUnavailable: Boolean
        get() = errorMessage != null || syncState == AnimeExtensionRepoSyncState.Error

    val stateDetailLabel: String?
        get() = repoStatus().detail

    val countSummary: String
        get() = buildList {
            add("$installedCount installed")
            add("$availableCount available")
            if (updateCount > 0) add("$updateCount updates")
            if (sourceCount > 0) add("$sourceCount sources")
        }.joinToString(", ")

    val statusLabel: String
        get() = repoStatus().label

    val canOpenWebsite: Boolean
        get() = openWebsiteEnabled && (website.isNotBlank() || baseUrl.isNotBlank())

    val canCopyIndexUrl: Boolean
        get() = copyIndexEnabled && baseUrl.isNotBlank()

    val canRefresh: Boolean
        get() = refreshEnabled && enabled

    val canEnable: Boolean
        get() = enableToggleEnabled && !enabled

    val canDisable: Boolean
        get() = enableToggleEnabled && enabled

    val canEdit: Boolean
        get() = editEnabled

    val canTrust: Boolean
        get() = trustEnabled && !trusted

    val canRemove: Boolean
        get() = removeEnabled
}

data class AnimeExtensionUiModel(
    val id: String,
    val name: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long = 0L,
    val libVersion: Double = 0.0,
    val language: String? = null,
    val repositoryBaseUrl: String? = null,
    val repositoryName: String? = null,
    val iconUrl: String? = null,
    val signatureHash: String = "",
    val kind: AnimeExtensionKind = AnimeExtensionKind.Unknown,
    val installStep: AnimeExtensionInstallStep = AnimeExtensionInstallStep.Idle,
    val trustState: AnimeExtensionTrustState = AnimeExtensionTrustState.Trusted,
    val sources: List<AnimeExtensionSourceUiModel> = emptyList(),
    val operationStates: Map<AnimeExtensionOperationKind, AnimeExtensionOperationState> = emptyMap(),
    val installed: Boolean = false,
    val available: Boolean = !installed,
    val hasUpdate: Boolean = false,
    val obsolete: Boolean = false,
    val shared: Boolean = false,
    val nsfw: Boolean = false,
    val torrent: Boolean = false,
    val incognito: Boolean = false,
    val selected: Boolean = false,
) {
    val stableKey: String
        get() = "${packageName.ifBlank { id }}:${repositoryBaseUrl.orEmpty()}:$versionCode"

    val sourceCount: Int
        get() = sources.size

    val sourceCountLabel: String
        get() = when (sourceCount) {
            0 -> "No sources"
            1 -> "1 source"
            else -> "$sourceCount sources"
        }

    val languageLabel: String?
        get() = language?.takeIf(String::isNotBlank)?.animeExtensionLanguageDisplayName()

    val installBusy: Boolean
        get() = installStep == AnimeExtensionInstallStep.Pending ||
            installStep == AnimeExtensionInstallStep.Downloading ||
            installStep == AnimeExtensionInstallStep.Installing

    val isUntrusted: Boolean
        get() = trustState == AnimeExtensionTrustState.Untrusted

    val visibleWarningLabel: String?
        get() = when {
            isUntrusted -> "UNTRUSTED"
            obsolete || trustState == AnimeExtensionTrustState.Obsolete -> "OBSOLETE"
            nsfw -> "NSFW"
            torrent -> "TORRENT"
            else -> null
        }
}

data class AnimeExtensionGroup(
    val header: AnimeExtensionHeader,
    val extensions: List<AnimeExtensionUiModel>,
)

data class AnimeExtensionRepoListState(
    val repositories: List<AnimeExtensionRepoUiModel> = emptyList(),
    val selectedBaseUrl: String? = null,
    val refreshing: Boolean = false,
    val autoSyncEnabled: Boolean = true,
    val message: String? = null,
) {
    val selectedRepository: AnimeExtensionRepoUiModel?
        get() {
            val selected = selectedBaseUrl?.trim()?.trimEnd('/')
            return repositories.firstOrNull {
                it.normalizedBaseUrl == selected || it.selected
            }
        }

    val disabledCount: Int
        get() = repositories.count { !it.enabled }

    val enabledCount: Int
        get() = repositories.count { it.enabled }

    val unavailableCount: Int
        get() = repositories.count { it.isUnavailable }

    val installedCount: Int
        get() = repositories.sumOf { it.installedCount }

    val availableCount: Int
        get() = repositories.sumOf { it.availableCount }

    val updateCount: Int
        get() = repositories.sumOf { it.updateCount }

    val sourceCount: Int
        get() = repositories.sumOf { it.sourceCount }

    val hasRepositories: Boolean
        get() = repositories.isNotEmpty()
}

data class AnimeExtensionRepoFormState(
    val mode: AnimeExtensionRepoDialogMode = AnimeExtensionRepoDialogMode.Add,
    val url: String = "",
    val originalBaseUrl: String? = null,
    val name: String = "",
    val shortName: String = "",
    val website: String = "",
    val signingKeyFingerprint: String = "",
    val saving: Boolean = false,
    val validationMessages: List<AnimeExtensionRepoValidationMessage> = emptyList(),
) {
    val normalizedUrl: String
        get() = url.trim().trimEnd('/')

    val editing: Boolean
        get() = mode == AnimeExtensionRepoDialogMode.Edit

    val normalizedOriginalBaseUrl: String?
        get() = originalBaseUrl?.trim()?.trimEnd('/')

    fun toSaveRequest(): ChimahonAnimeExtensionRepoSaveRequest {
        return ChimahonAnimeExtensionRepoSaveRequest(
            baseUrl = normalizedUrl,
            name = name.trim(),
            shortName = shortName.trim().takeIf(String::isNotBlank),
            website = website.trim(),
            signingKeyFingerprint = signingKeyFingerprint.trim(),
            originalBaseUrl = normalizedOriginalBaseUrl,
        )
    }
}

enum class AnimeExtensionRepoValidationSeverity {
    Info,
    Warning,
    Error,
}

data class AnimeExtensionRepoValidationMessage(
    val text: String,
    val severity: AnimeExtensionRepoValidationSeverity = AnimeExtensionRepoValidationSeverity.Error,
)

data class AnimeExtensionRepoValidationResult(
    val isValid: Boolean,
    val messages: List<AnimeExtensionRepoValidationMessage> = emptyList(),
) {
    val hasErrors: Boolean
        get() = messages.any { it.severity == AnimeExtensionRepoValidationSeverity.Error }
}

sealed interface AnimeExtensionRepoSheetState {
    data object None : AnimeExtensionRepoSheetState
    data class Form(val state: AnimeExtensionRepoFormState) : AnimeExtensionRepoSheetState
    data class Confirm(val url: String, val confirming: Boolean = false) : AnimeExtensionRepoSheetState
    data class Remove(val repository: AnimeExtensionRepoUiModel, val removing: Boolean = false) : AnimeExtensionRepoSheetState
    data class Conflict(
        val oldRepository: AnimeExtensionRepoUiModel,
        val newRepository: AnimeExtensionRepoUiModel,
    ) : AnimeExtensionRepoSheetState
}

data class AnimeExtensionListState(
    val updates: List<AnimeExtensionUiModel> = emptyList(),
    val installed: List<AnimeExtensionUiModel> = emptyList(),
    val available: List<AnimeExtensionUiModel> = emptyList(),
    val untrusted: List<AnimeExtensionUiModel> = emptyList(),
    val repositories: AnimeExtensionRepoListState = AnimeExtensionRepoListState(),
    val platform: AnimeExtensionPlatformState = AnimeExtensionPlatformState(),
    val query: String = "",
    val filter: AnimeExtensionFilter = AnimeExtensionFilter.All,
    val sort: AnimeExtensionSort = AnimeExtensionSort.Name,
    val selectedLanguage: String = AnimeExtensionAllLanguages,
    val visibleAvailableCount: Int = AnimeExtensionPageSize,
    val autoLoadMore: Boolean = true,
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val loadingMore: Boolean = false,
    val errorMessage: String? = null,
    val installPermissionWarning: Boolean = false,
) {
    val allExtensions: List<AnimeExtensionUiModel>
        get() = updates + installed + available + untrusted

    val isEmpty: Boolean
        get() = allExtensions.isEmpty()

    val languageOptions: List<String>
        get() = listOf(AnimeExtensionAllLanguages) + allExtensions
            .mapNotNull { it.language?.takeIf(String::isNotBlank)?.animeExtensionLanguageCode() }
            .distinct()
            .sorted()

    val updateCount: Int
        get() = updates.size

    val installedCount: Int
        get() = installed.size + updates.size

    val availableCount: Int
        get() = available.size
}

data class AnimeExtensionActions(
    val onExtensionAction: (AnimeExtensionAction, AnimeExtensionUiModel) -> Unit = { _, _ -> },
    val onInstall: (AnimeExtensionUiModel) -> Unit = {
        onExtensionAction(AnimeExtensionAction.Install, it)
    },
    val onUpdate: (AnimeExtensionUiModel) -> Unit = {
        onExtensionAction(AnimeExtensionAction.Update, it)
    },
    val onUninstall: (AnimeExtensionUiModel) -> Unit = {
        onExtensionAction(AnimeExtensionAction.Uninstall, it)
    },
    val onTrust: (AnimeExtensionUiModel) -> Unit = {
        onExtensionAction(AnimeExtensionAction.Trust, it)
    },
    val onCancel: (AnimeExtensionUiModel) -> Unit = {
        onExtensionAction(AnimeExtensionAction.Cancel, it)
    },
    val onRetryExtension: (AnimeExtensionUiModel) -> Unit = {
        onExtensionAction(AnimeExtensionAction.Retry, it)
    },
    val onSetSourceEnabled: (AnimeExtensionUiModel, AnimeExtensionSourceUiModel, Boolean) -> Unit = { extension, _, enabled ->
        onExtensionAction(
            if (enabled) AnimeExtensionAction.EnableSource else AnimeExtensionAction.DisableSource,
            extension,
        )
    },
    val onToggleSource: (AnimeExtensionUiModel, AnimeExtensionSourceUiModel) -> Unit = { extension, source ->
        onSetSourceEnabled(extension, source, !source.enabled)
    },
    val onOpenSourceSettings: (AnimeExtensionUiModel, AnimeExtensionSourceUiModel) -> Unit = { extension, _ ->
        onExtensionAction(AnimeExtensionAction.OpenSettings, extension)
    },
    val onUpdateAll: () -> Unit = {},
    val onLoadMore: () -> Unit = {},
    val onRefresh: () -> Unit = {},
    val onRetry: () -> Unit = {},
    val onQueryChange: (String) -> Unit = {},
    val onFilterChange: (AnimeExtensionFilter) -> Unit = {},
    val onLanguageChange: (String) -> Unit = {},
    val onSortChange: (AnimeExtensionSort) -> Unit = {},
)

data class AnimeExtensionRepoActions(
    val onRepositoryAction: (AnimeExtensionRepoAction, AnimeExtensionRepoUiModel) -> Unit = { _, _ -> },
    val onAdd: () -> Unit = {},
    val onRefreshAll: () -> Unit = {},
    val onSetRepositoryEnabled: (AnimeExtensionRepoUiModel, Boolean) -> Unit = { repository, enabled ->
        onRepositoryAction(
            if (enabled) AnimeExtensionRepoAction.Enable else AnimeExtensionRepoAction.Disable,
            repository,
        )
    },
    val onSelectRepository: (AnimeExtensionRepoUiModel) -> Unit = {
        onRepositoryAction(AnimeExtensionRepoAction.Select, it)
    },
    val onOpenRepositoryWebsite: (AnimeExtensionRepoUiModel) -> Unit = {
        onRepositoryAction(AnimeExtensionRepoAction.OpenWebsite, it)
    },
    val onCopyRepositoryIndexUrl: (AnimeExtensionRepoUiModel) -> Unit = {
        onRepositoryAction(AnimeExtensionRepoAction.CopyIndexUrl, it)
    },
    val onRefreshRepository: (AnimeExtensionRepoUiModel) -> Unit = {
        onRepositoryAction(AnimeExtensionRepoAction.Refresh, it)
    },
    val onEditRepository: (AnimeExtensionRepoUiModel) -> Unit = {
        onRepositoryAction(AnimeExtensionRepoAction.Edit, it)
    },
    val onEnableRepository: (AnimeExtensionRepoUiModel) -> Unit = {
        onSetRepositoryEnabled(it, true)
    },
    val onDisableRepository: (AnimeExtensionRepoUiModel) -> Unit = {
        onSetRepositoryEnabled(it, false)
    },
    val onTrustRepository: (AnimeExtensionRepoUiModel) -> Unit = {
        onRepositoryAction(AnimeExtensionRepoAction.Trust, it)
    },
    val onRemoveRepository: (AnimeExtensionRepoUiModel) -> Unit = {
        onRepositoryAction(AnimeExtensionRepoAction.Remove, it)
    },
    val onUrlChange: (String) -> Unit = {},
    val onNameChange: (String) -> Unit = {},
    val onShortNameChange: (String) -> Unit = {},
    val onWebsiteChange: (String) -> Unit = {},
    val onSigningKeyChange: (String) -> Unit = {},
    val onSaveForm: () -> Unit = {},
    val onSubmitForm: (AnimeExtensionRepoFormState) -> Unit = { onSaveForm() },
    val onDismissSheet: () -> Unit = {},
    val onConfirmRemove: () -> Unit = {},
    val onConfirmRemoveRepository: (AnimeExtensionRepoUiModel) -> Unit = { onConfirmRemove() },
    val onConfirmAddRepository: (String) -> Unit = {},
    val onConfirmConflict: () -> Unit = {},
    val onConfirmConflictRepositories: (AnimeExtensionRepoUiModel, AnimeExtensionRepoUiModel) -> Unit = { _, _ ->
        onConfirmConflict()
    },
)

data class AnimeExtensionAutoLoadState(
    val enabled: Boolean,
    val hasMore: Boolean,
    val loading: Boolean = false,
    val visibleCount: Int = 0,
    val totalCount: Int = 0,
    val missingList: Boolean = false,
)

data class AnimeExtensionDetailsState(
    val extension: AnimeExtensionUiModel? = null,
    val platform: AnimeExtensionPlatformState = AnimeExtensionPlatformState(),
    val incognito: Boolean = false,
    val loading: Boolean = false,
    val errorMessage: String? = null,
) {
    val sources: List<AnimeExtensionSourceUiModel>
        get() = extension?.sources.orEmpty()
}

const val AnimeExtensionAllLanguages: String = "All"

fun AnimeExtensionListState.filteredGroups(): List<AnimeExtensionGroup> {
    val groups = mutableListOf<AnimeExtensionGroup>()
    val updateItems = updates.filteredAndSorted(this)
    val installedItems = installed.filteredAndSorted(this)
    val availableItems = available.filteredAndSorted(this)
    val untrustedItems = untrusted.filteredAndSorted(this)

    if (filter == AnimeExtensionFilter.All || filter == AnimeExtensionFilter.Updates) {
        if (updateItems.isNotEmpty()) groups += AnimeExtensionGroup(AnimeExtensionHeader.Updates, updateItems)
    }
    if (filter == AnimeExtensionFilter.All || filter == AnimeExtensionFilter.Untrusted) {
        if (untrustedItems.isNotEmpty()) groups += AnimeExtensionGroup(AnimeExtensionHeader.Untrusted, untrustedItems)
    }
    if (filter == AnimeExtensionFilter.All || filter == AnimeExtensionFilter.Installed) {
        if (installedItems.isNotEmpty()) groups += AnimeExtensionGroup(AnimeExtensionHeader.Installed, installedItems)
    }
    if (filter != AnimeExtensionFilter.Installed && filter != AnimeExtensionFilter.Updates) {
        val visible = availableItems.take(visibleAvailableCount.coerceAtLeast(0))
        if (visible.isNotEmpty()) groups += AnimeExtensionGroup(AnimeExtensionHeader.Available, visible)
    }
    return groups
}

fun AnimeExtensionListState.filteredAvailable(): List<AnimeExtensionUiModel> {
    return available.filteredAndSorted(this)
}

fun AnimeExtensionListState.autoLoadState(): AnimeExtensionAutoLoadState {
    val filteredCount = filteredAvailable().size
    val expectedCount = expectedAvailableCount(filteredCount)
    return AnimeExtensionAutoLoadState(
        enabled = autoLoadMore,
        hasMore = visibleAvailableCount < filteredCount || filteredCount < expectedCount,
        loading = loadingMore,
        visibleCount = visibleAvailableCount.coerceAtMost(filteredCount),
        totalCount = expectedCount,
        missingList = filteredCount < expectedCount,
    )
}

fun AnimeExtensionListState.hasActiveFilters(): Boolean {
    return query.isNotBlank() ||
        filter != AnimeExtensionFilter.All ||
        sort != AnimeExtensionSort.Name ||
        selectedLanguage != AnimeExtensionAllLanguages
}

fun AnimeExtensionListState.availableUpdatesByKey(): Map<String, AnimeExtensionUiModel> {
    val installedVersions = (installed + updates).associate { it.animeExtensionInstallKey() to it.versionName }
    return available
        .filter { available ->
            if (!available.platformSupportState(platform).supported) return@filter false
            val installedVersion = installedVersions[available.animeExtensionInstallKey()] ?: return@filter false
            compareAnimeExtensionVersions(available.versionName, installedVersion) > 0
        }
        .groupBy { it.animeExtensionInstallKey() }
        .mapValues { (_, entries) ->
            entries.maxWithOrNull { first, second ->
                compareAnimeExtensionVersions(first.versionName, second.versionName)
            }!!
        }
}

fun AnimeExtensionUiModel.animeExtensionInstallKey(): String {
    return "${kind.name}:${packageName.ifBlank { id }}"
}

fun AnimeExtensionUiModel.platformSupportState(
    platform: AnimeExtensionPlatformState,
): AnimeExtensionPlatformSupportState {
    val packageType = kind.toChimahonExtensionPackageType()
    if (packageType == null) {
        return if (platform.supportsAnyExtensionPackage) {
            AnimeExtensionPlatformSupportState.UnknownPackage
        } else {
            AnimeExtensionPlatformSupportState.UnsupportedPackage
        }
    }
    return packageType
        .toChimahonAnimeExtensionPlatformSupportState(platform.backendCapabilities)
        .toAnimeExtensionPlatformSupportState()
}

fun AnimeExtensionPlatformSupportState.unsupportedDetail(
    extension: AnimeExtensionUiModel,
    platform: AnimeExtensionPlatformState,
): String {
    return unsupportedReason(extension, platform) ?: "${extension.kind.title} anime extension support is available."
}

fun AnimeExtensionUiModel.operationState(kind: AnimeExtensionOperationKind): AnimeExtensionOperationState {
    operationStates[kind]?.let { return it }
    return when (kind) {
        AnimeExtensionOperationKind.Install,
        AnimeExtensionOperationKind.Update -> installStep.toAnimeExtensionOperationState(kind)
        AnimeExtensionOperationKind.Uninstall,
        AnimeExtensionOperationKind.Trust,
        AnimeExtensionOperationKind.ToggleSource,
        AnimeExtensionOperationKind.Repository -> AnimeExtensionOperationState()
    }
}

fun AnimeExtensionUiModel.actionState(
    action: AnimeExtensionAction,
    platform: AnimeExtensionPlatformState = AnimeExtensionPlatformState(),
): AnimeExtensionActionState {
    val kind = action.toAnimeExtensionOperationKind()
    val operation = kind?.let(::operationState) ?: AnimeExtensionOperationState()
    val supportState = platformSupportState(platform)
    val platformReason = supportState.unsupportedReason(this, platform)
    val blockedReason = when (action) {
        AnimeExtensionAction.Install -> when {
            !supportState.supported -> platformReason
            installed -> "Already installed."
            isUntrusted -> "Trust this extension before installing it."
            else -> null
        }
        AnimeExtensionAction.Update -> when {
            !supportState.supported -> platformReason
            !installed && !hasUpdate -> "Install this extension before updating it."
            !hasUpdate -> "Already up to date."
            isUntrusted -> "Trust this extension before updating it."
            else -> null
        }
        AnimeExtensionAction.Uninstall -> if (!installed) "Only installed extensions can be uninstalled." else null
        AnimeExtensionAction.Trust -> if (!isUntrusted) "Extension is already trusted." else null
        AnimeExtensionAction.OpenSettings -> if (!installed) "Install this extension before opening settings." else null
        else -> null
    }
    return AnimeExtensionActionState(
        action = action,
        title = action.actionTitle(this),
        enabled = blockedReason == null && !operation.busy,
        loading = operation.busy,
        active = action == AnimeExtensionAction.Update && hasUpdate,
        status = operation.status,
        blockedReason = blockedReason ?: operation.message,
    )
}

fun AnimeExtensionUiModel.defaultPrimaryAction(
    platform: AnimeExtensionPlatformState = AnimeExtensionPlatformState(),
): AnimeExtensionAction {
    return when {
        installBusy -> AnimeExtensionAction.Cancel
        installStep == AnimeExtensionInstallStep.Error -> AnimeExtensionAction.Retry
        isUntrusted -> AnimeExtensionAction.Trust
        installed -> AnimeExtensionAction.Details
        hasUpdate -> AnimeExtensionAction.Update
        !platformSupportState(platform).supported -> AnimeExtensionAction.Details
        else -> AnimeExtensionAction.Install
    }
}

fun AnimeExtensionSourceUiModel.actionState(
    extension: AnimeExtensionUiModel,
): AnimeExtensionSourceActionState {
    val action = if (enabled) AnimeExtensionAction.DisableSource else AnimeExtensionAction.EnableSource
    val blockedReason = when {
        !extension.installed -> "Install this extension before changing source state."
        !toggleEnabled -> statusMessage ?: "Source state cannot be changed."
        else -> statusMessage
    }
    return AnimeExtensionSourceActionState(
        action = action,
        title = if (enabled) "Disable source" else "Enable source",
        enabled = blockedReason == null && !toggleLoading,
        loading = toggleLoading,
        status = toggleStatus,
        message = blockedReason,
    )
}

fun AnimeExtensionRepoUiModel.repoStatus(): AnimeExtensionRepoStatusModel {
    return when {
        !enabled -> AnimeExtensionRepoStatusModel(
            label = AnimeExtensionRepoSyncState.Disabled.title,
            detail = "Disabled repositories stay in the list but are excluded from available anime extensions.",
            severity = AnimeExtensionRepoStatusSeverity.Disabled,
        )
        syncState == AnimeExtensionRepoSyncState.Refreshing -> AnimeExtensionRepoStatusModel(
            label = AnimeExtensionRepoSyncState.Refreshing.title,
            severity = AnimeExtensionRepoStatusSeverity.Busy,
        )
        errorMessage != null || syncState == AnimeExtensionRepoSyncState.Error -> AnimeExtensionRepoStatusModel(
            label = AnimeExtensionRepoSyncState.Error.title,
            detail = errorMessage,
            severity = AnimeExtensionRepoStatusSeverity.Error,
        )
        trusted.not() -> AnimeExtensionRepoStatusModel(
            label = "Untrusted",
            detail = "Repository signing metadata should be reviewed before installing extensions.",
            severity = AnimeExtensionRepoStatusSeverity.Warning,
        )
        syncState == AnimeExtensionRepoSyncState.Loaded -> AnimeExtensionRepoStatusModel(
            label = "Loaded",
            severity = AnimeExtensionRepoStatusSeverity.Ready,
        )
        else -> AnimeExtensionRepoStatusModel(
            label = syncState.title,
            severity = AnimeExtensionRepoStatusSeverity.Ready,
        )
    }
}

fun AnimeExtensionRepoUiModel.rowActionState(action: AnimeExtensionRepoAction): AnimeExtensionRepoRowActionState {
    return AnimeExtensionRepoRowActionState(
        action = action,
        label = action.repoActionLabel(),
        icon = action.repoActionIcon(),
        enabled = when (action) {
            AnimeExtensionRepoAction.Select -> true
            AnimeExtensionRepoAction.OpenWebsite -> canOpenWebsite
            AnimeExtensionRepoAction.CopyIndexUrl -> canCopyIndexUrl
            AnimeExtensionRepoAction.Refresh -> canRefresh
            AnimeExtensionRepoAction.Edit -> canEdit
            AnimeExtensionRepoAction.Enable -> canEnable
            AnimeExtensionRepoAction.Disable -> canDisable
            AnimeExtensionRepoAction.Trust -> canTrust
            AnimeExtensionRepoAction.Remove -> canRemove
        },
        loading = action == AnimeExtensionRepoAction.Refresh &&
            syncState == AnimeExtensionRepoSyncState.Refreshing,
        active = when (action) {
            AnimeExtensionRepoAction.Enable -> !enabled
            AnimeExtensionRepoAction.Disable -> enabled.not()
            AnimeExtensionRepoAction.Trust -> trusted.not()
            else -> false
        },
    )
}

fun AnimeExtensionRepoUiModel.rowActionStates(
    actions: List<AnimeExtensionRepoAction> = defaultActionOrder(),
): List<AnimeExtensionRepoRowActionState> {
    return actions.map(::rowActionState)
}

fun AnimeExtensionActions.dispatchExtensionAction(
    action: AnimeExtensionAction,
    extension: AnimeExtensionUiModel,
) {
    when (action) {
        AnimeExtensionAction.Install -> onInstall(extension)
        AnimeExtensionAction.Update -> onUpdate(extension)
        AnimeExtensionAction.Uninstall -> onUninstall(extension)
        AnimeExtensionAction.Trust -> onTrust(extension)
        AnimeExtensionAction.Cancel -> onCancel(extension)
        AnimeExtensionAction.Retry -> onRetryExtension(extension)
        else -> onExtensionAction(action, extension)
    }
}

fun List<AnimeExtensionUiModel>.filteredAndSorted(state: AnimeExtensionListState): List<AnimeExtensionUiModel> {
    val selectedLanguage = state.selectedLanguage.animeExtensionLanguageCode()
    return asSequence()
        .filter { it.matchesAnimeExtensionQuery(state.query) }
        .filter {
            state.selectedLanguage == AnimeExtensionAllLanguages ||
                it.language?.animeExtensionLanguageCode() == selectedLanguage
        }
        .filter {
            when (state.filter) {
                AnimeExtensionFilter.All,
                AnimeExtensionFilter.Installed,
                AnimeExtensionFilter.Available,
                AnimeExtensionFilter.Updates,
                AnimeExtensionFilter.Untrusted,
                -> true
                AnimeExtensionFilter.Obsolete -> it.obsolete
                AnimeExtensionFilter.Nsfw -> it.nsfw
                AnimeExtensionFilter.Torrent -> it.torrent
            }
        }
        .let { entries ->
            when (state.sort) {
                AnimeExtensionSort.Name -> entries.sortedBy { it.name.lowercase() }
                AnimeExtensionSort.Language -> entries.sortedWith(
                    compareBy<AnimeExtensionUiModel> { it.language?.animeExtensionLanguageCode().orEmpty() }
                        .thenBy { it.name.lowercase() },
                )
                AnimeExtensionSort.Repository -> entries.sortedWith(
                    compareBy<AnimeExtensionUiModel> { it.repositoryName.orEmpty().lowercase() }
                        .thenBy { it.name.lowercase() },
                )
                AnimeExtensionSort.SourceCount -> entries.sortedWith(
                    compareByDescending<AnimeExtensionUiModel> { it.sourceCount }
                        .thenBy { it.name.lowercase() },
                )
                AnimeExtensionSort.Version -> entries.sortedWith(
                    compareByDescending<AnimeExtensionUiModel> { it.versionCode }
                        .thenBy { it.name.lowercase() },
                )
            }
        }
        .toList()
}

fun AnimeExtensionUiModel.matchesAnimeExtensionQuery(query: String): Boolean {
    val normalized = query.trim()
    return normalized.isBlank() ||
        name.contains(normalized, ignoreCase = true) ||
        packageName.contains(normalized, ignoreCase = true) ||
        versionName.contains(normalized, ignoreCase = true) ||
        repositoryName.orEmpty().contains(normalized, ignoreCase = true) ||
        repositoryBaseUrl.orEmpty().contains(normalized, ignoreCase = true) ||
        sources.any { source ->
            source.name.contains(normalized, ignoreCase = true) ||
                source.language.contains(normalized, ignoreCase = true) ||
                source.baseUrl.contains(normalized, ignoreCase = true)
        }
}

fun validateAnimeExtensionRepoForm(
    state: AnimeExtensionRepoFormState,
    knownBaseUrls: Collection<String> = emptyList(),
): AnimeExtensionRepoValidationResult {
    val messages = state.validationMessages.toMutableList()
    val normalized = state.normalizedUrl
    if (normalized.isBlank()) {
        messages += AnimeExtensionRepoValidationMessage("Repository URL is required.")
    }
    if (normalized.any(Char::isWhitespace)) {
        messages += AnimeExtensionRepoValidationMessage("Repository URL cannot contain spaces.")
    }
    if (normalized.isNotBlank() && !normalized.startsWith("http://") && !normalized.startsWith("https://")) {
        messages += AnimeExtensionRepoValidationMessage("Repository URL must start with http:// or https://.")
    }
    if (normalized.startsWith("http://")) {
        messages += AnimeExtensionRepoValidationMessage(
            text = "HTTPS is recommended for Android-compatible extension repositories.",
            severity = AnimeExtensionRepoValidationSeverity.Warning,
        )
    }
    if (normalized.endsWith("/index.min.json")) {
        messages += AnimeExtensionRepoValidationMessage("Use the repository base URL; the index path is added automatically.")
    }
    val website = state.website.trim()
    if (
        website.isNotBlank() &&
        (!website.startsWith("http://") && !website.startsWith("https://") || website.any(Char::isWhitespace))
    ) {
        messages += AnimeExtensionRepoValidationMessage("Website must be a valid http:// or https:// URL.")
    }
    if (state.name.length > 80) {
        messages += AnimeExtensionRepoValidationMessage(
            text = "Repository names over 80 characters may be truncated in compact layouts.",
            severity = AnimeExtensionRepoValidationSeverity.Warning,
        )
    }
    val known = knownBaseUrls
        .map { it.trim().trimEnd('/') }
        .filter { it.isNotBlank() }
        .toSet()
    if (normalized in known && normalized != state.normalizedOriginalBaseUrl) {
        messages += AnimeExtensionRepoValidationMessage("Repository already exists.")
    }
    if (messages.isEmpty()) {
        messages += AnimeExtensionRepoValidationMessage(
            text = "Metadata can be filled from the repository when the live service saves it.",
            severity = AnimeExtensionRepoValidationSeverity.Info,
        )
    }
    return AnimeExtensionRepoValidationResult(
        isValid = messages.none { it.severity == AnimeExtensionRepoValidationSeverity.Error },
        messages = messages,
    )
}

fun AnimeExtensionRepoUiModel.defaultActionOrder(): List<AnimeExtensionRepoAction> {
    return buildList {
        add(AnimeExtensionRepoAction.OpenWebsite)
        add(AnimeExtensionRepoAction.CopyIndexUrl)
        add(AnimeExtensionRepoAction.Refresh)
        if (!trusted) add(AnimeExtensionRepoAction.Trust)
        add(if (enabled) AnimeExtensionRepoAction.Disable else AnimeExtensionRepoAction.Enable)
        add(AnimeExtensionRepoAction.Edit)
        add(AnimeExtensionRepoAction.Remove)
    }
}

fun AnimeExtensionRepoAction.repoActionLabel(): String {
    return when (this) {
        AnimeExtensionRepoAction.Select -> "Select"
        AnimeExtensionRepoAction.OpenWebsite -> "Open"
        AnimeExtensionRepoAction.CopyIndexUrl -> "Copy index URL"
        AnimeExtensionRepoAction.Refresh -> "Refresh"
        AnimeExtensionRepoAction.Edit -> "Edit"
        AnimeExtensionRepoAction.Enable -> "Enable"
        AnimeExtensionRepoAction.Disable -> "Disable"
        AnimeExtensionRepoAction.Trust -> "Trust"
        AnimeExtensionRepoAction.Remove -> "Remove"
    }
}

fun AnimeExtensionRepoAction.repoActionIcon(): AnimeExtensionIcon {
    return when (this) {
        AnimeExtensionRepoAction.Select -> AnimeExtensionIcon.Check
        AnimeExtensionRepoAction.OpenWebsite -> AnimeExtensionIcon.Web
        AnimeExtensionRepoAction.CopyIndexUrl -> AnimeExtensionIcon.Copy
        AnimeExtensionRepoAction.Refresh -> AnimeExtensionIcon.Refresh
        AnimeExtensionRepoAction.Edit -> AnimeExtensionIcon.Edit
        AnimeExtensionRepoAction.Enable -> AnimeExtensionIcon.Visibility
        AnimeExtensionRepoAction.Disable -> AnimeExtensionIcon.VisibilityOff
        AnimeExtensionRepoAction.Trust -> AnimeExtensionIcon.Trust
        AnimeExtensionRepoAction.Remove -> AnimeExtensionIcon.Delete
    }
}

fun AnimeExtensionRepoUiModel.repoMarker(): String {
    return shortName
        ?.trim()
        ?.firstOrNull()
        ?.uppercaseChar()
        ?.toString()
        ?: name.trim().firstOrNull()?.uppercaseChar()?.toString()
        ?: "A"
}

fun String.toAnimeExtensionIndexUrl(): String {
    val trimmed = trim().trimEnd('/')
    if (trimmed.isBlank()) return ""
    return if (trimmed.endsWith(".json", ignoreCase = true) || trimmed.endsWith(".js", ignoreCase = true)) {
        trimmed
    } else {
        "$trimmed/index.min.json"
    }
}

fun String.animeExtensionRepoHost(): String {
    return ifBlank { "repo" }
        .substringAfter("://", this)
        .substringBefore('/')
        .ifBlank { "repo" }
}

fun String.animeExtensionLanguageCode(): String {
    return trim().lowercase().ifBlank { "other" }
}

fun String.animeExtensionLanguageDisplayName(): String {
    val code = animeExtensionLanguageCode()
    return when (code) {
        "all" -> AnimeExtensionAllLanguages
        "en" -> "English"
        "ja", "jp" -> "Japanese"
        "ko", "kr" -> "Korean"
        "zh", "cn" -> "Chinese"
        "es" -> "Spanish"
        "fr" -> "French"
        "de" -> "German"
        "it" -> "Italian"
        "pt", "pt-br" -> "Portuguese"
        "ru" -> "Russian"
        "ar" -> "Arabic"
        "tr" -> "Turkish"
        "id" -> "Indonesian"
        "th" -> "Thai"
        "vi" -> "Vietnamese"
        else -> code.uppercase()
    }
}

fun ChimahonAnimeExtensionRepoSummary.toAnimeExtensionRepoUiModel(
    enabled: Boolean = this.enabled,
    selected: Boolean = false,
    syncState: AnimeExtensionRepoSyncState = state.toAnimeExtensionRepoSyncState(),
): AnimeExtensionRepoUiModel {
    return AnimeExtensionRepoUiModel(
        baseUrl = baseUrl,
        name = name,
        shortName = shortName,
        website = website,
        signingKeyFingerprint = signingKeyFingerprint,
        enabled = enabled,
        trusted = trusted,
        selected = selected,
        syncState = syncState,
        errorMessage = unavailableReason,
        installedCount = installedCount,
        availableCount = availableCount,
        updateCount = updateCount,
        sourceCount = sourceCount,
        languages = languages,
    )
}

fun ChimahonExtensionRepoEntry.toAnimeExtensionRepoUiModel(
    enabled: Boolean = true,
    selected: Boolean = false,
    syncState: AnimeExtensionRepoSyncState = AnimeExtensionRepoSyncState.Idle,
): AnimeExtensionRepoUiModel {
    return AnimeExtensionRepoUiModel(
        baseUrl = baseUrl,
        name = name,
        shortName = shortName,
        website = website,
        signingKeyFingerprint = signingKeyFingerprint,
        enabled = enabled,
        selected = selected,
        syncState = syncState,
    )
}

fun ChimahonAnimeExtensionRepoDialog?.toAnimeExtensionRepoSheetState(
    repositories: List<AnimeExtensionRepoUiModel> = emptyList(),
): AnimeExtensionRepoSheetState {
    return when (this) {
        null -> AnimeExtensionRepoSheetState.None
        ChimahonAnimeExtensionRepoDialog.Create -> AnimeExtensionRepoSheetState.Form(
            AnimeExtensionRepoFormState(mode = AnimeExtensionRepoDialogMode.Add),
        )
        is ChimahonAnimeExtensionRepoDialog.Confirm -> AnimeExtensionRepoSheetState.Confirm(url = url)
        is ChimahonAnimeExtensionRepoDialog.Delete -> {
            val repository = repositories.firstOrNull { it.normalizedBaseUrl == baseUrl.trim().trimEnd('/') }
                ?: AnimeExtensionRepoUiModel(
                    baseUrl = baseUrl,
                    name = baseUrl.animeExtensionRepoHost(),
                )
            AnimeExtensionRepoSheetState.Remove(repository = repository)
        }
        is ChimahonAnimeExtensionRepoDialog.Edit -> AnimeExtensionRepoSheetState.Form(
            state = repo.toAnimeExtensionRepoFormState(),
        )
        is ChimahonAnimeExtensionRepoDialog.Conflict -> AnimeExtensionRepoSheetState.Conflict(
            oldRepository = oldRepo.toAnimeExtensionRepoUiModel(),
            newRepository = newRepo.toAnimeExtensionRepoUiModel(),
        )
    }
}

fun ChimahonAnimeExtensionRepoSummary.toAnimeExtensionRepoFormState(): AnimeExtensionRepoFormState {
    return AnimeExtensionRepoFormState(
        mode = AnimeExtensionRepoDialogMode.Edit,
        url = normalizedBaseUrl,
        originalBaseUrl = normalizedBaseUrl,
        name = name,
        shortName = shortName.orEmpty(),
        website = website,
        signingKeyFingerprint = signingKeyFingerprint,
    )
}

fun ChimahonRepoExtensionEntry.toAnimeExtensionUiModel(
    repositoryName: String? = null,
    installStep: AnimeExtensionInstallStep = AnimeExtensionInstallStep.Idle,
): AnimeExtensionUiModel {
    return AnimeExtensionUiModel(
        id = id,
        name = name,
        packageName = id,
        versionName = version,
        language = language,
        repositoryBaseUrl = repoBaseUrl,
        repositoryName = repositoryName,
        kind = packageType.toAnimeExtensionKind(),
        installStep = installStep,
        sources = List(sourceCount.coerceAtLeast(0)) { index ->
            AnimeExtensionSourceUiModel(
                id = index.toLong(),
                name = "Source ${index + 1}",
                language = language,
            )
        },
        nsfw = isNsfw,
        installed = false,
        available = true,
    )
}

fun ChimahonInstalledExtensionEntry.toAnimeExtensionUiModel(
    repositoryBaseUrl: String? = null,
    repositoryName: String? = null,
    hasUpdate: Boolean = false,
    obsolete: Boolean = false,
): AnimeExtensionUiModel {
    return AnimeExtensionUiModel(
        id = id,
        name = name,
        packageName = id,
        versionName = version,
        repositoryBaseUrl = repositoryBaseUrl,
        repositoryName = repositoryName,
        kind = packageType.toAnimeExtensionKind(),
        sources = List(sourceCount.coerceAtLeast(0)) { index ->
            AnimeExtensionSourceUiModel(
                id = index.toLong(),
                name = "Source ${index + 1}",
                language = "",
            )
        },
        installed = true,
        available = false,
        hasUpdate = hasUpdate,
        obsolete = obsolete,
    )
}

fun compareAnimeExtensionVersions(first: String, second: String): Int {
    if (first == second) return 0
    val firstTokens = AnimeExtensionVersionToken.findAll(first).map(MatchResult::value).toList()
    val secondTokens = AnimeExtensionVersionToken.findAll(second).map(MatchResult::value).toList()
    val tokenCount = maxOf(firstTokens.size, secondTokens.size)
    repeat(tokenCount) { index ->
        val left = firstTokens.getOrNull(index) ?: "0"
        val right = secondTokens.getOrNull(index) ?: "0"
        val leftNumber = left.toLongOrNull()
        val rightNumber = right.toLongOrNull()
        val comparison = when {
            leftNumber != null && rightNumber != null -> leftNumber.compareTo(rightNumber)
            leftNumber != null -> 1
            rightNumber != null -> -1
            else -> left.compareTo(right, ignoreCase = true)
        }
        if (comparison != 0) return comparison
    }
    return first.compareTo(second, ignoreCase = true)
}

private fun ChimahonExtensionPackageType.toAnimeExtensionKind(): AnimeExtensionKind {
    return when (this) {
        ChimahonExtensionPackageType.AndroidApk -> AnimeExtensionKind.Apk
        ChimahonExtensionPackageType.JavaScript -> AnimeExtensionKind.JavaScript
    }
}

private fun AnimeExtensionKind.toChimahonExtensionPackageType(): ChimahonExtensionPackageType? {
    return when (this) {
        AnimeExtensionKind.Apk -> ChimahonExtensionPackageType.AndroidApk
        AnimeExtensionKind.JavaScript -> ChimahonExtensionPackageType.JavaScript
        AnimeExtensionKind.Unknown -> null
    }
}

private fun ChimahonAnimeExtensionPlatformSupportState.toAnimeExtensionPlatformSupportState(): AnimeExtensionPlatformSupportState {
    return when (this) {
        ChimahonAnimeExtensionPlatformSupportState.Supported -> AnimeExtensionPlatformSupportState.Supported
        ChimahonAnimeExtensionPlatformSupportState.ApkUnsupported -> AnimeExtensionPlatformSupportState.ApkUnsupported
        ChimahonAnimeExtensionPlatformSupportState.JavaScriptUnsupported ->
            AnimeExtensionPlatformSupportState.JavaScriptUnsupported
        ChimahonAnimeExtensionPlatformSupportState.UnsupportedPackage ->
            AnimeExtensionPlatformSupportState.UnsupportedPackage
    }
}

private fun AnimeExtensionPlatformSupportState.unsupportedReason(
    extension: AnimeExtensionUiModel,
    platform: AnimeExtensionPlatformState,
): String? {
    if (supported) return null
    val platformLabel = platform.platformName.takeIf(String::isNotBlank)?.let { " on $it" }.orEmpty()
    return when (this) {
        AnimeExtensionPlatformSupportState.Supported,
        AnimeExtensionPlatformSupportState.UnknownPackage -> null
        AnimeExtensionPlatformSupportState.ApkUnsupported ->
            "Android APK anime extensions are unavailable$platformLabel."
        AnimeExtensionPlatformSupportState.JavaScriptUnsupported ->
            "JavaScript anime extensions are unavailable$platformLabel."
        AnimeExtensionPlatformSupportState.UnsupportedPackage ->
            "${extension.kind.title} anime extensions are unavailable$platformLabel."
    }
}

private fun AnimeExtensionAction.toAnimeExtensionOperationKind(): AnimeExtensionOperationKind? {
    return when (this) {
        AnimeExtensionAction.Install -> AnimeExtensionOperationKind.Install
        AnimeExtensionAction.Update,
        AnimeExtensionAction.UpdateAll -> AnimeExtensionOperationKind.Update
        AnimeExtensionAction.Uninstall -> AnimeExtensionOperationKind.Uninstall
        AnimeExtensionAction.Trust -> AnimeExtensionOperationKind.Trust
        AnimeExtensionAction.EnableSource,
        AnimeExtensionAction.DisableSource,
        AnimeExtensionAction.EnableAllSources,
        AnimeExtensionAction.DisableAllSources -> AnimeExtensionOperationKind.ToggleSource
        else -> null
    }
}

private fun AnimeExtensionAction.actionTitle(extension: AnimeExtensionUiModel): String {
    return when (this) {
        AnimeExtensionAction.Open -> "Open"
        AnimeExtensionAction.Details -> "Details"
        AnimeExtensionAction.Install -> "Install"
        AnimeExtensionAction.Update -> "Update"
        AnimeExtensionAction.UpdateAll -> "Update all"
        AnimeExtensionAction.Cancel -> "Cancel"
        AnimeExtensionAction.Retry -> "Retry"
        AnimeExtensionAction.Uninstall -> "Uninstall"
        AnimeExtensionAction.Trust -> "Trust"
        AnimeExtensionAction.OpenWebsite -> "Open website"
        AnimeExtensionAction.OpenSettings -> "Settings"
        AnimeExtensionAction.EnableSource -> "Enable source"
        AnimeExtensionAction.DisableSource -> "Disable source"
        AnimeExtensionAction.EnableAllSources -> "Enable all sources"
        AnimeExtensionAction.DisableAllSources -> "Disable all sources"
        AnimeExtensionAction.ClearCookies -> "Clear cookies"
        AnimeExtensionAction.AppInfo -> if (extension.hasUpdate) "Update" else "App info"
        AnimeExtensionAction.CopyDebugInfo -> "Copy debug info"
        AnimeExtensionAction.ToggleIncognito -> "Toggle incognito"
    }
}

private fun AnimeExtensionInstallStep.toAnimeExtensionOperationState(
    kind: AnimeExtensionOperationKind,
): AnimeExtensionOperationState {
    return when (this) {
        AnimeExtensionInstallStep.Idle -> AnimeExtensionOperationState()
        AnimeExtensionInstallStep.Pending -> AnimeExtensionOperationState(AnimeExtensionOperationStatus.Queued)
        AnimeExtensionInstallStep.Downloading -> AnimeExtensionOperationState(AnimeExtensionOperationStatus.Downloading)
        AnimeExtensionInstallStep.Installing -> AnimeExtensionOperationState(
            if (kind == AnimeExtensionOperationKind.Update) {
                AnimeExtensionOperationStatus.Updating
            } else {
                AnimeExtensionOperationStatus.Installing
            },
        )
        AnimeExtensionInstallStep.Installed -> AnimeExtensionOperationState(AnimeExtensionOperationStatus.Complete)
        AnimeExtensionInstallStep.Error -> AnimeExtensionOperationState(AnimeExtensionOperationStatus.Error)
    }
}

private fun AnimeExtensionListState.expectedAvailableCount(filteredCount: Int): Int {
    val canUseRepoCount =
        query.isBlank() &&
            selectedLanguage == AnimeExtensionAllLanguages &&
            filter != AnimeExtensionFilter.Installed &&
            filter != AnimeExtensionFilter.Updates
    return if (canUseRepoCount) {
        maxOf(filteredCount, repositories.availableCount)
    } else {
        filteredCount
    }
}

private val AnimeExtensionVersionToken = Regex("""\d+|[A-Za-z]+""")

private fun ChimahonAnimeExtensionRepoState.toAnimeExtensionRepoSyncState(): AnimeExtensionRepoSyncState {
    return when (this) {
        ChimahonAnimeExtensionRepoState.Idle -> AnimeExtensionRepoSyncState.Idle
        ChimahonAnimeExtensionRepoState.Refreshing -> AnimeExtensionRepoSyncState.Refreshing
        ChimahonAnimeExtensionRepoState.Loaded -> AnimeExtensionRepoSyncState.Loaded
        ChimahonAnimeExtensionRepoState.Disabled -> AnimeExtensionRepoSyncState.Disabled
        ChimahonAnimeExtensionRepoState.Unavailable -> AnimeExtensionRepoSyncState.Error
    }
}
