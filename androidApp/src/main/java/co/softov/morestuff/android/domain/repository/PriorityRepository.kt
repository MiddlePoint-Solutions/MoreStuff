package co.softov.morestuff.android.domain.repository

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure

interface PriorityRepository {

    suspend fun getHighestPriorityScore(): Long
    suspend fun getLowestPriorityScore(): Long
    suspend fun increaseTaskPriorityScore(taskId: Long, scopeId: Long): Either<Failure, Long>
    suspend fun decreaseTaskPriorityScore(taskId: Long, scopeId: Long): Either<Failure, Long>

    suspend fun updateTasksPriorityScoreByAdding(
        taskId: Long,
        priorityScore: Long,
        scopeId: Long
    ): Either<Failure, Long>

    suspend fun updateTasksPriorityScoreBySubtracting(
        taskId: Long,
        priorityScore: Long,
        scopeId: Long
    ): Either<Failure, Long>

    suspend fun updateTaskPriority(taskId: Long, priorityScore: Long, scopeId: Long): Either<Failure, Long>

}