package co.softov.morestuff.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColors(
    primary = Color.White,
    primaryVariant = Gray100,
    onPrimary = Color.Black,
    secondary = Orange300,
    secondaryVariant = Orange500,
    onSecondary = Color.Black,
    error = Red800
)

val Colors.userChatItem: Color
    get() = Indigo400

val Colors.appChatItem: Color
    get() = Indigo200

private val DarkColors = darkColors(
    primary = BlueGray600,
    primaryVariant = BlueGray900,
    onPrimary = Color.White,
    secondary = Orange500,
    secondaryVariant = Orange600,
    onSecondary = Color.White,
    error = Red200
)

@Composable
fun MoreStuffTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        typography = MoreStuffTypography,
        content = content
    )
}