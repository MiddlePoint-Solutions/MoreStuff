package co.softov.morestuff.android.domain.nav

import android.os.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize


sealed class OnBoarding : Parcelable {

    @Parcelize
    data object Welcome : OnBoarding()

    @Parcelize
    data object NotificationPermission : OnBoarding()

    @Parcelize
    data object WorkSpaceReady : OnBoarding()

    @Parcelize
    data object ChatWithYourTask : OnBoarding()

    @Parcelize
    data object DailyTaskReview : OnBoarding()
}

