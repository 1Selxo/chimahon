package app.chimahon.shared.reader.settings

import app.chimahon.shared.ChimahonDualPageMode
import app.chimahon.shared.ChimahonReaderCanvas
import app.chimahon.shared.ChimahonReaderColorFilterMode
import app.chimahon.shared.ChimahonReaderFlashColor
import app.chimahon.shared.ChimahonReaderHideThreshold
import app.chimahon.shared.ChimahonReaderLandscapeZoomType
import app.chimahon.shared.ChimahonReaderMode
import app.chimahon.shared.ChimahonReaderNavigationMode
import app.chimahon.shared.ChimahonReaderOrientation
import app.chimahon.shared.ChimahonReaderScale
import app.chimahon.shared.ChimahonReaderSettings
import app.chimahon.shared.ChimahonReaderTapNavigationLayout
import app.chimahon.shared.ChimahonReaderZoomStart
import app.chimahon.shared.ChimahonTapZoneInvert
import app.chimahon.shared.ChimahonWebtoonScaleType
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonReaderSettingsDialog(
    settings: ChimahonReaderSettings,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    initialTab: ChimahonReaderSettingsTab = ChimahonReaderSettingsTab.ReadingMode,
    onShowMenus: () -> Unit = {},
    onHideMenus: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    LaunchedEffect(selectedTab) {
        if (selectedTab == ChimahonReaderSettingsTab.ColorFilter) {
            onHideMenus()
        } else {
            onShowMenus()
        }
    }

    ReaderSheetDialog(
        onDismissRequest = {
            onDismissRequest()
            onShowMenus()
        },
        modifier = modifier,
    ) {
        ChimahonReaderSettingsDialogContent(
            settings = settings,
            onSettingsChange = onSettingsChange,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
        )
    }
}

