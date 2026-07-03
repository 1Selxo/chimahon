package app.chimahon.shared.novelopsui

import app.chimahon.shared.novelui.ChimahonNovelBackendError
import app.chimahon.shared.novelui.ChimahonNovelSyncOverview
import app.chimahon.shared.novelui.ChimahonNovelSyncUiState
import app.chimahon.shared.novelui.toNovelSyncUiState

data class ChimahonNovelSyncSettings(
    val enabled: Boolean = false,
    val mode: ChimahonNovelSyncBackendMode = ChimahonNovelSyncBackendMode.Off,
    val direction: ChimahonNovelSyncBackendDirection = ChimahonNovelSyncBackendDirection.Auto,
    val autoSyncOnOpen: Boolean = false,
    val autoSyncOnClose: Boolean = false,
    val autoSyncPeriodic: Boolean = false,
    val autoSyncIntervalMinutes: Int = 10,
    val statisticsSyncEnabled: Boolean = true,
    val statisticsSyncMode: ChimahonNovelStatisticsSyncMode = ChimahonNovelStatisticsSyncMode.Merge,
    val audioBookSyncEnabled: Boolean = false,
)

enum class ChimahonNovelSyncBackendMode {
    Off,
    Auto,
    Import,
    Export,
    TwoWay,
}

enum class ChimahonNovelSyncBackendDirection {
    Auto,
    Import,
    Export,
}

enum class ChimahonNovelStatisticsSyncMode {
    LocalWins,
    RemoteWins,
    Merge,
}

enum class ChimahonNovelDriveAuthBackendStatus {
    MissingConfiguration,
    NotConnected,
    Connecting,
    Connected,
    Failed,
}

data class ChimahonNovelDeviceCodePrompt(
    val verificationUrl: String,
    val userCode: String,
    val expiresInSeconds: Long,
    val pollIntervalSeconds: Long,
)

data class ChimahonNovelDeviceCodePollRequest(
    val prompt: ChimahonNovelDeviceCodePrompt,
    val pollIntervalSeconds: Long = prompt.pollIntervalSeconds,
)

data class ChimahonNovelDeviceCodePollResult(
    val status: ChimahonNovelDeviceCodePollStatus,
    val nextPollIntervalSeconds: Long = 0L,
    val message: String? = null,
    val snapshot: ChimahonNovelSyncSnapshot? = null,
) {
    val authorized: Boolean
        get() = status == ChimahonNovelDeviceCodePollStatus.Authorized
}

enum class ChimahonNovelDeviceCodePollStatus {
    Authorized,
    Pending,
    SlowDown,
    TransientNetworkFailure,
    Failed,
    Expired,
}

data class ChimahonNovelSyncSnapshot(
    val providerName: String = "TTSU sync",
    val settings: ChimahonNovelSyncSettings = ChimahonNovelSyncSettings(),
    val authStatus: ChimahonNovelDriveAuthBackendStatus = ChimahonNovelDriveAuthBackendStatus.NotConnected,
    val prompt: ChimahonNovelDeviceCodePrompt? = null,
    val syncing: Boolean = false,
    val progress: ChimahonNovelSyncProgress = ChimahonNovelSyncProgress(),
    val lastSyncEpochMillis: Long = 0L,
    val summary: ChimahonNovelSyncSummary = ChimahonNovelSyncSummary(),
    val errorMessage: String? = null,
) {
    val connected: Boolean
        get() = authStatus == ChimahonNovelDriveAuthBackendStatus.Connected

    val enabled: Boolean
        get() = settings.enabled && settings.mode != ChimahonNovelSyncBackendMode.Off
}

data class ChimahonNovelSyncProgress(
    val processedCount: Int = 0,
    val totalCount: Int = 0,
    val progressPercent: Int? = null,
    val currentNovelId: String? = null,
    val currentTitle: String? = null,
    val message: String? = null,
) {
    val normalizedPercent: Int?
        get() = progressPercent?.coerceIn(0, 100)
            ?: if (totalCount > 0) {
                ((processedCount.toDouble() / totalCount.toDouble()) * 100.0).toInt().coerceIn(0, 100)
            } else {
                null
            }
}

data class ChimahonNovelSyncSummary(
    val total: Int = 0,
    val imported: Int = 0,
    val exported: Int = 0,
    val synced: Int = 0,
    val skipped: Int = 0,
    val failed: Int = 0,
) {
    val hasWork: Boolean
        get() = total > 0 || imported > 0 || exported > 0 || synced > 0 || skipped > 0 || failed > 0

    val displayLabel: String?
        get() = buildList {
            if (total > 0) add("$total total")
            if (imported > 0) add("$imported imported")
            if (exported > 0) add("$exported exported")
            if (synced > 0) add("$synced synced")
            if (skipped > 0) add("$skipped skipped")
            if (failed > 0) add("$failed failed")
        }.joinToString(" - ").takeIf(String::isNotBlank)
}

