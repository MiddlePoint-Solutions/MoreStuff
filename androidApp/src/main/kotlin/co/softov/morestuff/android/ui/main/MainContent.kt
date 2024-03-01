package co.softov.morestuff.android.ui.main

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.domain.model.Shareable
import co.softov.morestuff.android.domain.nav.ScopeScreen
import co.softov.morestuff.android.domain.nav.Screen.*
import co.softov.morestuff.android.ui.chat.task.TaskChatScreen
import co.softov.morestuff.android.ui.home.HomeScreen
import co.softov.morestuff.android.ui.image.ImageImportScreen
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.navigation.ChildStack
import co.softov.morestuff.android.ui.onboarding.OnBoardingScreen
import co.softov.morestuff.android.ui.review.ReviewScreen
import co.softov.morestuff.android.ui.scopes.CreateScopeScreen
import co.softov.morestuff.android.ui.scopes.ScopesScreen
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
        modifier = Modifier.fillMaxSize(),
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

            is Review -> ReviewScreen(currentScopeId = screen.scopeId)

            Settings -> SettingsScreen()

            Scopes -> ScopesScreen(onBack = navigation::pop)

            is CreateScope -> CreateScopeScreen(
                onBack = navigation::pop,
                onSaveScope = screen.onSave
            )

            is TaskChat -> TaskChatScreen(
                taskId = screen.taskId,
                onBack = navigation::pop
            )

            is ImagePreview -> {
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
                    if (shareable is Shareable.Image) {
                        navigation.push(ImagePreview(Uri.parse(shareable.uris), taskId))
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
        }
    }
}