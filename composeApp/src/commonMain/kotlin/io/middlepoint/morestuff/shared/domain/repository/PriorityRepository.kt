package io.middlepoint.morestuff.shared.domain.repository

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel

interface PriorityRepository {

    suspend fun getHighestPriorityScore(): Long
    suspend fun getLowestPriorityScore(): Long
    suspend fun increaseTaskPriorityScore(taskId: Uuid): Either<Failure, Long>
    suspend fun decreaseTaskPriorityScore(taskId: Uuid): Either<Failure, Long>

    suspend fun updateTasksPriorityScoreByAdding(
        taskId: Uuid,
        priorityScore: Long,
    ): Either<Failure, Long>

    suspend fun updateTasksPriorityScoreBySubtracting(
        taskId: Uuid,
        priorityScore: Long,
    ): Either<Failure, Long>

    suspend fun updateTaskPriority(taskId: Uuid, priorityScore: Long): Either<Failure, Long>

    suspend fun updateTasksPriorities(tasks: List<TaskUiModel>): Either<Failure, Unit>


}