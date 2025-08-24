package io.middlepoint.morestuff.android.domain.usecase.task

import io.middlepoint.morestuff.shared.data.utils.generate
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import io.middlepoint.morestuff.shared.domain.usecase.task.AddTasksToScopeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.AddTasksToScopeUseCaseImpl
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class InsertTaskIntoScopeUseCaseTest {
  private val taskRepository: TaskRepository = mockk(relaxed = true)
  private val useCase: AddTasksToScopeUseCase = AddTasksToScopeUseCaseImpl(taskRepository)

  @Test
  fun `insert task into scope`() = runBlocking {
    val taskIds = listOf(Uuid.generate())
    val scopeId = Uuid.generate()
    useCase(taskIds, scopeId)
    coVerify(exactly = 1) { taskRepository.insertTasksIntoScope(taskIds, scopeId) }
  }
}
