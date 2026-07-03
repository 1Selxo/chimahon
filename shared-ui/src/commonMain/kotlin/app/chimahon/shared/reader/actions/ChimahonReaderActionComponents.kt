package app.chimahon.shared.reader.actions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

data class ChimahonReaderActionsColors(
    val chrome: Color,
    val control: Color,
    val selectedControl: Color,
    val divider: Color,
    val onChrome: Color,
    val secondaryText: Color,
    val error: Color,
)

object ChimahonReaderActionsDefaults {
    fun colors(): ChimahonReaderActionsColors {
        return ChimahonReaderActionsColors(
            chrome = Color(0xFF17171B),
            control = Color(0xFF29292F),
            selectedControl = Color(0xFF5C5A86),
            divider = Color(0xFF34343B),
            onChrome = Color.White,
            secondaryText = Color(0xFFB9B6C0),
            error = Color(0xFFFFB4AB),
        )
    }
}

@Composable
fun ChimahonReaderExtraActionsPanel(
    state: ChimahonReaderPageActionsState,
    autoScrollState: ChimahonReaderAutoScrollState,
    onAction: (ChimahonReaderActionId) -> Unit,
    onAutoScrollFrequencyChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonReaderActionsColors = ChimahonReaderActionsDefaults.colors(),
    showShortcuts: Boolean = true,
    showHelpActions: Boolean = true,
) {
    val boostAction = state.pageActionDescriptors(
        includeRetryPage = false,
        includeTextActions = false,
        includeResetView = false,
    ).first { it.id == ChimahonReaderActionId.BoostPage }

    ChimahonReaderActionsContainer(
        modifier = modifier,
        colors = colors,
    ) {
        ChimahonReaderActionsHeader(
            title = "Reader utilities",
            subtitle = state.pageSubtitle,
            colors = colors,
        )
        ChimahonReaderAutoScrollControls(
            state = autoScrollState,
            onAction = onAction,
            onFrequencyChange = onAutoScrollFrequencyChange,
            colors = colors,
            showShortcut = showShortcuts,
        )
        ChimahonReaderActionRow(
            action = state.retryAllDescriptor(),
            onAction = onAction,
            colors = colors,
            showShortcut = showShortcuts,
        )
        ChimahonReaderActionRow(
            action = boostAction,
            onAction = onAction,
            colors = colors,
            showShortcut = showShortcuts,
        )
        if (showHelpActions) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ChimahonReaderSmallIconButton(
                    imageVector = Icons.Outlined.HelpOutline,
                    contentDescription = "Autoscroll help",
                    colors = colors,
                    onClick = { onAction(ChimahonReaderActionId.AutoScrollHelp) },
                )
                ChimahonReaderSmallIconButton(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "Retry all help",
                    colors = colors,
                    onClick = { onAction(ChimahonReaderActionId.RetryAllHelp) },
                )
                ChimahonReaderSmallIconButton(
                    imageVector = Icons.Outlined.SwapHoriz,
                    contentDescription = "Boost page help",
                    colors = colors,
                    onClick = { onAction(ChimahonReaderActionId.BoostPageHelp) },
                )
            }
        }
        state.message?.let { message ->
            ChimahonReaderActionMessage(message = message, colors = colors)
        }
    }
}

