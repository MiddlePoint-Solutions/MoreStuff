package co.softov.morestuff.androidApp.presentation.settings

import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewState

data class SettingsViewState(
    val snoozeLimit: Int = 0
) : BaseViewState

sealed class SettingsViewEvent : BaseViewEvent