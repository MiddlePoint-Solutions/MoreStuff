package co.softov.morestuff.android.domain.service

import co.softov.morestuff.android.ui.model.MessageUiModel

interface ShareTaskMessage {
    fun shareMessage(message: MessageUiModel)
}
