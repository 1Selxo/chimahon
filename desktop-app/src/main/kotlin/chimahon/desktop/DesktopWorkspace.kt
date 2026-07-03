package chimahon.desktop

import app.chimahon.shared.ChimahonServiceApp
import app.chimahon.shared.ChimahonDesktopCommandRequest
import app.chimahon.shared.ChimahonSharedAppServices
import androidx.compose.runtime.Composable

@Composable
fun ChimahonDesktopApp(
    services: ChimahonSharedAppServices,
    desktopCommandRequest: ChimahonDesktopCommandRequest?,
    onDesktopCommandHandled: (Long) -> Unit,
    onReaderActiveChanged: (Boolean) -> Unit = {},
) {
    ChimahonServiceApp(
        services = services,
        desktopCommandRequest = desktopCommandRequest,
        onDesktopCommandHandled = onDesktopCommandHandled,
        onReaderActiveChanged = onReaderActiveChanged,
    )
}
