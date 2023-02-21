package co.softov.morestuff.android.domain.redux.state

import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.model.Defaults
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.state.SettingAction.*
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.InitAction

data class AppSettingsState(
    val dailySnoozeLimit: Int = Defaults.DEFAULT_SNOOZE_LIMIT,
    val smartReminderEnabled: Boolean = Defaults.DEFAULT_SMART_REMINDER_ENABLED
)

sealed class SettingAction : Action.FeatureAction() {

    data class LoadSettings(val settings: AppSettings) : PriorityAction()
    data class SetSnoozeLimit(val limit: Int) : PriorityAction()
    data class EnableSmartReminder(val enable: Boolean) : PriorityAction()

}

fun AppState.reduceSettingState(action: Action): AppState {
    return when (action) {
        is InitAction,
        is SettingAction -> copy(settingState = settingState.reduce(action))
        else -> this
    }
}

fun AppSettingsState.reduce(action: Action): AppSettingsState {
    return when (action) {
        is LoadSettings -> copy(
            dailySnoozeLimit = action.settings.snoozeLimit,
            smartReminderEnabled = action.settings.smartReminderEnabled
        )

        is SetSnoozeLimit -> copy(dailySnoozeLimit = action.limit)
        is EnableSmartReminder -> copy(smartReminderEnabled = action.enable)
        else -> this
    }
}



