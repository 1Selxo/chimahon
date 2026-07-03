package app.chimahon.shared.settingsui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Slider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonSettingsScreenContent(
    screen: ChimahonSettingsScreen,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item(key = "${screen.route.key}:header") {
            ChimahonSettingsHeader(
                title = screen.title,
                subtitle = screen.subtitle,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
        }
        items(screen.sections, key = { it.key }) { section ->
            ChimahonSettingsSectionCard(
                section = section,
                onEvent = onEvent,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        item(key = "${screen.route.key}:bottom-spacer") {
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun ChimahonSettingsHeader(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.68f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun ChimahonSettingsSectionCard(
    section: ChimahonSettingsSection,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!section.subtitle.isNullOrBlank()) {
                Text(
                    text = section.subtitle,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.56f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface,
            border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
            elevation = 0.dp,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                section.preferences.forEachIndexed { index, preference ->
                    ChimahonPreferenceRow(preference = preference, onEvent = onEvent)
                    if (index < section.preferences.lastIndex) {
                        Divider(
                            modifier = Modifier.padding(start = 20.dp),
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.07f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonSettingsEntrySection(
    title: String,
    entries: List<ChimahonSettingsEntryRow>,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle2,
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
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface,
            border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
            elevation = 0.dp,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                entries.forEachIndexed { index, entry ->
                    ChimahonSettingsEntryListRow(entry = entry, onEvent = onEvent)
                    if (index < entries.lastIndex) {
                        Divider(
                            modifier = Modifier.padding(start = 20.dp),
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.07f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonSettingsEntryListRow(
    entry: ChimahonSettingsEntryRow,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val clickEvent = {
        val route = entry.route
        if (route != null) {
            onEvent(ChimahonSettingsUiEvent.Navigate(route))
        } else {
            onEvent(ChimahonSettingsUiEvent.ActionClicked(entry.key))
        }
    }
    PreferenceRowScaffold(
        title = entry.title,
        subtitle = entry.subtitle,
        enabled = entry.enabled,
        modifier = modifier.clickable(enabled = entry.enabled, onClick = clickEvent),
        trailing = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!entry.value.isNullOrBlank()) {
                    PreferenceValueText(entry.value, entry.enabled)
                }
                if (!entry.actionLabel.isNullOrBlank()) {
                    TextButton(
                        enabled = entry.enabled,
                        onClick = clickEvent,
                    ) {
                        Text(
                            text = entry.actionLabel,
                            color = if (entry.destructive) {
                                MaterialTheme.colors.error
                            } else {
                                MaterialTheme.colors.primary
                            },
                        )
                    }
                } else if (entry.route != null) {
                    Text(
                        text = ">",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSurface.copy(alpha = if (entry.enabled) 0.56f else 0.28f),
                    )
                }
            }
        },
    )
}

@Composable
fun ChimahonPreferenceRow(
    preference: ChimahonPreference,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (preference) {
        is ChimahonPreference.Action -> ChimahonActionPreference(
            preference = preference,
            onEvent = onEvent,
            modifier = modifier,
        )
        is ChimahonPreference.Info -> ChimahonInfoPreference(
            preference = preference,
            modifier = modifier,
        )
        is ChimahonPreference.ListSelect -> ChimahonListPreference(
            preference = preference,
            onEvent = onEvent,
            modifier = modifier,
        )
        is ChimahonPreference.MultiSelect -> ChimahonMultiSelectPreference(
            preference = preference,
            onEvent = onEvent,
            modifier = modifier,
        )
        is ChimahonPreference.NestedScreen -> ChimahonNestedPreference(
            preference = preference,
            onEvent = onEvent,
            modifier = modifier,
        )
        is ChimahonPreference.Slider -> ChimahonSliderPreference(
            preference = preference,
            onEvent = onEvent,
            modifier = modifier,
        )
        is ChimahonPreference.Switch -> ChimahonSwitchPreference(
            preference = preference,
            onEvent = onEvent,
            modifier = modifier,
        )
    }
}

@Composable
fun ChimahonSwitchPreference(
    preference: ChimahonPreference.Switch,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    PreferenceRowScaffold(
        title = preference.title,
        subtitle = preference.subtitle,
        enabled = preference.enabled,
        modifier = modifier.clickable(enabled = preference.enabled) {
            onEvent(ChimahonSettingsUiEvent.ToggleChanged(preference.key, !preference.checked))
        },
        trailing = {
            Switch(
                checked = preference.checked,
                onCheckedChange = {
                    onEvent(ChimahonSettingsUiEvent.ToggleChanged(preference.key, it))
                },
                enabled = preference.enabled,
            )
        },
    )
}

@Composable
fun ChimahonSliderPreference(
    preference: ChimahonPreference.Slider,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                PreferenceTitle(preference.title, preference.enabled)
                PreferenceSubtitle(preference.subtitle, preference.enabled)
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = preference.spec.valueLabel,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.primary.copy(alpha = if (preference.enabled) 1f else 0.38f),
                maxLines = 1,
            )
        }
        Slider(
            value = preference.spec.value.coerceIn(
                preference.spec.valueRange.start,
                preference.spec.valueRange.endInclusive,
            ),
            onValueChange = {
                onEvent(ChimahonSettingsUiEvent.SliderChanged(preference.key, it))
            },
            valueRange = preference.spec.valueRange,
            steps = preference.spec.steps,
            enabled = preference.enabled,
        )
    }
}

@Composable
fun ChimahonListPreference(
    preference: ChimahonPreference.ListSelect,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember(preference.key) { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxWidth()) {
        PreferenceRowScaffold(
            title = preference.title,
            subtitle = preference.subtitle,
            enabled = preference.enabled,
            modifier = Modifier.clickable(enabled = preference.enabled) { expanded = true },
            trailing = {
                PreferenceValueText(preference.selectedTitle, preference.enabled)
            },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            preference.options.forEach { option ->
                DropdownMenuItem(
                    enabled = preference.enabled && option.enabled,
                    onClick = {
                        expanded = false
                        onEvent(ChimahonSettingsUiEvent.OptionSelected(preference.key, option.key))
                    },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        RadioButton(
                            selected = option.key == preference.selectedKey,
                            onClick = null,
                            enabled = preference.enabled && option.enabled,
                        )
                        Column {
                            Text(option.title, style = MaterialTheme.typography.body2)
                            if (!option.subtitle.isNullOrBlank()) {
                                Text(
                                    text = option.subtitle,
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonMultiSelectPreference(
    preference: ChimahonPreference.MultiSelect,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember(preference.key) { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxWidth()) {
        PreferenceRowScaffold(
            title = preference.title,
            subtitle = preference.subtitle,
            enabled = preference.enabled,
            modifier = Modifier.clickable(enabled = preference.enabled) { expanded = true },
            trailing = {
                PreferenceValueText(preference.selectedTitle, preference.enabled)
            },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            preference.options.forEach { option ->
                val selected = option.key in preference.selectedKeys
                DropdownMenuItem(
                    enabled = preference.enabled && option.enabled,
                    onClick = {
                        onEvent(
                            ChimahonSettingsUiEvent.MultiOptionToggled(
                                key = preference.key,
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
                            enabled = preference.enabled && option.enabled,
                        )
                        Column {
                            Text(option.title, style = MaterialTheme.typography.body2)
                            if (!option.subtitle.isNullOrBlank()) {
                                Text(
                                    text = option.subtitle,
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonActionPreference(
    preference: ChimahonPreference.Action,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    PreferenceRowScaffold(
        title = preference.title,
        subtitle = preference.subtitle,
        enabled = preference.enabled,
        modifier = modifier.clickable(enabled = preference.enabled) {
            onEvent(ChimahonSettingsUiEvent.ActionClicked(preference.key))
        },
        trailing = {
            if (!preference.actionLabel.isNullOrBlank()) {
                TextButton(
                    enabled = preference.enabled,
                    onClick = { onEvent(ChimahonSettingsUiEvent.ActionClicked(preference.key)) },
                ) {
                    Text(
                        text = preference.actionLabel,
                        color = if (preference.destructive) {
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
fun ChimahonNestedPreference(
    preference: ChimahonPreference.NestedScreen,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    PreferenceRowScaffold(
        title = preference.title,
        subtitle = preference.subtitle,
        enabled = preference.enabled,
        modifier = modifier.clickable(enabled = preference.enabled) {
            onEvent(ChimahonSettingsUiEvent.Navigate(preference.route))
        },
        trailing = {
            Text(
                text = ">",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface.copy(alpha = if (preference.enabled) 0.56f else 0.28f),
            )
        },
    )
}

@Composable
fun ChimahonInfoPreference(
    preference: ChimahonPreference.Info,
    modifier: Modifier = Modifier,
) {
    val toneColor = preference.tone.settingsToneColor()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(toneColor.copy(alpha = 0.10f))
            .padding(horizontal = 20.dp, vertical = 14.dp),
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
                text = when (preference.tone) {
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
                text = preference.title,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.onSurface,
            )
            PreferenceSubtitle(preference.subtitle, preference.enabled)
        }
    }
}

@Composable
private fun PreferenceRowScaffold(
    title: String,
    subtitle: String?,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            PreferenceTitle(title, enabled)
            PreferenceSubtitle(subtitle, enabled)
        }
        trailing()
    }
}

@Composable
private fun PreferenceTitle(title: String, enabled: Boolean) {
    Text(
        text = title,
        style = MaterialTheme.typography.body1,
        color = MaterialTheme.colors.onSurface.copy(alpha = if (enabled) 0.90f else 0.38f),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun PreferenceSubtitle(subtitle: String?, enabled: Boolean) {
    if (!subtitle.isNullOrBlank()) {
        Text(
            text = subtitle,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = if (enabled) 0.60f else 0.32f),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun PreferenceValueText(value: String, enabled: Boolean) {
    Text(
        text = value,
        style = MaterialTheme.typography.body2,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colors.primary.copy(alpha = if (enabled) 1f else 0.38f),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.widthIn(max = 180.dp),
    )
}

@Composable
private fun ChimahonPreferenceTone.settingsToneColor(): Color = when (this) {
    ChimahonPreferenceTone.Error -> MaterialTheme.colors.error
    ChimahonPreferenceTone.Info -> MaterialTheme.colors.primary
    ChimahonPreferenceTone.Success -> Color(0xFF2E7D32)
    ChimahonPreferenceTone.Warning -> Color(0xFFE08B00)
}
