package co.softov.morestuff.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = Orange300,
    onSecondary = Color.Black,
    error = Red800
)

val ColorScheme.userChatItem: Color
    get() = Indigo400

val ColorScheme.appChatItem: Color
    get() = Indigo200

private val DarkColors = darkColorScheme(
    primary = BlueGray600,
    primaryContainer = BlueGray900,
    onPrimary = Color.White,
    secondary = Orange500,
    secondaryContainer = Orange600,
    onSecondary = Color.White,
    error = Red200
)

@Composable
fun MoreStuffTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content
    )
}