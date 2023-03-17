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
import co.softov.morestuff.android.domain.usecase.BaseFlowUseCase
import co.softov.morestuff.android.domain.usecase.BaseUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleFlowUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.toInstant

interface GetSchedulePriorityUseCase :
    BaseFlowUseCase<Failure, GetSchedulePriorityParams, PriorityOptionsResult>

data class GetSchedulePriorityParams(
    val taskId: Long
)

class GetSchedulePriorityUseCaseImpl(
    private val getActiveScheduleFlow: GetActiveScheduleFlowUseCase,
    private val mapScheduleToPriority: MapScheduleToPriorityUseCase,
    private val getPriorityOptionsUseCase: GetPriorityOptionsUseCase,
    private val getUpcomingPriorityUseCase: GetUpcomingPriorityUseCase,
) : GetSchedulePriorityUseCase {
    override fun invoke(
        params: GetSchedulePriorityParams
    ): Flow<Either<Failure, PriorityOptionsResult>> {
        return getActiveScheduleFlow(params.taskId).map {
            it.fold(
                ifLeft = {
                    getUpcomingPriorityUseCase(GetUpcomingPriorityParams())
                        .flatMap { priority ->
                            val priorityParams = GetPriorityOptionsParams(priority, priority)
                            getPriorityOptionsUseCase(priorityParams)
                        }
                },
                ifRight = { schedule ->
                    mapScheduleToPriority(schedule)
                        .flatMap { priority ->
                            val priorityParams = GetPriorityOptionsParams(priority, priority)
                            getPriorityOptionsUseCase(priorityParams)
                        }
                }
            )
        }
    }

}







