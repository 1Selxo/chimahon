package app.chimahon.shared.novelinputui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Mouse
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NovelInputParityPanel(
    state: NovelInputUiState,
    actions: NovelInputUiActions,
    modifier: Modifier = Modifier,
) {
    val profile = state.profile
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        NovelInputHeader(state = state)
        if (state.showConflictWarnings && state.conflictWarnings.isNotEmpty()) {
            NovelInputConflictWarnings(
                warnings = state.conflictWarnings,
                onWarningClick = actions.onConflictSelected,
            )
        }
        NovelInputSection(
            title = "Page and scroll",
            subtitle = "${profile.pageScrollMode.modeLabel} - ${profile.pageScrollMode.flowLabel}",
        ) {
            NovelPageScrollModeControls(
                state = profile.pageScrollMode,
                onChange = { next ->
                    actions.onPageScrollModeChange(next)
                    actions.onProfileChange(profile.withPageScrollMode(next))
                },
            )
        }
        NovelInputSection(
            title = "Tap zones",
            subtitle = "Shared touch and click navigation map",
        ) {
            NovelTapZonePreview(
                layout = profile.tapZones,
                readingFlow = profile.pageScrollMode.readingFlow,
                onZoneClick = actions.onTapZoneSelected,
                modifier = Modifier.fillMaxWidth(),
            )
            NovelInputHintRows(
                hints = profile.inputHints.take(2),
                modifier = Modifier.padding(top = 10.dp),
            )
        }
        NovelInputSection(
            title = "Vertical writing",
            subtitle = profile.verticalWriting.summary,
        ) {
            NovelVerticalWritingControls(
                state = profile.verticalWriting,
                onChange = { next ->
                    actions.onVerticalWritingChange(next)
                    actions.onProfileChange(profile.withVerticalWriting(next))
                },
            )
        }
        NovelInputSection(
            title = "Mouse and selection",
            subtitle = profile.mouse.wheelSummary,
        ) {
            NovelMouseSelectionBehaviorHints(
                mouse = profile.mouse,
                selection = profile.selection,
                onMouseChange = { next ->
                    actions.onMouseInputChange(next)
                    actions.onProfileChange(profile.withMouseInput(next))
                },
                onSelectionChange = { next ->
                    actions.onSelectionBehaviorChange(next)
                    actions.onProfileChange(profile.withSelectionBehavior(next))
                },
            )
        }
        NovelInputSection(
            title = "Keyboard shortcuts",
            subtitle = "${profile.keyboardShortcuts.size} reader bindings",
        ) {
            NovelKeyboardShortcutRows(
                shortcuts = profile.keyboardShortcuts,
                onShortcutClick = actions.onKeyboardShortcutSelected,
            )
        }
        NovelInputSection(
            title = "Lookup shortcuts",
            subtitle = "${profile.lookupShortcuts.size} lookup rows",
        ) {
            NovelLookupShortcutRows(
                rows = profile.lookupShortcuts,
                onShortcutClick = actions.onLookupShortcutSelected,
            )
        }
    }
}

@Composable
fun NovelPageScrollModeControls(
    state: NovelPageScrollModeState,
    onChange: (NovelPageScrollModeState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        NovelChoiceRow(
            title = "Mode",
            values = NovelReaderViewportMode.entries,
            selected = state.mode,
            label = { it.title },
            onSelected = { mode -> onChange(state.copy(mode = mode)) },
        )
        NovelChoiceRow(
            title = "Reading flow",
            values = NovelReadingFlow.entries,
            selected = state.readingFlow,
            label = { it.title },
            onSelected = { flow -> onChange(state.copy(readingFlow = flow)) },
        )
        NovelChoiceRow(
            title = "Page turns",
            values = NovelPageTurnBehavior.entries,
            selected = state.pageTurnBehavior,
            label = { it.title },
            onSelected = { behavior -> onChange(state.copy(pageTurnBehavior = behavior)) },
        )
        NovelCheckRow(
            title = "Tap zones in scroll mode",
            detail = "Keep edge taps active while continuous scrolling is selected.",
            checked = state.keepTapZonesInScrollMode,
            onCheckedChange = { onChange(state.copy(keepTapZonesInScrollMode = it)) },
        )
        NovelCheckRow(
            title = "Progress scrubber",
            detail = "Expose a shared progress control for paged and scroll modes.",
            checked = state.showProgressScrubber,
            onCheckedChange = { onChange(state.copy(showProgressScrubber = it)) },
        )
    }
}

