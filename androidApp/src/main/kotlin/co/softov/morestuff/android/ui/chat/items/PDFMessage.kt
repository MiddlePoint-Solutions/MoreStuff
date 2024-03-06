package co.softov.morestuff.android.ui.chat.items

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.model.MessageUiModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PDFMessage(
    pdfUri: Uri,
    actions: ChatActions,
    message: MessageUiModel,
    showMenu: () -> Unit,
) {

    val imageLoader = LocalContext.current.imageLoader

    val fileUri by remember {
        derivedStateOf {
            if (pdfUri.scheme != "file") {
                Uri.fromFile(File(pdfUri.path ?: ""))
            } else {
                pdfUri
            }
        }
    }

    var renderer: PdfRenderer? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val file = ParcelFileDescriptor.open(
                    fileUri.toFile(), ParcelFileDescriptor.MODE_READ_ONLY
                )
                renderer = PdfRenderer(file)
            } catch (e: Exception) {
                Timber.e(e)
            }
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
        Box {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                BoxWithConstraints(modifier = Modifier.weight(0.8f)) {
                    val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
                    PDFPageItem(
                        uri = fileUri,
                        renderer = renderer,
                        width = width,
                        height = width,
                        imageLoader = imageLoader,
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
                        text = stringResource(R.string.filetype_pdf),
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
    renderer: PdfRenderer?,
    width: Int,
    height: Int,
    imageLoader: ImageLoader,
    scale: Float = 0.38f,
) {

    val context = LocalContext.current
    val mutex = remember { Mutex() }
    val cacheKey = remember { MemoryCache.Key("$uri-$scale-${width}x${height}") }
    var bitmap: Bitmap? by remember {
        mutableStateOf(imageLoader.memoryCache?.get(cacheKey)?.bitmap)
    }

    if (renderer != null) {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                val destBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(destBitmap)
                canvas.drawColor(android.graphics.Color.WHITE)
                canvas.drawBitmap(destBitmap, 0f, 0f, null)
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
            }
        }
    }

    val request = ImageRequest.Builder(context)
        .size(width)
        .crossfade(true)
        .memoryCacheKey(cacheKey)
        .data(bitmap)
        .build()

    Image(
        modifier = Modifier.aspectRatio(1f),
        contentScale = ContentScale.Crop,
        painter = rememberAsyncImagePainter(request),
        contentDescription = "PDF preview"
    )

}
