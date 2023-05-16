package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class IncreaseTaskPriorityScoreUseCaseTest {

    private val mockTaskRepository: TaskRepository = mockk()
    private val increaseTaskScore = IncreaseTaskPriorityScoreUseCaseImpl(mockTaskRepository)

    @Test
    fun `Given a task id, when use case is invoked, then verify the priority score is increased`() {
        runBlocking {
            val taskId: Long = 1
            val expected = Either.Right(true)

            coEvery { mockTaskRepository.increaseTaskPriorityScore(taskId) } returns expected
            val result = increaseTaskScore.invoke(taskId)
            assertEquals(expected, result)
        }
    }
}
