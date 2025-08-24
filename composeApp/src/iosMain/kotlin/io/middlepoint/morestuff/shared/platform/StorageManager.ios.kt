package io.middlepoint.morestuff.shared.platform

import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

class StorageManagerImpl : StorageManager {

  private val appStoragePath: String = NSSearchPathForDirectoriesInDomains(
    NSDocumentDirectory,
    NSUserDomainMask,
    true
  ).first() as String

  override fun getAppStorageDirectory(folder: MediaFolder): String =
    "$appStoragePath/${folder.folderName}"

  override fun getAppStoragePathToSavedFile(folder: MediaFolder, savedPath: String): String {
    val filename = getFileName(savedPath, true)
    return "${getAppStorageDirectory(folder)}/$filename"
  }

}