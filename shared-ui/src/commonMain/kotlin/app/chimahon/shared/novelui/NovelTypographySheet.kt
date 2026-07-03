package app.chimahon.shared.novelui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun ChimahonNovelTypographySheet(
    state: ChimahonNovelTypographyState,
    onStateChange: (ChimahonNovelTypographyState) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    additionalSettings: @Composable ColumnScope.() -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        elevation = 18.dp,
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
    ) {
        Column(
            modifier = Modifier
                .heightIn(max = 560.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Reader appearance",
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                )
                TextButton(onClick = onDismiss) { Text("Close") }
            }

            ChimahonNovelSettingsSection(title = "Theme") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ChimahonNovelReaderTheme.entries.forEach { theme ->
                        ChimahonNovelChoiceChip(
                            text = theme.title,
                            selected = state.theme == theme,
                            onClick = { onStateChange(state.copy(theme = theme)) },
                        )
                    }
                }
            }

            ChimahonNovelSettingsSection(title = "Mode") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ChimahonNovelChoiceChip(
                        modifier = Modifier.weight(1f),
                        text = "Paginated",
                        selected = !state.continuousMode,
                        onClick = { onStateChange(state.copy(continuousMode = false)) },
                    )
                    ChimahonNovelChoiceChip(
                        modifier = Modifier.weight(1f),
                        text = "Continuous",
                        selected = state.continuousMode,
                        onClick = { onStateChange(state.copy(continuousMode = true)) },
                    )
                }
            }

            ChimahonNovelSettingsSection(title = "Typography") {
                ChimahonNovelFontRow(
                    selectedFont = state.selectedFont,
                    fonts = state.fontOptions,
                    onFontSelected = { onStateChange(state.copy(selectedFont = it)) },
                )
                ChimahonNovelSliderRow(
                    label = "Font size",
                    value = state.fontSize,
                    valueRange = 12f..72f,
                    valueText = "${state.fontSize.oneDecimal()} px",
                    onValueChange = { onStateChange(state.copy(fontSize = it.roundToStep(0.5f))) },
                )
                ChimahonNovelSliderRow(
                    label = "Line height",
                    value = state.lineHeight,
                    valueRange = 1f..2.5f,
                    valueText = state.lineHeight.twoDecimals(),
                    onValueChange = { onStateChange(state.copy(lineHeight = it.roundToStep(0.05f))) },
                )
                ChimahonNovelSliderRow(
                    label = "Character spacing",
                    value = state.characterSpacing,
                    valueRange = 0f..0.5f,
                    valueText = state.characterSpacing.twoDecimals(),
                    onValueChange = { onStateChange(state.copy(characterSpacing = it.roundToStep(0.05f))) },
                )
                ChimahonNovelSliderRow(
                    label = "Paragraph spacing",
                    value = state.paragraphSpacing,
                    valueRange = 0f..2f,
                    valueText = "${state.paragraphSpacing.twoDecimals()} em",
                    onValueChange = { onStateChange(state.copy(paragraphSpacing = it.roundToStep(0.05f))) },
                )
                ChimahonNovelSwitchRow(
                    label = "Hide furigana",
                    checked = state.hideFurigana,
                    onCheckedChange = { onStateChange(state.copy(hideFurigana = it)) },
                )
            }

            ChimahonNovelSettingsSection(title = "Layout") {
                ChimahonNovelSwitchRow(
                    label = "Vertical writing",
                    checked = state.verticalWriting,
                    onCheckedChange = { onStateChange(state.copy(verticalWriting = it)) },
                )
                ChimahonNovelSwitchRow(
                    label = "Justify text",
                    checked = state.justifyText,
                    onCheckedChange = { onStateChange(state.copy(justifyText = it)) },
                )
                ChimahonNovelSwitchRow(
                    label = "Avoid page breaks",
                    checked = state.avoidPageBreak,
                    onCheckedChange = { onStateChange(state.copy(avoidPageBreak = it)) },
                )
                ChimahonNovelSliderRow(
                    label = "Horizontal padding",
                    value = state.horizontalPaddingPercent,
                    valueRange = 0f..50f,
                    valueText = "${state.horizontalPaddingPercent.roundToInt()}%",
                    onValueChange = { onStateChange(state.copy(horizontalPaddingPercent = it.roundToStep(0.5f))) },
                )
                ChimahonNovelSliderRow(
                    label = "Vertical padding",
                    value = state.verticalPaddingPercent,
                    valueRange = 0f..50f,
                    valueText = "${state.verticalPaddingPercent.roundToInt()}%",
                    onValueChange = { onStateChange(state.copy(verticalPaddingPercent = it.roundToStep(0.5f))) },
                )
            }

            ChimahonNovelSettingsSection(title = "Navigation") {
                ChimahonNovelSliderRow(
                    label = "Tap zone",
                    value = state.tapZonePercent,
                    valueRange = 5f..45f,
                    valueText = "${state.tapZonePercent.roundToInt()}%",
                    onValueChange = { onStateChange(state.copy(tapZonePercent = it.roundToStep(1f))) },
                )
                ChimahonNovelSliderRow(
                    label = "Chapter swipe distance",
                    value = state.chapterSwipeDistance,
                    valueRange = 40f..240f,
                    valueText = "${state.chapterSwipeDistance.roundToInt()} px",
                    onValueChange = { onStateChange(state.copy(chapterSwipeDistance = it.roundToStep(4f))) },
                )
                ChimahonNovelSwitchRow(
                    label = "Use volume keys to navigate",
                    checked = state.volumeKeys,
                    onCheckedChange = { onStateChange(state.copy(volumeKeys = it)) },
                )
                if (state.volumeKeys) {
                    ChimahonNovelSwitchRow(
                        label = "Invert volume keys",
                        checked = state.volumeKeysInverted,
                        onCheckedChange = { onStateChange(state.copy(volumeKeysInverted = it)) },
                    )
                }
                ChimahonNovelSwitchRow(
                    label = "Keep screen on",
                    checked = state.keepScreenOn,
                    onCheckedChange = { onStateChange(state.copy(keepScreenOn = it)) },
                )
            }

            additionalSettings()
        }
    }
}

