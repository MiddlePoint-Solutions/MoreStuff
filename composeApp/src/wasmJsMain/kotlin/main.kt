import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import io.middlepoint.morestuff.shared.di.initKoin
import io.middlepoint.morestuff.shared.ui.App

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  initKoin()

  CanvasBasedWindow("MoreStuff", canvasElementId = "MoreStuffCanvas") {
    App()
  }
}
