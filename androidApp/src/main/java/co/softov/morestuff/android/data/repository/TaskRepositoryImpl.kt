package co.softov.morestuff.android.data.repository

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.right
import arrow.core.rightIfNotNull
import co.softov.morestuff.android.data.mapper.TaskWithScheduleDataMapper
import co.softov.morestuff.android.data.mapper.TaskDb
import co.softov.morestuff.android.data.mapper.TaskDataMapper
import co.softov.morestuff.android.data.mapper.mapCompleteTaskData
import co.softov.morestuff.android.data.mapper.mapList
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskDoesNotExist
import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class TaskRepositoryImpl(
    database: StuffDb,
    private val mapTaskData: TaskDataMapper,
    private val mapTaskWithScheduleData: TaskWithScheduleDataMapper,
    private val timeManager: TimeManager,
) : TaskRepository {

    private val taskQueries = database.taskQueries
    private fun getLastInsertedRowId(): Long = taskQueries.lastInsertRowId().executeAsOne()

    override suspend fun createTask(
        title: String,
        priorityScore: Long,
        taskType: TaskType,
    ): TaskDomain {
        return taskQueries.transactionWithResult {
            val data = createTaskData(
                title = title,
                priorityScore = priorityScore,
                taskType = taskType
            )
            taskQueries.insertTask(data)
            val taskId = getLastInsertedRowId()
            taskQueries.selectTaskById(taskId, mapper = mapTaskData).executeAsOne()
        }
    }

    override suspend fun getTask(taskId: Long): Either<Failure, TaskDomain> =
        taskQueries.selectTaskById(id = taskId, mapper = mapTaskData).executeAsOneOrNull()
            .rightIfNotNull { TaskDoesNotExist }

    override fun getTaskFlow(taskId: Long): Flow<TaskDomain> =
        taskQueries.selectTaskById(id = taskId, mapper = mapTaskData).asFlow().mapToOne()

    override fun getActiveTasksFlow(): Flow<List<TaskDomain>> =
        taskQueries.selectAllActive(mapper = mapTaskWithScheduleData).asFlow().mapToList()

    override fun getNowTasksFlow(): Flow<List<TaskDomain>> =
        taskQueries.selectHighestPriorityTasks(mapper = mapTaskData).asFlow().mapToList()

    override fun getLaterTasksFlow(): Flow<List<TaskDomain>> =
        taskQueries.selectLowestPriorityTasks(mapper = mapTaskData).asFlow().mapToList()

    override fun getCompleteTasksFlow(): Flow<List<TaskDomain>> =
        taskQueries.selectAllComplete(mapper = mapTaskData).asFlow().mapToList()

    override suspend fun getHighestPriorityScore(): Long = taskQueries
        .selectHighestPriorityScore().executeAsOne().max ?: 0

    override suspend fun getLowestPriorityScore(): Long = taskQueries
        .selectLowestPriorityScore().executeAsOne().min ?: 0

    override suspend fun updateTasksComplete(
        taskIds: List<Long>,
        complete: Boolean,
    ): Either<Failure, Boolean> {
        val time = when (complete) {
            true -> timeManager.nowLocalDateTimeString
            false -> null
        }

        return taskQueries.transactionWithResult {
            taskIds.forEach {
                taskQueries.updateTaskComplete(time, it)
            }
            Right(true)
        }
    }

    override suspend fun updateTaskTitle(taskId: Long, title: String): Either<Failure, Boolean> {
        taskQueries.updateTaskTitle(title, taskId)
        return Right(true)
    }

    private fun createTaskData(
        title: String,
        priorityScore: Long,
        taskType: TaskType,
    ) = TaskDb(
        id = 0,
        uuid = UUID.randomUUID().toString(),
        title = title,
        create_time = timeManager.getCreateTime(),
        complete_time = null,
        priority_score = priorityScore,
        task_type = taskType
    )

    override suspend fun increaseTaskPriorityScore(taskId: Long): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            when (val taskDb = taskQueries.selectTaskById(id = taskId).executeAsOneOrNull()) {
                null -> Left(TaskDoesNotExist)
                else -> {
                    val newPriorityScore = taskDb.priority_score + 1
                    taskQueries.updateTaskPriorityScore(newPriorityScore, taskId)
                    Right(newPriorityScore)
                }
            }
        }
    }

    override suspend fun decreaseTaskPriorityScore(taskId: Long): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            when (val taskDb = taskQueries.selectTaskById(id = taskId).executeAsOneOrNull()) {
                null -> Left(TaskDoesNotExist)
                else -> {
                    val newPriorityScore = taskDb.priority_score - 1
                    taskQueries.updateTaskPriorityScore(newPriorityScore, taskId)
                    Right(newPriorityScore)
                }
            }
        }
    }

    override suspend fun getTaskAbovePriorityScore(priorityScore: Long): Either<Failure, TaskDomain> {
        return taskQueries.selectAbovePriorityScore(priorityScore, mapper = mapTaskData)
            .executeAsOneOrNull()
            .rightIfNotNull { TaskDoesNotExist }
    }

    override suspend fun getTaskBelowPriorityScore(priorityScore: Long): Either<Failure, TaskDomain> {
        return taskQueries.selectBelowPriorityScore(priorityScore, mapper = mapTaskData)
            .executeAsOneOrNull()
            .rightIfNotNull { TaskDoesNotExist }
    }

    override suspend fun reorderTaskByAdding(
        taskId: Long,
        priorityScore: Long
    ): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            taskQueries.incrementTasksPriorityScore(priorityScore)
            taskQueries.updateTaskPriorityScore(priorityScore, taskId)
            Right(priorityScore)
        }
    }

    override suspend fun reorderTaskBySubtracting(
        taskId: Long,
        priorityScore: Long
    ): Either<Failure, Long> {
        return taskQueries.transactionWithResult {
            taskQueries.decrementTasksPriorityScore(priorityScore)
            taskQueries.updateTaskPriorityScore(priorityScore, taskId)
            Right(priorityScore)
        }
    }

    override suspend fun updateTaskPriority(
        taskId: Long,
        priorityScore: Long
    ): Either<Failure, Long> {
        taskQueries.updateTaskPriorityScore(priorityScore, taskId)
        return Right(priorityScore)
    }

    override suspend fun getTasksWithoutSchedule(): Either<Failure, List<TaskDomain>> =
        taskQueries.getActiveTaskWhithoutSchedule(mapper = mapTaskData)
            .executeAsList()
            .right()

    override suspend fun getTasksWithSchedule(): Either<Failure, List<TaskDomain>> {
        return taskQueries.selectActiveTasksWithSchedule(
            schedule_type = listOf(ScheduleType.OneTime),
            mapper = mapTaskWithScheduleData
        ).executeAsList().right()
    }

    override fun searchTasks(searchText: String): Flow<List<TaskDomain>> =
        taskQueries.searchTasks(searchText, mapper = mapTaskData).asFlow().mapToList()
}