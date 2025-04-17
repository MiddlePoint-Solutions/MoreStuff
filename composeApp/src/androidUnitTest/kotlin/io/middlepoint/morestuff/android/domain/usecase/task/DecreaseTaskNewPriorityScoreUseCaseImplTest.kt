package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import arrow.core.getOrElse
import io.middlepoint.morestuff.shared.domain.repository.PriorityRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class DecreaseTaskNewPriorityScoreUseCaseImplTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should decrease task priority score when invoked`() = runTest {
        val repository: PriorityRepository = mockk()
        val useCase = IncreaseTaskPriorityScoreUseCaseImpl(repository)
        val taskId = 1L
        val initialPriorityScore = 5L
        val increasedPriorityScore = initialPriorityScore - 1

        coEvery { repository.increaseTaskPriorityScore(taskId) } returns Either.Right(
            increasedPriorityScore
        )
        val result = useCase.invoke(taskId)

        assert(result.isRight())
        assert(result.getOrElse { 0L } == increasedPriorityScore)
        coVerify(exactly = 1) { repository.increaseTaskPriorityScore(taskId) }
    }
}
