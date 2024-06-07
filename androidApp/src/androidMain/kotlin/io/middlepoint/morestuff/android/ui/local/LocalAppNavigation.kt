package io.middlepoint.morestuff.android.ui.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.github.xxfast.decompose.router.stack.Router

val LocalAppRouter = compositionLocalOf<Router<Screen>> {
    error("Navigation not provided!")
}

@Composable
fun ProvideAppRouter(router: Router<Screen>, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppRouter provides router, content = content)
}