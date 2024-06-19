package io.middlepoint.morestuff.shared

import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual class File actual constructor(path: String) {
    actual fun exists(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun delete(): Boolean {
        TODO("Not yet implemented")
    }
}

actual fun generateUUID(): String {
    TODO("Not yet implemented")
}

actual fun formatString(format: String, vararg args: Any): String {
    TODO("Not yet implemented")
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
        factoryOf(::ImageHandlerImpl) bind ImageHandler::class
        factoryOf(::PDFHandlerImpl) bind PDFHandler::class
    }