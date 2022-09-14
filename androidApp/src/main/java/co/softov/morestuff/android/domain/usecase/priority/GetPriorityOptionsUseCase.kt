package co.softov.morestuff.android.domain.usecase.priority

import arrow.core.Either
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.enums.*
import co.softov.morestuff.android.domain.enums.DefaultOption.*
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.PriorityOptionsError
import co.softov.morestuff.android.domain.service.TimeManager
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
                    val options = mutableListOf<PriorityOption>(Auto)

                    val now = TimeUtils.currentLocalDateTime
                    val morning = TimeUtils.todayLocalDateTime(8)
                    val noon = TimeUtils.todayLocalDateTime(12)
                    val afternoon = TimeUtils.todayLocalDateTime(18)

                    val timeOptions = TimeOfDayOption.values()
                    when {
                        now < morning -> options.addAll(timeOptions)
                        now < noon -> options.addAll(
                            timeOptions.filterNot { it == TimeOfDayOption.Morning }
                        )
                        now < afternoon -> options.addAll(
                            timeOptions.filterNot {
                                it == TimeOfDayOption.Morning || it == TimeOfDayOption.Noon
                            }
                        )
                    }

                    options.add(Specify)
                    PriorityOptionsResult(params.priority, options)
                }
                is Priority.Tomorrow -> {
                    val options = mutableListOf<PriorityOption>(Auto)
                    options.addAll(TimeOfDayOption.values())
                    options.add(Specify)
                    PriorityOptionsResult(params.priority, options)
                }
                is Priority.Later -> {
                    val options = mutableListOf<PriorityOption>(Auto)
                    options.addAll(LaterOption.values())
                    options.add(Specify)
                    PriorityOptionsResult(params.priority, options)
                }
            }
        }.mapLeft {
            PriorityOptionsError(it.message)
        }


}