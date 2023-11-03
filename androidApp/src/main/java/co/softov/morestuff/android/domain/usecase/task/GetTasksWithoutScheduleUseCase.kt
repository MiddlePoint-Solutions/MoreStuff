package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ReviewTasks
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetTasksWithoutScheduleUseCase {
    suspend operator fun invoke(): Either<Failure, List<TaskDomain>>
}

class GetTasksWithoutScheduleUseCaseImpl(
    private val taskRepository: TaskRepository,
) : GetTasksWithoutScheduleUseCase {

    override suspend fun invoke(): Either<Failure, List<TaskDomain>> =
        taskRepository.getTasksWithoutSchedule()
}

