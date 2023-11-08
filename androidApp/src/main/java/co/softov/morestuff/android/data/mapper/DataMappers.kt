package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.util.TimeFormatter


interface DataMappers {
    val messageDataMapper: MessageDataMapper
    val messageDbMapper: MessageDbMapper
    val scheduleDbMapper: ScheduleDbMapper
    val scheduleDomainMapper: ScheduleDomainMapper
    val taskDbMapper: TaskDataMapper
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

}

