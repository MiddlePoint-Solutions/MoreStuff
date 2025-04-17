package io.middlepoint.morestuff.shared.domain.redux.action

import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.domain.redux.store.Action

sealed class TaskAction : Action.FeatureAction() {

  data class CreateUserTaskAction(
    val title: String,
    val priority: Priority,
    val scopeId: Uuid,
    val onTaskCreated: ((Task) -> Unit)? = null
  ) : TaskAction()

  data class CompleteTasksAction(val taskIds: List<Uuid>, val complete: Boolean) : TaskAction()

  data class UpdateTaskTitleAction(val taskId: Uuid, val title: String) : TaskAction()

  data object CreateHintTask : TaskAction()

  data class DeleteTasksAction(val taskIds: List<Uuid>) : TaskAction()

  data class TaskCreatedAction(
    val task: Task,
    val priority: Priority,
  ) : TaskAction()

  data class UpdateTasksToScopeAction(val taskIds: List<Uuid>, val scopeId: Uuid) : TaskAction()
  data class RemoveTasksFromScopeAction(val taskIds: List<Uuid>, val scopeId: Uuid) : TaskAction()
}