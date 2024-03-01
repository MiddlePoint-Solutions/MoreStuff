package co.softov.morestuff.android.ui.chat.items

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toFile
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.model.MessageUiModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import kotlin.math.sqrt


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PDFMessage(
    pdfUri: Uri,
    actions: ChatActions,
    message: MessageUiModel,
    showMenu: () -> Unit,
) {
    val imageLoader = LocalContext.current.imageLoader
    val rendererScope = rememberCoroutineScope()
    val mutex = remember { Mutex() }
    val fileUri = if (pdfUri.scheme != "file") Uri.fromFile(File(pdfUri.path ?: "")) else pdfUri
    val renderer by produceState<PdfRenderer?>(null, fileUri) {
        rendererScope.launch(Dispatchers.IO) {
            val input =
                ParcelFileDescriptor.open(fileUri.toFile(), ParcelFileDescriptor.MODE_READ_ONLY)
            value = PdfRenderer(input)
        }
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
        modifier = Modifier
            .padding(2.dp)
            .combinedClickable(
                onClick = { actions.onPdfSelected(message) },
                onLongClick = showMenu
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            BoxWithConstraints(modifier = Modifier.weight(0.8f)) {
                val scaleFactor = 0.75f
                val width = with(LocalDensity.current) { maxWidth.toPx() * scaleFactor }.toInt()
                val height = (width * sqrt(2f)).toInt()
                renderer?.let {
                    PDFPageItem(
                        index = 0,
                        uri = fileUri,
                        renderer = it,
                        width = width,
                        height = height,
                        imageLoader = imageLoader,
                        scope = rendererScope,
                        mutex = mutex,
                    )
                }
            }

            Text(
                text = pdfFileName,
                modifier = Modifier
                    .weight(2f)
                    .padding(4.dp),
                textAlign = TextAlign.Start,
                fontSize = 12.sp
            )
            MessageTime(
                formattedTimeOnly = message.formattedTimeOnly,
                modifier = Modifier.padding(end = 8.dp).align(Alignment.Bottom)
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
    index: Int,
    uri: Uri,
    renderer: PdfRenderer?,
    width: Int,
    height: Int,
    imageLoader: ImageLoader,
    scope: CoroutineScope,
    mutex: Mutex,
    scale: Float = 0.9f,
    offsetX: Float = 45f,
    offsetY: Float = 10f,
) {
    val cacheKey = MemoryCache.Key("$uri-$index-${width}x${height}")
    val cacheValue: Bitmap? = imageLoader.memoryCache?.get(cacheKey)?.bitmap

    var bitmap by remember { mutableStateOf(cacheValue) }
    if (bitmap == null) {
        DisposableEffect(uri, index) {
            val job = scope.launch(Dispatchers.IO) {
                val destinationBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                mutex.withLock {
                    renderer?.openPage(index)?.use { page ->
                        val matrix = Matrix().apply {
                            postScale(scale, scale)
                            postTranslate(-offsetX * scale, -offsetY * scale)
                        }
                        page.render(
                            destinationBitmap,
                            null,
                            matrix,
                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                        )
                    }
                }
                bitmap = destinationBitmap
            }
            onDispose { job.cancel() }
        }
        Box(modifier = Modifier.background(Color.White).aspectRatio(1f / sqrt(2f)).fillMaxWidth())
    } else {
        val request = ImageRequest.Builder(LocalContext.current)
            .size(width, height)
            .memoryCacheKey(cacheKey)
            .data(bitmap)
            .build()

        Image(
            modifier = Modifier
                .background(Color.White)
                .aspectRatio(1f / sqrt(2f))
                .fillMaxWidth(),
            contentScale = ContentScale.Crop,
            painter = rememberAsyncImagePainter(request),
            contentDescription = "Page ${index + 1}"
        )
    }
}


