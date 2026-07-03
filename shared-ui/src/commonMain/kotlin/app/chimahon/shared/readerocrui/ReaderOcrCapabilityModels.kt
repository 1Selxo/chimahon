package app.chimahon.shared.readerocrui

enum class ChimahonReaderOcrCapabilityProvider(
    val key: String,
    val label: String,
) {
    Glens("glens", "GLens"),
    Vision("vision", "Vision"),
    Platform("platform", "Platform OCR"),
    Manual("manual", "Manual lookup"),
}

enum class ChimahonReaderOcrCapabilityAvailability {
    Available,
    Checking,
    Disabled,
    Unsupported,
    MissingPermission,
    Offline,
    RateLimited,
    Error,
}

data class ChimahonReaderOcrCapabilityUiModel(
    val provider: ChimahonReaderOcrCapabilityProvider,
    val availability: ChimahonReaderOcrCapabilityAvailability,
    val title: String = provider.label,
    val detail: String = availability.defaultDetail(provider),
    val actionLabel: String? = availability.defaultActionLabel(),
    val canRetry: Boolean = availability.canRetry,
    val canOpenSettings: Boolean = availability.canOpenSettings,
) {
    val available: Boolean
        get() = availability == ChimahonReaderOcrCapabilityAvailability.Available

    val busy: Boolean
        get() = availability == ChimahonReaderOcrCapabilityAvailability.Checking

    val blocking: Boolean
        get() = availability != ChimahonReaderOcrCapabilityAvailability.Available &&
            availability != ChimahonReaderOcrCapabilityAvailability.Checking
}

data class ChimahonReaderOcrCapabilityStatusUiState(
    val providers: List<ChimahonReaderOcrCapabilityUiModel> = defaultReaderOcrCapabilityProviders(),
    val selectedProvider: ChimahonReaderOcrCapabilityProvider = providers.firstOrNull()?.provider
        ?: ChimahonReaderOcrCapabilityProvider.Glens,
    val message: String? = null,
) {
    val selected: ChimahonReaderOcrCapabilityUiModel?
        get() = providers.firstOrNull { it.provider == selectedProvider }

    val primary: ChimahonReaderOcrCapabilityUiModel?
        get() = selected ?: providers.firstOrNull()

    val anyAvailable: Boolean
        get() = providers.any { it.available }

    val summaryText: String
        get() = message ?: primary?.let { "${it.title}: ${it.detail}" } ?: "OCR capability unknown"
}

fun defaultReaderOcrCapabilityProviders(): List<ChimahonReaderOcrCapabilityUiModel> {
    return listOf(
        ChimahonReaderOcrCapabilityUiModel(
            provider = ChimahonReaderOcrCapabilityProvider.Glens,
            availability = ChimahonReaderOcrCapabilityAvailability.Checking,
        ),
        ChimahonReaderOcrCapabilityUiModel(
            provider = ChimahonReaderOcrCapabilityProvider.Vision,
            availability = ChimahonReaderOcrCapabilityAvailability.Checking,
        ),
    )
}

fun ChimahonReaderOcrEngine.toCapabilityProvider(): ChimahonReaderOcrCapabilityProvider {
    return when (this) {
        ChimahonReaderOcrEngine.Glens -> ChimahonReaderOcrCapabilityProvider.Glens
        ChimahonReaderOcrEngine.Platform -> ChimahonReaderOcrCapabilityProvider.Platform
        ChimahonReaderOcrEngine.Manual -> ChimahonReaderOcrCapabilityProvider.Manual
    }
}

fun ChimahonReaderOcrCapabilityProvider.toReaderOcrEngine(): ChimahonReaderOcrEngine {
    return when (this) {
        ChimahonReaderOcrCapabilityProvider.Glens -> ChimahonReaderOcrEngine.Glens
        ChimahonReaderOcrCapabilityProvider.Vision,
        ChimahonReaderOcrCapabilityProvider.Platform,
        -> ChimahonReaderOcrEngine.Platform
        ChimahonReaderOcrCapabilityProvider.Manual -> ChimahonReaderOcrEngine.Manual
    }
}

private val ChimahonReaderOcrCapabilityAvailability.canRetry: Boolean
    get() = when (this) {
        ChimahonReaderOcrCapabilityAvailability.Offline,
        ChimahonReaderOcrCapabilityAvailability.RateLimited,
        ChimahonReaderOcrCapabilityAvailability.Error,
        -> true
        ChimahonReaderOcrCapabilityAvailability.Available,
        ChimahonReaderOcrCapabilityAvailability.Checking,
        ChimahonReaderOcrCapabilityAvailability.Disabled,
        ChimahonReaderOcrCapabilityAvailability.Unsupported,
        ChimahonReaderOcrCapabilityAvailability.MissingPermission,
        -> false
    }

private val ChimahonReaderOcrCapabilityAvailability.canOpenSettings: Boolean
    get() = this == ChimahonReaderOcrCapabilityAvailability.Disabled ||
        this == ChimahonReaderOcrCapabilityAvailability.MissingPermission

private fun ChimahonReaderOcrCapabilityAvailability.defaultDetail(
    provider: ChimahonReaderOcrCapabilityProvider,
): String {
    return when (this) {
        ChimahonReaderOcrCapabilityAvailability.Available -> "${provider.label} is ready"
        ChimahonReaderOcrCapabilityAvailability.Checking -> "Checking ${provider.label}"
        ChimahonReaderOcrCapabilityAvailability.Disabled -> "${provider.label} is disabled"
        ChimahonReaderOcrCapabilityAvailability.Unsupported -> "${provider.label} is unavailable on this device"
        ChimahonReaderOcrCapabilityAvailability.MissingPermission -> "${provider.label} needs permission"
        ChimahonReaderOcrCapabilityAvailability.Offline -> "${provider.label} needs a network connection"
        ChimahonReaderOcrCapabilityAvailability.RateLimited -> "${provider.label} is temporarily rate limited"
        ChimahonReaderOcrCapabilityAvailability.Error -> "${provider.label} failed"
    }
}

private fun ChimahonReaderOcrCapabilityAvailability.defaultActionLabel(): String? {
    return when (this) {
        ChimahonReaderOcrCapabilityAvailability.Disabled -> "Enable"
        ChimahonReaderOcrCapabilityAvailability.MissingPermission -> "Allow"
        ChimahonReaderOcrCapabilityAvailability.Offline,
        ChimahonReaderOcrCapabilityAvailability.RateLimited,
        ChimahonReaderOcrCapabilityAvailability.Error,
        -> "Retry"
        ChimahonReaderOcrCapabilityAvailability.Available,
        ChimahonReaderOcrCapabilityAvailability.Checking,
        ChimahonReaderOcrCapabilityAvailability.Unsupported,
        -> null
    }
}
