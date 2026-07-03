package app.chimahon.shared.library

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
import androidx.compose.material.icons.outlined.CollectionsBookmark
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
fun ChimahonLibraryStateHost(
    state: ChimahonLibraryUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onBrowseClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(24.dp),
    content: @Composable () -> Unit,
) {
    when {
        state.loading -> ChimahonLibraryLoadingState(modifier = modifier, contentPadding = contentPadding)
        state.errorMessage != null -> ChimahonLibraryErrorState(
            modifier = modifier,
            message = state.errorMessage,
            onRetry = onRetry,
            contentPadding = contentPadding,
        )
        state.entries.isEmpty() -> ChimahonLibraryEmptyState(
            modifier = modifier,
            onBrowseClick = onBrowseClick,
            contentPadding = contentPadding,
        )
        else -> content()
    }
}

@Composable
fun ChimahonLibraryLoadingState(
    modifier: Modifier = Modifier,
    message: String = "Loading library",
    contentPadding: PaddingValues = PaddingValues(24.dp),
) {
    ChimahonLibraryCenteredState(
        modifier = modifier,
        icon = null,
        title = message,
        subtitle = "Fetching entries and categories",
        contentPadding = contentPadding,
    ) {
        CircularProgressIndicator(modifier = Modifier.size(36.dp), strokeWidth = 3.dp)
    }
}

@Composable
fun ChimahonLibraryEmptyState(
    modifier: Modifier = Modifier,
    title: String = "Your library is empty",
    subtitle: String = "Add manga from Browse to see it here.",
    onBrowseClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(24.dp),
) {
    ChimahonLibraryCenteredState(
        modifier = modifier,
        icon = Icons.Outlined.CollectionsBookmark,
        title = title,
        subtitle = subtitle,
        contentPadding = contentPadding,
    ) {
        onBrowseClick?.let {
            Button(onClick = it) {
                Text("Browse")
            }
        }
    }
}

@Composable
fun ChimahonLibraryErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Library failed to load",
    contentPadding: PaddingValues = PaddingValues(24.dp),
) {
    ChimahonLibraryCenteredState(
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
private fun ChimahonLibraryCenteredState(
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
