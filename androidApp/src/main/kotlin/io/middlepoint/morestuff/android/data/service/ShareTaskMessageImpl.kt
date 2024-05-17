package io.middlepoint.morestuff.android.data.service

import android.content.Context
import android.content.Intent
import io.middlepoint.morestuff.android.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.domain.service.ShareTaskMessage

class ShareTaskMessageImpl(
    private val context: Context,
) : ShareTaskMessage {

        override fun shareMessage(message: MessageUiModel) {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message.content)
                type = "text/plain"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val chooserIntent = Intent.createChooser(intent, "Share Message").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(chooserIntent)
        }
}
