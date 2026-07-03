package app.chimahon.shared.timelineui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
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
fun TimelineUpdateListRow(
    row: TimelineUpdateRow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    density: TimelineRowDensity = TimelineRowDensity.Comfortable,
    colors: TimelineColors = TimelineDefaults.colors(),
    coverContent: @Composable BoxScope.(String?) -> Unit = {
        TimelineCoverPlaceholder(title = row.mangaTitle, colors = colors)
    },
) {
    TimelineSwipeActionBackground(
        start = row.startSwipe,
        end = row.endSwipe,
        colors = colors,
        modifier = modifier,
    ) {
        TimelineRowSurface(
            selected = row.selected,
            onClick = onClick,
            colors = colors,
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = if (density == TimelineRowDensity.Compact) 56.dp else 78.dp)
                        .padding(TimelineDefaults.rowPadding(density)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TimelineLeaderCover(
                        visible = row.leader || density == TimelineRowDensity.Comfortable,
                        title = row.mangaTitle,
                        thumbnailUrl = row.thumbnailUrl,
                        selected = row.selected,
                        density = density,
                        colors = colors,
                        coverContent = coverContent,
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp, end = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(if (density == TimelineRowDensity.Compact) 2.dp else 4.dp),
                    ) {
                        if (row.leader || density == TimelineRowDensity.Comfortable) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = row.mangaTitle,
                                    color = if (row.read) colors.secondaryText else colors.onSurface,
                                    fontSize = if (density == TimelineRowDensity.Compact) 13.sp else 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f),
                                )
                                TimelineBadgeChip(
                                    badge = TimelineBadge(row.updateStateLabel(), if (row.read) TimelineBadgeKind.Muted else TimelineBadgeKind.Active),
                                    colors = colors,
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!row.read) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(18.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(colors.primary),
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(
                                text = row.updateSubtitle(),
                                color = if (row.read) colors.secondaryText else colors.onSurface,
                                fontSize = 13.sp,
                                fontWeight = if (row.read) FontWeight.Normal else FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (density == TimelineRowDensity.Comfortable && row.badges.isNotEmpty()) {
                            TimelineInlineBadges(row.badges, colors)
                        }
                    }
                    TimelineActionRail(row.quickActions, colors)
                }
                TimelineDivider(selected = row.selected, colors = colors, startPadding = 78.dp)
            }
        }
    }
}

@Composable
fun TimelineHistoryListRow(
    row: TimelineHistoryRow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    density: TimelineRowDensity = TimelineRowDensity.Comfortable,
    colors: TimelineColors = TimelineDefaults.colors(),
    coverContent: @Composable BoxScope.(String?) -> Unit = {
        TimelineCoverPlaceholder(title = row.mangaTitle, colors = colors)
    },
) {
    TimelineSwipeActionBackground(
        start = row.startSwipe,
        end = row.endSwipe,
        colors = colors,
        modifier = modifier,
    ) {
        TimelineRowSurface(
            selected = row.selected,
            onClick = onClick,
            colors = colors,
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = if (density == TimelineRowDensity.Compact) 62.dp else 84.dp)
                        .padding(TimelineDefaults.rowPadding(density)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TimelineLeaderCover(
                        visible = true,
                        title = row.mangaTitle,
                        thumbnailUrl = row.thumbnailUrl,
                        selected = row.selected,
                        density = density,
                        colors = colors,
                        coverContent = coverContent,
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp, end = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(if (density == TimelineRowDensity.Compact) 2.dp else 4.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = row.mangaTitle,
                                color = if (row.read) colors.secondaryText else colors.onSurface,
                                fontSize = if (density == TimelineRowDensity.Compact) 13.sp else 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            TimelineBadgeChip(
                                badge = TimelineBadge(row.historyStateLabel(), if (row.read) TimelineBadgeKind.Muted else TimelineBadgeKind.Warning),
                                colors = colors,
                            )
                        }
                        Text(
                            text = row.historySubtitle(),
                            color = colors.secondaryText,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (density == TimelineRowDensity.Comfortable && row.badges.isNotEmpty()) {
                            TimelineInlineBadges(row.badges, colors)
                        }
                        if (density == TimelineRowDensity.Comfortable && row.totalCount > 0.0) {
                            LinearProgressIndicator(
                                progress = row.progressFraction ?: (row.readCount / row.totalCount).toFloat().coerceIn(0f, 1f),
                                color = colors.primary,
                                backgroundColor = colors.divider,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(50)),
                            )
                        }
                    }
                    row.resumeLabel?.takeIf { density == TimelineRowDensity.Comfortable }?.let { resumeLabel ->
                        Text(
                            text = resumeLabel,
                            color = colors.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 6.dp),
                        )
                    }
                    TimelineActionRail(row.quickActions, colors)
                }
                TimelineDivider(selected = row.selected, colors = colors, startPadding = 78.dp)
            }
        }
    }
}

