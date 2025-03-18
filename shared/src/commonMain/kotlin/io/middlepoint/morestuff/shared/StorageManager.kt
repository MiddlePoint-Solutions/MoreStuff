package io.middlepoint.morestuff.shared

interface StorageManager {

  fun getAppStorageDirectory(folder: MediaFolder): String
  fun getAppStoragePathToSavedFile(folder: MediaFolder, savedPath: String): String

  fun getFileName(path: String, withExtension: Boolean = false): String =
    path.substringAfterLast('/')
      .apply {
        if (withExtension) {
          substringAfterLast('.')
        }
      }

}