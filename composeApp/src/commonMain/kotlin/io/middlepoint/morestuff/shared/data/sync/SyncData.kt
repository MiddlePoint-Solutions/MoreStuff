@file:Suppress("PropertyName")

package io.middlepoint.morestuff.shared.data.sync

import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SyncData(
  val tasks: List<Sync<TaskSync>>,
  val scopes: List<Sync<ScopeSync>>,
  val messages: List<TaskRelationSync<MessageSync>>,
  @SerialName("tasks_scopes")
  val tasksScopes: List<TaskScopeSync>,
  @SerialName("messages_extras")
  val messageExtras: List<MessageRelationSync<MessageExtraSync>>,
  val schedules: List<TaskRelationSync<ScheduleSync>>
) {

  fun maxUpdatedAt() =
    (tasks.map { it.updatedAt }
            + scopes.map { it.updatedAt }
            + messages.map { it.updatedAt }
            + tasksScopes.map { it.updatedAt }
            + messageExtras.map { it.updatedAt }
            + schedules.map { it.updatedAt })
      .maxOrNull()

  fun containsChanges() =
    tasks.isNotEmpty() ||
            scopes.isNotEmpty() ||
            messages.isNotEmpty() ||
            tasksScopes.isNotEmpty() ||
            messageExtras.isNotEmpty() ||
            schedules.isNotEmpty()

}

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
data class TaskRelationSync<T>(
  val id: Uuid,
  @SerialName("task_id")
  val taskId: Uuid,
  @SerialName("created_at")
  val createdAt: Instant,
  @SerialName("updated_at")
  val updatedAt: Instant,
  val deleted: Boolean,
  val data: T,
)

@Serializable
data class MessageRelationSync<T>(
  val id: Uuid,
  @SerialName("message_id")
  val messageId: Uuid,
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
data class MessageExtraSync(
  val id: Uuid,
  val message_id: Uuid,
  val url: String,
  val created_at: Instant,
  val updated_at: Instant,
  val data_type: String,
  val deleted: Boolean,
)

@Serializable
data class TaskScopeSync(
  @SerialName("task_id")
  val taskId: Uuid,
  @SerialName("scope_id")
  val scopeId: Uuid,
  @SerialName("created_at")
  val createdAt: Instant,
  @SerialName("updated_at")
  val updatedAt: Instant,
  val deleted: Boolean,
)

@Serializable
data class ScheduleSync(
  val id: Uuid,
  val task_id: Uuid,
  val created_at: Instant,
  val updated_at: Instant,
  val scheduled_at: Instant,
  val timezone: String,
  val active: Boolean,
  val schedule_type: ScheduleType,
  val deleted: Boolean,
)