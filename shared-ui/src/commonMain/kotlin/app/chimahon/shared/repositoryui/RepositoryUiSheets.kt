package app.chimahon.shared.repositoryui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonRepositoryFormSheet(
    state: ChimahonRepositoryFormState,
    onUrlChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    knownBaseUrls: Collection<String> = emptyList(),
    validation: ChimahonRepositoryValidationResult = validateChimahonRepositoryForm(state, knownBaseUrls),
    onNameChange: (String) -> Unit = {},
    onShortNameChange: (String) -> Unit = {},
    onWebsiteChange: (String) -> Unit = {},
    onSigningKeyFingerprintChange: (String) -> Unit = {},
    colors: ChimahonRepositoryUiColors = ChimahonRepositoryUiDefaults.colors(),
) {
    ChimahonRepositorySheetSurface(
        modifier = modifier,
        colors = colors,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ChimahonRepositorySheetHeader(
                title = state.mode.title,
                subtitle = if (state.isEditing) {
                    "Update this repository and refresh its metadata."
                } else {
                    "Add a compatible Tachiyomi/Mihon extension repository."
                },
                icon = Icons.Outlined.Extension,
                colors = colors,
            )
            OutlinedTextField(
                value = state.url,
                onValueChange = onUrlChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Repository URL") },
                placeholder = { Text("https://example.org/repo") },
                singleLine = true,
                isError = !validation.isValid,
                enabled = !state.saving,
            )
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name") },
                placeholder = { Text("Read from repository metadata") },
                singleLine = true,
                enabled = !state.saving,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = state.shortName,
                    onValueChange = onShortNameChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("Short name") },
                    singleLine = true,
                    enabled = !state.saving,
                )
                OutlinedTextField(
                    value = state.website,
                    onValueChange = onWebsiteChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("Website") },
                    singleLine = true,
                    enabled = !state.saving,
                )
            }
            OutlinedTextField(
                value = state.signingKeyFingerprint,
                onValueChange = onSigningKeyFingerprintChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Signing key fingerprint") },
                placeholder = { Text("Optional") },
                singleLine = true,
                enabled = !state.saving,
            )
            ChimahonRepositoryValidationMessages(
                validation = validation,
                colors = colors,
            )
            if (state.normalizedUrl.isNotBlank()) {
                ChimahonRepositoryIndexPreviewRow(
                    indexUrl = state.normalizedUrl.toChimahonRepositoryIndexUrl(),
                    colors = colors,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = onDismiss,
                    enabled = !state.saving,
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = onSave,
                    enabled = validation.isValid && !state.saving,
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    if (state.saving) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colors.onPrimary,
                        )
                    }
                    Text(if (state.saving) "Saving" else state.mode.actionTitle)
                }
            }
        }
    }
}

@Composable
fun ChimahonRepositoryRemoveConfirmationSheet(
    repository: ChimahonRepositoryUiModel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    removing: Boolean = false,
    colors: ChimahonRepositoryUiColors = ChimahonRepositoryUiDefaults.colors(),
) {
    ChimahonRepositorySheetSurface(
        modifier = modifier,
        colors = colors,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ChimahonRepositorySheetHeader(
                title = "Remove repository?",
                subtitle = repository.displayName,
                icon = Icons.Outlined.Delete,
                iconTint = colors.error,
                colors = colors,
            )
            Text(
                text = "Remove ${repository.displayName} from extension repositories? Installed extensions stay installed.",
                color = colors.content,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
            ChimahonRepositoryIndexPreviewRow(
                indexUrl = repository.indexUrl,
                colors = colors,
            )
            if (repository.counts.hasAnyExtensions) {
                ChimahonRepositoryExtensionBadges(
                    counts = repository.counts,
                    colors = colors,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = onDismiss,
                    enabled = !removing,
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = onConfirm,
                    enabled = !removing,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colors.error,
                        contentColor = Color.White,
                    ),
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    if (removing) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color.White,
                        )
                    }
                    Text(if (removing) "Removing" else "Remove")
                }
            }
        }
    }
}

