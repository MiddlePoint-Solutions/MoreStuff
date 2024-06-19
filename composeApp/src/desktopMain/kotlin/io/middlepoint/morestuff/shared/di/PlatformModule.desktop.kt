package io.middlepoint.morestuff.shared.di

import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.data.DriverFactory
import io.middlepoint.morestuff.shared.ClipboardHelper
import io.middlepoint.morestuff.shared.ClipboardHelperImpl
import io.middlepoint.morestuff.shared.DataMigrationHelper
import io.middlepoint.morestuff.shared.DataMigrationHelperImpl
import io.middlepoint.morestuff.shared.ImageHandler
import io.middlepoint.morestuff.shared.ImageHandlerImpl
import io.middlepoint.morestuff.shared.PDFHandler
import io.middlepoint.morestuff.shared.PDFHandlerImpl
import io.middlepoint.morestuff.shared.ShareHelper
import io.middlepoint.morestuff.shared.ShareHelperImpl
import io.middlepoint.morestuff.shared.TimeFormatter
import io.middlepoint.morestuff.shared.TimeFormatterImpl
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
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
import java.util.prefs.Preferences

actual val platformModule: Module = module {
    factory { Logger.withTag(it.getOrNull() ?: "MoreStuffDesktop") }
    factoryOf(::DriverFactory)

    factoryOf(::VoiceToTextParserImpl) bind VoiceToTextParser::class
    singleOf(::SchedulerImpl) bind Scheduler::class
    singleOf(::NotifierImpl) bind Notifier::class

    single<Settings> {
        val preferences = Preferences.userRoot()
        PreferencesSettings(preferences)
    }
}