package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.StorageManager
import io.middlepoint.morestuff.shared.data.sync.ScheduleSync
import io.middlepoint.morestuff.shared.data.sync.ScopeSync
import io.middlepoint.morestuff.shared.data.sync.Sync
import io.middlepoint.morestuff.shared.data.sync.TaskRelationSync
import io.middlepoint.morestuff.shared.data.sync.TaskSync
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.domain.model.core.Task

typealias DataMapper<I, O> = (I) -> O

inline fun <I, O> mapList(input: List<I>, mapListItem: (I) -> O): List<O> {
  return input.map { mapListItem(it) }
}

inline fun <I, O> mapNullInputList(input: List<I>?, mapListItem: (I) -> O): List<O> {
  return input?.map { mapListItem(it) } ?: emptyList()
}

inline fun <I, O> mapNullOutputList(input: List<I>, mapListItem: (I) -> O): List<O>? {
  return if (input.isEmpty()) null else input.map { mapListItem(it) }
}

interface DataMappers {
  val userDataMapper: UserDataMapper
  val messageDataMapper: MessageDataMapper
  val messageSyncMapper: MessageSyncMapper
  val messageExtraSyncMapper: MessageExtraSyncMapper
  val scheduleDataMapper: ScheduleDataMapper<Schedule>
  val scheduleSyncMapper: ScheduleDataMapper<TaskRelationSync<ScheduleSync>>
  val taskDataMapper: TaskDataMapper<Task>
  val taskSyncMapper: TaskDataMapper<Sync<TaskSync>>
  val taskScopeSyncMapper: TaskScopeSyncMapper
  val scopeDataMapper: ScopeDataMapper<Scope>
  val scopeSyncMapper: ScopeDataMapper<Sync<ScopeSync>>
}

class DataMappersImpl(
  private val storageManager: StorageManager,
) : DataMappers {

  override val userDataMapper: UserDataMapper
    get() = makeUserDataMapper()

  override val messageDataMapper: MessageDataMapper
    get() = makeMessageDataMap(storageManager::getAppStoragePathToSavedFile)

  override val messageSyncMapper: MessageSyncMapper
    get() = makeMessageSyncMapper()

  override val messageExtraSyncMapper: MessageExtraSyncMapper
    get() = makeMessageExtraSyncMapper()

  override val scheduleDataMapper: ScheduleDataMapper<Schedule>
    get() = makeScheduleDataMapper()

  override val scheduleSyncMapper: ScheduleDataMapper<TaskRelationSync<ScheduleSync>>
    get() = makeScheduleSyncMapper()

  override val taskDataMapper: TaskDataMapper<Task>
    get() = makeTaskDataMapper()

  override val taskSyncMapper: TaskDataMapper<Sync<TaskSync>>
    get() = makeTaskSyncMapper()

  override val taskScopeSyncMapper: TaskScopeSyncMapper
    get() = makeTaskScopeSyncMapper()

  override val scopeDataMapper: ScopeDataMapper<Scope>
    get() = makeScopeDataMapper()

  override val scopeSyncMapper: ScopeDataMapper<Sync<ScopeSync>>
    get() = makeScopeSyncMapper()
}
