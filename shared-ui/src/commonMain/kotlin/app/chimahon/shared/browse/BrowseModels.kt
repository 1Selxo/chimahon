package app.chimahon.shared.browse

import app.chimahon.shared.ChimahonBrowseSourceDisplayMode
import app.chimahon.shared.ChimahonExtensionPackageType
import app.chimahon.shared.ChimahonExtensionRegistryPlatformSupport
import app.chimahon.shared.ChimahonExtensionRegistryPlatformSupportMessage
import app.chimahon.shared.ChimahonExtensionRepoCatalog
import app.chimahon.shared.ChimahonExtensionRepoEntry
import app.chimahon.shared.ChimahonInstalledExtensionEntry
import app.chimahon.shared.ChimahonMangaEntry
import app.chimahon.shared.ChimahonRemoteMangaEntry
import app.chimahon.shared.ChimahonRepoExtensionEntry
import app.chimahon.shared.ChimahonSourceBrowseMode
import app.chimahon.shared.ChimahonSourceEntry
import app.chimahon.shared.toChimahonExtensionRegistryPlatformSupportMessage

const val BrowseExtensionPageSize: Int = 40
const val BrowseExtensionPrefetchItemCount: Int = 80
const val BrowseSourcePrefetchItemCount: Int = 36

enum class BrowseSourceQuickFilter(val title: String) {
    All("All"),
    Latest("Latest"),
    PopularOnly("Popular only"),
    Pinned("Pinned"),
}

enum class BrowseSourceSort(val title: String) {
    Name("Name"),
    Language("Language"),
    Capability("Capability"),
    Pinned("Pinned"),
}

data class BrowseSourceListState(
    val sources: List<ChimahonSourceEntry>,
    val query: String = "",
    val selectedLanguage: String = BrowseAllLanguages,
    val enabledLanguages: List<String> = emptyList(),
    val pinnedSourceIds: List<Long> = emptyList(),
    val quickFilter: BrowseSourceQuickFilter = BrowseSourceQuickFilter.All,
    val sort: BrowseSourceSort = BrowseSourceSort.Name,
    val displayMode: ChimahonBrowseSourceDisplayMode = ChimahonBrowseSourceDisplayMode.List,
    val groupByLanguage: Boolean = true,
    val showLanguageBadges: Boolean = true,
    val showLocalSource: Boolean = true,
)

data class BrowseSourceActions(
    val onOpen: (ChimahonSourceEntry, ChimahonSourceBrowseMode) -> Unit = { _, _ -> },
    val onTogglePinned: (ChimahonSourceEntry) -> Unit = {},
    val onOpenSettings: (ChimahonSourceEntry) -> Unit = {},
    val onInstallExtension: (() -> Unit)? = null,
    val onOpenLocalImport: (() -> Unit)? = null,
)

data class BrowseSourceGroup(
    val key: String,
    val title: String?,
    val sources: List<ChimahonSourceEntry>,
)

enum class BrowseExtensionFilter(val title: String) {
    All("All"),
    Installed("Installed"),
    Available("Available"),
    Updates("Updates"),
    Nsfw("NSFW"),
}

sealed interface BrowseExtensionCatalogState {
    data object Idle : BrowseExtensionCatalogState
    data object Loading : BrowseExtensionCatalogState
    data class Disabled(val repo: ChimahonExtensionRepoEntry) : BrowseExtensionCatalogState
    data class Ready(val catalog: ChimahonExtensionRepoCatalog) : BrowseExtensionCatalogState
    data class Failed(val message: String) : BrowseExtensionCatalogState
}

data class BrowseExtensionRepoListState(
    val repos: List<ChimahonExtensionRepoEntry>,
    val selectedRepoBaseUrl: String? = null,
    val disabledRepoBaseUrls: List<String> = emptyList(),
    val untrustedRepoBaseUrls: List<String> = emptyList(),
    val removingRepoBaseUrls: List<String> = emptyList(),
    val pendingRemovalBaseUrl: String? = null,
    val loadedRepoBaseUrl: String? = null,
    val message: String? = null,
)

data class BrowseExtensionRepoActions(
    val onSelect: (ChimahonExtensionRepoEntry) -> Unit = {},
    val onRefresh: (ChimahonExtensionRepoEntry) -> Unit = {},
    val onEdit: (ChimahonExtensionRepoEntry) -> Unit = {},
    val onRemove: (ChimahonExtensionRepoEntry) -> Unit = {},
    val onCopyIndexUrl: (ChimahonExtensionRepoEntry) -> Unit = {},
    val onSetEnabled: (ChimahonExtensionRepoEntry, Boolean) -> Unit = { _, _ -> },
    val onSetTrusted: (ChimahonExtensionRepoEntry, Boolean) -> Unit = { _, _ -> },
    val onOpenWebsite: (ChimahonExtensionRepoEntry) -> Unit = {},
)

