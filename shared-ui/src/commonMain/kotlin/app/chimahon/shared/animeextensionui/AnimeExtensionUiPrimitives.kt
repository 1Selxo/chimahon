package app.chimahon.shared.animeextensionui

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AnimeExtensionUiColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val content: Color,
    val secondaryContent: Color,
    val primary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val divider: Color,
    val warning: Color,
    val error: Color,
    val success: Color,
)

object AnimeExtensionUiDefaults {
    @Composable
    fun colors(
        background: Color = MaterialTheme.colors.background,
        surface: Color = MaterialTheme.colors.surface,
        surfaceVariant: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.06f),
        content: Color = MaterialTheme.colors.onSurface,
        secondaryContent: Color = content.copy(alpha = 0.64f),
        primary: Color = MaterialTheme.colors.primary,
        primaryContainer: Color = MaterialTheme.colors.primary.copy(alpha = 0.14f),
        onPrimaryContainer: Color = MaterialTheme.colors.primary,
        divider: Color = content.copy(alpha = 0.10f),
        warning: Color = Color(0xFF9A6500),
        error: Color = MaterialTheme.colors.error,
        success: Color = Color(0xFF2E7D32),
    ): AnimeExtensionUiColors {
        return AnimeExtensionUiColors(
            background = background,
            surface = surface,
            surfaceVariant = surfaceVariant,
            content = content,
            secondaryContent = secondaryContent,
            primary = primary,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            divider = divider,
            warning = warning,
            error = error,
            success = success,
        )
    }
}

@Composable
fun AnimeExtensionLabel(
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
fun AnimeExtensionAvatarTile(
    marker: String,
    modifier: Modifier = Modifier,
    icon: AnimeExtensionIcon? = null,
    image: (@Composable () -> Unit)? = null,
    size: Dp = 42.dp,
    active: Boolean = false,
    warning: Boolean = false,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    val background = when {
        warning -> colors.error.copy(alpha = 0.12f)
        active -> colors.primaryContainer
        else -> colors.surfaceVariant
    }
    val foreground = when {
        warning -> colors.error
        active -> colors.primary
        else -> colors.secondaryContent
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape((size.value * 0.22f).dp))
            .background(background)
            .border(1.dp, colors.divider, RoundedCornerShape((size.value * 0.22f).dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (image != null) {
            image()
        } else {
            AnimeExtensionLabel(
                text = marker.take(3).uppercase(),
                color = foreground,
                size = if (marker.length > 2) 10 else 13,
                weight = FontWeight.Bold,
            )
        }
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
fun AnimeExtensionIconButton(
    icon: AnimeExtensionIcon,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    val background = if (active) colors.primaryContainer else Color.Transparent
    val tint = when {
        !enabled -> colors.secondaryContent.copy(alpha = 0.38f)
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
fun AnimeExtensionChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    error: Boolean = false,
    warning: Boolean = false,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    val background = when {
        error -> colors.error.copy(alpha = 0.12f)
        warning -> colors.warning.copy(alpha = 0.14f)
        selected -> colors.primaryContainer
        else -> colors.surfaceVariant
    }
    val foreground = when {
        error -> colors.error
        warning -> colors.warning
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
        AnimeExtensionLabel(
            text = text,
            color = foreground,
            size = 11,
            weight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun AnimeExtensionChipRow(
    chips: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(chips, key = { it }) { chip ->
            AnimeExtensionChip(
                text = chip,
                selected = chip == selected,
                onClick = { onSelect(chip) },
                colors = colors,
            )
        }
    }
}

@Composable
fun AnimeExtensionSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    count: Int? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(start = 16.dp, end = 16.dp, top = 13.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimeExtensionLabel(
            text = title.uppercase(),
            color = colors.secondaryContent,
            size = 11,
            weight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(actionLabel)
            }
        } else if (count != null) {
            AnimeExtensionLabel(
                text = "$count",
                color = colors.secondaryContent,
                size = 10,
            )
        }
    }
}

@Composable
fun AnimeExtensionStatusRow(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    icon: AnimeExtensionIcon = AnimeExtensionIcon.Info,
    action: String? = null,
    loading: Boolean = false,
    error: Boolean = false,
    onAction: (() -> Unit)? = null,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimeExtensionAvatarTile(
            marker = if (error) "!" else "",
            icon = icon,
            active = !error,
            warning = error,
            colors = colors,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp, end = 10.dp),
        ) {
            AnimeExtensionLabel(
                text = title,
                color = if (error) colors.error else colors.content,
                size = 14,
                weight = FontWeight.SemiBold,
                maxLines = 2,
            )
            AnimeExtensionLabel(
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
fun AnimeExtensionListSurface(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    warning: Boolean = false,
    enabled: Boolean = true,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
    content: @Composable () -> Unit,
) {
    val borderColor = when {
        warning -> colors.error.copy(alpha = 0.38f)
        selected -> colors.primary.copy(alpha = 0.42f)
        else -> colors.divider
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.62f),
        color = if (selected) colors.primary.copy(alpha = 0.035f) else colors.surface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor),
        elevation = 0.dp,
    ) {
        content()
    }
}

@Composable
fun AnimeExtensionActionTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colors.onPrimary,
            )
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(18.dp),
            )
        }
        Text(text)
    }
}

@Composable
fun AnimeExtensionDivider(
    modifier: Modifier = Modifier,
    startIndent: Dp = 0.dp,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    Row(modifier = modifier.fillMaxWidth()) {
        if (startIndent > 0.dp) Spacer(Modifier.width(startIndent))
        Box(
            modifier = Modifier
                .weight(1f)
                .background(colors.divider)
                .size(height = 1.dp, width = 1.dp),
        )
    }
}

fun Modifier.animeExtensionContentDescription(value: String): Modifier {
    return semantics { contentDescription = value }
}
