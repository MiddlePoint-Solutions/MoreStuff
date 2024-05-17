package io.middlepoint.morestuff.shared.domain.usecase.priority

import io.middlepoint.morestuff.shared.domain.service.TimeManager
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlin.math.max

interface GetPlanPriorityScoreUseCase {
    suspend operator fun invoke(
        scheduleTimeLocal: LocalDateTime,
        taskCreateTimeUtc: String? = null,
    ): Long
}

class GetPlanPriorityScoreUseCaseImpl(
    private val getLowestPriorityScoreUseCase: GetLowestPriorityScoreUseCase,
    private val getHighestPriorityScoreUseCase: GetHighestPriorityScoreUseCase,
    private val timeManager: TimeManager,
) : GetPlanPriorityScoreUseCase {
    override suspend fun invoke(
        scheduleTimeLocal: LocalDateTime,
        taskCreateTimeUtc: String?,
    ): Long {

        val createTime =
            taskCreateTimeUtc?.toInstant()?.epochSeconds ?: timeManager.nowUtcInstant.epochSeconds
        val scheduleTime = timeManager.localDateTimeToUtc(scheduleTimeLocal).epochSeconds
        val currentTime = timeManager.nowUtcInstant.epochSeconds

        val hp = getHighestPriorityScoreUseCase()
        val lp = getLowestPriorityScoreUseCase()

        println(
            "createTime: $createTime\n scheduleTime: $scheduleTime\n currentTime: $currentTime"
        )

        val currentDiff = max(0, currentTime - createTime)
        val scheduleDiff = scheduleTime - createTime
        val priorityDiff = hp - lp

        return (lp + (currentDiff.toFloat() / scheduleDiff.toFloat()) * priorityDiff).toLong()
    }
}
