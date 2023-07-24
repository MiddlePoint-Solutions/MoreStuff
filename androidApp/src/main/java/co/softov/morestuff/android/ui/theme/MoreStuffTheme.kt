package co.softov.morestuff.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import co.softov.morestuff.android.domain.enums.AppTheme


private val lightColors = lightColorScheme(
    primary = primaryLightTheme,
    primaryContainer = primaryContainerLightTheme,
    onPrimary = Color.Black,
    secondary = Orange500,
    secondaryContainer = secondaryContainerLightTheme,
    onSecondary = Color.Black,
    error = Red200,
)

val ColorScheme.userChatItem: Color
    get() = Indigo400

val ColorScheme.appChatItem: Color
    get() = Indigo200

val darkBlue = Color(30, 31, 45)
val darkSurface = Color(39, 40, 53)

private val darkColors = darkColorScheme(
    primary = Color(73, 69, 79),
    primaryContainer = BlueGray900,
    onPrimary = Color.White,
    secondary = Orange500,
    secondaryContainer = Orange600,
    onSecondary = Color.White,
    error = Red200,
    background = darkBlue,
    surface = darkSurface
)

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    errorContainer = md_theme_light_errorContainer,
    onErrorContainer = md_theme_light_onErrorContainer,
    outline = md_theme_light_outline,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    inverseSurface = md_theme_light_inverseSurface,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    errorContainer = md_theme_dark_errorContainer,
    onErrorContainer = md_theme_dark_onErrorContainer,
    outline = md_theme_dark_outline,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    inverseSurface = md_theme_dark_inverseSurface,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

@Composable
fun MoreStuffTheme(
    content: @Composable () -> Unit
) {
    val darkTheme = isDarkTheme()

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
    ) {
        CustomSelectionColor(darkTheme) {
            content()
        }
    }
}

@Composable
fun MoreStuffSettingTheme(
    content: @Composable () -> Unit
) {
    val darkTheme = isDarkTheme()

    MaterialTheme(
        colorScheme = if (darkTheme) darkColors.copy(surface = darkBlue) else lightColors,
        typography = Typography(),
    ) {
        CustomSelectionColor(darkTheme) {
            content()
        }
    }
}

@Composable
fun isDarkTheme(): Boolean {
    val darkTheme = when (LocalTheme.current) {
        AppTheme.System -> isSystemInDarkTheme()
        AppTheme.Light -> false
        AppTheme.Dark -> true
    }
    return darkTheme
}


@Composable
fun CustomSelectionColor(darkTheme: Boolean, content: @Composable () -> Unit) {
    val customSelectionColor = if (!darkTheme) {
        BlueDark
    } else {
        BlueLight
    }
    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = customSelectionColor,
            backgroundColor = customSelectionColor.copy(alpha = 0.5f)
        )
    ) {
        content()
    }
}

val LocalTheme = compositionLocalOf { AppTheme.System }

@Composable
fun ProvideAppTheme(theme: AppTheme, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalTheme provides theme, content = content)
}
