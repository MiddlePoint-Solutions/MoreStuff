package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DecreaseTaskPriorityScoreUseCaseTest {

    private val mockTaskRepository: TaskRepository = mockk()
    private val decreaseTaskScore = DecreaseTaskPriorityScoreUseCaseImpl(mockTaskRepository)

    @Test
    fun `Given a task id, when use case is invoked, then verify the priority score is decreased`() {
        runBlocking {
            val taskId: Long = 1
            val expected = Either.Right(true)

            coEvery { mockTaskRepository.decreaseTaskPriorityScore(taskId) } returns expected
            val result = decreaseTaskScore.invoke(taskId)

            assertEquals(expected, result)
        }
    }
}
