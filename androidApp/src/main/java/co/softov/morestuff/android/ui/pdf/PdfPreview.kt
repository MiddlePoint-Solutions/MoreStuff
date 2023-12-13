package co.softov.morestuff.android.ui.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.items.MessageTime
import co.softov.morestuff.android.ui.chat.items.MockData
import co.softov.morestuff.android.ui.model.MessageUiModel
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PdfPreview(
    pdfUri: Uri,
    actions: ChatActions,
    message: MessageUiModel,
    showMenu: MutableState<Boolean>,
) {
    val context = LocalContext.current
    val imageUriState = produceState<Uri?>(initialValue = null, pdfUri) {
        value = pdfUri.path?.let { loadPdfPreview(context, it) }
    }

    val imageUri = imageUriState.value

    imageUri?.let {
        Surface(
            shape = RoundedCornerShape(
                topStart = 10.dp,
                topEnd = 10.dp,
                bottomEnd = 7.dp,
                bottomStart = 10.dp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(2.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "PDF Preview",
                    modifier = Modifier
                        .padding(start = 1.dp, top = 1.dp, end = 1.dp, bottom = 10.dp)
                        .combinedClickable(
                            onClick = { actions.onPdfSelected(message) },
                            onLongClick = { showMenu.value = true }
                        )
                        .sizeIn(minHeight = 200.dp, maxHeight = 400.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Inside
                )
                Row(Modifier.align(Alignment.End)) {
                    MessageTime(
                        formattedTimeOnly = MockData.messageUiModel.formattedTimeOnly,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}


private suspend fun loadPdfPreview(context: Context, pdfUri: String): Uri? {
    return withContext(Dispatchers.IO) {
        try {
            val pdfFile = File(pdfUri)
            if (!pdfFile.exists()) {
                return@withContext null
            }

            PdfRenderer(
                ParcelFileDescriptor.open(
                    pdfFile,
                    ParcelFileDescriptor.MODE_READ_ONLY
                )
            ).use { renderer ->
                val page = renderer.openPage(0)
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()

                val imageFile = File(context.cacheDir, "pdf_preview.jpg")
                FileOutputStream(imageFile).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
                    fos.flush()
                }
                Uri.fromFile(imageFile)
            }
        } catch (e: Exception) {
            Timber.tag("PdfPreview").e(e, "Error loading PDF preview")
            null
        }
    }
}





