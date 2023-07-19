package co.softov.morestuff.android.data.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import co.softov.morestuff.android.domain.service.ImageHandlerInterface
import co.softov.morestuff.android.domain.service.TimeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.nio.file.Path

class ImageHandler(private val context: Context, private val timeManager: TimeManager) :
    ImageHandlerInterface {

    override suspend fun handleImages(
        uris: Uri,
        id: Long,
    ): String = withContext(Dispatchers.IO) {

        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uris)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val imageFile = createImageFile(timeManager).toFile()

        val outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(imageFile)
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        outputStream.close()

        return@withContext imageFile.absolutePath
    }

    private fun createImageFile(timeManager: TimeManager): Path {
        val currentMoment = timeManager.nowLocalDateTime
        val timeStamp =
            "${currentMoment.year}${currentMoment.monthNumber}${currentMoment.dayOfMonth}_${currentMoment.hour}${currentMoment.minute}${currentMoment.second}"
        val imageFileName = "JPEG_" + timeStamp + "_"
        return kotlin.io.path.createTempFile(prefix = imageFileName, suffix = ".jpg")
    }
}
