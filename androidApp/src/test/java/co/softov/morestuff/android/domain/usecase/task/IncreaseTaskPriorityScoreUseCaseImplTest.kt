package co.softov.morestuff.android.domain.usecase.task


import arrow.core.Either
import arrow.core.getOrElse
import co.softov.morestuff.android.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test


class IncreaseTaskPriorityScoreUseCaseImplTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should increase task priority score when invoked`() = runTest {
        val taskRepository: TaskRepository = mockk()
        val useCase = IncreaseTaskPriorityScoreUseCaseImpl(taskRepository)
        val taskId = 1L
        val initialPriorityScore = 5L
        val increasedPriorityScore = initialPriorityScore + 1

        coEvery { taskRepository.increaseTaskPriorityScore(taskId) } returns Either.Right(
            increasedPriorityScore
        )
        val result = useCase.invoke(taskId)

        assert(result.isRight())
        assert(result.getOrElse { 0L } == increasedPriorityScore)
        coVerify(exactly = 1) { taskRepository.increaseTaskPriorityScore(taskId) }
    }
}
