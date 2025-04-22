package io.middlepoint.morestuff.shared.ui.model.map

import io.middlepoint.morestuff.shared.domain.model.core.IAMessage
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import io.middlepoint.morestuff.shared.ui.model.IAMessageUiModel

class IAMessageUiMapper(private val timeFormatter: TimeFormatter) {

    fun map(input: IAMessage): IAMessageUiModel =
        IAMessageUiModel(
            id = input.id,
            scopeId = input.scopeId,
            contentType = input.contentType,
            createTime = timeFormatter.formatToDateTime(input.createTime),
            seenTime = input.seenTime,
            content = input.content,
            replyType = input.replyType,
            replyContent = input.replyContent,
            replyTime = input.replyTime,
            openGraphResult = input.openGraphResult,
            messageData = input.messageData,
            formattedTime = timeFormatter.formatDisplayDayMonth(input.createTime),
            formattedTimeOnly = timeFormatter.formatDisplayTime(input.createTime)
        )

    fun map(input: List<IAMessage>): List<IAMessageUiModel> = input.map(this::map)
}