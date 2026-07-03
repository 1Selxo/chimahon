package app.chimahon.shared.settingsui

import app.chimahon.shared.ChimahonSettings

enum class ChimahonSettingsParityArea(
    val key: String,
    val title: String,
    val subtitle: String,
) {
    Theme(
        key = "theme",
        title = "Theme",
        subtitle = "Theme mode, palette, app icon, and interface rhythm",
    ),
    Language(
        key = "language",
        title = "Language",
        subtitle = "Source language visibility and lookup languages",
    ),
    Downloads(
        key = "downloads",
        title = "Downloads",
        subtitle = "Network, storage, cleanup, automation, and queue behavior",
    ),
    Reader(
        key = "reader",
        title = "Reader",
        subtitle = "Reading mode, controls, image tuning, OCR, and performance",
    ),
    Anime(
        key = "anime",
        title = "Anime",
        subtitle = "Anime library, episode defaults, and tracking",
    ),
    Player(
        key = "player",
        title = "Player",
        subtitle = "Playback, gestures, subtitles, audio, and decoder options",
    ),
    LightNovels(
        key = "light_novels",
        title = "Light novels",
        subtitle = "Novel reader typography, layout, and lookup defaults",
    ),
    Desktop(
        key = "desktop",
        title = "Desktop",
        subtitle = "Large-screen navigation and desktop reader controls",
    ),
    Repositories(
        key = "repositories",
        title = "Repositories",
        subtitle = "Extension repository sync, disabled repos, and removal",
    ),
}

data class ChimahonSettingsParitySectionPack(
    val theme: List<ChimahonSettingsSection>,
    val language: List<ChimahonSettingsSection>,
    val downloads: List<ChimahonSettingsSection>,
    val reader: List<ChimahonSettingsSection>,
    val anime: List<ChimahonSettingsSection>,
    val player: List<ChimahonSettingsSection>,
    val lightNovels: List<ChimahonSettingsSection>,
    val desktop: List<ChimahonSettingsSection>,
    val repositories: List<ChimahonSettingsSection>,
) {
    fun sectionsFor(area: ChimahonSettingsParityArea): List<ChimahonSettingsSection> = when (area) {
        ChimahonSettingsParityArea.Anime -> anime
        ChimahonSettingsParityArea.Desktop -> desktop
        ChimahonSettingsParityArea.Downloads -> downloads
        ChimahonSettingsParityArea.Language -> language
        ChimahonSettingsParityArea.LightNovels -> lightNovels
        ChimahonSettingsParityArea.Player -> player
        ChimahonSettingsParityArea.Reader -> reader
        ChimahonSettingsParityArea.Repositories -> repositories
        ChimahonSettingsParityArea.Theme -> theme
    }

    fun toAreaModels(): List<ChimahonSettingsParityAreaModel> {
        return ChimahonSettingsParityArea.entries.map { area ->
            ChimahonSettingsParityAreaModel(
                area = area,
                sections = sectionsFor(area),
            )
        }
    }
}

data class ChimahonSettingsParityAreaModel(
    val area: ChimahonSettingsParityArea,
    val title: String = area.title,
    val subtitle: String = area.subtitle,
    val sections: List<ChimahonSettingsSection> = emptyList(),
) {
    val categories: List<ChimahonPreferenceCategoryUiModel>
        get() = sections.map { it.toPreferenceCategoryUiModel() }
}

fun ChimahonSettings.toSettingsParitySectionPack(): ChimahonSettingsParitySectionPack {
    val appearanceScreen = appearance.toAppearanceSettingsScreen()
    val animeScreen = toAnimeSettingsScreen()
    val browseScreen = browse.toBrowseSettingsScreen()
    val readerScreen = reader.toReaderSettingsScreen()
    return ChimahonSettingsParitySectionPack(
        theme = appearanceScreen.sections.filterKeys(
            "appearance.theme",
            "appearance.interface",
        ),
        language = toLanguageSettingsSections(),
        downloads = downloads.toDownloadSettingsScreen().sections,
        reader = readerScreen.sections.filterNot { section ->
            section.key == "reader.novel" || section.key == "reader.desktop"
        },
        anime = animeScreen.sections.filter { section ->
            section.key.startsWith("animeLibrary.") || section.key == "animePlayer.tracking"
        },
        player = animeScreen.sections.filter { section ->
            section.key.startsWith("animePlayer.") && section.key != "animePlayer.tracking"
        },
        lightNovels = readerScreen.sections.filterKeys("reader.novel"),
        desktop = appearanceScreen.sections.filterKeys("appearance.interface") +
            readerScreen.sections.filterKeys("reader.desktop"),
        repositories = browseScreen.sections.filterKeys("browse.extensions") +
            toRepositoryActionSections(),
    )
}

