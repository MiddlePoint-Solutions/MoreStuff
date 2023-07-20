package co.softov.morestuff.android.data.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import co.softov.morestuff.android.domain.service.ImageHandlerInterface
import co.softov.morestuff.android.domain.service.TimeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.nio.file.Path

class ImageHandler(private val context: Context, private val timeManager: TimeManager) :
    ImageHandlerInterface {

    override suspend fun handleImages(uris: Uri, id: Long): String = withContext(Dispatchers.IO) {

        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uris)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val exifInterface = ExifInterface(contentResolver.openInputStream(uris)!!)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> bitmap.rotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> bitmap.rotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> bitmap.rotate(270f)
            else -> bitmap
        }

        val imageFile = createImageFile(timeManager).toFile()

        val outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(imageFile)
        }

        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        outputStream.close()

        return@withContext imageFile.absolutePath
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
