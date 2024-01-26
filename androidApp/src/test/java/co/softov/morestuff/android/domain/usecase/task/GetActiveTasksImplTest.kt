package co.softov.morestuff.android.domain.usecase.task


import co.softov.morestuff.android.domain.model.defaultScope
import co.softov.morestuff.android.domain.repository.TaskRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test


class GetActiveTasksUseCaseTest {

    private val taskRepository: TaskRepository = mockk(relaxed = true)
    private val getScopeActiveTasksUseCase = GetScopeActiveTasksFlowUseCaseImpl(taskRepository)

    @Test
    fun `when 'All' scopeId is passed, should get all tasks`() = runTest {
        getScopeActiveTasksUseCase(defaultScope.id)
        coVerify { taskRepository.getScopeActiveTasksFlow(defaultScope.id) }
    }

    @Test
    fun `when not 'All' scopeId is passed, should get scoped tasks`() = runTest {
        val scopeId = 1337L
        getScopeActiveTasksUseCase(scopeId)
        coVerify { taskRepository.getScopeActiveTasksFlow(scopeId) }
    }

}



