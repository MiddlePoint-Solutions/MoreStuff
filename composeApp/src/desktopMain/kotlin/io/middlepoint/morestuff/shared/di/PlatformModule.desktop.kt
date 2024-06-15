package io.middlepoint.morestuff.shared.di

import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.data.DriverFactory
import ClipboardHelper
import ClipboardHelperImpl
import DataMigrationHelper
import DataMigrationHelperImpl
import ImageHandler
import ImageHandlerImpl
import PDFHandler
import PDFHandlerImpl
import ShareHelper
import ShareHelperImpl
import TimeFormatter
import TimeFormatterImpl
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

    factoryOf(::TimeFormatterImpl) bind TimeFormatter::class
    factoryOf(::ClipboardHelperImpl) bind ClipboardHelper::class
    factoryOf(::VoiceToTextParserImpl) bind VoiceToTextParser::class
    factoryOf(::ShareHelperImpl) bind ShareHelper::class
    factoryOf(::DataMigrationHelperImpl) bind DataMigrationHelper::class
    factoryOf(::ImageHandlerImpl) bind ImageHandler::class
    factoryOf(::PDFHandlerImpl) bind PDFHandler::class

    singleOf(::SchedulerImpl) bind Scheduler::class
    singleOf(::NotifierImpl) bind Notifier::class

    single<Settings> {
        val preferences = Preferences.userRoot()
        PreferencesSettings(preferences)
    }
}