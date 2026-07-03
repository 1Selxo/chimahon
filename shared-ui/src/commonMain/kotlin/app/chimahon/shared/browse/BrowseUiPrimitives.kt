package app.chimahon.shared.browse

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

data class BrowseColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val content: Color,
    val secondaryContent: Color,
    val primary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val divider: Color,
    val error: Color,
)

object BrowseDefaults {
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
        error: Color = MaterialTheme.colors.error,
    ): BrowseColors {
        return BrowseColors(
            background = background,
            surface = surface,
            surfaceVariant = surfaceVariant,
            content = content,
            secondaryContent = secondaryContent,
            primary = primary,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            divider = divider,
            error = error,
        )
    }
}

@Composable
fun BrowseLabel(
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
fun BrowseIconButton(
    icon: BrowseIcon,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    val background = when {
        active -> colors.primaryContainer
        else -> Color.Transparent
    }
    val tint = when {
        !enabled -> colors.secondaryContent.copy(alpha = 0.42f)
        active -> colors.primary
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
fun BrowseAvatarTile(
    marker: String,
    modifier: Modifier = Modifier,
    icon: BrowseIcon? = null,
    size: Dp = 42.dp,
    active: Boolean = false,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    val background = if (active) colors.primaryContainer else colors.surfaceVariant
    val foreground = if (active) colors.primary else colors.secondaryContent
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape((size.value * 0.24f).dp))
            .background(background)
            .border(1.dp, colors.divider, RoundedCornerShape((size.value * 0.24f).dp)),
        contentAlignment = Alignment.Center,
    ) {
        BrowseLabel(
            text = marker.take(3).uppercase(),
            color = foreground,
            size = if (marker.length > 2) 10 else 13,
            weight = FontWeight.Bold,
        )
        if (icon != null) {
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
                    contentDescription = "",
                    tint = foreground,
                    modifier = Modifier.size((size.value * 0.20f).dp),
                )
            }
        }
    }
}

@Composable
fun BrowseChip(
    text: String,
    selected: Boolean,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    error: Boolean = false,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    val background = when {
        error -> colors.error.copy(alpha = 0.12f)
        selected -> colors.primaryContainer
        else -> colors.surfaceVariant
    }
    val foreground = when {
        error -> colors.error
        selected -> colors.primary
        else -> colors.secondaryContent
    }
    Box(
        modifier = modifier
            .heightIn(min = 28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        BrowseLabel(
            text = text,
            color = foreground,
            size = 11,
            weight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun BrowseChipRow(
    chips: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(chips, key = { it }) { chip ->
            BrowseChip(
                text = chip,
                selected = chip == selected,
                onClick = { onSelect(chip) },
                colors = colors,
            )
        }
    }
}

@Composable
fun BrowseSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    count: Int? = null,
    detail: String? = null,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(start = 16.dp, end = 16.dp, top = 13.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BrowseLabel(
            text = title.uppercase(),
            color = colors.secondaryContent,
            size = 11,
            weight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        val trailing = detail ?: count?.let { "$it" }
        if (trailing != null) {
            BrowseLabel(
                text = trailing,
                color = colors.secondaryContent,
                size = 10,
            )
        }
    }
}

@Composable
fun BrowseStatusRow(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    icon: BrowseIcon = BrowseIcon.Info,
    action: String? = null,
    loading: Boolean = false,
    error: Boolean = false,
    onAction: (() -> Unit)? = null,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BrowseAvatarTile(
            marker = if (error) "!" else "",
            icon = icon,
            active = !error,
            colors = colors,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp, end = 10.dp),
        ) {
            BrowseLabel(
                text = title,
                color = if (error) colors.error else colors.content,
                size = 14,
                weight = FontWeight.SemiBold,
                maxLines = 2,
            )
            BrowseLabel(
                text = subtitle,
                color = colors.secondaryContent,
                size = 11,
                lineHeight = 16,
                maxLines = 3,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = colors.primary,
            )
        } else if (action != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(action)
            }
        }
    }
}

@Composable
fun BrowseSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    onSearch: (() -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceVariant)
            .border(BorderStroke(1.dp, colors.divider), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = BrowseIcon.Search.imageVector,
            contentDescription = "Search",
            tint = colors.secondaryContent,
            modifier = Modifier.size(19.dp),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (query.isBlank()) {
                BrowseLabel(
                    text = placeholder,
                    color = colors.secondaryContent,
                    size = 13,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = colors.content,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (query.isNotBlank()) {
            BrowseIconButton(
                icon = BrowseIcon.Close,
                contentDescription = "Clear search",
                onClick = { onClear?.invoke() ?: onQueryChange("") },
                colors = colors,
            )
        }
        if (onSearch != null) {
            BrowseIconButton(
                icon = BrowseIcon.Forward,
                contentDescription = "Submit search",
                onClick = onSearch,
                colors = colors,
            )
        }
    }
}

@Composable
fun BrowseDivider(
    modifier: Modifier = Modifier,
    startIndent: Dp = 0.dp,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    Row(modifier = modifier.fillMaxWidth()) {
        if (startIndent > 0.dp) {
            Spacer(modifier = Modifier.width(startIndent))
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(colors.divider),
        )
    }
}

@Composable
fun BrowseCardSurface(
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = colors.surface,
        contentColor = colors.content,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, colors.divider),
        elevation = 0.dp,
        content = content,
    )
}
