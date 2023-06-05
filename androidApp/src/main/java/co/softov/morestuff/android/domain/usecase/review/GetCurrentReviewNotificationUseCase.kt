package co.softov.morestuff.android.domain.usecase.review

import co.softov.morestuff.android.domain.enums.ReviewNotification
import co.softov.morestuff.android.domain.service.TimeManager

interface GetCurrentReviewNotificationUseCase {

    operator fun invoke(): ReviewNotification

}

class GetCurrentReviewNotificationUseCaseImpl(
    private val timeManager: TimeManager
) : GetCurrentReviewNotificationUseCase {
    override fun invoke(): ReviewNotification =
        when (timeManager.nowLocalDateTime.time.hour > 19) {
            true -> ReviewNotification.Evening
            false -> ReviewNotification.Morning
        }

}

