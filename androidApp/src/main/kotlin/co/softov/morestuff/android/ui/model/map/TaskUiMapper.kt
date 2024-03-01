package co.softov.morestuff.android.ui.model.map

import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.android.ui.model.TaskUiModel

class TaskUiMapper(
    private val timeFormatter: TimeFormatter,
) {
    fun map(input: TaskDomain, position: Int = 0): TaskUiModel = TaskUiModel(
        id = input.id,
        createTime = timeFormatter.formatToDateTime(input.createTime) ?: "",
        completeTime = timeFormatter.formatToDateTime(input.completeTime) ?: "",
        title = input.title,
        priorityScore = input.priorityScore,
        position = position,
        isComplete = input.isComplete,
        extraDetails = input.extraDetails,
        hasSchedule = input.hasSchedule,
        hasReminder = input.hasReminder,
    )

    fun map(input: List<TaskDomain>): List<TaskUiModel> =
        input.mapIndexed { index, task -> map(task, index + 1) }

}
