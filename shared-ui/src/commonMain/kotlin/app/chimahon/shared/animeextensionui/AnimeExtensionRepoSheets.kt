package app.chimahon.shared.animeextensionui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
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

@Composable
fun AnimeExtensionRepoSheetHost(
    state: AnimeExtensionRepoSheetState,
    actions: AnimeExtensionRepoActions,
    modifier: Modifier = Modifier,
    knownBaseUrls: Collection<String> = emptyList(),
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    if (state == AnimeExtensionRepoSheetState.None) return
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.26f))
            .clickable(onClick = actions.onDismissSheet),
        contentAlignment = Alignment.BottomCenter,
    ) {
        when (state) {
            AnimeExtensionRepoSheetState.None -> Unit
            is AnimeExtensionRepoSheetState.Form -> {
                AnimeExtensionRepoFormSheet(
                    state = state.state,
                    actions = actions,
                    knownBaseUrls = knownBaseUrls,
                    colors = colors,
                    modifier = Modifier.clickable(onClick = {}),
                )
            }
            is AnimeExtensionRepoSheetState.Confirm -> {
                AnimeExtensionRepoConfirmSheet(
                    url = state.url,
                    confirming = state.confirming,
                    actions = actions,
                    colors = colors,
                    modifier = Modifier.clickable(onClick = {}),
                )
            }
            is AnimeExtensionRepoSheetState.Remove -> {
                AnimeExtensionRepoRemoveSheet(
                    repository = state.repository,
                    removing = state.removing,
                    actions = actions,
                    colors = colors,
                    modifier = Modifier.clickable(onClick = {}),
                )
            }
            is AnimeExtensionRepoSheetState.Conflict -> {
                AnimeExtensionRepoConflictSheet(
                    oldRepository = state.oldRepository,
                    newRepository = state.newRepository,
                    actions = actions,
                    colors = colors,
                    modifier = Modifier.clickable(onClick = {}),
                )
            }
        }
    }
}

@Composable
fun AnimeExtensionRepoFormSheet(
    state: AnimeExtensionRepoFormState,
    actions: AnimeExtensionRepoActions,
    modifier: Modifier = Modifier,
    knownBaseUrls: Collection<String> = emptyList(),
    validation: AnimeExtensionRepoValidationResult = validateAnimeExtensionRepoForm(state, knownBaseUrls),
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    AnimeExtensionSheetSurface(
        modifier = modifier,
        colors = colors,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AnimeExtensionSheetHeader(
                title = state.mode.title,
                subtitle = if (state.editing) {
                    "Update anime repository metadata and refresh available sources."
                } else {
                    "Add a compatible anime extension repository."
                },
                icon = AnimeExtensionIcon.Repo,
                colors = colors,
            )
            OutlinedTextField(
                value = state.url,
                onValueChange = actions.onUrlChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Repository URL") },
                placeholder = { Text("https://example.org/anime/repo") },
                singleLine = true,
                isError = validation.hasErrors,
                enabled = !state.saving,
            )
            OutlinedTextField(
                value = state.name,
                onValueChange = actions.onNameChange,
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
                    onValueChange = actions.onShortNameChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("Short name") },
                    singleLine = true,
                    enabled = !state.saving,
                )
                OutlinedTextField(
                    value = state.website,
                    onValueChange = actions.onWebsiteChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("Website") },
                    singleLine = true,
                    enabled = !state.saving,
                )
            }
            OutlinedTextField(
                value = state.signingKeyFingerprint,
                onValueChange = actions.onSigningKeyChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Signing key fingerprint") },
                placeholder = { Text("Optional") },
                singleLine = true,
                enabled = !state.saving,
            )
            AnimeExtensionRepoValidationMessages(
                validation = validation,
                colors = colors,
            )
            if (state.normalizedUrl.isNotBlank()) {
                AnimeExtensionIndexPreviewRow(
                    indexUrl = state.normalizedUrl.toAnimeExtensionIndexUrl(),
                    colors = colors,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = actions.onDismissSheet,
                    enabled = !state.saving,
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = { actions.onSubmitForm(state) },
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
fun AnimeExtensionRepoConfirmSheet(
    url: String,
    actions: AnimeExtensionRepoActions,
    modifier: Modifier = Modifier,
    confirming: Boolean = false,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    val normalizedUrl = url.trim().trimEnd('/')
    AnimeExtensionSheetSurface(
        modifier = modifier,
        colors = colors,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            AnimeExtensionSheetHeader(
                title = "Add anime repository?",
                subtitle = "Review the repository URL before adding it to anime extension sources.",
                icon = AnimeExtensionIcon.Repo,
                colors = colors,
            )
            AnimeExtensionIndexPreviewRow(
                indexUrl = normalizedUrl.toAnimeExtensionIndexUrl(),
                colors = colors,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = actions.onDismissSheet,
                    enabled = !confirming,
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = { actions.onConfirmAddRepository(normalizedUrl) },
                    enabled = normalizedUrl.isNotBlank() && !confirming,
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    if (confirming) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colors.onPrimary,
                        )
                    }
                    Text(if (confirming) "Adding" else "Add")
                }
            }
        }
    }
}

