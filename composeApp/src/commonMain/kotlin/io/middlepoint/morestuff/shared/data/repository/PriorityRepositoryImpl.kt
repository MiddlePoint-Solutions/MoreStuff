package io.middlepoint.morestuff.shared.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import arrow.core.Either
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.PriorityRepository
import io.middlepoint.morestuff.shared.domain.repository.TaskDoesNotExist
import io.middlepoint.morestuff.shared.domain.service.logger
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel

class PriorityRepositoryImpl(
    database: StuffDb,
) : PriorityRepository {

    private val taskQueries = database.tasksQueries

    override suspend fun getHighestPriorityScore(): Long = taskQueries
        .selectHighestPriorityScore().awaitAsOne().max ?: 0

    override suspend fun getLowestPriorityScore(): Long = taskQueries
        .selectLowestPriorityScore().awaitAsOne().min ?: 0

    override suspend fun increaseTaskPriorityScore(taskId: Uuid): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            when (val taskDb = taskQueries.selectTaskById(id = taskId).awaitAsOneOrNull()) {
                null -> Either.Left(TaskDoesNotExist)
                else -> {
                    val newPriorityScore = taskDb.priority_score + 1
                    taskQueries.updateTaskPriorityScore(newPriorityScore, taskId)
                    Either.Right(newPriorityScore)
                }
            }
        }
    }

    override suspend fun decreaseTaskPriorityScore(taskId: Uuid): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            when (val taskDb = taskQueries.selectTaskById(id = taskId).awaitAsOneOrNull()) {
                null -> Either.Left(TaskDoesNotExist)
                else -> {
                    val newPriorityScore = taskDb.priority_score - 1
                    taskQueries.updateTaskPriorityScore(newPriorityScore, taskId)
                    Either.Right(newPriorityScore)
                }
            }
        }
    }

    override suspend fun updateTasksPriorityScoreByAdding(
        taskId: Uuid,
        priorityScore: Long,
    ): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            taskQueries.incrementTasksPriorityScore(priorityScore)
            taskQueries.updateTaskPriorityScore(priorityScore, taskId)
            Either.Right(priorityScore)
        }
    }

    override suspend fun updateTasksPriorityScoreBySubtracting(
        taskId: Uuid,
        priorityScore: Long,
    ): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            taskQueries.decrementTasksPriorityScore(priorityScore)
            taskQueries.updateTaskPriorityScore(priorityScore, taskId)
            Either.Right(priorityScore)
        }
    }

    override suspend fun updateTaskPriority(
        taskId: Uuid,
        priorityScore: Long,
    ): Either<Failure, Long> {
        taskQueries.updateTaskPriorityScore(priorityScore, taskId)
        return Either.Right(priorityScore)
    }

    override suspend fun updateTasksPriorities(tasks: List<TaskUiModel>): Either<Failure, Unit> {
        return taskQueries.transactionWithResult {
            tasks.forEach { task ->
                taskQueries.updateTaskPriorityScore(task.priorityScore, task.id)
                logger.d { "Updated task ${task.id} - ${task.title} to priority ${task.priorityScore}" }
            }
            Either.Right(Unit)
        }
    }

}