package io.middlepoint.morestuff.shared.domain.nav

import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.serialization.Serializable


@Serializable
sealed class OnBoarding : Parcelable {

    data object Welcome : OnBoarding()

    data object NotificationPermission : OnBoarding()

    data object Ready : OnBoarding()

    data object ChatWithYourTasks : OnBoarding()

    data object ReviewReminder : OnBoarding()

    data object Review : OnBoarding()
}

