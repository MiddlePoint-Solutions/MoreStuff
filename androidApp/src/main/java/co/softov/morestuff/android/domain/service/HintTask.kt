package co.softov.morestuff.android.domain.service

import co.softov.morestuff.android.data.service.HintTaskName

interface HintTask {
    fun getHintTasks(): List<HintTaskName>
}
