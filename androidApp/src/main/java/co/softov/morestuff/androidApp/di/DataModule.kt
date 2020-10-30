package co.softov.morestuff.androidApp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import co.softov.morestuff.androidApp.Constants
import co.softov.morestuff.androidApp.app.Debugger
import co.softov.morestuff.androidApp.app.SchedulerImpl
import co.softov.morestuff.androidApp.data.mapper.makeMessageDbMapper
import co.softov.morestuff.androidApp.data.mapper.makeScheduleDbMapper
import co.softov.morestuff.androidApp.data.mapper.makeScheduleWithTitleDbMapper
import co.softov.morestuff.androidApp.data.mapper.makeTaskDbMapper
import co.softov.morestuff.androidApp.data.repository.MessageRepositoryImpl
import co.softov.morestuff.androidApp.data.repository.PreferenceRepositoryImpl
import co.softov.morestuff.androidApp.data.repository.ScheduleRepositoryImpl
import co.softov.morestuff.androidApp.data.repository.TaskRepositoryImpl
import co.softov.morestuff.androidApp.data.service.NotifierImpl
import co.softov.morestuff.androidApp.data.service.TimeManagerImpl
import co.softov.morestuff.androidApp.domain.Debug
import co.softov.morestuff.androidApp.domain.Scheduler
import co.softov.morestuff.androidApp.domain.repository.MessageRepository
import co.softov.morestuff.androidApp.domain.repository.PreferenceRepository
import co.softov.morestuff.androidApp.domain.repository.ScheduleRepository
import co.softov.morestuff.androidApp.domain.repository.TaskRepository
import co.softov.morestuff.androidApp.domain.service.Notifier
import co.softov.morestuff.androidApp.domain.service.TimeManager
import co.softov.morestuff.androidApp.domain.usecase.message.GetPagedMessages
import co.softov.morestuff.androidApp.domain.usecase.message.GetPagedMessagesImpl
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinApiExtension
import org.koin.dsl.module

@OptIn(KoinApiExtension::class)
val dataModule = module {

    // Debugging
    single<Debug>(createdAtStart = true) { Debugger(androidApplication()) }

    // Database
    single { createDatabase(androidApplication()) }

    // Repositories
    single<TaskRepository> {
        TaskRepositoryImpl(
            database = get(),
            mapTaskDb = makeTaskDbMapper()
        )
    }

    single<MessageRepository> {
        MessageRepositoryImpl(
            database = get(),
            mapMessageDb = makeMessageDbMapper()
        )
    }

    single<ScheduleRepository> {
        ScheduleRepositoryImpl(
            database = get(),
            mapScheduleDb = makeScheduleDbMapper(),
            mapScheduleWithTitleDb = makeScheduleWithTitleDbMapper(),
            mapLaterScheduleWithTitleDb = makeScheduleWithTitleDbMapper(),
            mapTimeScheduleWithTitleDb = makeScheduleWithTitleDbMapper()
        )
    }

    single<PreferenceRepository> {
        PreferenceRepositoryImpl(
            getSharedPreferences(
                androidContext()
            )
        )
    }

    // Services
    single<Scheduler> { SchedulerImpl(androidContext()) }
    single<Notifier> { NotifierImpl(androidContext()) }
    factory<TimeManager> { TimeManagerImpl(debug = get()) }

    // Platform specific use-cases
    factory<GetPagedMessages> {
        GetPagedMessagesImpl(
            database = get(),
            mapMessageDb = makeMessageDbMapper()
        )
    }

}

internal fun getSharedPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
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