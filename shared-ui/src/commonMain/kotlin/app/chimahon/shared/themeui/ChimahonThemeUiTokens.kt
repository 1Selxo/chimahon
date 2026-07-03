package app.chimahon.shared.themeui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ChimahonThemeUiAccent {
    Default,
    Blue,
    Green,
    Orange,
    Pink,
    Purple,
    Red,
    Teal,
}

enum class ChimahonThemeMode {
    Light,
    Dark,
    System,
}

enum class ChimahonAppTheme(val key: String, val title: String) {
    Default("default", "Default"),
    Monet("monet", "Dynamic color"),
    Custom("custom", "Custom"),
    Cottoncandy("cottoncandy", "Cotton candy"),
    Mocha("mocha", "Mocha"),
    Catppuccin("catppuccin", "Catppuccin"),
    GreenApple("green_apple", "Green apple"),
    Lavender("lavender", "Lavender"),
    MidnightDusk("midnight_dusk", "Midnight dusk"),
    Nord("nord", "Nord"),
    StrawberryDaiquiri("strawberry_daiquiri", "Strawberry daiquiri"),
    Tako("tako", "Tako"),
    TealTurquoise("teal_turquoise", "Teal turquoise"),
    TidalWave("tidal_wave", "Tidal wave"),
    YinYang("yin_yang", "Yin & Yang"),
    Yotsuba("yotsuba", "Yotsuba"),
    Monochrome("monochrome", "Monochrome"),
    Cloudflare("cloudflare", "Cloudflare"),
    Doom("doom", "Doom"),
    Matrix("matrix", "Matrix"),
    Sapphire("sapphire", "Sapphire"),
}

enum class ChimahonDynamicColorMode {
    Off,
    Platform,
    Seed,
}

enum class ChimahonDynamicColorSource {
    None,
    Platform,
    Wallpaper,
    Cover,
    Custom,
}

enum class ChimahonThemeUiDensity {
    Compact,
    Comfortable,
}

@Immutable
data class ChimahonThemePreference(
    val themeMode: ChimahonThemeMode = ChimahonThemeMode.System,
    val appTheme: ChimahonAppTheme = ChimahonAppTheme.Default,
    val accent: ChimahonThemeUiAccent = ChimahonThemeUiAccent.Default,
    val amoled: Boolean = false,
    val dynamicColorMode: ChimahonDynamicColorMode = ChimahonDynamicColorMode.Platform,
    val dynamicColorSource: ChimahonDynamicColorSource = ChimahonDynamicColorSource.Platform,
    val seedColor: Color? = null,
    val density: ChimahonThemeUiDensity = ChimahonThemeUiDensity.Comfortable,
)

@Immutable
data class ChimahonThemeUiColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val elevatedSurface: Color,
    val inverseSurface: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val divider: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
    val info: Color,
    val onInfo: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val scrim: Color,
    val outlineVariant: Color = divider,
    val inverseOnSurface: Color = background,
    val surfaceTint: Color = primary,
    val surfaceContainerLowest: Color = surface,
    val surfaceContainerLow: Color = surface,
    val surfaceContainer: Color = surfaceVariant,
    val surfaceContainerHigh: Color = elevatedSurface,
    val surfaceContainerHighest: Color = elevatedSurface,
    val onSecondaryContainer: Color = onSecondary,
) {
    val disabledContent: Color
        get() = onSurface.copy(alpha = 0.38f)

    val secondaryText: Color
        get() = onSurface.copy(alpha = 0.68f)
}

@Immutable
data class ChimahonDynamicThemeColors(
    val light: ChimahonThemeUiColors? = null,
    val dark: ChimahonThemeUiColors? = null,
    val source: ChimahonDynamicColorSource = ChimahonDynamicColorSource.None,
) {
    val available: Boolean
        get() = light != null || dark != null

    fun colorsFor(darkTheme: Boolean): ChimahonThemeUiColors? {
        return if (darkTheme) {
            dark ?: light
        } else {
            light ?: dark
        }
    }
}

@Immutable
data class ChimahonThemeUiDimensions(
    val screenPadding: Dp,
    val compactScreenPadding: Dp,
    val sectionGap: Dp,
    val itemGap: Dp,
    val appBarHeight: Dp,
    val compactAppBarHeight: Dp,
    val bottomBarHeight: Dp,
    val navigationRailWidth: Dp,
    val listItemMinHeight: Dp,
    val denseListItemMinHeight: Dp,
    val cardPadding: Dp,
    val cardGap: Dp,
    val iconButtonSize: Dp,
    val smallIconButtonSize: Dp,
    val readerControlSize: Dp,
    val readerSmallControlSize: Dp,
    val compactListItemMinHeight: Dp = denseListItemMinHeight,
    val compactCardMinHeight: Dp = 76.dp,
    val compactCardPadding: Dp = 12.dp,
    val compactCardGap: Dp = 8.dp,
    val libraryCoverWidth: Dp = 112.dp,
    val libraryCoverHeight: Dp = 168.dp,
    val browseSourceMinHeight: Dp = 56.dp,
)

@Immutable
data class ChimahonThemeUiRadii(
    val extraSmall: Dp,
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val pill: Dp,
)

@Immutable
data class ChimahonThemeUiElevation(
    val none: Dp,
    val appBar: Dp,
    val card: Dp,
    val dialog: Dp,
    val readerChrome: Dp,
)

