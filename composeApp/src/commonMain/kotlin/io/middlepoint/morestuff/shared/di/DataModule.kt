package io.middlepoint.morestuff.shared.di

import MoreStuff.composeApp.BuildConfig
import io.github.jan.supabase.annotations.SupabaseInternal
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
import io.middlepoint.morestuff.shared.data.repository.MessageRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.OpenAIRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.PriorityRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.ScheduleRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.ScopeRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.TaskRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.TimeFormatterImpl
import io.middlepoint.morestuff.shared.data.repository.UserRepositoryImpl
import io.middlepoint.morestuff.shared.data.service.DevToolsImpl
import io.middlepoint.morestuff.shared.data.sync.DataSyncManager
import io.middlepoint.morestuff.shared.data.sync.DataSyncManagerImpl
import io.middlepoint.morestuff.shared.data.utils.MigrationHelper
import io.middlepoint.morestuff.shared.domain.DevTools
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
import io.middlepoint.morestuff.shared.domain.repository.OpenAIRepository
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

  singleOf(::DevToolsImpl) bind DevTools::class
  singleOf(::DataMappersImpl) bind DataMappers::class
  singleOf(::MessageDataMap)

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

  singleOf(::ScheduleRepositoryImpl) bind ScheduleRepository::class

  single<UserRepository> {
    UserRepositoryImpl(
      settings = get(named(SharedSettings.Encrypted))
    )
  }

  singleOf(::ScopeRepositoryImpl) bind ScopeRepository::class
  singleOf(::PriorityRepositoryImpl) bind PriorityRepository::class
  singleOf(::TimeFormatterImpl) bind TimeFormatter::class
  singleOf(::OpenAIRepositoryImpl) bind OpenAIRepository::class

  factoryOf(::MigrationHelper)

  singleOf(::DataSyncManagerImpl) bind DataSyncManager::class

}

val supabaseModule = module {
  single {
    createSupabaseClient(
      supabaseUrl = BuildConfig.SUPABASE_URL,
      supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
      if(BuildConfig.DEBUG) {
        defaultLogLevel = LogLevel.DEBUG
      }
      install(Auth) {
        sessionManager = SettingsSessionManager(settings = get()) // TODO: use encrypted settings
        codeVerifierCache = SettingsCodeVerifierCache(settings = get()) // TODO: use encrypted settings
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

