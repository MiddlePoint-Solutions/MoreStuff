package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.StorageManager


interface DataMappers {
    val messageDataMapper: MessageDataMapper
    val messageDbMapper: MessageDbMapper
    val scheduleDbMapper: ScheduleDbMapper
    val scheduleDomainMapper: ScheduleDomainMapper
    val taskDbMapper: TaskDataMapper
    val scopeDbMapper: ScopeDataMapper
    val iaMessageDbMapper: IAMessageDbMapper
    val iaMessageDataMapper: IAMessageDataMapper
}

class DataMappersImpl(
    private val storageManager: StorageManager,
    private val messageDataMap: MessageDataMap
) : DataMappers {

    override val messageDataMapper: MessageDataMapper
        get() = messageDataMap

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

    override val iaMessageDbMapper: IAMessageDbMapper
        get() = makeIAMessageDbMapper()

    override val iaMessageDataMapper: IAMessageDataMapper
        get() = IAMessageDataMap(storageManager)
}

