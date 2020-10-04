package co.softov.morestuff.androidApp.data.repository


import co.softov.morestuff.androidApp.data.mapper.ScheduleDbMapper
import co.softov.morestuff.androidApp.data.mapper.ScheduleWithTitleDbMapper
import co.softov.morestuff.androidApp.data.mapper.mapList
import co.softov.morestuff.androidApp.domain.enums.Message
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.model.ScheduleWithTitle
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.ScheduleDoesNotExist
import co.softov.morestuff.androidApp.domain.repository.ScheduleRepository
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.*

class ScheduleRepositoryImpl(
    database: StuffDb,
    private val mapScheduleDb: ScheduleDbMapper,
    private val mapScheduleWithTitleDb: ScheduleWithTitleDbMapper
) : ScheduleRepository {

    private val scheduleQueries = database.scheduleQueries

    override suspend fun createSchedule(
        taskId: Long,
        scheduleTime: Long
    ): SimpleResult<Long> {
        Timber.d("createTaskReminderSchedule: $taskId for ${Date(scheduleTime)}")
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis
        scheduleQueries.insertSchedule(taskId, currentTime, scheduleTime, Message.TASK_REMINDER)
        return Result.Success(scheduleQueries.lastInsertRowId().executeAsOne())
    }

    override suspend fun getSchedule(scheduleId: Long): SimpleResult<Schedule> {
        return when (val schedule =
            scheduleQueries.selectScheduleById(scheduleId).executeAsOneOrNull()) {
            null -> Result.Failure(ScheduleDoesNotExist)
            else -> Result.Success(mapScheduleDb(schedule))
        }
    }

    override suspend fun getActiveSchedules(): SimpleResult<List<Schedule>> {
        return Result.Success(scheduleQueries.selectActiveSchedules().executeAsList()
            .map { mapScheduleDb(it) })
    }

    override suspend fun getActiveSchedulesFlow(): SimpleResult<Flow<List<Schedule>>> {
        return Result.Success(
            scheduleQueries.selectActiveSchedules().asFlow().mapToList()
                .map { mapList(it, mapScheduleDb) })
    }

    override suspend fun getActiveSchedulesWithTitleFlow(): SimpleResult<Flow<List<ScheduleWithTitle>>> {
        return Result.Success(
            scheduleQueries.selectActiveSchedulesWithTaskTitle().asFlow().mapToList()
                .map { mapList(it, mapScheduleWithTitleDb) })
    }

    override suspend fun getActiveScheduleForTask(taskId: Long): SimpleResult<Schedule> {
        return when (val schedule =
            scheduleQueries.selectActiveScheduleByTaskId(taskId).executeAsOneOrNull()) {
            null -> Result.Failure(ScheduleDoesNotExist)
            else -> Result.Success(mapScheduleDb(schedule))
        }
    }

    override suspend fun setScheduleFulfilled(scheduleId: Long): SimpleResult<Long> {
        scheduleQueries.updateScheduleFulfilled(true, scheduleId)
        return Result.Success(scheduleId)
    }
}