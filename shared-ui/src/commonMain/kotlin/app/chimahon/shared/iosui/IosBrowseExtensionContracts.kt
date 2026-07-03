package app.chimahon.shared.iosui

import app.chimahon.shared.ChimahonExtensionPackageType
import app.chimahon.shared.ChimahonInstalledExtensionEntry
import app.chimahon.shared.ChimahonRepoExtensionEntry
import app.chimahon.shared.animeextensionui.AnimeExtensionKind
import app.chimahon.shared.animeextensionui.AnimeExtensionListState
import app.chimahon.shared.animeextensionui.AnimeExtensionPlatformState
import app.chimahon.shared.animeextensionui.AnimeExtensionRepoFormState
import app.chimahon.shared.animeextensionui.AnimeExtensionRepoValidationMessage
import app.chimahon.shared.animeextensionui.AnimeExtensionRepoValidationSeverity
import app.chimahon.shared.animeextensionui.AnimeExtensionUiModel
import app.chimahon.shared.animeextensionui.platformSupportState
import app.chimahon.shared.animeextensionui.unsupportedDetail
import app.chimahon.shared.animeextensionui.validateAnimeExtensionRepoForm
import app.chimahon.shared.browse.BrowseExtensionListState
import app.chimahon.shared.browse.compareBrowseVersions
import app.chimahon.shared.repositoryui.ChimahonRepositoryFormState
import app.chimahon.shared.repositoryui.ChimahonRepositoryValidationMessage
import app.chimahon.shared.repositoryui.ChimahonRepositoryValidationSeverity
import app.chimahon.shared.repositoryui.validateChimahonRepositoryForm

private const val IosDefaultCompactWidth = 744f
private const val IosDefaultSidebarMinWidth = 276f
private const val IosDefaultSidebarMaxWidth = 360f
private const val IosDefaultSidebarFraction = 0.34f
private const val IosDefaultContentMinWidth = 420f
private const val IosDefaultTopBarHeight = 44f
private const val IosDefaultKeyboardAnimationMillis = 250

data class IosUiEdgeInsets(
    val top: Float = 0f,
    val bottom: Float = 0f,
    val leading: Float = 0f,
    val trailing: Float = 0f,
) {
    val horizontal: Float
        get() = leading + trailing

    val vertical: Float
        get() = top + bottom

    fun normalized(): IosUiEdgeInsets {
        return copy(
            top = top.coerceAtLeast(0f),
            bottom = bottom.coerceAtLeast(0f),
            leading = leading.coerceAtLeast(0f),
            trailing = trailing.coerceAtLeast(0f),
        )
    }

    fun maxWith(other: IosUiEdgeInsets): IosUiEdgeInsets {
        return IosUiEdgeInsets(
            top = maxOf(top, other.top),
            bottom = maxOf(bottom, other.bottom),
            leading = maxOf(leading, other.leading),
            trailing = maxOf(trailing, other.trailing),
        )
    }

    companion object {
        val Zero: IosUiEdgeInsets = IosUiEdgeInsets()
    }
}

enum class IosKeyboardDockState {
    Hidden,
    Docked,
    Floating,
    Split,
}

enum class IosKeyboardAnimationCurve {
    EaseInOut,
    EaseIn,
    EaseOut,
    Linear,
}

data class IosKeyboardObstruction(
    val dockState: IosKeyboardDockState = IosKeyboardDockState.Hidden,
    val keyboardTop: Float? = null,
    val keyboardHeight: Float = 0f,
    val bottomOverlap: Float = 0f,
    val safeAreaAdjustedBottomOverlap: Float = 0f,
    val animationMillis: Int = IosDefaultKeyboardAnimationMillis,
    val animationCurve: IosKeyboardAnimationCurve = IosKeyboardAnimationCurve.EaseInOut,
) {
    val visible: Boolean
        get() = dockState != IosKeyboardDockState.Hidden && keyboardHeight > 0f

    val obstructsBottomEdge: Boolean
        get() = visible &&
            dockState == IosKeyboardDockState.Docked &&
            safeAreaAdjustedBottomOverlap > 0f

    val scrollInset: Float
        get() = if (obstructsBottomEdge) safeAreaAdjustedBottomOverlap else 0f

    companion object {
        val Hidden: IosKeyboardObstruction = IosKeyboardObstruction()
    }
}

