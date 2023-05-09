package co.softov.morestuff.android.domain.redux

import co.softov.morestuff.android.domain.redux.state.AppSettingsState
import co.softov.morestuff.android.domain.redux.state.ReviewState

data class AppState(
    val settingState: AppSettingsState = AppSettingsState(),
    val reviewState: ReviewState = ReviewState()
)

// Settings
val AppState.dailySnoozeLimit: Int get() = settingState.settings.snoozeLimit
val AppState.smartReminderEnabled: Boolean get() = settingState.settings.smartReminderEnabled