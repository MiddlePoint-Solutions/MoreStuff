package io.middlepoint.morestuff.shared.di

import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.data.DriverFactory
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import io.middlepoint.morestuff.shared.data.VoiceToTextParser
import io.middlepoint.morestuff.shared.data.VoiceToTextParserImpl
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
import java.util.prefs.Preferences

actual val platformModule: Module = module {
    factory { Logger.withTag(it.getOrNull() ?: "MoreStuffDesktop") }
    factoryOf(::DriverFactory)

    factoryOf(::VoiceToTextParserImpl) bind VoiceToTextParser::class
    singleOf(::SchedulerImpl) bind Scheduler::class
    singleOf(::NotifierImpl) bind Notifier::class
    factoryOf(::SharedFunctionsHandler)

    factory<Settings>(named(SharedSettings.Encrypted)) {
        val preferences = Preferences.userRoot()
        PreferencesSettings(preferences)
    }

    factory<Settings>(named(SharedSettings.Unencrypted)) {
        val preferences = Preferences.userRoot()
        PreferencesSettings(preferences)
    }
}