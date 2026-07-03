package app.chimahon.shared.themeui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

enum class ChimahonReaderCanvasTone {
    Black,
    Gray,
    White,
}

enum class ChimahonReaderScrimEdge {
    Top,
    Bottom,
}

@Composable
fun ChimahonReaderChromeSurface(
    modifier: Modifier = Modifier,
    floating: Boolean = false,
    content: @Composable RowScope.() -> Unit,
) {
    val tokens = ChimahonThemeUi.readerOverlay
    val alpha = if (floating) tokens.floatingAlpha else tokens.barAlpha
    Surface(
        modifier = modifier,
        color = tokens.chrome.copy(alpha = alpha),
        contentColor = tokens.onChrome,
        shape = RoundedCornerShape(if (floating) 28.dp else 0.dp),
        border = if (floating) BorderStroke(1.dp, tokens.divider.copy(alpha = 0.92f)) else null,
        elevation = if (floating) ChimahonThemeUi.elevation.readerChrome else 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (floating) 56.dp else 64.dp)
                .padding(horizontal = if (floating) 12.dp else 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

@Composable
fun ChimahonReaderOverlayButton(
    imageVector: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    compact: Boolean = false,
) {
    val tokens = ChimahonThemeUi.readerOverlay
    val size = if (compact) {
        ChimahonThemeUi.dimensions.readerSmallControlSize
    } else {
        ChimahonThemeUi.dimensions.readerControlSize
    }
    val background = when {
        selected -> tokens.selectedControl
        enabled -> tokens.control.copy(alpha = tokens.controlAlpha)
        else -> tokens.disabledControl.copy(alpha = 0.72f)
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(background)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = when {
                !enabled -> tokens.secondaryText.copy(alpha = 0.46f)
                selected -> tokens.onChrome
                else -> tokens.onChrome.copy(alpha = 0.92f)
            },
            modifier = Modifier.size(if (compact) 20.dp else 24.dp),
        )
    }
}

@Composable
fun ChimahonReaderChapterPill(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    val tokens = ChimahonThemeUi.readerOverlay
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tokens.control.copy(alpha = tokens.controlAlpha))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (leading != null) {
            leading()
        }
        Box(modifier = Modifier.weight(1f)) {
            androidx.compose.foundation.layout.Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
                    color = tokens.onChrome,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.caption,
                    color = tokens.secondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (trailing != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = trailing,
            )
        }
    }
}

@Composable
fun ChimahonReaderOverlayScrim(
    edge: ChimahonReaderScrimEdge,
    modifier: Modifier = Modifier,
) {
    val tokens = ChimahonThemeUi.readerOverlay
    val gradient = when (edge) {
        ChimahonReaderScrimEdge.Top -> Brush.verticalGradient(
            0f to tokens.topScrim,
            1f to Color.Transparent,
        )
        ChimahonReaderScrimEdge.Bottom -> Brush.verticalGradient(
            0f to Color.Transparent,
            1f to tokens.bottomScrim,
        )
    }
    Box(modifier = modifier.background(gradient))
}

@Composable
fun ChimahonReaderOverlayVeil(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ChimahonThemeUi.readerOverlay.veil),
        content = content,
    )
}

@Composable
fun ChimahonReaderOverlayDivider(
    modifier: Modifier = Modifier,
) {
    Divider(
        modifier = modifier,
        color = ChimahonThemeUi.readerOverlay.divider.copy(alpha = 0.92f),
    )
}

fun ChimahonReaderCanvasTokens.backgroundFor(
    tone: ChimahonReaderCanvasTone,
    pureBlack: Boolean = false,
): Color {
    return when (tone) {
        ChimahonReaderCanvasTone.Black -> if (pureBlack) Color.Black else blackBackground
        ChimahonReaderCanvasTone.Gray -> grayBackground
        ChimahonReaderCanvasTone.White -> whiteBackground
    }
}

fun ChimahonReaderCanvasTokens.contentColorFor(
    tone: ChimahonReaderCanvasTone,
): Color {
    return when (tone) {
        ChimahonReaderCanvasTone.Black,
        ChimahonReaderCanvasTone.Gray,
        -> onDarkCanvas
        ChimahonReaderCanvasTone.White -> onLightCanvas
    }
}

fun ChimahonReaderCanvasTokens.hudBackgroundFor(
    tone: ChimahonReaderCanvasTone,
): Color {
    return when (tone) {
        ChimahonReaderCanvasTone.Black -> blackBackground.copy(alpha = 0.94f)
        ChimahonReaderCanvasTone.Gray -> grayBackground.copy(alpha = 0.92f)
        ChimahonReaderCanvasTone.White -> Color.White.copy(alpha = 0.90f)
    }
}

fun ChimahonReaderCanvasTone.readerBackgroundColor(
    pureBlack: Boolean = false,
): Color {
    return when (this) {
        ChimahonReaderCanvasTone.Black -> if (pureBlack) Color.Black else Color(0xFF0D0D0F)
        ChimahonReaderCanvasTone.Gray -> Color(0xFF242424)
        ChimahonReaderCanvasTone.White -> Color(0xFFF4F4F4)
    }
}

fun ChimahonReaderCanvasTone.readerHudBackgroundColor(): Color {
    return when (this) {
        ChimahonReaderCanvasTone.Black -> Color(0xFF17171B).copy(alpha = 0.94f)
        ChimahonReaderCanvasTone.Gray -> Color(0xFF27272B).copy(alpha = 0.92f)
        ChimahonReaderCanvasTone.White -> Color(0xFFFFFFFF).copy(alpha = 0.90f)
    }
}

fun ChimahonReaderCanvasTone.readerHudContentColor(): Color {
    return when (this) {
        ChimahonReaderCanvasTone.Black,
        ChimahonReaderCanvasTone.Gray,
        -> Color.White
        ChimahonReaderCanvasTone.White -> Color(0xFF242329)
    }
}
