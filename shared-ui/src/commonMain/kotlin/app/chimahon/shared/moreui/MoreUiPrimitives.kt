package app.chimahon.shared.moreui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChimahonMoreColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val content: Color,
    val secondaryContent: Color,
    val primary: Color,
    val primaryContainer: Color,
    val divider: Color,
    val error: Color,
    val warning: Color,
)

object ChimahonMoreDefaults {
    @Composable
    fun colors(
        background: Color = MaterialTheme.colors.background,
        surface: Color = MaterialTheme.colors.surface,
        surfaceVariant: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.06f),
        content: Color = MaterialTheme.colors.onSurface,
        secondaryContent: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
        primary: Color = MaterialTheme.colors.primary,
        primaryContainer: Color = MaterialTheme.colors.primary.copy(alpha = 0.14f),
        divider: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.10f),
        error: Color = MaterialTheme.colors.error,
        warning: Color = Color(0xFFB26A00),
    ): ChimahonMoreColors {
        return ChimahonMoreColors(
            background = background,
            surface = surface,
            surfaceVariant = surfaceVariant,
            content = content,
            secondaryContent = secondaryContent,
            primary = primary,
            primaryContainer = primaryContainer,
            divider = divider,
            error = error,
            warning = warning,
        )
    }
}

@Composable
fun ChimahonMoreLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface,
    size: Int = 14,
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
fun ChimahonMoreCategoryHeader(
    title: String,
    modifier: Modifier = Modifier,
    detail: String? = null,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChimahonMoreLabel(
            text = title.uppercase(),
            color = colors.primary,
            size = 12,
            weight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        if (!detail.isNullOrBlank()) {
            ChimahonMoreLabel(
                text = detail,
                color = colors.secondaryContent,
                size = 11,
            )
        }
    }
}

@Composable
fun ChimahonMoreIconTile(
    icon: ChimahonMoreIcon,
    modifier: Modifier = Modifier,
    marker: String? = null,
    active: Boolean = false,
    warning: Boolean = false,
    size: Dp = 42.dp,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    val tint = when {
        warning -> colors.warning
        active -> colors.primary
        else -> colors.secondaryContent
    }
    val background = when {
        warning -> colors.warning.copy(alpha = 0.12f)
        active -> colors.primaryContainer
        else -> colors.surfaceVariant
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape((size.value * 0.24f).dp))
            .background(background)
            .border(1.dp, tint.copy(alpha = 0.16f), RoundedCornerShape((size.value * 0.24f).dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (!marker.isNullOrBlank()) {
            ChimahonMoreLabel(
                text = marker.take(3).uppercase(),
                color = tint,
                size = if (marker.length > 2) 10 else 13,
                weight = FontWeight.Bold,
            )
        } else {
            Icon(
                imageVector = icon.imageVector,
                contentDescription = "",
                tint = tint,
                modifier = Modifier.size((size.value * 0.50f).dp),
            )
        }
    }
}

@Composable
fun ChimahonMoreDivider(
    modifier: Modifier = Modifier,
    startIndent: Dp = 0.dp,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    Row(modifier = modifier.fillMaxWidth()) {
        if (startIndent > 0.dp) Spacer(modifier = Modifier.width(startIndent))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(colors.divider),
        )
    }
}

@Composable
fun ChimahonMoreActionPill(
    action: ChimahonMorePreferenceAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) colors.primaryContainer else Color.Transparent)
            .clickable(enabled = action.enabled && !action.loading, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (action.loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = colors.primary,
            )
        } else {
            ChimahonMoreLabel(
                text = action.label,
                color = if (action.enabled) colors.primary else colors.secondaryContent.copy(alpha = 0.46f),
                size = 12,
                weight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun ChimahonMoreStatusPillView(
    pill: ChimahonMoreStatusPill,
    modifier: Modifier = Modifier,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    val foreground = when {
        pill.warning -> colors.warning
        pill.active -> colors.primary
        else -> colors.secondaryContent
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(foreground.copy(alpha = if (pill.active || pill.warning) 0.14f else 0.08f))
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        ChimahonMoreLabel(
            text = pill.text,
            color = foreground,
            size = 11,
            weight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun ChimahonMoreCard(
    modifier: Modifier = Modifier,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
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
