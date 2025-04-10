package io.middlepoint.morestuff.shared.domain.redux.state

import io.middlepoint.morestuff.shared.domain.enums.isReady

data class AppState(
    val userState: UserState = UserState(),
    val settings: AppSettingsState = AppSettingsState(),
)

fun AppState.isReady() =
    userState.status.isReady()
            && settings.status.isReady()

fun AppState.isAuthenticated() = userState.user != null

