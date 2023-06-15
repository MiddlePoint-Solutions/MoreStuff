package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.createListOfTasks
import co.softov.morestuff.android.domain.createScheduleForTest
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.repository.TaskDoesNotExist
import co.softov.morestuff.android.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test


internal class UpdatePlanTaskPriorityUseCaseImplTest {

    private val taskRepository = mockk<TaskRepository>(relaxed = true)
    private val scheduleRepository = mockk<ScheduleRepository>(relaxed = true)
    private val getPlanPriorityScoreUseCase = mockk<GetPlanPriorityScoreUseCase>(relaxed = true)
    private val useCase = UpdatePlanTaskPriorityUseCaseImpl(
        taskRepository,
        scheduleRepository,
        getPlanPriorityScoreUseCase
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `verify that priority of tasks with schedule is updated`() = runTest {
        val tasks = createListOfTasks(5) // Create a list of 5 tasks for the test
        val tasksWithSchedule = tasks.subList(0, 3) // The first 3 tasks have schedules
        val tasksWithoutSchedule = tasks.subList(3, 5) // The last 2 tasks do not have schedules

        val schedules = tasksWithSchedule.mapIndexed { index, task ->
            createScheduleForTest(taskId = task.id, scheduleTimeLocal = "2023-06-15T0${index + 1}:00:00")
        } // Create schedules for the tasks with schedules, with different schedule times

        coEvery { taskRepository.getActiveTasksFlow() } returns flowOf(tasks) // Return the complete list of tasks in the flow
        coEvery { scheduleRepository.getActiveScheduleForTask(any()) } answers { invocation ->
            val taskId = firstArg<Long>()
            if (tasksWithSchedule.any { it.id == taskId }) {
                val index = tasksWithSchedule.indexOfFirst { it.id == taskId }
                Either.Right(schedules[index])
            } else {
                Either.Left(TaskDoesNotExist)
            }
        }
        coEvery { getPlanPriorityScoreUseCase(any(), any()) } answers { invocation ->
            val scheduleTimeLocal = firstArg<String>()
            // Return a different priority score based on the schedule time
            if (scheduleTimeLocal.endsWith("01:00:00")) {
                10L
            } else if (scheduleTimeLocal.endsWith("02:00:00")) {
                20L
            } else {
                30L
            }
        }

        useCase.invoke()

        tasksWithSchedule.forEach { task ->
            val schedule = schedules.find { it.taskId == task.id }
            val newPriorityScore = if (schedule?.scheduleLocalTime?.endsWith("01:00:00") == true) {
                10L
            } else if (schedule?.scheduleLocalTime?.endsWith("02:00:00") == true) {
                20L
            } else {
                30L
            }
            coVerify { taskRepository.updateTaskPriority(task.id, newPriorityScore) } // Verify that the priority was updated for tasks with schedules
        }

        tasksWithoutSchedule.forEach { task ->
            coVerify(exactly = 0) { taskRepository.updateTaskPriority(task.id, any()) } // Verify that the priority was not updated for tasks without schedules
        }
    }
}








