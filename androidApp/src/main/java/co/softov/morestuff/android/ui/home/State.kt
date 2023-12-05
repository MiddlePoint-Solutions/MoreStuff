package co.softov.morestuff.android.ui.home

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.ui.model.NotificationState

@Immutable
data class HomeUiModel(
    val confettiEnabled: Boolean = true,
    val recentlyCompletedTasks: List<Long> = listOf(),
    val notification: NotificationState = NotificationState.None,
    val selectedScopeId: Long = 0,
) : BaseViewState

@Immutable
sealed class HomeUiEvent : BaseViewEvent {
    data object ClearTaskSelection : HomeUiEvent()
    data object CompleteSelectedTasks : HomeUiEvent()
    data object DeleteSelectedTasks : HomeUiEvent()
    data object UndoComplete : HomeUiEvent()
    data class CompleteTask(val taskId: Long) : HomeUiEvent()
    data class ToggleTaskSelection(val taskId: Long) : HomeUiEvent()
    data class SetConfettiEnabled(val enabled: Boolean) : HomeUiEvent()
    data class SetNotification(val notification: NotificationState) : HomeUiEvent()
    data class AddSelectedTasksToScope(val scopeId: Long) : HomeUiEvent()
    data object DeleteSelectedTasksFromScope : HomeUiEvent()
    data class CreateScope(val uid: String, val name: String) : HomeUiEvent()
    data class ScopeSelected(val scopeId: Long) : HomeUiEvent()
    data class DeleteScope(val scopeId: Long) : HomeUiEvent()
    data class UpdateScopeName(val scopeId: Long, val newName: String) : HomeUiEvent()
}