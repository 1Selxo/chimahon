package app.chimahon.shared.playerui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonPlayerChrome(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
    videoContent: @Composable BoxScope.() -> Unit = {
        ChimahonPlayerVideoPlaceholder(state = state, actions = actions)
    },
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(role = Role.Button, onClick = actions.onToggleControls),
    ) {
        videoContent()
        if (state.errorMessage != null) {
            ChimahonPlayerErrorState(
                message = state.errorMessage,
                onRetry = actions.onRetry,
                modifier = Modifier.align(Alignment.Center),
            )
        } else if (state.loading) {
            ChimahonPlayerLoadingState(
                title = "Loading video",
                detail = state.episodeTitle ?: state.title,
                modifier = Modifier.align(Alignment.Center),
            )
        } else if (state.durationSeconds <= 0L && state.currentEpisodeId == null) {
            ChimahonPlayerEmptyState(
                modifier = Modifier.align(Alignment.Center),
            )
        }
        if (state.controlsVisible) {
            ChimahonPlayerTopBar(
                state = state,
                actions = actions,
                modifier = Modifier.align(Alignment.TopCenter),
            )
            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                ChimahonPlayerChapterStrip(state = state, actions = actions)
                ChimahonPlayerEpisodeStrip(state = state, actions = actions)
                ChimahonPlayerBottomControls(state = state, actions = actions)
            }
        }
        ChimahonPlayerAndroidParityOverlays(
            state = state,
            actions = actions,
            modifier = Modifier.matchParentSize(),
        )
        state.message?.let { message ->
            ChimahonPlayerTransientMessageChip(
                message = message,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 96.dp),
            )
        }
        ChimahonPlayerOverlaySheet(
            state = state,
            actions = actions,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun ChimahonPlayerVideoPlaceholder(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.14f))
                    .clickable(role = Role.Button, onClick = actions.onTogglePlayback),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (state.paused) {
                        ChimahonPlayerIcon.Play.imageVector
                    } else {
                        ChimahonPlayerIcon.Pause.imageVector
                    },
                    contentDescription = if (state.paused) "Play" else "Pause",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp),
                )
            }
            Text(
                text = state.title,
                color = Color.White,
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.SemiBold),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp),
            )
            state.episodeTitle?.let {
                Text(
                    text = it,
                    color = Color.White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp),
                )
            }
        }
    }
}

@Composable
fun ChimahonPlayerLoadingState(
    title: String,
    modifier: Modifier = Modifier,
    detail: String? = null,
) {
    Column(
        modifier = modifier
            .clip(RoundedStateShape)
            .background(Color.Black.copy(alpha = 0.62f))
            .padding(horizontal = 28.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircularProgressIndicator(color = Color.White)
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
        )
        if (!detail.isNullOrBlank()) {
            Text(
                text = detail,
                color = Color.White.copy(alpha = 0.68f),
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun ChimahonPlayerErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedStateShape)
            .background(Color.Black.copy(alpha = 0.72f))
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = ChimahonPlayerIcon.Close.imageVector,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(36.dp),
        )
        Text(
            text = "Video failed",
            color = Color.White,
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.SemiBold),
        )
        Text(
            text = message,
            color = Color.White.copy(alpha = 0.72f),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
        )
        ChimahonPlayerActionPill(
            icon = ChimahonPlayerIcon.Refresh,
            label = "Retry",
            onClick = onRetry,
        )
    }
}

@Composable
fun ChimahonPlayerEmptyState(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedStateShape)
            .background(Color.Black.copy(alpha = 0.62f))
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = ChimahonPlayerIcon.Chapters.imageVector,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(40.dp),
        )
        Text(
            text = "No video loaded",
            color = Color.White,
            style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
        )
        Text(
            text = "Choose an episode to start playback.",
            color = Color.White.copy(alpha = 0.68f),
            style = MaterialTheme.typography.body2,
        )
    }
}

@Composable
fun ChimahonPlayerTransientMessageChip(
    message: ChimahonPlayerTransientMessage,
    modifier: Modifier = Modifier,
) {
    val accent = when (message.tone) {
        ChimahonPlayerTone.Neutral -> Color.White
        ChimahonPlayerTone.Positive -> Color(0xFF8DE39E.toInt())
        ChimahonPlayerTone.Warning -> Color(0xFFFFD166.toInt())
        ChimahonPlayerTone.Error -> Color(0xFFFF8A80.toInt())
    }
    Column(
        modifier = modifier
            .clip(RoundedStateShape)
            .background(Color.Black.copy(alpha = 0.72f))
            .padding(horizontal = 18.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message.title,
            color = accent,
            style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.SemiBold),
        )
        if (!message.detail.isNullOrBlank()) {
            Text(
                text = message.detail,
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.caption,
            )
        }
    }
}

private val RoundedStateShape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
