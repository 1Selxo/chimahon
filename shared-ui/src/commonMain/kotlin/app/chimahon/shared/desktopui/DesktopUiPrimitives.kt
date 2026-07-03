package app.chimahon.shared.desktopui

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChimahonDesktopColors(
    val surface: Color,
    val floatingSurface: Color,
    val surfaceVariant: Color,
    val content: Color,
    val secondaryContent: Color,
    val primary: Color,
    val primaryContainer: Color,
    val divider: Color,
    val error: Color,
    val warning: Color,
)

object ChimahonDesktopDefaults {
    @Composable
    fun colors(
        surface: Color = MaterialTheme.colors.surface,
        floatingSurface: Color = MaterialTheme.colors.surface.copy(alpha = 0.96f),
        surfaceVariant: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.07f),
        content: Color = MaterialTheme.colors.onSurface,
        secondaryContent: Color = content.copy(alpha = 0.64f),
        primary: Color = MaterialTheme.colors.primary,
        primaryContainer: Color = primary.copy(alpha = 0.16f),
        divider: Color = content.copy(alpha = 0.12f),
        error: Color = MaterialTheme.colors.error,
        warning: Color = Color(0xFFE6A700),
    ): ChimahonDesktopColors {
        return ChimahonDesktopColors(
            surface = surface,
            floatingSurface = floatingSurface,
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

    val FloatingShape = RoundedCornerShape(18.dp)
    val RowShape = RoundedCornerShape(8.dp)
    val ChipShape = RoundedCornerShape(50)
}

@Composable
fun ChimahonDesktopText(
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
fun ChimahonDesktopIconButton(
    icon: ChimahonDesktopIcon,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
    style: ChimahonDesktopActionStyle = ChimahonDesktopActionStyle.Neutral,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    val tint = when {
        !enabled -> colors.secondaryContent.copy(alpha = 0.42f)
        style == ChimahonDesktopActionStyle.Destructive -> colors.error
        style == ChimahonDesktopActionStyle.Warning -> colors.warning
        selected || style == ChimahonDesktopActionStyle.Primary -> colors.primary
        else -> colors.secondaryContent
    }
    val background = if (selected || style == ChimahonDesktopActionStyle.Primary) {
        colors.primaryContainer
    } else {
        Color.Transparent
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
fun ChimahonDesktopActionChip(
    item: ChimahonDesktopActionItem,
    onClick: (ChimahonDesktopActionItem) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    Row(
        modifier = modifier
            .heightIn(min = 40.dp)
            .clip(ChimahonDesktopDefaults.ChipShape)
            .background(if (item.selected) colors.primaryContainer else colors.surfaceVariant)
            .clickable(enabled = item.enabled && !item.loading) { onClick(item) }
            .padding(start = 10.dp, end = 12.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (item.loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = colors.primary,
            )
        } else {
            Icon(
                imageVector = item.icon.imageVector,
                contentDescription = item.title,
                tint = item.actionTint(colors),
                modifier = Modifier.size(18.dp),
            )
        }
        ChimahonDesktopText(
            text = item.title,
            color = item.actionTint(colors),
            size = 12,
            weight = FontWeight.SemiBold,
        )
        item.shortcut?.let {
            ChimahonDesktopKeyCap(
                text = it,
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonDesktopKeyCap(
    text: String,
    modifier: Modifier = Modifier,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    Box(
        modifier = modifier
            .heightIn(min = 22.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(colors.surface)
            .border(1.dp, colors.divider, RoundedCornerShape(5.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        ChimahonDesktopText(
            text = text,
            color = colors.secondaryContent,
            size = 10,
            weight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun ChimahonDesktopFloatingSurface(
    modifier: Modifier = Modifier,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
    maxWidth: Dp = 620.dp,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = ChimahonDesktopDefaults.FloatingShape,
        color = colors.floatingSurface,
        contentColor = colors.content,
        elevation = 12.dp,
        border = BorderStroke(1.dp, colors.divider),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = maxWidth),
        ) {
            content()
        }
    }
}

@Composable
fun ChimahonDesktopDivider(
    modifier: Modifier = Modifier,
    indent: Dp = 0.dp,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    Row(modifier = modifier.fillMaxWidth()) {
        if (indent > 0.dp) {
            Spacer(Modifier.width(indent))
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(colors.divider),
        )
    }
}

private fun ChimahonDesktopActionItem.actionTint(colors: ChimahonDesktopColors): Color {
    return when {
        !enabled -> colors.secondaryContent.copy(alpha = 0.42f)
        style == ChimahonDesktopActionStyle.Destructive -> colors.error
        style == ChimahonDesktopActionStyle.Warning -> colors.warning
        selected || style == ChimahonDesktopActionStyle.Primary -> colors.primary
        else -> colors.content
    }
}