data class IosTopStatusBarSafeArea(
    val statusBarHeight: Float = 0f,
    val safeAreaTop: Float = 0f,
    val topBarHeight: Float = IosDefaultTopBarHeight,
    val overlaysContent: Boolean = true,
) {
    val statusProtectedTop: Float
        get() = maxOf(statusBarHeight, safeAreaTop).coerceAtLeast(0f)

    val contentTopPadding: Float
        get() = if (overlaysContent) statusProtectedTop else 0f

    val chromeHeight: Float
        get() = topBarHeight.coerceAtLeast(0f) + contentTopPadding
}

data class IosViewportObstructionState(
    val width: Float,
    val height: Float,
    val safeArea: IosUiEdgeInsets = IosUiEdgeInsets.Zero,
    val topStatusBar: IosTopStatusBarSafeArea = IosTopStatusBarSafeArea(),
    val keyboard: IosKeyboardObstruction = IosKeyboardObstruction.Hidden,
) {
    val normalizedSafeArea: IosUiEdgeInsets
        get() = safeArea.normalized()

    val contentInsets: IosUiEdgeInsets
        get() = normalizedSafeArea.maxWith(
            IosUiEdgeInsets(
                top = topStatusBar.contentTopPadding,
                bottom = keyboard.scrollInset,
            ),
        )

    val unobstructedHeight: Float
        get() = (height - contentInsets.top - contentInsets.bottom).coerceAtLeast(0f)

    val unobstructedWidth: Float
        get() = (width - contentInsets.horizontal).coerceAtLeast(0f)
}

data class IosFocusedContentBounds(
    val top: Float,
    val bottom: Float,
) {
    val height: Float
        get() = (bottom - top).coerceAtLeast(0f)

    fun normalized(): IosFocusedContentBounds {
        return if (bottom >= top) {
            this
        } else {
            copy(top = bottom, bottom = top)
        }
    }
}

data class IosKeyboardAvoidanceState(
    val focusedBounds: IosFocusedContentBounds?,
    val scrollDelta: Float = 0f,
    val bottomMargin: Float = 12f,
    val keyboard: IosKeyboardObstruction = IosKeyboardObstruction.Hidden,
) {
    val shouldScroll: Boolean
        get() = scrollDelta > 0f
}

fun IosViewportObstructionState.keyboardAvoidanceFor(
    focusedBounds: IosFocusedContentBounds?,
    bottomMargin: Float = 12f,
): IosKeyboardAvoidanceState {
    if (focusedBounds == null || !keyboard.obstructsBottomEdge) {
        return IosKeyboardAvoidanceState(
            focusedBounds = focusedBounds,
            bottomMargin = bottomMargin,
            keyboard = keyboard,
        )
    }

    val keyboardTop = keyboard.keyboardTop ?: (height - keyboard.bottomOverlap)
    val safeBottomForFocus = (keyboardTop - bottomMargin.coerceAtLeast(0f)).coerceAtLeast(contentInsets.top)
    val normalizedFocus = focusedBounds.normalized()
    return IosKeyboardAvoidanceState(
        focusedBounds = normalizedFocus,
        scrollDelta = (normalizedFocus.bottom - safeBottomForFocus).coerceAtLeast(0f),
        bottomMargin = bottomMargin,
        keyboard = keyboard,
    )
}

fun iosKeyboardObstructionFromFrame(
    viewportHeight: Float,
    keyboardTop: Float?,
    keyboardHeight: Float,
    safeAreaBottom: Float = 0f,
    dockState: IosKeyboardDockState = IosKeyboardDockState.Docked,
    animationMillis: Int = IosDefaultKeyboardAnimationMillis,
    animationCurve: IosKeyboardAnimationCurve = IosKeyboardAnimationCurve.EaseInOut,
): IosKeyboardObstruction {
    val normalizedHeight = keyboardHeight.coerceAtLeast(0f)
    if (normalizedHeight == 0f || dockState == IosKeyboardDockState.Hidden) {
        return IosKeyboardObstruction.Hidden
    }

    val top = keyboardTop ?: (viewportHeight - normalizedHeight)
    val bottomOverlap = (viewportHeight - top).coerceIn(0f, viewportHeight.coerceAtLeast(0f))
    val adjustedOverlap = (bottomOverlap - safeAreaBottom.coerceAtLeast(0f)).coerceAtLeast(0f)
    return IosKeyboardObstruction(
        dockState = dockState,
        keyboardTop = top,
        keyboardHeight = normalizedHeight,
        bottomOverlap = bottomOverlap,
        safeAreaAdjustedBottomOverlap = adjustedOverlap,
        animationMillis = animationMillis.coerceAtLeast(0),
        animationCurve = animationCurve,
    )
}

