package io.middlepoint.morestuff.shared

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.net.toUri
import io.github.vinceglb.filekit.PlatformFile
import java.util.Locale

actual fun formatString(format: String, vararg args: Any): String {
  return String.format(Locale.US, format, *args)
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
actual fun requiresNotificationsPermission(): Boolean =
  Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

actual val platform: Platform
  get() = Platform.Android

actual fun createKmpFile(path: String): PlatformFile = PlatformFile(path.toUri())
