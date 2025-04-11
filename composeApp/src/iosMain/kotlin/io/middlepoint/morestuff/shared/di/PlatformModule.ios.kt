package io.middlepoint.morestuff.shared.di

import co.touchlab.kermit.Logger
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import io.middlepoint.morestuff.shared.data.DriverFactory
import io.middlepoint.morestuff.shared.data.VoiceToTextParser
import io.middlepoint.morestuff.shared.data.VoiceToTextParserImpl
import io.middlepoint.morestuff.shared.data.repository.OpenAIRepositoryImpl
import io.middlepoint.morestuff.shared.domain.service.Notifier
import io.middlepoint.morestuff.shared.domain.service.NotifierImpl
import io.middlepoint.morestuff.shared.domain.service.Scheduler
import io.middlepoint.morestuff.shared.domain.service.SchedulerImpl
import io.middlepoint.morestuff.shared.ui.utils.SharedFunctionsHandler
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults


@OptIn(ExperimentalSettingsImplementation::class)
actual val platformModule: Module = module {
    factory { Logger.withTag(it.getOrNull() ?: "MoreStuff-iOS") }
    factoryOf(::DriverFactory)

    factoryOf(::VoiceToTextParserImpl) bind VoiceToTextParser::class
    singleOf(::SchedulerImpl) bind Scheduler::class
    singleOf(::NotifierImpl) bind Notifier::class
    singleOf(::SharedFunctionsHandler)

    single<Settings>(named(SharedSettings.Encrypted)) {
        KeychainSettings(service = "ENCRYPTED_SETTINGS")
    }

    single<Settings>(named(SharedSettings.Encrypted)) {
        KeychainSettings(service = OpenAIRepositoryImpl.ENCRYPTED_DATABASE_NAME)
    }

    single<Settings>(named(SharedSettings.Unencrypted)) {
        val delegate: NSUserDefaults = NSUserDefaults.standardUserDefaults
        NSUserDefaultsSettings(delegate)
    }
}
