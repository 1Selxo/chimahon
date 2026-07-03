package app.chimahon.shared.novelreaderui

import app.chimahon.shared.novelui.ChimahonNovelBackendError
import app.chimahon.shared.novelui.ChimahonNovelBookRecord
import app.chimahon.shared.novelui.ChimahonNovelChapterRecord
import app.chimahon.shared.novelui.ChimahonNovelMutationResult
import app.chimahon.shared.novelui.ChimahonNovelProgressSummary

data class ChimahonNovelReaderSessionLoadRequest(
    val novelId: String,
    val chapterId: String? = null,
    val chapterIndex: Int? = null,
    val initialProgress: Double? = null,
    val includeAdjacentChapters: Boolean = true,
    val includeParagraphs: Boolean = true,
    val forceReload: Boolean = false,
)

data class ChimahonNovelReaderChapterLoadRequest(
    val novelId: String,
    val chapterId: String? = null,
    val chapterIndex: Int? = null,
    val href: String? = null,
    val initialProgress: Double = 0.0,
)

data class ChimahonNovelReaderSessionData(
    val novel: ChimahonNovelBookRecord,
    val chapters: List<ChimahonNovelChapterRecord> = emptyList(),
    val sections: List<ChimahonNovelReaderSectionRecord> = emptyList(),
    val pageParagraphs: List<ChimahonNovelReaderParagraphRecord> = emptyList(),
    val progress: ChimahonNovelReaderProgressSnapshot = ChimahonNovelReaderProgressSnapshot(novelId = novel.id),
    val preferences: ChimahonNovelReaderPreferences = ChimahonNovelReaderPreferences(),
    val sourceLabel: String? = novel.sourceInfo?.displayName ?: novel.source.title,
    val loadingMessage: String? = null,
    val errorMessage: String? = null,
)

data class ChimahonNovelReaderChapterData(
    val novelId: String,
    val chapter: ChimahonNovelChapterRecord,
    val sections: List<ChimahonNovelReaderSectionRecord> = emptyList(),
    val progress: ChimahonNovelReaderProgressSnapshot = ChimahonNovelReaderProgressSnapshot(
        novelId = novelId,
        chapterId = chapter.id,
        chapterIndex = chapter.spineIndex ?: chapter.sourceOrder.coerceAtLeast(0),
    ),
)

data class ChimahonNovelReaderSessionLoadResult(
    val session: ChimahonNovelReaderSessionData? = null,
    val loadedAtMillis: Long = 0L,
    val errors: List<ChimahonNovelBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()

    val found: Boolean
        get() = session != null
}

