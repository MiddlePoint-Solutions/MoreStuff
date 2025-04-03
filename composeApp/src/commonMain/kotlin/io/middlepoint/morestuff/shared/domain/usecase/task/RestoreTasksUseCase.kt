package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import arrow.core.flatMap
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Message
import io.middlepoint.morestuff.shared.domain.model.TaskDomain
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

interface RestoreTasksUseCase {
    suspend operator fun invoke(
        deletedTasks: List<TaskDomain>,
        taskScopes: Map<Long, List<Long>> = mapOf(),
        taskMessages: Map<Long, List<Message>> = mapOf()
    ): Either<Failure, List<TaskDomain>>
}

class RestoreTasksUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val scheduleRepository: ScheduleRepository,
    private val messageRepository: MessageRepository
) : RestoreTasksUseCase {
    override suspend fun invoke(
        deletedTasks: List<TaskDomain>,
        taskScopes: Map<Long, List<Long>>,
        taskMessages: Map<Long, List<Message>>
    ): Either<Failure, List<TaskDomain>> {
        if (deletedTasks.isEmpty()) {
            return Either.Right(emptyList())
        }
        val restoredTasksResult = taskRepository.restoreTasks(deletedTasks, taskScopes)
        return restoredTasksResult.flatMap { restoredTasks ->
            val taskMapping = deletedTasks.zip(restoredTasks).toMap()
            taskMapping.forEach { (originalTask, restoredTask) ->
                originalTask.schedule.forEach { schedule ->
                    scheduleRepository.restoreSchedule(schedule, restoredTask.id)
                }

                val messages = taskMessages[originalTask.id] ?: emptyList()
                messages.forEach { message ->
                    messageRepository.restoreMessage(
                        taskId = restoredTask.id,
                        scheduleId = 0,
                        contentType = message.contentType.value,
                        messageData = message.messageData,
                        content = message.content,
                        createTime = message.createTime,
                        seenTime = message.seenTime,
                        replyType = message.replyType,
                        replyContent = message.replyContent,
                        replyTime = message.replyTime
                    )
                }
            }

            Either.Right(restoredTasks)
        }
    }
}