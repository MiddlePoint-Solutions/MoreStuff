package io.middlepoint.morestuff.shared.data.service

import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.exists
import io.middlepoint.morestuff.shared.MoreStuffApp

actual fun getMigrationDataFile(): PlatformFile? =
  PlatformFile("/storage/emulated/0/Android/data/${MoreStuffApp.INSTANCE.packageName}/files/files/migration_json.json").let {
    if (it.exists()) {
      it
    } else {
      null
    }
  }
