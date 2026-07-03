package app.chimahon.shared.trackingui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonTrackingScreen(
    state: ChimahonTrackingUiState,
    callbacks: ChimahonTrackingCallbacks,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(key = "tracking-header") {
            ChimahonTrackingScreenHeader(state)
        }
        item(key = "tracking-accounts") {
            ChimahonTrackerAccountSection(
                accounts = state.accounts,
                callbacks = callbacks,
            )
        }
        item(key = "tracking-media-summary") {
            ChimahonTrackingMediaSummarySection(
                summaries = state.mediaSummaries(),
                selectedMediaKind = state.selectedMediaKind,
                onMediaKindSelected = callbacks.onMediaKindSelected,
            )
        }
        item(key = "tracking-entry-header") {
            ChimahonTrackingEntryHeader(
                state = state,
                callbacks = callbacks,
            )
        }
        when {
            state.loading -> item(key = "tracking-loading") {
                ChimahonTrackingLoadingState(message = "Loading tracking...")
            }
            state.errorMessage != null -> item(key = "tracking-error") {
                ChimahonTrackingErrorState(
                    title = "Tracking failed",
                    message = state.errorMessage,
                    onRetry = callbacks.onRetry,
                )
            }
            state.filteredEntries.isEmpty() -> item(key = "tracking-empty") {
                ChimahonTrackingEmptyState(
                    title = "No tracked entries",
                    message = "Connect a tracker or search from a manga, anime, or light novel detail page.",
                )
            }
            else -> items(
                items = state.filteredEntries,
                key = { it.stableLazyKey },
            ) { entry ->
                ChimahonTrackingEntryCard(
                    entry = entry,
                    callbacks = callbacks,
                )
            }
        }
    }
}

@Composable
fun ChimahonTrackingScreenHeader(
    state: ChimahonTrackingUiState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = state.title,
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        val subtitle = state.subtitle
            ?: "${state.loggedInAccounts} connected, ${state.entries.size} tracked"
        Text(
            text = subtitle,
            modifier = Modifier.padding(top = 2.dp),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.62f),
        )
        if (state.syncing) {
            Row(
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(2.dp),
                    strokeWidth = 2.dp,
                )
                Text(
                    text = "Syncing trackers",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.primary,
                )
            }
        }
    }
}

@Composable
fun ChimahonTrackingSectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onBackground,
            fontWeight = FontWeight.SemiBold,
        )
        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.58f),
            )
        }
    }
}

@Composable
fun ChimahonTrackingFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (selected) {
        MaterialTheme.colors.primary.copy(alpha = 0.14f)
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.06f)
    }
    val content = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
    Text(
        text = text,
        modifier = modifier
            .clickable(onClick = onClick)
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        style = MaterialTheme.typography.caption,
        color = content,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
    )
}

@Composable
fun ChimahonTrackingStatusChip(
    text: String,
    tone: ChimahonTrackingTone,
    modifier: Modifier = Modifier,
) {
    val colors = tone.colors()
    Text(
        text = text,
        modifier = modifier
            .background(colors.background, RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        style = MaterialTheme.typography.caption,
        color = colors.content,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun ChimahonTrackingLoadingState(
    message: String,
    modifier: Modifier = Modifier,
) {
    ChimahonTrackingStateSurface(modifier = modifier) {
        CircularProgressIndicator(strokeWidth = 2.dp)
        Text(
            text = message,
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f),
        )
    }
}

@Composable
fun ChimahonTrackingEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    ChimahonTrackingStateSurface(modifier = modifier) {
        Icon(
            imageVector = ChimahonTrackingIcon.Sync.imageVector(),
            contentDescription = null,
            tint = MaterialTheme.colors.primary,
        )
        Text(
            text = title,
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = message,
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.64f),
        )
    }
}

@Composable
fun ChimahonTrackingInlineEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8.dp),
        elevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle2,
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = message,
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.64f),
            )
        }
    }
}

@Composable
fun ChimahonTrackingErrorState(
    title: String,
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ChimahonTrackingStateSurface(modifier = modifier) {
        Icon(
            imageVector = ChimahonTrackingIcon.Error.imageVector(),
            contentDescription = null,
            tint = MaterialTheme.colors.error,
        )
        Text(
            text = title,
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = message,
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.64f),
        )
        TextButton(
            onClick = onRetry,
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Text("Retry")
        }
    }
}

@Composable
private fun ChimahonTrackingStateSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8.dp),
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}

enum class ChimahonTrackingTone {
    Info,
    Success,
    Warning,
    Error,
    Muted,
}

data class ChimahonTrackingToneColors(
    val background: Color,
    val content: Color,
)

@Composable
fun ChimahonTrackingTone.colors(): ChimahonTrackingToneColors {
    return when (this) {
        ChimahonTrackingTone.Info -> ChimahonTrackingToneColors(
            background = MaterialTheme.colors.primary.copy(alpha = 0.12f),
            content = MaterialTheme.colors.primary,
        )
        ChimahonTrackingTone.Success -> ChimahonTrackingToneColors(
            background = Color(0xFF2E7D32).copy(alpha = 0.14f),
            content = Color(0xFF2E7D32),
        )
        ChimahonTrackingTone.Warning -> ChimahonTrackingToneColors(
            background = Color(0xFFB26A00).copy(alpha = 0.14f),
            content = Color(0xFFB26A00),
        )
        ChimahonTrackingTone.Error -> ChimahonTrackingToneColors(
            background = MaterialTheme.colors.error.copy(alpha = 0.14f),
            content = MaterialTheme.colors.error,
        )
        ChimahonTrackingTone.Muted -> ChimahonTrackingToneColors(
            background = MaterialTheme.colors.onSurface.copy(alpha = 0.07f),
            content = MaterialTheme.colors.onSurface.copy(alpha = 0.64f),
        )
    }
}

fun ChimahonTrackerSyncStatus.statusTone(): ChimahonTrackingTone {
    return when (this) {
        ChimahonTrackerSyncStatus.Idle -> ChimahonTrackingTone.Muted
        ChimahonTrackerSyncStatus.Pending -> ChimahonTrackingTone.Info
        ChimahonTrackerSyncStatus.Syncing -> ChimahonTrackingTone.Info
        ChimahonTrackerSyncStatus.Synced -> ChimahonTrackingTone.Success
        ChimahonTrackerSyncStatus.Failed -> ChimahonTrackingTone.Error
        ChimahonTrackerSyncStatus.Disabled -> ChimahonTrackingTone.Muted
    }
}

fun ChimahonTrackerAccountStatus.statusTone(): ChimahonTrackingTone {
    return when (this) {
        ChimahonTrackerAccountStatus.LoggedOut -> ChimahonTrackingTone.Muted
        ChimahonTrackerAccountStatus.LoggedIn -> ChimahonTrackingTone.Success
        ChimahonTrackerAccountStatus.Expired -> ChimahonTrackingTone.Warning
        ChimahonTrackerAccountStatus.Syncing -> ChimahonTrackingTone.Info
        ChimahonTrackerAccountStatus.Error -> ChimahonTrackingTone.Error
    }
}
