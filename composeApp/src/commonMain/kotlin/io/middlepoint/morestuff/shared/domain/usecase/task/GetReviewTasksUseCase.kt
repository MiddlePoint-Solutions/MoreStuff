package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import arrow.core.raise.either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.ReviewTasks
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

interface GetReviewTasksUseCase {
  suspend operator fun invoke(scopeId: Uuid): Either<Failure, ReviewTasks>
}

class GetReviewTasksUseCaseImpl(
  private val taskRepository: TaskRepository
) : GetReviewTasksUseCase {

  override suspend fun invoke(scopeId: Uuid): Either<Failure, ReviewTasks> = either {
    ReviewTasks(taskRepository.getScopeActiveTasks(scopeId), mapOf())
  }

}