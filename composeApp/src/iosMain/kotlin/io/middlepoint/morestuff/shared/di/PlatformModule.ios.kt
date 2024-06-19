package io.middlepoint.morestuff.shared.di

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
import co.touchlab.kermit.Logger
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
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
import platform.Foundation.NSUserDefaults

actual val platformModule: Module = module {
    factory { Logger.withTag(it.getOrNull() ?: "MoreStuff-iOS") }
    factoryOf(::DriverFactory)

    factoryOf(::ClipboardHelperImpl) bind ClipboardHelper::class
    factoryOf(::VoiceToTextParserImpl) bind VoiceToTextParser::class
    factoryOf(::ShareHelperImpl) bind ShareHelper::class
    factoryOf(::DataMigrationHelperImpl) bind DataMigrationHelper::class
    factoryOf(::ImageHandlerImpl) bind ImageHandler::class
    factoryOf(::PDFHandlerImpl) bind PDFHandler::class

    singleOf(::SchedulerImpl) bind Scheduler::class
    singleOf(::NotifierImpl) bind Notifier::class

    single<Settings> {
        val delegate: NSUserDefaults = NSUserDefaults.standardUserDefaults
        NSUserDefaultsSettings(delegate)
    }
}
