package io.middlepoint.morestuff.shared

interface ShareHelper {
    fun shareMessage(content: String)
}

expect class ShareHelperImpl: ShareHelper
