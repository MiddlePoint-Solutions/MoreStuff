package io.middlepoint.morestuff.shared.domain.service

import io.middlepoint.morestuff.shared.ui.model.MessageUiModel

interface ShareTaskMessage {
    fun shareMessage(message: MessageUiModel)
}
