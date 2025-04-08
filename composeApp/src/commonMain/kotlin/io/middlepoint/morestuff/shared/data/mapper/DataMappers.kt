package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.StorageManager


interface DataMappers {
  val userDataMapper: UserDataMapper
  val messageDataMapper: MessageDataMapper
  val messageDbMapper: MessageDbMapper
  val scheduleDbMapper: ScheduleDbMapper
  val scheduleDomainMapper: ScheduleDomainMapper
  val taskDbMapper: TaskDataMapper
  val scopeDbMapper: ScopeDataMapper
}

class DataMappersImpl(
  private val storageManager: StorageManager,
) : DataMappers {

  override val userDataMapper: UserDataMapper
    get() = makeUserDataMapper()

  override val messageDataMapper: MessageDataMapper
    get() = makeMessageDataMap(storageManager::getAppStoragePathToSavedFile)

  override val messageDbMapper: MessageDbMapper
    get() = makeMessageDbMapper()

  override val scheduleDbMapper: ScheduleDbMapper
    get() = makeScheduleDbMapper()

  override val scheduleDomainMapper: ScheduleDomainMapper
    get() = makeScheduleDomainMapper()

  override val taskDbMapper: TaskDataMapper
    get() = makeTaskDbMapper()

  override val scopeDbMapper: ScopeDataMapper
    get() = makeScopeDbMapper()

}

