import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.vinceglb.filekit.FileKit
import io.middlepoint.morestuff.shared.di.initKoin
import io.middlepoint.morestuff.shared.ui.App

fun main() {
  initKoin()
  FileKit.init(appId = "MoreStuff")

  application {
    val windowState: WindowState = rememberWindowState(
      position = WindowPosition.Aligned(Alignment.Center),
      width = 1200.dp,
      height = 700.dp,
    )

    Window(
      onCloseRequest = ::exitApplication,
      state = windowState,
      title = "MoreStuff",
    ) {
      App()
    }
  }
}
