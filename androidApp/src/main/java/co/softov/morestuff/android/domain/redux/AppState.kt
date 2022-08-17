package co.softov.morestuff.android.domain.redux

import co.softov.morestuff.android.domain.redux.state.DashboardState
import co.softov.morestuff.android.domain.redux.state.UserState

data class AppState(
    val userState: UserState = UserState(),
    val schedule: DashboardState = DashboardState(),
)

val AppState.snoozeLimit: Int get() = userState.taskSnoozeLimit