@Composable
fun ChimahonReaderSettingsDialogContent(
    settings: ChimahonReaderSettings,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
    selectedTab: ChimahonReaderSettingsTab,
    onTabSelected: (ChimahonReaderSettingsTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    ReaderSheetScaffold(
        title = "Reader settings",
        subtitle = "${settings.mode.readerSettingsTitle()} - ${settings.scale.readerSettingsTitle()} - " +
            settings.canvas.readerSettingsTitle(),
        modifier = modifier,
    ) {
        ReaderSettingsTabRow(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
        )
        when (selectedTab) {
            ChimahonReaderSettingsTab.ReadingMode -> ReaderReadingModeSettingsPage(
                settings = settings,
                onSettingsChange = onSettingsChange,
            )
            ChimahonReaderSettingsTab.General -> ReaderGeneralSettingsPage(
                settings = settings,
                onSettingsChange = onSettingsChange,
            )
            ChimahonReaderSettingsTab.ColorFilter -> ReaderColorFilterSettingsPage(
                settings = settings,
                onSettingsChange = onSettingsChange,
            )
        }
    }
}

@Composable
private fun ReaderSettingsTabRow(
    selectedTab: ChimahonReaderSettingsTab,
    onTabSelected: (ChimahonReaderSettingsTab) -> Unit,
) {
    ReaderSegmentedRow(
        options = ChimahonReaderSettingsTab.entries.map { it.title },
        selected = selectedTab.title,
        onSelect = { title ->
            ChimahonReaderSettingsTab.entries
                .firstOrNull { it.title == title }
                ?.let(onTabSelected)
        },
    )
}

@Composable
private fun ReaderReadingModeSettingsPage(
    settings: ChimahonReaderSettings,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
) {
    ReaderSection(title = "For this series") {
        ReaderChoiceRow(
            label = "Reading mode",
            options = ChimahonReaderMode.entries.map { it.readerSettingsTitle() },
            selected = settings.mode.readerSettingsTitle(),
            onSelect = { title ->
                ChimahonReaderMode.entries
                    .firstOrNull { it.readerSettingsTitle() == title }
                    ?.let { onSettingsChange(settings.copy(mode = it)) }
            },
        )
        ReaderChoiceRow(
            label = "Orientation",
            options = ChimahonReaderOrientation.entries.map { it.readerSettingsTitle() },
            selected = settings.orientation.readerSettingsTitle(),
            onSelect = { title ->
                ChimahonReaderOrientation.entries
                    .firstOrNull { it.readerSettingsTitle() == title }
                    ?.let { onSettingsChange(settings.copy(orientation = it)) }
            },
        )
    }

    if (settings.mode.isPagedMode()) {
        ReaderPagedViewerSettings(settings, onSettingsChange)
    } else {
        ReaderWebtoonViewerSettings(settings, onSettingsChange)
    }

    ReaderSection(title = "Vertical+ viewer") {
        ReaderSwitchRow(
            label = "Tap scrolls by page",
            checked = settings.continuousVerticalTappingByPage,
            onCheckedChange = { onSettingsChange(settings.copy(continuousVerticalTappingByPage = it)) },
        )
    }
}

@Composable
private fun ReaderPagedViewerSettings(
    settings: ChimahonReaderSettings,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
) {
    ReaderSection(title = "Pager viewer") {
        ReaderChoiceRow(
            label = "Navigation",
            options = ChimahonReaderNavigationMode.entries.map { it.readerSettingsTitle() },
            selected = settings.navigationMode.readerSettingsTitle(),
            onSelect = { title ->
                ChimahonReaderNavigationMode.entries
                    .firstOrNull { it.readerSettingsTitle() == title }
                    ?.let { onSettingsChange(settings.copy(navigationMode = it)) }
            },
        )
        ReaderChoiceRow(
            label = "Invert taps",
            options = ChimahonTapZoneInvert.entries.map { it.readerSettingsTitle() },
            selected = settings.invertTapZones.readerSettingsTitle(),
            onSelect = { title ->
                ChimahonTapZoneInvert.entries
                    .firstOrNull { it.readerSettingsTitle() == title }
                    ?.let { onSettingsChange(settings.copy(invertTapZones = it)) }
            },
        )
        ReaderChoiceRow(
            label = "Image scale",
            options = ChimahonReaderScale.entries.map { it.readerSettingsTitle() },
            selected = settings.scale.readerSettingsTitle(),
            onSelect = { title ->
                ChimahonReaderScale.entries
                    .firstOrNull { it.readerSettingsTitle() == title }
                    ?.let { onSettingsChange(settings.copy(scale = it)) }
            },
        )
        ReaderChoiceRow(
            label = "Zoom start",
            options = ChimahonReaderZoomStart.entries.map { it.title },
            selected = settings.pagedZoomStart.title,
            onSelect = { title ->
                ChimahonReaderZoomStart.entries
                    .firstOrNull { it.title == title }
                    ?.let { onSettingsChange(settings.copy(pagedZoomStart = it)) }
            },
        )
        ReaderChoiceRow(
            label = "Page layout",
            options = ChimahonDualPageMode.entries.map { it.readerSettingsTitle() },
            selected = settings.dualPageMode.readerSettingsTitle(),
            onSelect = { title ->
                ChimahonDualPageMode.entries
                    .firstOrNull { it.readerSettingsTitle() == title }
                    ?.let { onSettingsChange(settings.copy(dualPageMode = it)) }
            },
        )
        ReaderSwitchRow(
            label = "Smaller tap zones",
            checked = settings.smallerTapZones,
            onCheckedChange = { onSettingsChange(settings.copy(smallerTapZones = it)) },
        )
        ReaderSwitchRow(
            label = "Crop borders",
            checked = settings.cropBorders,
            onCheckedChange = { onSettingsChange(settings.copy(cropBorders = it)) },
        )
        ReaderSwitchRow(
            label = "Landscape zoom",
            checked = settings.landscapeZoom,
            onCheckedChange = { onSettingsChange(settings.copy(landscapeZoom = it)) },
        )
        if (settings.landscapeZoom) {
            ReaderChoiceRow(
                label = "Wide image zoom mode",
                options = ChimahonReaderLandscapeZoomType.entries.map { it.title },
                selected = settings.landscapeZoomType.title,
                onSelect = { title ->
                    ChimahonReaderLandscapeZoomType.entries
                        .firstOrNull { it.title == title }
                        ?.let { onSettingsChange(settings.copy(landscapeZoomType = it)) }
                },
            )
        }
        ReaderSwitchRow(
            label = "Navigate to pan",
            checked = settings.navigateToPan,
            onCheckedChange = { onSettingsChange(settings.copy(navigateToPan = it)) },
        )
        ReaderSwitchRow(
            label = "Split wide pages",
            checked = settings.splitWidePages,
            onCheckedChange = { onSettingsChange(settings.copy(splitWidePages = it)) },
        )
        ReaderSwitchRow(
            label = "Rotate wide pages",
            checked = settings.rotateWidePagesToFit,
            onCheckedChange = { onSettingsChange(settings.copy(rotateWidePagesToFit = it)) },
        )
        if (settings.rotateWidePagesToFit) {
            ReaderSwitchRow(
                label = "Invert page rotation",
                checked = settings.invertWidePageRotation,
                onCheckedChange = { onSettingsChange(settings.copy(invertWidePageRotation = it)) },
            )
        }
        ReaderSwitchRow(
            label = "Invert double pages",
            checked = settings.invertDoublePages,
            onCheckedChange = { onSettingsChange(settings.copy(invertDoublePages = it)) },
        )
        ReaderSwitchRow(
            label = "Page transitions",
            checked = settings.pageTransitions,
            onCheckedChange = { onSettingsChange(settings.copy(pageTransitions = it)) },
        )
        ReaderSwitchRow(
            label = "Disable zoom in",
            checked = settings.pagedDisableZoomIn,
            onCheckedChange = { onSettingsChange(settings.copy(pagedDisableZoomIn = it)) },
        )
        if (!settings.pagedDisableZoomIn) {
            ReaderSwitchRow(
                label = "Double tap zoom",
                checked = settings.doubleTapToZoom,
                onCheckedChange = { onSettingsChange(settings.copy(doubleTapToZoom = it)) },
            )
        }
        ReaderStepperRow(
            label = "Center margin",
            value = "${settings.centerMarginDp} dp",
            onDecrease = {
                onSettingsChange(settings.copy(centerMarginDp = (settings.centerMarginDp - 2).coerceIn(0, 64)))
            },
            onIncrease = {
                onSettingsChange(settings.copy(centerMarginDp = (settings.centerMarginDp + 2).coerceIn(0, 64)))
            },
        )
    }
}

@Composable
private fun ReaderWebtoonViewerSettings(
    settings: ChimahonReaderSettings,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
) {
    ReaderSection(title = if (settings.mode == ChimahonReaderMode.Vertical) "Vertical viewer" else "Webtoon viewer") {
        ReaderChoiceRow(
            label = "Navigation",
            options = ChimahonReaderNavigationMode.entries.map { it.readerSettingsTitle() },
            selected = settings.navigationMode.readerSettingsTitle(),
            onSelect = { title ->
                ChimahonReaderNavigationMode.entries
                    .firstOrNull { it.readerSettingsTitle() == title }
                    ?.let { onSettingsChange(settings.copy(navigationMode = it)) }
            },
        )
        ReaderChoiceRow(
            label = "Invert taps",
            options = ChimahonTapZoneInvert.entries.map { it.readerSettingsTitle() },
            selected = settings.invertTapZones.readerSettingsTitle(),
            onSelect = { title ->
                ChimahonTapZoneInvert.entries
                    .firstOrNull { it.readerSettingsTitle() == title }
                    ?.let { onSettingsChange(settings.copy(invertTapZones = it)) }
            },
        )
        ReaderChoiceRow(
            label = "Scale type",
            options = ChimahonWebtoonScaleType.entries.map { it.title },
            selected = settings.webtoonScaleType.title,
            onSelect = { title ->
                ChimahonWebtoonScaleType.entries
                    .firstOrNull { it.title == title }
                    ?.let { onSettingsChange(settings.copy(webtoonScaleType = it)) }
            },
        )
        ReaderStepperRow(
            label = "Side padding",
            value = "${settings.webtoonSidePaddingPercent}%",
            onDecrease = {
                onSettingsChange(
                    settings.copy(
                        webtoonSidePaddingPercent = (settings.webtoonSidePaddingPercent - 1).coerceIn(0, 25),
                    ),
                )
            },
            onIncrease = {
                onSettingsChange(
                    settings.copy(
                        webtoonSidePaddingPercent = (settings.webtoonSidePaddingPercent + 1).coerceIn(0, 25),
                    ),
                )
            },
        )
        ReaderSwitchRow(
            label = "Smaller tap zones",
            checked = settings.smallerTapZones,
            onCheckedChange = { onSettingsChange(settings.copy(smallerTapZones = it)) },
        )
        ReaderSwitchRow(
            label = "Crop borders",
            checked = settings.cropBorders,
            onCheckedChange = { onSettingsChange(settings.copy(cropBorders = it)) },
        )
        ReaderSwitchRow(
            label = "Smooth scroll",
            checked = settings.pageTransitions,
            onCheckedChange = { onSettingsChange(settings.copy(pageTransitions = it)) },
        )
        ReaderChoiceRow(
            label = "Dual page",
            options = ChimahonDualPageMode.entries.map { it.readerSettingsTitle() },
            selected = settings.dualPageMode.readerSettingsTitle(),
            onSelect = { title ->
                ChimahonDualPageMode.entries
                    .firstOrNull { it.readerSettingsTitle() == title }
                    ?.let { onSettingsChange(settings.copy(dualPageMode = it)) }
            },
        )
        if (settings.dualPageMode != ChimahonDualPageMode.Off) {
            ReaderSwitchRow(
                label = "Dual page invert",
                checked = settings.invertDoublePages,
                onCheckedChange = { onSettingsChange(settings.copy(invertDoublePages = it)) },
            )
        }
        ReaderSwitchRow(
            label = "Rotate wide pages",
            checked = settings.rotateWidePagesToFitWebtoon,
            onCheckedChange = { onSettingsChange(settings.copy(rotateWidePagesToFitWebtoon = it)) },
        )
        if (settings.rotateWidePagesToFitWebtoon) {
            ReaderSwitchRow(
                label = "Invert page rotation",
                checked = settings.invertWidePageRotationWebtoon,
                onCheckedChange = { onSettingsChange(settings.copy(invertWidePageRotationWebtoon = it)) },
            )
        }
        ReaderSwitchRow(
            label = "Double tap zoom",
            checked = settings.doubleTapToZoom,
            onCheckedChange = { onSettingsChange(settings.copy(doubleTapToZoom = it)) },
        )
        ReaderSwitchRow(
            label = "Pinch to zoom",
            checked = settings.webtoonPinchToZoom,
            onCheckedChange = { onSettingsChange(settings.copy(webtoonPinchToZoom = it)) },
        )
        ReaderSwitchRow(
            label = "Disable zoom out",
            checked = settings.webtoonDisableZoomOut,
            onCheckedChange = { onSettingsChange(settings.copy(webtoonDisableZoomOut = it)) },
        )
        ReaderSwitchRow(
            label = "Long strip smart scale",
            checked = settings.smartLongStripGapScale,
            onCheckedChange = { onSettingsChange(settings.copy(smartLongStripGapScale = it)) },
        )
        ReaderChoiceRow(
            label = "Hide threshold",
            options = ChimahonReaderHideThreshold.entries.map { it.title },
            selected = settings.webtoonReaderHideThreshold.title,
            onSelect = { title ->
                ChimahonReaderHideThreshold.entries
                    .firstOrNull { it.title == title }
                    ?.let { onSettingsChange(settings.copy(webtoonReaderHideThreshold = it)) }
            },
        )
    }
}

@Composable
private fun ReaderGeneralSettingsPage(
    settings: ChimahonReaderSettings,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
) {
    ReaderSection(title = "Reader theme") {
        ReaderCanvasSwatchRow(
            selected = settings.canvas,
            onSelect = { onSettingsChange(settings.copy(canvas = it)) },
        )
        ReaderSwitchRow(
            label = "Pure black background",
            supportingText = "Use OLED black for the black reader theme",
            checked = settings.pureBlackBackground,
            onCheckedChange = { onSettingsChange(settings.copy(pureBlackBackground = it)) },
        )
    }

    ReaderSection(title = "Display") {
        ReaderSwitchRow(
            label = "Show page number",
            checked = settings.showPageNumber,
            onCheckedChange = { onSettingsChange(settings.copy(showPageNumber = it)) },
        )
        ReaderSwitchRow(
            label = "Force horizontal seekbar",
            checked = settings.forceHorizontalSeekbar,
            onCheckedChange = { onSettingsChange(settings.copy(forceHorizontalSeekbar = it)) },
        )
        if (!settings.forceHorizontalSeekbar) {
            ReaderSwitchRow(
                label = "Vertical seekbar in landscape",
                checked = settings.landscapeVerticalSeekbar,
                onCheckedChange = { onSettingsChange(settings.copy(landscapeVerticalSeekbar = it)) },
            )
            ReaderSwitchRow(
                label = "Left-handed vertical seekbar",
                checked = settings.leftVerticalSeekbar,
                onCheckedChange = { onSettingsChange(settings.copy(leftVerticalSeekbar = it)) },
            )
        }
        ReaderSwitchRow(
            label = "Fullscreen",
            checked = settings.fullscreen,
            onCheckedChange = { onSettingsChange(settings.copy(fullscreen = it)) },
        )
        ReaderSwitchRow(
            label = "Draw under cutout",
            checked = settings.drawUnderCutout,
            onCheckedChange = { onSettingsChange(settings.copy(drawUnderCutout = it)) },
        )
        ReaderSwitchRow(
            label = "Keep screen on",
            checked = settings.keepScreenOn,
            onCheckedChange = { onSettingsChange(settings.copy(keepScreenOn = it)) },
        )
        ReaderSwitchRow(
            label = "Show reading mode",
            checked = settings.showReadingMode,
            onCheckedChange = { onSettingsChange(settings.copy(showReadingMode = it)) },
        )
    }

    ReaderSection(title = "Reader behavior") {
        ReaderSwitchRow(
            label = "Read with long tap",
            checked = settings.readWithLongTap,
            onCheckedChange = { onSettingsChange(settings.copy(readWithLongTap = it)) },
        )
        ReaderSwitchRow(
            label = "Show OCR outlines",
            checked = settings.ocrOutlineVisible,
            onCheckedChange = { onSettingsChange(settings.copy(ocrOutlineVisible = it)) },
        )
        ReaderSwitchRow(
            label = "Always show transition",
            checked = settings.alwaysShowChapterTransition,
            onCheckedChange = { onSettingsChange(settings.copy(alwaysShowChapterTransition = it)) },
        )
        ReaderSwitchRow(
            label = "Auto webtoon mode",
            checked = settings.useAutoWebtoon,
            onCheckedChange = { onSettingsChange(settings.copy(useAutoWebtoon = it)) },
        )
        ReaderSwitchRow(
            label = "Show controls on start",
            checked = settings.keepControlsVisible,
            onCheckedChange = { onSettingsChange(settings.copy(keepControlsVisible = it)) },
        )
        ReaderSwitchRow(
            label = "Show navigation overlay",
            checked = settings.showNavigationOverlayOnStart,
            onCheckedChange = { onSettingsChange(settings.copy(showNavigationOverlayOnStart = it)) },
        )
        ReaderSwitchRow(
            label = "Startup delay",
            checked = settings.readerStartupDelay,
            onCheckedChange = { onSettingsChange(settings.copy(readerStartupDelay = it)) },
        )
        ReaderSwitchRow(
            label = "Preserve position",
            checked = settings.preserveReadingPosition,
            onCheckedChange = { onSettingsChange(settings.copy(preserveReadingPosition = it)) },
        )
    }

    ReaderSection(title = "Page flash") {
        ReaderSwitchRow(
            label = "Flash on page change",
            checked = settings.flashOnPageChange,
            onCheckedChange = { onSettingsChange(settings.copy(flashOnPageChange = it)) },
        )
        if (settings.flashOnPageChange) {
            ReaderStepperRow(
                label = "Flash duration",
                value = "${settings.flashDurationMillis} ms",
                onDecrease = {
                    onSettingsChange(
                        settings.copy(flashDurationMillis = (settings.flashDurationMillis - 100).coerceIn(100, 1_500)),
                    )
                },
                onIncrease = {
                    onSettingsChange(
                        settings.copy(flashDurationMillis = (settings.flashDurationMillis + 100).coerceIn(100, 1_500)),
                    )
                },
            )
            ReaderStepperRow(
                label = "Flash interval",
                value = "${settings.flashPageInterval} page(s)",
                onDecrease = {
                    onSettingsChange(settings.copy(flashPageInterval = (settings.flashPageInterval - 1).coerceIn(1, 10)))
                },
                onIncrease = {
                    onSettingsChange(settings.copy(flashPageInterval = (settings.flashPageInterval + 1).coerceIn(1, 10)))
                },
            )
            ReaderChoiceRow(
                label = "Flash color",
                options = ChimahonReaderFlashColor.entries.map { it.readerSettingsTitle() },
                selected = settings.flashColor.readerSettingsTitle(),
                onSelect = { title ->
                    ChimahonReaderFlashColor.entries
                        .firstOrNull { it.readerSettingsTitle() == title }
                        ?.let { onSettingsChange(settings.copy(flashColor = it)) }
                },
            )
        }
    }

    ReaderSection(title = "Controls") {
        ReaderSwitchRow(
            label = "Tap zones",
            checked = settings.tapZonesEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(tapZonesEnabled = it)) },
        )
        ReaderChoiceRow(
            label = "Tap layout",
            options = ChimahonReaderTapNavigationLayout.entries.map { it.title },
            selected = settings.tapNavigationLayout.title,
            onSelect = { title ->
                ChimahonReaderTapNavigationLayout.entries
                    .firstOrNull { it.title == title }
                    ?.let { onSettingsChange(settings.copy(tapNavigationLayout = it)) }
            },
        )
        ReaderStepperRow(
            label = "Tap zone size",
            value = "${settings.tapZonePercent}%",
            onDecrease = {
                onSettingsChange(settings.copy(tapZonePercent = (settings.tapZonePercent - 1).coerceIn(0, 40)))
            },
            onIncrease = {
                onSettingsChange(settings.copy(tapZonePercent = (settings.tapZonePercent + 1).coerceIn(0, 40)))
            },
        )
        ReaderSwitchRow(
            label = "Swipe navigation",
            checked = settings.swipeNavigationEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(swipeNavigationEnabled = it)) },
        )
        ReaderSwitchRow(
            label = "Volume keys",
            checked = settings.volumeKeysEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(volumeKeysEnabled = it)) },
        )
        ReaderSwitchRow(
            label = "Invert volume keys",
            checked = settings.volumeKeysInverted,
            onCheckedChange = { onSettingsChange(settings.copy(volumeKeysInverted = it)) },
        )
        ReaderSwitchRow(
            label = "Long tap controls",
            checked = settings.longTapEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(longTapEnabled = it)) },
        )
    }

    ReaderSection(title = "Chapter navigation") {
        ReaderSwitchRow(
            label = "Skip read chapters",
            checked = settings.skipReadChapters,
            onCheckedChange = { onSettingsChange(settings.copy(skipReadChapters = it)) },
        )
        ReaderSwitchRow(
            label = "Skip filtered chapters",
            checked = settings.skipFilteredChapters,
            onCheckedChange = { onSettingsChange(settings.copy(skipFilteredChapters = it)) },
        )
        ReaderSwitchRow(
            label = "Skip duplicate chapters",
            checked = settings.skipDuplicateChapters,
            onCheckedChange = { onSettingsChange(settings.copy(skipDuplicateChapters = it)) },
        )
    }

    ReaderSection(title = "Bottom buttons") {
        readerBottomButtonOptions().forEach { option ->
            ReaderSwitchRow(
                label = option.title,
                checked = option.isEnabledIn(settings.bottomButtons),
                onCheckedChange = { checked ->
                    val updatedButtons = if (checked) {
                        (option.removeFrom(settings.bottomButtons) + option.key).distinct()
                    } else {
                        option.removeFrom(settings.bottomButtons)
                    }
                    onSettingsChange(settings.copy(bottomButtons = updatedButtons))
                },
            )
        }
    }

    ReaderSection(title = "Page loading") {
        ReaderStepperRow(
            label = "Preload pages",
            value = settings.preloadSize.toString(),
            onDecrease = {
                onSettingsChange(settings.copy(preloadSize = (settings.preloadSize - 1).coerceIn(1, 20)))
            },
            onIncrease = {
                onSettingsChange(settings.copy(preloadSize = (settings.preloadSize + 1).coerceIn(1, 20)))
            },
        )
        ReaderStepperRow(
            label = "Reader threads",
            value = settings.readerThreads.toString(),
            onDecrease = {
                onSettingsChange(settings.copy(readerThreads = (settings.readerThreads - 1).coerceIn(1, 5)))
            },
            onIncrease = {
                onSettingsChange(settings.copy(readerThreads = (settings.readerThreads + 1).coerceIn(1, 5)))
            },
        )
        ReaderStepperRow(
            label = "Cache size",
            value = "${settings.readerCacheSizeMb} MB",
            onDecrease = {
                onSettingsChange(settings.copy(readerCacheSizeMb = (settings.readerCacheSizeMb - 50).coerceIn(50, 2_000)))
            },
            onIncrease = {
                onSettingsChange(settings.copy(readerCacheSizeMb = (settings.readerCacheSizeMb + 50).coerceIn(50, 2_000)))
            },
        )
        ReaderSwitchRow(
            label = "Aggressive loading",
            checked = settings.aggressivePageLoading,
            onCheckedChange = { onSettingsChange(settings.copy(aggressivePageLoading = it)) },
        )
        ReaderSwitchRow(
            label = "Folder per manga",
            checked = settings.folderPerManga,
            onCheckedChange = { onSettingsChange(settings.copy(folderPerManga = it)) },
        )
    }
}

