package io.middlepoint.morestuff.android.ui.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import io.middlepoint.morestuff.shared.domain.enums.AppTheme

val LocalTheme = compositionLocalOf { AppTheme.System }

@Composable
fun ProvideAppTheme(theme: AppTheme, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalTheme provides theme, content = content)
}