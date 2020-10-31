package co.softov.morestuff.androidApp.domain.repository


import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.androidApp.domain.Failure
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.model.Task

interface TaskRepository {

    suspend fun createTask(title: String): SimpleResult<Task>
    suspend fun getTask(taskId: Long): SimpleResult<Task>
    suspend fun setTaskComplete(taskId: Long): SimpleResult<Boolean>
    suspend fun getActiveTasksFlow(): SimpleResult<Flow<List<Task>>>
    suspend fun getCompleteTasksFlow(): SimpleResult<Flow<List<Task>>>
}

object TaskDoesNotExist: Failure.FeatureFailure()