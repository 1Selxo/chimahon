package app.chimahon.shared.browse

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

enum class BrowseIcon {
    Add,
    Back,
    Check,
    Close,
    Copy,
    Delete,
    Download,
    Edit,
    Extension,
    Filter,
    Forward,
    Info,
    Latest,
    Link,
    Local,
    More,
    Repo,
    Refresh,
    Search,
    Settings,
    Sort,
    Source,
    Star,
    Swap,
    Visibility,
    VisibilityOff,
    Web,
}

val BrowseIcon.imageVector: ImageVector
    get() = when (this) {
        BrowseIcon.Add -> Icons.Outlined.Add
        BrowseIcon.Back -> Icons.Outlined.ArrowBack
        BrowseIcon.Check -> Icons.Outlined.CheckCircle
        BrowseIcon.Close -> Icons.Outlined.Close
        BrowseIcon.Copy -> Icons.Outlined.ContentCopy
        BrowseIcon.Delete -> Icons.Outlined.DeleteOutline
        BrowseIcon.Download -> Icons.Outlined.Download
        BrowseIcon.Edit -> Icons.Outlined.Edit
        BrowseIcon.Extension -> Icons.Outlined.Extension
        BrowseIcon.Filter -> Icons.Outlined.FilterList
        BrowseIcon.Forward -> Icons.Outlined.ArrowForward
        BrowseIcon.Info -> Icons.Outlined.Info
        BrowseIcon.Latest -> Icons.Outlined.NewReleases
        BrowseIcon.Link -> Icons.Outlined.Link
        BrowseIcon.Local -> Icons.Outlined.Storage
        BrowseIcon.More -> Icons.Outlined.MoreHoriz
        BrowseIcon.Repo -> Icons.Outlined.Link
        BrowseIcon.Refresh -> Icons.Outlined.Refresh
        BrowseIcon.Search -> Icons.Outlined.Search
        BrowseIcon.Settings -> Icons.Outlined.Settings
        BrowseIcon.Sort -> Icons.Outlined.Reorder
        BrowseIcon.Source -> Icons.Outlined.Explore
        BrowseIcon.Star -> Icons.Outlined.Star
        BrowseIcon.Swap -> Icons.Outlined.SwapHoriz
        BrowseIcon.Visibility -> Icons.Outlined.Visibility
        BrowseIcon.VisibilityOff -> Icons.Outlined.VisibilityOff
        BrowseIcon.Web -> Icons.Outlined.Public
    }

@Composable
fun BrowseIconVector(icon: BrowseIcon): ImageVector {
    return icon.imageVector
}
