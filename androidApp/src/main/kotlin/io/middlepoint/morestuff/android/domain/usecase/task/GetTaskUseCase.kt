package io.middlepoint.morestuff.android.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.TaskDomain
import io.middlepoint.morestuff.android.domain.repository.TaskRepository

interface GetTaskUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure,TaskDomain>
}

class GetTaskUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetTaskUseCase {
    override suspend fun invoke(taskId: Long): Either<Failure,TaskDomain> {
        return taskRepository.getTask(taskId)
    }
}