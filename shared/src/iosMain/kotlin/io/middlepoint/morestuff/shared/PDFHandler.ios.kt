package io.middlepoint.morestuff.shared

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSLog
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.lastPathComponent
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentInteractionController

class PDFHandlerImpl : PDFHandler {

  override suspend fun savePDF(uri: String): String? = withContext(Dispatchers.Default) {
    try {
      val originalFileName = getOriginalFileName(uri) ?: "PDF_Default.pdf"
      val pdfFilePath = createPDFFile(originalFileName)

      val fileManager = NSFileManager.defaultManager
      val url = NSURL.URLWithString(uri)

      if (url != null) {
        val data = NSData.dataWithContentsOfURL(url)
        fileManager.createFileAtPath(pdfFilePath, data, null)
        pdfFilePath
      } else {
        null
      }
    } catch (e: Exception) {
      NSLog("Error handling PDF: $e")
      null
    }
  }

  private fun createPDFFile(originalFileName: String): String {
    val pdfFileName =
      if (originalFileName.endsWith(".pdf")) originalFileName else "$originalFileName.pdf"
    val tempDir = NSTemporaryDirectory()
    return tempDir + (if (tempDir.endsWith("/")) "" else "/") + pdfFileName
  }

  @OptIn(ExperimentalForeignApi::class)
  override fun sharePDF(pdfPath: String) {
    val fileURL = NSURL.fileURLWithPath(pdfPath)
    val documentController = UIDocumentInteractionController.interactionControllerWithURL(fileURL)
    documentController.UTI = "com.adobe.pdf"

    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    if (rootViewController != null) {
      val rect = CGRectMake(0.0, 0.0, 0.0, 0.0)
      documentController.presentOptionsMenuFromRect(rect, rootViewController.view, true)
    }
  }

  private fun getOriginalFileName(uri: String): String? {
    val url = NSURL.URLWithString(uri)
    return url?.lastPathComponent
  }

  override fun openPDF(pdfPath: String) {
    val pdfFile = NSURL.fileURLWithPath(pdfPath)
    val documentController = UIDocumentInteractionController.interactionControllerWithURL(pdfFile)
    documentController.UTI = "com.adobe.pdf"

    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    if (rootViewController != null) {
      documentController.presentPreviewAnimated(true)
    } else {
      NSLog("PDF file not found or cannot be opened")
    }
  }
}