enum class IosBrowseSplitMode {
    Compact,
    SidebarContent,
}

data class IosBrowseSplitSizing(
    val viewportWidth: Float,
    val safeArea: IosUiEdgeInsets = IosUiEdgeInsets.Zero,
    val compactBreakpoint: Float = IosDefaultCompactWidth,
    val minSidebarWidth: Float = IosDefaultSidebarMinWidth,
    val maxSidebarWidth: Float = IosDefaultSidebarMaxWidth,
    val preferredSidebarFraction: Float = IosDefaultSidebarFraction,
    val minContentWidth: Float = IosDefaultContentMinWidth,
    val dividerWidth: Float = 1f,
) {
    val availableWidth: Float
        get() = (viewportWidth - safeArea.normalized().horizontal).coerceAtLeast(0f)

    val supportsSplit: Boolean
        get() = availableWidth >= compactBreakpoint &&
            availableWidth >= minSidebarWidth.coerceAtLeast(0f) + minContentWidth.coerceAtLeast(0f)

    fun resolve(): IosBrowseSplitLayout {
        if (!supportsSplit) {
            return IosBrowseSplitLayout(
                mode = IosBrowseSplitMode.Compact,
                safeArea = safeArea.normalized(),
                sidebarWidth = availableWidth,
                contentWidth = availableWidth,
                dividerWidth = 0f,
            )
        }

        val boundedFraction = preferredSidebarFraction.coerceIn(0.2f, 0.5f)
        val preferredSidebar = availableWidth * boundedFraction
        val maxSidebarForContent = availableWidth - minContentWidth.coerceAtLeast(0f)
        val sidebar = preferredSidebar
            .coerceAtLeast(minSidebarWidth.coerceAtLeast(0f))
            .coerceAtMost(maxSidebarWidth.coerceAtLeast(minSidebarWidth))
            .coerceAtMost(maxSidebarForContent)
            .coerceAtLeast(0f)
        val divider = dividerWidth.coerceAtLeast(0f)
        return IosBrowseSplitLayout(
            mode = IosBrowseSplitMode.SidebarContent,
            safeArea = safeArea.normalized(),
            sidebarWidth = sidebar,
            contentWidth = (availableWidth - sidebar - divider).coerceAtLeast(0f),
            dividerWidth = divider,
        )
    }
}

data class IosBrowseSplitLayout(
    val mode: IosBrowseSplitMode,
    val safeArea: IosUiEdgeInsets,
    val sidebarWidth: Float,
    val contentWidth: Float,
    val dividerWidth: Float,
) {
    val split: Boolean
        get() = mode == IosBrowseSplitMode.SidebarContent

    val totalWidth: Float
        get() = if (split) {
            sidebarWidth + dividerWidth + contentWidth
        } else {
            maxOf(sidebarWidth, contentWidth)
        }
}

enum class IosExtensionInstallSupport {
    Supported,
    AlreadyInstalled,
    UpdateAvailable,
    PlatformBlocked,
}

data class IosDisabledApkInstallMessage(
    val title: String,
    val detail: String,
    val badgeLabel: String = "APK",
    val actionLabel: String? = null,
) {
    companion object {
        fun forPlatform(platformName: String = "iOS"): IosDisabledApkInstallMessage {
            return IosDisabledApkInstallMessage(
                title = "APK install unavailable",
                detail = "Android APK extensions cannot be installed on $platformName. Use JavaScript extensions from compatible repositories instead.",
            )
        }
    }
}

