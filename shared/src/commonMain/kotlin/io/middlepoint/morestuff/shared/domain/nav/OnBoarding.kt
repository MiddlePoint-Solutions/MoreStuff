package io.middlepoint.morestuff.shared.domain.nav

import android.os.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize


sealed class OnBoarding : Parcelable {

    @Parcelize
    data object Welcome : OnBoarding()

    @Parcelize
    data object NotificationPermission : OnBoarding()

    @Parcelize
    data object Ready : OnBoarding()

    @Parcelize
    data object ChatWithYourTasks : OnBoarding()

    @Parcelize
    data object ReviewReminder : OnBoarding()

    @Parcelize
    data object Review : OnBoarding()
}

