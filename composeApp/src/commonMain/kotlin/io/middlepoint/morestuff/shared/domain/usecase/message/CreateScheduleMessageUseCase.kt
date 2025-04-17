package io.middlepoint.morestuff.shared.domain.usecase.message

import arrow.core.Either
import arrow.core.flatMap
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskForScheduleUseCase

interface CreateScheduleMessageUseCase {
    suspend operator fun invoke(scheduleId: Uuid): Either<Failure, Message>
}

class CreateScheduleMessageUseCaseImpl(
    private val getTaskForScheduleUseCase: GetTaskForScheduleUseCase,
    private val createMessageUseCase: CreateMessageUseCase
) : CreateScheduleMessageUseCase {

    override suspend fun invoke(scheduleId: Uuid): Either<Failure, Message> {
        return getTaskForScheduleUseCase(scheduleId).flatMap { task ->
            createMessageUseCase(
                task.id,
                task.title,
                ContentType.TASK_REMINDER,
                messageExtra = null,
                scheduleId
            )
        }
    }
}