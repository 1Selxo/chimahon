package app.chimahon.shared.playerui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Cast
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.DownloadForOffline
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.OndemandVideo
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PictureInPictureAlt
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material.icons.outlined.Theaters
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.ui.graphics.vector.ImageVector

enum class ChimahonPlayerIcon {
    Back,
    Close,
    Play,
    Pause,
    SeekBack,
    SeekForward,
    Previous,
    Next,
    Hoster,
    Subtitles,
    Audio,
    Quality,
    Chapters,
    Speed,
    Filters,
    Tune,
    Settings,
    More,
    EpisodeList,
    Lock,
    Unlock,
    Fullscreen,
    FullscreenExit,
    Fit,
    PictureInPicture,
    Theater,
    Cast,
    Ocr,
    Lookup,
    Brightness,
    Volume,
    Download,
    DownloadOffline,
    OpenSource,
    Copy,
    Share,
    Refresh,
    Keyboard,
    Controller,
    Visibility,
}

val ChimahonPlayerIcon.imageVector: ImageVector
    get() = when (this) {
        ChimahonPlayerIcon.Back -> Icons.Outlined.ArrowBack
        ChimahonPlayerIcon.Close -> Icons.Outlined.Close
        ChimahonPlayerIcon.Play -> Icons.Outlined.PlayArrow
        ChimahonPlayerIcon.Pause -> Icons.Outlined.Pause
        ChimahonPlayerIcon.SeekBack -> Icons.Filled.Replay10
        ChimahonPlayerIcon.SeekForward -> Icons.Filled.FastForward
        ChimahonPlayerIcon.Previous -> Icons.Outlined.SkipPrevious
        ChimahonPlayerIcon.Next -> Icons.Outlined.SkipNext
        ChimahonPlayerIcon.Hoster -> Icons.Outlined.Public
        ChimahonPlayerIcon.Subtitles -> Icons.Outlined.Subtitles
        ChimahonPlayerIcon.Audio -> Icons.Outlined.VolumeUp
        ChimahonPlayerIcon.Quality -> Icons.Outlined.OndemandVideo
        ChimahonPlayerIcon.Chapters -> Icons.Outlined.OndemandVideo
        ChimahonPlayerIcon.Speed -> Icons.Outlined.Speed
        ChimahonPlayerIcon.Filters -> Icons.Outlined.FilterList
        ChimahonPlayerIcon.Tune -> Icons.Outlined.Settings
        ChimahonPlayerIcon.Settings -> Icons.Outlined.Settings
        ChimahonPlayerIcon.More -> Icons.Outlined.MoreVert
        ChimahonPlayerIcon.EpisodeList -> Icons.Outlined.OndemandVideo
        ChimahonPlayerIcon.Lock -> Icons.Filled.Lock
        ChimahonPlayerIcon.Unlock -> Icons.Filled.LockOpen
        ChimahonPlayerIcon.Fullscreen -> Icons.Outlined.CropFree
        ChimahonPlayerIcon.FullscreenExit -> Icons.Outlined.CropFree
        ChimahonPlayerIcon.Fit -> Icons.Outlined.CropFree
        ChimahonPlayerIcon.PictureInPicture -> Icons.Outlined.PictureInPictureAlt
        ChimahonPlayerIcon.Theater -> Icons.Outlined.Theaters
        ChimahonPlayerIcon.Cast -> Icons.Outlined.Cast
        ChimahonPlayerIcon.Ocr -> Icons.Outlined.CropFree
        ChimahonPlayerIcon.Lookup -> Icons.Outlined.Search
        ChimahonPlayerIcon.Brightness -> Icons.Outlined.Visibility
        ChimahonPlayerIcon.Volume -> Icons.Outlined.VolumeUp
        ChimahonPlayerIcon.Download -> Icons.Outlined.Download
        ChimahonPlayerIcon.DownloadOffline -> Icons.Outlined.DownloadForOffline
        ChimahonPlayerIcon.OpenSource -> Icons.Outlined.OpenInBrowser
        ChimahonPlayerIcon.Copy -> Icons.Outlined.ContentCopy
        ChimahonPlayerIcon.Share -> Icons.Outlined.IosShare
        ChimahonPlayerIcon.Refresh -> Icons.Outlined.Refresh
        ChimahonPlayerIcon.Keyboard -> Icons.Outlined.Keyboard
        ChimahonPlayerIcon.Controller -> Icons.Outlined.Keyboard
        ChimahonPlayerIcon.Visibility -> Icons.Outlined.Visibility
    }
