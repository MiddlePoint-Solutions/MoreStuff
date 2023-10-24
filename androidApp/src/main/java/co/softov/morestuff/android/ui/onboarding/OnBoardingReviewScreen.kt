package co.softov.morestuff.android.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
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
fun OnBoardingReviewScreen(
    onNext: () -> Unit,
) {
    val tasks = listOf(
        ReviewItemUiModel(
            id = 1,
            createTime = "10:30 AM",
            title = stringResource(R.string.right_priority),
            priorityScore = 5,
            isCompleted = false,
        ),
        ReviewItemUiModel(
            id = 2,
            createTime = "11:00 AM",
            title = stringResource(R.string.left_priority),
            priorityScore = 3,
            isCompleted = false
        ),
        ReviewItemUiModel(
            id = 3,
            createTime = "11:30 AM",
            title = stringResource(R.string.up_priority),
            priorityScore = 7,
            isCompleted = false
        ),
        ReviewItemUiModel(
            id = 4,
            createTime = "12:30 AM",
            title = stringResource(R.string.down_priority),
            priorityScore = -1,
            isCompleted = false
        )
    )
    var reloadCards by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(9000)
            reloadCards++
        }

    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.review_your_tasks),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            key(reloadCards) {
                ReviewTasks(tasks)
            }
        }

        Row(
            Modifier
                .padding(bottom = 56.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            Column {
                Button(
                    onClick = { reloadCards++ },
                    modifier = Modifier
                        .width(187.dp)
                        .height(43.dp)
                        .padding(bottom = 6.dp),
                    content = {
                        Text(
                            text = stringResource(R.string.reload),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }
                )
                Button(
                    onClick = { onNext() },
                    modifier = Modifier
                        .width(187.dp)
                        .height(43.dp),
                    content = {
                        Text(
                            text = stringResource(R.string.button_next),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }
                )

            }
        }
    }
}

@Composable
private fun ReviewTasks(tasks: List<ReviewItemUiModel>) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {

        val states = tasks.map { it to rememberSwipeableCardState() }

        Box(
            modifier = Modifier
                .size(400.dp),
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
    Box(modifier = modifier.padding(20.dp)) {
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
                    val direction = getSwipeDirection(index)
                    val partialOffset = createSwipeOffset(direction, state)
                    state.offset.animateTo(partialOffset)
                    state.swipe(direction)
                    cardAnimated--
                }
            }

        }
    }
}

private fun getSwipeDirection(position: Int): SwipeDirection {
    return when (position % 4) {
        0 -> SwipeDirection.Down
        1 -> SwipeDirection.Up
        2 -> SwipeDirection.Left
        3 -> SwipeDirection.Right
        else -> SwipeDirection.None
    }
}

private fun createSwipeOffset(direction: SwipeDirection, state: SwipeableCardState): Offset {
    val fraction = 0.10f
    return when (direction) {
        SwipeDirection.Left -> Offset(-state.maxWidth * fraction, 0f)
        SwipeDirection.Right -> Offset(state.maxWidth * fraction, 0f)
        SwipeDirection.Up -> Offset(0f, -state.maxHeight * fraction)
        SwipeDirection.Down -> Offset(0f, state.maxHeight * fraction)
        SwipeDirection.None -> Offset(0f, 0f)
    }
}