@Composable
fun NovelTapZonePreview(
    layout: NovelTapZoneLayout,
    readingFlow: NovelReadingFlow,
    modifier: Modifier = Modifier,
    onZoneClick: (NovelTapZoneId) -> Unit = {},
) {
    val colors = MaterialTheme.colors
    Surface(
        modifier = modifier,
        color = colors.surface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, colors.onSurface.copy(alpha = 0.12f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            NovelTapZoneBand(
                zone = NovelTapZoneId.TopEdge,
                action = layout.actionForZone(NovelTapZoneId.TopEdge, readingFlow),
                onClick = onZoneClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                NovelTapZoneCell(
                    zone = NovelTapZoneId.LeadingEdge,
                    action = layout.actionForZone(NovelTapZoneId.LeadingEdge, readingFlow),
                    onClick = onZoneClick,
                    modifier = Modifier.weight(layout.leadingWidthPercent.zoneWeight()),
                )
                NovelTapZoneCell(
                    zone = NovelTapZoneId.Center,
                    action = layout.actionForZone(NovelTapZoneId.Center, readingFlow),
                    onClick = onZoneClick,
                    modifier = Modifier.weight(layout.centerWidthPercent.zoneWeight()),
                )
                NovelTapZoneCell(
                    zone = NovelTapZoneId.TrailingEdge,
                    action = layout.actionForZone(NovelTapZoneId.TrailingEdge, readingFlow),
                    onClick = onZoneClick,
                    modifier = Modifier.weight(layout.trailingWidthPercent.zoneWeight()),
                )
            }
            NovelTapZoneBand(
                zone = NovelTapZoneId.BottomEdge,
                action = layout.actionForZone(NovelTapZoneId.BottomEdge, readingFlow),
                onClick = onZoneClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
            )
        }
    }
}

@Composable
fun NovelVerticalWritingControls(
    state: NovelVerticalWritingState,
    onChange: (NovelVerticalWritingState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        NovelSwitchRow(
            title = "Vertical writing",
            detail = state.summary,
            checked = state.enabled,
            onCheckedChange = { onChange(state.copy(enabled = it)) },
        )
        NovelChoiceRow(
            title = "Vertical flow",
            values = listOf(NovelReadingFlow.VerticalRightToLeft, NovelReadingFlow.VerticalLeftToRight),
            selected = state.flow,
            label = { it.title },
            enabled = state.enabled,
            onSelected = { flow -> onChange(state.copy(flow = flow)) },
        )
        NovelChoiceRow(
            title = "Latin glyphs",
            values = NovelLatinGlyphBehavior.entries,
            selected = state.latinGlyphBehavior,
            label = { it.title },
            enabled = state.enabled,
            onSelected = { behavior -> onChange(state.copy(latinGlyphBehavior = behavior)) },
        )
        NovelCheckRow(
            title = "Tate-chu-yoko",
            detail = "Keep short number runs upright in vertical text.",
            checked = state.tateChuYoko,
            enabled = state.enabled,
            onCheckedChange = { onChange(state.copy(tateChuYoko = it)) },
        )
        NovelCheckRow(
            title = "Ruby text",
            detail = "Reserve room for furigana and reading aids.",
            checked = state.showRubyText,
            enabled = state.enabled,
            onCheckedChange = { onChange(state.copy(showRubyText = it)) },
        )
        NovelCheckRow(
            title = "Compact punctuation",
            detail = "Use tighter punctuation spacing for vertical Japanese prose.",
            checked = state.compactPunctuation,
            enabled = state.enabled,
            onCheckedChange = { onChange(state.copy(compactPunctuation = it)) },
        )
        NovelCheckRow(
            title = "Mirror tap zones",
            detail = "Swap previous and next page edges for right-to-left flows.",
            checked = state.mirrorTapZones,
            enabled = state.enabled,
            onCheckedChange = { onChange(state.copy(mirrorTapZones = it)) },
        )
    }
}

