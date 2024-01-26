package co.softov.morestuff.android.ui.schedule

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.usecase.task.GetScopeActiveTasksFlowUseCase
import co.softov.morestuff.android.ui.model.map.TaskUiMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ScopeViewModel(
    scopeId: Long,
    selectedTasksFlow: Flow<List<Long>>,
    getScopeActiveTasksFlowUseCase: GetScopeActiveTasksFlowUseCase,
    taskMapper: TaskUiMapper,
) : NoStateViewModel() {

    val scopeTasks = getScopeActiveTasksFlowUseCase(scopeId)
        .combine(selectedTasksFlow) { tasks, selected -> taskMapper.map(tasks, selected) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = listOf()
        )

    init {
        loadData()
    }

}