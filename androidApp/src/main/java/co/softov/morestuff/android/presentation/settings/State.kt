package co.softov.morestuff.android.presentation.settings

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState

data class SettingsViewState(
    val snoozeLimit: Int = 0
) : BaseViewState

sealed class SettingsViewEvent : BaseViewEvent