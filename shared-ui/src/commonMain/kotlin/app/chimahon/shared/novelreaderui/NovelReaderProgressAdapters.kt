package app.chimahon.shared.novelreaderui

import app.chimahon.shared.novelui.ChimahonNovelBackendError
import app.chimahon.shared.novelui.ChimahonNovelBookRecord
import app.chimahon.shared.novelui.ChimahonNovelChapterRecord
import app.chimahon.shared.novelui.ChimahonNovelReaderBarState

fun ChimahonNovelReaderSessionLoadResult.toNovelReaderUiState(
    hud: NovelReaderHudState = NovelReaderHudState(),
): NovelReaderUiState {
    val sessionData = session
    val message = errors.toNovelReaderErrorMessage()
    if (sessionData == null) {
        return NovelReaderUiState(
            loadingMessage = null,
            errorMessage = message,
        )
    }

    return sessionData.toNovelReaderUiState(
        hud = hud,
        errorMessage = message ?: sessionData.errorMessage,
    )
}

fun ChimahonNovelReaderSessionData.toNovelReaderUiState(
    hud: NovelReaderHudState = NovelReaderHudState(),
    drawerVisible: Boolean = false,
    typographyVisible: Boolean = false,
    selection: NovelReaderSelectionState? = null,
    errorMessage: String? = this.errorMessage,
): NovelReaderUiState {
    val activeChapterId = progress.chapterId
        ?: chapters.getOrNull(progress.chapterIndex.coerceAtLeast(0))?.id

    return NovelReaderUiState(
        title = novel.displayTitle,
        subtitle = novel.author,
        sourceLabel = sourceLabel,
        chapters = chapters.map { chapter ->
            chapter.toNovelReaderChapterUiModel(current = chapter.id == activeChapterId)
        },
        sections = sections.map { it.toNovelReaderSectionUiModel() },
        pageParagraphs = pageParagraphs.map { it.toNovelReaderParagraphUiModel() },
        activeChapterId = activeChapterId,
        activeSectionId = progress.sectionId,
        pageIndex = progress.pageIndex,
        pageCount = progress.pageCount,
        progress = progress.normalizedProgress.toFloat(),
        readingMode = preferences.readingMode,
        readingDirection = preferences.readingDirection,
        layout = preferences.toNovelReaderLayoutState(),
        hud = hud,
        progressPersistence = progress.toNovelReaderProgressPersistenceUiState(),
        selection = selection,
        drawerVisible = drawerVisible,
        typographyVisible = typographyVisible,
        loadingMessage = loadingMessage,
        errorMessage = errorMessage,
    )
}

fun ChimahonNovelReaderChapterLoadResult.toNovelReaderUiState(
    previous: NovelReaderUiState,
): NovelReaderUiState {
    val chapterData = chapter ?: return previous.copy(errorMessage = errors.toNovelReaderErrorMessage())
    val chapterId = chapterData.chapter.id
    val updatedChapters = previous.chapters.map { existing ->
        if (existing.id == chapterId) {
            chapterData.chapter.toNovelReaderChapterUiModel(current = true)
        } else {
            existing.copy(current = false)
        }
    }.ifEmpty {
        listOf(chapterData.chapter.toNovelReaderChapterUiModel(current = true))
    }
    val updatedSections = previous.sections
        .filterNot { it.chapterId == chapterId } + chapterData.sections.map { it.toNovelReaderSectionUiModel() }

    return previous.copy(
        chapters = updatedChapters,
        sections = updatedSections,
        activeChapterId = chapterId,
        activeSectionId = chapterData.progress.sectionId,
        pageIndex = chapterData.progress.pageIndex,
        pageCount = chapterData.progress.pageCount,
        progress = chapterData.progress.normalizedProgress.toFloat(),
        progressPersistence = chapterData.progress.toNovelReaderProgressPersistenceUiState(previous.progressPersistence),
        errorMessage = errors.toNovelReaderErrorMessage(),
    )
}

fun ChimahonNovelBookRecord.toNovelReaderBarState(
    progress: ChimahonNovelReaderProgressSnapshot,
    chapters: List<ChimahonNovelChapterRecord> = emptyList(),
): ChimahonNovelReaderBarState {
    val currentChapter = progress.chapterId?.let { id -> chapters.firstOrNull { it.id == id } }
        ?: chapters.getOrNull(progress.chapterIndex)
    val chapterCount = chapters.size.takeIf { it > 0 } ?: this.chapterCount

    return ChimahonNovelReaderBarState(
        title = displayTitle,
        chapterTitle = currentChapter?.displayTitle,
        progressLabel = "${progress.percent}%",
        chapterIndex = progress.chapterIndex.coerceIn(0, (chapterCount - 1).coerceAtLeast(0)),
        chapterCount = chapterCount,
        trackingActive = progress.readTimeSeconds > 0L || progress.sessionReadTimeSeconds > 0L,
        canGoPrevious = progress.chapterIndex > 0,
        canGoNext = chapterCount > 0 && progress.chapterIndex < chapterCount - 1,
    )
}

