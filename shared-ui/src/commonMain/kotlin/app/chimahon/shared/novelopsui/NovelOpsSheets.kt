package app.chimahon.shared.novelopsui

import app.chimahon.shared.novelui.ChimahonNovelCategoryUiModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

sealed interface NovelOpsSheetState {
    data object None : NovelOpsSheetState
    data class ImportConflict(
        val candidate: NovelImportCandidateUiModel,
        val policies: List<NovelImportConflictPolicy> = NovelImportConflictPolicy.values().toList(),
        val selectedPolicy: NovelImportConflictPolicy = NovelImportConflictPolicy.MergeProgress,
    ) : NovelOpsSheetState
    data class EditCategory(
        val category: ChimahonNovelCategoryUiModel?,
        val name: String,
        val saving: Boolean = false,
    ) : NovelOpsSheetState
    data class DeleteCategory(
        val category: ChimahonNovelCategoryUiModel,
        val deleting: Boolean = false,
    ) : NovelOpsSheetState
}

data class NovelOpsSheetActions(
    val onDismiss: () -> Unit = {},
    val onPolicySelected: (NovelImportConflictPolicy) -> Unit = {},
    val onResolveConflict: () -> Unit = {},
    val onCategoryNameChange: (String) -> Unit = {},
    val onSaveCategory: () -> Unit = {},
    val onDeleteCategory: () -> Unit = {},
)

@Composable
fun NovelOpsSheetHost(
    state: NovelOpsSheetState,
    actions: NovelOpsSheetActions,
    modifier: Modifier = Modifier,
) {
    if (state == NovelOpsSheetState.None) return
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.26f))
            .clickable(onClick = actions.onDismiss),
        contentAlignment = Alignment.BottomCenter,
    ) {
        when (state) {
            NovelOpsSheetState.None -> Unit
            is NovelOpsSheetState.ImportConflict -> NovelImportConflictSheet(
                state = state,
                actions = actions,
                modifier = Modifier.clickable(onClick = {}),
            )
            is NovelOpsSheetState.EditCategory -> NovelEditCategorySheet(
                state = state,
                actions = actions,
                modifier = Modifier.clickable(onClick = {}),
            )
            is NovelOpsSheetState.DeleteCategory -> NovelDeleteCategorySheet(
                state = state,
                actions = actions,
                modifier = Modifier.clickable(onClick = {}),
            )
        }
    }
}

@Composable
fun NovelImportConflictSheet(
    state: NovelOpsSheetState.ImportConflict,
    actions: NovelOpsSheetActions,
    modifier: Modifier = Modifier,
) {
    NovelOpsSheetSurface(modifier = modifier) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NovelOpsSheetTitle(
                title = "Import conflict",
                subtitle = "${state.candidate.title} already exists in the novel library.",
            )
            state.policies.forEach { policy ->
                NovelOpsChip(
                    text = policy.title,
                    selected = policy == state.selectedPolicy,
                    onClick = { actions.onPolicySelected(policy) },
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = actions.onDismiss) { Text("Cancel") }
                Button(
                    onClick = actions.onResolveConflict,
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text("Continue")
                }
            }
        }
    }
}

@Composable
fun NovelEditCategorySheet(
    state: NovelOpsSheetState.EditCategory,
    actions: NovelOpsSheetActions,
    modifier: Modifier = Modifier,
) {
    NovelOpsSheetSurface(modifier = modifier) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NovelOpsSheetTitle(
                title = if (state.category == null) "Create category" else "Rename category",
                subtitle = "Novel category names sync with backups and TTSU metadata.",
            )
            OutlinedTextField(
                value = state.name,
                onValueChange = actions.onCategoryNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name") },
                singleLine = true,
                enabled = !state.saving,
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = actions.onDismiss, enabled = !state.saving) { Text("Cancel") }
                Button(
                    onClick = actions.onSaveCategory,
                    enabled = state.name.isNotBlank() && !state.saving,
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text(if (state.saving) "Saving" else "Save")
                }
            }
        }
    }
}

@Composable
fun NovelDeleteCategorySheet(
    state: NovelOpsSheetState.DeleteCategory,
    actions: NovelOpsSheetActions,
    modifier: Modifier = Modifier,
) {
    NovelOpsSheetSurface(modifier = modifier) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NovelOpsSheetTitle(
                title = "Delete category?",
                subtitle = "Books in ${state.category.title} stay in the library and move to the default category.",
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = actions.onDismiss, enabled = !state.deleting) { Text("Cancel") }
                Button(
                    onClick = actions.onDeleteCategory,
                    enabled = !state.deleting,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.error,
                        contentColor = Color.White,
                    ),
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text(if (state.deleting) "Deleting" else "Delete")
                }
            }
        }
    }
}

@Composable
fun NovelOpsSheetTitle(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        NovelOpsLabel(
            text = title,
            size = 18,
            weight = FontWeight.SemiBold,
        )
        NovelOpsLabel(
            text = subtitle,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.64f),
            size = 12,
            maxLines = 3,
            modifier = Modifier.padding(top = 3.dp),
        )
    }
}

@Composable
fun NovelOpsSheetSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 620.dp),
        color = MaterialTheme.colors.surface,
        elevation = 12.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        content()
    }
}
