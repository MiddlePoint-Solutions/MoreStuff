package co.softov.morestuff.androidApp.domain.usecase.message

import co.softov.morestuff.androidApp.domain.enums.ContentType
import co.softov.morestuff.androidApp.domain.model.Message
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.usecase.task.GetScheduleTaskUseCase

interface CreateScheduleMessageUseCase {
    suspend operator fun invoke(scheduleId: Long): SimpleResult<Message>
}

class CreateScheduleMessageUseCaseImpl(
    private val getScheduleTaskUseCase: GetScheduleTaskUseCase,
    private val createMessageUseCase: CreateMessageUseCase
) : CreateScheduleMessageUseCase {

    override suspend fun invoke(scheduleId: Long): SimpleResult<Message> {
        return getScheduleTaskUseCase(scheduleId).fold(
            success = { task ->
                createMessageUseCase(task.id, task.title, ContentType.TASK_REMINDER)
            },
            failure = {
                Result.Failure(it)
            }
        )
    }
}