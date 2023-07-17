package co.softov.morestuff.android.domain.redux

import co.softov.morestuff.android.domain.redux.state.AppSettingsState
import co.softov.morestuff.android.domain.nav.Screen

data class AppState(
    val settingState: AppSettingsState = AppSettingsState(),
)