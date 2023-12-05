package co.softov.morestuff.android.ui.schedule

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.app.presentation.viewmodel.NoViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.NoViewState
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksFlowUseCase
import co.softov.morestuff.android.ui.home.HomeUiEvent
import co.softov.morestuff.android.ui.model.TaskUiModel
import co.softov.morestuff.android.ui.model.map.TaskUiMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ScopeViewModel(
    scopeId: Long,
    selectedTasksFlow: Flow<List<Long>>,
    getActiveTasksFlowUseCase: GetActiveTasksFlowUseCase,
    taskMapper: TaskUiMapper,
) : NoStateViewModel() {

    val scopeTasks = getActiveTasksFlowUseCase(scopeId)
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

data class ScopeUiModel(
    val tasks: List<ScopeDomain> = listOf()
)