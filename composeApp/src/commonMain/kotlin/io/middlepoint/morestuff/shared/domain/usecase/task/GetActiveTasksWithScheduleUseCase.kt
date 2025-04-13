package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository


interface GetActiveTasksWithScheduleUseCase {
    suspend operator fun invoke(scheduleTypes: List<ScheduleType>): Either<Failure, List<Task>>
}


class GetActiveTasksWithScheduleUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetActiveTasksWithScheduleUseCase {
    override suspend operator fun invoke(scheduleTypes: List<ScheduleType>): Either<Failure, List<Task>> {
        return taskRepository.getTasksWithSchedule(scheduleTypes )
    }
}