@Immutable
data class ChimahonAppBarTokens(
    val height: Dp,
    val compactHeight: Dp,
    val backgroundAlpha: Float,
    val dividerAlpha: Float,
    val titleAlpha: Float,
    val subtitleAlpha: Float,
)

@Immutable
data class ChimahonCardTokens(
    val cornerRadius: Dp,
    val outlinedBorderAlpha: Float,
    val selectedContainerAlpha: Float,
    val pressedContainerAlpha: Float,
    val minHeight: Dp = 96.dp,
    val compactMinHeight: Dp = 76.dp,
    val compactPadding: Dp = 12.dp,
)

@Immutable
data class ChimahonListItemTokens(
    val minHeight: Dp,
    val denseMinHeight: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val iconSize: Dp,
    val leadingBoxSize: Dp,
    val selectedContainerAlpha: Float,
    val supportingTextAlpha: Float,
    val compactHorizontalPadding: Dp = 12.dp,
    val compactVerticalPadding: Dp = 6.dp,
    val compactLeadingBoxSize: Dp = 36.dp,
)

@Immutable
data class ChimahonReaderOverlayTokens(
    val background: Color,
    val chrome: Color,
    val chromeElevated: Color,
    val control: Color,
    val selectedControl: Color,
    val disabledControl: Color,
    val divider: Color,
    val onChrome: Color,
    val secondaryText: Color,
    val accent: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val topScrim: Color,
    val bottomScrim: Color,
    val veil: Color,
    val barAlpha: Float,
    val floatingAlpha: Float,
    val controlAlpha: Float,
)

@Immutable
data class ChimahonNavigationRailTokens(
    val width: Dp,
    val container: Color,
    val divider: Color,
    val selectedIndicator: Color,
    val selectedIcon: Color,
    val selectedLabel: Color,
    val unselectedIcon: Color,
    val unselectedLabel: Color,
    val disabledIcon: Color,
    val disabledLabel: Color,
    val itemMinHeight: Dp,
    val indicatorWidth: Dp,
    val indicatorHeight: Dp,
    val badgeContainer: Color,
    val badgeContent: Color,
)

@Immutable
data class ChimahonBrowseLibraryTokens(
    val libraryBackground: Color,
    val libraryCategoryContainer: Color,
    val libraryCategoryContent: Color,
    val libraryEntryContainer: Color,
    val libraryEntrySelectedContainer: Color,
    val libraryEntryPressedContainer: Color,
    val browseBackground: Color,
    val browseSourceContainer: Color,
    val browseSourcePinnedContainer: Color,
    val coverPlaceholder: Color,
    val unreadBadgeContainer: Color,
    val unreadBadgeContent: Color,
    val downloadedBadgeContainer: Color,
    val downloadedBadgeContent: Color,
    val languageBadgeContainer: Color,
    val languageBadgeContent: Color,
    val inLibraryOverlay: Color,
    val favoriteOverlay: Color,
    val divider: Color,
)

@Immutable
data class ChimahonReaderCanvasTokens(
    val blackBackground: Color,
    val grayBackground: Color,
    val whiteBackground: Color,
    val onDarkCanvas: Color,
    val onLightCanvas: Color,
    val pageGap: Color,
    val pageShadow: Color,
    val progressTrack: Color,
    val progressIndicator: Color,
    val ocrHighlight: Color,
    val ocrSelectedHighlight: Color,
    val selectionHandle: Color,
    val flashBlack: Color,
    val flashWhite: Color,
)

@Immutable
data class ChimahonThemeSwatchTokens(
    val key: String,
    val title: String,
    val subtitle: String,
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val onBackground: Color,
    val dynamic: Boolean = false,
    val amoled: Boolean = false,
)

@Immutable
data class ChimahonThemeUiResolvedStyle(
    val dark: Boolean,
    val amoled: Boolean,
    val appTheme: ChimahonAppTheme,
    val accent: ChimahonThemeUiAccent,
    val density: ChimahonThemeUiDensity,
    val dynamicColorSource: ChimahonDynamicColorSource,
    val colors: ChimahonThemeUiColors,
    val dimensions: ChimahonThemeUiDimensions,
    val radii: ChimahonThemeUiRadii,
    val elevation: ChimahonThemeUiElevation,
    val appBarTokens: ChimahonAppBarTokens,
    val cardTokens: ChimahonCardTokens,
    val listItemTokens: ChimahonListItemTokens,
    val readerOverlayTokens: ChimahonReaderOverlayTokens,
    val navigationRailTokens: ChimahonNavigationRailTokens,
    val browseLibraryTokens: ChimahonBrowseLibraryTokens,
    val readerCanvasTokens: ChimahonReaderCanvasTokens,
)

object ChimahonThemeUiDefaults {
    fun resolvedStyle(
        preference: ChimahonThemePreference = ChimahonThemePreference(),
        systemDark: Boolean,
        dynamicColors: ChimahonDynamicThemeColors = ChimahonDynamicThemeColors(),
    ): ChimahonThemeUiResolvedStyle {
        val dark = preference.themeMode.resolve(systemDark)
        val colors = colorsForPreference(
            preference = preference,
            systemDark = systemDark,
            dynamicColors = dynamicColors,
        )
        return ChimahonThemeUiResolvedStyle(
            dark = dark,
            amoled = dark && preference.amoled,
            appTheme = preference.appTheme,
            accent = preference.accent,
            density = preference.density,
            dynamicColorSource = dynamicSourceFor(preference, dynamicColors),
            colors = colors,
            dimensions = dimensions(preference.density),
            radii = radii(),
            elevation = elevation(),
            appBarTokens = appBarTokens(),
            cardTokens = cardTokens(preference.density),
            listItemTokens = listItemTokens(preference.density),
            readerOverlayTokens = readerOverlayTokens(colors, dark && preference.amoled),
            navigationRailTokens = navigationRailTokens(colors),
            browseLibraryTokens = browseLibraryTokens(colors),
            readerCanvasTokens = readerCanvasTokens(colors, dark && preference.amoled),
        )
    }

