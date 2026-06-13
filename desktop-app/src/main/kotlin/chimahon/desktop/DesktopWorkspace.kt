package chimahon.desktop

import app.chimahon.shared.ChimahonServiceApp
import app.chimahon.shared.ChimahonSharedAppServices
import androidx.compose.runtime.Composable

@Composable
fun ChimahonDesktopApp(services: ChimahonSharedAppServices) {
    ChimahonServiceApp(services)
}
