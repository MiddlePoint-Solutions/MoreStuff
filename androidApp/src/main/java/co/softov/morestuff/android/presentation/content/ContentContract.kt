package co.softov.morestuff.android.presentation.content

import androidx.paging.PagedList
import co.softov.morestuff.android.app.presentation.navigation.BaseConductor
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.model.Message

data class ContentViewState(
    val data: PagedList<Message>? = null,
    val priority: Priority = Priority.Today(),
    val currentTimeOptionId: Long = 0
) : BaseViewState

sealed class ContentViewEvent : BaseViewEvent {
    object Init : ContentViewEvent()
    data class ShowChatData(val data: PagedList<Message>) : ContentViewEvent()
    data class ChangePriority(val priority: Priority) : ContentViewEvent()
}

interface ContentConductor : BaseConductor {
    fun showTaskList()
    fun showTodayTimePicker()
    fun showTomorrowTimePicker()
    fun showDateTimePicker()
}

