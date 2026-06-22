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

    return JMenuBar().apply {
        add(menu("File", AwtKeyEvent.VK_F) {
            add(item("Open Downloads Folder") { DesktopPlatformAffordances.openDirectory(DesktopDirectory.Downloads) })
            add(item("Open Data Folder") { DesktopPlatformAffordances.openDirectory(DesktopDirectory.Files) })
            add(item("Open Cache Folder") { DesktopPlatformAffordances.openDirectory(DesktopDirectory.Cache) })
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
            add(item("Settings", appShortcut(AwtKeyEvent.VK_S)) { onCommand(ChimahonDesktopCommand.Settings) })
            add(item("Download Queue", appShortcut(AwtKeyEvent.VK_D)) {
                onCommand(ChimahonDesktopCommand.DownloadQueue)
            })
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
            add(item("Copy Downloads Path") {
                DesktopPlatformAffordances.copyDirectoryPath(DesktopDirectory.Downloads)
            })
            add(item("Copy Data Path") { DesktopPlatformAffordances.copyDirectoryPath(DesktopDirectory.Files) })
            add(item("Copy Cache Path") { DesktopPlatformAffordances.copyDirectoryPath(DesktopDirectory.Cache) })
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
    }
}

private fun installDesktopInputBridge(
    window: java.awt.Window,
    onCommand: (ChimahonDesktopCommand) -> Unit,
): AutoCloseable {
    val mouseListener = AWTEventListener { event ->
        val mouseEvent = event as? AwtMouseEvent ?: return@AWTEventListener
        if (mouseEvent.id != AwtMouseEvent.MOUSE_PRESSED) return@AWTEventListener
        if (!mouseEvent.belongsTo(window)) return@AWTEventListener

        val command = when (mouseEvent.button) {
            4 -> ChimahonDesktopCommand.ReaderPreviousPage
            5 -> ChimahonDesktopCommand.ReaderNextPage
            else -> null
        } ?: return@AWTEventListener

        onCommand(command)
        mouseEvent.consume()
    }

    Toolkit.getDefaultToolkit().addAWTEventListener(mouseListener, AWTEvent.MOUSE_EVENT_MASK)
    return AutoCloseable {
        Toolkit.getDefaultToolkit().removeAWTEventListener(mouseListener)
    }
}

private fun AwtMouseEvent.belongsTo(window: java.awt.Window): Boolean {
    val eventComponent = component ?: return false
    return eventComponent == window || SwingUtilities.getWindowAncestor(eventComponent) == window
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
