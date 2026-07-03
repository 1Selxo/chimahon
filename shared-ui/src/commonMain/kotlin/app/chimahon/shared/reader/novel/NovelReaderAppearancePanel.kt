package app.chimahon.shared.reader.novel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun NovelReaderAppearancePanel(
    appearance: NovelReaderAppearanceState,
    colors: NovelReaderResolvedColors,
    onAppearanceChange: (NovelReaderAppearanceState) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    additionalSettings: @Composable ColumnScope.() -> Unit = {},
) {
    NovelReaderPanelSurface(
        title = "Appearance",
        colors = colors,
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 448.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            AppearanceSection(title = "Theme", colors = colors) {
                val activeCustomTheme = NovelReaderCustomTheme(
                    name = "Custom",
                    backgroundColor = appearance.customBackgroundColor,
                    textColor = appearance.customTextColor,
                )
                val customChoices = remember(
                    appearance.theme,
                    appearance.customBackgroundColor,
                    appearance.customTextColor,
                    appearance.customThemes,
                ) {
                    if (appearance.theme == NovelReaderTheme.Custom &&
                        appearance.customThemes.none {
                            it.backgroundColor == appearance.customBackgroundColor &&
                                it.textColor == appearance.customTextColor
                        }
                    ) {
                        listOf(activeCustomTheme) + appearance.customThemes
                    } else {
                        appearance.customThemes
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    NovelReaderDefaults.themeOptions.forEach { option ->
                        NovelReaderThemeSwatchButton(
                            label = option.label,
                            backgroundColor = option.backgroundColor,
                            textColor = option.textColor,
                            splitBackgroundColor = option.splitBackgroundColor,
                            selected = appearance.theme == option.theme,
                            colors = colors,
                            onClick = {
                                onAppearanceChange(appearance.copy(theme = option.theme))
                            },
                        )
                    }

                    customChoices.forEachIndexed { index, customTheme ->
                        NovelReaderThemeSwatchButton(
                            label = customTheme.name.ifBlank { "Custom ${index + 1}" },
                            backgroundColor = customTheme.backgroundColor,
                            textColor = customTheme.textColor,
                            selected = appearance.theme == NovelReaderTheme.Custom &&
                                appearance.customBackgroundColor == customTheme.backgroundColor &&
                                appearance.customTextColor == customTheme.textColor,
                            colors = colors,
                            onClick = {
                                onAppearanceChange(
                                    appearance.copy(
                                        theme = NovelReaderTheme.Custom,
                                        customBackgroundColor = customTheme.backgroundColor,
                                        customTextColor = customTheme.textColor,
                                    ),
                                )
                            },
                        )
                    }
                }

                if (appearance.theme == NovelReaderTheme.System) {
                    ReaderSwitchRow(
                        label = "System uses sepia",
                        checked = appearance.systemLightSepia,
                        colors = colors,
                        onCheckedChange = {
                            onAppearanceChange(appearance.copy(systemLightSepia = it))
                        },
                    )
                }
            }

            AppearanceSection(title = "Mode", colors = colors) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ReaderChoiceButton(
                        text = "Paginated",
                        selected = !appearance.continuousMode,
                        colors = colors,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onAppearanceChange(appearance.copy(continuousMode = false))
                        },
                    )
                    ReaderChoiceButton(
                        text = "Continuous",
                        selected = appearance.continuousMode,
                        colors = colors,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onAppearanceChange(appearance.copy(continuousMode = true))
                        },
                    )
                }
            }

            AppearanceSection(title = "Typography", colors = colors) {
                FontDropdown(
                    appearance = appearance,
                    colors = colors,
                    onFontSelected = { font ->
                        onAppearanceChange(appearance.copy(selectedFont = font))
                    },
                )
                ReaderSliderRow(
                    label = "Font size",
                    value = appearance.fontSize.toFloat(),
                    valueText = "${appearance.fontSize.shortDecimal()} px",
                    valueRange = 12f..72f,
                    steps = 119,
                    colors = colors,
                    onValueChange = {
                        onAppearanceChange(
                            appearance.copy(fontSize = roundToStep(it, 0.5)),
                        )
                    },
                )
                ReaderSliderRow(
                    label = "Line height",
                    value = appearance.lineHeight.toFloat(),
                    valueText = appearance.lineHeight.shortDecimal(),
                    valueRange = 1.0f..2.5f,
                    steps = 29,
                    colors = colors,
                    onValueChange = {
                        onAppearanceChange(
                            appearance.copy(lineHeight = roundToStep(it, 0.05)),
                        )
                    },
                )
                ReaderSwitchRow(
                    label = "Hide furigana",
                    checked = appearance.hideFurigana,
                    colors = colors,
                    onCheckedChange = {
                        onAppearanceChange(appearance.copy(hideFurigana = it))
                    },
                )
                ReaderSwitchRow(
                    label = "Keep screen on",
                    checked = appearance.keepScreenOn,
                    colors = colors,
                    onCheckedChange = {
                        onAppearanceChange(appearance.copy(keepScreenOn = it))
                    },
                )
            }

            AppearanceSection(title = "Margins", colors = colors) {
                ReaderSliderRow(
                    label = "Horizontal padding",
                    value = appearance.horizontalPadding.toFloat(),
                    valueText = "${appearance.horizontalPadding.shortDecimal()}%",
                    valueRange = 0f..50f,
                    steps = 99,
                    colors = colors,
                    onValueChange = {
                        onAppearanceChange(
                            appearance.copy(horizontalPadding = roundToStep(it, 0.5)),
                        )
                    },
                )
                ReaderSliderRow(
                    label = "Vertical padding",
                    value = appearance.verticalPadding.toFloat(),
                    valueText = "${appearance.verticalPadding.shortDecimal()}%",
                    valueRange = 0f..50f,
                    steps = 99,
                    colors = colors,
                    onValueChange = {
                        onAppearanceChange(
                            appearance.copy(verticalPadding = roundToStep(it, 0.5)),
                        )
                    },
                )
            }

            AppearanceSection(title = "Layout", colors = colors) {
                ReaderSwitchRow(
                    label = "Vertical writing",
                    checked = appearance.verticalWriting,
                    colors = colors,
                    onCheckedChange = {
                        onAppearanceChange(appearance.copy(verticalWriting = it))
                    },
                )
                ReaderSwitchRow(
                    label = "Avoid page breaks",
                    checked = appearance.avoidPageBreak,
                    colors = colors,
                    onCheckedChange = {
                        onAppearanceChange(appearance.copy(avoidPageBreak = it))
                    },
                )
                ReaderSwitchRow(
                    label = "Justify text",
                    checked = appearance.justifyText,
                    colors = colors,
                    onCheckedChange = {
                        onAppearanceChange(appearance.copy(justifyText = it))
                    },
                )
                ReaderSwitchRow(
                    label = "Advanced layout",
                    checked = appearance.layoutAdvanced,
                    colors = colors,
                    onCheckedChange = {
                        onAppearanceChange(appearance.copy(layoutAdvanced = it))
                    },
                )

                if (appearance.layoutAdvanced) {
                    ReaderSliderRow(
                        label = "Character spacing",
                        value = appearance.characterSpacing.toFloat(),
                        valueText = appearance.characterSpacing.shortDecimal(),
                        valueRange = 0f..0.5f,
                        steps = 9,
                        colors = colors,
                        onValueChange = {
                            onAppearanceChange(
                                appearance.copy(characterSpacing = roundToStep(it, 0.05)),
                            )
                        },
                    )
                    ReaderSliderRow(
                        label = "Paragraph spacing",
                        value = appearance.paragraphSpacing.toFloat(),
                        valueText = "${appearance.paragraphSpacing.shortDecimal()} em",
                        valueRange = 0f..2f,
                        steps = 39,
                        colors = colors,
                        onValueChange = {
                            onAppearanceChange(
                                appearance.copy(paragraphSpacing = roundToStep(it, 0.05)),
                            )
                        },
                    )
                    ReaderSliderRow(
                        label = "Tap zone",
                        value = appearance.tapZonePercent.toFloat(),
                        valueText = "${appearance.tapZonePercent}%",
                        valueRange = 5f..45f,
                        steps = 39,
                        colors = colors,
                        onValueChange = {
                            onAppearanceChange(
                                appearance.copy(tapZonePercent = it.roundToInt()),
                            )
                        },
                    )
                    ReaderSliderRow(
                        label = "Swipe distance",
                        value = appearance.chapterSwipeDistance.toFloat(),
                        valueText = "${appearance.chapterSwipeDistance} px",
                        valueRange = 48f..192f,
                        steps = 143,
                        colors = colors,
                        onValueChange = {
                            onAppearanceChange(
                                appearance.copy(chapterSwipeDistance = it.roundToInt()),
                            )
                        },
                    )
                }
            }

            additionalSettings()
        }
    }
}

