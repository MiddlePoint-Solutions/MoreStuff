package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.createScheduleForTest
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.service.Scheduler
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class CancelActiveScheduleUseCaseImplTest {
  private val scheduler = mockk<Scheduler>()
  private val getActiveSchedule = mockk<GetActiveSchedulesUseCase>(relaxed = true)
  private val setScheduleFulfilled = mockk<SetScheduleFulfilledUseCase>(relaxed = true)
  private val cancelActiveScheduleUseCaseImpl = CancelActiveScheduleUseCaseImpl(
    scheduler,
    getActiveSchedule,
    setScheduleFulfilled
  )


  @Test
  fun `cancels schedule`() = runBlocking {
    val schedule = createScheduleForTest(
      scheduleType = ScheduleType.OneTime
    )

    coEvery {
      getActiveSchedule(
        listOf(schedule.taskId),
        listOf(ScheduleType.OneTime)
      )
    } returns Either.Right(
      listOf(schedule)
    )
    coEvery { scheduler.cancelSchedule(schedule.id) } just Runs

    cancelActiveScheduleUseCaseImpl(listOf(schedule.taskId), listOf(ScheduleType.OneTime))

    coVerify { setScheduleFulfilled(schedule.id) }
    coVerify { scheduler.cancelSchedule(schedule.id) }
  }
}
