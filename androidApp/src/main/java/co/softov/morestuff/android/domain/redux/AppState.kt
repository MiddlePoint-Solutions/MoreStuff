package co.softov.morestuff.android.domain.redux

import co.softov.morestuff.android.domain.redux.state.AppSettings

data class AppState(
    val settings: AppSettings = AppSettings(),
)