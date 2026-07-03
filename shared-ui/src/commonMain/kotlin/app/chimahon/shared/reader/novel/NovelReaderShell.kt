package app.chimahon.shared.reader.novel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun rememberNovelReaderCommandBridge(): NovelReaderCommandBridge {
    return remember { NovelReaderCommandBridge() }
}

@Composable
fun NovelReaderShell(
    state: NovelReaderUiState,
    modifier: Modifier = Modifier,
    systemDark: Boolean = false,
    onBack: () -> Unit = {},
    onHudVisibleChange: (Boolean) -> Unit = {},
    onPanelChange: (NovelReaderPanel?) -> Unit = {},
    onAppearanceChange: (NovelReaderAppearanceState) -> Unit = {},
    onViewportTap: (NovelReaderViewportTap) -> Unit = {},
    onProgressChanged: (NovelReaderPosition) -> Unit = {},
    onLookupRequested: (NovelReaderLookupRequest) -> Unit = {},
    onSentenceReady: (String) -> Unit = {},
    onDismissLookupRequested: () -> Unit = {},
    onInternalLinkClicked: (String) -> Unit = {},
    onSelectionRectsReceived: (List<NovelReaderSelectionRect>) -> Unit = {},
    onCommand: (NovelReaderCommand) -> Unit = {},
    additionalAppearanceSettings: @Composable ColumnScope.() -> Unit = {},
    viewport: @Composable (NovelReaderViewportScope) -> Unit = { scope ->
        NovelReaderViewportPlaceholder(scope = scope)
    },
) {
    val colors = state.appearance.resolveColors(systemDark)
    val viewportCallbacks = NovelReaderViewportCallbacks(
        onTap = { tap ->
            onViewportTap(tap)
            onDismissLookupRequested()
            onCommand(NovelReaderCommand.DismissLookup)
            when {
                state.focusMode && tap.zone == NovelReaderViewportTapZone.Content -> {
                    onCommand(NovelReaderCommand.ChangeFocusMode(false))
                }
                tap.zone != NovelReaderViewportTapZone.Content -> {
                    onHudVisibleChange(!state.showHud)
                }
            }
        },
        onProgressChanged = { position ->
            onProgressChanged(position)
            onCommand(NovelReaderCommand.SaveProgress(position))
        },
        onLookupRequested = onLookupRequested,
        onSentenceReady = onSentenceReady,
        onDismissLookupRequested = onDismissLookupRequested,
        onInternalLinkClicked = { url ->
            onInternalLinkClicked(url)
            onCommand(NovelReaderCommand.JumpToUrl(url))
        },
        onSelectionRectsReceived = onSelectionRectsReceived,
    )

    NovelReaderThemedArea(colors = colors) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colors.background),
        ) {
            val viewportScope = NovelReaderViewportScope(
                state = state,
                colors = colors,
                callbacks = viewportCallbacks,
                onCommand = onCommand,
            )
            viewport(viewportScope)

            if (state.loadingMessage != null) {
                ReaderStatusScrim(
                    text = state.loadingMessage,
                    colors = colors,
                    loading = true,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            if (state.errorMessage != null) {
                ReaderStatusScrim(
                    text = state.errorMessage,
                    colors = colors,
                    loading = false,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            if (state.tracking.isTracking) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 18.dp, end = 14.dp)
                        .size(10.dp),
                    color = Color(0xFF4CAF50),
                    shape = MaterialTheme.shapes.small,
                    content = {},
                )
            }

            if (state.showHud) {
                NovelReaderTopBar(
                    state = state,
                    colors = colors,
                    onBack = onBack,
                    onToggleHud = { onHudVisibleChange(false) },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth(),
                )

                NovelReaderBottomControls(
                    state = state,
                    colors = colors,
                    onCommand = onCommand,
                    onOpenPanel = onPanelChange,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                )
            }

            when (state.activePanel) {
                NovelReaderPanel.Appearance -> {
                    NovelReaderAppearancePanel(
                        appearance = state.appearance,
                        colors = colors,
                        onAppearanceChange = { nextAppearance ->
                            onAppearanceChange(nextAppearance)
                            onCommand(NovelReaderCommand.ApplyAppearance(nextAppearance))
                            if (nextAppearance.continuousMode != state.appearance.continuousMode) {
                                onCommand(NovelReaderCommand.ChangeMode(nextAppearance.continuousMode))
                            }
                        },
                        onDismiss = { onPanelChange(null) },
                        additionalSettings = additionalAppearanceSettings,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                    )
                }
                NovelReaderPanel.Chapters -> {
                    NovelReaderChapterListPanel(
                        state = state,
                        colors = colors,
                        onCommand = onCommand,
                        onDismiss = { onPanelChange(null) },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                    )
                }
                NovelReaderPanel.Statistics -> {
                    NovelReaderStatisticsPanel(
                        tracking = state.tracking,
                        colors = colors,
                        onDismiss = { onPanelChange(null) },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                    )
                }
                NovelReaderPanel.Sasayaki -> {
                    NovelReaderSasayakiPanel(
                        colors = colors,
                        onDismiss = { onPanelChange(null) },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                    )
                }
                null -> Unit
            }
        }
    }
}

