package io.middlepoint.morestuff.shared.platform

class StorageManagerImpl : StorageManager {

  override fun getAppStorageDirectory(folder: MediaFolder): String {
    TODO("Not yet implemented")
  }

  override fun getAppStoragePathToSavedFile(folder: MediaFolder, savedPath: String): String {
    // TODO: For now return the saved path, This might change depending on if we change the storage option.
    return savedPath
  }

}
