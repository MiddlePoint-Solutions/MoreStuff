package io.middlepoint.morestuff.android.domain.redux

import io.middlepoint.morestuff.android.domain.redux.state.AppSettings

data class AppState(
    val settings: AppSettings = AppSettings(),
)

