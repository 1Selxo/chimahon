package app.chimahon.shared.reader.appbars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReaderBottomBar(
    state: ReaderBottomBarState,
    actions: ReaderBottomBarActions,
    modifier: Modifier = Modifier,
    colors: ReaderAppBarColors = ReaderAppBarDefaults.colors(),
) {
    Row(
        modifier = modifier
            .heightIn(min = 56.dp)
            .background(colors.surface)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (state.enabledButtons.hasReaderBottomButton(ReaderBottomButton.ViewChapters)) {
            actions.onClickChapterList?.let {
                ReaderBottomBarButton(
                    icon = ReaderAppBarIcon.Chapters,
                    contentDescription = ReaderBottomButton.ViewChapters.contentDescription,
                    colors = colors,
                    onClick = it,
                )
            }
        }

        if (state.enabledButtons.hasReaderBottomButton(ReaderBottomButton.WebView)) {
            actions.onClickWebView?.let {
                ReaderBottomBarButton(
                    icon = ReaderAppBarIcon.WebView,
                    contentDescription = ReaderBottomButton.WebView.contentDescription,
                    colors = colors,
                    onClick = it,
                )
            }
        }

        if (state.enabledButtons.hasReaderBottomButton(ReaderBottomButton.Browser)) {
            actions.onClickBrowser?.let {
                ReaderBottomBarButton(
                    icon = ReaderAppBarIcon.Browser,
                    contentDescription = ReaderBottomButton.Browser.contentDescription,
                    colors = colors,
                    onClick = it,
                )
            }
        }

        if (state.enabledButtons.hasReaderBottomButton(ReaderBottomButton.Share)) {
            actions.onClickShare?.let {
                ReaderBottomBarButton(
                    icon = ReaderAppBarIcon.Share,
                    contentDescription = ReaderBottomButton.Share.contentDescription,
                    colors = colors,
                    onClick = it,
                )
            }
        }

        if (state.enabledButtons.hasReaderBottomButton(ReaderBottomButton.ReadingMode)) {
            actions.onClickReadingMode?.let {
                ReaderBottomBarButton(
                    icon = ReaderAppBarIcon.ReadingMode,
                    contentDescription = state.readingModeContentDescription,
                    colors = colors,
                    onClick = it,
                )
            }
        }

        if (state.enabledButtons.hasReaderBottomButton(ReaderBottomButton.Rotation)) {
            actions.onClickOrientation?.let {
                ReaderBottomBarButton(
                    icon = ReaderAppBarIcon.Rotation,
                    contentDescription = state.orientationContentDescription,
                    colors = colors,
                    onClick = it,
                )
            }
        }

        val cropButton = state.currentReadingMode.cropButton()
        if (state.enabledButtons.hasReaderBottomButton(cropButton)) {
            actions.onClickCropBorder?.let {
                ReaderBottomBarButton(
                    icon = ReaderAppBarIcon.Crop,
                    contentDescription = cropButton.contentDescription,
                    colors = colors,
                    active = state.cropEnabled,
                    onClick = it,
                )
            }
        }

        if (
            !state.dualPageSplitEnabled &&
            state.currentReadingMode.paged &&
            state.enabledButtons.hasReaderBottomButton(ReaderBottomButton.PageLayout)
        ) {
            actions.onClickPageLayout?.let {
                ReaderBottomBarButton(
                    icon = ReaderAppBarIcon.PageLayout,
                    contentDescription = ReaderBottomButton.PageLayout.contentDescription,
                    colors = colors,
                    active = state.pageLayoutActive,
                    onClick = it,
                )
            }
        }

        if (state.doublePages && state.enabledButtons.hasReaderBottomButton(ReaderBottomButton.ShiftPage)) {
            actions.onClickShiftPage?.let {
                ReaderBottomBarButton(
                    icon = ReaderAppBarIcon.ShiftPage,
                    contentDescription = ReaderBottomButton.ShiftPage.contentDescription,
                    colors = colors,
                    active = state.shiftPageActive,
                    onClick = it,
                )
            }
        }

        if (
            state.ocr.visible &&
            state.enabledButtons.hasReaderBottomButton(ReaderBottomButton.OcrLookup)
        ) {
            actions.onToggleOcrLookup?.let {
                ReaderBottomBarButton(
                    icon = ReaderAppBarIcon.Ocr,
                    contentDescription = state.ocr.contentDescription,
                    colors = colors,
                    active = state.ocr.enabled,
                    enabled = state.ocr.canToggle,
                    loading = state.ocr.loading,
                    onClick = it,
                )
            }
        }

        if (state.enabledButtons.hasReaderBottomButton(ReaderBottomButton.MangaStats)) {
            actions.onClickMangaStats?.let {
                ReaderBottomBarButton(
                    icon = ReaderAppBarIcon.Stats,
                    contentDescription = ReaderBottomButton.MangaStats.contentDescription,
                    colors = colors,
                    active = state.mangaStatsActive,
                    onClick = it,
                )
            }
        }

        if (state.showSettingsButton) {
            ReaderBottomBarButton(
                icon = ReaderAppBarIcon.Settings,
                contentDescription = ReaderBottomButton.Settings.contentDescription,
                colors = colors,
                active = state.settingsActive,
                onClick = actions.onClickSettings,
            )
        }
    }
}

@Composable
fun ReaderBottomBarButton(
    icon: ReaderAppBarIcon,
    contentDescription: String,
    colors: ReaderAppBarColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    active: Boolean = false,
    loading: Boolean = false,
) {
    ReaderIconButton(
        icon = icon,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        tint = colors.primary,
        active = active,
        enabled = enabled,
        loading = loading,
        activeTint = colors.primary,
    )
}