enum class BrowseExtensionRepoTrustState(val title: String, val badgeLabel: String) {
    Trusted("Trusted", "TRUSTED"),
    Untrusted("Untrusted", "UNTRUSTED"),
    Unsigned("Unsigned", "UNSIGNED"),
}

enum class BrowseExtensionRepoRemovalState(val title: String) {
    Idle("Idle"),
    Confirming("Confirming"),
    Removing("Removing"),
}

data class BrowseExtensionRepoPolicyState(
    val enabled: Boolean = true,
    val trustState: BrowseExtensionRepoTrustState = BrowseExtensionRepoTrustState.Unsigned,
    val removalState: BrowseExtensionRepoRemovalState = BrowseExtensionRepoRemovalState.Idle,
) {
    val trusted: Boolean
        get() = trustState == BrowseExtensionRepoTrustState.Trusted

    val canRefresh: Boolean
        get() = enabled && removalState != BrowseExtensionRepoRemovalState.Removing

    val canRemove: Boolean
        get() = removalState != BrowseExtensionRepoRemovalState.Removing

    val canTrust: Boolean
        get() = trustState == BrowseExtensionRepoTrustState.Untrusted
}

data class BrowseExtensionListState(
    val installed: List<ChimahonInstalledExtensionEntry> = emptyList(),
    val available: List<ChimahonRepoExtensionEntry> = emptyList(),
    val apkExtensionsSupported: Boolean = true,
    val javaScriptExtensionsSupported: Boolean = true,
    val platformName: String = "",
    val query: String = "",
    val filter: BrowseExtensionFilter = BrowseExtensionFilter.All,
    val visibleAvailableCount: Int = BrowseExtensionPageSize,
    val autoLoadMore: Boolean = true,
    val loadingMore: Boolean = false,
    val catalogState: BrowseExtensionCatalogState = BrowseExtensionCatalogState.Idle,
    val installingKey: String? = null,
    val uninstallingKey: String? = null,
)

data class BrowseExtensionActions(
    val onInstall: (ChimahonRepoExtensionEntry) -> Unit = {},
    val onUpdate: (ChimahonRepoExtensionEntry) -> Unit = {},
    val onUninstall: (ChimahonInstalledExtensionEntry) -> Unit = {},
    val onLoadMore: () -> Unit = {},
    val onRetryCatalog: () -> Unit = {},
    val onEnableRepo: (ChimahonExtensionRepoEntry) -> Unit = {},
)

data class BrowseAutoLoadState(
    val enabled: Boolean,
    val hasMore: Boolean,
    val loading: Boolean = false,
    val visibleCount: Int = 0,
    val totalCount: Int = 0,
) {
    val continuationStatus: BrowseAutoLoadContinuationStatus
        get() = resolveBrowseAutoLoadContinuationStatus()

    val continuationTitle: String
        get() = continuationStatus.title

    val continuationSubtitle: String
        get() = when (continuationStatus) {
            BrowseAutoLoadContinuationStatus.Disabled -> "${visibleCount.coerceAtLeast(0)} shown"
            BrowseAutoLoadContinuationStatus.Complete -> "${totalCount.coerceAtLeast(0)} total"
            BrowseAutoLoadContinuationStatus.LoadingInitial,
            BrowseAutoLoadContinuationStatus.LoadingMore,
            BrowseAutoLoadContinuationStatus.ReadyForContinuation,
            BrowseAutoLoadContinuationStatus.WaitingForScroll,
            -> "${visibleCount.coerceAtMost(totalCount).coerceAtLeast(0)} of ${totalCount.coerceAtLeast(0)} shown"
        }
}

enum class BrowseAutoLoadContinuationStatus(val title: String) {
    Disabled("More extensions available"),
    LoadingInitial("Loading extensions"),
    LoadingMore("Loading more extensions"),
    ReadyForContinuation("More extensions load automatically"),
    WaitingForScroll("More extensions load near the end"),
    Complete("All extensions shown"),
}

enum class BrowseMigrationStep(val title: String) {
    Source("Select source"),
    Title("Select title"),
    Target("Select target"),
    Search("Search target"),
    Result("Choose result"),
}

