package io.middlepoint.morestuff.shared.ui.screen.chat.items

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.core.net.toFile
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File


@Composable
actual fun PDFPagePreview(
  pdfFile: PlatformFile,
  width: Int,
  height: Int,
  scale: Float,
) {

  val cacheKey by remember {
    derivedStateOf {
      MemoryCache.Key("${pdfFile.uri.lastPathSegment}-$scale-${width}x${height}")
    }
  }
  var bitmap: Bitmap? by remember(pdfFile) { mutableStateOf(null) }

  LaunchedEffect(pdfFile) {
    withContext(Dispatchers.IO) {
      try {
        val file = if (pdfFile.uri.path?.contains("/cache/") == true) {
          ParcelFileDescriptor.open(
            pdfFile.uri.toFile(), ParcelFileDescriptor.MODE_READ_ONLY
          )
        } else {
          ParcelFileDescriptor.open(
            File(pdfFile.uri.path ?: ""), ParcelFileDescriptor.MODE_READ_ONLY
          )
        }

        val renderer = PdfRenderer(file)
        val destBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(destBitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(destBitmap, 0f, 0f, null)
        val mutex = Mutex()

        mutex.withLock {
          renderer.openPage(0)?.use { page ->
            val matrix = Matrix().apply {
              preScale(scale, scale)
            }
            page.render(
              destBitmap,
              null,
              matrix,
              PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
            )

            bitmap = destBitmap
          }
          renderer.close()
        }

      } catch (e: Exception) {
        Logger.e { "${pdfFile.uri} --> $e" }
      }
    }
  }

  Image(
    modifier = Modifier.aspectRatio(1f),
    contentScale = ContentScale.Crop,
    painter = rememberAsyncImagePainter(
      model = ImageRequest.Builder(LocalPlatformContext.current)
        .data(data = bitmap)
        .crossfade(true)
        .scale(Scale.FIT)
        .memoryCacheKey(cacheKey)
        .build(),
      imageLoader = ImageLoader(LocalPlatformContext.current)
    ),
    contentDescription = "PDF preview"
  )

}
