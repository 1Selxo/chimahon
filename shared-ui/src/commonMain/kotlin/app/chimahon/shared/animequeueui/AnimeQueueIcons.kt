package app.chimahon.shared.animequeueui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowDown
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowUp
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector

val ChimahonAnimeQueueActionIcon.imageVector: ImageVector
    get() = when (this) {
        ChimahonAnimeQueueActionIcon.More -> Icons.Outlined.MoreHoriz
        ChimahonAnimeQueueActionIcon.SelectAll -> Icons.Outlined.DoneAll
        ChimahonAnimeQueueActionIcon.ClearSelection -> Icons.Outlined.Close
        ChimahonAnimeQueueActionIcon.MarkSeen -> Icons.Outlined.CheckCircle
        ChimahonAnimeQueueActionIcon.MarkUnseen -> Icons.Outlined.VisibilityOff
        ChimahonAnimeQueueActionIcon.Download -> Icons.Outlined.Download
        ChimahonAnimeQueueActionIcon.Play -> Icons.Outlined.PlayArrow
        ChimahonAnimeQueueActionIcon.Pause -> Icons.Outlined.Pause
        ChimahonAnimeQueueActionIcon.Resume -> Icons.Outlined.PlayArrow
        ChimahonAnimeQueueActionIcon.Retry -> Icons.Outlined.Refresh
        ChimahonAnimeQueueActionIcon.Cancel -> Icons.Outlined.Cancel
        ChimahonAnimeQueueActionIcon.Delete -> Icons.Outlined.DeleteOutline
        ChimahonAnimeQueueActionIcon.Open -> Icons.Outlined.OpenInNew
        ChimahonAnimeQueueActionIcon.Remove -> Icons.Outlined.RemoveCircleOutline
        ChimahonAnimeQueueActionIcon.MoveTop -> Icons.Outlined.KeyboardDoubleArrowUp
        ChimahonAnimeQueueActionIcon.MoveUp -> Icons.Outlined.ArrowUpward
        ChimahonAnimeQueueActionIcon.MoveDown -> Icons.Outlined.ArrowDownward
        ChimahonAnimeQueueActionIcon.MoveBottom -> Icons.Outlined.KeyboardDoubleArrowDown
        ChimahonAnimeQueueActionIcon.Priority -> Icons.Outlined.Reorder
        ChimahonAnimeQueueActionIcon.Playlist -> Icons.Outlined.FormatListNumbered
        ChimahonAnimeQueueActionIcon.Stream -> Icons.Outlined.Public
        ChimahonAnimeQueueActionIcon.Local -> Icons.Outlined.Storage
        ChimahonAnimeQueueActionIcon.Error -> Icons.Outlined.ErrorOutline
    }

val ChimahonAnimeQueueSurfaceKind.defaultIcon: ImageVector
    get() = when (this) {
        ChimahonAnimeQueueSurfaceKind.WatchQueue -> Icons.Outlined.Movie
        ChimahonAnimeQueueSurfaceKind.DownloadQueue -> Icons.Outlined.Download
        ChimahonAnimeQueueSurfaceKind.AutoNext -> Icons.Outlined.FormatListNumbered
    }
