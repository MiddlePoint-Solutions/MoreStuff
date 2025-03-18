package io.middlepoint.morestuff.shared.ui.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import io.github.xxfast.decompose.router.stack.Router
import io.middlepoint.morestuff.shared.domain.nav.Screen

val LocalUserInteractionEnabled = compositionLocalOf<Boolean> {
  error("User Interaction enabled not provided!")
}

@Composable
fun ProvideUserInteractionEnabled(enabled: Boolean, content: @Composable () -> Unit) {
  CompositionLocalProvider(LocalUserInteractionEnabled provides enabled, content = content)
}