    fun colorsForPreference(
        preference: ChimahonThemePreference,
        systemDark: Boolean,
        dynamicColors: ChimahonDynamicThemeColors = ChimahonDynamicThemeColors(),
    ): ChimahonThemeUiColors {
        val dark = preference.themeMode.resolve(systemDark)
        val usePlatformDynamic = preference.dynamicColorMode == ChimahonDynamicColorMode.Platform &&
            preference.appTheme == ChimahonAppTheme.Monet
        val platformColors = if (usePlatformDynamic) dynamicColors.colorsFor(dark) else null
        if (platformColors != null) {
            return platformColors.withAmoled(dark = dark, amoled = preference.amoled)
        }

        val seed = when {
            preference.dynamicColorMode == ChimahonDynamicColorMode.Seed && preference.seedColor != null ->
                preference.seedColor
            preference.appTheme == ChimahonAppTheme.Custom && preference.seedColor != null ->
                preference.seedColor
            preference.appTheme == ChimahonAppTheme.Monet && preference.seedColor != null ->
                preference.seedColor
            else -> null
        }
        if (seed != null) {
            return seededColors(seed = seed, dark = dark)
                .withAmoled(dark = dark, amoled = preference.amoled)
        }

        return colors(
            dark = dark,
            amoled = preference.amoled,
            accent = preference.accent,
            appTheme = preference.appTheme,
        )
    }

    fun colors(
        dark: Boolean,
        amoled: Boolean = false,
        accent: ChimahonThemeUiAccent = ChimahonThemeUiAccent.Default,
        appTheme: ChimahonAppTheme = ChimahonAppTheme.Default,
    ): ChimahonThemeUiColors {
        val defaultPrimary = appTheme.primaryColor(dark)
        val primary = accent.primaryOverride(dark) ?: defaultPrimary
        val secondary = appTheme.secondaryColor(dark, primary)
        val tertiary = appTheme.tertiaryColor(dark)
        val background = appTheme.backgroundColor(dark)
        val surface = background
        val surfaceContainer = appTheme.surfaceContainerColor(dark)
        val surfaceVariant = surfaceContainer
        val surfaceContainerLow = blendColors(surfaceContainer, surface, 0.42f)
        val surfaceContainerHigh = blendColors(surfaceContainer, if (dark) Color.White else Color.Black, 0.08f)
        val surfaceContainerHighest = blendColors(surfaceContainer, if (dark) Color.White else Color.Black, 0.14f)
        val elevatedSurface = surfaceContainerHigh
        val onSurface = contrastTextFor(background)
        val base = ChimahonThemeUiColors(
            background = background,
            surface = surface,
            surfaceVariant = surfaceVariant,
            elevatedSurface = elevatedSurface,
            inverseSurface = if (dark) blendColors(onSurface, Color.Black, 0.05f) else Color(0xFF303034),
            onSurface = onSurface,
            onSurfaceVariant = onSurface.copy(alpha = if (dark) 0.78f else 0.72f),
            outline = onSurface.copy(alpha = if (dark) 0.34f else 0.28f),
            divider = onSurface.copy(alpha = if (dark) 0.18f else 0.12f),
            primary = primary,
            onPrimary = contrastTextFor(primary),
            primaryContainer = containerFor(primary, dark),
            onPrimaryContainer = contrastTextFor(containerFor(primary, dark)),
            secondary = secondary,
            onSecondary = contrastTextFor(secondary),
            secondaryContainer = containerFor(secondary, dark),
            tertiary = tertiary,
            onTertiary = contrastTextFor(tertiary),
            success = if (dark) Color(0xFF7DDB91) else Color(0xFF1B7F4A),
            onSuccess = if (dark) Color(0xFF003916) else Color.White,
            warning = if (dark) Color(0xFFFFD18B) else Color(0xFF8A5400),
            onWarning = if (dark) Color(0xFF4A2B00) else Color.White,
            info = if (dark) Color(0xFF85D8FF) else Color(0xFF006780),
            onInfo = if (dark) Color(0xFF003545) else Color.White,
            error = if (dark) Color(0xFFFFB4AB) else Color(0xFFBA1A1A),
            onError = if (dark) Color(0xFF690005) else Color.White,
            errorContainer = if (dark) Color(0xFF93000A) else Color(0xFFFFDAD6),
            scrim = Color.Black.copy(alpha = if (dark) 0.58f else 0.38f),
            outlineVariant = onSurface.copy(alpha = if (dark) 0.22f else 0.18f),
            inverseOnSurface = if (dark) Color(0xFF1B1B1F) else Color(0xFFF2F0F4),
            surfaceTint = primary,
            surfaceContainerLowest = blendColors(surfaceContainer, surface, 0.62f),
            surfaceContainerLow = surfaceContainerLow,
            surfaceContainer = surfaceContainer,
            surfaceContainerHigh = surfaceContainerHigh,
            surfaceContainerHighest = surfaceContainerHighest,
            onSecondaryContainer = contrastTextFor(containerFor(secondary, dark)),
        )
        return base.withAmoled(dark = dark, amoled = amoled)
    }

