package co.softov.morestuff.android.data.repository

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.PriorityRepository
import co.softov.morestuff.android.domain.repository.TaskDoesNotExist
import co.softov.morestuff.db.StuffDb

class PriorityRepositoryImpl(
    database: StuffDb,
) : PriorityRepository {

    private val taskQueries = database.taskQueries

    override suspend fun getHighestPriorityScore(): Long = taskQueries
        .selectHighestPriorityScore().executeAsOne().max ?: 0

    override suspend fun getLowestPriorityScore(): Long = taskQueries
        .selectLowestPriorityScore().executeAsOne().min ?: 0

    override suspend fun increaseTaskPriorityScore(taskId: Long, scopeId: Long): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            when (val taskDb = taskQueries.selectTaskById(id = taskId).executeAsOneOrNull()) {
                null -> Either.Left(TaskDoesNotExist)
                else -> {
                    val newPriorityScore = taskDb.priority_score + 1
                    taskQueries.updateTaskPriorityScore(newPriorityScore, taskId, scopeId)
                    Either.Right(newPriorityScore)
                }
            }
        }
    }

    override suspend fun decreaseTaskPriorityScore(taskId: Long, scopeId: Long): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            when (val taskDb = taskQueries.selectTaskById(id = taskId).executeAsOneOrNull()) {
                null -> Either.Left(TaskDoesNotExist)
                else -> {
                    val newPriorityScore = taskDb.priority_score - 1
                    taskQueries.updateTaskPriorityScore(newPriorityScore, taskId, scopeId)
                    Either.Right(newPriorityScore)
                }
            }
        }
    }

    override suspend fun updateTasksPriorityScoreByAdding(
        taskId: Long,
        priorityScore: Long,
        scopeId: Long
    ): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            taskQueries.incrementTasksPriorityScore(priorityScore)
            taskQueries.updateTaskPriorityScore(priorityScore, taskId, scopeId)
            Either.Right(priorityScore)
        }
    }

    override suspend fun updateTasksPriorityScoreBySubtracting(
        taskId: Long,
        priorityScore: Long,
        scopeId: Long
    ): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            taskQueries.decrementTasksPriorityScore(priorityScore)
            taskQueries.updateTaskPriorityScore(priorityScore, taskId, scopeId)
            Either.Right(priorityScore)
        }
    }

    override suspend fun updateTaskPriority(
        taskId: Long,
        priorityScore: Long,
        scopeId: Long
    ): Either<Failure, Long> {
        taskQueries.updateTaskPriorityScore(priorityScore, taskId, scopeId)
        return Either.Right(priorityScore)
    }

}