@Composable
fun ChimahonReaderAutoScrollControls(
    state: ChimahonReaderAutoScrollState,
    onAction: (ChimahonReaderActionId) -> Unit,
    onFrequencyChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonReaderActionsColors = ChimahonReaderActionsDefaults.colors(),
    showShortcut: Boolean = true,
) {
    val action = state.toActionDescriptor()
    val actionTextColor = if (action.enabled || action.active) {
        colors.onChrome
    } else {
        colors.secondaryText.copy(alpha = 0.48f)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.control.copy(alpha = 0.34f))
            .border(1.dp, colors.divider.copy(alpha = 0.58f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp)
                .clip(RoundedCornerShape(6.dp))
                .clickable(enabled = action.enabled, role = Role.Button) {
                    onAction(ChimahonReaderActionId.ToggleAutoScroll)
                }
                .padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = if (state.running) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                contentDescription = action.title,
                tint = actionTextColor,
                modifier = Modifier.size(20.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                ChimahonReaderActionText(
                    text = action.title,
                    color = actionTextColor,
                    size = 13,
                    weight = FontWeight.SemiBold,
                    maxLines = 1,
                )
                action.supportingText?.let {
                    ChimahonReaderActionText(
                        text = it,
                        color = if (action.enabled) colors.secondaryText else colors.secondaryText.copy(alpha = 0.48f),
                        size = 11,
                        maxLines = 1,
                    )
                }
            }
            if (showShortcut) {
                action.shortcut?.let { shortcut ->
                    ChimahonReaderShortcutKeycap(shortcut = shortcut, colors = colors)
                }
            }
            ChimahonReaderTogglePill(
                checked = state.running,
                enabled = action.enabled || action.active,
                colors = colors,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChimahonReaderFrequencyField(
                value = state.frequencyText,
                enabled = state.featureAvailable && !state.busy,
                error = !state.frequencyValid,
                onValueChange = onFrequencyChange,
                colors = colors,
                modifier = Modifier.weight(1f),
            )
            ChimahonReaderSmallIconButton(
                imageVector = Icons.Outlined.HelpOutline,
                contentDescription = "Autoscroll help",
                colors = colors,
                onClick = { onAction(ChimahonReaderActionId.AutoScrollHelp) },
            )
        }
        if (!state.frequencyValid || !state.featureAvailable) {
            ChimahonReaderActionText(
                text = action.unavailableReason ?: state.invalidFrequencyMessage,
                color = colors.error,
                size = 11,
                lineHeight = 15,
                maxLines = 2,
            )
        }
    }
}

@Composable
fun ChimahonReaderPageActionsDialog(
    state: ChimahonReaderPageActionsState,
    onDismiss: () -> Unit,
    onAction: (ChimahonReaderActionId) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonReaderActionsColors = ChimahonReaderActionsDefaults.colors(),
    showShortcuts: Boolean = true,
) {
    val actions = state.pageActionDescriptors()
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .widthIn(min = 320.dp, max = 460.dp)
                .heightIn(max = 660.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.chrome.copy(alpha = 0.99f))
                .border(1.dp, colors.divider.copy(alpha = 0.82f), RoundedCornerShape(8.dp))
                .chimahonReaderActionShortcuts(actions = actions, onAction = onAction)
                .focusable()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ChimahonReaderPageActionsMenu(
                state = state,
                actions = actions,
                onAction = onAction,
                colors = colors,
                showShortcuts = showShortcuts,
                showContainer = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 560.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                ChimahonReaderTextButton(
                    text = "Close",
                    colors = colors,
                    onClick = onDismiss,
                )
            }
        }
    }
}

@Composable
fun ChimahonReaderPageActionsMenu(
    state: ChimahonReaderPageActionsState,
    onAction: (ChimahonReaderActionId) -> Unit,
    modifier: Modifier = Modifier,
    actions: List<ChimahonReaderActionDescriptor> = state.pageActionDescriptors(),
    colors: ChimahonReaderActionsColors = ChimahonReaderActionsDefaults.colors(),
    showShortcuts: Boolean = true,
    showContainer: Boolean = true,
) {
    ChimahonReaderActionsContainer(
        modifier = modifier.verticalScroll(rememberScrollState()),
        colors = colors,
        showContainer = showContainer,
    ) {
        ChimahonReaderActionsHeader(
            title = "Page actions",
            subtitle = state.pageSubtitle,
            colors = colors,
        )
        actions.forEach { action ->
            ChimahonReaderActionRow(
                action = action,
                onAction = onAction,
                colors = colors,
                showShortcut = showShortcuts,
            )
        }
        state.message?.let { message ->
            ChimahonReaderActionMessage(message = message, colors = colors)
        }
    }
}

