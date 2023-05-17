package co.softov.morestuff.android.domain.redux

import co.softov.morestuff.android.domain.redux.state.AppSettingsState

data class AppState(
    val settingState: AppSettingsState = AppSettingsState(),
)

// Settings
val AppState.dailySnoozeLimit: Int get() = settingState.settings.snoozeLimit
val AppState.smartReminderEnabled: Boolean get() = settingState.settings.smartReminderEnabled