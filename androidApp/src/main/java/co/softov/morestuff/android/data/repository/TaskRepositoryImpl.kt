package co.softov.morestuff.android.data.repository

import arrow.core.Either
import arrow.core.Either.Right
import arrow.core.left
import arrow.core.right
import arrow.core.rightIfNotNull
import co.softov.morestuff.android.data.mapper.ScheduleDbMapper
import co.softov.morestuff.android.data.mapper.TaskDataMapper
import co.softov.morestuff.android.data.mapper.TaskDb
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskDoesNotExist
import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.UUID

class TaskRepositoryImpl(
    database: StuffDb,
    private val mapTaskData: TaskDataMapper,
    private val mapScheduleDb: ScheduleDbMapper,
    private val timeManager: TimeManager,
) : TaskRepository {

    private val taskQueries = database.taskQueries
    private val scheduleQueries = database.scheduleQueries
    private val lastInsertedRowId get() = taskQueries.lastInsertRowId().executeAsOne()

    override suspend fun createTask(
        title: String,
        priorityScore: Long,
        taskType: TaskType,
    ): TaskDomain {
        return taskQueries.transactionWithResult {
            val data = createTaskData(
                title = title,
                priorityScore = priorityScore,
                taskType = taskType
            )
            taskQueries.insertTask(data)
            val taskId = lastInsertedRowId
            taskQueries.selectTaskById(taskId, mapper = mapTaskData).executeAsOne()
        }
    }

    override suspend fun getTask(taskId: Long): Either<Failure, TaskDomain> =
        getTaskFlow(taskId).firstOrNull().rightIfNotNull { TaskDoesNotExist }

    override fun getTaskFlow(taskId: Long): Flow<TaskDomain> {
        val taskFlow = taskQueries.selectTaskById(taskId, mapTaskData)
            .asFlow()
            .mapToOne()

        val schedulesFlow = scheduleQueries.selectActiveSchedulesByTaskId(taskId, mapScheduleDb)
            .asFlow()
            .mapToList()

        return taskFlow.combine(schedulesFlow) { task, schedules ->
            task.copy(schedule = schedules)
        }
    }

    override fun getActiveTasksFlow(): Flow<List<TaskDomain>> {
        val tasksFlow = taskQueries.selectAllActive(mapTaskData)
            .asFlow()
            .mapToList()

        val schedulesFlow = scheduleQueries.selectActiveSchedules(mapScheduleDb)
            .asFlow()
            .mapToList()
            .map { it.groupBy { schedule -> schedule.taskId } }

        return tasksFlow.combine(schedulesFlow) { tasks, schedules ->
            tasks.map { task ->
                task.copy(schedule = schedules[task.id] ?: listOf()).also {
                    Timber.d("task schedules: ${it.schedule.size}")
                }
            }
        }
    }

    override fun getCompleteTasksFlow(): Flow<List<TaskDomain>> =
        taskQueries.selectAllComplete(mapper = mapTaskData).asFlow().mapToList()

    override suspend fun getTaskAbovePriorityScore(
        priorityScore: Long
    ): Either<Failure, TaskDomain> = taskQueries.transactionWithResult {
        val task = taskQueries.selectAbovePriorityScore(priorityScore, mapper = mapTaskData)
            .executeAsOneOrNull()

        task?.let {
            val schedules = scheduleQueries.selectActiveSchedulesByTaskId(it.id, mapScheduleDb)
                .executeAsList()
            task.copy(schedule = schedules).right()
        } ?: TaskDoesNotExist.left()
    }

    override suspend fun getTaskBelowPriorityScore(priorityScore: Long): Either<Failure, TaskDomain> =
        taskQueries.transactionWithResult {
            val task = taskQueries.selectBelowPriorityScore(priorityScore, mapper = mapTaskData)
                .executeAsOneOrNull()

            task?.let {
                val schedules = scheduleQueries.selectActiveSchedulesByTaskId(it.id, mapScheduleDb)
                    .executeAsList()
                task.copy(schedule = schedules).right()
            } ?: TaskDoesNotExist.left()
        }

    override suspend fun updateTasksComplete(
        taskIds: List<Long>,
        complete: Boolean,
    ): Either<Failure, Boolean> {
        val time = when (complete) {
            true -> timeManager.nowLocalDateTimeString
            false -> null
        }

        return taskQueries.transactionWithResult {
            taskIds.forEach {
                taskQueries.updateTaskComplete(time, it)
            }
            Right(true)
        }
    }

    override suspend fun updateTaskTitle(
        taskId: Long,
        title: String
    ): Either<Failure, Boolean> {
        taskQueries.updateTaskTitle(title, taskId)
        return Right(true)
    }

    override suspend fun getTasksWithoutSchedule(): Either<Failure, List<TaskDomain>> =
        taskQueries.getActiveTaskWithoutSchedule(
            listOf(ScheduleType.OneTime),
            mapper = mapTaskData
        ).executeAsList()
            .right()

    override suspend fun getTasksWithSchedule(
        scheduleTypes: List<ScheduleType>
    ): Either<Failure, List<TaskDomain>> {

        val tasksFlow = taskQueries.selectAllActive(mapTaskData)
            .asFlow()
            .mapToList()

        val schedulesFlow = scheduleQueries.selectActiveSchedules(mapScheduleDb)
            .asFlow()
            .mapToList()

        return tasksFlow.combine(schedulesFlow) { tasks, schedules ->
            schedules
                .groupBy { it.taskId }
                .run {
                    tasks
                        .filter { containsKey(it.id) }
                        .map { task ->
                            task.copy(
                                schedule = get(task.id) ?: listOf()
                            )
                        }
                }
        }.firstOrNull().rightIfNotNull { TaskDoesNotExist }
    }

    override fun searchTasks(searchText: String): Flow<List<TaskDomain>> =
        taskQueries.searchTasks(searchText, mapper = mapTaskData).asFlow().mapToList()

    private fun createTaskData(
        title: String,
        priorityScore: Long,
        taskType: TaskType,
    ) = TaskDb(
        id = 0,
        uuid = UUID.randomUUID().toString(),
        title = title,
        create_time = timeManager.getCreateTime(),
        complete_time = null,
        priority_score = priorityScore,
        task_type = taskType
    )

}