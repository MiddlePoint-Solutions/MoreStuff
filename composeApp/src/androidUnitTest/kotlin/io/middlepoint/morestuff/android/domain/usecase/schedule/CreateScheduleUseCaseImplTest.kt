package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import arrow.core.right
import io.middlepoint.morestuff.shared.data.service.TimeManagerImpl
import io.middlepoint.morestuff.android.domain.createScheduleUseCaseTest
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class CreateScheduleUseCaseImplTest {
  private val scheduleRepository = mockk<ScheduleRepository>(relaxed = true)
  private val timeManager = TimeManagerImpl()
  private val cancelActiveScheduleUseCase = mockk<CancelActiveScheduleUseCase>(relaxed = true)

  private val createTime =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()

  private val createScheduleUseCaseImpl = CreateScheduleUseCaseImpl(
    scheduleRepository,
    cancelActiveScheduleUseCase
  )

  @Test
  fun `creates schedule`() = runBlocking {
    val taskId = Uuid("1")
    val localTime = timeManager.nowLocalDateTime
    val schedule = createScheduleUseCaseTest(
      createTime = createTime,
      scheduleTimeLocal = localTime.toString(),
      scheduleTimeUtc = timeManager.nowUtcInstantString,
      timeZone = TimeZone.currentSystemDefault().id
    )

    coEvery { cancelActiveScheduleUseCase(any(), any()) } coAnswers { listOf(schedule).right() }

    coEvery {
      scheduleRepository.createSchedule(
        taskId,
        ScheduleType.OneTime,
        localTime
      )
    } coAnswers {
      Either.Right(schedule)
    }

    createScheduleUseCaseImpl.invoke(taskId, ScheduleType.OneTime, localTime).onRight {
      Assertions.assertEquals(schedule, it)
    }

    coVerify {
      cancelActiveScheduleUseCase(any(), any())
      scheduleRepository.createSchedule(any(), any(), any())
    }

  }
}
