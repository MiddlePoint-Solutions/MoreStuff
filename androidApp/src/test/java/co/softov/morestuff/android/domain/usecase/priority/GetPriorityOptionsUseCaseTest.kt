package co.softov.morestuff.android.domain.usecase.priority

import co.softov.morestuff.android.domain.expectedTodayOptionsSize
import co.softov.morestuff.android.domain.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

class GetPriorityOptionsUseCaseTest {

    private val getPriorityOptionsUseCase = GetPriorityOptionsUseCase()

    @Test
    fun `getPriorityOptions returns options for Today`() {
        val getPriorityOptions =
            getPriorityOptionsUseCase::class.members.find { it.name == "getPriorityOptions" }!!
        getPriorityOptions.isAccessible = true
        val params = GetPriorityOptionsParams(
            Priority.Today(DefaultOption.Auto),
            Priority.Today(DefaultOption.Auto)
        )
        val result = getPriorityOptions.call(getPriorityOptionsUseCase, params)
        assertEquals(expectedTodayOptionsSize(), (result as List<*>).size)
    }

    @Test
    fun `getPriorityOptions returns  options for Tomorrow`() {
        val getPriorityOptions =
            getPriorityOptionsUseCase::class.members.find { it.name == "getPriorityOptions" }!!
        getPriorityOptions.isAccessible = true
        val params = GetPriorityOptionsParams(
            Priority.Today(DefaultOption.Auto),
            Priority.Tomorrow(TimeOfDayOption.Noon)
        )
        val result = getPriorityOptions.call(getPriorityOptionsUseCase, params)
        assertEquals(6, (result as List<*>).size)
    }

    @Test
    fun `getPriorityOptions returns  options for Later`() {
        val getPriorityOptions =
            getPriorityOptionsUseCase::class.members.find { it.name == "getPriorityOptions" }!!
        getPriorityOptions.isAccessible = true
        val params = GetPriorityOptionsParams(
            Priority.Today(DefaultOption.Auto),
            Priority.Later(DefaultOption.Auto)
        )
        val result = getPriorityOptions.call(getPriorityOptionsUseCase, params)
        assertEquals(3, (result as List<*>).size)
    }

    @Test
    fun `setNextPriorityOption sets  priority option`() {
        val setNextPriorityOption =
            getPriorityOptionsUseCase::class.members.find { it.name == "setNextPriorityOption" }!!
        setNextPriorityOption.isAccessible = true
        val options = listOf(
            DefaultOption.Auto,
            TimeOfDayOption.Morning,
            TimeOfDayOption.Noon,
            TimeOfDayOption.Afternoon,
            TimeOfDayOption.Evening,
            DefaultOption.Custom
        )
        val currentPriority = Priority.Today(TimeOfDayOption.Noon)
        val nextPriority = Priority.Tomorrow(TimeOfDayOption.Noon)
        val result = setNextPriorityOption.call(
            getPriorityOptionsUseCase,
            options,
            nextPriority,
            currentPriority
        )
        assertEquals(nextPriority, result)
    }


    @Test
    fun `getLaterTimeOptions returns  options`() {
        val getLaterTimeOptions =
            getPriorityOptionsUseCase::class.memberFunctions.find { it.name == "getLaterTimeOptions" }!!
        getLaterTimeOptions.isAccessible = true
        val result = getLaterTimeOptions.call(getPriorityOptionsUseCase) as List<*>
        val expected = listOf(
            DefaultOption.Auto,
            LaterOption.Weekend,
            DefaultOption.Custom
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getCurrentTimeOption returns  option`() {
        val getCurrentTimeOption =
            getPriorityOptionsUseCase::class.memberFunctions.find { it.name == "getCurrentTimeOption" }!!
        getCurrentTimeOption.isAccessible = true
        val options = listOf(
            DefaultOption.Auto,
            TimeOfDayOption.Morning,
            TimeOfDayOption.Noon,
            TimeOfDayOption.Afternoon,
            TimeOfDayOption.Evening,
            DefaultOption.Custom
        )
        val current = Priority.Today(DefaultOption.Auto)
        val defaultOption = DefaultOption.Auto
        val result =
            getCurrentTimeOption.call(getPriorityOptionsUseCase, options, current, defaultOption)
        assertEquals(current.option, result)
    }

    @Test
    fun `getTomorrowTimeOptions returns  options`() {
        val getTomorrowTimeOptions =
            getPriorityOptionsUseCase::class.memberFunctions.find { it.name == "getTomorrowTimeOptions" }!!
        getTomorrowTimeOptions.isAccessible = true
        val result = getTomorrowTimeOptions.call(getPriorityOptionsUseCase) as List<*>
        val expected = listOf(
            DefaultOption.Auto,
            *TimeOfDayOption.values(),
            DefaultOption.Custom
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getTodayTimeOptions returns options`() {
        val getTodayTimeOptions =
            getPriorityOptionsUseCase::class.memberFunctions.find { it.name == "getTodayTimeOptions" }!!
        getTodayTimeOptions.isAccessible = true
        val result = getTodayTimeOptions.call(getPriorityOptionsUseCase) as List<*>

        val now = LocalDateTime.now()
        val morning = now.toLocalDate().atTime(8, 0)
        val noon = now.toLocalDate().atTime(12, 0)
        val afternoon = now.toLocalDate().atTime(18, 0)

        val expected = buildList<PriorityOption> {
            add(DefaultOption.Auto)
            when {
                now < morning -> addAll(TimeOfDayOption.values())
                now < noon -> addAll(
                    TimeOfDayOption.values()
                        .filterNot { it == TimeOfDayOption.Morning }
                )
                now < afternoon -> addAll(
                    TimeOfDayOption.values()
                        .filterNot {
                            it == TimeOfDayOption.Morning || it == TimeOfDayOption.Noon
                        }
                )
            }
            add(DefaultOption.Custom)
        }
        assertEquals(expected, result)
    }
}

