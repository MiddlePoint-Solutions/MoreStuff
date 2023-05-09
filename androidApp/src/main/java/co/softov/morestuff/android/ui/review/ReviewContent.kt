package co.softov.morestuff.android.ui.review

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.util.rememberRandomColor
import co.softov.morestuff.android.presentation.presenter.ReviewModel
import co.softov.morestuff.android.presentation.presenter.ReviewRound
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import co.softov.morestuff.android.ui.priority.PriorityButton
import co.softov.morestuff.android.ui.review.swipeable.*
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun ReviewContent(
    modifier: Modifier = Modifier,
    viewModel: ReviewViewModel = koinViewModel()
) {
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

            val model by viewModel.uiModel.collectAsState()



            Box {

                when (model.round) {
                    ReviewRound.Priority -> {

                        Column {
                            PriorityReviewTopBar(navigateUp = viewModel::navigateBack)
                            RoundInfo(model)
                        }

                        val states =
                            model.roundItems.map { it to rememberSwipeableCardState(model.roundNumber) }

                        val visibleState = remember(model.roundNumber) {
                            MutableTransitionState(false)
                        }

                        TaskPrioritySwipe(
                            modifier = modifier.align(Alignment.Center),
                            states = states,
                            onSwiped = { schedule, direction, isLast ->
                                viewModel.onTaskSwiped(schedule, direction, isLast)

//                                scope.launch {
//                                    if (isLast) {
//                                        delay(500)
//                                        visibleState.targetState = false
//                                    }
//                                }.invokeOnCompletion {
//                                    viewModel.onTaskSwiped(schedule, direction, isLast)
//                                }
                            },
                        )

                        SlideAnimation(
                            visibleState = visibleState,
                            modifier = modifier.align(Alignment.BottomCenter),
                        ) {
                            ReviewSwipeControls(
                                states = states,
                                modifier = modifier.align(Alignment.BottomCenter),
                                undoAction = viewModel::undoTask,
                                key = model.roundNumber
                            )
                        }

                        LaunchedEffect(key1 = model.roundNumber) {
                            visibleState.targetState = true
                        }
                    }

                    ReviewRound.Final -> {

                        val visibleState = remember(model.roundNumber) {
                            MutableTransitionState(false)
                        }
                        val transition = updateTransition(visibleState, "Visible state")

                        val screenWidth = with(LocalDensity.current) {
                            LocalConfiguration.current.screenWidthDp.dp.toPx()
                        }

                        val xPosition by transition.animateFloat(label = "xPosition") {
                            if (it) 0f else screenWidth
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PriorityReviewTopBar(navigateUp = viewModel::navigateBack)

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        translationX = xPosition
                                    }
                            ) {
                                when {
                                    model.roundItems.isEmpty() -> {
                                        Text(
                                            "Wooops, nothing to work on? add a new task",
                                            modifier.align(Alignment.Center)
                                        )
                                        // TODO: add a new task
                                    }

                                    else -> {
                                       // TODO(Joseph): go back to main screen
                                    }
                                }
                            }
                        }

//                        SlideAnimation(
//                            visibleState = visibleState,
//                            modifier = modifier.align(Alignment.BottomCenter),
//                        ) {
//                            ReviewFinalControls(
//                                modifier = modifier.align(Alignment.BottomCenter),
//                                resetAction = viewModel::reset,
//                                finishAction = viewModel::confirmResults
//                            )
//                        }

                        LaunchedEffect(key1 = model.roundNumber) {
                            visibleState.targetState = true
                        }
                    }
                }
            }
        }
    }
}

private fun List<Pair<ScheduleListItemViewModel, SwipeableCardState>>.lastSwipedItem() =
    reversed().firstOrNull { it.second.offset.value == Offset(0f, 0f) }?.run {
        getOrNull(indexOf(this) + 1)
    } ?: firstOrNull()

private fun List<Pair<ScheduleListItemViewModel, SwipeableCardState>>.firstVisibleOrNull() =
    reversed().firstOrNull { it.second.offset.value == Offset(0f, 0f) }

private fun List<Pair<ScheduleListItemViewModel, SwipeableCardState>>.firstVisibleStateOrNull() =
    firstVisibleOrNull()?.second

