package app.chimahon.shared.novelreaderui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Mouse
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun NovelReaderSelectionOverlay(
    selection: NovelReaderSelectionState,
    actions: NovelReaderActions,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (selection.anchored && selection.handlesVisible) {
            NovelReaderSelectionHandle(
                handle = selection.resolvedStartHandle,
                selection = selection,
                actions = actions,
                palette = palette,
            )
            NovelReaderSelectionHandle(
                handle = selection.resolvedEndHandle,
                selection = selection,
                actions = actions,
                palette = palette,
            )
        }

        NovelReaderSelectionActionRow(
            selection = selection,
            actions = actions,
            palette = palette,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun NovelReaderSelectionHandle(
    handle: NovelReaderSelectionHandleUiModel,
    selection: NovelReaderSelectionState,
    actions: NovelReaderActions,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .offset {
                IntOffset(
                    x = handle.x.roundToInt(),
                    y = handle.y.roundToInt(),
                )
            }
            .pointerInput(handle, selection) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    actions.onSelectionHandleDrag(
                        handle.role,
                        handle.x + dragAmount.x,
                        handle.y + dragAmount.y,
                        selection,
                    )
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier
                .width(2.dp)
                .heightIn(min = 18.dp),
            color = palette.accent.copy(alpha = 0.82f),
        ) {}
        Surface(
            modifier = Modifier.size(22.dp),
            color = palette.accent,
            shape = CircleShape,
            border = BorderStroke(2.dp, palette.surface),
            elevation = 8.dp,
        ) {}
    }
}

@Composable
fun NovelReaderSelectionActionRow(
    selection: NovelReaderSelectionState,
    actions: NovelReaderActions,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = palette.surface,
        shape = RoundedCornerShape(18.dp),
        elevation = 18.dp,
        border = BorderStroke(1.dp, palette.outline),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selection.title,
                        color = palette.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val meta = listOfNotNull(selection.language, selection.dictionaryProfileName).joinToString(" - ")
                    if (meta.isNotBlank()) {
                        Text(text = meta, color = palette.muted, fontSize = 12.sp)
                    }
                }
                if (selection.lookupInProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = palette.onSurface,
                        strokeWidth = 2.dp,
                    )
                }
            }
            Text(
                text = selection.sentence,
                color = palette.muted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 13.sp,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NovelReaderSelectionButton(
                    text = "Lookup",
                    icon = Icons.Outlined.Search,
                    palette = palette,
                    onClick = { actions.onSelectionAction(NovelReaderSelectionAction.Lookup, selection) },
                )
                NovelReaderSelectionButton(
                    text = "Copy",
                    icon = Icons.Outlined.ContentCopy,
                    palette = palette,
                    onClick = { actions.onSelectionAction(NovelReaderSelectionAction.Copy, selection) },
                )
                NovelReaderSelectionButton(
                    text = "Search",
                    icon = Icons.Outlined.Search,
                    palette = palette,
                    onClick = { actions.onSelectionAction(NovelReaderSelectionAction.SearchWeb, selection) },
                )
                if (selection.ankiEnabled) {
                    NovelReaderSelectionButton(
                        text = "Anki",
                        icon = Icons.Outlined.TextFields,
                        palette = palette,
                        onClick = { actions.onSelectionAction(NovelReaderSelectionAction.AddToAnki, selection) },
                    )
                }
                NovelReaderSelectionButton(
                    text = "Translate",
                    icon = Icons.Outlined.TextFields,
                    palette = palette,
                    onClick = { actions.onSelectionAction(NovelReaderSelectionAction.Translate, selection) },
                )
                NovelReaderSelectionButton(
                    text = "Clear",
                    icon = Icons.Outlined.Close,
                    palette = palette,
                    onClick = { actions.onSelectionAction(NovelReaderSelectionAction.Clear, selection) },
                )
            }
        }
    }
}

@Composable
fun NovelReaderInputHintRow(
    hints: List<NovelReaderInputHint>,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        hints.forEach { hint ->
            NovelReaderInputHintChip(hint = hint, palette = palette)
        }
    }
}

