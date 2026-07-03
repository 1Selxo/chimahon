package app.chimahon.shared.reader.novel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

data class NovelReaderDocument(
    val id: String = "",
    val title: String = "Untitled novel",
    val subtitle: String? = null,
    val author: String? = null,
    val chapters: List<NovelReaderChapter> = emptyList(),
) {
    val chapterCount: Int
        get() = chapters.size
}

data class NovelReaderChapter(
    val index: Int,
    val title: String,
    val href: String? = null,
    val characterCount: Int? = null,
    val isImageOnly: Boolean = false,
)

data class NovelReaderPosition(
    val chapterIndex: Int = 0,
    val progress: Double = 0.0,
    val characterOffset: Int? = null,
) {
    val boundedProgress: Double
        get() = progress.coerceIn(0.0, 1.0)

    val progressPercent: Int
        get() = (boundedProgress * 100.0).roundToInt()
}

data class NovelReaderUiState(
    val document: NovelReaderDocument = NovelReaderDocument(),
    val position: NovelReaderPosition = NovelReaderPosition(),
    val appearance: NovelReaderAppearanceState = NovelReaderAppearanceState(),
    val viewportContent: NovelReaderViewportContent = NovelReaderViewportContent.Text(
        text = NovelReaderDefaults.previewText,
    ),
    val showHud: Boolean = true,
    val focusMode: Boolean = false,
    val activePanel: NovelReaderPanel? = null,
    val tracking: NovelReaderTrackingState = NovelReaderTrackingState(),
    val loadingMessage: String? = null,
    val errorMessage: String? = null,
) {
    val currentChapter: NovelReaderChapter?
        get() = document.chapters.getOrNull(position.chapterIndex)

    val progressLabel: String
        get() {
            val chapterCount = document.chapterCount
            val percent = "${position.progressPercent}%"
            return if (chapterCount > 0) {
                "${position.chapterIndex + 1} / $chapterCount - $percent"
            } else {
                percent
            }
        }
}

sealed interface NovelReaderViewportContent {
    data object Empty : NovelReaderViewportContent

    data class Loading(
        val message: String = "Opening...",
    ) : NovelReaderViewportContent

    data class Error(
        val message: String,
    ) : NovelReaderViewportContent

    data class Web(
        val chapterUrl: String?,
        val title: String? = null,
        val htmlPreview: String? = null,
        val isImageOnly: Boolean = false,
    ) : NovelReaderViewportContent

    data class Text(
        val text: String,
    ) : NovelReaderViewportContent
}

enum class NovelReaderPanel {
    Appearance,
    Chapters,
    Statistics,
    Sasayaki,
}

enum class NovelReaderTheme(val title: String, val wireName: String) {
    System("System", "system"),
    Light("Light", "light"),
    Dark("Dark", "dark"),
    Sepia("Sepia", "sepia"),
    PureBlack("Pure black", "pure_black"),
    Custom("Custom", "custom"),
}

data class NovelReaderCustomTheme(
    val name: String = "",
    val backgroundColor: Int = 0xFFF2E2C9.toInt(),
    val textColor: Int = 0xFF000000.toInt(),
)

