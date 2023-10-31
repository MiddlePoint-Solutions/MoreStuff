package co.softov.morestuff.android.domain.service

import co.softov.morestuff.android.domain.model.HintTask

interface HintTaskProvider {
    fun getHintTasks(): List<HintTask>
}
