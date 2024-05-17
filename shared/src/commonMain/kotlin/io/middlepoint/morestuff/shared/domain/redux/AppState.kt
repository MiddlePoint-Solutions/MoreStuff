package io.middlepoint.morestuff.shared.domain.redux

import io.middlepoint.morestuff.shared.domain.redux.state.AppSettings

data class AppState(
    val settings: AppSettings = AppSettings(),
)

