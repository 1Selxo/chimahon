package app.chimahon.shared.queueui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.MarkChatRead
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector

val ChimahonQueueActionIcon.imageVector: ImageVector
    get() = when (this) {
        ChimahonQueueActionIcon.More -> Icons.Outlined.MoreHoriz
        ChimahonQueueActionIcon.SelectAll -> Icons.Outlined.DoneAll
        ChimahonQueueActionIcon.ClearSelection -> Icons.Outlined.Close
        ChimahonQueueActionIcon.MarkRead -> Icons.Outlined.MarkChatRead
        ChimahonQueueActionIcon.MarkUnread -> Icons.Outlined.VisibilityOff
        ChimahonQueueActionIcon.Download -> Icons.Outlined.FileDownload
        ChimahonQueueActionIcon.Pause -> Icons.Outlined.Pause
        ChimahonQueueActionIcon.Resume -> Icons.Outlined.PlayArrow
        ChimahonQueueActionIcon.Retry -> Icons.Outlined.Refresh
        ChimahonQueueActionIcon.Cancel -> Icons.Outlined.Cancel
        ChimahonQueueActionIcon.Delete -> Icons.Outlined.DeleteOutline
        ChimahonQueueActionIcon.Open -> Icons.Outlined.OpenInNew
        ChimahonQueueActionIcon.Remove -> Icons.Outlined.RemoveCircleOutline
    }

val ChimahonQueueSurfaceKind.defaultIcon: ImageVector
    get() = when (this) {
        ChimahonQueueSurfaceKind.Updates -> Icons.Outlined.CheckCircle
        ChimahonQueueSurfaceKind.History -> Icons.Outlined.MarkChatRead
        ChimahonQueueSurfaceKind.Downloads -> Icons.Outlined.FileDownload
    }
