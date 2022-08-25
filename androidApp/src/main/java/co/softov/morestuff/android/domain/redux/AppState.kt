package co.softov.morestuff.android.domain.redux

import co.softov.morestuff.android.domain.redux.state.PriorityState
import co.softov.morestuff.android.domain.redux.state.UserState

data class AppState(
    val user: UserState = UserState(),
    val priorityState: PriorityState = PriorityState(),
)

val AppState.snoozeLimit: Int get() = user.taskSnoozeLimit