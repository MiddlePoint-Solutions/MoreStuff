package io.middlepoint.morestuff.shared.domain.nav

import kotlinx.serialization.Serializable

@Serializable
sealed class OnBoarding() {

  @Serializable
  data object Welcome : OnBoarding()

  @Serializable
  data object NotificationPermission : OnBoarding()
  data object Ready : OnBoarding()

  data object ChatWithYourTasks : OnBoarding()

  //    data object ReviewReminder : OnBoarding()
  @Serializable
  data object Review : OnBoarding()
}

