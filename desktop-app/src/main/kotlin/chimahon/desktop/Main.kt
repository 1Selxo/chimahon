package chimahon.desktop

import app.chimahon.shared.ChimahonDesktopCommand
import app.chimahon.shared.ChimahonDesktopCommandRequest
import app.chimahon.shared.ChimahonSharedAppServices
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import java.awt.AWTEvent
import java.awt.Frame
import java.awt.Toolkit
import java.awt.event.AWTEventListener
import java.awt.event.InputEvent
import java.awt.event.KeyEvent as AwtKeyEvent
import java.awt.event.MouseEvent as AwtMouseEvent
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.KeyStroke
import javax.swing.SwingUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun main() {
    DesktopPlatformAffordances.configureRuntime()

    application {
        var services by remember { mutableStateOf<ChimahonSharedAppServices?>(null) }
        var startupError by remember { mutableStateOf<String?>(null) }
        var commandSerial by remember { mutableStateOf(0L) }
        var desktopCommandRequest by remember { mutableStateOf<ChimahonDesktopCommandRequest?>(null) }
        val dispatchDesktopCommand: (ChimahonDesktopCommand) -> Unit = { command ->
            commandSerial += 1
            desktopCommandRequest = ChimahonDesktopCommandRequest(commandSerial, command)
        }
        val closeApplication = {
            services?.close()
            exitApplication()
        }

        Window(
            onCloseRequest = closeApplication,
            state = WindowState(width = 1180.dp, height = 740.dp),
            title = "Chimahon",
        ) {
            DisposableEffect(window) {
                DesktopPlatformAffordances.configureWindow(window)
                val inputBridge = installDesktopInputBridge(
                    window = window,
                    onCommand = dispatchDesktopCommand,
                )
                window.jMenuBar = createDesktopMenuBar(
                    window = window,
                    onCommand = dispatchDesktopCommand,
                    onQuit = closeApplication,
                )
                onDispose {
                    inputBridge.close()
                    window.jMenuBar = null
                }
            }

            LaunchedEffect(Unit) {
                runCatching {
                    withContext(Dispatchers.Default) {
                        ChimahonSharedAppServices()
                    }
                }.fold(
                    onSuccess = { services = it },
                    onFailure = {
                        startupError = it.message ?: "Desktop services could not be initialized."
                    },
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                        when (event.key) {
                            Key.Escape -> {
                                dispatchDesktopCommand(ChimahonDesktopCommand.Back)
                                true
                            }
                            Key.F5 -> {
                                dispatchDesktopCommand(ChimahonDesktopCommand.Refresh)
                                true
                            }
                            else -> false
                        }
                    },
            ) {
                when {
                    services != null -> ChimahonDesktopApp(
                        services = checkNotNull(services),
                        desktopCommandRequest = desktopCommandRequest,
                        onDesktopCommandHandled = { id ->
                            if (desktopCommandRequest?.id == id) desktopCommandRequest = null
                        },
                    )
                    startupError != null -> DesktopStartupStatus(
                        title = "Chimahon could not start",
                        detail = checkNotNull(startupError),
                    )
                    else -> DesktopStartupStatus(
                        title = "Starting Chimahon",
                        detail = "Preparing the shared database, storage, and extension engine.",
                    )
                }
            }
        }
    }
}

