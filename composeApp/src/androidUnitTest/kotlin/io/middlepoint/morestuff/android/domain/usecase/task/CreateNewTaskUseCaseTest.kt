package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.android.domain.createTaskForTest
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import io.middlepoint.morestuff.shared.domain.enums.TaskType
import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.model.defaultScope
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetDefaultPriorityScoreUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*


class CreateNewTaskUseCaseTest {
    private val taskRepository: TaskRepository = mockk()
    private val getDefaultPriorityScoreUseCase = mockk<GetDefaultPriorityScoreUseCase>()
    private val useCase: CreateNewTaskUseCaseImpl =
        CreateNewTaskUseCaseImpl(taskRepository, getDefaultPriorityScoreUseCase)

    @Test
    fun `create new task use case`() = runBlocking {
        val taskParams = TaskParams("title", Priority.Now(), TaskType.User, defaultScope.id)
        val task = createTaskForTest()
        coEvery { taskRepository.createTask(any(), any(), any(), any()) } returns task
        coEvery { getDefaultPriorityScoreUseCase(taskParams.priority) } returns 0
        val result = useCase.invoke(taskParams)
        assertEquals(task, result)
    }
}














