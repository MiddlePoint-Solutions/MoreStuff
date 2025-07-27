import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.middlepoint.morestuff.shared.domain.service.NavigationHelper
import io.middlepoint.morestuff.shared.ui.App
import org.koin.compose.koinInject


@OptIn(ExperimentalDecomposeApi::class)
fun MainViewController() = ComposeUIViewController {
    val navigationHelper = koinInject<NavigationHelper>()
    var initialScreen by remember { mutableStateOf<Screen?>(null) }
    val accessToken = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
      navigationHelper.shareable.collect { shareable ->
        initialScreen = when (shareable) {
//          is Shareable.Image -> {
//            Screen.Share(shareable, shareable.uri)
//          }
//
//          is Shareable.Pdf -> {
//            Screen.Share(shareable, shareable.uri)
//          }

          is Shareable.Text -> {
            Screen.Share(shareable, shareable.message)
          }

          else -> {
            null
          }
        }
      }
    }

    LaunchedEffect(Unit) {
      navigationHelper.navigation.collect { screen ->
        initialScreen = screen
      }
    }

    LaunchedEffect(Unit) {
      navigationHelper.code.collect { code ->
        accessToken.value = code
      }
    }

    App(screen = initialScreen, accessToken = accessToken.value)
}
