package app.chimahon.shared.downloadsui

import app.chimahon.shared.ChimahonDownloadState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowDown
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowUp
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ChimahonDownloadUiIcon {
    Check,
    Clear,
    Download,
    Error,
    Filter,
    More,
    MoveBottom,
    MoveTop,
    Pause,
    Resume,
    Refresh,
    Retry,
    Search,
    Storage,
    Queue,
}

val ChimahonDownloadUiIcon.imageVector: ImageVector
    get() = when (this) {
        ChimahonDownloadUiIcon.Check -> Icons.Outlined.CheckCircle
        ChimahonDownloadUiIcon.Clear -> Icons.Outlined.DeleteOutline
        ChimahonDownloadUiIcon.Download -> Icons.Outlined.Download
        ChimahonDownloadUiIcon.Error -> Icons.Outlined.ErrorOutline
        ChimahonDownloadUiIcon.Filter -> Icons.Outlined.FilterList
        ChimahonDownloadUiIcon.More -> Icons.Outlined.MoreHoriz
        ChimahonDownloadUiIcon.MoveBottom -> Icons.Outlined.KeyboardDoubleArrowDown
        ChimahonDownloadUiIcon.MoveTop -> Icons.Outlined.KeyboardDoubleArrowUp
        ChimahonDownloadUiIcon.Pause -> Icons.Outlined.Pause
        ChimahonDownloadUiIcon.Resume -> Icons.Outlined.PlayArrow
        ChimahonDownloadUiIcon.Refresh -> Icons.Outlined.Refresh
        ChimahonDownloadUiIcon.Retry -> Icons.Outlined.Refresh
        ChimahonDownloadUiIcon.Search -> Icons.Outlined.Search
        ChimahonDownloadUiIcon.Storage -> Icons.Outlined.Storage
        ChimahonDownloadUiIcon.Queue -> Icons.Outlined.SwapVert
    }

fun ChimahonDownloadState.downloadIcon(): ChimahonDownloadUiIcon {
    return when (this) {
        ChimahonDownloadState.Queued -> ChimahonDownloadUiIcon.Download
        ChimahonDownloadState.Downloading -> ChimahonDownloadUiIcon.Download
        ChimahonDownloadState.Paused -> ChimahonDownloadUiIcon.Pause
        ChimahonDownloadState.Downloaded -> ChimahonDownloadUiIcon.Check
        ChimahonDownloadState.Error -> ChimahonDownloadUiIcon.Error
    }
}

fun ChimahonDownloadState.downloadColor(): Color {
    return when (this) {
        ChimahonDownloadState.Queued -> Color(0xFF5D6382)
        ChimahonDownloadState.Downloading -> Color(0xFF4F7DFF)
        ChimahonDownloadState.Paused -> Color(0xFFC27A25)
        ChimahonDownloadState.Downloaded -> Color(0xFF2E7D4F)
        ChimahonDownloadState.Error -> Color(0xFFC4464A)
    }
}

fun ChimahonDownloadState.downloadBackgroundColor(): Color {
    return downloadColor().copy(alpha = 0.14f)
}

@Composable
fun ChimahonDownloadStatusIcon(state: ChimahonDownloadState): ImageVector {
    return state.downloadIcon().imageVector
}
