package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import arrow.core.left
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.TaskReorderFailure
import co.softov.morestuff.android.domain.repository.PriorityRepository

val NA: Nothing? = null


interface ReorderTaskUseCase {
    suspend operator fun invoke(
        taskId: Long,
        aboveScore: Long? = NA,
        belowScore: Long? = NA,
    ): Either<Failure, Long>
}

class ReorderTaskUseCaseImpl(
    private val priorityRepository: PriorityRepository,
    private val updateTaskPriorityScoreUseCase: UpdateTaskPriorityScoreUseCase,
    val scopeId: Long,
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
            updateTaskPriorityScoreUseCase(taskId, (belowScore ?: 0) + 1, scopeId)
        }

        belowScore == NA -> {
            updateTaskPriorityScoreUseCase(taskId, aboveScore - 1, scopeId)
        }

        else -> {
            val average = (aboveScore + belowScore) / 2
            if (aboveScore == average) {
                priorityRepository.updateTasksPriorityScoreByAdding(taskId, average, scopeId)
            } else if (belowScore == average) {
                priorityRepository.updateTasksPriorityScoreBySubtracting(taskId, average, scopeId)
            } else {
                updateTaskPriorityScoreUseCase(taskId, average, scopeId)
            }
        }
    }
}