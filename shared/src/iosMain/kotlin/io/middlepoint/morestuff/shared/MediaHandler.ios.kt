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
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentInteractionController
import platform.UIKit.UIDocumentInteractionControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject
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

  @OptIn(ExperimentalForeignApi::class)
  override fun sharePDF(path: String) {
    val fileUrl = NSURL.fileURLWithPath(path)
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
    val pdfFile = NSURL.fileURLWithPath(path)
    val documentController = UIDocumentInteractionController.interactionControllerWithURL(pdfFile)
    documentController.UTI = "com.adobe.pdf"

    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    if (rootViewController != null) {
      val delegate = DocumentInteractionControllerDelegate(rootViewController)
      documentController.delegate = delegate

      val success = documentController.presentPreviewAnimated(true)
      if (!success) {
        logger.e { "Failed to present PDF preview. Path: $path" }
      } else {
        logger.d { "PDF preview presented successfully. Path: $path" }
      }
    } else {
      logger.e { "Root view controller not found. Unable to open PDF. Path: $path" }
      NSLog("PDF file not found or cannot be opened. Path: $path")
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
    if (documentsDirectory != null) {
      val customFolderPath = createDirectoryIfNeeded(documentsDirectory, folder)
      val customFolderURL = NSURL.fileURLWithPath(customFolderPath)
      val fileURL = customFolderURL.URLByAppendingPathComponent(fileName)
      if (fileURL != null) {
        byteArray.toNSData().writeToURL(fileURL, true)
        return fileURL.path ?: ""
      }
    }
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




class DocumentInteractionControllerDelegate(
  private val viewController: UIViewController
) : NSObject(), UIDocumentInteractionControllerDelegateProtocol {

  override fun documentInteractionControllerViewControllerForPreview(controller: UIDocumentInteractionController): UIViewController {
    return viewController
  }
}