@Composable
private fun SlideAnimation(
    visibleState: MutableTransitionState<Boolean>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visibleState = visibleState,
        modifier = modifier,
        enter = slideInVertically { it * 2 },
        exit = slideOutVertically { it * 2 }
    ) {
        content()
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PriorityReviewTopBar(
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.priority_review),
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_navigate_back)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun ReviewSwipeControls(
    states: List<Pair<ScheduleListItemViewModel, SwipeableCardState>>,
    modifier: Modifier = Modifier,
    undoAction: (ScheduleListItemViewModel) -> Unit = {},
    key: Any? = Unit,
) {

    val scope = rememberCoroutineScope()

    val undoLastAction: () -> Unit = remember(key) {
        {
            scope.launch {
                states.run {
                    Timber.d("Undoing")
                    lastSwipedItem()?.let { lastItem ->
                        lastItem.second.undo()
                        undoAction(lastItem.first)
                    }
                }
            }
        }
    }

    val lowAction: () -> Unit = remember(key) {
        {
            scope.launch {
                states.firstVisibleStateOrNull()?.swipe(SwipeDirection.Left)
            }
        }
    }

    val highAction: () -> Unit = remember(key) {
        {
            scope.launch {
                states.firstVisibleStateOrNull()?.swipe(SwipeDirection.Right)
            }
        }
    }

    val doneAction: () -> Unit = remember(key) {
        {
            scope.launch {
                states.firstVisibleStateOrNull()?.swipe(SwipeDirection.Up)
            }
        }
    }

    val laterAction: () -> Unit = remember(key) {
        {
            scope.launch {
                states.firstVisibleStateOrNull()?.swipe(SwipeDirection.Down)
            }
        }
    }

    Column(
        modifier = modifier
    ) {

        Box(Modifier.align(Alignment.CenterHorizontally)) {
            CircleButton(
                onClick = undoLastAction,
                icon = Icons.Rounded.Undo
            )
        }

        Row(
            Modifier
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CircleButton(
                onClick = lowAction,
                icon = Icons.Rounded.ThumbDown
            )
            CircleButton(
                onClick = laterAction,
                icon = Icons.Rounded.Close
            )
            CircleButton(
                onClick = doneAction,
                icon = Icons.Rounded.Done
            )
            CircleButton(
                onClick = highAction,
                icon = Icons.Rounded.ThumbUp
            )
        }
    }
}

@Composable
@OptIn(ExperimentalSwipeableCardApi::class)
private fun TaskPrioritySwipe(
    modifier: Modifier,
    states: List<Pair<ScheduleListItemViewModel, SwipeableCardState>>,
    onSwiped: (schedule: ScheduleListItemViewModel, direction: SwipeDirection, isLast: Boolean) -> Unit,
) {

    Box(
        modifier
            .padding(24.dp)
            .fillMaxSize()
    ) {
        states.forEachIndexed { index, (schedule, state) ->
            if (state.swipedDirection == null) {

                val selectedState = remember(schedule.taskId) { MutableTransitionState(false) }
                val selectedTransition = updateTransition(selectedState, "Selected Transition")
                val ratio by selectedTransition.animateFloat(label = "AspectRatio") {
                    if (it) 0.8f else 1f
                }

                TaskCard(
                    modifier = modifier
                        .layoutId(schedule.taskId)
                        .fillMaxSize()
                        .aspectRatio(ratio)
                        .swipableCard(state = state)
                        .graphicsLayer {
                            translationY = -(5 * index).dp.toPx()
                        }
                        .clickable {
                            selectedState.targetState = !selectedState.currentState
                        },
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
    modifier: Modifier = Modifier,
    schedule: ScheduleListItemViewModel,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
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
            Column(Modifier.align(Alignment.Center)) {
                Text(
                    text = schedule.taskTitle,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun RoundInfo(
    model: ReviewModel,
    modifier: Modifier = Modifier
) {

    val info by remember(model.roundNumber) {
        mutableStateOf("Round ${model.roundNumber} - ${model.round}")
    }
    val instructions by remember(model.round) {
        when (model.round) {
            ReviewRound.Priority -> "<-- Low    High -->"
            ReviewRound.Final -> ""
        }.let {
            mutableStateOf(it)
        }
    }

    Column(
        modifier = modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = info,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = instructions,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )

        if (BuildConfig.DEBUG) {

            val total by remember {
                mutableStateOf("Total: ${model.roundItems.size}")
            }

            Text(
                text = total,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            val debug = with(model) {
                "RoundItems: ${roundItems.size}  Tomorrow: ${tomorrow.size} High: ${high.size}  Low: ${low.size}  Done: ${done.size}"
            }

            Text(
                text = debug,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview
@Composable
fun TaskCardPreview() {
    MoreStuffTheme(darkTheme = true) {
        TaskCard(
            modifier = Modifier.aspectRatio(1f),
            schedule = ScheduleListItemViewModel(taskTitle = "Hellooooo there")
        )
    }
}

@Preview
@Composable
fun ReviewSwipeControlsPreview() {
    MoreStuffTheme(darkTheme = true) {
        ReviewSwipeControls(listOf())
    }
}
