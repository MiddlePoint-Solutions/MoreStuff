package io.middlepoint.morestuff.shared.data.sync

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.toJsonObject
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.data.mapper.DataMappers
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

interface DataSyncManager {

  suspend fun push()
  suspend fun pull()

}

class DataSyncManagerImpl(
  database: StuffDb,
  private val dataMappers: DataMappers,
  private val supabase: SupabaseClient,
  private val settings: Settings
) : DataSyncManager {

  private val tasks = database.tasksQueries
  private val scopes = database.scopesQueries
  private val messages = database.messagesQueries
  private val tasksScopes = database.tasksScopesQueries
  private val messageExtras = database.messagesExtraQueries
  private val schedules = database.schedulesQueries

  private val logger = Logger.withTag("DataSyncManager")

  private var lastPushTime: Instant
    get() = Instant.fromEpochMilliseconds(settings.getLong(KEY_LAST_PUSH_TIME, 0))
    set(value) = settings.putLong(KEY_LAST_PUSH_TIME, value.toEpochMilliseconds())

  @OptIn(SupabaseInternal::class)
  override suspend fun push() {

    val fromTime = lastPushTime

    val syncData = tasks.transactionWithResult {

      val tasksSync = tasks.selectAllUpdates(
        updated_at = fromTime,
        mapper = dataMappers.taskSyncMapper
      ).executeAsList()

      val scopesSync = scopes.selectAllUpdates(
        updated_at = fromTime,
        mapper = dataMappers.scopeSyncMapper
      ).executeAsList()

      val messagesSync = messages.selectAllUpdates(
        updated_at = fromTime,
        mapper = dataMappers.messageSyncMapper
      ).executeAsList()

      val tasksScopesSync = tasksScopes.selectAllUpdates(
        updated_at = fromTime,
        mapper = dataMappers.taskScopeSyncMapper
      ).executeAsList()

      val messageExtraSync = messageExtras.selectAllUpdates(
        updated_at = fromTime,
        mapper = dataMappers.messageExtraSyncMapper
      ).executeAsList()

      val schedulesSync = schedules.selectAllUpdates(
        updated_at = fromTime,
        mapper = dataMappers.scheduleSyncMapper
      ).executeAsList()

      SyncData(
        tasks = tasksSync,
        scopes = scopesSync,
        messages = messagesSync,
        tasksScopes = tasksScopesSync,
        messageExtras = messageExtraSync,
        schedules = schedulesSync
      )
    }

    logger.d { "Pushing: ${logSyncData(syncData)}" }
    supabase.postgrest
      .rpc(
        function = UPSERT_BULK_RPC,
        parameters = buildJsonObject {
          put("payload", Json.encodeToString(syncData).toJsonObject())
        }
      )
    logger.d { "Pushed!" }

    syncData.maxUpdatedAt()?.let { updatedAt ->
      lastPushTime = updatedAt
    }

  }

  private fun logSyncData(data: SyncData) = buildString {
    append("Tasks: ${data.tasks.count()}")
    appendLine()
    append("Scopes: ${data.scopes.count()}")
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
    val dataSync = supabase.postgrest.rpc(
      function = GET_ALL_CHANGES_RPC,
      parameters = buildJsonObject {
        put("since", Instant.fromEpochMilliseconds(0).toString())
      }
    )

    logger.d { "Pull result: ${dataSync.decodeAs<SyncData>()}" }
  }

  companion object {
    const val KEY_LAST_PUSH_TIME = "KEY_LAST_PUSH_TIME"
    const val UPSERT_BULK_RPC = "upsert_bulk"
    const val GET_ALL_CHANGES_RPC = "get_all_changes"
  }
}