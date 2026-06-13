package chimahon.desktop

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun main() = application {
    var services by remember { mutableStateOf<ChimahonSharedAppServices?>(null) }
    var startupError by remember { mutableStateOf<String?>(null) }

    Window(
        onCloseRequest = {
            services?.close()
            exitApplication()
        },
        state = WindowState(width = 1180.dp, height = 740.dp),
        title = "Chimahon",
    ) {
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

        when {
            services != null -> ChimahonDesktopApp(services = checkNotNull(services))
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
