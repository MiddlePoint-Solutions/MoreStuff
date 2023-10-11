package co.softov.morestuff.android.domain.service

import co.softov.morestuff.android.data.service.MessageHint
import co.softov.morestuff.android.domain.model.Priority

interface HintTaskProvider {

    fun getTaskTitles(): List<String>
    fun getTaskMessages(taskName: String, priority: Priority): List<MessageHint>
}
