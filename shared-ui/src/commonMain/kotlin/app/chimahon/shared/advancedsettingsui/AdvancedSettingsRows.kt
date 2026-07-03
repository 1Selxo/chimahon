package app.chimahon.shared.advancedsettingsui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
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
fun ChimahonAdvancedSettingsGroupCard(
    group: ChimahonAdvancedSettingsGroup,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = group.title,
                style = MaterialTheme.typography.subtitle2,
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!group.subtitle.isNullOrBlank()) {
                Text(
                    text = group.subtitle,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.60f),
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
                group.items.forEachIndexed { index, item ->
                    ChimahonAdvancedSettingRow(item = item, onEvent = onEvent)
                    if (index < group.items.lastIndex) {
                        Divider(
                            modifier = Modifier.padding(start = 72.dp),
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.07f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonAdvancedSettingRow(
    item: ChimahonAdvancedSettingItem,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (item) {
        is ChimahonAdvancedSettingItem.Action -> AdvancedActionRow(item, onEvent, modifier)
        is ChimahonAdvancedSettingItem.Choice -> AdvancedChoiceRow(item, onEvent, modifier)
        is ChimahonAdvancedSettingItem.Info -> AdvancedInfoRow(item, modifier)
        is ChimahonAdvancedSettingItem.Link -> AdvancedLinkRow(item, onEvent, modifier)
        is ChimahonAdvancedSettingItem.MultiChoice -> AdvancedMultiChoiceRow(item, onEvent, modifier)
        is ChimahonAdvancedSettingItem.Slider -> AdvancedSliderRow(item, onEvent, modifier)
        is ChimahonAdvancedSettingItem.Toggle -> AdvancedToggleRow(item, onEvent, modifier)
    }
}

@Composable
private fun AdvancedToggleRow(
    item: ChimahonAdvancedSettingItem.Toggle,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    AdvancedBaseRow(
        item = item,
        modifier = modifier.clickable(enabled = item.enabled) {
            onEvent(ChimahonAdvancedSettingsEvent.ToggleChanged(item.key, !item.checked))
        },
        trailing = {
            Switch(
                checked = item.checked,
                onCheckedChange = { onEvent(ChimahonAdvancedSettingsEvent.ToggleChanged(item.key, it)) },
                enabled = item.enabled,
            )
        },
    )
}

@Composable
private fun AdvancedChoiceRow(
    item: ChimahonAdvancedSettingItem.Choice,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember(item.key) { mutableStateOf(false) }
    AdvancedBaseRow(
        item = item,
        detail = item.selectedTitle,
        modifier = modifier.clickable(enabled = item.enabled) { expanded = true },
        trailing = {
            Box {
                TextButton(
                    onClick = { expanded = true },
                    enabled = item.enabled,
                ) {
                    Text(item.selectedTitle)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    item.options.forEach { option ->
                        DropdownMenuItem(
                            enabled = option.enabled,
                            onClick = {
                                expanded = false
                                onEvent(ChimahonAdvancedSettingsEvent.ChoiceSelected(item.key, option.key))
                            },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = option.key == item.selectedKey,
                                    onClick = null,
                                    enabled = option.enabled,
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(option.title, style = MaterialTheme.typography.body2)
                                    if (!option.subtitle.isNullOrBlank()) {
                                        Text(
                                            text = option.subtitle,
                                            style = MaterialTheme.typography.caption,
                                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.60f),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun AdvancedMultiChoiceRow(
    item: ChimahonAdvancedSettingItem.MultiChoice,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember(item.key) { mutableStateOf(false) }
    AdvancedBaseRow(
        item = item,
        detail = item.selectedTitle,
        modifier = modifier.clickable(enabled = item.enabled) { expanded = true },
        trailing = {
            Box {
                TextButton(
                    onClick = { expanded = true },
                    enabled = item.enabled,
                ) {
                    Text(item.selectedTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    item.options.forEach { option ->
                        val selected = option.key in item.selectedKeys
                        DropdownMenuItem(
                            enabled = item.enabled && option.enabled,
                            onClick = {
                                onEvent(
                                    ChimahonAdvancedSettingsEvent.MultiChoiceToggled(
                                        key = item.key,
                                        optionKey = option.key,
                                        selected = !selected,
                                    ),
                                )
                            },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selected,
                                    onCheckedChange = null,
                                    enabled = item.enabled && option.enabled,
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(option.title, style = MaterialTheme.typography.body2)
                                    if (!option.subtitle.isNullOrBlank()) {
                                        Text(
                                            text = option.subtitle,
                                            style = MaterialTheme.typography.caption,
                                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.60f),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun AdvancedSliderRow(
    item: ChimahonAdvancedSettingItem.Slider,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AdvancedIconTile(icon = item.icon, tone = item.tone, enabled = item.enabled)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = if (item.enabled) 0.90f else 0.38f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!item.subtitle.isNullOrBlank()) {
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = if (item.enabled) 0.60f else 0.32f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Text(
                text = item.valueLabel,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.primary.copy(alpha = if (item.enabled) 1f else 0.38f),
                fontWeight = FontWeight.SemiBold,
            )
        }
        Slider(
            value = item.value,
            onValueChange = { onEvent(ChimahonAdvancedSettingsEvent.SliderChanged(item.key, it)) },
            valueRange = item.valueRange,
            steps = item.steps,
            enabled = item.enabled,
        )
    }
}

@Composable
private fun AdvancedActionRow(
    item: ChimahonAdvancedSettingItem.Action,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    AdvancedBaseRow(
        item = item,
        modifier = modifier.clickable(enabled = item.enabled) {
            onEvent(ChimahonAdvancedSettingsEvent.ActionClicked(item.key))
        },
        below = {
            val progress = item.progress
            if (progress != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 72.dp, end = 20.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = progress.label,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f),
                    )
                    if (progress.fraction == null) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    } else {
                        LinearProgressIndicator(
                            progress = progress.fraction.coerceIn(0f, 1f),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        },
        trailing = {
            TextButton(
                onClick = { onEvent(ChimahonAdvancedSettingsEvent.ActionClicked(item.key)) },
                enabled = item.enabled,
            ) {
                Text(
                    text = item.actionLabel,
                    color = advancedToneColor(item.tone).copy(alpha = if (item.enabled) 1f else 0.38f),
                )
            }
        },
    )
}

@Composable
private fun AdvancedInfoRow(
    item: ChimahonAdvancedSettingItem.Info,
    modifier: Modifier = Modifier,
) {
    AdvancedBaseRow(
        item = item,
        detail = item.value,
        modifier = modifier,
        trailing = {
            if (!item.value.isNullOrBlank()) {
                ChimahonAdvancedStatusPill(text = item.value, tone = item.tone, enabled = item.enabled)
            }
        },
    )
}

@Composable
private fun AdvancedLinkRow(
    item: ChimahonAdvancedSettingItem.Link,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    AdvancedBaseRow(
        item = item,
        modifier = modifier.clickable(enabled = item.enabled) {
            onEvent(ChimahonAdvancedSettingsEvent.Navigate(item.route))
        },
        trailing = {
            Text(
                text = ">",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface.copy(alpha = if (item.enabled) 0.56f else 0.28f),
            )
        },
    )
}

@Composable
private fun AdvancedBaseRow(
    item: ChimahonAdvancedSettingItem,
    modifier: Modifier = Modifier,
    detail: String? = null,
    trailing: @Composable RowScope.() -> Unit = {},
    below: @Composable () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AdvancedIconTile(icon = item.icon, tone = item.tone, enabled = item.enabled)
            Spacer(Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = if (item.enabled) 0.90f else 0.38f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val supporting = item.subtitle ?: detail
                if (!supporting.isNullOrBlank()) {
                    Text(
                        text = supporting,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = if (item.enabled) 0.60f else 0.32f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = trailing,
            )
        }
        below()
    }
}

@Composable
fun AdvancedIconTile(
    icon: ChimahonAdvancedSettingIcon?,
    tone: ChimahonAdvancedSettingTone,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val tint = advancedToneColor(tone)
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(tint.copy(alpha = if (enabled) 0.13f else 0.06f)),
        contentAlignment = Alignment.Center,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon.imageVector,
                contentDescription = null,
                tint = tint.copy(alpha = if (enabled) 1f else 0.38f),
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
fun ChimahonAdvancedStatusPill(
    text: String,
    tone: ChimahonAdvancedSettingTone,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val color = advancedToneColor(tone)
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color.copy(alpha = if (enabled) 0.13f else 0.06f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.caption,
            color = color.copy(alpha = if (enabled) 1f else 0.38f),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun advancedToneColor(tone: ChimahonAdvancedSettingTone): Color {
    return when (tone) {
        ChimahonAdvancedSettingTone.Neutral -> MaterialTheme.colors.primary
        ChimahonAdvancedSettingTone.Info -> MaterialTheme.colors.primary
        ChimahonAdvancedSettingTone.Success -> Color(0xFF2E7D32)
        ChimahonAdvancedSettingTone.Warning -> Color(0xFFB26A00)
        ChimahonAdvancedSettingTone.Error -> MaterialTheme.colors.error
    }
}
