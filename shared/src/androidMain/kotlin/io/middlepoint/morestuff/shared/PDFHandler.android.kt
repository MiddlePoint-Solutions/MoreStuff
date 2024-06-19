package io.middlepoint.morestuff.shared

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path


actual class PDFHandlerImpl(
    private val context: Context,
) : PDFHandler {

    override suspend fun savePDF(uri: String): String? = withContext(Dispatchers.IO) {
        try {
            val originalFileName = getOriginalFileName(uri.toUri()) ?: "PDF_Default.pdf"
            val pdfFile = createPDFFile(originalFileName).toFile()

            context.contentResolver.openInputStream(uri.toUri())?.use { inputStream ->
                FileOutputStream(pdfFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            pdfFile.absolutePath
        } catch (e: Exception) {
            Logger.e("Error handling PDF: $e")
            null
        }
    }

    private fun createPDFFile(originalFileName: String): Path {
        val pdfFileName =
            if (originalFileName.endsWith(".pdf")) originalFileName else "$originalFileName.pdf"
        return kotlin.io.path.createTempFile(prefix = "", suffix = pdfFileName)
    }

    override fun sharePDF(pdfPath: String) {
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

    override fun openPDF(pdfPath: String) {

        val pdfFile = if (pdfPath.startsWith("/")) {
            File(pdfPath)
        } else {
            File(context.cacheDir, pdfPath)
        }

        Logger.d("openPdf - File path: ${pdfFile.absolutePath}")
        if (pdfFile.exists()) {
            try {
                val pdfUri: Uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    pdfFile
                )

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = pdfUri
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                }

                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Error while opening ${pdfFile.name}", Toast.LENGTH_SHORT)
                    .show()
            }

        } else {
            Toast.makeText(context, "PDF file not found", Toast.LENGTH_SHORT).show()
        }
    }


}
