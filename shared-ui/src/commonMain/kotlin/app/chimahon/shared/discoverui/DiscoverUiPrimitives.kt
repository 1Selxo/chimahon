package app.chimahon.shared.discoverui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChimahonDiscoverColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val content: Color,
    val secondaryContent: Color,
    val primary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val divider: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
)

object ChimahonDiscoverDefaults {
    @Composable
    fun colors(
        background: Color = MaterialTheme.colors.background,
        surface: Color = MaterialTheme.colors.surface,
        surfaceVariant: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.06f),
        content: Color = MaterialTheme.colors.onSurface,
        secondaryContent: Color = content.copy(alpha = 0.62f),
        primary: Color = MaterialTheme.colors.primary,
        primaryContainer: Color = MaterialTheme.colors.primary.copy(alpha = 0.14f),
        onPrimaryContainer: Color = MaterialTheme.colors.primary,
        divider: Color = content.copy(alpha = 0.10f),
        success: Color = Color(0xFF2E7D32),
        warning: Color = Color(0xFFE08B00),
        error: Color = MaterialTheme.colors.error,
    ): ChimahonDiscoverColors {
        return ChimahonDiscoverColors(
            background = background,
            surface = surface,
            surfaceVariant = surfaceVariant,
            content = content,
            secondaryContent = secondaryContent,
            primary = primary,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            divider = divider,
            success = success,
            warning = warning,
            error = error,
        )
    }
}

@Composable
fun ChimahonDiscoverLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface,
    size: Int = 13,
    weight: FontWeight = FontWeight.Normal,
    maxLines: Int = 1,
    lineHeight: Int = size + 4,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = size.sp,
        fontWeight = weight,
        lineHeight = lineHeight.sp,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun ChimahonDiscoverIconButton(
    icon: ChimahonDiscoverIcon,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    val background = if (selected) colors.primaryContainer else Color.Transparent
    val tint = when {
        !enabled -> colors.secondaryContent.copy(alpha = 0.38f)
        selected -> colors.primary
        else -> colors.secondaryContent
    }
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(enabled = enabled && !loading, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = colors.primary,
            )
        } else {
            Icon(
                imageVector = icon.imageVector,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun ChimahonDiscoverIconTile(
    marker: String,
    icon: ChimahonDiscoverIcon,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    selected: Boolean = false,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    val background = if (selected) colors.primaryContainer else colors.surfaceVariant
    val foreground = if (selected) colors.primary else colors.secondaryContent
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape((size.value * 0.18f).dp))
            .background(background)
            .border(1.dp, colors.divider, RoundedCornerShape((size.value * 0.18f).dp)),
        contentAlignment = Alignment.Center,
    ) {
        ChimahonDiscoverLabel(
            text = marker.uppercase().take(3),
            color = foreground,
            size = if (marker.length > 2) 10 else 13,
            weight = FontWeight.Bold,
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size((size.value * 0.34f).dp)
                .clip(CircleShape)
                .background(colors.surface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon.imageVector,
                contentDescription = null,
                tint = foreground,
                modifier = Modifier.size((size.value * 0.20f).dp),
            )
        }
    }
}

