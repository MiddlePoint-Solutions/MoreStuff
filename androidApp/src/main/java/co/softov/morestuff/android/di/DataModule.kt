package co.softov.morestuff.android.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import co.softov.morestuff.android.app.DevToolsImpl
import co.softov.morestuff.android.app.SchedulerImpl
import co.softov.morestuff.android.data.Constants
import co.softov.morestuff.android.data.mapper.makeMessageDbMapper
import co.softov.morestuff.android.data.mapper.makeScheduleDbMapper
import co.softov.morestuff.android.data.mapper.makeScheduleWithTitleDbMapper
import co.softov.morestuff.android.data.mapper.makeTaskDbMapper
import co.softov.morestuff.android.data.repository.*
import co.softov.morestuff.android.data.service.NotifierImpl
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.repository.*
import co.softov.morestuff.android.domain.service.Notifier
import co.softov.morestuff.android.domain.service.Scheduler
import co.softov.morestuff.android.domain.usecase.message.GetPagedMessages
import co.softov.morestuff.android.domain.usecase.message.GetPagedMessagesImpl
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.domain.usecase.time.TimeFormatterImpl
import co.softov.morestuff.db.StuffDb
import com.russhwolf.settings.*
import com.squareup.sqldelight.android.AndroidSqliteDriver
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {

    // Debugging
    single<ObservableSettings> {
        SharedPreferencesSettings(getSharedPreferences(androidContext()))
    }

    single { WorkManager.getInstance(androidContext()) }

    singleOf(::DevToolsImpl) bind DevTools::class
    // Database
    single { createDatabase(androidApplication()) }

    //Time
    single<TimeFormatter> { TimeFormatterImpl() }

    // Repositories
    single<TaskRepository> {
        TaskRepositoryImpl(
            database = get(),
            mapTaskDb = makeTaskDbMapper(),
            timeManager = get()
        )
    }

    single<MessageRepository> {
        MessageRepositoryImpl(
            database = get(),
            mapMessageDb = makeMessageDbMapper(timeFormatter = get()),
            timeManager = get()
        )
    }

    single<ScheduleRepository> {
        ScheduleRepositoryImpl(
            database = get(),
            mapScheduleDb = makeScheduleDbMapper(),
            mapScheduleWithTitleDb = makeScheduleWithTitleDbMapper(),
            timeManager = get()
        )
    }

    single<PreferenceRepository> {
        PreferenceRepositoryImpl(
            getSharedPreferences(
                androidContext()
            )
        )
    }


    single<UserRepository> {
        UserRepositoryImpl(
            settings = getSettings(androidContext()),
        )
    }

    // Services
    singleOf(::SchedulerImpl) bind Scheduler::class
    singleOf(::NotifierImpl) bind Notifier::class

    // Platform specific use-cases
    factoryOf(::GetPagedMessagesImpl) bind GetPagedMessages::class
}

internal fun getSharedPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
}

internal fun getSettings(context: Context): Settings {
    return SharedPreferencesSettings(getSharedPreferences(context))
}


internal fun createDatabase(context: Context): StuffDb {
    return StuffDb(
        AndroidSqliteDriver(
            StuffDb.Schema,
            context,
            Constants.DATABASE_NAME
        )
    )
}