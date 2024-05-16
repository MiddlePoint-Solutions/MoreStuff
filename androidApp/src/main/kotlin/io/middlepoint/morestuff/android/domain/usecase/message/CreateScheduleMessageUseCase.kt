package io.middlepoint.morestuff.android.domain.usecase.message

import arrow.core.Either
import arrow.core.flatMap
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.enums.ContentType
import io.middlepoint.morestuff.android.domain.model.Message
import io.middlepoint.morestuff.android.domain.usecase.task.GetTaskForScheduleUseCase

interface CreateScheduleMessageUseCase {
    suspend operator fun invoke(scheduleId: Long): Either<Failure, Message>
}

class CreateScheduleMessageUseCaseImpl(
    private val getTaskForScheduleUseCase: GetTaskForScheduleUseCase,
    private val createMessageUseCase: CreateMessageUseCase
) : CreateScheduleMessageUseCase {

    override suspend fun invoke(scheduleId: Long): Either<Failure, Message> {
        return getTaskForScheduleUseCase(scheduleId).flatMap { task ->
            createMessageUseCase(
                task.id,
                task.title,
                ContentType.TASK_REMINDER,
                messageData = null,
                scheduleId
            )
        }
    }
}