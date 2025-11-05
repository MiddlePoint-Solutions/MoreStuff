import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Tray
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

    var isVisible by remember { mutableStateOf(true) }

    Window(
      onCloseRequest = { isVisible = false },
      state = windowState,
      visible = isVisible,
      title = "MoreStuff",
      icon = TrayIcon
    ) {
      App()
    }

    if (!isVisible) {
      Tray(
        TrayIcon,
        tooltip = "Counter",
        onAction = { isVisible = true },
        menu = {
          Item("Exit", onClick = ::exitApplication)
        },
      )
    }
  }
}

object TrayIcon : Painter() {
  override val intrinsicSize = Size(256f, 256f)

  override fun DrawScope.onDraw() {
    drawOval(Color(0xFFFFA500))
  }
}
