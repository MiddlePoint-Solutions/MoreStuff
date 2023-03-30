package co.softov.morestuff.android.domain.usecase.priority

import arrow.core.getOrHandle
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.TimeOfDayOption
import co.softov.morestuff.android.domain.timeManager
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class GetUpcomingPriorityUseCaseTest {

    private val useCase = GetUpcomingPriorityUseCaseImpl(timeManager)

    @Test
    fun `should return Today afternoon priority when utc time is today morning`() =
        runBlocking {
            val params = GetUpcomingPriorityParams(
                utcTime = timeManager.todayUtcString(11, 59)
            )
            val result = useCase(params).getOrHandle { throw (Throwable(it.toString())) }
            Assertions.assertEquals(Priority.today.copy(option = TimeOfDayOption.Afternoon), result)
        }

    @Test
    fun `should return Today evening priority when utc time is today afternoon`() =
        runBlocking {
            val params = GetUpcomingPriorityParams(
                utcTime = timeManager.todayUtcString(16, 20)
            )
            val result = useCase(params).getOrHandle { throw (Throwable(it.toString())) }
            Assertions.assertEquals(Priority.today.copy(option = TimeOfDayOption.Evening), result)
        }

    @Test
    fun `should return Tomorrow morning priority when utc time is today evening`() =
        runBlocking {
            val params = GetUpcomingPriorityParams(
                utcTime = timeManager.todayUtcString(18, 20)
            )
            val result = useCase(params).getOrHandle { throw (Throwable(it.toString())) }
            Assertions.assertEquals(Priority.Tomorrow(TimeOfDayOption.Morning), result)
        }

}