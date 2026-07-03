package app.chimahon.shared.novelimportui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChimahonNovelImportUiColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val content: Color,
    val secondaryContent: Color,
    val primary: Color,
    val primaryContainer: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val divider: Color,
)

object ChimahonNovelImportUiDefaults {
    @Composable
    fun colors(
        background: Color = MaterialTheme.colors.background,
        surface: Color = MaterialTheme.colors.surface,
        surfaceVariant: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.06f),
        content: Color = MaterialTheme.colors.onSurface,
        secondaryContent: Color = content.copy(alpha = 0.62f),
        primary: Color = MaterialTheme.colors.primary,
        primaryContainer: Color = primary.copy(alpha = 0.14f),
        success: Color = Color(0xFF2E7D4F),
        warning: Color = Color(0xFFC27A25),
        error: Color = MaterialTheme.colors.error,
        divider: Color = content.copy(alpha = 0.10f),
    ): ChimahonNovelImportUiColors {
        return ChimahonNovelImportUiColors(
            background = background,
            surface = surface,
            surfaceVariant = surfaceVariant,
            content = content,
            secondaryContent = secondaryContent,
            primary = primary,
            primaryContainer = primaryContainer,
            success = success,
            warning = warning,
            error = error,
            divider = divider,
        )
    }
}

@Composable
fun NovelImportLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface,
    size: Int = 13,
    weight: FontWeight = FontWeight.Normal,
    maxLines: Int = 1,
    lineHeight: Int = size + 4,
    textAlign: TextAlign? = null,
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
        textAlign = textAlign,
    )
}

@Composable
fun NovelImportIconButton(
    icon: ChimahonNovelImportUiIcon,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
    destructive: Boolean = false,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    val tint = when {
        !enabled -> colors.secondaryContent.copy(alpha = 0.36f)
        destructive -> colors.error
        active -> colors.primary
        else -> colors.secondaryContent
    }
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (active) colors.primaryContainer else Color.Transparent)
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
fun NovelImportStatusChip(
    text: String,
    kind: ChimahonNovelImportStatusKind,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    val foreground = kind.foreground(colors)
    val background = kind.background(colors)
    Box(
        modifier = modifier
            .heightIn(min = 25.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        NovelImportLabel(
            text = text,
            color = foreground,
            size = 10,
            weight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun NovelImportProgressBar(
    fraction: Float,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
    color: Color = colors.primary,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(colors.surfaceVariant),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .height(5.dp)
                .background(color),
        )
    }
}

@Composable
fun NovelImportLinearProgress(
    progress: ChimahonNovelImportProgressUiModel,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        LinearProgressIndicator(
            progress = progress.normalizedFraction,
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(100.dp)),
            color = colors.primary,
            backgroundColor = colors.surfaceVariant,
        )
        progress.displayLabel?.let { label ->
            NovelImportLabel(
                text = label,
                color = colors.secondaryContent,
                size = 11,
            )
        }
    }
}

