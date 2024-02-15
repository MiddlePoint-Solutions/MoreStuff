package co.softov.morestuff.android.ui.home

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.model.defaultScope
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class HomeState(
    val currentScopeId: Long = defaultScope.id,
    val scopes: List<ScopeDomain> = listOf(),
    val selectedTasks: List<Long> = listOf(),
) : Parcelable

@Immutable
sealed class HomeEvent {
    data object ClearTaskSelection : HomeEvent()
    data object CompleteSelectedTasks : HomeEvent()
    data object DeleteSelectedTasks : HomeEvent()
    data class ToggleTaskSelection(val taskId: Long) : HomeEvent()
    data class MoveSelectedTasksToScope(val scopeId: Long) : HomeEvent()
    data class ScopeSelected(val scopeId: Long) : HomeEvent()
    data class CreateScopeForSelectedTasks(val title: String) : HomeEvent()

}