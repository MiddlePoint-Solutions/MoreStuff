package io.middlepoint.morestuff.shared.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.navigate
import io.github.xxfast.decompose.router.stack.Router
import io.github.xxfast.decompose.router.stack.rememberRouter
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.middlepoint.morestuff.shared.ui.local.ProvideAppRouter
import io.middlepoint.morestuff.shared.ui.local.ProvideAppTheme
import io.middlepoint.morestuff.shared.ui.screen.main.MainContent
import io.middlepoint.morestuff.shared.ui.screen.main.MainEvent
import io.middlepoint.morestuff.shared.ui.screen.main.MainViewModel
import io.middlepoint.morestuff.shared.ui.screen.settings.koinInjectOnRoute
import io.middlepoint.morestuff.shared.ui.theme.MoreStuffTheme
import io.middlepoint.morestuff.shared.ui.theme.surfaceContainerElevation
import org.koin.compose.KoinContext

@Composable
fun App(
  screen: Screen? = null
) {
  KoinContext {
    val router: Router<Screen> = rememberRouter(Screen::class) { listOf(Screen.Home) }
    val viewModel = koinInjectOnRoute(MainViewModel::class)
    val model by viewModel.models.collectAsState()

    ProvideAppTheme(model.theme) {
      MoreStuffTheme {
        ProvideAppRouter(router) {
          Box(
            modifier = Modifier
              .background(MaterialTheme.colorScheme.surfaceContainerElevation)
              .windowInsetsPadding(WindowInsets.safeDrawing.exclude(WindowInsets.ime))
          ) {
            if (model.ready) {
//              val initialScreen = if (model.showOnBoarding) {
//                Screen.OnBoarding
//              } else {
//                screen
//              }

              MainContent(
                shareContent = { taskId, content ->
                  viewModel.take(
                    MainEvent.ShareContent(taskId, content)
                  )
                },
                onBoardingComplete = {
                  viewModel.take(MainEvent.OnBoardingComplete)
                }
              )
            }
          }
        }
      }
    }

    LaunchedEffect(screen) {
      if (screen != null) {
        router.navigate {
          listOf(Screen.Home, screen)
        }
      }
    }
  }
}