data class ChimahonNovelSyncBookResult(
    val novelId: String,
    val title: String,
    val status: ChimahonNovelSyncBookStatus,
    val errorMessage: String? = null,
)

enum class ChimahonNovelSyncBookStatus {
    Imported,
    Exported,
    Synced,
    Skipped,
    Failed,
}

data class ChimahonNovelSyncRequest(
    val novelIds: List<String> = emptyList(),
    val direction: ChimahonNovelSyncBackendDirection = ChimahonNovelSyncBackendDirection.Auto,
    val force: Boolean = false,
    val trigger: ChimahonNovelSyncTrigger = ChimahonNovelSyncTrigger.Manual,
)

enum class ChimahonNovelSyncTrigger {
    Manual,
    LibraryOpen,
    ReaderOpen,
    ReaderClose,
    Periodic,
}

data class ChimahonNovelSyncCommandResult(
    val snapshot: ChimahonNovelSyncSnapshot = ChimahonNovelSyncSnapshot(),
    val results: List<ChimahonNovelSyncBookResult> = emptyList(),
    val errors: List<ChimahonNovelBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty() && results.none { it.status == ChimahonNovelSyncBookStatus.Failed }
}

interface ChimahonNovelSyncRepositoryBridge {
    suspend fun loadSync(): ChimahonNovelSyncSnapshot

    suspend fun saveSettings(settings: ChimahonNovelSyncSettings): ChimahonNovelSyncCommandResult

    suspend fun requestDeviceCode(
        clientId: String,
        clientSecret: String,
    ): ChimahonNovelSyncCommandResult

    suspend fun pollDeviceCode(request: ChimahonNovelDeviceCodePollRequest): ChimahonNovelDeviceCodePollResult

    suspend fun revokeDriveAccess(): ChimahonNovelSyncCommandResult

    suspend fun syncLibrary(request: ChimahonNovelSyncRequest = ChimahonNovelSyncRequest()): ChimahonNovelSyncCommandResult

    suspend fun syncNovel(novelId: String, request: ChimahonNovelSyncRequest = ChimahonNovelSyncRequest()): ChimahonNovelSyncCommandResult

    suspend fun cancelSync(): ChimahonNovelSyncCommandResult

    suspend fun clearCache(): ChimahonNovelSyncCommandResult
}

data class ChimahonNovelSyncRepositoryCallbacks(
    val loadSync: suspend () -> ChimahonNovelSyncSnapshot,
    val saveSettings: suspend (ChimahonNovelSyncSettings) -> ChimahonNovelSyncCommandResult,
    val requestDeviceCode: suspend (String, String) -> ChimahonNovelSyncCommandResult,
    val pollDeviceCode: suspend (ChimahonNovelDeviceCodePollRequest) -> ChimahonNovelDeviceCodePollResult,
    val revokeDriveAccess: suspend () -> ChimahonNovelSyncCommandResult,
    val syncLibrary: suspend (ChimahonNovelSyncRequest) -> ChimahonNovelSyncCommandResult,
    val syncNovel: suspend (String, ChimahonNovelSyncRequest) -> ChimahonNovelSyncCommandResult,
    val cancelSync: suspend () -> ChimahonNovelSyncCommandResult,
    val clearCache: suspend () -> ChimahonNovelSyncCommandResult,
)

class ChimahonCallbackNovelSyncRepositoryBridge(
    private val callbacks: ChimahonNovelSyncRepositoryCallbacks,
) : ChimahonNovelSyncRepositoryBridge {
    override suspend fun loadSync(): ChimahonNovelSyncSnapshot {
        return callbacks.loadSync()
    }

    override suspend fun saveSettings(settings: ChimahonNovelSyncSettings): ChimahonNovelSyncCommandResult {
        return callbacks.saveSettings(settings)
    }

    override suspend fun requestDeviceCode(
        clientId: String,
        clientSecret: String,
    ): ChimahonNovelSyncCommandResult {
        return callbacks.requestDeviceCode(clientId, clientSecret)
    }

    override suspend fun pollDeviceCode(
        request: ChimahonNovelDeviceCodePollRequest,
    ): ChimahonNovelDeviceCodePollResult {
        return callbacks.pollDeviceCode(request)
    }

    override suspend fun revokeDriveAccess(): ChimahonNovelSyncCommandResult {
        return callbacks.revokeDriveAccess()
    }

    override suspend fun syncLibrary(request: ChimahonNovelSyncRequest): ChimahonNovelSyncCommandResult {
        return callbacks.syncLibrary(request)
    }

    override suspend fun syncNovel(
        novelId: String,
        request: ChimahonNovelSyncRequest,
    ): ChimahonNovelSyncCommandResult {
        return callbacks.syncNovel(novelId, request)
    }

    override suspend fun cancelSync(): ChimahonNovelSyncCommandResult {
        return callbacks.cancelSync()
    }

    override suspend fun clearCache(): ChimahonNovelSyncCommandResult {
        return callbacks.clearCache()
    }
}

