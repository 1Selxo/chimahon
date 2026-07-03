package app.chimahon.shared.reader.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonReaderChapterListSheet(
    items: List<ChimahonReaderChapterListItem>,
    onChapterSelected: (ChimahonReaderChapterListItem) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Chapters",
    dismissOnSelect: Boolean = true,
) {
    ReaderSheetDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        ChimahonReaderChapterListContent(
            items = items,
            onChapterSelected = { item ->
                onChapterSelected(item)
                if (dismissOnSelect) onDismissRequest()
            },
            title = title,
        )
    }
}

@Composable
fun ChimahonReaderChapterListContent(
    items: List<ChimahonReaderChapterListItem>,
    onChapterSelected: (ChimahonReaderChapterListItem) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Chapters",
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 680.dp)
            .padding(bottom = 14.dp),
    ) {
        ReaderSheetHeader(
            title = title,
            subtitle = if (items.isEmpty()) "No chapters" else "${items.size} item(s)",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
        )
        if (items.isEmpty()) {
            Text(
                text = "No chapters available",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(
                    items = items,
                    key = { it.key },
                ) { item ->
                    ReaderChapterListRow(
                        item = item,
                        onClick = { onChapterSelected(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ReaderChapterListRow(
    item: ChimahonReaderChapterListItem,
    onClick: () -> Unit,
) {
    val indent = (20 + item.depth.coerceIn(0, 6) * 16).dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = indent, end = 20.dp, top = 13.dp, bottom = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title.ifBlank { "Chapter ${item.index}" },
                style = MaterialTheme.typography.body1,
                fontWeight = if (item.selected) FontWeight.Bold else FontWeight.Normal,
                color = if (item.selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!item.subtitle.isNullOrBlank() || !item.fragment.isNullOrBlank()) {
                Text(
                    text = item.subtitle ?: item.fragment.orEmpty(),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.56f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.characterCount?.toString() ?: "...",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
            maxLines = 1,
        )
    }
}
