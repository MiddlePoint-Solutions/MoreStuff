package io.middlepoint.morestuff.shared.domain.redux.state

data class AppState(
    val userState: UserState = UserState(),
    val settings: AppSettings = AppSettings(),
)

