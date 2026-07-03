package app.chimahon.shared.themeui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalChimahonThemeUiColors = staticCompositionLocalOf {
    ChimahonThemeUiDefaults.colors(dark = false)
}

val LocalChimahonThemePreference = staticCompositionLocalOf {
    ChimahonThemePreference()
}

val LocalChimahonThemeUiDimensions = staticCompositionLocalOf {
    ChimahonThemeUiDefaults.dimensions()
}

val LocalChimahonThemeUiRadii = staticCompositionLocalOf {
    ChimahonThemeUiDefaults.radii()
}

val LocalChimahonThemeUiElevation = staticCompositionLocalOf {
    ChimahonThemeUiDefaults.elevation()
}

val LocalChimahonAppBarTokens = staticCompositionLocalOf {
    ChimahonThemeUiDefaults.appBarTokens()
}

val LocalChimahonCardTokens = staticCompositionLocalOf {
    ChimahonThemeUiDefaults.cardTokens()
}

val LocalChimahonListItemTokens = staticCompositionLocalOf {
    ChimahonThemeUiDefaults.listItemTokens()
}

val LocalChimahonReaderOverlayTokens = staticCompositionLocalOf {
    ChimahonThemeUiDefaults.readerOverlayTokens()
}

val LocalChimahonNavigationRailTokens = staticCompositionLocalOf {
    ChimahonThemeUiDefaults.navigationRailTokens()
}

val LocalChimahonBrowseLibraryTokens = staticCompositionLocalOf {
    ChimahonThemeUiDefaults.browseLibraryTokens()
}

val LocalChimahonReaderCanvasTokens = staticCompositionLocalOf {
    ChimahonThemeUiDefaults.readerCanvasTokens()
}

object ChimahonThemeUi {
    val preference: ChimahonThemePreference
        @Composable
        @ReadOnlyComposable
        get() = LocalChimahonThemePreference.current

    val colors: ChimahonThemeUiColors
        @Composable
        @ReadOnlyComposable
        get() = LocalChimahonThemeUiColors.current

    val dimensions: ChimahonThemeUiDimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalChimahonThemeUiDimensions.current

    val radii: ChimahonThemeUiRadii
        @Composable
        @ReadOnlyComposable
        get() = LocalChimahonThemeUiRadii.current

    val elevation: ChimahonThemeUiElevation
        @Composable
        @ReadOnlyComposable
        get() = LocalChimahonThemeUiElevation.current

    val appBar: ChimahonAppBarTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalChimahonAppBarTokens.current

    val card: ChimahonCardTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalChimahonCardTokens.current

    val listItem: ChimahonListItemTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalChimahonListItemTokens.current

    val readerOverlay: ChimahonReaderOverlayTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalChimahonReaderOverlayTokens.current

    val navigationRail: ChimahonNavigationRailTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalChimahonNavigationRailTokens.current

    val browseLibrary: ChimahonBrowseLibraryTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalChimahonBrowseLibraryTokens.current

    val readerCanvas: ChimahonReaderCanvasTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalChimahonReaderCanvasTokens.current
}

