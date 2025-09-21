import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import au.edu.curtin.madassignment2.ui.theme.*

private val LightColorScheme = lightColorScheme(
    primary = LeatherBrown,
    onPrimary = Parchment,
    primaryContainer = VintageRed,
    onPrimaryContainer = Parchment,

    secondary = AmberGlow,
    onSecondary = InkBlack,

    background = Parchment,
    onBackground = InkBlack,

    surface = Parchment,
    onSurface = InkBlack,

    surfaceVariant = AntiqueGray,
    onSurfaceVariant = Parchment
)

private val DarkColorScheme = darkColorScheme(
    primary = AmberGlow,
    onPrimary = InkBlack,
    primaryContainer = VintageRed,
    onPrimaryContainer = Parchment,

    secondary = LeatherBrown,
    onSecondary = Parchment,

    background = Color(0xFF2B2B2B), // Deep ink background
    onBackground = Parchment,

    surface = Color(0xFF3E3E3E),
    onSurface = Parchment,

    surfaceVariant = LeatherBrown,
    onSurfaceVariant = Parchment
)

@Composable
fun PocketLibraryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}