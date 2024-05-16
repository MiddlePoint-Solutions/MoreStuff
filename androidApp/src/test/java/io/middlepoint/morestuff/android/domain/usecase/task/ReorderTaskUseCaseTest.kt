package io.middlepoint.morestuff.android.domain.usecase.task

import arrow.core.right
import io.middlepoint.morestuff.android.domain.repository.PriorityRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlinx.coroutines.runBlocking

class ReorderTaskUseCaseImplTest {

    private val priorityRepository: PriorityRepository = mockk()
    private val updateTaskPriorityScoreUseCase: UpdateTaskPriorityScoreUseCase = mockk()

    private lateinit var reorderTaskUseCase: ReorderTaskUseCaseImpl

    @BeforeEach
    fun setup() {
        reorderTaskUseCase = ReorderTaskUseCaseImpl(priorityRepository, updateTaskPriorityScoreUseCase)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `when no above or below score, should return failure`() = runBlocking {
        val result = reorderTaskUseCase.invoke(1)
        assertTrue(result.isLeft())
    }

    @Test
    fun `when only below score, should update task with below score + 1`() = runBlocking {
        coEvery { updateTaskPriorityScoreUseCase(1, 6) } returns 6L.right()

        val result = reorderTaskUseCase.invoke(1, NA, 5)
        assertTrue(result.isRight())
        assertEquals(6, result.getOrNull())

        coVerify(exactly = 1) { updateTaskPriorityScoreUseCase(1, 6) }
    }

    @Test
    fun `when only above score, should update task with above score - 1`() = runBlocking {
        coEvery { updateTaskPriorityScoreUseCase(1, 4) } returns 4L.right()

        val result = reorderTaskUseCase.invoke(1, 5, NA)
        assertTrue(result.isRight())
        assertEquals(4, result.getOrNull())

        coVerify(exactly = 1) { updateTaskPriorityScoreUseCase(1, 4) }
    }

    @Test
    fun `when both scores and above equals average, should reorder by adding`() = runBlocking {
        coEvery { priorityRepository.updateTasksPriorityScoreByAdding(1, 5) } returns 5L.right()

        val result = reorderTaskUseCase.invoke(1, 5, 5)
        assertTrue(result.isRight())
        assertEquals(5, result.getOrNull())

        coVerify(exactly = 1) { priorityRepository.updateTasksPriorityScoreByAdding(1, 5) }
    }

    @Test
    fun `when both scores and below equals average, should reorder by subtracting`() = runBlocking {
        coEvery { priorityRepository.updateTasksPriorityScoreBySubtracting(1, 5) } returns 5L.right()

        val result = reorderTaskUseCase.invoke(1, 6, 5)
        assertTrue(result.isRight())
        assertEquals(5, result.getOrNull())

        coVerify(exactly = 1) { priorityRepository.updateTasksPriorityScoreBySubtracting(1, 5) }
    }

    @Test
    fun `when both scores are different, should update task with average score`() = runBlocking {
        coEvery { updateTaskPriorityScoreUseCase(1, 5) } returns 5L.right()

        val result = reorderTaskUseCase.invoke(1, 6, 4)
        assertTrue(result.isRight())
        assertEquals(5, result.getOrNull())

        coVerify(exactly = 1) { updateTaskPriorityScoreUseCase(1, 5) }
    }
}
