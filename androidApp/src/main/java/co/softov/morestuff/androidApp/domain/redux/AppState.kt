package co.softov.morestuff.androidApp.domain.redux

import co.softov.morestuff.androidApp.domain.redux.state.UserState

data class AppState(
    val userState: UserState = UserState()
)

val AppState.snoozeLimit: Int get() = userState.taskSnoozeLimit