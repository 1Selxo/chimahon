package app.chimahon.shared.library

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonLibraryCategoryTabs(
    state: ChimahonLibraryCategoryTabsState,
    onCategorySelected: (Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state.style) {
        ChimahonLibraryCategoryTabStyle.ScrollableTabs -> ChimahonLibraryCategoryTabs(
            categories = state.visibleCategories,
            selectedCategoryId = state.selectedCategoryId,
            onCategorySelected = onCategorySelected,
            modifier = modifier,
            showItemCount = state.showItemCount,
        )
        ChimahonLibraryCategoryTabStyle.Chips -> ChimahonLibraryCategoryChipRow(
            categories = state.visibleCategories,
            selectedCategoryId = state.selectedCategoryId,
            onCategorySelected = onCategorySelected,
            modifier = modifier,
            showItemCount = state.showItemCount,
        )
    }
}

@Composable
fun ChimahonLibraryCategoryTabs(
    categories: List<ChimahonLibraryCategoryUiModel>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    showItemCount: Boolean = true,
) {
    if (categories.isEmpty()) return
    val selectedIndex = categories.indexOfFirst { it.id == selectedCategoryId }.takeIf { it >= 0 } ?: 0
    ScrollableTabRow(
        modifier = modifier,
        selectedTabIndex = selectedIndex,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.primary,
        edgePadding = 0.dp,
    ) {
        categories.forEach { category ->
            Tab(
                selected = category.id == selectedCategoryId,
                onClick = { onCategorySelected(category.id) },
                text = {
                    Text(
                        text = category.titleForTabs(showItemCount),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                enabled = !category.hidden,
            )
        }
    }
}

@Composable
fun ChimahonLibraryCategoryChipRow(
    state: ChimahonLibraryCategoryTabsState,
    onCategorySelected: (Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    ChimahonLibraryCategoryChipRow(
        categories = state.visibleCategories,
        selectedCategoryId = state.selectedCategoryId,
        onCategorySelected = onCategorySelected,
        modifier = modifier,
        showItemCount = state.showItemCount,
    )
}

@Composable
fun ChimahonLibraryCategoryChipRow(
    categories: List<ChimahonLibraryCategoryUiModel>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    showItemCount: Boolean = true,
) {
    if (categories.isEmpty()) return
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        categories.forEach { category ->
            ChimahonLibraryCategoryChip(
                category = category,
                selected = category.id == selectedCategoryId,
                showItemCount = showItemCount,
                onClick = { onCategorySelected(category.id) },
            )
        }
    }
}

@Composable
fun ChimahonLibraryCategoryChip(
    category: ChimahonLibraryCategoryUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showItemCount: Boolean = true,
) {
    val background = if (selected) {
        MaterialTheme.colors.primary.copy(alpha = 0.16f)
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.06f)
    }
    val contentColor = if (selected) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.82f)
    }
    Surface(
        modifier = modifier
            .alpha(if (category.hidden) 0.42f else 1f)
            .clickable(enabled = !category.hidden, role = Role.Tab, onClick = onClick)
            .semantics {
                role = Role.Tab
                this.selected = selected
            },
        shape = RoundedCornerShape(50),
        color = background,
        contentColor = contentColor,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            text = category.titleForTabs(showItemCount),
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
