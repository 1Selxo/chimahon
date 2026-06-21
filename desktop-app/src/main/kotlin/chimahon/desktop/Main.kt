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
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.LocalWindow
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
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
            val window = LocalWindow.current
            DisposableEffect(window) {
                DesktopPlatformAffordances.configureWindow(window)
                onDispose {}
            }

            DesktopMenuBar(
                onCommand = dispatchDesktopCommand,
                onQuit = closeApplication,
            )

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

@Composable
private fun DesktopMenuBar(
    onCommand: (ChimahonDesktopCommand) -> Unit,
    onQuit: () -> Unit,
) {
    val usesMeta = DesktopPlatformAffordances.menuShortcutUsesMeta
    fun appShortcut(key: Key): KeyShortcut = KeyShortcut(key, ctrl = !usesMeta, meta = usesMeta)

    MenuBar {
        Menu("File", mnemonic = 'F') {
            Item("Open Downloads Folder", onClick = {
                DesktopPlatformAffordances.openDirectory(DesktopDirectory.Downloads)
            })
            Item("Open Data Folder", onClick = {
                DesktopPlatformAffordances.openDirectory(DesktopDirectory.Files)
            })
            Item("Open Cache Folder", onClick = {
                DesktopPlatformAffordances.openDirectory(DesktopDirectory.Cache)
            })
            Separator()
            Item(
                "Quit",
                shortcut = KeyShortcut(Key.Q, ctrl = !usesMeta, meta = usesMeta),
                onClick = onQuit,
            )
        }
        Menu("Navigate", mnemonic = 'N') {
            Item(
                "Back (Esc)",
                onClick = { onCommand(ChimahonDesktopCommand.Back) },
            )
            Separator()
            Item(
                "Library",
                shortcut = appShortcut(Key.L),
                onClick = { onCommand(ChimahonDesktopCommand.Library) },
            )
            Item(
                "Updates",
                shortcut = appShortcut(Key.U),
                onClick = { onCommand(ChimahonDesktopCommand.Updates) },
            )
            Item(
                "History",
                shortcut = appShortcut(Key.H),
                onClick = { onCommand(ChimahonDesktopCommand.History) },
            )
            Item(
                "Browse Sources",
                shortcut = appShortcut(Key.B),
                onClick = { onCommand(ChimahonDesktopCommand.BrowseSources) },
            )
            Item(
                "Browse Extensions",
                shortcut = appShortcut(Key.E),
                onClick = { onCommand(ChimahonDesktopCommand.BrowseExtensions) },
            )
            Item(
                "Browse Feed",
                onClick = { onCommand(ChimahonDesktopCommand.BrowseFeed) },
            )
            Item(
                "Migrate",
                onClick = { onCommand(ChimahonDesktopCommand.BrowseMigrate) },
            )
            Item(
                "More",
                shortcut = appShortcut(Key.M),
                onClick = { onCommand(ChimahonDesktopCommand.More) },
            )
            Separator()
            Item(
                "Settings",
                shortcut = appShortcut(Key.S),
                onClick = { onCommand(ChimahonDesktopCommand.Settings) },
            )
            Item(
                "Download Queue",
                shortcut = appShortcut(Key.D),
                onClick = { onCommand(ChimahonDesktopCommand.DownloadQueue) },
            )
        }
        Menu("Edit", mnemonic = 'E') {
            Item(
                "Search",
                shortcut = appShortcut(Key.F),
                onClick = { onCommand(ChimahonDesktopCommand.Search) },
            )
            Item(
                "Toggle Filters",
                shortcut = KeyShortcut(Key.F, ctrl = !usesMeta, meta = usesMeta, shift = true),
                onClick = { onCommand(ChimahonDesktopCommand.ToggleFilters) },
            )
            Item(
                "Refresh",
                shortcut = appShortcut(Key.R),
                onClick = { onCommand(ChimahonDesktopCommand.Refresh) },
            )
            Item(
                "Refresh (F5)",
                shortcut = KeyShortcut(Key.F5),
                onClick = { onCommand(ChimahonDesktopCommand.Refresh) },
            )
            Separator()
            Item("Copy Downloads Path", onClick = {
                DesktopPlatformAffordances.copyDirectoryPath(DesktopDirectory.Downloads)
            })
            Item("Copy Data Path", onClick = {
                DesktopPlatformAffordances.copyDirectoryPath(DesktopDirectory.Files)
            })
            Item("Copy Cache Path", onClick = {
                DesktopPlatformAffordances.copyDirectoryPath(DesktopDirectory.Cache)
            })
        }
        Menu("Reader", mnemonic = 'R') {
            Item("Previous Page", onClick = { onCommand(ChimahonDesktopCommand.ReaderPreviousPage) })
            Item("Next Page", onClick = { onCommand(ChimahonDesktopCommand.ReaderNextPage) })
            Item("First Page", onClick = { onCommand(ChimahonDesktopCommand.ReaderFirstPage) })
            Item("Last Page", onClick = { onCommand(ChimahonDesktopCommand.ReaderLastPage) })
            Separator()
            Item("Previous Chapter", onClick = { onCommand(ChimahonDesktopCommand.ReaderPreviousChapter) })
            Item("Next Chapter", onClick = { onCommand(ChimahonDesktopCommand.ReaderNextChapter) })
            Separator()
            Item("Toggle Controls", onClick = { onCommand(ChimahonDesktopCommand.ReaderToggleControls) })
            Item("Back / Close Reader Panel", onClick = { onCommand(ChimahonDesktopCommand.Back) })
            Separator()
            Item("Cycle Page/Webtoon Mode", onClick = { onCommand(ChimahonDesktopCommand.ReaderCycleMode) })
            Item("Open Reader Settings", onClick = { onCommand(ChimahonDesktopCommand.ReaderOpenSettings) })
            Item("Open Chapter List", onClick = { onCommand(ChimahonDesktopCommand.ReaderOpenChapters) })
            Item("Toggle Reader Stats", onClick = { onCommand(ChimahonDesktopCommand.ReaderToggleStats) })
            Item("Toggle Crop Borders", onClick = { onCommand(ChimahonDesktopCommand.ReaderToggleCrop) })
            Separator()
            Item("Bookmark Chapter", onClick = { onCommand(ChimahonDesktopCommand.ReaderBookmarkChapter) })
            Item("Download Chapter", onClick = { onCommand(ChimahonDesktopCommand.ReaderDownloadChapter) })
            Item("Mark Chapter Read", onClick = { onCommand(ChimahonDesktopCommand.ReaderMarkChapterRead) })
            Item("Open Chapter URL", onClick = { onCommand(ChimahonDesktopCommand.ReaderOpenChapterUrl) })
        }
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
