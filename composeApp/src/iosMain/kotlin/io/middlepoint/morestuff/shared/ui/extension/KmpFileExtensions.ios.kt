package io.middlepoint.morestuff.shared.ui.extension

import com.mohamedrejeb.calf.core.PlatformContext
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.io.getName

actual fun KmpFile.getFileName(context: PlatformContext): String? = getName(context)