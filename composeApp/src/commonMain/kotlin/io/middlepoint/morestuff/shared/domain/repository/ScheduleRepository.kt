package io.middlepoint.morestuff.shared.domain.repository


import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.FeatureFailure
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

interface ScheduleRepository {

    suspend fun createSchedule(
        taskId: Uuid,
        scheduleType: ScheduleType,
        localDateTime: LocalDateTime
    ): Either<Failure, Schedule>

    suspend fun getSchedule(scheduleId: Uuid): Either<Failure, Schedule>
    suspend fun getSchedules(scheduleIds: List<Uuid>): Either<Failure, List<Schedule>>

    suspend fun getActiveSchedules(): Either<Failure, List<Schedule>>

    fun getActiveSchedulesFlow(): Flow<List<Schedule>>

    suspend fun setScheduleFulfilled(scheduleId: Uuid): Either<Failure, Boolean>

    suspend fun getActiveSchedulesForTasks(
        taskIds: List<Uuid>,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<Schedule>>

}

object ScheduleDoesNotExist : FeatureFailure