@Composable
fun NovelImportSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search staged files",
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceVariant)
            .border(BorderStroke(1.dp, colors.divider), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ChimahonNovelImportUiIcon.Search.imageVector,
            contentDescription = "Search",
            tint = colors.secondaryContent,
            modifier = Modifier.size(18.dp),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (query.isBlank()) {
                NovelImportLabel(
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
    }
}

@Composable
fun NovelImportSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            NovelImportLabel(
                text = title.uppercase(),
                color = colors.secondaryContent,
                size = 11,
                weight = FontWeight.Bold,
            )
            if (!subtitle.isNullOrBlank()) {
                NovelImportLabel(
                    text = subtitle,
                    color = colors.secondaryContent,
                    size = 10,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        if (actionLabel != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
fun NovelImportCardSurface(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    fillMaxWidth: Boolean = true,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }
    val widthModifier = if (fillMaxWidth) modifier.fillMaxWidth() else modifier
    Surface(
        modifier = widthModifier
            .clip(RoundedCornerShape(8.dp))
            .then(clickableModifier),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) colors.primaryContainer else colors.surface,
        contentColor = colors.content,
        border = BorderStroke(
            1.dp,
            if (selected) colors.primary.copy(alpha = 0.36f) else colors.divider,
        ),
        elevation = 0.dp,
        content = content,
    )
}

@Composable
fun NovelImportStatusCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    icon: ChimahonNovelImportUiIcon = ChimahonNovelImportUiIcon.Import,
    kind: ChimahonNovelImportStatusKind = ChimahonNovelImportStatusKind.Info,
    progress: ChimahonNovelImportProgressUiModel? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    NovelImportCardSurface(
        modifier = modifier,
        colors = colors,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(kind.background(colors)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon.imageVector,
                    contentDescription = null,
                    tint = kind.foreground(colors),
                    modifier = Modifier.size(21.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                NovelImportLabel(
                    text = title,
                    color = if (kind == ChimahonNovelImportStatusKind.Error) colors.error else colors.content,
                    size = 14,
                    weight = FontWeight.SemiBold,
                    maxLines = 2,
                )
                NovelImportLabel(
                    text = subtitle,
                    color = colors.secondaryContent,
                    size = 11,
                    lineHeight = 16,
                    maxLines = 3,
                )
                progress?.takeIf { it.normalizedFraction > 0f }?.let {
                    NovelImportProgressBar(
                        fraction = it.normalizedFraction,
                        colors = colors,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
            if (actionLabel != null && onAction != null) {
                TextButton(onClick = onAction) {
                    Text(actionLabel)
                }
            }
        }
    }
}

@Composable
fun NovelImportCoverPlaceholder(
    title: String,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    val initials = title
        .split(' ', '-', '_')
        .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
        .take(2)
        .joinToString("")
        .ifBlank { "LN" }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(colors.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        NovelImportLabel(
            text = initials,
            color = colors.primary,
            size = if (initials.length > 2) 18 else 22,
            weight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

@Composable
fun NovelImportCenteredState(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    icon: ChimahonNovelImportUiIcon = ChimahonNovelImportUiIcon.File,
    kind: ChimahonNovelImportStatusKind = ChimahonNovelImportStatusKind.Info,
    loading: Boolean = false,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(kind.background(colors)),
                contentAlignment = Alignment.Center,
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        color = colors.primary,
                        strokeWidth = 3.dp,
                    )
                } else {
                    Icon(
                        imageVector = icon.imageVector,
                        contentDescription = null,
                        tint = kind.foreground(colors),
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
            NovelImportLabel(
                text = title,
                color = colors.content,
                size = 19,
                weight = FontWeight.SemiBold,
                maxLines = 2,
                textAlign = TextAlign.Center,
            )
            NovelImportLabel(
                text = body,
                color = colors.secondaryContent,
                size = 13,
                lineHeight = 18,
                maxLines = 4,
                textAlign = TextAlign.Center,
            )
            if (actionLabel != null && onAction != null) {
                TextButton(onClick = onAction) {
                    Text(actionLabel)
                }
            }
        }
    }
}

private fun ChimahonNovelImportStatusKind.foreground(colors: ChimahonNovelImportUiColors): Color {
    return when (this) {
        ChimahonNovelImportStatusKind.Info -> colors.secondaryContent
        ChimahonNovelImportStatusKind.Active -> colors.primary
        ChimahonNovelImportStatusKind.Success -> colors.success
        ChimahonNovelImportStatusKind.Warning -> colors.warning
        ChimahonNovelImportStatusKind.Error -> colors.error
        ChimahonNovelImportStatusKind.Muted -> colors.secondaryContent.copy(alpha = 0.72f)
    }
}

private fun ChimahonNovelImportStatusKind.background(colors: ChimahonNovelImportUiColors): Color {
    return when (this) {
        ChimahonNovelImportStatusKind.Info -> colors.surfaceVariant
        ChimahonNovelImportStatusKind.Active -> colors.primaryContainer
        ChimahonNovelImportStatusKind.Success -> colors.success.copy(alpha = 0.14f)
        ChimahonNovelImportStatusKind.Warning -> colors.warning.copy(alpha = 0.14f)
        ChimahonNovelImportStatusKind.Error -> colors.error.copy(alpha = 0.12f)
        ChimahonNovelImportStatusKind.Muted -> colors.surfaceVariant
    }
}
