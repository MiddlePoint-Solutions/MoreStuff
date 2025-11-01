package io.middlepoint.morestuff.shared.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import io.github.jan.supabase.SupabaseClient
import io.middlepoint.morestuff.shared.domain.navigation.Home
import io.middlepoint.morestuff.shared.domain.navigation.AppRoute
import io.middlepoint.morestuff.shared.domain.navigation.SignIn
import io.middlepoint.morestuff.shared.ui.local.ProvideAppTheme
import io.middlepoint.morestuff.shared.ui.screen.main.MoreStuffNavHost
import io.middlepoint.morestuff.shared.ui.screen.main.MainEvent
import io.middlepoint.morestuff.shared.ui.screen.main.MainViewModel
import io.middlepoint.morestuff.shared.ui.theme.MoreStuffTheme
import io.middlepoint.morestuff.shared.ui.utils.handleDeeplinkFragment
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.start
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun App(
  appRoute: AppRoute? = null,
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

  val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()

  ProvideAppTheme(model.theme) {
    MoreStuffTheme {

      var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.CHAT) }
      NavigationSuiteScaffold(
        navigationSuiteItems = {
          AppDestinations.entries.forEach {
            item(
              icon = {
                Icon(
                  it.icon,
                  contentDescription = stringResource(it.contentDescription)
                )
              },
              label = { Text(stringResource(it.label)) },
              selected = it == currentDestination,
              onClick = { currentDestination = it }
            )
          }
        }
      ) {
        // TODO: Destination content.

        Box(
          modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
        ) {

          // TODO: this needs to be replaced with NavBackStack?
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

            MoreStuffNavHost(
              startDestination = startDestination,
              shareContent = { taskId, content ->
                viewModel.take(MainEvent.ShareContent(taskId, content))
              }
            )

            LaunchedEffect(appRoute) {
              if (model.isAuthenticated && appRoute != null) {
                navController.navigate(appRoute)
              }
            }
          }
        }
      }
    }
  }
}

enum class AppDestinations(
  val label: StringResource,
  val icon: ImageVector,
  val contentDescription: StringResource
) {
  CHAT(Res.string.start, Icons.Default.Home, Res.string.start),
  TASKS(Res.string.start, Icons.AutoMirrored.Filled.List, Res.string.start),
}
