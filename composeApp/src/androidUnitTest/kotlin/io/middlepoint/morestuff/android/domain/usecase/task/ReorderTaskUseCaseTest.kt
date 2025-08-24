package io.middlepoint.morestuff.android.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.shared.data.utils.generate
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.PriorityRepository
import io.middlepoint.morestuff.shared.domain.usecase.task.ReorderTaskUseCaseImpl
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ReorderTaskNewUseCaseImplTest {

    private val priorityRepository: PriorityRepository = mockk()
    private lateinit var reorderTaskUseCase: ReorderTaskUseCaseImpl

    @BeforeEach
    fun setup() {
        reorderTaskUseCase = ReorderTaskUseCaseImpl(priorityRepository)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `when task list is empty, should return success`() = runBlocking {
        val result = reorderTaskUseCase.invoke(emptyList())
        assertTrue(result.isRight())
    }

    @Test
    fun `when task list is not empty, should reorder tasks based on highest priority`() = runBlocking {
        val task1 = TaskUiModel(id = Uuid.generate(), title = "Task 1", priorityScore = 10)
        val task2 = TaskUiModel(id = Uuid.generate(), title = "Task 2", priorityScore = 8)
        val task3 = TaskUiModel(id = Uuid.generate(), title = "Task 3", priorityScore = 5)
        val updatedTasks = listOf(task1, task2, task3)

        coEvery { priorityRepository.getHighestPriorityScore() } returns 100L
        coEvery { priorityRepository.updateTasksPriorities(any()) } returns Either.Right(Unit)

        val result = reorderTaskUseCase.invoke(updatedTasks)
        assertTrue(result.isRight())

        coVerify(exactly = 1) { priorityRepository.getHighestPriorityScore() }
        coVerify(exactly = 1) { priorityRepository.updateTasksPriorities(any()) }
    }
}
