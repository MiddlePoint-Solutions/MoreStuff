package io.middlepoint.morestuff.shared.domain.nav

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.Serializable

//@Serializable
sealed class OnBoarding() : Parcelable {

  @Parcelize
  data object Welcome : OnBoarding()

  //    data object NotificationPermission : OnBoarding()
  @Parcelize
  data object Ready : OnBoarding()

//    data object ChatWithYourTasks : OnBoarding()

  //    data object ReviewReminder : OnBoarding()
  @Parcelize
  data object Review : OnBoarding()
}

