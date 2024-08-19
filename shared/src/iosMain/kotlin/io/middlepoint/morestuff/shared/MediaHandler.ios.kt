package io.middlepoint.morestuff.shared

import co.touchlab.kermit.Logger
import com.mohamedrejeb.calf.core.PlatformContext
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.io.getPath
import com.mohamedrejeb.calf.io.readByteArray
import io.middlepoint.morestuff.shared.MediaFolder.Files
import io.middlepoint.morestuff.shared.MediaFolder.Images
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.cinterop.toCValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSLog
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.URLByAppendingPathComponent
import platform.Foundation.create
import platform.Foundation.writeToURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentInteractionController
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
        writeToFile(it, path, Images, generateFileName(time))
      }
    }

  override suspend fun savePDF(media: KmpFile): String = withContext(Dispatchers.Main) {
    media.readByteArray(PlatformContext.INSTANCE).let {
      val path = media.getPath(PlatformContext.INSTANCE) ?: ""
      writeToFile(it, path, Files, getFileName(path))
    }
  }

  // TODO: paths should already contain proper path (sandboxed)
  override fun shareImage(imagePath: String) {
    val fileUrl = getLocalFileSandboxUrl(imagePath, Images)

    val activityViewController =
      UIActivityViewController(activityItems = listOf(fileUrl), applicationActivities = null)

    UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
      activityViewController,
      true,
      null
    )
  }

  private fun getLocalFileSandboxUrl(path: String, folder: MediaFolder): NSURL {
    val filename = getFileName(path, true)
    val directory = NSSearchPathForDirectoriesInDomains(
      NSDocumentDirectory,
      NSUserDomainMask,
      true
    ).first() as String
    return NSURL.fileURLWithPath("$directory/${folder.folderName}/$filename")
  }

  @OptIn(ExperimentalForeignApi::class)
  override fun sharePDF(path: String) {
    val fileUrl = getLocalFileSandboxUrl(path, Files)
    val documentController =
      UIDocumentInteractionController.interactionControllerWithURL(fileUrl)
    documentController.UTI = "com.adobe.pdf"

    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    if (rootViewController != null) {
      val rect = CGRectMake(0.0, 0.0, 0.0, 0.0)
      documentController.presentOptionsMenuFromRect(rect, rootViewController.view, true)
    }
  }

  override fun openPDF(path: String) {
    val pdfFile = getLocalFileSandboxUrl(path, Files)
    val documentController = UIDocumentInteractionController.interactionControllerWithURL(pdfFile)
    documentController.UTI = "com.adobe.pdf"

    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    if (rootViewController != null) {
      documentController.presentPreviewAnimated(true)
    } else {
      NSLog("PDF file not found or cannot be opened")
    }
  }

  private fun writeToFile(
    byteArray: ByteArray,
    url: String,
    folder: MediaFolder,
    title: String,
  ): String {
    val extension = url.substringAfterLast('.', "")
    val fileName = "$title.$extension"
    val documentsDirectory = getDocumentsDirectory()
    logger.d { "Documents directory: $documentsDirectory" }
    if (documentsDirectory != null) {
      val customFolderPath = createDirectoryIfNeeded(documentsDirectory, folder)
      val customFolderURL = NSURL.fileURLWithPath(customFolderPath)
      val fileURL = customFolderURL.URLByAppendingPathComponent(fileName)
      if (fileURL != null) {
        byteArray.toNSData().writeToURL(fileURL, true)
        return fileURL.path ?: ""
      }
    }
    logger.e { "creating directory: $documentsDirectory" }
    return ""
  }

  @OptIn(ExperimentalForeignApi::class)
  private fun getDocumentsDirectory() = NSFileManager.defaultManager.URLForDirectory(
    NSDocumentDirectory,
    NSUserDomainMask,
    null,
    false,
    null
  )

  @OptIn(ExperimentalForeignApi::class)
  private fun createDirectoryIfNeeded(
    documentsDirectory: NSURL,
    folder: MediaFolder
  ): String {
    val customFolderPath = "${documentsDirectory.path}/${folder.folderName}"
    if (!NSFileManager.defaultManager.fileExistsAtPath(customFolderPath)) {
      logger.d { "creating directory: $customFolderPath" }
      NSFileManager.defaultManager.createDirectoryAtPath(
        customFolderPath,
        true,
        null,
        null
      )
    }
    return customFolderPath
  }

  @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
  private fun ByteArray.toNSData(): NSData {
    val data = malloc(this.size.convert<size_t>())
    memcpy(data, this.toCValues(), this.size.convert<size_t>())
    return NSData.create(bytes = data, length = this.size.toULong())
  }

  private fun generateFileName(time: LocalDateTime): String =
    "${time.year}${time.monthNumber}${time.dayOfMonth}_${time.hour}${time.minute}${time.second}"

  private fun getFileName(path: String, withExtension: Boolean = false): String =
    path.substringAfterLast('/')
      .apply {
        if (withExtension) {
          substringAfterLast('.')
        }
      }

}
