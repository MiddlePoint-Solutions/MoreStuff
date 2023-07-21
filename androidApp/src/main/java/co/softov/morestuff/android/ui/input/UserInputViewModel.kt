package co.softov.morestuff.android.ui.input

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.data.utils.currentTimeZoneInstant
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.ui.home.PriorityModel
import co.softov.morestuff.android.ui.home.mapToDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class UserInputViewModel(
    private val timeManager: TimeManager,
) : NoStateViewModel() {

    var priorityModel = MutableStateFlow<PriorityModel>(PriorityModel.Now)
        private set

    var userInput by mutableStateOf("")
        private set

    private fun createPlanModel() = timeManager.getDefaultPlanTime().run {
        PriorityModel.Plan(
            planTime = timeManager.getDefaultPlanTime(),
            hour = hour,
            minute = minute,
            epochMs = currentTimeZoneInstant.toEpochMilliseconds()
        )
    }

    fun updatePlanTime(hour: Int, minute: Int) {
        priorityModel.update { model ->
            (model as? PriorityModel.Plan)?.let { plan ->
                val updatedTime = timeManager.localDateTime(plan.planTime, hour, minute)
                plan.copy(
                    planTime = updatedTime,
                    hour = hour,
                    minute = minute,
                    epochMs = updatedTime.currentTimeZoneInstant.toEpochMilliseconds()
                )
            } ?: model
        }
    }

    fun updatePlanDate(dateMillis: Long) {
        priorityModel.update { model ->
            (model as? PriorityModel.Plan)?.let { plan ->
                val updatedTime = timeManager.epochMillisToLocalDateTime(
                    dateMillis,
                    plan.hour,
                    plan.minute
                )
                plan.copy(
                    planTime = updatedTime,
                    relativeDisplay = timeManager.getRelativeDate(updatedTime.toString()),
                    epochMs = updatedTime.currentTimeZoneInstant.toEpochMilliseconds()
                )
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
        priorityModel.update { priority }
    }

}