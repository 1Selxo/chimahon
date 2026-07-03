package app.chimahon.shared.reader.actions

import app.chimahon.shared.ChimahonReaderPage
import app.chimahon.shared.ChimahonReaderPageActions
import app.chimahon.shared.ChimahonReaderRequest

enum class ChimahonReaderActionId {
    RetryPage,
    RetryAllPages,
    BoostPage,
    SetCover,
    SavePage,
    SharePage,
    OpenPageUrl,
    CopyPageUrl,
    SharePageUrl,
    CopyPageInfo,
    SharePageInfo,
    ToggleOcrLookup,
    ResetView,
    ToggleAutoScroll,
    AutoScrollHelp,
    RetryAllHelp,
    BoostPageHelp,
}

enum class ChimahonReaderPageLoadState {
    Unknown,
    Queued,
    Loading,
    Downloading,
    Ready,
    Error,
}

enum class ChimahonReaderBoostPageAvailability {
    Available,
    MissingPage,
    ErrorPage,
    Downloading,
    AlreadyReady,
    MissingLoader,
}

val ChimahonReaderBoostPageAvailability.canBoost: Boolean
    get() = this == ChimahonReaderBoostPageAvailability.Available

val ChimahonReaderBoostPageAvailability.disabledReason: String?
    get() = when (this) {
        ChimahonReaderBoostPageAvailability.Available -> null
        ChimahonReaderBoostPageAvailability.MissingPage -> "No current page is selected."
        ChimahonReaderBoostPageAvailability.ErrorPage -> "Errored pages need retry first."
        ChimahonReaderBoostPageAvailability.Downloading -> "This page is already downloading."
        ChimahonReaderBoostPageAvailability.AlreadyReady -> "This page is already downloaded."
        ChimahonReaderBoostPageAvailability.MissingLoader -> "The current loader cannot boost pages."
    }

enum class ChimahonReaderShortcutKey(val displayName: String) {
    A("A"),
    B("B"),
    C("C"),
    G("G"),
    I("I"),
    L("L"),
    O("O"),
    R("R"),
    S("S"),
}

data class ChimahonReaderActionShortcut(
    val key: ChimahonReaderShortcutKey,
    val primary: Boolean = false,
    val shift: Boolean = false,
    val alt: Boolean = false,
    val label: String = formatChimahonReaderShortcutLabel(key, primary, shift, alt),
)

data class ChimahonReaderActionDescriptor(
    val id: ChimahonReaderActionId,
    val title: String,
    val subtitle: String? = null,
    val enabled: Boolean = true,
    val active: Boolean = false,
    val busy: Boolean = false,
    val unavailableReason: String? = null,
    val shortcut: ChimahonReaderActionShortcut? = id.defaultShortcut(),
) {
    val supportingText: String?
        get() = if (enabled || unavailableReason == null) subtitle else unavailableReason
}

data class ChimahonReaderAutoScrollState(
    val visible: Boolean = false,
    val running: Boolean = false,
    val frequencyText: String = "3.0",
    val frequencyValid: Boolean = frequencyText.trim().toDoubleOrNull()?.let { it > 0.0 } == true,
    val featureAvailable: Boolean = true,
    val busy: Boolean = false,
    val invalidFrequencyMessage: String = "Autoscroll interval is invalid.",
) {
    val canToggle: Boolean
        get() = featureAvailable && frequencyValid && !busy

    val parsedFrequencySeconds: Double?
        get() = frequencyText.trim().toDoubleOrNull()?.takeIf { it > 0.0 }
}

fun ChimahonReaderAutoScrollState.toActionDescriptor(): ChimahonReaderActionDescriptor {
    val unavailableReason = when {
        !featureAvailable -> "Autoscroll is unavailable for this reader."
        !frequencyValid -> invalidFrequencyMessage
        else -> null
    }
    return ChimahonReaderActionDescriptor(
        id = ChimahonReaderActionId.ToggleAutoScroll,
        title = if (running) "Pause autoscroll" else "Start autoscroll",
        subtitle = parsedFrequencySeconds?.let { "${it.formatReaderSeconds()}s interval" }
            ?: frequencyText.takeIf { it.isNotBlank() },
        enabled = canToggle,
        active = running,
        unavailableReason = unavailableReason,
    )
}

