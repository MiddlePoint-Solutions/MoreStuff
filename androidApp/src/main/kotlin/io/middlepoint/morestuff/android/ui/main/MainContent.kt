package io.middlepoint.morestuff.android.ui.main

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.router.stack.replaceCurrent
import io.middlepoint.morestuff.android.ui.chat.task.TaskChatScreen
import io.middlepoint.morestuff.android.ui.home.HomeScreen
import io.middlepoint.morestuff.android.ui.image.ImageImportScreen
import io.middlepoint.morestuff.android.ui.local.LocalAppNavigation
import io.middlepoint.morestuff.android.ui.navigation.ChildStack
import io.middlepoint.morestuff.android.ui.onboarding.OnBoardingScreen
import io.middlepoint.morestuff.android.ui.review.ReviewScreen
import io.middlepoint.morestuff.android.ui.scopes.CreateScopeScreen
import io.middlepoint.morestuff.android.ui.scopes.ScopesScreen
import io.middlepoint.morestuff.android.ui.settings.SettingsScreen
import io.middlepoint.morestuff.android.ui.share.ShareScreen
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.middlepoint.morestuff.shared.domain.nav.Screen.CreateScope
import io.middlepoint.morestuff.shared.domain.nav.Screen.Home
import io.middlepoint.morestuff.shared.domain.nav.Screen.ImagePreview
import io.middlepoint.morestuff.shared.domain.nav.Screen.OnBoarding
import io.middlepoint.morestuff.shared.domain.nav.Screen.Review
import io.middlepoint.morestuff.shared.domain.nav.Screen.Scopes
import io.middlepoint.morestuff.shared.domain.nav.Screen.Settings
import io.middlepoint.morestuff.shared.domain.nav.Screen.Share
import io.middlepoint.morestuff.shared.domain.nav.Screen.TaskChat

@Composable
fun MainContent(
    initialScreen: Screen?,
    shareContent: (taskId: Long, content: Shareable) -> Unit,
    onBoardingComplete: () -> Unit,
) {
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
        key = "MainChildStack",
        handleBackButton = true,
        animation = stackAnimation(slide() + fade()),
    ) { screen ->
        when (screen) {

            OnBoarding -> OnBoardingScreen(
                onBoardingComplete = {
                    onBoardingComplete()
                    navigation.replaceCurrent(Home)
                }
            )

            Home -> HomeScreen()

            is Review -> ReviewScreen(currentScopeId = screen.scopeId)

            Settings -> SettingsScreen(onBack = navigation::pop)

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
                    imageUri = Uri.parse(screen.imageUri),
                    onImport = { message ->
                        val shareableImage = Shareable.Image(screen.imageUri, message)
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
                        navigation.push(ImagePreview(shareable.uris, taskId))
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