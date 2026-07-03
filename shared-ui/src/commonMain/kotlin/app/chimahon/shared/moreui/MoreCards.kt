package app.chimahon.shared.moreui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonMoreInfoCard(
    state: ChimahonMoreInfoCardState,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    ChimahonMoreCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 7.dp),
        colors = colors,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChimahonMoreIconTile(
                icon = state.icon,
                active = state.selected,
                warning = state.warning,
                colors = colors,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
            ) {
                ChimahonMoreLabel(
                    text = state.title,
                    color = if (state.warning) colors.warning else colors.content,
                    size = 15,
                    weight = FontWeight.SemiBold,
                    maxLines = 2,
                )
                ChimahonMoreLabel(
                    text = state.subtitle,
                    color = colors.secondaryContent,
                    size = 12,
                    lineHeight = 17,
                    maxLines = 4,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            if (state.action != null && onAction != null) {
                ChimahonMoreActionPill(
                    action = state.action,
                    onClick = onAction,
                    selected = state.selected,
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun ChimahonMoreAboutHeader(
    appName: String,
    version: String,
    platform: String,
    modifier: Modifier = Modifier,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = 20.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ChimahonMoreIconTile(
            icon = ChimahonMoreIcon.About,
            marker = appName.firstOrNull()?.uppercaseChar()?.toString() ?: "C",
            active = true,
            size = 64.dp,
            colors = colors,
        )
        ChimahonMoreLabel(
            text = appName,
            color = colors.content,
            size = 22,
            weight = FontWeight.Bold,
            modifier = Modifier.padding(top = 14.dp),
        )
        ChimahonMoreLabel(
            text = "$version - $platform",
            color = colors.secondaryContent,
            size = 12,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
fun ChimahonMoreAboutLinkRow(
    link: ChimahonMoreAboutLink,
    onOpen: (ChimahonMoreAboutLink) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    ChimahonMoreListItem(
        title = link.title,
        subtitle = link.subtitle,
        icon = link.icon,
        value = link.url,
        onClick = { onOpen(link) },
        modifier = modifier,
        colors = colors,
    )
}

@Composable
fun ChimahonMoreHelpTopicCard(
    topic: ChimahonMoreHelpTopic,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    ChimahonMoreCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 7.dp),
        colors = colors,
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ChimahonMoreIconTile(
                    icon = topic.icon,
                    active = true,
                    size = 38.dp,
                    colors = colors,
                )
                ChimahonMoreLabel(
                    text = topic.title,
                    color = colors.content,
                    size = 15,
                    weight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                )
            }
            Text(
                text = topic.body,
                color = colors.secondaryContent,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 11.dp),
            )
            if (topic.action != null && onAction != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onAction, enabled = topic.action.enabled) {
                        Text(topic.action.label)
                    }
                }
            }
        }
    }
}
