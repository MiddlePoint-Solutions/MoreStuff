package co.softov.morestuff.android.domain.usecase.priority

import arrow.core.Either
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.model.*
import co.softov.morestuff.android.domain.model.DefaultOption.*
import co.softov.morestuff.android.domain.usecase.BaseUseCase

data class GetPriorityOptionsParams(val priority: Priority)

data class PriorityOptionsResult(
    val priority: Priority,
    val options: List<PriorityOption>,
)

class GetPriorityOptionsUseCase :
    BaseUseCase<Failure, GetPriorityOptionsParams, PriorityOptionsResult> {

    override suspend fun invoke(params: GetPriorityOptionsParams): Either<Failure, PriorityOptionsResult> =
        Either.catch {
            when (params.priority) {
                is Priority.Today -> {

                    val now = TimeUtils.nowLocalDateTime
                    val morning = TimeUtils.todayLocalDateTime(8)
                    val noon = TimeUtils.todayLocalDateTime(12)
                    val afternoon = TimeUtils.todayLocalDateTime(18)
                    val timeOptions = TimeOfDayOption.values()

                    val options = buildList {
                        add(Auto)
                        when {
                            now < morning -> addAll(timeOptions)
                            now < noon -> addAll(
                                timeOptions.filterNot { it == TimeOfDayOption.Morning }
                            )
                            now < afternoon -> addAll(
                                timeOptions.filterNot {
                                    it == TimeOfDayOption.Morning || it == TimeOfDayOption.Noon
                                }
                            )
                        }
                    }

                    PriorityOptionsResult(params.priority, options)
                }
                is Priority.Tomorrow -> {
                    val options = buildList {
                        add(Auto)
                        addAll(TimeOfDayOption.values())
                    }
                    PriorityOptionsResult(params.priority, options)
                }
                is Priority.Later -> {
                    val options = buildList {
                        add(Auto)
                        add(LaterOption.Weekend)
                        add(Custom)
                    }
                    PriorityOptionsResult(params.priority, options)
                }
            }
        }.mapLeft {
            PriorityOptionsError(it.message)
        }


}