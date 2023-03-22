@file:OptIn(ExperimentalSwipeableCardApi::class)

package co.softov.morestuff.android.ui.review

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.app.util.rememberRandomColor
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel

import co.softov.morestuff.android.ui.review.swipeable.Direction
import co.softov.morestuff.android.ui.review.swipeable.ExperimentalSwipeableCardApi
import co.softov.morestuff.android.ui.review.swipeable.rememberSwipeableCardState
import co.softov.morestuff.android.ui.review.swipeable.swipableCard
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import timber.log.Timber

@Composable
@OptIn(ExperimentalSwipeableCardApi::class)
fun ReviewContent(
    modifier: Modifier = Modifier,
    viewModel: ReviewViewModel = getViewModel()
) {

    val model by viewModel.model.collectAsState()
    val scope = rememberCoroutineScope()

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
        Box {
            val states = model.items.map { it to rememberSwipeableCardState() }
            var hint by remember {
                mutableStateOf("Swipe a card or press a button below")
            }

            Hint(hint)

            Box(
                modifier
                    .padding(24.dp)
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .align(Alignment.Center)
            ) {
                states.forEach { (schedule, state) ->
                    if (state.swipedDirection == null) {
                        TaskCard(
                            modifier = modifier
                                .layoutId(schedule.taskId)
                                .fillMaxSize()
                                .swipableCard(
                                    state = state,
                                    blockedDirections = listOf(),
                                    onSwiped = {
                                        // swipes are handled by the LaunchedEffect
                                        // so that we track button clicks & swipes
                                        // from the same place
                                    },
                                    onSwipeCancel = {
                                        hint = "You canceled the swipe"
                                    }
                                ),
                            schedule = schedule
                        )
                    }
                    LaunchedEffect(schedule, state.swipedDirection) {
                        if (state.swipedDirection != null) {
                            hint = "You swiped ${stringFrom(state.swipedDirection!!)}"
                        }
                    }
                }
            }
            Column(
                Modifier
                    .align(Alignment.BottomCenter)
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