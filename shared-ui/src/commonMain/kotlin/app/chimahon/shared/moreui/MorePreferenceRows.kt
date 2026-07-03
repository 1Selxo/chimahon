package app.chimahon.shared.moreui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonMoreMenuSection(
    section: ChimahonMoreMenuSectionState,
    onEntryClick: (ChimahonMoreMenuEntry) -> Unit,
    modifier: Modifier = Modifier,
    onEntryToggle: (ChimahonMoreMenuEntry, Boolean) -> Unit = { entry, _ -> onEntryClick(entry) },
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ChimahonMoreCategoryHeader(title = section.title, colors = colors)
        section.entries.forEach { entry ->
            ChimahonMoreMenuEntryRow(
                entry = entry,
                onClick = { onEntryClick(entry) },
                onToggle = { checked -> onEntryToggle(entry, checked) },
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonMoreMenuEntryRow(
    entry: ChimahonMoreMenuEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onToggle: (Boolean) -> Unit = {},
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    val isToggle = entry.kind == ChimahonMoreMenuEntryKind.Toggle
    ChimahonMoreListItem(
        title = entry.title,
        subtitle = entry.subtitle,
        icon = entry.icon,
        marker = entry.badge?.takeIf { it.length <= 3 },
        value = entry.value,
        enabled = entry.enabled,
        selected = entry.selected,
        warning = entry.warning || entry.kind == ChimahonMoreMenuEntryKind.Destructive,
        pills = entry.pills.takeIf { !isToggle }.orEmpty(),
        onClick = if (isToggle) {
            { onToggle(!entry.selected) }
        } else {
            onClick
        },
        trailing = when {
            isToggle -> {
                {
                    Switch(
                        checked = entry.selected,
                        onCheckedChange = onToggle,
                        enabled = entry.enabled,
                    )
                }
            }
            entry.action != null -> {
                {
                    ChimahonMoreActionPill(
                        action = entry.action,
                        onClick = onClick,
                        selected = entry.selected,
                        colors = colors,
                    )
                }
            }
            else -> null
        },
        modifier = modifier,
        colors = colors,
    )
}

@Composable
fun ChimahonMoreListItem(
    title: String,
    subtitle: String,
    icon: ChimahonMoreIcon,
    modifier: Modifier = Modifier,
    marker: String? = null,
    value: String? = null,
    enabled: Boolean = true,
    selected: Boolean = false,
    warning: Boolean = false,
    pills: List<ChimahonMoreStatusPill> = emptyList(),
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .then(if (onClick != null) Modifier.clickable(enabled = enabled, onClick = onClick) else Modifier)
                .padding(start = 16.dp, end = 8.dp, top = 9.dp, bottom = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChimahonMoreIconTile(
                icon = icon,
                marker = marker,
                active = selected,
                warning = warning,
                colors = colors,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
            ) {
                ChimahonMoreLabel(
                    text = title,
                    color = colors.content.copy(alpha = if (enabled) 0.92f else 0.44f),
                    size = 14,
                    weight = FontWeight.SemiBold,
                    maxLines = 2,
                )
                ChimahonMoreLabel(
                    text = subtitle,
                    color = colors.secondaryContent.copy(alpha = if (enabled) 1f else 0.54f),
                    size = 12,
                    lineHeight = 17,
                    maxLines = 3,
                    modifier = Modifier.padding(top = 3.dp),
                )
                if (pills.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.padding(top = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(pills, key = { it.text }) { pill ->
                            ChimahonMoreStatusPillView(pill = pill, colors = colors)
                        }
                    }
                }
            }
            if (!value.isNullOrBlank()) {
                ChimahonMoreLabel(
                    text = value,
                    color = colors.secondaryContent,
                    size = 12,
                    maxLines = 1,
                    modifier = Modifier.padding(end = 8.dp),
                )
            }
            if (trailing != null) {
                trailing()
            } else if (onClick != null) {
                Icon(
                    imageVector = ChimahonMoreIcon.Chevron.imageVector,
                    contentDescription = "Open $title",
                    tint = colors.secondaryContent,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
        }
        ChimahonMoreDivider(startIndent = 72.dp, colors = colors)
    }
}

@Composable
fun ChimahonMoreSwitchRow(
    title: String,
    subtitle: String,
    icon: ChimahonMoreIcon,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    ChimahonMoreListItem(
        title = title,
        subtitle = subtitle,
        icon = icon,
        enabled = enabled,
        selected = checked,
        onClick = { onCheckedChange(!checked) },
        trailing = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
            )
        },
        modifier = modifier,
        colors = colors,
    )
}

@Composable
fun ChimahonMoreValueRow(
    title: String,
    subtitle: String,
    icon: ChimahonMoreIcon,
    value: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    ChimahonMoreListItem(
        title = title,
        subtitle = subtitle,
        icon = icon,
        value = value,
        onClick = onClick,
        modifier = modifier,
        colors = colors,
    )
}

@Composable
fun ChimahonMoreChoiceRow(
    title: String,
    options: List<ChimahonMorePreferenceOption>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
    ) {
        ChimahonMoreLabel(
            text = title,
            color = colors.content,
            size = 14,
            weight = FontWeight.SemiBold,
        )
        if (!subtitle.isNullOrBlank()) {
            ChimahonMoreLabel(
                text = subtitle,
                color = colors.secondaryContent,
                size = 12,
                lineHeight = 17,
                maxLines = 2,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(options, key = { it.key }) { option ->
                ChimahonMoreOptionChip(
                    option = option,
                    selected = option.key == selectedKey,
                    onClick = { onSelect(option.key) },
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun ChimahonMoreMultiChoiceRow(
    title: String,
    options: List<ChimahonMorePreferenceOption>,
    selectedKeys: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
    ) {
        ChimahonMoreLabel(
            text = title,
            color = colors.content,
            size = 14,
            weight = FontWeight.SemiBold,
        )
        if (!subtitle.isNullOrBlank()) {
            ChimahonMoreLabel(
                text = subtitle,
                color = colors.secondaryContent,
                size = 12,
                lineHeight = 17,
                maxLines = 2,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(options, key = { it.key }) { option ->
                ChimahonMoreOptionChip(
                    option = option,
                    selected = option.key in selectedKeys,
                    onClick = { onToggle(option.key) },
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun ChimahonMoreOptionChip(
    option: ChimahonMorePreferenceOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    Column(
        modifier = modifier
            .heightIn(min = 38.dp)
            .background(
                color = if (selected) colors.primaryContainer else colors.surfaceVariant,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(19.dp),
            )
            .clickable(enabled = option.enabled, onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 8.dp),
    ) {
        ChimahonMoreLabel(
            text = option.title,
            color = if (selected) colors.primary else colors.secondaryContent,
            size = 12,
            weight = FontWeight.SemiBold,
        )
        option.subtitle?.let {
            ChimahonMoreLabel(
                text = it,
                color = colors.secondaryContent,
                size = 10,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
