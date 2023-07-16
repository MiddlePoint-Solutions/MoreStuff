package co.softov.morestuff.android.ui.settings

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.redux.middleware.DevAction
import co.softov.morestuff.android.domain.redux.state.SettingAction

class SettingsViewModel(
    private val devTools: DevTools,
) : BaseViewModel<SettingsViewState, SettingsViewEvent>(SettingsViewState()) {

    override fun onReduceState(event: SettingsViewEvent): SettingsViewState {
        return state
    }

    fun onSnoozeLimitChanged(limit: Int) {
        dispatchAppStoreAction(SettingAction.SetSnoozeLimit(limit))
    }

    fun smartReminderEnabled(enable: Boolean) {
        dispatchAppStoreAction(SettingAction.EnableSmartReminder(enable))
    }

    fun clearPendingMessages() {
        dispatchAppStoreAction(DevAction.ClearActiveReminderMessages)
    }

    fun testReviewNotification() {
        devTools.testReviewNotification()
    }

    fun selectAppTheme(theme: AppTheme) {
        dispatchAppStoreAction(SettingAction.SetAppTheme(theme))
    }
}