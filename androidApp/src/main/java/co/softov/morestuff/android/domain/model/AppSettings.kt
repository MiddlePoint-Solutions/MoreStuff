package co.softov.morestuff.android.domain.model

import co.softov.morestuff.android.domain.enums.AppTheme

data class AppSettings(
    val isFirstTime: Boolean = false,
    val appTheme: AppTheme = AppTheme.System,
    val snoozeLimit: Int = Defaults.DEFAULT_SNOOZE_LIMIT,
)