@Composable
fun NovelReaderThemeSwatchButton(
    label: String,
    backgroundColor: Int,
    textColor: Int,
    selected: Boolean,
    colors: NovelReaderResolvedColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    splitBackgroundColor: Int? = null,
) {
    Surface(
        modifier = modifier
            .width(84.dp)
            .height(64.dp)
            .clickable(onClick = onClick),
        color = if (selected) colors.selected else colors.background,
        contentColor = colors.text,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) colors.text else colors.outline,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(backgroundColor))
                    .border(1.dp, colors.outline, RoundedCornerShape(6.dp)),
            ) {
                if (splitBackgroundColor != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .fillMaxWidth(0.48f)
                            .background(Color(splitBackgroundColor)),
                    )
                }
                Text(
                    text = "Aa",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.caption,
                    color = Color(textColor),
                )
            }
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption,
                color = if (selected) colors.text else colors.mutedText,
            )
        }
    }
}

@Composable
private fun AppearanceSection(
    title: String,
    colors: NovelReaderResolvedColors,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle2,
            color = colors.text,
        )
        content()
    }
}

@Composable
private fun FontDropdown(
    appearance: NovelReaderAppearanceState,
    colors: NovelReaderResolvedColors,
    onFontSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clickable { expanded = true },
            color = colors.background,
            contentColor = colors.text,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, colors.outline),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = appearance.selectedFont,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body2,
                )
                Text(
                    text = "Font",
                    style = MaterialTheme.typography.caption,
                    color = colors.mutedText,
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            appearance.fontOptions.forEach { font ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onFontSelected(font)
                    },
                ) {
                    Text(text = font)
                }
            }
        }
    }
}

