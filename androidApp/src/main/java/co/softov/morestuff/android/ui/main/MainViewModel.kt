package co.softov.morestuff.android.ui.main

import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.redux.middleware.MessageAction

class MainViewModel : NoStateViewModel() {

    fun shareTextToTask(taskId: Long, content: String) {
        store.dispatch(MessageAction.CreateUserTaskMessageAction(taskId, content))
    }

}