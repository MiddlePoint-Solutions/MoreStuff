import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.defaultRouterContext
import io.github.xxfast.decompose.router.stack.Router
import io.github.xxfast.decompose.router.stack.rememberRouter
import io.middlepoint.morestuff.shared.di.initKoin
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.middlepoint.morestuff.shared.ui.local.ProvideAppRouter
import io.middlepoint.morestuff.shared.ui.screen.main.MainContent
import io.middlepoint.morestuff.shared.ui.screen.main.MainEvent
import org.koin.core.context.startKoin

fun main() {

    initKoin {  }

    application {
        val windowState: WindowState = rememberWindowState()
        val rootRouterContext: RouterContext = defaultRouterContext(windowState = windowState)
        Window(
            onCloseRequest = ::exitApplication,
            title = "MoreStuff",
        ) {
            CompositionLocalProvider(LocalRouterContext provides rootRouterContext) {

                val router: Router<Screen> = rememberRouter(Screen::class) { listOf(Screen.Home) }

                ProvideAppRouter(router) {

                    MainContent(
                        initialScreen = null,
                        shareContent = { taskId, content ->

                        },
                        onBoardingComplete = {
                        }
                    )

                }

            }
        }
    }
}
