import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.middlepoint.morestuff.shared.domain.service.NavigationHelper
import io.middlepoint.morestuff.shared.ui.App
import org.koin.compose.koinInject


@OptIn(ExperimentalDecomposeApi::class)
fun MainViewController(routerContext: RouterContext) = ComposeUIViewController {
  CompositionLocalProvider(LocalRouterContext provides routerContext) {
    val navigationHelper = koinInject<NavigationHelper>()
    val initialScreen = remember { mutableStateOf<Screen?>(null) }
    val accessToken = remember { mutableStateOf<String?>(null) }
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
    LaunchedEffect(Unit) {
      navigationHelper.navigation.collect { screen ->
        initialScreen.value = screen
      }
    }

    LaunchedEffect(Unit) {
      navigationHelper.code.collect { code ->
        accessToken.value = code
      }
    }


    PredictiveBackGestureOverlay(
      backDispatcher = routerContext.backHandler as BackDispatcher,
      backIcon = { progress, _ ->
        /*PredictiveBackGestureIcon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            progress = progress,
        )*/
      },
      modifier = Modifier.fillMaxSize(),
    ) {
      App(screen = initialScreen.value, accessToken = accessToken.value)
    }

  }
}