@Composable
fun ProvideChimahonThemeUi(
    dark: Boolean = isSystemInDarkTheme(),
    amoled: Boolean = false,
    accent: ChimahonThemeUiAccent = ChimahonThemeUiAccent.Default,
    density: ChimahonThemeUiDensity = ChimahonThemeUiDensity.Comfortable,
    themePreference: ChimahonThemePreference = ChimahonThemePreference(
        themeMode = if (dark) ChimahonThemeMode.Dark else ChimahonThemeMode.Light,
        accent = accent,
        amoled = amoled,
        density = density,
    ),
    colors: ChimahonThemeUiColors = ChimahonThemeUiDefaults.colors(
        dark = dark,
        amoled = amoled,
        accent = accent,
    ),
    dimensions: ChimahonThemeUiDimensions = ChimahonThemeUiDefaults.dimensions(density),
    radii: ChimahonThemeUiRadii = ChimahonThemeUiDefaults.radii(),
    elevation: ChimahonThemeUiElevation = ChimahonThemeUiDefaults.elevation(),
    appBarTokens: ChimahonAppBarTokens = ChimahonThemeUiDefaults.appBarTokens(),
    cardTokens: ChimahonCardTokens = ChimahonThemeUiDefaults.cardTokens(density),
    listItemTokens: ChimahonListItemTokens = ChimahonThemeUiDefaults.listItemTokens(density),
    readerOverlayTokens: ChimahonReaderOverlayTokens = ChimahonThemeUiDefaults.readerOverlayTokens(
        colors = colors,
        amoled = dark && amoled,
    ),
    navigationRailTokens: ChimahonNavigationRailTokens = ChimahonThemeUiDefaults.navigationRailTokens(colors),
    browseLibraryTokens: ChimahonBrowseLibraryTokens = ChimahonThemeUiDefaults.browseLibraryTokens(colors),
    readerCanvasTokens: ChimahonReaderCanvasTokens = ChimahonThemeUiDefaults.readerCanvasTokens(
        colors = colors,
        amoled = dark && amoled,
    ),
    installMaterialTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val providedContent: @Composable () -> Unit = {
        CompositionLocalProvider(
            LocalChimahonThemePreference provides themePreference,
            LocalChimahonThemeUiColors provides colors,
            LocalChimahonThemeUiDimensions provides dimensions,
            LocalChimahonThemeUiRadii provides radii,
            LocalChimahonThemeUiElevation provides elevation,
            LocalChimahonAppBarTokens provides appBarTokens,
            LocalChimahonCardTokens provides cardTokens,
            LocalChimahonListItemTokens provides listItemTokens,
            LocalChimahonReaderOverlayTokens provides readerOverlayTokens,
            LocalChimahonNavigationRailTokens provides navigationRailTokens,
            LocalChimahonBrowseLibraryTokens provides browseLibraryTokens,
            LocalChimahonReaderCanvasTokens provides readerCanvasTokens,
            content = content,
        )
    }
    if (installMaterialTheme) {
        MaterialTheme(
            colors = if (dark) colors.toDarkMaterialColors() else colors.toLightMaterialColors(),
            content = providedContent,
        )
    } else {
        providedContent()
    }
}

@Composable
fun ProvideChimahonThemeUi(
    preference: ChimahonThemePreference,
    systemDark: Boolean = isSystemInDarkTheme(),
    dynamicColors: ChimahonDynamicThemeColors = ChimahonDynamicThemeColors(),
    installMaterialTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val resolved = ChimahonThemeUiDefaults.resolvedStyle(
        preference = preference,
        systemDark = systemDark,
        dynamicColors = dynamicColors,
    )
    ProvideChimahonThemeUi(
        dark = resolved.dark,
        amoled = resolved.amoled,
        accent = resolved.accent,
        density = resolved.density,
        themePreference = preference,
        colors = resolved.colors,
        dimensions = resolved.dimensions,
        radii = resolved.radii,
        elevation = resolved.elevation,
        appBarTokens = resolved.appBarTokens,
        cardTokens = resolved.cardTokens,
        listItemTokens = resolved.listItemTokens,
        readerOverlayTokens = resolved.readerOverlayTokens,
        navigationRailTokens = resolved.navigationRailTokens,
        browseLibraryTokens = resolved.browseLibraryTokens,
        readerCanvasTokens = resolved.readerCanvasTokens,
        installMaterialTheme = installMaterialTheme,
        content = content,
    )
}

fun ChimahonThemeUiColors.toLightMaterialColors() = lightColors(
    primary = primary,
    primaryVariant = primaryContainer,
    secondary = secondary,
    secondaryVariant = secondaryContainer,
    background = background,
    surface = surface,
    error = error,
    onPrimary = onPrimary,
    onSecondary = onSecondary,
    onBackground = onSurface,
    onSurface = onSurface,
    onError = onError,
)

fun ChimahonThemeUiColors.toDarkMaterialColors() = darkColors(
    primary = primary,
    primaryVariant = primaryContainer,
    secondary = secondary,
    secondaryVariant = secondaryContainer,
    background = background,
    surface = surface,
    error = error,
    onPrimary = onPrimary,
    onSecondary = onSecondary,
    onBackground = onSurface,
    onSurface = onSurface,
    onError = onError,
)
