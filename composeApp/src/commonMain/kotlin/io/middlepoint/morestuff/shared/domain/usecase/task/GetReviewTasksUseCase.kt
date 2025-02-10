package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.ReviewTasks
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskChatMessagesUseCase

interface GetReviewTasksUseCase {
  suspend operator fun invoke(scopeId: Long): Either<Failure, ReviewTasks>
}

class GetReviewTasksUseCaseImpl(
  private val getTasksWithoutScheduleUseCase: GetTasksWithoutScheduleUseCase,
  private val getTaskChatMessagesUseCase: GetTaskChatMessagesUseCase
) : GetReviewTasksUseCase {

  override suspend fun invoke(scopeId: Long): Either<Failure, ReviewTasks> =
    getTasksWithoutScheduleUseCase(scopeId).map { tasks ->
      val messagesMap = tasks.associate {
        it.id to (getTaskChatMessagesUseCase(it.id).asList())
      }
      ReviewTasks(tasks, messagesMap)
    }

}