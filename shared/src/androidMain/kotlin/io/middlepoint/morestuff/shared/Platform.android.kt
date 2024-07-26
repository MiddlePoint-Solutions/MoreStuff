package io.middlepoint.morestuff.shared

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.Locale


actual typealias File = java.io.File

actual fun generateUUID(): String = java.util.UUID.randomUUID().toString()

actual fun formatString(format: String, vararg args: Any): String {
  return String.format(Locale.US, format, *args)
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
actual fun requiresNotificationsPermission(): Boolean =
  Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

actual val sharedModule: Module
  get() = module {
    factoryOf(::TimeFormatterImpl) bind TimeFormatter::class
    factoryOf(::ClipboardHelperImpl) bind ClipboardHelper::class
    factoryOf(::ShareHelperImpl) bind ShareHelper::class
    factoryOf(::DataMigrationHelperImpl) bind DataMigrationHelper::class
    factoryOf(::ImageHandlerImpl) bind ImageHandler::class
    factoryOf(::PDFHandlerImpl) bind PDFHandler::class
  }
actual val platform: Platform
  get() = Platform.Android