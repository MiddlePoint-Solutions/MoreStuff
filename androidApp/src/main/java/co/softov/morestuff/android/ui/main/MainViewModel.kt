package co.softov.morestuff.android.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.data.utils.currentTimeZoneInstant
import co.softov.morestuff.android.data.utils.inEpochMilliseconds
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
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import timber.log.Timber

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

    var planModel by mutableStateOf(createPlanModel())
        private set

    private fun createPlanModel() = timeManager.getDefaultPlanTime().run {
        PlanModel(
            planTime = this,
            hour = hour,
            minute = minute,
            epochMs = currentTimeZoneInstant.toEpochMilliseconds()
        )
    }


    fun updatePlanTime(hour: Int, minute: Int) {
        val updatedPlanTime = timeManager.localDateTime(planModel.planTime, hour, minute)
        planModel = planModel.copy(
            planTime = updatedPlanTime,
            hour = hour,
            minute = minute,
            epochMs = updatedPlanTime.currentTimeZoneInstant.toEpochMilliseconds()
        )
    }

    fun updatePlanDate(dateMillis: Long) {
        Timber.d("updatePlanDate: $dateMillis")
        val updatedPlanTime =
            timeManager.epochMillisToLocalDateTime(dateMillis, planModel.hour, planModel.minute)
        planModel = planModel.copy(
            planTime = updatedPlanTime,
            relativeDisplay = timeManager.getRelativeDate(updatedPlanTime.toString()),
            epochMs = updatedPlanTime.currentTimeZoneInstant.toEpochMilliseconds()
        )
        Timber.d("updatePlanDate: $planModel")

    }

    fun addNewTask(title: String) {
        val priority = when (priorityModel) {
            PriorityUI.Now -> Priority.Now()
            PriorityUI.Later -> Priority.Later()
            PriorityUI.Plan -> Priority.Plan(planModel.planTime.toString())
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