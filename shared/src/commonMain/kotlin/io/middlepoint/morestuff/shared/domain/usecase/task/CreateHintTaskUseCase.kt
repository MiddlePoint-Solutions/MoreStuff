package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.TaskType
import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.model.defaultScope
import io.middlepoint.morestuff.shared.domain.service.HintTaskProvider
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateMessageUseCase
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString

interface CreateHintTaskUseCase {
  suspend operator fun invoke()
}

class CreateHintTaskUseCaseImpl(
  private val createTaskUseCase: CreateTaskUseCase,
  private val createMessageUseCase: CreateMessageUseCase,
  private val hintTaskProvider: HintTaskProvider
) : CreateHintTaskUseCase {

  @OptIn(ExperimentalResourceApi::class)
  override suspend fun invoke() {
    val hintTasks = hintTaskProvider.getHintTasks()

    hintTasks.forEach { hintTask ->
      val priorityParams = Priority.Now()
      val title = getString(hintTask.taskTitle)
      val taskParams = TaskParams(title, priorityParams, TaskType.User, defaultScope.id)
      val createdTask = createTaskUseCase(taskParams)

      hintTask.taskMessages.forEach { messageHint ->
        val content = getString(messageHint)
        createMessageUseCase(
          taskId = createdTask.id,
          title = content,
          contentType = ContentType.APP_TASK_MESSAGE,
          messageData = null
        )
      }
    }
  }
}