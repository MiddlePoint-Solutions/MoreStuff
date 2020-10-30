package co.softov.morestuff.androidApp.domain.redux

import co.softov.morestuff.androidApp.domain.redux.state.UserState

data class AppState(
    val signInState: UserState = UserState()
)