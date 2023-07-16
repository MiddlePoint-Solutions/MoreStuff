package co.softov.morestuff.android.ui

import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.MessageAction
import co.softov.morestuff.android.domain.redux.store.OnResumeAction
import co.softov.morestuff.android.ui.main.MainModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : NoStateViewModel() {

    val model: MutableStateFlow<MainModel> = MutableStateFlow(MainModel())

    init {
        loadData()
    }

    override fun onAppStateChange(state: AppState) {
        model.update {
            it.copy(
                showOnBoarding = state.settingState.isFirstTime,
                theme = state.settingState.appTheme
            )
        }
    }

    fun onResume() {
        store.dispatch(OnResumeAction)
    }

    fun shareTextToTask(taskId: Long, content: String) {
        store.dispatch(MessageAction.CreateUserTaskMessageAction(taskId, content))
    }

}