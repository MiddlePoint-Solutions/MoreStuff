package io.middlepoint.morestuff.shared.data.repository


import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import arrow.core.Either
import arrow.core.Either.Right
import arrow.core.left
import arrow.core.right
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.data.mapper.DataMappers
import io.middlepoint.morestuff.shared.data.model.ScheduleData
import io.middlepoint.morestuff.shared.data.utils.generate
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.domain.repository.ScheduleDoesNotExist
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class ScheduleRepositoryImpl(
  database: StuffDb,
  private val mapper: DataMappers,
  private val timeManager: TimeManager,
) : ScheduleRepository {

  private val scheduleQueries = database.schedulesQueries

  override suspend fun createSchedule(
    taskId: Uuid,
    scheduleType: ScheduleType,
    localDateTime: LocalDateTime,
  ): Either<Failure, Schedule> = scheduleQueries.transactionWithResult {

    val createdAt = timeManager.nowUtcInstant

    val data = ScheduleData(
      id = Uuid.generate(),
      task_id = taskId,
      created_at = createdAt,
      updated_at = createdAt,
      scheduled_at = localDateTime.toInstant(TimeZone.UTC),
      timezone = timeManager.currentTimeZone.id,
      active = true,
      schedule_type = scheduleType,
    )

    scheduleQueries.insertSchedule(data)
    scheduleQueries
      .selectScheduleById(data.id, mapper.scheduleDataMapper)
      .executeAsOne().right()
  }

  override suspend fun getSchedule(scheduleId: Uuid): Either<Failure, Schedule> =
    scheduleQueries.selectScheduleById(scheduleId, mapper.scheduleDataMapper)
      .executeAsOneOrNull()
      ?.right() ?: ScheduleDoesNotExist.left()

  override suspend fun getSchedules(scheduleIds: List<Uuid>): Either<Failure, List<Schedule>> =
    scheduleQueries.selectSchedulesById(scheduleIds, mapper.scheduleDataMapper)
      .executeAsList()
      .right()

  override suspend fun getActiveSchedules(): Either<Failure, List<Schedule>> {
    val allScheduleTypes = listOf(ScheduleType.OneTime, ScheduleType.Reminder)
    return scheduleQueries
      .selectActiveSchedules(allScheduleTypes, mapper.scheduleDataMapper)
      .executeAsList()
      .right()
  }

  override fun getActiveSchedulesFlow(): Flow<List<Schedule>> {
    val allScheduleTypes = listOf(ScheduleType.OneTime, ScheduleType.Reminder)
    return scheduleQueries.selectActiveSchedules(allScheduleTypes, mapper.scheduleDataMapper)
      .asFlow()
      .mapToList(Dispatchers.IO)
  }

  override suspend fun getActiveSchedulesForTasks(
    taskIds: List<Uuid>,
    scheduleType: List<ScheduleType>
  ): Either<Failure, List<Schedule>> =
    scheduleQueries.selectActiveScheduleByTaskId(
      taskIds,
      scheduleType,
      mapper = mapper.scheduleDataMapper
    ).executeAsList().right()

  override suspend fun setScheduleFulfilled(scheduleId: Uuid): Either<Failure, Boolean> {
    scheduleQueries.updateScheduleActive(false, scheduleId)
    return Right(true)
  }

}