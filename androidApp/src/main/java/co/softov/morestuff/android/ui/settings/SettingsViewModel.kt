package co.softov.morestuff.android.ui.settings

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.redux.middleware.DevAction
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.service.Scheduler

class SettingsViewModel(
    private val scheduler: Scheduler
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

    fun navigateBackSettings() {
        router.exit()
    }

    fun clearPendingMessages() {
        dispatchAppStoreAction(DevAction.ClearActiveReminderMessages)
    }

    fun testReviewActivity() {
//        router.navigateTo(Screens.notificationActivity)
        scheduler.scheduleMorningReview()
    }
}