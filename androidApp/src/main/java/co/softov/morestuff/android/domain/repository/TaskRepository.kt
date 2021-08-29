package co.softov.morestuff.android.domain.repository


import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.model.SimpleResult
import co.softov.morestuff.android.domain.model.Task

interface TaskRepository {

    suspend fun createTask(title: String): SimpleResult<Task>
    suspend fun getTask(taskId: Long): SimpleResult<Task>
    suspend fun setTaskComplete(taskId: Long): SimpleResult<Boolean>
    suspend fun getActiveTasksFlow(): Flow<List<Task>>
    suspend fun getCompleteTasksFlow(): Flow<List<Task>>
}

object TaskDoesNotExist: Failure.FeatureFailure()