@Composable
fun NovelReaderThemedArea(
    colors: NovelReaderResolvedColors,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            primary = colors.text,
            primaryVariant = colors.text,
            secondary = colors.text,
            background = colors.background,
            surface = colors.background,
            onPrimary = colors.background,
            onSecondary = colors.background,
            onBackground = colors.text,
            onSurface = colors.text,
        ),
        content = content,
    )
}

@Composable
fun NovelReaderTopBar(
    state: NovelReaderUiState,
    colors: NovelReaderResolvedColors,
    onBack: () -> Unit,
    onToggleHud: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = colors.chrome,
        contentColor = colors.chromeText,
        elevation = 0.dp,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.chromeText,
                )
            }
        },
        title = {
            Column {
                Text(
                    text = state.document.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.subtitle1,
                )
                val subtitle = state.currentChapter?.title ?: state.document.subtitle
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.caption,
                        color = colors.mutedText,
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onToggleHud) {
                Icon(
                    imageVector = Icons.Outlined.VisibilityOff,
                    contentDescription = "Hide controls",
                    tint = colors.chromeText,
                )
            }
        },
    )
}

@Composable
fun NovelReaderBottomControls(
    state: NovelReaderUiState,
    colors: NovelReaderResolvedColors,
    onCommand: (NovelReaderCommand) -> Unit,
    onOpenPanel: (NovelReaderPanel?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = colors.chrome,
        contentColor = colors.chromeText,
        elevation = 8.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ReaderHudIconButton(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.SkipPrevious,
                            contentDescription = "Previous page",
                            tint = colors.chromeText,
                        )
                    },
                    onClick = { onCommand(NovelReaderCommand.Paginate(forward = false)) },
                )
                Slider(
                    value = state.position.boundedProgress.toFloat(),
                    onValueChange = { progress ->
                        onCommand(NovelReaderCommand.SeekToProgress(progress.toDouble()))
                    },
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = colors.chromeText,
                        activeTrackColor = colors.chromeText,
                        inactiveTrackColor = colors.outline,
                    ),
                )
                ReaderHudIconButton(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.SkipNext,
                            contentDescription = "Next page",
                            tint = colors.chromeText,
                        )
                    },
                    onClick = { onCommand(NovelReaderCommand.Paginate(forward = true)) },
                )
                Text(
                    text = state.progressLabel,
                    style = MaterialTheme.typography.caption,
                    color = colors.mutedText,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ReaderHudIconButton(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.FormatListNumbered,
                            contentDescription = "Chapters",
                            tint = colors.chromeText,
                        )
                    },
                    onClick = { onOpenPanel(NovelReaderPanel.Chapters) },
                )
                ReaderHudIconButton(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = "Appearance",
                            tint = colors.chromeText,
                        )
                    },
                    onClick = { onOpenPanel(NovelReaderPanel.Appearance) },
                )
                ReaderHudIconButton(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.QueryStats,
                            contentDescription = "Statistics",
                            tint = colors.chromeText,
                        )
                    },
                    onClick = { onOpenPanel(NovelReaderPanel.Statistics) },
                )
                ReaderHudIconButton(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.VolumeUp,
                            contentDescription = "Sasayaki",
                            tint = colors.chromeText,
                        )
                    },
                    onClick = { onOpenPanel(NovelReaderPanel.Sasayaki) },
                )
                ReaderHudIconButton(
                    icon = {
                        Icon(
                            imageVector = if (state.focusMode) {
                                Icons.Outlined.VisibilityOff
                            } else {
                                Icons.Outlined.Visibility
                            },
                            contentDescription = "Focus mode",
                            tint = colors.chromeText,
                        )
                    },
                    onClick = {
                        onCommand(NovelReaderCommand.ChangeFocusMode(!state.focusMode))
                    },
                )
            }
        }
    }
}

