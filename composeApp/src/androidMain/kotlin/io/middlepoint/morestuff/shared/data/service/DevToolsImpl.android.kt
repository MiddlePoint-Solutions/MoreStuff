package io.middlepoint.morestuff.shared.data.service

import io.github.vinceglb.filekit.PlatformFile

actual fun getMigrationDataFile(): PlatformFile? =
  PlatformFile("/storage/emulated/0/Android/data/io.middlepoint.morestuff.dev/files/files/migration_json.json")