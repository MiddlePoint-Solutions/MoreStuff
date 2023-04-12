package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.right
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.Schedule
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class RescheduleTaskUseCaseTest {

    private val cancelActiveScheduleUseCase: CancelActiveScheduleUseCase = mockk()
    private val createScheduleUseCase: CreateScheduleUseCase = mockk()

    @Test
    fun `rescheduling task use-case uses expected use-cases`() = runBlocking {
        val useCase = RescheduleTaskUseCaseImpl(
            cancelActiveScheduleUseCase = cancelActiveScheduleUseCase,
            createScheduleUseCase = createScheduleUseCase
        )

        coEvery { cancelActiveScheduleUseCase.invoke(any()) } returns Schedule.empty().right()
        coEvery { createScheduleUseCase.invoke(any(), any()) } returns Schedule.empty().right()

        val params = RescheduleTaskUseCaseParams(0, Priority.today)
        useCase(params)

        coVerifyOrder {
            cancelActiveScheduleUseCase.invoke(any())
            createScheduleUseCase.invoke(any(), any())
        }
    }

}

