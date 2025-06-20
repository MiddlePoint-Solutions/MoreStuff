package io.middlepoint.morestuff.shared.data.utils

import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.MediaHandler
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.model.core.ScopeDomain
import io.middlepoint.morestuff.shared.domain.model.core.TaskDomain
import io.middlepoint.morestuff.shared.domain.model.core.defaultScope
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class DataMigration(
  val items: List<MigrationItem>,
  val tasks: List<TaskDomain>,
  val scopes: List<ScopeDomain>,
  val messages: List<Message>,
) {
  fun infoString(): String {
    return "DataMigration(items=${items.size}, tasks=${tasks.size}, scopes=${scopes.size}, messages=${messages.size})"
  }
}

@Serializable
data class MigrationItem(
  val taskId: Long,
  val scopeId: Long,
  val messageIds: List<Long>
)

class MigrationHelper(
  val taskRepository: TaskRepository,
  val messageRepository: MessageRepository,
  val scopeRepository: ScopeRepository,
  val mediaHandler: MediaHandler,
) {

  suspend fun export(share: Boolean = false) {
    // get all the tasks
    val tasks = taskRepository.getAllTasks()
    // get scopes
    val scopes = scopeRepository.getScopes().getOrElse { listOf() }
    // create a task to messages map
    val taskMessagesMap = buildMap {
      tasks.forEach { task ->
        val messages = messageRepository.getTaskChatMessages(task.id)
        put(task.id, messages)
      }
    }

    val taskScopeMap = buildMap {
      tasks.forEach { task ->
        try { // This is to fix errors that arise when task belongs to more then one scope (legacy issue).
          scopeRepository.getScopeByTaskId(task.id).getOrElse { defaultScope }
        } catch (e: Throwable) {
          defaultScope
        }.let { scope ->
          put(
            task.id,
            scope.id
          ) // TODO: when migrating we need to take into account the "Stuff" scope
        }
      }
    }

    val migrationItems = buildList {
      tasks.forEach { task ->
        add(
          MigrationItem(
            taskId = task.id,
            scopeId = taskScopeMap.getValue(task.id),
            messageIds = taskMessagesMap.getValue(task.id).map { it.id }
          )
        )
      }
    }

    val dataMigration = DataMigration(
      items = migrationItems,
      tasks = tasks,
      scopes = scopes,
      messages = taskMessagesMap.values.flatten()
    )

    Logger.apply { setTag("MigrationHelper") }.d(dataMigration.infoString())

    val jsonData = Json.encodeToString(dataMigration)
    val path = mediaHandler.saveJsonToFile(jsonData, "migration_json")

    Logger.apply { setTag("MigrationHelper") }.d("Success ${!path.isNullOrBlank()}")

    if (share) {
      path?.let {
        mediaHandler.shareFile(it)
      }
    }
  }

}