package app.chimahon.shared.themeui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonScreenSurface(
    modifier: Modifier = Modifier,
    color: Color = ChimahonThemeUi.colors.background,
    contentColor: Color = ChimahonThemeUi.colors.onSurface,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
    ) {
        Column(content = content)
    }
}

@Composable
fun ChimahonSurface(
    modifier: Modifier = Modifier,
    color: Color = ChimahonThemeUi.colors.surface,
    contentColor: Color = ChimahonThemeUi.colors.onSurface,
    shape: Shape = RoundedCornerShape(ChimahonThemeUi.radii.medium),
    border: BorderStroke? = null,
    elevation: androidx.compose.ui.unit.Dp = ChimahonThemeUi.elevation.none,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = shape,
        border = border,
        elevation = elevation,
        content = content,
    )
}

@Composable
fun ChimahonCard(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    outlined: Boolean = true,
    enabled: Boolean = true,
    compact: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = ChimahonThemeUi.colors
    val cardTokens = ChimahonThemeUi.card
    val shape = RoundedCornerShape(cardTokens.cornerRadius)
    val background = if (selected) {
        colors.primary.copy(alpha = cardTokens.selectedContainerAlpha)
    } else {
        colors.surface
    }
    val cardModifier = if (compact) {
        modifier.heightIn(min = cardTokens.compactMinHeight)
    } else {
        modifier
    }
    val border = if (outlined || selected) {
        BorderStroke(
            width = 1.dp,
            color = if (selected) colors.primary.copy(alpha = 0.46f) else colors.outline,
        )
    } else {
        null
    }
    Surface(
        modifier = cardModifier.thenClickable(enabled = enabled, onClick = onClick, role = Role.Button),
        color = background,
        contentColor = colors.onSurface,
        shape = shape,
        border = border,
        elevation = if (selected) ChimahonThemeUi.elevation.card else ChimahonThemeUi.elevation.none,
    ) {
        Column(
            modifier = Modifier.padding(
                if (compact) cardTokens.compactPadding else ChimahonThemeUi.dimensions.cardPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content,
        )
    }
}

@Composable
fun ChimahonNavigationRailSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val tokens = ChimahonThemeUi.navigationRail
    Surface(
        modifier = modifier.width(tokens.width),
        color = tokens.container,
        contentColor = tokens.unselectedLabel,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}

@Composable
fun ChimahonBrowseLibrarySurface(
    modifier: Modifier = Modifier,
    browse: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val tokens = ChimahonThemeUi.browseLibrary
    ChimahonScreenSurface(
        modifier = modifier,
        color = if (browse) tokens.browseBackground else tokens.libraryBackground,
        contentColor = ChimahonThemeUi.colors.onSurface,
        content = content,
    )
}

@Composable
fun ChimahonAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    compact: Boolean = false,
    showDivider: Boolean = true,
    backgroundColor: Color = ChimahonThemeUi.colors.surface.copy(
        alpha = ChimahonThemeUi.appBar.backgroundAlpha,
    ),
    contentColor: Color = ChimahonThemeUi.colors.onSurface,
) {
    val appBar = ChimahonThemeUi.appBar
    val height = if (compact) appBar.compactHeight else appBar.height
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = height)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (navigationIcon != null) {
                Box(
                    modifier = Modifier.size(ChimahonThemeUi.dimensions.iconButtonSize),
                    contentAlignment = Alignment.Center,
                ) {
                    navigationIcon()
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = if (navigationIcon == null) ChimahonThemeUi.dimensions.compactScreenPadding else 4.dp,
                        end = 8.dp,
                    ),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                    color = contentColor.copy(alpha = appBar.titleAlpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.caption,
                        color = contentColor.copy(alpha = appBar.subtitleAlpha),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Row(
                modifier = Modifier.padding(end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = actions,
            )
        }
        if (showDivider) {
            Divider(color = contentColor.copy(alpha = appBar.dividerAlpha))
        }
    }
}

