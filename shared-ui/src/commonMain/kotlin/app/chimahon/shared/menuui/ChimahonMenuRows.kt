package app.chimahon.shared.menuui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChimahonMenuColors(
    val surface: Color,
    val onSurface: Color,
    val secondaryText: Color,
    val icon: Color,
    val disabled: Color,
    val selectedContainer: Color,
    val primary: Color,
    val warning: Color,
    val destructive: Color,
    val divider: Color,
)

object ChimahonMenuDefaults {
    @Composable
    fun colors(): ChimahonMenuColors {
        return ChimahonMenuColors(
            surface = MaterialTheme.colors.surface,
            onSurface = MaterialTheme.colors.onSurface,
            secondaryText = MaterialTheme.colors.onSurface.copy(alpha = 0.64f),
            icon = MaterialTheme.colors.onSurface.copy(alpha = 0.72f),
            disabled = MaterialTheme.colors.onSurface.copy(alpha = 0.36f),
            selectedContainer = MaterialTheme.colors.primary.copy(alpha = 0.12f),
            primary = MaterialTheme.colors.primary,
            warning = Color(0xFF9A6500),
            destructive = MaterialTheme.colors.error,
            divider = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
        )
    }
}

@Composable
fun ChimahonMenuActionRow(
    action: ChimahonMenuAction,
    onAction: (ChimahonMenuAction) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonMenuColors = ChimahonMenuDefaults.colors(),
    compact: Boolean = false,
    showSupportingText: Boolean = true,
    interactive: Boolean = true,
) {
    val enabled = action.clickable
    val contentColor = action.contentColor(colors)
    val secondaryColor = if (action.enabled) colors.secondaryText else colors.disabled
    val rowRole = when (action.role) {
        ChimahonMenuActionRole.Toggle -> Role.Checkbox
        ChimahonMenuActionRole.Radio -> Role.RadioButton
        ChimahonMenuActionRole.Normal -> Role.Button
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (action.active) colors.selectedContainer else Color.Transparent)
            .then(
                if (interactive) {
                    Modifier.clickable(
                        enabled = enabled,
                        role = rowRole,
                        onClick = { onAction(action) },
                    )
                } else {
                    Modifier
                },
            )
            .semantics {
                if (action.role != ChimahonMenuActionRole.Normal) {
                    stateDescription = if (action.active) "Selected" else "Not selected"
                }
            }
            .padding(
                horizontal = if (compact) 12.dp else 16.dp,
                vertical = if (compact) 8.dp else 12.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChimahonMenuLeadingIcon(
            icon = action.icon?.imageVector,
            active = action.active,
            busy = action.busy,
            enabled = action.enabled,
            colors = colors,
        )
        Spacer(Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = action.label,
                color = contentColor,
                fontSize = 16.sp,
                fontWeight = if (action.active) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val supporting = action.supportingText
            if (showSupportingText && !supporting.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = supporting,
                    color = secondaryColor,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        ChimahonMenuTrailingContent(
            action = action,
            colors = colors,
        )
    }
}

@Composable
fun ChimahonMenuSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    colors: ChimahonMenuColors = ChimahonMenuDefaults.colors(),
) {
    Text(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        text = title,
        color = colors.secondaryText,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun ChimahonMenuLeadingIcon(
    icon: ImageVector?,
    active: Boolean,
    busy: Boolean,
    enabled: Boolean,
    colors: ChimahonMenuColors,
) {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        when {
            busy -> CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = colors.primary,
                strokeWidth = 2.dp,
            )
            icon != null -> Icon(
                imageVector = icon,
                contentDescription = null,
                tint = when {
                    !enabled -> colors.disabled
                    active -> colors.primary
                    else -> colors.icon
                },
            )
        }
    }
}

@Composable
private fun ChimahonMenuTrailingContent(
    action: ChimahonMenuAction,
    colors: ChimahonMenuColors,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!action.badge.isNullOrBlank()) {
            ChimahonMenuBadge(
                label = action.badge,
                colors = colors,
                active = action.active,
            )
        }
        if (!action.shortcut.isNullOrBlank()) {
            Text(
                text = action.shortcut,
                color = colors.secondaryText,
                fontSize = 12.sp,
                maxLines = 1,
            )
        }
        when (action.role) {
            ChimahonMenuActionRole.Radio -> RadioButton(
                selected = action.selected || action.checked,
                enabled = action.enabled,
                onClick = null,
            )
            ChimahonMenuActionRole.Toggle -> {
                if (action.checked) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = if (action.enabled) colors.primary else colors.disabled,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            ChimahonMenuActionRole.Normal -> Unit
        }
    }
}

@Composable
private fun ChimahonMenuBadge(
    label: String,
    colors: ChimahonMenuColors,
    active: Boolean,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (active) colors.primary else colors.selectedContainer)
            .padding(horizontal = 7.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (active) colors.surface else colors.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

private fun ChimahonMenuAction.contentColor(colors: ChimahonMenuColors): Color {
    if (!enabled) return colors.disabled
    return when (tone) {
        ChimahonMenuActionTone.Normal -> colors.onSurface
        ChimahonMenuActionTone.Primary -> colors.primary
        ChimahonMenuActionTone.Warning -> colors.warning
        ChimahonMenuActionTone.Destructive -> colors.destructive
    }
}
