package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.screen.chat.ChatActions


// TODO: Multiplatform PDF Message
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PDFMessage(
    pdfUri: String,
    actions: ChatActions,
    message: MessageUiModel,
    showMenu: () -> Unit,
) {

//    val imageLoader = LocalContext.current.imageLoader
//
//    val fileUri by remember {
//        derivedStateOf {
//            if (pdfUri.scheme != "file") {
//                Uri.fromFile(File(pdfUri.path ?: ""))
//            } else {
//                pdfUri
//            }
//        }
//    }
//
//    var renderer: PdfRenderer? by remember { mutableStateOf(null) }
//
//    LaunchedEffect(Unit) {
//        withContext(Dispatchers.IO) {
//            try {
//                val file = ParcelFileDescriptor.open(
//                    fileUri.toFile(), ParcelFileDescriptor.MODE_READ_ONLY
//                )
//                renderer = PdfRenderer(file)
//            } catch (e: Exception) {
//                Timber.e(e)
//            }
//        }
//    }
//
//    val pdfFileName = getFileNameFromUri(fileUri) ?: "PDF Unknown"
//
//    Surface(
//        shape = RoundedCornerShape(
//            topStart = 14.dp,
//            topEnd = 14.dp,
//            bottomEnd = 5.dp,
//            bottomStart = 14.dp
//        ),
//        color = MaterialTheme.colorScheme.primary,
//        modifier = Modifier
//            .padding(2.dp)
//            .combinedClickable(
//                onClick = { actions.onPdfSelected(message) },
//                onLongClick = showMenu
//            )
//    ) {
//        Box {
//            Row(
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                BoxWithConstraints(modifier = Modifier.weight(0.8f)) {
//                    val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
//                    PDFPageItem(
//                        uri = fileUri,
//                        renderer = renderer,
//                        width = width,
//                        height = width,
//                        imageLoader = imageLoader,
//                    )
//                }
//
//                Column(
//                    modifier = Modifier
//                        .padding(8.dp)
//                        .weight(2f)
//                ) {
//                    Text(
//                        text = pdfFileName,
//                        textAlign = TextAlign.Start,
//                        maxLines = 2,
//                        overflow = TextOverflow.Ellipsis,
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//
//                    Text(
//                        text = stringResource(R.string.filetype_pdf),
//                        style = MaterialTheme.typography.bodySmall.copy(
//                            color = MaterialTheme.colorScheme.outlineVariant
//                        )
//                    )
//                }
//            }
//
//            MessageTime(
//                formattedTimeOnly = message.formattedTimeOnly,
//                modifier = Modifier.align(Alignment.BottomEnd)
//            )
//
//        }
//    }
}
//
//@Composable
//private fun getFileNameFromUri(uri: Uri): String? {
//    val context = LocalContext.current
//    var fileName: String? = null
//
//    if (uri.scheme.equals("content", ignoreCase = true)) {
//        val cursor = context.contentResolver.query(uri, null, null, null, null)
//        cursor?.use {
//            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//            if (nameIndex >= 0 && it.moveToFirst()) {
//                fileName = it.getString(nameIndex)
//            }
//        }
//    } else if (uri.scheme.equals("file", ignoreCase = true)) {
//        fileName = uri.lastPathSegment
//    }
//
//    return fileName
//}
//
//@Composable
//fun PDFPageItem(
//    uri: Uri,
//    renderer: PdfRenderer?,
//    width: Int,
//    height: Int,
//    imageLoader: ImageLoader,
//    scale: Float = 0.38f,
//) {
//
//    val context = LocalContext.current
//    val mutex = remember { Mutex() }
//    val cacheKey = remember { MemoryCache.Key("$uri-$scale-${width}x${height}") }
//    var bitmap: Bitmap? by remember {
//        mutableStateOf(imageLoader.memoryCache?.get(cacheKey)?.bitmap)
//    }
//
//    if (renderer != null) {
//        LaunchedEffect(Unit) {
//            withContext(Dispatchers.IO) {
//                val destBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//                val canvas = Canvas(destBitmap)
//                canvas.drawColor(android.graphics.Color.WHITE)
//                canvas.drawBitmap(destBitmap, 0f, 0f, null)
//                mutex.withLock {
//                    renderer.openPage(0)?.use { page ->
//                        val matrix = Matrix().apply {
//                            preScale(scale, scale)
//                        }
//                        page.render(
//                            destBitmap,
//                            null,
//                            matrix,
//                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
//                        )
//
//                        bitmap = destBitmap
//                    }
//                    renderer.close()
//                }
//            }
//        }
//    }
//
//    val request = ImageRequest.Builder(context)
//        .size(width)
//        .crossfade(true)
//        .memoryCacheKey(cacheKey)
//        .data(bitmap)
//        .build()
//
//    Image(
//        modifier = Modifier.aspectRatio(1f),
//        contentScale = ContentScale.Crop,
//        painter = rememberAsyncImagePainter(request),
//        contentDescription = "PDF preview"
//    )
//
//}
