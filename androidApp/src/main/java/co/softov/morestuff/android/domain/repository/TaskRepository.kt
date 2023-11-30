package co.softov.morestuff.android.domain.repository


import arrow.core.Either
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.FeatureFailure
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.model.TaskDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface TaskRepository {

    suspend fun createTask(
        title: String,
        priorityScore: Long,
        taskType: TaskType,
    ): TaskDomain

    suspend fun getTask(taskId: Long): Either<Failure, TaskDomain>
    suspend fun updateTasksComplete(
        taskIds: List<Long>,
        complete: Boolean,
    ): Either<Failure, Boolean>

    suspend fun updateTaskTitle(taskId: Long, title: String): Either<Failure, Boolean>
    fun getTaskFlow(taskId: Long): Flow<TaskDomain>
    fun getActiveTasksFlow(): Flow<List<TaskDomain>>
    fun getCompleteTasksFlow(): Flow<List<TaskDomain>>

    suspend fun getTaskAbovePriorityScore(priorityScore: Long): Either<Failure, TaskDomain>
    suspend fun getTaskBelowPriorityScore(priorityScore: Long): Either<Failure, TaskDomain>

    suspend fun getTasksWithoutSchedule(scopeId: Long): Either<Failure, List<TaskDomain>>
    suspend fun getTasksWithSchedule(scheduleTypes: List<ScheduleType>): Either<Failure, List<TaskDomain>>
    fun searchTasks(searchText: String): Flow<List<TaskDomain>>
    suspend fun countActiveTasks(): Either<Failure, Int>
    suspend fun deleteTasks(taskIds: List<Long>): Either<Failure, Boolean>

    suspend fun insertTaskIntoScope(taskId: Long, scopeId: Long)
    suspend fun removeTaskFromScope(taskId: Long, scopeId: Long)
    fun updateCurrentScopeId(newScopeId: Long)
    val currentScopeId: MutableStateFlow<Long>

}

object TaskDoesNotExist : FeatureFailure