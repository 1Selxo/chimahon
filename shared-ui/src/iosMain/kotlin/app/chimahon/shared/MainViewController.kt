package app.chimahon.shared

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIColor
import platform.UIKit.UIViewController

@Suppress("FunctionName")
fun MainViewController(): UIViewController = ComposeUIViewController {
    val services = remember { ChimahonSharedAppServices() }
    DisposableEffect(services) {
        onDispose {
            services.close()
        }
    }
    ChimahonServiceApp(services)
}.apply {
    view.backgroundColor = UIColor.whiteColor
}
