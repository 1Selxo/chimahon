package app.chimahon.shared.downloadhistoryui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChimahonDownloadHistoryColors(
    val background: Color,
    val surface: Color,
    val selectedSurface: Color,
    val content: Color,
    val secondaryContent: Color,
    val outline: Color,
    val primary: Color,
    val primaryContainer: Color,
    val error: Color,
    val warning: Color,
    val success: Color,
)

object ChimahonDownloadHistoryDefaults {
    @Composable
    fun colors(): ChimahonDownloadHistoryColors {
        val colors = MaterialTheme.colors
        return ChimahonDownloadHistoryColors(
            background = colors.background,
            surface = colors.surface,
            selectedSurface = colors.primary.copy(alpha = 0.10f),
            content = colors.onSurface,
            secondaryContent = colors.onSurface.copy(alpha = 0.64f),
            outline = colors.onSurface.copy(alpha = 0.12f),
            primary = colors.primary,
            primaryContainer = colors.primary.copy(alpha = 0.14f),
            error = colors.error,
            warning = Color(0xFFB26A00),
            success = Color(0xFF2E7D32),
        )
    }
}
@Composable
fun ChimahonDownloadHistoryRowSurface(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
    content: @Composable () -> Unit,
) {
    Surface(
        color = if (selected) colors.selectedSurface else colors.surface,
        contentColor = colors.content,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        elevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp),
        ) {
            content()
        }
    }
}

@Composable
fun ChimahonDownloadHistoryCover(
    mediaKind: ChimahonMediaKind,
    thumbnailUrl: String?,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
    coverContent: @Composable BoxScope.(String?) -> Unit = {
        ChimahonDownloadHistoryCoverPlaceholder(mediaKind = mediaKind, colors = colors)
    },
) {
    Box(
        modifier = modifier
            .size(width = 48.dp, height = 64.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(colors.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        coverContent(thumbnailUrl)
    }
}

@Composable
fun ChimahonDownloadHistoryCoverPlaceholder(
    mediaKind: ChimahonMediaKind,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    Icon(
        imageVector = mediaKind.icon,
        contentDescription = mediaKind.title,
        tint = colors.primary,
        modifier = modifier.size(24.dp),
    )
}

@Composable
fun ChimahonDownloadHistoryChipRow(
    chips: List<ChimahonDownloadHistoryChip>,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    if (chips.isEmpty()) return
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        chips.take(5).forEach { chip ->
            ChimahonDownloadHistoryChip(
                chip = chip,
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonDownloadHistoryChip(
    chip: ChimahonDownloadHistoryChip,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    val tint = when (chip.kind) {
        ChimahonDownloadHistoryChipKind.Source -> colors.secondaryContent
        ChimahonDownloadHistoryChipKind.Language -> colors.primary
        ChimahonDownloadHistoryChipKind.Media -> colors.primary
        ChimahonDownloadHistoryChipKind.Status -> colors.success
        ChimahonDownloadHistoryChipKind.Warning -> colors.warning
        ChimahonDownloadHistoryChipKind.Local -> colors.secondaryContent
    }
    Row(
        modifier = modifier
            .height(26.dp)
            .clip(CircleShape)
            .background(if (chip.selected) tint.copy(alpha = 0.18f) else colors.content.copy(alpha = 0.06f))
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = chip.kind.imageVector,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = chip.text,
            color = colors.content.copy(alpha = if (chip.enabled) 0.78f else 0.38f),
            fontSize = 11.sp,
            fontWeight = if (chip.selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ChimahonDownloadHistoryActionButtons(
    actions: List<ChimahonDownloadHistoryAction>,
    modifier: Modifier = Modifier,
    maxVisibleActions: Int = 3,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        actions.take(maxVisibleActions).forEach { action ->
            ChimahonDownloadHistoryActionButton(
                action = action,
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonDownloadHistoryActionButton(
    action: ChimahonDownloadHistoryAction,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    IconButton(
        onClick = action.onClick,
        enabled = action.enabled,
        modifier = modifier.size(40.dp),
    ) {
        Icon(
            imageVector = action.icon.imageVector,
            contentDescription = action.label,
            tint = when {
                !action.enabled -> colors.secondaryContent.copy(alpha = 0.36f)
                action.destructive -> colors.error
                action.role == ChimahonDownloadHistoryActionRole.Primary -> colors.primary
                else -> colors.secondaryContent
            },
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
fun ChimahonDownloadHistoryProgressBar(
    progress: ChimahonDownloadHistoryProgress,
    status: ChimahonDownloadHistoryStatus?,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    val progressColor = when (status) {
        ChimahonDownloadHistoryStatus.Failed,
        ChimahonDownloadHistoryStatus.Canceled -> colors.error
        ChimahonDownloadHistoryStatus.Paused -> colors.warning
        ChimahonDownloadHistoryStatus.Complete -> colors.success
        else -> colors.primary
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LinearProgressIndicator(
            progress = progress.normalizedFraction,
            color = progressColor,
            backgroundColor = colors.content.copy(alpha = 0.10f),
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(CircleShape),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = progress.displayPercentLabel,
            color = colors.secondaryContent,
            fontSize = 11.sp,
            maxLines = 1,
        )
    }
}
