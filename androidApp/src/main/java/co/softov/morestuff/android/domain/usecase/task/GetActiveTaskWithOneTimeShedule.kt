package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetActiveTasksWithOneTimeScheduleUseCase {
    suspend operator fun invoke(): Either<Failure, List<TaskDomain>>
}


class GetActiveTasksWithOneTimeScheduleUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetActiveTasksWithOneTimeScheduleUseCase {
    override suspend operator fun invoke(): Either<Failure, List<TaskDomain>> {
        return taskRepository.getActiveTasksWithOneTimeSchedule()
    }
}