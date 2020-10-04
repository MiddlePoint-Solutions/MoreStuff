package co.softov.morestuff.androidApp.data.repository

import co.softov.morestuff.androidApp.data.mapper.mapList
import co.softov.morestuff.androidApp.data.mapper.taskDbMapper
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.model.Task
import co.softov.morestuff.androidApp.domain.repository.TaskDoesNotExist
import co.softov.morestuff.androidApp.domain.repository.TaskRepository
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class TaskRepositoryImpl(
    database: StuffDb,
    private val mapTaskDb: taskDbMapper
) : TaskRepository {

    private val taskQueries = database.taskQueries

    override suspend fun createTask(title: String): SimpleResult<Long> {
        val currentTime = Calendar.getInstance().timeInMillis
        taskQueries.insertTask(currentTime, title)
        return Result.Success(taskQueries.lastInsertRowId().executeAsOne())
    }

    override suspend fun getTask(taskId: Long): SimpleResult<Task> {
        return when (val taskDb = taskQueries.selectTaskById(id = taskId).executeAsOneOrNull()) {
            null -> Result.Failure(TaskDoesNotExist)
            else -> Result.Success(mapTaskDb(taskDb))
        }
    }

    override suspend fun getActiveTasksFlow(): SimpleResult<Flow<List<Task>>> {
        return Result.Success(taskQueries
            .selectAllActive()
            .asFlow()
            .mapToList()
            .map { mapList(it, mapTaskDb) })
    }

    override suspend fun getCompleteTasksFlow(): SimpleResult<Flow<List<Task>>> {
        return Result.Success(
            taskQueries.selectAllComplete().asFlow().mapToList().map { mapList(it, mapTaskDb) })
    }

    override suspend fun setTaskComplete(taskId: Long): SimpleResult<Boolean> {
        taskQueries.updateTaskComplete(Calendar.getInstance().timeInMillis, taskId)
        return Result.Success(true)
    }
}