package co.softov.morestuff.android.domain.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class TimeManagerImplTest {

    private val timeManager = TimeManagerImpl()

    @Test
    fun `getCreateTime`() {
        val createTime = timeManager.getCreateTime()
        assertNotNull(createTime)
    }

    @Test
    fun `utcStringToLocalDateTime`() {
        val utcTime = "2023-03-27T12:00:00.000Z"
        val utcZoneId = ZoneId.of("UTC")
        val zonedDateTime = ZonedDateTime.parse(utcTime).withZoneSameInstant(utcZoneId)
        val localDateTime = zonedDateTime.toLocalDateTime()
        assertNotNull(localDateTime)
        assertEquals(2023, localDateTime.year)
        assertEquals(3, localDateTime.monthValue)
        assertEquals(27, localDateTime.dayOfMonth)
        assertEquals(12, localDateTime.hour)
        assertEquals(0, localDateTime.minute)
    }

    @Test
    fun `isToday `() {
        val localTime = LocalDateTime.now().toString()
        val result = timeManager.isToday(localTime)
        assertTrue(result)
    }

    @Test
    fun `isTomorrow `() {
        val localTime = LocalDateTime.now().plusDays(1).toString()
        val result = timeManager.isTomorrow(localTime)
        assertTrue(result)
    }

    @Test
    fun `isLater `() {
        val localTime = LocalDateTime.now().plusDays(2).toString()
        val result = timeManager.isLater(localTime)
        assertTrue(result)
    }

    @Test
    fun `todayUtcString`() {
        val hour = 12
        val minute = 30
        val utcString = timeManager.todayUtcString(hour, minute)
        assertNotNull(utcString)
    }

    @Test
    fun `tomorrowUtcString`() {
        val hour = 12
        val minute = 30
        val utcString = timeManager.tomorrowUtcString(hour, minute)
        assertNotNull(utcString)
    }

    @Test
    fun `tomorrowLocalDateTimeString`() {
        val hour = 12
        val minute = 30
        val localDateTimeString = timeManager.tomorrowLocalDateTimeString(hour, minute)
        assertNotNull(localDateTimeString)
    }

    @Test
    fun `tomorrowLocalDateTime`() {
        val hour = 12
        val minute = 30
        val localDateTime = timeManager.tomorrowLocalDateTime(hour, minute)
        assertNotNull(localDateTime)
    }

    @Test
    fun `todayLocalDateTimeString`() {
        val hour = 12
        val minute = 30
        val localDateTimeString = timeManager.todayLocalDateTimeString(hour, minute)
        assertNotNull(localDateTimeString)
    }

    @Test
    fun `todayLocalDateTime`() {
        val hour = 12
        val minute = 30
        val localDateTime = timeManager.todayLocalDateTime(hour, minute)
        assertNotNull(localDateTime)
    }

    @Test
    fun `todayLocalDateTimeByAdding`() {
        val hour = 2
        val minute = 30
        val localDateTime = timeManager.todayLocalDateTimeByAdding(hour, minute)
        assertNotNull(localDateTime)
        val expectedDateTime =
            LocalDateTime.now().plusHours(hour.toLong()).plusMinutes(minute.toLong())
        assertEquals(expectedDateTime.truncatedTo(ChronoUnit.MINUTES).minute, localDateTime.minute)
    }

    @Test
    fun testWeekendLocalDateTime() {
        val timeManager = TimeManagerImpl()
        val weekendDateTime = timeManager.weekendLocalDateTime()
        assertEquals(DayOfWeek.SATURDAY, weekendDateTime.dayOfWeek)
        assertEquals(10, weekendDateTime.hour)
        assertEquals(0, weekendDateTime.minute)
    }

    @Test
    fun `formatTime`() {
        val timeString = "2023-03-27T12:00:00.000Z"
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val utcZoneId = ZoneId.of("UTC")
        val zonedDateTime = ZonedDateTime.parse(timeString).withZoneSameInstant(utcZoneId)
        val formatter = DateTimeFormatter.ofPattern(pattern).withZone(utcZoneId)
        val formattedString = formatter.format(zonedDateTime)
        assertNotNull(formattedString)
        assertEquals("2023-03-27 12:00:00", formattedString)
    }
}