    fun dimensions(
        density: ChimahonThemeUiDensity = ChimahonThemeUiDensity.Comfortable,
    ): ChimahonThemeUiDimensions {
        val compact = density == ChimahonThemeUiDensity.Compact
        return ChimahonThemeUiDimensions(
            screenPadding = if (compact) 14.dp else 16.dp,
            compactScreenPadding = if (compact) 10.dp else 12.dp,
            sectionGap = if (compact) 18.dp else 24.dp,
            itemGap = if (compact) 8.dp else 12.dp,
            appBarHeight = 64.dp,
            compactAppBarHeight = 56.dp,
            bottomBarHeight = 72.dp,
            navigationRailWidth = if (compact) 88.dp else 96.dp,
            listItemMinHeight = if (compact) 56.dp else 64.dp,
            denseListItemMinHeight = if (compact) 44.dp else 48.dp,
            cardPadding = if (compact) 14.dp else 16.dp,
            cardGap = if (compact) 8.dp else 12.dp,
            iconButtonSize = 48.dp,
            smallIconButtonSize = 40.dp,
            readerControlSize = 48.dp,
            readerSmallControlSize = 40.dp,
            compactListItemMinHeight = 44.dp,
            compactCardMinHeight = 76.dp,
            compactCardPadding = 12.dp,
            compactCardGap = 8.dp,
            libraryCoverWidth = if (compact) 96.dp else 112.dp,
            libraryCoverHeight = if (compact) 144.dp else 168.dp,
            browseSourceMinHeight = if (compact) 52.dp else 56.dp,
        )
    }

    fun radii(): ChimahonThemeUiRadii {
        return ChimahonThemeUiRadii(
            extraSmall = 3.dp,
            small = 4.dp,
            medium = 8.dp,
            large = 12.dp,
            pill = 999.dp,
        )
    }

    fun elevation(): ChimahonThemeUiElevation {
        return ChimahonThemeUiElevation(
            none = 0.dp,
            appBar = 2.dp,
            card = 1.dp,
            dialog = 8.dp,
            readerChrome = 6.dp,
        )
    }

    fun appBarTokens(): ChimahonAppBarTokens {
        return ChimahonAppBarTokens(
            height = 64.dp,
            compactHeight = 56.dp,
            backgroundAlpha = 0.98f,
            dividerAlpha = 0.12f,
            titleAlpha = 0.96f,
            subtitleAlpha = 0.68f,
        )
    }

    fun cardTokens(
        density: ChimahonThemeUiDensity = ChimahonThemeUiDensity.Comfortable,
    ): ChimahonCardTokens {
        val compact = density == ChimahonThemeUiDensity.Compact
        return ChimahonCardTokens(
            cornerRadius = 8.dp,
            outlinedBorderAlpha = 0.14f,
            selectedContainerAlpha = 0.12f,
            pressedContainerAlpha = 0.08f,
            minHeight = if (compact) 84.dp else 96.dp,
            compactMinHeight = 76.dp,
            compactPadding = 12.dp,
        )
    }

    fun listItemTokens(
        density: ChimahonThemeUiDensity = ChimahonThemeUiDensity.Comfortable,
    ): ChimahonListItemTokens {
        val compact = density == ChimahonThemeUiDensity.Compact
        return ChimahonListItemTokens(
            minHeight = if (compact) 56.dp else 64.dp,
            denseMinHeight = if (compact) 44.dp else 48.dp,
            horizontalPadding = if (compact) 14.dp else 16.dp,
            verticalPadding = if (compact) 7.dp else 8.dp,
            iconSize = 24.dp,
            leadingBoxSize = if (compact) 36.dp else 40.dp,
            selectedContainerAlpha = 0.12f,
            supportingTextAlpha = 0.68f,
            compactHorizontalPadding = 12.dp,
            compactVerticalPadding = 6.dp,
            compactLeadingBoxSize = 36.dp,
        )
    }

    fun readerOverlayTokens(
        colors: ChimahonThemeUiColors = colors(dark = true),
        amoled: Boolean = false,
    ): ChimahonReaderOverlayTokens {
        val chrome = if (amoled) Color(0xFF0C0C0C) else Color(0xFF17171B)
        val chromeElevated = if (amoled) Color(0xFF131313) else Color(0xFF202027)
        val control = if (amoled) Color(0xFF1B1B1B) else Color(0xFF29292F)
        return ChimahonReaderOverlayTokens(
            background = if (amoled) Color.Black else Color(0xFF0D0D0F),
            chrome = chrome,
            chromeElevated = chromeElevated,
            control = control,
            selectedControl = colors.primary.copy(alpha = 0.34f),
            disabledControl = Color(0xFF35353B),
            divider = Color(0xFF34343B),
            onChrome = Color.White,
            secondaryText = Color(0xFFB9B6C0),
            accent = colors.primary,
            success = colors.success,
            warning = colors.warning,
            error = colors.error,
            topScrim = Color.Black.copy(alpha = 0.62f),
            bottomScrim = Color.Black.copy(alpha = 0.74f),
            veil = Color.Black.copy(alpha = 0.36f),
            barAlpha = 0.94f,
            floatingAlpha = 0.88f,
            controlAlpha = 0.92f,
        )
    }

