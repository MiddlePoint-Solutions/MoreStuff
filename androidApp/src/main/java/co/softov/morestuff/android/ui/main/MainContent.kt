package co.softov.morestuff.android.ui.main

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import co.softov.morestuff.android.nav.Screen
import co.softov.morestuff.android.nav.Screen.*
import co.softov.morestuff.android.ui.chat.task.TaskChatContent
import co.softov.morestuff.android.ui.home.HomeScreen
import co.softov.morestuff.android.ui.review.ReviewContent
import co.softov.morestuff.android.ui.settings.SettingsScreen
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
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push

@Composable
fun MainContent(
    initialScreen: Screen? = null
) {
    val navigation = remember { StackNavigation<Screen>() }
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
                showTaskChat = { taskId -> navigation.push(TaskChat(taskId)) }
            )

            Review -> ReviewContent(onBack = navigation::pop)
            Settings -> SettingsScreen(onBack = navigation::pop)

            is TaskChat -> TaskChatContent(
                taskId = screen.taskId,
                onBack = navigation::pop
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
