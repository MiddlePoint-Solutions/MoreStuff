package co.softov.morestuff.android.data.repository

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import co.softov.morestuff.android.data.mapper.mapList
import co.softov.morestuff.android.data.mapper.taskDbMapper
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.model.Scope
import co.softov.morestuff.android.domain.repository.TaskDoesNotExist
import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    database: StuffDb,
    private val mapTaskDb: taskDbMapper
) : TaskRepository {

    private val taskQueries = database.taskQueries
    private val lastInsertId: Long get() = taskQueries.lastInsertRowId().executeAsOne()

    override suspend fun createTask(title: String): Either<Failure,Scope> {
        val currentTime = TimeUtils.currentLocalDateTimeString
        return taskQueries.transactionWithResult {
            taskQueries.insertTask(currentTime, title)
            val taskId = lastInsertId
            val scope = Scope(id = taskId, title = title, createTime = currentTime)
            Right(scope)
        }
    }

    override suspend fun getTask(taskId: Long): Either<Failure,Scope> {
        return when (val taskDb = taskQueries.selectTaskById(id = taskId).executeAsOneOrNull()) {
            null -> Left(TaskDoesNotExist)
            else -> Right(mapTaskDb(taskDb))
        }
    }

    override suspend fun getActiveTasksFlow(): Flow<List<Scope>> {
        return taskQueries.selectAllActive().asFlow().mapToList().map { mapList(it, mapTaskDb) }
    }

    override suspend fun getCompleteTasksFlow(): Flow<List<Scope>> {
        return taskQueries.selectAllComplete().asFlow().mapToList().map { mapList(it, mapTaskDb) }
    }

    override suspend fun setTaskComplete(taskId: Long): Either<Failure,Boolean> {
        val time = TimeUtils.currentLocalDateTimeString
        taskQueries.updateTaskComplete(time, taskId)
        return Right(true)
    }
}