package io.middlepoint.morestuff.shared.ui.screen.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.selectNext
import io.github.xxfast.decompose.router.pages.RoutedContent
import io.github.xxfast.decompose.router.pages.rememberRouter
import io.middlepoint.morestuff.shared.domain.nav.OnBoarding
import io.middlepoint.morestuff.shared.domain.nav.OnBoarding.*
import io.middlepoint.morestuff.shared.requiresNotificationsPermission


@OptIn(ExperimentalDecomposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(
  onBoardingComplete: () -> Unit,
//    viewModel: SettingsViewModel = koinViewModel()
) {

  val navigation = rememberRouter(OnBoarding::class) {
    Pages(
      items = buildList {
        add(Welcome)
        add(ChatWithYourTasks)
        add(Review)
        if (requiresNotificationsPermission()) {
          add(NotificationPermission)
        }
//            add(ReviewReminder)
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
      NotificationPermission -> OnBoardingNotificationPermissionScreen(onNext = navigation::selectNext)
//            ReviewReminder -> {
//                val reviewTime by viewModel.models.collectAsState()
//                OnBoardingReviewNotificationScreen(
//                    onNext = navigation::selectNext,
//                    currentReviewTime = { reviewTime.reviewTime },
//                    onReviewTimeChange = { viewModel.take(SettingsEvent.SetReviewTime(it.first, it.second)) }
//                )
//            }
      ChatWithYourTasks -> OnBoardingTaskChatScreen(onNext = navigation::selectNext)
      Review -> OnBoardingReviewScreen(onNext = navigation::selectNext)
      Ready -> OnBoardingCompleteScreen(onFinish = onBoardingComplete)
    }
  }
}
