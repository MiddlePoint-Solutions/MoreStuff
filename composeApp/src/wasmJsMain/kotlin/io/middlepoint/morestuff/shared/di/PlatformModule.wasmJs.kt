package io.middlepoint.morestuff.shared.di

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import io.middlepoint.morestuff.shared.data.DriverFactory
import io.middlepoint.morestuff.shared.data.VoiceToTextParser
import io.middlepoint.morestuff.shared.data.VoiceToTextParserImpl
import io.middlepoint.morestuff.shared.domain.service.Notifier
import io.middlepoint.morestuff.shared.domain.service.NotifierImpl
import io.middlepoint.morestuff.shared.domain.service.Scheduler
import io.middlepoint.morestuff.shared.domain.service.SchedulerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    factory { Logger.withTag(it.getOrNull() ?: "MoreStuff-iOS") }
    factoryOf(::DriverFactory)

    factoryOf(::VoiceToTextParserImpl) bind VoiceToTextParser::class
    singleOf(::SchedulerImpl) bind Scheduler::class
    singleOf(::NotifierImpl) bind Notifier::class

    single<Settings> { StorageSettings() }
}
