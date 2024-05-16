package io.middlepoint.morestuff.android.ui.model.map

import io.middlepoint.morestuff.android.domain.model.Message
import io.middlepoint.morestuff.android.domain.util.TimeFormatter
import io.middlepoint.morestuff.android.ui.model.MessageUiModel

class MessageUiMapper(private val timeFormatter: TimeFormatter) {

    fun map(input: Message): MessageUiModel =
        MessageUiModel(
            id = input.id,
            taskId = input.taskId,
            scheduleId = input.scheduleId,
            contentType = input.contentType,
            createTime = timeFormatter.formatToDateTime(input.createTime) ?: "",
            content = input.content,
            messageData = input.messageData,
            replyType = input.replyType,
            replyContent = input.replyContent,
            formattedTime = timeFormatter.formatTimeDayMonthInDeviceLanguage(input.createTime)
                ?: "",
            formattedTimeOnly = timeFormatter.formatTimeOnly(input.createTime) ?: "",
            openGraphResult = input.openGraphResult
        )

    fun map(input: List<Message>): List<MessageUiModel> = input.map(this::map)
}
