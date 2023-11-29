package co.softov.morestuff.android.ui.main

import android.net.Uri
import androidx.compose.runtime.Composable
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.domain.nav.Screen.AboutLibraries
import co.softov.morestuff.android.domain.nav.Screen.Home
import co.softov.morestuff.android.domain.nav.Screen.OnBoarding
import co.softov.morestuff.android.domain.nav.Screen.Review
import co.softov.morestuff.android.domain.nav.Screen.Settings
import co.softov.morestuff.android.domain.nav.Screen.Share
import co.softov.morestuff.android.domain.nav.Screen.TaskChat
import co.softov.morestuff.android.domain.nav.Shareable
import co.softov.morestuff.android.ui.chat.task.TaskChatScreen
import co.softov.morestuff.android.ui.home.HomeScreen
import co.softov.morestuff.android.ui.image.ImageImportScreen
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.navigation.ChildStack
import co.softov.morestuff.android.ui.onboarding.OnBoardingScreen
import co.softov.morestuff.android.ui.review.ReviewScreen
import co.softov.morestuff.android.ui.scope.CreateScopesScreen
import co.softov.morestuff.android.ui.settings.AboutLibrariesScreen
import co.softov.morestuff.android.ui.settings.SettingsScreen
import co.softov.morestuff.android.ui.share.ShareScreen
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.router.stack.replaceCurrent
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainContent(
    initialScreen: Screen?,
    shareContent: (taskId: Long, content: Shareable) -> Unit,
) {
    val viewModel: MainViewModel = koinViewModel()
    val navigation = LocalAppNavigation.current

    ChildStack(
        source = navigation,
        initialStack = {
            when (initialScreen) {
                OnBoarding -> listOf(OnBoarding)
                null -> listOf(Home)
                else -> listOf(Home, initialScreen)
            }
        },
        handleBackButton = true,
        animation = stackAnimation(slide() + fade()),
    ) { screen ->
        when (screen) {

            OnBoarding -> OnBoardingScreen(
                onBoardingComplete = {
                    navigation.replaceCurrent(Home)
                    viewModel.onBoardingCompleted()
                    viewModel.sendHintTask()
                }
            )

            Home -> HomeScreen()
            Review -> ReviewScreen()
            Settings -> SettingsScreen()
            Screen.CreateScope -> CreateScopesScreen(onBack = navigation::pop)

            is TaskChat -> TaskChatScreen(
                taskId = screen.taskId,
                onBack = navigation::pop
            )

            is Screen.ImagePreview -> {
                ImageImportScreen(
                    imageUri = screen.imageUri,
                    onImport = { message ->
                        val shareableImage = Shareable.Image(screen.imageUri.toString(), message)
                        shareContent(screen.taskId, shareableImage)
                        navigation.replaceAll(Home, TaskChat(screen.taskId))
                    },
                    onBack = navigation::pop

                )

            }

            is Share -> {
                ShareScreen(
                    onBack = navigation::pop,
                    shareable = screen.shareable,
                ) { taskId, shareable ->
                    if (shareable is Shareable.Image && !viewModel.creatingNewTask.value!!) {
                        navigation.push(Screen.ImagePreview(Uri.parse(shareable.uris), taskId))

                    } else {
                        navigation.replaceCurrent(
                            TaskChat(taskId),
                            onComplete = {
                                shareContent(taskId, shareable)
                            }
                        )
                    }
                }
            }

            is AboutLibraries -> AboutLibrariesScreen()
        }
    }
}