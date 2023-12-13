package co.softov.morestuff.android.ui.pdf

import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.items.MessageTime
import co.softov.morestuff.android.ui.model.MessageUiModel
import coil.imageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.io.File
import kotlin.math.sqrt


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PdfPreview(
    pdfUri: Uri,
    actions: ChatActions,
    message: MessageUiModel,
    showMenu: MutableState<Boolean>,
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
    val pdfFileName = getFileNameFromUri(fileUri) ?: "PDF Desconocido"


    Surface(
        shape = RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp,
            bottomEnd = 7.dp,
            bottomStart = 10.dp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(2.dp)
            .combinedClickable(
                onClick = { actions.onPdfSelected(message) },
                onLongClick = { showMenu.value = true }
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            BoxWithConstraints(modifier = Modifier.weight(1f)) {
                val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
                val height = (width * sqrt(2f)).toInt()
                renderer?.let {
                    PdfPageItem(
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
                    .padding(8.dp),
                textAlign = TextAlign.Start
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
    }
    else if (uri.scheme.equals("file", ignoreCase = true)) {
        fileName = uri.lastPathSegment
    }

    return fileName
}




