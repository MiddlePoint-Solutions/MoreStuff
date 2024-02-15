package co.softov.morestuff.android.ui.model.map

import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.android.ui.model.TaskUiModel

class TaskUiMapper(
    private val timeFormatter: TimeFormatter,
) {
    private fun internalMap(input: TaskDomain, position: Int): TaskUiModel {
        return TaskUiModel(
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
            hasScope = input.hasScope
        )
    }

    fun map(input: List<TaskDomain>): List<TaskUiModel> {
        return input.mapIndexed { index, task ->
            internalMap(task, index + 1)
        }
    }
}