private fun createDesktopMenuBar(
    window: java.awt.Window,
    onCommand: (ChimahonDesktopCommand) -> Unit,
    onQuit: () -> Unit,
): JMenuBar {
    val menuShortcutMask = Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx

    fun shortcut(
        keyCode: Int,
        modifiers: Int = 0,
    ): KeyStroke = KeyStroke.getKeyStroke(keyCode, modifiers)

    fun appShortcut(
        keyCode: Int,
        modifiers: Int = 0,
    ): KeyStroke = shortcut(keyCode, menuShortcutMask or modifiers)

    fun item(
        title: String,
        shortcut: KeyStroke? = null,
        action: () -> Unit,
    ): JMenuItem = JMenuItem(title).apply {
        accelerator = shortcut
        addActionListener { action() }
    }

    fun menu(title: String, mnemonic: Int, build: JMenu.() -> Unit): JMenu =
        JMenu(title).apply {
            setMnemonic(mnemonic)
            build()
        }

    fun placeholderItem(
        surface: String,
        title: String,
        shortcut: KeyStroke? = null,
        shortcutLabel: String? = null,
    ): JMenuItem = item(title, shortcut) {
        DesktopPlatformAffordances.copyDesktopActionPlaceholder(
            surface = surface,
            action = title,
            shortcut = shortcutLabel,
        )
    }

    val appShortcutLabel = DesktopPlatformAffordances.menuShortcutLabel

    return JMenuBar().apply {
        add(menu("File", AwtKeyEvent.VK_F) {
            DesktopDirectory.entries.forEach { directory ->
                add(item("Open ${directory.title} Folder") {
                    DesktopPlatformAffordances.openDirectory(directory)
                })
            }
            addSeparator()
            add(item("Share Storage Paths") { DesktopPlatformAffordances.shareStorageSummary() })
            addSeparator()
            add(item("Quit", appShortcut(AwtKeyEvent.VK_Q), onQuit))
        })
        add(menu("Navigate", AwtKeyEvent.VK_N) {
            add(item("Back", shortcut(AwtKeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.Back)
            })
            addSeparator()
            add(item("Library", appShortcut(AwtKeyEvent.VK_L)) { onCommand(ChimahonDesktopCommand.Library) })
            add(item("Updates", appShortcut(AwtKeyEvent.VK_U)) { onCommand(ChimahonDesktopCommand.Updates) })
            add(item("History", appShortcut(AwtKeyEvent.VK_H)) { onCommand(ChimahonDesktopCommand.History) })
            add(item("Browse Sources", appShortcut(AwtKeyEvent.VK_B)) {
                onCommand(ChimahonDesktopCommand.BrowseSources)
            })
            add(item("Browse Extensions", appShortcut(AwtKeyEvent.VK_E)) {
                onCommand(ChimahonDesktopCommand.BrowseExtensions)
            })
            add(item("Browse Feed", appShortcut(AwtKeyEvent.VK_B, InputEvent.SHIFT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.BrowseFeed)
            })
            add(item("Migrate", appShortcut(AwtKeyEvent.VK_M, InputEvent.SHIFT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.BrowseMigrate)
            })
            add(item("More", appShortcut(AwtKeyEvent.VK_M)) { onCommand(ChimahonDesktopCommand.More) })
            addSeparator()
            add(item("Settings", appShortcut(AwtKeyEvent.VK_COMMA)) { onCommand(ChimahonDesktopCommand.Settings) })
            add(item("Download Queue", appShortcut(AwtKeyEvent.VK_D)) {
                onCommand(ChimahonDesktopCommand.DownloadQueue)
            })
        })
        add(menu("Anime", AwtKeyEvent.VK_A) {
            add(item("Anime Library", appShortcut(AwtKeyEvent.VK_A, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.Library)
            })
            add(item("Anime Updates", appShortcut(AwtKeyEvent.VK_U, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.Updates)
            })
            add(item("Anime History", appShortcut(AwtKeyEvent.VK_H, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.History)
            })
            addSeparator()
            add(
                item(
                    "Browse Anime Sources",
                    appShortcut(AwtKeyEvent.VK_A, InputEvent.ALT_DOWN_MASK or InputEvent.SHIFT_DOWN_MASK),
                ) {
                    onCommand(ChimahonDesktopCommand.BrowseSources)
                },
            )
            add(
                item(
                    "Browse Anime Extensions",
                    appShortcut(AwtKeyEvent.VK_E, InputEvent.ALT_DOWN_MASK or InputEvent.SHIFT_DOWN_MASK),
                ) {
                    onCommand(ChimahonDesktopCommand.BrowseExtensions)
                },
            )
            add(
                item(
                    "Anime Download Queue",
                    appShortcut(AwtKeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK or InputEvent.SHIFT_DOWN_MASK),
                ) {
                    onCommand(ChimahonDesktopCommand.DownloadQueue)
                },
            )
            addSeparator()
            add(
                placeholderItem(
                    surface = "Anime",
                    title = "Open Anime Details",
                    shortcut = appShortcut(AwtKeyEvent.VK_I, InputEvent.SHIFT_DOWN_MASK),
                    shortcutLabel = "$appShortcutLabel+Shift+I",
                ),
            )
            add(
                placeholderItem(
                    surface = "Anime",
                    title = "Open Episode List",
                    shortcut = appShortcut(AwtKeyEvent.VK_G, InputEvent.SHIFT_DOWN_MASK),
                    shortcutLabel = "$appShortcutLabel+Shift+G",
                ),
            )
            add(
                placeholderItem(
                    surface = "Anime",
                    title = "Track Anime",
                    shortcut = appShortcut(AwtKeyEvent.VK_T, InputEvent.SHIFT_DOWN_MASK),
                    shortcutLabel = "$appShortcutLabel+Shift+T",
                ),
            )
        })
        add(menu("Edit", AwtKeyEvent.VK_E) {
            add(item("Search", appShortcut(AwtKeyEvent.VK_F)) { onCommand(ChimahonDesktopCommand.Search) })
            add(
                item(
                    "Toggle Filters",
                    KeyStroke.getKeyStroke(
                        AwtKeyEvent.VK_F,
                        menuShortcutMask or InputEvent.SHIFT_DOWN_MASK,
                    ),
                ) { onCommand(ChimahonDesktopCommand.ToggleFilters) },
            )
            add(item("Refresh", appShortcut(AwtKeyEvent.VK_R)) { onCommand(ChimahonDesktopCommand.Refresh) })
            add(item("Refresh (F5)", shortcut(AwtKeyEvent.VK_F5)) {
                onCommand(ChimahonDesktopCommand.Refresh)
            })
            addSeparator()
            DesktopDirectory.entries.forEach { directory ->
                add(item("Copy ${directory.title} Path") {
                    DesktopPlatformAffordances.copyDirectoryPath(directory)
                })
            }
            add(item("Copy All Storage Paths", appShortcut(AwtKeyEvent.VK_P, InputEvent.SHIFT_DOWN_MASK)) {
                DesktopPlatformAffordances.copyStorageSummary()
            })
        })
        add(menu("Reader", AwtKeyEvent.VK_R) {
            add(item("Previous Page", appShortcut(AwtKeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderPreviousPage)
            })
            add(item("Next Page", appShortcut(AwtKeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderNextPage)
            })
            add(item("First Page", appShortcut(AwtKeyEvent.VK_HOME, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderFirstPage)
            })
            add(item("Last Page", appShortcut(AwtKeyEvent.VK_END, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderLastPage)
            })
            addSeparator()
            add(
                item(
                    "Previous Chapter",
                    appShortcut(AwtKeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK or InputEvent.SHIFT_DOWN_MASK),
                ) {
                    onCommand(ChimahonDesktopCommand.ReaderPreviousChapter)
                },
            )
            add(
                item(
                    "Next Chapter",
                    appShortcut(AwtKeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK or InputEvent.SHIFT_DOWN_MASK),
                ) {
                    onCommand(ChimahonDesktopCommand.ReaderNextChapter)
                },
            )
            addSeparator()
            add(item("Toggle Controls", appShortcut(AwtKeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderToggleControls)
            })
            add(item("Back / Close Reader Panel", shortcut(AwtKeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.Back)
            })
            addSeparator()
            add(item("Cycle Page/Webtoon Mode", appShortcut(AwtKeyEvent.VK_M, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderCycleMode)
            })
            add(item("Open Reader Settings", appShortcut(AwtKeyEvent.VK_COMMA, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderOpenSettings)
            })
            add(item("Open Chapter List", appShortcut(AwtKeyEvent.VK_C, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderOpenChapters)
            })
            add(item("Toggle Reader Stats", appShortcut(AwtKeyEvent.VK_I, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderToggleStats)
            })
            add(item("Toggle Crop Borders", appShortcut(AwtKeyEvent.VK_F, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderToggleCrop)
            })
            add(item("Cycle Orientation", appShortcut(AwtKeyEvent.VK_T, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderCycleOrientation)
            })
            add(item("Cycle Page Layout", appShortcut(AwtKeyEvent.VK_L, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderCyclePageLayout)
            })
            add(item("Shift Double Pages", appShortcut(AwtKeyEvent.VK_P, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderShiftDoublePages)
            })
            addSeparator()
            add(item("Bookmark Chapter", appShortcut(AwtKeyEvent.VK_B, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderBookmarkChapter)
            })
            add(item("Download Chapter", appShortcut(AwtKeyEvent.VK_D, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderDownloadChapter)
            })
            add(item("Mark Chapter Read", appShortcut(AwtKeyEvent.VK_R, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderMarkChapterRead)
            })
            add(item("Open Chapter URL", appShortcut(AwtKeyEvent.VK_O, InputEvent.ALT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.ReaderOpenChapterUrl)
            })
            add(
                item(
                    "Share Chapter URL",
                    appShortcut(AwtKeyEvent.VK_S, InputEvent.ALT_DOWN_MASK or InputEvent.SHIFT_DOWN_MASK),
                ) {
                    onCommand(ChimahonDesktopCommand.ReaderShareChapter)
                },
            )
        })
        add(menu("Player", AwtKeyEvent.VK_P) {
            add(item("Close Player", shortcut(AwtKeyEvent.VK_ESCAPE)) {
                onCommand(ChimahonDesktopCommand.Back)
            })
            addSeparator()
            add(
                placeholderItem(
                    surface = "Player",
                    title = "Play / Pause",
                    shortcut = appShortcut(AwtKeyEvent.VK_SPACE, InputEvent.SHIFT_DOWN_MASK),
                    shortcutLabel = "$appShortcutLabel+Shift+Space",
                ),
            )
            add(
                placeholderItem(
                    surface = "Player",
                    title = "Seek Backward",
                    shortcut = appShortcut(AwtKeyEvent.VK_J, InputEvent.SHIFT_DOWN_MASK),
                    shortcutLabel = "$appShortcutLabel+Shift+J",
                ),
            )
            add(
                placeholderItem(
                    surface = "Player",
                    title = "Seek Forward",
                    shortcut = appShortcut(AwtKeyEvent.VK_L, InputEvent.SHIFT_DOWN_MASK),
                    shortcutLabel = "$appShortcutLabel+Shift+L",
                ),
            )
            addSeparator()
            add(
                placeholderItem(
                    surface = "Player",
                    title = "Previous Episode",
                    shortcut = appShortcut(AwtKeyEvent.VK_OPEN_BRACKET, InputEvent.SHIFT_DOWN_MASK),
                    shortcutLabel = "$appShortcutLabel+Shift+[",
                ),
            )
            add(
                placeholderItem(
                    surface = "Player",
                    title = "Next Episode",
                    shortcut = appShortcut(AwtKeyEvent.VK_CLOSE_BRACKET, InputEvent.SHIFT_DOWN_MASK),
                    shortcutLabel = "$appShortcutLabel+Shift+]",
                ),
            )
            addSeparator()
            add(
                placeholderItem(
                    surface = "Player",
                    title = "Subtitle Settings",
                    shortcut = appShortcut(AwtKeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK),
                    shortcutLabel = "$appShortcutLabel+Shift+S",
                ),
            )
            add(
                placeholderItem(
                    surface = "Player",
                    title = "Audio Delay",
                    shortcut = appShortcut(AwtKeyEvent.VK_Y, InputEvent.SHIFT_DOWN_MASK),
                    shortcutLabel = "$appShortcutLabel+Shift+Y",
                ),
            )
            add(
                placeholderItem(
                    surface = "Player",
                    title = "Video Filters",
                    shortcut = appShortcut(AwtKeyEvent.VK_V, InputEvent.SHIFT_DOWN_MASK),
                    shortcutLabel = "$appShortcutLabel+Shift+V",
                ),
            )
            add(item("Player Settings", appShortcut(AwtKeyEvent.VK_COMMA, InputEvent.SHIFT_DOWN_MASK)) {
                onCommand(ChimahonDesktopCommand.Settings)
            })
        })
        add(menu("Window", AwtKeyEvent.VK_W) {
            add(item("Minimize") { window.minimize() })
            add(item("Toggle Maximize", shortcut(AwtKeyEvent.VK_F11)) { window.toggleMaximized() })
        })
        add(menu("Help", AwtKeyEvent.VK_H) {
            add(item("Copy Keyboard Shortcuts") {
                DesktopPlatformAffordances.copyTextToClipboard(desktopShortcutReference())
            })
            add(item("Share Keyboard Shortcuts") {
                DesktopPlatformAffordances.shareText(
                    text = desktopShortcutReference(),
                    title = "Chimahon keyboard shortcuts",
                )
            })
        })
    }
}

