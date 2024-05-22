package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.right
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetPlanPriorityScoreUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.Test


internal class UpdatePlannedTasksPriorityUseCaseImplTest {

    private val getActiveTasksWithScheduleUseCase =
        mockk<GetActiveTasksWithScheduleUseCase>(relaxed = true)
    private val getPlanPriorityScoreUseCase = mockk<GetPlanPriorityScoreUseCase>(relaxed = true)
    private val updateTaskPriorityScoreUseCase =
        mockk<UpdateTaskPriorityScoreUseCase>(relaxed = true)

    private val useCase = UpdatePlannedTasksPriorityUseCaseImpl(
        getActiveTasksWithScheduleUseCase,
        getPlanPriorityScoreUseCase,
        updateTaskPriorityScoreUseCase
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `verify that priority of tasks with schedule is updated`() = runTest {
        val tasks = io.middlepoint.morestuff.shared.domain.createListOfTasks(3)
          .mapIndexed { index, task ->
            val schedule = io.middlepoint.morestuff.shared.domain.createScheduleForTest(
              taskId = task.id,
              scheduleTimeLocal = "2023-06-15T0${index + 1}:00:00",
              scheduleType = ScheduleType.OneTime
            )
            task.copy(schedule = listOf(schedule))
        }

        val schedules = tasks.mapIndexed { index, task ->
          io.middlepoint.morestuff.shared.domain.createScheduleForTest(
            taskId = task.id,
            scheduleTimeLocal = "2023-06-15T0${index + 1}:00:00",
            scheduleType = ScheduleType.OneTime
          )
        }

        coEvery { getActiveTasksWithScheduleUseCase(any()) } returns tasks.right()
        coEvery { getPlanPriorityScoreUseCase(any(), any()) } answers {
            val scheduleTimeLocal = firstArg<LocalDateTime>().toString()

            if (scheduleTimeLocal.endsWith("01:00:00")) {
                10L
            } else if (scheduleTimeLocal.endsWith("02:00:00")) {
                20L
            } else {
                30L
            }
        }

        useCase.invoke()

        tasks.forEach { task ->
            val schedule = schedules.find { it.taskId == task.id }
            val newPriorityScore = if (schedule?.scheduleLocalTime?.endsWith("01:00:00") == true) {
                10L
            } else if (schedule?.scheduleLocalTime?.endsWith("02:00:00") == true) {
                20L
            } else {
                30L
            }

        }

        coVerify(exactly = tasks.size) { updateTaskPriorityScoreUseCase(any(), any()) }

    }

    @Test
    fun `verify that priority of tasks without schedule is not updated`() = runTest {
        val tasksWithoutSchedule = io.middlepoint.morestuff.shared.domain.createListOfTasks(2).map { it.copy(schedule = listOf()) }

        coEvery { getActiveTasksWithScheduleUseCase(any()) } returns tasksWithoutSchedule.right()

        useCase.invoke()

        tasksWithoutSchedule.forEach { task ->
            coVerify(exactly = 0) { updateTaskPriorityScoreUseCase(task.id, any()) }
        }
    }
}









