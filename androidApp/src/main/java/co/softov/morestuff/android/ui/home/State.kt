package co.softov.morestuff.android.ui.home

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.ui.model.NotificationState

@Immutable
data class HomeUiModel(
    val confettiEnabled: Boolean = true,
    val selectedScopeId: Long = 0,
) : BaseViewState

@Immutable
sealed class HomeUiEvent : BaseViewEvent {
    data object ClearTaskSelection : HomeUiEvent()
    data object CompleteSelectedTasks : HomeUiEvent()
    data object DeleteSelectedTasks : HomeUiEvent()
    data class UndoComplete(val tasks: List<Long>) : HomeUiEvent()
    data class ToggleTaskSelection(val taskId: Long) : HomeUiEvent()
    data class SetConfettiEnabled(val enabled: Boolean) : HomeUiEvent()
    data class MoveSelectedTasksToScope(val scopeId: Long) : HomeUiEvent()
    data object DeleteSelectedTasksFromScope : HomeUiEvent()
    data class ScopeSelected(val scopeId: Long) : HomeUiEvent()
    data class UndoMoveTasks(val fromScopeId: Long, val tasks: List<Long>) : HomeUiEvent()

}