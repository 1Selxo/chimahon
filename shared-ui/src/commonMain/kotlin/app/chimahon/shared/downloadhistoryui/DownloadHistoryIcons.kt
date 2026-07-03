package app.chimahon.shared.downloadhistoryui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowDown
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowUp
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector

val ChimahonMediaKind.icon: ImageVector
    get() = when (this) {
        ChimahonMediaKind.Manga -> Icons.Outlined.MenuBook
        ChimahonMediaKind.Anime -> Icons.Outlined.Movie
        ChimahonMediaKind.LightNovel -> Icons.Outlined.Subtitles
    }
val ChimahonDownloadHistoryActionIcon.imageVector: ImageVector
    get() = when (this) {
        ChimahonDownloadHistoryActionIcon.More -> Icons.Outlined.MoreVert
        ChimahonDownloadHistoryActionIcon.Open -> Icons.Outlined.OpenInNew
        ChimahonDownloadHistoryActionIcon.Source -> Icons.Outlined.Public
        ChimahonDownloadHistoryActionIcon.MarkRead -> Icons.Outlined.DoneAll
        ChimahonDownloadHistoryActionIcon.MarkUnread -> Icons.Outlined.VisibilityOff
        ChimahonDownloadHistoryActionIcon.Download -> Icons.Outlined.Download
        ChimahonDownloadHistoryActionIcon.Pause -> Icons.Outlined.Pause
        ChimahonDownloadHistoryActionIcon.Resume -> Icons.Outlined.PlayArrow
        ChimahonDownloadHistoryActionIcon.Retry -> Icons.Outlined.Refresh
        ChimahonDownloadHistoryActionIcon.Remove -> Icons.Outlined.RemoveCircleOutline
        ChimahonDownloadHistoryActionIcon.Delete -> Icons.Outlined.Delete
        ChimahonDownloadHistoryActionIcon.MoveTop -> Icons.Outlined.KeyboardDoubleArrowUp
        ChimahonDownloadHistoryActionIcon.MoveUp -> Icons.Outlined.ArrowUpward
        ChimahonDownloadHistoryActionIcon.MoveDown -> Icons.Outlined.ArrowDownward
        ChimahonDownloadHistoryActionIcon.MoveBottom -> Icons.Outlined.KeyboardDoubleArrowDown
        ChimahonDownloadHistoryActionIcon.Cancel -> Icons.Outlined.Cancel
        ChimahonDownloadHistoryActionIcon.Copy -> Icons.Outlined.ContentCopy
    }

val ChimahonDownloadHistoryStatus.imageVector: ImageVector
    get() = when (this) {
        ChimahonDownloadHistoryStatus.Queued -> Icons.Outlined.Schedule
        ChimahonDownloadHistoryStatus.Downloading -> Icons.Outlined.Download
        ChimahonDownloadHistoryStatus.Paused -> Icons.Outlined.Pause
        ChimahonDownloadHistoryStatus.Complete -> Icons.Outlined.CheckCircle
        ChimahonDownloadHistoryStatus.Failed -> Icons.Outlined.ErrorOutline
        ChimahonDownloadHistoryStatus.Canceled -> Icons.Outlined.Cancel
        ChimahonDownloadHistoryStatus.Read -> Icons.Outlined.DoneAll
        ChimahonDownloadHistoryStatus.Unread -> Icons.Outlined.VisibilityOff
        ChimahonDownloadHistoryStatus.Seen -> Icons.Outlined.Visibility
        ChimahonDownloadHistoryStatus.Unseen -> Icons.Outlined.VisibilityOff
    }

val ChimahonDownloadHistoryChipKind.imageVector: ImageVector
    get() = when (this) {
        ChimahonDownloadHistoryChipKind.Source -> Icons.Outlined.Public
        ChimahonDownloadHistoryChipKind.Language -> Icons.Outlined.Language
        ChimahonDownloadHistoryChipKind.Media -> Icons.Outlined.MenuBook
        ChimahonDownloadHistoryChipKind.Status -> Icons.Outlined.CheckCircle
        ChimahonDownloadHistoryChipKind.Warning -> Icons.Outlined.ErrorOutline
        ChimahonDownloadHistoryChipKind.Local -> Icons.Outlined.History
    }