    fun navigationRailTokens(
        colors: ChimahonThemeUiColors = colors(dark = false),
    ): ChimahonNavigationRailTokens {
        return ChimahonNavigationRailTokens(
            width = 96.dp,
            container = colors.surfaceContainer,
            divider = colors.outlineVariant,
            selectedIndicator = colors.secondaryContainer,
            selectedIcon = colors.onSecondaryContainer,
            selectedLabel = colors.primary,
            unselectedIcon = colors.onSurfaceVariant,
            unselectedLabel = colors.onSurfaceVariant,
            disabledIcon = colors.disabledContent,
            disabledLabel = colors.disabledContent,
            itemMinHeight = 56.dp,
            indicatorWidth = 56.dp,
            indicatorHeight = 32.dp,
            badgeContainer = colors.error,
            badgeContent = colors.onError,
        )
    }

    fun browseLibraryTokens(
        colors: ChimahonThemeUiColors = colors(dark = false),
    ): ChimahonBrowseLibraryTokens {
        return ChimahonBrowseLibraryTokens(
            libraryBackground = colors.background,
            libraryCategoryContainer = colors.surfaceContainerLow,
            libraryCategoryContent = colors.onSurfaceVariant,
            libraryEntryContainer = colors.surface,
            libraryEntrySelectedContainer = colors.primary.copy(alpha = 0.12f),
            libraryEntryPressedContainer = colors.primary.copy(alpha = 0.08f),
            browseBackground = colors.background,
            browseSourceContainer = colors.surfaceContainerLow,
            browseSourcePinnedContainer = colors.secondaryContainer.copy(alpha = 0.82f),
            coverPlaceholder = colors.surfaceContainerHigh,
            unreadBadgeContainer = colors.secondary,
            unreadBadgeContent = colors.onSecondary,
            downloadedBadgeContainer = colors.tertiary,
            downloadedBadgeContent = colors.onTertiary,
            languageBadgeContainer = colors.surfaceContainerHighest,
            languageBadgeContent = colors.onSurfaceVariant,
            inLibraryOverlay = colors.scrim.copy(alpha = 0.38f),
            favoriteOverlay = colors.primary.copy(alpha = 0.18f),
            divider = colors.divider,
        )
    }

    fun readerCanvasTokens(
        colors: ChimahonThemeUiColors = colors(dark = true),
        amoled: Boolean = false,
    ): ChimahonReaderCanvasTokens {
        return ChimahonReaderCanvasTokens(
            blackBackground = if (amoled) Color.Black else Color(0xFF0D0D0F),
            grayBackground = Color(0xFF242424),
            whiteBackground = Color(0xFFF4F4F4),
            onDarkCanvas = Color.White,
            onLightCanvas = Color(0xFF242329),
            pageGap = Color.Black.copy(alpha = if (amoled) 1f else 0.32f),
            pageShadow = Color.Black.copy(alpha = 0.28f),
            progressTrack = Color.White.copy(alpha = 0.22f),
            progressIndicator = colors.primary,
            ocrHighlight = colors.primary.copy(alpha = 0.24f),
            ocrSelectedHighlight = colors.tertiary.copy(alpha = 0.28f),
            selectionHandle = colors.primary,
            flashBlack = Color.Black.copy(alpha = 0.28f),
            flashWhite = Color.White.copy(alpha = 0.34f),
        )
    }

    fun themeSwatches(
        selected: ChimahonAppTheme = ChimahonAppTheme.Default,
        dark: Boolean = false,
        amoled: Boolean = false,
    ): List<ChimahonThemeSwatchTokens> {
        return ChimahonAppTheme.entries.map { appTheme ->
            themeSwatch(
                appTheme = appTheme,
                selected = selected,
                dark = dark,
                amoled = amoled,
            )
        }
    }

    fun themeSwatch(
        appTheme: ChimahonAppTheme,
        selected: ChimahonAppTheme = appTheme,
        dark: Boolean = false,
        amoled: Boolean = false,
    ): ChimahonThemeSwatchTokens {
        val colors = colors(
            dark = dark,
            amoled = amoled,
            appTheme = appTheme,
        )
        return ChimahonThemeSwatchTokens(
            key = appTheme.key,
            title = appTheme.title,
            subtitle = when (appTheme) {
                ChimahonAppTheme.Monet -> "Platform dynamic fallback"
                ChimahonAppTheme.Custom -> "Seeded custom palette"
                else -> if (appTheme == selected) "Selected" else "Theme preset"
            },
            primary = colors.primary,
            secondary = colors.secondary,
            background = colors.background,
            onBackground = colors.onSurface,
            dynamic = appTheme == ChimahonAppTheme.Monet || appTheme == ChimahonAppTheme.Custom,
            amoled = dark && amoled,
        )
    }
}

private fun ChimahonThemeMode.resolve(systemDark: Boolean): Boolean {
    return when (this) {
        ChimahonThemeMode.Light -> false
        ChimahonThemeMode.Dark -> true
        ChimahonThemeMode.System -> systemDark
    }
}

private fun dynamicSourceFor(
    preference: ChimahonThemePreference,
    dynamicColors: ChimahonDynamicThemeColors,
): ChimahonDynamicColorSource {
    return when {
        preference.dynamicColorMode == ChimahonDynamicColorMode.Off -> ChimahonDynamicColorSource.None
        preference.appTheme == ChimahonAppTheme.Monet &&
            preference.dynamicColorMode == ChimahonDynamicColorMode.Platform &&
            dynamicColors.available -> dynamicColors.source.takeUnless { it == ChimahonDynamicColorSource.None }
            ?: ChimahonDynamicColorSource.Platform
        preference.seedColor != null &&
            (preference.dynamicColorMode == ChimahonDynamicColorMode.Seed ||
                preference.appTheme == ChimahonAppTheme.Monet ||
                preference.appTheme == ChimahonAppTheme.Custom) -> preference.dynamicColorSource.seedFallbackSource()
        else -> ChimahonDynamicColorSource.None
    }
}

