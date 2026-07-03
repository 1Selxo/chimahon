package app.chimahon.shared.reader.settings

import app.chimahon.shared.ChimahonReaderCanvas
import app.chimahon.shared.ChimahonReaderCustomTheme
import app.chimahon.shared.ChimahonReaderSettings
import app.chimahon.shared.ChimahonReaderTextTheme

data class ChimahonReaderAppearanceState(
    val settings: ChimahonReaderSettings,
    val availableFonts: List<String> = ChimahonReaderAppearanceDefaults.defaultFonts,
    val importedFonts: Set<String> = emptySet(),
    val fontImportInProgress: Boolean = false,
) {
    val allFonts: List<String>
        get() = (availableFonts + importedFonts).distinct()
}

object ChimahonReaderAppearanceDefaults {
    val defaultFonts: List<String> = listOf(
        "System",
        "System Serif",
        "System Sans-Serif",
    )
}

data class ChimahonReaderThemeSwatch(
    val theme: ChimahonReaderTextTheme,
    val label: String,
    val backgroundColor: Int,
    val textColor: Int,
    val splitBackgroundColor: Int? = null,
)

val ChimahonReaderThemeSwatches: List<ChimahonReaderThemeSwatch> = listOf(
    ChimahonReaderThemeSwatch(
        theme = ChimahonReaderTextTheme.System,
        label = "System",
        backgroundColor = 0xFFFFFFFF.toInt(),
        textColor = 0xFF111111.toInt(),
        splitBackgroundColor = 0xFF121212.toInt(),
    ),
    ChimahonReaderThemeSwatch(
        theme = ChimahonReaderTextTheme.Light,
        label = "Light",
        backgroundColor = 0xFFFFFFFF.toInt(),
        textColor = 0xFF111111.toInt(),
    ),
    ChimahonReaderThemeSwatch(
        theme = ChimahonReaderTextTheme.Dark,
        label = "Dark",
        backgroundColor = 0xFF121212.toInt(),
        textColor = 0xFFE0E0E0.toInt(),
    ),
    ChimahonReaderThemeSwatch(
        theme = ChimahonReaderTextTheme.Sepia,
        label = "Sepia",
        backgroundColor = 0xFFF2E2C9.toInt(),
        textColor = 0xFF3C2C1C.toInt(),
    ),
    ChimahonReaderThemeSwatch(
        theme = ChimahonReaderTextTheme.PureBlack,
        label = "AMOLED",
        backgroundColor = 0xFF000000.toInt(),
        textColor = 0xFFE0E0E0.toInt(),
    ),
)

enum class ChimahonReaderSettingsTab(val title: String) {
    ReadingMode("Reading mode"),
    General("General"),
    ColorFilter("Color filter"),
}

data class ChimahonReaderChapterListItem(
    val key: String,
    val title: String,
    val index: Int,
    val characterCount: Int? = null,
    val fragment: String? = null,
    val depth: Int = 0,
    val subtitle: String? = null,
    val selected: Boolean = false,
)

data class ChimahonReaderStatisticPeriod(
    val charactersRead: Int = 0,
    val readingTimeSeconds: Double = 0.0,
    val lastReadingSpeed: Int = 0,
)

data class ChimahonReaderStatisticsState(
    val isTracking: Boolean = false,
    val session: ChimahonReaderStatisticPeriod = ChimahonReaderStatisticPeriod(),
    val today: ChimahonReaderStatisticPeriod = ChimahonReaderStatisticPeriod(),
    val allTime: ChimahonReaderStatisticPeriod = ChimahonReaderStatisticPeriod(),
    val currentCharacter: Int = 0,
    val frozenCharacter: Int = currentCharacter,
    val totalCharacters: Int = 0,
    val currentChapterEndCharacter: Int = totalCharacters,
) {
    val projectionCharacter: Int
        get() = if (isTracking) currentCharacter else frozenCharacter
}

fun ChimahonReaderSettings.withCustomTheme(theme: ChimahonReaderCustomTheme): ChimahonReaderSettings {
    return copy(
        textTheme = ChimahonReaderTextTheme.Custom,
        customBackgroundColor = theme.backgroundColor,
        customTextColor = theme.textColor,
    )
}

fun ChimahonReaderCanvas.readerSettingsTitle(): String {
    return when (this) {
        ChimahonReaderCanvas.Black -> "Black"
        ChimahonReaderCanvas.Gray -> "Gray"
        ChimahonReaderCanvas.White -> "White"
    }
}