data class ChimahonNovelReaderChapterLoadResult(
    val chapter: ChimahonNovelReaderChapterData? = null,
    val loadedAtMillis: Long = 0L,
    val errors: List<ChimahonNovelBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

data class ChimahonNovelReaderSectionRecord(
    val id: String,
    val chapterId: String,
    val title: String? = null,
    val sourceOrder: Int = Int.MAX_VALUE,
    val startOffset: Int? = null,
    val characterCount: Int? = null,
    val paragraphs: List<ChimahonNovelReaderParagraphRecord> = emptyList(),
)

data class ChimahonNovelReaderParagraphRecord(
    val id: String,
    val text: String,
    val type: NovelReaderParagraphType = NovelReaderParagraphType.Body,
    val chapterId: String? = null,
    val sectionId: String? = null,
    val sourceOrder: Int = Int.MAX_VALUE,
    val startOffset: Int? = null,
    val sasayakiCueId: String? = null,
)

data class ChimahonNovelReaderPreferences(
    val readingMode: NovelReadingMode = NovelReadingMode.Paged,
    val readingDirection: NovelReadingDirection = NovelReadingDirection.Default,
    val fontSize: Float = 18f,
    val lineHeight: Float = 1.6f,
    val characterSpacing: Float = 0f,
    val paragraphSpacing: Float = 0.5f,
    val horizontalPaddingPercent: Float = 10f,
    val verticalPaddingPercent: Float = 10f,
    val selectedFont: String = "System Serif",
    val fontOptions: List<String> = listOf("System Serif", "System Sans", "Noto Serif", "Noto Sans", "Monospace"),
    val theme: NovelReaderTheme = NovelReaderTheme.System,
    val verticalWriting: Boolean = false,
    val justifyText: Boolean = false,
    val avoidPageBreak: Boolean = true,
    val hideFurigana: Boolean = false,
    val keepScreenOn: Boolean = false,
    val tapZonePercent: Float = 20f,
    val chapterSwipeDistance: Float = 96f,
    val backgroundColor: Int = 0xFFFFFFFF.toInt(),
    val textColor: Int = 0xFF111111.toInt(),
)

data class ChimahonNovelReaderProgressSnapshot(
    val novelId: String,
    val chapterId: String? = null,
    val chapterIndex: Int = 0,
    val sectionId: String? = null,
    val sectionIndex: Int = 0,
    val pageIndex: Int = 0,
    val pageCount: Int = 0,
    val progress: Double = 0.0,
    val chapterProgress: Double = progress,
    val characterOffset: Int = 0,
    val chapterCharacterOffset: Int? = null,
    val totalCharacterCount: Int? = null,
    val chapterCharacterCount: Int? = null,
    val readCharacterCount: Int = 0,
    val sessionReadCharacterCount: Int = 0,
    val readTimeSeconds: Long = 0L,
    val sessionReadTimeSeconds: Long = 0L,
    val lastReadEpochMillis: Long = 0L,
    val completed: Boolean = false,
) {
    val normalizedProgress: Double
        get() = progress.coerceIn(0.0, 1.0)

    val normalizedChapterProgress: Double
        get() = chapterProgress.coerceIn(0.0, 1.0)

    val percent: Int
        get() = (normalizedProgress * 100.0).toInt().coerceIn(0, 100)

    fun toNovelProgressSummary(): ChimahonNovelProgressSummary {
        return ChimahonNovelProgressSummary(
            chapterId = chapterId,
            chapterIndex = chapterIndex,
            sectionId = sectionId,
            pageIndex = pageIndex,
            pageCount = pageCount,
            progress = normalizedProgress,
            characterOffset = characterOffset,
            readCharacterCount = readCharacterCount,
            characterCount = totalCharacterCount,
            readTimeSeconds = readTimeSeconds,
            lastReadEpochMillis = lastReadEpochMillis,
            completed = completed,
        )
    }
}

data class ChimahonNovelReaderProgressUpdate(
    val novelId: String,
    val chapterId: String? = null,
    val chapterIndex: Int = 0,
    val sectionId: String? = null,
    val pageIndex: Int = 0,
    val pageCount: Int = 0,
    val progress: Double = 0.0,
    val chapterProgress: Double = progress,
    val characterOffset: Int = 0,
    val readCharacterCount: Int = 0,
    val sessionReadCharacterCount: Int = 0,
    val readTimeSeconds: Long = 0L,
    val sessionReadTimeSeconds: Long = 0L,
    val completed: Boolean = false,
    val trigger: ChimahonNovelReaderProgressTrigger = ChimahonNovelReaderProgressTrigger.PageChanged,
) {
    val snapshot: ChimahonNovelReaderProgressSnapshot
        get() = ChimahonNovelReaderProgressSnapshot(
            novelId = novelId,
            chapterId = chapterId,
            chapterIndex = chapterIndex,
            sectionId = sectionId,
            pageIndex = pageIndex,
            pageCount = pageCount,
            progress = progress,
            chapterProgress = chapterProgress,
            characterOffset = characterOffset,
            readCharacterCount = readCharacterCount,
            sessionReadCharacterCount = sessionReadCharacterCount,
            readTimeSeconds = readTimeSeconds,
            sessionReadTimeSeconds = sessionReadTimeSeconds,
            completed = completed,
        )
}

enum class ChimahonNovelReaderProgressTrigger {
    PageChanged,
    ChapterChanged,
    ReaderOpened,
    ReaderClosed,
    Periodic,
    Sync,
}

data class ChimahonNovelReaderProgressMutationResult(
    val progress: ChimahonNovelReaderProgressSnapshot? = null,
    val mutation: ChimahonNovelMutationResult = ChimahonNovelMutationResult(),
) {
    val successful: Boolean
        get() = mutation.successful
}

interface ChimahonNovelReaderRepositoryBridge {
    suspend fun loadSession(request: ChimahonNovelReaderSessionLoadRequest): ChimahonNovelReaderSessionLoadResult

    suspend fun loadChapter(request: ChimahonNovelReaderChapterLoadRequest): ChimahonNovelReaderChapterLoadResult

    suspend fun saveProgress(update: ChimahonNovelReaderProgressUpdate): ChimahonNovelReaderProgressMutationResult

    suspend fun markChapterRead(novelId: String, chapterId: String, read: Boolean): ChimahonNovelMutationResult

    suspend fun resolveInternalLink(
        novelId: String,
        href: String,
    ): ChimahonNovelReaderChapterLoadRequest?
}

data class ChimahonNovelReaderRepositoryCallbacks(
    val loadSession: suspend (ChimahonNovelReaderSessionLoadRequest) -> ChimahonNovelReaderSessionLoadResult,
    val loadChapter: suspend (ChimahonNovelReaderChapterLoadRequest) -> ChimahonNovelReaderChapterLoadResult,
    val saveProgress: suspend (ChimahonNovelReaderProgressUpdate) -> ChimahonNovelReaderProgressMutationResult,
    val markChapterRead: suspend (String, String, Boolean) -> ChimahonNovelMutationResult,
    val resolveInternalLink: suspend (String, String) -> ChimahonNovelReaderChapterLoadRequest?,
)

class ChimahonCallbackNovelReaderRepositoryBridge(
    private val callbacks: ChimahonNovelReaderRepositoryCallbacks,
) : ChimahonNovelReaderRepositoryBridge {
    override suspend fun loadSession(
        request: ChimahonNovelReaderSessionLoadRequest,
    ): ChimahonNovelReaderSessionLoadResult {
        return callbacks.loadSession(request)
    }

    override suspend fun loadChapter(
        request: ChimahonNovelReaderChapterLoadRequest,
    ): ChimahonNovelReaderChapterLoadResult {
        return callbacks.loadChapter(request)
    }

    override suspend fun saveProgress(
        update: ChimahonNovelReaderProgressUpdate,
    ): ChimahonNovelReaderProgressMutationResult {
        return callbacks.saveProgress(update)
    }

    override suspend fun markChapterRead(
        novelId: String,
        chapterId: String,
        read: Boolean,
    ): ChimahonNovelMutationResult {
        return callbacks.markChapterRead(novelId, chapterId, read)
    }

    override suspend fun resolveInternalLink(
        novelId: String,
        href: String,
    ): ChimahonNovelReaderChapterLoadRequest? {
        return callbacks.resolveInternalLink(novelId, href)
    }
}

interface ChimahonNovelReaderService : ChimahonNovelReaderRepositoryBridge

class ChimahonRepositoryNovelReaderService(
    private val bridge: ChimahonNovelReaderRepositoryBridge,
) : ChimahonNovelReaderService,
    ChimahonNovelReaderRepositoryBridge by bridge

fun ChimahonNovelReaderRepositoryBridge.asNovelReaderService(): ChimahonNovelReaderService {
    return ChimahonRepositoryNovelReaderService(this)
}
