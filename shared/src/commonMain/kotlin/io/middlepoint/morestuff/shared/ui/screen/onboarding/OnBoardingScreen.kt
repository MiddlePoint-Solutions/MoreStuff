package io.middlepoint.morestuff.shared.ui.screen.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.selectNext
import io.middlepoint.morestuff.shared.domain.nav.OnBoarding
import io.middlepoint.morestuff.shared.domain.nav.OnBoarding.Ready
import io.middlepoint.morestuff.shared.domain.nav.OnBoarding.Review
import io.middlepoint.morestuff.shared.domain.nav.OnBoarding.Welcome
import io.middlepoint.morestuff.shared.navigation.ChildPages


@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun OnBoardingScreen(
    onBoardingComplete: () -> Unit,
//    viewModel: SettingsViewModel = koinViewModel()
) {

    val navigation = remember { PagesNavigation<OnBoarding>() }
    val pages = remember {
        buildList {
            add(Welcome)
//            add(ChatWithYourTasks)
            add(Review)
//            if (requiresNotificationsPermission()) {
//                add(NotificationPermission)
//            }
//            add(ReviewReminder)
            add(Ready)
        }
    }

    ChildPages(
        source = navigation,
        serializer = null,
        modifier = Modifier.fillMaxSize(),
        initialPages = { Pages(items = pages, selectedIndex = 0) }
    ) { screen ->
        when (screen) {
            Welcome -> OnBoardingWelcomeScreen(onNext = navigation::selectNext)
//            NotificationPermission -> OnBoardingNotificationPermissionScreen(onNext = navigation::selectNext)
//            ReviewReminder -> {
//                val reviewTime by viewModel.models.collectAsState()
//                OnBoardingReviewNotificationScreen(
//                    onNext = navigation::selectNext,
//                    currentReviewTime = { reviewTime.reviewTime },
//                    onReviewTimeChange = { viewModel.take(SettingsEvent.SetReviewTime(it.first, it.second)) }
//                )
//            }
//            ChatWithYourTasks -> OnBoardingTaskChatScreen(onNext = navigation::selectNext)
            Review -> OnBoardingReviewScreen(onNext = navigation::selectNext)
            Ready -> OnBoardingCompleteScreen(onFinish = onBoardingComplete)
        }
    }
}
