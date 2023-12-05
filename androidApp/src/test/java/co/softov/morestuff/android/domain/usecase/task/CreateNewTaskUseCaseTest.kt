package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.android.domain.createTaskForTest
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.usecase.priority.GetDefaultPriorityScoreUseCase
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
        val taskParams = TaskParams("title", Priority.Now(), TaskType.User)
        val task = createTaskForTest()
        coEvery { taskRepository.createTask(any(), any(), any(),) } returns task
        coEvery { getDefaultPriorityScoreUseCase(taskParams.priority) } returns 0
        val result = useCase.invoke(taskParams)
        assertEquals(task, result)
    }
}














