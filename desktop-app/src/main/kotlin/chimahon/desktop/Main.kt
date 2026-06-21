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
import java.awt.Toolkit
import java.awt.event.KeyEvent as AwtKeyEvent
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.KeyStroke
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
                window.jMenuBar = createDesktopMenuBar(
                    onCommand = dispatchDesktopCommand,
                    onQuit = closeApplication,
                )
                onDispose {
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
    fun appShortcut(keyCode: Int): KeyStroke =
        KeyStroke.getKeyStroke(keyCode, Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx)

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
            add(item("Back") { onCommand(ChimahonDesktopCommand.Back) })
            addSeparator()
            add(item("Library", appShortcut(AwtKeyEvent.VK_L)) { onCommand(ChimahonDesktopCommand.Library) })
            add(item("Updates", appShortcut(AwtKeyEvent.VK_U)) { onCommand(ChimahonDesktopCommand.Updates) })
            add(item("History", appShortcut(AwtKeyEvent.VK_H)) { onCommand(ChimahonDesktopCommand.History) })
            add(item("Browse Sources", appShortcut(AwtKeyEvent.VK_B)) { onCommand(ChimahonDesktopCommand.BrowseSources) })
            add(item("Browse Extensions", appShortcut(AwtKeyEvent.VK_E)) { onCommand(ChimahonDesktopCommand.BrowseExtensions) })
            add(item("Browse Feed") { onCommand(ChimahonDesktopCommand.BrowseFeed) })
            add(item("Migrate") { onCommand(ChimahonDesktopCommand.BrowseMigrate) })
            add(item("More", appShortcut(AwtKeyEvent.VK_M)) { onCommand(ChimahonDesktopCommand.More) })
            addSeparator()
            add(item("Settings", appShortcut(AwtKeyEvent.VK_S)) { onCommand(ChimahonDesktopCommand.Settings) })
            add(item("Download Queue", appShortcut(AwtKeyEvent.VK_D)) { onCommand(ChimahonDesktopCommand.DownloadQueue) })
        })
        add(menu("Edit", AwtKeyEvent.VK_E) {
            add(item("Search", appShortcut(AwtKeyEvent.VK_F)) { onCommand(ChimahonDesktopCommand.Search) })
            add(
                item(
                    "Toggle Filters",
                    KeyStroke.getKeyStroke(
                        AwtKeyEvent.VK_F,
                        Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx or java.awt.event.InputEvent.SHIFT_DOWN_MASK,
                    ),
                ) { onCommand(ChimahonDesktopCommand.ToggleFilters) },
            )
            add(item("Refresh", appShortcut(AwtKeyEvent.VK_R)) { onCommand(ChimahonDesktopCommand.Refresh) })
            add(item("Refresh (F5)", KeyStroke.getKeyStroke(AwtKeyEvent.VK_F5, 0)) {
                onCommand(ChimahonDesktopCommand.Refresh)
            })
            addSeparator()
            add(item("Copy Downloads Path") { DesktopPlatformAffordances.copyDirectoryPath(DesktopDirectory.Downloads) })
            add(item("Copy Data Path") { DesktopPlatformAffordances.copyDirectoryPath(DesktopDirectory.Files) })
            add(item("Copy Cache Path") { DesktopPlatformAffordances.copyDirectoryPath(DesktopDirectory.Cache) })
        })
        add(menu("Reader", AwtKeyEvent.VK_R) {
            add(item("Previous Page") { onCommand(ChimahonDesktopCommand.ReaderPreviousPage) })
            add(item("Next Page") { onCommand(ChimahonDesktopCommand.ReaderNextPage) })
            add(item("First Page") { onCommand(ChimahonDesktopCommand.ReaderFirstPage) })
            add(item("Last Page") { onCommand(ChimahonDesktopCommand.ReaderLastPage) })
            addSeparator()
            add(item("Previous Chapter") { onCommand(ChimahonDesktopCommand.ReaderPreviousChapter) })
            add(item("Next Chapter") { onCommand(ChimahonDesktopCommand.ReaderNextChapter) })
            addSeparator()
            add(item("Toggle Controls") { onCommand(ChimahonDesktopCommand.ReaderToggleControls) })
            add(item("Back / Close Reader Panel") { onCommand(ChimahonDesktopCommand.Back) })
            addSeparator()
            add(item("Cycle Page/Webtoon Mode") { onCommand(ChimahonDesktopCommand.ReaderCycleMode) })
            add(item("Open Reader Settings") { onCommand(ChimahonDesktopCommand.ReaderOpenSettings) })
            add(item("Open Chapter List") { onCommand(ChimahonDesktopCommand.ReaderOpenChapters) })
            add(item("Toggle Reader Stats") { onCommand(ChimahonDesktopCommand.ReaderToggleStats) })
            add(item("Toggle Crop Borders") { onCommand(ChimahonDesktopCommand.ReaderToggleCrop) })
            addSeparator()
            add(item("Bookmark Chapter") { onCommand(ChimahonDesktopCommand.ReaderBookmarkChapter) })
            add(item("Download Chapter") { onCommand(ChimahonDesktopCommand.ReaderDownloadChapter) })
            add(item("Mark Chapter Read") { onCommand(ChimahonDesktopCommand.ReaderMarkChapterRead) })
            add(item("Open Chapter URL") { onCommand(ChimahonDesktopCommand.ReaderOpenChapterUrl) })
        })
    }
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
