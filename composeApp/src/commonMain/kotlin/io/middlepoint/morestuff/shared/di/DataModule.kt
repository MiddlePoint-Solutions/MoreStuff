package io.middlepoint.morestuff.shared.di

import MoreStuff.composeApp.BuildConfig
import com.russhwolf.settings.Settings
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsCodeVerifierCache
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.appleNativeLogin
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.logging.LogLevel
import io.github.jan.supabase.postgrest.Postgrest
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.data.createDatabase
import io.middlepoint.morestuff.shared.data.mapper.DataMappers
import io.middlepoint.morestuff.shared.data.mapper.DataMappersImpl
import io.middlepoint.morestuff.shared.data.mapper.MessageDataMap
import io.middlepoint.morestuff.shared.data.repository.AuthRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.LlmRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.MessageRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.PriorityRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.ScheduleRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.ScopeRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.TaskRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.TimeFormatterImpl
import io.middlepoint.morestuff.shared.data.repository.UserRepositoryImpl
import io.middlepoint.morestuff.shared.data.service.DevToolsImpl
import io.middlepoint.morestuff.shared.domain.service.DataSyncManager
import io.middlepoint.morestuff.shared.data.sync.DataSyncManagerImpl
import io.middlepoint.morestuff.shared.data.utils.MigrationHelper
import io.middlepoint.morestuff.shared.domain.DevTools
import io.middlepoint.morestuff.shared.domain.repository.AuthRepository
import io.middlepoint.morestuff.shared.domain.repository.LlmRepository
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
import io.middlepoint.morestuff.shared.domain.repository.PriorityRepository
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import io.middlepoint.morestuff.shared.domain.repository.UserRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

enum class SharedSettings {
  Unencrypted,
  Encrypted
}

val dataModule = module {

  single<StuffDb> { createDatabase(get()) }

  single<DevTools> {
    DevToolsImpl(
      settings = get(named(SharedSettings.Unencrypted)),
      notifier = get(),
      dataMigration = get(),
      migrationHelper = get(),
      dataSyncManager = get(),
    )
  }

  singleOf(::MessageDataMap)

  //singleOf(::DevToolsImpl) bind DevTools::class
  singleOf(::DataMappersImpl) bind DataMappers::class
  singleOf(::TaskRepositoryImpl) bind TaskRepository::class
  singleOf(::MessageRepositoryImpl) bind MessageRepository::class
  singleOf(::ScheduleRepositoryImpl) bind ScheduleRepository::class
  singleOf(::AuthRepositoryImpl) bind AuthRepository::class

  single<UserRepository> {
    UserRepositoryImpl(
      settings = get(named(SharedSettings.Unencrypted))
    )
  }

  singleOf(::ScopeRepositoryImpl) bind ScopeRepository::class


  single<LlmRepository> {
    LlmRepositoryImpl(
      settings = get(named(SharedSettings.Encrypted)),
    )
  }

  singleOf(::PriorityRepositoryImpl) bind PriorityRepository::class
  singleOf(::TimeFormatterImpl) bind TimeFormatter::class

  factoryOf(::MigrationHelper)

  single<DataSyncManager> {
    DataSyncManagerImpl(
      database = get(),
      dataMappers = get(),
      supabase = get(),
      settings = get(named(SharedSettings.Encrypted)),
      timeManager = get(),
    )
  }

}

val supabaseModule = module {
  single {

    val settings = get<Settings>(named(SharedSettings.Encrypted))

    createSupabaseClient(
      supabaseUrl = BuildConfig.SUPABASE_URL,
      supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
      if (BuildConfig.DEBUG) {
        defaultLogLevel = LogLevel.DEBUG
      }
      install(Auth) {
        sessionManager = SettingsSessionManager(settings)
        codeVerifierCache = SettingsCodeVerifierCache(settings)
        host = BuildConfig.APP_HOST_LOGIN
        scheme = BuildConfig.APP_SCHEME
      }
      install(ComposeAuth) {
        googleNativeLogin(serverClientId = BuildConfig.GOOGLE_SERVER_CLIENT_ID)
        appleNativeLogin()
      }

      install(Postgrest)

    }
  }
}

