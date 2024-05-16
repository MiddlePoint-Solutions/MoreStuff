package io.middlepoint.morestuff.android.domain.service

import io.middlepoint.morestuff.android.ui.model.MessageUiModel

interface ShareTaskMessage {
    fun shareMessage(message: MessageUiModel)
}
