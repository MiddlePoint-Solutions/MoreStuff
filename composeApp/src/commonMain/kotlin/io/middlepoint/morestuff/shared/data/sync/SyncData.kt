package io.middlepoint.morestuff.shared.data.sync

import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SyncData(
  val tasks: List<Sync<TaskSync>>,
  val scopes: List<Sync<ScopeSync>>,
  val messages: List<Sync<MessageSync>>,
  @SerialName("tasks_scopes")
  val tasksScopes: List<TaskScopeSync>
)

@Serializable
data class Sync<T>(
  val id: Uuid,
  @SerialName("created_at")
  val createdAt: Instant,
  @SerialName("updated_at")
  val updatedAt: Instant,
  val deleted: Boolean,
  val data: T,
)

@Serializable
data class TaskSync(
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
data class ScopeSync(
  val id: Uuid,
  val scope_name: String,
  val scope_order: Int,
  val created_at: Instant,
  val updated_at: Instant,
  val deleted: Boolean
)

@Serializable
data class MessageSync(
  val id: Uuid,
  val taskId: Uuid,
  val schedule_id: Uuid?,
  val created_at: Instant,
  val updated_at: Instant,
  val content_type: Int,
  val content: String,
  val deleted: Boolean,
)

@Serializable
data class TaskScopeSync(
  val task_id: Uuid,
  val scope_id: Uuid,
  val created_at: Instant,
  val updated_at: Instant,
  val deleted: Boolean,
)