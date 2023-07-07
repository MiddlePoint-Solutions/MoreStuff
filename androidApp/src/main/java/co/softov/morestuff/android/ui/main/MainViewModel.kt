package co.softov.morestuff.android.ui.main

import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.redux.middleware.MessageAction
import co.softov.morestuff.android.domain.redux.store.OnResumeAction

class MainViewModel : NoStateViewModel() {

    fun onResume() {
        store.dispatch(OnResumeAction)
    }

    fun shareTextToTask(taskId: Long, content: String) {
        store.dispatch(MessageAction.CreateUserTaskMessageAction(taskId, content))
    }

}