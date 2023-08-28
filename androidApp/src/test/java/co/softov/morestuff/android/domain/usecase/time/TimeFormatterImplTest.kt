package co.softov.morestuff.android.domain.usecase.time


import android.content.Context
import co.softov.morestuff.android.domain.timeManager
import co.softov.morestuff.android.data.utils.TimeFormatterImpl
import co.softov.morestuff.android.domain.util.TimeFormatter
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal class TimeFormatterImplTest {

    private val timeFormatter = spyk(TimeFormatterImpl(mockk())) {
        every { is24HourFormat } returns true
    }

    @Test
    fun `formatTime should format input time string using the provided pattern`() {
        val timeString = "2023-03-27T12:00:00.000Z"
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val formattedString = timeFormatter.formatTime(timeString, pattern)
        assertNotNull(formattedString)
        val dateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
        val outputJavaLocalDateTime = LocalDateTime.parse(formattedString, dateTimeFormatter)
        val outputLocalDateTime = outputJavaLocalDateTime.toKotlinLocalDateTime()
        val inputInstant = timeString.toInstant()
        val inputLocalDateTime = inputInstant.toLocalDateTime(timeManager.currentTimeZone)
        assertEquals(inputLocalDateTime, outputLocalDateTime)
    }

    @Test
    fun `formatTimeOnly should format input time string to HH mm format`() {
        val timeString = "2023-03-27T12:00:00.000Z"
        val expectedFormattedString = ZonedDateTime.parse(timeString)
            .withZoneSameInstant(ZoneId.systemDefault())
            .toLocalTime()
            .format(DateTimeFormatter.ofPattern("HH:mm"))

        val formattedString = timeFormatter.formatTimeOnly(timeString)
        assertEquals(expectedFormattedString, formattedString)
    }

    @Test
    fun `formatTimeDayAndMonth should format input time string to EEEE, MMMM d format`() {
        val timeString = "2023-03-27T12:00:00.000Z"
        val expectedFormattedString = ZonedDateTime.parse(timeString)
            .withZoneSameInstant(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))

        val formattedString = timeFormatter.formatTimeDayAndMonth(timeString)
        assertEquals(expectedFormattedString, formattedString)
    }

    @Test
    fun `formatToDateTime should format input time string to ddMMyyyyHHmm format`() {
        val timeString = "2023-03-27T12:00:00.000Z"
        val expectedFormattedString = ZonedDateTime.parse(timeString)
            .withZoneSameInstant(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

        val formattedString = timeFormatter.formatToDateTime(timeString)
        assertEquals(expectedFormattedString, formattedString)
    }


}