@Composable
fun NovelMouseSelectionBehaviorHints(
    mouse: NovelMouseInputState,
    selection: NovelSelectionBehaviorState,
    onMouseChange: (NovelMouseInputState) -> Unit,
    onSelectionChange: (NovelSelectionBehaviorState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        NovelChoiceRow(
            title = "Wheel",
            values = NovelMouseWheelBehavior.entries,
            selected = mouse.wheelBehavior,
            label = { it.title },
            onSelected = { behavior -> onMouseChange(mouse.copy(wheelBehavior = behavior)) },
        )
        NovelChoiceRow(
            title = "Ctrl/Cmd wheel",
            values = NovelMouseWheelBehavior.entries,
            selected = mouse.primaryModifiedWheelBehavior,
            label = { it.title },
            onSelected = { behavior -> onMouseChange(mouse.copy(primaryModifiedWheelBehavior = behavior)) },
        )
        NovelChoiceRow(
            title = "Side buttons",
            values = NovelMouseButtonBehavior.entries,
            selected = mouse.sideButtonBehavior,
            label = { it.title },
            onSelected = { behavior -> onMouseChange(mouse.copy(sideButtonBehavior = behavior)) },
        )
        NovelCheckRow(
            title = "Invert wheel direction",
            detail = "Reverse previous and next actions for wheel navigation.",
            checked = mouse.invertWheelDirection,
            onCheckedChange = { onMouseChange(mouse.copy(invertWheelDirection = it)) },
        )
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.10f))
        NovelChoiceRow(
            title = "Mouse drag",
            values = NovelSelectionActivation.entries,
            selected = selection.mouseDrag,
            label = { it.title },
            onSelected = { activation -> onSelectionChange(selection.copy(mouseDrag = activation)) },
        )
        NovelChoiceRow(
            title = "Touch long press",
            values = NovelSelectionActivation.entries,
            selected = selection.touchLongPress,
            label = { it.title },
            onSelected = { activation -> onSelectionChange(selection.copy(touchLongPress = activation)) },
        )
        NovelChoiceRow(
            title = "Double click",
            values = NovelSelectionActivation.entries,
            selected = selection.doubleClick,
            label = { it.title },
            onSelected = { activation -> onSelectionChange(selection.copy(doubleClick = activation)) },
        )
        NovelCheckRow(
            title = "Keep toolbar visible",
            detail = "Leave lookup and copy actions mounted after text selection.",
            checked = selection.keepSelectionToolbarVisible,
            onCheckedChange = { onSelectionChange(selection.copy(keepSelectionToolbarVisible = it)) },
        )
        NovelCheckRow(
            title = "Paged text selection",
            detail = "Allow selection handles inside paginated reader surfaces.",
            checked = selection.allowTextSelectionInPagedMode,
            onCheckedChange = { onSelectionChange(selection.copy(allowTextSelectionInPagedMode = it)) },
        )
    }
}

@Composable
fun NovelKeyboardShortcutRows(
    shortcuts: List<NovelKeyboardShortcut>,
    modifier: Modifier = Modifier,
    onShortcutClick: (NovelKeyboardShortcut) -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        shortcuts.forEach { shortcut ->
            NovelKeyboardShortcutRow(
                shortcut = shortcut,
                onClick = onShortcutClick,
            )
        }
    }
}

@Composable
fun NovelKeyboardShortcutRow(
    shortcut: NovelKeyboardShortcut,
    modifier: Modifier = Modifier,
    onClick: (NovelKeyboardShortcut) -> Unit = {},
) {
    NovelInputSurfaceRow(
        modifier = modifier.clickable(enabled = shortcut.enabled) { onClick(shortcut) },
        enabled = shortcut.enabled,
        leadingIcon = Icons.Outlined.Keyboard,
        title = shortcut.title,
        detail = shortcut.description,
        trailing = {
            NovelShortcutKeyCaps(
                chords = shortcut.chords,
                enabled = shortcut.enabled,
            )
        },
    )
}

@Composable
fun NovelLookupShortcutRows(
    rows: List<NovelLookupShortcutRow>,
    modifier: Modifier = Modifier,
    onShortcutClick: (NovelLookupShortcutRow) -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        rows.forEach { row ->
            NovelLookupShortcutRowView(
                row = row,
                onClick = onShortcutClick,
            )
        }
    }
}

