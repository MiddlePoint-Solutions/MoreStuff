package io.middlepoint.morestuff.shared

import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val sharedModule: Module
  get() = module {
    singleOf(::StorageManagerImpl) bind StorageManager::class
    singleOf(::TimeUtilsImpl) bind TimeUtils::class
    factoryOf(::ClipboardHelperImpl) bind ClipboardHelper::class
    factoryOf(::ShareHelperImpl) bind ShareHelper::class
    factoryOf(::DataMigrationHelperImpl) bind DataMigrationHelper::class
    factoryOf(::MediaHandlerImpl) bind MediaHandler::class
    factoryOf(::AndroidAppSettingsHandlerImpl) bind AppSettingsHandler::class

  }