package co.softov.morestuff.android.ui.main

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.nav.Shareable
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.MessageAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.redux.store.OnResumeAction
import co.softov.morestuff.android.domain.usecase.settings.CheckFirstTimeUseCase
import co.softov.morestuff.android.domain.usecase.settings.GetAppThemeUseCase
import co.softov.morestuff.android.ui.main.MainStates.Idle
import co.softov.morestuff.android.ui.main.MainStates.Ready
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    getAppThemeUseCase: GetAppThemeUseCase,
    checkFirstTimeUseCase: CheckFirstTimeUseCase,
) : NoStateViewModel() {

    var appTheme by mutableStateOf(getAppThemeUseCase())
        private set

    val states: StateFlow<MainStates> = moleculeFlow(RecompositionMode.Immediate) {
        val store by store.state.collectAsState()
        Ready(
            showOnBoarding = checkFirstTimeUseCase(),
            theme = store.settings.appTheme
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Idle)

    init {
        loadData()
    }

    override fun onAppStateChange(state: AppState) {
        appTheme = state.settings.appTheme
    }

    fun onResume() {
        dispatchAppStoreAction(OnResumeAction)
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
                    MessageAction.CreatePdfMessageAction(
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