interface ChimahonNovelSyncService : ChimahonNovelSyncRepositoryBridge

class ChimahonRepositoryNovelSyncService(
    private val bridge: ChimahonNovelSyncRepositoryBridge,
) : ChimahonNovelSyncService,
    ChimahonNovelSyncRepositoryBridge by bridge

fun ChimahonNovelSyncRepositoryBridge.asNovelSyncService(): ChimahonNovelSyncService {
    return ChimahonRepositoryNovelSyncService(this)
}

fun ChimahonNovelSyncSnapshot.toNovelTtuSyncState(
    formatEpochMillis: (Long) -> String? = { null },
): NovelTtuSyncState {
    return NovelTtuSyncState(
        enabled = enabled,
        mode = settings.mode.toNovelSyncMode(),
        direction = settings.direction.toNovelSyncDirection(),
        authStatus = authStatus.toNovelDriveAuthStatus(),
        prompt = prompt?.toNovelDeviceCodePromptUiModel(),
        syncing = syncing,
        progressPercent = progress.normalizedPercent,
        lastSyncLabel = lastSyncEpochMillis.takeIf { it > 0L }?.let(formatEpochMillis),
        summary = summary.displayLabel ?: progress.message,
        errorMessage = errorMessage,
    )
}

fun ChimahonNovelSyncSnapshot.toNovelLibrarySyncOverview(
    formatEpochMillis: (Long) -> String? = { null },
): ChimahonNovelSyncOverview {
    return ChimahonNovelSyncOverview(
        providerName = providerName,
        enabled = enabled,
        connected = connected,
        syncing = syncing,
        progressPercent = progress.normalizedPercent,
        lastSyncEpochMillis = lastSyncEpochMillis,
        lastSyncLabel = lastSyncEpochMillis.takeIf { it > 0L }?.let(formatEpochMillis),
        summaryLabel = summary.displayLabel ?: progress.message,
        errorMessage = errorMessage,
    )
}

fun ChimahonNovelSyncSnapshot.toNovelLibrarySyncUiState(
    formatEpochMillis: (Long) -> String? = { null },
): ChimahonNovelSyncUiState {
    return toNovelLibrarySyncOverview(formatEpochMillis).toNovelSyncUiState()
}

private fun ChimahonNovelSyncBackendMode.toNovelSyncMode(): NovelSyncMode {
    return when (this) {
        ChimahonNovelSyncBackendMode.Off -> NovelSyncMode.Off
        ChimahonNovelSyncBackendMode.Auto -> NovelSyncMode.Auto
        ChimahonNovelSyncBackendMode.Import -> NovelSyncMode.Import
        ChimahonNovelSyncBackendMode.Export -> NovelSyncMode.Export
        ChimahonNovelSyncBackendMode.TwoWay -> NovelSyncMode.TwoWay
    }
}

private fun ChimahonNovelSyncBackendDirection.toNovelSyncDirection(): NovelSyncDirection {
    return when (this) {
        ChimahonNovelSyncBackendDirection.Auto -> NovelSyncDirection.Auto
        ChimahonNovelSyncBackendDirection.Import -> NovelSyncDirection.Import
        ChimahonNovelSyncBackendDirection.Export -> NovelSyncDirection.Export
    }
}

private fun ChimahonNovelDriveAuthBackendStatus.toNovelDriveAuthStatus(): NovelDriveAuthStatus {
    return when (this) {
        ChimahonNovelDriveAuthBackendStatus.MissingConfiguration -> NovelDriveAuthStatus.MissingConfiguration
        ChimahonNovelDriveAuthBackendStatus.NotConnected -> NovelDriveAuthStatus.NotConnected
        ChimahonNovelDriveAuthBackendStatus.Connecting -> NovelDriveAuthStatus.Connecting
        ChimahonNovelDriveAuthBackendStatus.Connected -> NovelDriveAuthStatus.Connected
        ChimahonNovelDriveAuthBackendStatus.Failed -> NovelDriveAuthStatus.Failed
    }
}

private fun ChimahonNovelDeviceCodePrompt.toNovelDeviceCodePromptUiModel(): NovelDeviceCodePromptUiModel {
    return NovelDeviceCodePromptUiModel(
        verificationUrl = verificationUrl,
        userCode = userCode,
        expiresInSeconds = expiresInSeconds,
        pollIntervalSeconds = pollIntervalSeconds,
    )
}
