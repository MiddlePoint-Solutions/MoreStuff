package co.softov.morestuff.android.ui.input

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.data.utils.currentTimeZoneInstant
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.ui.home.PriorityInputModel
import co.softov.morestuff.android.ui.home.PriorityModel
import co.softov.morestuff.android.ui.home.PlanModel
import co.softov.morestuff.android.ui.home.mapToDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime

class UserInputViewModel(
    private val timeManager: TimeManager,
    private val timeFormatter: TimeFormatter,
) : NoStateViewModel() {

    val priorityModel = MutableStateFlow(
        PriorityInputModel(
            priority = PriorityModel.Now,
            planTime = createPlanTime()
        )
    )

    var userInput by mutableStateOf("")
        private set

    private fun createPlanModel() = timeManager.getDefaultPlanTime().run {
        PriorityModel.Plan(localDateTime = timeManager.getDefaultPlanTime())
    }

    private fun createPlanTime(
        time: LocalDateTime = timeManager.getDefaultPlanTime()
    ) = PlanModel(
        localDateTime = time,
        displayDate = timeFormatter.formatTimeDayAndMonth(time.toString()) ?: "Error"
    )

    fun updatePlanTime(hour: Int, minute: Int) {
        with(priorityModel.value.planTime) {
            updatePlan(hour, minute, epochMs)
        }
    }

    fun updatePlanDate(dateMillis: Long) {
        with(priorityModel.value.planTime) {
            updatePlan(hour, minute, dateMillis)
        }
    }

    private fun updatePlan(hour: Int, minute: Int, dateMillis: Long) {
        priorityModel.update { model ->
            (model.priority as? PriorityModel.Plan)?.let { plan ->
                val updatedTime = timeManager.epochMillisToLocalDateTime(dateMillis, hour, minute)
                val priority = plan.copy(localDateTime = updatedTime)
                val planTime = model.planTime.copy(
                    localDateTime = updatedTime,
                    hour = hour,
                    minute = minute,
                    epochMs = updatedTime.currentTimeZoneInstant.toEpochMilliseconds()
                )
                PriorityInputModel(priority, planTime)
            } ?: model
        }
    }

    fun createNewTask(title: String) {
        store.dispatch(
            TaskAction.CreateUserTaskAction(
                title,
                priorityModel.value.mapToDomain()
            )
        )
    }

    fun setNowPriority() {
        priorityChanged(PriorityModel.Now)
    }

    fun setLaterPriority() {
        priorityChanged(PriorityModel.Later)
    }

    fun setPlanPriority() {
        priorityChanged(createPlanModel())
    }

    fun updateUserInput(input: String) {
        userInput = input
    }

    private fun priorityChanged(priority: PriorityModel) {
        priorityModel.update {
            PriorityInputModel(
                priority = priority,
                planTime = (priority as? PriorityModel.Plan)?.let { plan ->
                    createPlanTime(plan.localDateTime)
                } ?: it.planTime,
            )
        }
    }

}