package io.middlepoint.morestuff.shared.di

import ClipboardHelper
import ClipboardHelperImpl
import TimeFormatter
import TimeFormatterImpl
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.middlepoint.morestuff.shared.domain.service.NotifierImpl
import io.middlepoint.morestuff.shared.domain.service.SchedulerImpl
import io.middlepoint.morestuff.shared.data.DriverFactory
import ImageHandlerImpl
import PDFHandlerImpl
import io.middlepoint.morestuff.shared.data.VoiceToTextParser
import io.middlepoint.morestuff.shared.data.VoiceToTextParserImpl
import DataMigrationHelper
import DataMigrationHelperImpl
import ImageHandler
import io.middlepoint.morestuff.shared.domain.service.Notifier
import PDFHandler
import io.middlepoint.morestuff.shared.domain.service.Scheduler
import ShareHelper
import ShareHelperImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {

    factory { Logger.withTag(it.getOrNull() ?: "MoreStuffAndroid") }
    factory<DriverFactory> { DriverFactory(androidApplication()) }

    factoryOf(::TimeFormatterImpl) bind TimeFormatter::class
    factoryOf(::ClipboardHelperImpl) bind ClipboardHelper::class
    factoryOf(::VoiceToTextParserImpl) bind VoiceToTextParser::class
    factoryOf(::ShareHelperImpl) bind ShareHelper::class
    factoryOf(::DataMigrationHelperImpl) bind DataMigrationHelper::class
    factoryOf(::ImageHandlerImpl) bind ImageHandler::class
    factoryOf(::PDFHandlerImpl) bind PDFHandler::class

    singleOf(::SchedulerImpl) bind Scheduler::class
    singleOf(::NotifierImpl) bind Notifier::class

    single { WorkManager.getInstance(androidApplication()) }
    single { NotificationManagerCompat.from(androidApplication()) }


    single<Settings> {
        SharedPreferencesSettings(
            PreferenceManager.getDefaultSharedPreferences(androidApplication())
        )
    }
}
