package io.middlepoint.morestuff.shared

interface ClipboardHelper {
    fun copyToClipboard(text: String)
}

// TODO: this is not the correct way of doing it, causes issues with iOS build
expect class ClipboardHelperImpl : ClipboardHelper