data class IosExtensionInstallUiState(
    val support: IosExtensionInstallSupport,
    val primaryLabel: String,
    val enabled: Boolean,
    val active: Boolean = false,
    val message: IosDisabledApkInstallMessage? = null,
) {
    val blocked: Boolean
        get() = support == IosExtensionInstallSupport.PlatformBlocked
}

fun ChimahonRepoExtensionEntry.toIosBrowseInstallUiState(
    installedVersion: String? = null,
    apkExtensionsSupported: Boolean = false,
    platformName: String = "iOS",
): IosExtensionInstallUiState {
    if (packageType == ChimahonExtensionPackageType.AndroidApk && !apkExtensionsSupported) {
        return IosExtensionInstallUiState(
            support = IosExtensionInstallSupport.PlatformBlocked,
            primaryLabel = "Unavailable",
            enabled = false,
            message = IosDisabledApkInstallMessage.forPlatform(platformName),
        )
    }

    val updateAvailable = installedVersion != null && compareBrowseVersions(version, installedVersion) > 0
    return when {
        updateAvailable -> IosExtensionInstallUiState(
            support = IosExtensionInstallSupport.UpdateAvailable,
            primaryLabel = "Update",
            enabled = true,
            active = true,
        )
        installedVersion != null -> IosExtensionInstallUiState(
            support = IosExtensionInstallSupport.AlreadyInstalled,
            primaryLabel = "Installed",
            enabled = false,
            active = true,
        )
        else -> IosExtensionInstallUiState(
            support = IosExtensionInstallSupport.Supported,
            primaryLabel = "Install",
            enabled = true,
        )
    }
}

fun ChimahonInstalledExtensionEntry.toIosBrowseUpdateUiState(
    availableUpdate: ChimahonRepoExtensionEntry?,
    apkExtensionsSupported: Boolean = false,
    platformName: String = "iOS",
): IosExtensionInstallUiState {
    if (availableUpdate == null) {
        return IosExtensionInstallUiState(
            support = IosExtensionInstallSupport.AlreadyInstalled,
            primaryLabel = "Installed",
            enabled = false,
            active = true,
        )
    }
    return availableUpdate.toIosBrowseInstallUiState(
        installedVersion = version,
        apkExtensionsSupported = apkExtensionsSupported,
        platformName = platformName,
    )
}

fun BrowseExtensionListState.iosDisabledApkInstallMessage(
    platformName: String = "iOS",
): IosDisabledApkInstallMessage? {
    if (apkExtensionsSupported) return null
    val hasVisibleApkPackage = available.any { it.packageType == ChimahonExtensionPackageType.AndroidApk } ||
        installed.any { it.packageType == ChimahonExtensionPackageType.AndroidApk }
    return if (hasVisibleApkPackage) IosDisabledApkInstallMessage.forPlatform(platformName) else null
}

fun AnimeExtensionUiModel.toIosExtensionInstallUiState(
    platform: AnimeExtensionPlatformState = AnimeExtensionPlatformState.Ios,
): IosExtensionInstallUiState {
    val supportState = platformSupportState(platform)
    if (!supportState.supported) {
        val message = if (kind == AnimeExtensionKind.Apk) {
            IosDisabledApkInstallMessage.forPlatform(platform.platformName.ifBlank { "iOS" })
        } else {
            IosDisabledApkInstallMessage(
                title = "Extension unavailable",
                detail = supportState.unsupportedDetail(this, platform),
                badgeLabel = supportState.badgeLabel,
            )
        }
        return IosExtensionInstallUiState(
            support = IosExtensionInstallSupport.PlatformBlocked,
            primaryLabel = "Unavailable",
            enabled = false,
            message = message,
        )
    }

    return when {
        hasUpdate -> IosExtensionInstallUiState(
            support = IosExtensionInstallSupport.UpdateAvailable,
            primaryLabel = "Update",
            enabled = true,
            active = true,
        )
        installed -> IosExtensionInstallUiState(
            support = IosExtensionInstallSupport.AlreadyInstalled,
            primaryLabel = "Installed",
            enabled = false,
            active = true,
        )
        else -> IosExtensionInstallUiState(
            support = IosExtensionInstallSupport.Supported,
            primaryLabel = "Install",
            enabled = true,
        )
    }
}

