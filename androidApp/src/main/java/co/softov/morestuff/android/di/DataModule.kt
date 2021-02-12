package co.softov.morestuff.android.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import co.softov.morestuff.android.data.Constants
import co.softov.morestuff.android.app.Debugger
import co.softov.morestuff.android.app.SchedulerImpl
import co.softov.morestuff.android.data.mapper.makeMessageDbMapper
import co.softov.morestuff.android.data.mapper.makeScheduleDbMapper
import co.softov.morestuff.android.data.mapper.makeScheduleWithTitleDbMapper
import co.softov.morestuff.android.data.mapper.makeTaskDbMapper
import co.softov.morestuff.android.data.repository.*
import co.softov.morestuff.android.data.service.NotifierImpl
import co.softov.morestuff.android.data.service.TimeManagerImpl
import co.softov.morestuff.android.domain.Debug
import co.softov.morestuff.android.domain.repository.*
import co.softov.morestuff.android.domain.service.Scheduler
import co.softov.morestuff.android.domain.service.Notifier
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.message.GetPagedMessages
import co.softov.morestuff.android.domain.usecase.message.GetPagedMessagesImpl
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
            mapScheduleWithTitleDb = makeScheduleWithTitleDbMapper()
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
            prefs = getSharedPreferences(androidContext())
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