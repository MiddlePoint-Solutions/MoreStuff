package io.middlepoint.morestuff.shared.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneNotNull
import arrow.core.Either
import arrow.core.Either.Right
import arrow.core.left
import arrow.core.right
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.data.mapper.DataMappers
import io.middlepoint.morestuff.shared.data.mapper.TaskDb
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.enums.TaskType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.domain.repository.TaskDoesNotExist
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.generateUUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
  database: StuffDb,
  private val mapper: DataMappers,
  private val timeManager: TimeManager,
) : TaskRepository {

  private val taskQueries = database.taskQueries
  private val scheduleQueries = database.scheduleQueries
  private val messageQueries = database.messageQueries
  private val taskScopeQueries = database.taskScopeQueries
  private val lastInsertedRowId get() = taskQueries.lastInsertRowId().executeAsOne()

  override suspend fun createTask(
    title: String,
    priorityScore: Long,
    taskType: TaskType,
    scopeId: Long?,
  ): Task {
    return taskQueries.transactionWithResult {
      val data = createTaskData(
        title = title,
        priorityScore = priorityScore,
        taskType = taskType
      )
      taskQueries.insertTask(data)
      val taskId = lastInsertedRowId

      scopeId?.let {
        taskScopeQueries.insert(taskId, it)
      }

      taskQueries.selectTaskById(taskId, mapper = mapper.taskDataMapper).executeAsOne()
    }
  }

  override suspend fun getTask(taskId: Long): Either<Failure, Task> =
    getTaskFlow(taskId).firstOrNull()?.right() ?: TaskDoesNotExist.left()

  override suspend fun getAllTasks(): List<Task> {
    return taskQueries.selectAllActive(mapper.taskDataMapper).executeAsList()
  }

  override fun getTaskFlow(taskId: Long): Flow<Task> {
    val taskFlow = taskQueries.selectTaskById(taskId, mapper.taskDataMapper)
      .asFlow()
      .mapToOneNotNull(Dispatchers.IO)

    val schedulesFlow =
      scheduleQueries.selectActiveSchedulesByTaskId(taskId, mapper.scheduleDataMapper)
        .asFlow()
        .mapToList(Dispatchers.IO)

    return combine(taskFlow, schedulesFlow) { task, schedules ->
      task.copy(schedule = schedules)
    }
  }

  override fun getActiveTasksFlow(): Flow<List<Task>> {
    val tasksFlow = taskQueries.selectAllActive(mapper.taskDataMapper)
      .asFlow().mapToList(Dispatchers.IO)
    return combinedTaskFlow(tasksFlow)
  }

  override fun getScopeActiveTasksFlow(scopeId: Long): Flow<List<Task>> {
    val tasksFlow = taskQueries.selectTasksByScopeId(scopeId, mapper.taskDataMapper)
      .asFlow().mapToList(Dispatchers.IO)
    return combinedTaskFlow(tasksFlow)
  }

  override fun getCompleteTasksFlow(): Flow<List<Task>> =
    taskQueries.selectAllComplete(mapper = mapper.taskDataMapper)
      .asFlow()
      .mapToList(Dispatchers.IO)

  override suspend fun getTaskAbovePriorityScore(
    priorityScore: Long,
  ): Either<Failure, Task> = taskQueries.transactionWithResult {
    val task = taskQueries.selectAbovePriorityScore(priorityScore, mapper = mapper.taskDataMapper)
      .executeAsOneOrNull()

    task?.let {
      val schedules =
        scheduleQueries.selectActiveSchedulesByTaskId(it.id, mapper.scheduleDataMapper)
          .executeAsList()
      task.copy(schedule = schedules).right()
    } ?: TaskDoesNotExist.left()
  }

  override suspend fun getTaskBelowPriorityScore(priorityScore: Long): Either<Failure, Task> =
    taskQueries.transactionWithResult {
      val task =
        taskQueries.selectBelowPriorityScore(priorityScore, mapper = mapper.taskDataMapper)
          .executeAsOneOrNull()

      task?.let {
        val schedules =
          scheduleQueries.selectActiveSchedulesByTaskId(it.id, mapper.scheduleDataMapper)
            .executeAsList()
        task.copy(schedule = schedules).right()
      } ?: TaskDoesNotExist.left()
    }

  override suspend fun updateTasksComplete(
    taskIds: List<Long>,
    complete: Boolean,
  ): Either<Failure, Boolean> {
    val time = when (complete) {
      true -> timeManager.nowUtcInstantString
      false -> null
    }

    return taskQueries.transactionWithResult {
      taskQueries.updateTaskComplete(time, taskIds)
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

  override suspend fun getTasksWithoutSchedule(
    scopeId: Long
  ): Either<Failure, List<Task>> {
    val tasksWithoutSchedule = taskQueries.getActiveTaskWithoutScheduleByScopeId(
      scope_id = scopeId,
      schedule_types = listOf(ScheduleType.OneTime),
      mapper = mapper.taskDataMapper
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
  ): Either<Failure, List<Task>> {

    val tasksFlow = taskQueries.selectAllActive(mapper.taskDataMapper)
      .asFlow()
      .mapToList(Dispatchers.IO)

    val schedulesFlow =
      scheduleQueries.selectActiveSchedules(scheduleTypes, mapper.scheduleDataMapper)
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
    }.firstOrNull()?.right() ?: TaskDoesNotExist.left()
  }

  override fun searchTasks(searchText: String, activeOnly: Boolean): Flow<List<Task>> {
    val isCyrillic = searchText.any { char ->
      char.code in 0x0400..0x04FF
    }
    if (searchText.isEmpty() || !isCyrillic) {
      return when (activeOnly) {
        true -> taskQueries.searchActiveTasks(searchText, mapper = mapper.taskDataMapper)
        false -> taskQueries.searchTasks(searchText, mapper = mapper.taskDataMapper)
      }.asFlow()
        .mapToList(Dispatchers.IO)
        .let { combinedTaskFlow(it) }
    } else {
      val tasksFlow = when (activeOnly) {
        true -> taskQueries.selectAllActiveForSearch(mapper.taskDataMapper)
        false -> taskQueries.selectAllForSearch(mapper.taskDataMapper)
      }.asFlow().mapToList(Dispatchers.IO)

      return tasksFlow
        .map { tasks ->
          tasks.filter { task ->
            task.title.contains(searchText, ignoreCase = true)
          }
        }
        .let { combinedTaskFlow(it) }
    }
  }

  private fun createTaskData(
    title: String,
    priorityScore: Long,
    taskType: TaskType,
  ) = TaskDb(
    id = 0,
    uuid = generateUUID(),
    title = title,
    create_time = timeManager.getCreateTime(),
    complete_time = null,
    priority_score = priorityScore,
    task_type = taskType
  )

  override suspend fun deleteTasks(taskIds: List<Long>): Either<Failure, Boolean> {
    taskQueries.deleteTask(taskIds)
    return Right(true)
  }

  override suspend fun countActiveTasks(): Either<Failure, Int> =
    taskQueries.countActiveTasks().executeAsOne().toInt().right()


  override suspend fun insertTasksIntoScope(taskIds: List<Long>, scopeId: Long) {
    taskScopeQueries.transaction {
      taskIds.forEach { taskScopeQueries.insert(it, scopeId) }
    }
  }

  override suspend fun removeTasksFromScope(taskIds: List<Long>, scopeId: Long) {
    taskScopeQueries.remove(taskIds, scopeId)
  }

  override suspend fun updateTasksScope(taskIds: List<Long>, scopeId: Long) {
    taskScopeQueries.update(scope_id = scopeId, task_ids = taskIds)
  }

  private fun combinedTaskFlow(tasksFlow: Flow<List<Task>>): Flow<List<Task>> {
    val schedulesFlow =
      scheduleQueries.selectActiveSchedules(ScheduleType.entries, mapper.scheduleDataMapper)
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

  override suspend fun getTasksByIds(taskIds: List<Long>): Either<Failure, List<Task>> {
    if (taskIds.isEmpty()) return Either.Right(listOf())

    return taskQueries.transactionWithResult {
      val tasks = mutableListOf<Task>()

      for (taskId in taskIds) {
        val task = taskQueries.selectTaskById(taskId, mapper.taskDataMapper).executeAsOneOrNull()
          ?: continue

        val schedules = scheduleQueries
          .selectActiveSchedulesByTaskId(taskId, mapper.scheduleDataMapper)
          .executeAsList()

        tasks.add(task.copy(schedule = schedules))
      }

      tasks.right()
    }
  }
}
