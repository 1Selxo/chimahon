package app.chimahon.shared.animequeueui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonAnimeQueueStateHost(
    loading: Boolean,
    errorMessage: String?,
    empty: Boolean,
    surfaceKind: ChimahonAnimeQueueSurfaceKind,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    emptyTitle: String = defaultAnimeQueueEmptyTitle(surfaceKind),
    emptySubtitle: String = defaultAnimeQueueEmptySubtitle(surfaceKind),
    loadingMessage: String = defaultAnimeQueueLoadingTitle(surfaceKind),
    contentPadding: PaddingValues = PaddingValues(24.dp),
    emptyActionLabel: String? = null,
    onEmptyActionClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    when {
        loading -> ChimahonAnimeQueueLoadingState(
            modifier = modifier,
            title = loadingMessage,
            surfaceKind = surfaceKind,
            contentPadding = contentPadding,
        )
        errorMessage != null -> ChimahonAnimeQueueErrorState(
            modifier = modifier,
            message = errorMessage,
            onRetry = onRetry,
            surfaceKind = surfaceKind,
            contentPadding = contentPadding,
        )
        empty -> ChimahonAnimeQueueEmptyState(
            modifier = modifier,
            title = emptyTitle,
            subtitle = emptySubtitle,
            surfaceKind = surfaceKind,
            actionLabel = emptyActionLabel,
            onActionClick = onEmptyActionClick,
            contentPadding = contentPadding,
        )
        else -> content()
    }
}

@Composable
fun ChimahonAnimeQueueLoadingState(
    surfaceKind: ChimahonAnimeQueueSurfaceKind,
    modifier: Modifier = Modifier,
    title: String = defaultAnimeQueueLoadingTitle(surfaceKind),
    subtitle: String = "Syncing queue information",
    contentPadding: PaddingValues = PaddingValues(24.dp),
) {
    ChimahonAnimeQueueCenteredState(
        modifier = modifier,
        icon = surfaceKind.defaultIcon,
        title = title,
        subtitle = subtitle,
        contentPadding = contentPadding,
    ) {
        CircularProgressIndicator(modifier = Modifier.size(36.dp), strokeWidth = 3.dp)
    }
}

@Composable
fun ChimahonAnimeQueueEmptyState(
    surfaceKind: ChimahonAnimeQueueSurfaceKind,
    modifier: Modifier = Modifier,
    title: String = defaultAnimeQueueEmptyTitle(surfaceKind),
    subtitle: String = defaultAnimeQueueEmptySubtitle(surfaceKind),
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(24.dp),
) {
    ChimahonAnimeQueueCenteredState(
        modifier = modifier,
        icon = surfaceKind.defaultIcon,
        title = title,
        subtitle = subtitle,
        contentPadding = contentPadding,
    ) {
        if (actionLabel != null && onActionClick != null) {
            Button(onClick = onActionClick) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
fun ChimahonAnimeQueueErrorState(
    message: String,
    onRetry: () -> Unit,
    surfaceKind: ChimahonAnimeQueueSurfaceKind,
    modifier: Modifier = Modifier,
    title: String = defaultAnimeQueueErrorTitle(surfaceKind),
    contentPadding: PaddingValues = PaddingValues(24.dp),
) {
    ChimahonAnimeQueueCenteredState(
        modifier = modifier,
        icon = ChimahonAnimeQueueActionIcon.Error.imageVector,
        title = title,
        subtitle = message,
        contentPadding = contentPadding,
    ) {
        TextButton(onClick = onRetry) {
            Icon(
                imageVector = ChimahonAnimeQueueActionIcon.Retry.imageVector,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp),
            )
            Text("Retry")
        }
    }
}

@Composable
private fun ChimahonAnimeQueueCenteredState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    contentPadding: PaddingValues = PaddingValues(24.dp),
    action: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (icon != null) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colors.primary.copy(alpha = 0.14f),
                    contentColor = MaterialTheme.colors.primary,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(18.dp)
                            .size(36.dp),
                    )
                }
            }
            Text(
                text = title,
                color = MaterialTheme.colors.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            action?.invoke()
        }
    }
}

private fun defaultAnimeQueueEmptyTitle(surfaceKind: ChimahonAnimeQueueSurfaceKind): String {
    return when (surfaceKind) {
        ChimahonAnimeQueueSurfaceKind.WatchQueue -> "Watch queue is empty"
        ChimahonAnimeQueueSurfaceKind.DownloadQueue -> "Download queue is empty"
        ChimahonAnimeQueueSurfaceKind.AutoNext -> "No upcoming episodes"
    }
}

private fun defaultAnimeQueueEmptySubtitle(surfaceKind: ChimahonAnimeQueueSurfaceKind): String {
    return when (surfaceKind) {
        ChimahonAnimeQueueSurfaceKind.WatchQueue -> "Queued episodes will appear here before playback."
        ChimahonAnimeQueueSurfaceKind.DownloadQueue -> "Episode downloads will appear here with priority and reorder controls."
        ChimahonAnimeQueueSurfaceKind.AutoNext -> "Auto-next will list upcoming episodes when a playlist is available."
    }
}

private fun defaultAnimeQueueLoadingTitle(surfaceKind: ChimahonAnimeQueueSurfaceKind): String {
    return when (surfaceKind) {
        ChimahonAnimeQueueSurfaceKind.WatchQueue -> "Loading watch queue"
        ChimahonAnimeQueueSurfaceKind.DownloadQueue -> "Loading downloads"
        ChimahonAnimeQueueSurfaceKind.AutoNext -> "Loading playlist"
    }
}

private fun defaultAnimeQueueErrorTitle(surfaceKind: ChimahonAnimeQueueSurfaceKind): String {
    return when (surfaceKind) {
        ChimahonAnimeQueueSurfaceKind.WatchQueue -> "Watch queue failed to load"
        ChimahonAnimeQueueSurfaceKind.DownloadQueue -> "Download queue failed to load"
        ChimahonAnimeQueueSurfaceKind.AutoNext -> "Playlist failed to load"
    }
}
