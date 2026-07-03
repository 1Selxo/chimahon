package app.chimahon.shared.menuui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ChimahonActionSheetOverlay(
    visible: Boolean,
    state: ChimahonActionSheetState,
    onDismissRequest: () -> Unit,
    onAction: (ChimahonMenuAction) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonMenuColors = ChimahonMenuDefaults.colors(),
) {
    if (!visible) return
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.32f)),
            contentAlignment = Alignment.BottomCenter,
        ) {
            ChimahonActionSheetContent(
                state = state,
                onDismissRequest = onDismissRequest,
                onAction = { action ->
                    onAction(action)
                    if (action.closeMenuOnClick) {
                        onDismissRequest()
                    }
                },
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonActionSheetContent(
    state: ChimahonActionSheetState,
    onDismissRequest: () -> Unit,
    onAction: (ChimahonMenuAction) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonMenuColors = ChimahonMenuDefaults.colors(),
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 720.dp),
        color = colors.surface,
        contentColor = colors.onSurface,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        elevation = 12.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 680.dp),
        ) {
            ChimahonActionSheetHeader(
                title = state.title,
                subtitle = state.subtitle,
                onDismissRequest = onDismissRequest,
                colors = colors,
            )
            Divider(color = colors.divider)
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
            ) {
                state.sections.withIndex()
                    .filter { it.value.visibleActions.isNotEmpty() }
                    .forEach { indexed ->
                        val section = indexed.value
                        if (indexed.index > 0) {
                            Spacer(Modifier.height(8.dp))
                            Divider(color = colors.divider)
                        }
                        section.title?.takeIf { it.isNotBlank() }?.let { title ->
                            ChimahonMenuSectionHeader(title = title, colors = colors)
                        }
                        section.visibleActions.forEach { action ->
                            ChimahonMenuActionRow(
                                action = action.copy(
                                    selected = action.selected || state.primaryActionId == action.id,
                                ),
                                onAction = onAction,
                                colors = colors,
                            )
                        }
                    }
            }
            Divider(color = colors.divider)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onDismissRequest) {
                    Text(state.dismissLabel)
                }
            }
        }
    }
}

@Composable
private fun ChimahonActionSheetHeader(
    title: String,
    subtitle: String?,
    onDismissRequest: () -> Unit,
    colors: ChimahonMenuColors,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 8.dp, top = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                color = colors.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = colors.secondaryText,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        IconButton(onClick = onDismissRequest) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Outlined.Close,
                contentDescription = "Close",
                tint = colors.icon,
            )
        }
    }
}
