package app.chimahon.shared.readerocrui

import app.chimahon.shared.lookup.ChimahonLookupDefinition
import app.chimahon.shared.lookup.ChimahonLookupResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonReaderOcrLookupCard(
    state: ChimahonReaderOcrLookupCardState,
    actions: ChimahonReaderOcrUiActions,
    modifier: Modifier = Modifier,
    colors: ChimahonReaderOcrColors = readerOcrColors(),
    maxResults: Int = 6,
) {
    if (!state.visible) return
    Surface(
        modifier = modifier,
        color = colors.chrome,
        contentColor = colors.onChrome,
        shape = RoundedCornerShape(12.dp),
        elevation = 12.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 172.dp, max = 420.dp)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ChimahonReaderOcrLookupHeader(
                state = state,
                actions = actions,
                colors = colors,
            )
            ChimahonReaderOcrLookupTextField(
                value = state.lookupText,
                onValueChange = actions.onLookupTextChange,
                colors = colors,
            )
            when {
                state.loading -> ChimahonReaderOcrLookupLoading(colors)
                state.errorMessage != null -> ChimahonReaderOcrLookupMessage(
                    title = "Lookup failed",
                    message = state.errorMessage,
                    colors = colors,
                )
                state.results.isEmpty() -> ChimahonReaderOcrLookupMessage(
                    title = "No dictionary results",
                    message = "Try a shorter text selection or rescan OCR.",
                    colors = colors,
                )
                else -> ChimahonReaderOcrResultList(
                    results = state.results.take(maxResults),
                    selectedResult = state.selectedResult,
                    colors = colors,
                    onResultSelected = actions.onLookupResultSelected,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
        }
    }
}

@Composable
fun ChimahonReaderOcrResultList(
    results: List<ChimahonLookupResult>,
    selectedResult: ChimahonLookupResult?,
    onResultSelected: (ChimahonLookupResult) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonReaderOcrColors = readerOcrColors(),
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(results, key = { it.id }) { result ->
            ChimahonReaderOcrResultRow(
                result = result,
                selected = result.id == selectedResult?.id,
                colors = colors,
                onClick = { onResultSelected(result) },
            )
        }
    }
}

@Composable
private fun ChimahonReaderOcrLookupHeader(
    state: ChimahonReaderOcrLookupCardState,
    actions: ChimahonReaderOcrUiActions,
    colors: ChimahonReaderOcrColors,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "OCR lookup",
                color = colors.onChrome,
                style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (state.boxCount > 0) {
                    "Box ${state.selectedBoxIndex + 1} / ${state.boxCount}"
                } else {
                    "Tap text on the page"
                },
                color = colors.secondaryText,
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        ChimahonReaderOcrMiniAction(
            action = ChimahonReaderOcrAction.PreviousMatch,
            enabled = state.selectedBoxIndex > 0,
            colors = colors,
            onClick = { actions.run(ChimahonReaderOcrAction.PreviousMatch) },
        )
        ChimahonReaderOcrMiniAction(
            action = ChimahonReaderOcrAction.NextMatch,
            enabled = state.selectedBoxIndex + 1 < state.boxCount,
            colors = colors,
            onClick = { actions.run(ChimahonReaderOcrAction.NextMatch) },
        )
        ChimahonReaderOcrMiniAction(
            action = ChimahonReaderOcrAction.CopyText,
            enabled = state.lookupText.isNotBlank(),
            colors = colors,
            onClick = { actions.run(ChimahonReaderOcrAction.CopyText) },
        )
        ChimahonReaderOcrMiniAction(
            action = ChimahonReaderOcrAction.SearchWeb,
            enabled = state.lookupText.isNotBlank(),
            colors = colors,
            onClick = { actions.run(ChimahonReaderOcrAction.SearchWeb) },
        )
        ChimahonReaderOcrMiniAction(
            action = ChimahonReaderOcrAction.Close,
            enabled = true,
            colors = colors,
            onClick = actions.onDismissLookup,
        )
    }
}

@Composable
private fun ChimahonReaderOcrLookupTextField(
    value: String,
    onValueChange: (String) -> Unit,
    colors: ChimahonReaderOcrColors,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(colors.accent),
        textStyle = TextStyle(
            color = colors.onChrome,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.control)
            .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .semantics { contentDescription = "OCR lookup text" },
        decorationBox = { innerTextField ->
            Box {
                if (value.isBlank()) {
                    Text(
                        text = "Lookup text",
                        color = colors.secondaryText,
                        style = MaterialTheme.typography.body2,
                    )
                }
                innerTextField()
            }
        },
    )
}

@Composable
private fun ChimahonReaderOcrResultRow(
    result: ChimahonLookupResult,
    selected: Boolean,
    colors: ChimahonReaderOcrColors,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) colors.selectedControl else colors.control)
            .border(
                width = 1.dp,
                color = if (selected) colors.accent.copy(alpha = 0.62f) else colors.divider,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = result.term.expression,
                color = colors.onChrome,
                style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (result.term.reading.isNotBlank()) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = result.term.reading,
                    color = colors.secondaryText,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        result.tags.take(4).takeIf { it.isNotEmpty() }?.let { tags ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                tags.forEach { tag ->
                    ChimahonReaderOcrSmallPill(
                        text = tag,
                        colors = colors,
                    )
                }
            }
        }
        result.definitions.take(3).forEachIndexed { index, definition ->
            if (index > 0) {
                Divider(color = colors.divider)
            }
            ChimahonReaderOcrDefinitionLine(
                definition = definition,
                colors = colors,
            )
        }
    }
}

@Composable
private fun ChimahonReaderOcrDefinitionLine(
    definition: ChimahonLookupDefinition,
    colors: ChimahonReaderOcrColors,
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        if (definition.dictionary.isNotBlank()) {
            Text(
                text = definition.dictionary,
                color = colors.secondaryText,
                style = MaterialTheme.typography.overline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = definition.text,
            color = colors.onChrome,
            style = MaterialTheme.typography.body2,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ChimahonReaderOcrSmallPill(
    text: String,
    colors: ChimahonReaderOcrColors,
) {
    Text(
        text = text,
        color = colors.secondaryText,
        style = MaterialTheme.typography.overline.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.chrome)
            .border(1.dp, colors.divider, RoundedCornerShape(10.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp),
    )
}

@Composable
private fun ChimahonReaderOcrLookupLoading(colors: ChimahonReaderOcrColors) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = colors.accent,
            strokeWidth = 2.dp,
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = "Looking up...",
            color = colors.secondaryText,
            style = MaterialTheme.typography.body2,
        )
    }
}

@Composable
private fun ChimahonReaderOcrLookupMessage(
    title: String,
    message: String,
    colors: ChimahonReaderOcrColors,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = title,
            color = colors.onChrome,
            style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text = message,
            color = colors.secondaryText,
            style = MaterialTheme.typography.body2,
        )
    }
}

@Composable
private fun ChimahonReaderOcrMiniAction(
    action: ChimahonReaderOcrAction,
    enabled: Boolean,
    colors: ChimahonReaderOcrColors,
    onClick: () -> Unit,
) {
    IconButton(
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier.size(36.dp),
    ) {
        Icon(
            imageVector = action.imageVector,
            contentDescription = action.contentDescription,
            tint = if (enabled) colors.onChrome else colors.secondaryText.copy(alpha = 0.44f),
            modifier = Modifier.size(20.dp),
        )
    }
}
