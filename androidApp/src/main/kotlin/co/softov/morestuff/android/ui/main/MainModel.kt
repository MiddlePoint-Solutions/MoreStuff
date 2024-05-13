package co.softov.morestuff.android.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.domain.model.Shareable
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.MessageAction
import co.softov.morestuff.android.domain.redux.state.SettingAction
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
                ready = true,
                theme = it.settings.appTheme,
                showOnBoarding = it.settings.isFirstTime
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
                                MessageAction.CreateImageMessageAction(
                                    event.taskId,
                                    uris,
                                    message
                                )
                            )
                        }

                        is Shareable.Pdf -> {
                            store.dispatch(
                                MessageAction.CreatePDFMessageAction(
                                    event.taskId,
                                    uris,
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