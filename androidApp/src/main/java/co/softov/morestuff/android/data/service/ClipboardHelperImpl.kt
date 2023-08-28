package co.softov.morestuff.android.data.service

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import co.softov.morestuff.android.domain.service.ClipboardHelper

class ClipboardHelperImpl(private val context: Context): ClipboardHelper {
    override fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied to clipboard", text)
        clipboard.setPrimaryClip(clip)
    }
}