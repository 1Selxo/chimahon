package app.chimahon.shared.trackingui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonTrackingMediaSummarySection(
    summaries: List<ChimahonTrackingMediaSummaryUiModel>,
    selectedMediaKind: ChimahonTrackingMediaKind?,
    onMediaKindSelected: (ChimahonTrackingMediaKind?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ChimahonTrackingSectionHeader(
            title = "Media tracking",
            subtitle = "${summaries.sumOf { it.trackedCount }} tracked",
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            summaries.forEach { summary ->
                ChimahonTrackingMediaSummaryCard(
                    summary = summary,
                    selected = selectedMediaKind == summary.mediaKind,
                    onClick = {
                        onMediaKindSelected(
                            if (selectedMediaKind == summary.mediaKind) null else summary.mediaKind,
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun ChimahonTrackingMediaSummaryCard(
    summary: ChimahonTrackingMediaSummaryUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) {
        MaterialTheme.colors.primary.copy(alpha = 0.42f)
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    }
    Surface(
        modifier = modifier
            .width(196.dp)
            .clickable(onClick = onClick),
        color = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = summary.mediaKind.icon(),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colors.primary,
                )
                Text(
                    text = summary.mediaKind.pluralLabel,
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = summary.summaryLabel,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.64f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ChimahonTrackingStatusChip(
                    text = "${summary.completedCount} done",
                    tone = ChimahonTrackingTone.Success,
                )
                if (summary.pendingSyncCount > 0) {
                    ChimahonTrackingStatusChip(
                        text = "${summary.pendingSyncCount} pending",
                        tone = ChimahonTrackingTone.Info,
                    )
                }
                if (summary.errorCount > 0) {
                    ChimahonTrackingStatusChip(
                        text = "${summary.errorCount} failed",
                        tone = ChimahonTrackingTone.Error,
                    )
                }
            }
            summary.nextSyncLabel?.let {
                Text(
                    text = "Next sync $it",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.56f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
