package co.softov.morestuff.androidApp.presentation.dashboard.options

import co.softov.morestuff.androidApp.presentation.dashboard.addTime
import co.softov.morestuff.androidApp.presentation.dashboard.setTime
import java.util.Calendar

enum class TimeOption {
    ONE_HOUR, MORNING, NOON, AFTER_NOON, EVENING, NIGHT, CUSTOM, SOME_DAY;

    companion object {

        val TODAY = listOf(ONE_HOUR, MORNING, NOON, AFTER_NOON, EVENING, NIGHT, CUSTOM)
        val TOMORROW = listOf(MORNING, NOON, AFTER_NOON, EVENING, NIGHT, CUSTOM)
        val LATER = listOf(SOME_DAY, CUSTOM)

        fun getFilteredTodayOptions(currentHour: Int): List<TimeOption> {
            return TODAY.filter { option ->
                when (option) {
                    MORNING -> currentHour < 8
                    NOON -> currentHour < 12
                    AFTER_NOON -> currentHour < 15
                    EVENING -> currentHour < 19
                    NIGHT -> currentHour < 21
                    ONE_HOUR -> true
                    CUSTOM -> true
                    SOME_DAY -> false
                }
            }
        }

        fun getCompactFilteredTodayOptions(currentHour: Int): List<TimeOption> {
            val option = when {
                currentHour < 8 -> MORNING
                currentHour < 12 -> NOON
                currentHour < 15 -> AFTER_NOON
                currentHour < 19 -> EVENING
                currentHour < 21 -> NIGHT
                else -> null
            }

            return option?.let { filteredOption ->
                listOf(ONE_HOUR, filteredOption, CUSTOM)
            } ?: listOf(ONE_HOUR, CUSTOM)
        }
    }
}

fun List<TimeOption>.createTodayOptions(): List<OptionListItemViewModel> {
    return mapIndexed { index, option ->
        val time = when (option) {
            TimeOption.ONE_HOUR -> Calendar.getInstance().addTime(1)
            TimeOption.MORNING -> Calendar.getInstance().setTime(9, 0)
            TimeOption.NOON -> Calendar.getInstance().setTime(12, 0)
            TimeOption.AFTER_NOON -> Calendar.getInstance().setTime(15, 0)
            TimeOption.EVENING -> Calendar.getInstance().setTime(19, 0)
            TimeOption.NIGHT -> Calendar.getInstance().setTime(21, 0)
            else -> 0L
        }
        OptionListItemViewModel(id = index.toLong(), time = time, option = option)
    }
}

fun List<TimeOption>.createLaterOptions(): List<OptionListItemViewModel> {
    return mapIndexed { index, option ->
        OptionListItemViewModel(id = index.toLong(), option = option)
    }
}

fun List<TimeOption>.createTomorrowOptions(): List<OptionListItemViewModel> {
    val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
    return mapIndexed { index, option ->
        val time = when (option) {
            TimeOption.MORNING -> calendar.setTime(9, 0)
            TimeOption.NOON -> calendar.setTime(12, 0)
            TimeOption.AFTER_NOON -> calendar.setTime(15, 0)
            TimeOption.EVENING -> calendar.setTime(19, 0)
            TimeOption.NIGHT -> calendar.setTime(21, 0)
            else -> 0L
        }
        OptionListItemViewModel(id = index.toLong(), time = time, option = option)
    }
}

