package co.softov.morestuff.android.domain.redux

import co.softov.morestuff.android.domain.redux.state.AppSettings
import co.softov.morestuff.android.domain.redux.state.TaskState

data class AppState(
    val settings: AppSettings = AppSettings(),
    val tasks: TaskState = TaskState()
)

