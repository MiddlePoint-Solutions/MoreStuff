package co.softov.morestuff.android.data.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import co.softov.morestuff.android.domain.service.PdfHandler
import co.softov.morestuff.android.domain.service.TimeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path


class PdfHandlerImpl(
    private val context: Context,
    private val timeManager: TimeManager
) : PdfHandler {

override suspend fun savePdf(uri: String): String? = withContext(Dispatchers.IO) {
    try {
        val originalFileName = getOriginalFileName(uri.toUri()) ?: "PDF_Default.pdf"
        val pdfFile = createPdfFile(originalFileName).toFile()

        context.contentResolver.openInputStream(uri.toUri())?.use { inputStream ->
            FileOutputStream(pdfFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        pdfFile.absolutePath
    } catch (e: Exception) {
        Timber.e(e, "Error handling PDF")
        null
    }
}

    private fun createPdfFile(originalFileName: String): Path {
    val pdfFileName = if (originalFileName.endsWith(".pdf")) originalFileName else "$originalFileName.pdf"
    return kotlin.io.path.createTempFile(prefix = "", suffix = pdfFileName)
}

    override fun sharePdf(pdfPath: String) {
        val packageName = context.packageName
        val file = File(pdfPath)
        val contentUri = FileProvider.getUriForFile(context, "$packageName.fileprovider", file)

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, contentUri)
            type = "application/pdf"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val chooserIntent = Intent.createChooser(intent, null).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(chooserIntent)
    }
    private fun getOriginalFileName(uri: Uri): String? {
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
            fileName = File(uri.path ?: "").name
        }

        return fileName
    }

}
