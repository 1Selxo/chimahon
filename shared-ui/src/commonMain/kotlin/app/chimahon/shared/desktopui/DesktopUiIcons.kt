package app.chimahon.shared.desktopui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Mouse
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.ViewColumn
import androidx.compose.ui.graphics.vector.ImageVector

enum class ChimahonDesktopIcon {
    Back,
    Browser,
    Chapters,
    Copy,
    Crop,
    Delete,
    Download,
    Extension,
    Filter,
    Help,
    Info,
    Keyboard,
    More,
    Mouse,
    Next,
    Ocr,
    PageLayout,
    Previous,
    Public,
    Refresh,
    Search,
    Settings,
    Share,
    Stats,
    Swap,
    Touchpad,
}

val ChimahonDesktopIcon.imageVector: ImageVector
    get() = when (this) {
        ChimahonDesktopIcon.Back -> Icons.Outlined.ArrowBack
        ChimahonDesktopIcon.Browser -> Icons.Outlined.Public
        ChimahonDesktopIcon.Chapters -> Icons.Outlined.FormatListNumbered
        ChimahonDesktopIcon.Copy -> Icons.Outlined.ContentCopy
        ChimahonDesktopIcon.Crop -> Icons.Outlined.CropFree
        ChimahonDesktopIcon.Delete -> Icons.Outlined.DeleteOutline
        ChimahonDesktopIcon.Download -> Icons.Outlined.Download
        ChimahonDesktopIcon.Extension -> Icons.Outlined.Extension
        ChimahonDesktopIcon.Filter -> Icons.Outlined.FilterList
        ChimahonDesktopIcon.Help -> Icons.Outlined.HelpOutline
        ChimahonDesktopIcon.Info -> Icons.Outlined.Info
        ChimahonDesktopIcon.Keyboard -> Icons.Outlined.Keyboard
        ChimahonDesktopIcon.More -> Icons.Outlined.MoreHoriz
        ChimahonDesktopIcon.Mouse -> Icons.Outlined.Mouse
        ChimahonDesktopIcon.Next -> Icons.Outlined.SkipNext
        ChimahonDesktopIcon.Ocr -> Icons.Outlined.Search
        ChimahonDesktopIcon.PageLayout -> Icons.Outlined.ViewColumn
        ChimahonDesktopIcon.Previous -> Icons.Outlined.SkipPrevious
        ChimahonDesktopIcon.Public -> Icons.Outlined.Public
        ChimahonDesktopIcon.Refresh -> Icons.Outlined.Refresh
        ChimahonDesktopIcon.Search -> Icons.Outlined.Search
        ChimahonDesktopIcon.Settings -> Icons.Outlined.Settings
        ChimahonDesktopIcon.Share -> Icons.Outlined.Share
        ChimahonDesktopIcon.Stats -> Icons.Outlined.QueryStats
        ChimahonDesktopIcon.Swap -> Icons.Outlined.SwapHoriz
        ChimahonDesktopIcon.Touchpad -> Icons.Outlined.TouchApp
    }

fun ChimahonDesktopInputDevice.icon(): ChimahonDesktopIcon {
    return when (this) {
        ChimahonDesktopInputDevice.Keyboard -> ChimahonDesktopIcon.Keyboard
        ChimahonDesktopInputDevice.Mouse -> ChimahonDesktopIcon.Mouse
        ChimahonDesktopInputDevice.Touchpad -> ChimahonDesktopIcon.Touchpad
        ChimahonDesktopInputDevice.Gamepad -> ChimahonDesktopIcon.Extension
    }
}
