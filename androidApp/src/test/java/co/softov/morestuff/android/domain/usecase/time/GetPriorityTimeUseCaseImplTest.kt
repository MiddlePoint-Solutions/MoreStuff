package co.softov.morestuff.android.domain.usecase.time

import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.model.DefaultOption
import co.softov.morestuff.android.domain.model.LaterOption
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.TimeOfDayOption
import co.softov.morestuff.android.domain.service.TimeManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class GetPriorityTimeUseCaseImplTest {

    private val devTools = mockk<DevTools>()
    private val timeManager = mockk<TimeManager>()
    private val useCase: GetPriorityTimeUseCase = GetPriorityTimeUseCaseImpl(devTools, timeManager)


    @Test
    fun `get priority time for morning option`() = runBlocking {
        val priority = Priority.Today(TimeOfDayOption.Morning)
        val fixedDate = "2023-03-10"

        every { devTools.debugReminders } returns false
        every {
            timeManager.todayLocalDateTime(
                8,
                0
            )
        } returns LocalDateTime.parse("$fixedDate" + "T08:00")

        val result = useCase.invoke(priority)

        assertEquals("$fixedDate" + "T08:00", result)
    }

    @Test
    fun `get priority time for noon option`() = runBlocking {
        val priority = Priority.Today(TimeOfDayOption.Noon)
        val fixedDate = "2023-03-10"

        every { devTools.debugReminders } returns false
        every {
            timeManager.todayLocalDateTime(
                12,
                0
            )
        } returns LocalDateTime.parse("$fixedDate" + "T12:00")

        val result = useCase.invoke(priority)

        assertEquals("$fixedDate" + "T12:00", result)
    }


    @Test
    fun `get priority time for afternoon option`() = runBlocking {
        val priority = Priority.Today(TimeOfDayOption.Afternoon)
        val fixedDate = "2023-03-10"

        every { devTools.debugReminders } returns false
        every {
            timeManager.todayLocalDateTime(
                17,
                0
            )
        } returns LocalDateTime.parse("$fixedDate" + "T17:00")

        val result = useCase.invoke(priority)

        assertEquals("$fixedDate" + "T17:00", result)
    }


    @Test
    fun `get priority time for evening option`() = runBlocking {
        val priority = Priority.Today(TimeOfDayOption.Evening)
        val fixedDate = "2023-03-10"

        every { devTools.debugReminders } returns false
        every {
            timeManager.todayLocalDateTime(
                20,
                0
            )
        } returns LocalDateTime.parse("$fixedDate" + "T20:00")

        val result = useCase.invoke(priority)

        assertEquals("$fixedDate" + "T20:00", result)
    }


    @Test
    fun `get priority time for custom default option`() = runBlocking {
        val priority = Priority.Tomorrow(DefaultOption.Custom)
        val fixedDate = "2023-03-11"

        every { devTools.debugReminders } returns false
        every {
            timeManager.tomorrowLocalDateTime(
                9,
                0
            )
        } returns LocalDateTime.parse("$fixedDate" + "T09:00")

        val result = useCase.invoke(priority)

        assertEquals("$fixedDate" + "T09:00", result)
    }


    @Test
    fun `get priority time for someday later option`() = runBlocking {
        val priority = Priority.Later(LaterOption.Someday)

        every { devTools.debugReminders } returns false

        val result = useCase.invoke(priority)

        assertEquals(null, result)
    }
}
