package co.softov.morestuff.android.data

import co.softov.morestuff.android.domain.enums.AppSetting
import co.softov.morestuff.android.domain.enums.AppSetting.*

val AppSetting.key: String
    get() = when (this) {
        FirstTime -> Constants.KEY_FIRST_TIME
        AppTheme -> Constants.KEY_APP_THEME
        SnoozeLimit -> Constants.KEY_USER_SNOOZE_LIMIT
    }