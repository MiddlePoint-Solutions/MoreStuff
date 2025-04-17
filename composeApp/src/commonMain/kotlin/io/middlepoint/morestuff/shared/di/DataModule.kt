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
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.data.createDatabase
import io.middlepoint.morestuff.shared.data.mapper.DataMappers
import io.middlepoint.morestuff.shared.data.mapper.DataMappersImpl
import io.middlepoint.morestuff.shared.data.mapper.MessageDataMap
import io.middlepoint.morestuff.shared.data.repository.MessageRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.PriorityRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.ScheduleRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.ScopeRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.TaskRepositoryImpl
import io.middlepoint.morestuff.shared.data.repository.TimeFormatterImpl
import io.middlepoint.morestuff.shared.data.repository.UserRepositoryImpl
import io.middlepoint.morestuff.shared.data.service.DevToolsImpl
import io.middlepoint.morestuff.shared.data.utils.MigrationHelper
import io.middlepoint.morestuff.shared.domain.DevTools
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
import io.middlepoint.morestuff.shared.domain.repository.PriorityRepository
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import io.middlepoint.morestuff.shared.domain.repository.UserRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {

  single<StuffDb> { createDatabase(get()) }

  singleOf(::DevToolsImpl) bind DevTools::class
  singleOf(::DataMappersImpl) bind DataMappers::class
  singleOf(::MessageDataMap)

  // Repositories
  single<TaskRepository> {
    TaskRepositoryImpl(
      database = get(),\
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
    UserRepositoryImpl(settings = get())
  }

  single<ScopeRepository> {
    ScopeRepositoryImpl(
      database = get(),
      dataMappers = get()
    )
  }

  singleOf(::PriorityRepositoryImpl) bind PriorityRepository::class
  singleOf(::TimeFormatterImpl) bind TimeFormatter::class

  factoryOf(::MigrationHelper)

}

@OptIn(SupabaseInternal::class)
val supabaseModule = module {
  single {
    createSupabaseClient(
      supabaseUrl = BuildConfig.SUPABASE_URL,
      supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
      install(Auth) {
        sessionManager = SettingsSessionManager(settings = get()) // TODO: use encrypted settings
        codeVerifierCache = SettingsCodeVerifierCache(settings = get()) // TODO: use encrypted settings
      }
      install(ComposeAuth) {
        googleNativeLogin(serverClientId = BuildConfig.GOOGLE_SERVER_CLIENT_ID)
        appleNativeLogin() // TODO
      }

    }
  }
}

