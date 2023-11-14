package co.softov.morestuff.android.ui.chat.items

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.model.MessageUiModel

object MockData {

    val chatActions = ChatActions(scheduleAction = { _, _ -> })

    val messageUiModel: MessageUiModel
        get() {
            val userNewTaskMessage = userNewTask
            val formattedTime = ""
            val formattedTimeOnly = "10:00"

            return MessageUiModel(
                id = userNewTaskMessage.id,
                taskId = userNewTaskMessage.taskId,
                scheduleId = userNewTaskMessage.scheduleId,
                contentType = userNewTaskMessage.contentType,
                createTime = userNewTaskMessage.createTime,
                content = userNewTaskMessage.content,
                replyType = userNewTaskMessage.replyType,
                replyContent = userNewTaskMessage.replyContent,
                openGraphResult = userNewTaskMessage.openGraphResult,
                messageData = userNewTaskMessage.messageData,
                formattedTime = formattedTime,
                formattedTimeOnly = formattedTimeOnly
            )
        }

    val userNewTask: Message
        get() =
            Message(
                0,
                0,
                0,
                ContentType.USER_NEW_TASK,
                "The big bang",
                null,
                content = "Hello there!",
                null,
                null,
                null,
                null,
                null
            )

}