fun AnimeExtensionListState.iosDisabledApkInstallMessage(
    platform: AnimeExtensionPlatformState = AnimeExtensionPlatformState.Ios,
): IosDisabledApkInstallMessage? {
    if (platform.apkExtensionsSupported) return null
    val hasApkPackage = (updates + installed + available + untrusted).any { it.kind == AnimeExtensionKind.Apk }
    return if (hasApkPackage) {
        IosDisabledApkInstallMessage.forPlatform(platform.platformName.ifBlank { "iOS" })
    } else {
        null
    }
}

enum class IosRepoTextFieldId {
    Url,
    Name,
    ShortName,
    Website,
    SigningKeyFingerprint,
}

enum class IosRepoTextFieldSeverity {
    Info,
    Warning,
    Error,
}

enum class IosRepoKeyboardHint {
    Text,
    Url,
    Ascii,
}

data class IosRepoTextFieldIssue(
    val fieldId: IosRepoTextFieldId,
    val message: String,
    val severity: IosRepoTextFieldSeverity = IosRepoTextFieldSeverity.Info,
)

data class IosRepoTextFieldState(
    val id: IosRepoTextFieldId,
    val label: String,
    val value: String,
    val placeholder: String = "",
    val enabled: Boolean = true,
    val required: Boolean = false,
    val keyboardHint: IosRepoKeyboardHint = IosRepoKeyboardHint.Text,
    val issues: List<IosRepoTextFieldIssue> = emptyList(),
) {
    val normalizedValue: String
        get() = value.trim()

    val hasError: Boolean
        get() = issues.any { it.severity == IosRepoTextFieldSeverity.Error }

    val supportText: String?
        get() = issues.firstOrNull { it.severity == IosRepoTextFieldSeverity.Error }?.message
            ?: issues.firstOrNull { it.severity == IosRepoTextFieldSeverity.Warning }?.message
            ?: issues.firstOrNull()?.message
}

data class IosRepoTextFieldSet(
    val title: String,
    val actionTitle: String,
    val saving: Boolean,
    val fields: List<IosRepoTextFieldState>,
) {
    val isValid: Boolean
        get() = fields.none { it.hasError }

    val primaryIssue: IosRepoTextFieldIssue?
        get() = fields
            .flatMap { it.issues }
            .firstOrNull { it.severity == IosRepoTextFieldSeverity.Error }
            ?: fields.flatMap { it.issues }.firstOrNull()

    fun field(id: IosRepoTextFieldId): IosRepoTextFieldState? {
        return fields.firstOrNull { it.id == id }
    }
}

fun ChimahonRepositoryFormState.toIosRepoTextFieldSet(
    knownBaseUrls: Collection<String> = emptyList(),
): IosRepoTextFieldSet {
    val issues = validateChimahonRepositoryForm(this, knownBaseUrls)
        .messages
        .map { it.toIosRepoTextFieldIssue() }
    return IosRepoTextFieldSet(
        title = mode.title,
        actionTitle = mode.actionTitle,
        saving = saving,
        fields = iosRepoTextFields(
            url = url,
            name = name,
            shortName = shortName,
            website = website,
            signingKeyFingerprint = signingKeyFingerprint,
            enabled = !saving,
            issues = issues,
        ),
    )
}

fun AnimeExtensionRepoFormState.toIosRepoTextFieldSet(
    knownBaseUrls: Collection<String> = emptyList(),
): IosRepoTextFieldSet {
    val issues = validateAnimeExtensionRepoForm(this, knownBaseUrls)
        .messages
        .map { it.toIosRepoTextFieldIssue() }
    return IosRepoTextFieldSet(
        title = mode.title,
        actionTitle = mode.actionTitle,
        saving = saving,
        fields = iosRepoTextFields(
            url = url,
            name = name,
            shortName = shortName,
            website = website,
            signingKeyFingerprint = signingKeyFingerprint,
            enabled = !saving,
            issues = issues,
        ),
    )
}

