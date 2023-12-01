package co.softov.morestuff.android.ui.schedule

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksFlowUseCase
import co.softov.morestuff.android.ui.home.HomeUiEvent
import co.softov.morestuff.android.ui.model.TaskUiModel
import co.softov.morestuff.android.ui.model.map.TaskUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ScopeViewModel(
    scopeId: Long,
    getActiveTasksFlowUseCase: GetActiveTasksFlowUseCase,
    taskMapper: TaskUiMapper,
) : NoStateViewModel() {

    val scopeTasks = MutableStateFlow(listOf<TaskUiModel>())

    init {
        loadData()
        getActiveTasksFlowUseCase(scopeId)
            .mapLatest { taskMapper.map(it, listOf()/* TODO set selection*/) }
            .onEach { scopeTasks.value = it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = listOf()
            )

    }

}