data class BrowseMigrationState(
    val step: BrowseMigrationStep,
    val source: ChimahonSourceEntry? = null,
    val manga: ChimahonMangaEntry? = null,
    val targetSource: ChimahonSourceEntry? = null,
    val query: String = "",
    val resultCount: Int = 0,
    val migratingUrl: String? = null,
    val message: String? = null,
)

const val BrowseAllLanguages: String = "All"

fun BrowseSourceListState.filteredSources(): List<ChimahonSourceEntry> {
    val normalizedQuery = query.trim()
    val enabledLanguageSet = enabledLanguages.map(String::browseLanguageCode).toSet()
    val selectedLanguageCode = selectedLanguage.browseLanguageCode()
    val pinnedIds = pinnedSourceIds.toSet()
    return sources
        .asSequence()
        .filter { showLocalSource || !it.isBrowseLocalSource() }
        .filter {
            enabledLanguageSet.isEmpty() || it.language.browseLanguageCode() in enabledLanguageSet
        }
        .filter {
            selectedLanguage == BrowseAllLanguages || it.language.browseLanguageCode() == selectedLanguageCode
        }
        .filter {
            normalizedQuery.isBlank() ||
                it.name.contains(normalizedQuery, ignoreCase = true) ||
                it.language.contains(normalizedQuery, ignoreCase = true) ||
                it.id.toString().contains(normalizedQuery)
        }
        .filter {
            when (quickFilter) {
                BrowseSourceQuickFilter.All -> true
                BrowseSourceQuickFilter.Latest -> it.supportsLatest
                BrowseSourceQuickFilter.PopularOnly -> !it.supportsLatest
                BrowseSourceQuickFilter.Pinned -> it.id in pinnedIds
            }
        }
        .let { entries ->
            when (sort) {
                BrowseSourceSort.Name -> entries.sortedWith(compareBy<ChimahonSourceEntry> { it.name.lowercase() })
                BrowseSourceSort.Language -> entries.sortedWith(
                    compareBy<ChimahonSourceEntry> { it.language.browseLanguageCode() }
                        .thenBy { it.name.lowercase() },
                )
                BrowseSourceSort.Capability -> entries.sortedWith(
                    compareBy<ChimahonSourceEntry> { if (it.supportsLatest) 0 else 1 }
                        .thenBy { it.name.lowercase() },
                )
                BrowseSourceSort.Pinned -> entries.sortedWith(
                    compareByDescending<ChimahonSourceEntry> { it.id in pinnedIds }
                        .thenBy { it.name.lowercase() },
                )
            }
        }
        .toList()
}

fun BrowseSourceListState.sourceGroups(): List<BrowseSourceGroup> {
    val filtered = filteredSources()
    val pinnedIds = pinnedSourceIds.toSet()
    val pinned = filtered.filter { it.id in pinnedIds }
    val remaining = filtered.filterNot { it.id in pinnedIds }
    val groups = mutableListOf<BrowseSourceGroup>()
    if (pinned.isNotEmpty()) {
        groups += BrowseSourceGroup("pinned", "Pinned", pinned)
    }
    if (groupByLanguage) {
        remaining
            .groupBy { it.language.browseLanguageCode() }
            .toList()
            .sortedBy { it.first }
            .forEach { (language, sources) ->
                groups += BrowseSourceGroup("language:$language", language.uppercase(), sources)
            }
    } else if (remaining.isNotEmpty()) {
        groups += BrowseSourceGroup("all", null, remaining)
    }
    return groups
}

fun BrowseSourceListState.languageOptions(): List<String> {
    val installedLanguages = sources
        .map { it.language.browseLanguageCode() }
        .distinct()
        .sorted()
    val enabled = enabledLanguages.map(String::browseLanguageCode).toSet()
    val visibleLanguages = installedLanguages.filter { enabled.isEmpty() || it in enabled }
    return listOf(BrowseAllLanguages) + visibleLanguages
}

fun BrowseSourceListState.hasActiveFilters(): Boolean {
    return query.isNotBlank() ||
        selectedLanguage != BrowseAllLanguages ||
        enabledLanguages.isNotEmpty() ||
        quickFilter != BrowseSourceQuickFilter.All ||
        sort != BrowseSourceSort.Name
}

fun BrowseExtensionListState.filteredInstalled(): List<ChimahonInstalledExtensionEntry> {
    return installed.filter { extension ->
        (filter == BrowseExtensionFilter.All || filter == BrowseExtensionFilter.Installed) &&
            extension.matchesBrowseExtensionQuery(query)
    }
}

