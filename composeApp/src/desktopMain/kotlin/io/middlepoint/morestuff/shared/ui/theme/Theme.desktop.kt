package io.middlepoint.morestuff.shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
    content()
  }
}