@Composable
private fun ReaderColorFilterSettingsPage(
    settings: ChimahonReaderSettings,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
) {
    ReaderSection(title = "Custom brightness") {
        ReaderSwitchRow(
            label = "Custom brightness",
            checked = settings.customBrightnessEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(customBrightnessEnabled = it)) },
        )
        if (settings.customBrightnessEnabled) {
            ReaderSliderRow(
                label = "Brightness",
                value = settings.customBrightnessValue.toFloat(),
                valueLabel = settings.customBrightnessValue.toString(),
                valueRange = -75f..100f,
                onValueChange = { onSettingsChange(settings.copy(customBrightnessValue = it.toInt())) },
            )
        }
    }

    ReaderSection(title = "Custom color filter") {
        ReaderSwitchRow(
            label = "Color filter",
            checked = settings.colorFilterEnabled,
            onCheckedChange = { enabled ->
                onSettingsChange(
                    settings.copy(
                        colorFilterEnabled = enabled,
                        colorFilterValue = if (enabled && settings.colorFilterValue == 0) {
                            0x66000000
                        } else {
                            settings.colorFilterValue
                        },
                    ),
                )
            },
        )
        if (settings.colorFilterEnabled) {
            ReaderColorPreview(settings)
            ReaderColorChannelRow("Red", settings, 16, onSettingsChange)
            ReaderColorChannelRow("Green", settings, 8, onSettingsChange)
            ReaderColorChannelRow("Blue", settings, 0, onSettingsChange)
            ReaderColorChannelRow("Alpha", settings, 24, onSettingsChange)
            ReaderChoiceRow(
                label = "Blend mode",
                options = ChimahonReaderColorFilterMode.entries.map { it.title },
                selected = settings.colorFilterMode.title,
                onSelect = { title ->
                    ChimahonReaderColorFilterMode.entries
                        .firstOrNull { it.title == title }
                        ?.let { onSettingsChange(settings.copy(colorFilterMode = it)) }
                },
            )
        }
    }

    ReaderSection(title = "Display effects") {
        ReaderSwitchRow(
            label = "Grayscale",
            checked = settings.grayscale,
            onCheckedChange = { onSettingsChange(settings.copy(grayscale = it)) },
        )
        ReaderSwitchRow(
            label = "Invert colors",
            checked = settings.invertColors,
            onCheckedChange = { onSettingsChange(settings.copy(invertColors = it)) },
        )
    }
}

