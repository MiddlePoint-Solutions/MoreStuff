package io.middlepoint.morestuff.shared.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneNotNull
import arrow.core.Either
import arrow.core.Either.Right
import arrow.core.left
import arrow.core.right
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.db.Tasks_scopes
import io.middlepoint.morestuff.shared.data.mapper.DataMappers
import io.middlepoint.morestuff.shared.data.model.TaskData
import io.middlepoint.morestuff.shared.data.utils.generate
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.domain.repository.TaskDoesNotExist
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class TaskRepositoryImpl(
  database: StuffDb,
  private val mapper: DataMappers,
  private val timeManager: TimeManager,
) : TaskRepository {

  private val taskQueries = database.tasksQueries
  private val scheduleQueries = database.schedulesQueries
  private val messageQueries = database.messagesQueries
  private val taskScopeQueries = database.tasksScopesQueries

  override suspend fun createTask(
    title: String,
    scopeId: Uuid,
    priorityScore: Long,
  ): Task {
    return taskQueries.transactionWithResult {

      val data = createTaskData(
        title = title,
        createdAt = timeManager.nowUtcInstant,
        priorityScore = priorityScore,
      )
      taskQueries.insertTask(data)

      val tasksScopesItem = Tasks_scopes(
        task_id = data.id,
        scope_id = scopeId,
        created_at = data.created_at,
        updated_at = data.updated_at
      )
      taskScopeQueries.insert(tasksScopesItem)

      taskQueries.selectTaskById(data.id, mapper = mapper.taskDataMapper).executeAsOne()
    }
  }

  override suspend fun getTask(taskId: Uuid): Either<Failure, Task> =
    taskQueries.selectTaskById(taskId, mapper.taskDataMapper)
      .executeAsOneOrNull()?.right() ?: TaskDoesNotExist.left()

  override suspend fun getAllTasks(): List<Task> =
    taskQueries.selectAll(mapper = mapper.taskDataMapper).executeAsList()

  override suspend fun getActiveTasks(): List<Task> {
    return taskQueries.selectAllActive(mapper = mapper.taskDataMapper).executeAsList()
  }

  override fun getTaskFlow(taskId: Uuid): Flow<Task> {
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

  override fun getScopeActiveTasksFlow(scopeId: Uuid): Flow<List<Task>> {
    val tasksFlow = taskQueries.selectTasksByScopeId(scopeId, mapper.taskDataMapper)
      .asFlow().mapToList(Dispatchers.IO)
    return combinedTaskFlow(tasksFlow)
  }

  override suspend fun getScopeActiveTasks(scopeId: Uuid): List<Task> {
    return taskQueries.selectTasksByScopeId(scopeId, mapper.taskDataMapper).executeAsList()
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
    taskIds: List<Uuid>,
    complete: Boolean,
  ): Either<Failure, Boolean> {
    val time = when (complete) {
      true -> timeManager.nowUtcInstant
      false -> null
    }

    return taskQueries.transactionWithResult {
      taskQueries.updateTaskComplete(time, taskIds)
      Right(true)
    }
  }

  override suspend fun updateTaskTitle(
    taskId: Uuid,
    title: String,
  ): Either<Failure, Boolean> {
    taskQueries.updateTaskTitle(title, taskId)
    return Right(true)
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
    createdAt: Instant,
    priorityScore: Long,
  ) = TaskData(
    id = Uuid.generate(),
    title = title,
    created_at = createdAt,
    updated_at = createdAt,
    completed_at = null,
    completed_timezone = null,
    priority_score = priorityScore
  )

  override suspend fun deleteTasks(taskIds: List<Uuid>): Either<Failure, Boolean> {
    taskQueries.deleteTask(taskIds)
    return Right(true)
  }

  override suspend fun countActiveTasks(): Either<Failure, Int> =
    taskQueries.countActiveTasks().executeAsOne().toInt().right()


  override suspend fun insertTasksIntoScope(taskIds: List<Uuid>, scopeId: Uuid) {
    taskScopeQueries.transaction {
      val tasksScope = taskIds.map { createTasksScopeData(it, scopeId) }
      tasksScope.forEach { taskScopeQueries.insert(it) }
    }
  }

  private fun createTasksScopeData(
    it: Uuid,
    scopeId: Uuid
  ): Tasks_scopes {
    val createdAt = timeManager.nowUtcInstant
    return Tasks_scopes(
      task_id = it,
      scope_id = scopeId,
      created_at = createdAt,
      updated_at = createdAt
    )
  }

  override suspend fun removeTasksFromScope(taskIds: List<Uuid>, scopeId: Uuid) {
    taskScopeQueries.remove(taskIds, scopeId)
  }

  override suspend fun updateTasksScope(taskIds: List<Uuid>, scopeId: Uuid) {
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

  override suspend fun getTasksByIds(taskIds: List<Uuid>): Either<Failure, List<Task>> {
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
