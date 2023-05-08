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
    private val taskId: Long,
) : ViewModel(), KoinComponent {

    private val _model = MutableStateFlow(TaskPriorityModel())
    val model: StateFlow<TaskPriorityModel> get() = _model


    init {
       
    }

    fun reset() {

    }

    fun priorityChanged(priority: Priority) {
        // TODO(Joseph): update schedule
    }

    fun onPriorityOptionChanged(option: PriorityOption) {
       
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
