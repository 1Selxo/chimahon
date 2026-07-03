package app.chimahon.shared.novelimportui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CollectionsBookmark
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
import androidx.compose.ui.graphics.vector.ImageVector

enum class ChimahonNovelImportUiIcon {
    File,
    Storage,
    Queue,
    Import,
    Metadata,
    Chapters,
    Check,
    Error,
    Warning,
    Pause,
    Resume,
    Retry,
    Refresh,
    Remove,
    More,
    Search,
    Filter,
    Sync,
    Expand,
    Collapse,
}

val ChimahonNovelImportUiIcon.imageVector: ImageVector
    get() = when (this) {
        ChimahonNovelImportUiIcon.File -> Icons.Outlined.CollectionsBookmark
        ChimahonNovelImportUiIcon.Storage -> Icons.Outlined.Storage
        ChimahonNovelImportUiIcon.Queue -> Icons.Outlined.SwapVert
        ChimahonNovelImportUiIcon.Import -> Icons.Outlined.Download
        ChimahonNovelImportUiIcon.Metadata -> Icons.Outlined.Bookmark
        ChimahonNovelImportUiIcon.Chapters -> Icons.Outlined.CollectionsBookmark
        ChimahonNovelImportUiIcon.Check -> Icons.Outlined.CheckCircle
        ChimahonNovelImportUiIcon.Error -> Icons.Outlined.ErrorOutline
        ChimahonNovelImportUiIcon.Warning -> Icons.Outlined.ErrorOutline
        ChimahonNovelImportUiIcon.Pause -> Icons.Outlined.Pause
        ChimahonNovelImportUiIcon.Resume -> Icons.Outlined.PlayArrow
        ChimahonNovelImportUiIcon.Retry -> Icons.Outlined.Refresh
        ChimahonNovelImportUiIcon.Refresh -> Icons.Outlined.Refresh
        ChimahonNovelImportUiIcon.Remove -> Icons.Outlined.DeleteOutline
        ChimahonNovelImportUiIcon.More -> Icons.Outlined.MoreHoriz
        ChimahonNovelImportUiIcon.Search -> Icons.Outlined.Search
        ChimahonNovelImportUiIcon.Filter -> Icons.Outlined.FilterList
        ChimahonNovelImportUiIcon.Sync -> Icons.Outlined.SwapVert
        ChimahonNovelImportUiIcon.Expand -> Icons.Outlined.KeyboardDoubleArrowDown
        ChimahonNovelImportUiIcon.Collapse -> Icons.Outlined.KeyboardDoubleArrowUp
    }

fun ChimahonNovelImportFileStatus.icon(): ChimahonNovelImportUiIcon {
    return when (this) {
        ChimahonNovelImportFileStatus.Staged -> ChimahonNovelImportUiIcon.File
        ChimahonNovelImportFileStatus.Queued -> ChimahonNovelImportUiIcon.Queue
        ChimahonNovelImportFileStatus.ReadingMetadata -> ChimahonNovelImportUiIcon.Metadata
        ChimahonNovelImportFileStatus.Ready -> ChimahonNovelImportUiIcon.Check
        ChimahonNovelImportFileStatus.Importing -> ChimahonNovelImportUiIcon.Import
        ChimahonNovelImportFileStatus.Imported -> ChimahonNovelImportUiIcon.Check
        ChimahonNovelImportFileStatus.Skipped -> ChimahonNovelImportUiIcon.Remove
        ChimahonNovelImportFileStatus.Warning -> ChimahonNovelImportUiIcon.Warning
        ChimahonNovelImportFileStatus.Failed -> ChimahonNovelImportUiIcon.Error
        ChimahonNovelImportFileStatus.Canceled -> ChimahonNovelImportUiIcon.Remove
    }
}

fun ChimahonNovelImportChapterStatus.icon(): ChimahonNovelImportUiIcon {
    return when (this) {
        ChimahonNovelImportChapterStatus.Ready -> ChimahonNovelImportUiIcon.Chapters
        ChimahonNovelImportChapterStatus.Selected -> ChimahonNovelImportUiIcon.Check
        ChimahonNovelImportChapterStatus.Skipped -> ChimahonNovelImportUiIcon.Remove
        ChimahonNovelImportChapterStatus.Duplicate -> ChimahonNovelImportUiIcon.Warning
        ChimahonNovelImportChapterStatus.Warning -> ChimahonNovelImportUiIcon.Warning
        ChimahonNovelImportChapterStatus.Error -> ChimahonNovelImportUiIcon.Error
    }
}
