package au.edu.curtin.madassignment2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    // Primary colors - main brand colors
    primary = LeatherBrown,
    onPrimary = Parchment,
    primaryContainer = VintageRed,
    onPrimaryContainer = Parchment,

    // Secondary colors - supporting colors
    secondary = AmberGlow,
    onSecondary = InkBlack,
    secondaryContainer = BookSpine,
    onSecondaryContainer = CreamyWhite,

    // Tertiary colors - accent colors
    tertiary = DustyBlue,
    onTertiary = Parchment,
    tertiaryContainer = GoldLeaf,
    onTertiaryContainer = InkBlack,

    // Error colors
    error = VintageRed,
    onError = Parchment,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    // Background colors
    background = Parchment,
    onBackground = InkBlack,

    // Surface colors
    surface = Parchment,
    onSurface = InkBlack,
    surfaceVariant = AntiqueGray,
    onSurfaceVariant = Parchment,
    surfaceTint = LeatherBrown,

    // Inverse colors
    inverseSurface = InkBlack,
    inverseOnSurface = Parchment,
    inversePrimary = AmberGlow,

    // Outline colors
    outline = AntiqueGray,
    outlineVariant = Color(0xFFCAC4D0),

    // Other surface colors
    surfaceBright = CreamyWhite,
    surfaceDim = Color(0xFFDDD7C9),
    surfaceContainer = Color(0xFFF3EDF7),
    surfaceContainerHigh = Color(0xFFECE6F0),
    surfaceContainerHighest = Color(0xFFE6E0E9),
    surfaceContainerLow = Color(0xFFF9F3FD),
    surfaceContainerLowest = Color(0xFFFFFFFF)
)

private val DarkColorScheme = darkColorScheme(
    // Primary colors - adjusted for dark theme
    primary = AmberGlow,
    onPrimary = InkBlack,
    primaryContainer = VintageRed,
    onPrimaryContainer = Parchment,

    // Secondary colors
    secondary = LeatherBrown,
    onSecondary = Parchment,
    secondaryContainer = DeepMahogany,
    onSecondaryContainer = AmberGlow,

    // Tertiary colors
    tertiary = WornCopper,
    onTertiary = InkBlack,
    tertiaryContainer = DustyBlue,
    onTertiaryContainer = Parchment,

    // Error colors
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // Background colors - deep ink background
    background = Color(0xFF1A1A1A),
    onBackground = Parchment,

    // Surface colors
    surface = Color(0xFF2B2B2B),
    onSurface = Parchment,
    surfaceVariant = LeatherBrown,
    onSurfaceVariant = Parchment,
    surfaceTint = AmberGlow,

    // Inverse colors
    inverseSurface = Parchment,
    inverseOnSurface = InkBlack,
    inversePrimary = LeatherBrown,

    // Outline colors
    outline = AntiqueGray,
    outlineVariant = Color(0xFF49454F),

    // Other surface colors
    surfaceBright = Color(0xFF3B3B3B),
    surfaceDim = Color(0xFF1A1A1A),
    surfaceContainer = Color(0xFF211F26),
    surfaceContainerHigh = Color(0xFF2B2930),
    surfaceContainerHighest = Color(0xFF36343B),
    surfaceContainerLow = Color(0xFF1D1B20),
    surfaceContainerLowest = Color(0xFF0F0D13)
)

@Composable
fun PocketLibraryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to always use our custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
            val context = androidx.compose.ui.platform.LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}