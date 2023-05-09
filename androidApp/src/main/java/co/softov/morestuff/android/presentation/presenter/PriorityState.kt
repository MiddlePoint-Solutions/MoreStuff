package co.softov.morestuff.android.presentation.presenter

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel

data class PriorityViewState(
    val items: List<TaskDomain> = listOf()
) : BaseViewState

sealed class PriorityViewEvent : BaseViewEvent {
    data class InitPriorityState(val items: List<TaskDomain>) : PriorityViewEvent()

    data class ReorderItem(val fromPosition: Int, val toPosition: Int) : PriorityViewEvent()

    data class CompleteItem(val item: TaskDomain) : PriorityViewEvent()
}


