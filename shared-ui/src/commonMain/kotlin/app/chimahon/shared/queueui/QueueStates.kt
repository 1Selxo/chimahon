package app.chimahon.shared.queueui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
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
fun ChimahonQueueStateHost(
    loading: Boolean,
    errorMessage: String?,
    empty: Boolean,
    surfaceKind: ChimahonQueueSurfaceKind,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    emptyTitle: String = defaultQueueEmptyTitle(surfaceKind),
    emptySubtitle: String = defaultQueueEmptySubtitle(surfaceKind),
    loadingMessage: String = defaultQueueLoadingTitle(surfaceKind),
    contentPadding: PaddingValues = PaddingValues(24.dp),
    emptyActionLabel: String? = null,
    onEmptyActionClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    when {
        loading -> ChimahonQueueLoadingState(
            modifier = modifier,
            title = loadingMessage,
            surfaceKind = surfaceKind,
            contentPadding = contentPadding,
        )
        errorMessage != null -> ChimahonQueueErrorState(
            modifier = modifier,
            message = errorMessage,
            onRetry = onRetry,
            surfaceKind = surfaceKind,
            contentPadding = contentPadding,
        )
        empty -> ChimahonQueueEmptyState(
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
fun ChimahonQueueLoadingState(
    surfaceKind: ChimahonQueueSurfaceKind,
    modifier: Modifier = Modifier,
    title: String = defaultQueueLoadingTitle(surfaceKind),
    subtitle: String = "Syncing queue information",
    contentPadding: PaddingValues = PaddingValues(24.dp),
) {
    ChimahonQueueCenteredState(
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
fun ChimahonQueueEmptyState(
    surfaceKind: ChimahonQueueSurfaceKind,
    modifier: Modifier = Modifier,
    title: String = defaultQueueEmptyTitle(surfaceKind),
    subtitle: String = defaultQueueEmptySubtitle(surfaceKind),
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(24.dp),
) {
    ChimahonQueueCenteredState(
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
fun ChimahonQueueErrorState(
    message: String,
    onRetry: () -> Unit,
    surfaceKind: ChimahonQueueSurfaceKind,
    modifier: Modifier = Modifier,
    title: String = "${surfaceKind.name} failed to load",
    contentPadding: PaddingValues = PaddingValues(24.dp),
) {
    ChimahonQueueCenteredState(
        modifier = modifier,
        icon = Icons.Outlined.Info,
        title = title,
        subtitle = message,
        contentPadding = contentPadding,
    ) {
        TextButton(onClick = onRetry) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp),
            )
            Text("Retry")
        }
    }
}

@Composable
private fun ChimahonQueueCenteredState(
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

private fun defaultQueueEmptyTitle(surfaceKind: ChimahonQueueSurfaceKind): String {
    return when (surfaceKind) {
        ChimahonQueueSurfaceKind.Updates -> "No recent updates"
        ChimahonQueueSurfaceKind.History -> "No reading history"
        ChimahonQueueSurfaceKind.Downloads -> "Download queue is empty"
    }
}

private fun defaultQueueEmptySubtitle(surfaceKind: ChimahonQueueSurfaceKind): String {
    return when (surfaceKind) {
        ChimahonQueueSurfaceKind.Updates -> "New chapters will appear here after library updates."
        ChimahonQueueSurfaceKind.History -> "Chapters you read will be listed here."
        ChimahonQueueSurfaceKind.Downloads -> "Download chapters for offline reading to see them here."
    }
}

private fun defaultQueueLoadingTitle(surfaceKind: ChimahonQueueSurfaceKind): String {
    return when (surfaceKind) {
        ChimahonQueueSurfaceKind.Updates -> "Loading updates"
        ChimahonQueueSurfaceKind.History -> "Loading history"
        ChimahonQueueSurfaceKind.Downloads -> "Loading downloads"
    }
}
