package io.middlepoint.morestuff.shared.ui.screen.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.selectNext
import io.github.xxfast.decompose.router.pages.RoutedContent
import io.github.xxfast.decompose.router.pages.rememberRouter
import io.middlepoint.morestuff.shared.domain.nav.OnBoarding.ChatWithYourTasks
import io.middlepoint.morestuff.shared.domain.nav.OnBoarding.NotificationPermission
import io.middlepoint.morestuff.shared.domain.nav.OnBoarding.Ready
import io.middlepoint.morestuff.shared.domain.nav.OnBoarding.SignIn
import io.middlepoint.morestuff.shared.domain.nav.OnBoarding.SignInEmail
import io.middlepoint.morestuff.shared.domain.nav.OnBoarding.Welcome
import io.middlepoint.morestuff.shared.requiresNotificationsPermission


@Composable
fun OnBoardingScreen(
  onBoardingComplete: () -> Unit,
) {

  val navigation = rememberRouter {
    Pages(
      items = buildList {
        add(Welcome)
        add(ChatWithYourTasks)
        if (requiresNotificationsPermission()) {
          add(NotificationPermission)
        }
//        add(SignIn)
//        add(SignInEmail)
        add(Ready)
      },
      selectedIndex = 0
    )
  }

  RoutedContent(
    router = navigation,
    modifier = Modifier.fillMaxSize(),
    pager = { modifier, state, key, pageContent ->
      HorizontalPager(
        modifier = modifier,
        state = state,
        key = key,
        userScrollEnabled = false,
        pageContent = pageContent,
      )
    },
  ) { screen ->
    when (screen) {
      Welcome -> OnBoardingWelcomeScreen(onNext = navigation::selectNext)
      SignIn -> OnBoardingSignInScreen(
        onNext = navigation::selectNext,
        onSignInWithEmail = { navigation.selectNext() }
      )

      SignInEmail -> OnBoardingSignInEmailScreen(
        onNext = navigation::selectNext
      )

      NotificationPermission -> OnBoardingNotificationPermissionScreen(onNext = navigation::selectNext)
      ChatWithYourTasks -> OnBoardingTaskChatScreen(onNext = navigation::selectNext)
      Ready -> OnBoardingCompleteScreen(onFinish = onBoardingComplete)
    }
  }
}