private fun installDesktopInputBridge(
    window: java.awt.Window,
    onCommand: (ChimahonDesktopCommand) -> Unit,
): AutoCloseable {
    val keyListener = AWTEventListener { event ->
        val keyEvent = event as? AwtKeyEvent ?: return@AWTEventListener
        if (keyEvent.id != AwtKeyEvent.KEY_PRESSED) return@AWTEventListener
        if (!keyEvent.belongsTo(window)) return@AWTEventListener

        val command = keyEvent.desktopCommandOrNull() ?: return@AWTEventListener
        onCommand(command)
        keyEvent.consume()
    }

    val mouseListener = AWTEventListener { event ->
        val mouseEvent = event as? AwtMouseEvent ?: return@AWTEventListener
        if (mouseEvent.id != AwtMouseEvent.MOUSE_PRESSED) return@AWTEventListener
        if (!mouseEvent.belongsTo(window)) return@AWTEventListener

        val command = when {
            mouseEvent.button == 4 && mouseEvent.isShiftDown -> ChimahonDesktopCommand.ReaderPreviousChapter
            mouseEvent.button == 5 && mouseEvent.isShiftDown -> ChimahonDesktopCommand.ReaderNextChapter
            mouseEvent.button == 4 -> ChimahonDesktopCommand.ReaderPreviousPage
            mouseEvent.button == 5 -> ChimahonDesktopCommand.ReaderNextPage
            else -> null
        } ?: return@AWTEventListener

        onCommand(command)
        mouseEvent.consume()
    }

    Toolkit.getDefaultToolkit().addAWTEventListener(keyListener, AWTEvent.KEY_EVENT_MASK)
    Toolkit.getDefaultToolkit().addAWTEventListener(mouseListener, AWTEvent.MOUSE_EVENT_MASK)
    return AutoCloseable {
        Toolkit.getDefaultToolkit().removeAWTEventListener(keyListener)
        Toolkit.getDefaultToolkit().removeAWTEventListener(mouseListener)
    }
}

