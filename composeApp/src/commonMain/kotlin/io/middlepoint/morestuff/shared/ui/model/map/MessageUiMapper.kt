package io.middlepoint.morestuff.shared.ui.model.map

import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel

class MessageUiMapper(private val timeFormatter: TimeFormatter) {

  fun map(input: Message): MessageUiModel =
    MessageUiModel(
      id = input.id,
      taskId = input.taskId,
      scheduleId = input.scheduleId,
      contentType = input.contentType,
      createTime = timeFormatter.formatToDateTime(input.createTime),
      content = input.content,
      messageData = input.messageData,
      replyType = input.replyType,
      replyContent = input.replyContent,
      formattedTime = timeFormatter.formatDisplayDayMonth(input.createTime),
      formattedTimeOnly = timeFormatter.formatDisplayTime(input.createTime),
      openGraphResult = input.openGraphResult
    )

  fun map(input: List<Message>): List<MessageUiModel> = input.map(this::map)
}
