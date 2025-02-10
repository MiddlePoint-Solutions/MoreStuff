package io.middlepoint.morestuff.shared.ui.model.map

import io.middlepoint.morestuff.shared.domain.model.TaskDomain
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel

class TaskUiMapper(
  private val timeFormatter: TimeFormatter,
) {
  fun map(input: TaskDomain, position: Int = 0): TaskUiModel = TaskUiModel(
    id = input.id,
    createTime = timeFormatter.formatToDateTime(input.createTime),
    completeTime = input.completeTime?.let(timeFormatter::formatDisplayCompleteTime) ?: "",
    title = input.title,
    priorityScore = input.priorityScore,
    position = position,
    isComplete = input.isComplete,
    extraDetails = input.extraDetails,
    hasSchedule = input.hasSchedule,
    hasReminder = input.hasReminder,
    scheduleTime = input.getScheduleOrNull()?.scheduleUtcTime?:""
  )

  fun map(input: List<TaskDomain>): List<TaskUiModel> =
    input.mapIndexed { index, task -> map(task, index + 1) }

}
