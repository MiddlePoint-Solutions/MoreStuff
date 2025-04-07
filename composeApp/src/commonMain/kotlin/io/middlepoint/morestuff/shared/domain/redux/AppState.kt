package io.middlepoint.morestuff.shared.domain.redux

import io.middlepoint.morestuff.shared.domain.redux.state.AppSettings
import io.middlepoint.morestuff.shared.domain.redux.state.UserState

data class AppState(
    val userState: UserState = UserState(),
    val settings: AppSettings = AppSettings(),
)

