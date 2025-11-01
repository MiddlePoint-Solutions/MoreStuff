import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.ExperimentalDecomposeApi
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.navigation.Import
import io.middlepoint.morestuff.shared.domain.navigation.AppRoute
import io.middlepoint.morestuff.shared.domain.service.NavigationHelper
import io.middlepoint.morestuff.shared.ui.App
import org.koin.compose.koinInject


@OptIn(ExperimentalDecomposeApi::class)
fun MainViewController() = ComposeUIViewController {
  val navigationHelper = koinInject<NavigationHelper>()
  var initialAppRoute by remember { mutableStateOf<AppRoute?>(null) }
  val accessToken = remember { mutableStateOf<String?>(null) }

  LaunchedEffect(Unit) {
    navigationHelper.shareable.collect { shareable ->
      initialAppRoute = when (shareable) {
//          is Shareable.Image -> {
//            Screen.Share(shareable, shareable.uri)
//          }
//
//          is Shareable.Pdf -> {
//            Screen.Share(shareable, shareable.uri)
//          }

        is Shareable.Text -> {
          Import.Text(shareable.message)
        }

        else -> {
          null
        }
      }
    }
  }

  LaunchedEffect(Unit) {
    navigationHelper.navigation.collect { screen ->
      initialAppRoute = screen
    }
  }

  LaunchedEffect(Unit) {
    navigationHelper.code.collect { code ->
      accessToken.value = code
    }
  }

  App(appRoute = initialAppRoute, accessToken = accessToken.value)
}
