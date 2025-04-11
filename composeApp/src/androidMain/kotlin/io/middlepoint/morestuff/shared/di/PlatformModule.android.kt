package io.middlepoint.morestuff.shared.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.work.WorkManager
import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.middlepoint.morestuff.shared.data.DriverFactory
import io.middlepoint.morestuff.shared.data.VoiceToTextParser
import io.middlepoint.morestuff.shared.data.VoiceToTextParserImpl
import io.middlepoint.morestuff.shared.data.repository.OpenAIRepositoryImpl
import io.middlepoint.morestuff.shared.domain.service.Notifier
import io.middlepoint.morestuff.shared.domain.service.NotifierImpl
import io.middlepoint.morestuff.shared.domain.service.Scheduler
import io.middlepoint.morestuff.shared.domain.service.SchedulerImpl
import io.middlepoint.morestuff.shared.ui.utils.SharedFunctionsHandler
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {

    factory { Logger.withTag(it.getOrNull() ?: "MoreStuffAndroid") }
    factory<DriverFactory> { DriverFactory(androidApplication()) }

    factoryOf(::VoiceToTextParserImpl) bind VoiceToTextParser::class
    singleOf(::SchedulerImpl) bind Scheduler::class
    singleOf(::NotifierImpl) bind Notifier::class
    singleOf(::SharedFunctionsHandler)

    single { WorkManager.getInstance(androidApplication()) }
    single { NotificationManagerCompat.from(androidApplication()) }


    single<Settings>(named(SharedSettings.Encrypted)) {
        SharedPreferencesSettings(
            EncryptedSharedPreferences.create(
                OpenAIRepositoryImpl.ENCRYPTED_DATABASE_NAME,
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                get<Context>(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            ),
            false
        )
    }

    single<Settings>(named(SharedSettings.Unencrypted)) {
        SharedPreferencesSettings(
            androidApplication().getSharedPreferences(
                "UNENCRYPTED_SETTINGS",
                Context.MODE_PRIVATE
            ),
            false
        )
    }
}
