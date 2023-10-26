package co.softov.morestuff.android.ui.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import co.softov.morestuff.android.domain.nav.OnBoarding
import co.softov.morestuff.android.domain.nav.OnBoarding.*
import co.softov.morestuff.android.ui.navigation.ChildPages
import co.softov.morestuff.android.ui.utils.requiresNotificationsPermission
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.selectNext


@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun OnBoardingScreen(
    onBoardingComplete: () -> Unit,
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
            ReviewReminder -> OnBoardingReviewNotificationScreen(onNext = navigation::selectNext)
            ChatWithYourTasks -> OnBoardingTaskChatScreen(onNext = navigation::selectNext)
            Review -> OnBoardingReviewScreen(onNext = navigation::selectNext)
            Ready -> OnBoardingCompleteScreen(onFinish = onBoardingComplete)
        }
    }
}
