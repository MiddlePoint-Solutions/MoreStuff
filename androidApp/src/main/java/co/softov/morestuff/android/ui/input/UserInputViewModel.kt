package co.softov.morestuff.android.ui.input

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.message.GetMessagesUseCase
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.android.ui.chat.task.MessageUiModel
import co.softov.morestuff.android.ui.model.PriorityInputUiModel
import co.softov.morestuff.android.ui.model.PriorityUiModel
import co.softov.morestuff.android.ui.model.ScheduleUiModel
import co.softov.morestuff.android.ui.model.mapToDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime

class UserInputViewModel(
    getMessagesUseCase: GetMessagesUseCase,
    private val timeManager: TimeManager,
    private val timeFormatter: TimeFormatter,
) : NoStateViewModel() {

    val messages: StateFlow<List<MessageUiModel>> = getMessagesUseCase()
        .map { messages ->
            messages.map { message ->
                val messageDateTime = timeManager.utcStringToLocalDateTime(message.createTime)
                val formattedTime =
                    timeFormatter.formatTimeWithDayMonthYear(messageDateTime.toString()) ?: ""
                val formattedTimeOnly =
                    timeFormatter.formatTimeOnly(messageDateTime.toString()) ?: ""
                MessageUiModel(message, formattedTime, formattedTimeOnly)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = listOf()
        )

    val priorityModel = MutableStateFlow(
        PriorityInputUiModel(
            priority = PriorityUiModel.Now,
            planTime = createPlanTime()
        )
    )

    var userInput by mutableStateOf("")
        private set

    private fun createPlanModel() = timeManager.getDefaultPlanTime().run {
        PriorityUiModel.Plan(localDateTime = timeManager.getDefaultPlanTime())
    }

    private fun createPlanTime(
        time: LocalDateTime = timeManager.getDefaultPlanTime()
    ) = ScheduleUiModel(
        localDateTime = time,
        displayDate = timeFormatter.formatTimeDayAndMonth(time.toString()) ?: "Error",
        displayTime = timeFormatter.formatTimeOnly(time.toString()) ?: "--:--"
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
            (model.priority as? PriorityUiModel.Plan)?.let { plan ->
                val updatedTime = timeManager.epochMillisToLocalDateTime(dateMillis, hour, minute)
                val priority = plan.copy(localDateTime = updatedTime)
                val planTime = createPlanTime(updatedTime)
                PriorityInputUiModel(priority, planTime)
            } ?: model
        }
    }

    suspend fun createNewTask(title: String) {
        dispatchSuspend(
            TaskAction.CreateUserTaskAction(
                title.trim(),
                priorityModel.value.mapToDomain()
            )
        )
        userInput = ""
    }

    fun setNowPriority() {
        priorityChanged(PriorityUiModel.Now)
    }

    fun setLaterPriority() {
        priorityChanged(PriorityUiModel.Later)
    }

    fun setPlanPriority() {
        priorityChanged(createPlanModel())
    }

    fun updateUserInput(input: String) {
        userInput = input
    }

    private fun priorityChanged(priority: PriorityUiModel) {
        priorityModel.update {
            PriorityInputUiModel(
                priority = priority,
                planTime = (priority as? PriorityUiModel.Plan)?.let { plan ->
                    createPlanTime(plan.localDateTime)
                } ?: it.planTime,
            )
        }
    }

}