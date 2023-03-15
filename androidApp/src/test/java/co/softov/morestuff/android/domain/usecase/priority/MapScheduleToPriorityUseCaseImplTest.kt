package co.softov.morestuff.android.domain.usecase.priority

import arrow.core.getOrHandle
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.model.TimeOfDayOption
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.days

internal class MapScheduleToPriorityUseCaseImplTest {

    private val useCase = MapScheduleToPriorityUseCaseImpl()
    private val schedule = Schedule.empty()

    @Test
    fun `should return Today afternoon priority when schedule is for today afternoon`() =
        runBlocking {
            val localTime = TimeUtils.todayLocalDateTimeString(16, 20)
            val testSchedule = schedule.copy(scheduleLocalTime = localTime)
            val result = useCase(testSchedule).getOrHandle { throw (Throwable(it.toString())) }
            assertEquals(Priority.today.copy(option = TimeOfDayOption.Afternoon), result)
        }

    @Test
    fun `should return Tomorrow afternoon priority when schedule is for tomorrow afternoon`() =
        runBlocking {
            val localTime = TimeUtils.tomorrowLocalDateTimeString(16, 20)
            val testSchedule = schedule.copy(scheduleLocalTime = localTime)
            val result = useCase(testSchedule).getOrHandle { throw (Throwable(it.toString())) }
            assertEquals(Priority.tomorrow.copy(option = TimeOfDayOption.Afternoon), result)
        }

    @Test
    fun `should return Later priority when schedule is for later`() =
        runBlocking {
            val localTime = (TimeUtils.nowUtcInstant + 3.days)
                .toLocalDateTime(TimeUtils.currentTimeZone)
                .toString()

            val testSchedule = schedule.copy(scheduleLocalTime = localTime)
            val result = useCase(testSchedule).getOrHandle { throw (Throwable(it.toString())) }
            assertEquals(Priority.later, result)
        }

    @Test
    fun `should return Later as default priority when schedule is for later`() = runBlocking {
        val result = useCase(schedule).getOrHandle { throw (Throwable(it.toString())) }
        assertEquals(Priority.later, result)
    }

}