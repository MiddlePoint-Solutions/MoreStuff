package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.StorageManager
import io.middlepoint.morestuff.shared.data.model.TaskSync
import io.middlepoint.morestuff.shared.domain.model.core.Task


interface DataMappers {
  val userDataMapper: UserDataMapper
  val messageDataMapper: MessageDataMapper
  val scheduleDataMapper: ScheduleDataMapper
  val taskDataMapper: TaskDataMapper<Task>
  val taskSyncMapper: TaskDataMapper<TaskSync>
  val scopeDataMapper: ScopeDataMapper
}

class DataMappersImpl(
  private val storageManager: StorageManager,
) : DataMappers {

  override val userDataMapper: UserDataMapper
    get() = makeUserDataMapper()

  override val messageDataMapper: MessageDataMapper
    get() = makeMessageDataMap(storageManager::getAppStoragePathToSavedFile)

  override val scheduleDataMapper: ScheduleDataMapper
    get() = makeScheduleDataMapper()

  override val taskDataMapper: TaskDataMapper<Task>
    get() = makeTaskDataMapper()

  override val taskSyncMapper: TaskDataMapper<TaskSync>
    get() = makeTaskSyncMapper()

  override val scopeDataMapper: ScopeDataMapper
    get() = makeScopeDbMapper()

}
