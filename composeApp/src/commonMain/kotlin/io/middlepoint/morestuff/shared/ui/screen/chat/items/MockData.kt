package io.middlepoint.morestuff.shared.ui.screen.chat.items

import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.screen.chat.ChatActions
import kotlinx.datetime.Clock

object MockData {

  val chatActions =
    ChatActions(scheduleAction = { _, _ -> })

  fun createMessages(count: Int) = buildList {
    repeat(count) {
      add(
        MessageUiModel(
          id = Uuid("$it"),
          taskId = userNewTask.taskId,
          scheduleId = userNewTask.scheduleId,
          contentType = userNewTask.contentType,
          createAt = userNewTask.createdAt.toString(),
          content = userNewTask.content,
          openGraphResult = userNewTask.openGraphResult,
          messageExtra = userNewTask.messageExtra,
          formattedTime = "",
          formattedTimeOnly = "10:00"
        )
      )
    }
  }

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
        createAt = userNewTask.createdAt.toString(),
        content = userNewTaskMessage.content,
        openGraphResult = userNewTaskMessage.openGraphResult,
        messageExtra = userNewTaskMessage.messageExtra,
        formattedTime = formattedTime,
        formattedTimeOnly = formattedTimeOnly
      )
    }

  val userNewTask: Message
    get() = Message(
      id = Uuid("id"),
      taskId = Uuid("taskId"),
      scheduleId = null,
      contentType = ContentType.USER_NEW_TASK,
      createdAt = Clock.System.now(),
      updatedAt = Clock.System.now(),
      content = "Hello there!",
    )

}
