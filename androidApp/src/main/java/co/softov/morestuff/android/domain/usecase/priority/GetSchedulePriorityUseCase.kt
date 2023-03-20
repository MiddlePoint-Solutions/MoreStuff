package co.softov.morestuff.android.domain.usecase.priority

import arrow.core.Either
import arrow.core.flatMap
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.PriorityOptionsModel
import co.softov.morestuff.android.domain.usecase.BaseFlowUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleFlowUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GetSchedulePriorityUseCase :
    BaseFlowUseCase<Failure, GetSchedulePriorityParams, SchedulePriorityResult>

data class GetSchedulePriorityParams(
    val taskId: Long
)

data class SchedulePriorityResult(
    val model: PriorityOptionsModel,
    val scheduleActive: Boolean,
)

class GetSchedulePriorityUseCaseImpl(
    private val getActiveScheduleFlow: GetActiveScheduleFlowUseCase,
    private val mapScheduleToPriority: MapScheduleToPriorityUseCase,
    private val getPriorityOptionsUseCase: GetPriorityOptionsUseCase,
    private val getUpcomingPriorityUseCase: GetUpcomingPriorityUseCase,
) : GetSchedulePriorityUseCase {
    override fun invoke(
        params: GetSchedulePriorityParams
    ): Flow<Either<Failure, SchedulePriorityResult>> {
        return getActiveScheduleFlow(params.taskId).map {
            it.fold(
                ifLeft = {
                    getUpcomingPriorityUseCase(GetUpcomingPriorityParams())
                        .flatMap { priority ->
                            val priorityParams = GetPriorityOptionsParams(priority, priority)
                            getPriorityOptionsUseCase(priorityParams).map { result ->
                                SchedulePriorityResult(
                                    model = result,
                                    scheduleActive = false
                                )
                            }
                        }
                },
                ifRight = { schedule ->
                    mapScheduleToPriority(schedule)
                        .flatMap { priority ->
                            val priorityParams = GetPriorityOptionsParams(priority, priority)
                            getPriorityOptionsUseCase(priorityParams).map { result ->
                                SchedulePriorityResult(
                                    model = result,
                                    scheduleActive = true
                                )
                            }
                        }
                }
            )
        }
    }

}







