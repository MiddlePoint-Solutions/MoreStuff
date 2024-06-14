package io.middlepoint.morestuff.shared.ui.screen.chat.items

import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.Message
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.screen.chat.ChatActions

object MockData {

  val chatActions =
    ChatActions(scheduleAction = { _, _ -> })

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
