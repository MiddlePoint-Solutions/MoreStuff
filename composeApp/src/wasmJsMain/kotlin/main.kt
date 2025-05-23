import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import androidx.compose.ui.window.ComposeViewport
import io.middlepoint.morestuff.shared.ui.App
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {

  CanvasBasedWindow("MoreStuff", canvasElementId = "moreStuffCanvas") {
    App()
  }

}