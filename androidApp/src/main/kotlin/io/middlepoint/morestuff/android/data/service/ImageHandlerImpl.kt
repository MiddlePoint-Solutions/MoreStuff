package io.middlepoint.morestuff.android.data.service

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import io.middlepoint.morestuff.android.domain.service.ImageHandler
import io.middlepoint.morestuff.android.domain.service.TimeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path

class ImageHandlerImpl(
    private val context: Context,
    private val timeManager: TimeManager
) : ImageHandler {
    override suspend fun saveImages(uris: String): String? = withContext(Dispatchers.IO) {
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

            val imageFile = createImageFile(timeManager).toFile()

            outputStream = withContext(Dispatchers.IO) {
                FileOutputStream(imageFile)
            }

            rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            outputStream?.close()
            imageFile.absolutePath
        } catch (e: Exception) {
            Timber.e(e, "Error handling image")
            ""
        }

        savedImagePath
    }

    private fun Bitmap.rotate(angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }


    private fun createImageFile(timeManager: TimeManager): Path {
        val currentMoment = timeManager.nowLocalDateTime
        val timeStamp =
            "${currentMoment.year}${currentMoment.monthNumber}${currentMoment.dayOfMonth}_${currentMoment.hour}${currentMoment.minute}${currentMoment.second}"
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
