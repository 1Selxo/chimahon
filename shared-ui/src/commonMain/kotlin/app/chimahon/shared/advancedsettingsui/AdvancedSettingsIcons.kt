package app.chimahon.shared.advancedsettingsui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector

val ChimahonAdvancedSettingIcon.imageVector: ImageVector
    get() = when (this) {
        ChimahonAdvancedSettingIcon.Backup -> Icons.Outlined.Backup
        ChimahonAdvancedSettingIcon.Restore -> Icons.Outlined.Restore
        ChimahonAdvancedSettingIcon.Storage -> Icons.Outlined.Storage
        ChimahonAdvancedSettingIcon.Palette -> Icons.Outlined.Palette
        ChimahonAdvancedSettingIcon.Navigation -> Icons.Outlined.Reorder
        ChimahonAdvancedSettingIcon.Security -> Icons.Outlined.Security
        ChimahonAdvancedSettingIcon.Privacy -> Icons.Outlined.VisibilityOff
        ChimahonAdvancedSettingIcon.Diagnostics -> Icons.Outlined.Info
        ChimahonAdvancedSettingIcon.Sync -> Icons.Outlined.Sync
        ChimahonAdvancedSettingIcon.Delete -> Icons.Outlined.DeleteOutline
        ChimahonAdvancedSettingIcon.Search -> Icons.Outlined.Search
        ChimahonAdvancedSettingIcon.Visibility -> Icons.Outlined.Visibility
        ChimahonAdvancedSettingIcon.Hidden -> Icons.Outlined.VisibilityOff
        ChimahonAdvancedSettingIcon.Settings -> Icons.Outlined.Settings
        ChimahonAdvancedSettingIcon.Download -> Icons.Outlined.Download
        ChimahonAdvancedSettingIcon.Lock -> Icons.Outlined.Lock
        ChimahonAdvancedSettingIcon.Tune -> Icons.Outlined.Tune
    }

val ChimahonAdvancedSettingsRoute.icon: ChimahonAdvancedSettingIcon
    get() = when (this) {
        ChimahonAdvancedSettingsRoute.Overview -> ChimahonAdvancedSettingIcon.Settings
        ChimahonAdvancedSettingsRoute.Appearance -> ChimahonAdvancedSettingIcon.Palette
        ChimahonAdvancedSettingsRoute.BackupRestore -> ChimahonAdvancedSettingIcon.Backup
        ChimahonAdvancedSettingsRoute.DataStorage -> ChimahonAdvancedSettingIcon.Storage
        ChimahonAdvancedSettingsRoute.SecurityPrivacy -> ChimahonAdvancedSettingIcon.Security
        ChimahonAdvancedSettingsRoute.NavigationEditor -> ChimahonAdvancedSettingIcon.Navigation
        ChimahonAdvancedSettingsRoute.AnimeAndNovel -> ChimahonAdvancedSettingIcon.Visibility
        ChimahonAdvancedSettingsRoute.Diagnostics -> ChimahonAdvancedSettingIcon.Diagnostics
    }
