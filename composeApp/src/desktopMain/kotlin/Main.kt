import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.vinceglb.filekit.FileKit
import io.middlepoint.morestuff.shared.di.initKoin
import io.middlepoint.morestuff.shared.ui.App
import io.middlepoint.morestuff.shared.ui.theme.DarkColors
import io.middlepoint.morestuff.shared.ui.theme.LightColors
import io.middlepoint.morestuff.shared.ui.theme.MoreStuffTheme
import io.middlepoint.morestuff.shared.ui.theme.md_theme_dark_onPrimary
import io.middlepoint.morestuff.shared.ui.theme.md_theme_light_onPrimary
import org.jetbrains.jewel.foundation.DisabledAppearanceValues
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.dark
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.light
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls

fun main() {

  initKoin()
  FileKit.init(appId = "MoreStuff")

  application {

    val isDark = isSystemInDarkTheme()

    // Pick Jewel theme based on system theme
    val intUiThemeDefinition = remember(isDark) {
      if (isDark) {
        JewelTheme.darkThemeDefinition(
          disabledAppearanceValues = DisabledAppearanceValues.dark()
        )
      } else {
        JewelTheme.lightThemeDefinition(
          disabledAppearanceValues = DisabledAppearanceValues.light()
        )
      }
    }

    val appTitleColor = remember(isDark) {
      if (isDark) {
        DarkColors.onSurface
      } else {
        LightColors.onSurface
      }
    }

    val windowState: WindowState = rememberWindowState(
      position = WindowPosition.Aligned(Alignment.Center),
      width = 1200.dp,
      height = 700.dp,
    )

    var isVisible by remember { mutableStateOf(true) }

    IntUiTheme(
      theme = intUiThemeDefinition,
      styling = ComponentStyling.default().decoratedWindow()
    ) {

      DecoratedWindow(
        onCloseRequest = { isVisible = false },
        state = windowState,
        visible = isVisible,
        icon = TrayIcon
      ) {

        TitleBar(Modifier.newFullscreenControls()) {
          Text(
            "MoreStuff",
            style = MaterialTheme.typography.titleMedium.copy(
              color = appTitleColor
            )
          )
        }

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
}

object TrayIcon : Painter() {
  override val intrinsicSize = Size(256f, 256f)

  override fun DrawScope.onDraw() {
    drawOval(Color(0xFFFFA500))
  }
}
