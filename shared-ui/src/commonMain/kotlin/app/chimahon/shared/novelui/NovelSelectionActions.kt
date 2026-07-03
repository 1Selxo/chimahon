package app.chimahon.shared.novelui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonNovelTextSelectionActionRow(
    state: ChimahonNovelSelectionUiState,
    onAction: (ChimahonNovelSelectionAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        elevation = 10.dp,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.wordLabel,
                        color = MaterialTheme.colors.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val subtitle = listOfNotNull(
                        state.language?.uppercase()?.takeIf(String::isNotBlank),
                        state.dictionaryProfileName?.takeIf(String::isNotBlank),
                    ).joinToString(" - ")
                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                if (state.lookupInProgress) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.primary,
                        strokeWidth = 2.dp,
                    )
                }
                TextButton(onClick = { onAction(ChimahonNovelSelectionAction.Dismiss) }) {
                    Text("Close")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ChimahonNovelSelectionButton(
                    text = if (state.lookupInProgress) "Looking up" else "Lookup",
                    enabled = state.selectedText.isNotBlank() && !state.lookupInProgress,
                    primary = true,
                    onClick = { onAction(ChimahonNovelSelectionAction.Lookup) },
                )
                ChimahonNovelSelectionButton(
                    text = "Copy",
                    enabled = state.selectedText.isNotBlank(),
                    onClick = { onAction(ChimahonNovelSelectionAction.Copy) },
                )
                ChimahonNovelSelectionButton(
                    text = "Search web",
                    enabled = state.selectedText.isNotBlank(),
                    onClick = { onAction(ChimahonNovelSelectionAction.SearchWeb) },
                )
                ChimahonNovelSelectionButton(
                    text = "Anki",
                    enabled = state.ankiEnabled && state.selectedText.isNotBlank(),
                    onClick = { onAction(ChimahonNovelSelectionAction.AddToAnki) },
                )
                ChimahonNovelSelectionButton(
                    text = "Translate",
                    enabled = state.selectedText.isNotBlank(),
                    onClick = { onAction(ChimahonNovelSelectionAction.Translate) },
                )
            }
        }
    }
}

@Composable
fun ChimahonNovelLookupPreviewCard(
    state: ChimahonNovelSelectionUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.05f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = state.selectedText,
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.SemiBold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        if (state.sentence.isNotBlank() && state.sentence != state.selectedText) {
            Text(
                text = state.sentence,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.66f),
                fontSize = 13.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ChimahonNovelSelectionButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
) {
    val content = if (primary) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = content.copy(alpha = if (primary) 0.16f else 0.08f),
            contentColor = if (primary) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
            disabledBackgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.04f),
            disabledContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.30f),
        ),
        elevation = null,
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            maxLines = 1,
        )
    }
}
