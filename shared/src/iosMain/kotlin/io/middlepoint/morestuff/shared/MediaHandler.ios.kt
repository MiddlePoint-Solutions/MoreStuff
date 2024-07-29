package io.middlepoint.morestuff.shared

import co.touchlab.kermit.Logger
import com.mohamedrejeb.calf.core.PlatformContext
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.io.getPath
import com.mohamedrejeb.calf.io.readByteArray
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.cinterop.toCValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.URLByAppendingPathComponent
import platform.Foundation.create
import platform.Foundation.writeToURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.posix.malloc
import platform.posix.memcpy
import platform.posix.size_t

class MediaHandlerImpl(
  private val logger: Logger
) : MediaHandler {

  override suspend fun saveMedia(media: KmpFile, time: LocalDateTime): String =
    withContext(Dispatchers.Main) {
      media.readByteArray(PlatformContext.INSTANCE).let {
        val path = media.getPath(PlatformContext.INSTANCE) ?: ""
        writeToFile(it, path, createImageFile(time))
      }
    }

  @OptIn(ExperimentalForeignApi::class)
  private fun writeToFile(
    byteArray: ByteArray,
    url: String,
    title: String,
  ): String {
    val extension = url.substringAfterLast('.', "")
    val fileName = "$title.$extension"
    val folderName = "images"
    val documentsDirectory = NSSearchPathForDirectoriesInDomains(
      NSDocumentDirectory,
      NSUserDomainMask,
      true
    ).firstOrNull()
    logger.d { "Documents directory: $documentsDirectory" }
    if (documentsDirectory != null) {
      val customFolderPath = "$documentsDirectory/$folderName"
      val customFolderURL = NSURL.fileURLWithPath(customFolderPath)

      if (!NSFileManager.defaultManager.fileExistsAtPath(customFolderPath)) {
        logger.d { "creating directory: $customFolderPath" }
        NSFileManager.defaultManager.createDirectoryAtPath(
          customFolderPath,
          true,
          null,
          null
        )
      }

      val fileURL = customFolderURL.URLByAppendingPathComponent(fileName)

      if (fileURL != null) {
        byteArray.toNSData().writeToURL(fileURL, true)
        return fileURL.path ?: ""
      }
    }
    logger.e { "creating directory: $documentsDirectory" }
    return ""
  }

  private fun createImageFile(time: LocalDateTime): String {
    val timeStamp =
      "${time.year}${time.monthNumber}${time.dayOfMonth}_${time.hour}${time.minute}${time.second}"
    return "img_$timeStamp"
  }

  override fun shareImage(imagePath: String) {
    val fileUrl = NSURL.fileURLWithPath(imagePath)
    val activityViewController =
      UIActivityViewController(activityItems = listOf(fileUrl), applicationActivities = null)

    UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
      activityViewController,
      true,
      null
    )
  }

  @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
  private fun ByteArray.toNSData(): NSData {
    val data = malloc(this.size.convert<size_t>())
    memcpy(data, this.toCValues(), this.size.convert<size_t>())
    return NSData.create(bytes = data, length = this.size.toULong())
  }
}
