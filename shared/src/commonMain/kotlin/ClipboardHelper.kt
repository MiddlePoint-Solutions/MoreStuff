interface ClipboardHelper {
    fun copyToClipboard(text: String)
}

expect class ClipboardHelperImpl : ClipboardHelper