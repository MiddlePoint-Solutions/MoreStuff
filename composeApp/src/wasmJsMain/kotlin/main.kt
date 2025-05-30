import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.defaultRouterContext
import io.middlepoint.morestuff.shared.di.initKoin
import io.middlepoint.morestuff.shared.ui.App

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  initKoin()

  val rootRouterContext: RouterContext = defaultRouterContext()
  CanvasBasedWindow("MoreStuff", canvasElementId = "MoreStuffCanvas") {
    CompositionLocalProvider(LocalRouterContext provides rootRouterContext) {
      App()
    }
  }
}