@Composable
private fun ReaderColorChannelRow(
    label: String,
    settings: ChimahonReaderSettings,
    shift: Int,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
) {
    ReaderSliderRow(
        label = label,
        value = settings.colorFilterValue.colorComponent(shift).toFloat(),
        valueLabel = settings.colorFilterValue.colorComponent(shift).toString(),
        valueRange = 0f..255f,
        onValueChange = {
            onSettingsChange(
                settings.copy(
                    colorFilterValue = settings.colorFilterValue.withColorComponentValue(shift, it.toInt()),
                ),
            )
        },
    )
}

@Composable
private fun ReaderColorPreview(settings: ChimahonReaderSettings) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.06f))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ReaderSmallColorSwatch(
            color = settings.colorFilterValue,
            modifier = Modifier.size(34.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = settings.colorFilterMode.title,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "R ${settings.colorFilterValue.colorComponent(16)}  " +
                    "G ${settings.colorFilterValue.colorComponent(8)}  " +
                    "B ${settings.colorFilterValue.colorComponent(0)}  " +
                    "A ${settings.colorFilterValue.colorComponent(24)}",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun ReaderCanvasSwatchRow(
    selected: ChimahonReaderCanvas,
    onSelect: (ChimahonReaderCanvas) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ChimahonReaderCanvas.entries.forEach { canvas ->
            ReaderCanvasSwatchButton(
                canvas = canvas,
                selected = canvas == selected,
                onClick = { onSelect(canvas) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ReaderCanvasSwatchButton(
    canvas: ChimahonReaderCanvas,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = canvas.readerSettingsColor()
    val foreground = if (canvas == ChimahonReaderCanvas.White) Color(0xFF242329) else Color.White
    Surface(
        modifier = modifier
            .height(74.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = background,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.16f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(9.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Aa",
                color = foreground,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
            Text(
                text = canvas.readerSettingsTitle(),
                color = foreground.copy(alpha = 0.78f),
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private data class ReaderBottomButtonOption(
    val key: String,
    val title: String,
    val aliases: Set<String> = emptySet(),
)

private fun readerBottomButtonOptions(): List<ReaderBottomButtonOption> = listOf(
    ReaderBottomButtonOption("vc", "View chapters", aliases = setOf("chapters")),
    ReaderBottomButtonOption("wb", "Open in WebView", aliases = setOf("source")),
    ReaderBottomButtonOption("br", "Open in browser", aliases = setOf("browser")),
    ReaderBottomButtonOption("sh", "Share", aliases = setOf("share")),
    ReaderBottomButtonOption("rm", "Reading mode", aliases = setOf("mode")),
    ReaderBottomButtonOption("rot", "Rotation", aliases = setOf("rotation")),
    ReaderBottomButtonOption("cbp", "Crop borders pager", aliases = setOf("crop")),
    ReaderBottomButtonOption("cbc", "Crop borders continuous vertical"),
    ReaderBottomButtonOption("cbw", "Crop borders webtoon"),
    ReaderBottomButtonOption("pl", "Page layout", aliases = setOf("layout")),
    ReaderBottomButtonOption("ocr", "GLens OCR lookup", aliases = setOf("lookup", "glens")),
    ReaderBottomButtonOption("ms", "Manga stats", aliases = setOf("stats")),
)

private fun ReaderBottomButtonOption.isEnabledIn(buttons: List<String>): Boolean {
    return buttons.any { saved -> saved == key || saved in aliases }
}

private fun ReaderBottomButtonOption.removeFrom(buttons: List<String>): List<String> {
    val removable = aliases + key
    return buttons.filterNot { it in removable }
}

private fun ChimahonReaderMode.isPagedMode(): Boolean {
    return this == ChimahonReaderMode.LeftToRight || this == ChimahonReaderMode.RightToLeft
}

private fun ChimahonReaderMode.readerSettingsTitle(): String {
    return when (this) {
        ChimahonReaderMode.Webtoon -> "Webtoon"
        ChimahonReaderMode.Vertical -> "Vertical"
        ChimahonReaderMode.LeftToRight -> "Left to right"
        ChimahonReaderMode.RightToLeft -> "Right to left"
    }
}

private fun ChimahonReaderScale.readerSettingsTitle(): String {
    return when (this) {
        ChimahonReaderScale.FitScreen -> "Fit screen"
        ChimahonReaderScale.FitWidth -> "Fit width"
        ChimahonReaderScale.FitHeight -> "Fit height"
    }
}

private fun ChimahonReaderOrientation.readerSettingsTitle(): String {
    return when (this) {
        ChimahonReaderOrientation.Free -> "Free"
        ChimahonReaderOrientation.Portrait -> "Portrait"
        ChimahonReaderOrientation.Landscape -> "Landscape"
        ChimahonReaderOrientation.ReversePortrait -> "Reverse portrait"
        ChimahonReaderOrientation.ReverseLandscape -> "Reverse landscape"
    }
}

private fun ChimahonReaderNavigationMode.readerSettingsTitle(): String {
    return when (this) {
        ChimahonReaderNavigationMode.Automatic -> "Automatic"
        ChimahonReaderNavigationMode.LeftToRight -> "Left to right"
        ChimahonReaderNavigationMode.RightToLeft -> "Right to left"
        ChimahonReaderNavigationMode.Vertical -> "Vertical"
    }
}

private fun ChimahonTapZoneInvert.readerSettingsTitle(): String {
    return when (this) {
        ChimahonTapZoneInvert.None -> "None"
        ChimahonTapZoneInvert.Horizontal -> "Horizontal"
        ChimahonTapZoneInvert.Vertical -> "Vertical"
        ChimahonTapZoneInvert.Both -> "Both"
    }
}

private fun ChimahonDualPageMode.readerSettingsTitle(): String {
    return when (this) {
        ChimahonDualPageMode.Off -> "Off"
        ChimahonDualPageMode.Automatic -> "Automatic"
        ChimahonDualPageMode.Always -> "Always"
    }
}

private fun ChimahonReaderFlashColor.readerSettingsTitle(): String {
    return when (this) {
        ChimahonReaderFlashColor.Black -> "Black"
        ChimahonReaderFlashColor.White -> "White"
        ChimahonReaderFlashColor.WhiteBlack -> "White + black"
    }
}

private fun ChimahonReaderCanvas.readerSettingsColor(): Color {
    return when (this) {
        ChimahonReaderCanvas.Black -> Color.Black
        ChimahonReaderCanvas.Gray -> Color(0xFF242424)
        ChimahonReaderCanvas.White -> Color(0xFFF4F4F4)
    }
}

private fun Int.colorComponent(shift: Int): Int {
    return (this ushr shift) and 0xFF
}

private fun Int.withColorComponentValue(shift: Int, value: Int): Int {
    val mask = 0xFF shl shift
    return (this and mask.inv()) or (value.coerceIn(0, 255) shl shift)
}
