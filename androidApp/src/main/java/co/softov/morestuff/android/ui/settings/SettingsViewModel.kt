package co.softov.morestuff.android.ui.settings

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.enums.ReviewNotification
import co.softov.morestuff.android.domain.redux.middleware.DevAction
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.service.Notifier
import co.softov.morestuff.android.domain.service.Scheduler
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val notifier: Notifier,
    private val getActiveTasksUseCase: GetActiveTasksUseCase
) : BaseViewModel<SettingsViewState, SettingsViewEvent>(SettingsViewState()) {

    override fun onReduceState(event: SettingsViewEvent): SettingsViewState {
        return state
    }

    init {
        viewModelScope.launch {
            activeTasksForExport = getActiveTasksUseCase().first().map { it.title }
        }
    }

    var activeTasksForExport: List<String> = listOf()
        private set

    fun onSnoozeLimitChanged(limit: Int) {
        dispatchAppStoreAction(SettingAction.SetSnoozeLimit(limit))
    }

    fun smartReminderEnabled(enable: Boolean) {
        dispatchAppStoreAction(SettingAction.EnableSmartReminder(enable))
    }

    fun clearPendingMessages() {
        dispatchAppStoreAction(DevAction.ClearActiveReminderMessages)
    }

    fun testReviewActivity() {
        notifier.showReviewNotification(ReviewNotification.Evening)
    }
}