@Composable
fun ChimahonSelectedRepositoryActionsSheet(
    repository: ChimahonRepositoryUiModel,
    actions: ChimahonRepositoryUiActions,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    actionSpecs: List<ChimahonRepositoryActionSpec> = repository.defaultActionSpecs(),
    colors: ChimahonRepositoryUiColors = ChimahonRepositoryUiDefaults.colors(),
) {
    ChimahonRepositorySheetSurface(
        modifier = modifier,
        colors = colors,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ChimahonRepositoryMiniAvatar(
                    text = repository.initial,
                    colors = colors,
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                ) {
                    Text(
                        text = repository.displayName,
                        color = colors.content,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = repository.baseUrl,
                        color = colors.secondaryContent,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text("Done")
                }
            }
            ChimahonRepositorySyncStatusRow(
                sync = repository.sync,
                enabled = repository.enabled,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
                colors = colors,
            )
            if (repository.counts.hasAnyExtensions) {
                ChimahonRepositoryExtensionBadges(
                    counts = repository.counts,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                    colors = colors,
                )
            }
            Divider(color = colors.divider)
            actionSpecs.forEach { spec ->
                ChimahonRepositoryActionListRow(
                    spec = spec,
                    onClick = {
                        actions.perform(spec.action, repository)
                        if (spec.action != ChimahonRepositoryAction.Remove) {
                            onDismiss()
                        }
                    },
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun ChimahonRepositoryActionListRow(
    spec: ChimahonRepositoryActionSpec,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonRepositoryUiColors = ChimahonRepositoryUiDefaults.colors(),
) {
    val foreground = when {
        !spec.enabled -> colors.secondaryContent.copy(alpha = 0.42f)
        spec.destructive -> colors.error
        else -> colors.content
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(enabled = spec.enabled, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (spec.destructive) {
                        colors.error.copy(alpha = 0.10f)
                    } else {
                        colors.surfaceVariant
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = spec.action.repositorySheetActionIcon(),
                contentDescription = spec.title,
                tint = foreground,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
        ) {
            Text(
                text = spec.title,
                color = foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!spec.subtitle.isNullOrBlank()) {
                Text(
                    text = spec.subtitle,
                    color = colors.secondaryContent.copy(alpha = if (spec.enabled) 1f else 0.42f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun ChimahonRepositoryIndexPreviewRow(
    indexUrl: String,
    modifier: Modifier = Modifier,
    colors: ChimahonRepositoryUiColors,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.ContentCopy,
            contentDescription = "Repository index URL",
            tint = colors.secondaryContent,
            modifier = Modifier.size(18.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
        ) {
            Text(
                text = "Index URL",
                color = colors.secondaryContent,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = indexUrl,
                color = colors.content,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 1.dp),
            )
        }
    }
}

@Composable
private fun ChimahonRepositorySheetHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    colors: ChimahonRepositoryUiColors,
    iconTint: Color = colors.primary,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            Text(
                text = title,
                color = colors.content,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                color = colors.secondaryContent,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun ChimahonRepositorySheetSurface(
    modifier: Modifier = Modifier,
    colors: ChimahonRepositoryUiColors,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.surface,
        contentColor = colors.content,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, colors.divider),
        elevation = 0.dp,
        content = content,
    )
}

@Composable
private fun ChimahonRepositoryMiniAvatar(
    text: String,
    colors: ChimahonRepositoryUiColors,
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text.take(3).uppercase(),
            color = colors.primary,
            fontSize = if (text.length > 2) 10.sp else 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

private fun ChimahonRepositoryAction.repositorySheetActionIcon(): ImageVector {
    return when (this) {
        ChimahonRepositoryAction.Select -> Icons.Outlined.CheckCircle
        ChimahonRepositoryAction.Refresh -> Icons.Outlined.Refresh
        ChimahonRepositoryAction.Edit -> Icons.Outlined.Edit
        ChimahonRepositoryAction.CopyIndexUrl -> Icons.Outlined.ContentCopy
        ChimahonRepositoryAction.OpenWebsite -> Icons.Outlined.OpenInNew
        ChimahonRepositoryAction.Enable -> Icons.Outlined.Visibility
        ChimahonRepositoryAction.Disable -> Icons.Outlined.VisibilityOff
        ChimahonRepositoryAction.Trust -> Icons.Outlined.Security
        ChimahonRepositoryAction.Remove -> Icons.Outlined.Delete
    }
}
