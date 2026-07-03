package app.chimahon.shared.settingsui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Slider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonAndroidSettingsScreenContent(
    screen: ChimahonSettingsScreen,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = 12.dp),
) {
    val categories = remember(screen) { screen.toPreferenceCategoryUiModels() }
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item(key = "${screen.route.key}:android-header") {
            ChimahonSettingsHeader(
                title = screen.title,
                subtitle = screen.subtitle,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            )
        }
        items(categories, key = { it.key }) { category ->
            ChimahonPreferenceCategory(
                category = category,
                onEvent = onEvent,
            )
        }
        item(key = "${screen.route.key}:android-bottom-spacer") {
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun ChimahonPreferenceCategory(
    category: ChimahonPreferenceCategoryUiModel,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    showDividers: Boolean = true,
) {
    if (category.hasRows) {
        Column(modifier = modifier.fillMaxWidth()) {
            ChimahonPreferenceCategoryHeader(
                title = category.title,
                subtitle = category.subtitle,
                modifier = Modifier.padding(start = 24.dp, top = 18.dp, end = 24.dp, bottom = 4.dp),
            )
            category.rows.forEachIndexed { index, row ->
                ChimahonPreferenceCategoryRow(
                    row = row,
                    onEvent = onEvent,
                )
                if (showDividers && index < category.rows.lastIndex) {
                    Divider(
                        modifier = Modifier.padding(start = 24.dp),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
                    )
                }
            }
        }
    }
}

@Composable
fun ChimahonPreferenceCategoryHeader(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.56f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun ChimahonPreferenceCategoryRow(
    row: ChimahonPreferenceRowUiModel,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (row.widget) {
        ChimahonPreferenceRowWidget.Action -> ChimahonActionPreferenceRow(
            row = row,
            onEvent = onEvent,
            modifier = modifier,
        )
        ChimahonPreferenceRowWidget.Info -> ChimahonInfoPreferenceRow(
            row = row,
            modifier = modifier,
        )
        ChimahonPreferenceRowWidget.List -> ChimahonListPreferenceRow(
            row = row,
            onEvent = onEvent,
            modifier = modifier,
        )
        ChimahonPreferenceRowWidget.MultiSelect -> ChimahonMultiSelectPreferenceRow(
            row = row,
            onEvent = onEvent,
            modifier = modifier,
        )
        ChimahonPreferenceRowWidget.Navigation -> ChimahonNavigationPreferenceRow(
            row = row,
            onEvent = onEvent,
            modifier = modifier,
        )
        ChimahonPreferenceRowWidget.Plain -> ChimahonTwoLinePreferenceRow(
            row = row,
            modifier = modifier,
        )
        ChimahonPreferenceRowWidget.Slider -> ChimahonSliderPreferenceRow(
            row = row,
            onEvent = onEvent,
            modifier = modifier,
        )
        ChimahonPreferenceRowWidget.Switch -> ChimahonSwitchPreferenceRow(
            row = row,
            onEvent = onEvent,
            modifier = modifier,
        )
    }
}

@Composable
fun ChimahonTwoLinePreferenceRow(
    row: ChimahonPreferenceRowUiModel,
    modifier: Modifier = Modifier,
    titleColor: Color? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val secondaryText = row.secondaryText
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = if (secondaryText.isNullOrBlank()) 56.dp else 72.dp)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = row.title,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Normal,
                color = titleColor
                    ?: MaterialTheme.colors.onSurface.copy(alpha = if (row.enabled) 0.90f else 0.38f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!secondaryText.isNullOrBlank()) {
                Text(
                    text = secondaryText,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = if (row.enabled) 0.62f else 0.32f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (trailing != null) {
            trailing()
        }
    }
}

@Composable
fun ChimahonSwitchPreferenceRow(
    row: ChimahonPreferenceRowUiModel,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val checked = row.checked == true
    ChimahonTwoLinePreferenceRow(
        row = row,
        modifier = modifier.clickable(enabled = row.enabled) {
            onEvent(ChimahonSettingsUiEvent.ToggleChanged(row.key, !checked))
        },
        trailing = {
            Switch(
                checked = checked,
                onCheckedChange = {
                    onEvent(ChimahonSettingsUiEvent.ToggleChanged(row.key, it))
                },
                enabled = row.enabled,
            )
        },
    )
}

@Composable
fun ChimahonSliderPreferenceRow(
    row: ChimahonPreferenceRowUiModel,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spec = row.sliderSpec
    if (spec == null) {
        ChimahonTwoLinePreferenceRow(row = row, modifier = modifier)
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = row.title,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface.copy(alpha = if (row.enabled) 0.90f else 0.38f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!row.summary.isNullOrBlank()) {
                        Text(
                            text = row.summary,
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = if (row.enabled) 0.62f else 0.32f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Text(
                    text = spec.valueLabel,
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.primary.copy(alpha = if (row.enabled) 1f else 0.38f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 96.dp),
                )
            }
            Slider(
                value = spec.value.coerceIn(spec.valueRange.start, spec.valueRange.endInclusive),
                onValueChange = {
                    onEvent(ChimahonSettingsUiEvent.SliderChanged(row.key, it))
                },
                valueRange = spec.valueRange,
                steps = spec.steps,
                enabled = row.enabled,
            )
        }
    }
}

@Composable
fun ChimahonListPreferenceRow(
    row: ChimahonPreferenceRowUiModel,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember(row.key) { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxWidth()) {
        ChimahonTwoLinePreferenceRow(
            row = row,
            modifier = Modifier.clickable(enabled = row.enabled) { expanded = true },
            trailing = {
                PreferenceChevron(enabled = row.enabled)
            },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            row.options.forEach { option ->
                DropdownMenuItem(
                    enabled = row.enabled && option.enabled,
                    onClick = {
                        expanded = false
                        onEvent(ChimahonSettingsUiEvent.OptionSelected(row.key, option.key))
                    },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        RadioButton(
                            selected = option.key == row.selectedKey,
                            onClick = null,
                            enabled = row.enabled && option.enabled,
                        )
                        OptionText(option = option)
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonMultiSelectPreferenceRow(
    row: ChimahonPreferenceRowUiModel,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember(row.key) { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxWidth()) {
        ChimahonTwoLinePreferenceRow(
            row = row,
            modifier = Modifier.clickable(enabled = row.enabled) { expanded = true },
            trailing = {
                PreferenceChevron(enabled = row.enabled)
            },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            row.options.forEach { option ->
                val selected = option.key in row.selectedKeys
                DropdownMenuItem(
                    enabled = row.enabled && option.enabled,
                    onClick = {
                        onEvent(
                            ChimahonSettingsUiEvent.MultiOptionToggled(
                                key = row.key,
                                optionKey = option.key,
                                selected = !selected,
                            ),
                        )
                    },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Checkbox(
                            checked = selected,
                            onCheckedChange = null,
                            enabled = row.enabled && option.enabled,
                        )
                        OptionText(option = option)
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonActionPreferenceRow(
    row: ChimahonPreferenceRowUiModel,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    ChimahonTwoLinePreferenceRow(
        row = row,
        modifier = modifier.clickable(enabled = row.enabled) {
            onEvent(ChimahonSettingsUiEvent.ActionClicked(row.key))
        },
        titleColor = if (row.destructive) {
            MaterialTheme.colors.error.copy(alpha = if (row.enabled) 1f else 0.38f)
        } else {
            null
        },
        trailing = {
            if (!row.actionLabel.isNullOrBlank()) {
                TextButton(
                    enabled = row.enabled,
                    onClick = { onEvent(ChimahonSettingsUiEvent.ActionClicked(row.key)) },
                ) {
                    Text(
                        text = row.actionLabel,
                        color = if (row.destructive) {
                            MaterialTheme.colors.error
                        } else {
                            MaterialTheme.colors.primary
                        },
                    )
                }
            }
        },
    )
}

@Composable
fun ChimahonNavigationPreferenceRow(
    row: ChimahonPreferenceRowUiModel,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    ChimahonTwoLinePreferenceRow(
        row = row,
        modifier = modifier.clickable(enabled = row.enabled && row.targetRoute != null) {
            row.targetRoute?.let { onEvent(ChimahonSettingsUiEvent.Navigate(it)) }
        },
        trailing = {
            PreferenceChevron(enabled = row.enabled)
        },
    )
}

@Composable
fun ChimahonInfoPreferenceRow(
    row: ChimahonPreferenceRowUiModel,
    modifier: Modifier = Modifier,
) {
    val toneColor = row.tone.androidPreferenceToneColor()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(toneColor.copy(alpha = 0.10f))
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(toneColor.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = when (row.tone) {
                    ChimahonPreferenceTone.Error -> "!"
                    ChimahonPreferenceTone.Info -> "i"
                    ChimahonPreferenceTone.Success -> "+"
                    ChimahonPreferenceTone.Warning -> "!"
                },
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Bold,
                color = toneColor,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = row.title,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.onSurface.copy(alpha = if (row.enabled) 0.90f else 0.38f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!row.summary.isNullOrBlank()) {
                Text(
                    text = row.summary,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = if (row.enabled) 0.62f else 0.32f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun PreferenceChevron(enabled: Boolean) {
    Text(
        text = ">",
        style = MaterialTheme.typography.body1,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colors.onSurface.copy(alpha = if (enabled) 0.46f else 0.24f),
    )
}

@Composable
private fun OptionText(option: ChimahonPreferenceOption) {
    Column(modifier = Modifier.widthIn(max = 260.dp)) {
        Text(
            text = option.title,
            style = MaterialTheme.typography.body2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (!option.subtitle.isNullOrBlank()) {
            Text(
                text = option.subtitle,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ChimahonPreferenceTone.androidPreferenceToneColor(): Color = when (this) {
    ChimahonPreferenceTone.Error -> MaterialTheme.colors.error
    ChimahonPreferenceTone.Info -> MaterialTheme.colors.primary
    ChimahonPreferenceTone.Success -> Color(0xFF2E7D32)
    ChimahonPreferenceTone.Warning -> Color(0xFFE08B00)
}
