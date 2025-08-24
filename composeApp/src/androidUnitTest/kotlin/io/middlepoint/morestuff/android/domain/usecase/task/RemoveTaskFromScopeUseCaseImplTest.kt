package io.middlepoint.morestuff.android.domain.usecase.task

import io.middlepoint.morestuff.shared.data.utils.generate
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import io.middlepoint.morestuff.shared.domain.usecase.task.RemoveTasksFromScopeUseCaseImpl
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test


class RemoveTaskFromScopeUseCaseTest {
  private val taskRepository: TaskRepository = mockk(relaxed = true)
  private val useCase = RemoveTasksFromScopeUseCaseImpl(taskRepository)

  @Test
  fun `remove task from scope`() = runBlocking {
    val taskIds = listOf(Uuid.generate())
    val scopeId = Uuid.generate()
    useCase.invoke(taskIds, scopeId)
    coVerify(exactly = 1) { taskRepository.removeTasksFromScope(taskIds, scopeId) }
  }
}