data class NovelReaderAppearanceState(
    val fontSize: Double = 18.0,
    val lineHeight: Double = 1.6,
    val characterSpacing: Double = 0.0,
    val paragraphSpacing: Double = 0.0,
    val horizontalPadding: Double = 10.0,
    val verticalPadding: Double = 10.0,
    val selectedFont: String = "System Serif",
    val fontUrl: String? = null,
    val fontOptions: List<String> = NovelReaderDefaults.fontOptions,
    val theme: NovelReaderTheme = NovelReaderTheme.System,
    val customBackgroundColor: Int = 0xFFF2E2C9.toInt(),
    val customTextColor: Int = 0xFF000000.toInt(),
    val customThemes: List<NovelReaderCustomTheme> = emptyList(),
    val verticalWriting: Boolean = true,
    val justifyText: Boolean = false,
    val avoidPageBreak: Boolean = true,
    val hideFurigana: Boolean = false,
    val layoutAdvanced: Boolean = false,
    val tapZonePercent: Int = 20,
    val chapterSwipeDistance: Int = 96,
    val continuousMode: Boolean = false,
    val keepScreenOn: Boolean = false,
    val systemLightSepia: Boolean = false,
) {
    fun resolveColors(systemDark: Boolean = false): NovelReaderResolvedColors {
        val (background, text) = when (theme) {
            NovelReaderTheme.Light -> 0xFFFFFFFF.toInt() to 0xFF000000.toInt()
            NovelReaderTheme.Dark -> 0xFF121212.toInt() to 0xFFE0E0E0.toInt()
            NovelReaderTheme.Sepia -> 0xFFF2E2C9.toInt() to 0xFF3C2C1C.toInt()
            NovelReaderTheme.PureBlack -> 0xFF000000.toInt() to 0xFFE0E0E0.toInt()
            NovelReaderTheme.Custom -> customBackgroundColor to customTextColor
            NovelReaderTheme.System -> {
                if (systemDark) {
                    if (systemLightSepia) {
                        0xFF1C140C.toInt() to 0xFFF2E2C9.toInt()
                    } else {
                        0xFF121212.toInt() to 0xFFE0E0E0.toInt()
                    }
                } else if (systemLightSepia) {
                    0xFFF2E2C9.toInt() to 0xFF3C2C1C.toInt()
                } else {
                    0xFFFFFFFF.toInt() to 0xFF000000.toInt()
                }
            }
        }
        return NovelReaderResolvedColors(
            background = Color(background),
            text = Color(text),
        )
    }
}

data class NovelReaderResolvedColors(
    val background: Color,
    val text: Color,
) {
    val chrome: Color
        get() = background.copy(alpha = 0.94f)

    val chromeText: Color
        get() = text

    val mutedText: Color
        get() = text.copy(alpha = 0.68f)

    val outline: Color
        get() = text.copy(alpha = 0.18f)

    val selected: Color
        get() = text.copy(alpha = 0.12f)
}

data class NovelReaderTrackingState(
    val isTracking: Boolean = false,
    val charactersRead: Int = 0,
    val totalCharacters: Int? = null,
    val readingTimeSeconds: Long = 0L,
    val charactersPerHour: Int? = null,
)

data class NovelReaderLookupRequest(
    val word: String,
    val sentence: String,
    val rect: NovelReaderSelectionRect = NovelReaderSelectionRect(),
)

data class NovelReaderSelectionRect(
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 0f,
    val height: Float = 0f,
)

data class NovelReaderViewportTap(
    val x: Float,
    val y: Float,
    val zone: NovelReaderViewportTapZone,
)

enum class NovelReaderViewportTapZone {
    Top,
    Content,
    Bottom,
}

data class NovelReaderViewportCallbacks(
    val onTap: (NovelReaderViewportTap) -> Unit = {},
    val onProgressChanged: (NovelReaderPosition) -> Unit = {},
    val onLookupRequested: (NovelReaderLookupRequest) -> Unit = {},
    val onSentenceReady: (String) -> Unit = {},
    val onDismissLookupRequested: () -> Unit = {},
    val onInternalLinkClicked: (String) -> Unit = {},
    val onSelectionRectsReceived: (List<NovelReaderSelectionRect>) -> Unit = {},
)

class NovelReaderViewportScope(
    val state: NovelReaderUiState,
    val colors: NovelReaderResolvedColors,
    val callbacks: NovelReaderViewportCallbacks,
    val onCommand: (NovelReaderCommand) -> Unit,
)

sealed interface NovelReaderCommand {
    data class LoadChapter(
        val url: String,
        val progress: Double = 0.0,
        val title: String? = null,
    ) : NovelReaderCommand

    data class JumpToChapter(
        val chapterIndex: Int,
        val progress: Double = 0.0,
        val fragment: String? = null,
    ) : NovelReaderCommand

    data class JumpToFragment(
        val fragment: String,
    ) : NovelReaderCommand

    data class JumpToUrl(
        val url: String,
    ) : NovelReaderCommand

    data class ApplySasayakiCues(
        val cuesJson: String,
    ) : NovelReaderCommand

    data class HighlightSasayakiCue(
        val cueId: String,
        val reveal: Boolean,
    ) : NovelReaderCommand

    data object ClearSasayakiCue : NovelReaderCommand

