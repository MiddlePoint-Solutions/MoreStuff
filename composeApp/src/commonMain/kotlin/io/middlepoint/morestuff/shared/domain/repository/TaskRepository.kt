package io.middlepoint.morestuff.shared.domain.repository


import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.FeatureFailure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface TaskRepository {

    suspend fun createTask(
        title: String,
        scopeId: Uuid,
        priorityScore: Long,
        completedAt: Instant? = null,
        completedTimezone: String? = null,
    ): Task

    suspend fun getTask(taskId: Uuid): Either<Failure, Task>

    suspend fun getAllTasks(): List<Task>
    suspend fun getActiveTasks(): List<Task>

    suspend fun updateTasksComplete(
        taskIds: List<Uuid>,
        complete: Boolean,
    ): Either<Failure, Boolean>

    suspend fun updateTaskTitle(taskId: Uuid, title: String): Either<Failure, Boolean>
    fun getTaskFlow(taskId: Uuid): Flow<Task>
    fun getActiveTasksFlow(): Flow<List<Task>>
    fun getScopeActiveTasksFlow(scopeId: Uuid): Flow<List<Task>>
    suspend fun getScopeActiveTasks(scopeId: Uuid): List<Task>
    fun getCompleteTasksFlow(): Flow<List<Task>>

    suspend fun getTaskAbovePriorityScore(priorityScore: Long): Either<Failure, Task>
    suspend fun getTaskBelowPriorityScore(priorityScore: Long): Either<Failure, Task>
    suspend fun getTasksWithSchedule(scheduleTypes: List<ScheduleType>): Either<Failure, List<Task>>
    fun searchTasks(searchText: String, activeOnly: Boolean): Flow<List<Task>>
    suspend fun countActiveTasks(): Either<Failure, Int>
    suspend fun deleteTasks(taskIds: List<Uuid>): Either<Failure, Boolean>
    suspend fun insertTasksIntoScope(taskIds: List<Uuid>, scopeId: Uuid)
    suspend fun removeTasksFromScope(taskIds: List<Uuid>, scopeId: Uuid)
    suspend fun updateTasksScope(taskIds: List<Uuid>, scopeId: Uuid)
    suspend fun getTasksByIds(taskIds: List<Uuid>): Either<Failure, List<Task>>


}

object TaskDoesNotExist : FeatureFailure