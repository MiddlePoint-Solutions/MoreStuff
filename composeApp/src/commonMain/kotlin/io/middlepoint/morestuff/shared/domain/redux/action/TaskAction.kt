package io.middlepoint.morestuff.shared.domain.redux.action

import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.domain.redux.store.Action

sealed class TaskAction : Action.FeatureAction() {

  data class CreateUserTaskAction(
    val title: String,
    val priority: Priority,
    val scopeId: Long,
    val onTaskCreated: ((Task) -> Unit)? = null
  ) : TaskAction()

  data class CompleteTasksAction(val taskIds: List<Long>, val complete: Boolean) : TaskAction()

  data class UpdateTaskTitleAction(val taskId: Long, val title: String) : TaskAction()

  data object CreateHintTask : TaskAction()

  data class DeleteTasksAction(val taskIds: List<Long>) : TaskAction()

  data class TaskCreatedAction(
    val task: Task,
    val priority: Priority,
  ) : TaskAction()

  data class UpdateTasksToScopeAction(val taskIds: List<Long>, val scopeId: Long) : TaskAction()
  data class RemoveTasksFromScopeAction(val taskIds: List<Long>, val scopeId: Long) : TaskAction()
}