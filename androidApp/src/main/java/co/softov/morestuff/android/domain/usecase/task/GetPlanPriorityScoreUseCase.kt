package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.service.TimeManager
import kotlinx.datetime.toInstant
import kotlin.math.max

interface GetPlanPriorityScoreUseCase {
    suspend operator fun invoke(
        taskCreateTimeUtc: String? = null,
        scheduleTimeUtc: String? = null
    ): Long
}

class GetPlanPriorityScoreUseCaseImpl(
    private val getLowestPriorityScoreUseCase: GetLowestPriorityScoreUseCase,
    private val getHighestPriorityScoreUseCase: GetHighestPriorityScoreUseCase,
    private val timeManager: TimeManager,
) : GetPlanPriorityScoreUseCase {
    override suspend fun invoke(
        taskCreateTimeUtc: String?,
        scheduleTimeUtc: String?
    ): Long {

        val lp = getLowestPriorityScoreUseCase()
        if(taskCreateTimeUtc == null || scheduleTimeUtc == null) {
            return lp
        }

        val hp = getHighestPriorityScoreUseCase()
        val createTimeMs = taskCreateTimeUtc.toInstant().epochSeconds
        val scheduleTimeMs = scheduleTimeUtc.toInstant().epochSeconds
        val currentTimeMs = timeManager.nowUtcInstant.epochSeconds

        return lp + (
                max(0, currentTimeMs - createTimeMs) /
                        max(0, scheduleTimeMs - createTimeMs)
                ) * (hp - lp)
    }
}