data class ChimahonReaderPageActionsState(
    val mangaTitle: String,
    val chapterName: String,
    val pageNumber: Int,
    val pageUrl: String? = null,
    val pageLoadState: ChimahonReaderPageLoadState = ChimahonReaderPageLoadState.Unknown,
    val failedPageCount: Int? = null,
    val retryAllAvailable: Boolean = true,
    val retryPageAvailable: Boolean = pageLoadState == ChimahonReaderPageLoadState.Error,
    val boostPageAvailability: ChimahonReaderBoostPageAvailability = pageLoadState.toBoostPageAvailability(),
    val setCoverAvailable: Boolean = true,
    val setCoverUnavailableReason: String? = null,
    val savePageAvailable: Boolean = true,
    val sharePageAvailable: Boolean = true,
    val ocrAvailable: Boolean = true,
    val ocrActive: Boolean = false,
    val ocrLoading: Boolean = false,
    val canResetView: Boolean = false,
    val busyAction: ChimahonReaderActionId? = null,
    val message: String? = null,
) {
    val pageSubtitle: String
        get() = "${chapterName.ifBlank { "Chapter" }} - page $pageNumber"

    val hasPageUrl: Boolean
        get() = pageUrl != null

    fun pageActionDescriptors(
        includeRetryPage: Boolean = true,
        includeTextActions: Boolean = true,
        includeResetView: Boolean = true,
    ): List<ChimahonReaderActionDescriptor> = buildList {
        if (includeRetryPage) {
            add(
                descriptor(
                    id = ChimahonReaderActionId.RetryPage,
                    title = "Retry image",
                    subtitle = "Request this page image again.",
                    available = retryPageAvailable,
                    unavailableReason = "This page is not in an error state.",
                ),
            )
        }
        add(
            descriptor(
                id = ChimahonReaderActionId.BoostPage,
                title = "Boost page",
                subtitle = "Prioritize the current page download.",
                available = boostPageAvailability.canBoost,
                unavailableReason = boostPageAvailability.disabledReason,
            ),
        )
        add(
            descriptor(
                id = ChimahonReaderActionId.SetCover,
                title = "Set as cover",
                subtitle = "Use this page as the manga cover.",
                available = setCoverAvailable,
                unavailableReason = setCoverUnavailableReason ?: "This page cannot be used as a cover.",
            ),
        )
        add(
            descriptor(
                id = ChimahonReaderActionId.ToggleOcrLookup,
                title = when {
                    ocrLoading -> "Loading OCR lookup"
                    ocrActive -> "Hide OCR lookup"
                    else -> "Show OCR lookup"
                },
                subtitle = "Toggle the page OCR lookup overlay.",
                available = ocrAvailable && !ocrLoading,
                active = ocrActive || ocrLoading,
                unavailableReason = if (ocrLoading) "OCR lookup is loading." else "OCR lookup is unavailable.",
            ),
        )
        add(
            descriptor(
                id = ChimahonReaderActionId.SavePage,
                title = if (busyAction == ChimahonReaderActionId.SavePage) "Saving page" else "Save page",
                subtitle = "Write this page image to storage.",
                available = savePageAvailable,
                unavailableReason = "This page image cannot be saved.",
            ),
        )
        add(
            descriptor(
                id = ChimahonReaderActionId.SharePage,
                title = if (busyAction == ChimahonReaderActionId.SharePage) "Sharing page" else "Share page",
                subtitle = "Share this page image.",
                available = sharePageAvailable,
                unavailableReason = "This page image cannot be shared.",
            ),
        )
        add(
            descriptor(
                id = ChimahonReaderActionId.OpenPageUrl,
                title = if (hasPageUrl) "Open page URL" else "Page URL unavailable",
                subtitle = "Open the source URL for this page.",
                available = hasPageUrl,
                unavailableReason = "This page does not have a source URL.",
            ),
        )
        add(
            descriptor(
                id = ChimahonReaderActionId.CopyPageUrl,
                title = if (hasPageUrl) "Copy page URL" else "Page URL unavailable",
                subtitle = "Copy the source URL for this page.",
                available = hasPageUrl,
                unavailableReason = "This page does not have a source URL.",
            ),
        )
        add(
            descriptor(
                id = ChimahonReaderActionId.SharePageUrl,
                title = if (hasPageUrl) "Share page URL" else "Page URL unavailable",
                subtitle = "Share the source URL for this page.",
                available = hasPageUrl,
                unavailableReason = "This page does not have a source URL.",
            ),
        )
        if (includeTextActions) {
            add(
                descriptor(
                    id = ChimahonReaderActionId.CopyPageInfo,
                    title = "Copy page info",
                    subtitle = "Copy title, chapter, page number, and URL.",
                ),
            )
            add(
                descriptor(
                    id = ChimahonReaderActionId.SharePageInfo,
                    title = "Share page info",
                    subtitle = "Share title, chapter, page number, and URL.",
                ),
            )
        }
        if (includeResetView) {
            add(
                descriptor(
                    id = ChimahonReaderActionId.ResetView,
                    title = if (canResetView) "Reset zoom and pan" else "Zoom and pan already reset",
                    subtitle = "Restore the page viewport.",
                    available = canResetView,
                    unavailableReason = "The page viewport is already reset.",
                ),
            )
        }
    }

    fun extraActionDescriptors(
        autoScrollState: ChimahonReaderAutoScrollState,
        includeHelp: Boolean = true,
    ): List<ChimahonReaderActionDescriptor> = buildList {
        add(retryAllDescriptor())
        add(
            descriptor(
                id = ChimahonReaderActionId.BoostPage,
                title = "Boost page",
                subtitle = "Prioritize the current page download.",
                available = boostPageAvailability.canBoost,
                unavailableReason = boostPageAvailability.disabledReason,
            ),
        )
        add(autoScrollDescriptor(autoScrollState))
        if (includeHelp) {
            add(
                descriptor(
                    id = ChimahonReaderActionId.AutoScrollHelp,
                    title = "Autoscroll help",
                    subtitle = "Show autoscroll details.",
                    shortcut = null,
                ),
            )
            add(
                descriptor(
                    id = ChimahonReaderActionId.RetryAllHelp,
                    title = "Retry all help",
                    subtitle = "Show retry-all details.",
                    shortcut = null,
                ),
            )
            add(
                descriptor(
                    id = ChimahonReaderActionId.BoostPageHelp,
                    title = "Boost page help",
                    subtitle = "Show boost-page details.",
                    shortcut = null,
                ),
            )
        }
    }

    fun retryAllDescriptor(): ChimahonReaderActionDescriptor {
        return descriptor(
            id = ChimahonReaderActionId.RetryAllPages,
            title = "Retry all failed pages",
            subtitle = failedPageCount?.retryAllSummary() ?: "Retry every failed page in this chapter.",
            available = retryAllAvailable,
            unavailableReason = "There are no failed pages to retry.",
        )
    }

    fun autoScrollDescriptor(
        autoScrollState: ChimahonReaderAutoScrollState,
    ): ChimahonReaderActionDescriptor {
        val unavailableReason = when {
            !autoScrollState.featureAvailable -> "Autoscroll is unavailable for this reader."
            !autoScrollState.frequencyValid -> autoScrollState.invalidFrequencyMessage
            else -> null
        }
        return descriptor(
            id = ChimahonReaderActionId.ToggleAutoScroll,
            title = if (autoScrollState.running) "Pause autoscroll" else "Start autoscroll",
            subtitle = autoScrollState.parsedFrequencySeconds?.let { "${it.formatReaderSeconds()}s interval" }
                ?: autoScrollState.frequencyText.takeIf { it.isNotBlank() },
            available = autoScrollState.canToggle,
            active = autoScrollState.running,
            unavailableReason = unavailableReason,
        )
    }

    private fun descriptor(
        id: ChimahonReaderActionId,
        title: String,
        subtitle: String? = null,
        available: Boolean = true,
        active: Boolean = false,
        unavailableReason: String? = null,
        shortcut: ChimahonReaderActionShortcut? = id.defaultShortcut(),
    ): ChimahonReaderActionDescriptor {
        val actionBusy = busyAction == id
        return ChimahonReaderActionDescriptor(
            id = id,
            title = title,
            subtitle = subtitle,
            enabled = available && busyAction == null,
            active = active || actionBusy,
            busy = actionBusy,
            unavailableReason = unavailableReason,
            shortcut = shortcut,
        )
    }

    companion object {
        fun from(
            request: ChimahonReaderRequest,
            page: ChimahonReaderPage,
            pageLoadState: ChimahonReaderPageLoadState = ChimahonReaderPageLoadState.Unknown,
            failedPageCount: Int? = null,
            retryAllAvailable: Boolean = true,
            retryPageAvailable: Boolean = pageLoadState == ChimahonReaderPageLoadState.Error,
            boostPageAvailability: ChimahonReaderBoostPageAvailability = pageLoadState.toBoostPageAvailability(),
            setCoverAvailable: Boolean = true,
            setCoverUnavailableReason: String? = null,
            savePageAvailable: Boolean = true,
            sharePageAvailable: Boolean = true,
            ocrAvailable: Boolean = true,
            ocrActive: Boolean = false,
            ocrLoading: Boolean = false,
            canResetView: Boolean = false,
            busyAction: ChimahonReaderActionId? = null,
            message: String? = null,
        ): ChimahonReaderPageActionsState {
            return ChimahonReaderPageActionsState(
                mangaTitle = request.mangaTitle.ifBlank { "Untitled manga" },
                chapterName = request.chapterName.ifBlank { "Chapter" },
                pageNumber = ChimahonReaderPageActions.pageDisplayNumber(page),
                pageUrl = ChimahonReaderPageActions.pageUrl(page),
                pageLoadState = pageLoadState,
                failedPageCount = failedPageCount,
                retryAllAvailable = retryAllAvailable,
                retryPageAvailable = retryPageAvailable,
                boostPageAvailability = boostPageAvailability,
                setCoverAvailable = setCoverAvailable,
                setCoverUnavailableReason = setCoverUnavailableReason,
                savePageAvailable = savePageAvailable,
                sharePageAvailable = sharePageAvailable,
                ocrAvailable = ocrAvailable,
                ocrActive = ocrActive,
                ocrLoading = ocrLoading,
                canResetView = canResetView,
                busyAction = busyAction,
                message = message,
            )
        }
    }
}

