package io.middlepoint.morestuff.shared

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path

class ImageHandlerImpl(
    private val context: Context
) : ImageHandler {
    override suspend fun saveImages(uris: String, time: LocalDateTime): String? = withContext(Dispatchers.IO) {
        val outputStream: FileOutputStream?

        val savedImagePath = try {
            val contentResolver = context.contentResolver
            val uri = uris.toUri()


            val exifInterface = uri.let { uri ->
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    ExifInterface(inputStream)
                }
            }
            val orientation = exifInterface?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            ) ?: ExifInterface.ORIENTATION_NORMAL


            val bitmap = uri.let { uri ->
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
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

            outputStream = withContext(Dispatchers.IO) {
                FileOutputStream(imageFile)
            }

            rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            outputStream?.close()
            imageFile.absolutePath
        } catch (e: Exception) {
            Logger.e("Error handling image: $e")
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


}
