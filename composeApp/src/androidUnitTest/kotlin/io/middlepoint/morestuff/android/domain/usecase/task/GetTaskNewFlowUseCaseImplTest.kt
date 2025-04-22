package io.middlepoint.morestuff.android.domain.usecase.task

import io.middlepoint.morestuff.android.domain.createTaskForTest
import io.middlepoint.morestuff.shared.data.utils.generate
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskFlowUseCaseImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetTaskNewFlowUseCaseImplTest {
    private val taskRepository = mockk<TaskRepository>()
    private val getTaskFlowUseCase = GetTaskFlowUseCaseImpl(taskRepository)

    @Test
    fun `given taskId, should return a task flow`() {

        val taskId = Uuid.generate()
        val expectedTask = createTaskForTest(id = taskId)

        coEvery { taskRepository.getTaskFlow(taskId) } returns flowOf(expectedTask)

        val taskFlow = runBlocking { getTaskFlowUseCase(taskId).toList() }

        assertEquals(1, taskFlow.size)
        assertEquals(expectedTask, taskFlow.first())
    }
}
