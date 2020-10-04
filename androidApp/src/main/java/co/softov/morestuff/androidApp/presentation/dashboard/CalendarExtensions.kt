package co.softov.morestuff.androidApp.presentation.dashboard

import java.util.Calendar

fun Calendar.getHourOfDay(): Int = get(Calendar.HOUR_OF_DAY)

fun Calendar.addTime(hour: Int, minute: Int = -1): Long {
    add(Calendar.HOUR_OF_DAY, hour)
    if (minute in 0..59) add(Calendar.MINUTE, minute)
    return timeInMillis
}

fun Calendar.setTime(hour: Int, minute: Int = -1): Long {
    set(Calendar.HOUR_OF_DAY, hour)
    if (minute in 0..59) set(Calendar.MINUTE, minute)
    return timeInMillis
}