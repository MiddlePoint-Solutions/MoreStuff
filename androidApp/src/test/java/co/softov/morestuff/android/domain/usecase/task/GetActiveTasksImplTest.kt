package co.softov.morestuff.android.domain.usecase.task


import co.softov.morestuff.android.domain.createListOfTasks
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class GetActiveTasksUseCaseTest {

    private val mockTaskRepository: TaskRepository = mockk()
    private val getActiveTasksImpl = GetActiveTasksFlowUseCaseImpl(mockTaskRepository)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Test getActiveTasksFlow is called`() = runTest {
        val exampleTasks: List<TaskDomain> = createListOfTasks(2)

        coEvery { mockTaskRepository.getActiveTasksFlow() } returns flowOf(exampleTasks)

        val activeTasksFlow = getActiveTasksImpl.invoke()

        coVerify { mockTaskRepository.getActiveTasksFlow() }

        assertEquals(exampleTasks, activeTasksFlow.first())
    }

}



