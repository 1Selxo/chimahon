package app.chimahon.shared.novelreaderui

import kotlin.math.abs

data class NovelReaderProgressPersistenceRequest(
    val update: ChimahonNovelReaderProgressUpdate,
    val previous: ChimahonNovelReaderProgressSnapshot? = null,
    val policy: NovelReaderProgressPersistencePolicy = NovelReaderProgressPersistencePolicy(),
    val shouldPersist: Boolean = true,
    val reason: NovelReaderProgressPersistenceReason = NovelReaderProgressPersistenceReason.Forced,
)

enum class NovelReaderProgressPersistenceReason {
    Disabled,
    TriggerDisabled,
    NoMeaningfulChange,
    ReaderOpened,
    PageChanged,
    ChapterChanged,
    ReaderClosed,
    Periodic,
    Sync,
    Forced,
}

fun NovelReaderUiState.toNovelReaderProgressPersistenceRequest(
    novelId: String,
    trigger: ChimahonNovelReaderProgressTrigger = ChimahonNovelReaderProgressTrigger.PageChanged,
    previous: ChimahonNovelReaderProgressSnapshot? = null,
    readTimeSeconds: Long = previous?.readTimeSeconds ?: 0L,
    sessionReadTimeSeconds: Long = previous?.sessionReadTimeSeconds ?: 0L,
    readCharacterCount: Int = previous?.readCharacterCount ?: 0,
    sessionReadCharacterCount: Int = previous?.sessionReadCharacterCount ?: 0,
    completed: Boolean = progress >= 1f,
): NovelReaderProgressPersistenceRequest {
    val policy = progressPersistence.policy
    val update = toNovelReaderProgressUpdate(
        novelId = novelId,
        trigger = trigger,
        readTimeSeconds = readTimeSeconds,
        sessionReadTimeSeconds = sessionReadTimeSeconds,
        readCharacterCount = readCharacterCount,
        sessionReadCharacterCount = sessionReadCharacterCount,
        completed = completed,
    )
    val reason = persistenceReasonFor(
        update = update,
        previous = previous,
        policy = policy,
        trigger = trigger,
    )

    return NovelReaderProgressPersistenceRequest(
        update = update,
        previous = previous,
        policy = policy,
        shouldPersist = reason != NovelReaderProgressPersistenceReason.Disabled &&
            reason != NovelReaderProgressPersistenceReason.TriggerDisabled &&
            reason != NovelReaderProgressPersistenceReason.NoMeaningfulChange,
        reason = reason,
    )
}

suspend fun ChimahonNovelReaderRepositoryBridge.saveProgress(
    request: NovelReaderProgressPersistenceRequest,
): ChimahonNovelReaderProgressMutationResult? {
    if (!request.shouldPersist) return null
    return saveProgress(request.update)
}

fun NovelReaderProgressPersistenceRequest.toPendingPersistenceUiState(
    previous: NovelReaderProgressPersistenceUiState = NovelReaderProgressPersistenceUiState(),
): NovelReaderProgressPersistenceUiState {
    return previous.copy(
        policy = policy,
        pendingSave = shouldPersist,
        lastErrorMessage = null,
    )
}

fun ChimahonNovelReaderProgressMutationResult.toNovelReaderProgressPersistenceUiState(
    previous: NovelReaderProgressPersistenceUiState = NovelReaderProgressPersistenceUiState(),
    fallbackUpdate: ChimahonNovelReaderProgressUpdate? = null,
    savedAtEpochMillis: Long = 0L,
): NovelReaderProgressPersistenceUiState {
    val snapshot = progress ?: fallbackUpdate?.snapshot
    return previous.copy(
        pendingSave = false,
        lastSavedProgress = snapshot?.normalizedProgress?.toFloat() ?: previous.lastSavedProgress,
        lastSavedChapterId = snapshot?.chapterId ?: previous.lastSavedChapterId,
        lastSavedPageIndex = snapshot?.pageIndex ?: previous.lastSavedPageIndex,
        lastSavedReadTimeSeconds = snapshot?.readTimeSeconds ?: previous.lastSavedReadTimeSeconds,
        lastSavedEpochMillis = savedAtEpochMillis.takeIf { it > 0L } ?: previous.lastSavedEpochMillis,
        lastErrorMessage = mutation.errors.firstOrNull()?.message,
    )
}

private fun persistenceReasonFor(
    update: ChimahonNovelReaderProgressUpdate,
    previous: ChimahonNovelReaderProgressSnapshot?,
    policy: NovelReaderProgressPersistencePolicy,
    trigger: ChimahonNovelReaderProgressTrigger,
): NovelReaderProgressPersistenceReason {
    if (!policy.enabled) return NovelReaderProgressPersistenceReason.Disabled
    if (!policy.enabledFor(trigger)) return NovelReaderProgressPersistenceReason.TriggerDisabled
    if (previous == null) {
        return when (trigger) {
            ChimahonNovelReaderProgressTrigger.ReaderOpened -> NovelReaderProgressPersistenceReason.ReaderOpened
            ChimahonNovelReaderProgressTrigger.PageChanged -> NovelReaderProgressPersistenceReason.PageChanged
            ChimahonNovelReaderProgressTrigger.ChapterChanged -> NovelReaderProgressPersistenceReason.ChapterChanged
            ChimahonNovelReaderProgressTrigger.ReaderClosed -> NovelReaderProgressPersistenceReason.ReaderClosed
            ChimahonNovelReaderProgressTrigger.Periodic -> NovelReaderProgressPersistenceReason.Periodic
            ChimahonNovelReaderProgressTrigger.Sync -> NovelReaderProgressPersistenceReason.Sync
        }
    }
    if (update.chapterId != previous.chapterId) return NovelReaderProgressPersistenceReason.ChapterChanged
    if (trigger == ChimahonNovelReaderProgressTrigger.ReaderClosed) {
        return NovelReaderProgressPersistenceReason.ReaderClosed
    }
    if (trigger == ChimahonNovelReaderProgressTrigger.Sync) {
        return NovelReaderProgressPersistenceReason.Sync
    }

    val progressDelta = abs(update.progress - previous.normalizedProgress)
    val pageChanged = update.pageIndex != previous.pageIndex || update.pageCount != previous.pageCount
    val readTimeDelta = update.readTimeSeconds - previous.readTimeSeconds
    val meaningful = pageChanged ||
        progressDelta >= policy.minimumProgressDelta ||
        readTimeDelta >= policy.minimumReadSecondsDelta

    if (!meaningful) return NovelReaderProgressPersistenceReason.NoMeaningfulChange
    return when (trigger) {
        ChimahonNovelReaderProgressTrigger.ReaderOpened -> NovelReaderProgressPersistenceReason.ReaderOpened
        ChimahonNovelReaderProgressTrigger.PageChanged -> NovelReaderProgressPersistenceReason.PageChanged
        ChimahonNovelReaderProgressTrigger.ChapterChanged -> NovelReaderProgressPersistenceReason.ChapterChanged
        ChimahonNovelReaderProgressTrigger.ReaderClosed -> NovelReaderProgressPersistenceReason.ReaderClosed
        ChimahonNovelReaderProgressTrigger.Periodic -> NovelReaderProgressPersistenceReason.Periodic
        ChimahonNovelReaderProgressTrigger.Sync -> NovelReaderProgressPersistenceReason.Sync
    }
}