private fun ChimahonDynamicColorSource.seedFallbackSource(): ChimahonDynamicColorSource {
    return when (this) {
        ChimahonDynamicColorSource.Wallpaper,
        ChimahonDynamicColorSource.Cover,
        ChimahonDynamicColorSource.Custom,
        -> this
        ChimahonDynamicColorSource.None,
        ChimahonDynamicColorSource.Platform,
        -> ChimahonDynamicColorSource.Custom
    }
}

private fun seededColors(
    seed: Color,
    dark: Boolean,
): ChimahonThemeUiColors {
    val primary = if (dark) {
        blendColors(seed, Color.White, 0.56f)
    } else {
        blendColors(seed, Color.Black, 0.82f)
    }
    val secondary = if (dark) {
        blendColors(seed, Color.White, 0.42f)
    } else {
        blendColors(seed, Color.Black, 0.68f)
    }
    val base = ChimahonThemeUiDefaults.colors(dark = dark)
    val surface = if (dark) {
        blendColors(seed, Color.Black, 0.10f)
    } else {
        blendColors(seed, Color.White, 0.05f)
    }
    val surfaceContainer = if (dark) {
        blendColors(seed, Color(0xFF211F26), 0.16f)
    } else {
        blendColors(seed, Color(0xFFF3EDF7), 0.10f)
    }
    return base.copy(
        background = surface,
        surface = surface,
        surfaceVariant = surfaceContainer,
        elevatedSurface = blendColors(surfaceContainer, if (dark) Color.White else Color.Black, 0.08f),
        onSurface = contrastTextFor(surface),
        onSurfaceVariant = contrastTextFor(surface).copy(alpha = if (dark) 0.78f else 0.72f),
        primary = primary,
        onPrimary = contrastTextFor(primary),
        primaryContainer = containerFor(primary, dark),
        onPrimaryContainer = contrastTextFor(containerFor(primary, dark)),
        secondary = secondary,
        onSecondary = contrastTextFor(secondary),
        secondaryContainer = containerFor(secondary, dark),
        onSecondaryContainer = contrastTextFor(containerFor(secondary, dark)),
        tertiary = if (dark) blendColors(seed, Color.White, 0.34f) else blendColors(seed, Color.Black, 0.56f),
        surfaceTint = primary,
        surfaceContainerLowest = blendColors(surfaceContainer, surface, 0.62f),
        surfaceContainerLow = blendColors(surfaceContainer, surface, 0.42f),
        surfaceContainer = surfaceContainer,
        surfaceContainerHigh = blendColors(surfaceContainer, if (dark) Color.White else Color.Black, 0.08f),
        surfaceContainerHighest = blendColors(surfaceContainer, if (dark) Color.White else Color.Black, 0.14f),
    )
}

private fun ChimahonThemeUiColors.withAmoled(
    dark: Boolean,
    amoled: Boolean,
): ChimahonThemeUiColors {
    if (!dark || !amoled) return this
    val container = Color(0xFF0C0C0C)
    return copy(
        background = Color.Black,
        surface = Color.Black,
        surfaceVariant = container,
        elevatedSurface = Color(0xFF131313),
        onSurface = Color.White,
        onSurfaceVariant = Color.White.copy(alpha = 0.78f),
        inverseSurface = Color.White,
        inverseOnSurface = Color.Black,
        outline = Color.White.copy(alpha = 0.30f),
        divider = Color.White.copy(alpha = 0.16f),
        outlineVariant = Color.White.copy(alpha = 0.20f),
        surfaceContainerLowest = container,
        surfaceContainerLow = container,
        surfaceContainer = container,
        surfaceContainerHigh = Color(0xFF131313),
        surfaceContainerHighest = Color(0xFF1B1B1B),
    )
}

private fun ChimahonThemeUiAccent.primaryOverride(dark: Boolean): Color? {
    return when (this) {
        ChimahonThemeUiAccent.Default -> null
        ChimahonThemeUiAccent.Blue -> if (dark) Color(0xFF9BCBFF) else Color(0xFF0061A4)
        ChimahonThemeUiAccent.Green -> if (dark) Color(0xFF7DDB91) else Color(0xFF006D3B)
        ChimahonThemeUiAccent.Orange -> if (dark) Color(0xFFFFB86C) else Color(0xFF9A4F00)
        ChimahonThemeUiAccent.Pink -> if (dark) Color(0xFFFFB1C8) else Color(0xFFB71858)
        ChimahonThemeUiAccent.Purple -> if (dark) Color(0xFFD0BCFF) else Color(0xFF6750A4)
        ChimahonThemeUiAccent.Red -> if (dark) Color(0xFFFFB4AB) else Color(0xFFBA1A1A)
        ChimahonThemeUiAccent.Teal -> if (dark) Color(0xFF80D8CF) else Color(0xFF006A60)
    }
}

