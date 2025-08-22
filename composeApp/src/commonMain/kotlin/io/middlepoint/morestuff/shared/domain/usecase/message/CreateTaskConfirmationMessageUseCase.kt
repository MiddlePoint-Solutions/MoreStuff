package io.middlepoint.morestuff.shared.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.coroutines.delay

interface CreateTaskConfirmationMessageUseCase {
    suspend operator fun invoke(taskId: Uuid, priority: Priority): Either<Failure,Boolean>
}

class CreateTaskConfirmationMessageUseCaseImpl(
    private val createMessageUseCase: CreateMessageUseCase
) : CreateTaskConfirmationMessageUseCase {

    override suspend fun invoke(taskId: Uuid, priority: Priority): Either<Failure,Boolean> {
        delay(1300)
        val confirmTitle = when (priority) {
            is Priority.Plan -> createLaterConfirmationTitle(priority)
            is Priority.Now -> createTodayConfirmationTitle(priority)
            is Priority.Later -> createTomorrowConfirmationTitle(priority)
        }
        createMessageUseCase(
            taskId,
            title = confirmTitle,
            contentType = ContentType.CONFIRM_TASK,
            messageExtra = null,
            scheduleId = null
        )
        return Either.Right(true)
    }

    private fun createTodayConfirmationTitle(
        today: Priority.Now
    ): String {
        return "No worries, will remind you soon!"
    }

    private fun createTomorrowConfirmationTitle(
        tomorrow: Priority.Later
    ): String {
        return "Great, remind you tomorrow!"
    }

    private fun createLaterConfirmationTitle(
        later: Priority.Plan
    ): String {
        return "Saving this for later"
    }
}