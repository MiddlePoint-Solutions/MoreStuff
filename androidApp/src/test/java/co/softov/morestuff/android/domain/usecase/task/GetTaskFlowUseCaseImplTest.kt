package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.createTaskForTest
import co.softov.morestuff.android.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetTaskFlowUseCaseImplTest {
    private val taskRepository = mockk<TaskRepository>()
    private val getTaskFlowUseCase = GetTaskFlowUseCaseImpl(taskRepository)

    @Test
    fun `given taskId, when invoke is called, then should return a task flow`() {

        val taskId = 1L
        val expectedTask = createTaskForTest()

        coEvery { taskRepository.getTaskFlow(taskId) } returns flowOf(expectedTask)

        val taskFlow = runBlocking { getTaskFlowUseCase.invoke(taskId).toList() }

        assertEquals(1, taskFlow.size)
        assertEquals(expectedTask, taskFlow.first())
    }
}