@Composable
fun NovelReaderViewportPlaceholder(
    scope: NovelReaderViewportScope,
    modifier: Modifier = Modifier,
) {
    val state = scope.state
    val colors = scope.colors
    val appearance = state.appearance
    val sourceText = state.viewportContent.lookupSourceText()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .pointerInput(appearance.tapZonePercent, sourceText) {
                detectTapGestures(
                    onTap = { offset ->
                        val zoneSize = (size.height * appearance.tapZonePercent.coerceIn(5, 45)) / 100f
                        val zone = when {
                            offset.y <= zoneSize -> NovelReaderViewportTapZone.Top
                            offset.y >= size.height - zoneSize -> NovelReaderViewportTapZone.Bottom
                            else -> NovelReaderViewportTapZone.Content
                        }
                        scope.callbacks.onTap(
                            NovelReaderViewportTap(
                                x = offset.x,
                                y = offset.y,
                                zone = zone,
                            ),
                        )
                    },
                    onLongPress = { offset ->
                        val word = sourceText.firstLookupWord() ?: return@detectTapGestures
                        scope.callbacks.onLookupRequested(
                            NovelReaderLookupRequest(
                                word = word,
                                sentence = sourceText.take(180),
                                rect = NovelReaderSelectionRect(
                                    x = offset.x,
                                    y = offset.y,
                                    width = 1f,
                                    height = 1f,
                                ),
                            ),
                        )
                    },
                )
            },
    ) {
        when (val content = state.viewportContent) {
            NovelReaderViewportContent.Empty -> {
                ReaderMessage(
                    text = "No chapter loaded",
                    colors = colors,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            is NovelReaderViewportContent.Loading -> {
                ReaderMessage(
                    text = content.message,
                    colors = colors,
                    loading = true,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            is NovelReaderViewportContent.Error -> {
                ReaderMessage(
                    text = content.message,
                    colors = colors,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            is NovelReaderViewportContent.Text -> {
                NovelReaderTextViewport(
                    text = content.text,
                    appearance = appearance,
                    colors = colors,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is NovelReaderViewportContent.Web -> {
                NovelReaderWebViewportPlaceholder(
                    content = content,
                    appearance = appearance,
                    colors = colors,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun NovelReaderTextViewport(
    text: String,
    appearance: NovelReaderAppearanceState,
    colors: NovelReaderResolvedColors,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val horizontalPadding = (appearance.horizontalPadding.coerceIn(0.0, 50.0) * 2.0).toFloat().dp
    val verticalPadding = (appearance.verticalPadding.coerceIn(0.0, 50.0) * 2.0).toFloat().dp
    SelectionContainer {
        Text(
            text = text,
            modifier = modifier
                .verticalScroll(scrollState)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            color = colors.text,
            fontSize = appearance.fontSize.toFloat().sp,
            lineHeight = (appearance.fontSize * appearance.lineHeight).toFloat().sp,
            style = MaterialTheme.typography.body1,
        )
    }
}

@Composable
fun NovelReaderWebViewportPlaceholder(
    content: NovelReaderViewportContent.Web,
    appearance: NovelReaderAppearanceState,
    colors: NovelReaderResolvedColors,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val displayText = content.htmlPreview
        ?.takeIf(String::isNotBlank)
        ?: content.chapterUrl
        ?: "Web content is not loaded"
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(
                horizontal = (appearance.horizontalPadding.coerceIn(0.0, 50.0) * 2.0).toFloat().dp,
                vertical = (appearance.verticalPadding.coerceIn(0.0, 50.0) * 2.0).toFloat().dp,
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (!content.title.isNullOrBlank()) {
            Text(
                text = content.title,
                style = MaterialTheme.typography.h6,
                color = colors.text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (content.isImageOnly) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = colors.text,
                backgroundColor = colors.outline,
            )
        }
        SelectionContainer {
            Text(
                text = displayText,
                color = colors.text,
                style = MaterialTheme.typography.body1,
                fontSize = appearance.fontSize.toFloat().sp,
                lineHeight = (appearance.fontSize * appearance.lineHeight).toFloat().sp,
            )
        }
    }
}

@Composable
fun NovelReaderChapterListPanel(
    state: NovelReaderUiState,
    colors: NovelReaderResolvedColors,
    onCommand: (NovelReaderCommand) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NovelReaderPanelSurface(
        title = "Chapters",
        colors = colors,
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 360.dp)
                .verticalScroll(scrollState),
        ) {
            state.document.chapters.forEach { chapter ->
                val selected = chapter.index == state.position.chapterIndex
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (selected) colors.selected else Color.Transparent)
                        .clickable {
                            onCommand(
                                NovelReaderCommand.JumpToChapter(
                                    chapterIndex = chapter.index,
                                    progress = 0.0,
                                ),
                            )
                            onDismiss()
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = chapter.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.body1,
                        color = colors.text,
                    )
                    Text(
                        text = chapter.chapterMetaLabel(),
                        style = MaterialTheme.typography.caption,
                        color = colors.mutedText,
                    )
                }
            }
            if (state.document.chapters.isEmpty()) {
                ReaderMessage(
                    text = "No chapters",
                    colors = colors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                )
            }
        }
    }
}

@Composable
fun NovelReaderStatisticsPanel(
    tracking: NovelReaderTrackingState,
    colors: NovelReaderResolvedColors,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NovelReaderPanelSurface(
        title = "Statistics",
        colors = colors,
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        StatisticLine("Tracking", if (tracking.isTracking) "Active" else "Paused", colors)
        StatisticLine("Characters", tracking.characterCountLabel(), colors)
        StatisticLine("Reading time", tracking.readingTimeSeconds.durationLabel(), colors)
        StatisticLine("Speed", tracking.charactersPerHour?.let { "$it chars/hour" } ?: "Not available", colors)
    }
}

@Composable
fun NovelReaderSasayakiPanel(
    colors: NovelReaderResolvedColors,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NovelReaderPanelSurface(
        title = "Sasayaki",
        colors = colors,
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        Text(
            text = "No cues loaded",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            style = MaterialTheme.typography.body1,
            color = colors.mutedText,
        )
    }
}

@Composable
fun NovelReaderPanelSurface(
    title: String,
    colors: NovelReaderResolvedColors,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.heightIn(max = 520.dp),
        color = colors.chrome,
        contentColor = colors.text,
        elevation = 12.dp,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.subtitle1,
                    color = colors.text,
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = colors.text,
                    )
                }
            }
            Divider(color = colors.outline)
            content()
        }
    }
}

@Composable
private fun ReaderHudIconButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(42.dp),
        content = icon,
    )
}

@Composable
private fun ReaderStatusScrim(
    text: String,
    colors: NovelReaderResolvedColors,
    loading: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = colors.chrome,
        contentColor = colors.text,
        elevation = 8.dp,
    ) {
        ReaderMessage(
            text = text,
            colors = colors,
            loading = loading,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun ReaderMessage(
    text: String,
    colors: NovelReaderResolvedColors,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(color = colors.text)
            Spacer(modifier = Modifier.height(12.dp))
        }
        Text(
            text = text,
            color = colors.text,
            style = MaterialTheme.typography.body1,
        )
    }
}

@Composable
private fun StatisticLine(
    label: String,
    value: String,
    colors: NovelReaderResolvedColors,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = colors.mutedText,
            style = MaterialTheme.typography.body2,
        )
        Text(
            text = value,
            color = colors.text,
            style = MaterialTheme.typography.body2,
        )
    }
}

