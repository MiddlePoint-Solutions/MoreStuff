package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.StorageManager
import io.middlepoint.morestuff.shared.data.model.TaskData
import io.middlepoint.morestuff.shared.data.model.TaskSync
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
  val scheduleDataMapper: ScheduleDataMapper
  val taskDataMapper: TaskDataMapper<Task>
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

  override val scopeDataMapper: ScopeDataMapper
    get() = makeScopeDbMapper()

}
