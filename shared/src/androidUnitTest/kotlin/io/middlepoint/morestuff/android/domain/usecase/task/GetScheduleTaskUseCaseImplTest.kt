package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import arrow.core.right
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetScheduleUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test


class GetScheduleTaskUseCaseImplTest {

    @Test
     fun `invoke should return task when schedule is found`() {
        val getScheduleUseCase: GetScheduleUseCase = mockk()
        val getTaskUseCase: GetTaskUseCase = mockk()

        val scheduleId = 1L
        val taskId = 1L
        val schedule =
          io.middlepoint.morestuff.shared.domain.createScheduleForTest(scheduleType = ScheduleType.OneTime)
        val task = io.middlepoint.morestuff.shared.domain.createTaskForTest()

        coEvery { getScheduleUseCase(scheduleId) } returns schedule.right()
        coEvery { getTaskUseCase(taskId) } returns task.right()

        runBlocking {

            when (val result = getScheduleUseCase(scheduleId)) {
                is Either.Right -> getTaskUseCase(result.value.taskId)
                is Either.Left -> result
            }
        }

    }

}



