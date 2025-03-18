package io.middlepoint.morestuff.shared.domain.service

import io.middlepoint.morestuff.shared.domain.model.HintTask

interface HintTaskProvider {
    fun getHintTasks(): List<HintTask>
}
