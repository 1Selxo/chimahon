package app.chimahon.shared.discoverui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.ui.graphics.vector.ImageVector

enum class ChimahonDiscoverIcon {
    Back,
    Check,
    Close,
    Error,
    Filter,
    Manga,
    Anime,
    LightNovel,
    More,
    Popular,
    Latest,
    Refresh,
    Search,
    Settings,
    Star,
    Warning,
}

val ChimahonDiscoverIcon.imageVector: ImageVector
    get() = when (this) {
        ChimahonDiscoverIcon.Back -> Icons.Outlined.ArrowBack
        ChimahonDiscoverIcon.Check -> Icons.Outlined.CheckCircle
        ChimahonDiscoverIcon.Close -> Icons.Outlined.Close
        ChimahonDiscoverIcon.Error -> Icons.Outlined.ErrorOutline
        ChimahonDiscoverIcon.Filter -> Icons.Outlined.FilterList
        ChimahonDiscoverIcon.Manga -> Icons.Outlined.CollectionsBookmark
        ChimahonDiscoverIcon.Anime -> Icons.Outlined.PlayCircle
        ChimahonDiscoverIcon.LightNovel -> Icons.Outlined.MenuBook
        ChimahonDiscoverIcon.More -> Icons.Outlined.MoreVert
        ChimahonDiscoverIcon.Popular -> Icons.Outlined.Explore
        ChimahonDiscoverIcon.Latest -> Icons.Outlined.NewReleases
        ChimahonDiscoverIcon.Refresh -> Icons.Outlined.Refresh
        ChimahonDiscoverIcon.Search -> Icons.Outlined.Search
        ChimahonDiscoverIcon.Settings -> Icons.Outlined.Settings
        ChimahonDiscoverIcon.Star -> Icons.Outlined.Star
        ChimahonDiscoverIcon.Warning -> Icons.Outlined.WarningAmber
    }

val ChimahonDiscoverContentType.icon: ChimahonDiscoverIcon
    get() = when (this) {
        ChimahonDiscoverContentType.Manga -> ChimahonDiscoverIcon.Manga
        ChimahonDiscoverContentType.Anime -> ChimahonDiscoverIcon.Anime
        ChimahonDiscoverContentType.LightNovel -> ChimahonDiscoverIcon.LightNovel
    }

val ChimahonDiscoverBrowseMode.icon: ChimahonDiscoverIcon
    get() = when (this) {
        ChimahonDiscoverBrowseMode.Popular -> ChimahonDiscoverIcon.Popular
        ChimahonDiscoverBrowseMode.Latest -> ChimahonDiscoverIcon.Latest
        ChimahonDiscoverBrowseMode.Search -> ChimahonDiscoverIcon.Search
        ChimahonDiscoverBrowseMode.Library -> ChimahonDiscoverIcon.Manga
    }

val ChimahonDiscoverSourceHealth.icon: ChimahonDiscoverIcon
    get() = when (this) {
        ChimahonDiscoverSourceHealth.Ready -> ChimahonDiscoverIcon.Check
        ChimahonDiscoverSourceHealth.Loading -> ChimahonDiscoverIcon.Refresh
        ChimahonDiscoverSourceHealth.Warning -> ChimahonDiscoverIcon.Warning
        ChimahonDiscoverSourceHealth.Error -> ChimahonDiscoverIcon.Error
        ChimahonDiscoverSourceHealth.Disabled -> ChimahonDiscoverIcon.Close
    }
