package co.softov.morestuff.android.data.service

import android.content.Context
import android.content.Intent
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
            val pdfFile = createPdfFile(timeManager).toFile()
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

    private fun createPdfFile(timeManager: TimeManager): Path {
        val currentMoment = timeManager.nowLocalDateTime
        val timeStamp = "${currentMoment.year}${currentMoment.monthNumber}${currentMoment.dayOfMonth}_${currentMoment.hour}${currentMoment.minute}${currentMoment.second}"
        val pdfFileName = "PDF_$timeStamp.pdf"
        return kotlin.io.path.createTempFile(prefix = pdfFileName, suffix = ".pdf")
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

}
