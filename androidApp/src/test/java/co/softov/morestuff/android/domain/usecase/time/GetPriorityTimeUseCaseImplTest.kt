package co.softov.morestuff.android.domain.usecase.time

import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.model.DefaultOption
import co.softov.morestuff.android.domain.model.LaterOption
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.TimeOfDayOption
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// TODO: waiting for TimeUtils refactor into interface

/*class GetPriorityTimeUseCaseImplTest {

    private val devTools = mockk<DevTools>()

    private val useCase: GetPriorityTimeUseCase = GetPriorityTimeUseCaseImpl(devTools)

    @Test
    fun `get priority time for morning option`() = runBlocking {
        val priority = Priority.Today(TimeOfDayOption.Morning)

        every { devTools.debugReminders } returns false

        val result = useCase.invoke(priority)

        assertEquals("2023-03-10T08:00", result)
    }

    @Test
    fun `get priority time for noon option`() = runBlocking {
        val priority = Priority.Today(TimeOfDayOption.Noon)

        every { devTools.debugReminders } returns false

        val result = useCase.invoke(priority)

        assertEquals("2023-03-10T12:00", result)
    }

    @Test
    fun `get priority time for afternoon option`() = runBlocking {
        val priority = Priority.Today(TimeOfDayOption.Afternoon)

        every { devTools.debugReminders } returns false

        val result = useCase.invoke(priority)

        assertEquals("2023-03-10T17:00", result)
    }

    @Test
    fun `get priority time for evening option`() = runBlocking {
        val priority = Priority.Today(TimeOfDayOption.Evening)

        every { devTools.debugReminders } returns false

        val result = useCase.invoke(priority)

        assertEquals("2023-03-10T20:00", result)
    }

    @Test
    fun `get priority time for custom default option`() = runBlocking {
        val priority = Priority.Tomorrow(DefaultOption.Custom)

        every { devTools.debugReminders } returns false

        val result = useCase.invoke(priority)

        assertEquals("2023-03-11T09:00", result)
    }


    @Test
    fun `get priority time for someday later option`() = runBlocking {
        val priority = Priority.Later(LaterOption.Someday)

        every { devTools.debugReminders } returns false

        val result = useCase.invoke(priority)

        assertEquals(null, result)
    }
}*/
