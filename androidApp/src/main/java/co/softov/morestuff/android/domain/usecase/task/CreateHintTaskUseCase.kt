package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.service.HintTaskProvider
import co.softov.morestuff.android.domain.usecase.message.CreateMessageUseCase

interface CreateHintTaskUseCase {
    suspend operator fun invoke(): List<TaskDomain>
}

class CreateHintTaskUseCaseImpl(
    private val createTaskUseCase: CreateTaskUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
    private val hintTaskProvider: HintTaskProvider
) : CreateHintTaskUseCase {

    override suspend fun invoke(): List<TaskDomain> {
        val taskTitles = hintTaskProvider.getTaskTitles()
        val createdTasks = mutableListOf<TaskDomain>()

        for (title in taskTitles) {
            val priorityParams = Priority.Now()
            val taskParams = TaskParams(title, priorityParams, TaskType.User)
            val createdTask = createTaskUseCase(taskParams)
            createdTasks.add(createdTask)

            val messages = hintTaskProvider.getTaskMessages(title, priorityParams)
            for (messageHint in messages) {
                createMessageUseCase(
                    taskId = createdTask.id,
                    title = messageHint.content,
                    contentType = ContentType.APP_TASK_MESSAGE,
                    messageData = null
                )

            }
        }

        return createdTasks
    }
}