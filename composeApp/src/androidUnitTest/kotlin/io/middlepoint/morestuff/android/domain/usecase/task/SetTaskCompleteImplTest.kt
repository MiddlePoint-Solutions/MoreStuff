package io.middlepoint.morestuff.android.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.shared.data.utils.generate
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import io.middlepoint.morestuff.shared.domain.usecase.task.SetTaskCompleteImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SetTaskCompleteImplTest {
    @Test
    fun `invoke should return true when task is set as completed`() {
        val taskId = listOf(Uuid.generate())
        val taskRepository: TaskRepository = mockk()
        val setTaskCompleteUseCase = SetTaskCompleteImpl(taskRepository)

        coEvery { taskRepository.updateTasksComplete(taskId, true) } returns Either.Right(
            true
        )

        runBlocking {
            val result = setTaskCompleteUseCase.invoke(taskId, true)
            coVerify { taskRepository.updateTasksComplete(taskId, true) }
            assertTrue(result is Either.Right)
            result.map { assertTrue(it) }
        }
    }
}

