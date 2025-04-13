package io.middlepoint.morestuff.shared.domain.repository


import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.enums.TaskType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.FeatureFailure
import io.middlepoint.morestuff.shared.domain.model.core.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun createTask(
        title: String,
        priorityScore: Long,
        taskType: TaskType,
        scopeId: Long?,
    ): Task

    suspend fun getTask(taskId: Long): Either<Failure, Task>
    suspend fun getAllTasks(): List<Task>

    suspend fun updateTasksComplete(
        taskIds: List<Long>,
        complete: Boolean,
    ): Either<Failure, Boolean>

    suspend fun updateTaskTitle(taskId: Long, title: String): Either<Failure, Boolean>
    fun getTaskFlow(taskId: Long): Flow<Task>
    fun getActiveTasksFlow(): Flow<List<Task>>
    fun getScopeActiveTasksFlow(scopeId: Long): Flow<List<Task>>
    fun getCompleteTasksFlow(): Flow<List<Task>>

    suspend fun getTaskAbovePriorityScore(priorityScore: Long): Either<Failure, Task>
    suspend fun getTaskBelowPriorityScore(priorityScore: Long): Either<Failure, Task>
    suspend fun getTasksWithoutSchedule(scopeId: Long): Either<Failure, List<Task>>
    suspend fun getTasksWithSchedule(scheduleTypes: List<ScheduleType>): Either<Failure, List<Task>>
    fun searchTasks(searchText: String, activeOnly: Boolean): Flow<List<Task>>
    suspend fun countActiveTasks(): Either<Failure, Int>
    suspend fun deleteTasks(taskIds: List<Long>): Either<Failure, Boolean>
    suspend fun insertTasksIntoScope(taskIds: List<Long>, scopeId: Long)
    suspend fun removeTasksFromScope(taskIds: List<Long>, scopeId: Long)
    suspend fun updateTasksScope(taskIds: List<Long>, scopeId: Long)
    suspend fun getTasksByIds(taskIds: List<Long>): Either<Failure, List<Task>>


}

object TaskDoesNotExist : FeatureFailure