@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package app.chimahon.shared.desktopui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonDesktopReaderOverlayControls(
    state: ChimahonDesktopReaderOverlayState,
    onAction: (ChimahonDesktopActionItem) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    ChimahonDesktopFloatingSurface(
        modifier = modifier.widthIn(max = 820.dp),
        colors = colors,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    ChimahonDesktopText(
                        text = state.title,
                        color = colors.content,
                        size = 15,
                        weight = FontWeight.Bold,
                    )
                    if (state.subtitle.isNotBlank()) {
                        ChimahonDesktopText(
                            text = state.subtitle,
                            color = colors.secondaryContent,
                            size = 11,
                            maxLines = 2,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
                if (state.pageLabel.isNotBlank()) {
                    ChimahonDesktopKeyCap(
                        text = state.pageLabel,
                        colors = colors,
                    )
                }
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.actions.forEach { item ->
                    ChimahonDesktopActionChip(
                        item = item,
                        onClick = onAction,
                        colors = colors,
                    )
                }
            }

            ChimahonDesktopReaderStateStrip(
                state = state,
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonDesktopReaderStateStrip(
    state: ChimahonDesktopReaderOverlayState,
    modifier: Modifier = Modifier,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (state.modeLabel.isNotBlank()) {
            ChimahonDesktopStatePill(
                text = state.modeLabel,
                icon = ChimahonDesktopIcon.Swap,
                selected = true,
                colors = colors,
            )
        }
        ChimahonDesktopStatePill(
            text = if (state.controlsVisible) "Controls shown" else "Controls hidden",
            icon = ChimahonDesktopIcon.Keyboard,
            selected = state.controlsVisible,
            colors = colors,
        )
        ChimahonDesktopStatePill(
            text = "OCR",
            icon = ChimahonDesktopIcon.Ocr,
            selected = state.ocrActive,
            colors = colors,
        )
        ChimahonDesktopStatePill(
            text = "Crop",
            icon = ChimahonDesktopIcon.Crop,
            selected = state.cropActive,
            colors = colors,
        )
        ChimahonDesktopStatePill(
            text = "Chapters",
            icon = ChimahonDesktopIcon.Chapters,
            selected = state.chapterListActive,
            colors = colors,
        )
        ChimahonDesktopStatePill(
            text = "Stats",
            icon = ChimahonDesktopIcon.Stats,
            selected = state.statsActive,
            colors = colors,
        )
    }
}

@Composable
fun ChimahonDesktopStatePill(
    text: String,
    icon: ChimahonDesktopIcon,
    selected: Boolean,
    modifier: Modifier = Modifier,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    Row(
        modifier = modifier
            .background(
                if (selected) colors.primaryContainer else colors.surfaceVariant,
                ChimahonDesktopDefaults.ChipShape,
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = icon.imageVector,
            contentDescription = text,
            tint = if (selected) colors.primary else colors.secondaryContent,
        )
        ChimahonDesktopText(
            text = text,
            color = if (selected) colors.primary else colors.secondaryContent,
            size = 11,
            weight = FontWeight.SemiBold,
        )
    }
}

fun chimahonDefaultReaderDesktopActions(): List<ChimahonDesktopActionItem> {
    return listOf(
        ChimahonDesktopActionItem("reader.mode", "Mode", "Cycle reading mode", "M", ChimahonDesktopIcon.Swap),
        ChimahonDesktopActionItem("reader.settings", "Settings", "Open fit and zoom controls", "S", ChimahonDesktopIcon.Settings),
        ChimahonDesktopActionItem("reader.chapters", "Chapters", "Open chapter list", "C", ChimahonDesktopIcon.Chapters),
        ChimahonDesktopActionItem("reader.ocr", "OCR", "Toggle GLens OCR lookup", "G", ChimahonDesktopIcon.Ocr),
        ChimahonDesktopActionItem("reader.crop", "Crop", "Toggle crop borders", "F", ChimahonDesktopIcon.Crop),
        ChimahonDesktopActionItem("reader.stats", "Stats", "Show reader statistics", "I", ChimahonDesktopIcon.Stats),
        ChimahonDesktopActionItem("reader.browser", "Source", "Open chapter URL", "O", ChimahonDesktopIcon.Browser),
        ChimahonDesktopActionItem("reader.share", "Share", "Share chapter URL", "Ctrl+Alt+Shift+S", ChimahonDesktopIcon.Share),
    )
}
