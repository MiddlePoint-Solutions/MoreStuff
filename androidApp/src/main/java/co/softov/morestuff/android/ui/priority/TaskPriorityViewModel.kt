package co.softov.morestuff.android.ui.priority

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.usecase.priority.GetPriorityOptionsParams
import co.softov.morestuff.android.domain.usecase.priority.GetPriorityOptionsUseCase
import co.softov.morestuff.android.domain.usecase.priority.GetSchedulePriorityParams
import co.softov.morestuff.android.domain.usecase.priority.GetSchedulePriorityUseCase
import co.softov.morestuff.android.presentation.model.PriorityOptionsModel
import co.softov.morestuff.android.presentation.model.toModel
import co.softov.morestuff.android.ui.chat.task.model.TaskPriorityModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class TaskPriorityViewModel(
    private val store: AppStore,
    private val getSchedulePriorityUseCase: GetSchedulePriorityUseCase,
    private val getPriorityOptionsUseCase: GetPriorityOptionsUseCase,
    private val taskId: Long,
) : ViewModel(), KoinComponent {

    private val _model = MutableStateFlow(TaskPriorityModel())
    val model: StateFlow<TaskPriorityModel> get() = _model

    private lateinit var currentPriorityCopy: PriorityOptionsModel

    init {
        reset()
    }

    fun reset() {
        viewModelScope.launch {
            _model.updateAndGet { current ->
                val priorityModel = getSchedulePriorityUseCase(GetSchedulePriorityParams(taskId))
                    .first()
                    .fold(
                        ifLeft = { current.priorityModel },
                        ifRight = { it.toModel() }
                    )

                current.copy(
                    priorityModel = priorityModel,
                    showConfirmation = false
                )
            }
            currentPriorityCopy = model.value.priorityModel
        }
    }

    fun priorityChanged(priority: Priority) {
        viewModelScope.launch {
            val params = GetPriorityOptionsParams(
                model.value.priorityModel.current,
                priority
            )
            getPriorityOptionsUseCase(params).map { result ->
                val update = PriorityOptionsModel(
                    current = result.priority,
                    options = result.options
                )
                _model.update {
                    it.copy(
                        priorityModel = update,
                        showConfirmation = currentPriorityCopy != update
                    )
                }
            }
        }
    }

    fun onPriorityOptionChanged(option: PriorityOption) {
        _model.update {
            val change = when (val current = it.priorityModel.current) {
                is Priority.Later -> current.copy(option)
                is Priority.Today -> current.copy(option)
                is Priority.Tomorrow -> current.copy(option)
            }

            it.copy(
                priorityModel = it.priorityModel.copy(current = change),
                showConfirmation = currentPriorityCopy.current != change
            )
        }
    }

    fun updateTaskSchedule() {
        if (currentPriorityCopy != model.value.priorityModel) {
            store.dispatch(
                ScheduleAction.RescheduleTaskAction(
                    taskId = taskId,
                    priority = model.value.priorityModel.current
                )
            )
        }
    }

}
