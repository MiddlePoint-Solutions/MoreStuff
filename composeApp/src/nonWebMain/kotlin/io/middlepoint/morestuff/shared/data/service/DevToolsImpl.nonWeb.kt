package io.middlepoint.morestuff.shared.data.service

import io.github.vinceglb.filekit.delete

actual suspend fun deleteMigrationDataFile() {
  getMigrationDataFile()?.delete(false)
}