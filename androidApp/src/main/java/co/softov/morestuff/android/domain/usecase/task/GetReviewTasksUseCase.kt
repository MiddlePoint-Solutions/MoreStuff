package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import arrow.core.flatMap
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ReviewTasks
import co.softov.morestuff.android.domain.repository.TaskRepository

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