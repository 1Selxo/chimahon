package app.chimahon.shared.timelineui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TimelineColors(
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val secondaryText: Color,
    val primary: Color,
    val primaryContainer: Color,
    val divider: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val mutedContainer: Color,
)

object TimelineDefaults {
    @Composable
    fun colors(): TimelineColors {
        val material = MaterialTheme.colors
        return TimelineColors(
            background = material.background,
            surface = material.surface,
            onSurface = material.onSurface,
            secondaryText = material.onSurface.copy(alpha = 0.64f),
            primary = material.primary,
            primaryContainer = material.primary.copy(alpha = 0.14f),
            divider = material.onSurface.copy(alpha = 0.10f),
            success = Color(0xFF00796B),
            warning = Color(0xFF9A6500),
            error = material.error,
            mutedContainer = material.onSurface.copy(alpha = 0.07f),
        )
    }

    fun rowPadding(density: TimelineRowDensity): PaddingValues {
        return when (density) {
            TimelineRowDensity.Compact -> PaddingValues(start = 16.dp, top = 6.dp, end = 8.dp, bottom = 6.dp)
            TimelineRowDensity.Comfortable -> PaddingValues(start = 16.dp, top = 10.dp, end = 8.dp, bottom = 10.dp)
        }
    }

    fun coverWidth(density: TimelineRowDensity) = when (density) {
        TimelineRowDensity.Compact -> 42.dp
        TimelineRowDensity.Comfortable -> 52.dp
    }
}

