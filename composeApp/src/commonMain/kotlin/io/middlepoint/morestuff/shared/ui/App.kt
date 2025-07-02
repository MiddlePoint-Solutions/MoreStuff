package io.middlepoint.morestuff.shared.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.navigate
import io.github.jan.supabase.SupabaseClient
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
import io.middlepoint.morestuff.shared.ui.utils.handleDeeplinkFragment
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App(screen: Screen? = null, accessToken: String? = null) {
  KoinContext {
    val router: Router<Screen> = rememberRouter { listOf(Screen.Home) }
    val viewModel = koinInjectOnRoute(MainViewModel::class)
    val model by viewModel.models.collectAsState()
    val supabase: SupabaseClient = koinInject()

    LaunchedEffect(accessToken) {
      if (!accessToken.isNullOrBlank()) {
        supabase.handleDeeplinkFragment(accessToken) { session ->
          viewModel.take(MainEvent.OnBoardingComplete)
        }
      }
    }

    ProvideAppTheme(model.theme) {
      MoreStuffTheme {
        ProvideAppRouter(router) {
          Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
          ) {
            if (model.ready) {
              if (!model.isAuthenticated) {
                router.navigate { listOf(Screen.SignIn) }
              }
              MainContent(
                shareContent = { taskId, content ->
                  viewModel.take(MainEvent.ShareContent(taskId, content))
                }
              )
            }
          }
        }
      }
    }
    LaunchedEffect(screen) {
      if (screen != null) {
        if (screen == Screen.Home) {
          router.navigate { listOf(screen) }
        } else {
          router.navigate { listOf(Screen.Home, screen) }
        }
      }
    }


    /*    LaunchedEffect(screen) {
          if (screen != null) {
            router.navigate {
              listOf(Screen.Home, screen)
            }
          }
        }*/
  }
}