package co.softov.morestuff.android.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import androidx.work.WorkManager
import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.android.app.DevToolsImpl
import co.softov.morestuff.android.app.SchedulerImpl
import co.softov.morestuff.android.data.Constants
import co.softov.morestuff.android.data.mapper.SelectMasterMessagesMapper
import co.softov.morestuff.android.data.mapper.SelectMessageByIdMapper
import co.softov.morestuff.android.data.mapper.SelectMessageByTaskIdMapper
import co.softov.morestuff.android.data.mapper.makeTaskWithScheduleDataMapper
import co.softov.morestuff.android.data.mapper.makeMessageDbMapper
import co.softov.morestuff.android.data.mapper.makeMessageWithDataMapper
import co.softov.morestuff.android.data.mapper.makeScheduleDataMapper
import co.softov.morestuff.android.data.mapper.makeScheduleDomainMapper
import co.softov.morestuff.android.data.mapper.makeScheduleWithTitleDbMapper
import co.softov.morestuff.android.data.mapper.makeSelectTaskMessagesByContentTypeMapper
import co.softov.morestuff.android.data.mapper.makeTaskDbMapper
import co.softov.morestuff.android.data.repository.*
import co.softov.morestuff.android.data.service.NotifierImpl
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.repository.*
import co.softov.morestuff.android.domain.service.Notifier
import co.softov.morestuff.android.domain.service.Scheduler
import co.softov.morestuff.android.domain.usecase.message.GetPagedMessagesUseCase
import co.softov.morestuff.android.domain.usecase.message.GetPagedMessagesUseCaseImpl
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.domain.usecase.time.TimeFormatterImpl
import co.softov.morestuff.db.Schedule
import co.softov.morestuff.db.StuffDb
import co.softov.morestuff.db.Task
import com.russhwolf.settings.*
import com.squareup.sqldelight.EnumColumnAdapter
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

    single<Settings> { SharedPreferencesSettings(getSharedPreferences(androidContext())) }

    singleOf(::DevToolsImpl) bind DevTools::class
    // Database
    single { createDatabase(androidApplication()) }

    // WorkManager
    single {
        WorkManager.getInstance(androidApplication())
    }
    single { NotificationManagerCompat.from(androidApplication()) }

    //Time
    single<TimeFormatter> { TimeFormatterImpl() }

    // Repositories
    single<TaskRepository> {
        TaskRepositoryImpl(
            database = get(),
            mapTaskData = makeTaskDbMapper(),
            mapTaskWithScheduleData = makeTaskWithScheduleDataMapper(),
            timeManager = get()
        )
    }

    single<MessageRepository> {
        MessageRepositoryImpl(
            database = get(),
            mapMessageDb = makeMessageDbMapper(timeFormatter = get()),
            mapMessageTaskChatDb = makeSelectTaskMessagesByContentTypeMapper(timeFormatter = get()),
            selectMasterMessagesMapper = SelectMasterMessagesMapper(timeFormatter = get()),
            selectMessageByTaskIdMapper = SelectMessageByTaskIdMapper(timeFormatter = get()),
            selectMessageByIdMapper = SelectMessageByIdMapper(timeFormatter = get()),
            imageMessageDataMapper = makeMessageWithDataMapper(),
            timeManager = get(),
            context = get()
        )
    }

    single<ScheduleRepository> {
        ScheduleRepositoryImpl(
            database = get(),
            mapScheduleData = makeScheduleDataMapper(),
            mapScheduleDomain = makeScheduleDomainMapper(),
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
    factoryOf(::GetPagedMessagesUseCaseImpl) bind GetPagedMessagesUseCase::class
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
        ),
        taskAdapter = Task.Adapter(
            task_typeAdapter = EnumColumnAdapter()
        ),
        scheduleAdapter = Schedule.Adapter(
            schedule_typeAdapter = EnumColumnAdapter()
        )
    )
}