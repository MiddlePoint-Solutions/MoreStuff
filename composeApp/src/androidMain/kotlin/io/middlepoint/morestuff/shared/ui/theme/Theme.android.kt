package io.middlepoint.morestuff.shared.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.ui.local.LocalTheme

@Composable
actual fun MoreStuffTheme(content: @Composable () -> Unit) {

  val isDarkTheme = when (LocalTheme.current) {
    AppTheme.System -> isSystemInDarkTheme()
    AppTheme.Light -> false
    AppTheme.Dark -> true
  }

  val colorScheme = when {
    isDarkTheme -> DarkColors
    else -> LightColors
  }

  MaterialTheme(
    colorScheme = colorScheme
  ) {
    val view = LocalView.current
    if (!view.isInEditMode) {
      LaunchedEffect(isDarkTheme) {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
      }
    }

    content()
  }
}