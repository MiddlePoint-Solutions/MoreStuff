package io.middlepoint.morestuff.shared

import android.content.Context
import android.text.format.DateFormat

class TimeUtilsImpl(
  private val context: Context
) : TimeUtils {

   override fun is24HourFormat(): Boolean = DateFormat.is24HourFormat(context)

}