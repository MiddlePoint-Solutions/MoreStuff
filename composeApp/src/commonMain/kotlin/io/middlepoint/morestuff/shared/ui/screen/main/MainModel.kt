package io.middlepoint.morestuff.shared.ui.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.middlepoint.morestuff.shared.createKmpFile
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.action.MessageAction
import io.middlepoint.morestuff.shared.domain.redux.action.SettingAction
import io.middlepoint.morestuff.shared.domain.redux.state.isAuthenticated
import io.middlepoint.morestuff.shared.domain.redux.state.isReady
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@Composable
fun mainModel(
  initialState: MainState,
  events: Flow<MainEvent>,
  store: AppStore = koinInject()
): MainState {

  var currentState by remember { mutableStateOf(initialState) }

  LaunchedEffect(Unit) {
    store.state.collectLatest {
      currentState = MainState(
        ready = it.isReady(),
        theme = it.settings.appTheme,
        isAuthenticated = it.isAuthenticated()
      )
    }
  }

  LaunchedEffect(Unit) {
    events.collect { event ->
      when (event) {

        MainEvent.OnBoardingComplete -> {
          store.dispatch(SettingAction.OnBoardingComplete)
        }

        is MainEvent.ShareContent -> with(event.content) {
          when (this) {
            is Shareable.Image -> {
              store.dispatch(
                MessageAction.CreateFileMessageAction(
                  event.taskId,
                  createKmpFile(uri),
                  message
                )
              )
            }

            is Shareable.Pdf -> {
              store.dispatch(
                MessageAction.CreatePDFMessageAction(
                  event.taskId,
                  createKmpFile(uri),
                  message
                )
              )
            }

            is Shareable.Text -> {
              store.dispatch(
                MessageAction.CreateUserTaskMessageAction(
                  event.taskId,
                  message
                )
              )
            }
          }
        }
      }
    }
  }

  return currentState
}