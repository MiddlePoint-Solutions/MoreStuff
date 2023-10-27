package co.softov.morestuff.android.data.repository

import arrow.core.Either
import arrow.core.Either.Right
import arrow.core.left
import arrow.core.right
import arrow.core.rightIfNotNull
import co.softov.morestuff.android.data.mapper.DataMappers
import co.softov.morestuff.android.data.mapper.TaskDb
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskDoesNotExist
import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.db.StuffDb
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import co.softov.morestuff.android.domain.enums.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID

class TaskRepositoryImpl(
    database: StuffDb,
    private val mapper: DataMappers,
    private val timeManager: TimeManager,
) : TaskRepository {

    private val taskQueries = database.taskQueries
    private val scheduleQueries = database.scheduleQueries
    private val messageQueries = database.messageQueries
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
            taskQueries.selectTaskById(taskId, mapper = mapper.taskDbMapper).executeAsOne()
        }
    }

    override suspend fun getTask(taskId: Long): Either<Failure, TaskDomain> =
        getTaskFlow(taskId).firstOrNull().rightIfNotNull { TaskDoesNotExist }

    override fun getTaskFlow(taskId: Long): Flow<TaskDomain> {
        val taskFlow = taskQueries.selectTaskById(taskId, mapper.taskDbMapper)
            .asFlow()
            .mapToOne(Dispatchers.IO)

        val schedulesFlow =
            scheduleQueries.selectActiveSchedulesByTaskId(taskId, mapper.scheduleDbMapper)
                .asFlow()
                .mapToList(Dispatchers.IO)

        return taskFlow.combine(schedulesFlow) { task, schedules ->
            task.copy(schedule = schedules)
        }
    }

    override fun getActiveTasksFlow(): Flow<List<TaskDomain>> {
        val tasksFlow = taskQueries.selectAllActive(mapper.taskDbMapper)
            .asFlow()
            .mapToList(Dispatchers.IO)
        val allScheduleTypes = listOf(ScheduleType.OneTime, ScheduleType.Reminder)
        val schedulesFlow =
            scheduleQueries.selectActiveSchedules(allScheduleTypes, mapper.scheduleDbMapper)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { it.groupBy { schedule -> schedule.taskId } }

        val messagesFlow =
            messageQueries.selectFirstTaskMessageWithType(ContentType.TASK_MESSAGE.value)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { messages ->
                    messages.groupBy { message -> message.task_id }
                        .mapValues { (_, messagesForTask) -> messagesForTask.firstOrNull() }
                }

        return combine(tasksFlow, schedulesFlow, messagesFlow) { tasks, schedules, messageTaskIds ->
            tasks.map { task ->
                task.copy(
                    schedule = schedules[task.id] ?: listOf(),
                    extraDetails = messageTaskIds.contains(task.id)
                )
            }
        }
    }

    override fun getCompleteTasksFlow(): Flow<List<TaskDomain>> =
        taskQueries.selectAllComplete(mapper = mapper.taskDbMapper)
            .asFlow()
            .mapToList(Dispatchers.IO)

    override suspend fun getTaskAbovePriorityScore(
        priorityScore: Long,
    ): Either<Failure, TaskDomain> = taskQueries.transactionWithResult {
        val task = taskQueries.selectAbovePriorityScore(priorityScore, mapper = mapper.taskDbMapper)
            .executeAsOneOrNull()

        task?.let {
            val schedules =
                scheduleQueries.selectActiveSchedulesByTaskId(it.id, mapper.scheduleDbMapper)
                    .executeAsList()
            task.copy(schedule = schedules).right()
        } ?: TaskDoesNotExist.left()
    }

    override suspend fun getTaskBelowPriorityScore(priorityScore: Long): Either<Failure, TaskDomain> =
        taskQueries.transactionWithResult {
            val task =
                taskQueries.selectBelowPriorityScore(priorityScore, mapper = mapper.taskDbMapper)
                    .executeAsOneOrNull()

            task?.let {
                val schedules =
                    scheduleQueries.selectActiveSchedulesByTaskId(it.id, mapper.scheduleDbMapper)
                        .executeAsList()
                task.copy(schedule = schedules).right()
            } ?: TaskDoesNotExist.left()
        }

    override suspend fun updateTasksComplete(
        taskId: Long,
        complete: Boolean,
    ): Either<Failure, Boolean> {
        val time = when (complete) {
            true -> timeManager.nowLocalDateTimeString
            false -> null
        }

        return taskQueries.transactionWithResult {
            taskQueries.updateTaskComplete(time, taskId)
            Right(true)
        }
    }

    override suspend fun updateTaskTitle(
        taskId: Long,
        title: String,
    ): Either<Failure, Boolean> {
        taskQueries.updateTaskTitle(title, taskId)
        return Right(true)
    }

    override suspend fun getTasksWithoutSchedule(): Either<Failure, List<TaskDomain>> {
        val tasksWithoutSchedule = taskQueries.getActiveTaskWithoutSchedule(
            listOf(ScheduleType.OneTime),
            mapper = mapper.taskDbMapper
        ).executeAsList()

        val firstTaskMessagesWithType =
            messageQueries.selectFirstTaskMessageWithType(ContentType.TASK_MESSAGE.value)
                .executeAsList()
                .map { it.task_id }
                .toSet()

        val tasksWithExtraDetails = tasksWithoutSchedule.map { task ->
            task.copy(
                extraDetails = firstTaskMessagesWithType.contains(task.id)
            )
        }
        return tasksWithExtraDetails.right()
    }


    override suspend fun getTasksWithSchedule(
        scheduleTypes: List<ScheduleType>,
    ): Either<Failure, List<TaskDomain>> {

        val tasksFlow = taskQueries.selectAllActive(mapper.taskDbMapper)
            .asFlow()
            .mapToList(Dispatchers.IO)

        val schedulesFlow =
            scheduleQueries.selectActiveSchedules(scheduleTypes, mapper.scheduleDbMapper)
                .asFlow()
                .mapToList(Dispatchers.IO)

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
        taskQueries.searchTasks(searchText, mapper = mapper.taskDbMapper)
            .asFlow()
            .mapToList(Dispatchers.IO)

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

    override suspend fun countActiveTasks(): Either<Failure, Int> =
        taskQueries.countActiveTasks().executeAsOne().toInt().right()

    override suspend fun deleteTask(taskId: Long): Either<Failure, Boolean> {
        taskQueries.transaction {
            taskQueries.deleteTask(taskId)
        }
        return Right(true)
    }

}