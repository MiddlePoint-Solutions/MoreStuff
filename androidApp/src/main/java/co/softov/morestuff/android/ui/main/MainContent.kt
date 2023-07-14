package co.softov.morestuff.android.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import co.softov.morestuff.android.app.util.LifecycleEventsObserver
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.domain.nav.Screen.Home
import co.softov.morestuff.android.domain.nav.Screen.OnBoarding
import co.softov.morestuff.android.domain.nav.Screen.Review
import co.softov.morestuff.android.domain.nav.Screen.Settings
import co.softov.morestuff.android.domain.nav.Screen.Share
import co.softov.morestuff.android.domain.nav.Screen.TaskChat
import co.softov.morestuff.android.ui.chat.task.TaskChatScreen
import co.softov.morestuff.android.ui.home.HomeScreen
import co.softov.morestuff.android.ui.navigation.ChildStack
import co.softov.morestuff.android.ui.onboarding.OnBoardingContent
import co.softov.morestuff.android.ui.review.ReviewScreen
import co.softov.morestuff.android.ui.settings.SettingsScreen
import co.softov.morestuff.android.ui.share.ShareScreen
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainContent(
    initialScreen: Screen?,
    navigation: StackNavigation<Screen>,
    viewModel: MainViewModel = koinViewModel(),
) {

    val scope = rememberCoroutineScope()

    LifecycleEventsObserver(
        onResume = { viewModel.onResume() }
    )

    val model = viewModel.model

    ChildStack(
        source = navigation,
        initialStack = {
            when {
                model.showOnBoarding -> listOf(OnBoarding)
                else -> initialScreen?.let { listOf(Home, it) } ?: listOf(Home)
            }
        },
        handleBackButton = true,
        animation = stackAnimation(slide() + fade()),
    ) { screen ->
        when (screen) {

            OnBoarding -> OnBoardingContent(
                onBoardingComplete = {
                    scope.launch { navigation.replaceCurrent(Home) }
                }
            )

            Home -> HomeScreen(
                showSettings = { navigation.push(Settings) },
                showReview = { navigation.push(Review) },
                showTaskChat = { navigation.push(TaskChat(it)) }
            )

            Review -> ReviewScreen(onBack = navigation::pop)
            Settings -> SettingsScreen(onBack = navigation::pop)

            is TaskChat -> TaskChatScreen(
                taskId = screen.taskId,
                onBack = navigation::pop
            )

            is Share -> ShareScreen(
                onBack = navigation::pop,
                shareable = screen.shareable,
                content = screen.content,
                shareToExistingTask = { taskId ->
                    navigation.replaceCurrent(
                        TaskChat(taskId),
                        onComplete = { viewModel.shareTextToTask(taskId, screen.content) }
                    )
                },
            )
        }
    }
}