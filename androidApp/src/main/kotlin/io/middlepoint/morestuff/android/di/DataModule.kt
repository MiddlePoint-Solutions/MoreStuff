package io.middlepoint.morestuff.android.di

import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.middlepoint.morestuff.android.app.service.NotifierImpl
import io.middlepoint.morestuff.android.app.service.SchedulerImpl
import io.middlepoint.morestuff.android.data.Constants
import io.middlepoint.morestuff.android.data.mapper.DataMappers
import io.middlepoint.morestuff.android.data.mapper.DataMappersImpl
import io.middlepoint.morestuff.android.data.repository.MessageRepositoryImpl
import io.middlepoint.morestuff.android.data.repository.PriorityRepositoryImpl
import io.middlepoint.morestuff.android.data.repository.ScheduleRepositoryImpl
import io.middlepoint.morestuff.android.data.repository.ScopeRepositoryImpl
import io.middlepoint.morestuff.android.data.repository.TaskRepositoryImpl
import io.middlepoint.morestuff.android.data.repository.UserRepositoryImpl
import io.middlepoint.morestuff.android.data.service.DataMigrationHelperImpl
import io.middlepoint.morestuff.android.data.service.DevToolsImpl
import io.middlepoint.morestuff.android.data.service.ImageHandlerImpl
import io.middlepoint.morestuff.android.data.service.PDFHandlerImpl
import io.middlepoint.morestuff.android.data.utils.TimeFormatterImpl
import io.middlepoint.morestuff.android.domain.DevTools
import io.middlepoint.morestuff.android.domain.redux.AppState
import io.middlepoint.morestuff.android.domain.repository.MessageRepository
import io.middlepoint.morestuff.android.domain.repository.PriorityRepository
import io.middlepoint.morestuff.android.domain.repository.ScheduleRepository
import io.middlepoint.morestuff.android.domain.repository.ScopeRepository
import io.middlepoint.morestuff.android.domain.repository.TaskRepository
import io.middlepoint.morestuff.android.domain.repository.UserRepository
import io.middlepoint.morestuff.android.domain.service.DataMigrationHelper
import io.middlepoint.morestuff.android.domain.service.ImageHandler
import io.middlepoint.morestuff.android.domain.service.Notifier
import io.middlepoint.morestuff.android.domain.service.PDFHandler
import io.middlepoint.morestuff.android.domain.service.Scheduler
import io.middlepoint.morestuff.android.domain.usecase.message.GetPagedMessagesUseCase
import io.middlepoint.morestuff.android.domain.usecase.message.GetPagedMessagesUseCaseImpl
import io.middlepoint.morestuff.android.domain.util.TimeFormatter
import io.middlepoint.morestuff.db.Message
import io.middlepoint.morestuff.db.Schedule
import io.middlepoint.morestuff.db.Scope
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.db.Task
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {

    single<ImageHandler> { ImageHandlerImpl(timeManager = get(), context = androidApplication()) }
    single<PDFHandler> { PDFHandlerImpl(context = androidApplication()) }

    single { AppState() }

    single<Settings> { SharedPreferencesSettings(getSharedPreferences(androidContext())) }

    singleOf(::DevToolsImpl) bind DevTools::class
    singleOf(::DataMappersImpl) bind DataMappers::class

    // Database
    single { createDatabase(androidApplication()) }

    // WorkManager
    single {
        WorkManager.getInstance(androidApplication())
    }

    single { NotificationManagerCompat.from(androidApplication()) }

    //Time
    single<TimeFormatter> { TimeFormatterImpl(androidApplication()) }

    // Repositories
    single<TaskRepository> {
        TaskRepositoryImpl(
            database = get(),
            timeManager = get(),
            mapper = get(),
        )
    }


    single<MessageRepository> {
        MessageRepositoryImpl(
            database = get(),
            mapper = get(),
            timeManager = get(),
        )
    }

    single<ScheduleRepository> {
        ScheduleRepositoryImpl(
            database = get(),
            mapper = get(),
        )
    }

    single<UserRepository> {
        UserRepositoryImpl(
            settings = getSettings(androidContext()),
        )
    }
    single<ScopeRepository> {
        ScopeRepositoryImpl(
            database = get(),
            dataMappers = get()
        )
    }

    singleOf(::PriorityRepositoryImpl) bind PriorityRepository::class

    // Services
    singleOf(::SchedulerImpl) bind Scheduler::class
    singleOf(::NotifierImpl) bind Notifier::class
    singleOf(::DataMigrationHelperImpl) bind DataMigrationHelper::class

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
            Constants.DATABASE_NAME,
            factory = RequerySQLiteOpenHelperFactory()
        ),
        taskAdapter = Task.Adapter(
            task_typeAdapter = EnumColumnAdapter()
        ),
        scheduleAdapter = Schedule.Adapter(
            schedule_typeAdapter = EnumColumnAdapter()
        ),
        messageAdapter = Message.Adapter(
            content_typeAdapter = IntColumnAdapter,
            reply_typeAdapter = IntColumnAdapter
        ),
        scopeAdapter = Scope.Adapter(
            scope_orderAdapter = IntColumnAdapter,
        )
    )
}