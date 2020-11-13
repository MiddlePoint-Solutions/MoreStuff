package co.softov.morestuff.androidApp.presentation.content

import co.softov.morestuff.androidApp.domain.enums.ReplyType

class ChatResponseActions(
    var scheduleResponse: (scheduleId: Long, ReplyType) -> Unit
)