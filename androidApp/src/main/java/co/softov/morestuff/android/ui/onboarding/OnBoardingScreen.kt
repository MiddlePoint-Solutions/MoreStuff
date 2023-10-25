package co.softov.morestuff.android.ui.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.nav.OnBoarding
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
            add(OnBoarding.Welcome)
            if (requiresNotificationsPermission()) {
                add(OnBoarding.NotificationPermission)
            }
            add(OnBoarding.DailyTaskReview)
            add(OnBoarding.ChatWithYourTask)
            add(OnBoarding.TaskReview)
            add(OnBoarding.WorkSpaceReady)
        }
    }

    ChildPages(
        source = navigation,
        modifier = Modifier.fillMaxSize(),
        initialPages = { Pages(items = pages, selectedIndex = 0) }
    ) { screen ->
        when (screen) {
            is OnBoarding.Welcome -> {
                OnBoardingWelcomeScreen(
                    onNext = {
                        if (requiresNotificationsPermission()) {
                            navigation.selectNext()
                        } else {
                            onBoardingComplete()
                        }
                    }
                )
            }

            OnBoarding.NotificationPermission -> OnBoardingNotificationPermissionScreen(onNext = navigation::selectNext)
            OnBoarding.DailyTaskReview -> {
                OnBoardingReviewNotificationScreen(onNext = navigation::selectNext)
            }

            OnBoarding.ChatWithYourTask -> OnBoardingTaskChatScreen(onNext = navigation::selectNext)
            OnBoarding.TaskReview -> OnBoardingReviewScreen(onNext = navigation::selectNext)
            OnBoarding.WorkSpaceReady -> OnBoardingCompleteScreen(onFinish = onBoardingComplete)

        }
    }
}
