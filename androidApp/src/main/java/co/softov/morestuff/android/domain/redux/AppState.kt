package co.softov.morestuff.android.domain.redux

import co.softov.morestuff.android.domain.redux.state.DashboardState
import co.softov.morestuff.android.domain.redux.state.UserState

data class AppState(
    val user: UserState = UserState(),
    val dashboard: DashboardState = DashboardState(),
)

val AppState.snoozeLimit: Int get() = user.taskSnoozeLimit