package app.chimahon.shared.reader.settings

import app.chimahon.shared.ChimahonReaderCustomTheme
import app.chimahon.shared.ChimahonReaderSettings
import app.chimahon.shared.ChimahonReaderTextTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import kotlin.math.roundToInt

@Composable
fun ChimahonReaderAppearanceSheet(
    state: ChimahonReaderAppearanceState,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onImportFont: (() -> Unit)? = null,
    onDeleteFont: ((String) -> Unit)? = null,
    additionalSettings: @Composable ColumnScope.() -> Unit = {},
) {
    ReaderSheetDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        ChimahonReaderAppearanceSheetContent(
            state = state,
            onSettingsChange = onSettingsChange,
            onImportFont = onImportFont,
            onDeleteFont = onDeleteFont,
            additionalSettings = additionalSettings,
        )
    }
}

@Composable
fun ChimahonReaderAppearanceSheetContent(
    state: ChimahonReaderAppearanceState,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
    modifier: Modifier = Modifier,
    onImportFont: (() -> Unit)? = null,
    onDeleteFont: ((String) -> Unit)? = null,
    additionalSettings: @Composable ColumnScope.() -> Unit = {},
) {
    val settings = state.settings
    var showCustomThemeDialog by remember(settings.customBackgroundColor, settings.customTextColor) {
        mutableStateOf(false)
    }
    var renameTarget by remember { mutableStateOf<ChimahonReaderCustomTheme?>(null) }
    var deleteTarget by remember { mutableStateOf<ChimahonReaderCustomTheme?>(null) }

    ReaderSheetScaffold(
        title = "Appearance",
        subtitle = "Novel reader typography and theme",
        modifier = modifier,
    ) {
        ReaderThemeSection(
            settings = settings,
            onSettingsChange = onSettingsChange,
            onAddTheme = { showCustomThemeDialog = true },
            onRenameTheme = { renameTarget = it },
            onDeleteTheme = { deleteTarget = it },
        )

        ReaderSection(title = "Mode") {
            ReaderSegmentedRow(
                options = listOf("Paginated", "Continuous"),
                selected = if (settings.continuousMode) "Continuous" else "Paginated",
                onSelect = { selected ->
                    onSettingsChange(settings.copy(continuousMode = selected == "Continuous"))
                },
            )
        }

        ReaderTypographySection(
            state = state,
            onSettingsChange = onSettingsChange,
            onImportFont = onImportFont,
            onDeleteFont = onDeleteFont,
        )

        ReaderSection(title = "Margins") {
            ReaderSliderRow(
                label = "Horizontal padding",
                value = settings.horizontalPadding.toFloat(),
                valueLabel = formatChimahonReaderNumber(settings.horizontalPadding, "%", decimals = 1),
                valueRange = 0f..50f,
                steps = 99,
                onValueChange = {
                    onSettingsChange(
                        settings.copy(horizontalPadding = roundToStep(it, 0.5).coerceIn(0.0, 50.0)),
                    )
                },
            )
            ReaderSliderRow(
                label = "Vertical padding",
                value = settings.verticalPadding.toFloat(),
                valueLabel = formatChimahonReaderNumber(settings.verticalPadding, "%", decimals = 1),
                valueRange = 0f..50f,
                steps = 99,
                onValueChange = {
                    onSettingsChange(
                        settings.copy(verticalPadding = roundToStep(it, 0.5).coerceIn(0.0, 50.0)),
                    )
                },
            )
        }

        ReaderLayoutSection(
            settings = settings,
            onSettingsChange = onSettingsChange,
        )

        additionalSettings()
    }

    if (showCustomThemeDialog) {
        CustomReaderThemeDialog(
            initialName = "",
            initialBackgroundColor = settings.customBackgroundColor,
            initialTextColor = settings.customTextColor,
            title = "Save theme",
            confirmLabel = "Save",
            onDismiss = { showCustomThemeDialog = false },
            onConfirm = { theme ->
                onSettingsChange(
                    settings
                        .copy(customThemes = (settings.customThemes + theme).distinct())
                        .withCustomTheme(theme),
                )
                showCustomThemeDialog = false
            },
        )
    }

    renameTarget?.let { target ->
        CustomReaderThemeRenameDialog(
            target = target,
            onDismiss = { renameTarget = null },
            onConfirm = { renamed ->
                onSettingsChange(
                    settings.copy(
                        customThemes = settings.customThemes.map { theme ->
                            if (theme == target) renamed else theme
                        },
                    ),
                )
                renameTarget = null
            },
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete theme") },
            text = { Text("Delete \"${target.name.ifBlank { "Custom theme" }}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val nextThemes = settings.customThemes.filterNot { it == target }
                        val nextSettings = if (
                            settings.textTheme == ChimahonReaderTextTheme.Custom &&
                            settings.customBackgroundColor == target.backgroundColor &&
                            settings.customTextColor == target.textColor
                        ) {
                            settings.copy(
                                textTheme = ChimahonReaderTextTheme.Sepia,
                                customThemes = nextThemes,
                            )
                        } else {
                            settings.copy(customThemes = nextThemes)
                        }
                        onSettingsChange(nextSettings)
                        deleteTarget = null
                    },
                ) {
                    Text("Delete", color = MaterialTheme.colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun ReaderThemeSection(
    settings: ChimahonReaderSettings,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
    onAddTheme: () -> Unit,
    onRenameTheme: (ChimahonReaderCustomTheme) -> Unit,
    onDeleteTheme: (ChimahonReaderCustomTheme) -> Unit,
) {
    ReaderSection(title = "Theme") {
        val currentCustomTheme = settings.customThemes.firstOrNull {
            it.backgroundColor == settings.customBackgroundColor &&
                it.textColor == settings.customTextColor
        } ?: ChimahonReaderCustomTheme(
            backgroundColor = settings.customBackgroundColor,
            textColor = settings.customTextColor,
        )
        val customThemeChoices = remember(
            settings.customThemes,
            settings.textTheme,
            settings.customBackgroundColor,
            settings.customTextColor,
        ) {
            if (
                settings.textTheme == ChimahonReaderTextTheme.Custom &&
                currentCustomTheme !in settings.customThemes
            ) {
                listOf(currentCustomTheme) + settings.customThemes
            } else {
                settings.customThemes
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ChimahonReaderThemeSwatches.forEach { option ->
                ReaderThemeSwatchButton(
                    label = option.label,
                    backgroundColor = option.backgroundColor,
                    textColor = option.textColor,
                    splitBackgroundColor = option.splitBackgroundColor,
                    selected = settings.textTheme == option.theme,
                    onClick = { onSettingsChange(settings.copy(textTheme = option.theme)) },
                )
            }
            customThemeChoices.forEachIndexed { index, customTheme ->
                ReaderThemeSwatchButton(
                    label = customTheme.name.ifBlank { "Custom ${index + 1}" },
                    backgroundColor = customTheme.backgroundColor,
                    textColor = customTheme.textColor,
                    selected = settings.textTheme == ChimahonReaderTextTheme.Custom &&
                        settings.customBackgroundColor == customTheme.backgroundColor &&
                        settings.customTextColor == customTheme.textColor,
                    onClick = { onSettingsChange(settings.withCustomTheme(customTheme)) },
                    onRenameClick = { onRenameTheme(customTheme) },
                    onDeleteClick = { onDeleteTheme(customTheme) },
                )
            }
            AddThemeButton(onClick = onAddTheme)
        }

        if (settings.textTheme == ChimahonReaderTextTheme.System) {
            ReaderSwitchRow(
                label = "System uses Sepia",
                checked = settings.systemLightSepia,
                onCheckedChange = { onSettingsChange(settings.copy(systemLightSepia = it)) },
            )
        }
    }
}

@Composable
private fun ReaderTypographySection(
    state: ChimahonReaderAppearanceState,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
    onImportFont: (() -> Unit)?,
    onDeleteFont: ((String) -> Unit)?,
) {
    val settings = state.settings
    ReaderSection(title = "Typography") {
        ReaderDropdownRow(
            label = "Font family",
            selected = settings.selectedFont,
            options = state.allFonts.ifEmpty { ChimahonReaderAppearanceDefaults.defaultFonts },
            onSelect = { onSettingsChange(settings.copy(selectedFont = it)) },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (onImportFont != null) {
                ReaderPrimaryButton(
                    label = "Import font",
                    onClick = onImportFont,
                    busy = state.fontImportInProgress,
                    modifier = Modifier.weight(1f),
                )
            }
            if (settings.selectedFont in state.importedFonts && onDeleteFont != null) {
                OutlinedButton(
                    onClick = { onDeleteFont(settings.selectedFont) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Delete font", color = MaterialTheme.colors.error)
                }
            }
        }
        ReaderSliderRow(
            label = "Font size",
            value = settings.fontSize.toFloat(),
            valueLabel = formatChimahonReaderNumber(settings.fontSize, "px", decimals = 1),
            valueRange = 12f..72f,
            steps = 119,
            onValueChange = {
                onSettingsChange(settings.copy(fontSize = roundToStep(it, 0.5).coerceIn(12.0, 72.0)))
            },
        )
        ReaderSliderRow(
            label = "Line height",
            value = settings.lineHeight.toFloat(),
            valueLabel = formatChimahonReaderNumber(settings.lineHeight, decimals = 2),
            valueRange = 1.0f..2.5f,
            steps = 29,
            onValueChange = {
                onSettingsChange(settings.copy(lineHeight = roundToStep(it, 0.05).coerceIn(1.0, 2.5)))
            },
        )
        ReaderSwitchRow(
            label = "Hide furigana",
            checked = settings.hideFurigana,
            onCheckedChange = { onSettingsChange(settings.copy(hideFurigana = it)) },
        )
        ReaderSwitchRow(
            label = "Keep screen on",
            checked = settings.keepScreenOn,
            onCheckedChange = { onSettingsChange(settings.copy(keepScreenOn = it)) },
        )
    }
}

@Composable
private fun ReaderLayoutSection(
    settings: ChimahonReaderSettings,
    onSettingsChange: (ChimahonReaderSettings) -> Unit,
) {
    ReaderSection(title = "Layout") {
        Text(
            text = "Writing mode",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.84f),
        )
        ReaderSegmentedRow(
            options = listOf("Vertical", "Horizontal"),
            selected = if (settings.verticalWriting) "Vertical" else "Horizontal",
            onSelect = { selected ->
                onSettingsChange(settings.copy(verticalWriting = selected == "Vertical"))
            },
        )
        ReaderSliderRow(
            label = "Tap zone size",
            value = settings.tapZonePercent.toFloat(),
            valueLabel = "${settings.tapZonePercent}%",
            valueRange = 0f..40f,
            steps = 39,
            onValueChange = {
                onSettingsChange(settings.copy(tapZonePercent = it.roundToInt().coerceIn(0, 40)))
            },
        )
        ReaderSwitchRow(
            label = "Advanced",
            checked = settings.layoutAdvanced,
            onCheckedChange = { onSettingsChange(settings.copy(layoutAdvanced = it)) },
        )
        if (settings.layoutAdvanced) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ReaderSwitchRow(
                    label = "Avoid page break",
                    checked = settings.avoidPageBreak,
                    onCheckedChange = { onSettingsChange(settings.copy(avoidPageBreak = it)) },
                )
                ReaderSwitchRow(
                    label = "Justify text",
                    checked = settings.justifyText,
                    onCheckedChange = { onSettingsChange(settings.copy(justifyText = it)) },
                )
                ReaderSliderRow(
                    label = "Character spacing",
                    value = settings.characterSpacing.toFloat(),
                    valueLabel = formatChimahonReaderNumber(settings.characterSpacing, decimals = 2),
                    valueRange = 0f..0.5f,
                    steps = 9,
                    onValueChange = {
                        onSettingsChange(
                            settings.copy(characterSpacing = roundToStep(it, 0.05).coerceIn(0.0, 0.5)),
                        )
                    },
                )
                ReaderSliderRow(
                    label = "Paragraph spacing",
                    value = settings.paragraphSpacing.toFloat(),
                    valueLabel = formatChimahonReaderNumber(settings.paragraphSpacing, " em", decimals = 2),
                    valueRange = 0f..2f,
                    steps = 39,
                    onValueChange = {
                        onSettingsChange(
                            settings.copy(paragraphSpacing = roundToStep(it, 0.05).coerceIn(0.0, 2.0)),
                        )
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReaderThemeSwatchButton(
    label: String,
    backgroundColor: Int,
    textColor: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    splitBackgroundColor: Int? = null,
    onRenameClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
) {
    var showMenu by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier
            .width(84.dp)
            .height(66.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = if (onRenameClick != null || onDeleteClick != null) {
                    { showMenu = true }
                } else {
                    null
                },
            ),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) {
            MaterialTheme.colors.primary.copy(alpha = 0.12f)
        } else {
            MaterialTheme.colors.surface
        },
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) {
                MaterialTheme.colors.primary
            } else {
                MaterialTheme.colors.onSurface.copy(alpha = 0.14f)
            },
        ),
    ) {
        Box {
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
            ) {
                if (onRenameClick != null) {
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                            onRenameClick()
                        },
                    ) {
                        Text("Rename")
                    }
                }
                if (onDeleteClick != null) {
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                            onDeleteClick()
                        },
                    ) {
                        Text("Delete", color = MaterialTheme.colors.error)
                    }
                }
            }
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
                        .height(29.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(backgroundColor))
                        .border(
                            1.dp,
                            MaterialTheme.colors.onSurface.copy(alpha = 0.16f),
                            RoundedCornerShape(6.dp),
                        ),
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
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.Bold,
                        color = Color(textColor),
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.caption,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                )
            }
        }
    }
}

