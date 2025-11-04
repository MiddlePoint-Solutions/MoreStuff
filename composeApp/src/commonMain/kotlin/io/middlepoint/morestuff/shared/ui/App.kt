package io.middlepoint.morestuff.shared.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.jan.supabase.SupabaseClient
import io.middlepoint.morestuff.shared.domain.navigation.Home
import io.middlepoint.morestuff.shared.domain.navigation.AppRoute
import io.middlepoint.morestuff.shared.domain.navigation.SignIn
import io.middlepoint.morestuff.shared.ui.local.ProvideAppTheme
import io.middlepoint.morestuff.shared.ui.screen.main.MoreStuffNavRoutes
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
  accessToken: String? = null, // TODO: this is ugly
) {

  val viewModel = koinInject<MainViewModel>()
  val model by viewModel.models.collectAsState()
  val supabase: SupabaseClient = koinInject()

  var currentDestination by rememberSaveable { mutableStateOf(AppDestination.CHAT) }
  val navigationSuiteState = rememberNavigationSuiteScaffoldState(
    initialValue = if (model.isAuthenticated) {
      NavigationSuiteScaffoldValue.Visible
    } else {
      NavigationSuiteScaffoldValue.Hidden
    }
  )

  LaunchedEffect(model, appRoute) {
    if (model.isAuthenticated) {
      navigationSuiteState.show()
    } else {
      navigationSuiteState.hide()
    }
  }

  // TODO: change this
  LaunchedEffect(accessToken) {
    if (!accessToken.isNullOrBlank()) {
      supabase.handleDeeplinkFragment(accessToken) { session ->
        viewModel.take(MainEvent.OnBoardingComplete)
      }
    }
  }

  ProvideAppTheme(model.theme) {
    MoreStuffTheme {

      NavigationSuiteScaffold(
        navigationSuiteItems = navigationSuiteItems(
          current = currentDestination,
          onChange = { currentDestination = it }
        ),
        state = navigationSuiteState
      ) {
        Surface(
//          modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
        ) {

          if (model.ready) {
            val startDestination by remember(model) {
              derivedStateOf {
                if (model.isAuthenticated) {
                  appRoute ?: Home
                } else {
                  SignIn
                }
              }
            }

            MoreStuffNavRoutes(
              startDestination = startDestination,
              shareContent = { taskId, content ->
                viewModel.take(MainEvent.ShareContent(taskId, content))
              }
            )
          }
        }
      }
    }
  }
}

private fun navigationSuiteItems(
  current: AppDestination,
  onChange: (AppDestination) -> Unit
): NavigationSuiteScope.() -> Unit {
  return {
    AppDestination.entries.forEach {
      item(
        icon = {
          Icon(
            it.icon,
            contentDescription = stringResource(it.contentDescription)
          )
        },
        selected = current == it,
        onClick = { onChange(it) }
      )
    }
  }
}

enum class AppDestination(
  val label: StringResource,
  val icon: ImageVector,
  val contentDescription: StringResource
) {
  CHAT(Res.string.start, Icons.Default.Home, Res.string.start),
  TASKS(Res.string.start, Icons.AutoMirrored.Filled.List, Res.string.start),
}
