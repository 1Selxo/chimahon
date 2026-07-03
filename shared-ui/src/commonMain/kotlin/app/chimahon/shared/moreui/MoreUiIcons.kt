package app.chimahon.shared.moreui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.ui.graphics.vector.ImageVector

val ChimahonMoreIcon.imageVector: ImageVector
    get() = when (this) {
        ChimahonMoreIcon.About -> Icons.Outlined.Info
        ChimahonMoreIcon.Anime -> Icons.Outlined.LibraryBooks
        ChimahonMoreIcon.Backup -> Icons.Outlined.Backup
        ChimahonMoreIcon.Categories -> Icons.Outlined.LibraryBooks
        ChimahonMoreIcon.Check -> Icons.Outlined.CheckCircle
        ChimahonMoreIcon.Chevron -> Icons.Outlined.ArrowForward
        ChimahonMoreIcon.Delete -> Icons.Outlined.DeleteOutline
        ChimahonMoreIcon.Desktop -> Icons.Outlined.Settings
        ChimahonMoreIcon.Download -> Icons.Outlined.Download
        ChimahonMoreIcon.Edit -> Icons.Outlined.Edit
        ChimahonMoreIcon.Extension -> Icons.Outlined.Extension
        ChimahonMoreIcon.Help -> Icons.Outlined.HelpOutline
        ChimahonMoreIcon.History -> Icons.Outlined.Sync
        ChimahonMoreIcon.Info -> Icons.Outlined.Info
        ChimahonMoreIcon.Library -> Icons.Outlined.LibraryBooks
        ChimahonMoreIcon.Link -> Icons.Outlined.Link
        ChimahonMoreIcon.Novel -> Icons.Outlined.MenuBook
        ChimahonMoreIcon.Palette -> Icons.Outlined.Palette
        ChimahonMoreIcon.Player -> Icons.Outlined.Settings
        ChimahonMoreIcon.Queue -> Icons.Outlined.Download
        ChimahonMoreIcon.Reader -> Icons.Outlined.MenuBook
        ChimahonMoreIcon.Repository -> Icons.Outlined.Storage
        ChimahonMoreIcon.Restore -> Icons.Outlined.Restore
        ChimahonMoreIcon.Search -> Icons.Outlined.Search
        ChimahonMoreIcon.Security -> Icons.Outlined.Security
        ChimahonMoreIcon.Settings -> Icons.Outlined.Settings
        ChimahonMoreIcon.Storage -> Icons.Outlined.Storage
        ChimahonMoreIcon.Sync -> Icons.Outlined.Sync
        ChimahonMoreIcon.Theme -> Icons.Outlined.Palette
        ChimahonMoreIcon.Warning -> Icons.Outlined.WarningAmber
    }