fun NovelReaderUiState.toNovelReaderProgressUpdate(
    novelId: String,
    trigger: ChimahonNovelReaderProgressTrigger = ChimahonNovelReaderProgressTrigger.PageChanged,
    readTimeSeconds: Long = 0L,
    sessionReadTimeSeconds: Long = 0L,
    readCharacterCount: Int = 0,
    sessionReadCharacterCount: Int = 0,
    completed: Boolean = progress >= 1f,
): ChimahonNovelReaderProgressUpdate {
    val chapter = activeChapter
    val section = activeSection
    return ChimahonNovelReaderProgressUpdate(
        novelId = novelId,
        chapterId = chapter?.id ?: activeChapterId,
        chapterIndex = chapter?.let { chapters.indexOfFirst { candidate -> candidate.id == it.id } }
            ?.takeIf { it >= 0 }
            ?: 0,
        sectionId = section?.id ?: activeSectionId,
        pageIndex = pageIndex,
        pageCount = pageCount,
        progress = progress.toDouble().coerceIn(0.0, 1.0),
        chapterProgress = chapter?.progress?.toDouble()?.coerceIn(0.0, 1.0) ?: progress.toDouble().coerceIn(0.0, 1.0),
        characterOffset = section?.startOffset ?: 0,
        readCharacterCount = readCharacterCount,
        sessionReadCharacterCount = sessionReadCharacterCount,
        readTimeSeconds = readTimeSeconds,
        sessionReadTimeSeconds = sessionReadTimeSeconds,
        completed = completed,
        trigger = trigger,
    )
}

fun ChimahonNovelChapterRecord.toNovelReaderChapterUiModel(
    current: Boolean = false,
): NovelReaderChapterUiModel {
    return NovelReaderChapterUiModel(
        id = id,
        title = displayTitle,
        href = href,
        sourceOrder = sourceOrder,
        spineIndex = spineIndex,
        characterCount = characterCount,
        progress = progressFraction,
        current = current,
        read = effectivelyRead,
        bookmarked = bookmarked,
        imageOnly = imageOnly,
    )
}

fun ChimahonNovelReaderSectionRecord.toNovelReaderSectionUiModel(): NovelReaderSectionUiModel {
    return NovelReaderSectionUiModel(
        id = id,
        chapterId = chapterId,
        title = title,
        sourceOrder = sourceOrder,
        startOffset = startOffset,
        characterCount = characterCount,
        paragraphs = paragraphs.map { it.toNovelReaderParagraphUiModel() },
    )
}

fun ChimahonNovelReaderParagraphRecord.toNovelReaderParagraphUiModel(): NovelReaderParagraphUiModel {
    return NovelReaderParagraphUiModel(
        id = id,
        text = text,
        type = type,
        chapterId = chapterId,
        sectionId = sectionId,
        sourceOrder = sourceOrder,
        startOffset = startOffset,
        sasayakiCueId = sasayakiCueId,
    )
}

fun ChimahonNovelReaderPreferences.toNovelReaderLayoutState(): NovelReaderLayoutState {
    return NovelReaderLayoutState(
        fontSize = fontSize,
        lineHeight = lineHeight,
        characterSpacing = characterSpacing,
        paragraphSpacing = paragraphSpacing,
        horizontalPaddingPercent = horizontalPaddingPercent,
        verticalPaddingPercent = verticalPaddingPercent,
        selectedFont = selectedFont,
        fontOptions = fontOptions,
        theme = theme,
        verticalWriting = verticalWriting,
        justifyText = justifyText,
        avoidPageBreak = avoidPageBreak,
        hideFurigana = hideFurigana,
        keepScreenOn = keepScreenOn,
        tapZonePercent = tapZonePercent,
        chapterSwipeDistance = chapterSwipeDistance,
        backgroundColor = backgroundColor,
        textColor = textColor,
    )
}

fun ChimahonNovelReaderProgressSnapshot.toNovelReaderProgressPersistenceUiState(
    previous: NovelReaderProgressPersistenceUiState = NovelReaderProgressPersistenceUiState(),
): NovelReaderProgressPersistenceUiState {
    return previous.copy(
        pendingSave = false,
        lastSavedProgress = normalizedProgress.toFloat(),
        lastSavedChapterId = chapterId,
        lastSavedPageIndex = pageIndex,
        lastSavedReadTimeSeconds = readTimeSeconds,
        lastSavedEpochMillis = lastReadEpochMillis,
        lastErrorMessage = null,
    )
}

private fun List<ChimahonNovelBackendError>.toNovelReaderErrorMessage(): String? {
    return firstOrNull()?.let { error -> error.message ?: error.code.name }
}
