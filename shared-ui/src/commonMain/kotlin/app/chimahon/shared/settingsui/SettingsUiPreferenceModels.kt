package app.chimahon.shared.settingsui

enum class ChimahonPreferenceRowWidget {
    Plain,
    Switch,
    Slider,
    List,
    MultiSelect,
    Action,
    Navigation,
    Info,
}

data class ChimahonPreferenceCategoryUiModel(
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val rows: List<ChimahonPreferenceRowUiModel> = emptyList(),
) {
    val hasRows: Boolean
        get() = rows.isNotEmpty()
}

data class ChimahonPreferenceRowUiModel(
    val key: String,
    val title: String,
    val summary: String? = null,
    val value: String? = null,
    val settingsKey: String = key,
    val enabled: Boolean = true,
    val widget: ChimahonPreferenceRowWidget = ChimahonPreferenceRowWidget.Plain,
    val checked: Boolean? = null,
    val sliderSpec: ChimahonSliderSpec? = null,
    val options: List<ChimahonPreferenceOption> = emptyList(),
    val selectedKey: String? = null,
    val selectedKeys: Set<String> = emptySet(),
    val emptyLabel: String = "None",
    val actionLabel: String? = null,
    val destructive: Boolean = false,
    val targetRoute: ChimahonSettingsRoute? = null,
    val tone: ChimahonPreferenceTone = ChimahonPreferenceTone.Info,
) {
    val secondaryText: String?
        get() = value?.takeIf { it.isNotBlank() }
            ?: summary?.takeIf { it.isNotBlank() }

    val isTwoLine: Boolean
        get() = !secondaryText.isNullOrBlank()
}

fun ChimahonSettingsScreen.toPreferenceCategoryUiModels(): List<ChimahonPreferenceCategoryUiModel> {
    return sections.map { it.toPreferenceCategoryUiModel() }
}

fun ChimahonSettingsSection.toPreferenceCategoryUiModel(): ChimahonPreferenceCategoryUiModel {
    return ChimahonPreferenceCategoryUiModel(
        key = key,
        title = title,
        subtitle = subtitle,
        rows = preferences.map { it.toPreferenceRowUiModel() },
    )
}

fun ChimahonPreference.toPreferenceRowUiModel(): ChimahonPreferenceRowUiModel {
    return when (this) {
        is ChimahonPreference.Action -> ChimahonPreferenceRowUiModel(
            key = key,
            title = title,
            summary = subtitle,
            enabled = enabled,
            widget = ChimahonPreferenceRowWidget.Action,
            actionLabel = actionLabel,
            destructive = destructive,
        )
        is ChimahonPreference.Info -> ChimahonPreferenceRowUiModel(
            key = key,
            title = title,
            summary = subtitle,
            enabled = enabled,
            widget = ChimahonPreferenceRowWidget.Info,
            tone = tone,
        )
        is ChimahonPreference.ListSelect -> ChimahonPreferenceRowUiModel(
            key = key,
            title = title,
            summary = subtitle,
            value = selectedTitle,
            enabled = enabled,
            widget = ChimahonPreferenceRowWidget.List,
            options = options,
            selectedKey = selectedKey,
        )
        is ChimahonPreference.MultiSelect -> ChimahonPreferenceRowUiModel(
            key = key,
            title = title,
            summary = subtitle,
            value = selectedTitle,
            enabled = enabled,
            widget = ChimahonPreferenceRowWidget.MultiSelect,
            options = options,
            selectedKeys = selectedKeys,
            emptyLabel = emptyLabel,
        )
        is ChimahonPreference.NestedScreen -> ChimahonPreferenceRowUiModel(
            key = key,
            title = title,
            summary = subtitle,
            enabled = enabled,
            widget = ChimahonPreferenceRowWidget.Navigation,
            targetRoute = route,
        )
        is ChimahonPreference.Slider -> ChimahonPreferenceRowUiModel(
            key = key,
            title = title,
            summary = subtitle,
            value = spec.valueLabel,
            enabled = enabled,
            widget = ChimahonPreferenceRowWidget.Slider,
            sliderSpec = spec,
        )
        is ChimahonPreference.Switch -> ChimahonPreferenceRowUiModel(
            key = key,
            title = title,
            summary = subtitle,
            enabled = enabled,
            widget = ChimahonPreferenceRowWidget.Switch,
            checked = checked,
        )
    }
}

fun ChimahonSettingsEntryRow.toPreferenceRowUiModel(): ChimahonPreferenceRowUiModel {
    return ChimahonPreferenceRowUiModel(
        key = key,
        title = title,
        summary = subtitle,
        value = value,
        settingsKey = settingsKey,
        enabled = enabled,
        widget = when {
            route != null -> ChimahonPreferenceRowWidget.Navigation
            actionLabel != null -> ChimahonPreferenceRowWidget.Action
            else -> ChimahonPreferenceRowWidget.Plain
        },
        actionLabel = actionLabel,
        destructive = destructive,
        targetRoute = route,
    )
}

fun Iterable<ChimahonSettingsEntryRow>.toPreferenceRowUiModels(): List<ChimahonPreferenceRowUiModel> {
    return map { it.toPreferenceRowUiModel() }
}
