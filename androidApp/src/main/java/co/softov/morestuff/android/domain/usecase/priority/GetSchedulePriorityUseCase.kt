package co.softov.morestuff.android.domain.usecase.priority

import arrow.core.Either
import arrow.core.flatMap
import co.softov.morestuff.android.app.util.anyLog
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOptionsResult
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.repository.ScheduleDoesNotExist
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.BaseUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleUseCase
import kotlinx.datetime.toInstant

interface GetSchedulePriorityUseCase :
    BaseUseCase<Failure, GetSchedulePriorityParams, PriorityOptionsResult>

data class GetSchedulePriorityParams(
    val taskId: Long
)

class GetSchedulePriorityUseCaseImpl(
    private val getActiveSchedule: GetActiveScheduleUseCase,
    private val mapScheduleToPriority: MapScheduleToPriorityUseCase,
    private val getPriorityOptionsUseCase: GetPriorityOptionsUseCase,
    private val getUpcomingPriorityUseCase: GetUpcomingPriorityUseCase,
) : GetSchedulePriorityUseCase {
    override suspend fun invoke(
        params: GetSchedulePriorityParams
    ): Either<Failure, PriorityOptionsResult> {
        return getActiveSchedule(params.taskId).fold(
            ifLeft = {
                getUpcomingPriorityUseCase(GetUpcomingPriorityParams())
                    .flatMap { priority ->
                        val priorityParams = GetPriorityOptionsParams(priority, priority)
                        getPriorityOptionsUseCase(priorityParams)
                    }
            },
            ifRight = {
                mapScheduleToPriority(it)
                    .flatMap { priority ->
                        val priorityParams = GetPriorityOptionsParams(priority, priority)
                        getPriorityOptionsUseCase(priorityParams)
                    }
            }
        )
    }

}







