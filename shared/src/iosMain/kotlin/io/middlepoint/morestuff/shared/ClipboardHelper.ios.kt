package io.middlepoint.morestuff.shared

import platform.UIKit.UIPasteboard

class ClipboardHelperImpl : ClipboardHelper {

    override fun copyToClipboard(text: String) {
        // Access the general pasteboard and set the string
        val pasteboard = UIPasteboard.generalPasteboard()
        pasteboard.setValue(text, forPasteboardType = "public.text")
    }
}
