package co.softov.morestuff.android.ui.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import co.softov.morestuff.android.domain.nav.Screen
import com.arkivanov.decompose.router.stack.StackNavigation

val LocalAppNavigation = staticCompositionLocalOf<StackNavigation<Screen>> {
    error("Navigation not provided!")
}

@Composable
fun ProvideAppNavigation(navigator: StackNavigation<Screen>, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppNavigation provides navigator, content = content)
}