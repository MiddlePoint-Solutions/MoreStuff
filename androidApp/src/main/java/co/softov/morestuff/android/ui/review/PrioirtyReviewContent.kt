@file:OptIn(ExperimentalSwipeableCardApi::class)

package co.softov.morestuff.android.ui.review

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.app.util.rememberRandomColor
import co.softov.morestuff.android.presentation.presenter.PriorityRound
import co.softov.morestuff.android.ui.list.ScheduleListItem
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import co.softov.morestuff.android.ui.review.swipeable.*
import co.softov.morestuff.android.ui.review.swipeable.Direction
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import timber.log.Timber

@Composable
fun ReviewContent(
    modifier: Modifier = Modifier,
    viewModel: PriorityReviewViewModel = getViewModel()
) {

    val model by viewModel.model.collectAsState()
    val scope = rememberCoroutineScope()

    Surface {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xfff68084),
                            Color(0xffa6c0fe),
                        )
                    )
                )
                .systemBarsPadding()
        ) {
            Box(
                modifier = Modifier
            ) {

                var hint by remember {
                    mutableStateOf("Swipe a card or press a button below")
                }

                Hint(hint)

                val states = model.items.map { it to rememberSwipeableCardState(model.number) }
                val visibleState = remember(model.number) { MutableTransitionState(false) }
                val transition = updateTransition(visibleState, "Visible state")

                when (model.round) {
                    PriorityRound.Initial -> {
                        TaskPrioritySwipe(
                            modifier = modifier.align(Alignment.Center),
                            states = states,
                            onSwiped = viewModel::onTaskSwiped,
                        )
                    }
                    PriorityRound.Next -> {

                        val screenWidth = with(LocalDensity.current) {
                            LocalConfiguration.current.screenWidthDp.dp.toPx()
                        }

                        val xPosition by transition.animateFloat(label = "xPosition") {
                            if (it) 0f else screenWidth
                        }

                        Box(modifier = Modifier.graphicsLayer {
                            translationX = xPosition
                        }) {
                            TaskPrioritySwipe(
                                modifier = modifier
                                    .align(Alignment.Center)
                                    .layoutId("${model.round}-${model.number}"),
                                states = states,
                                onSwiped = viewModel::onTaskSwiped,
                            )
                        }

                        LaunchedEffect(key1 = model.number) {
                            visibleState.targetState = true
                        }
                    }
                    PriorityRound.Final -> {

                        val screenWidth = with(LocalDensity.current) {
                            LocalConfiguration.current.screenWidthDp.dp.toPx()
                        }

                        val xPosition by transition.animateFloat(label = "xPosition") {
                            if (it) 0f else screenWidth
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxSize()
                                .aspectRatio(1f)
                                .graphicsLayer {
                                    translationX = xPosition
                                }
                        ) {

                            when {
                                states.isEmpty() -> {
                                    Text(
                                        "Wooops, nothing to work on? add a new task",
                                        modifier.align(Alignment.Center)
                                    )
                                }
                                states.size == 1 -> {
                                    // TODO: disable swiping
                                    TaskPrioritySwipe(
                                        modifier = modifier,
                                        states = states,
                                        onSwiped = viewModel::onTaskSwiped,
                                    )
                                }
                                else -> {
                                    LazyColumn(
                                        contentPadding = PaddingValues(8.dp)
                                    ) {
                                        items(model.items) {
                                            ScheduleListItem(it)
                                        }
                                    }
                                }
                            }

                        }
                        LaunchedEffect(key1 = model.number) {
                            visibleState.targetState = true
                        }
                    }
                }


                Column(
                    Modifier.align(Alignment.BottomCenter)
                ) {

                    CircleButton(
                        onClick = {
                            scope.launch {
                                states.reversed().run {
                                    firstOrNull {
                                        it.second.offset.value == Offset(0f, 0f)
                                    }?.let { last ->
                                        val index = indexOf(last)
                                        Timber.d("Undo task: ${getOrNull(index - 1)?.first?.taskTitle}")
                                        getOrNull(index - 1)?.second?.undo()
                                    }
                                }
                            }
                        },
                        icon = Icons.Rounded.Undo
                    )

                    Row(
                        Modifier
                            .padding(horizontal = 24.dp, vertical = 32.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CircleButton(
                            onClick = {
                                scope.launch {
                                    val last = states.reversed()
                                        .firstOrNull {
                                            it.second.offset.value == Offset(0f, 0f)
                                        }?.second
                                    last?.swipe(Direction.Left)
                                }
                            },
                            icon = Icons.Rounded.ThumbDown
                        )
                        CircleButton(
                            onClick = {
                                scope.launch {
                                    val last = states.reversed()
                                        .firstOrNull {
                                            it.second.offset.value == Offset(0f, 0f)
                                        }?.second
                                    last?.swipe(Direction.Down)
                                }
                            },
                            icon = Icons.Rounded.Close
                        )
                        CircleButton(
                            onClick = {
                                scope.launch {
                                    val last = states.reversed()
                                        .firstOrNull {
                                            it.second.offset.value == Offset(0f, 0f)
                                        }?.second
                                    last?.swipe(Direction.Up)
                                }
                            },
                            icon = Icons.Rounded.Done
                        )
                        CircleButton(
                            onClick = {
                                scope.launch {
                                    val last = states.reversed()
                                        .firstOrNull {
                                            it.second.offset.value == Offset(0f, 0f)
                                        }?.second

                                    last?.swipe(Direction.Right)
                                }
                            },
                            icon = Icons.Rounded.ThumbUp
                        )
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalSwipeableCardApi::class)
private fun TaskPrioritySwipe(
    modifier: Modifier,
    states: List<Pair<ScheduleListItemViewModel, SwipeableCardState>>,
    onSwiped: (schedule: ScheduleListItemViewModel, direction: Direction, isLast: Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()

    // TODO: this could possibly be used to remove the current gesture
    //  https://stackoverflow.com/questions/73488235/jetpack-compose-detect-drag-gesture-and-detect-interaction-source
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsDraggedAsState()

    Box(
        modifier
            .padding(24.dp)
            .fillMaxSize()
            .aspectRatio(1f)
    ) {
        states.forEachIndexed { index, (schedule, state) ->
            if (state.swipedDirection == null) {
                TaskCard(
                    modifier = modifier
                        .layoutId(schedule.taskId)
                        .fillMaxSize()
                        .padding(top = (5 * index).dp)
                        .swipableCard(
                            state = state,
                            blockedDirections = listOf(),
                        )
                        .clickable { scope.launch { state.flip() } },
                    schedule = schedule
                )
            }
            LaunchedEffect(schedule, state.swipedDirection) {
                state.swipedDirection?.let {
                    onSwiped(schedule, it, states.first().first == schedule)
                }
            }
        }
    }
}

@Composable
private fun CircleButton(
    onClick: () -> Unit,
    icon: ImageVector,
) {
    IconButton(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .size(56.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
        onClick = onClick
    ) {
        Icon(
            icon, null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun TaskCard(
    modifier: Modifier,
    schedule: ScheduleListItemViewModel,
) {
    Card(
        modifier, elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            rememberRandomColor(),
                            rememberRandomColor(),
                        )
                    )
                )
        ) {
            Scrim(Modifier.align(Alignment.BottomCenter))
            Column(Modifier.align(Alignment.Center)) {
                Text(
                    text = schedule.taskTitle,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun Hint(text: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Debug(text: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}


private fun stringFrom(direction: Direction): String {
    return when (direction) {
        Direction.Left -> "Left 👈"
        Direction.Right -> "Right 👉"
        Direction.Up -> "Up 👆"
        Direction.Down -> "Down 👇"
    }
}

@Composable
fun Scrim(modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
            .height(180.dp)
            .fillMaxWidth()
    )
}