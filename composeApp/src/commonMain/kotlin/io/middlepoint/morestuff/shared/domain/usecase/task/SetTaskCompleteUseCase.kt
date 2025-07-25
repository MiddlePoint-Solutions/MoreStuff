package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.DEFAULT_SCOPE_NAME
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopeByTaskIdUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopesByNameUseCase

interface SetTaskCompleteUseCase {
  suspend operator fun invoke(taskIds: List<Uuid>, complete: Boolean): Either<Failure, Boolean>
}

class SetTaskCompleteImpl(
  private val taskRepository: TaskRepository,
  private val getScopeByTaskIdUseCase: GetScopeByTaskIdUseCase,
  private val getScopesByNameUseCase: GetScopesByNameUseCase,
  private val updateTasksScopeUseCase: UpdateTasksScopeUseCase,
) : SetTaskCompleteUseCase {
  override suspend fun invoke(taskIds: List<Uuid>, complete: Boolean): Either<Failure, Boolean> {
    return taskRepository.updateTasksComplete(taskIds, complete).also {
      if (!complete) {
        taskIds.forEach { taskId ->
          getScopeByTaskIdUseCase(taskId).fold(
            ifLeft = {
              addTaskToDefaultScope(taskId)
            },
            ifRight = { scope ->
              if (scope.deleted) {
                addTaskToDefaultScope(taskId)
              }
            }
          )
        }
      }
    }
  }

  private suspend fun addTaskToDefaultScope(taskId: Uuid) {
    getScopesByNameUseCase(DEFAULT_SCOPE_NAME).map {
      if (it.isNotEmpty()) {
        updateTasksScopeUseCase(listOf(taskId), it[0].id)
      }
    }
  }
}