@Composable
fun ChimahonReaderPageActionStrip(
    state: ChimahonReaderPageActionsState,
    onAction: (ChimahonReaderActionId) -> Unit,
    modifier: Modifier = Modifier,
    onOpenMenu: (() -> Unit)? = null,
    colors: ChimahonReaderActionsColors = ChimahonReaderActionsDefaults.colors(),
) {
    val pageActions = state.pageActionDescriptors()
    val actions = buildList {
        if (state.retryPageAvailable) {
            add(pageActions.first { it.id == ChimahonReaderActionId.RetryPage })
        }
        add(pageActions.first { it.id == ChimahonReaderActionId.ToggleOcrLookup })
        add(pageActions.first { it.id == ChimahonReaderActionId.OpenPageUrl })
        add(pageActions.first { it.id == ChimahonReaderActionId.SavePage })
        add(pageActions.first { it.id == ChimahonReaderActionId.SharePage })
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(colors.chrome.copy(alpha = 0.82f))
            .border(1.dp, colors.divider.copy(alpha = 0.66f), RoundedCornerShape(50))
            .padding(horizontal = 6.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        actions.forEach { action ->
            ChimahonReaderIconActionButton(
                action = action,
                colors = colors,
                onClick = { onAction(action.id) },
            )
        }
        if (onOpenMenu != null) {
            ChimahonReaderSmallIconButton(
                imageVector = Icons.Outlined.MoreHoriz,
                contentDescription = "More page actions",
                colors = colors,
                onClick = onOpenMenu,
            )
        }
    }
}

@Composable
fun ChimahonReaderActionRow(
    action: ChimahonReaderActionDescriptor,
    onAction: (ChimahonReaderActionId) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonReaderActionsColors = ChimahonReaderActionsDefaults.colors(),
    showShortcut: Boolean = true,
) {
    val enabledColor = if (action.enabled || action.active) {
        colors.onChrome
    } else {
        colors.secondaryText.copy(alpha = 0.46f)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (action.active) colors.selectedControl else colors.control.copy(alpha = 0.52f))
            .semantics {
                stateDescription = when {
                    action.busy -> "In progress"
                    action.active -> "Active"
                    action.enabled -> "Available"
                    else -> "Unavailable"
                }
            }
            .clickable(enabled = action.enabled, role = Role.Button) { onAction(action.id) }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = action.imageVector,
            contentDescription = action.title,
            tint = enabledColor,
            modifier = Modifier.size(19.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            ChimahonReaderActionText(
                text = action.title,
                color = enabledColor,
                size = 12,
                weight = FontWeight.SemiBold,
                maxLines = 1,
            )
            action.supportingText?.let { supportingText ->
                ChimahonReaderActionText(
                    text = supportingText,
                    color = if (action.enabled) colors.secondaryText else colors.secondaryText.copy(alpha = 0.48f),
                    size = 10,
                    lineHeight = 14,
                    maxLines = 2,
                )
            }
        }
        if (showShortcut) {
            action.shortcut?.let { shortcut ->
                ChimahonReaderShortcutKeycap(
                    shortcut = shortcut,
                    colors = colors,
                    enabled = action.enabled,
                )
            }
        }
    }
}

@Composable
private fun ChimahonReaderActionsContainer(
    modifier: Modifier,
    colors: ChimahonReaderActionsColors,
    showContainer: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val containerModifier = if (showContainer) {
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.chrome.copy(alpha = 0.98f))
            .border(1.dp, colors.divider.copy(alpha = 0.82f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp)
    } else {
        modifier
    }
    Column(
        modifier = containerModifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content,
    )
}

