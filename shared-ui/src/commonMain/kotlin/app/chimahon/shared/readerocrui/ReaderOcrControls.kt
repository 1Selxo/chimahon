package app.chimahon.shared.readerocrui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Public
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonReaderOcrToolbar(
    state: ChimahonReaderOcrUiState,
    actions: ChimahonReaderOcrUiActions,
    modifier: Modifier = Modifier,
    buttons: List<ChimahonReaderOcrToolbarButtonState> = defaultReaderOcrToolbarButtons(state),
    showLanguageSelector: Boolean = true,
    showStatus: Boolean = true,
) {
    val colors = readerOcrColors()
    Surface(
        modifier = modifier,
        color = colors.chrome,
        contentColor = colors.onChrome,
        shape = RoundedCornerShape(28.dp),
        elevation = 8.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            buttons.forEach { button ->
                ChimahonReaderOcrToolbarButton(
                    state = button,
                    onClick = { actions.run(button.action) },
                    colors = colors,
                )
            }
            if (showLanguageSelector) {
                Spacer(Modifier.width(2.dp))
                ChimahonReaderOcrLanguageSelector(
                    languages = state.languages,
                    selectedLanguageId = state.selectedLanguageId,
                    enabled = state.enabled && state.phase != ChimahonReaderOcrLoadPhase.Loading,
                    colors = colors,
                    onLanguageSelected = actions.onLanguageSelected,
                )
            }
            if (showStatus) {
                Spacer(Modifier.width(2.dp))
                ChimahonReaderOcrStatusChip(
                    title = state.statusText,
                    detail = state.detailText,
                    phase = state.phase,
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun ChimahonReaderOcrToolbarButton(
    state: ChimahonReaderOcrToolbarButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonReaderOcrColors = readerOcrColors(),
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (state.selected) colors.selectedControl else colors.control)
            .alpha(if (state.enabled) 1f else 0.42f)
            .clickable(enabled = state.enabled, role = Role.Button, onClick = onClick)
            .semantics {
                contentDescription = state.action.contentDescription
                stateDescription = when {
                    state.loading -> "Loading"
                    state.selected -> "Selected"
                    state.enabled -> "Available"
                    else -> "Disabled"
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        if (state.loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = colors.onChrome,
                strokeWidth = 2.dp,
            )
        } else {
            Icon(
                imageVector = state.action.imageVector,
                contentDescription = null,
                tint = colors.onChrome,
                modifier = Modifier.size(22.dp),
            )
        }
        state.badgeText?.let { badge ->
            Text(
                text = badge,
                color = colors.onAccent,
                style = MaterialTheme.typography.overline.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(colors.accent)
                    .padding(horizontal = 4.dp),
            )
        }
    }
}

@Composable
fun ChimahonReaderOcrLanguageSelector(
    languages: List<ChimahonReaderOcrLanguageOption>,
    selectedLanguageId: String,
    onLanguageSelected: (ChimahonReaderOcrLanguageOption) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ChimahonReaderOcrColors = readerOcrColors(),
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = languages.firstOrNull { it.id == selectedLanguageId }
        ?: languages.firstOrNull()
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(colors.control)
                .clickable(enabled = enabled && languages.isNotEmpty()) { expanded = true }
                .semantics {
                    contentDescription = "OCR language"
                    stateDescription = selected?.displayLabel ?: "No language selected"
                }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Public,
                contentDescription = null,
                tint = colors.secondaryText,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = selected?.label ?: "Auto",
                color = colors.onChrome,
                style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            languages.forEach { language ->
                DropdownMenuItem(
                    enabled = language.enabled,
                    onClick = {
                        expanded = false
                        onLanguageSelected(language)
                    },
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (language.id == selectedLanguageId) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary,
                                modifier = Modifier.size(18.dp),
                            )
                        } else {
                            Spacer(Modifier.size(18.dp))
                        }
                        Column {
                            Text(
                                text = language.label,
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onSurface,
                            )
                            if (language.nativeLabel.isNotBlank() && language.nativeLabel != language.label) {
                                Text(
                                    text = language.nativeLabel,
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
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
fun ChimahonReaderOcrBoxToggleChips(
    modes: List<ChimahonReaderOcrBoxMode>,
    selectedMode: ChimahonReaderOcrBoxMode,
    boxesVisible: Boolean,
    onModeSelected: (ChimahonReaderOcrBoxMode) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ChimahonReaderOcrColors = readerOcrColors(),
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        modes.forEach { mode ->
            val selected = mode == selectedMode && boxesVisible
            ChimahonReaderOcrChip(
                text = mode.label,
                selected = selected,
                enabled = enabled,
                colors = colors,
                contentDescription = mode.description,
                onClick = { onModeSelected(mode) },
            )
        }
    }
}

@Composable
fun ChimahonReaderOcrStatusChip(
    title: String,
    modifier: Modifier = Modifier,
    detail: String? = null,
    phase: ChimahonReaderOcrLoadPhase = ChimahonReaderOcrLoadPhase.Idle,
    colors: ChimahonReaderOcrColors = readerOcrColors(),
) {
    val accent = when (phase) {
        ChimahonReaderOcrLoadPhase.Failed -> MaterialTheme.colors.error
        ChimahonReaderOcrLoadPhase.Loading -> colors.accent
        ChimahonReaderOcrLoadPhase.Ready -> colors.ready
        ChimahonReaderOcrLoadPhase.Idle -> colors.secondaryText
    }
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(colors.control)
            .border(1.dp, accent.copy(alpha = 0.34f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(accent),
        )
        Column {
            Text(
                text = title,
                color = colors.onChrome,
                style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!detail.isNullOrBlank()) {
                Text(
                    text = detail,
                    color = colors.secondaryText,
                    style = MaterialTheme.typography.overline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun ChimahonReaderOcrTapHintChip(
    visible: Boolean,
    modifier: Modifier = Modifier,
    text: String = "Tap highlighted text to look up",
    colors: ChimahonReaderOcrColors = readerOcrColors(),
    onDismiss: (() -> Unit)? = null,
) {
    if (!visible) return
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(colors.chrome)
            .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = if (onDismiss == null) 12.dp else 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            color = colors.onChrome,
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (onDismiss != null) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = ChimahonReaderOcrAction.Close.imageVector,
                    contentDescription = "Dismiss OCR hint",
                    tint = colors.secondaryText,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun ChimahonReaderOcrChip(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    colors: ChimahonReaderOcrColors,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) colors.selectedControl else colors.control)
            .border(
                width = 1.dp,
                color = if (selected) colors.accent.copy(alpha = 0.72f) else colors.divider,
                shape = RoundedCornerShape(18.dp),
            )
            .alpha(if (enabled) 1f else 0.46f)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics {
                this.contentDescription = contentDescription
                stateDescription = if (selected) "Selected" else "Not selected"
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            color = colors.onChrome,
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
fun readerOcrColors(): ChimahonReaderOcrColors {
    val dark = MaterialTheme.colors.isLight.not()
    return ChimahonReaderOcrColors(
        chrome = if (dark) Color(0xEA17171C) else Color(0xF6FFFFFF),
        control = if (dark) Color(0xFF2B2B31).copy(alpha = 0.86f) else Color(0xFFEFEAF7),
        selectedControl = if (dark) MaterialTheme.colors.primary.copy(alpha = 0.82f) else Color(0xFFE1D8F6),
        onChrome = if (dark) Color.White else Color(0xFF25222E),
        secondaryText = if (dark) Color.White.copy(alpha = 0.66f) else Color(0xFF696272),
        accent = MaterialTheme.colors.primary,
        onAccent = MaterialTheme.colors.onPrimary,
        ready = Color(0xFF52B788),
        divider = if (dark) Color.White.copy(alpha = 0.14f) else Color(0xFFD8CEE5),
    )
}

data class ChimahonReaderOcrColors(
    val chrome: Color,
    val control: Color,
    val selectedControl: Color,
    val onChrome: Color,
    val secondaryText: Color,
    val accent: Color,
    val onAccent: Color,
    val ready: Color,
    val divider: Color,
)
