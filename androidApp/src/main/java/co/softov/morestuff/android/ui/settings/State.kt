package co.softov.morestuff.android.ui.settings

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.domain.enums.AppTheme

data class SettingsViewState(
    val appThemeIndex: Int = AppTheme.System.ordinal,
    val snoozeLimit: Int = 0
) : BaseViewState