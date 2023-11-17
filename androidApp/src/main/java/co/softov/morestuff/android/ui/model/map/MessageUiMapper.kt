package co.softov.morestuff.android.ui.model.map

import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.android.ui.model.MessageUiModel

class MessageUiMapper(private val timeFormatter: TimeFormatter) {

    fun internalMap(input: Message): MessageUiModel {
        return MessageUiModel(
            id = input.id,
            taskId = input.taskId,
            scheduleId = input.scheduleId,
            contentType = input.contentType,
            createTime = timeFormatter.formatToDateTime(input.createTime) ?: "",
            content = input.content,
            messageData = input.messageData,
            replyType = input.replyType,
            replyContent = input.replyContent,
            formattedTime = timeFormatter.formatTimeDayMonthInDeviceLanguage(input.createTime) ?: "",
            formattedTimeOnly = timeFormatter.formatTimeOnly(input.createTime) ?: "",
            openGraphResult = input.openGraphResult
        )
    }

    fun map(input: List<Message>): List<MessageUiModel> {
        return input.map { message ->
            internalMap(message)
        }
    }
}
