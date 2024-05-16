package io.middlepoint.morestuff.android.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.TaskDomain
import io.middlepoint.morestuff.android.domain.repository.TaskRepository

interface GetTasksWithoutScheduleUseCase {
    suspend operator fun invoke(scopeId: Long): Either<Failure, List<TaskDomain>>
}

class GetTasksWithoutScheduleUseCaseImpl(
    private val taskRepository: TaskRepository,
) : GetTasksWithoutScheduleUseCase {

    override suspend fun invoke(scopeId: Long): Either<Failure, List<TaskDomain>> =
        taskRepository.getTasksWithoutSchedule(scopeId)
}