@Composable
fun NovelLookupShortcutRowView(
    row: NovelLookupShortcutRow,
    modifier: Modifier = Modifier,
    onClick: (NovelLookupShortcutRow) -> Unit = {},
) {
    NovelInputSurfaceRow(
        modifier = modifier.clickable(enabled = row.enabled) { onClick(row) },
        enabled = row.enabled,
        leadingIcon = row.action.lookupIcon(),
        title = row.title,
        detail = row.detail,
        trailing = {
            NovelShortcutKeyCaps(
                chords = row.shortcuts,
                enabled = row.enabled,
            )
        },
    )
}

@Composable
fun NovelInputHintRows(
    hints: List<NovelInputHintRow>,
    modifier: Modifier = Modifier,
    onHintClick: (NovelInputHintRow) -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        hints.forEach { hint ->
            NovelInputHintRowView(
                hint = hint,
                onClick = onHintClick,
            )
        }
    }
}

@Composable
fun NovelInputHintRowView(
    hint: NovelInputHintRow,
    modifier: Modifier = Modifier,
    onClick: (NovelInputHintRow) -> Unit = {},
) {
    NovelInputSurfaceRow(
        modifier = modifier.clickable(enabled = hint.enabled) { onClick(hint) },
        enabled = hint.enabled,
        leadingIcon = hint.device.icon(),
        title = hint.title,
        detail = hint.detail,
        trailing = {
            if (hint.hasShortcut) {
                NovelKeyCap(
                    label = hint.shortcut,
                    enabled = hint.enabled,
                    selected = hint.status == NovelInputHintStatus.Active,
                )
            }
        },
    )
}

@Composable
fun NovelInputConflictWarnings(
    warnings: List<NovelInputConflictWarning>,
    modifier: Modifier = Modifier,
    onWarningClick: (NovelInputConflictWarning) -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        warnings.forEach { warning ->
            NovelInputConflictWarningRow(
                warning = warning,
                onClick = onWarningClick,
            )
        }
    }
}

@Composable
fun NovelInputConflictWarningRow(
    warning: NovelInputConflictWarning,
    modifier: Modifier = Modifier,
    onClick: (NovelInputConflictWarning) -> Unit = {},
) {
    val color = warning.severity.warningColor()
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(warning) },
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.28f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(color.copy(alpha = 0.16f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = warning.severity.title.take(1),
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = warning.title,
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = warning.message,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            warning.shortcutLabel?.let { label ->
                NovelKeyCap(label = label, selected = warning.blocking)
            }
        }
    }
}

@Composable
fun NovelShortcutKeyCaps(
    chords: List<NovelShortcutChord>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .widthIn(max = 240.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (chords.isEmpty()) {
            NovelKeyCap(label = "Unassigned", enabled = false)
        } else {
            chords.forEach { chord ->
                NovelKeyCap(
                    label = chord.displayLabel,
                    enabled = enabled,
                )
            }
        }
    }
}

@Composable
fun NovelKeyCap(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
) {
    val colors = MaterialTheme.colors
    val background = when {
        selected -> colors.primary.copy(alpha = 0.16f)
        else -> colors.onSurface.copy(alpha = 0.06f)
    }
    val content = when {
        !enabled -> colors.onSurface.copy(alpha = 0.34f)
        selected -> colors.primary
        else -> colors.onSurface
    }
    Surface(
        modifier = modifier,
        color = background,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, content.copy(alpha = 0.16f)),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            text = label,
            color = content,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun NovelInputHeader(
    state: NovelInputUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = state.title,
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = state.subtitle,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.64f),
            style = MaterialTheme.typography.body2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun NovelInputSection(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.10f)),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            content()
        }
    }
}

@Composable
private fun <T> NovelChoiceRow(
    title: String,
    values: List<T>,
    selected: T,
    label: (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = title,
            color = MaterialTheme.colors.onSurface.copy(alpha = if (enabled) 0.78f else 0.38f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            values.forEach { value ->
                NovelChoiceChip(
                    text = label(value),
                    selected = value == selected,
                    enabled = enabled,
                    onClick = { onSelected(value) },
                )
            }
        }
    }
}

