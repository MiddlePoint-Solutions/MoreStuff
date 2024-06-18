import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.middlepoint.morestuff.shared.ui.App

fun MainViewController(routerContext: RouterContext) = ComposeUIViewController {
  CompositionLocalProvider(LocalRouterContext provides routerContext) {
    App()
  }
}
