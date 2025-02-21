package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.repository.PriorityRepository
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import io.middlepoint.morestuff.shared.ui.screen.schedule.logger


interface ReorderTaskUseCase {
    suspend operator fun invoke(updatedTasks: List<TaskUiModel>): Either<Failure, Unit>
}

class ReorderTaskUseCaseImpl(
    private val priorityRepository: PriorityRepository
) : ReorderTaskUseCase {
    override suspend operator fun invoke(updatedTasks: List<TaskUiModel>): Either<Failure, Unit> {
        if (updatedTasks.isEmpty()) return Either.Right(Unit)

        val highestPriority = priorityRepository.getHighestPriorityScore()

        val updatedPriorities = updatedTasks.mapIndexed { index, task ->
            val newPriority = highestPriority - index
            logger.d { "Updating ${task.title} (ID: ${task.id}) from ${task.priorityScore} to $newPriority" }
            task.copy(priorityScore = newPriority)
        }

        return priorityRepository.updateTasksPriorities(updatedPriorities)
    }
}




val NA: Nothing? = null


/*interface ReorderTaskUseCase {
    suspend operator fun invoke(
        taskId: Long,
        aboveScore: Long? = NA,
        belowScore: Long? = NA,
    ): Either<Failure, Long>
}*/


/*
class ReorderTaskUseCaseImpl(
    private val priorityRepository: PriorityRepository,
    private val updateTaskPriorityScoreUseCase: UpdateTaskPriorityScoreUseCase,
) : ReorderTaskUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        aboveScore: Long?,
        belowScore: Long?,
    ): Either<Failure, Long> = when {
        aboveScore == NA && belowScore == NA -> {
            TaskReorderFailure("No values provided for above or below priority score").left()
        }

        aboveScore == NA -> {
            updateTaskPriorityScoreUseCase(taskId, (belowScore ?: 0) + 1)
        }

        belowScore == NA -> {
            updateTaskPriorityScoreUseCase(taskId, aboveScore - 1)
        }

        else -> {
            val average = (aboveScore + belowScore) / 2
            if (aboveScore == average) {
                priorityRepository.updateTasksPriorityScoreByAdding(taskId, average)
            } else if (belowScore == average) {
                priorityRepository.updateTasksPriorityScoreBySubtracting(taskId, average)
            } else {
                updateTaskPriorityScoreUseCase(taskId, average)
            }
        }
    }
}*/
