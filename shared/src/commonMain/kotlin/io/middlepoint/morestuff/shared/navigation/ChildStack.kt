package io.middlepoint.morestuff.shared.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack

@Composable
inline fun <reified C : Any> ChildStack(
  source: StackNavigation<C>,
  noinline initialStack: () -> List<C>,
  modifier: Modifier = Modifier,
  key: String = "DefaultChildStack",
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
        saveStack = { null }, // TODO!
        restoreStack = { null },
        key = key,
        handleBackButton = handleBackButton,
        childFactory = { _, childComponentContext -> childComponentContext }
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

