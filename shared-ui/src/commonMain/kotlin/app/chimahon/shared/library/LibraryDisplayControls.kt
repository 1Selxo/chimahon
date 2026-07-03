package app.chimahon.shared.library

import app.chimahon.shared.ChimahonLibraryCoverRatio
import app.chimahon.shared.ChimahonLibraryDisplayMode
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.ViewColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonLibraryDisplayModeToggle(
    selected: ChimahonLibraryDisplayMode,
    onSelected: (ChimahonLibraryDisplayMode) -> Unit,
    modifier: Modifier = Modifier,
    modes: List<ChimahonLibraryDisplayMode> = ChimahonLibraryDisplayMode.entries,
) {
    ChimahonLibrarySegmentedRow(modifier = modifier) {
        modes.forEach { mode ->
            ChimahonLibrarySegmentButton(
                label = mode.libraryDisplayTitle(),
                icon = mode.displayIcon(),
                selected = selected == mode,
                onClick = { onSelected(mode) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun ChimahonLibraryCoverRatioToggle(
    selected: ChimahonLibraryCoverRatio,
    onSelected: (ChimahonLibraryCoverRatio) -> Unit,
    modifier: Modifier = Modifier,
    ratios: List<ChimahonLibraryCoverRatio> = ChimahonLibraryCoverRatio.entries,
) {
    ChimahonLibrarySegmentedRow(modifier = modifier) {
        ratios.forEach { ratio ->
            ChimahonLibraryTextSegmentButton(
                label = ratio.libraryCoverRatioTitle(),
                selected = selected == ratio,
                onClick = { onSelected(ratio) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun ChimahonLibraryDisplaySwitches(
    state: ChimahonLibraryFilterSortState,
    onStateChange: (ChimahonLibraryFilterSortState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ChimahonLibrarySwitchRow(
            title = "Unread badges",
            checked = state.showUnreadBadges,
            onCheckedChange = { onStateChange(state.copy(showUnreadBadges = it)) },
        )
        ChimahonLibrarySwitchRow(
            title = "Downloaded badges",
            checked = state.showDownloadedBadges,
            onCheckedChange = { onStateChange(state.copy(showDownloadedBadges = it)) },
        )
        ChimahonLibrarySwitchRow(
            title = "Bookmarked badges",
            checked = state.showBookmarkedBadges,
            onCheckedChange = { onStateChange(state.copy(showBookmarkedBadges = it)) },
        )
        ChimahonLibrarySwitchRow(
            title = "Local badges",
            checked = state.showLocalBadges,
            onCheckedChange = { onStateChange(state.copy(showLocalBadges = it)) },
        )
        ChimahonLibrarySwitchRow(
            title = "Language badges",
            checked = state.showLanguageBadges,
            onCheckedChange = { onStateChange(state.copy(showLanguageBadges = it)) },
        )
        ChimahonLibrarySwitchRow(
            title = "Source badges",
            checked = state.showSourceBadges,
            onCheckedChange = { onStateChange(state.copy(showSourceBadges = it)) },
        )
        ChimahonLibrarySwitchRow(
            title = "Tracking badges",
            checked = state.showTrackingBadges,
            onCheckedChange = { onStateChange(state.copy(showTrackingBadges = it)) },
        )
        ChimahonLibrarySwitchRow(
            title = "Latest chapter",
            checked = state.showLatestChapter,
            onCheckedChange = { onStateChange(state.copy(showLatestChapter = it)) },
        )
        ChimahonLibrarySwitchRow(
            title = "Continue buttons",
            checked = state.showContinueButtons,
            onCheckedChange = { onStateChange(state.copy(showContinueButtons = it)) },
        )
    }
}

@Composable
fun ChimahonLibrarySwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { onCheckedChange(!checked) }),
        color = MaterialTheme.colors.surface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun ChimahonLibrarySegmentedRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = content,
    )
}

@Composable
fun ChimahonLibrarySegmentButton(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (selected) MaterialTheme.colors.primary.copy(alpha = 0.14f) else MaterialTheme.colors.surface
    val contentColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.72f)
    Surface(
        modifier = modifier
            .clickable(role = Role.RadioButton, onClick = onClick)
            .semantics {
                role = Role.RadioButton
                this.selected = selected
            },
        color = background,
        contentColor = contentColor,
        shape = RoundedCornerShape(9.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null)
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun ChimahonLibraryTextSegmentButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (selected) MaterialTheme.colors.primary.copy(alpha = 0.14f) else MaterialTheme.colors.surface
    val contentColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.72f)
    Surface(
        modifier = modifier
            .clickable(role = Role.RadioButton, onClick = onClick)
            .semantics {
                role = Role.RadioButton
                this.selected = selected
            },
        color = background,
        contentColor = contentColor,
        shape = RoundedCornerShape(9.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun ChimahonLibraryDisplayMode.displayIcon(): ImageVector {
    return when (this) {
        ChimahonLibraryDisplayMode.ComfortableGrid,
        ChimahonLibraryDisplayMode.ComfortableGridPanorama,
        ChimahonLibraryDisplayMode.CompactGrid,
        -> Icons.Outlined.ViewColumn
        ChimahonLibraryDisplayMode.CoverOnlyGrid -> Icons.Outlined.CollectionsBookmark
        ChimahonLibraryDisplayMode.List -> Icons.Outlined.FormatListNumbered
    }
}
