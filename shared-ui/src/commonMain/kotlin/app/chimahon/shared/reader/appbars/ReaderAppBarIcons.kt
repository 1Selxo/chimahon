package app.chimahon.shared.reader.appbars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.ScreenRotation
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.ViewColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ReaderAppBarIcon {
    Back,
    Bookmark,
    BookmarkBorder,
    Browser,
    Chapters,
    Crop,
    More,
    Next,
    Ocr,
    PageLayout,
    Previous,
    ReadingMode,
    Rotation,
    Settings,
    Share,
    ShiftPage,
    Stats,
    WebView,
}

val ReaderAppBarIcon.imageVector: ImageVector
    get() = when (this) {
        ReaderAppBarIcon.Back -> Icons.Outlined.ArrowBack
        ReaderAppBarIcon.Bookmark -> Icons.Outlined.Bookmark
        ReaderAppBarIcon.BookmarkBorder -> Icons.Outlined.BookmarkBorder
        ReaderAppBarIcon.Browser -> Icons.Outlined.Explore
        ReaderAppBarIcon.Chapters -> Icons.Outlined.FormatListNumbered
        ReaderAppBarIcon.Crop -> Icons.Outlined.CropFree
        ReaderAppBarIcon.More -> Icons.Outlined.MoreHoriz
        ReaderAppBarIcon.Next -> Icons.Outlined.SkipNext
        ReaderAppBarIcon.Ocr -> Icons.Outlined.Search
        ReaderAppBarIcon.PageLayout -> Icons.Outlined.ViewColumn
        ReaderAppBarIcon.Previous -> Icons.Outlined.SkipPrevious
        ReaderAppBarIcon.ReadingMode -> Icons.Outlined.SwapHoriz
        ReaderAppBarIcon.Rotation -> Icons.Outlined.ScreenRotation
        ReaderAppBarIcon.Settings -> Icons.Outlined.Settings
        ReaderAppBarIcon.Share -> Icons.Outlined.Share
        ReaderAppBarIcon.ShiftPage -> Icons.Outlined.SwapHoriz
        ReaderAppBarIcon.Stats -> Icons.Outlined.QueryStats
        ReaderAppBarIcon.WebView -> Icons.Outlined.Public
    }

data class ReaderAppBarColors(
    val surface: Color,
    val content: Color,
    val secondaryContent: Color,
    val primary: Color,
    val selectedControl: Color,
    val divider: Color,
    val disabledContent: Color,
)

object ReaderAppBarDefaults {
    val DefaultBottomButtonKeys: Set<String> = setOf(
        ReaderBottomButton.ViewChapters.key,
        ReaderBottomButton.WebView.key,
        ReaderBottomButton.Browser.key,
        ReaderBottomButton.Share.key,
        ReaderBottomButton.ReadingMode.key,
        ReaderBottomButton.Rotation.key,
        ReaderBottomButton.CropBordersPager.key,
        ReaderBottomButton.CropBordersContinuousVertical.key,
        ReaderBottomButton.CropBordersWebtoon.key,
        ReaderBottomButton.PageLayout.key,
        ReaderBottomButton.ShiftPage.key,
        ReaderBottomButton.OcrLookup.key,
        ReaderBottomButton.MangaStats.key,
        ReaderBottomButton.Settings.key,
    )

    @Composable
    fun colors(
        surface: Color = readerAppBarSurfaceColor(),
        content: Color = androidx.compose.material.MaterialTheme.colors.onSurface,
        secondaryContent: Color = content.copy(alpha = 0.64f),
        primary: Color = androidx.compose.material.MaterialTheme.colors.primary,
        selectedControl: Color = Color(0xFF5C5A86),
        divider: Color = content.copy(alpha = 0.10f),
        disabledContent: Color = content.copy(alpha = 0.30f),
    ): ReaderAppBarColors {
        return ReaderAppBarColors(
            surface = surface,
            content = content,
            secondaryContent = secondaryContent,
            primary = primary,
            selectedControl = selectedControl,
            divider = divider,
            disabledContent = disabledContent,
        )
    }
}
