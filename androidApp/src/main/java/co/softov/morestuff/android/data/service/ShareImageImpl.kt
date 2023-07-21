package co.softov.morestuff.android.data.service

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import co.softov.morestuff.android.domain.service.ShareImage
import java.io.File

class ShareImageImpl(private val context: Context) :
    ShareImage {

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