fun iosBrowseRepoUrlTextFieldSet(
    url: String,
    saving: Boolean = false,
    message: String? = null,
    knownBaseUrls: Collection<String> = emptyList(),
): IosRepoTextFieldSet {
    val state = ChimahonRepositoryFormState(
        url = url,
        saving = saving,
        message = message,
    )
    val fieldSet = state.toIosRepoTextFieldSet(knownBaseUrls)
    return fieldSet.copy(
        title = "Add extension repo",
        actionTitle = "Save",
        fields = fieldSet.fields.filter { it.id == IosRepoTextFieldId.Url },
    )
}

private fun iosRepoTextFields(
    url: String,
    name: String,
    shortName: String,
    website: String,
    signingKeyFingerprint: String,
    enabled: Boolean,
    issues: List<IosRepoTextFieldIssue>,
): List<IosRepoTextFieldState> {
    return listOf(
        IosRepoTextFieldState(
            id = IosRepoTextFieldId.Url,
            label = "Repository URL",
            value = url,
            placeholder = "https://example.org/repo",
            enabled = enabled,
            required = true,
            keyboardHint = IosRepoKeyboardHint.Url,
            issues = issues.forField(IosRepoTextFieldId.Url),
        ),
        IosRepoTextFieldState(
            id = IosRepoTextFieldId.Name,
            label = "Name",
            value = name,
            placeholder = "Read from repository metadata",
            enabled = enabled,
            issues = issues.forField(IosRepoTextFieldId.Name),
        ),
        IosRepoTextFieldState(
            id = IosRepoTextFieldId.ShortName,
            label = "Short name",
            value = shortName,
            enabled = enabled,
            issues = issues.forField(IosRepoTextFieldId.ShortName),
        ),
        IosRepoTextFieldState(
            id = IosRepoTextFieldId.Website,
            label = "Website",
            value = website,
            enabled = enabled,
            keyboardHint = IosRepoKeyboardHint.Url,
            issues = issues.forField(IosRepoTextFieldId.Website),
        ),
        IosRepoTextFieldState(
            id = IosRepoTextFieldId.SigningKeyFingerprint,
            label = "Signing key fingerprint",
            value = signingKeyFingerprint,
            placeholder = "Optional",
            enabled = enabled,
            keyboardHint = IosRepoKeyboardHint.Ascii,
            issues = issues.forField(IosRepoTextFieldId.SigningKeyFingerprint),
        ),
    )
}

private fun List<IosRepoTextFieldIssue>.forField(
    fieldId: IosRepoTextFieldId,
): List<IosRepoTextFieldIssue> {
    return filter { it.fieldId == fieldId }
}

private fun ChimahonRepositoryValidationMessage.toIosRepoTextFieldIssue(): IosRepoTextFieldIssue {
    return IosRepoTextFieldIssue(
        fieldId = text.toIosRepoTextFieldId(),
        message = text,
        severity = when (severity) {
            ChimahonRepositoryValidationSeverity.Info -> IosRepoTextFieldSeverity.Info
            ChimahonRepositoryValidationSeverity.Warning -> IosRepoTextFieldSeverity.Warning
            ChimahonRepositoryValidationSeverity.Error -> IosRepoTextFieldSeverity.Error
        },
    )
}

private fun AnimeExtensionRepoValidationMessage.toIosRepoTextFieldIssue(): IosRepoTextFieldIssue {
    return IosRepoTextFieldIssue(
        fieldId = text.toIosRepoTextFieldId(),
        message = text,
        severity = when (severity) {
            AnimeExtensionRepoValidationSeverity.Info -> IosRepoTextFieldSeverity.Info
            AnimeExtensionRepoValidationSeverity.Warning -> IosRepoTextFieldSeverity.Warning
            AnimeExtensionRepoValidationSeverity.Error -> IosRepoTextFieldSeverity.Error
        },
    )
}

private fun String.toIosRepoTextFieldId(): IosRepoTextFieldId {
    val normalized = lowercase()
    return when {
        "short name" in normalized -> IosRepoTextFieldId.ShortName
        "website" in normalized -> IosRepoTextFieldId.Website
        "fingerprint" in normalized || "signing key" in normalized -> IosRepoTextFieldId.SigningKeyFingerprint
        "name" in normalized && "repository" !in normalized -> IosRepoTextFieldId.Name
        else -> IosRepoTextFieldId.Url
    }
}
