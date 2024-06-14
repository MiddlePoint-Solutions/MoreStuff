package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import arrow.core.flatMap
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.ReviewTasks
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

interface GetReviewTasksUseCase {
    suspend operator fun invoke(scopeId:Long): Either<Failure, ReviewTasks>

}
class GetReviewTasksUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val getTasksWithoutScheduleUseCase: GetTasksWithoutScheduleUseCase,
) : GetReviewTasksUseCase {

    override suspend fun invoke(scopeId:Long): Either<Failure, ReviewTasks> =
        taskRepository.countActiveTasks().flatMap { count ->
            getTasksWithoutScheduleUseCase(scopeId).map { tasks ->
                ReviewTasks(tasks, count)
            }
        }
}