package app.chimahon.shared.novelui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonNovelLoadingState(
    message: String = "Loading novels...",
    modifier: Modifier = Modifier,
) {
    ChimahonNovelCenteredState(
        modifier = modifier,
        badge = {
            CircularProgressIndicator(
                modifier = Modifier.size(34.dp),
                color = MaterialTheme.colors.primary,
                strokeWidth = 3.dp,
            )
        },
        title = message,
        body = "Preparing the local novel library.",
    )
}

@Composable
fun ChimahonNovelEmptyLibraryState(
    onImportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ChimahonNovelCenteredState(
        modifier = modifier,
        badge = { ChimahonNovelStateTile(text = "EPUB") },
        title = "No novels in library",
        body = "Import an EPUB to start reading.",
        actionLabel = "Import EPUB",
        onActionClick = onImportClick,
    )
}

@Composable
fun ChimahonNovelFilteredEmptyState(
    modifier: Modifier = Modifier,
) {
    ChimahonNovelCenteredState(
        modifier = modifier,
        badge = { ChimahonNovelStateTile(text = "?") },
        title = "No matching novels",
        body = "Try another category, search, or display filter.",
    )
}

@Composable
fun ChimahonNovelErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Novels failed to load",
) {
    ChimahonNovelCenteredState(
        modifier = modifier,
        badge = { ChimahonNovelStateTile(text = "!") },
        title = title,
        body = message,
        actionLabel = "Retry",
        onActionClick = onRetry,
    )
}

@Composable
fun ChimahonNovelChapterEmptyState(
    hasFilters: Boolean,
    modifier: Modifier = Modifier,
) {
    ChimahonNovelCenteredState(
        modifier = modifier.padding(vertical = 36.dp),
        badge = { ChimahonNovelStateTile(text = if (hasFilters) "?" else "TOC") },
        title = if (hasFilters) "No chapters match" else "No chapters found",
        body = if (hasFilters) {
            "Clear the active chapter filters."
        } else {
            "This EPUB has no readable table of contents yet."
        },
    )
}

@Composable
fun ChimahonNovelReaderMessageState(
    title: String,
    body: String? = null,
    loading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    ChimahonNovelCenteredState(
        modifier = modifier,
        badge = {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(34.dp),
                    color = MaterialTheme.colors.primary,
                    strokeWidth = 3.dp,
                )
            } else {
                ChimahonNovelStateTile(text = "!")
            }
        },
        title = title,
        body = body,
    )
}

@Composable
private fun ChimahonNovelCenteredState(
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    badge: @Composable () -> Unit,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            badge()
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = title,
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.sp,
                textAlign = TextAlign.Center,
            )
            if (!body.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = body,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
            }
            if (actionLabel != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onActionClick,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary,
                    ),
                ) {
                    Text(text = actionLabel)
                }
            }
        }
    }
}

@Composable
fun ChimahonNovelStateTile(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colors.primary.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.Bold,
            fontSize = if (text.length > 3) 13.sp else 22.sp,
            maxLines = 1,
        )
    }
}

@Composable
fun ChimahonNovelCoverPlaceholder(
    title: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.primary.copy(alpha = 0.16f),
) {
    val initials = title
        .split(' ', '-', '_')
        .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
        .take(2)
        .joinToString("")
        .ifBlank { "N" }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            maxLines = 1,
        )
    }
}
