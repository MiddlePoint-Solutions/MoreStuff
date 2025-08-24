package io.middlepoint.morestuff.android.domain.usecase.task


import arrow.core.Either
import arrow.core.getOrElse
import io.middlepoint.morestuff.shared.data.utils.generate
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.PriorityRepository
import io.middlepoint.morestuff.shared.domain.usecase.task.IncreaseTaskPriorityScoreUseCaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test


class IncreaseTaskNewPriorityScoreUseCaseImplTest {

    @Test
    fun `should increase task priority score when invoked`() = runTest {
        val repository: PriorityRepository = mockk()
        val useCase = IncreaseTaskPriorityScoreUseCaseImpl(repository)
        val taskId = Uuid.generate()
        val initialPriorityScore = 5L
        val increasedPriorityScore = initialPriorityScore + 1

        coEvery { repository.increaseTaskPriorityScore(taskId) } returns Either.Right(
            increasedPriorityScore
        )
        val result = useCase.invoke(taskId)

        assert(result.isRight())
        assert(result.getOrElse { 0L } == increasedPriorityScore)
        coVerify(exactly = 1) { repository.increaseTaskPriorityScore(taskId) }
    }
}
