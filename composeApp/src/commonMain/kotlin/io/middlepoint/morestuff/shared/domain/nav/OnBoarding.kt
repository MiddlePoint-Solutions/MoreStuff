package io.middlepoint.morestuff.shared.domain.nav

import kotlinx.serialization.Serializable

@Serializable
sealed class OnBoarding() {

  @Serializable
  data object Welcome : OnBoarding()

  @Serializable
  data object NotificationPermission : OnBoarding()
  data object Ready : OnBoarding()

  @Serializable
  data object ChatWithYourTasks : OnBoarding()

  @Serializable
  data object Review : OnBoarding()

  @Serializable
  data object SignIn: OnBoarding()

  @Serializable
  data object SignInEmail: OnBoarding()
}

