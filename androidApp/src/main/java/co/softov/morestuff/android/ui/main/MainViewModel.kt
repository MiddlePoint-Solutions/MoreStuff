package co.softov.morestuff.android.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.redux.store.OnResumeAction
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.message.GetMessagesUseCase
import co.softov.morestuff.android.ui.Screens
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDateTime

class MainViewModel(
    getMessagesUseCase: GetMessagesUseCase,
    private val timeManager: TimeManager,
) : NoStateViewModel() {

    val messages: StateFlow<List<Message>> =
        getMessagesUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = listOf()
            )

    var priorityModel by mutableStateOf(PriorityUI.Now)
        private set

    var planTime: LocalDateTime by mutableStateOf(timeManager.getDefaultPlanTime())

    fun updatePlanTime(hour: Int, minute: Int) {
        planTime = timeManager.localDateTime(planTime, hour, minute)
    }

    fun addNewTask(title: String) {
        val priority = when (priorityModel) {
            PriorityUI.Now -> Priority.Now()
            PriorityUI.Later -> Priority.Later()
            PriorityUI.Plan -> Priority.Plan(planTime.toString())
        }
        store.dispatch(TaskAction.CreateUserTaskAction(title, priority))
    }

    fun scheduleResponse(scheduleId: Long, replyType: ReplyType) {
        store.dispatch(UserResponseAction(scheduleId, replyType))
    }

    fun priorityChanged(priority: PriorityUI) {
        priorityModel = priority
    }

    fun onResume() {
        store.dispatch(OnResumeAction)
    }

    fun showTaskList() {
        router.showBottomSheet(Screens.taskLists)
    }

}