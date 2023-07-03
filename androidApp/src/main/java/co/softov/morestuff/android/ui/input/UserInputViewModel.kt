package co.softov.morestuff.android.ui.input

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.data.utils.currentTimeZoneInstant
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.task.CreateTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.TaskParams
import co.softov.morestuff.android.ui.home.PlanModel
import co.softov.morestuff.android.ui.home.PriorityUI
import timber.log.Timber

class UserInputViewModel(
    private val timeManager: TimeManager,
    private val createTaskUseCase: CreateTaskUseCase
) : NoStateViewModel() {

    var priorityModel by mutableStateOf(PriorityUI.Now)
        private set

    var planModel by mutableStateOf(createPlanModel())
        private set

    val currentPriority
        get() = when (priorityModel) {
            PriorityUI.Now -> Priority.Now()
            PriorityUI.Later -> Priority.Later()
            PriorityUI.Plan -> Priority.Plan(planModel.planTime.toString())
        }

    private fun createPlanModel() = timeManager.getDefaultPlanTime().run {
        PlanModel(
            planTime = timeManager.getDefaultPlanTime(),
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

    fun createNewTask(title: String) {
        store.dispatch(TaskAction.CreateUserTaskAction(title, currentPriority))
    }

    /**
     * Creates a new task and returns its id.
     * This is only used when we need the taskId for navigation.
     *
     * @return TaskId of the newly created task
     */
    suspend fun createNewShareableTask(title: String): Long {
        val priority = currentPriority
        val params = TaskParams(title, priority, TaskType.User)
        val task = createTaskUseCase(params)
        store.dispatchSuspend(TaskAction.TaskCreatedAction(task, priority))
        return task.id
    }

    fun priorityChanged(priority: PriorityUI) {
        priorityModel = priority
    }

}