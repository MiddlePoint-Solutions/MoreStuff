package io.middlepoint.morestuff.shared.data.sync

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.toJsonObject
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable
data class TaskSync(
  val id: Uuid,
  @SerialName("created_at")
  val createdAt: Instant,
  @SerialName("updated_at")
  val updatedAt: Instant,
  val deleted: Boolean,
  val data: TaskJson,
)

@Serializable
data class TaskJson(
  val id: Uuid,
  val created_at: Instant,
  val updated_at: Instant,
  val completed_at: Instant?,
  val completed_timezone: String?,
  val title: String,
  val priority_score: Long,
  val deleted: Boolean,
)

@Serializable
data class DataSync(
  val tasks: List<TaskSync>
)

interface DataSyncManager {

  suspend fun push()

}

class DataSyncManagerImpl(
  database: StuffDb,
  private val supabase: SupabaseClient
) : DataSyncManager {

  private val tasks = database.tasksQueries

  @OptIn(SupabaseInternal::class)
  override suspend fun push() {

    val tasksDataSync =
      tasks.selectAllActive { id, created_at, updated_at, completed_at, completed_timezone, title, priority_score, deleted ->
        TaskSync(
          id = id,
          createdAt = created_at,
          updatedAt = updated_at,
          deleted = deleted,
          data = TaskJson(
            id,
            created_at,
            updated_at,
            completed_at,
            completed_timezone,
            title,
            priority_score,
            deleted
          )
        )
      }.executeAsList()

    val dataSync = DataSync(
      tasks = tasksDataSync
    )

//    supabase.postgrest.from("tasks").insert(tasksDataSync[0])

    supabase.postgrest
      .rpc("upsert_bulk", buildJsonObject {
        put("payload", Json.encodeToString(dataSync).toJsonObject())
      })

  }
}