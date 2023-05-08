package co.softov.morestuff.android.domain.usecase.priority

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.TimeOfDayOption
import co.softov.morestuff.android.data.service.TimeManager
import co.softov.morestuff.android.domain.usecase.BaseUseCase

interface GetUpcomingPriorityUseCase : BaseUseCase<Failure, GetUpcomingPriorityParams, Priority> {

}

data class GetUpcomingPriorityParams(val utcTime: String? = null)

class GetUpcomingPriorityUseCaseImpl( val timeManager: TimeManager) :
    GetUpcomingPriorityUseCase {

    override suspend fun invoke(params: GetUpcomingPriorityParams): Either<Failure, Priority> {
        val localTime = when (params.utcTime) {
            null -> timeManager.nowLocalDateTime
            else -> timeManager.utcStringToLocalDateTime(params.utcTime)
        }

        //  TODO: consider using a sealed class to represent the time.
        val priority = when {
            localTime.hour < 8 -> Priority.Now(TimeOfDayOption.Morning)
            localTime.hour in 9..11 -> Priority.Now(TimeOfDayOption.Afternoon)
            localTime.hour in 12..16 -> Priority.Now(TimeOfDayOption.Evening)
            else -> Priority.Later(TimeOfDayOption.Morning)
        }

        return Either.Right(priority)
    }

}
