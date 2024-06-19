package io.middlepoint.morestuff.shared.di

import io.middlepoint.morestuff.shared.ClipboardHelper
import io.middlepoint.morestuff.shared.ClipboardHelperImpl
import io.middlepoint.morestuff.shared.TimeFormatter
import io.middlepoint.morestuff.shared.TimeFormatterImpl
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.middlepoint.morestuff.shared.domain.service.NotifierImpl
import io.middlepoint.morestuff.shared.domain.service.SchedulerImpl
import io.middlepoint.morestuff.shared.data.DriverFactory
import io.middlepoint.morestuff.shared.ImageHandlerImpl
import io.middlepoint.morestuff.shared.PDFHandlerImpl
import io.middlepoint.morestuff.shared.data.VoiceToTextParser
import io.middlepoint.morestuff.shared.data.VoiceToTextParserImpl
import io.middlepoint.morestuff.shared.DataMigrationHelper
import io.middlepoint.morestuff.shared.DataMigrationHelperImpl
import io.middlepoint.morestuff.shared.ImageHandler
import io.middlepoint.morestuff.shared.domain.service.Notifier
import io.middlepoint.morestuff.shared.PDFHandler
import io.middlepoint.morestuff.shared.domain.service.Scheduler
import io.middlepoint.morestuff.shared.ShareHelper
import io.middlepoint.morestuff.shared.ShareHelperImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {

    factory { Logger.withTag(it.getOrNull() ?: "MoreStuffAndroid") }
    factory<DriverFactory> { DriverFactory(androidApplication()) }

    factoryOf(::VoiceToTextParserImpl) bind VoiceToTextParser::class
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
