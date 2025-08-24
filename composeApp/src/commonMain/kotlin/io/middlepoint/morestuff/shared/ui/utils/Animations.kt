package io.middlepoint.morestuff.shared.ui.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntOffset

@Stable
fun defaultEnterTransition(
  animationSpec: FiniteAnimationSpec<IntOffset> = tween(),
  initialOffsetX: (fullWidth: Int) -> Int = { it / 2 },
): EnterTransition =
  slideIn(
    initialOffset = { IntOffset(initialOffsetX(it.width), 0) },
    animationSpec = animationSpec
  ) + fadeIn(
    animationSpec = tween(300)
  )

@Stable
fun defaultExitTransition(
  animationSpec: FiniteAnimationSpec<IntOffset> = tween(100),
  targetOffsetX: (fullWidth: Int) -> Int = { it / 2 },
): ExitTransition =
  slideOut(
    targetOffset = { IntOffset(targetOffsetX(0), 0) },
    animationSpec = animationSpec
  ) + fadeOut(
    animationSpec = tween(100)
  )