fun BrowseExtensionListState.filteredAvailable(): List<ChimahonRepoExtensionEntry> {
    val installedKeys = installed.map { it.browseInstallKey() }.toSet()
    return available.filter { extension ->
        extension.matchesBrowseExtensionQuery(query) &&
            when (filter) {
                BrowseExtensionFilter.All,
                BrowseExtensionFilter.Available,
                -> true
                BrowseExtensionFilter.Installed -> false
                BrowseExtensionFilter.Updates -> extension.browseInstallKey() in installedKeys
                BrowseExtensionFilter.Nsfw -> extension.isNsfw
            }
    }
}

fun BrowseExtensionListState.visibleAvailable(): List<ChimahonRepoExtensionEntry> {
    return filteredAvailable().take(visibleAvailableCount.coerceAtLeast(0))
}

fun BrowseExtensionListState.autoLoadState(): BrowseAutoLoadState {
    val filteredCount = filteredAvailable().size
    return BrowseAutoLoadState(
        enabled = autoLoadMore,
        hasMore = visibleAvailableCount < filteredCount,
        loading = loadingMore,
        visibleCount = visibleAvailableCount.coerceAtMost(filteredCount),
        totalCount = filteredCount,
    )
}

fun BrowseExtensionListState.extensionRegistryPlatformSupport(): ChimahonExtensionRegistryPlatformSupport {
    return ChimahonExtensionRegistryPlatformSupport(
        apkExtensionsSupported = apkExtensionsSupported,
        javaScriptExtensionsSupported = javaScriptExtensionsSupported,
        platformName = platformName,
    )
}

fun BrowseExtensionListState.availableUpdatesByKey(): Map<String, ChimahonRepoExtensionEntry> {
    val installedVersions = installed.associate { it.browseInstallKey() to it.version }
    val platformSupport = extensionRegistryPlatformSupport()
    return available
        .filter { available ->
            if (!available.isSupportedByBrowsePlatform(platformSupport)) return@filter false
            val installedVersion = installedVersions[available.browseInstallKey()] ?: return@filter false
            compareBrowseVersions(available.version, installedVersion) > 0
        }
        .groupBy { it.browseInstallKey() }
        .mapValues { (_, entries) -> entries.maxWithOrNull { first, second -> compareBrowseVersions(first.version, second.version) }!! }
}

fun BrowseExtensionRepoListState.policyFor(
    repo: ChimahonExtensionRepoEntry,
): BrowseExtensionRepoPolicyState {
    val disabled = repo.baseUrl in disabledRepoBaseUrls
    val untrusted = repo.baseUrl in untrustedRepoBaseUrls
    val removalState = when {
        repo.baseUrl in removingRepoBaseUrls -> BrowseExtensionRepoRemovalState.Removing
        repo.baseUrl == pendingRemovalBaseUrl -> BrowseExtensionRepoRemovalState.Confirming
        else -> BrowseExtensionRepoRemovalState.Idle
    }
    val trustState = when {
        untrusted -> BrowseExtensionRepoTrustState.Untrusted
        repo.signingKeyFingerprint.isNotBlank() -> BrowseExtensionRepoTrustState.Trusted
        else -> BrowseExtensionRepoTrustState.Unsigned
    }
    return BrowseExtensionRepoPolicyState(
        enabled = !disabled,
        trustState = trustState,
        removalState = removalState,
    )
}

fun ChimahonSourceEntry.isBrowseLocalSource(): Boolean {
    return id == 0L || name.equals("Local source", ignoreCase = true) || name.equals("Local", ignoreCase = true)
}

fun ChimahonSourceEntry.browseSourceInitials(): String {
    val words = name
        .replace('-', ' ')
        .replace('_', ' ')
        .split(' ')
        .mapNotNull { word -> word.firstOrNull { it.isLetterOrDigit() }?.uppercaseChar()?.toString() }
    return when {
        words.size >= 2 -> (words[0] + words[1]).take(2)
        words.size == 1 -> words[0]
        else -> language.browseLanguageCode().take(2).uppercase().ifBlank { "S" }
    }
}

fun ChimahonExtensionRepoEntry.browseRepoInitial(): String {
    return shortName
        ?.trim()
        ?.firstOrNull()
        ?.uppercaseChar()
        ?.toString()
        ?: name.trim().firstOrNull()?.uppercaseChar()?.toString()
        ?: "R"
}

fun ChimahonExtensionRepoEntry.browseRepoHost(): String {
    return website.ifBlank { baseUrl }
        .substringAfter("://", website.ifBlank { baseUrl })
        .substringBefore('/')
        .ifBlank { "repo" }
}

