package io.middlepoint.morestuff.shared.ui.model.map

import io.middlepoint.morestuff.shared.domain.model.ReviewTasks
import io.middlepoint.morestuff.shared.domain.model.TaskDomain
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import io.middlepoint.morestuff.shared.ui.model.ReviewItemUiModel

class ReviewTasksMapper(
  private val timeFormatter: TimeFormatter,
) {
  fun map(input: TaskDomain, position: Int, count: Int): ReviewItemUiModel = ReviewItemUiModel(
    id = input.id,
    createTime = timeFormatter.formatToDateTime(input.createTime),
    title = input.title,
    priorityScore = input.priorityScore,
    position = "$position",
    isCompleted = input.isComplete,
    extraDetails = input.extraDetails
  )

  fun map(input: ReviewTasks): List<ReviewItemUiModel> {
    return input.tasks.mapIndexed { index, task ->
      map(task, index + 1, input.activeTasksCount)
    }
  }
}