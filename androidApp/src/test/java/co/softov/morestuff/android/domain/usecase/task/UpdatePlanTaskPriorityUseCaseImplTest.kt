package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.createListOfTasks
import co.softov.morestuff.android.domain.createScheduleForTest
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.repository.TaskDoesNotExist
import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleForTaskUseCase
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
    private val getActiveScheduleForTaskUseCase = mockk<GetActiveScheduleForTaskUseCase>(relaxed = true)
    private val useCase = UpdatePlanTaskPriorityUseCaseImpl(
        taskRepository,
        getPlanPriorityScoreUseCase,
        getActiveScheduleForTaskUseCase
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `verify that priority of tasks with schedule is updated`() = runTest {
        val tasks = createListOfTasks(3)

        val schedules = tasks.mapIndexed { index, task ->
            createScheduleForTest(taskId = task.id, scheduleTimeLocal = "2023-06-15T0${index + 1}:00:00")
        }

        coEvery { taskRepository.getActiveTasksFlow() } returns flowOf(tasks)
        coEvery { getActiveScheduleForTaskUseCase(any()) } answers {
            val taskId = firstArg<Long>()
            val index = tasks.indexOfFirst { it.id == taskId }
            Either.Right(schedules[index])
        }
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
            coVerify { taskRepository.updateTaskPriority(task.id, newPriorityScore) }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `verify that priority of tasks without schedule is not updated`() = runTest {
        val tasks = createListOfTasks(2)

        coEvery { taskRepository.getActiveTasksFlow() } returns flowOf(tasks)
        coEvery { scheduleRepository.getActiveScheduleForTask(any()) } returns Either.Left(TaskDoesNotExist)

        useCase.invoke()

        tasks.forEach { task ->
            coVerify(exactly = 0) { taskRepository.updateTaskPriority(task.id, any()) }
        }
    }
}








