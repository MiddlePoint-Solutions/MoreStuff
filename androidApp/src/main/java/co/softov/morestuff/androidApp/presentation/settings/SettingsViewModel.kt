package co.softov.morestuff.androidApp.presentation.settings

import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.androidApp.domain.redux.state.UserAction
import co.softov.morestuff.androidApp.domain.redux.state.UserAction.*

class SettingsViewModel : BaseViewModel<SettingsViewState, SettingsViewEvent>(SettingsViewState()) {

    override fun onReduceState(event: SettingsViewEvent): SettingsViewState {
        return state
    }

    fun onSnoozeLimitChanged(limit: Int) {
        dispatchAppStoreAction(ChangeSnoozeLimit(limit))
    }
}