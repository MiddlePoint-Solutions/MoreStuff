package co.softov.morestuff.android.data.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import co.softov.morestuff.android.domain.service.ImageHandler
import co.softov.morestuff.android.domain.service.TimeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Path

class ImageHandlerImpl(
    private val context: Context,
    private val timeManager: TimeManager
) : ImageHandler {

    override suspend fun handleImages(uris: Uri, id: Long): String? = withContext(Dispatchers.IO) {

        var inputStream: InputStream?
        var outputStream: FileOutputStream?

        val savedImagePath = try {
            val contentResolver = context.contentResolver
            inputStream = contentResolver.openInputStream(uris)
            inputStream?.let {
                val bitmap = BitmapFactory.decodeStream(inputStream)

                val exifInterface = ExifInterface(inputStream)
                val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                inputStream.close()

                val rotatedBitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> bitmap.rotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> bitmap.rotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> bitmap.rotate(270f)
                    else -> bitmap
                }

                val imageFile = createImageFile(timeManager).toFile()

                outputStream = withContext(Dispatchers.IO) {
                    FileOutputStream(imageFile)
                }

                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                outputStream?.close()
                imageFile.absolutePath
            }
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
}