@Composable
private fun ChimahonReaderActionsHeader(
    title: String,
    subtitle: String,
    colors: ChimahonReaderActionsColors,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(colors.selectedControl.copy(alpha = 0.74f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = colors.onChrome,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            ChimahonReaderActionText(
                text = title,
                color = colors.onChrome,
                size = 15,
                weight = FontWeight.SemiBold,
                maxLines = 1,
            )
            ChimahonReaderActionText(
                text = subtitle,
                color = colors.secondaryText,
                size = 11,
                lineHeight = 15,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun ChimahonReaderFrequencyField(
    value: String,
    enabled: Boolean,
    error: Boolean,
    onValueChange: (String) -> Unit,
    colors: ChimahonReaderActionsColors,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = true,
        textStyle = TextStyle(
            color = when {
                !enabled -> colors.secondaryText.copy(alpha = 0.48f)
                error -> colors.error
                else -> colors.onChrome
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(colors.chrome.copy(alpha = 0.62f))
            .border(
                width = 1.dp,
                color = if (error) colors.error.copy(alpha = 0.72f) else colors.divider.copy(alpha = 0.72f),
                shape = RoundedCornerShape(6.dp),
            )
            .padding(horizontal = 12.dp),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isBlank()) {
                        ChimahonReaderActionText(
                            text = "Seconds",
                            color = colors.secondaryText.copy(alpha = 0.58f),
                            size = 12,
                            maxLines = 1,
                        )
                    }
                    innerTextField()
                }
                Spacer(modifier = Modifier.width(8.dp))
                ChimahonReaderActionText(
                    text = "s",
                    color = colors.secondaryText,
                    size = 12,
                    weight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
        },
    )
}

@Composable
private fun ChimahonReaderTogglePill(
    checked: Boolean,
    enabled: Boolean,
    colors: ChimahonReaderActionsColors,
) {
    Box(
        modifier = Modifier
            .width(44.dp)
            .height(24.dp)
            .clip(CircleShape)
            .background(
                when {
                    checked -> colors.selectedControl
                    enabled -> colors.secondaryText.copy(alpha = 0.32f)
                    else -> colors.secondaryText.copy(alpha = 0.16f)
                },
            )
            .padding(3.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(if (enabled || checked) colors.onChrome else colors.secondaryText.copy(alpha = 0.48f)),
        )
    }
}

@Composable
private fun ChimahonReaderIconActionButton(
    action: ChimahonReaderActionDescriptor,
    colors: ChimahonReaderActionsColors,
    onClick: () -> Unit,
) {
    ChimahonReaderSmallIconButton(
        imageVector = action.imageVector,
        contentDescription = action.title,
        colors = colors,
        enabled = action.enabled,
        active = action.active,
        onClick = onClick,
    )
}

@Composable
private fun ChimahonReaderSmallIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    colors: ChimahonReaderActionsColors,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    active: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (active) colors.selectedControl.copy(alpha = 0.86f) else colors.control.copy(alpha = 0.58f))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = if (enabled || active) colors.onChrome else colors.secondaryText.copy(alpha = 0.42f),
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun ChimahonReaderTextButton(
    text: String,
    colors: ChimahonReaderActionsColors,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(colors.control.copy(alpha = 0.72f))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        ChimahonReaderActionText(
            text = text,
            color = colors.onChrome,
            size = 12,
            weight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun ChimahonReaderShortcutKeycap(
    shortcut: ChimahonReaderActionShortcut,
    colors: ChimahonReaderActionsColors,
    enabled: Boolean = true,
) {
    Box(
        modifier = Modifier
            .widthIn(max = 112.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(colors.chrome.copy(alpha = 0.72f))
            .border(1.dp, colors.divider.copy(alpha = 0.72f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
    ) {
        ChimahonReaderActionText(
            text = shortcut.label,
            color = if (enabled) colors.secondaryText else colors.secondaryText.copy(alpha = 0.42f),
            size = 9,
            weight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun ChimahonReaderActionMessage(
    message: String,
    colors: ChimahonReaderActionsColors,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(colors.control.copy(alpha = 0.38f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        ChimahonReaderActionText(
            text = message,
            color = colors.secondaryText,
            size = 11,
            lineHeight = 16,
            maxLines = 3,
        )
    }
}

@Composable
private fun ChimahonReaderActionText(
    text: String,
    color: Color,
    size: Int,
    modifier: Modifier = Modifier,
    weight: FontWeight = FontWeight.Normal,
    lineHeight: Int = size + 4,
    maxLines: Int = Int.MAX_VALUE,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(
            color = color,
            fontSize = size.sp,
            fontWeight = weight,
            lineHeight = lineHeight.sp,
        ),
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}

private val ChimahonReaderActionDescriptor.imageVector: ImageVector
    get() = when (id) {
        ChimahonReaderActionId.RetryPage,
        ChimahonReaderActionId.RetryAllPages,
        -> Icons.Outlined.Refresh
        ChimahonReaderActionId.BoostPage -> Icons.Outlined.SwapHoriz
        ChimahonReaderActionId.SetCover -> Icons.Outlined.Bookmark
        ChimahonReaderActionId.SavePage -> Icons.Outlined.Download
        ChimahonReaderActionId.SharePage,
        ChimahonReaderActionId.SharePageInfo,
        -> Icons.Outlined.Share
        ChimahonReaderActionId.OpenPageUrl -> Icons.Outlined.Public
        ChimahonReaderActionId.CopyPageUrl -> Icons.Outlined.ContentCopy
        ChimahonReaderActionId.SharePageUrl -> Icons.Outlined.Link
        ChimahonReaderActionId.CopyPageInfo -> Icons.Outlined.Info
        ChimahonReaderActionId.ToggleOcrLookup -> Icons.Outlined.Search
        ChimahonReaderActionId.ResetView -> Icons.Outlined.CropFree
        ChimahonReaderActionId.ToggleAutoScroll -> if (active) Icons.Outlined.Pause else Icons.Outlined.PlayArrow
        ChimahonReaderActionId.AutoScrollHelp,
        ChimahonReaderActionId.RetryAllHelp,
        ChimahonReaderActionId.BoostPageHelp,
        -> Icons.Outlined.HelpOutline
    }
