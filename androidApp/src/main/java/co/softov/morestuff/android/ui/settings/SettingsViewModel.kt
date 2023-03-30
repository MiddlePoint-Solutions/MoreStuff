package co.softov.morestuff.android.ui.settings

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.redux.state.SettingAction

class SettingsViewModel : BaseViewModel<SettingsViewState, SettingsViewEvent>(SettingsViewState()) {

    override fun onReduceState(event: SettingsViewEvent): SettingsViewState {
        return state
    }

    fun onSnoozeLimitChanged(limit: Int) {
        dispatchAppStoreAction(SettingAction.SetSnoozeLimit(limit))
    }

    fun smartReminderEnabled(enable: Boolean) {
        dispatchAppStoreAction(SettingAction.EnableSmartReminder(enable))
    }
    fun navigateBack() {
        router.exit()
    }
}