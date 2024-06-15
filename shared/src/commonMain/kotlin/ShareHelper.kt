interface ShareHelper {
    fun shareMessage(content: String)
}

expect class ShareHelperImpl: ShareHelper
