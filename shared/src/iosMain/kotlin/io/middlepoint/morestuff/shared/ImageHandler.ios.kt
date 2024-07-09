package io.middlepoint.morestuff.shared

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import platform.Foundation.NSData
import platform.Foundation.NSLog
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.writeToURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

class ImageHandlerImpl(
  private val logger: Logger
) : ImageHandler {

  override suspend fun saveImages(uris: String, time: LocalDateTime): String? =
    withContext(Dispatchers.Main) {
      logger.d { "uris: $uris" }
      val savedImagePath = try {
        val url = NSURL.URLWithString(uris)
        val data = url?.let { NSData.dataWithContentsOfURL(it) }
        val image = data?.let { UIImage(data = data) }

        val imageFilePath = createImageFile(time)
        val fileUrl = NSURL.fileURLWithPath(imageFilePath)
        image?.let {
          val jpegData = UIImageJPEGRepresentation(it, 0.5)
          jpegData?.writeToURL(fileUrl, true)
        }

        imageFilePath
      } catch (e: Exception) {
        NSLog("Error handling image: $e")
        ""
      }

      savedImagePath
    }

  private fun createImageFile(time: kotlinx.datetime.LocalDateTime): String {
    val timeStamp =
      "${time.year}${time.monthNumber}${time.dayOfMonth}_${time.hour}${time.minute}${time.second}"
    val imageFileName = "JPEG_$timeStamp.jpg"
    val tempDir = NSTemporaryDirectory()
    return tempDir + (if (tempDir.endsWith("/")) "" else "/") + imageFileName
  }

  override fun shareImage(imagePath: String) {
    val fileUrl = NSURL.fileURLWithPath(imagePath)
    val activityViewController =
      UIActivityViewController(activityItems = listOf(fileUrl), applicationActivities = null)

    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    if (rootViewController != null) {
      rootViewController.presentViewController(activityViewController, true, null)
    }
  }
}