fun ChimahonExtensionRepoEntry.browseCatalogIndexUrl(): String {
    return if (baseUrl.endsWith(".js", ignoreCase = true) || baseUrl.endsWith(".json", ignoreCase = true)) {
        baseUrl
    } else {
        "${baseUrl.trimEnd('/')}/index.min.json"
    }
}

fun ChimahonRepoExtensionEntry.browseInstallKey(): String {
    return "${packageType.name}:$id"
}

fun ChimahonRepoExtensionEntry.isSupportedByBrowsePlatform(apkExtensionsSupported: Boolean): Boolean {
    return packageType != ChimahonExtensionPackageType.AndroidApk || apkExtensionsSupported
}

fun ChimahonRepoExtensionEntry.isSupportedByBrowsePlatform(
    platformSupport: ChimahonExtensionRegistryPlatformSupport,
): Boolean {
    return browsePlatformSupportMessage(platformSupport).supported
}

fun ChimahonInstalledExtensionEntry.isSupportedByBrowsePlatform(
    platformSupport: ChimahonExtensionRegistryPlatformSupport,
): Boolean {
    return packageType
        .toChimahonExtensionRegistryPlatformSupportMessage(platformSupport)
        .supported
}

fun ChimahonRepoExtensionEntry.browsePlatformSupportMessage(
    platformSupport: ChimahonExtensionRegistryPlatformSupport,
): ChimahonExtensionRegistryPlatformSupportMessage {
    return toChimahonExtensionRegistryPlatformSupportMessage(platformSupport)
}

fun ChimahonInstalledExtensionEntry.browseInstallKey(): String {
    return "${packageType.name}:$id"
}

fun ChimahonRepoExtensionEntry.matchesBrowseExtensionQuery(query: String): Boolean {
    val normalizedQuery = query.trim()
    return normalizedQuery.isBlank() ||
        name.contains(normalizedQuery, ignoreCase = true) ||
        id.contains(normalizedQuery, ignoreCase = true) ||
        language.contains(normalizedQuery, ignoreCase = true) ||
        version.contains(normalizedQuery, ignoreCase = true)
}

fun ChimahonInstalledExtensionEntry.matchesBrowseExtensionQuery(query: String): Boolean {
    val normalizedQuery = query.trim()
    return normalizedQuery.isBlank() ||
        name.contains(normalizedQuery, ignoreCase = true) ||
        id.contains(normalizedQuery, ignoreCase = true) ||
        version.contains(normalizedQuery, ignoreCase = true)
}

fun ChimahonExtensionPackageType.browseMarker(): String {
    return when (this) {
        ChimahonExtensionPackageType.JavaScript -> "JS"
        ChimahonExtensionPackageType.AndroidApk -> "APK"
    }
}

fun String.browseLanguageCode(): String {
    return trim().lowercase().ifBlank { "multi" }
}

fun ChimahonMangaEntry.browseMigrationSubtitle(sourceName: String): String {
    return listOfNotNull(
        sourceName,
        author?.takeIf(String::isNotBlank),
        status.takeIf(String::isNotBlank),
    ).joinToString(" - ")
}

fun ChimahonRemoteMangaEntry.browseCandidateSubtitle(sourceName: String): String {
    return listOfNotNull(
        sourceName,
        author?.takeIf(String::isNotBlank),
        status.takeIf(String::isNotBlank),
    ).joinToString(" - ")
}

fun compareBrowseVersions(first: String, second: String): Int {
    if (first == second) return 0
    val firstTokens = BrowseVersionToken.findAll(first).map(MatchResult::value).toList()
    val secondTokens = BrowseVersionToken.findAll(second).map(MatchResult::value).toList()
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

private fun BrowseAutoLoadState.resolveBrowseAutoLoadContinuationStatus(): BrowseAutoLoadContinuationStatus {
    return when {
        !enabled && hasMore -> BrowseAutoLoadContinuationStatus.Disabled
        loading && visibleCount <= 0 -> BrowseAutoLoadContinuationStatus.LoadingInitial
        loading -> BrowseAutoLoadContinuationStatus.LoadingMore
        !hasMore -> BrowseAutoLoadContinuationStatus.Complete
        visibleCount < minOf(totalCount, BrowseExtensionPrefetchItemCount) ->
            BrowseAutoLoadContinuationStatus.ReadyForContinuation
        else -> BrowseAutoLoadContinuationStatus.WaitingForScroll
    }
}

private val BrowseVersionToken = Regex("""\d+|[A-Za-z]+""")
