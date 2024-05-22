package io.middlepoint.morestuff.shared.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.middlepoint.morestuff.android.data.Constants
import io.middlepoint.morestuff.db.Message
import io.middlepoint.morestuff.db.Schedule
import io.middlepoint.morestuff.db.Scope
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.db.Task
import io.middlepoint.morestuff.shared.data.ClipboardHelperImpl
import io.middlepoint.morestuff.shared.data.DataMigrationHelperImpl
import io.middlepoint.morestuff.shared.data.ImageHandlerImpl
import io.middlepoint.morestuff.shared.data.PDFHandlerImpl
import io.middlepoint.morestuff.shared.data.ShareTaskMessageImpl
import io.middlepoint.morestuff.shared.data.TimeFormatterImpl
import io.middlepoint.morestuff.shared.data.VoiceToTextParserImpl
import io.middlepoint.morestuff.shared.domain.service.ClipboardHelper
import io.middlepoint.morestuff.shared.domain.service.DataMigrationHelper
import io.middlepoint.morestuff.shared.domain.service.ImageHandler
import io.middlepoint.morestuff.shared.domain.service.PDFHandler
import io.middlepoint.morestuff.shared.domain.service.ShareTaskMessage
import io.middlepoint.morestuff.shared.domain.service.VoiceToTextParser
import io.middlepoint.morestuff.shared.domain.util.TimeFormatter
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {

  factory { Logger.withTag(it.getOrNull() ?: "MoreStuffAndroid") }

  single<StuffDb> { createDatabase(androidContext()) }

  factoryOf(::VoiceToTextParserImpl) bind VoiceToTextParser::class
  factoryOf(::ShareTaskMessageImpl) bind ShareTaskMessage::class
  singleOf(::DataMigrationHelperImpl) bind DataMigrationHelper::class
  factoryOf(::ClipboardHelperImpl) bind ClipboardHelper::class

  single { WorkManager.getInstance(androidApplication()) }
  single { NotificationManagerCompat.from(androidApplication()) }
  single<TimeFormatter> { TimeFormatterImpl(androidApplication()) }
  single<ImageHandler> { ImageHandlerImpl(timeManager = get(), context = androidApplication()) }
  single<PDFHandler> { PDFHandlerImpl(context = androidApplication()) }

  single<Settings> {
    SharedPreferencesSettings(
      PreferenceManager.getDefaultSharedPreferences(androidApplication())
    )
  }
}

fun createDatabase(context: Context): StuffDb {
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