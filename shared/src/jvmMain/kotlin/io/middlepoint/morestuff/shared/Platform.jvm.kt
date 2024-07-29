package io.middlepoint.morestuff.shared

import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.UUID

actual class File actual constructor(path: String) {
    actual fun exists(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun delete(): Boolean {
        TODO("Not yet implemented")
    }
}

actual fun generateUUID(): String {
    return UUID.randomUUID().toString()
}

actual fun formatString(format: String, vararg args: Any): String {
    return String.format(format, *args)
}

actual fun requiresNotificationsPermission(): Boolean {
    TODO("Not yet implemented")
}

actual val sharedModule: Module
    get() = module {
        factoryOf(::TimeFormatterImpl) bind TimeFormatter::class
        factoryOf(::ClipboardHelperImpl) bind ClipboardHelper::class
        factoryOf(::ShareHelperImpl) bind ShareHelper::class
        factoryOf(::DataMigrationHelperImpl) bind DataMigrationHelper::class
        factoryOf(::MediaHandlerImpl) bind MediaHandler::class
        factoryOf(::PDFHandlerImpl) bind PDFHandler::class
    }
actual val platform: Platform
  get() = TODO("Not yet implemented")