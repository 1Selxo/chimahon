package app.chimahon.shared.downloadsui

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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChimahonDownloadUiColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val content: Color,
    val secondaryContent: Color,
    val primary: Color,
    val primaryContainer: Color,
    val divider: Color,
    val error: Color,
)

object ChimahonDownloadUiDefaults {
    @Composable
    fun colors(
        background: Color = MaterialTheme.colors.background,
        surface: Color = MaterialTheme.colors.surface,
        surfaceVariant: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.06f),
        content: Color = MaterialTheme.colors.onSurface,
        secondaryContent: Color = content.copy(alpha = 0.62f),
        primary: Color = MaterialTheme.colors.primary,
        primaryContainer: Color = primary.copy(alpha = 0.14f),
        divider: Color = content.copy(alpha = 0.10f),
        error: Color = MaterialTheme.colors.error,
    ): ChimahonDownloadUiColors {
        return ChimahonDownloadUiColors(
            background = background,
            surface = surface,
            surfaceVariant = surfaceVariant,
            content = content,
            secondaryContent = secondaryContent,
            primary = primary,
            primaryContainer = primaryContainer,
            divider = divider,
            error = error,
        )
    }
}

@Composable
fun DownloadLabel(
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
fun DownloadIconButton(
    icon: ChimahonDownloadUiIcon,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
    destructive: Boolean = false,
    colors: ChimahonDownloadUiColors = ChimahonDownloadUiDefaults.colors(),
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
fun DownloadStatusChip(
    text: String,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    error: Boolean = false,
    colors: ChimahonDownloadUiColors = ChimahonDownloadUiDefaults.colors(),
) {
    val background = when {
        error -> colors.error.copy(alpha = 0.12f)
        active -> colors.primaryContainer
        else -> colors.surfaceVariant
    }
    val foreground = when {
        error -> colors.error
        active -> colors.primary
        else -> colors.secondaryContent
    }
    Box(
        modifier = modifier
            .heightIn(min = 26.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(background)
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        DownloadLabel(
            text = text,
            color = foreground,
            size = 10,
            weight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun DownloadProgressBar(
    fraction: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    colors: ChimahonDownloadUiColors = ChimahonDownloadUiDefaults.colors(),
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
fun DownloadSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search downloads",
    colors: ChimahonDownloadUiColors = ChimahonDownloadUiDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceVariant)
            .border(BorderStroke(1.dp, colors.divider), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ChimahonDownloadUiIcon.Search.imageVector,
            contentDescription = "Search downloads",
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
                DownloadLabel(placeholder, color = colors.secondaryContent, size = 13)
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
fun DownloadSectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadUiColors = ChimahonDownloadUiDefaults.colors(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 7.dp),
    ) {
        DownloadLabel(
            text = title.uppercase(),
            color = colors.secondaryContent,
            size = 11,
            weight = FontWeight.Bold,
        )
        DownloadLabel(
            text = subtitle,
            color = colors.secondaryContent,
            size = 10,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
fun DownloadStatusCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    icon: ChimahonDownloadUiIcon = ChimahonDownloadUiIcon.Download,
    action: String? = null,
    loading: Boolean = false,
    error: Boolean = false,
    onAction: (() -> Unit)? = null,
    colors: ChimahonDownloadUiColors = ChimahonDownloadUiDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (error) colors.error.copy(alpha = 0.12f) else colors.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon.imageVector,
                contentDescription = "",
                tint = if (error) colors.error else colors.primary,
                modifier = Modifier.size(21.dp),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            DownloadLabel(
                text = title,
                color = if (error) colors.error else colors.content,
                size = 14,
                weight = FontWeight.SemiBold,
                maxLines = 2,
            )
            DownloadLabel(
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
            androidx.compose.material.TextButton(onClick = onAction) {
                Text(action)
            }
        }
    }
}

@Composable
fun DownloadCardSurface(
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadUiColors = ChimahonDownloadUiDefaults.colors(),
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = colors.surface,
        contentColor = colors.content,
        border = BorderStroke(1.dp, colors.divider),
        elevation = 0.dp,
        content = content,
    )
}

@Composable
fun DownloadDivider(
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadUiColors = ChimahonDownloadUiDefaults.colors(),
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(colors.divider),
    )
}

@Composable
fun DownloadStatPill(
    label: String,
    value: String,
    icon: ChimahonDownloadUiIcon,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colors.primary,
    colors: ChimahonDownloadUiColors = ChimahonDownloadUiDefaults.colors(),
) {
    Row(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.background)
            .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icon.imageVector, "", tint = tint, modifier = Modifier.size(16.dp))
        DownloadLabel(label, color = colors.secondaryContent, size = 10)
        DownloadLabel(value, color = colors.content, size = 11, weight = FontWeight.SemiBold)
    }
}
