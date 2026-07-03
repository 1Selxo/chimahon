package app.chimahon.shared.mangaui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonMangaDetailLoadingState(
    modifier: Modifier = Modifier,
    message: String = "Loading manga",
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            CircularProgressIndicator()
            Text(
                text = message,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.72f),
                style = MaterialTheme.typography.body2,
            )
        }
    }
}

@Composable
fun ChimahonMangaDetailErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ChimahonMangaCenteredState(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colors.error,
                modifier = Modifier.size(34.dp),
            )
        },
        title = "Manga fetch failed",
        message = message,
        action = {
            Button(onClick = onRetry) {
                Text("Retry")
            }
        },
    )
}

@Composable
fun ChimahonMangaDetailEmptyState(
    modifier: Modifier = Modifier,
) {
    ChimahonMangaCenteredState(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Outlined.MenuBook,
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(34.dp),
            )
        },
        title = "No manga selected",
        message = "Choose a manga from Library or Browse.",
    )
}

@Composable
fun ChimahonMangaChapterEmptyState(
    hasFilters: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colors.primary.copy(alpha = 0.12f),
        ) {
            Icon(
                imageVector = Icons.Outlined.MenuBook,
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier
                    .padding(18.dp)
                    .size(32.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (hasFilters) "No chapters match filters" else "No chapters found",
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Text(
            modifier = Modifier.padding(top = 6.dp),
            text = if (hasFilters) {
                "Change filters or sorting to show hidden chapters."
            } else {
                "Pull refresh or open the source page to fetch chapters."
            },
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.62f),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ChimahonMangaCenteredState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    action: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colors.primary.copy(alpha = 0.12f),
            ) {
                Box(
                    modifier = Modifier.padding(18.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    icon()
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = title,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = message,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.62f),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
            )
            if (action != null) {
                Spacer(Modifier.height(18.dp))
                action()
            }
        }
    }
}