@Composable
private fun AddThemeButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .width(66.dp)
            .height(66.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.14f)),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add custom theme",
                tint = MaterialTheme.colors.primary,
            )
            Text("New", style = MaterialTheme.typography.caption)
        }
    }
}

@Composable
private fun CustomReaderThemeDialog(
    initialName: String,
    initialBackgroundColor: Int,
    initialTextColor: Int,
    title: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (ChimahonReaderCustomTheme) -> Unit,
) {
    var themeName by remember { mutableStateOf(initialName) }
    var backgroundInput by remember { mutableStateOf(chimahonReaderColorToHex(initialBackgroundColor)) }
    var textInput by remember { mutableStateOf(chimahonReaderColorToHex(initialTextColor)) }
    val parsedBackgroundColor = parseChimahonReaderColor(backgroundInput)
    val parsedTextColor = parseChimahonReaderColor(textInput)
    val previewBackgroundColor = parsedBackgroundColor ?: initialBackgroundColor
    val previewTextColor = parsedTextColor ?: initialTextColor
    val colorsValid = parsedBackgroundColor != null && parsedTextColor != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(previewBackgroundColor),
                    border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.14f)),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = themeName.ifBlank { "Custom" },
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Bold,
                            color = Color(previewTextColor),
                        )
                        Text(
                            text = "Sample reader text",
                            style = MaterialTheme.typography.body2,
                            color = Color(previewTextColor),
                        )
                    }
                }
                OutlinedTextField(
                    value = themeName,
                    onValueChange = { themeName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Name") },
                    placeholder = { Text("Theme name") },
                )
                ColorInputField(
                    label = "Background",
                    value = backgroundInput,
                    parsedColor = parsedBackgroundColor,
                    onValueChange = { backgroundInput = it },
                )
                ColorInputField(
                    label = "Text",
                    value = textInput,
                    parsedColor = parsedTextColor,
                    onValueChange = { textInput = it },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val backgroundColor = parsedBackgroundColor ?: return@TextButton
                    val textColor = parsedTextColor ?: return@TextButton
                    onConfirm(
                        ChimahonReaderCustomTheme(
                            name = themeName,
                            backgroundColor = backgroundColor,
                            textColor = textColor,
                        ),
                    )
                },
                enabled = colorsValid,
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun CustomReaderThemeRenameDialog(
    target: ChimahonReaderCustomTheme,
    onDismiss: () -> Unit,
    onConfirm: (ChimahonReaderCustomTheme) -> Unit,
) {
    var name by remember(target) { mutableStateOf(target.name) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename theme") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Theme name") },
                placeholder = { Text("Enter a name") },
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(target.copy(name = name)) },
                enabled = name.isNotBlank(),
            ) {
                Text("Rename")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun ColorInputField(
    label: String,
    value: String,
    parsedColor: Int?,
    onValueChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text(label) },
                placeholder = { Text("Hex or RGB") },
                isError = parsedColor == null,
            )
            ReaderSmallColorSwatch(parsedColor ?: 0x00000000)
        }
        if (parsedColor == null) {
            Text(
                text = "Enter a hex or RGB color",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.error,
            )
        } else {
            Text(
                text = chimahonReaderColorToHex(parsedColor),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
            )
        }
    }
}
