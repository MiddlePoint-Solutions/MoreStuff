package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository


interface GetActiveTasksWithScheduleUseCase {
    suspend operator fun invoke(scheduleTypes: List<ScheduleType>): Either<Failure, List<TaskDomain>>
}


class GetActiveTasksWithScheduleUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetActiveTasksWithScheduleUseCase {
    override suspend operator fun invoke(scheduleTypes: List<ScheduleType>): Either<Failure, List<TaskDomain>> {
        return taskRepository.getTasksWithSchedule(scheduleTypes )
    }
}
