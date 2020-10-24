package co.softov.morestuff.androidApp.presentation.content

import androidx.paging.PagedList
import co.softov.morestuff.androidApp.app.presentation.navigation.BaseConductor
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.enums.TimeOption
import co.softov.morestuff.androidApp.domain.model.Message
import co.softov.morestuff.androidApp.presentation.dashboard.options.OptionListItemViewModel

data class ContentViewState(
    val data: PagedList<Message>? = null,
    val priority: Priority = Priority.Today(TimeOption.Default),
    val timeOptions: List<OptionListItemViewModel> = listOf(),
    val currentTimeOptionId: Long = 0
) : BaseViewState

sealed class ContentViewEvent : BaseViewEvent {
    object Init : ContentViewEvent()
    data class ShowChatData(val data: PagedList<Message>) : ContentViewEvent()
    data class ChangePriority(val priority: Priority) : ContentViewEvent()
    data class SetTimeOption(val optionId: Long) : ContentViewEvent()
    data class SetCustomTime(val time: Long) : ContentViewEvent()
}

interface ContentConductor : BaseConductor {
    fun showTaskList()
    fun showTodayTimePicker()
    fun showTomorrowTimePicker()
    fun showDateTimePicker()
}

