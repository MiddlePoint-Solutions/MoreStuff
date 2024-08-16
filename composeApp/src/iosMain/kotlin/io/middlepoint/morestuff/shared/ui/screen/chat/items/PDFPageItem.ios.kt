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
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
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
import platform.CoreGraphics.CGContextFillRect
import platform.CoreGraphics.CGContextRelease
import platform.CoreGraphics.CGContextScaleCTM
import platform.CoreGraphics.CGContextSetFillColorWithColor
import platform.CoreGraphics.CGContextTranslateCTM
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageRelease
import platform.CoreGraphics.CGPDFDocumentCreateWithURL
import platform.CoreGraphics.CGPDFDocumentGetPage
import platform.CoreGraphics.CGPDFDocumentIsUnlocked
import platform.CoreGraphics.CGPDFDocumentRelease
import platform.CoreGraphics.CGPDFPageGetBoxRect
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.kCGPDFMediaBox
import platform.Foundation.NSData
import platform.UIKit.UIColor
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
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
        val cfpath = CFStringCreateWithCString(kCFAllocatorDefault, url, kCFStringEncodingUTF8)
        val cfurl = CFURLCreateWithFileSystemPath(kCFAllocatorDefault, cfpath, kCFURLPOSIXPathStyle, false)

        val document = CGPDFDocumentCreateWithURL(cfurl)?.takeIf { CGPDFDocumentIsUnlocked(it) }
            ?: return@withContext null

        val pageRef = CGPDFDocumentGetPage(document, 1u) ?: run {
            CGPDFDocumentRelease(document)
            return@run null
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

        CGContextSetFillColorWithColor(context, UIColor.whiteColor.CGColor)
        CGContextFillRect(context, CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble()))

        CGContextTranslateCTM(context, 0.0, height.toDouble())
        CGContextScaleCTM(context, scale, -scale)

        CGContextDrawPDFPage(context, pageRef)

        val image = CGBitmapContextCreateImage(context)
        val uiImage = UIImage.imageWithCGImage(image)

        val thumbnailData = UIImagePNGRepresentation(uiImage)
        val byteArray = thumbnailData?.let { data ->
            ByteArray(data.length.toInt()).apply {
                usePinned { pinned ->
                    memcpy(pinned.addressOf(0), data.bytes, data.length)
                }
            }
        }

        // Clean up
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