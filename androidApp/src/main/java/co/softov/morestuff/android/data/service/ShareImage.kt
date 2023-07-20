package co.softov.morestuff.android.data.service

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import co.softov.morestuff.android.domain.service.ShareImageInterface
import java.io.File

class ShareImage(private val context: Context, private val applicationId: String) :
    ShareImageInterface {

    override fun shareImage(imagePath: String) {
        val file = File(imagePath)
        val contentUri = FileProvider.getUriForFile(context, "$applicationId.fileprovider", file)

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