package app.chimahon.shared.discoverui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonDiscoverExtensionHealthBanner(
    extension: ChimahonDiscoverExtensionHealthUiModel,
    modifier: Modifier = Modifier,
    onRefresh: (() -> Unit)? = null,
    onOpenRepository: (() -> Unit)? = null,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    val toneColor = extension.health.badgeTone.color(colors)
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.divider),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ChimahonDiscoverExtensionHealthIcon(
                    health = extension.health,
                    toneColor = toneColor,
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp, end = 8.dp),
                ) {
                    ChimahonDiscoverLabel(
                        text = extension.title,
                        color = colors.content,
                        size = 14,
                        weight = FontWeight.SemiBold,
                    )
                    ChimahonDiscoverLabel(
                        text = extension.subtitle,
                        color = colors.secondaryContent,
                        size = 12,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                if (onOpenRepository != null) {
                    ChimahonDiscoverIconButton(
                        icon = ChimahonDiscoverIcon.Popular,
                        contentDescription = "Open extension repository",
                        onClick = onOpenRepository,
                        colors = colors,
                    )
                }
                if (onRefresh != null) {
                    ChimahonDiscoverIconButton(
                        icon = ChimahonDiscoverIcon.Refresh,
                        contentDescription = "Refresh extension",
                        onClick = onRefresh,
                        loading = extension.health == ChimahonDiscoverSourceHealth.Loading,
                        colors = colors,
                    )
                }
            }
            ChimahonDiscoverBadgeRow(
                badges = extension.badges,
                modifier = Modifier.padding(top = 10.dp),
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonDiscoverExtensionHealthStrip(
    extensions: List<ChimahonDiscoverExtensionHealthUiModel>,
    modifier: Modifier = Modifier,
    onRefresh: ((ChimahonDiscoverExtensionHealthUiModel) -> Unit)? = null,
    onOpenRepository: ((ChimahonDiscoverExtensionHealthUiModel) -> Unit)? = null,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    if (extensions.isEmpty()) return
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        extensions.forEachIndexed { index, extension ->
            if (index > 0) {
                Spacer(modifier = Modifier.size(8.dp))
            }
            ChimahonDiscoverExtensionHealthBanner(
                extension = extension,
                onRefresh = onRefresh?.let { refresh -> { refresh(extension) } },
                onOpenRepository = onOpenRepository?.let { open -> { open(extension) } },
                colors = colors,
            )
        }
    }
}

@Composable
private fun ChimahonDiscoverExtensionHealthIcon(
    health: ChimahonDiscoverSourceHealth,
    toneColor: Color,
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(toneColor.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
    ) {
        if (health == ChimahonDiscoverSourceHealth.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(19.dp),
                strokeWidth = 2.dp,
                color = toneColor,
            )
        } else {
            Icon(
                imageVector = health.icon.imageVector,
                contentDescription = health.title,
                tint = toneColor,
                modifier = Modifier.size(21.dp),
            )
        }
    }
}
