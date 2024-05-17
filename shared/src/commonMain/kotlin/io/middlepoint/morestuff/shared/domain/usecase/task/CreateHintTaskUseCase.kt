package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.TaskType
import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.model.defaultScope
import io.middlepoint.morestuff.shared.domain.service.HintTaskProvider
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateMessageUseCase

interface CreateHintTaskUseCase {
    suspend operator fun invoke()
}

class CreateHintTaskUseCaseImpl(
    private val createTaskUseCase: CreateTaskUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
    private val hintTaskProvider: HintTaskProvider
) : CreateHintTaskUseCase {

    override suspend fun invoke() {
        val hintTasks = hintTaskProvider.getHintTasks()

        hintTasks.forEach { hintTask ->
            val priorityParams = Priority.Now()
            val taskParams =
                TaskParams(hintTask.taskTitle, priorityParams, TaskType.User, defaultScope.id)
            val createdTask = createTaskUseCase(taskParams)

            hintTask.taskMessages.forEach { messageHint ->
                createMessageUseCase(
                    taskId = createdTask.id,
                    title = messageHint.content,
                    contentType = ContentType.APP_TASK_MESSAGE,
                    messageData = null
                )
            }
        }
    }
}