@Composable
fun NovelReaderInputHintChip(
    hint: NovelReaderInputHint,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(100.dp),
        color = palette.text.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, palette.outline),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = hint.kind.icon(),
                contentDescription = null,
                tint = palette.muted,
                modifier = Modifier.size(16.dp),
            )
            Text(text = hint.label, color = palette.muted, fontSize = 12.sp, maxLines = 1)
            Text(
                text = hint.shortcut,
                color = palette.onSurface,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun NovelReaderTypographyEntryPanel(
    layout: NovelReaderLayoutState,
    palette: NovelReaderPalette,
    actions: NovelReaderActions,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = palette.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        elevation = 18.dp,
        border = BorderStroke(1.dp, palette.outline),
    ) {
        Column(
            modifier = Modifier
                .heightIn(max = 420.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Palette, contentDescription = null, tint = palette.onSurface)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Reader appearance",
                        color = palette.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                    )
                    Text(
                        text = "${layout.selectedFont} - ${layout.fontSize.oneDecimal()} px - ${layout.lineHeight.twoDecimals()} line",
                        color = palette.muted,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                TextButton(onClick = actions.onCloseTypography) { Text("Close", color = palette.onSurface) }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NovelReaderQuickToggle(
                    text = "Vertical",
                    checked = layout.verticalWriting,
                    palette = palette,
                    onCheckedChange = { actions.onLayoutChange(layout.copy(verticalWriting = it)) },
                )
                NovelReaderQuickToggle(
                    text = "Justify",
                    checked = layout.justifyText,
                    palette = palette,
                    onCheckedChange = { actions.onLayoutChange(layout.copy(justifyText = it)) },
                )
                NovelReaderQuickToggle(
                    text = "Hide furigana",
                    checked = layout.hideFurigana,
                    palette = palette,
                    onCheckedChange = { actions.onLayoutChange(layout.copy(hideFurigana = it)) },
                )
                NovelReaderQuickToggle(
                    text = "Keep on",
                    checked = layout.keepScreenOn,
                    palette = palette,
                    onCheckedChange = { actions.onLayoutChange(layout.copy(keepScreenOn = it)) },
                )
            }

            NovelReaderFontChoiceRow(
                layout = layout,
                palette = palette,
                onLayoutChange = actions.onLayoutChange,
            )

            NovelReaderThemeChoiceRow(
                layout = layout,
                palette = palette,
                onLayoutChange = actions.onLayoutChange,
            )

            NovelReaderTypographySlider(
                title = "Text size",
                valueLabel = "${layout.fontSize.oneDecimal()} sp",
                value = layout.fontSize,
                valueRange = 10f..72f,
                palette = palette,
                onValueChange = { actions.onLayoutChange(layout.copy(fontSize = it)) },
            )
            NovelReaderTypographySlider(
                title = "Line height",
                valueLabel = layout.lineHeight.twoDecimals(),
                value = layout.lineHeight,
                valueRange = 1f..2.8f,
                palette = palette,
                onValueChange = { actions.onLayoutChange(layout.copy(lineHeight = it)) },
            )
            NovelReaderTypographySlider(
                title = "Letter spacing",
                valueLabel = "${layout.characterSpacing.twoDecimals()} sp",
                value = layout.characterSpacing,
                valueRange = (-0.5f)..1.5f,
                palette = palette,
                onValueChange = { actions.onLayoutChange(layout.copy(characterSpacing = it)) },
            )
            NovelReaderTypographySlider(
                title = "Paragraph spacing",
                valueLabel = layout.paragraphSpacing.twoDecimals(),
                value = layout.paragraphSpacing,
                valueRange = 0f..2.5f,
                palette = palette,
                onValueChange = { actions.onLayoutChange(layout.copy(paragraphSpacing = it)) },
            )
            NovelReaderTypographySlider(
                title = "Side margin",
                valueLabel = "${layout.horizontalPaddingPercent.oneDecimal()}%",
                value = layout.horizontalPaddingPercent,
                valueRange = 0f..50f,
                palette = palette,
                onValueChange = { actions.onLayoutChange(layout.copy(horizontalPaddingPercent = it)) },
            )
            NovelReaderTypographySlider(
                title = "Vertical margin",
                valueLabel = "${layout.verticalPaddingPercent.oneDecimal()}%",
                value = layout.verticalPaddingPercent,
                valueRange = 0f..50f,
                palette = palette,
                onValueChange = { actions.onLayoutChange(layout.copy(verticalPaddingPercent = it)) },
            )
            NovelReaderTypographySlider(
                title = "Tap zone",
                valueLabel = "${layout.tapZonePercent.oneDecimal()}%",
                value = layout.tapZonePercent,
                valueRange = 5f..45f,
                palette = palette,
                onValueChange = { actions.onLayoutChange(layout.copy(tapZonePercent = it)) },
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NovelReaderAdjustButton(
                    text = "Reset",
                    palette = palette,
                    onClick = { actions.onLayoutChange(NovelReaderLayoutState()) },
                )
            }

            NovelReaderInputHintRow(
                hints = NovelReaderDefaults.desktopInputHints,
                palette = palette,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun NovelReaderFontChoiceRow(
    layout: NovelReaderLayoutState,
    palette: NovelReaderPalette,
    onLayoutChange: (NovelReaderLayoutState) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "Font", color = palette.muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            layout.fontOptions.forEach { font ->
                NovelReaderChoicePill(
                    text = font,
                    selected = font == layout.selectedFont,
                    palette = palette,
                    onClick = { onLayoutChange(layout.copy(selectedFont = font)) },
                )
            }
        }
    }
}

@Composable
private fun NovelReaderThemeChoiceRow(
    layout: NovelReaderLayoutState,
    palette: NovelReaderPalette,
    onLayoutChange: (NovelReaderLayoutState) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "Theme", color = palette.muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NovelReaderTheme.entries.forEach { theme ->
                NovelReaderThemePill(
                    theme = theme,
                    selected = theme == layout.theme,
                    palette = palette,
                    onClick = { onLayoutChange(layout.copy(theme = theme)) },
                )
            }
        }
    }
}

