package co.softov.morestuff.android.data.repository

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import co.softov.morestuff.android.data.mapper.mapList
import co.softov.morestuff.android.data.mapper.taskDbMapper
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.repository.TaskDoesNotExist
import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.android.data.service.TimeManager
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    database: StuffDb,
    private val mapTaskDb: taskDbMapper,
    private val timeManager: TimeManager,
) : TaskRepository {

    private val taskQueries = database.taskQueries
    private val lastInsertId: Long get() = taskQueries.lastInsertRowId().executeAsOne()

    override suspend fun createTask(title: String): Either<Failure, Task> {
        val currentTime = timeManager.getCreateTime()
        return taskQueries.transactionWithResult {
            taskQueries.insertTask(currentTime, title)
            val taskId = lastInsertId
            val task = Task(id = taskId, title = title, createTime = currentTime)
            Right(task)
        }
    }

    override suspend fun getTask(taskId: Long): Either<Failure, Task> {
        return when (val taskDb = taskQueries.selectTaskById(id = taskId).executeAsOneOrNull()) {
            null -> Left(TaskDoesNotExist)
            else -> Right(mapTaskDb(taskDb))
        }
    }

    override fun getTaskFlow(taskId: Long): Flow<Task> {
        return taskQueries.selectTaskById(id = taskId).asFlow().mapToOne().map { mapTaskDb(it) }
    }

    override suspend fun getActiveTasksFlow(): Flow<List<Task>> {
        return taskQueries.selectAllActive().asFlow().mapToList().map { mapList(it, mapTaskDb) }
    }

    override suspend fun getCompleteTasksFlow(): Flow<List<Task>> {
        return taskQueries.selectAllComplete(mapper = { id, create_time, complete_time, title ->
            Task(
                id,
                title,
                create_time,
                complete_time,
            )
        }).asFlow().mapToList()
    }

    override suspend fun updateTaskComplete(
        taskId: Long,
        complete: Boolean,
    ): Either<Failure, Boolean> {
        val time = when (complete) {
            true -> timeManager.nowLocalDateTimeString
            false -> null
        }
        taskQueries.updateTaskComplete(time, taskId)
        return Right(true)
    }

    override suspend fun updateTaskTitle(taskId: Long, title: String): Either<Failure, Boolean> {
        taskQueries.updateTaskTitle(title, taskId)
        return Right(true)
    }
}