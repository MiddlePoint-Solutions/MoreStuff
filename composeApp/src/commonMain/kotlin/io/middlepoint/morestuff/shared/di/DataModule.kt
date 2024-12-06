package io.middlepoint.morestuff.shared.di

import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.data.DriverFactory
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
import io.middlepoint.morestuff.shared.domain.DevTools
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
import io.middlepoint.morestuff.shared.domain.repository.PriorityRepository
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import io.middlepoint.morestuff.shared.domain.repository.UserRepository
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

}

