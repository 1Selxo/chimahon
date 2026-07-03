package app.chimahon.shared.settingsui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonSettingsScaffold(
    state: ChimahonSettingsUiState,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val selectedScreen = state.selectedScreen ?: state.screens.firstOrNull()
        if (maxWidth >= 840.dp) {
            Row(modifier = Modifier.fillMaxSize()) {
                ChimahonSettingsNavigationPane(
                    screens = state.screens,
                    selectedRoute = selectedScreen?.route ?: state.selectedRoute,
                    onEvent = onEvent,
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxHeight(),
                )
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
                )
                if (selectedScreen != null) {
                    ChimahonSettingsScreenContent(
                        screen = selectedScreen,
                        onEvent = onEvent,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                }
            }
        } else {
            if (selectedScreen == null) {
                ChimahonSettingsNavigationPane(
                    screens = state.screens,
                    selectedRoute = state.selectedRoute,
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                ChimahonSettingsScreenContent(
                    screen = selectedScreen,
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun ChimahonSettingsNavigationPane(
    screens: List<ChimahonSettingsScreen>,
    selectedRoute: ChimahonSettingsRoute,
    onEvent: (ChimahonSettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "settings-list-title") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colors.onBackground,
                )
                Text(
                    text = "Configure Chimahon",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.62f),
                )
            }
        }
        items(screens, key = { it.route.key }) { screen ->
            ChimahonSettingsNavigationRow(
                screen = screen,
                selected = screen.route == selectedRoute,
                onClick = { onEvent(ChimahonSettingsUiEvent.Navigate(screen.route)) },
            )
        }
    }
}

@Composable
fun ChimahonSettingsNavigationRow(
    screen: ChimahonSettingsScreen,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) {
            MaterialTheme.colors.primary.copy(alpha = 0.13f)
        } else {
            MaterialTheme.colors.surface
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colors.primary.copy(alpha = 0.28f)
            } else {
                MaterialTheme.colors.onSurface.copy(alpha = 0.07f)
            },
        ),
        elevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = screen.title,
                    style = MaterialTheme.typography.body1,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = MaterialTheme.colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = screen.subtitle,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
