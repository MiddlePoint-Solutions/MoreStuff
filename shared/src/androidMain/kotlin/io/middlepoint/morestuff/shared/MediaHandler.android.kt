package io.middlepoint.morestuff.shared

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import co.touchlab.kermit.Logger
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.io.getName
import com.mohamedrejeb.calf.io.readByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path

class MediaHandlerImpl(
  private val context: Context,
  private val logger: Logger
) : MediaHandler {

  override suspend fun saveMedia(media: KmpFile, time: LocalDateTime): String? =
    withContext(Dispatchers.IO) {
      val uri = media.uri
      val outputStream: FileOutputStream?

      val savedImagePath = try {
        val contentResolver = context.contentResolver

        val exifInterface = contentResolver.openInputStream(uri)?.use { inputStream ->
          ExifInterface(inputStream)
        }

        val orientation = exifInterface?.getAttributeInt(
          ExifInterface.TAG_ORIENTATION,
          ExifInterface.ORIENTATION_NORMAL
        ) ?: ExifInterface.ORIENTATION_NORMAL


        val bitmap = contentResolver.openInputStream(uri)?.use { inputStream ->
          BitmapFactory.decodeStream(inputStream)
        }

        val rotatedBitmap = bitmap?.let {
          when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> it.rotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> it.rotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> it.rotate(270f)
            else -> it
          }
        }

        val imageFile = createImageFile(time).toFile()
        outputStream = FileOutputStream(imageFile)
        rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        outputStream.close()
        imageFile.absolutePath
      } catch (e: Exception) {
        logger.e("Error handling image: $e")
        ""
      }

      savedImagePath
    }

  private fun Bitmap.rotate(angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
  }


  private fun createImageFile(time: LocalDateTime): Path {
    val timeStamp =
      "${time.year}${time.monthNumber}${time.dayOfMonth}_${time.hour}${time.minute}${time.second}"
    val imageFileName = "JPEG_" + timeStamp + "_"
    return kotlin.io.path.createTempFile(prefix = imageFileName, suffix = ".jpg")
  }

  override fun shareImage(imagePath: String) {
    val packageName = context.packageName
    val file = File(imagePath)
    val contentUri = FileProvider.getUriForFile(context, "$packageName.fileprovider", file)

    val intent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_STREAM, contentUri)
      type = "image/jpg"
      flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
    }
    val chooserIntent = Intent.createChooser(intent, null).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    context.startActivity(chooserIntent)
  }

  override suspend fun savePDF(media: KmpFile): String? = withContext(Dispatchers.IO) {
    try {
      val originalFileName = media.getName(context) ?: "PDF_Default.pdf"
      val pdfFile = createPDFFile(originalFileName).toFile()

      media.readByteArray(context).inputStream().use { inputStream ->
        FileOutputStream(pdfFile).use { outputStream ->
          inputStream.copyTo(outputStream)
        }
      }

      pdfFile.toUri().toString()
    } catch (e: Exception) {
      logger.e("Error saving PDF: $e")
      null
    }
  }

  private fun createPDFFile(originalFileName: String): Path {
    val pdfFileName =
      if (originalFileName.endsWith(".pdf")) originalFileName else "$originalFileName.pdf"
    return kotlin.io.path.createTempFile(prefix = "", suffix = pdfFileName)
  }

  override fun sharePDF(pdfPath: String) {
    logger.d("sharePDF - File path: $pdfPath")

    val pdfFile = if (pdfPath.startsWith("/")) {
      File(pdfPath)
    } else {
      File(context.cacheDir, pdfPath.toUri().lastPathSegment ?: "")
    }

    val contentUri = FileProvider.getUriForFile(
      context, "${context.packageName}.fileprovider", pdfFile
    )

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

  override fun openPDF(pdfPath: String) {

    logger.d("openPdf - File path: $pdfPath")

    val pdfFile = if (pdfPath.startsWith("/")) {
      File(pdfPath)
    } else {
      File(context.cacheDir, pdfPath.toUri().lastPathSegment ?: "")
    }

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
        logger.e("Error while opening ${pdfFile.name}", e)
        Toast.makeText(context, "Error while opening ${pdfFile.name}", Toast.LENGTH_SHORT)
          .show()
      }

    } else {
      Toast.makeText(context, "PDF file not found", Toast.LENGTH_SHORT).show()
    }
  }
}