fun ChimahonSettings.toSettingsParityAreas(): List<ChimahonSettingsParityAreaModel> {
    return toSettingsParitySectionPack().toAreaModels()
}

fun chimahonSettingsLanguageOptions(): List<ChimahonPreferenceOption> = listOf(
    ChimahonPreferenceOption("ja", "Japanese"),
    ChimahonPreferenceOption("en", "English"),
    ChimahonPreferenceOption("ko", "Korean"),
    ChimahonPreferenceOption("zh", "Chinese"),
    ChimahonPreferenceOption("zh-Hans", "Chinese (Simplified)"),
    ChimahonPreferenceOption("zh-Hant", "Chinese (Traditional)"),
    ChimahonPreferenceOption("fr", "French"),
    ChimahonPreferenceOption("de", "German"),
    ChimahonPreferenceOption("es", "Spanish"),
    ChimahonPreferenceOption("it", "Italian"),
    ChimahonPreferenceOption("pt-BR", "Portuguese (Brazil)"),
    ChimahonPreferenceOption("ru", "Russian"),
    ChimahonPreferenceOption("id", "Indonesian"),
    ChimahonPreferenceOption("th", "Thai"),
    ChimahonPreferenceOption("vi", "Vietnamese"),
)

private fun ChimahonSettings.toLanguageSettingsSections(): List<ChimahonSettingsSection> {
    return listOf(
        ChimahonSettingsSection(
            key = "language.sources",
            title = "Source languages",
            subtitle = "Mihon-style language filtering and source metadata display.",
            preferences = listOf(
                ChimahonPreference.MultiSelect(
                    key = "browse.enabledLanguages",
                    title = "Enabled source languages",
                    subtitle = "Empty keeps every installed source language visible.",
                    selectedKeys = browse.enabledLanguages.toSet(),
                    options = chimahonSettingsLanguageOptions(),
                    emptyLabel = "All languages",
                ),
                ChimahonPreference.Switch(
                    key = "browse.groupSourcesByLanguage",
                    title = "Group sources by language",
                    checked = browse.groupSourcesByLanguage,
                ),
                ChimahonPreference.Switch(
                    key = "browse.showSourceLanguage",
                    title = "Show source language",
                    checked = browse.showSourceLanguage,
                ),
            ),
        ),
        ChimahonSettingsSection(
            key = "language.lookup",
            title = "Lookup languages",
            subtitle = "Dictionary and OCR language preferences used from reader overlays.",
            preferences = listOf(
                ChimahonPreference.Switch(
                    key = "dictionary.enabled",
                    title = "Dictionary lookup",
                    checked = dictionary.enabled,
                ),
                ChimahonPreference.Switch(
                    key = "dictionary.ocrEnabled",
                    title = "OCR lookup",
                    checked = dictionary.ocrEnabled,
                    enabled = dictionary.enabled,
                ),
                ChimahonPreference.MultiSelect(
                    key = "dictionary.enabledLanguages",
                    title = "Lookup languages",
                    selectedKeys = dictionary.enabledLanguages.toSet(),
                    options = chimahonSettingsLanguageOptions(),
                    enabled = dictionary.enabled,
                    emptyLabel = "Default language",
                ),
            ),
        ),
    )
}

private fun ChimahonSettings.toRepositoryActionSections(): List<ChimahonSettingsSection> {
    return listOf(
        ChimahonSettingsSection(
            key = "browse.repositories.actions",
            title = "Repository actions",
            subtitle = "Android-style repository management shortcuts.",
            preferences = listOf(
                ChimahonSettingsEntryRows.extensionRepositories(
                    disabledCount = browse.disabledExtensionRepoUrls.size,
                ).asPreference(),
                ChimahonSettingsEntryRows.removeRepository().asPreference(),
            ),
        ),
    )
}

private fun List<ChimahonSettingsSection>.filterKeys(
    vararg keys: String,
): List<ChimahonSettingsSection> {
    val keySet = keys.toSet()
    return filter { it.key in keySet }
}
