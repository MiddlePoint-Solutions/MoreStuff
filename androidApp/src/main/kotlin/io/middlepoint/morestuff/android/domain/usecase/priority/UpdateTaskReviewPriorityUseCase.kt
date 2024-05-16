package io.middlepoint.morestuff.android.domain.usecase.priority

import arrow.core.Either
import arrow.core.flatMap
import io.middlepoint.morestuff.android.domain.enums.PriorityActionType
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.Priority
import io.middlepoint.morestuff.android.domain.usecase.task.GetTaskUseCase
import io.middlepoint.morestuff.android.domain.usecase.task.UpdateTaskPriorityScoreUseCase

interface UpdateTaskReviewPriorityUseCase {
    suspend operator fun invoke(taskId: Long, actionType: PriorityActionType): Either<Failure, Long>
}

class UpdateTaskReviewPriorityUseCaseImpl(
    private val getDefaultPriorityScoreUseCase: GetDefaultPriorityScoreUseCase,
    private val getTaskAbovePriorityScoreUseCase: GetTaskAbovePriorityScoreUseCase,
    private val getTaskBelowPriorityScoreUseCase: GetTaskBelowPriorityScoreUseCase,
    private val updateTaskPriorityScoreUseCase: UpdateTaskPriorityScoreUseCase,
    private val getTaskUseCase: GetTaskUseCase,
) : UpdateTaskReviewPriorityUseCase {

    override suspend fun invoke(
        taskId: Long,
        actionType: PriorityActionType,
    ): Either<Failure, Long> = when (actionType) {
        PriorityActionType.Now -> {
            val score = getDefaultPriorityScoreUseCase(Priority.Now())
            updateTaskPriorityScoreUseCase(taskId, score)
        }

        PriorityActionType.Later -> {
            val score = getDefaultPriorityScoreUseCase(Priority.Later())
            updateTaskPriorityScoreUseCase(taskId, score)
        }

        PriorityActionType.More -> getTaskUseCase(taskId).flatMap { task ->
            getTaskAbovePriorityScoreUseCase(task.priorityScore).flatMap { taskAbove ->
                updateTaskPriorityScoreUseCase(taskId, taskAbove.priorityScore + 1)
            }
        }

        PriorityActionType.Less -> getTaskUseCase(taskId).flatMap { task ->
            getTaskBelowPriorityScoreUseCase(task.priorityScore).flatMap { taskBelow ->
                updateTaskPriorityScoreUseCase(taskId, taskBelow.priorityScore - 1)
            }
        }

        PriorityActionType.Done -> getTaskUseCase(taskId).map { it.priorityScore }
    }
}