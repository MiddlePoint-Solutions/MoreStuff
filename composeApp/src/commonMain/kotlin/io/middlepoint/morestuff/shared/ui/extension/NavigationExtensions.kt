package io.middlepoint.morestuff.shared.ui.extension

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

inline fun <reified T : Any> NavGraphBuilder.animatedComposable(
  typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
  deepLinks: List<NavDeepLink> = emptyList(),
  noinline enterTransition:
  (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
  EnterTransition?)? = {
    slideInHorizontally(
      spring(
        stiffness = Spring.StiffnessLow,
        visibilityThreshold = IntOffset.Companion.VisibilityThreshold
      )
    ) { it / 2 } + fadeIn(spring(stiffness = Spring.StiffnessLow))
  },
  noinline exitTransition:
  (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
  ExitTransition?)? = {
    slideOutHorizontally { -it } +
            fadeOut(spring(stiffness = Spring.StiffnessLow))
  },
  noinline popEnterTransition:
  (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
  EnterTransition?)? = { slideInHorizontally { -it / 2 } + fadeIn() },
  noinline popExitTransition:
  (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
  ExitTransition?)? = { slideOutHorizontally { it / 2 } + fadeOut() },
  noinline sizeTransform:
  (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
  SizeTransform?)? =
    null,
  noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
  composable(
    T::class,
    typeMap,
    deepLinks,
    enterTransition,
    exitTransition,
    popEnterTransition,
    popExitTransition,
    sizeTransform,
    content
  )
}