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
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.uri
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.middlepoint.morestuff.shared.Constants.KEY_FILES_DIRECTORY
import io.middlepoint.morestuff.shared.Constants.KEY_IMAGES_DIRECTORY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import java.io.File
import java.io.FileOutputStream

object Constants {
  const val KEY_IMAGES_DIRECTORY = "images"
  const val KEY_FILES_DIRECTORY = "files"
}

class MediaHandlerImpl(
  private val context: Context,
  private val logger: Logger
) : MediaHandler {


  override suspend fun saveMedia(media: PlatformFile, time: LocalDateTime): String? =
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

        val imageFile = createImageFile(time)
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


  private fun getAppSpecificStorageDir(type: String): File {
    val appFilesDir = context.getExternalFilesDir(null) ?: context.filesDir
    val mediaDir = File(appFilesDir, type)
    if (!mediaDir.exists()) {
      mediaDir.mkdirs()
    }
    return mediaDir
  }


  // TODO: change files location according to: https://developer.android.com/training/data-storage
  private fun createImageFile(time: LocalDateTime): File {
    val timeStamp =
      "${time.year}${time.monthNumber}${time.dayOfMonth}_${time.hour}${time.minute}${time.second}"
    val imageFileName = "JPEG_$timeStamp.jpg"

    val storageDir = getAppSpecificStorageDir(KEY_IMAGES_DIRECTORY)

    return File(storageDir, imageFileName)
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

  override suspend fun savePDF(media: PlatformFile): String? = withContext(Dispatchers.IO) {
    try {
      val originalFileName = media.name ?: "PDF_Default.pdf"
      val pdfFile = createPDFFile(originalFileName)

      media.readBytes().inputStream().use { inputStream ->
        FileOutputStream(pdfFile).use { outputStream ->
          inputStream.copyTo(outputStream)
        }
      }

      pdfFile.absolutePath
    } catch (e: Exception) {
      logger.e("Error saving PDF: $e")
      null
    }
  }


  private fun createPDFFile(originalFileName: String): File {
    val pdfFileName =
      if (originalFileName.endsWith(".pdf")) originalFileName else "$originalFileName.pdf"
    val storageDir = getAppSpecificStorageDir(KEY_FILES_DIRECTORY)
    return File(storageDir, pdfFileName)
  }

  override fun sharePDF(path: String) {
    logger.d("sharePDF - File path: $path")

    val pdfFile = if (path.startsWith("/")) {
      File(path)
    } else {
      File(context.cacheDir, path.toUri().lastPathSegment ?: "")
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

  override fun openPDF(path: String) {

    logger.d("openPdf - File path: $path")

    val pdfFile = if (path.startsWith("/")) {
      File(path)
    } else {
      File(context.cacheDir, path.toUri().lastPathSegment ?: "")
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

  override suspend fun saveJsonToFile(jsonString: String, fileName: String): String? = withContext(Dispatchers.IO) {
    try {
      val jsonFileName = "$fileName.json"
      val storageDir = getAppSpecificStorageDir(KEY_FILES_DIRECTORY)
      val jsonFile = File(storageDir, jsonFileName)

      FileOutputStream(jsonFile).use { outputStream ->
        outputStream.write(jsonString.toByteArray())
      }

      jsonFile.absolutePath
    } catch (e: Exception) {
      logger.e("Error saving JSON file: $e")
      null
    }
  }

  override fun shareFile(path: String) {
    logger.d("share - File path: $path")

    val file = if (path.startsWith("/")) {
      File(path)
    } else {
      File(context.cacheDir, path.toUri().lastPathSegment ?: "")
    }

    val contentUri = FileProvider.getUriForFile(
      context, "${context.packageName}.fileprovider", file
    )

    val intent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_STREAM, contentUri)
//      type = "application/pdf"
      flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
    }
    val chooserIntent = Intent.createChooser(intent, null).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    context.startActivity(chooserIntent)
  }
}