@Composable
private fun ReaderSliderRow(
    label: String,
    value: Float,
    valueText: String,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    colors: NovelReaderResolvedColors,
    onValueChange: (Float) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.body2,
                color = colors.text,
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.caption,
                color = colors.mutedText,
            )
        }
        Slider(
            value = value.coerceIn(valueRange.start, valueRange.endInclusive),
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = colors.text,
                activeTrackColor = colors.text,
                inactiveTrackColor = colors.outline,
            ),
        )
    }
}

@Composable
private fun ReaderSwitchRow(
    label: String,
    checked: Boolean,
    colors: NovelReaderResolvedColors,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body2,
            color = colors.text,
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun ReaderChoiceButton(
    text: String,
    selected: Boolean,
    colors: NovelReaderResolvedColors,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .height(42.dp)
            .clickable(onClick = onClick),
        color = if (selected) colors.selected else colors.background,
        contentColor = colors.text,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) colors.text else colors.outline,
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun roundToStep(
    value: Float,
    step: Double,
): Double {
    return ((value.toDouble() / step).roundToInt() * step)
}

private fun Double.shortDecimal(maxDigits: Int = 2): String {
    val multiplier = when (maxDigits.coerceIn(0, 3)) {
        0 -> 1
        1 -> 10
        2 -> 100
        else -> 1000
    }
    val scaled = (this * multiplier).roundToInt()
    val integer = scaled / multiplier
    val fraction = abs(scaled % multiplier)
    if (fraction == 0 || multiplier == 1) return integer.toString()

    val fractionText = fraction
        .toString()
        .padStart(multiplier.toString().length - 1, '0')
        .trimEnd('0')
    return "$integer.$fractionText"
}
