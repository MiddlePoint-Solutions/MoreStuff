package io.middlepoint.morestuff.shared.domain.usecase.priority

import arrow.core.Either
import arrow.core.flatMap
import io.middlepoint.morestuff.shared.domain.enums.ReviewActionType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.UpdateTaskPriorityScoreUseCase

interface UpdateTaskReviewPriorityUseCase {
    suspend operator fun invoke(taskId: Uuid, actionType: ReviewActionType): Either<Failure, Long>
}

class UpdateTaskReviewPriorityUseCaseImpl(
    private val getDefaultPriorityScoreUseCase: GetDefaultPriorityScoreUseCase,
    private val getTaskAbovePriorityScoreUseCase: GetTaskAbovePriorityScoreUseCase,
    private val getTaskBelowPriorityScoreUseCase: GetTaskBelowPriorityScoreUseCase,
    private val updateTaskPriorityScoreUseCase: UpdateTaskPriorityScoreUseCase,
    private val getTaskUseCase: GetTaskUseCase,
) : UpdateTaskReviewPriorityUseCase {

    override suspend fun invoke(
        taskId: Uuid,
        actionType: ReviewActionType,
    ): Either<Failure, Long> = when (actionType) {
        ReviewActionType.Now -> {
            val score = getDefaultPriorityScoreUseCase(Priority.Now())
            updateTaskPriorityScoreUseCase(taskId, score)
        }

        ReviewActionType.Later -> {
            val score = getDefaultPriorityScoreUseCase(Priority.Later())
            updateTaskPriorityScoreUseCase(taskId, score)
        }

        ReviewActionType.More -> getTaskUseCase(taskId).flatMap { task ->
            getTaskAbovePriorityScoreUseCase(task.priorityScore).flatMap { taskAbove ->
                updateTaskPriorityScoreUseCase(taskId, taskAbove.priorityScore + 1)
            }
        }

        ReviewActionType.Less -> getTaskUseCase(taskId).flatMap { task ->
            getTaskBelowPriorityScoreUseCase(task.priorityScore).flatMap { taskBelow ->
                updateTaskPriorityScoreUseCase(taskId, taskBelow.priorityScore - 1)
            }
        }

        ReviewActionType.Done -> getTaskUseCase(taskId).map { it.priorityScore }
    }
}