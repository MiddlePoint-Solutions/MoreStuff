package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.createListOfTasks
import co.softov.morestuff.android.domain.createScheduleForTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test


internal class UpdatePlannedTasksPriorityUseCaseImplTest {

    private val getActiveTasksWithScheduleUseCase = mockk<GetActiveTasksWithScheduleUseCase>(relaxed = true)
    private val getPlanPriorityScoreUseCase = mockk<GetPlanPriorityScoreUseCase>(relaxed = true)
    private val updateTaskPriorityScoreUseCase = mockk<UpdateTaskPriorityScoreUseCase>(relaxed = true)
    private val useCase = UpdatePlannedTasksPriorityUseCaseImpl(
        getActiveTasksWithScheduleUseCase,
        getPlanPriorityScoreUseCase,
        updateTaskPriorityScoreUseCase
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `verify that priority of tasks with schedule is updated`() = runTest {
        val tasks = createListOfTasks(3).mapIndexed { index, task ->
            val schedule = createScheduleForTest(taskId = task.id, scheduleTimeLocal = "2023-06-15T0${index + 1}:00:00")
            task.copy(activeSchedule = schedule)
        }

        val schedules = tasks.mapIndexed { index, task ->
            createScheduleForTest(taskId = task.id, scheduleTimeLocal = "2023-06-15T0${index + 1}:00:00")
        }

        coEvery { getActiveTasksWithScheduleUseCase() } returns flowOf(tasks)
        coEvery { getPlanPriorityScoreUseCase(any(), any()) } answers {
            val scheduleTimeLocal = firstArg<String>()

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
            coVerify(exactly = tasks.size) { updateTaskPriorityScoreUseCase(any(), any()) }
        }
    }




    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `verify that priority of tasks without schedule is not updated`() = runTest {
        val tasks = createListOfTasks(2)

        coEvery { getActiveTasksWithScheduleUseCase.invoke() } returns flowOf(tasks)

        useCase.invoke()

        tasks.forEach { task ->
            coVerify(exactly = 0) { updateTaskPriorityScoreUseCase(task.id, any()) }
        }
    }
}









