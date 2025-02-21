package io.middlepoint.morestuff.shared.data.repository

import arrow.core.Either
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.repository.PriorityRepository
import io.middlepoint.morestuff.shared.domain.repository.TaskDoesNotExist
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import io.middlepoint.morestuff.shared.ui.screen.schedule.logger

class PriorityRepositoryImpl(
    database: StuffDb,
) : PriorityRepository {

    private val taskQueries = database.taskQueries

    override suspend fun getHighestPriorityScore(): Long = taskQueries
        .selectHighestPriorityScore().executeAsOne().max ?: 0

    override suspend fun getLowestPriorityScore(): Long = taskQueries
        .selectLowestPriorityScore().executeAsOne().min ?: 0

    override suspend fun increaseTaskPriorityScore(taskId: Long): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            when (val taskDb = taskQueries.selectTaskById(id = taskId).executeAsOneOrNull()) {
                null -> Either.Left(TaskDoesNotExist)
                else -> {
                    val newPriorityScore = taskDb.priority_score + 1
                    taskQueries.updateTaskPriorityScore(newPriorityScore, taskId)
                    Either.Right(newPriorityScore)
                }
            }
        }
    }

    override suspend fun decreaseTaskPriorityScore(taskId: Long): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            when (val taskDb = taskQueries.selectTaskById(id = taskId).executeAsOneOrNull()) {
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
        taskId: Long,
        priorityScore: Long,
    ): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            taskQueries.incrementTasksPriorityScore(priorityScore)
            taskQueries.updateTaskPriorityScore(priorityScore, taskId)
            Either.Right(priorityScore)
        }
    }

    override suspend fun updateTasksPriorityScoreBySubtracting(
        taskId: Long,
        priorityScore: Long,
    ): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            taskQueries.decrementTasksPriorityScore(priorityScore)
            taskQueries.updateTaskPriorityScore(priorityScore, taskId)
            Either.Right(priorityScore)
        }
    }

    override suspend fun updateTaskPriority(
        taskId: Long,
        priorityScore: Long,
    ): Either<Failure, Long> {
        logger.d { "Actualizando prioridad de task $taskId a $priorityScore" }

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