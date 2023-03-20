package co.softov.morestuff.android.ui.priority

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.usecase.priority.*
import co.softov.morestuff.android.ui.chat.task.model.TaskPriorityModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

@OptIn(ExperimentalCoroutinesApi::class)
class TaskPriorityViewModel(
    private val store: AppStore,
    private val getSchedulePriorityUseCase: GetSchedulePriorityUseCase,
    private val getPriorityOptionsUseCase: GetPriorityOptionsUseCase,
    private val taskId: Long,
) : ViewModel(), KoinComponent {

    private val _model = MutableStateFlow(TaskPriorityModel())
    val model: StateFlow<TaskPriorityModel> get() = _model

    private lateinit var currentPriorityCopy: SchedulePriorityResult

    init {
        viewModelScope.launch {
            getSchedulePriorityUseCase(GetSchedulePriorityParams(taskId))
                .mapLatest {
                    _model.updateAndGet { current ->
                        it.fold(
                            ifLeft = { current },
                            ifRight = {
                                currentPriorityCopy = it
                                current.copy(
                                    priorityModel = it.model,
                                    showConfirmation = !it.scheduleActive
                                )
                            }
                        )
                    }
                }.launchIn(this)
        }
    }

    fun reset() {
        _model.update {
            it.copy(
                priorityModel = currentPriorityCopy.model,
                showConfirmation = !currentPriorityCopy.scheduleActive
            )
        }
    }

    fun priorityChanged(priority: Priority) {
        viewModelScope.launch {
            val params = GetPriorityOptionsParams(
                model.value.priorityModel.priority,
                priority
            )
            getPriorityOptionsUseCase(params).map { result ->
                _model.update {
                    it.copy(
                        priorityModel = result,
                        showConfirmation = currentPriorityCopy.model.priority != result.priority
                    )
                }
            }
        }
    }

    fun onPriorityOptionChanged(option: PriorityOption) {
        _model.update {
            val change = when (val current = it.priorityModel.priority) {
                is Priority.Later -> current.copy(option)
                is Priority.Today -> current.copy(option)
                is Priority.Tomorrow -> current.copy(option)
            }

            it.copy(
                priorityModel = it.priorityModel.copy(priority = change),
                showConfirmation = currentPriorityCopy.model.priority != change
            )
        }
    }

    fun updateTaskSchedule() {
        store.dispatch(
            ScheduleAction.RescheduleTaskAction(
                taskId = taskId,
                priority = model.value.priorityModel.priority
            )
        )
    }

}
