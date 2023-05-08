package co.softov.morestuff.android.data.repository

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.right
import co.softov.morestuff.android.data.mapper.TaskData
import co.softov.morestuff.android.data.mapper.mapList
import co.softov.morestuff.android.data.mapper.taskDbMapper
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskDoesNotExist
import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class TaskRepositoryImpl(
    database: StuffDb,
    private val mapTaskDb: taskDbMapper,
    private val timeManager: TimeManager,
) : TaskRepository {

    private val taskQueries = database.taskQueries
    private fun getLastInsertedRowId(): Long = taskQueries.lastInsertRowId().executeAsOne()

    override suspend fun createTask(
        title: String,
        priorityScore: Long,
        taskType: TaskType,
    ): Either<Failure, TaskDomain> {
        return taskQueries.transactionWithResult {
            val data = createTaskData(
                title = title,
                priorityScore = priorityScore,
                taskType = taskType
            )
            taskQueries.insertTask(data)
            val taskId = getLastInsertedRowId()
            mapTaskDb(data)
                .copy(id = taskId)
                .right()
        }
    }

    override suspend fun getTask(taskId: Long): Either<Failure, TaskDomain> {
        return when (val taskDb = taskQueries.selectTaskById(id = taskId).executeAsOneOrNull()) {
            null -> Left(TaskDoesNotExist)
            else -> Right(mapTaskDb(taskDb))
        }
    }

    override fun getTaskFlow(taskId: Long): Flow<TaskDomain> {
        return taskQueries.selectTaskById(id = taskId).asFlow().mapToOne().map { mapTaskDb(it) }
    }

    override suspend fun getActiveTasksFlow(): Flow<List<TaskDomain>> {
        return taskQueries.selectAllActive().asFlow().mapToList().map { mapList(it, mapTaskDb) }
    }

    override suspend fun getCompleteTasksFlow(): Flow<List<TaskDomain>> = taskQueries
        .selectAllComplete(mapper = ::TaskDomain)
        .asFlow()
        .mapToList()

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
    ) = TaskData(
        id = 0,
        uuid = UUID.randomUUID().toString(),
        title = title,
        create_time = timeManager.getCreateTime(),
        complete_time = null,
        priority_score = priorityScore,
        task_type = taskType
    )
}