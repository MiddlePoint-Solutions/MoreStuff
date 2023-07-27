package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetActiveTasksWithReminderScheduleUseCase {
    suspend operator fun invoke(): Either<Failure, List<TaskDomain>>
}


class GetActiveTasksWithReminderScheduleUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetActiveTasksWithReminderScheduleUseCase {
    override suspend operator fun invoke(): Either<Failure, List<TaskDomain>> {
        return taskRepository.getActiveTasksWithReminderSchedule()
    }
}