@Composable
fun TimelineSwipeActionBackground(
    start: TimelineSwipeActionVisual?,
    end: TimelineSwipeActionVisual?,
    colors: TimelineColors,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.matchParentSize()) {
            start?.let {
                TimelineSwipeActionPane(
                    visual = it,
                    colors = colors,
                    modifier = Modifier.weight(1f),
                    alignment = Alignment.CenterStart,
                )
            } ?: Spacer(Modifier.weight(1f))
            end?.let {
                TimelineSwipeActionPane(
                    visual = it,
                    colors = colors,
                    modifier = Modifier.weight(1f),
                    alignment = Alignment.CenterEnd,
                )
            } ?: Spacer(Modifier.weight(1f))
        }
        content()
    }
}

@Composable
fun TimelineSwipeActionPane(
    visual: TimelineSwipeActionVisual,
    colors: TimelineColors,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
) {
    val paneColor = when {
        visual.state == TimelineSwipeVisualState.Disabled -> colors.mutedContainer
        visual.destructive -> colors.error.copy(alpha = 0.16f)
        visual.state == TimelineSwipeVisualState.Confirmed -> colors.success.copy(alpha = 0.18f)
        visual.state == TimelineSwipeVisualState.Active -> colors.primary.copy(alpha = 0.18f)
        visual.state == TimelineSwipeVisualState.Revealed -> colors.primary.copy(alpha = 0.11f)
        else -> Color.Transparent
    }
    val contentColor = when {
        visual.state == TimelineSwipeVisualState.Disabled -> colors.secondaryText.copy(alpha = 0.42f)
        visual.destructive -> colors.error
        visual.state == TimelineSwipeVisualState.Confirmed -> colors.success
        else -> colors.primary
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(paneColor)
            .padding(horizontal = 18.dp),
        contentAlignment = alignment,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = visual.icon.imageVector,
                contentDescription = visual.label,
                tint = contentColor,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = visual.label,
                color = contentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun TimelineLeaderCover(
    visible: Boolean,
    title: String,
    thumbnailUrl: String?,
    selected: Boolean,
    density: TimelineRowDensity,
    colors: TimelineColors,
    coverContent: @Composable BoxScope.(String?) -> Unit,
) {
    val coverWidth = TimelineDefaults.coverWidth(density)
    Box(
        modifier = Modifier
            .width(coverWidth)
            .aspectRatio(2f / 3f),
        contentAlignment = Alignment.BottomEnd,
    ) {
        if (visible) {
            TimelineCover(
                title = title,
                thumbnailUrl = thumbnailUrl,
                modifier = Modifier.fillMaxSize(),
                colors = colors,
                content = coverContent,
            )
            TimelineSelectionBadge(
                selected = selected,
                modifier = Modifier.padding(4.dp),
                colors = colors,
            )
        }
    }
}

@Composable
private fun TimelineInlineBadges(
    badges: List<TimelineBadge>,
    colors: TimelineColors,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        badges.take(4).forEach { badge ->
            TimelineBadgeChip(
                badge = badge,
                colors = colors,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}

@Composable
private fun TimelineActionRail(
    actions: List<TimelineQuickAction>,
    colors: TimelineColors,
) {
    if (actions.isEmpty()) return
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        actions.take(3).forEach { action ->
            TimelineQuickActionButton(action = action, colors = colors)
        }
    }
}

@Composable
private fun TimelineDivider(
    selected: Boolean,
    colors: TimelineColors,
    startPadding: androidx.compose.ui.unit.Dp,
) {
    Box(
        modifier = Modifier
            .padding(start = startPadding)
            .fillMaxWidth()
            .height(1.dp)
            .background(if (selected) Color.Transparent else colors.divider),
    )
}

private fun TimelineUpdateRow.updateSubtitle(): String {
    return listOfNotNull(
        chapterTitle,
        scanlator?.takeIf { it.isNotBlank() },
        sourceLabel,
    ).joinToString(" - ")
}

private fun TimelineUpdateRow.updateStateLabel(): String {
    return when {
        read -> "Read"
        lastPageRead > 0L -> "Started"
        else -> "Unread"
    }
}

private fun TimelineHistoryRow.historySubtitle(): String {
    return listOfNotNull(
        chapterTitle,
        readAtLabel ?: readAt?.timelineDateBucket("Read"),
        readDuration.takeIf { it > 0L }?.timelineReadingDurationLabel(),
        sourceLabel,
    ).joinToString(" - ")
}

private fun TimelineHistoryRow.historyStateLabel(): String {
    return when {
        read -> "Read"
        lastPageRead > 0L -> "Started"
        else -> "Unread"
    }
}
