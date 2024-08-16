package io.middlepoint.morestuff.shared

import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val sharedModule: Module
  get() = module {
      factoryOf(::TimeFormatterImpl) bind TimeFormatter::class
      factoryOf(::ClipboardHelperImpl) bind ClipboardHelper::class
      factoryOf(::ShareHelperImpl) bind ShareHelper::class
      factoryOf(::DataMigrationHelperImpl) bind DataMigrationHelper::class
      factoryOf(::MediaHandlerImpl) bind MediaHandler::class
  }