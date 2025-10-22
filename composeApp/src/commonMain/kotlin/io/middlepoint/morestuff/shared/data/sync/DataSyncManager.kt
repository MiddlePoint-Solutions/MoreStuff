package io.middlepoint.morestuff.shared.data.sync

import app.cash.sqldelight.async.coroutines.awaitAsList
import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.toJsonObject
import io.middlepoint.morestuff.db.Messages
import io.middlepoint.morestuff.db.Messages_extra
import io.middlepoint.morestuff.db.Schedules
import io.middlepoint.morestuff.db.Scopes
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.db.Tasks
import io.middlepoint.morestuff.db.Tasks_scopes
import io.middlepoint.morestuff.shared.data.mapper.DataMappers
import io.middlepoint.morestuff.shared.domain.DevTools
import io.middlepoint.morestuff.shared.domain.enums.SyncFailure
import io.middlepoint.morestuff.shared.domain.enums.SyncStatus.Error
import io.middlepoint.morestuff.shared.domain.enums.SyncStatus.Initializing
import io.middlepoint.morestuff.shared.domain.enums.SyncStatus.Success
import io.middlepoint.morestuff.shared.domain.service.DataSyncManager
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import kotlinx.coroutines.flow.flow
import kotlin.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class DataSyncManagerImpl(
  database: StuffDb,
  private val dataMappers: DataMappers,
  private val supabase: SupabaseClient,
  private val settings: Settings,
  private val timeManager: TimeManager,
  private val devTools: DevTools,
) : DataSyncManager {

  private val tasks = database.tasksQueries
  private val scopes = database.scopesQueries
  private val messages = database.messagesQueries
  private val tasksScopes = database.tasksScopesQueries
  private val messageExtras = database.messagesExtraQueries
  private val schedules = database.schedulesQueries

  private val logger = Logger.withTag("DataSyncManager")

  private var lastPushTime: Instant
    get() = try {
      settings.getStringOrNull(KEY_LAST_PUSH_TIME)
        ?.let { Instant.parse(it) }
        ?: Instant.fromEpochMilliseconds(0)
    } catch (e: Exception) {
      logger.e("Error getting last push time", e)
      Instant.fromEpochMilliseconds(0)
    }
    set(value) = settings.putString(KEY_LAST_PUSH_TIME, value.toString())

  private var lastPullTime: Instant
    get() = try {
      settings.getStringOrNull(KEY_LAST_PULL_TIME)
        ?.let { Instant.parse(it) }
        ?: Instant.fromEpochMilliseconds(0)
    } catch (e: Exception) {
      logger.e("Error getting last pull time", e)
      Instant.fromEpochMilliseconds(0)
    }
    set(value) = settings.putString(KEY_LAST_PULL_TIME, value.toString())

  /**
   * Current sync method: Push -> Pull
   * In the future we should change to use a server‐time token.
   */
  override fun sync() = flow {
    emit(Initializing)
    try {
      push()
      pull()
      performDataMigrationIfNeeded()
      emit(Success(timeManager.nowUtcInstant))
    } catch (e: Throwable) {
      logger.e("Sync Exception", e)
      emit(Error(SyncFailure(e.toString())))
    }
  }

  private suspend fun performDataMigrationIfNeeded() {
    if (devTools.importMigrationData()) { // TODO: remove in next release version
      push()
      pull()
      devTools.migrationComplete()
    }
  }

  @OptIn(SupabaseInternal::class)
  override suspend fun push() {

    val fromTime = lastPushTime

    logger.d { "lastPushTime: $lastPushTime" }

    val data = tasks.transactionWithResult {

      val tasksSync = tasks.selectAllUpdates(
        updated_at = fromTime,
        mapper = dataMappers.taskSyncMapper
      ).awaitAsList()

      val scopesSync = scopes.selectAllUpdates(
        updated_at = fromTime,
        mapper = dataMappers.scopeSyncMapper
      ).awaitAsList()

      val messagesSync = messages.selectAllUpdates(
        updated_at = fromTime,
        mapper = dataMappers.messageSyncMapper
      ).awaitAsList()

      val tasksScopesSync = tasksScopes.selectAllUpdates(
        updated_at = fromTime,
        mapper = dataMappers.taskScopeSyncMapper
      ).awaitAsList()

      val messageExtraSync = messageExtras.selectAllUpdates(
        updated_at = fromTime,
        mapper = dataMappers.messageExtraSyncMapper
      ).awaitAsList()

      val schedulesSync = schedules.selectAllUpdates(
        updated_at = fromTime,
        mapper = dataMappers.scheduleSyncMapper
      ).awaitAsList()

      SyncData(
        tasks = tasksSync,
        scopes = scopesSync,
        messages = messagesSync,
        tasksScopes = tasksScopesSync,
        messageExtras = messageExtraSync,
        schedules = schedulesSync
      )
    }

    if (data.containsChanges()) {
      logger.d { "Pushing: ${logSyncData(data)}" }
      supabase.postgrest
        .rpc(
          function = UPSERT_BULK_RPC,
          parameters = buildJsonObject {
            put("payload", Json.encodeToString(data).toJsonObject())
          }
        )
      logger.d { "Push Complete" }
    }

    data.maxUpdatedAt()?.let {
      logger.d { "new lastPushTime: $it" }
      lastPushTime = it
    }

  }

  private fun logSyncData(data: SyncData) = buildString {
    append("Tasks: ${data.tasks.count()}")
    appendLine()
    append("Scopes: ${data.scopes.count()}")
//    append("Scopes: ${data.scopes}")
    appendLine()
    append("TasksScopes: ${data.tasksScopes.count()}")
    appendLine()
    append("messages: ${data.messages.count()}")
    appendLine()
    append("messages_extras: ${data.messageExtras.count()}")
    appendLine()
    append("schedules: ${data.schedules.count()}")
  }

  override suspend fun pull() {

    logger.d { "lastPullTime: $lastPullTime" }

    val data = supabase.postgrest.rpc(
      function = GET_ALL_CHANGES_RPC,
      parameters = buildJsonObject {
        put("since", lastPullTime.toString())
      }
    ).decodeAs<SyncData>()

    logger.d { "Pull result:  ${logSyncData(data)}" }

    if (data.containsChanges()) {
      val localData = data.toLocalData()
      tasks.transactionWithResult {
        localData.tasks.forEach { tasks.upsertTask(it) }
        localData.scopes.forEach { scopes.upsertScope(it) }
        localData.messages.forEach { messages.upsertMessage(it) }
        localData.tasksScopes.forEach { tasksScopes.upsert(it) }
        localData.messageExtras.forEach { messageExtras.upsertMessageExtra(it) }
        localData.schedules.forEach { schedules.upsertSchedule(it) }
      }
    }

    logger.d { "Pull Complete" }

    data.maxUpdatedAt()?.let { updatedAt ->
      logger.d { "new lastPullTime: $updatedAt" }
      lastPullTime = updatedAt
    }
  }

  override suspend fun resetData() {
    lastPushTime = Instant.fromEpochMilliseconds(0)
    lastPullTime = Instant.fromEpochMilliseconds(0)
    settings.clear()
    tasks.transaction {
      var deleted = tasks.deleteAll()
      deleted += scopes.deleteAll()
      logger.d { "resetData, items deleted: $deleted" }
    }
  }

  private fun SyncData.toLocalData() = LocalData(
    tasks = tasks.map {
      it.data.run {
        Tasks(
          id,
          created_at,
          updated_at,
          completed_at,
          completed_timezone,
          title,
          priority_score,
          deleted
        )
      }
    },
    scopes = scopes.map {
      it.data.run {
        Scopes(
          id,
          scope_name,
          scope_order,
          created_at,
          updated_at,
          deleted
        )
      }
    },
    messages = messages.map {
      it.data.run {
        Messages(
          id,
          taskId,
          schedule_id,
          created_at,
          updated_at,
          content_type,
          content,
          deleted
        )
      }
    },
    tasksScopes = tasksScopes.map {
      it.run {
        Tasks_scopes(
          taskId,
          scopeId,
          createdAt,
          updatedAt,
          deleted
        )
      }
    },
    messageExtras = messageExtras.map {
      it.data.run {
        Messages_extra(
          id,
          message_id,
          url,
          created_at,
          updated_at,
          data_type,
          deleted
        )
      }
    },
    schedules = schedules.map {
      it.data.run {
        Schedules(
          id,
          task_id,
          created_at,
          updated_at,
          scheduled_at,
          timezone,
          active,
          schedule_type,
          deleted
        )
      }
    }
  )

  companion object {
    const val KEY_LAST_PUSH_TIME = "KEY_LAST_PUSH_TIME"
    const val KEY_LAST_PULL_TIME = "KEY_LAST_PULL_TIME"
    const val UPSERT_BULK_RPC = "upsert_bulk"
    const val GET_ALL_CHANGES_RPC = "get_all_changes"
  }
}