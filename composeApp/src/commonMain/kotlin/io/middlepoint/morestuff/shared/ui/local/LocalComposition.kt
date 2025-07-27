package io.middlepoint.morestuff.shared.ui.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalUserInteractionEnabled = compositionLocalOf<Boolean> {
  error("User Interaction enabled not provided!")
}

@Composable
fun ProvideUserInteractionEnabled(enabled: Boolean, content: @Composable () -> Unit) {
  CompositionLocalProvider(LocalUserInteractionEnabled provides enabled, content = content)
}