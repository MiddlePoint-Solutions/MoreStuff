package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.usecase.task.GetScheduleTaskUseCase

interface CreateScheduleMessageUseCase {
    suspend operator fun invoke(scheduleId: Long): Either<Failure,Message>
}

class CreateScheduleMessageUseCaseImpl(
    private val getScheduleTaskUseCase: GetScheduleTaskUseCase,
    private val createMessageUseCase: CreateMessageUseCase
) : CreateScheduleMessageUseCase {

    override suspend fun invoke(scheduleId: Long): Either<Failure,Message> {
        return getScheduleTaskUseCase(scheduleId).fold(
            ifRight = { task ->
                createMessageUseCase(task.id, scheduleId, task.title, ContentType.TASK_REMINDER)
            },
            ifLeft = {
                Either.Left(it)
            }
        )
    }
}