    data class UpdateTextColor(
        val hex: String?,
    ) : NovelReaderCommand

    data class ChangeMode(
        val continuous: Boolean,
    ) : NovelReaderCommand

    data class ApplyAppearance(
        val appearance: NovelReaderAppearanceState,
    ) : NovelReaderCommand

    data class ChangeFocusMode(
        val focusMode: Boolean,
    ) : NovelReaderCommand

    data class Paginate(
        val forward: Boolean,
    ) : NovelReaderCommand

    data class SeekToProgress(
        val progress: Double,
    ) : NovelReaderCommand

    data class SaveProgress(
        val position: NovelReaderPosition,
    ) : NovelReaderCommand

    data object ClearSelection : NovelReaderCommand

    data class HighlightSelection(
        val charCount: Int,
    ) : NovelReaderCommand

    data class GetSelectionRects(
        val charCount: Int,
        val startOffset: Int = 0,
    ) : NovelReaderCommand

    data object DismissLookup : NovelReaderCommand
}

class NovelReaderCommandBridge {
    var chapterUrl: String? by mutableStateOf(null)
        private set

    var chapterTitle: String? by mutableStateOf(null)
        private set

    var progress: Double by mutableStateOf(0.0)
        private set

    private val commandQueue = mutableStateListOf<NovelReaderCommand>()

    val pendingCommands: List<NovelReaderCommand>
        get() = commandQueue

    fun send(command: NovelReaderCommand) {
        commandQueue += command
    }

    fun consumePendingCommands(): List<NovelReaderCommand> {
        val commands = commandQueue.toList()
        commandQueue.clear()
        return commands
    }

    fun updateState(
        url: String?,
        progress: Double,
        title: String? = null,
    ) {
        chapterUrl = url
        chapterTitle = title
        this.progress = progress.coerceIn(0.0, 1.0)
    }

    fun updateProgress(progress: Double) {
        this.progress = progress.coerceIn(0.0, 1.0)
    }

    fun loadChapter(
        url: String,
        progress: Double = 0.0,
        title: String? = null,
    ) {
        updateState(url, progress, title)
        send(NovelReaderCommand.LoadChapter(url, progress, title))
    }

    fun paginate(forward: Boolean) {
        send(NovelReaderCommand.Paginate(forward))
    }

    fun clearSelection() {
        send(NovelReaderCommand.ClearSelection)
    }
}

data class NovelReaderThemeOption(
    val theme: NovelReaderTheme,
    val label: String,
    val backgroundColor: Int,
    val textColor: Int,
    val splitBackgroundColor: Int? = null,
)

object NovelReaderDefaults {
    val fontOptions: List<String> = listOf(
        "System Serif",
        "System Sans-Serif",
        "Serif",
        "Sans Serif",
        "Monospace",
    )

    val themeOptions: List<NovelReaderThemeOption> = listOf(
        NovelReaderThemeOption(
            theme = NovelReaderTheme.System,
            label = "System",
            backgroundColor = 0xFFFFFFFF.toInt(),
            textColor = 0xFF111111.toInt(),
            splitBackgroundColor = 0xFF121212.toInt(),
        ),
        NovelReaderThemeOption(
            theme = NovelReaderTheme.Light,
            label = "Light",
            backgroundColor = 0xFFFFFFFF.toInt(),
            textColor = 0xFF111111.toInt(),
        ),
        NovelReaderThemeOption(
            theme = NovelReaderTheme.Dark,
            label = "Dark",
            backgroundColor = 0xFF121212.toInt(),
            textColor = 0xFFE0E0E0.toInt(),
        ),
        NovelReaderThemeOption(
            theme = NovelReaderTheme.Sepia,
            label = "Sepia",
            backgroundColor = 0xFFF2E2C9.toInt(),
            textColor = 0xFF3C2C1C.toInt(),
        ),
        NovelReaderThemeOption(
            theme = NovelReaderTheme.PureBlack,
            label = "Black",
            backgroundColor = 0xFF000000.toInt(),
            textColor = 0xFFE0E0E0.toInt(),
        ),
    )

    const val previewText: String =
        "The common novel reader shell is ready for a platform viewport. " +
            "Text content can render here while WebView, EPUB, and lookup plumbing are integrated."
}
