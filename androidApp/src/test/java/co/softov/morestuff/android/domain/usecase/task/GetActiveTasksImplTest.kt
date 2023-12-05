package co.softov.morestuff.android.domain.usecase.task


import co.softov.morestuff.android.domain.model.scopeAll
import co.softov.morestuff.android.domain.repository.TaskRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test


class GetActiveTasksUseCaseTest {

    private val taskRepository: TaskRepository = mockk(relaxed = true)
    private val getActiveTasksUseCase = GetActiveTasksFlowUseCaseImpl(taskRepository)

    @Test
    fun `when 'All' scopeId is passed, should get all tasks`() = runTest {
        getActiveTasksUseCase(scopeAll.id)
        coVerify { taskRepository.getActiveTasksFlow(false, scopeAll.id) }
    }

    @Test
    fun `when not 'All' scopeId is passed, should get scoped tasks`() = runTest {
        val scopeId = 1337L
        getActiveTasksUseCase(scopeId)
        coVerify { taskRepository.getActiveTasksFlow(true, scopeId) }
    }

}



