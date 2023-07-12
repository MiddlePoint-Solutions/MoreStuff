package co.softov.morestuff.android.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.MessageAction
import co.softov.morestuff.android.domain.redux.store.OnResumeAction

class MainViewModel : NoStateViewModel() {

    var model by mutableStateOf(MainModel())
        private set

    init {
        loadData()
    }

    override fun onAppStateChange(state: AppState) {
        model = model.copy(showOnBoarding = state.settingState.isFirstTime)
    }

    fun onResume() {
        store.dispatch(OnResumeAction)
    }

    fun shareTextToTask(taskId: Long, content: String) {
        store.dispatch(MessageAction.CreateUserTaskMessageAction(taskId, content))
    }

}