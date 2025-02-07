package io.middlepoint.morestuff.shared.ui.screen.onboarding

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.android.ui.review.swipeable.ExperimentalSwipeableCardApi
import io.middlepoint.morestuff.shared.ui.components.swipeable.SwipeDirection
import io.middlepoint.morestuff.shared.ui.components.swipeable.SwipeableCardState
import io.middlepoint.morestuff.shared.ui.components.swipeable.rememberSwipeableCardState
import io.middlepoint.morestuff.shared.ui.components.TaskCard
import io.middlepoint.morestuff.shared.ui.components.swipeable.swipableCard
import io.middlepoint.morestuff.shared.ui.model.ReviewItemUiModel
import kotlinx.coroutines.delay
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.onboarding_review_all_done
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun OnBoardingReviewCards(
  tasks: List<ReviewItemUiModel>,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
  ) {

    val states = tasks.map { it to rememberSwipeableCardState() }

    Column(
      modifier = Modifier.align(Alignment.Center),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = Icons.Default.DoneAll,
        contentDescription = "",
        modifier = Modifier.size(40.dp),
        tint = MaterialTheme.colorScheme.secondary
      )

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = stringResource(Res.string.onboarding_review_all_done),
        style = MaterialTheme.typography.headlineMedium.copy(
          fontSize = 38.sp
        ),
        color = MaterialTheme.colorScheme.secondary
      )
    }

    OnBoardingTaskPrioritySwipe(
      modifier = Modifier.align(Alignment.Center),
      states = states,
    )
  }
}

@Composable
@OptIn(ExperimentalSwipeableCardApi::class)
private fun OnBoardingTaskPrioritySwipe(
  modifier: Modifier = Modifier,
  states: List<Pair<ReviewItemUiModel, SwipeableCardState>>,
) {
  Box(modifier = modifier.padding(20.dp)) {
    var cardAnimated by remember { mutableIntStateOf(states.size - 1) }

    for ((index, pair) in states.reversed().withIndex()) {
      val (task, state) = pair

      TaskCard(
        modifier = Modifier
          .layoutId(task.id)
          .swipableCard(
            state = state,
            enabled = false
          ),
        item = task,
        isClickable = false,
      )

      if (index == cardAnimated && state.swipedDirection == null) {
        LaunchedEffect(cardAnimated) {
          delay(1500)
          val direction = getSwipeDirection(index)
          val partialOffset = createSwipeOffset(direction, state)
          state.offset.animateTo(partialOffset)
          state.swipe(direction, animationSpec = tween(700))
          cardAnimated--
        }
      }

    }
  }
}

private fun getSwipeDirection(position: Int): SwipeDirection {
  return when (position % 4) {
    0 -> SwipeDirection.Left
    1 -> SwipeDirection.Right
    2 -> SwipeDirection.Down
    3 -> SwipeDirection.Up
    else -> SwipeDirection.None
  }
}

private fun createSwipeOffset(direction: SwipeDirection, state: SwipeableCardState): Offset {
  val fraction = 0.05f
  return when (direction) {
    SwipeDirection.Left -> Offset(-state.maxWidth * fraction, 0f)
    SwipeDirection.Right -> Offset(state.maxWidth * fraction, 0f)
    SwipeDirection.Up -> Offset(0f, -state.maxHeight * fraction)
    SwipeDirection.Down -> Offset(0f, state.maxHeight * fraction)
    SwipeDirection.None -> Offset(0f, 0f)
  }
}