private fun ChimahonAppTheme.primaryColor(dark: Boolean): Color {
    return when (this) {
        ChimahonAppTheme.Default,
        ChimahonAppTheme.Monet,
        ChimahonAppTheme.Custom,
        -> if (dark) Color(0xFFB0C6FF) else Color(0xFF0058CA)
        ChimahonAppTheme.Cottoncandy -> if (dark) Color(0xFFFFB3B4) else Color(0xFF8F4A4C)
        ChimahonAppTheme.Mocha -> if (dark) Color(0xFFFFB77F) else Color(0xFF89511F)
        ChimahonAppTheme.Catppuccin -> if (dark) Color(0xFFCBA6F7) else Color(0xFF8839EF)
        ChimahonAppTheme.GreenApple -> if (dark) Color(0xFF7ADB8F) else Color(0xFF005927)
        ChimahonAppTheme.Lavender -> if (dark) Color(0xFFA177FF) else Color(0xFF6D41C8)
        ChimahonAppTheme.MidnightDusk -> if (dark) Color(0xFFF02475) else Color(0xFFBB0054)
        ChimahonAppTheme.Nord -> if (dark) Color(0xFF88C0D0) else Color(0xFF5E81AC)
        ChimahonAppTheme.StrawberryDaiquiri -> if (dark) Color(0xFFFFB2B8) else Color(0xFFA10833)
        ChimahonAppTheme.Tako -> if (dark) Color(0xFFF3B375) else Color(0xFF66577E)
        ChimahonAppTheme.TealTurquoise -> if (dark) Color(0xFF40E0D0) else Color(0xFF008080)
        ChimahonAppTheme.TidalWave -> if (dark) Color(0xFF5ED4FC) else Color(0xFF006780)
        ChimahonAppTheme.YinYang -> if (dark) Color.White else Color.Black
        ChimahonAppTheme.Yotsuba -> if (dark) Color(0xFFFFB59D) else Color(0xFFAE3200)
        ChimahonAppTheme.Monochrome -> if (dark) Color.White else Color.Black
        ChimahonAppTheme.Cloudflare -> Color(0xFFF38020)
        ChimahonAppTheme.Doom -> Color(0xFFFF0000)
        ChimahonAppTheme.Matrix -> Color(0xFF00FF00)
        ChimahonAppTheme.Sapphire -> Color(0xFF1E88E5)
    }
}

private fun ChimahonAppTheme.secondaryColor(
    dark: Boolean,
    fallback: Color,
): Color {
    return when (this) {
        ChimahonAppTheme.Cottoncandy -> if (dark) Color(0xFF80D4D8) else Color(0xFF00696D)
        ChimahonAppTheme.Mocha -> if (dark) Color(0xFFF6BC70) else Color(0xFF815511)
        ChimahonAppTheme.Nord -> Color(0xFF81A1C1)
        ChimahonAppTheme.StrawberryDaiquiri -> if (dark) Color(0xFFED4A65) else Color(0xFFA10833)
        else -> fallback
    }
}

private fun ChimahonAppTheme.tertiaryColor(dark: Boolean): Color {
    return when (this) {
        ChimahonAppTheme.Default,
        ChimahonAppTheme.Monet,
        ChimahonAppTheme.Custom,
        -> if (dark) Color(0xFF7ADC77) else Color(0xFF006E1B)
        ChimahonAppTheme.Cottoncandy -> if (dark) Color(0xFFEBB5ED) else Color(0xFF7B4E7F)
        ChimahonAppTheme.Mocha -> if (dark) Color(0xFFAED18D) else Color(0xFF48672E)
        ChimahonAppTheme.Catppuccin -> if (dark) Color(0xFFCBA6F7) else Color(0xFF8839EF)
        ChimahonAppTheme.GreenApple -> if (dark) Color(0xFFFFB3AC) else Color(0xFF9D0012)
        ChimahonAppTheme.Lavender -> Color(0xFFCDBDFF)
        ChimahonAppTheme.MidnightDusk -> if (dark) Color(0xFF55971C) else Color(0xFF006638)
        ChimahonAppTheme.Nord -> if (dark) Color(0xFF5E81AC) else Color(0xFF88C0D0)
        ChimahonAppTheme.StrawberryDaiquiri -> if (dark) Color(0xFFE8C08E) else Color(0xFF5F441D)
        ChimahonAppTheme.Tako -> if (dark) Color(0xFF66577E) else Color(0xFFF3B375)
        ChimahonAppTheme.TealTurquoise -> if (dark) Color(0xFFBF1F2F) else Color(0xFFFF7F7F)
        ChimahonAppTheme.TidalWave -> Color(0xFF92F7BC)
        ChimahonAppTheme.YinYang -> if (dark) Color.Black else Color.White
        ChimahonAppTheme.Yotsuba -> if (dark) Color(0xFFD7C68D) else Color(0xFF6B5E2F)
        ChimahonAppTheme.Monochrome -> if (dark) Color(0xFF777777) else Color(0xFF888888)
        ChimahonAppTheme.Cloudflare -> if (dark) Color(0xFF1B1B22) else Color(0xFFEFF2F5)
        ChimahonAppTheme.Doom -> Color(0xFFBFBFBF)
        ChimahonAppTheme.Matrix -> if (dark) Color.White else Color.Black
        ChimahonAppTheme.Sapphire -> if (dark) Color(0xFF212121) else Color(0xFFE1F5FE)
    }
}

