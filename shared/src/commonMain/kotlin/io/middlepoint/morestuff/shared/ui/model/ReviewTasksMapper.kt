package io.middlepoint.morestuff.shared.ui.model

import io.middlepoint.morestuff.shared.domain.model.ReviewTasks
import io.middlepoint.morestuff.shared.domain.model.TaskDomain
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.domain.util.TimeFormatter

class ReviewTasksMapper(
  private val timeFormatter: TimeFormatter,
  private val timeManager: TimeManager,
) {
  fun map(input: TaskDomain, position: Int, count: Int): ReviewItemUiModel {
    val inputDateTime = timeManager.nowLocalDateTimeString

    val createTime: String
    if (input.completeTime != null) {
      createTime = timeFormatter.formatToDateTime(input.completeTime) ?: ""
      timeFormatter.formatToDateTime(input.completeTime)
    } else {
      createTime = timeFormatter.formatToDateTime(inputDateTime) ?: ""
    }

    return ReviewItemUiModel(
      id = input.id,
      createTime = createTime,
      title = input.title,
      priorityScore = input.priorityScore,
      position = "$position",
      isCompleted = input.isComplete,
      extraDetails = input.extraDetails
    )
  }

  fun map(input: ReviewTasks): List<ReviewItemUiModel> {
    return input.tasks.mapIndexed { index, task ->
      map(task, index + 1, input.activeTasksCount)
    }
  }
}