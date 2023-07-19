package co.softov.morestuff.android.domain.usecase.priority

import arrow.core.Either
import arrow.core.flatMap
import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.usecase.task.GetDefaultPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.GetTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.ReorderTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskPriorityScoreUseCase
import timber.log.Timber

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
        actionType: PriorityActionType
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
            Timber.d("Current task score: ${task.priorityScore}" )
            getTaskAbovePriorityScoreUseCase(task.priorityScore).flatMap { taskAbove ->
                Timber.d("Above task score: ${taskAbove.priorityScore}" )
                updateTaskPriorityScoreUseCase(taskId, taskAbove.priorityScore + 1)
            }
        }

        PriorityActionType.Less -> getTaskUseCase(taskId).flatMap { task ->
            Timber.d("Current task score: ${task.priorityScore}" )
            getTaskBelowPriorityScoreUseCase(task.priorityScore).flatMap { taskBelow ->
                Timber.d("Below task score: ${taskBelow.priorityScore}" )
                updateTaskPriorityScoreUseCase(taskId, taskBelow.priorityScore - 1)
            }
        }

    }
}