private fun ChimahonAppTheme.backgroundColor(dark: Boolean): Color {
    return when (this) {
        ChimahonAppTheme.Default,
        ChimahonAppTheme.Monet,
        ChimahonAppTheme.Custom,
        -> if (dark) Color(0xFF1B1B1F) else Color(0xFFFEFBFF)
        ChimahonAppTheme.Cottoncandy -> if (dark) Color(0xFF1A1111) else Color(0xFFFFF8F7)
        ChimahonAppTheme.Mocha -> if (dark) Color(0xFF19120C) else Color(0xFFFFF8F5)
        ChimahonAppTheme.Catppuccin -> if (dark) Color(0xFF181825) else Color(0xFFE6E9EF)
        ChimahonAppTheme.GreenApple -> if (dark) Color(0xFF0F1510) else Color(0xFFF6FBF2)
        ChimahonAppTheme.Lavender -> if (dark) Color(0xFF111129) else Color(0xFFEDE2FF)
        ChimahonAppTheme.MidnightDusk -> if (dark) Color(0xFF16151D) else Color(0xFFFFFBFF)
        ChimahonAppTheme.Nord -> if (dark) Color(0xFF2E3440) else Color(0xFFECEFF4)
        ChimahonAppTheme.StrawberryDaiquiri -> if (dark) Color(0xFF201A1A) else Color(0xFFFAFAFA)
        ChimahonAppTheme.Tako -> if (dark) Color(0xFF21212E) else Color(0xFFF7F5FF)
        ChimahonAppTheme.TealTurquoise -> if (dark) Color(0xFF202125) else Color(0xFFFAFAFA)
        ChimahonAppTheme.TidalWave -> if (dark) Color(0xFF001C3B) else Color(0xFFFDFBFF)
        ChimahonAppTheme.YinYang -> if (dark) Color(0xFF1E1E1E) else Color(0xFFFDFDFD)
        ChimahonAppTheme.Yotsuba -> if (dark) Color(0xFF211A18) else Color(0xFFFCFCFC)
        ChimahonAppTheme.Monochrome -> if (dark) Color.Black else Color.White
        ChimahonAppTheme.Cloudflare -> if (dark) Color(0xFF1B1B22) else Color(0xFFEFF2F5)
        ChimahonAppTheme.Doom -> if (dark) Color(0xFF1B1B1B) else Color(0xFF212121)
        ChimahonAppTheme.Matrix -> if (dark) Color(0xFF111111) else Color.Black
        ChimahonAppTheme.Sapphire -> if (dark) Color(0xFF212121) else Color.White
    }
}

private fun ChimahonAppTheme.surfaceContainerColor(dark: Boolean): Color {
    return when (this) {
        ChimahonAppTheme.Default,
        ChimahonAppTheme.Monet,
        ChimahonAppTheme.Custom,
        -> if (dark) Color(0xFF211F26) else Color(0xFFF3EDF7)
        ChimahonAppTheme.Cottoncandy -> if (dark) Color(0xFF261D1E) else Color(0xFFFBEAEB)
        ChimahonAppTheme.Mocha -> if (dark) Color(0xFF261E18) else Color(0xFFFBEBE1)
        ChimahonAppTheme.Catppuccin -> if (dark) Color(0xFF1E1E2E) else Color(0xFFEFF1F5)
        ChimahonAppTheme.GreenApple -> if (dark) Color(0xFF1C211C) else Color(0xFFEAEFE6)
        ChimahonAppTheme.Lavender -> if (dark) Color(0xFF1D193B) else Color(0xFFE4D5F8)
        ChimahonAppTheme.MidnightDusk -> if (dark) Color(0xFF281624) else Color(0xFFF9E6F1)
        ChimahonAppTheme.Nord -> if (dark) Color(0xFF414C5C) else Color(0xFFDAE0EA)
        ChimahonAppTheme.StrawberryDaiquiri -> if (dark) Color(0xFF322727) else Color(0xFFF6EAED)
        ChimahonAppTheme.Tako -> if (dark) Color(0xFF2A2A3C) else Color(0xFFE8E0EB)
        ChimahonAppTheme.TealTurquoise -> if (dark) Color(0xFF233133) else Color(0xFFEBF3F1)
        ChimahonAppTheme.TidalWave -> if (dark) Color(0xFF082B4B) else Color(0xFFE8EFF5)
        ChimahonAppTheme.YinYang -> if (dark) Color(0xFF313131) else Color(0xFFE8E8E8)
        ChimahonAppTheme.Yotsuba -> if (dark) Color(0xFF332723) else Color(0xFFF6EBE7)
        ChimahonAppTheme.Monochrome -> if (dark) Color.Black else Color.White
        else -> blendColors(backgroundColor(dark), primaryColor(dark), if (dark) 0.88f else 0.92f)
    }
}

private fun containerFor(color: Color, dark: Boolean): Color {
    return if (dark) {
        blendColors(color, Color.Black, 0.40f)
    } else {
        blendColors(color, Color.White, 0.18f)
    }
}

private fun contrastTextFor(color: Color): Color {
    val luminance = 0.299f * color.red + 0.587f * color.green + 0.114f * color.blue
    return if (luminance > 0.56f) Color(0xFF1B1B1F) else Color.White
}

private fun blendColors(
    foreground: Color,
    background: Color,
    foregroundAmount: Float,
): Color {
    val amount = foregroundAmount.coerceIn(0f, 1f)
    val inverse = 1f - amount
    return Color(
        foreground.red * amount + background.red * inverse,
        foreground.green * amount + background.green * inverse,
        foreground.blue * amount + background.blue * inverse,
        foreground.alpha * amount + background.alpha * inverse,
    )
}
