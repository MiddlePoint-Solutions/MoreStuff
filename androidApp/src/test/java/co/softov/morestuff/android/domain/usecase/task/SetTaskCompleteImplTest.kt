package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SetTaskCompleteImplTest {
    @Test
    fun `invoke should return true when task is set as completed`() {
        val taskId = 1L
        val taskRepository: TaskRepository = mockk()
        val setTaskCompleteUseCase = SetTaskCompleteImpl(taskRepository)

        coEvery { taskRepository.updateTasksComplete(taskId, true) } returns Either.Right(
            true
        )

        runBlocking {
            val result = setTaskCompleteUseCase(taskId, true)
            coVerify { taskRepository.updateTasksComplete(taskId, true) }
            assertTrue(result is Either.Right)
            result.map { assertTrue(it) }
        }
    }
}

