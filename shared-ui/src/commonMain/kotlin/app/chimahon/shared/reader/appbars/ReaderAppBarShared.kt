package app.chimahon.shared.reader.appbars

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp

@Composable
internal fun readerAppBarSurfaceColor(): Color {
    return MaterialTheme.colors.surface.copy(alpha = if (isSystemInDarkTheme()) 0.90f else 0.95f)
}

@Composable
internal fun ReaderIconButton(
    icon: ReaderAppBarIcon,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colors.onSurface,
    enabled: Boolean = true,
    active: Boolean = false,
    loading: Boolean = false,
    activeTint: Color = MaterialTheme.colors.primary,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
            if (active) stateDescription = "Active"
        },
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = if (active) activeTint else tint,
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    imageVector = icon.imageVector,
                    contentDescription = null,
                    tint = when {
                        !enabled -> tint.copy(alpha = 0.36f)
                        active -> activeTint
                        else -> tint
                    },
                )
            }
        }
    }
}

@Composable
internal fun ReaderFilledIconButton(
    icon: ReaderAppBarIcon,
    contentDescription: String,
    colors: ReaderAppBarColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    rotateDegrees: Float = 0f,
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .alpha(if (enabled) 1f else 0.46f)
            .clip(CircleShape)
            .background(colors.surface)
            .semantics {
                this.contentDescription = contentDescription
                stateDescription = if (enabled) "Available" else "Unavailable"
            }
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon.imageVector,
            contentDescription = null,
            tint = if (enabled) colors.primary else colors.disabledContent,
            modifier = Modifier.graphicsRotate(rotateDegrees),
        )
    }
}

internal fun Modifier.graphicsRotate(degrees: Float): Modifier {
    return if (degrees == 0f) this else rotate(degrees)
}
