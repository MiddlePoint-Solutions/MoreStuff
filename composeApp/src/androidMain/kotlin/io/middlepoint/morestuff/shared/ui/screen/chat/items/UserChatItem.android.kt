package io.middlepoint.morestuff.shared.ui.screen.chat.items

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.mohamedrejeb.calf.io.KmpFile
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.filetype_pdf
import org.jetbrains.compose.resources.stringResource
import java.io.File

@Composable
actual fun PDFMessageItem(
  pdfUri: String,
  message: MessageUiModel,
  modifier: Modifier
) {

  val fileUri by remember {
    derivedStateOf { Uri.fromFile(File(pdfUri)) }
  }

  val pdfFileName = getFileNameFromUri(fileUri) ?: "PDF Unknown"

  Surface(
    shape = RoundedCornerShape(
      topStart = 14.dp,
      topEnd = 14.dp,
      bottomEnd = 5.dp,
      bottomStart = 14.dp
    ),
    color = MaterialTheme.colorScheme.primary,
    modifier = modifier.padding(2.dp)
  ) {
    Box {
      Row(
        modifier = Modifier.fillMaxWidth()
      ) {
        BoxWithConstraints(modifier = Modifier.weight(0.8f)) {
          val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
          PDFPageItem(
            uri = fileUri,
            width = width,
            height = width,
          )
        }

        Column(
          modifier = Modifier
            .padding(8.dp)
            .weight(2f)
        ) {
          Text(
            text = pdfFileName,
            textAlign = TextAlign.Start,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge
          )

          Text(
            text = stringResource(Res.string.filetype_pdf),
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.outlineVariant
            )
          )
        }
      }

      MessageTime(
        formattedTimeOnly = message.formattedTimeOnly,
        modifier = Modifier.align(Alignment.BottomEnd)
      )

    }
  }
}

@Composable
private fun getFileNameFromUri(uri: Uri): String? {
  val context = LocalContext.current
  var fileName: String? = null

  if (uri.scheme.equals("content", ignoreCase = true)) {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
      val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
      if (nameIndex >= 0 && it.moveToFirst()) {
        fileName = it.getString(nameIndex)
      }
    }
  } else if (uri.scheme.equals("file", ignoreCase = true)) {
    fileName = uri.lastPathSegment
  }

  return fileName
}

@Composable
fun PDFPageItem(
  uri: Uri,
  width: Int,
  height: Int,
  scale: Float = 0.38f,
) {

  var renderer: PdfRenderer? by remember { mutableStateOf(null) }

  LaunchedEffect(Unit) {
    withContext(Dispatchers.IO) {
      try {
        val file = ParcelFileDescriptor.open(
          uri.toFile(), ParcelFileDescriptor.MODE_READ_ONLY
        )
        renderer = PdfRenderer(file)
      } catch (e: Exception) {
        Logger.e { "$e $uri" }
      }
    }
  }

  val mutex = remember { Mutex() }
  val cacheKey = remember { MemoryCache.Key("$uri-$scale-${width}x${height}") }
  var bitmap: Bitmap? by remember { mutableStateOf(null) }

  if (renderer != null) {
    LaunchedEffect(Unit) {
      withContext(Dispatchers.IO) {
        val destBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(destBitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        canvas.drawBitmap(destBitmap, 0f, 0f, null)
        mutex.withLock {
          renderer?.openPage(0)?.use { page ->
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
          renderer?.close()
        }
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
