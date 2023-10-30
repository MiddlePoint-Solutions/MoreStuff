package co.softov.morestuff.android.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.ui.model.ReviewItemUiModel
import co.softov.morestuff.android.ui.review.TaskCard
import co.softov.morestuff.android.ui.review.swipeable.ExperimentalSwipeableCardApi
import co.softov.morestuff.android.ui.review.swipeable.SwipeDirection
import co.softov.morestuff.android.ui.review.swipeable.SwipeableCardState
import co.softov.morestuff.android.ui.review.swipeable.rememberSwipeableCardState
import co.softov.morestuff.android.ui.review.swipeable.swipableCard
import co.softov.morestuff.android.ui.theme.surfaceContainer
import kotlinx.coroutines.delay


@Composable
fun ReviewCardsOnBoarding(tasks: List<ReviewItemUiModel>) {

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {

        val states = tasks.map { it to rememberSwipeableCardState() }

        Box(
            modifier = Modifier
                .size(width = 450.dp, height = 470.dp),
            contentAlignment = Alignment.Center
        ) {
            OnBoardingTaskPrioritySwipe(
                modifier = Modifier
                    .offset(y = 32.dp)
                    .fillMaxHeight(1f),
                states = states,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalSwipeableCardApi::class)
private fun OnBoardingTaskPrioritySwipe(
    modifier: Modifier = Modifier,
    states: List<Pair<ReviewItemUiModel, SwipeableCardState>>,
) {
    Box(modifier = modifier.padding(5.dp),contentAlignment = Alignment.Center) {
        var cardAnimated by remember { mutableStateOf(states.size - 1) }

        for ((index, pair) in states.reversed().withIndex()) {
            val (task, state) = pair

            TaskCard(
                modifier = Modifier
                    .layoutId(task.id)
                    .swipableCard(state = state),
                task = task,
                onComplete = {},
                isVisible = true,
                isClickable = false,
                showTaskChat = {}
            )

            if (index == cardAnimated && state.swipedDirection == null) {
                LaunchedEffect(cardAnimated) {
                    delay(1000)
                    val direction = SwipeDirection(index)
                    val partialOffset = SwipeOffset(direction, state)
                    state.offset.animateTo(partialOffset)
                    state.swipe(direction)
                    cardAnimated--
                }
            }

        }
    }
}


private fun SwipeDirection(position: Int): SwipeDirection {
    return when (position % 4) {
        0 -> SwipeDirection.Down
        1 -> SwipeDirection.Up
        2 -> SwipeDirection.Left
        3 -> SwipeDirection.Right
        else -> SwipeDirection.None
    }
}

private fun SwipeOffset(direction: SwipeDirection, state: SwipeableCardState): Offset {
    val fraction = 0.10f
    return when (direction) {
        SwipeDirection.Left -> Offset(-state.maxWidth * fraction, 0f)
        SwipeDirection.Right -> Offset(state.maxWidth * fraction, 0f)
        SwipeDirection.Up -> Offset(0f, -state.maxHeight * fraction)
        SwipeDirection.Down -> Offset(0f, state.maxHeight * fraction)
        SwipeDirection.None -> Offset(0f, 0f)
    }
}
