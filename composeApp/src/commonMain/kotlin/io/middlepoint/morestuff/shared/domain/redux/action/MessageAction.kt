package io.middlepoint.morestuff.shared.domain.redux.action

import io.github.vinceglb.filekit.PlatformFile
import io.middlepoint.morestuff.shared.domain.redux.store.Action

sealed class MessageAction : Action.FeatureAction() {
    data class CreateScheduleMessageAction(val scheduleId: Long) : MessageAction()
    data class CreateUserTaskMessageAction(val taskId: Long, val content: String) :
        MessageAction()
    data class CreateAppTaskMessageAction(
        val taskId: Long,
        val content: String
    ) : MessageAction()

    data class CreateFileMessageAction(
      val taskId: Long,
      val file: PlatformFile,
      val message: String
    ) : MessageAction()

    data class CreatePDFMessageAction(
      val taskId: Long,
      val file: PlatformFile,
      val message: String
    ) : MessageAction()

    data class DeleteMessageAction(val messageId: Long) : MessageAction()

    data class UpdateMessageContentAction(val messageId: Long, val content: String) : TaskAction()

}