package co.softov.morestuff.android.domain.service

import co.softov.morestuff.android.data.service.TimeManagerImpl
import kotlinx.datetime.toJavaLocalDateTime
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
    fun `getCreateTime should return the current UTC instant as a string`() {
        val createTime = timeManager.getCreateTime()
        assertNotNull(createTime)
    }

    @Test
    fun `utcStringToLocalDateTime should convert UTC string to LocalDateTime`() {
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
        val localDateTime = timeManager.todayLocalDateTime()
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