private fun AwtKeyEvent.desktopCommandOrNull(): ChimahonDesktopCommand? {
    val menuShortcutDown = isMenuShortcutDown()
    val altDown = isAltDown
    val shiftDown = isShiftDown

    return when {
        keyCode == AwtKeyEvent.VK_ESCAPE && !menuShortcutDown && !altDown -> ChimahonDesktopCommand.Back
        keyCode == AwtKeyEvent.VK_F5 && !menuShortcutDown && !altDown -> ChimahonDesktopCommand.Refresh
        keyCode == AwtKeyEvent.VK_LEFT && altDown && !menuShortcutDown && !shiftDown -> ChimahonDesktopCommand.Back

        keyCode == AwtKeyEvent.VK_F && menuShortcutDown && !altDown && shiftDown ->
            ChimahonDesktopCommand.ToggleFilters
        keyCode == AwtKeyEvent.VK_F && menuShortcutDown && !altDown -> ChimahonDesktopCommand.Search
        keyCode == AwtKeyEvent.VK_R && menuShortcutDown && !altDown && !shiftDown ->
            ChimahonDesktopCommand.Refresh

        menuShortcutDown && !altDown && !shiftDown && keyCode in setOf(AwtKeyEvent.VK_1, AwtKeyEvent.VK_L) ->
            ChimahonDesktopCommand.Library
        menuShortcutDown && !altDown && !shiftDown && keyCode in setOf(AwtKeyEvent.VK_2, AwtKeyEvent.VK_U) ->
            ChimahonDesktopCommand.Updates
        menuShortcutDown && !altDown && !shiftDown && keyCode in setOf(AwtKeyEvent.VK_3, AwtKeyEvent.VK_H) ->
            ChimahonDesktopCommand.History
        menuShortcutDown && !altDown && !shiftDown && keyCode in setOf(AwtKeyEvent.VK_4, AwtKeyEvent.VK_B) ->
            ChimahonDesktopCommand.BrowseSources
        menuShortcutDown && !altDown && !shiftDown && keyCode in setOf(AwtKeyEvent.VK_5, AwtKeyEvent.VK_M) ->
            ChimahonDesktopCommand.More
        keyCode == AwtKeyEvent.VK_E && menuShortcutDown && !altDown && !shiftDown ->
            ChimahonDesktopCommand.BrowseExtensions
        keyCode == AwtKeyEvent.VK_B && menuShortcutDown && !altDown && shiftDown ->
            ChimahonDesktopCommand.BrowseFeed
        keyCode == AwtKeyEvent.VK_M && menuShortcutDown && !altDown && shiftDown ->
            ChimahonDesktopCommand.BrowseMigrate
        keyCode == AwtKeyEvent.VK_COMMA && menuShortcutDown && !altDown ->
            ChimahonDesktopCommand.Settings
        keyCode == AwtKeyEvent.VK_D && menuShortcutDown && !altDown && !shiftDown ->
            ChimahonDesktopCommand.DownloadQueue

        keyCode == AwtKeyEvent.VK_LEFT && menuShortcutDown && altDown && shiftDown ->
            ChimahonDesktopCommand.ReaderPreviousChapter
        keyCode == AwtKeyEvent.VK_RIGHT && menuShortcutDown && altDown && shiftDown ->
            ChimahonDesktopCommand.ReaderNextChapter
        keyCode == AwtKeyEvent.VK_LEFT && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderPreviousPage
        keyCode == AwtKeyEvent.VK_RIGHT && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderNextPage
        keyCode == AwtKeyEvent.VK_HOME && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderFirstPage
        keyCode == AwtKeyEvent.VK_END && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderLastPage
        keyCode == AwtKeyEvent.VK_ENTER && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderToggleControls
        keyCode == AwtKeyEvent.VK_M && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderCycleMode
        keyCode == AwtKeyEvent.VK_COMMA && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderOpenSettings
        keyCode == AwtKeyEvent.VK_C && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderOpenChapters
        keyCode == AwtKeyEvent.VK_I && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderToggleStats
        keyCode == AwtKeyEvent.VK_F && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderToggleCrop
        keyCode == AwtKeyEvent.VK_T && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderCycleOrientation
        keyCode == AwtKeyEvent.VK_L && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderCyclePageLayout
        keyCode == AwtKeyEvent.VK_P && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderShiftDoublePages
        keyCode == AwtKeyEvent.VK_B && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderBookmarkChapter
        keyCode == AwtKeyEvent.VK_D && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderDownloadChapter
        keyCode == AwtKeyEvent.VK_R && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderMarkChapterRead
        keyCode == AwtKeyEvent.VK_O && menuShortcutDown && altDown ->
            ChimahonDesktopCommand.ReaderOpenChapterUrl
        keyCode == AwtKeyEvent.VK_S && menuShortcutDown && altDown && shiftDown ->
            ChimahonDesktopCommand.ReaderShareChapter

        else -> null
    }
}

