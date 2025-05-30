package io.middlepoint.morestuff.shared.data.utils

import arrow.core.Either
import arrow.core.raise.either
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readString
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.MessageExtra
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.legacy.LegacyMessage
import io.middlepoint.morestuff.shared.domain.model.core.legacy.LegacyScope
import io.middlepoint.morestuff.shared.domain.model.core.legacy.LegacyTask
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import io.middlepoint.morestuff.shared.platform.MediaHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class DataMigration(
  val items: List<MigrationItem>,
  val tasks: List<LegacyTask>,
  val scopes: List<LegacyScope>,
  val messages: List<LegacyMessage>,
)

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

  suspend fun export() {
    // get all the tasks
//    val tasks = taskRepository.getAllTasks()
//    // get scopes
//    val scopes = scopeRepository.getScopes().getOrElse { listOf() }
//    // create a task to messages map
//    val taskMessagesMap = buildMap {
//      tasks.forEach { task ->
//        val messages = messageRepository.getTaskChatMessages(task.id)
//        put(task.id, messages)
//      }
//    }
//
//    val taskScopeMap = buildMap {
//      tasks.forEach { task ->
//        val scope = scopeRepository.getScopeByTaskId(task.id).getOrElse { defaultScope }
//        put(
//          task.id,
//          scope.id
//        ) // TODO: when migrating we need to take into account the "Stuff" scope
//      }
//    }
//
//    val migrationItems = buildList {
//      tasks.forEach { task ->
//        add(
//          MigrationItem(
//            taskId = task.id,
//            scopeId = taskScopeMap.getValue(task.id),
//            messageIds = taskMessagesMap.getValue(task.id).map { it.id }
//          )
//        )
//      }
//    }
//
//    val dataMigration = DataMigration(
//      items = migrationItems,
//      tasks = tasks,
//      scopes = scopes,
//      messages = taskMessagesMap.values.flatten()
//    )
//
//    val jsonData = Json.encodeToString(dataMigration)
//    val path = mediaHandler.saveJsonToFile(jsonData, "migration_test")
//
//    path?.let {
//      mediaHandler.shareFile(it)
//    }

  }

  suspend fun import(jsonFile: PlatformFile): Either<Failure, Boolean> = either {

    val jsonContent = jsonFile.readString()
    val dataMigration = Json.decodeFromString<DataMigration>(jsonContent)

    val newScopesMap = buildMap {
      dataMigration.scopes.sortedBy { it.order }.forEach { scope ->
        scopeRepository.createScope(scope.name).onRight { newScope ->
          put(scope.id, newScope.id)
        }
      }
    }

    dataMigration.tasks.sortedBy { it.priorityScore }.forEach { oldTask ->

      val migrationItem = dataMigration.items.first { it.taskId == oldTask.id }

      val newTaskScopeId = newScopesMap.getValue(migrationItem.scopeId)
      val newTask = taskRepository.createTask(oldTask.title, newTaskScopeId, oldTask.priorityScore)

      dataMigration.messages.filter {
        migrationItem.messageIds.contains(it.id)
      }.sortedByDescending {
        it.createTime
      }.forEach { message ->
        messageRepository.createMessage(
          newTask.id,
          null,
          message.contentType.value,
          message.messageData?.let { data ->
            MessageExtra(
              id = Uuid.generate(),
              url = data.filePath,
              creationTime = data.creationTime,
              messageType = data.messageType
            )
          },
          message.content
        )
      }
    }

    true
  }

}