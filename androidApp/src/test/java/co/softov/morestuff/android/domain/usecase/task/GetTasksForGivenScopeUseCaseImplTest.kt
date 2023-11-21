package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.createListOfTasks
import co.softov.morestuff.android.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GetTasksForGivenScopeUseCaseTest {
    private val taskRepository: TaskRepository = mockk()
    private val useCase: GetTasksForGivenScopeUseCase = GetTasksForGivenScopeUseCaseImpl(taskRepository)

    @Test
    fun `get tasks for given scope`() = runBlocking {
        val scopeId = 1L
        val expectedTasks = createListOfTasks(4)

        coEvery { taskRepository.getTasksForGivenScope(scopeId) } returns expectedTasks

        val result = useCase(scopeId)

        Assertions.assertEquals(expectedTasks, result)
    }
}
