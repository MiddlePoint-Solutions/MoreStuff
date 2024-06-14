package io.middlepoint.morestuff.shared.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import io.middlepoint.morestuff.shared.domain.service.ClipboardHelper

class ClipboardHelperImpl(private val context: Context): ClipboardHelper {
    override fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied to clipboard", text)
        clipboard.setPrimaryClip(clip)
    }
}