package app.chimahon.shared.reader.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonReaderStatisticsSheet(
    state: ChimahonReaderStatisticsState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleTracking: (() -> Unit)? = null,
) {
    ReaderSheetDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        ChimahonReaderStatisticsContent(
            state = state,
            onToggleTracking = onToggleTracking,
        )
    }
}

@Composable
fun ChimahonReaderStatisticsContent(
    state: ChimahonReaderStatisticsState,
    modifier: Modifier = Modifier,
    onToggleTracking: (() -> Unit)? = null,
) {
    ReaderSheetScaffold(
        title = "Statistics",
        subtitle = if (state.isTracking) "Timer running" else "Timer paused",
        modifier = modifier,
        trailing = if (onToggleTracking != null) {
            {
                IconButton(onClick = onToggleTracking) {
                    Icon(
                        imageVector = if (state.isTracking) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (state.isTracking) "Pause timer" else "Resume timer",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        } else {
            null
        },
    ) {
        val projectionCharacter = state.projectionCharacter
        val session = state.session
        ReaderStatsSection(title = "Session") {
            ReaderStatRow("Characters read", session.charactersRead.toString())
            ReaderStatRow("Reading speed", "${session.lastReadingSpeed} / h")
            ReaderStatRow("Reading time", formatChimahonReaderDuration(session.readingTimeSeconds.toLong()))
            ReaderStatRow(
                "Time to finish book",
                formatChimahonReaderEta(
                    chimahonReaderSecondsRemaining(
                        remainingCharacters = state.totalCharacters - projectionCharacter,
                        speed = session.lastReadingSpeed,
                    ),
                ),
            )
            ReaderStatRow(
                "Time to finish chapter",
                formatChimahonReaderEta(
                    chimahonReaderSecondsRemaining(
                        remainingCharacters = state.currentChapterEndCharacter - projectionCharacter,
                        speed = session.lastReadingSpeed,
                    ),
                ),
            )
        }

        ReaderStatsSection(title = "Today") {
            ReaderStatRow("Characters read", state.today.charactersRead.toString())
            ReaderStatRow("Reading speed", "${state.today.lastReadingSpeed} / h")
            ReaderStatRow("Reading time", formatChimahonReaderDuration(state.today.readingTimeSeconds.toLong()))
        }

        ReaderStatsSection(title = "All time") {
            ReaderStatRow("Characters read", state.allTime.charactersRead.toString())
            ReaderStatRow("Reading speed", "${state.allTime.lastReadingSpeed} / h")
            ReaderStatRow("Reading time", formatChimahonReaderDuration(state.allTime.readingTimeSeconds.toLong()))
        }
    }
}

@Composable
private fun ReaderStatsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.Bold,
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            content()
        }
    }
}

@Composable
private fun ReaderStatRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.64f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}
