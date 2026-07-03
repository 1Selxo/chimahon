package app.chimahon.shared.trackingui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector

enum class ChimahonTrackingIcon {
    Account,
    Manga,
    Anime,
    LightNovel,
    Synced,
    Error,
    Sync,
    Search,
    Open,
    Edit,
    Remove,
    Private,
    Score,
}

fun ChimahonTrackingMediaKind.icon(): ImageVector {
    return when (this) {
        ChimahonTrackingMediaKind.Manga -> Icons.Outlined.CollectionsBookmark
        ChimahonTrackingMediaKind.Anime -> Icons.Outlined.PlayCircle
        ChimahonTrackingMediaKind.LightNovel -> Icons.Outlined.Bookmark
    }
}

fun ChimahonTrackingIcon.imageVector(): ImageVector {
    return when (this) {
        ChimahonTrackingIcon.Account -> Icons.Outlined.Public
        ChimahonTrackingIcon.Manga -> Icons.Outlined.CollectionsBookmark
        ChimahonTrackingIcon.Anime -> Icons.Outlined.PlayCircle
        ChimahonTrackingIcon.LightNovel -> Icons.Outlined.Bookmark
        ChimahonTrackingIcon.Synced -> Icons.Outlined.CheckCircle
        ChimahonTrackingIcon.Error -> Icons.Outlined.ErrorOutline
        ChimahonTrackingIcon.Sync -> Icons.Outlined.Sync
        ChimahonTrackingIcon.Search -> Icons.Outlined.Search
        ChimahonTrackingIcon.Open -> Icons.Outlined.Link
        ChimahonTrackingIcon.Edit -> Icons.Outlined.Edit
        ChimahonTrackingIcon.Remove -> Icons.Outlined.DeleteOutline
        ChimahonTrackingIcon.Private -> Icons.Outlined.VisibilityOff
        ChimahonTrackingIcon.Score -> Icons.Outlined.QueryStats
    }
}

fun ChimahonTrackerSyncStatus.icon(): ImageVector {
    return when (this) {
        ChimahonTrackerSyncStatus.Failed -> ChimahonTrackingIcon.Error.imageVector()
        ChimahonTrackerSyncStatus.Synced -> ChimahonTrackingIcon.Synced.imageVector()
        ChimahonTrackerSyncStatus.Idle,
        ChimahonTrackerSyncStatus.Pending,
        ChimahonTrackerSyncStatus.Syncing,
        ChimahonTrackerSyncStatus.Disabled,
        -> ChimahonTrackingIcon.Sync.imageVector()
    }
}

fun ChimahonTrackerAccountStatus.icon(): ImageVector {
    return when (this) {
        ChimahonTrackerAccountStatus.Error,
        ChimahonTrackerAccountStatus.Expired,
        -> ChimahonTrackingIcon.Error.imageVector()
        ChimahonTrackerAccountStatus.LoggedIn,
        ChimahonTrackerAccountStatus.Syncing,
        -> ChimahonTrackingIcon.Synced.imageVector()
        ChimahonTrackerAccountStatus.LoggedOut -> ChimahonTrackingIcon.Account.imageVector()
    }
}
