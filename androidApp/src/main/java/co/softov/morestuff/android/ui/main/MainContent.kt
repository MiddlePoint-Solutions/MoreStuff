package co.softov.morestuff.android.ui.main

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import co.softov.morestuff.android.app.util.LifecycleEventsObserver
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.domain.nav.Screen.Home
import co.softov.morestuff.android.domain.nav.Screen.Review
import co.softov.morestuff.android.domain.nav.Screen.Settings
import co.softov.morestuff.android.domain.nav.Screen.Share
import co.softov.morestuff.android.domain.nav.Screen.TaskChat
import co.softov.morestuff.android.domain.nav.Shareable
import co.softov.morestuff.android.ui.chat.task.TaskChatScreen
import co.softov.morestuff.android.ui.home.HomeScreen
import co.softov.morestuff.android.ui.review.ReviewContent
import co.softov.morestuff.android.ui.review.ReviewScreen
import co.softov.morestuff.android.ui.settings.SettingsScreen
import co.softov.morestuff.android.ui.share.ShareScreen
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.StackNavigationSource
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainContent(
    initialScreen: Screen?,
    navigation: StackNavigation<Screen>,
    viewModel: MainViewModel = koinViewModel(),
) {

    LifecycleEventsObserver(
        onResume = { viewModel.onResume() }
    )

    ChildStack(
        source = navigation,
        initialStack = { initialScreen?.let { listOf(Home, it) } ?: listOf(Home) },
        handleBackButton = true,
        animation = stackAnimation(fade() + scale()),
    ) { screen ->
        when (screen) {
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

val LocalComponentContext: ProvidableCompositionLocal<ComponentContext> =
    staticCompositionLocalOf { error("Root component context was not provided") }

@Composable
fun ProvideComponentContext(
    componentContext: ComponentContext,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalComponentContext provides componentContext, content = content)
}

@Composable
inline fun <reified C : Parcelable> ChildStack(
    source: StackNavigationSource<C>,
    noinline initialStack: () -> List<C>,
    modifier: Modifier = Modifier,
    handleBackButton: Boolean = false,
    animation: StackAnimation<C, ComponentContext>? = null,
    noinline content: @Composable (C) -> Unit,
) {
    val componentContext = LocalComponentContext.current

    Children(
        stack = remember {
            componentContext.childStack(
                source = source,
                initialStack = initialStack,
                handleBackButton = handleBackButton,
                childFactory = { _, childComponentContext -> childComponentContext },
            )
        },
        modifier = modifier,
        animation = animation,
    ) { child ->
        ProvideComponentContext(child.instance) {
            content(child.configuration)
        }
    }
}
