package co.softov.morestuff.android.domain.repository


import arrow.core.Either
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.FeatureFailure
import co.softov.morestuff.android.domain.model.TaskDomain
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun createTask(
        title: String,
        priorityScore: Long,
        taskType: TaskType,
    ): Either<Failure, TaskDomain>

    suspend fun getTask(taskId: Long): Either<Failure, TaskDomain>
    suspend fun updateTasksComplete(
        taskIds: List<Long>,
        complete: Boolean,
    ): Either<Failure, Boolean>

    suspend fun updateTaskTitle(taskId: Long, title: String): Either<Failure, Boolean>
    fun getTaskFlow(taskId: Long): Flow<TaskDomain>
    fun getActiveTasksFlow(): Flow<List<TaskDomain>>
    fun getNowTasksFlow(): Flow<List<TaskDomain>>
    fun getLaterTasksFlow(): Flow<List<TaskDomain>>
    fun getCompleteTasksFlow(): Flow<List<TaskDomain>>
    suspend fun getHighestPriorityScore(): Long
    suspend fun getLowestPriorityScore(): Long
    suspend fun increaseTaskPriorityScore(taskId: Long): Either<Failure, Long>
    suspend fun decreaseTaskPriorityScore(taskId: Long): Either<Failure, Long>
    suspend fun reorderTaskByAdding(
        taskId: Long,
        priorityScore: Long,
    ): Either<Failure, Long>

    suspend fun reorderTaskBySubtracting(
        taskId: Long,
        priorityScore: Long
    ): Either<Failure, Long>

    suspend fun updateTaskPriority(taskId: Long, priorityScore: Long): Either<Failure, Long>
    suspend fun getTasksWithoutScheduleFlow(): Flow<List<TaskDomain>>
    suspend fun getActiveTasksWithScheduleFlow(): Flow<List<TaskDomain>>
}

object TaskDoesNotExist : FeatureFailure