@Composable
fun ChimahonDiscoverChip(
    text: String,
    selected: Boolean,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    tone: ChimahonDiscoverBadgeTone = ChimahonDiscoverBadgeTone.Neutral,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    val toneColor = tone.color(colors)
    val background = when {
        selected -> colors.primaryContainer
        tone != ChimahonDiscoverBadgeTone.Neutral -> toneColor.copy(alpha = 0.14f)
        else -> colors.surfaceVariant
    }
    val foreground = if (selected) colors.primary else toneColor
    Box(
        modifier = modifier
            .heightIn(min = 28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        ChimahonDiscoverLabel(
            text = text,
            color = foreground,
            size = 11,
            weight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun ChimahonDiscoverChipRow(
    chips: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(chips, key = { it }) { chip ->
            ChimahonDiscoverChip(
                text = chip,
                selected = chip == selected,
                onClick = { onSelect(chip) },
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonDiscoverBadgeRow(
    badges: List<ChimahonDiscoverBadge>,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    if (badges.isEmpty()) return
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(badges, key = { "${it.text}:${it.tone}" }) { badge ->
            ChimahonDiscoverChip(
                text = badge.text,
                selected = false,
                tone = badge.tone,
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonDiscoverSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    onClear: () -> Unit = { onQueryChange("") },
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceVariant)
            .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ChimahonDiscoverIcon.Search.imageVector,
            contentDescription = null,
            tint = colors.secondaryContent,
            modifier = Modifier.size(19.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isBlank()) {
                ChimahonDiscoverLabel(
                    text = placeholder,
                    color = colors.secondaryContent,
                    size = 14,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = colors.content,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (query.isNotBlank()) {
            ChimahonDiscoverIconButton(
                icon = ChimahonDiscoverIcon.Close,
                contentDescription = "Clear search",
                onClick = onClear,
                modifier = Modifier.size(32.dp),
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonDiscoverSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    count: Int? = null,
    detail: String? = null,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(start = 16.dp, end = 16.dp, top = 13.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChimahonDiscoverLabel(
            text = title.uppercase(),
            color = colors.secondaryContent,
            size = 11,
            weight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        val trailing = detail ?: count?.toString()
        if (trailing != null) {
            ChimahonDiscoverLabel(
                text = trailing,
                color = colors.secondaryContent,
                size = 10,
            )
        }
    }
}

@Composable
fun ChimahonDiscoverHealthBanner(
    title: String,
    subtitle: String,
    health: ChimahonDiscoverSourceHealth,
    modifier: Modifier = Modifier,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    val tone = when (health) {
        ChimahonDiscoverSourceHealth.Ready -> ChimahonDiscoverBadgeTone.Success
        ChimahonDiscoverSourceHealth.Loading -> ChimahonDiscoverBadgeTone.Primary
        ChimahonDiscoverSourceHealth.Warning -> ChimahonDiscoverBadgeTone.Warning
        ChimahonDiscoverSourceHealth.Error -> ChimahonDiscoverBadgeTone.Error
        ChimahonDiscoverSourceHealth.Disabled -> ChimahonDiscoverBadgeTone.Neutral
    }
    val toneColor = tone.color(colors)
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.divider),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(toneColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                if (health == ChimahonDiscoverSourceHealth.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(19.dp),
                        strokeWidth = 2.dp,
                        color = toneColor,
                    )
                } else {
                    Icon(
                        imageVector = health.icon.imageVector,
                        contentDescription = null,
                        tint = toneColor,
                        modifier = Modifier.size(21.dp),
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                ChimahonDiscoverLabel(
                    text = title,
                    color = colors.content,
                    size = 14,
                    weight = FontWeight.SemiBold,
                )
                ChimahonDiscoverLabel(
                    text = subtitle,
                    color = colors.secondaryContent,
                    size = 12,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            if (action != null && onAction != null) {
                TextButton(onClick = onAction) {
                    Text(text = action)
                }
            }
        }
    }
}

@Composable
fun ChimahonDiscoverDivider(
    modifier: Modifier = Modifier,
    startIndent: Dp = 0.dp,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(startIndent))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(colors.divider),
        )
    }
}

internal fun ChimahonDiscoverBadgeTone.color(colors: ChimahonDiscoverColors): Color {
    return when (this) {
        ChimahonDiscoverBadgeTone.Neutral -> colors.secondaryContent
        ChimahonDiscoverBadgeTone.Primary -> colors.primary
        ChimahonDiscoverBadgeTone.Success -> colors.success
        ChimahonDiscoverBadgeTone.Warning -> colors.warning
        ChimahonDiscoverBadgeTone.Error -> colors.error
    }
}
