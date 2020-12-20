package co.softov.morestuff.androidApp.presentation.list

import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.redux.middleware.ScheduleAction.RescheduleTaskAction
import co.softov.morestuff.androidApp.domain.redux.middleware.TaskAction.TaskCompleteAction

abstract
class BaseListViewModel<State : BaseViewState, Event : BaseViewEvent>(
    initialState: State
) : BaseViewModel<State, Event>(initialState) {

    open fun rescheduleTaskLater(taskId: Long) {
        dispatchAppStoreAction(RescheduleTaskAction(taskId, Priority.Later()))
    }

    open fun rescheduleTaskOneHour(taskId: Long) {
        dispatchAppStoreAction(RescheduleTaskAction(taskId, Priority.Today()))
    }

    open fun rescheduleTaskTomorrow(taskId: Long) {
        dispatchAppStoreAction(RescheduleTaskAction(taskId, Priority.Tomorrow()))
    }

    open fun rescheduleTaskComplete(taskId: Long) {
        dispatchAppStoreAction(TaskCompleteAction(taskId))
    }
}