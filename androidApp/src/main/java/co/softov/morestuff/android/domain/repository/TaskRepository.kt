package co.softov.morestuff.android.domain.repository


import arrow.core.Either
import co.softov.morestuff.android.domain.enums.TaskType
import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.FeatureFailure
import co.softov.morestuff.android.domain.model.TaskDomain

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
    suspend fun reorderTask(taskId: Long, priorityScore: Long): Either<Failure, Long>
}

object TaskDoesNotExist : FeatureFailure