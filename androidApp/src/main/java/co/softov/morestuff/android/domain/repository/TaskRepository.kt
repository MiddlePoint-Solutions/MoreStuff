package co.softov.morestuff.android.domain.repository


import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.model.Scope

interface TaskRepository {

    suspend fun createTask(title: String): Either<Failure, Scope>
    suspend fun getTask(taskId: Long): Either<Failure,Scope>
    suspend fun setTaskComplete(taskId: Long): Either<Failure,Boolean>
    suspend fun getActiveTasksFlow(): Flow<List<Scope>>
    suspend fun getCompleteTasksFlow(): Flow<List<Scope>>
}

object TaskDoesNotExist: Failure.FeatureFailure()