package co.softov.morestuff.android.domain.usecase.priority

import arrow.core.Either
import co.softov.morestuff.android.domain.model.*
import co.softov.morestuff.android.domain.model.DefaultOption.*
import co.softov.morestuff.android.data.service.TimeManager
import co.softov.morestuff.android.domain.usecase.BaseUseCase

data class GetPriorityOptionsParams(val current: Priority, val next: Priority)

class GetPriorityOptionsUseCase(private val timeManager: TimeManager) :
    BaseUseCase<Failure, GetPriorityOptionsParams, PriorityOptionsModel> {

    override suspend fun invoke(params: GetPriorityOptionsParams): Either<Failure, PriorityOptionsModel> =
        Either.catch {
            val options = getPriorityOptions(params)
            val priority = setNextPriorityOption(options, params.next, params.current)
            PriorityOptionsModel(priority, options)
        }.mapLeft {
            PriorityOptionsError(it.message)
        }

    private fun getPriorityOptions(params: GetPriorityOptionsParams) =
        when (params.next) {
            is Priority.Today -> getTodayTimeOptions()
            is Priority.Tomorrow -> getTomorrowTimeOptions()
            is Priority.Later -> getLaterTimeOptions()
        }

    private fun setNextPriorityOption(
        it: List<PriorityOption>,
        next: Priority,
        current: Priority,
    ) = when (next) {
        is Priority.Later -> next.copy(
            option = getCurrentTimeOption(it, current, next.option)
        )
        is Priority.Today -> next.copy(
            option = getCurrentTimeOption(it, current, next.option)
        )
        is Priority.Tomorrow -> next.copy(
            option = getCurrentTimeOption(it, current, next.option)
        )
    }

    private fun getLaterTimeOptions() = buildList<PriorityOption> {
        add(Auto)
        add(LaterOption.Weekend)
        add(Custom)
    }


    private fun getCurrentTimeOption(
        options: List<PriorityOption>,
        current: Priority,
        defaultOption: PriorityOption,
    ): PriorityOption = if (options.contains(current.option)) {
        current.option
    } else defaultOption

    private fun getTomorrowTimeOptions() = buildList<PriorityOption> {
        add(Auto)
        addAll(TimeOfDayOption.values())
        add(Custom)
    }

    private fun getTodayTimeOptions() = buildList<PriorityOption> {
        val now = timeManager.nowLocalDateTime
        val morning = timeManager.todayLocalDateTime(8)
        val noon = timeManager.todayLocalDateTime(12)
        val afternoon = timeManager.todayLocalDateTime(18)
        val timeOptions = TimeOfDayOption.values()

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
        add(Custom)
    }


}