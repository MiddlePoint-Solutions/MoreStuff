import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.middlepoint.morestuff.shared.domain.service.NavigationHelper
import io.middlepoint.morestuff.shared.ui.App
import org.koin.compose.koinInject


fun MainViewController(routerContext: RouterContext) = ComposeUIViewController {
    CompositionLocalProvider(LocalRouterContext provides routerContext) {

        val navigationHelper = koinInject<NavigationHelper>()
        val initialScreen = remember { mutableStateOf<Screen?>(null) }

        LaunchedEffect(Unit) {
            navigationHelper.shareable.collect { shareable ->
                initialScreen.value = when (shareable) {
                    is Shareable.Image -> {
                        Screen.Share(shareable, shareable.uri)
                    }

                    is Shareable.Pdf -> {
                        Screen.Share(shareable, shareable.uri)
                    }

                    is Shareable.Text -> {
                        Screen.Share(shareable, shareable.message)
                    }

                    else -> {
                        null
                    }
                }
            }
        }
        App(screen = initialScreen.value)
    }
}






