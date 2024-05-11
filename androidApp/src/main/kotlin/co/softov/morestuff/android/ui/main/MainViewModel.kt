package co.softov.morestuff.android.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import co.softov.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.model.Shareable
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.MessageAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.usecase.settings.CheckFirstTimeUseCase
import co.softov.morestuff.android.domain.usecase.settings.GetAppThemeUseCase
import co.softov.morestuff.android.ui.main.MainState.Loading
import co.softov.morestuff.android.ui.main.MainState.Ready
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import org.koin.compose.koinInject
import timber.log.Timber

class MainViewModel2 : MoleculeViewModel<MainEvent, MainState>() {

    override val initialState: MainState = Loading

    @Composable
    override fun models(events: Flow<MainEvent>): MainState {
        return mainModel(initialState, events)
    }
}

@Composable
fun mainModel(
    initialState: MainState,
    events: Flow<MainEvent>,
    store: AppStore = koinInject(),
    getAppThemeUseCase: GetAppThemeUseCase = koinInject(),
    checkFirstTimeUseCase: CheckFirstTimeUseCase = koinInject(),
): MainState {

    var appTheme by remember { mutableStateOf(getAppThemeUseCase()) }
    var isFirstTime by remember { mutableStateOf(checkFirstTimeUseCase()) }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {

                MainEvent.OnBoardingComplete -> {
                    store.dispatch(SettingAction.OnBoardingComplete)
                }

                is MainEvent.ShareContent -> with(event) {
                    when (content) {
                        is Shareable.Image -> {
                            store.dispatch(
                                MessageAction.CreateImageMessageAction(
                                    taskId,
                                    content.uris,
                                    content.message
                                )
                            )
                        }

                        is Shareable.Pdf -> {
                            store.dispatch(
                                MessageAction.CreatePDFMessageAction(
                                    taskId,
                                    content.uris,
                                    content.message
                                )
                            )
                        }

                        is Shareable.Text -> {
                            store.dispatch(
                                MessageAction.CreateUserTaskMessageAction(
                                    taskId,
                                    content.message
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        store.state.collectLatest {
            appTheme = it.settings.appTheme
            isFirstTime = it.settings.isFirstTime
        }
    }

    return Ready(
        theme = appTheme,
        showOnBoarding = isFirstTime,
    )
}

class MainViewModel(
    getAppThemeUseCase: GetAppThemeUseCase,
    checkFirstTimeUseCase: CheckFirstTimeUseCase,
) : NoStateViewModel() {

    private val scope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main)

    var appTheme by mutableStateOf(getAppThemeUseCase())
        private set

    val states: StateFlow<MainState> by lazy {
        moleculeFlow(mode = RecompositionMode.ContextClock) {
            var currentState by remember { mutableStateOf<MainState>(Loading) }
            LaunchedEffect(Unit) {
                val showOnBoarding = checkFirstTimeUseCase()
                store.state.collect {
                    currentState = Ready(
                        showOnBoarding = showOnBoarding,
                        theme = it.settings.appTheme
                    )
                }
            }
            currentState
        }.onEach { Timber.d("Main State: $it") }
            .stateIn(scope, SharingStarted.Lazily, Loading)
    }

    init {
        loadData()
    }

    override fun onAppStateChange(state: AppState) {
        appTheme = state.settings.appTheme
    }

    fun shareContentToTask(taskId: Long, content: Shareable) {
        when (content) {
            is Shareable.Text -> {
                dispatchAppStoreAction(
                    MessageAction.CreateUserTaskMessageAction(
                        taskId,
                        content.message
                    )
                )
            }

            is Shareable.Image -> {
                dispatchAppStoreAction(
                    MessageAction.CreateImageMessageAction(
                        taskId,
                        content.uris,
                        content.message
                    )
                )
            }

            is Shareable.Pdf -> {
                dispatchAppStoreAction(
                    MessageAction.CreatePDFMessageAction(
                        taskId,
                        content.uris,
                        content.message
                    )
                )
            }
        }
    }

    fun onBoardingCompleted() {
        dispatchAppStoreAction(SettingAction.OnBoardingComplete)
    }


    fun sendHintTask() {
        dispatchAppStoreAction(TaskAction.CreateHintTask)
    }

}
