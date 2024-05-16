package io.middlepoint.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.service.Scheduler
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ScheduleAtTimeUseCaseImplTest {
    private val scheduler = mockk<Scheduler>()
    private val scheduleAtTimeUseCaseImpl = ScheduleAtTimeUseCaseImpl(scheduler)
    private val scheduleId = 2L
    private val time = "12:00"


    @Test
    fun `schedule at Time use case`() = runBlocking {
        coEvery { scheduler.scheduleAtExact(scheduleId, time) } just Runs
        scheduleAtTimeUseCaseImpl.invoke(scheduleId, time)
        coVerify { scheduler.scheduleAtExact(scheduleId, time) }
    }

}