private fun AwtKeyEvent.isMenuShortcutDown(): Boolean {
    return if (DesktopPlatformAffordances.menuShortcutUsesMeta) isMetaDown else isControlDown
}

private fun AwtKeyEvent.belongsTo(window: java.awt.Window): Boolean {
    val eventComponent = component ?: return false
    return eventComponent == window || SwingUtilities.getWindowAncestor(eventComponent) == window
}

private fun AwtMouseEvent.belongsTo(window: java.awt.Window): Boolean {
    val eventComponent = component ?: return false
    return eventComponent == window || SwingUtilities.getWindowAncestor(eventComponent) == window
}

private fun java.awt.Window.minimize() {
    (this as? Frame)?.state = Frame.ICONIFIED
}

private fun java.awt.Window.toggleMaximized() {
    val frame = this as? Frame ?: return
    val isMaximized = (frame.extendedState and Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH
    frame.extendedState = if (isMaximized) {
        frame.extendedState and Frame.MAXIMIZED_BOTH.inv()
    } else {
        frame.extendedState or Frame.MAXIMIZED_BOTH
    }
}

private fun desktopShortcutReference(): String {
    val shortcut = DesktopPlatformAffordances.menuShortcutLabel
    return """
        Chimahon desktop shortcuts

        App
        Back: Esc or Alt+Left
        Search: $shortcut+F
        Toggle filters: $shortcut+Shift+F
        Refresh: F5 or $shortcut+R
        Library: $shortcut+1 or $shortcut+L
        Updates: $shortcut+2 or $shortcut+U
        History: $shortcut+3 or $shortcut+H
        Browse sources: $shortcut+4 or $shortcut+B
        Browse extensions: $shortcut+E
        Browse feed: $shortcut+Shift+B
        Migrate: $shortcut+Shift+M
        More: $shortcut+5 or $shortcut+M
        Settings: $shortcut+Comma
        Download queue: $shortcut+D

        Anime
        Anime library/updates/history: $shortcut+Alt+A/U/H
        Browse anime sources/extensions: $shortcut+Alt+Shift+A/E
        Anime download queue: $shortcut+Alt+Shift+Q
        Open anime details: $shortcut+Shift+I
        Episode list: $shortcut+Shift+G
        Track anime: $shortcut+Shift+T

        Reader
        Previous/next page: Left/Right, Page Up/Page Down, or $shortcut+Alt+Left/Right
        First/last page: Home/End or $shortcut+Alt+Home/End
        Previous/next chapter: $shortcut+Alt+Shift+Left/Right
        Toggle controls: Enter or $shortcut+Alt+Enter
        Cycle reading mode: M or $shortcut+Alt+M
        Reader settings: S or $shortcut+Alt+Comma
        Chapter list: C or $shortcut+Alt+C
        Stats: I or $shortcut+Alt+I
        Crop borders: F or $shortcut+Alt+F
        Bookmark chapter: B or $shortcut+Alt+B
        Download chapter: D or $shortcut+Alt+D
        Mark chapter read: R or $shortcut+Alt+R
        Open chapter URL: O or $shortcut+Alt+O
        Share chapter URL: $shortcut+Alt+Shift+S

        Player placeholders
        Close player: Esc
        Play/pause: $shortcut+Shift+Space
        Seek backward/forward: $shortcut+Shift+J/L
        Previous/next episode: $shortcut+Shift+[/]
        Subtitle settings: $shortcut+Shift+S
        Audio delay: $shortcut+Shift+Y
        Video filters: $shortcut+Shift+V
        Player settings: $shortcut+Shift+Comma

        Mouse
        Back/forward mouse buttons: reader previous/next page
        Shift+back/forward mouse buttons: reader previous/next chapter
    """.trimIndent()
}

@Composable
private fun DesktopStartupStatus(
    title: String,
    detail: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBFE))
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            BasicText(
                text = title,
                style = TextStyle(
                    color = Color(0xFF1D1B20),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            BasicText(
                text = detail,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .widthIn(max = 460.dp),
                style = TextStyle(
                    color = Color(0xFF625B66),
                    fontSize = 13.sp,
                ),
            )
        }
    }
}