@Composable
fun TimelineDateHeader(
    title: String,
    detail: String?,
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title.uppercase(),
            color = colors.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (!detail.isNullOrBlank()) {
            Text(
                text = detail,
                color = colors.secondaryText,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun TimelineBadgeChip(
    badge: TimelineBadge,
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
) {
    val chipColors = badge.kind.timelineChipColors(colors)
    Row(
        modifier = modifier
            .heightIn(min = 24.dp)
            .clip(RoundedCornerShape(50))
            .background(chipColors.container)
            .border(BorderStroke(1.dp, chipColors.content.copy(alpha = 0.14f)), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        badge.icon?.let {
            Icon(
                imageVector = it.imageVector,
                contentDescription = null,
                tint = chipColors.content,
                modifier = Modifier.size(13.dp),
            )
        }
        Text(
            text = badge.text,
            color = chipColors.content,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun TimelineQuickActionButton(
    action: TimelineQuickAction,
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
) {
    IconButton(
        onClick = action.onClick,
        enabled = action.enabled,
        modifier = modifier
            .size(40.dp)
            .semantics {
                contentDescription = action.label
                if (action.active) stateDescription = "Active"
            },
    ) {
        Icon(
            imageVector = action.icon.imageVector,
            contentDescription = null,
            tint = when {
                !action.enabled -> colors.secondaryText.copy(alpha = 0.38f)
                action.destructive -> colors.error
                action.active -> colors.primary
                else -> colors.secondaryText
            },
        )
    }
}

@Composable
fun TimelineFilterBar(
    state: TimelineFilterBarState,
    onSearchQueryChange: (String) -> Unit,
    onFilterChipClick: (TimelineFilterChip) -> Unit,
    onSortChipClick: (TimelineFilterChip) -> Unit,
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .border(1.dp, colors.divider)
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TimelineSearchField(
                query = state.searchQuery,
                placeholder = state.searchPlaceholder,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier.weight(1f),
                colors = colors,
            )
            state.resultLabel?.let {
                Spacer(Modifier.width(10.dp))
                Text(
                    text = it,
                    color = colors.secondaryText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
        }
        if (state.activeBadges.isNotEmpty()) {
            TimelineBadgeRow(
                badges = state.activeBadges,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                colors = colors,
            )
        }
        if (state.filterChips.isNotEmpty()) {
            TimelineChipRow(
                chips = state.filterChips,
                onChipClick = onFilterChipClick,
                label = "Filter",
                colors = colors,
            )
        }
        if (state.sortChips.isNotEmpty()) {
            TimelineChipRow(
                chips = state.sortChips,
                onChipClick = onSortChipClick,
                label = "Sort",
                colors = colors,
            )
        }
    }
}

@Composable
fun TimelineSearchField(
    query: String,
    placeholder: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
) {
    Row(
        modifier = modifier
            .heightIn(min = 38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.mutedContainer)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = colors.secondaryText,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isBlank()) {
                Text(
                    text = placeholder,
                    color = colors.secondaryText,
                    fontSize = 14.sp,
                    maxLines = 1,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = TextStyle(color = colors.onSurface, fontSize = 14.sp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun TimelineChipRow(
    chips: List<TimelineFilterChip>,
    onChipClick: (TimelineFilterChip) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = colors.secondaryText,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.padding(start = 16.dp, top = 6.dp, bottom = 4.dp),
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(chips, key = { it.id }) { chip ->
                TimelineFilterChipItem(
                    chip = chip,
                    onClick = { onChipClick(chip) },
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun TimelineFilterChipItem(
    chip: TimelineFilterChip,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
) {
    val content = if (chip.selected) colors.primary else colors.secondaryText
    Row(
        modifier = modifier
            .heightIn(min = 32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (chip.selected) colors.primaryContainer else colors.surface)
            .border(
                1.dp,
                if (chip.selected) colors.primary.copy(alpha = 0.28f) else colors.divider,
                RoundedCornerShape(8.dp),
            )
            .clickable(enabled = chip.enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = chip.label,
            color = if (chip.enabled) content else colors.secondaryText.copy(alpha = 0.36f),
            fontSize = 12.sp,
            fontWeight = if (chip.selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
        )
        chip.badge?.let {
            Text(
                text = it,
                color = content,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun TimelineBadgeRow(
    badges: List<TimelineBadge>,
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(badges, key = { "${it.text}:${it.kind}" }) { badge ->
            TimelineBadgeChip(badge = badge, colors = colors)
        }
    }
}

@Composable
fun TimelineCover(
    title: String,
    thumbnailUrl: String?,
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
    content: @Composable BoxScope.(String?) -> Unit = { TimelineCoverPlaceholder(title = title, colors = colors) },
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(colors.mutedContainer),
        contentAlignment = Alignment.Center,
    ) {
        content(thumbnailUrl)
    }
}

@Composable
fun TimelineCoverPlaceholder(
    title: String,
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Outlined.CollectionsBookmark,
            contentDescription = title,
            tint = colors.secondaryText.copy(alpha = 0.50f),
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
fun TimelineLoadingState(
    title: String = "Loading timeline",
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
) {
    TimelineStateSurface(modifier = modifier, colors = colors) {
        CircularProgressIndicator(color = colors.primary, modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
        Spacer(Modifier.height(14.dp))
        Text(text = title, color = colors.secondaryText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun TimelineEmptyState(
    title: String,
    detail: String,
    icon: TimelineQuickActionIcon,
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
) {
    TimelineStateSurface(modifier = modifier, colors = colors) {
        Icon(imageVector = icon.imageVector, contentDescription = null, tint = colors.primary, modifier = Modifier.size(32.dp))
        Spacer(Modifier.height(12.dp))
        Text(text = title, color = colors.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        if (detail.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(text = detail, color = colors.secondaryText, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
fun TimelineErrorState(
    message: String,
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
) {
    TimelineStateSurface(modifier = modifier, colors = colors) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(colors.error.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "!", color = colors.error, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Text(text = "Timeline unavailable", color = colors.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        Text(text = message, color = colors.secondaryText, fontSize = 13.sp, lineHeight = 18.sp)
    }
}

@Composable
private fun TimelineStateSurface(
    modifier: Modifier,
    colors: TimelineColors,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}

data class TimelineChipColors(
    val container: Color,
    val content: Color,
)

private fun TimelineBadgeKind.timelineChipColors(colors: TimelineColors): TimelineChipColors {
    return when (this) {
        TimelineBadgeKind.Info -> TimelineChipColors(colors.primaryContainer, colors.primary)
        TimelineBadgeKind.Active -> TimelineChipColors(colors.primary.copy(alpha = 0.16f), colors.primary)
        TimelineBadgeKind.Success -> TimelineChipColors(colors.success.copy(alpha = 0.14f), colors.success)
        TimelineBadgeKind.Warning -> TimelineChipColors(colors.warning.copy(alpha = 0.14f), colors.warning)
        TimelineBadgeKind.Error -> TimelineChipColors(colors.error.copy(alpha = 0.14f), colors.error)
        TimelineBadgeKind.Muted -> TimelineChipColors(colors.mutedContainer, colors.secondaryText)
    }
}

@Composable
fun TimelineRowSurface(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick),
        color = if (selected) colors.primaryContainer else colors.surface,
        elevation = if (selected) 1.dp else 0.dp,
    ) {
        content()
    }
}

@Composable
fun TimelineSelectionBadge(
    selected: Boolean,
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
) {
    if (!selected) return
    Box(
        modifier = modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(colors.primary),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = TimelineQuickActionIcon.CheckCircle.imageVector,
            contentDescription = "Selected",
            tint = colors.surface,
            modifier = Modifier.size(15.dp),
        )
    }
}
