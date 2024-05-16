package io.middlepoint.morestuff.android.domain.repository

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure

interface PriorityRepository {

    suspend fun getHighestPriorityScore(): Long
    suspend fun getLowestPriorityScore(): Long
    suspend fun increaseTaskPriorityScore(taskId: Long): Either<Failure, Long>
    suspend fun decreaseTaskPriorityScore(taskId: Long): Either<Failure, Long>

    suspend fun updateTasksPriorityScoreByAdding(
        taskId: Long,
        priorityScore: Long,
    ): Either<Failure, Long>

    suspend fun updateTasksPriorityScoreBySubtracting(
        taskId: Long,
        priorityScore: Long,
    ): Either<Failure, Long>

    suspend fun updateTaskPriority(taskId: Long, priorityScore: Long): Either<Failure, Long>

}