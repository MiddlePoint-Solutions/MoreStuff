package io.middlepoint.morestuff.android.ui.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.middlepoint.morestuff.shared.domain.nav.OnBoarding
import io.middlepoint.morestuff.shared.domain.nav.OnBoarding.*
import io.middlepoint.morestuff.android.ui.navigation.ChildPages
import io.middlepoint.morestuff.android.ui.settings.SettingsEvent
import io.middlepoint.morestuff.android.ui.settings.SettingsViewModel
import io.middlepoint.morestuff.android.ui.utils.requiresNotificationsPermission
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.selectNext
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun OnBoardingScreen(
    onBoardingComplete: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {

    val navigation = remember { PagesNavigation<OnBoarding>() }
    val pages = remember {
        buildList {
            add(Welcome)
            add(ChatWithYourTasks)
            add(Review)
            if (requiresNotificationsPermission()) {
                add(NotificationPermission)
            }
            add(ReviewReminder)
            add(Ready)
        }
    }

    ChildPages(
        source = navigation,
        modifier = Modifier.fillMaxSize(),
        initialPages = { Pages(items = pages, selectedIndex = 0) }
    ) { screen ->
        when (screen) {
            Welcome -> OnBoardingWelcomeScreen(onNext = navigation::selectNext)
            NotificationPermission -> OnBoardingNotificationPermissionScreen(onNext = navigation::selectNext)
            ReviewReminder -> {
                val reviewTime by viewModel.models.collectAsState()
                OnBoardingReviewNotificationScreen(
                    onNext = navigation::selectNext,
                    currentReviewTime = { reviewTime.reviewTime },
                    onReviewTimeChange = { viewModel.take(SettingsEvent.SetReviewTime(it.first, it.second)) }
                )
            }
            ChatWithYourTasks -> OnBoardingTaskChatScreen(onNext = navigation::selectNext)
            Review -> OnBoardingReviewScreen(onNext = navigation::selectNext)
            Ready -> OnBoardingCompleteScreen(onFinish = onBoardingComplete)
        }
    }
}
