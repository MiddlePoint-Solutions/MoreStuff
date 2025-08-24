package io.middlepoint.morestuff.android.domain.usecase.time


import io.middlepoint.morestuff.shared.data.repository.TimeFormatterImpl
import io.middlepoint.morestuff.shared.platform.TimeUtils
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.TimeZone
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TimeFormatterImplTest {

  private fun provideTimeUtils(use24Hours: Boolean) = mockk<TimeUtils> {
    every { is24HourFormat() } returns use24Hours
  }

  private val timeFormatter24Hours = TimeFormatterImpl(provideTimeUtils(true))
  private val timeFormatter = TimeFormatterImpl(provideTimeUtils(false))

  @Test
  fun `format display time should return time string only`() {
    val timeString = "2023-03-27T12:00:00.000Z"
    val formattedString = timeFormatter.formatDisplayTime(timeString, TimeZone.UTC)
    assertEquals("12:00 PM", formattedString)
  }

  @Test
  fun `format display 24 hours time should return time string only`() {
    val timeString = "2023-03-27T12:00:00.000Z"
    val formattedString = timeFormatter24Hours.formatDisplayTime(timeString, TimeZone.UTC)
    assertEquals("12:00", formattedString)
  }

  @Test
  fun `format month and day should return correct format`() {
    val timeString = "2023-03-27T12:00:00.000Z"
    val formattedString = timeFormatter.formatDisplayDayMonth(timeString, TimeZone.UTC)
    assertEquals("March 27", formattedString)
  }

}