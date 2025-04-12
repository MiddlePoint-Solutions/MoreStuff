package io.middlepoint.morestuff.shared.domain.usecase.priority

import arrow.core.right
import io.middlepoint.morestuff.android.domain.createTaskForTest
import io.middlepoint.morestuff.shared.domain.enums.ReviewActionType
import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.UpdateTaskPriorityScoreUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class UpdateTaskNewReviewPriorityUseCaseTest {

    private val getDefaultPriorityScoreUseCase: GetDefaultPriorityScoreUseCase = mockk()
    private val getTaskAbovePriorityScoreUseCase: GetTaskAbovePriorityScoreUseCase = mockk()
    private val getTaskBelowPriorityScoreUseCase: GetTaskBelowPriorityScoreUseCase = mockk()
    private val updateTaskPriorityScoreUseCase: UpdateTaskPriorityScoreUseCase = mockk(relaxed = true)
    private val getTaskUseCase: GetTaskUseCase = mockk()

    private val useCase = UpdateTaskReviewPriorityUseCaseImpl(
        getDefaultPriorityScoreUseCase,
        getTaskAbovePriorityScoreUseCase,
        getTaskBelowPriorityScoreUseCase,
        updateTaskPriorityScoreUseCase,
        getTaskUseCase
    )

    @Test
    fun `Review NOW action updates task priority score`() {
        runBlocking {
            val priorityScore = 100L
            val taskId = 666L
            val priority = Priority.Now()

            coEvery { getDefaultPriorityScoreUseCase(priority) } returns priorityScore
            useCase(taskId, ReviewActionType.Now)
            coVerify { getDefaultPriorityScoreUseCase(priority) }
            coVerify { updateTaskPriorityScoreUseCase(taskId, priorityScore) }
        }
    }

    @Test
    fun `Review LATER action updates task priority score`() {
        runBlocking {
            val priorityScore = -100L
            val taskId = 666L
            val priority = Priority.Later()

            coEvery { getDefaultPriorityScoreUseCase(priority) } returns priorityScore
            useCase(taskId, ReviewActionType.Later)
            coVerify { getDefaultPriorityScoreUseCase(priority) }
            coVerify { updateTaskPriorityScoreUseCase(taskId, priorityScore) }
        }
    }

    @Test
    fun `Review MORE action updates task priority score`() {
        runBlocking {
            val taskId = 666L
            val taskPriorityScore = 100L
            val nextPriorityScore = 120L
            val expectedPriorityScore = 121L
            val currentTask = createTaskForTest(
              id = taskId,
              priorityScore = taskPriorityScore
            )
            val nextPriorityTask = createTaskForTest(
              id = 777L,
              priorityScore = nextPriorityScore
            )

            coEvery { getTaskUseCase(taskId) } returns currentTask.right()
            coEvery { getTaskAbovePriorityScoreUseCase(taskPriorityScore) } returns nextPriorityTask.right()
            useCase(taskId, ReviewActionType.More)
            coVerify { getTaskUseCase(taskId) }
            coVerify { getTaskAbovePriorityScoreUseCase(taskPriorityScore) }
            coVerify { updateTaskPriorityScoreUseCase(taskId, expectedPriorityScore) }
        }
    }

    @Test
    fun `Review LESS action updates task priority score`() {
        runBlocking {
            val taskId = 666L
            val taskPriorityScore = 100L
            val nextPriorityScore = 90L
            val expectedPriorityScore = 89L
            val currentTask = createTaskForTest(
              id = taskId,
              priorityScore = taskPriorityScore
            )
            val nextPriorityTask = createTaskForTest(
              id = 777L,
              priorityScore = nextPriorityScore
            )

            coEvery { getTaskUseCase(taskId) } returns currentTask.right()
            coEvery { getTaskBelowPriorityScoreUseCase(taskPriorityScore) } returns nextPriorityTask.right()
            useCase(taskId, ReviewActionType.Less)
            coVerify { getTaskUseCase(taskId) }
            coVerify { getTaskBelowPriorityScoreUseCase(taskPriorityScore) }
            coVerify { updateTaskPriorityScoreUseCase(taskId, expectedPriorityScore) }
        }
    }

}