package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.mohamedrejeb.calf.core.LocalPlatformContext
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.io.getPath
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image
import platform.CoreGraphics.CGContextRestoreGState
import platform.CoreGraphics.CGContextSaveGState
import platform.CoreGraphics.CGContextScaleCTM
import platform.CoreGraphics.CGContextTranslateCTM
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.PDFKit.PDFDocument
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

@Composable
actual fun PDFPagePreview(
  pdfFile: KmpFile,
  width: Int,
  height: Int,
  scale: Float
) {

  var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
  val platformContext = LocalPlatformContext.current

  LaunchedEffect(pdfFile) {
    val url = pdfFile.getPath(platformContext) ?: ""
    val imageData = renderPage(url, width, height, scale)
    imageData?.let {
      val skiaImage = Image.makeFromEncoded(it)
      imageBitmap = skiaImage.toComposeImageBitmap()
    }
  }


}

@OptIn(ExperimentalForeignApi::class)
private suspend fun renderPage(url: String, width: Int, height: Int, scale: Float): ByteArray? {
  return withContext(Dispatchers.Default) {
    val nsUrl = NSURL.fileURLWithPath(url)
    val document = PDFDocument(nsUrl)
    val page = document.pageAtIndex(0u) ?: return@withContext null

    val pdfScale = scale * platform.UIKit.UIScreen.mainScreen.scale
    val pdfSize = CGSizeMake(width.toDouble() * pdfScale, height.toDouble() * pdfScale)

    // Begin image context
    UIGraphicsBeginImageContextWithOptions(pdfSize, true, 0.0)
    val context = UIGraphicsGetCurrentContext() ?: return@withContext null

    // Save the graphics state
    CGContextSaveGState(context)

    // Transformations
    CGContextScaleCTM(context, pdfScale, pdfScale)
    CGContextTranslateCTM(context, 0.0, height.toDouble())
    CGContextScaleCTM(context, 1.0, -1.0)

    // Get the CGRect for the page and draw it in the context
    val displayBoxMediaBox = 0L
    page.drawWithBox(displayBoxMediaBox, context)

    // Restore the graphics state
    CGContextRestoreGState(context)

    // Get the image from the current context
    val image = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()

    val imageData = image?.let { UIImageJPEGRepresentation(it, 1.0) as NSData }
    return@withContext imageData?.toByteArray()
  }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
  val data = this
  val bytes = ByteArray(data.length.toInt())
  memcpy(bytes.refTo(0), data.bytes, data.length)
  return bytes
}