@Composable
private fun NovelChoiceChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = MaterialTheme.colors
    val background = if (selected) colors.primary.copy(alpha = 0.14f) else colors.onSurface.copy(alpha = 0.05f)
    val content = when {
        !enabled -> colors.onSurface.copy(alpha = 0.34f)
        selected -> colors.primary
        else -> colors.onSurface.copy(alpha = 0.74f)
    }
    Surface(
        modifier = modifier.clickable(enabled = enabled, onClick = onClick),
        color = background,
        shape = RoundedCornerShape(100.dp),
        border = BorderStroke(1.dp, content.copy(alpha = if (selected) 0.34f else 0.12f)),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            text = text,
            color = content,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun NovelSwitchRow(
    title: String,
    detail: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    NovelInputToggleRow(
        title = title,
        detail = detail,
        checked = checked,
        enabled = enabled,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        control = {
            Switch(
                checked = checked,
                enabled = enabled,
                onCheckedChange = onCheckedChange,
            )
        },
    )
}

@Composable
private fun NovelCheckRow(
    title: String,
    detail: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    NovelInputToggleRow(
        title = title,
        detail = detail,
        checked = checked,
        enabled = enabled,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        control = {
            Checkbox(
                checked = checked,
                enabled = enabled,
                onCheckedChange = onCheckedChange,
            )
        },
    )
}

@Composable
private fun NovelInputToggleRow(
    title: String,
    detail: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    control: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colors.onSurface.copy(alpha = if (enabled) 0.88f else 0.38f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = detail,
                color = MaterialTheme.colors.onSurface.copy(alpha = if (enabled) 0.60f else 0.34f),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        control()
    }
}

@Composable
private fun NovelInputSurfaceRow(
    title: String,
    detail: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.04f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface.copy(alpha = if (enabled) 0.68f else 0.34f),
                modifier = Modifier.size(20.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colors.onSurface.copy(alpha = if (enabled) 0.90f else 0.38f),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = detail,
                    color = MaterialTheme.colors.onSurface.copy(alpha = if (enabled) 0.62f else 0.34f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = trailing,
            )
        }
    }
}

@Composable
private fun NovelTapZoneBand(
    zone: NovelTapZoneId,
    action: NovelInputActionId?,
    onClick: (NovelTapZoneId) -> Unit,
    modifier: Modifier = Modifier,
) {
    NovelTapZoneBox(
        zone = zone,
        action = action,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun RowScope.NovelTapZoneCell(
    zone: NovelTapZoneId,
    action: NovelInputActionId?,
    onClick: (NovelTapZoneId) -> Unit,
    modifier: Modifier = Modifier,
) {
    NovelTapZoneBox(
        zone = zone,
        action = action,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun NovelTapZoneBox(
    zone: NovelTapZoneId,
    action: NovelInputActionId?,
    onClick: (NovelTapZoneId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colors
    val enabled = action != null
    val background = if (enabled) colors.primary.copy(alpha = 0.10f) else colors.onSurface.copy(alpha = 0.04f)
    Surface(
        modifier = modifier.clickable { onClick(zone) },
        color = background,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) colors.primary.copy(alpha = 0.26f) else colors.onSurface.copy(alpha = 0.10f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = zone.title,
                color = colors.onSurface.copy(alpha = 0.68f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = action?.title ?: "Disabled",
                color = if (enabled) colors.primary else colors.onSurface.copy(alpha = 0.42f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun NovelInputDevice.icon(): ImageVector {
    return when (this) {
        NovelInputDevice.Keyboard -> Icons.Outlined.Keyboard
        NovelInputDevice.Mouse -> Icons.Outlined.Mouse
        NovelInputDevice.Touch,
        NovelInputDevice.Touchpad,
        -> Icons.Outlined.TouchApp
    }
}

private fun NovelLookupShortcutAction.lookupIcon(): ImageVector {
    return when (this) {
        NovelLookupShortcutAction.LookupSelection,
        NovelLookupShortcutAction.LookupClipboard,
        NovelLookupShortcutAction.LookupHoveredText,
        NovelLookupShortcutAction.SearchSelection,
        -> Icons.Outlined.Search
        NovelLookupShortcutAction.CopySelection -> Icons.Outlined.ContentCopy
        NovelLookupShortcutAction.AddToAnki -> Icons.Outlined.TextFields
        NovelLookupShortcutAction.Dismiss -> Icons.Outlined.Keyboard
    }
}

private fun NovelInputConflictSeverity.warningColor(): Color {
    return when (this) {
        NovelInputConflictSeverity.Info -> Color(0xFF3F51B5)
        NovelInputConflictSeverity.Warning -> Color(0xFFB26A00)
        NovelInputConflictSeverity.Error -> Color(0xFFB00020)
    }
}

private fun Float.zoneWeight(): Float {
    return coerceIn(8f, 84f)
}
