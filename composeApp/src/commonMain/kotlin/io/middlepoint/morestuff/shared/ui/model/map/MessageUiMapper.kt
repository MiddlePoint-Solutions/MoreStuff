package io.middlepoint.morestuff.shared.ui.model.map

import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class MessageUiMapper(private val timeFormatter: TimeFormatter) {

  fun map(input: Message): MessageUiModel =
    MessageUiModel(
      id = input.id,
      taskId = input.taskId,
      scheduleId = input.scheduleId,
      contentType = input.contentType,
      createAt = input.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
      content = input.content,
      messageExtra = input.messageExtra,
      formattedTime = timeFormatter.formatDisplayDayMonth(input.createdAt),
      formattedTimeOnly = timeFormatter.formatDisplayTime(input.createdAt),
      openGraphResult = input.openGraphResult
    )

  fun map(input: List<Message>): List<MessageUiModel> = input.map(this::map)
}