@Composable
fun AnimeExtensionRepoRemoveSheet(
    repository: AnimeExtensionRepoUiModel,
    actions: AnimeExtensionRepoActions,
    modifier: Modifier = Modifier,
    removing: Boolean = false,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    AnimeExtensionSheetSurface(
        modifier = modifier,
        colors = colors,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            AnimeExtensionSheetHeader(
                title = "Remove anime repository?",
                subtitle = repository.displayName,
                icon = AnimeExtensionIcon.Delete,
                iconTint = colors.error,
                colors = colors,
            )
            AnimeExtensionLabel(
                text = "Remove ${repository.displayName} from anime extension repositories? Installed anime extensions stay installed.",
                color = colors.content,
                size = 14,
                lineHeight = 20,
                maxLines = 4,
            )
            AnimeExtensionIndexPreviewRow(
                indexUrl = repository.indexUrl,
                colors = colors,
            )
            if (repository.hasExtensions) {
                AnimeExtensionRepositoryBadges(
                    repository = repository,
                    colors = colors,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = actions.onDismissSheet,
                    enabled = !removing,
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = { actions.onConfirmRemoveRepository(repository) },
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
fun AnimeExtensionRepoConflictSheet(
    oldRepository: AnimeExtensionRepoUiModel,
    newRepository: AnimeExtensionRepoUiModel,
    actions: AnimeExtensionRepoActions,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    AnimeExtensionSheetSurface(
        modifier = modifier,
        colors = colors,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            AnimeExtensionSheetHeader(
                title = "Replace repository?",
                subtitle = "The signing key matches an existing anime repository.",
                icon = AnimeExtensionIcon.Security,
                iconTint = colors.warning,
                colors = colors,
            )
            AnimeExtensionConflictRow(
                label = "Current",
                repository = oldRepository,
                colors = colors,
            )
            AnimeExtensionConflictRow(
                label = "New",
                repository = newRepository,
                colors = colors,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = actions.onDismissSheet) {
                    Text("Cancel")
                }
                Button(
                    onClick = { actions.onConfirmConflictRepositories(oldRepository, newRepository) },
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text("Replace")
                }
            }
        }
    }
}

@Composable
fun AnimeExtensionConflictRow(
    label: String,
    repository: AnimeExtensionRepoUiModel,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimeExtensionAvatarTile(
            marker = repository.repoMarker(),
            icon = AnimeExtensionIcon.Repo,
            colors = colors,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            AnimeExtensionLabel(
                text = label,
                color = colors.secondaryContent,
                size = 10,
                weight = FontWeight.Bold,
            )
            AnimeExtensionLabel(
                text = repository.displayName,
                color = colors.content,
                size = 14,
                weight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 2.dp),
            )
            AnimeExtensionLabel(
                text = repository.normalizedBaseUrl,
                color = colors.secondaryContent,
                size = 11,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
fun AnimeExtensionRepoValidationMessages(
    validation: AnimeExtensionRepoValidationResult,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    if (validation.messages.isEmpty()) {
        AnimeExtensionLabel(
            text = "Repository URL is required. Metadata is checked before saving.",
            color = colors.secondaryContent,
            size = 11,
            lineHeight = 16,
            maxLines = 3,
            modifier = modifier,
        )
        return
    }
    Column(modifier = modifier.fillMaxWidth()) {
        validation.messages.forEach { message ->
            val color = when (message.severity) {
                AnimeExtensionRepoValidationSeverity.Info -> colors.secondaryContent
                AnimeExtensionRepoValidationSeverity.Warning -> colors.warning
                AnimeExtensionRepoValidationSeverity.Error -> colors.error
            }
            AnimeExtensionLabel(
                text = message.text,
                color = color,
                size = 11,
                lineHeight = 16,
                maxLines = 3,
            )
        }
    }
}

@Composable
fun AnimeExtensionIndexPreviewRow(
    indexUrl: String,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = AnimeExtensionIcon.Link.imageVector,
            contentDescription = null,
            tint = colors.secondaryContent,
            modifier = Modifier.size(18.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
        ) {
            AnimeExtensionLabel(
                text = "Index URL",
                color = colors.secondaryContent,
                size = 10,
                weight = FontWeight.Bold,
            )
            AnimeExtensionLabel(
                text = indexUrl,
                color = colors.content,
                size = 12,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
fun AnimeExtensionSheetHeader(
    title: String,
    subtitle: String,
    icon: AnimeExtensionIcon,
    modifier: Modifier = Modifier,
    iconTint: Color? = null,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(colors.primaryContainer, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon.imageVector,
                contentDescription = null,
                tint = iconTint ?: colors.primary,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            AnimeExtensionLabel(
                text = title,
                color = colors.content,
                size = 17,
                weight = FontWeight.SemiBold,
            )
            AnimeExtensionLabel(
                text = subtitle,
                color = colors.secondaryContent,
                size = 12,
                lineHeight = 17,
                maxLines = 3,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
fun AnimeExtensionSheetSurface(
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 720.dp),
        color = colors.surface,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        elevation = 12.dp,
    ) {
        content()
    }
}
