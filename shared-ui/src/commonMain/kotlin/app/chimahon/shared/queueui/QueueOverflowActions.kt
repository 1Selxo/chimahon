package app.chimahon.shared.queueui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ChimahonQueueOverflowMenu(
    actions: List<ChimahonQueueOverflowAction>,
    modifier: Modifier = Modifier,
    contentDescription: String = "More actions",
) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(
        modifier = modifier,
        onClick = { expanded = true },
        enabled = actions.isNotEmpty(),
    ) {
        Icon(Icons.Outlined.MoreVert, contentDescription = contentDescription)
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        actions.forEach { action ->
            DropdownMenuItem(
                enabled = action.enabled,
                onClick = {
                    expanded = false
                    action.onClick()
                },
            ) {
                ChimahonQueueActionMenuContent(action = action)
            }
        }
    }
}

@Composable
fun ChimahonQueueActionMenuContent(
    action: ChimahonQueueOverflowAction,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (action.destructive) {
        MaterialTheme.colors.error
    } else {
        Color.Unspecified
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = action.icon.imageVector,
            contentDescription = null,
            tint = contentColor,
        )
        Text(
            text = action.label,
            color = contentColor,
        )
    }
}
