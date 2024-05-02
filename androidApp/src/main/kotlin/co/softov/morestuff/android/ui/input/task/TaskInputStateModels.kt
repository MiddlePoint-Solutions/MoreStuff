package co.softov.morestuff.android.ui.input.task

import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.model.defaultScope
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.android.ui.input.createPlanTime
import co.softov.morestuff.android.ui.model.MessageUiModel
import co.softov.morestuff.android.ui.model.PriorityInputUiModel
import co.softov.morestuff.android.ui.model.PriorityUiModel

sealed class TaskInputEvent {
    data object SetNowPriority : TaskInputEvent()
    data object SetLaterPriority : TaskInputEvent()
    data object SetPlanPriority : TaskInputEvent()
    data class UpdatePlanDate(val utcTimeMillis: Long) : TaskInputEvent()
    data class UpdatePlanTime(val hour: Int, val minute: Int) : TaskInputEvent()
    data class SetCurrentScope(val scopeId: Long) : TaskInputEvent()
    data class CreateNewTask(val title: String) : TaskInputEvent()
}



data class UserInputState(
    val messages: List<MessageUiModel> = listOf(),
    val scopes: List<ScopeDomain> = listOf(),
    val currentScope: ScopeDomain = defaultScope,
    val priorityModel: PriorityInputUiModel,
    val taskId: Long? = null,
) {
    companion object {
        fun create(
            timeManager: TimeManager,
            timeFormatter: TimeFormatter
        ): UserInputState = UserInputState(
            priorityModel = PriorityInputUiModel(
                priority = PriorityUiModel.Now,
                planTime = createPlanTime(timeManager, timeFormatter)
            )
        )
    }
}