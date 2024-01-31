package co.softov.morestuff.android.ui.main

import co.softov.morestuff.android.domain.enums.AppTheme

sealed class MainStates {

    data object Idle: MainStates()

    data class Ready(
        val theme: AppTheme = AppTheme.System,
        val showOnBoarding: Boolean = false,
    ) : MainStates()

}

