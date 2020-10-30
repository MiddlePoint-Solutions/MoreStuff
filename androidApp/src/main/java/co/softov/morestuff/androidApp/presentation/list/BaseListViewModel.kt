package co.softov.morestuff.androidApp.presentation.list

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.enums.TimeOption
import co.softov.morestuff.androidApp.domain.enums.TimeOption.*
import co.softov.morestuff.androidApp.domain.redux.AppStore
import co.softov.morestuff.androidApp.domain.usecase.schedule.RescheduleTask
import co.softov.morestuff.androidApp.domain.usecase.task.SetTaskComplete

abstract
class BaseListViewModel<State : BaseViewState, Event : BaseViewEvent>(
    private val rescheduleTask: RescheduleTask,
    private val setTaskComplete: SetTaskComplete,
    initialState: State
) : BaseViewModel<State, Event>(initialState) {

    open fun rescheduleTaskLater(taskId: Long) {
        viewModelScope.launch { rescheduleTask(taskId, Priority.Later(Default)) }
    }

    open fun rescheduleTaskOneHour(taskId: Long) {
        viewModelScope.launch { rescheduleTask(taskId, Priority.Today(Default)) }
    }

    open fun rescheduleTaskTomorrow(taskId: Long) {
        viewModelScope.launch { rescheduleTask(taskId, Priority.Tomorrow(Default)) }
    }

    open fun rescheduleTaskComplete(taskId: Long) {
        viewModelScope.launch { setTaskComplete(taskId = taskId) }
    }
}