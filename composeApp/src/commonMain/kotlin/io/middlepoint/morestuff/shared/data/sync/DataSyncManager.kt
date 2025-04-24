package io.middlepoint.morestuff.shared.data.sync

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.toJsonObject
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.data.mapper.DataMappers
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

      SyncData(
        tasks = tasksSync,
        scopes = scopesSync,
        messages = messagesSync,
        tasksScopes = tasksScopesSync
      )
    }



    supabase.postgrest
      .rpc(
        function = "upsert_bulk",
        parameters = buildJsonObject {
          put("payload", Json.encodeToString(syncData).toJsonObject())
        }
      )
  }

  override suspend fun pull() {
    val dataSync = supabase.postgrest.rpc(
      function = "get_all_changes",
      parameters = buildJsonObject {
        put("since", Instant.fromEpochMilliseconds(0).toString())
      }
    )

    logger.d { "Pull result: ${dataSync.decodeAs<SyncData>()}" }
  }

  companion object {
    const val KEY_LAST_PUSH_TIME = "KEY_LAST_PUSH_TIME"
  }
}