package io.middlepoint.morestuff.android.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.enums.ScheduleType
import io.middlepoint.morestuff.android.domain.model.TaskDomain
import io.middlepoint.morestuff.android.domain.repository.TaskRepository


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
