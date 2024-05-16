package io.middlepoint.morestuff.android.domain.service

import io.middlepoint.morestuff.android.data.service.TimeManagerImpl
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class TimeManagerImplTest {

    private val timeManager = TimeManagerImpl()

    @Test
    fun `getCreateTime should return the current UTC instant as a string`() {
        val createTime = timeManager.getCreateTime()
        assertNotNull(createTime)
    }

    @Test
    fun `isToday should return true if the input date is today `() {
        val localTime = timeManager.nowLocalDateTime
        val result = timeManager.isToday(localTime.toString())
        assertTrue(result)
    }

    @Test
    fun `isTomorrow should return true if the input date is tomorrow `() {
        val localTime = timeManager.tomorrowLocalDateTime()
        val result = timeManager.isTomorrow(localTime.toString())
        assertTrue(result)
    }

    @Test
    fun `isLater should return true if the input date is later than tomorrow`() {
        val localTime = timeManager.tomorrowLocalDateTime().toJavaLocalDateTime().plusDays(1)
        val result = timeManager.isLater(localTime.toString())
        assertTrue(result)
    }

    @Test
    fun `todayUtcString should return the UTC string for today`() {
        val utcString = timeManager.todayUtcString()
        assertNotNull(utcString)
    }

    @Test
    fun `tomorrowUtcString should return the UTC string for tomorrow`() {
        val utcString = timeManager.tomorrowUtcString()
        assertNotNull(utcString)
    }

    @Test
    fun `tomorrowLocalDateTimeString should return a string`() {
        val localDateTimeString = timeManager.tomorrowLocalDateTimeString()
        assertNotNull(localDateTimeString)
    }

    @Test
    fun `tomorrowLocalDateTime should return LocalDateTime`() {
        val localDateTime = timeManager.tomorrowLocalDateTime()
        assertNotNull(localDateTime)
    }

    @Test
    fun `todayLocalDateTimeString should return a string`() {
        val localDateTimeString = timeManager.todayLocalDateTimeString()
        assertNotNull(localDateTimeString)
    }

    @Test
    fun `todayLocalDateTime should return LocalDateTime`() {
        val localDateTime = timeManager.nowLocalDateTime
        assertNotNull(localDateTime)
    }

    @Test
    fun `todayLocalDateTimeByAdding should return LocalDateTime with added hours and minutes`() {
        val hour = 2
        val minute = 30
        val localDateTime = timeManager.todayLocalDateTimeByAdding(hour, minute)
        assertNotNull(localDateTime)
        val expectedDateTime =
            LocalDateTime.now().plusHours(hour.toLong()).plusMinutes(minute.toLong())
        assertEquals(expectedDateTime.truncatedTo(ChronoUnit.MINUTES).minute, localDateTime.minute)
    }

    @Test
    fun `weekendLocalDateTime should return LocalDateTime for the upcoming weekend`() {
        val timeManager = TimeManagerImpl()
        val weekendDateTime = timeManager.weekendLocalDateTime()
        assertEquals(DayOfWeek.SATURDAY, weekendDateTime.dayOfWeek)
        assertEquals(10, weekendDateTime.hour)
        assertEquals(0, weekendDateTime.minute)
    }

    @Test
    fun `getDefaultPlanTime should return a LocalDateTime object representing the next quarter-hour`() {
        val defaultPlanTime = timeManager.getDefaultPlanTime()
        assertNotNull(defaultPlanTime)

        val now = LocalDateTime.now()
        val expectedHour: Int
        val expectedMinute: Int = when {
            now.minute < 10 -> {
                expectedHour = now.hour
                15
            }
            now.minute < 20 -> {
                expectedHour = now.hour
                30
            }
            now.minute < 35 -> {
                expectedHour = now.hour
                45
            }
            else -> {
                expectedHour = if(now.hour < 23) now.hour + 1 else 0
                0
            }
        }

        val expectedDefaultPlanTime = kotlinx.datetime.LocalDateTime(
            now.year,
            now.month,
            now.dayOfMonth,
            expectedHour,
            expectedMinute,
            0,
            0
        )
        assertEquals(expectedDefaultPlanTime, defaultPlanTime)
    }
}