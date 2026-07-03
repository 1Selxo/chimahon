package app.chimahon.shared.readerocrui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector

val ChimahonReaderOcrAction.imageVector: ImageVector
    get() = when (this) {
        ChimahonReaderOcrAction.ToggleLookup -> Icons.Outlined.Search
        ChimahonReaderOcrAction.Rescan -> Icons.Outlined.Refresh
        ChimahonReaderOcrAction.ToggleBoxes -> Icons.Outlined.Visibility
        ChimahonReaderOcrAction.PreviousMatch -> Icons.Outlined.ArrowBack
        ChimahonReaderOcrAction.NextMatch -> Icons.Outlined.ArrowForward
        ChimahonReaderOcrAction.CopyText -> Icons.Outlined.ContentCopy
        ChimahonReaderOcrAction.SearchWeb -> Icons.Outlined.Search
        ChimahonReaderOcrAction.Settings -> Icons.Outlined.Settings
        ChimahonReaderOcrAction.Close -> Icons.Outlined.Close
    }

val ChimahonReaderOcrBoxMode.imageVector: ImageVector
    get() = when (this) {
        ChimahonReaderOcrBoxMode.Blocks -> Icons.Outlined.Visibility
        ChimahonReaderOcrBoxMode.Lines -> Icons.Outlined.Visibility
        ChimahonReaderOcrBoxMode.Words -> Icons.Outlined.Visibility
    }

val ChimahonReaderOcrEngine.imageVector: ImageVector
    get() = when (this) {
        ChimahonReaderOcrEngine.Glens -> Icons.Outlined.Search
        ChimahonReaderOcrEngine.Platform -> Icons.Outlined.Public
        ChimahonReaderOcrEngine.Manual -> Icons.Outlined.VisibilityOff
    }
