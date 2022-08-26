package co.softov.morestuff.android.domain.repository


import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.model.Task

interface TaskRepository {

    suspend fun createTask(title: String): Either<Failure, Task>
    suspend fun getTask(taskId: Long): Either<Failure,Task>
    suspend fun setTaskComplete(taskId: Long): Either<Failure,Boolean>
    suspend fun getActiveTasksFlow(): Flow<List<Task>>
    suspend fun getCompleteTasksFlow(): Flow<List<Task>>
}

object TaskDoesNotExist: Failure.FeatureFailure()