@Composable
fun ChimahonListItem(
    headline: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    overlineText: String? = null,
    leadingIcon: ImageVector? = null,
    leadingContentDescription: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    dense: Boolean = false,
    compact: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val colors = ChimahonThemeUi.colors
    val listItem = ChimahonThemeUi.listItem
    val minHeight = when {
        compact -> ChimahonThemeUi.dimensions.compactListItemMinHeight
        dense -> listItem.denseMinHeight
        else -> listItem.minHeight
    }
    val horizontalPadding = if (compact) listItem.compactHorizontalPadding else listItem.horizontalPadding
    val verticalPadding = if (compact) listItem.compactVerticalPadding else listItem.verticalPadding
    val leadingBoxSize = if (compact) listItem.compactLeadingBoxSize else listItem.leadingBoxSize
    val background = if (selected) {
        colors.primary.copy(alpha = listItem.selectedContainerAlpha)
    } else {
        Color.Transparent
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .clip(RoundedCornerShape(ChimahonThemeUi.radii.medium))
            .background(background)
            .thenClickable(enabled = enabled, onClick = onClick, role = Role.Button)
            .padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null || leadingIcon != null) {
            Box(
                modifier = Modifier.size(leadingBoxSize),
                contentAlignment = Alignment.Center,
            ) {
                if (leading != null) {
                    leading()
                } else if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = leadingContentDescription,
                        modifier = Modifier.size(listItem.iconSize),
                        tint = if (selected) colors.primary else colors.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            if (!overlineText.isNullOrBlank()) {
                Text(
                    text = overlineText,
                    style = MaterialTheme.typography.overline,
                    color = colors.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = headline,
                style = MaterialTheme.typography.body1,
                color = colors.onSurface.copy(alpha = if (enabled) 0.94f else 0.38f),
                maxLines = if (supportingText == null) 2 else 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!supportingText.isNullOrBlank()) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.body2,
                    color = colors.onSurface.copy(
                        alpha = if (enabled) listItem.supportingTextAlpha else 0.38f,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                trailing()
            }
        }
    }
}

@Composable
fun ChimahonSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    action: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.subtitle2,
            color = ChimahonThemeUi.colors.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (action != null) {
            Row(content = action)
        }
    }
}

@Composable
fun ChimahonPill(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    val colors = ChimahonThemeUi.colors
    val background = if (selected) colors.primaryContainer else colors.surfaceVariant
    val contentColor = if (selected) colors.onPrimaryContainer else colors.onSurfaceVariant
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(ChimahonThemeUi.radii.pill))
            .background(background)
            .border(
                width = 1.dp,
                color = if (selected) colors.primary.copy(alpha = 0.34f) else colors.outline,
                shape = RoundedCornerShape(ChimahonThemeUi.radii.pill),
            )
            .thenClickable(enabled = enabled, onClick = onClick, role = Role.Button)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = contentColor.copy(alpha = if (enabled) 1f else 0.38f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ChimahonIconButtonSurface(
    imageVector: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    size: androidx.compose.ui.unit.Dp = ChimahonThemeUi.dimensions.iconButtonSize,
) {
    val colors = ChimahonThemeUi.colors
    val background = if (selected) colors.primary.copy(alpha = 0.14f) else Color.Transparent
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(background)
            .thenClickable(enabled = enabled, onClick = onClick, role = Role.Button),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = when {
                !enabled -> colors.disabledContent
                selected -> colors.primary
                else -> colors.onSurfaceVariant
            },
        )
    }
}

@Composable
private fun Modifier.thenClickable(
    enabled: Boolean,
    onClick: (() -> Unit)?,
    role: Role,
): Modifier {
    if (onClick == null) return this
    val interactionSource = remember { MutableInteractionSource() }
    return clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        role = role,
        onClick = onClick,
    )
}

@Composable
fun ProvideChimahonContentColor(
    color: Color,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides color,
        content = content,
    )
}
