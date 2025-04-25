package io.middlepoint.morestuff.shared.domain.redux.action

import io.github.vinceglb.filekit.PlatformFile
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.redux.store.Action

sealed class MessageAction : Action.FeatureAction() {
  data class CreateScheduleMessageAction(val scheduleId: Uuid) : MessageAction()
  data class CreateUserTaskMessageAction(val taskId: Uuid, val content: String) :
    MessageAction()

  data class CreateAppTaskMessageAction(
    val taskId: Uuid,
    val content: String
  ) : MessageAction()

  data class CreateFileMessageAction(
    val taskId: Uuid,
    val file: PlatformFile,
    val message: String
  ) : MessageAction()

  data class CreatePDFMessageAction(
    val taskId: Uuid,
    val file: PlatformFile,
    val message: String
  ) : MessageAction()

  data class DeleteMessageAction(val messageId: Uuid) : MessageAction()

  data class UpdateMessageContentAction(val messageId: Uuid, val content: String) : TaskAction()

  data class CreateAITaskMessageAction(
    val taskId: Uuid,
    val prompt: String
  ) : MessageAction()

  data class AIRequestStarted(val taskId: Uuid) : MessageAction()
  data class AIRequestFinished(val taskId: Uuid) : MessageAction()
  data class AIRequestFailed(val taskId: Uuid, val error: String?) :MessageAction()
  data class ClearAIErrorAction(val taskId: Uuid) : MessageAction()

}