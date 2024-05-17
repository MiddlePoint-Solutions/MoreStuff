package io.middlepoint.morestuff.shared.data.mapper


interface DataMappers {
    val messageDataMapper: MessageDataMapper
    val messageDbMapper: MessageDbMapper
    val scheduleDbMapper: ScheduleDbMapper
    val scheduleDomainMapper: ScheduleDomainMapper
    val taskDbMapper: TaskDataMapper
    val scopeDbMapper: ScopeDataMapper
}



class DataMappersImpl() : DataMappers {

    override val messageDataMapper: MessageDataMapper
        get() = makeMessageWithDataMapper()

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

