package co.softov.morestuff.android.ui.pdf

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
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


@Composable
fun PdfViewer(
    modifier: Modifier = Modifier,
    uri: Uri,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp)
) {
    val rendererScope = rememberCoroutineScope()
    val mutex = remember { Mutex() }
    val fileUri = if (uri.scheme != "file") Uri.fromFile(File(uri.path ?: "")) else uri

    val renderer by produceState<PdfRenderer?>(null, fileUri) {
        rendererScope.launch(Dispatchers.IO) {
            val input = ParcelFileDescriptor.open(fileUri.toFile(), ParcelFileDescriptor.MODE_READ_ONLY)
            value = PdfRenderer(input)
        }
        awaitDispose {
            val currentRenderer = value
            rendererScope.launch(Dispatchers.IO) {
                mutex.withLock {
                    currentRenderer?.close()
                }
            }
        }
    }

    val imageLoader = LocalContext.current.imageLoader
    val imageLoadingScope = rememberCoroutineScope()
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
        val height = (width * sqrt(2f)).toInt()
        val pageCount by remember(renderer) { derivedStateOf { renderer?.pageCount ?: 0 } }

        LazyColumn(verticalArrangement = verticalArrangement) {
            items(count = pageCount, key = { index -> "$fileUri-$index" }) { index ->
                PdfPageItem(index, fileUri, renderer, width, height, imageLoader, imageLoadingScope, mutex)
            }
        }
    }
}

@Composable
fun PdfPageItem(
    index: Int,
    uri: Uri,
    renderer: PdfRenderer?,
    width: Int,
    height: Int,
    imageLoader: ImageLoader,
    scope: CoroutineScope,
    mutex: Mutex
) {
    val cacheKey = MemoryCache.Key("$uri-$index")
    val cacheValue: Bitmap? = imageLoader.memoryCache?.get(cacheKey)?.bitmap

    var bitmap by remember { mutableStateOf(cacheValue) }
    if (bitmap == null) {
        DisposableEffect(uri, index) {
            val job = scope.launch(Dispatchers.IO) {
                val destinationBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                mutex.withLock {
                    renderer?.openPage(index)?.use { page ->
                        page.render(destinationBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
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
            modifier = Modifier.background(Color.White).aspectRatio(1f / sqrt(2f)).fillMaxWidth(),
            contentScale = ContentScale.Fit,
            painter = rememberAsyncImagePainter(request),
            contentDescription = "Page ${index + 1}"
        )
    }
}
