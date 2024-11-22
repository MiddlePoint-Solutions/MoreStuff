package io.middlepoint.morestuff.shared.ui.extension

import com.mohamedrejeb.calf.core.PlatformContext
import com.mohamedrejeb.calf.io.KmpFile

expect fun KmpFile.getFileName(context: PlatformContext): String?