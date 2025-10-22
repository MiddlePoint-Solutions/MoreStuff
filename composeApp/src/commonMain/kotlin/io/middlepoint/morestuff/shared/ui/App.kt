package io.middlepoint.morestuff.shared.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import io.github.jan.supabase.SupabaseClient
import io.middlepoint.morestuff.shared.domain.nav.Home
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.middlepoint.morestuff.shared.domain.nav.SignIn
import io.middlepoint.morestuff.shared.ui.local.ProvideAppTheme
import io.middlepoint.morestuff.shared.ui.screen.main.MainContent
import io.middlepoint.morestuff.shared.ui.screen.main.MainEvent
import io.middlepoint.morestuff.shared.ui.screen.main.MainViewModel
import io.middlepoint.morestuff.shared.ui.theme.MoreStuffTheme
import io.middlepoint.morestuff.shared.ui.utils.handleDeeplinkFragment
import org.koin.compose.koinInject

@Composable
fun App(
  screen: Screen? = null,
  windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo(true).windowSizeClass,
  accessToken: String? = null, // TODO: this is super ugly
) {

  val viewModel = koinInject<MainViewModel>()
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
      Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
      ) {
        val navController = rememberNavController()

        if (model.ready) {

          val startDestination by remember(model) {
            derivedStateOf {
              if (model.isAuthenticated) {
                Home
              } else {
                SignIn
              }
            }
          }

          MainContent(
            navController = navController,
            startDestination = startDestination,
            shareContent = { taskId, content ->
              viewModel.take(MainEvent.ShareContent(taskId, content))
            }
          )

          LaunchedEffect(screen) {
            if (model.isAuthenticated && screen != null) {
              navController.navigate(screen)
            }
          }
        }
      }
    }
  }
}
