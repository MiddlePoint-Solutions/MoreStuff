package co.softov.morestuff.android.ui.main

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.model.Shareable
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.MessageAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.usecase.settings.CheckFirstTimeUseCase
import co.softov.morestuff.android.domain.usecase.settings.GetAppThemeUseCase
import co.softov.morestuff.android.ui.main.MainStates.Loading
import co.softov.morestuff.android.ui.main.MainStates.Ready
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

class MainViewModel(
    getAppThemeUseCase: GetAppThemeUseCase,
    checkFirstTimeUseCase: CheckFirstTimeUseCase,
) : NoStateViewModel() {

    private val scope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main)

    var appTheme by mutableStateOf(getAppThemeUseCase())
        private set

    val states: StateFlow<MainStates> by lazy {
        moleculeFlow(mode = RecompositionMode.ContextClock) {
            var currentState by remember { mutableStateOf<MainStates>(Loading) }
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
                        content.content
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
