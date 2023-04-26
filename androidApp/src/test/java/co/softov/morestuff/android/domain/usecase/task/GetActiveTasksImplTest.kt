package co.softov.morestuff.android.domain.usecase.task


import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.android.domain.createListOfTasks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class GetActiveTasksUseCaseTest {

    @Test
    fun `Test get active tasks`() {
        val mockTaskRepository: TaskRepository = mockk()
        val getActiveTasksImpl = GetActiveTasksUseCaseImpl(mockTaskRepository)
        val exampleTasks: List<Task> = createListOfTasks(2)

        coEvery { mockTaskRepository.getActiveTasksFlow() } returns flowOf(exampleTasks)

        runBlocking {
            val activeTasksFlow = getActiveTasksImpl.invoke()
            assertEquals(exampleTasks, activeTasksFlow.first())
        }
    }

}

