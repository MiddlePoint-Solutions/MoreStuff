package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.service.TimeManager
import kotlinx.datetime.toInstant
import kotlin.math.max

interface GetPlanPriorityScoreUseCase {
    suspend operator fun invoke(
        scheduleTimeLocal: String,
        taskCreateTimeUtc: String? = null,
    ): Long
}

class GetPlanPriorityScoreUseCaseImpl(
    private val getLowestPriorityScoreUseCase: GetLowestPriorityScoreUseCase,
    private val getHighestPriorityScoreUseCase: GetHighestPriorityScoreUseCase,
    private val timeManager: TimeManager,
) : GetPlanPriorityScoreUseCase {
    override suspend fun invoke(
        scheduleTimeLocal: String,
        taskCreateTimeUtc: String?,
    ): Long {

        val createTime =
            taskCreateTimeUtc?.toInstant()?.epochSeconds ?: timeManager.nowUtcInstant.epochSeconds
        val scheduleTime = timeManager.localDateTimeStringToUtc(scheduleTimeLocal).epochSeconds
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
