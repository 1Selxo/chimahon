package app.chimahon.shared.animeextensionui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.GetApp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

enum class AnimeExtensionIcon {
    Block,
    Check,
    Close,
    Copy,
    Delete,
    Details,
    Download,
    Edit,
    Error,
    Extension,
    Filter,
    Install,
    Info,
    Link,
    More,
    Open,
    Repo,
    Refresh,
    Search,
    Security,
    Settings,
    Sort,
    Sync,
    Trust,
    Update,
    Visibility,
    VisibilityOff,
    Web,
}

val AnimeExtensionIcon.imageVector: ImageVector
    get() = when (this) {
        AnimeExtensionIcon.Block -> Icons.Outlined.Block
        AnimeExtensionIcon.Check -> Icons.Outlined.CheckCircle
        AnimeExtensionIcon.Close -> Icons.Outlined.Close
        AnimeExtensionIcon.Copy -> Icons.Outlined.ContentCopy
        AnimeExtensionIcon.Delete -> Icons.Outlined.DeleteOutline
        AnimeExtensionIcon.Details -> Icons.Outlined.Info
        AnimeExtensionIcon.Download -> Icons.Outlined.Download
        AnimeExtensionIcon.Edit -> Icons.Outlined.Edit
        AnimeExtensionIcon.Error -> Icons.Outlined.ErrorOutline
        AnimeExtensionIcon.Extension -> Icons.Outlined.Extension
        AnimeExtensionIcon.Filter -> Icons.Outlined.FilterList
        AnimeExtensionIcon.Install -> Icons.Outlined.GetApp
        AnimeExtensionIcon.Info -> Icons.Outlined.Info
        AnimeExtensionIcon.Link -> Icons.Outlined.Link
        AnimeExtensionIcon.More -> Icons.Outlined.MoreHoriz
        AnimeExtensionIcon.Open -> Icons.Outlined.OpenInNew
        AnimeExtensionIcon.Repo -> Icons.Outlined.Link
        AnimeExtensionIcon.Refresh -> Icons.Outlined.Refresh
        AnimeExtensionIcon.Search -> Icons.Outlined.Search
        AnimeExtensionIcon.Security -> Icons.Outlined.Security
        AnimeExtensionIcon.Settings -> Icons.Outlined.Settings
        AnimeExtensionIcon.Sort -> Icons.Outlined.Reorder
        AnimeExtensionIcon.Sync -> Icons.Outlined.Sync
        AnimeExtensionIcon.Trust -> Icons.Outlined.VerifiedUser
        AnimeExtensionIcon.Update -> Icons.Outlined.NewReleases
        AnimeExtensionIcon.Visibility -> Icons.Outlined.Visibility
        AnimeExtensionIcon.VisibilityOff -> Icons.Outlined.VisibilityOff
        AnimeExtensionIcon.Web -> Icons.Outlined.Public
    }

@Composable
fun AnimeExtensionIconVector(icon: AnimeExtensionIcon): ImageVector {
    return icon.imageVector
}
