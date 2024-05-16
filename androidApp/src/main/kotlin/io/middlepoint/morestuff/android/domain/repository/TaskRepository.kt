package io.middlepoint.morestuff.android.domain.repository


import arrow.core.Either
import io.middlepoint.morestuff.android.domain.enums.ScheduleType
import io.middlepoint.morestuff.android.domain.enums.TaskType
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.FeatureFailure
import io.middlepoint.morestuff.android.domain.model.TaskDomain
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun createTask(
        title: String,
        priorityScore: Long,
        taskType: TaskType,
        scopeId: Long?,
    ): TaskDomain

    suspend fun getTask(taskId: Long): Either<Failure, TaskDomain>
    suspend fun updateTasksComplete(
        taskIds: List<Long>,
        complete: Boolean,
    ): Either<Failure, Boolean>

    suspend fun updateTaskTitle(taskId: Long, title: String): Either<Failure, Boolean>
    fun getTaskFlow(taskId: Long): Flow<TaskDomain>
    fun getActiveTasksFlow(): Flow<List<TaskDomain>>
    fun getScopeActiveTasksFlow(scopeId: Long): Flow<List<TaskDomain>>
    fun getCompleteTasksFlow(): Flow<List<TaskDomain>>

    suspend fun getTaskAbovePriorityScore(priorityScore: Long): Either<Failure, TaskDomain>
    suspend fun getTaskBelowPriorityScore(priorityScore: Long): Either<Failure, TaskDomain>
    suspend fun getTasksWithoutSchedule(scopeId: Long): Either<Failure, List<TaskDomain>>
    suspend fun getTasksWithSchedule(scheduleTypes: List<ScheduleType>): Either<Failure, List<TaskDomain>>
    fun searchTasks(searchText: String, activeOnly: Boolean): Flow<List<TaskDomain>>
    suspend fun countActiveTasks(): Either<Failure, Int>
    suspend fun deleteTasks(taskIds: List<Long>): Either<Failure, Boolean>
    suspend fun insertTasksIntoScope(taskIds: List<Long>, scopeId: Long)
    suspend fun removeTasksFromScope(taskIds: List<Long>, scopeId: Long)
    suspend fun updateTasksScope(taskIds: List<Long>, scopeId: Long)
}

object TaskDoesNotExist : FeatureFailure