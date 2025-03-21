package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.mohamedrejeb.calf.core.LocalPlatformContext
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.path
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.CFURLCreateWithFileSystemPath
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.CoreFoundation.kCFURLPOSIXPathStyle
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGBitmapContextCreateImage
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGColorSpaceRelease
import platform.CoreGraphics.CGContextDrawPDFPage
import platform.CoreGraphics.CGContextRelease
import platform.CoreGraphics.CGContextScaleCTM
import platform.CoreGraphics.CGContextTranslateCTM
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageRelease
import platform.CoreGraphics.CGPDFDocumentCreateWithURL
import platform.CoreGraphics.CGPDFDocumentGetPage
import platform.CoreGraphics.CGPDFDocumentIsUnlocked
import platform.CoreGraphics.CGPDFDocumentRelease
import platform.CoreGraphics.CGPDFPageGetBoxRect
import platform.CoreGraphics.kCGPDFMediaBox
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.stringByRemovingPercentEncoding
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.posix.memcpy


@Composable
actual fun PDFPagePreview(
  pdfFile: PlatformFile,
  width: Int,
  height: Int,
  scale: Float,
) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val platformContext = LocalPlatformContext.current

    LaunchedEffect(pdfFile) {
        val url = pdfFile.path

        val imageData = renderPage(url, width, height, scale)
        if (imageData != null) {
            val skiaImage = Image.makeFromEncoded(imageData)
            imageBitmap = skiaImage.toComposeImageBitmap()
        } else {
        }
    }

    Image(
        bitmap = imageBitmap ?: ImageBitmap(
            width,
            height
        ),
        contentDescription = "PDF preview",
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxSize()
            .background(Color.White),
        contentScale = ContentScale.Inside
    )
}


@OptIn(ExperimentalForeignApi::class)
private suspend fun renderPage(url: String, width: Int, height: Int, scale: Float): ByteArray? {
    return withContext(Dispatchers.Default) {

        val filePath = normalizePath(
            if (url.startsWith("file://")) {
                url.removePrefix("file://")
            } else {
                url
            }
        )

        val fileManager = platform.Foundation.NSFileManager.defaultManager
        val fileExists = fileManager.fileExistsAtPath(filePath)
        if (!fileExists) {
            return@withContext null
        }

        // Crear CFURL
        val cfpath = CFStringCreateWithCString(kCFAllocatorDefault, filePath, kCFStringEncodingUTF8)
        val cfurl =
            CFURLCreateWithFileSystemPath(kCFAllocatorDefault, cfpath, kCFURLPOSIXPathStyle, false)
                ?: return@withContext null

        val document = CGPDFDocumentCreateWithURL(cfurl)?.takeIf { CGPDFDocumentIsUnlocked(it) }
        if (document == null) {
            return@withContext null
        }

        val pageRef = CGPDFDocumentGetPage(document, 1u)
        if (pageRef == null) {
            CGPDFDocumentRelease(document)
            return@withContext null
        }

        val pageRect = CGPDFPageGetBoxRect(pageRef, kCGPDFMediaBox)
        val scale = width.toDouble() / pageRect.size
        val height = (pageRect.size * scale).toInt()

        val colorSpace = CGColorSpaceCreateDeviceRGB()
        val context = CGBitmapContextCreate(
            null,
            width.toULong(),
            height.toULong(),
            8u,
            4u * width.toULong(),
            colorSpace,
            CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value
        )

        if (context == null) {
            CGColorSpaceRelease(colorSpace)
            CGPDFDocumentRelease(document)
            return@withContext null
        }

        val renderScaleFactor = 0.1
        val yOffset = -height.toDouble() * 0.2
        CGContextTranslateCTM(context, 0.0, yOffset)
        CGContextScaleCTM(context, scale * renderScaleFactor, scale * renderScaleFactor)
        CGContextDrawPDFPage(context, pageRef)

        val image = CGBitmapContextCreateImage(context)
        if (image == null) {
            CGContextRelease(context)
            CGColorSpaceRelease(colorSpace)
            CGPDFDocumentRelease(document)
            return@withContext null
        }

        val uiImage = UIImage.imageWithCGImage(image)
        val thumbnailData = UIImagePNGRepresentation(uiImage)

        val byteArray = thumbnailData?.toByteArray()

        CGColorSpaceRelease(colorSpace)
        CGContextRelease(context)
        CGImageRelease(image)
        CGPDFDocumentRelease(document)

        return@withContext byteArray
    }
}


@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val data = this
    val bytes = ByteArray(data.length.toInt())
    memcpy(bytes.refTo(0), data.bytes, data.length)
    return bytes
}

@OptIn(BetaInteropApi::class)
fun normalizePath(path: String): String {
    val nsString = NSString.create(string = path)
    return nsString.stringByRemovingPercentEncoding() ?: path
}