private fun NovelReaderViewportContent.lookupSourceText(): String {
    return when (this) {
        NovelReaderViewportContent.Empty -> ""
        is NovelReaderViewportContent.Loading -> message
        is NovelReaderViewportContent.Error -> message
        is NovelReaderViewportContent.Text -> text
        is NovelReaderViewportContent.Web -> htmlPreview ?: title ?: chapterUrl.orEmpty()
    }
}

private fun String.firstLookupWord(): String? {
    return split(Regex("""\s+"""))
        .firstOrNull { token -> token.any(Char::isLetterOrDigit) }
        ?.trim { character -> !character.isLetterOrDigit() }
        ?.takeIf(String::isNotBlank)
}

private fun NovelReaderChapter.chapterMetaLabel(): String {
    val parts = buildList {
        add("Chapter ${index + 1}")
        characterCount?.let { add("$it chars") }
        if (isImageOnly) add("image")
    }
    return parts.joinToString(" - ")
}

private fun NovelReaderTrackingState.characterCountLabel(): String {
    return if (totalCharacters != null && totalCharacters > 0) {
        "$charactersRead / $totalCharacters"
    } else {
        charactersRead.toString()
    }
}

private fun Long.durationLabel(): String {
    val hours = this / 3600L
    val minutes = (this % 3600L) / 60L
    return if (hours > 0L) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }
}
