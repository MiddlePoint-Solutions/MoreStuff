package io.middlepoint.morestuff.shared.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import io.github.jan.supabase.SupabaseClient
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.middlepoint.morestuff.shared.domain.nav.SignIn
import io.middlepoint.morestuff.shared.ui.local.ProvideAppTheme
import io.middlepoint.morestuff.shared.ui.screen.main.MainContent
import io.middlepoint.morestuff.shared.ui.screen.main.MainEvent
import io.middlepoint.morestuff.shared.ui.screen.main.MainViewModel
import io.middlepoint.morestuff.shared.ui.theme.MoreStuffTheme
import io.middlepoint.morestuff.shared.ui.utils.handleDeeplinkFragment
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App(
  screen: Screen? = null,
  accessToken: String? = null // TODO: this is super ugly
) {
  KoinContext {
    val navController = rememberNavController()
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

          if (model.ready) {
            MainContent(
              navController = navController,
              shareContent = { taskId, content ->
                viewModel.take(MainEvent.ShareContent(taskId, content))
              }
            )

            if (!model.isAuthenticated) {
              navController.navigate(SignIn(model.showOldUserMessage))
            }
          }
        }
      }
    }

    LaunchedEffect(screen) {
      if (screen != null) {
        navController.navigate(screen.toString())
      }
    }

  }
}