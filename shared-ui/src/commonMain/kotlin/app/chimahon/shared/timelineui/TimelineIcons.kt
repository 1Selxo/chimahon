package app.chimahon.shared.timelineui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.ui.graphics.vector.ImageVector

val TimelineQuickActionIcon.imageVector: ImageVector
    get() = when (this) {
        TimelineQuickActionIcon.Bookmark -> Icons.Outlined.Bookmark
        TimelineQuickActionIcon.BookmarkBorder -> Icons.Outlined.BookmarkBorder
        TimelineQuickActionIcon.CheckCircle -> Icons.Outlined.CheckCircle
        TimelineQuickActionIcon.Delete -> Icons.Outlined.DeleteOutline
        TimelineQuickActionIcon.DoneAll -> Icons.Outlined.DoneAll
        TimelineQuickActionIcon.Download -> Icons.Outlined.Download
        TimelineQuickActionIcon.Favorite -> Icons.Outlined.Favorite
        TimelineQuickActionIcon.FavoriteBorder -> Icons.Outlined.FavoriteBorder
        TimelineQuickActionIcon.History -> Icons.Outlined.History
        TimelineQuickActionIcon.More -> Icons.Outlined.MoreHoriz
        TimelineQuickActionIcon.Open -> Icons.Outlined.OpenInNew
        TimelineQuickActionIcon.Play -> Icons.Outlined.PlayArrow
        TimelineQuickActionIcon.Read -> Icons.Outlined.CheckCircle
        TimelineQuickActionIcon.Unread -> Icons.Outlined.RadioButtonUnchecked
        TimelineQuickActionIcon.Updates -> Icons.Outlined.NewReleases
    }

val TimelineSurfaceKind.imageVector: ImageVector
    get() = when (this) {
        TimelineSurfaceKind.Updates -> TimelineQuickActionIcon.Updates.imageVector
        TimelineSurfaceKind.History -> TimelineQuickActionIcon.History.imageVector
    }
