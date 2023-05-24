package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.createScheduleForTest
import co.softov.morestuff.android.domain.service.Scheduler
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class CancelActiveScheduleActionUseCaseImplTest {
    private val scheduler = mockk<Scheduler>()
    private val getActiveSchedule = mockk<GetActiveScheduleUseCase>(relaxed = true)
    private val setScheduleFulfilled = mockk<SetScheduleFulfilledUseCase>(relaxed = true)
    private val cancelActiveScheduleUseCaseImpl = CancelActiveScheduleUseCaseImpl(
        scheduler,
        getActiveSchedule,
        setScheduleFulfilled
    )


    @Test
    fun `cancels schedule`() = runBlocking {
        val taskId = 1L
        val scheduleId = 2L
        val createScheduleForTest = createScheduleForTest()

        coEvery { getActiveSchedule(taskId) } returns Either.Right(createScheduleForTest(scheduleId))
        coEvery { scheduler.cancelSchedule(2) } just Runs

        cancelActiveScheduleUseCaseImpl.invoke(taskId)

        coVerify { setScheduleFulfilled(scheduleId) }
        coVerify { scheduler.cancelSchedule(scheduleId) }
    }
}
