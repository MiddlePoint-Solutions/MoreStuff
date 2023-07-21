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

@Composable
fun MoreStuffTheme(
    content: @Composable () -> Unit
) {
    val darkTheme = when (LocalTheme.current) {
        AppTheme.System -> isSystemInDarkTheme()
        AppTheme.Light -> false
        AppTheme.Dark -> true
    }

    MaterialTheme(
        colorScheme = if (darkTheme) darkColors else lightColors,
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
    val darkTheme = when (LocalTheme.current) {
        AppTheme.System -> isSystemInDarkTheme()
        AppTheme.Light -> false
        AppTheme.Dark -> true
    }

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
