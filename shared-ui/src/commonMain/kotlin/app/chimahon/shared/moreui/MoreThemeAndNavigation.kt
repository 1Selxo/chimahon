package app.chimahon.shared.moreui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonMoreThemeSwatchRow(
    themes: List<ChimahonMoreThemeSwatch>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(themes, key = { it.key }) { theme ->
            ChimahonMoreThemeSwatchCard(
                swatch = theme,
                onClick = { onSelect(theme.key) },
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonMoreThemeSwatchCard(
    swatch: ChimahonMoreThemeSwatch,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    Column(
        modifier = modifier
            .size(width = 136.dp, height = 118.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surface)
            .border(
                width = 1.dp,
                color = if (swatch.selected) colors.primary else colors.divider,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(enabled = swatch.enabled, onClick = onClick)
            .padding(10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(swatch.background),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(swatch.primary),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(width = 52.dp, height = 12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(swatch.secondary),
            )
        }
        ChimahonMoreLabel(
            text = swatch.title,
            color = colors.content,
            size = 13,
            weight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 9.dp),
        )
        swatch.subtitle?.let {
            ChimahonMoreLabel(
                text = it,
                color = colors.secondaryContent,
                size = 10,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
fun ChimahonMoreColorSwatchRow(
    swatches: List<ChimahonMoreColorSwatch>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(swatches, key = { it.key }) { swatch ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(swatch.color)
                        .border(
                            width = if (swatch.selected) 3.dp else 1.dp,
                            color = if (swatch.selected) colors.primary else colors.divider,
                            shape = CircleShape,
                        )
                        .clickable(enabled = swatch.enabled) { onSelect(swatch.key) },
                    contentAlignment = Alignment.Center,
                ) {
                    if (swatch.selected) {
                        Icon(
                            imageVector = ChimahonMoreIcon.Check.imageVector,
                            contentDescription = "${swatch.title} selected",
                            tint = colors.surface,
                        )
                    }
                }
                ChimahonMoreLabel(
                    text = swatch.title,
                    color = colors.secondaryContent,
                    size = 10,
                    modifier = Modifier.padding(top = 5.dp),
                )
            }
        }
    }
}

@Composable
fun ChimahonMoreNavigationTabRow(
    tab: ChimahonMoreNavigationTabState,
    onChangeSection: (String, ChimahonMoreNavigationSection) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    ChimahonMoreListItem(
        title = tab.title,
        subtitle = tab.section.title,
        icon = tab.icon,
        selected = tab.section == ChimahonMoreNavigationSection.Navbar,
        enabled = tab.enabled,
        pills = listOf(ChimahonMoreStatusPill(tab.section.title, active = tab.section == ChimahonMoreNavigationSection.Navbar)),
        trailing = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ChimahonMoreNavigationSection.entries.forEach { section ->
                    ChimahonMoreActionPill(
                        action = ChimahonMorePreferenceAction(section.title, enabled = tab.enabled),
                        selected = section == tab.section,
                        onClick = { onChangeSection(tab.key, section) },
                        colors = colors,
                    )
                }
            }
        },
        modifier = modifier,
        colors = colors,
    )
}
