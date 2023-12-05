package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.repository.TaskRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
class InsertTaskIntoScopeUseCaseTest {
    private val taskRepository: TaskRepository = mockk(relaxed = true)
    private val useCase: AddTasksToScopeUseCase = AddTasksToScopeUseCaseImpl(taskRepository)

    @Test
    fun `insert task into scope`() = runBlocking {
        val taskId = 1L
        val scopeId = 2L

        useCase(taskId, scopeId)

        coVerify(exactly = 1) { taskRepository.insertTasksIntoScope(taskId, scopeId) }
    }
}
