package io.middlepoint.morestuff.shared.ui.model.map

import io.middlepoint.morestuff.shared.domain.model.Message
import io.middlepoint.morestuff.shared.domain.model.TaskDomain
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import io.middlepoint.morestuff.shared.ui.model.ReviewItemUiModel

class ReviewTasksMapper(
  private val timeFormatter: TimeFormatter,
  private val messageUiMapper: MessageUiMapper,
) {

  fun map(input: TaskDomain, messages: List<Message>, position: Int): ReviewItemUiModel =
    ReviewItemUiModel(
      id = input.id,
      createTime = timeFormatter.formatToDateTime(input.createTime),
      title = input.title,
      position = "$position",
      priorityScore = input.priorityScore,
      isCompleted = input.isComplete,
      extraDetails = input.extraDetails,
      messages = messageUiMapper.map(messages)
    )

  fun map(tasks: List<TaskDomain>, messages: Map<Long, List<Message>>) = tasks.map {
    map(it, messages[it.id] ?: listOf(), 0)
  }

}