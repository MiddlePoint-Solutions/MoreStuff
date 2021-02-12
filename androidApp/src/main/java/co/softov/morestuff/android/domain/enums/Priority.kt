package co.softov.morestuff.android.domain.enums

sealed class Priority(val value: PriorityOption) {
    data class Today(val option: TodayOption = TodayOption.Automatic) : Priority(option)
    data class Tomorrow(val option: TomorrowOption = TomorrowOption.Automatic) : Priority(option)
    data class Later(val option: LaterOption = LaterOption.Automatic) : Priority(option)
}

interface PriorityOption

enum class TodayOption : PriorityOption {
    Automatic
}

enum class TomorrowOption : PriorityOption {
    Automatic
}

enum class LaterOption : PriorityOption {
    Automatic
}

//enum class TimeOption {
//    CUSTOM, ONE_HOUR, MORNING, NOON, AFTER_NOON, EVENING, NIGHT, WEEKEND, SOME_DAY;
//
//    companion object {
//
//        val TODAY = listOf(ONE_HOUR, MORNING, NOON, AFTER_NOON, EVENING, NIGHT, CUSTOM)
//        val TOMORROW = listOf(MORNING, NOON, AFTER_NOON, EVENING, NIGHT, CUSTOM)
//        val LATER = listOf(SOME_DAY, WEEKEND, CUSTOM)
//
//        fun getFilteredTodayOptions(currentHour: Int): List<TimeOption> {
//            return TODAY.filter { option ->
//                when (option) {
//                    MORNING -> currentHour < 8
//                    NOON -> currentHour < 12
//                    AFTER_NOON -> currentHour < 15
//                    EVENING -> currentHour < 19
//                    NIGHT -> currentHour < 21
//                    ONE_HOUR -> true
//                    CUSTOM -> true
//                    SOME_DAY -> false
//                    WEEKEND -> false
//                }
//            }
//        }
//
//        fun getCompactFilteredTodayOptions(currentHour: Int): List<TimeOption> {
//            val option = when {
//                currentHour < 8 -> MORNING
//                currentHour < 12 -> NOON
//                currentHour < 15 -> AFTER_NOON
//                currentHour < 19 -> EVENING
//                currentHour < 21 -> NIGHT
//                else -> null
//            }
//
//            return option?.let { filteredOption ->
//                listOf(ONE_HOUR, filteredOption, CUSTOM)
//            } ?: listOf(ONE_HOUR, CUSTOM)
//        }
//    }
//}

