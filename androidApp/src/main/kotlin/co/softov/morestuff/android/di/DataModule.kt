package co.softov.morestuff.android.di

import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import co.softov.morestuff.android.app.service.NotifierImpl
import co.softov.morestuff.android.app.service.SchedulerImpl
import co.softov.morestuff.android.data.Constants
import co.softov.morestuff.android.data.mapper.DataMappers
import co.softov.morestuff.android.data.mapper.DataMappersImpl
import co.softov.morestuff.android.data.repository.MessageRepositoryImpl
import co.softov.morestuff.android.data.repository.PriorityRepositoryImpl
import co.softov.morestuff.android.data.repository.ScheduleRepositoryImpl
import co.softov.morestuff.android.data.repository.ScopeRepositoryImpl
import co.softov.morestuff.android.data.repository.TaskRepositoryImpl
import co.softov.morestuff.android.data.repository.UserRepositoryImpl
import co.softov.morestuff.android.data.service.DataMigrationHelperImpl
import co.softov.morestuff.android.data.service.DevToolsImpl
import co.softov.morestuff.android.data.service.ImageHandlerImpl
import co.softov.morestuff.android.data.service.PDFHandlerImpl
import co.softov.morestuff.android.data.utils.TimeFormatterImpl
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.domain.repository.PriorityRepository
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.repository.ScopeRepository
import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.android.domain.repository.UserRepository
import co.softov.morestuff.android.domain.service.DataMigrationHelper
import co.softov.morestuff.android.domain.service.ImageHandler
import co.softov.morestuff.android.domain.service.Notifier
import co.softov.morestuff.android.domain.service.PDFHandler
import co.softov.morestuff.android.domain.service.Scheduler
import co.softov.morestuff.android.domain.usecase.message.GetPagedMessagesUseCase
import co.softov.morestuff.android.domain.usecase.message.GetPagedMessagesUseCaseImpl
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.db.Message
import co.softov.morestuff.db.Schedule
import co.softov.morestuff.db.Scope
import co.softov.morestuff.db.StuffDb
import co.softov.morestuff.db.Task
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