package co.softov.morestuff.androidApp.presentation.content

import androidx.paging.PagedList
import co.softov.morestuff.androidApp.app.presentation.navigation.BaseConductor
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.model.Message
import co.softov.morestuff.androidApp.domain.model.TodayOption

data class ContentViewState(
    val data: PagedList<Message>? = null,
    val priority: Priority = Priority.Today(TodayOption.Automatic),
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

