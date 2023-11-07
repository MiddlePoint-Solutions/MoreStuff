package co.softov.morestuff.android.domain.service

import co.softov.morestuff.android.domain.model.Message

interface ShareTaskMessage {
    fun shareMessage(message: Message)
}
