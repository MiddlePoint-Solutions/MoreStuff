package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.usecase.task.GetTaskForScheduleUseCase

interface CreateScheduleMessageUseCase {
    suspend operator fun invoke(scheduleId: Long): Either<Failure,Message>
}

class CreateScheduleMessageUseCaseImpl(
    private val getTaskForScheduleUseCase: GetTaskForScheduleUseCase,
    private val createMessageUseCase: CreateMessageUseCase
) : CreateScheduleMessageUseCase {

    override suspend fun invoke(scheduleId: Long): Either<Failure,Message> {
        return getTaskForScheduleUseCase(scheduleId).fold(
            ifRight = { task ->
                createMessageUseCase(task.id, task.title, ContentType.TASK_REMINDER, messageWithData = null, scheduleId)
            },
            ifLeft = {
                Either.Left(it)
            }
        )
    }
}