@Composable
private fun ChimahonNovelSettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
        )
        content()
    }
}

@Composable
private fun ChimahonNovelFontRow(
    selectedFont: String,
    fonts: List<String>,
    onFontSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Font",
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = selectedFont,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.64f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            TextButton(onClick = { expanded = true }) {
                Text("Change")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                fonts.forEach { font ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            onFontSelected(font)
                        },
                    ) {
                        Text(font)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChimahonNovelSliderRow(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    valueText: String,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = label,
                color = MaterialTheme.colors.onSurface,
            )
            Text(
                text = valueText,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                fontSize = 12.sp,
            )
        }
        Slider(
            value = value.coerceIn(valueRange.start, valueRange.endInclusive),
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.primary,
                activeTrackColor = MaterialTheme.colors.primary,
                inactiveTrackColor = MaterialTheme.colors.onSurface.copy(alpha = 0.16f),
            ),
        )
    }
}

@Composable
private fun ChimahonNovelSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label,
            color = MaterialTheme.colors.onSurface,
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun ChimahonNovelChoiceChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = if (selected) {
            MaterialTheme.colors.primary.copy(alpha = 0.14f)
        } else {
            Color.Transparent
        },
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colors.primary.copy(alpha = 0.35f)
            else MaterialTheme.colors.onSurface.copy(alpha = 0.14f),
        ),
    ) {
        TextButton(onClick = onClick) {
            Text(
                text = text,
                color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                maxLines = 1,
            )
        }
    }
}

private fun Float.roundToStep(step: Float): Float {
    if (step <= 0f) return this
    return (this / step).roundToInt() * step
}

private fun Float.oneDecimal(): String {
    return ((this * 10f).roundToInt() / 10f).toString()
}

private fun Float.twoDecimals(): String {
    val rounded = (this * 100f).roundToInt() / 100f
    return rounded.toString()
}
