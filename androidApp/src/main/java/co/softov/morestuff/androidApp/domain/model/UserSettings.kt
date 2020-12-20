package co.softov.morestuff.androidApp.domain.model

import co.softov.morestuff.androidApp.domain.Defaults.DEFAULT_SNOOZE_LIMIT

data class UserSettings(
    val snoozeLimit: Int = DEFAULT_SNOOZE_LIMIT
)