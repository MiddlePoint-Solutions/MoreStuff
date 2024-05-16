package io.middlepoint.morestuff.android.domain.service

import io.middlepoint.morestuff.android.domain.model.HintTask

interface HintTaskProvider {
    fun getHintTasks(): List<HintTask>
}
