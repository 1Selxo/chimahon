package app.chimahon.shared.queueui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
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

@Composable
fun ChimahonQueueStatusChip(
    chip: ChimahonQueueStatusChipUiModel,
    modifier: Modifier = Modifier,
) {
    val colors = chimahonQueueChipColors(chip.kind)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = colors.background,
        contentColor = colors.content,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            text = chip.text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ChimahonQueueProgressChip(
    progress: ChimahonQueueProgress,
    modifier: Modifier = Modifier,
    status: ChimahonQueueStatusChipUiModel? = null,
    showBar: Boolean = true,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f),
        contentColor = MaterialTheme.colors.onSurface,
    ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (status != null) {
                    ChimahonQueueStatusChip(chip = status)
                }
                Text(
                    modifier = Modifier.weight(1f),
                    text = progress.statusLabel ?: progress.displayPercentLabel,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.72f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (progress.statusLabel != null) {
                    Text(
                        text = progress.displayPercentLabel,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.56f),
                        fontSize = 12.sp,
                        maxLines = 1,
                    )
                }
            }
            if (showBar) {
                Spacer(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(top = 34.dp)
                        .height(3.dp),
                )
                LinearProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(50)),
                    progress = progress.normalizedFraction,
                )
            }
        }
    }
}

@Composable
fun ChimahonQueueChapterCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Checkbox(
        modifier = modifier.size(48.dp),
        checked = checked,
        enabled = enabled,
        onCheckedChange = onCheckedChange,
    )
}

@Composable
internal fun chimahonQueueChipColors(kind: ChimahonQueueStatusChipKind): ChimahonQueueChipColors {
    return when (kind) {
        ChimahonQueueStatusChipKind.Info -> ChimahonQueueChipColors(
            background = MaterialTheme.colors.primary.copy(alpha = 0.12f),
            content = MaterialTheme.colors.primary,
        )
        ChimahonQueueStatusChipKind.Active -> ChimahonQueueChipColors(
            background = Color(0xFF2E7D32).copy(alpha = 0.14f),
            content = Color(0xFF2E7D32),
        )
        ChimahonQueueStatusChipKind.Success -> ChimahonQueueChipColors(
            background = Color(0xFF00897B).copy(alpha = 0.14f),
            content = Color(0xFF00796B),
        )
        ChimahonQueueStatusChipKind.Warning -> ChimahonQueueChipColors(
            background = Color(0xFFF9A825).copy(alpha = 0.18f),
            content = Color(0xFF8A6500),
        )
        ChimahonQueueStatusChipKind.Error -> ChimahonQueueChipColors(
            background = MaterialTheme.colors.error.copy(alpha = 0.14f),
            content = MaterialTheme.colors.error,
        )
        ChimahonQueueStatusChipKind.Muted -> ChimahonQueueChipColors(
            background = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
            content = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
        )
    }
}

internal data class ChimahonQueueChipColors(
    val background: Color,
    val content: Color,
)