@Composable
private fun NovelReaderTypographySlider(
    title: String,
    valueLabel: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    palette: NovelReaderPalette,
    onValueChange: (Float) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                color = palette.muted,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = valueLabel,
                color = palette.onSurface,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
            )
        }
        Slider(
            value = value.coerceIn(valueRange.start, valueRange.endInclusive),
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = palette.onSurface,
                activeTrackColor = palette.onSurface,
                inactiveTrackColor = palette.outline,
            ),
        )
    }
}

@Composable
private fun NovelReaderChoicePill(
    text: String,
    selected: Boolean,
    palette: NovelReaderPalette,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = if (selected) palette.selection else palette.text.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, if (selected) palette.text.copy(alpha = 0.34f) else palette.outline),
    ) {
        TextButton(onClick = onClick) {
            Text(
                text = text,
                color = palette.onSurface,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun NovelReaderThemePill(
    theme: NovelReaderTheme,
    selected: Boolean,
    palette: NovelReaderPalette,
    onClick: () -> Unit,
) {
    val swatch = theme.readerThemeSwatch()
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = if (selected) palette.selection else palette.text.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, if (selected) palette.text.copy(alpha = 0.34f) else palette.outline),
    ) {
        TextButton(onClick = onClick) {
            Surface(
                modifier = Modifier.size(16.dp),
                color = swatch,
                shape = CircleShape,
                border = BorderStroke(1.dp, palette.outline),
            ) {}
            Spacer(Modifier.width(6.dp))
            Text(text = theme.title, color = palette.onSurface, fontSize = 12.sp, maxLines = 1)
        }
    }
}

@Composable
fun NovelReaderLoadingState(
    message: String,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    NovelReaderCenteredState(
        title = message,
        message = "Preparing the chapter text and reading position.",
        palette = palette,
        modifier = modifier,
        leading = {
            CircularProgressIndicator(
                modifier = Modifier.size(42.dp),
                color = palette.onSurface,
                strokeWidth = 3.dp,
            )
        },
    )
}

@Composable
fun NovelReaderErrorState(
    title: String,
    message: String,
    onRetry: () -> Unit,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    NovelReaderCenteredState(
        title = title,
        message = message,
        palette = palette,
        modifier = modifier,
        trailing = {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = palette.onSurface,
                    contentColor = palette.background,
                ),
                elevation = null,
            ) {
                Text("Retry")
            }
        },
    )
}

@Composable
fun NovelReaderEmptyState(
    title: String,
    message: String,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    NovelReaderCenteredState(title = title, message = message, palette = palette, modifier = modifier)
}

@Composable
private fun NovelReaderCenteredState(
    title: String,
    message: String,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            leading?.invoke()
            Text(text = title, color = palette.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            Text(text = message, color = palette.muted, fontSize = 13.sp)
            trailing?.invoke()
        }
    }
}

@Composable
private fun NovelReaderSelectionButton(
    text: String,
    icon: ImageVector,
    palette: NovelReaderPalette,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = palette.text.copy(alpha = 0.08f),
            contentColor = palette.onSurface,
        ),
        elevation = null,
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(6.dp))
        Text(text = text, fontSize = 12.sp, maxLines = 1)
    }
}

@Composable
private fun NovelReaderQuickToggle(
    text: String,
    checked: Boolean,
    palette: NovelReaderPalette,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (checked) palette.selection else palette.text.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, if (checked) palette.text.copy(alpha = 0.32f) else palette.outline),
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = text, color = palette.onSurface, fontSize = 12.sp)
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun NovelReaderAdjustButton(
    text: String,
    palette: NovelReaderPalette,
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick) {
        Text(text = text, color = palette.onSurface, fontWeight = FontWeight.SemiBold)
    }
}

private fun NovelReaderInputKind.icon(): ImageVector {
    return when (this) {
        NovelReaderInputKind.Keyboard -> Icons.Outlined.Keyboard
        NovelReaderInputKind.Mouse -> Icons.Outlined.Mouse
        NovelReaderInputKind.Touch -> Icons.Outlined.TouchApp
    }
}

private fun Float.oneDecimal(): String {
    return ((this * 10f).toInt() / 10f).toString()
}

private fun Float.twoDecimals(): String {
    return ((this * 100f).toInt() / 100f).toString()
}

private fun NovelReaderTheme.readerThemeSwatch(): Color {
    return when (this) {
        NovelReaderTheme.System -> Color(0xFF6750A4)
        NovelReaderTheme.Light -> Color(0xFFFFFFFF)
        NovelReaderTheme.Dark -> Color(0xFF121212)
        NovelReaderTheme.Sepia -> Color(0xFFF3E3CB)
        NovelReaderTheme.PureBlack -> Color(0xFF000000)
        NovelReaderTheme.Custom -> Color(0xFF607D8B)
    }
}
