package co.softov.morestuff.android.domain.model

import co.softov.morestuff.android.domain.Defaults.DEFAULT_SNOOZE_LIMIT

data class UserSettings(
    val snoozeLimit: Int = DEFAULT_SNOOZE_LIMIT
)