fun ChimahonReaderPageLoadState.toBoostPageAvailability(): ChimahonReaderBoostPageAvailability {
    return when (this) {
        ChimahonReaderPageLoadState.Unknown,
        ChimahonReaderPageLoadState.Queued,
        -> ChimahonReaderBoostPageAvailability.Available
        ChimahonReaderPageLoadState.Loading,
        ChimahonReaderPageLoadState.Downloading,
        -> ChimahonReaderBoostPageAvailability.Downloading
        ChimahonReaderPageLoadState.Ready -> ChimahonReaderBoostPageAvailability.AlreadyReady
        ChimahonReaderPageLoadState.Error -> ChimahonReaderBoostPageAvailability.ErrorPage
    }
}

fun ChimahonReaderActionId.defaultShortcut(): ChimahonReaderActionShortcut? {
    return when (this) {
        ChimahonReaderActionId.RetryPage -> ChimahonReaderActionShortcut(ChimahonReaderShortcutKey.R)
        ChimahonReaderActionId.RetryAllPages -> {
            ChimahonReaderActionShortcut(ChimahonReaderShortcutKey.R, primary = true)
        }
        ChimahonReaderActionId.BoostPage -> ChimahonReaderActionShortcut(ChimahonReaderShortcutKey.B, shift = true)
        ChimahonReaderActionId.SetCover -> ChimahonReaderActionShortcut(ChimahonReaderShortcutKey.C, shift = true)
        ChimahonReaderActionId.SavePage -> ChimahonReaderActionShortcut(ChimahonReaderShortcutKey.S, primary = true)
        ChimahonReaderActionId.SharePage -> {
            ChimahonReaderActionShortcut(ChimahonReaderShortcutKey.S, primary = true, shift = true)
        }
        ChimahonReaderActionId.OpenPageUrl -> ChimahonReaderActionShortcut(ChimahonReaderShortcutKey.O)
        ChimahonReaderActionId.CopyPageUrl -> ChimahonReaderActionShortcut(ChimahonReaderShortcutKey.L, primary = true)
        ChimahonReaderActionId.SharePageUrl -> {
            ChimahonReaderActionShortcut(ChimahonReaderShortcutKey.L, primary = true, shift = true)
        }
        ChimahonReaderActionId.CopyPageInfo -> ChimahonReaderActionShortcut(ChimahonReaderShortcutKey.I, primary = true)
        ChimahonReaderActionId.SharePageInfo -> {
            ChimahonReaderActionShortcut(ChimahonReaderShortcutKey.I, primary = true, shift = true)
        }
        ChimahonReaderActionId.ToggleOcrLookup -> ChimahonReaderActionShortcut(ChimahonReaderShortcutKey.G)
        ChimahonReaderActionId.ToggleAutoScroll -> ChimahonReaderActionShortcut(ChimahonReaderShortcutKey.A)
        ChimahonReaderActionId.ResetView,
        ChimahonReaderActionId.AutoScrollHelp,
        ChimahonReaderActionId.RetryAllHelp,
        ChimahonReaderActionId.BoostPageHelp,
        -> null
    }
}

fun formatChimahonReaderShortcutLabel(
    key: ChimahonReaderShortcutKey,
    primary: Boolean,
    shift: Boolean,
    alt: Boolean,
): String {
    return buildList {
        if (primary) add("Ctrl/Cmd")
        if (alt) add("Alt")
        if (shift) add("Shift")
        add(key.displayName)
    }.joinToString(separator = "+")
}

private fun Int.retryAllSummary(): String {
    return when (this) {
        0 -> "No failed pages reported."
        1 -> "Retry 1 failed page."
        else -> "Retry $this failed pages."
    }
}

private fun Double.formatReaderSeconds(): String {
    val text = toString()
    return if (text.endsWith(".0")) {
        text.dropLast(2)
    } else {
        text
    }
}
